package geo.center

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?" {
            constraints {
                // apply constraints here
            }
        }

        "/"(controller: "location")
        "500"(view: '/error')
        "404"(view: '/notFound')
    }
}
