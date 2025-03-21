<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/react" prefix="react" %><%@
taglib uri="http://liferay.com/tld/site" prefix="liferay-site" %><%@
taglib uri="http://liferay.com/tld/site-navigation" prefix="liferay-site-navigation" %><%@
taglib uri="http://liferay.com/tld/staging" prefix="liferay-staging" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.application.list.PanelApp" %><%@
page import="com.liferay.application.list.PanelCategory" %><%@
page import="com.liferay.asset.auto.tagger.configuration.AssetAutoTaggerConfiguration" %><%@
page import="com.liferay.depot.application.DepotApplication" %><%@
page import="com.liferay.depot.exception.DepotEntryGroupRelToGroupException" %><%@
page import="com.liferay.depot.exception.DepotEntryNameException" %><%@
page import="com.liferay.depot.exception.DepotEntryStagedException" %><%@
page import="com.liferay.depot.model.DepotEntry" %><%@
page import="com.liferay.depot.model.DepotEntryGroupRel" %><%@
page import="com.liferay.depot.web.internal.constants.DepotAdminWebKeys" %><%@
page import="com.liferay.depot.web.internal.constants.DepotEntryConstants" %><%@
page import="com.liferay.depot.web.internal.constants.DepotPortletKeys" %><%@
page import="com.liferay.depot.web.internal.constants.DepotScreenNavigationEntryConstants" %><%@
page import="com.liferay.depot.web.internal.constants.SharingWebKeys" %><%@
page import="com.liferay.depot.web.internal.display.context.DepotAdminDLDisplayContext" %><%@
page import="com.liferay.depot.web.internal.display.context.DepotAdminDetailsDisplayContext" %><%@
page import="com.liferay.depot.web.internal.display.context.DepotAdminDisplayContext" %><%@
page import="com.liferay.depot.web.internal.display.context.DepotAdminManagementToolbarDisplayContext" %><%@
page import="com.liferay.depot.web.internal.display.context.DepotAdminMembershipsDisplayContext" %><%@
page import="com.liferay.depot.web.internal.display.context.DepotAdminRolesDisplayContext" %><%@
page import="com.liferay.depot.web.internal.display.context.DepotAdminSelectRoleDisplayContext" %><%@
page import="com.liferay.depot.web.internal.display.context.DepotAdminSelectRoleManagementToolbarDisplayContext" %><%@
page import="com.liferay.depot.web.internal.display.context.DepotAdminSitesDisplayContext" %><%@
page import="com.liferay.depot.web.internal.display.context.DepotAdminViewDepotDashboardDisplayContext" %><%@
page import="com.liferay.depot.web.internal.display.context.DepotApplicationDisplayContext" %><%@
page import="com.liferay.depot.web.internal.util.DepotLanguageUtil" %><%@
page import="com.liferay.document.library.kernel.exception.RequiredFileEntryTypeException" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException" %><%@
page import="com.liferay.portal.kernel.exception.DuplicateGroupException" %><%@
page import="com.liferay.portal.kernel.exception.GroupKeyException" %><%@
page import="com.liferay.portal.kernel.exception.LocaleException" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.model.Group" %><%@
page import="com.liferay.portal.kernel.model.GroupConstants" %><%@
page import="com.liferay.portal.kernel.model.Role" %><%@
page import="com.liferay.portal.kernel.model.User" %><%@
page import="com.liferay.portal.kernel.util.GetterUtil" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.LocaleUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.portal.kernel.util.PrefsPropsUtil" %><%@
page import="com.liferay.portal.kernel.util.PropertiesParamUtil" %><%@
page import="com.liferay.portal.kernel.util.PropsKeys" %><%@
page import="com.liferay.portal.kernel.util.StringUtil" %><%@
page import="com.liferay.portal.kernel.util.UnicodeFormatter" %><%@
page import="com.liferay.portal.kernel.util.UnicodeProperties" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.portal.util.PropsValues" %><%@
page import="com.liferay.sharing.configuration.SharingConfiguration" %><%@
page import="com.liferay.site.navigation.taglib.servlet.taglib.util.BreadcrumbEntriesUtil" %>

<%@ page import="jakarta.portlet.ActionRequest" %><%@
page import="jakarta.portlet.PortletURL" %>

<%@ page import="java.util.Collection" %><%@
page import="java.util.Collections" %><%@
page import="java.util.List" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
portletDisplay.setShowExportImportIcon(false);
%>