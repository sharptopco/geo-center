package geo.center

class Address {

    AddressService addressService

    String text
    float tripsPerWeek = 0.0
    int cost = 0.0

    static constraints = {
        text blank: false
    }

    static void initializeData() {
        if (!Address.count()) {
            [
                [text: '2361 New London Rd, Forest, VA 24551', weight: 5.5],
                [text: '901 Jefferson Street Lynchburg VA 24504', weight: 2],
                [text: '210 Chelsea Drive, Forest, VA 24551', weight: 1],
                [text: '3710 Old Forest Rd, Lynchburg, VA 24501', weight: 1.5],
                [text: '3900 Wards Rd, Lynchburg, VA 24502', weight: 4],
                [text: '801 Wyndhurst Dr, Lynchburg, VA 24502', weight: 3],
                [text: '275 Gaddy Rd, Lynchburg, VA 24502', weight: 1]
            ].each { new Address(it).save(failOnError: true) }
        }
    }
}
