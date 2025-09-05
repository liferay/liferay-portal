<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
boolean geolocation = GetterUtil.getBoolean(request.getAttribute("liferay-map:map:geolocation"));
double latitude = (Double)request.getAttribute("liferay-map:map:latitude");
double longitude = (Double)request.getAttribute("liferay-map:map:longitude");
String name = (String)request.getAttribute("liferay-map:map:name");
String points = (String)request.getAttribute("liferay-map:map:points");

name = AUIUtil.getNamespace(liferayPortletRequest, liferayPortletResponse) + name;
%>

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
	module="{App} from map-openstreetmap"
/>