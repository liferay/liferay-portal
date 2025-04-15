<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/adaptive-media-image" prefix="liferay-adaptive-media" %><%@
taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/commerce-ui" prefix="commerce-ui" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/frontend-data-set" prefix="frontend-data-set" %><%@
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/react" prefix="react" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.account.constants.AccountActionKeys" %><%@
page import="com.liferay.account.model.AccountEntry" %><%@
page import="com.liferay.commerce.checkout.web.internal.constants.CommerceCheckoutFDSNames" %><%@
page import="com.liferay.commerce.checkout.web.internal.constants.CommerceCheckoutScreenNavigationConstants" %><%@
page import="com.liferay.commerce.checkout.web.internal.display.context.BaseAddressCheckoutStepDisplayContext" %><%@
page import="com.liferay.commerce.checkout.web.internal.display.context.CheckoutDisplayContext" %><%@
page import="com.liferay.commerce.checkout.web.internal.display.context.DeliveryGroupDisplayContext" %><%@
page import="com.liferay.commerce.checkout.web.internal.display.context.OrderConfirmationCheckoutStepDisplayContext" %><%@
page import="com.liferay.commerce.checkout.web.internal.display.context.OrderSummaryCheckoutStepDisplayContext" %><%@
page import="com.liferay.commerce.checkout.web.internal.display.context.PaymentMethodCheckoutStepDisplayContext" %><%@
page import="com.liferay.commerce.checkout.web.internal.display.context.PaymentProcessCheckoutStepDisplayContext" %><%@
page import="com.liferay.commerce.checkout.web.internal.display.context.ShippingMethodCheckoutStepDisplayContext" %><%@
page import="com.liferay.commerce.checkout.web.internal.display.context.TermCommerceCheckoutStepDisplayContext" %><%@
page import="com.liferay.commerce.constants.CommerceAddressConstants" %><%@
page import="com.liferay.commerce.constants.CommerceCheckoutWebKeys" %><%@
page import="com.liferay.commerce.constants.CommerceOrderPaymentConstants" %><%@
page import="com.liferay.commerce.constants.CommerceWebKeys" %><%@
page import="com.liferay.commerce.context.CommerceContext" %><%@
page import="com.liferay.commerce.currency.model.CommerceMoney" %><%@
page import="com.liferay.commerce.discount.CommerceDiscountValue" %><%@
page import="com.liferay.commerce.discount.exception.CommerceDiscountLimitationTimesException" %><%@
page import="com.liferay.commerce.discount.exception.NoSuchDiscountException" %><%@
page import="com.liferay.commerce.exception.CommerceAddressCityException" %><%@
page import="com.liferay.commerce.exception.CommerceAddressCountryException" %><%@
page import="com.liferay.commerce.exception.CommerceAddressNameException" %><%@
page import="com.liferay.commerce.exception.CommerceAddressStreetException" %><%@
page import="com.liferay.commerce.exception.CommerceAddressZipException" %><%@
page import="com.liferay.commerce.exception.CommerceOrderBillingAddressException" %><%@
page import="com.liferay.commerce.exception.CommerceOrderDefaultBillingAddressException" %><%@
page import="com.liferay.commerce.exception.CommerceOrderGuestCheckoutException" %><%@
page import="com.liferay.commerce.exception.CommerceOrderPaymentMethodException" %><%@
page import="com.liferay.commerce.exception.CommerceOrderShippingAddressException" %><%@
page import="com.liferay.commerce.exception.CommerceOrderShippingAndBillingException" %><%@
page import="com.liferay.commerce.exception.CommerceOrderShippingMethodException" %><%@
page import="com.liferay.commerce.model.CommerceAddress" %><%@
page import="com.liferay.commerce.model.CommerceOrder" %><%@
page import="com.liferay.commerce.model.CommerceOrderPayment" %><%@
page import="com.liferay.commerce.model.CommerceShippingMethod" %><%@
page import="com.liferay.commerce.model.CommerceShippingOption" %><%@
page import="com.liferay.commerce.order.CommerceOrderValidatorResult" %><%@
page import="com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel" %><%@
page import="com.liferay.commerce.price.CommerceOrderPrice" %><%@
page import="com.liferay.commerce.price.CommerceProductPrice" %><%@
page import="com.liferay.commerce.pricing.constants.CommercePricingConstants" %><%@
page import="com.liferay.commerce.product.model.CPDefinition" %><%@
page import="com.liferay.commerce.product.model.CPInstance" %><%@
page import="com.liferay.commerce.term.model.CommerceTermEntry" %><%@
page import="com.liferay.commerce.util.CommerceCheckoutStep" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.bean.BeanParamUtil" %><%@
page import="com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.model.Country" %><%@
page import="com.liferay.portal.kernel.model.Region" %><%@
page import="com.liferay.portal.kernel.sanitizer.SanitizerUtil" %><%@
page import="com.liferay.portal.kernel.util.BigDecimalUtil" %><%@
page import="com.liferay.portal.kernel.util.CalendarFactoryUtil" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.FastDateFormatFactoryUtil" %><%@
page import="com.liferay.portal.kernel.util.GetterUtil" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.KeyValuePair" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %>

<%@ page import="java.math.BigDecimal" %>

<%@ page import="java.text.Format" %>

<%@ page import="java.util.Calendar" %><%@
page import="java.util.Date" %><%@
page import="java.util.Iterator" %><%@
page import="java.util.List" %><%@
page import="java.util.Map" %><%@
page import="java.util.Objects" %><%@
page import="java.util.StringJoiner" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />