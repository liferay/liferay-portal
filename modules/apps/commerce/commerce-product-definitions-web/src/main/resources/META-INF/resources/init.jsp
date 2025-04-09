<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/asset" prefix="liferay-asset" %><%@
taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/commerce-ui" prefix="commerce-ui" %><%@
taglib uri="http://liferay.com/tld/expando" prefix="liferay-expando" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/frontend-data-set" prefix="frontend-data-set" %><%@
taglib uri="http://liferay.com/tld/item-selector" prefix="liferay-item-selector" %><%@
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/react" prefix="react" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.asset.kernel.model.AssetRenderer" %><%@
page import="com.liferay.asset.kernel.model.AssetVocabularyConstants" %><%@
page import="com.liferay.commerce.constants.CPDefinitionInventoryConstants" %><%@
page import="com.liferay.commerce.constants.CommercePriceConstants" %><%@
page import="com.liferay.commerce.constants.CommerceWebKeys" %><%@
page import="com.liferay.commerce.context.CommerceContext" %><%@
page import="com.liferay.commerce.currency.model.CommerceCurrency" %><%@
page import="com.liferay.commerce.exception.CPDefinitionInventoryQuantityException" %><%@
page import="com.liferay.commerce.inventory.CPDefinitionInventoryEngine" %><%@
page import="com.liferay.commerce.model.CPDAvailabilityEstimate" %><%@
page import="com.liferay.commerce.model.CPDefinitionInventory" %><%@
page import="com.liferay.commerce.model.CommerceAvailabilityEstimate" %><%@
page import="com.liferay.commerce.price.list.exception.CommercePriceEntryPriceException" %><%@
page import="com.liferay.commerce.pricing.exception.CommerceUndefinedBasePriceListException" %><%@
page import="com.liferay.commerce.product.constants.CPAttachmentFileEntryConstants" %><%@
page import="com.liferay.commerce.product.constants.CPConstants" %><%@
page import="com.liferay.commerce.product.constants.CPInstanceConstants" %><%@
page import="com.liferay.commerce.product.constants.CPMeasurementUnitConstants" %><%@
page import="com.liferay.commerce.product.constants.CPPortletKeys" %><%@
page import="com.liferay.commerce.product.constants.CPWebKeys" %><%@
page import="com.liferay.commerce.product.definitions.web.internal.constants.CPConfigurationFDSNames" %><%@
page import="com.liferay.commerce.product.definitions.web.internal.constants.CommerceProductFDSNames" %><%@
page import="com.liferay.commerce.product.definitions.web.internal.display.context.CPAttachmentFileEntriesDisplayContext" %><%@
page import="com.liferay.commerce.product.definitions.web.internal.display.context.CPConfigurationListDisplayContext" %><%@
page import="com.liferay.commerce.product.definitions.web.internal.display.context.CPConfigurationListQualifiersDisplayContext" %><%@
page import="com.liferay.commerce.product.definitions.web.internal.display.context.CPDefinitionConfigurationDisplayContext" %><%@
page import="com.liferay.commerce.product.definitions.web.internal.display.context.CPDefinitionDisplayLayoutDisplayContext" %><%@
page import="com.liferay.commerce.product.definitions.web.internal.display.context.CPDefinitionLinkDisplayContext" %><%@
page import="com.liferay.commerce.product.definitions.web.internal.display.context.CPDefinitionOptionRelDisplayContext" %><%@
page import="com.liferay.commerce.product.definitions.web.internal.display.context.CPDefinitionOptionValueRelDisplayContext" %><%@
page import="com.liferay.commerce.product.definitions.web.internal.display.context.CPDefinitionSpecificationOptionValueDisplayContext" %><%@
page import="com.liferay.commerce.product.definitions.web.internal.display.context.CPDefinitionsDisplayContext" %><%@
page import="com.liferay.commerce.product.definitions.web.internal.display.context.CPInstanceDisplayContext" %><%@
page import="com.liferay.commerce.product.definitions.web.internal.display.context.CPInstanceUnitOfMeasureDisplayContext" %><%@
page import="com.liferay.commerce.product.definitions.web.internal.security.permission.resource.CommerceCatalogPermission" %><%@
page import="com.liferay.commerce.product.exception.CPAttachmentFileEntryExpirationDateException" %><%@
page import="com.liferay.commerce.product.exception.CPConfigurationEntryQuantityException" %><%@
page import="com.liferay.commerce.product.exception.CPDefinitionExpirationDateException" %><%@
page import="com.liferay.commerce.product.exception.CPDefinitionIgnoreSKUCombinationsException" %><%@
page import="com.liferay.commerce.product.exception.CPDefinitionLinkExpirationDateException" %><%@
page import="com.liferay.commerce.product.exception.CPDefinitionMetaDescriptionException" %><%@
page import="com.liferay.commerce.product.exception.CPDefinitionMetaKeywordsException" %><%@
page import="com.liferay.commerce.product.exception.CPDefinitionMetaTitleException" %><%@
page import="com.liferay.commerce.product.exception.CPDefinitionNameDefaultLanguageException" %><%@
page import="com.liferay.commerce.product.exception.CPDefinitionOptionRelPriceTypeException" %><%@
page import="com.liferay.commerce.product.exception.CPDefinitionOptionSKUContributorException" %><%@
page import="com.liferay.commerce.product.exception.CPDefinitionOptionValueRelCPInstanceException" %><%@
page import="com.liferay.commerce.product.exception.CPDefinitionOptionValueRelKeyException" %><%@
page import="com.liferay.commerce.product.exception.CPDefinitionOptionValueRelPriceException" %><%@
page import="com.liferay.commerce.product.exception.CPDefinitionOptionValueRelQuantityException" %><%@
page import="com.liferay.commerce.product.exception.CPDefinitionProductTypeNameException" %><%@
page import="com.liferay.commerce.product.exception.CPDefinitionSpecificationOptionValueKeyException" %><%@
page import="com.liferay.commerce.product.exception.CPDisplayLayoutEntryException" %><%@
page import="com.liferay.commerce.product.exception.CPDisplayLayoutEntryUuidException" %><%@
page import="com.liferay.commerce.product.exception.CPInstanceJsonException" %><%@
page import="com.liferay.commerce.product.exception.CPInstanceMaxPriceValueException" %><%@
page import="com.liferay.commerce.product.exception.CPInstancePriceException" %><%@
page import="com.liferay.commerce.product.exception.CPInstanceReplacementCPInstanceUuidException" %><%@
page import="com.liferay.commerce.product.exception.CPInstanceSkuException" %><%@
page import="com.liferay.commerce.product.exception.CPInstanceUnitOfMeasureIncrementalOrderQuantityException" %><%@
page import="com.liferay.commerce.product.exception.CPInstanceUnitOfMeasurePriceException" %><%@
page import="com.liferay.commerce.product.exception.CPInstanceUnitOfMeasureQuantityException" %><%@
page import="com.liferay.commerce.product.exception.CPInstanceUnitOfMeasureRateException" %><%@
page import="com.liferay.commerce.product.exception.DuplicateCPAttachmentFileEntryException" %><%@
page import="com.liferay.commerce.product.exception.DuplicateCPInstanceException" %><%@
page import="com.liferay.commerce.product.exception.DuplicateCPInstanceExternalReferenceCodeException" %><%@
page import="com.liferay.commerce.product.exception.DuplicateCPInstanceUnitOfMeasureKeyException" %><%@
page import="com.liferay.commerce.product.exception.DuplicateCProductExternalReferenceCodeException" %><%@
page import="com.liferay.commerce.product.exception.NoSuchCPAttachmentFileEntryException" %><%@
page import="com.liferay.commerce.product.exception.NoSuchCPDefinitionException" %><%@
page import="com.liferay.commerce.product.exception.NoSuchCPDefinitionLinkException" %><%@
page import="com.liferay.commerce.product.exception.NoSuchCPDefinitionOptionRelException" %><%@
page import="com.liferay.commerce.product.exception.NoSuchCPDefinitionOptionValueRelException" %><%@
page import="com.liferay.commerce.product.exception.NoSuchCPInstanceException" %><%@
page import="com.liferay.commerce.product.exception.NoSuchCProductException" %><%@
page import="com.liferay.commerce.product.exception.NoSuchCatalogException" %><%@
page import="com.liferay.commerce.product.exception.NoSuchSkuContributorCPDefinitionOptionRelException" %><%@
page import="com.liferay.commerce.product.model.CPAttachmentFileEntry" %><%@
page import="com.liferay.commerce.product.model.CPConfigurationEntry" %><%@
page import="com.liferay.commerce.product.model.CPConfigurationList" %><%@
page import="com.liferay.commerce.product.model.CPDefinition" %><%@
page import="com.liferay.commerce.product.model.CPDefinitionLink" %><%@
page import="com.liferay.commerce.product.model.CPDefinitionOptionRel" %><%@
page import="com.liferay.commerce.product.model.CPDefinitionOptionValueRel" %><%@
page import="com.liferay.commerce.product.model.CPDefinitionSpecificationOptionValue" %><%@
page import="com.liferay.commerce.product.model.CPDisplayLayout" %><%@
page import="com.liferay.commerce.product.model.CPInstance" %><%@
page import="com.liferay.commerce.product.model.CPInstanceUnitOfMeasure" %><%@
page import="com.liferay.commerce.product.model.CPOptionCategory" %><%@
page import="com.liferay.commerce.product.model.CPSpecificationOption" %><%@
page import="com.liferay.commerce.product.model.CPTaxCategory" %><%@
page import="com.liferay.commerce.product.model.CProduct" %><%@
page import="com.liferay.commerce.product.model.CommerceCatalog" %><%@
page import="com.liferay.commerce.product.option.CommerceOptionType" %><%@
page import="com.liferay.commerce.product.servlet.taglib.ui.constants.CPConfigurationListScreenNavigationConstants" %><%@
page import="com.liferay.commerce.product.servlet.taglib.ui.constants.CPDefinitionScreenNavigationConstants" %><%@
page import="com.liferay.commerce.product.servlet.taglib.ui.constants.CPInstanceScreenNavigationConstants" %><%@
page import="com.liferay.commerce.product.type.virtual.constants.VirtualCPTypeConstants" %><%@
page import="com.liferay.commerce.stock.activity.CommerceLowStockActivity" %><%@
page import="com.liferay.commerce.util.CommerceUtil" %><%@
page import="com.liferay.document.library.kernel.exception.NoSuchFileEntryException" %><%@
page import="com.liferay.friendly.url.exception.FriendlyURLLengthException" %><%@
page import="com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItem" %><%@
page import="com.liferay.frontend.taglib.clay.servlet.taglib.util.SelectOption" %><%@
page import="com.liferay.info.collection.provider.RelatedInfoItemCollectionProvider" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.bean.BeanParamUtil" %><%@
page import="com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.model.Layout" %><%@
page import="com.liferay.portal.kernel.model.Portlet" %><%@
page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %><%@
page import="com.liferay.portal.kernel.security.permission.ActionKeys" %><%@
page import="com.liferay.portal.kernel.service.PortletLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.HttpComponentsUtil" %><%@
page import="com.liferay.portal.kernel.util.LocaleUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.portal.kernel.util.StringUtil" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.portal.kernel.workflow.WorkflowConstants" %><%@
page import="com.liferay.taglib.servlet.PipingServletResponseFactory" %>

<%@ page import="java.math.BigDecimal" %>

<%@ page import="java.text.NumberFormat" %>

<%@ page import="java.util.ArrayList" %><%@
page import="java.util.Arrays" %><%@
page import="java.util.Calendar" %><%@
page import="java.util.List" %><%@
page import="java.util.Map" %><%@
page import="java.util.Objects" %><%@
page import="java.util.StringJoiner" %><%@
page import="java.util.TimeZone" %>

<%@ page import="javax.portlet.PortletURL" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
String languageId = LanguageUtil.getLanguageId(locale);
%>