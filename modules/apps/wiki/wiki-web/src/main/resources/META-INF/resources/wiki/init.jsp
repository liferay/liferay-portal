<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%@ taglib uri="http://liferay.com/tld/portal-workflow" prefix="liferay-portal-workflow" %>

<%@ page import="com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil" %><%@
page import="com.liferay.asset.kernel.model.AssetRenderer" %><%@
page import="com.liferay.asset.kernel.model.AssetRendererFactory" %><%@
page import="com.liferay.document.library.configuration.DLConfiguration" %><%@
page import="com.liferay.document.library.kernel.model.DLFileEntry" %><%@
page import="com.liferay.document.library.kernel.util.DLValidatorUtil" %><%@
page import="com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemListBuilder" %><%@
page import="com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil" %><%@
page import="com.liferay.portal.kernel.comment.Comment" %><%@
page import="com.liferay.portal.kernel.dao.orm.QueryUtil" %><%@
page import="com.liferay.portal.kernel.search.RelatedSearchResult" %><%@
page import="com.liferay.portal.kernel.servlet.HttpHeaders" %><%@
page import="com.liferay.portal.kernel.util.TempFileEntryUtil" %><%@
page import="com.liferay.trash.TrashHelper" %><%@
page import="com.liferay.trash.util.TrashWebKeys" %><%@
page import="com.liferay.wiki.configuration.WikiGroupServiceConfiguration" %><%@
page import="com.liferay.wiki.engine.WikiEngineRenderer" %><%@
page import="com.liferay.wiki.exception.DuplicateNodeNameException" %><%@
page import="com.liferay.wiki.exception.DuplicatePageException" %><%@
page import="com.liferay.wiki.exception.ImportFilesException" %><%@
page import="com.liferay.wiki.exception.NoSuchNodeException" %><%@
page import="com.liferay.wiki.exception.NoSuchPageException" %><%@
page import="com.liferay.wiki.exception.NodeNameException" %><%@
page import="com.liferay.wiki.exception.PageContentException" %><%@
page import="com.liferay.wiki.exception.PageTitleException" %><%@
page import="com.liferay.wiki.exception.PageVersionException" %><%@
page import="com.liferay.wiki.exception.RequiredNodeException" %><%@
page import="com.liferay.wiki.exception.WikiFormatException" %><%@
page import="com.liferay.wiki.social.WikiActivityKeys" %><%@
page import="com.liferay.wiki.util.comparator.PageVersionComparator" %><%@
page import="com.liferay.wiki.validator.WikiPageTitleValidator" %><%@
page import="com.liferay.wiki.web.internal.configuration.WikiPortletInstanceConfiguration" %><%@
page import="com.liferay.wiki.web.internal.display.context.WikiListPagesDisplayContext" %><%@
page import="com.liferay.wiki.web.internal.display.context.WikiNodeInfoPanelDisplayContext" %><%@
page import="com.liferay.wiki.web.internal.display.context.WikiPageInfoPanelDisplayContext" %><%@
page import="com.liferay.wiki.web.internal.display.context.helper.MailTemplatesHelper" %><%@
page import="com.liferay.wiki.web.internal.display.context.helper.WikiPortletInstanceSettingsHelper" %><%@
page import="com.liferay.wiki.web.internal.display.context.helper.WikiRequestHelper" %><%@
page import="com.liferay.wiki.web.internal.display.context.helper.WikiSocialActivityHelper" %><%@
page import="com.liferay.wiki.web.internal.display.context.helper.WikiURLHelper" %><%@
page import="com.liferay.wiki.web.internal.display.context.helper.WikiVisualizationHelper" %><%@
page import="com.liferay.wiki.web.internal.frontend.taglib.clay.servlet.taglib.WikiPageAttachmentHorizontalCard" %><%@
page import="com.liferay.wiki.web.internal.security.permission.resource.WikiNodePermission" %><%@
page import="com.liferay.wiki.web.internal.security.permission.resource.WikiPagePermission" %><%@
page import="com.liferay.wiki.web.internal.util.WikiPageAttachmentsUtil" %><%@
page import="com.liferay.wiki.web.internal.util.WikiUtil" %><%@
page import="com.liferay.wiki.web.internal.util.WikiWebComponentProvider" %>

<%@ page import="java.util.Objects" %>

<%
AssetHelper assetHelper = (AssetHelper)request.getAttribute(AssetWebKeys.ASSET_HELPER);

WikiRequestHelper wikiRequestHelper = new WikiRequestHelper(request);

WikiGroupServiceConfiguration siteWikiGroupServiceConfiguration = wikiRequestHelper.getWikiGroupServiceConfiguration();

WikiPortletInstanceSettingsHelper wikiPortletInstanceSettingsHelper = new WikiPortletInstanceSettingsHelper(wikiRequestHelper);

WikiWebComponentProvider wikiWebComponentProvider = WikiWebComponentProvider.getWikiWebComponentProvider();

WikiGroupServiceConfiguration wikiGroupServiceConfiguration = wikiWebComponentProvider.getWikiGroupServiceConfiguration();

Format dateTimeFormat = FastDateFormatFactoryUtil.getDateTime(locale, timeZone);
%>

<%@ include file="/wiki/init-ext.jsp" %>