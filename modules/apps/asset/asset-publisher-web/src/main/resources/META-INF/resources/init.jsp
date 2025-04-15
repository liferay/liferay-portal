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
taglib uri="http://liferay.com/tld/flags" prefix="liferay-flags" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/learn" prefix="liferay-learn" %><%@
taglib uri="http://liferay.com/tld/portal-workflow" prefix="liferay-portal-workflow" %><%@
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/ratings" prefix="liferay-ratings" %><%@
taglib uri="http://liferay.com/tld/react" prefix="react" %><%@
taglib uri="http://liferay.com/tld/rss" prefix="liferay-rss" %><%@
taglib uri="http://liferay.com/tld/site-navigation" prefix="liferay-site-navigation" %><%@
taglib uri="http://liferay.com/tld/social-bookmarks" prefix="liferay-social-bookmarks" %><%@
taglib uri="http://liferay.com/tld/template" prefix="liferay-template" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/user" prefix="liferay-user" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.asset.constants.AssetWebKeys" %><%@
page import="com.liferay.asset.display.page.constants.AssetDisplayPageConstants" %><%@
page import="com.liferay.asset.display.page.model.AssetDisplayPageEntry" %><%@
page import="com.liferay.asset.display.page.service.AssetDisplayPageEntryLocalServiceUtil" %><%@
page import="com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil" %><%@
page import="com.liferay.asset.kernel.exception.DuplicateQueryRuleException" %><%@
page import="com.liferay.asset.kernel.model.AssetEntry" %><%@
page import="com.liferay.asset.kernel.model.AssetRenderer" %><%@
page import="com.liferay.asset.kernel.model.AssetRendererFactory" %><%@
page import="com.liferay.asset.kernel.model.AssetVocabulary" %><%@
page import="com.liferay.asset.kernel.model.ClassType" %><%@
page import="com.liferay.asset.kernel.model.ClassTypeField" %><%@
page import="com.liferay.asset.kernel.model.ClassTypeReader" %><%@
page import="com.liferay.asset.kernel.service.AssetVocabularyLocalServiceUtil" %><%@
page import="com.liferay.asset.publisher.constants.AssetPublisherConstants" %><%@
page import="com.liferay.asset.publisher.constants.AssetPublisherPortletKeys" %><%@
page import="com.liferay.asset.publisher.constants.AssetPublisherWebKeys" %><%@
page import="com.liferay.asset.publisher.util.AssetEntryResult" %><%@
page import="com.liferay.asset.publisher.util.AssetPublisherHelper" %><%@
page import="com.liferay.asset.publisher.web.internal.constants.AssetPublisherSelectionStyleConstants" %><%@
page import="com.liferay.asset.publisher.web.internal.display.context.AssetPublisherDisplayContext" %><%@
page import="com.liferay.asset.publisher.web.internal.display.context.AssetPublisherViewContentDisplayContext" %><%@
page import="com.liferay.asset.publisher.web.internal.display.context.SelectStructureFieldDisplayContext" %><%@
page import="com.liferay.asset.publisher.web.internal.helper.AssetPublisherWebHelper" %><%@
page import="com.liferay.asset.publisher.web.internal.servlet.taglib.util.AssetEntryActionDropdownItemsProvider" %><%@
page import="com.liferay.asset.publisher.web.internal.util.AssetRendererUtil" %><%@
page import="com.liferay.asset.util.AssetHelper" %><%@
page import="com.liferay.asset.util.AssetPublisherAddItemHolder" %><%@
page import="com.liferay.asset.util.comparator.AssetRendererFactoryTypeNameComparator" %><%@
page import="com.liferay.document.library.kernel.document.conversion.DocumentConversionUtil" %><%@
page import="com.liferay.dynamic.data.mapping.model.DDMStructure" %><%@
page import="com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem" %><%@
page import="com.liferay.petra.string.StringBundler" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.dao.search.ResultRow" %><%@
page import="com.liferay.portal.kernel.dao.search.SearchContainer" %><%@
page import="com.liferay.portal.kernel.exception.NoSuchGroupException" %><%@
page import="com.liferay.portal.kernel.exception.NoSuchModelException" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.log.Log" %><%@
page import="com.liferay.portal.kernel.log.LogFactoryUtil" %><%@
page import="com.liferay.portal.kernel.model.ClassName" %><%@
page import="com.liferay.portal.kernel.model.Group" %><%@
page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %><%@
page import="com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder" %><%@
page import="com.liferay.portal.kernel.security.permission.ResourceActionsUtil" %><%@
page import="com.liferay.portal.kernel.service.ClassNameLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.GroupLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.servlet.SessionMessages" %><%@
page import="com.liferay.portal.kernel.trash.TrashHandler" %><%@
page import="com.liferay.portal.kernel.trash.TrashHandlerRegistryUtil" %><%@
page import="com.liferay.portal.kernel.util.ArrayUtil" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.FastDateFormatFactoryUtil" %><%@
page import="com.liferay.portal.kernel.util.GetterUtil" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.HttpComponentsUtil" %><%@
page import="com.liferay.portal.kernel.util.KeyValuePair" %><%@
page import="com.liferay.portal.kernel.util.KeyValuePairComparator" %><%@
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
page import="com.liferay.portal.kernel.view.count.ViewCountManagerUtil" %><%@
page import="com.liferay.portal.kernel.workflow.WorkflowConstants" %><%@
page import="com.liferay.portlet.display.template.constants.PortletDisplayTemplateConstants" %><%@
page import="com.liferay.rss.util.RSSUtil" %><%@
page import="com.liferay.taglib.aui.AUIUtil" %>

<%@ page import="jakarta.portlet.PortletURL" %>

<%@ page import="java.io.Serializable" %>

<%@ page import="java.text.Format" %>

<%@ page import="java.util.ArrayList" %><%@
page import="java.util.Arrays" %><%@
page import="java.util.Date" %><%@
page import="java.util.HashMap" %><%@
page import="java.util.List" %><%@
page import="java.util.Map" %><%@
page import="java.util.Objects" %><%@
page import="java.util.Set" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
AssetHelper assetHelper = (AssetHelper)request.getAttribute(AssetWebKeys.ASSET_HELPER);
AssetPublisherDisplayContext assetPublisherDisplayContext = (AssetPublisherDisplayContext)request.getAttribute(AssetPublisherWebKeys.ASSET_PUBLISHER_DISPLAY_CONTEXT);
AssetPublisherHelper assetPublisherHelper = (AssetPublisherHelper)request.getAttribute(AssetPublisherWebKeys.ASSET_PUBLISHER_HELPER);
AssetPublisherWebHelper assetPublisherWebHelper = (AssetPublisherWebHelper)request.getAttribute(AssetPublisherWebKeys.ASSET_PUBLISHER_WEB_HELPER);

Format dateFormat = FastDateFormatFactoryUtil.getDate(locale, timeZone);
%>

<%@ include file="/init-ext.jsp" %>