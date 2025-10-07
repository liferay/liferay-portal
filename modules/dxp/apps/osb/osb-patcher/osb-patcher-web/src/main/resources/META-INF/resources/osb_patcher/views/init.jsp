<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/portal-workflow" prefix="liferay-portal-workflow" %><%@
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/react" prefix="react" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.osb.patcher.configuration.PatcherConfiguration" %><%@
page import="com.liferay.osb.patcher.constants.PatcherActionKeys" %><%@
page import="com.liferay.osb.patcher.constants.PatcherBuildConstants" %><%@
page import="com.liferay.osb.patcher.constants.PatcherFixConstants" %><%@
page import="com.liferay.osb.patcher.constants.PatcherProductVersionConstants" %><%@
page import="com.liferay.osb.patcher.constants.WorkflowConstants" %><%@
page import="com.liferay.osb.patcher.model.PatcherAccount" %><%@
page import="com.liferay.osb.patcher.model.PatcherBuild" %><%@
page import="com.liferay.osb.patcher.model.PatcherFix" %><%@
page import="com.liferay.osb.patcher.model.PatcherFixComponent" %><%@
page import="com.liferay.osb.patcher.model.PatcherFixPack" %><%@
page import="com.liferay.osb.patcher.model.PatcherProductVersion" %><%@
page import="com.liferay.osb.patcher.model.PatcherProjectVersion" %><%@
page import="com.liferay.osb.patcher.permission.resource.PatcherPermission" %><%@
page import="com.liferay.osb.patcher.service.PatcherAccountLocalServiceUtil" %><%@
page import="com.liferay.osb.patcher.service.PatcherBuildLocalServiceUtil" %><%@
page import="com.liferay.osb.patcher.service.PatcherFixComponentLocalServiceUtil" %><%@
page import="com.liferay.osb.patcher.service.PatcherFixLocalServiceUtil" %><%@
page import="com.liferay.osb.patcher.service.PatcherFixPackLocalServiceUtil" %><%@
page import="com.liferay.osb.patcher.service.PatcherProductVersionLocalServiceUtil" %><%@
page import="com.liferay.osb.patcher.service.PatcherProjectVersionLocalServiceUtil" %><%@
page import="com.liferay.osb.patcher.util.JenkinsUtil" %><%@
page import="com.liferay.osb.patcher.util.PatcherBuildRelUtil" %><%@
page import="com.liferay.osb.patcher.util.PatcherBuildUtil" %><%@
page import="com.liferay.osb.patcher.util.PatcherFixPackUtil" %><%@
page import="com.liferay.osb.patcher.util.PatcherFixUtil" %><%@
page import="com.liferay.osb.patcher.util.PatcherProductVersionUtil" %><%@
page import="com.liferay.osb.patcher.util.PatcherProjectVersionUtil" %><%@
page import="com.liferay.osb.patcher.util.PatcherUtil" %><%@
page import="com.liferay.osb.patcher.util.comparator.PatcherFixStatusComparator" %><%@
page import="com.liferay.osb.patcher.web.internal.display.context.PatcherAccountsDisplayContext" %><%@
page import="com.liferay.osb.patcher.web.internal.display.context.PatcherAccountsManagementToolbarDisplayContext" %><%@
page import="com.liferay.osb.patcher.web.internal.display.context.PatcherAccountsViewDisplayContext" %><%@
page import="com.liferay.osb.patcher.web.internal.display.context.PatcherAccountsViewManagementToolbarDisplayContext" %><%@
page import="com.liferay.osb.patcher.web.internal.display.context.PatcherBuildFixesDisplayContext" %><%@
page import="com.liferay.osb.patcher.web.internal.display.context.PatcherBuildsDisplayContext" %><%@
page import="com.liferay.osb.patcher.web.internal.display.context.PatcherBuildsManagementToolbarDisplayContext" %><%@
page import="com.liferay.osb.patcher.web.internal.display.context.PatcherChildBuildsDisplayContext" %><%@
page import="com.liferay.osb.patcher.web.internal.display.context.PatcherCreateBuildsDisplayContext" %><%@
page import="com.liferay.osb.patcher.web.internal.display.context.PatcherDisplayContext" %><%@
page import="com.liferay.osb.patcher.web.internal.display.context.PatcherEditFixPackFieldsDisplayContext" %><%@
page import="com.liferay.osb.patcher.web.internal.display.context.PatcherFixBuildsDisplayContext" %><%@
page import="com.liferay.osb.patcher.web.internal.display.context.PatcherFixComponentsDisplayContext" %><%@
page import="com.liferay.osb.patcher.web.internal.display.context.PatcherFixComponentsManagementToolbarDisplayContext" %><%@
page import="com.liferay.osb.patcher.web.internal.display.context.PatcherFixPacksDisplayContext" %><%@
page import="com.liferay.osb.patcher.web.internal.display.context.PatcherFixPacksManagementToolbarDisplayContext" %><%@
page import="com.liferay.osb.patcher.web.internal.display.context.PatcherFixesDisplayContext" %><%@
page import="com.liferay.osb.patcher.web.internal.display.context.PatcherFixesManagementToolbarDisplayContext" %><%@
page import="com.liferay.osb.patcher.web.internal.display.context.PatcherProductVersionsDisplayContext" %><%@
page import="com.liferay.osb.patcher.web.internal.display.context.PatcherProductVersionsManagementToolbarDisplayContext" %><%@
page import="com.liferay.osb.patcher.web.internal.display.context.PatcherProjectVersionsDisplayContext" %><%@
page import="com.liferay.osb.patcher.web.internal.display.context.PatcherProjectVersionsManagementToolbarDisplayContext" %><%@
page import="com.liferay.osb.patcher.web.internal.display.context.PatcherViewBuildsDisplayContext" %><%@
page import="com.liferay.osb.patcher.web.internal.display.context.PatcherViewFixPacksDisplayContext" %><%@
page import="com.liferay.osb.patcher.web.internal.display.context.PatcherViewFixesDisplayContext" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.petra.string.StringUtil" %><%@
page import="com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil" %><%@
page import="com.liferay.portal.kernel.dao.orm.QueryUtil" %><%@
page import="com.liferay.portal.kernel.dao.search.SearchContainer" %><%@
page import="com.liferay.portal.kernel.exception.PortalException" %><%@
page import="com.liferay.portal.kernel.json.JSONFactoryUtil" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.language.UnicodeLanguageUtil" %><%@
page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %><%@
page import="com.liferay.portal.kernel.servlet.HttpHeaders" %><%@
page import="com.liferay.portal.kernel.util.FastDateFormatFactoryUtil" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.ListUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.servlet.BrowserSnifferUtil" %>

<%@ page import="java.text.Format" %>

<%@ page import="java.util.Date" %><%@
page import="java.util.List" %><%@
page import="java.util.Map" %><%@
page import="java.util.Set" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
PatcherConfiguration patcherConfiguration = ConfigurationProviderUtil.getCompanyConfiguration(PatcherConfiguration.class, themeDisplay.getCompanyId());

PatcherDisplayContext patcherDisplayContext = new PatcherDisplayContext(request, renderResponse);

Format dateTimeFormat = FastDateFormatFactoryUtil.getDateTime(locale, timeZone);
%>