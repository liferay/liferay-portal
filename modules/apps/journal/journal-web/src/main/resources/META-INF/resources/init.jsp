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
taglib uri="http://liferay.com/tld/data-engine" prefix="liferay-data-engine" %><%@
taglib uri="http://liferay.com/tld/expando" prefix="liferay-expando" %><%@
taglib uri="http://liferay.com/tld/friendly-url" prefix="liferay-friendly-url" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/journal" prefix="liferay-journal" %><%@
taglib uri="http://liferay.com/tld/layout" prefix="liferay-layout" %><%@
taglib uri="http://liferay.com/tld/portal-workflow" prefix="liferay-portal-workflow" %><%@
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/react" prefix="react" %><%@
taglib uri="http://liferay.com/tld/site-navigation" prefix="liferay-site-navigation" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/trash" prefix="liferay-trash" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/user" prefix="liferay-user" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.asset.auto.tagger.configuration.AssetAutoTaggerConfiguration" %><%@
page import="com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider" %><%@
page import="com.liferay.asset.kernel.model.AssetEntry" %><%@
page import="com.liferay.asset.kernel.model.AssetRendererFactory" %><%@
page import="com.liferay.asset.kernel.model.AssetVocabularyConstants" %><%@
page import="com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil" %><%@
page import="com.liferay.data.engine.rest.resource.exception.DataDefinitionValidationException" %><%@
page import="com.liferay.data.engine.rest.resource.exception.DataLayoutValidationException" %><%@
page import="com.liferay.document.library.kernel.exception.DuplicateFileEntryException" %><%@
page import="com.liferay.document.library.kernel.exception.FileSizeException" %><%@
page import="com.liferay.document.library.kernel.exception.NoSuchFileEntryException" %><%@
page import="com.liferay.dynamic.data.mapping.exception.NoSuchStructureException" %><%@
page import="com.liferay.dynamic.data.mapping.exception.NoSuchTemplateException" %><%@
page import="com.liferay.dynamic.data.mapping.exception.RequiredStructureException" %><%@
page import="com.liferay.dynamic.data.mapping.exception.RequiredTemplateException" %><%@
page import="com.liferay.dynamic.data.mapping.exception.StorageFieldRequiredException" %><%@
page import="com.liferay.dynamic.data.mapping.exception.StructureNameException" %><%@
page import="com.liferay.dynamic.data.mapping.exception.TemplateNameException" %><%@
page import="com.liferay.dynamic.data.mapping.exception.TemplateSmallImageContentException" %><%@
page import="com.liferay.dynamic.data.mapping.exception.TemplateSmallImageNameException" %><%@
page import="com.liferay.dynamic.data.mapping.exception.TemplateSmallImageSizeException" %><%@
page import="com.liferay.dynamic.data.mapping.model.DDMForm" %><%@
page import="com.liferay.dynamic.data.mapping.model.DDMFormField" %><%@
page import="com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions" %><%@
page import="com.liferay.dynamic.data.mapping.model.DDMStructure" %><%@
page import="com.liferay.dynamic.data.mapping.model.DDMTemplate" %><%@
page import="com.liferay.dynamic.data.mapping.model.LocalizedValue" %><%@
page import="com.liferay.dynamic.data.mapping.service.DDMStorageLinkLocalServiceUtil" %><%@
page import="com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil" %><%@
page import="com.liferay.dynamic.data.mapping.service.DDMTemplateLocalServiceUtil" %><%@
page import="com.liferay.exportimport.kernel.exception.ExportImportContentValidationException" %><%@
page import="com.liferay.friendly.url.exception.FriendlyURLLocalizationUrlTitleException" %><%@
page import="com.liferay.frontend.taglib.clay.servlet.taglib.util.VerticalNavItemList" %><%@
page import="com.liferay.frontend.taglib.form.navigator.constants.FormNavigatorConstants" %><%@
page import="com.liferay.journal.configuration.JournalFileUploadsConfiguration" %><%@
page import="com.liferay.journal.configuration.JournalGroupServiceConfiguration" %><%@
page import="com.liferay.journal.constants.JournalArticleConstants" %><%@
page import="com.liferay.journal.constants.JournalConstants" %><%@
page import="com.liferay.journal.constants.JournalFeedConstants" %><%@
page import="com.liferay.journal.constants.JournalFolderConstants" %><%@
page import="com.liferay.journal.constants.JournalWebKeys" %><%@
page import="com.liferay.journal.exception.ArticleContentException" %><%@
page import="com.liferay.journal.exception.ArticleContentSizeException" %><%@
page import="com.liferay.journal.exception.ArticleDisplayDateException" %><%@
page import="com.liferay.journal.exception.ArticleExpirationDateException" %><%@
page import="com.liferay.journal.exception.ArticleFriendlyURLException" %><%@
page import="com.liferay.journal.exception.ArticleIdException" %><%@
page import="com.liferay.journal.exception.ArticleSmallImageNameException" %><%@
page import="com.liferay.journal.exception.ArticleSmallImageSizeException" %><%@
page import="com.liferay.journal.exception.ArticleTitleException" %><%@
page import="com.liferay.journal.exception.ArticleVersionException" %><%@
page import="com.liferay.journal.exception.DuplicateArticleIdException" %><%@
page import="com.liferay.journal.exception.DuplicateFeedIdException" %><%@
page import="com.liferay.journal.exception.DuplicateFolderNameException" %><%@
page import="com.liferay.journal.exception.FeedContentFieldException" %><%@
page import="com.liferay.journal.exception.FeedIdException" %><%@
page import="com.liferay.journal.exception.FeedNameException" %><%@
page import="com.liferay.journal.exception.FeedTargetLayoutFriendlyUrlException" %><%@
page import="com.liferay.journal.exception.FeedTargetPortletIdException" %><%@
page import="com.liferay.journal.exception.FolderNameException" %><%@
page import="com.liferay.journal.exception.InvalidDDMStructureException" %><%@
page import="com.liferay.journal.exception.InvalidFolderException" %><%@
page import="com.liferay.journal.exception.MaxAddMenuFavItemsException" %><%@
page import="com.liferay.journal.exception.NoSuchArticleException" %><%@
page import="com.liferay.journal.exception.NoSuchFolderException" %><%@
page import="com.liferay.journal.model.JournalArticle" %><%@
page import="com.liferay.journal.model.JournalArticleDisplay" %><%@
page import="com.liferay.journal.model.JournalArticleLocalization" %><%@
page import="com.liferay.journal.model.JournalFeed" %><%@
page import="com.liferay.journal.model.JournalFolder" %><%@
page import="com.liferay.journal.service.JournalArticleLocalServiceUtil" %><%@
page import="com.liferay.journal.service.JournalArticleServiceUtil" %><%@
page import="com.liferay.journal.service.JournalFolderLocalServiceUtil" %><%@
page import="com.liferay.journal.service.JournalFolderServiceUtil" %><%@
page import="com.liferay.journal.util.comparator.ArticleVersionComparator" %><%@
page import="com.liferay.journal.web.internal.asset.model.JournalArticleAssetRenderer" %><%@
page import="com.liferay.journal.web.internal.configuration.JournalWebConfiguration" %><%@
page import="com.liferay.journal.web.internal.constants.JournalWebConstants" %><%@
page import="com.liferay.journal.web.internal.display.context.EditJournalFeedDisplayContext" %><%@
page import="com.liferay.journal.web.internal.display.context.JournalArticleCommentsManagementToolbarDisplayContext" %><%@
page import="com.liferay.journal.web.internal.display.context.JournalArticleVersionsManagementToolbarDisplayContext" %><%@
page import="com.liferay.journal.web.internal.display.context.JournalConfigurationDisplayContext" %><%@
page import="com.liferay.journal.web.internal.display.context.JournalDDMStructuresDisplayContext" %><%@
page import="com.liferay.journal.web.internal.display.context.JournalDDMStructuresManagementToolbarDisplayContext" %><%@
page import="com.liferay.journal.web.internal.display.context.JournalDDMTemplateDisplayContext" %><%@
page import="com.liferay.journal.web.internal.display.context.JournalDDMTemplateManagementToolbarDisplayContext" %><%@
page import="com.liferay.journal.web.internal.display.context.JournalDisplayContext" %><%@
page import="com.liferay.journal.web.internal.display.context.JournalEditArticleDisplayContext" %><%@
page import="com.liferay.journal.web.internal.display.context.JournalEditDDMStructuresDisplayContext" %><%@
page import="com.liferay.journal.web.internal.display.context.JournalEditDDMTemplateDisplayContext" %><%@
page import="com.liferay.journal.web.internal.display.context.JournalFeedsDisplayContext" %><%@
page import="com.liferay.journal.web.internal.display.context.JournalFeedsManagementToolbarDisplayContext" %><%@
page import="com.liferay.journal.web.internal.display.context.JournalHistoryDisplayContext" %><%@
page import="com.liferay.journal.web.internal.display.context.JournalHistoryManagementToolbarDisplayContext" %><%@
page import="com.liferay.journal.web.internal.display.context.JournalManagementToolbarDisplayContext" %><%@
page import="com.liferay.journal.web.internal.display.context.JournalMoveEntriesDisplayContext" %><%@
page import="com.liferay.journal.web.internal.display.context.JournalPreviewArticleContentTemplateDisplayContext" %><%@
page import="com.liferay.journal.web.internal.display.context.JournalVersionTabDisplayContext" %><%@
page import="com.liferay.journal.web.internal.display.context.JournalViewMoreMenuItemsDisplayContext" %><%@
page import="com.liferay.journal.web.internal.display.context.JournalViewMoreMenuItemsManagementToolbarDisplayContext" %><%@
page import="com.liferay.journal.web.internal.display.context.helper.JournalWebRequestHelper" %><%@
page import="com.liferay.journal.web.internal.exception.DDMStructureValidationModelListenerException" %><%@
page import="com.liferay.journal.web.internal.frontend.taglib.clay.servlet.taglib.JournalArticleCommentsVerticalCard" %><%@
page import="com.liferay.journal.web.internal.frontend.taglib.clay.servlet.taglib.JournalArticleHistoryVerticalCard" %><%@
page import="com.liferay.journal.web.internal.frontend.taglib.clay.servlet.taglib.JournalArticleVersionVerticalCard" %><%@
page import="com.liferay.journal.web.internal.frontend.taglib.clay.servlet.taglib.JournalArticleVerticalCard" %><%@
page import="com.liferay.journal.web.internal.frontend.taglib.clay.servlet.taglib.JournalDDMTemplateVerticalCard" %><%@
page import="com.liferay.journal.web.internal.frontend.taglib.clay.servlet.taglib.JournalFolderHorizontalCard" %><%@
page import="com.liferay.journal.web.internal.portlet.JournalPortlet" %><%@
page import="com.liferay.journal.web.internal.security.permission.resource.DDMStructurePermission" %><%@
page import="com.liferay.journal.web.internal.security.permission.resource.DDMTemplatePermission" %><%@
page import="com.liferay.journal.web.internal.security.permission.resource.JournalArticlePermission" %><%@
page import="com.liferay.journal.web.internal.security.permission.resource.JournalFeedPermission" %><%@
page import="com.liferay.journal.web.internal.security.permission.resource.JournalFolderPermission" %><%@
page import="com.liferay.journal.web.internal.security.permission.resource.JournalPermission" %><%@
page import="com.liferay.journal.web.internal.servlet.taglib.util.DDMStructureActionDropdownItemsProvider" %><%@
page import="com.liferay.journal.web.internal.servlet.taglib.util.JournalFeedActionDropdownItemsProvider" %><%@
page import="com.liferay.journal.web.internal.util.JournalPortletUtil" %><%@
page import="com.liferay.journal.web.internal.util.JournalUtil" %><%@
page import="com.liferay.petra.string.StringBundler" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.bean.BeanParamUtil" %><%@
page import="com.liferay.portal.kernel.dao.orm.QueryUtil" %><%@
page import="com.liferay.portal.kernel.exception.LocaleException" %><%@
page import="com.liferay.portal.kernel.exception.NoSuchImageException" %><%@
page import="com.liferay.portal.kernel.exception.NoSuchLayoutException" %><%@
page import="com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.language.UnicodeLanguageUtil" %><%@
page import="com.liferay.portal.kernel.model.*" %><%@
page import="com.liferay.portal.kernel.model.ModelHintsUtil" %><%@
page import="com.liferay.portal.kernel.model.impl.*" %><%@
page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %><%@
page import="com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder" %><%@
page import="com.liferay.portal.kernel.security.auth.PrincipalException" %><%@
page import="com.liferay.portal.kernel.security.permission.ActionKeys" %><%@
page import="com.liferay.portal.kernel.service.*" %><%@
page import="com.liferay.portal.kernel.servlet.MultiSessionMessages" %><%@
page import="com.liferay.portal.kernel.servlet.SessionMessages" %><%@
page import="com.liferay.portal.kernel.theme.ThemeDisplay" %><%@
page import="com.liferay.portal.kernel.upload.FileItem" %><%@
page import="com.liferay.portal.kernel.upload.LiferayFileItemException" %><%@
page import="com.liferay.portal.kernel.util.ArrayUtil" %><%@
page import="com.liferay.portal.kernel.util.Base64" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.FastDateFormatFactoryUtil" %><%@
page import="com.liferay.portal.kernel.util.GetterUtil" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlParserUtil" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.ListUtil" %><%@
page import="com.liferay.portal.kernel.util.LocaleUtil" %><%@
page import="com.liferay.portal.kernel.util.MapUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.portal.kernel.util.SetUtil" %><%@
page import="com.liferay.portal.kernel.util.StringUtil" %><%@
page import="com.liferay.portal.kernel.util.TextFormatter" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.portal.kernel.webdav.WebDAVUtil" %><%@
page import="com.liferay.portal.kernel.workflow.WorkflowConstants" %><%@
page import="com.liferay.portal.kernel.workflow.WorkflowDefinition" %><%@
page import="com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil" %><%@
page import="com.liferay.portal.model.impl.*" %><%@
page import="com.liferay.portal.servlet.BrowserSnifferUtil" %><%@
page import="com.liferay.portal.workflow.util.WorkflowDefinitionManagerUtil" %><%@
page import="com.liferay.rss.util.RSSUtil" %><%@
page import="com.liferay.taglib.search.ResultRow" %><%@
page import="com.liferay.taglib.util.CustomAttributesUtil" %>

<%@ page import="jakarta.portlet.ActionRequest" %><%@
page import="jakarta.portlet.PortletURL" %><%@
page import="jakarta.portlet.WindowState" %>

<%@ page import="java.text.Format" %>

<%@ page import="java.util.ArrayList" %><%@
page import="java.util.Arrays" %><%@
page import="java.util.Date" %><%@
page import="java.util.List" %><%@
page import="java.util.Locale" %><%@
page import="java.util.Map" %><%@
page import="java.util.Objects" %><%@
page import="java.util.Set" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<liferay-trash:defineObjects />

<portlet:defineObjects />

<%
AssetDisplayPageFriendlyURLProvider assetDisplayPageFriendlyURLProvider = (AssetDisplayPageFriendlyURLProvider)request.getAttribute(AssetDisplayPageFriendlyURLProvider.class.getName());

JournalWebConfiguration journalWebConfiguration = (JournalWebConfiguration)request.getAttribute(JournalWebConfiguration.class.getName());

JournalDisplayContext journalDisplayContext = (JournalDisplayContext)request.getAttribute(JournalDisplayContext.class.getName());

JournalWebRequestHelper journalWebRequestHelper = new JournalWebRequestHelper(request);

JournalGroupServiceConfiguration journalGroupServiceConfiguration = journalWebRequestHelper.getJournalGroupServiceConfiguration();

Format dateTimeFormat = FastDateFormatFactoryUtil.getDateTime(locale, timeZone);
%>

<%@ include file="/init-ext.jsp" %>