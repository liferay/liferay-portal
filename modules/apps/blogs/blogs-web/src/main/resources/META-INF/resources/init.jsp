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
taglib uri="http://liferay.com/tld/comment" prefix="liferay-comment" %><%@
taglib uri="http://liferay.com/tld/ddm" prefix="liferay-ddm" %><%@
taglib uri="http://liferay.com/tld/editor" prefix="liferay-editor" %><%@
taglib uri="http://liferay.com/tld/expando" prefix="liferay-expando" %><%@
taglib uri="http://liferay.com/tld/flags" prefix="liferay-flags" %><%@
taglib uri="http://liferay.com/tld/friendly-url" prefix="liferay-friendly-url" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/item-selector" prefix="liferay-item-selector" %><%@
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/ratings" prefix="liferay-ratings" %><%@
taglib uri="http://liferay.com/tld/react" prefix="react" %><%@
taglib uri="http://liferay.com/tld/reading-time" prefix="liferay-reading-time" %><%@
taglib uri="http://liferay.com/tld/rss" prefix="liferay-rss" %><%@
taglib uri="http://liferay.com/tld/social-bookmarks" prefix="liferay-social-bookmarks" %><%@
taglib uri="http://liferay.com/tld/template" prefix="liferay-template" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/trash" prefix="liferay-trash" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/user" prefix="liferay-user" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.asset.constants.AssetWebKeys" %><%@
page import="com.liferay.asset.kernel.model.AssetEntry" %><%@
page import="com.liferay.asset.kernel.model.AssetRenderer" %><%@
page import="com.liferay.asset.kernel.model.AssetTag" %><%@
page import="com.liferay.asset.kernel.model.AssetVocabularyConstants" %><%@
page import="com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil" %><%@
page import="com.liferay.asset.kernel.service.AssetTagLocalServiceUtil" %><%@
page import="com.liferay.asset.util.AssetHelper" %><%@
page import="com.liferay.asset.util.LinkedAssetEntryIdsUtil" %><%@
page import="com.liferay.blogs.configuration.BlogsGroupServiceOverriddenConfiguration" %><%@
page import="com.liferay.blogs.constants.BlogsConstants" %><%@
page import="com.liferay.blogs.constants.BlogsPortletKeys" %><%@
page import="com.liferay.blogs.exception.EntryContentException" %><%@
page import="com.liferay.blogs.exception.EntryCoverImageCropException" %><%@
page import="com.liferay.blogs.exception.EntryDescriptionException" %><%@
page import="com.liferay.blogs.exception.EntrySmallImageNameException" %><%@
page import="com.liferay.blogs.exception.EntrySmallImageScaleException" %><%@
page import="com.liferay.blogs.exception.EntryTitleException" %><%@
page import="com.liferay.blogs.exception.EntryUrlTitleException" %><%@
page import="com.liferay.blogs.exception.NoSuchEntryException" %><%@
page import="com.liferay.blogs.model.BlogsEntry" %><%@
page import="com.liferay.blogs.service.BlogsEntryLocalServiceUtil" %><%@
page import="com.liferay.blogs.settings.BlogsGroupServiceSettings" %><%@
page import="com.liferay.blogs.web.internal.configuration.BlogsPortletInstanceConfiguration" %><%@
page import="com.liferay.blogs.web.internal.constants.BlogsWebConstants" %><%@
page import="com.liferay.blogs.web.internal.display.context.BlogsAggregatorViewDisplayContext" %><%@
page import="com.liferay.blogs.web.internal.display.context.BlogsConfigurationDisplayContext" %><%@
page import="com.liferay.blogs.web.internal.display.context.BlogsEditEntryDisplayContext" %><%@
page import="com.liferay.blogs.web.internal.display.context.BlogsManagementToolbarDisplayContext" %><%@
page import="com.liferay.blogs.web.internal.display.context.BlogsViewDisplayContext" %><%@
page import="com.liferay.blogs.web.internal.display.context.BlogsViewEntriesDisplayContext" %><%@
page import="com.liferay.blogs.web.internal.display.context.BlogsViewImagesDisplayContext" %><%@
page import="com.liferay.blogs.web.internal.display.context.helper.BlogsPortletInstanceSettingsHelper" %><%@
page import="com.liferay.blogs.web.internal.security.permission.resource.BlogsEntryPermission" %><%@
page import="com.liferay.blogs.web.internal.servlet.taglib.util.BlogsEntryActionDropdownItemsProvider" %><%@
page import="com.liferay.blogs.web.internal.social.SocialBookmarksUtil" %><%@
page import="com.liferay.blogs.web.internal.util.BlogsEntryAssetEntryUtil" %><%@
page import="com.liferay.blogs.web.internal.util.BlogsEntryUtil" %><%@
page import="com.liferay.blogs.web.internal.util.BlogsPortletInstanceConfigurationUtil" %><%@
page import="com.liferay.blogs.web.internal.util.BlogsUtil" %><%@
page import="com.liferay.document.library.kernel.exception.FileSizeException" %><%@
page import="com.liferay.friendly.url.exception.DuplicateFriendlyURLEntryException" %><%@
page import="com.liferay.friendly.url.exception.FriendlyURLLocalizationUrlTitleException" %><%@
page import="com.liferay.frontend.taglib.clay.servlet.taglib.util.JSPNavigationItemList" %><%@
page import="com.liferay.petra.string.StringBundler" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil" %><%@
page import="com.liferay.portal.kernel.comment.CommentManagerUtil" %><%@
page import="com.liferay.portal.kernel.dao.search.SearchContainer" %><%@
page import="com.liferay.portal.kernel.exception.ImageResolutionException" %><%@
page import="com.liferay.portal.kernel.exception.PortalException" %><%@
page import="com.liferay.portal.kernel.exception.SystemException" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.model.BaseModel" %><%@
page import="com.liferay.portal.kernel.model.Group" %><%@
page import="com.liferay.portal.kernel.model.ModelHintsUtil" %><%@
page import="com.liferay.portal.kernel.model.Organization" %><%@
page import="com.liferay.portal.kernel.model.Portlet" %><%@
page import="com.liferay.portal.kernel.model.User" %><%@
page import="com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder" %><%@
page import="com.liferay.portal.kernel.security.permission.ActionKeys" %><%@
page import="com.liferay.portal.kernel.security.permission.ResourceActionsUtil" %><%@
page import="com.liferay.portal.kernel.service.GroupLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.OrganizationLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.PortletLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.UserLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.settings.GroupServiceSettingsLocator" %><%@
page import="com.liferay.portal.kernel.settings.ParameterMapSettingsLocator" %><%@
page import="com.liferay.portal.kernel.settings.PortletInstanceSettingsLocator" %><%@
page import="com.liferay.portal.kernel.upload.FileItem" %><%@
page import="com.liferay.portal.kernel.upload.LiferayFileItemException" %><%@
page import="com.liferay.portal.kernel.upload.UploadRequestSizeException" %><%@
page import="com.liferay.portal.kernel.upload.configuration.UploadServletRequestConfigurationProviderUtil" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.DateUtil" %><%@
page import="com.liferay.portal.kernel.util.FastDateFormatFactoryUtil" %><%@
page import="com.liferay.portal.kernel.util.GetterUtil" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.ListUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.portal.kernel.util.PropsUtil" %><%@
page import="com.liferay.portal.kernel.util.StringUtil" %><%@
page import="com.liferay.portal.kernel.util.URLCodec" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.portal.kernel.workflow.WorkflowConstants" %><%@
page import="com.liferay.portal.util.PropsValues" %><%@
page import="com.liferay.ratings.kernel.model.RatingsEntry" %><%@
page import="com.liferay.ratings.kernel.model.RatingsStats" %><%@
page import="com.liferay.ratings.kernel.service.RatingsEntryLocalServiceUtil" %><%@
page import="com.liferay.ratings.kernel.service.RatingsStatsLocalServiceUtil" %><%@
page import="com.liferay.rss.util.RSSUtil" %>

<%@ page import="jakarta.portlet.PortletRequest" %><%@
page import="jakarta.portlet.PortletURL" %><%@
page import="jakarta.portlet.WindowState" %>

<%@ page import="java.text.Format" %>

<%@ page import="java.util.ArrayList" %><%@
page import="java.util.Collections" %><%@
page import="java.util.Date" %><%@
page import="java.util.List" %><%@
page import="java.util.Map" %><%@
page import="java.util.Objects" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<liferay-trash:defineObjects />

<portlet:defineObjects />

<%
AssetHelper assetHelper = (AssetHelper)request.getAttribute(AssetWebKeys.ASSET_HELPER);

Format dateFormat = FastDateFormatFactoryUtil.getDate(locale, timeZone);
%>

<%@ include file="/init-ext.jsp" %>