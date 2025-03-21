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
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/user" prefix="liferay-user" %>

<%@ page import="com.liferay.account.model.AccountEntry" %><%@
page import="com.liferay.commerce.constants.CommerceOrderPaymentConstants" %><%@
page import="com.liferay.commerce.constants.CommerceReturnConstants" %><%@
page import="com.liferay.commerce.constants.CommerceShipmentFDSNames" %><%@
page import="com.liferay.commerce.currency.model.CommerceCurrency" %><%@
page import="com.liferay.commerce.exception.CommerceOrderBillingAddressException" %><%@
page import="com.liferay.commerce.exception.CommerceOrderItemPriceException" %><%@
page import="com.liferay.commerce.exception.CommerceOrderItemQuantityException" %><%@
page import="com.liferay.commerce.exception.CommerceOrderItemRequestedDeliveryDateException" %><%@
page import="com.liferay.commerce.exception.CommerceOrderNoteContentException" %><%@
page import="com.liferay.commerce.exception.CommerceOrderPaymentMethodException" %><%@
page import="com.liferay.commerce.exception.CommerceOrderPriceException" %><%@
page import="com.liferay.commerce.exception.CommerceOrderPurchaseOrderNumberException" %><%@
page import="com.liferay.commerce.exception.CommerceOrderRequestedDeliveryDateException" %><%@
page import="com.liferay.commerce.exception.CommerceOrderShippingAddressException" %><%@
page import="com.liferay.commerce.exception.CommerceOrderShippingMethodException" %><%@
page import="com.liferay.commerce.exception.CommerceOrderStatusException" %><%@
page import="com.liferay.commerce.exception.CommerceOrderTypeExpirationDateException" %><%@
page import="com.liferay.commerce.exception.CommerceOrderValidatorException" %><%@
page import="com.liferay.commerce.exception.DuplicateCommerceOrderExternalReferenceCodeException" %><%@
page import="com.liferay.commerce.exception.DuplicateCommerceOrderTypeExternalReferenceCodeException" %><%@
page import="com.liferay.commerce.exception.NoSuchOrderException" %><%@
page import="com.liferay.commerce.exception.NoSuchOrderNoteException" %><%@
page import="com.liferay.commerce.model.CommerceAddress" %><%@
page import="com.liferay.commerce.model.CommerceOrder" %><%@
page import="com.liferay.commerce.model.CommerceOrderItem" %><%@
page import="com.liferay.commerce.model.CommerceOrderNote" %><%@
page import="com.liferay.commerce.model.CommerceOrderType" %><%@
page import="com.liferay.commerce.model.CommerceReturn" %><%@
page import="com.liferay.commerce.model.CommerceReturnItem" %><%@
page import="com.liferay.commerce.model.CommerceShipment" %><%@
page import="com.liferay.commerce.notification.model.CommerceNotificationQueueEntry" %><%@
page import="com.liferay.commerce.order.CommerceOrderValidatorResult" %><%@
page import="com.liferay.commerce.order.web.internal.constants.CommerceOrderFDSNames" %><%@
page import="com.liferay.commerce.order.web.internal.constants.CommerceOrderScreenNavigationConstants" %><%@
page import="com.liferay.commerce.order.web.internal.constants.CommerceOrderTypeScreenNavigationConstants" %><%@
page import="com.liferay.commerce.order.web.internal.constants.CommerceReturnFDSNames" %><%@
page import="com.liferay.commerce.order.web.internal.constants.CommerceReturnItemScreenNavigationConstants" %><%@
page import="com.liferay.commerce.order.web.internal.constants.CommerceReturnScreenNavigationConstants" %><%@
page import="com.liferay.commerce.order.web.internal.display.context.CommerceOrderEditDisplayContext" %><%@
page import="com.liferay.commerce.order.web.internal.display.context.CommerceOrderListDisplayContext" %><%@
page import="com.liferay.commerce.order.web.internal.display.context.CommerceOrderNoteEditDisplayContext" %><%@
page import="com.liferay.commerce.order.web.internal.display.context.CommerceOrderTypeDisplayContext" %><%@
page import="com.liferay.commerce.order.web.internal.display.context.CommerceOrderTypeQualifiersDisplayContext" %><%@
page import="com.liferay.commerce.order.web.internal.display.context.CommerceReturnEditDisplayContext" %><%@
page import="com.liferay.commerce.order.web.internal.display.context.CommerceReturnItemCommentEditDisplayContext" %><%@
page import="com.liferay.commerce.order.web.internal.display.context.CommerceReturnListDisplayContext" %><%@
page import="com.liferay.commerce.order.web.internal.security.permission.resource.CommerceOrderPermission" %><%@
page import="com.liferay.commerce.product.model.CPMeasurementUnit" %><%@
page import="com.liferay.commerce.term.model.CommerceTermEntry" %><%@
page import="com.liferay.object.exception.DuplicateObjectEntryExternalReferenceCodeException" %><%@
page import="com.liferay.object.exception.NoSuchObjectEntryException" %><%@
page import="com.liferay.object.model.ObjectEntry" %><%@
page import="com.liferay.petra.string.StringBundler" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.bean.BeanParamUtil" %><%@
page import="com.liferay.portal.kernel.comment.Comment" %><%@
page import="com.liferay.portal.kernel.comment.DiscussionComment" %><%@
page import="com.liferay.portal.kernel.comment.DuplicateCommentException" %><%@
page import="com.liferay.portal.kernel.dao.search.ResultRow" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.model.User" %><%@
page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %><%@
page import="com.liferay.portal.kernel.security.permission.ActionKeys" %><%@
page import="com.liferay.portal.kernel.util.ArrayUtil" %><%@
page import="com.liferay.portal.kernel.util.CalendarFactoryUtil" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.FastDateFormatFactoryUtil" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.StringUtil" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.portal.kernel.workflow.WorkflowConstants" %>

<%@ page import="jakarta.portlet.PortletURL" %>

<%@ page import="java.text.Format" %>

<%@ page import="java.util.Calendar" %><%@
page import="java.util.Collections" %><%@
page import="java.util.Date" %><%@
page import="java.util.HashMap" %><%@
page import="java.util.List" %><%@
page import="java.util.Map" %><%@
page import="java.util.Objects" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />