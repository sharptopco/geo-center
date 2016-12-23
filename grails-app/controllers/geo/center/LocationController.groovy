package geo.center

import grails.transaction.Transactional

import static org.springframework.http.HttpStatus.*

@Transactional(readOnly = true)
class LocationController {

    LocationService locationService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        def list = Location.findAllByTripsPerWeekAndGenerated(0.0, false).sort { a, b -> a.cost <=> b.cost }
        respond list, model: [locationCount: list.size()], view: "index"
    }

    def all(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Location.list(params), model: [locationCount: Location.count()], view: "index"
    }

    def destinations(Integer max) {
        def list = Location.findAllByTripsPerWeekGreaterThan(0.0).sort { a, b -> b.tripsPerWeek <=> a.tripsPerWeek }
        respond list, model: [locationCount: list.size()], view: "index"
    }

    def show(Location location) {
        respond location
    }

    def create() {
        respond new Location(params)
    }

    @Transactional
    def save(Location location) {
        if (location == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (location.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond location.errors, view: 'create'
            return
        }

        location.cost = locationService.findWeeklyCost(location)

        location.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'location.label', default: 'Location'), location.id])
                redirect location
            }
            '*' { respond location, [status: CREATED] }
        }
    }

    def edit(Location location) {
        respond location
    }

    @Transactional
    def update(Location location) {
        if (location == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (location.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond location.errors, view: 'edit'
            return
        }

        location.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'location.label', default: 'Location'), location.id])
                redirect location
            }
            '*' { respond location, [status: OK] }
        }
    }

    @Transactional
    def delete(Location location) {

        if (location == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        location.delete flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'location.label', default: 'Location'), location.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'location.label', default: 'Location'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}
