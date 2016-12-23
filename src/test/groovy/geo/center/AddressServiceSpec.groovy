package geo.center

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
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
        church = new Address(text: "2361 New London Rd, Forest, VA 24551")
    }

    def cleanup() {
    }

    void "test find meters between addresses"() {
        expect:
        service.findMetersBetween(home, church) == 19076
    }

    void "test find seconds between addresses"() {
        expect:
        service.findSecondsBetween(home, church).setScale(1, BigDecimal.ROUND_HALF_UP) == 1076
    }

    void "test determine weekly vehicle cost"() {
        setup:
        church.tripsPerWeek = 5.5

        expect:
        service.findWeeklyVehicleCost(home, church).setScale(1, BigDecimal.ROUND_HALF_UP) == 32.6
    }
}
