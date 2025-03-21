<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/commerce-ui" prefix="commerce-ui" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/frontend-data-set" prefix="frontend-data-set" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.commerce.inventory.exception.CommerceInventoryReplenishmentQuantityException" %><%@
page import="com.liferay.commerce.inventory.exception.CommerceInventoryWarehouseItemQuantityException" %><%@
page import="com.liferay.commerce.inventory.exception.DuplicateCommerceInventoryWarehouseItemException" %><%@
page import="com.liferay.commerce.inventory.exception.MVCCException" %><%@
page import="com.liferay.commerce.inventory.model.CommerceInventoryReplenishmentItem" %><%@
page import="com.liferay.commerce.inventory.model.CommerceInventoryWarehouse" %><%@
page import="com.liferay.commerce.inventory.model.CommerceInventoryWarehouseItem" %><%@
page import="com.liferay.commerce.inventory.web.internal.constants.CommerceInventoryFDSNames" %><%@
page import="com.liferay.commerce.inventory.web.internal.constants.CommerceInventoryScreenNavigationConstants" %><%@
page import="com.liferay.commerce.inventory.web.internal.display.context.CommerceInventoryDisplayContext" %><%@
page import="com.liferay.commerce.product.exception.CPInstanceUnitOfMeasureKeyException" %><%@
page import="com.liferay.commerce.product.exception.NoSuchCPInstanceUnitOfMeasureException" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.security.auth.PrincipalException" %><%@
page import="com.liferay.portal.kernel.util.CalendarFactoryUtil" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %>

<%@ page import="java.util.Calendar" %><%@
page import="java.util.Date" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />