<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <g:set var="entityName" value="${message(code: 'location.label', default: 'Location')}"/>
        <title><g:message code="default.show.label" args="[entityName]"/></title>
        <style>
        #map {
            height: 600px;
            width: 100%;
        }
        </style>
    </head>

    <body>
        <a href="#show-location" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>

        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="list" action="index">House Locations</g:link></li>
                <li><g:link class="list" action="generated">Generated Locations</g:link></li>
                <li><g:link class="list" action="all">All Locations</g:link></li>
                <li><g:link class="list" action="destinations">Destinations</g:link></li>
                <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]"/></g:link></li>
            </ul>
        </div>

        <div id="show-location" class="content scaffold-show" role="main">
            <h1><g:message code="default.show.label" args="[entityName]"/></h1>
            <g:if test="${flash.message}">
                <div class="message" role="status">${flash.message}</div>
            </g:if>

            <div id="map"></div>
            <script>
                function initMap() {
                    var loc = {lat: ${location.lat}, lng: ${location.lng}};
                    var map = new google.maps.Map(document.getElementById('map'), {
                        zoom: 13,
                        center: loc,
                        mapTypeId: 'hybrid'
                    });
                    var marker = new google.maps.Marker({
                        position: loc,
                        map: map
                    });
                }
            </script>
            <script async defer
                    src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDYslcffsU9sCiC1mvpsa5yBurx2wxXtnc&callback=initMap">
            </script>
            <f:display bean="location"/>
            <g:form resource="${this.location}" method="DELETE">
                <fieldset class="buttons">
                    <g:link class="edit" action="edit" resource="${this.location}"><g:message code="default.button.edit.label"
                                                                                              default="Edit"/></g:link>
                    <input class="delete" type="submit" value="${message(code: 'default.button.delete.label', default: 'Delete')}"
                           onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/>
                </fieldset>
            </g:form>
        </div>
    </body>
</html>
