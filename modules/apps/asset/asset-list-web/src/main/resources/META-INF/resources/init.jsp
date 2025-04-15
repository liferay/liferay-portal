<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/asset" prefix="liferay-asset" %><%@
taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/ddm" prefix="liferay-ddm" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/portal-workflow" prefix="liferay-portal-workflow" %><%@
taglib uri="http://liferay.com/tld/react" prefix="react" %><%@
taglib uri="http://liferay.com/tld/site-navigation" prefix="liferay-site-navigation" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil" %><%@
page import="com.liferay.asset.kernel.exception.DuplicateQueryRuleException" %><%@
page import="com.liferay.asset.kernel.model.AssetEntry" %><%@
page import="com.liferay.asset.kernel.model.AssetRenderer" %><%@
page import="com.liferay.asset.kernel.model.AssetRendererFactory" %><%@
page import="com.liferay.asset.kernel.model.ClassType" %><%@
page import="com.liferay.asset.kernel.model.ClassTypeField" %><%@
page import="com.liferay.asset.kernel.model.ClassTypeReader" %><%@
page import="com.liferay.asset.kernel.service.AssetEntryServiceUtil" %><%@
page import="com.liferay.asset.list.constants.AssetListEntryTypeConstants" %><%@
page import="com.liferay.asset.list.constants.AssetListFormConstants" %><%@
page import="com.liferay.asset.list.exception.RequiredAssetListEntryException" %><%@
page import="com.liferay.asset.list.model.AssetListEntry" %><%@
page import="com.liferay.asset.list.model.AssetListEntryAssetEntryRel" %><%@
page import="com.liferay.asset.list.web.internal.constants.AssetListWebKeys" %><%@
page import="com.liferay.asset.list.web.internal.display.context.AssetListDisplayContext" %><%@
page import="com.liferay.asset.list.web.internal.display.context.AssetListEntryUsagesDisplayContext" %><%@
page import="com.liferay.asset.list.web.internal.display.context.AssetListEntryUsagesManagementToolbarDisplayContext" %><%@
page import="com.liferay.asset.list.web.internal.display.context.AssetListItemsDisplayContext" %><%@
page import="com.liferay.asset.list.web.internal.display.context.AssetListManagementToolbarDisplayContext" %><%@
page import="com.liferay.asset.list.web.internal.display.context.EditAssetListDisplayContext" %><%@
page import="com.liferay.asset.list.web.internal.display.context.InfoCollectionProviderDisplayContext" %><%@
page import="com.liferay.asset.list.web.internal.display.context.InfoCollectionProviderItemsDisplayContext" %><%@
page import="com.liferay.asset.list.web.internal.display.context.SelectStructureFieldDisplayContext" %><%@
page import="com.liferay.asset.list.web.internal.frontend.taglib.clay.servlet.taglib.AssetListEntryVerticalCard" %><%@
page import="com.liferay.asset.list.web.internal.servlet.taglib.util.AssetListEntryVariationActionDropdownItemsProvider" %><%@
page import="com.liferay.asset.list.web.internal.servlet.taglib.util.InfoCollectionProviderActionDropdownItems" %><%@
page import="com.liferay.asset.list.web.internal.servlet.taglib.util.ListItemsActionDropdownItems" %><%@
page import="com.liferay.asset.list.web.internal.servlet.taglib.util.ScopeActionDropdownItemsProvider" %><%@
page import="com.liferay.asset.util.comparator.AssetRendererFactoryTypeNameComparator" %><%@
page import="com.liferay.asset.util.comparator.ClassTypeNameComparator" %><%@
page import="com.liferay.dynamic.data.mapping.model.DDMStructure" %><%@
page import="com.liferay.dynamic.data.mapping.storage.Field" %><%@
page import="com.liferay.info.collection.provider.BetaInfoCollectionProvider" %><%@
page import="com.liferay.info.collection.provider.DeprecatedInfoCollectionProvider" %><%@
page import="com.liferay.info.field.InfoFieldValue" %><%@
page import="com.liferay.info.item.InfoItemFieldValues" %><%@
page import="com.liferay.info.item.provider.InfoItemFieldValuesProvider" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.dao.search.SearchContainer" %><%@
page import="com.liferay.portal.kernel.exception.NoSuchModelException" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.model.ClassName" %><%@
page import="com.liferay.portal.kernel.model.Group" %><%@
page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %><%@
page import="com.liferay.portal.kernel.security.permission.ResourceActionsUtil" %><%@
page import="com.liferay.portal.kernel.service.ClassNameLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.util.GetterUtil" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.KeyValuePair" %><%@
page import="com.liferay.portal.kernel.util.KeyValuePairComparator" %><%@
page import="com.liferay.portal.kernel.util.ListUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.portal.kernel.util.StringUtil" %><%@
page import="com.liferay.portal.kernel.util.UnicodeProperties" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.portal.kernel.workflow.WorkflowConstants" %><%@
page import="com.liferay.site.navigation.taglib.servlet.taglib.util.BreadcrumbEntriesUtil" %><%@
page import="com.liferay.taglib.search.ResultRow" %>

<%@ page import="jakarta.portlet.PortletURL" %>

<%@ page import="java.io.Serializable" %>

<%@ page import="java.util.ArrayList" %><%@
page import="java.util.Arrays" %><%@
page import="java.util.Date" %><%@
page import="java.util.List" %><%@
page import="java.util.Map" %><%@
page import="java.util.Objects" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
AssetListDisplayContext assetListDisplayContext = (AssetListDisplayContext)request.getAttribute(AssetListWebKeys.ASSET_LIST_DISPLAY_CONTEXT);
EditAssetListDisplayContext editAssetListDisplayContext = (EditAssetListDisplayContext)request.getAttribute(AssetListWebKeys.EDIT_ASSET_LIST_DISPLAY_CONTEXT);
%>