<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <g:set var="entityName" value="${message(code: 'location.label', default: 'Location')}"/>
        <title><g:message code="default.list.label" args="[entityName]"/></title>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
    </head>

    <body>
        <a href="#list-location" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>

        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="list" action="toReview">To Review</g:link></li>
                <li><g:link class="list" action="generated">Generated Locations</g:link></li>
                <li><g:link class="list" action="all">All Locations</g:link></li>
                <li><g:link class="list" action="destinations">Destinations</g:link></li>
                <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]"/></g:link></li>
            </ul>
        </div>

        <div id="list-location" class="content scaffold-list" role="main">
            <h1><g:message code="default.list.label" args="[entityName]"/></h1>
            <g:if test="${flash.message}">
                <div class="message" role="status">${flash.message}</div>
            </g:if>
            <f:table collection="${locationList}"/>

            <div class="pagination">
                <g:paginate total="${locationCount ?: 0}"/>
            </div>
        </div>
    </body>
</html>