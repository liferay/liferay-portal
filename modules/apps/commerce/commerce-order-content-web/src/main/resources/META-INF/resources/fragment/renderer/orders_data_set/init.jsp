<%--
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/frontend-data-set" prefix="frontend-data-set" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>

<%@ page import="com.liferay.commerce.order.content.web.internal.constants.CommerceOrderFragmentFDSNames" %><%@
page import="com.liferay.frontend.data.set.model.FDSActionDropdownItem" %><%@
page import="com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu" %><%@
page import="com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem" %><%@
page import="com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil" %><%@
page import="com.liferay.portal.kernel.json.JSONArray" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %>

<%@ page import="java.util.List" %><%@
page import="java.util.Map" %>

<liferay-theme:defineObjects />

<%
Map<String, Object> additionalProps = (Map<String, Object>)request.getAttribute("liferay-commerce:order-data-set:additionalProps");
String apiURL = (String)request.getAttribute("liferay-commerce:order-data-set:apiURL");
List<DropdownItem> bulkActionDropdownItems = (List<DropdownItem>)request.getAttribute("liferay-commerce:order-data-set:fdsBulkActionDropdownItems");
String displayStyle = (String)request.getAttribute("liferay-commerce:order-data-set:displayStyle");
List<FDSActionDropdownItem> fdsActionDropdownItems = (List<FDSActionDropdownItem>)request.getAttribute("liferay-commerce:order-data-set:fdsActionDropdownItems");
CreationMenu fdsCreationMenu = (CreationMenu)request.getAttribute("liferay-commerce:order-data-set:fdsCreationMenu");
String name = (String)request.getAttribute("liferay-commerce:order-data-set:name");
String namespace = (String)request.getAttribute("liferay-commerce:order-data-set:namespace");
JSONArray orderTypesJSONArray = (JSONArray)request.getAttribute("liferay-commerce:order-data-set:orderTypes");
String propsTransformer = (String)request.getAttribute("liferay-commerce:order-data-set:propsTransformer");
Map<String, Object> returnableOrderItemsContextParams = (Map<String, Object>)request.getAttribute("liferay-commerce:order-actions:returnableOrderItemsContextParams");
String viewReturnableOrderItemsURL = (String)request.getAttribute("liferay-commerce:order-data-set:viewReturnableOrderItemsURL");
%>