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
taglib uri="http://liferay.com/tld/react" prefix="react" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.account.model.AccountEntry" %><%@
page import="com.liferay.commerce.constants.CommercePriceConstants" %><%@
page import="com.liferay.commerce.currency.model.CommerceCurrency" %><%@
page import="com.liferay.commerce.discount.constants.CommerceDiscountConstants" %><%@
page import="com.liferay.commerce.discount.exception.CommerceDiscountAmountException" %><%@
page import="com.liferay.commerce.discount.exception.CommerceDiscountCouponCodeException" %><%@
page import="com.liferay.commerce.discount.exception.CommerceDiscountExpirationDateException" %><%@
page import="com.liferay.commerce.discount.exception.CommerceDiscountMaxPriceValueException" %><%@
page import="com.liferay.commerce.discount.exception.CommerceDiscountMinPriceValueException" %><%@
page import="com.liferay.commerce.discount.exception.CommerceDiscountRuleTypeSettingsException" %><%@
page import="com.liferay.commerce.discount.exception.DuplicateCommerceDiscountException" %><%@
page import="com.liferay.commerce.discount.exception.DuplicateCommerceDiscountExternalReferenceCodeException" %><%@
page import="com.liferay.commerce.discount.model.CommerceDiscount" %><%@
page import="com.liferay.commerce.discount.model.CommerceDiscountRule" %><%@
page import="com.liferay.commerce.discount.rule.type.CommerceDiscountRuleType" %><%@
page import="com.liferay.commerce.discount.rule.type.CommerceDiscountRuleTypeJSPContributor" %><%@
page import="com.liferay.commerce.discount.target.CommerceDiscountTarget" %><%@
page import="com.liferay.commerce.price.list.constants.CommercePriceListConstants" %><%@
page import="com.liferay.commerce.price.list.exception.CommercePriceEntryPriceException" %><%@
page import="com.liferay.commerce.price.list.exception.CommercePriceListCurrencyException" %><%@
page import="com.liferay.commerce.price.list.exception.CommercePriceListExpirationDateException" %><%@
page import="com.liferay.commerce.price.list.exception.CommercePriceListMaxPriceValueException" %><%@
page import="com.liferay.commerce.price.list.exception.CommercePriceListMinPriceValueException" %><%@
page import="com.liferay.commerce.price.list.exception.CommercePriceListParentPriceListGroupIdException" %><%@
page import="com.liferay.commerce.price.list.exception.CommerceTierPriceEntryMinQuantityException" %><%@
page import="com.liferay.commerce.price.list.exception.CommerceTierPriceEntryPriceException" %><%@
page import="com.liferay.commerce.price.list.exception.CommerceTierPriceEntryQuantityException" %><%@
page import="com.liferay.commerce.price.list.exception.DuplicateCommercePriceEntryException" %><%@
page import="com.liferay.commerce.price.list.exception.DuplicateCommercePriceEntryExternalReferenceCodeException" %><%@
page import="com.liferay.commerce.price.list.exception.DuplicateCommercePriceListExternalReferenceCodeException" %><%@
page import="com.liferay.commerce.price.list.exception.DuplicateCommerceTierPriceEntryException" %><%@
page import="com.liferay.commerce.price.list.exception.DuplicateCommerceTierPriceEntryExternalReferenceCodeException" %><%@
page import="com.liferay.commerce.price.list.model.CommercePriceEntry" %><%@
page import="com.liferay.commerce.price.list.model.CommercePriceList" %><%@
page import="com.liferay.commerce.price.list.model.CommerceTierPriceEntry" %><%@
page import="com.liferay.commerce.pricing.constants.CommercePriceModifierConstants" %><%@
page import="com.liferay.commerce.pricing.exception.CommercePriceModifierAmountException" %><%@
page import="com.liferay.commerce.pricing.exception.CommercePriceModifierExpirationDateException" %><%@
page import="com.liferay.commerce.pricing.exception.DuplicateCommercePricingClassExternalReferenceCodeException" %><%@
page import="com.liferay.commerce.pricing.exception.NoSuchPricingClassException" %><%@
page import="com.liferay.commerce.pricing.model.CommercePriceModifier" %><%@
page import="com.liferay.commerce.pricing.model.CommercePricingClass" %><%@
page import="com.liferay.commerce.pricing.type.CommercePriceModifierType" %><%@
page import="com.liferay.commerce.pricing.web.internal.constants.CommerceDiscountScreenNavigationConstants" %><%@
page import="com.liferay.commerce.pricing.web.internal.constants.CommercePriceListScreenNavigationConstants" %><%@
page import="com.liferay.commerce.pricing.web.internal.constants.CommercePricingClassScreenNavigationConstants" %><%@
page import="com.liferay.commerce.pricing.web.internal.constants.CommercePricingFDSNames" %><%@
page import="com.liferay.commerce.pricing.web.internal.display.context.AddedAllCommerceDiscountRuleDisplayContext" %><%@
page import="com.liferay.commerce.pricing.web.internal.display.context.AddedAnyCommerceDiscountRuleDisplayContext" %><%@
page import="com.liferay.commerce.pricing.web.internal.display.context.CPDefinitionPricingClassDisplayContext" %><%@
page import="com.liferay.commerce.pricing.web.internal.display.context.CPInstanceCommercePriceEntryDisplayContext" %><%@
page import="com.liferay.commerce.pricing.web.internal.display.context.CPInstanceCommerceTierPriceEntryDisplayContext" %><%@
page import="com.liferay.commerce.pricing.web.internal.display.context.CartTotalCommerceDiscountRuleDisplayContext" %><%@
page import="com.liferay.commerce.pricing.web.internal.display.context.CommerceChannelAccountEntryRelDisplayContext" %><%@
page import="com.liferay.commerce.pricing.web.internal.display.context.CommerceDiscountDisplayContext" %><%@
page import="com.liferay.commerce.pricing.web.internal.display.context.CommerceDiscountQualifiersDisplayContext" %><%@
page import="com.liferay.commerce.pricing.web.internal.display.context.CommerceDiscountRuleDisplayContext" %><%@
page import="com.liferay.commerce.pricing.web.internal.display.context.CommercePriceEntryDisplayContext" %><%@
page import="com.liferay.commerce.pricing.web.internal.display.context.CommercePriceListDisplayContext" %><%@
page import="com.liferay.commerce.pricing.web.internal.display.context.CommercePriceListQualifiersDisplayContext" %><%@
page import="com.liferay.commerce.pricing.web.internal.display.context.CommercePricingClassCPDefinitionDisplayContext" %><%@
page import="com.liferay.commerce.pricing.web.internal.display.context.CommercePricingClassDiscountDisplayContext" %><%@
page import="com.liferay.commerce.pricing.web.internal.display.context.CommercePricingClassDisplayContext" %><%@
page import="com.liferay.commerce.pricing.web.internal.display.context.CommercePricingClassPriceListDisplayContext" %><%@
page import="com.liferay.commerce.pricing.web.internal.display.context.CommerceTierCommercePriceEntryDisplayContext" %><%@
page import="com.liferay.commerce.product.constants.CommerceChannelAccountEntryRelConstants" %><%@
page import="com.liferay.commerce.product.exception.DuplicateCommerceChannelAccountEntryRelException" %><%@
page import="com.liferay.commerce.product.exception.NoSuchCatalogException" %><%@
page import="com.liferay.commerce.product.model.CPDefinition" %><%@
page import="com.liferay.commerce.product.model.CPInstance" %><%@
page import="com.liferay.commerce.product.model.CPInstanceUnitOfMeasure" %><%@
page import="com.liferay.commerce.product.model.CProduct" %><%@
page import="com.liferay.commerce.product.model.CommerceCatalog" %><%@
page import="com.liferay.commerce.product.model.CommerceChannel" %><%@
page import="com.liferay.commerce.product.model.CommerceChannelAccountEntryRel" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.bean.BeanParamUtil" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %><%@
page import="com.liferay.portal.kernel.security.permission.ActionKeys" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.LocaleUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.StringUtil" %><%@
page import="com.liferay.portal.kernel.util.UnicodeProperties" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.portal.kernel.workflow.WorkflowConstants" %><%@
page import="com.liferay.taglib.servlet.PipingServletResponseFactory" %><%@
page import="com.liferay.taglib.util.CustomAttributesUtil" %>

<%@ page import="jakarta.portlet.PortletURL" %>

<%@ page import="java.math.BigDecimal" %>

<%@ page import="java.util.List" %><%@
page import="java.util.Locale" %><%@
page import="java.util.Objects" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />