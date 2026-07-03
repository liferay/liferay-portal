<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String protocol = HttpComponentsUtil.getProtocol(request);

boolean geolocation = GetterUtil.getBoolean(request.getAttribute("liferay-map:map:geolocation"));
double latitude = (Double)request.getAttribute("liferay-map:map:latitude");
double longitude = (Double)request.getAttribute("liferay-map:map:longitude");
String name = (String)request.getAttribute("liferay-map:map:name");
String points = (String)request.getAttribute("liferay-map:map:points");

name = AUIUtil.getNamespace(liferayPortletRequest, liferayPortletResponse) + name;
%>

<liferay-util:html-top
	outputKey="com.liferay.map.google.maps#/view.jsp"
>
	<aui:script>
		Liferay.namespace('Maps').onGMapsReady = function (event) {
			Liferay.Maps.gmapsReady = true;

			Liferay.fire('gmapsReady');
		};

		if (
			!Liferay.Maps.gmapsReady &&
			!Liferay.Maps.gmapsLoading &&
			!document.querySelector(
				'script[src*="maps.googleapis.com/maps/api/js"][src*="callback=Liferay.Maps.onGMapsReady"]'
			)
		) {
			Liferay.Maps.gmapsLoading = true;

			var apiURL =
				'<%= protocol %>' +
				'://maps.googleapis.com/maps/api/js?v=3.exp&libraries=places&callback=Liferay.Maps.onGMapsReady';

			<c:if test="<%= Validator.isNotNull(googleMapsDisplayContext.getGoogleMapsAPIKey()) %>">
				apiURL += '&key=' + '<%= googleMapsDisplayContext.getGoogleMapsAPIKey() %>';
			</c:if>

			var script = document.createElement('script');

			script.setAttribute('src', apiURL);

			document.head.appendChild(script);

			script = null;
		}
	</aui:script>
</liferay-util:html-top>

<liferay-frontend:component
	context='<%=
		HashMapBuilder.<String, Object>put(
			"boundingBox", "#" + HtmlUtil.escapeJS(name) + "Map"
		).put(
			"data",
			() -> {
				if (Validator.isNull(points)) {
					return null;
				}

				return JSONFactoryUtil.createJSONObject(points);
			}
		).put(
			"geolocation", geolocation
		).put(
			"isMobile", BrowserSnifferUtil.isMobile(request)
		).put(
			"latitude", latitude
		).put(
			"longitude", longitude
		).put(
			"name", HtmlUtil.escapeJS(name)
		).put(
			"portletId", portletDisplay.getId()
		).build()
	%>'
	module="{App} from map-google-maps"
/>