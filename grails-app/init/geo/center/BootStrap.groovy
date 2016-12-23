package geo.center

class BootStrap {

    def init = { servletContext ->
        Location.initializeData()
    }
    def destroy = {
    }
}
