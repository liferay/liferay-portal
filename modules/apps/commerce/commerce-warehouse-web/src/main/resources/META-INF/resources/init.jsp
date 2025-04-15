<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/commerce-ui" prefix="commerce-ui" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/frontend-data-set" prefix="frontend-data-set" %><%@
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.commerce.exception.CommerceGeocoderException" %><%@
page import="com.liferay.commerce.inventory.exception.CommerceInventoryWarehouseActiveException" %><%@
page import="com.liferay.commerce.inventory.exception.CommerceInventoryWarehouseItemQuantityException" %><%@
page import="com.liferay.commerce.inventory.exception.CommerceInventoryWarehouseNameException" %><%@
page import="com.liferay.commerce.inventory.exception.DuplicateCommerceInventoryWarehouseExternalReferenceCodeException" %><%@
page import="com.liferay.commerce.inventory.exception.MVCCException" %><%@
page import="com.liferay.commerce.inventory.exception.NoSuchInventoryWarehouseException" %><%@
page import="com.liferay.commerce.inventory.model.CommerceInventoryWarehouse" %><%@
page import="com.liferay.commerce.inventory.model.CommerceInventoryWarehouseItem" %><%@
page import="com.liferay.commerce.product.model.CPInstance" %><%@
page import="com.liferay.commerce.warehouse.web.internal.constants.CommerceInventoryWarehouseFDSNames" %><%@
page import="com.liferay.commerce.warehouse.web.internal.constants.CommerceInventoryWarehouseScreenNavigationConstants" %><%@
page import="com.liferay.commerce.warehouse.web.internal.display.context.CommerceInventoryWarehouseItemsDisplayContext" %><%@
page import="com.liferay.commerce.warehouse.web.internal.display.context.CommerceInventoryWarehouseQualifiersDisplayContext" %><%@
page import="com.liferay.commerce.warehouse.web.internal.display.context.CommerceInventoryWarehousesDisplayContext" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.bean.BeanParamUtil" %><%@
page import="com.liferay.portal.kernel.dao.search.ResultRow" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %>

<%@ page import="jakarta.portlet.PortletURL" %>

<%@ page import="java.math.BigDecimal" %>

<%@ page import="java.util.List" %><%@
page import="java.util.Objects" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />