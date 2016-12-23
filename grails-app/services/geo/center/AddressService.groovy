package geo.center

import grails.plugins.rest.client.RestBuilder
import grails.transaction.Transactional

@Transactional
class AddressService {


    static final int ROUND_TRIP = 2
    static final BigDecimal MILES_PER_METER = 1.0 / 1609.0
    static final BigDecimal DOLLARS_PER_MILE = 0.25

    Integer findMetersBetween(Address start, Address end) {
        String url = buildUrl(start, end)
        def resp = new RestBuilder().get(url) { accept "application/json" }
        return resp.json.routes[0].legs[0].distance.value
    }

//        Address end = Address.findAllByTripsPerWeekNotEqual(0.0).first()
//        Address.findAllByWeightNotEqual(0.0).each {  }.sum()

    BigDecimal findWeeklyVehicleCost(Address start, end) {
        findMetersBetween(start, end) * end.tripsPerWeek * ROUND_TRIP * MILES_PER_METER * DOLLARS_PER_MILE
    }

    private GString buildUrl(Address start, Address end) {
        "http://maps.googleapis.com/maps/api/directions/json?origin=$start.text&destination=$end.text&sensor=false&alternatives=false"
    }
}
