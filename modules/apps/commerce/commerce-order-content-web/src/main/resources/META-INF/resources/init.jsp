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
taglib uri="http://liferay.com/tld/commerce" prefix="liferay-commerce" %><%@
taglib uri="http://liferay.com/tld/commerce-ui" prefix="commerce-ui" %><%@
taglib uri="http://liferay.com/tld/ddm" prefix="liferay-ddm" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/frontend-data-set" prefix="frontend-data-set" %><%@
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/template" prefix="liferay-template" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/user" prefix="liferay-user" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.account.exception.NoSuchEntryException" %><%@
page import="com.liferay.account.model.AccountEntry" %><%@
page import="com.liferay.commerce.constants.CommerceOrderActionKeys" %><%@
page import="com.liferay.commerce.constants.CommerceOrderConstants" %><%@
page import="com.liferay.commerce.constants.CommercePortletKeys" %><%@
page import="com.liferay.commerce.constants.CommerceWebKeys" %><%@
page import="com.liferay.commerce.currency.model.CommerceMoney" %><%@
page import="com.liferay.commerce.discount.CommerceDiscountValue" %><%@
page import="com.liferay.commerce.exception.CommerceOrderAccountLimitException" %><%@
page import="com.liferay.commerce.exception.CommerceOrderBillingAddressException" %><%@
page import="com.liferay.commerce.exception.CommerceOrderImporterTypeException" %><%@
page import="com.liferay.commerce.exception.CommerceOrderNoteContentException" %><%@
page import="com.liferay.commerce.exception.CommerceOrderPaymentMethodException" %><%@
page import="com.liferay.commerce.exception.CommerceOrderPurchaseOrderNumberException" %><%@
page import="com.liferay.commerce.exception.CommerceOrderRequestedDeliveryDateException" %><%@
page import="com.liferay.commerce.exception.CommerceOrderShippingAddressException" %><%@
page import="com.liferay.commerce.exception.CommerceOrderShippingMethodException" %><%@
page import="com.liferay.commerce.exception.CommerceOrderStatusException" %><%@
page import="com.liferay.commerce.exception.CommerceOrderValidatorException" %><%@
page import="com.liferay.commerce.exception.NoSuchOrderException" %><%@
page import="com.liferay.commerce.exception.NoSuchOrderNoteException" %><%@
page import="com.liferay.commerce.model.CommerceAddress" %><%@
page import="com.liferay.commerce.model.CommerceOrder" %><%@
page import="com.liferay.commerce.model.CommerceOrderNote" %><%@
page import="com.liferay.commerce.order.CommerceOrderValidatorResult" %><%@
page import="com.liferay.commerce.order.content.web.internal.constants.CommerceOrderFDSNames" %><%@
page import="com.liferay.commerce.order.content.web.internal.display.context.CommerceOrderContentDisplayContext" %><%@
page import="com.liferay.commerce.order.content.web.internal.importer.type.CSVCommerceOrderImporterTypeImpl" %><%@
page import="com.liferay.commerce.order.content.web.internal.portlet.CommerceOpenOrderContentPortlet" %><%@
page import="com.liferay.commerce.order.content.web.internal.portlet.CommerceOrderContentPortlet" %><%@
page import="com.liferay.commerce.order.importer.type.CommerceOrderImporterType" %><%@
page import="com.liferay.commerce.price.CommerceOrderPrice" %><%@
page import="com.liferay.commerce.pricing.constants.CommercePricingConstants" %><%@
page import="com.liferay.commerce.product.model.CommerceChannel" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.bean.BeanParamUtil" %><%@
page import="com.liferay.portal.kernel.dao.search.SearchContainer" %><%@
page import="com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.model.Country" %><%@
page import="com.liferay.portal.kernel.model.Region" %><%@
page import="com.liferay.portal.kernel.model.User" %><%@
page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %><%@
page import="com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder" %><%@
page import="com.liferay.portal.kernel.security.permission.ActionKeys" %><%@
page import="com.liferay.portal.kernel.servlet.SessionErrors" %><%@
page import="com.liferay.portal.kernel.servlet.SessionMessages" %><%@
page import="com.liferay.portal.kernel.util.CalendarFactoryUtil" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.FastDateFormatFactoryUtil" %><%@
page import="com.liferay.portal.kernel.util.GetterUtil" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.taglib.servlet.PipingServletResponseFactory" %>

<%@ page import="jakarta.portlet.PortletURL" %>

<%@ page import="java.math.BigDecimal" %>

<%@ page import="java.text.Format" %>

<%@ page import="java.util.ArrayList" %><%@
page import="java.util.Calendar" %><%@
page import="java.util.Collections" %><%@
page import="java.util.Date" %><%@
page import="java.util.HashMap" %><%@
page import="java.util.List" %><%@
page import="java.util.Map" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
String languageId = LanguageUtil.getLanguageId(locale);

String redirect = ParamUtil.getString(request, "redirect");

CommerceOrderContentDisplayContext commerceOrderContentDisplayContext = (CommerceOrderContentDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

SearchContainer<CommerceOrder> commerceOrderSearchContainer = commerceOrderContentDisplayContext.getSearchContainer();
%>