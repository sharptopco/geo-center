package geo.center

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Ignore
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@Mock(Address)
@TestFor(AddressService)
class AddressServiceSpec extends Specification {

    Address home
    Address church

    def setup() {
        home = new Address(text: "192 Shannon Forest Dr Rustburg, VA")
        church = new Address(text: "2361 New London Rd, Forest, VA 24551", tripsPerWeek: 5.5)
    }

    def cleanup() {
    }

    void "test find meters between addresses"() {
        expect:
        service.findMetersBetween(home, church) == 19076
    }

    void "test find seconds between addresses"() {
        expect:
        service.findSecondsBetween(home, church) == 1076
    }

    void "test find weekly vehicle cost"() {
        expect:
        service.findWeeklyVehicleCost(home, church).setScale(1, BigDecimal.ROUND_HALF_UP) == 32.6
    }

    void "test find weekly time cost"() {
        expect:
        service.findWeeklyTimeCost(home, church).setScale(1, BigDecimal.ROUND_HALF_UP) == 131.5
    }

    void "test find weekly cost with two locations"() {
        expect:
        service.findWeeklyCost(home, church).setScale(1, BigDecimal.ROUND_HALF_UP) == 164.1
    }

    void "test find weekly cost of a single location"() {
        setup:
        assert !Address.count()
        Address.initializeData()
        assert Address.count()

        expect:
        service.findWeeklyCost(home)?.setScale(1, BigDecimal.ROUND_HALF_UP) == 582.8
    }

    @Ignore
    void doStuff() {
        setup:
        assert !Address.count()
        Address.initializeData()
        assert Address.count()

        when:
        Map brcc = [lat: 37.2883773, lon: -79.3629626]
        Map work = [lat: 37.415531, lon: -79.1425467]
        int i = 0;
        Date startTime = new Date()
        for (float lat = brcc.lat; lat < work.lat; lat += 0.01) {
            for (float lon = brcc.lon; lon < work.lon; lon += 0.01) {
                println "${i++},$lat,$lon,${service.findWeeklyCost(new Address(text: "$lat, $lon"))}"
            }
        }
        Date endTime = new Date()
        def elapsedTime = endTime.time - startTime.time
        println "$elapsedTime elapsed; ${elapsedTime / (float) i} per location"

        then:
        true
    }
}
