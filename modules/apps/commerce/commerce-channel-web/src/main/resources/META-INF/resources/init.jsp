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
taglib uri="http://liferay.com/tld/react" prefix="react" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.account.constants.AccountConstants" %><%@
page import="com.liferay.account.exception.AccountEntryStatusException" %><%@
page import="com.liferay.account.exception.AccountEntryTypeException" %><%@
page import="com.liferay.account.model.AccountEntry" %><%@
page import="com.liferay.commerce.channel.web.internal.constants.CommerceChannelFDSNames" %><%@
page import="com.liferay.commerce.channel.web.internal.constants.CommerceChannelScreenNavigationConstants" %><%@
page import="com.liferay.commerce.channel.web.internal.display.context.CommerceChannelAccountEntryQualifiersDisplayContext" %><%@
page import="com.liferay.commerce.channel.web.internal.display.context.CommerceChannelCommerceCurrencyDisplayContext" %><%@
page import="com.liferay.commerce.channel.web.internal.display.context.CommerceChannelCountryDisplayContext" %><%@
page import="com.liferay.commerce.channel.web.internal.display.context.CommerceChannelDisplayContext" %><%@
page import="com.liferay.commerce.channel.web.internal.display.context.SiteCommerceChannelTypeDisplayContext" %><%@
page import="com.liferay.commerce.constants.CommerceOrderConstants" %><%@
page import="com.liferay.commerce.currency.model.CommerceCurrency" %><%@
page import="com.liferay.commerce.pricing.constants.CommercePricingConstants" %><%@
page import="com.liferay.commerce.product.channel.CommerceChannelType" %><%@
page import="com.liferay.commerce.product.constants.CommerceChannelConstants" %><%@
page import="com.liferay.commerce.product.exception.DuplicateCommerceChannelAccountEntryIdException" %><%@
page import="com.liferay.commerce.product.exception.DuplicateCommerceChannelExternalReferenceCodeException" %><%@
page import="com.liferay.commerce.product.exception.NoSuchChannelException" %><%@
page import="com.liferay.commerce.product.model.CPTaxCategory" %><%@
page import="com.liferay.commerce.product.model.CommerceChannel" %><%@
page import="com.liferay.document.library.kernel.exception.FileExtensionException" %><%@
page import="com.liferay.document.library.kernel.exception.InvalidFileException" %><%@
page import="com.liferay.marketplace.constants.MarketplaceActionKeys" %><%@
page import="com.liferay.marketplace.constants.MarketplacePortletKeys" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.bean.BeanParamUtil" %><%@
page import="com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.model.Group" %><%@
page import="com.liferay.portal.kernel.model.WorkflowDefinitionLink" %><%@
page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %><%@
page import="com.liferay.portal.kernel.repository.model.FileEntry" %><%@
page import="com.liferay.portal.kernel.security.permission.ActionKeys" %><%@
page import="com.liferay.portal.kernel.service.permission.PortletPermissionUtil" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.portal.kernel.workflow.WorkflowDefinition" %>

<%@ page import="java.util.ArrayList" %><%@
page import="java.util.Arrays" %><%@
page import="java.util.HashMap" %><%@
page import="java.util.List" %><%@
page import="java.util.Map" %><%@
page import="java.util.Objects" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />