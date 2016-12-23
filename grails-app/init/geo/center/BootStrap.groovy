package geo.center

class BootStrap {

    def init = { servletContext ->
        Address.initializeData()
    }
    def destroy = {
    }
}
