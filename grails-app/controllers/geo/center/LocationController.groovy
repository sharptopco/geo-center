package geo.center

import grails.transaction.Transactional

import static org.springframework.http.HttpStatus.*

@Transactional(readOnly = true)
class LocationController {

    boolean lazyInit = false

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

    def generated(Integer max) {
        def list = Location.findAllByGeneratedAndCostGreaterThan(true, 0.0).sort { a, b -> a.cost <=> b.cost }
        respond list, model: [locationCount: list.size()], view: "index"
    }

    def export() {
        String output = ""
        def list = Location.list()
        list.each {
            if (!location.lat || !location.lng) {
                refreshLatLng(location)
            }
            output += "new Location(text: '$it.text', lat: $it.lat, lng: $it.lng, generated: $it.generated, cost: $it.cost, tripsPerWeek: $it.tripsPerWeek).save(failOnError: true)<br />"
        }
        render output
    }

    @Transactional
    def show(Location location) {
        if (!location.lat || !location.lng) {
            refreshLatLng(location)
        }
//        println "$location.lat, $location.lng"
        respond location
    }

    def create() {
        respond new Location(params)
    }

    def generate() {
        Map church = [lat: 37.2883773, lon: -79.3629626]
        Map work = [lat: 37.415531, lon: -79.1425467]

        int i = 0;
        for (float lat = church.lat; lat < work.lat; lat += 0.01) {
            for (float lon = church.lon; lon < work.lon; lon += 0.01) {
                println "${i++},$lat,$lon"
                new Location(text: "$lat, $lon", generated: true).save()
            }
        }

        redirect action: "generated"
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

    private void refreshLatLng(Location location) {
        location.lat = null
        location.lng = null
        location.save(failOnError: true, flush: true)
        locationService.findSecondsBetween(location, location)
        location.save()
    }

}
