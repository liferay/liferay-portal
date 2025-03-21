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
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/site-navigation" prefix="liferay-site-navigation" %><%@
taglib uri="http://liferay.com/tld/staging" prefix="liferay-staging" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/trash" prefix="liferay-trash" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/user" prefix="liferay-user" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.exportimport.kernel.background.task.BackgroundTaskExecutorNames" %><%@
page import="com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants" %><%@
page import="com.liferay.exportimport.kernel.exception.RemoteExportException" %><%@
page import="com.liferay.exportimport.kernel.lar.ExportImportHelperUtil" %><%@
page import="com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys" %><%@
page import="com.liferay.exportimport.kernel.model.ExportImportConfiguration" %><%@
page import="com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalServiceUtil" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.dao.search.SearchContainer" %><%@
page import="com.liferay.portal.kernel.exception.NoSuchGroupException" %><%@
page import="com.liferay.portal.kernel.exception.NoSuchLayoutException" %><%@
page import="com.liferay.portal.kernel.exception.NoSuchRoleException" %><%@
page import="com.liferay.portal.kernel.exception.RemoteOptionsException" %><%@
page import="com.liferay.portal.kernel.group.capability.GroupCapabilityUtil" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.language.UnicodeLanguageUtil" %><%@
page import="com.liferay.portal.kernel.messaging.Message" %><%@
page import="com.liferay.portal.kernel.model.Layout" %><%@
page import="com.liferay.portal.kernel.model.LayoutConstants" %><%@
page import="com.liferay.portal.kernel.model.ModelHintsUtil" %><%@
page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %><%@
page import="com.liferay.portal.kernel.portlet.PortalPreferences" %><%@
page import="com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil" %><%@
page import="com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder" %><%@
page import="com.liferay.portal.kernel.scheduler.SchedulerEngineHelperUtil" %><%@
page import="com.liferay.portal.kernel.security.auth.AuthException" %><%@
page import="com.liferay.portal.kernel.security.auth.RemoteAuthException" %><%@
page import="com.liferay.portal.kernel.security.permission.ActionKeys" %><%@
page import="com.liferay.portal.kernel.service.LayoutLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.permission.GroupPermissionUtil" %><%@
page import="com.liferay.portal.kernel.servlet.SessionMessages" %><%@
page import="com.liferay.portal.kernel.util.CalendarFactoryUtil" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.GetterUtil" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.MapUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.portal.kernel.util.PortletKeys" %><%@
page import="com.liferay.portal.kernel.util.SessionTreeJSClicks" %><%@
page import="com.liferay.portal.kernel.util.StringUtil" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.portal.util.PropsValues" %><%@
page import="com.liferay.site.navigation.taglib.servlet.taglib.util.BreadcrumbEntriesUtil" %><%@
page import="com.liferay.staging.constants.StagingConfigurationPortletKeys" %><%@
page import="com.liferay.staging.constants.StagingProcessesPortletKeys" %><%@
page import="com.liferay.staging.processes.web.internal.dao.search.PublishResultRowSplitter" %><%@
page import="com.liferay.staging.processes.web.internal.display.context.PublishTemplatesDisplayContext" %><%@
page import="com.liferay.staging.processes.web.internal.display.context.ScheduledPublishProcessesDisplayContext" %><%@
page import="com.liferay.staging.processes.web.internal.display.context.StagingProcessesWebDisplayContext" %><%@
page import="com.liferay.staging.processes.web.internal.display.context.StagingProcessesWebPublishTemplatesToolbarDisplayContext" %><%@
page import="com.liferay.staging.processes.web.internal.display.context.StagingProcessesWebToolbarDisplayContext" %><%@
page import="com.liferay.taglib.search.ResultRow" %>

<%@ page import="jakarta.portlet.PortletMode" %><%@
page import="jakarta.portlet.PortletRequest" %><%@
page import="jakarta.portlet.PortletURL" %>

<%@ page import="java.io.Serializable" %>

<%@ page import="java.util.Calendar" %><%@
page import="java.util.Collections" %><%@
page import="java.util.Date" %><%@
page import="java.util.List" %><%@
page import="java.util.Map" %>

<liferay-frontend:defineObjects />

<liferay-staging:defineObjects />

<liferay-theme:defineObjects />

<liferay-trash:defineObjects />

<portlet:defineObjects />

<%
PortalPreferences portalPreferences = PortletPreferencesFactoryUtil.getPortalPreferences(liferayPortletRequest);

Calendar calendar = CalendarFactoryUtil.getCalendar(timeZone, locale);

int timeZoneOffset = timeZone.getOffset(calendar.getTimeInMillis());

PublishTemplatesDisplayContext publishTemplatesDisplayContext = new PublishTemplatesDisplayContext(request, renderResponse);

StagingProcessesWebDisplayContext stagingProcessesWebDisplayContext = new StagingProcessesWebDisplayContext(request, renderResponse);

StagingProcessesWebToolbarDisplayContext stagingProcessesWebToolbarDisplayContext = new StagingProcessesWebToolbarDisplayContext(request, pageContext, liferayPortletResponse);
%>

<%@ include file="/init-ext.jsp" %>