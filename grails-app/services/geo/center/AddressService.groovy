package geo.center

import grails.plugins.rest.client.RestBuilder
import grails.transaction.Transactional

@Transactional
class AddressService {


    static final int ROUND_TRIP = 2
    static final BigDecimal MILES_PER_METER = 1.0 / 1609.0
    static final BigDecimal HOURS_PER_SECOND = 1.0 / 3600.0
    static final BigDecimal DOLLARS_PER_MILE = 0.25
    static final BigDecimal DOLLARS_PER_HOUR = 40.0

    Integer findMetersBetween(Address start, Address end) {
        String url = buildUrl(start, end)
        def resp = new RestBuilder().get(url) { accept "application/json" }
        int sleepTime = 1000
        while (sleepTime < 10000 && resp?.json?.status == "OVER_QUERY_LIMIT") {
            Thread.sleep(sleepTime)
            resp = new RestBuilder().get(url) { accept "application/json" }
            sleepTime += 1000
        }
        return resp?.json?.routes[0]?.legs[0]?.distance?.value
    }

    Integer findSecondsBetween(Address start, Address end) {
        String url = buildUrl(start, end)
        def resp = new RestBuilder().get(url) { accept "application/json" }
        int sleepTime = 1000
        while (sleepTime < 10000 && resp?.json?.status == "OVER_QUERY_LIMIT") {
            Thread.sleep(sleepTime)
            resp = new RestBuilder().get(url) { accept "application/json" }
            sleepTime += 1000
        }
        return resp?.json?.routes[0]?.legs[0]?.duration?.value
    }

    BigDecimal findWeeklyVehicleCost(Address start, Address end) {
        findMetersBetween(start, end) * end.tripsPerWeek * ROUND_TRIP * MILES_PER_METER * DOLLARS_PER_MILE
    }

    BigDecimal findWeeklyTimeCost(Address start, Address end) {
        findSecondsBetween(start, end) * end.tripsPerWeek * ROUND_TRIP * HOURS_PER_SECOND * DOLLARS_PER_HOUR
    }

    BigDecimal findWeeklyCost(Address start, Address end) {
        findWeeklyTimeCost(start, end) + findWeeklyVehicleCost(start, end)
    }

    BigDecimal findWeeklyCost(Address start) {
        List destinations = Address.findAllByTripsPerWeekGreaterThan(0.0)
        if (destinations) {
            destinations.sum { end -> findWeeklyCost(start, end) }
        }
    }

    private GString buildUrl(Address start, Address end) {
        "http://maps.googleapis.com/maps/api/directions/json?origin=$start.text&destination=$end.text&sensor=false&alternatives=false"
    }
}
