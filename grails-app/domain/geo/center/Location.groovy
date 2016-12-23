package geo.center

class Location {

    String text
    float tripsPerWeek = 0.0
    int cost = 0.0
    boolean generated = false

    static constraints = {
        text blank: false
    }

    static void initializeData() {
        if (!Location.count()) {
            [
                [text: '2361 New London Rd, Forest, VA 24551', tripsPerWeek: 5.5],
                [text: '901 Jefferson Street Lynchburg VA 24504', tripsPerWeek: 2],
                [text: '210+Chelsea+Dr,+Forest,+VA+24551', tripsPerWeek: 1],
                [text: '3710 Old Forest Rd, Lynchburg, VA 24501', tripsPerWeek: 1.5],
                [text: '3900 Wards Rd, Lynchburg, VA 24502', tripsPerWeek: 4],
                [text: '801 Wyndhurst Dr, Lynchburg, VA 24502', tripsPerWeek: 3],
                [text: '275 Gaddy Rd, Lynchburg, VA 24502', tripsPerWeek: 1]
            ].each { new Location(it).save(failOnError: true) }
        }
    }
}
