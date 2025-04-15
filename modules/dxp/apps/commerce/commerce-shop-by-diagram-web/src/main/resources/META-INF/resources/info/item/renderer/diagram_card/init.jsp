<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://liferay.com/tld/adaptive-media-image" prefix="liferay-adaptive-media" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.commerce.constants.CommerceWebKeys" %><%@
page import="com.liferay.commerce.context.CommerceContext" %><%@
page import="com.liferay.commerce.product.catalog.CPCatalogEntry" %><%@
page import="com.liferay.commerce.product.constants.CPWebKeys" %><%@
page import="com.liferay.commerce.product.content.constants.CPContentWebKeys" %><%@
page import="com.liferay.commerce.product.content.helper.CPContentHelper" %><%@
page import="com.liferay.commerce.shop.by.diagram.constants.CSDiagramWebKeys" %><%@
page import="com.liferay.commerce.shop.by.diagram.model.CSDiagramSetting" %><%@
page import="com.liferay.commerce.shop.by.diagram.util.CSDiagramCPTypeHelper" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<%
CommerceContext commerceContext = (CommerceContext)request.getAttribute(CommerceWebKeys.COMMERCE_CONTEXT);
CPCatalogEntry cpCatalogEntry = (CPCatalogEntry)request.getAttribute(CPWebKeys.CP_CATALOG_ENTRY);
CPContentHelper cpContentHelper = (CPContentHelper)request.getAttribute(CPContentWebKeys.CP_CONTENT_HELPER);
CSDiagramCPTypeHelper csDiagramCPTypeHelper = (CSDiagramCPTypeHelper)request.getAttribute(CSDiagramWebKeys.CS_DIAGRAM_CP_TYPE_HELPER);

boolean showAddToCartButton = (boolean)request.getAttribute("liferay-commerce:product-card:showAddToCartButton");
boolean showImage = (boolean)request.getAttribute("liferay-commerce:product-card:showImage");
boolean showName = (boolean)request.getAttribute("liferay-commerce:product-card:showName");
boolean showPrice = (boolean)request.getAttribute("liferay-commerce:product-card:showPrice");
%>