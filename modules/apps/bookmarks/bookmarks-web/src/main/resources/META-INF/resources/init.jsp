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
taglib uri="http://liferay.com/tld/expando" prefix="liferay-expando" %><%@
taglib uri="http://liferay.com/tld/export-import-changeset" prefix="liferay-export-import-changeset" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/ratings" prefix="liferay-ratings" %><%@
taglib uri="http://liferay.com/tld/security" prefix="liferay-security" %><%@
taglib uri="http://liferay.com/tld/site-navigation" prefix="liferay-site-navigation" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/trash" prefix="liferay-trash" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil" %><%@
page import="com.liferay.asset.kernel.model.AssetEntry" %><%@
page import="com.liferay.asset.kernel.model.AssetRenderer" %><%@
page import="com.liferay.asset.kernel.model.AssetRendererFactory" %><%@
page import="com.liferay.asset.kernel.model.AssetVocabularyConstants" %><%@
page import="com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil" %><%@
page import="com.liferay.bookmarks.configuration.BookmarksGroupServiceOverriddenConfiguration" %><%@
page import="com.liferay.bookmarks.constants.BookmarksConstants" %><%@
page import="com.liferay.bookmarks.constants.BookmarksFolderConstants" %><%@
page import="com.liferay.bookmarks.constants.BookmarksPortletKeys" %><%@
page import="com.liferay.bookmarks.constants.BookmarksWebKeys" %><%@
page import="com.liferay.bookmarks.exception.EntryURLException" %><%@
page import="com.liferay.bookmarks.exception.FolderNameException" %><%@
page import="com.liferay.bookmarks.exception.NoSuchEntryException" %><%@
page import="com.liferay.bookmarks.exception.NoSuchFolderException" %><%@
page import="com.liferay.bookmarks.model.BookmarksEntry" %><%@
page import="com.liferay.bookmarks.model.BookmarksFolder" %><%@
page import="com.liferay.bookmarks.service.BookmarksEntryServiceUtil" %><%@
page import="com.liferay.bookmarks.service.BookmarksFolderLocalServiceUtil" %><%@
page import="com.liferay.bookmarks.service.BookmarksFolderServiceUtil" %><%@
page import="com.liferay.bookmarks.web.internal.dao.search.BookmarksResultRowSplitter" %><%@
page import="com.liferay.bookmarks.web.internal.display.context.BookmarksConfigurationDisplayContext" %><%@
page import="com.liferay.bookmarks.web.internal.display.context.BookmarksDisplayContext" %><%@
page import="com.liferay.bookmarks.web.internal.display.context.BookmarksManagementToolbarDisplayContext" %><%@
page import="com.liferay.bookmarks.web.internal.portlet.util.BookmarksUtil" %><%@
page import="com.liferay.bookmarks.web.internal.search.EntriesChecker" %><%@
page import="com.liferay.bookmarks.web.internal.search.EntriesMover" %><%@
page import="com.liferay.bookmarks.web.internal.security.permission.resource.BookmarksEntryPermission" %><%@
page import="com.liferay.bookmarks.web.internal.security.permission.resource.BookmarksFolderPermission" %><%@
page import="com.liferay.frontend.taglib.clay.servlet.taglib.util.JSPNavigationItemList" %><%@
page import="com.liferay.petra.string.CharPool" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil" %><%@
page import="com.liferay.portal.kernel.bean.BeanParamUtil" %><%@
page import="com.liferay.portal.kernel.dao.search.ResultRow" %><%@
page import="com.liferay.portal.kernel.dao.search.SearchContainer" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %><%@
page import="com.liferay.portal.kernel.portlet.PortalPreferences" %><%@
page import="com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil" %><%@
page import="com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder" %><%@
page import="com.liferay.portal.kernel.security.permission.ActionKeys" %><%@
page import="com.liferay.portal.kernel.service.permission.GroupPermissionUtil" %><%@
page import="com.liferay.portal.kernel.settings.GroupServiceSettingsLocator" %><%@
page import="com.liferay.portal.kernel.util.ArrayUtil" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.FastDateFormatFactoryUtil" %><%@
page import="com.liferay.portal.kernel.util.GetterUtil" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.KeyValuePair" %><%@
page import="com.liferay.portal.kernel.util.KeyValuePairComparator" %><%@
page import="com.liferay.portal.kernel.util.ListUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.portal.kernel.util.SetUtil" %><%@
page import="com.liferay.portal.kernel.util.StringUtil" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.portal.kernel.workflow.WorkflowConstants" %><%@
page import="com.liferay.site.navigation.taglib.servlet.taglib.util.BreadcrumbEntriesUtil" %><%@
page import="com.liferay.staging.StagingGroupHelper" %><%@
page import="com.liferay.staging.StagingGroupHelperUtil" %><%@
page import="com.liferay.subscription.service.SubscriptionLocalServiceUtil" %>

<%@ page import="jakarta.portlet.PortletRequest" %>

<%@ page import="java.text.Format" %>

<%@ page import="java.util.ArrayList" %><%@
page import="java.util.Arrays" %><%@
page import="java.util.List" %><%@
page import="java.util.Map" %><%@
page import="java.util.Objects" %><%@
page import="java.util.Set" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<liferay-trash:defineObjects />

<portlet:defineObjects />