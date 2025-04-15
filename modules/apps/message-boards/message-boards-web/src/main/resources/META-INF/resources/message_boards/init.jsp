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
taglib uri="http://liferay.com/tld/captcha" prefix="liferay-captcha" %><%@
taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/editor" prefix="liferay-editor" %><%@
taglib uri="http://liferay.com/tld/expando" prefix="liferay-expando" %><%@
taglib uri="http://liferay.com/tld/flags" prefix="liferay-flags" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/portal-workflow" prefix="liferay-portal-workflow" %><%@
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/ratings" prefix="liferay-ratings" %><%@
taglib uri="http://liferay.com/tld/rss" prefix="liferay-rss" %><%@
taglib uri="http://liferay.com/tld/security" prefix="liferay-security" %><%@
taglib uri="http://liferay.com/tld/site-navigation" prefix="liferay-site-navigation" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/trash" prefix="liferay-trash" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/user" prefix="liferay-user" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.asset.constants.AssetWebKeys" %><%@
page import="com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil" %><%@
page import="com.liferay.asset.kernel.model.AssetEntry" %><%@
page import="com.liferay.asset.kernel.model.AssetRenderer" %><%@
page import="com.liferay.asset.kernel.model.AssetRendererFactory" %><%@
page import="com.liferay.asset.kernel.model.AssetTag" %><%@
page import="com.liferay.asset.kernel.model.AssetVocabularyConstants" %><%@
page import="com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil" %><%@
page import="com.liferay.asset.kernel.service.AssetEntryServiceUtil" %><%@
page import="com.liferay.asset.kernel.service.AssetTagLocalServiceUtil" %><%@
page import="com.liferay.asset.util.AssetHelper" %><%@
page import="com.liferay.asset.util.LinkedAssetEntryIdsUtil" %><%@
page import="com.liferay.captcha.configuration.CaptchaConfiguration" %><%@
page import="com.liferay.document.library.configuration.DLConfiguration" %><%@
page import="com.liferay.document.library.kernel.antivirus.AntivirusScannerException" %><%@
page import="com.liferay.document.library.kernel.exception.DuplicateFileEntryException" %><%@
page import="com.liferay.document.library.kernel.exception.FileExtensionException" %><%@
page import="com.liferay.document.library.kernel.exception.FileNameException" %><%@
page import="com.liferay.document.library.kernel.exception.FileSizeException" %><%@
page import="com.liferay.document.library.kernel.model.DLFileEntry" %><%@
page import="com.liferay.document.library.kernel.util.DLValidatorUtil" %><%@
page import="com.liferay.frontend.taglib.servlet.taglib.util.EmptyResultMessageKeys" %><%@
page import="com.liferay.message.boards.constants.MBCategoryConstants" %><%@
page import="com.liferay.message.boards.constants.MBConstants" %><%@
page import="com.liferay.message.boards.constants.MBMessageConstants" %><%@
page import="com.liferay.message.boards.constants.MBPortletKeys" %><%@
page import="com.liferay.message.boards.display.context.MBAdminListDisplayContext" %><%@
page import="com.liferay.message.boards.display.context.MBHomeDisplayContext" %><%@
page import="com.liferay.message.boards.display.context.MBListDisplayContext" %><%@
page import="com.liferay.message.boards.exception.BannedUserException" %><%@
page import="com.liferay.message.boards.exception.CategoryNameException" %><%@
page import="com.liferay.message.boards.exception.LockedThreadException" %><%@
page import="com.liferay.message.boards.exception.MailingListEmailAddressException" %><%@
page import="com.liferay.message.boards.exception.MailingListInServerNameException" %><%@
page import="com.liferay.message.boards.exception.MailingListInUserNameException" %><%@
page import="com.liferay.message.boards.exception.MailingListOutEmailAddressException" %><%@
page import="com.liferay.message.boards.exception.MailingListOutServerNameException" %><%@
page import="com.liferay.message.boards.exception.MailingListOutUserNameException" %><%@
page import="com.liferay.message.boards.exception.MessageBodyException" %><%@
page import="com.liferay.message.boards.exception.MessageSubjectException" %><%@
page import="com.liferay.message.boards.exception.NoSuchCategoryException" %><%@
page import="com.liferay.message.boards.exception.NoSuchMessageException" %><%@
page import="com.liferay.message.boards.exception.RequiredMessageException" %><%@
page import="com.liferay.message.boards.exception.SplitThreadException" %><%@
page import="com.liferay.message.boards.model.MBBan" %><%@
page import="com.liferay.message.boards.model.MBCategory" %><%@
page import="com.liferay.message.boards.model.MBMailingList" %><%@
page import="com.liferay.message.boards.model.MBMessage" %><%@
page import="com.liferay.message.boards.model.MBMessageDisplay" %><%@
page import="com.liferay.message.boards.model.MBThread" %><%@
page import="com.liferay.message.boards.model.MBThreadFlag" %><%@
page import="com.liferay.message.boards.model.MBTreeWalker" %><%@
page import="com.liferay.message.boards.service.MBBanLocalServiceUtil" %><%@
page import="com.liferay.message.boards.service.MBCategoryLocalServiceUtil" %><%@
page import="com.liferay.message.boards.service.MBCategoryServiceUtil" %><%@
page import="com.liferay.message.boards.service.MBMailingListLocalServiceUtil" %><%@
page import="com.liferay.message.boards.service.MBMessageLocalServiceUtil" %><%@
page import="com.liferay.message.boards.service.MBMessageServiceUtil" %><%@
page import="com.liferay.message.boards.service.MBStatsUserLocalServiceUtil" %><%@
page import="com.liferay.message.boards.service.MBThreadFlagLocalServiceUtil" %><%@
page import="com.liferay.message.boards.service.MBThreadLocalServiceUtil" %><%@
page import="com.liferay.message.boards.settings.MBGroupServiceSettings" %><%@
page import="com.liferay.message.boards.util.comparator.CategoryTitleComparator" %><%@
page import="com.liferay.message.boards.util.comparator.ThreadModifiedDateComparator" %><%@
page import="com.liferay.message.boards.web.internal.dao.search.MBResultRowSplitter" %><%@
page import="com.liferay.message.boards.web.internal.display.MBCategoryDisplay" %><%@
page import="com.liferay.message.boards.web.internal.display.context.MBBannedUsersManagementToolbarDisplayContext" %><%@
page import="com.liferay.message.boards.web.internal.display.context.MBConfigurationDisplayContext" %><%@
page import="com.liferay.message.boards.web.internal.display.context.MBDisplayContext" %><%@
page import="com.liferay.message.boards.web.internal.display.context.MBEditMessageDisplayContext" %><%@
page import="com.liferay.message.boards.web.internal.display.context.MBEntriesManagementToolbarDisplayContext" %><%@
page import="com.liferay.message.boards.web.internal.display.context.MBNavigationDisplayContext" %><%@
page import="com.liferay.message.boards.web.internal.display.context.MBThreadsDisplayContext" %><%@
page import="com.liferay.message.boards.web.internal.display.context.MBViewStatisticsDisplayContext" %><%@
page import="com.liferay.message.boards.web.internal.display.context.helper.MBRequestHelper" %><%@
page import="com.liferay.message.boards.web.internal.portlet.action.ActionUtil" %><%@
page import="com.liferay.message.boards.web.internal.search.EntriesChecker" %><%@
page import="com.liferay.message.boards.web.internal.security.permission.MBCategoryPermission" %><%@
page import="com.liferay.message.boards.web.internal.security.permission.MBMessagePermission" %><%@
page import="com.liferay.message.boards.web.internal.security.permission.MBResourcePermission" %><%@
page import="com.liferay.message.boards.web.internal.util.MBBreadcrumbUtil" %><%@
page import="com.liferay.message.boards.web.internal.util.MBDisplayContextUtil" %><%@
page import="com.liferay.message.boards.web.internal.util.MBMailUtil" %><%@
page import="com.liferay.message.boards.web.internal.util.MBMessageIterator" %><%@
page import="com.liferay.message.boards.web.internal.util.MBRSSUtil" %><%@
page import="com.liferay.message.boards.web.internal.util.MBRequestUtil" %><%@
page import="com.liferay.message.boards.web.internal.util.MBSubscriptionUtil" %><%@
page import="com.liferay.message.boards.web.internal.util.MBUtil" %><%@
page import="com.liferay.petra.string.CharPool" %><%@
page import="com.liferay.petra.string.StringBundler" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil" %><%@
page import="com.liferay.portal.kernel.bean.BeanParamUtil" %><%@
page import="com.liferay.portal.kernel.bean.BeanPropertiesUtil" %><%@
page import="com.liferay.portal.kernel.captcha.CaptchaConfigurationException" %><%@
page import="com.liferay.portal.kernel.captcha.CaptchaException" %><%@
page import="com.liferay.portal.kernel.captcha.CaptchaTextException" %><%@
page import="com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker" %><%@
page import="com.liferay.portal.kernel.dao.search.SearchContainer" %><%@
page import="com.liferay.portal.kernel.editor.Editor" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.log.Log" %><%@
page import="com.liferay.portal.kernel.log.LogFactoryUtil" %><%@
page import="com.liferay.portal.kernel.model.ModelHintsConstants" %><%@
page import="com.liferay.portal.kernel.model.User" %><%@
page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %><%@
page import="com.liferay.portal.kernel.portlet.PortletURLFactoryUtil" %><%@
page import="com.liferay.portal.kernel.portlet.PortletURLUtil" %><%@
page import="com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder" %><%@
page import="com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil" %><%@
page import="com.liferay.portal.kernel.repository.model.FileEntry" %><%@
page import="com.liferay.portal.kernel.search.SearchResult" %><%@
page import="com.liferay.portal.kernel.security.permission.ActionKeys" %><%@
page import="com.liferay.portal.kernel.security.permission.ResourceActionsUtil" %><%@
page import="com.liferay.portal.kernel.service.ServiceContext" %><%@
page import="com.liferay.portal.kernel.service.UserLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.theme.ThemeDisplay" %><%@
page import="com.liferay.portal.kernel.upload.FileItem" %><%@
page import="com.liferay.portal.kernel.upload.LiferayFileItemException" %><%@
page import="com.liferay.portal.kernel.upload.UploadRequestSizeException" %><%@
page import="com.liferay.portal.kernel.upload.configuration.UploadServletRequestConfigurationProviderUtil" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.FastDateFormatFactoryUtil" %><%@
page import="com.liferay.portal.kernel.util.GetterUtil" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.ListUtil" %><%@
page import="com.liferay.portal.kernel.util.LocaleUtil" %><%@
page import="com.liferay.portal.kernel.util.OrderByComparator" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.portal.kernel.util.PortletKeys" %><%@
page import="com.liferay.portal.kernel.util.StringUtil" %><%@
page import="com.liferay.portal.kernel.util.TempFileEntryUtil" %><%@
page import="com.liferay.portal.kernel.util.TextFormatter" %><%@
page import="com.liferay.portal.kernel.util.Time" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.portal.kernel.workflow.WorkflowConstants" %><%@
page import="com.liferay.portal.util.PropsValues" %><%@
page import="com.liferay.site.navigation.taglib.servlet.taglib.util.BreadcrumbEntriesUtil" %><%@
page import="com.liferay.subscription.service.SubscriptionLocalServiceUtil" %><%@
page import="com.liferay.taglib.search.ResultRow" %><%@
page import="com.liferay.taglib.ui.InputEditorTag" %>

<%@ page import="jakarta.portlet.PortletRequest" %><%@
page import="jakarta.portlet.PortletURL" %>

<%@ page import="java.text.DecimalFormatSymbols" %><%@
page import="java.text.Format" %><%@
page import="java.text.NumberFormat" %>

<%@ page import="java.util.ArrayList" %><%@
page import="java.util.Collections" %><%@
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
AssetHelper assetHelper = (AssetHelper)request.getAttribute(AssetWebKeys.ASSET_HELPER);

String currentLanguageId = LanguageUtil.getLanguageId(request);

Locale currentLocale = LocaleUtil.fromLanguageId(currentLanguageId);

Locale defaultLocale = themeDisplay.getSiteDefaultLocale();

String defaultLanguageId = LocaleUtil.toLanguageId(defaultLocale);

CaptchaConfiguration captchaConfiguration = MBRequestUtil.getCaptchaConfiguration(request);

MBGroupServiceSettings mbGroupServiceSettings = MBRequestUtil.getMBGroupServiceSettings(request, themeDisplay.getSiteGroupId());

String[] priorities = mbGroupServiceSettings.getPriorities(currentLanguageId);

boolean allowAnonymousPosting = mbGroupServiceSettings.isAllowAnonymousPosting();
boolean enableFlags = mbGroupServiceSettings.isEnableFlags();
boolean enableRatings = mbGroupServiceSettings.isEnableRatings();
String messageFormat = mbGroupServiceSettings.getMessageFormat();
boolean subscribeByDefault = mbGroupServiceSettings.isSubscribeByDefault();
boolean threadAsQuestionByDefault = mbGroupServiceSettings.isThreadAsQuestionByDefault();

boolean enableRSS = mbGroupServiceSettings.isEnableRSS();
int rssDelta = mbGroupServiceSettings.getRSSDelta();
String rssDisplayStyle = mbGroupServiceSettings.getRSSDisplayStyle();
String rssFeedType = mbGroupServiceSettings.getRSSFeedType();

Format dateFormat = FastDateFormatFactoryUtil.getDate(locale, timeZone);
Format dateTimeFormat = FastDateFormatFactoryUtil.getDateTime(locale, timeZone);

NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);

MBDisplayContext mbDisplayContext = new MBDisplayContext(request);
%>

<%@ include file="/message_boards/init-ext.jsp" %>