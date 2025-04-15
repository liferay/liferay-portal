<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.Validator" %>

<liferay-theme:defineObjects />

<%
String actionIcon = (String)request.getAttribute("liferay-commerce:panel:actionIcon");
String actionLabel = (String)request.getAttribute("liferay-commerce:panel:actionLabel");
String actionTargetId = (String)request.getAttribute("liferay-commerce:panel:actionTargetId");
String actionUrl = (String)request.getAttribute("liferay-commerce:panel:actionUrl");
String bodyClasses = (String)request.getAttribute("liferay-commerce:panel:bodyClasses");
boolean collapsed = (boolean)request.getAttribute("liferay-commerce:panel:collapsed");
String collapseLabel = (String)request.getAttribute("liferay-commerce:panel:collapseLabel");
String collapseSwitchName = (String)request.getAttribute("liferay-commerce:panel:collapseSwitchName");
boolean collapsible = (boolean)request.getAttribute("liferay-commerce:panel:collapsible");
String elementClasses = (String)request.getAttribute("liferay-commerce:panel:elementClasses");
String randomNamespace = (String)request.getAttribute("liferay-commerce:panel:randomNamespace");
String showMoreId = (String)request.getAttribute("liferay-commerce:panel:showMoreId");
String showMoreUrl = (String)request.getAttribute("liferay-commerce:panel:showMoreUrl");
String title = (String)request.getAttribute("liferay-commerce:panel:title");

String linkId = Validator.isNotNull(actionTargetId) ? actionTargetId : (randomNamespace + "header-link");
String showMoreButtonId = Validator.isNotNull(showMoreId) ? showMoreId : (randomNamespace + "show-more-button");
String showMoreButtonWrapperId = randomNamespace + "show-more-button-wrapper";
%>