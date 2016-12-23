package geo.center

import grails.plugins.rest.client.RestBuilder
import grails.transaction.Transactional
import org.springframework.scheduling.annotation.Scheduled

@Transactional
class LocationService {

    Map cache = [:]

    static final int ROUND_TRIP = 2
    static final BigDecimal MILES_PER_METER = 1.0 / 1609.0
    static final BigDecimal HOURS_PER_SECOND = 1.0 / 3600.0
    static final BigDecimal DOLLARS_PER_MILE = 0.25
    static final BigDecimal DOLLARS_PER_HOUR = 40.0

    @Scheduled(fixedRate = 10000L)
    void dummyJob() {
        Location.findAllByGeneratedAndCost(true, 0.0, [max: 3]).each {
            println "calculating $it.text..."
            it.cost = findWeeklyCost(it)
            it.save(failOnError: true)
            println "$it.text = $it.cost"
        }
    }

    Integer findMetersBetween(Location start, Location end) {
        getFromCache(start, end, "meters")
    }

    Integer findSecondsBetween(Location start, Location end) {
        getFromCache(start, end, "seconds")
    }

    BigDecimal findWeeklyVehicleCost(Location start, Location end) {
        findMetersBetween(start, end) * end.tripsPerWeek * ROUND_TRIP * MILES_PER_METER * DOLLARS_PER_MILE
    }

    BigDecimal findWeeklyTimeCost(Location start, Location end) {
        findSecondsBetween(start, end) * end.tripsPerWeek * ROUND_TRIP * HOURS_PER_SECOND * DOLLARS_PER_HOUR
    }

    BigDecimal findWeeklyCost(Location start, Location end) {
        findWeeklyTimeCost(start, end) + findWeeklyVehicleCost(start, end)
    }

    BigDecimal findWeeklyCost(Location start) {
        List destinations = Location.findAllByTripsPerWeekGreaterThan(0.0)
        if (destinations) {
            destinations.sum { end -> findWeeklyCost(start, end) }
        }
    }

    /* PRIVATE HELPER METHODS */

    /**
     * get api key at https://developers.google.com/maps/documentation/directions/
     * manage api keys at https://console.developers.google.com/apis/credentials?project=geo-center
     *
     * @param start
     * @param end
     * @return
     */
    private String buildUrl(Location start, Location end) {
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=$start.text&destination=$end.text&sensor=false&alternatives=false"
        if (System.getenv("GOOGLE_API_KEY")) {
            url += "&key=${System.getenv("GOOGLE_API_KEY")}"
        }
//        println url
        return url
    }

    private void populateCache(String url) {
        def resp = new RestBuilder().get(url) { accept "application/json" }
        int sleepTime = 1000
        while (sleepTime < 10000 && !resp?.json?.routes) {
            Thread.sleep(sleepTime)
            resp = new RestBuilder().get(url) { accept "application/json" }
            sleepTime += 1000
        }
        cache[url] = [
            status : resp?.json?.status,
            meters : resp?.json?.status == "OK" ? resp?.json?.routes[0]?.legs[0]?.distance?.value : null,
            seconds: resp?.json?.status == "OK" ? resp?.json?.routes[0]?.legs[0]?.duration?.value : null,
            start  : resp?.json?.status == "OK" ? resp?.json?.routes[0]?.legs[0]?.start_location : null,
            end    : resp?.json?.status == "OK" ? resp?.json?.routes[0]?.legs[0]?.end_location : null
        ]
//        println "start lat, lng: ${cache[url]?.start.lat}, ${cache[url]?.start.lng}"
    }

    private Integer getFromCache(Location start, Location end, String field) {
        String url = buildUrl(start, end)
        if (!cache[url]?."$field") {
            populateCache(url)
        }
        if (!start.lat) start.lat = cache[url]?.start.lat
        if (!start.lng) start.lng = cache[url]?.start.lng
        if (!end.lat) end.lat = cache[url]?.end.lat
        if (!end.lat) end.lat = cache[url]?.end.lng
        return cache[url]."$field"
    }
}
