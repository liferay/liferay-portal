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
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/map" prefix="liferay-map" %><%@
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/react" prefix="react" %><%@
taglib uri="http://liferay.com/tld/site-navigation" prefix="liferay-site-navigation" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.asset.kernel.model.AssetVocabularyConstants" %><%@
page import="com.liferay.frontend.taglib.form.navigator.constants.FormNavigatorConstants" %><%@
page import="com.liferay.item.selector.ItemSelector" %><%@
page import="com.liferay.item.selector.criteria.GroupItemSelectorReturnType" %><%@
page import="com.liferay.map.constants.MapProviderWebKeys" %><%@
page import="com.liferay.petra.function.transform.TransformUtil" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil" %><%@
page import="com.liferay.portal.kernel.exception.AvailableLocaleException" %><%@
page import="com.liferay.portal.kernel.exception.DuplicateGroupException" %><%@
page import="com.liferay.portal.kernel.exception.GroupFriendlyURLException" %><%@
page import="com.liferay.portal.kernel.exception.GroupInheritContentException" %><%@
page import="com.liferay.portal.kernel.exception.GroupKeyException" %><%@
page import="com.liferay.portal.kernel.exception.GroupNameException" %><%@
page import="com.liferay.portal.kernel.exception.GroupParentException" %><%@
page import="com.liferay.portal.kernel.exception.LayoutSetVirtualHostException" %><%@
page import="com.liferay.portal.kernel.exception.NoSuchGroupException" %><%@
page import="com.liferay.portal.kernel.exception.NoSuchLayoutException" %><%@
page import="com.liferay.portal.kernel.exception.NoSuchLayoutSetException" %><%@
page import="com.liferay.portal.kernel.exception.NoSuchRoleException" %><%@
page import="com.liferay.portal.kernel.exception.PendingBackgroundTaskException" %><%@
page import="com.liferay.portal.kernel.exception.RequiredGroupException" %><%@
page import="com.liferay.portal.kernel.json.JSONFactoryUtil" %><%@
page import="com.liferay.portal.kernel.json.JSONObject" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.model.Group" %><%@
page import="com.liferay.portal.kernel.model.GroupConstants" %><%@
page import="com.liferay.portal.kernel.model.Layout" %><%@
page import="com.liferay.portal.kernel.model.LayoutSet" %><%@
page import="com.liferay.portal.kernel.model.LayoutSetPrototype" %><%@
page import="com.liferay.portal.kernel.model.MembershipRequest" %><%@
page import="com.liferay.portal.kernel.model.Organization" %><%@
page import="com.liferay.portal.kernel.model.Portlet" %><%@
page import="com.liferay.portal.kernel.model.SiteConstants" %><%@
page import="com.liferay.portal.kernel.model.UserGroup" %><%@
page import="com.liferay.portal.kernel.portlet.PortletProvider" %><%@
page import="com.liferay.portal.kernel.portlet.PortletProviderUtil" %><%@
page import="com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil" %><%@
page import="com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder" %><%@
page import="com.liferay.portal.kernel.security.permission.ActionKeys" %><%@
page import="com.liferay.portal.kernel.security.permission.ResourceActionsUtil" %><%@
page import="com.liferay.portal.kernel.service.GroupLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.LayoutLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.LayoutSetLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.LayoutSetPrototypeLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.LayoutSetPrototypeServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.OrganizationLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.PortletLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.UserGroupLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.UserLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.permission.PortalPermissionUtil" %><%@
page import="com.liferay.portal.kernel.util.GetterUtil" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.HttpComponentsUtil" %><%@
page import="com.liferay.portal.kernel.util.ListUtil" %><%@
page import="com.liferay.portal.kernel.util.LocaleUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.portal.kernel.util.PrefsPropsUtil" %><%@
page import="com.liferay.portal.kernel.util.PropertiesParamUtil" %><%@
page import="com.liferay.portal.kernel.util.PropsKeys" %><%@
page import="com.liferay.portal.kernel.util.StringUtil" %><%@
page import="com.liferay.portal.kernel.util.TextFormatter" %><%@
page import="com.liferay.portal.kernel.util.TreeMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.UnicodeFormatter" %><%@
page import="com.liferay.portal.kernel.util.UnicodeProperties" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.portal.kernel.workflow.WorkflowConstants" %><%@
page import="com.liferay.portal.liveusers.LiveUsers" %><%@
page import="com.liferay.portal.util.PropsValues" %><%@
page import="com.liferay.ratings.kernel.RatingsType" %><%@
page import="com.liferay.ratings.kernel.display.context.CompanyPortletRatingsDefinitionDisplayContext" %><%@
page import="com.liferay.ratings.kernel.display.context.GroupPortletRatingsDefinitionDisplayContext" %><%@
page import="com.liferay.ratings.kernel.transformer.RatingsDataTransformerUtil" %><%@
page import="com.liferay.site.admin.web.internal.configuration.SiteAdminConfiguration" %><%@
page import="com.liferay.site.admin.web.internal.constants.SiteAdminPortletKeys" %><%@
page import="com.liferay.site.admin.web.internal.constants.SiteAdminWebKeys" %><%@
page import="com.liferay.site.admin.web.internal.display.context.AddGroupDisplayContext" %><%@
page import="com.liferay.site.admin.web.internal.display.context.DefaultUserAssociationsDisplayContext" %><%@
page import="com.liferay.site.admin.web.internal.display.context.DisplaySettingsDisplayContext" %><%@
page import="com.liferay.site.admin.web.internal.display.context.MenuAccessConfigurationDisplayContext" %><%@
page import="com.liferay.site.admin.web.internal.display.context.SelectSiteInitializerDisplayContext" %><%@
page import="com.liferay.site.admin.web.internal.display.context.SiteAdminDisplayContext" %><%@
page import="com.liferay.site.admin.web.internal.display.context.SiteAdminManagementToolbarDisplayContext" %><%@
page import="com.liferay.site.admin.web.internal.frontend.taglib.clay.servlet.taglib.SelectSiteInitializerVerticalCard" %><%@
page import="com.liferay.site.admin.web.internal.frontend.taglib.clay.servlet.taglib.SiteVerticalCard" %><%@
page import="com.liferay.site.item.selector.SiteItemSelectorCriterion" %><%@
page import="com.liferay.site.settings.configuration.admin.display.SiteSettingsConfigurationScreenContributor" %><%@
page import="com.liferay.sites.kernel.util.Sites" %>

<%@ page import="jakarta.portlet.PortletPreferences" %><%@
page import="jakarta.portlet.PortletRequest" %><%@
page import="jakarta.portlet.PortletURL" %>

<%@ page import="java.util.ArrayList" %><%@
page import="java.util.List" %><%@
page import="java.util.Locale" %><%@
page import="java.util.Map" %><%@
page import="java.util.Objects" %><%@
page import="java.util.Set" %><%@
page import="java.util.TreeMap" %>

<%@ page import="org.osgi.service.cm.ConfigurationException" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
portletDisplay.setShowStagingIcon(false);
%>

<%@ include file="/init-ext.jsp" %>