<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://liferay.com/tld/adaptive-media-image" prefix="liferay-adaptive-media" %><%@
taglib uri="http://liferay.com/tld/commerce-ui" prefix="commerce-ui" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.commerce.product.catalog.CPCatalogEntry" %><%@
page import="com.liferay.commerce.product.catalog.CPSku" %><%@
page import="com.liferay.commerce.product.constants.CPWebKeys" %><%@
page import="com.liferay.commerce.product.content.constants.CPContentWebKeys" %><%@
page import="com.liferay.commerce.product.content.helper.CPContentHelper" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.portal.kernel.util.Validator" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<%
CPCatalogEntry cpCatalogEntry = (CPCatalogEntry)request.getAttribute(CPWebKeys.CP_CATALOG_ENTRY);
CPContentHelper cpContentHelper = (CPContentHelper)request.getAttribute(CPContentWebKeys.CP_CONTENT_HELPER);

boolean showAddToCartButton = (boolean)request.getAttribute("liferay-commerce:product-card:showAddToCartButton");
boolean showAddToWishListButton = (boolean)request.getAttribute("liferay-commerce:product-card:showAddToWishListButton");
boolean showAvailabilityLabel = (boolean)request.getAttribute("liferay-commerce:product-card:showAvailabilityLabel");
boolean showCompareCheckbox = (boolean)request.getAttribute("liferay-commerce:product-card:showCompareCheckbox");
boolean showDiscontinuedLabel = (boolean)request.getAttribute("liferay-commerce:product-card:showDiscontinuedLabel");
boolean showImage = (boolean)request.getAttribute("liferay-commerce:product-card:showImage");
boolean showName = (boolean)request.getAttribute("liferay-commerce:product-card:showName");
boolean showPrice = (boolean)request.getAttribute("liferay-commerce:product-card:showPrice");
boolean showSku = (boolean)request.getAttribute("liferay-commerce:product-card:showSku");
%>