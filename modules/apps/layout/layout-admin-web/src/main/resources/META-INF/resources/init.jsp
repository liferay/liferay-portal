<%--
/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/asset" prefix="liferay-asset" %><%@
taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/expando" prefix="liferay-expando" %><%@
taglib uri="http://liferay.com/tld/friendly-url" prefix="liferay-friendly-url" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/layout" prefix="liferay-layout" %><%@
taglib uri="http://liferay.com/tld/react" prefix="react" %><%@
taglib uri="http://liferay.com/tld/site-navigation" prefix="liferay-site-navigation" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.asset.kernel.model.AssetVocabularyConstants" %><%@
page import="com.liferay.document.library.kernel.exception.FileSizeException" %><%@
page import="com.liferay.exportimport.kernel.staging.LayoutStagingUtil" %><%@
page import="com.liferay.exportimport.kernel.staging.StagingUtil" %><%@
page import="com.liferay.friendly.url.exception.DuplicateFriendlyURLEntryException" %><%@
page import="com.liferay.frontend.taglib.clay.servlet.taglib.util.TabsItem" %><%@
page import="com.liferay.frontend.taglib.form.navigator.constants.FormNavigatorConstants" %><%@
page import="com.liferay.layout.admin.constants.LayoutAdminPortletKeys" %><%@
page import="com.liferay.layout.admin.constants.LayoutScreenNavigationEntryConstants" %><%@
page import="com.liferay.layout.admin.web.internal.constants.LayoutAdminFormNavigatorConstants" %><%@
page import="com.liferay.layout.admin.web.internal.constants.LayoutAdminWebKeys" %><%@
page import="com.liferay.layout.admin.web.internal.display.context.LayoutLookAndFeelDisplayContext" %><%@
page import="com.liferay.layout.admin.web.internal.display.context.LayoutUtilityPageEntryDisplayContext" %><%@
page import="com.liferay.layout.admin.web.internal.display.context.LayoutUtilityPageEntryImportDisplayContext" %><%@
page import="com.liferay.layout.admin.web.internal.display.context.LayoutUtilityPageEntryManagementToolbarDisplayContext" %><%@
page import="com.liferay.layout.admin.web.internal.display.context.LayoutsAdminDisplayContext" %><%@
page import="com.liferay.layout.admin.web.internal.display.context.LayoutsAdminManagementToolbarDisplayContext" %><%@
page import="com.liferay.layout.admin.web.internal.display.context.MillerColumnsDisplayContext" %><%@
page import="com.liferay.layout.admin.web.internal.display.context.OrphanPortletsDisplayContext" %><%@
page import="com.liferay.layout.admin.web.internal.display.context.OrphanPortletsManagementToolbarDisplayContext" %><%@
page import="com.liferay.layout.admin.web.internal.display.context.SelectLayoutPageTemplateEntryDisplayContext" %><%@
page import="com.liferay.layout.admin.web.internal.frontend.taglib.clay.servlet.taglib.LayoutUtilityPageEntryVerticalCard" %><%@
page import="com.liferay.layout.admin.web.internal.frontend.taglib.clay.servlet.taglib.SelectBasicTemplatesNavigationCard" %><%@
page import="com.liferay.layout.admin.web.internal.frontend.taglib.clay.servlet.taglib.SelectBasicTemplatesVerticalCard" %><%@
page import="com.liferay.layout.admin.web.internal.frontend.taglib.clay.servlet.taglib.SelectGlobalTemplatesVerticalCard" %><%@
page import="com.liferay.layout.admin.web.internal.frontend.taglib.clay.servlet.taglib.SelectLayoutMasterLayoutVerticalCard" %><%@
page import="com.liferay.layout.admin.web.internal.servlet.taglib.util.LayoutActionDropdownItemsProvider" %><%@
page import="com.liferay.layout.admin.web.internal.util.CustomizationSettingsUtil" %><%@
page import="com.liferay.layout.importer.LayoutsImporterResultEntry" %><%@
page import="com.liferay.layout.page.template.model.LayoutPageTemplateCollection" %><%@
page import="com.liferay.layout.page.template.model.LayoutPageTemplateEntry" %><%@
page import="com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalServiceUtil" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.bean.BeanParamUtil" %><%@
page import="com.liferay.portal.kernel.dao.search.ResultRow" %><%@
page import="com.liferay.portal.kernel.exception.GroupInheritContentException" %><%@
page import="com.liferay.portal.kernel.exception.ImageTypeException" %><%@
page import="com.liferay.portal.kernel.exception.LayoutFriendlyURLException" %><%@
page import="com.liferay.portal.kernel.exception.LayoutFriendlyURLsException" %><%@
page import="com.liferay.portal.kernel.exception.LayoutJavaScriptException" %><%@
page import="com.liferay.portal.kernel.exception.LayoutNameException" %><%@
page import="com.liferay.portal.kernel.exception.LayoutSetJavaScriptException" %><%@
page import="com.liferay.portal.kernel.exception.LayoutTypeException" %><%@
page import="com.liferay.portal.kernel.exception.NoSuchGroupException" %><%@
page import="com.liferay.portal.kernel.exception.NoSuchLayoutException" %><%@
page import="com.liferay.portal.kernel.exception.NoSuchRoleException" %><%@
page import="com.liferay.portal.kernel.exception.RequiredLayoutException" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.model.ColorScheme" %><%@
page import="com.liferay.portal.kernel.model.Group" %><%@
page import="com.liferay.portal.kernel.model.GroupConstants" %><%@
page import="com.liferay.portal.kernel.model.Layout" %><%@
page import="com.liferay.portal.kernel.model.LayoutBranch" %><%@
page import="com.liferay.portal.kernel.model.LayoutBranchConstants" %><%@
page import="com.liferay.portal.kernel.model.LayoutConstants" %><%@
page import="com.liferay.portal.kernel.model.LayoutPrototype" %><%@
page import="com.liferay.portal.kernel.model.LayoutRevision" %><%@
page import="com.liferay.portal.kernel.model.LayoutSet" %><%@
page import="com.liferay.portal.kernel.model.LayoutSetBranch" %><%@
page import="com.liferay.portal.kernel.model.LayoutSetBranchConstants" %><%@
page import="com.liferay.portal.kernel.model.LayoutSetPrototype" %><%@
page import="com.liferay.portal.kernel.model.LayoutTemplate" %><%@
page import="com.liferay.portal.kernel.model.LayoutTemplateConstants" %><%@
page import="com.liferay.portal.kernel.model.LayoutType" %><%@
page import="com.liferay.portal.kernel.model.LayoutTypeController" %><%@
page import="com.liferay.portal.kernel.model.LayoutTypePortlet" %><%@
page import="com.liferay.portal.kernel.model.Portlet" %><%@
page import="com.liferay.portal.kernel.model.Theme" %><%@
page import="com.liferay.portal.kernel.model.ThemeSetting" %><%@
page import="com.liferay.portal.kernel.model.UserGroup" %><%@
page import="com.liferay.portal.kernel.model.impl.VirtualLayout" %><%@
page import="com.liferay.portal.kernel.plugin.PluginPackage" %><%@
page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %><%@
page import="com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder" %><%@
page import="com.liferay.portal.kernel.security.permission.ActionKeys" %><%@
page import="com.liferay.portal.kernel.security.permission.ResourceActionsUtil" %><%@
page import="com.liferay.portal.kernel.service.GroupLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.LayoutLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.LayoutPrototypeLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.LayoutPrototypeServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.LayoutSetBranchLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.LayoutSetPrototypeLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.LayoutTemplateLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.ThemeLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.UserGroupLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.permission.GroupPermissionUtil" %><%@
page import="com.liferay.portal.kernel.servlet.HttpHeaders" %><%@
page import="com.liferay.portal.kernel.servlet.MultiSessionMessages" %><%@
page import="com.liferay.portal.kernel.servlet.ServletContextPool" %><%@
page import="com.liferay.portal.kernel.servlet.SessionErrors" %><%@
page import="com.liferay.portal.kernel.servlet.SessionMessages" %><%@
page import="com.liferay.portal.kernel.template.StringTemplateResource" %><%@
page import="com.liferay.portal.kernel.util.AggregateResourceBundle" %><%@
page import="com.liferay.portal.kernel.util.GetterUtil" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.HttpComponentsUtil" %><%@
page import="com.liferay.portal.kernel.util.ListUtil" %><%@
page import="com.liferay.portal.kernel.util.LocaleUtil" %><%@
page import="com.liferay.portal.kernel.util.MapUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.portal.kernel.util.PropertiesParamUtil" %><%@
page import="com.liferay.portal.kernel.util.ResourceBundleUtil" %><%@
page import="com.liferay.portal.kernel.util.StringUtil" %><%@
page import="com.liferay.portal.kernel.util.UnicodeProperties" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.portal.kernel.webserver.WebServerServletTokenUtil" %><%@
page import="com.liferay.portal.util.LayoutTypeControllerTracker" %><%@
page import="com.liferay.portal.util.PropsValues" %><%@
page import="com.liferay.segments.exception.RequiredSegmentsExperienceException" %><%@
page import="com.liferay.site.navigation.model.SiteNavigationMenu" %>

<%@ page import="jakarta.portlet.PortletRequest" %><%@
page import="jakarta.portlet.PortletURL" %>

<%@ page import="java.util.List" %><%@
page import="java.util.Locale" %><%@
page import="java.util.Map" %><%@
page import="java.util.Objects" %><%@
page import="java.util.ResourceBundle" %><%@
page import="java.util.TreeMap" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
LayoutsAdminDisplayContext layoutsAdminDisplayContext = (LayoutsAdminDisplayContext)request.getAttribute(LayoutAdminWebKeys.LAYOUT_PAGE_LAYOUT_ADMIN_DISPLAY_CONTEXT);

portletDisplay.setShowExportImportIcon(false);
%>

<%@ include file="/init-ext.jsp" %>