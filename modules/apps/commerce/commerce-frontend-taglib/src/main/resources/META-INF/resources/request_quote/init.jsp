<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<%
boolean disabled = (boolean)request.getAttribute("liferay-commerce:request-quote:disabled");
long commerceAccountId = (long)request.getAttribute("liferay-commerce:request-quote:commerceAccountId");
long commerceChannelId = (long)request.getAttribute("liferay-commerce:request-quote:commerceChannelId");
String commerceCurrencyCode = (String)request.getAttribute("liferay-commerce:request-quote:commerceCurrencyCode");
long cpDefinitionId = (long)request.getAttribute("liferay-commerce:request-quote:cpDefinitionId");
long cpInstanceId = (long)request.getAttribute("liferay-commerce:request-quote:cpInstanceId");
String namespace = (String)request.getAttribute("liferay-commerce:request-quote:namespace");
String orderDetailURL = (String)request.getAttribute("liferay-commerce:request-quote:orderDetailURL");
boolean priceOnApplication = (boolean)request.getAttribute("liferay-commerce:request-quote:priceOnApplication");
boolean requestQuoteEnabled = (boolean)request.getAttribute("liferay-commerce:request-quote:requestQuoteEnabled");
String skuOptions = (String)request.getAttribute("liferay-commerce:request-quote:skuOptions");

String randomNamespace = PortalUtil.generateRandomKey(request, "taglib") + StringPool.UNDERLINE;

String requestQuoteElementId = randomNamespace + "request_quote";
%>