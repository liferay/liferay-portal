<%--
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/react" prefix="react" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>

<%@ page import="com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem" %><%@
page import="com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.Validator" %>

<%@ page import="java.util.List" %><%@
page import="java.util.Map" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<%
String checkoutURL = (String)request.getAttribute("liferay-commerce:order-actions:checkoutURL");
long commerceOrderId = (long)request.getAttribute("liferay-commerce:order-actions:commerceOrderId");
List<DropdownItem> dropdownItems = (List<DropdownItem>)request.getAttribute("liferay-commerce:order-actions:dropdownItems");
String namespace = (String)request.getAttribute("liferay-commerce:order-actions:namespace");
boolean open = (boolean)request.getAttribute("liferay-commerce:order-actions:open");
String orderSummaryURL = (String)request.getAttribute("liferay-commerce:order-actions:orderSummaryURL");
boolean quickCheckoutEnabled = (boolean)request.getAttribute("liferay-commerce:order-actions:quickCheckoutEnabled");
String reorderURL = (String)request.getAttribute("liferay-commerce:order-actions:reorderURL");
Map<String, Object> returnableOrderItemsContextParams = (Map<String, Object>)request.getAttribute("liferay-commerce:order-actions:returnableOrderItemsContextParams");
String viewReturnableOrderItemsURL = (String)request.getAttribute("liferay-commerce:order-actions:viewReturnableOrderItemsURL");
%>