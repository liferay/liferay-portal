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
taglib uri="http://liferay.com/tld/expando" prefix="liferay-expando" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/frontend-data-set" prefix="frontend-data-set" %><%@
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.account.model.AccountEntry" %><%@
page import="com.liferay.commerce.constants.CommercePortletKeys" %><%@
page import="com.liferay.commerce.constants.CommerceShipmentFDSNames" %><%@
page import="com.liferay.commerce.exception.CommerceShipmentExpectedDateException" %><%@
page import="com.liferay.commerce.exception.CommerceShipmentItemQuantityException" %><%@
page import="com.liferay.commerce.exception.CommerceShipmentShippingDateException" %><%@
page import="com.liferay.commerce.exception.CommerceShipmentStatusException" %><%@
page import="com.liferay.commerce.exception.DuplicateCommerceShipmentException" %><%@
page import="com.liferay.commerce.exception.DuplicateCommerceShipmentExternalReferenceCodeException" %><%@
page import="com.liferay.commerce.exception.DuplicateCommerceShipmentItemException" %><%@
page import="com.liferay.commerce.exception.DuplicateCommerceShipmentItemExternalReferenceCodeException" %><%@
page import="com.liferay.commerce.exception.NoSuchShipmentException" %><%@
page import="com.liferay.commerce.exception.NoSuchShipmentItemException" %><%@
page import="com.liferay.commerce.model.CommerceAddress" %><%@
page import="com.liferay.commerce.model.CommerceOrderItem" %><%@
page import="com.liferay.commerce.model.CommerceShipment" %><%@
page import="com.liferay.commerce.model.CommerceShipmentItem" %><%@
page import="com.liferay.commerce.model.CommerceShippingMethod" %><%@
page import="com.liferay.commerce.product.model.CommerceChannel" %><%@
page import="com.liferay.commerce.shipment.web.internal.constants.CommerceShipmentScreenNavigationConstants" %><%@
page import="com.liferay.commerce.shipment.web.internal.display.context.CommerceShipmentDisplayContext" %><%@
page import="com.liferay.commerce.shipment.web.internal.display.context.CommerceShipmentItemDisplayContext" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.model.Country" %><%@
page import="com.liferay.portal.kernel.model.Region" %><%@
page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %><%@
page import="com.liferay.portal.kernel.util.CalendarFactoryUtil" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.FastDateFormatFactoryUtil" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %>

<%@ page import="java.text.DateFormat" %><%@
page import="java.text.Format" %>

<%@ page import="java.util.Calendar" %><%@
page import="java.util.Date" %><%@
page import="java.util.List" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
String lifecycle = (String)request.getAttribute(liferayPortletRequest.LIFECYCLE_PHASE);

String shipmentsURL = String.valueOf(PortalUtil.getControlPanelPortletURL(request, CommercePortletKeys.COMMERCE_SHIPMENT, lifecycle));

String redirect = ParamUtil.getString(request, "redirect", shipmentsURL);
%>