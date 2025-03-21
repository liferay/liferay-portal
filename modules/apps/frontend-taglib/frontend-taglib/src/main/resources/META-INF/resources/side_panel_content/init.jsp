<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>

<%@ page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.portal.kernel.util.Validator" %>

<%@ page import="jakarta.portlet.PortletURL" %>

<liferay-theme:defineObjects />

<%
String screenNavigatorKey = (String)request.getAttribute("liferay-frontend:side-panel-content:screenNavigatorKey");
Object screenNavigatorModelBean = (Object)request.getAttribute("liferay-frontend:side-panel-content:screenNavigatorModelBean");
PortletURL screenNavigatorPortletURL = (PortletURL)request.getAttribute("liferay-frontend:side-panel-content:screenNavigatorPortletURL");
String title = (String)request.getAttribute("liferay-frontend:side-panel-content:title");
%>