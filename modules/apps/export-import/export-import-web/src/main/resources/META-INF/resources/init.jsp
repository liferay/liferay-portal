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

<%@ page import="com.liferay.document.library.configuration.DLConfiguration" %><%@
page import="com.liferay.dynamic.data.mapping.exception.StructureDuplicateStructureKeyException" %><%@
page import="com.liferay.exportimport.kernel.background.task.BackgroundTaskExecutorNames" %><%@
page import="com.liferay.exportimport.kernel.configuration.ExportImportConfigurationUtil" %><%@
page import="com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants" %><%@
page import="com.liferay.exportimport.kernel.exception.LARFileException" %><%@
page import="com.liferay.exportimport.kernel.exception.LARFileNameException" %><%@
page import="com.liferay.exportimport.kernel.exception.LARFileSizeException" %><%@
page import="com.liferay.exportimport.kernel.exception.LARTypeException" %><%@
page import="com.liferay.exportimport.kernel.exception.LayoutImportException" %><%@
page import="com.liferay.exportimport.kernel.exception.RemoteExportException" %><%@
page import="com.liferay.exportimport.kernel.lar.ExportImportDateUtil" %><%@
page import="com.liferay.exportimport.kernel.lar.ExportImportHelper" %><%@
page import="com.liferay.exportimport.kernel.lar.ExportImportHelperUtil" %><%@
page import="com.liferay.exportimport.kernel.lar.ManifestSummary" %><%@
page import="com.liferay.exportimport.kernel.lar.PortletDataContext" %><%@
page import="com.liferay.exportimport.kernel.lar.PortletDataContextFactoryUtil" %><%@
page import="com.liferay.exportimport.kernel.lar.PortletDataException" %><%@
page import="com.liferay.exportimport.kernel.lar.PortletDataHandler" %><%@
page import="com.liferay.exportimport.kernel.lar.PortletDataHandlerBoolean" %><%@
page import="com.liferay.exportimport.kernel.lar.PortletDataHandlerChoice" %><%@
page import="com.liferay.exportimport.kernel.lar.PortletDataHandlerControl" %><%@
page import="com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys" %><%@
page import="com.liferay.exportimport.kernel.lar.StagedModelType" %><%@
page import="com.liferay.exportimport.kernel.lar.UserIdStrategy" %><%@
page import="com.liferay.exportimport.kernel.model.ExportImportConfiguration" %><%@
page import="com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalServiceUtil" %><%@
page import="com.liferay.exportimport.kernel.staging.StagingUtil" %><%@
page import="com.liferay.exportimport.web.internal.constants.ExportImportWebKeys" %><%@
page import="com.liferay.exportimport.web.internal.dao.search.ExportImportResultRowSplitter" %><%@
page import="com.liferay.exportimport.web.internal.display.context.ExportImportProcessDisplayContext" %><%@
page import="com.liferay.exportimport.web.internal.display.context.ExportImportPublishTemplatesDisplayContext" %><%@
page import="com.liferay.exportimport.web.internal.display.context.ExportImportToolbarDisplayContext" %><%@
page import="com.liferay.exportimport.web.internal.display.context.ExportLayoutsProcessesDisplayContext" %><%@
page import="com.liferay.exportimport.web.internal.display.context.ExportTemplatesToolbarDisplayContext" %><%@
page import="com.liferay.exportimport.web.internal.portlet.action.ExportImportMVCActionCommand" %><%@
page import="com.liferay.frontend.taglib.clay.servlet.taglib.util.JSPNavigationItemList" %><%@
page import="com.liferay.petra.string.StringBundler" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.background.task.util.comparator.BackgroundTaskComparatorFactoryUtil" %><%@
page import="com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil" %><%@
page import="com.liferay.portal.kernel.backgroundtask.BackgroundTask" %><%@
page import="com.liferay.portal.kernel.backgroundtask.BackgroundTaskManagerUtil" %><%@
page import="com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatus" %><%@
page import="com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatusRegistryUtil" %><%@
page import="com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants" %><%@
page import="com.liferay.portal.kernel.backgroundtask.display.BackgroundTaskDisplay" %><%@
page import="com.liferay.portal.kernel.backgroundtask.display.BackgroundTaskDisplayFactoryUtil" %><%@
page import="com.liferay.portal.kernel.dao.search.SearchContainer" %><%@
page import="com.liferay.portal.kernel.exception.LayoutPrototypeException" %><%@
page import="com.liferay.portal.kernel.exception.LocaleException" %><%@
page import="com.liferay.portal.kernel.exception.NoSuchGroupException" %><%@
page import="com.liferay.portal.kernel.exception.NoSuchLayoutException" %><%@
page import="com.liferay.portal.kernel.exception.NoSuchRoleException" %><%@
page import="com.liferay.portal.kernel.exception.PortalException" %><%@
page import="com.liferay.portal.kernel.exception.PortletIdException" %><%@
page import="com.liferay.portal.kernel.exception.RemoteOptionsException" %><%@
page import="com.liferay.portal.kernel.exception.SystemException" %><%@
page import="com.liferay.portal.kernel.group.capability.GroupCapabilityUtil" %><%@
page import="com.liferay.portal.kernel.json.JSONArray" %><%@
page import="com.liferay.portal.kernel.json.JSONFactoryUtil" %><%@
page import="com.liferay.portal.kernel.json.JSONObject" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.language.UnicodeLanguageUtil" %><%@
page import="com.liferay.portal.kernel.lock.DuplicateLockException" %><%@
page import="com.liferay.portal.kernel.model.Group" %><%@
page import="com.liferay.portal.kernel.model.Layout" %><%@
page import="com.liferay.portal.kernel.model.LayoutConstants" %><%@
page import="com.liferay.portal.kernel.model.LayoutRevision" %><%@
page import="com.liferay.portal.kernel.model.LayoutSet" %><%@
page import="com.liferay.portal.kernel.model.LayoutType" %><%@
page import="com.liferay.portal.kernel.model.LayoutTypePortlet" %><%@
page import="com.liferay.portal.kernel.model.ModelHintsUtil" %><%@
page import="com.liferay.portal.kernel.model.Portlet" %><%@
page import="com.liferay.portal.kernel.model.Ticket" %><%@
page import="com.liferay.portal.kernel.model.TicketConstants" %><%@
page import="com.liferay.portal.kernel.model.User" %><%@
page import="com.liferay.portal.kernel.portlet.PortalPreferences" %><%@
page import="com.liferay.portal.kernel.portlet.PortletBag" %><%@
page import="com.liferay.portal.kernel.portlet.PortletBagPool" %><%@
page import="com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil" %><%@
page import="com.liferay.portal.kernel.portlet.PortletURLUtil" %><%@
page import="com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder" %><%@
page import="com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil" %><%@
page import="com.liferay.portal.kernel.repository.model.FileEntry" %><%@
page import="com.liferay.portal.kernel.security.auth.AuthException" %><%@
page import="com.liferay.portal.kernel.security.auth.RemoteAuthException" %><%@
page import="com.liferay.portal.kernel.security.permission.ActionKeys" %><%@
page import="com.liferay.portal.kernel.security.permission.ResourceActionsUtil" %><%@
page import="com.liferay.portal.kernel.service.GroupLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.LayoutLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.LayoutRevisionLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.LayoutServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.LayoutSetLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.PortletLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.ServiceContext" %><%@
page import="com.liferay.portal.kernel.service.TicketLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.UserLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.permission.GroupPermissionUtil" %><%@
page import="com.liferay.portal.kernel.service.permission.PortletPermissionUtil" %><%@
page import="com.liferay.portal.kernel.servlet.SessionMessages" %><%@
page import="com.liferay.portal.kernel.upload.configuration.UploadServletRequestConfigurationProviderUtil" %><%@
page import="com.liferay.portal.kernel.util.AggregateResourceBundle" %><%@
page import="com.liferay.portal.kernel.util.ArrayUtil" %><%@
page import="com.liferay.portal.kernel.util.CalendarFactoryUtil" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.DateRange" %><%@
page import="com.liferay.portal.kernel.util.FastDateFormatFactoryUtil" %><%@
page import="com.liferay.portal.kernel.util.GetterUtil" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.ListUtil" %><%@
page import="com.liferay.portal.kernel.util.MapUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.portal.kernel.util.PortletKeys" %><%@
page import="com.liferay.portal.kernel.util.StringUtil" %><%@
page import="com.liferay.portal.kernel.util.Time" %><%@
page import="com.liferay.portal.kernel.util.Tuple" %><%@
page import="com.liferay.portal.kernel.util.UnicodeProperties" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.portal.kernel.util.comparator.PortletTitleComparator" %><%@
page import="com.liferay.portal.kernel.workflow.WorkflowConstants" %><%@
page import="com.liferay.portal.util.PropsValues" %><%@
page import="com.liferay.site.display.context.GroupDisplayContextHelper" %><%@
page import="com.liferay.site.navigation.taglib.servlet.taglib.util.BreadcrumbEntriesUtil" %><%@
page import="com.liferay.staging.StagingGroupHelper" %><%@
page import="com.liferay.staging.StagingGroupHelperUtil" %><%@
page import="com.liferay.taglib.search.ResultRow" %>

<%@ page import="jakarta.portlet.PortletRequest" %><%@
page import="jakarta.portlet.PortletURL" %>

<%@ page import="java.io.Serializable" %>

<%@ page import="java.text.DecimalFormatSymbols" %><%@
page import="java.text.Format" %>

<%@ page import="java.util.ArrayList" %><%@
page import="java.util.Arrays" %><%@
page import="java.util.Calendar" %><%@
page import="java.util.Collections" %><%@
page import="java.util.Date" %><%@
page import="java.util.HashMap" %><%@
page import="java.util.HashSet" %><%@
page import="java.util.List" %><%@
page import="java.util.Map" %><%@
page import="java.util.Objects" %><%@
page import="java.util.ResourceBundle" %><%@
page import="java.util.Set" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<liferay-trash:defineObjects />

<portlet:defineObjects />

<%
PortalPreferences portalPreferences = PortletPreferencesFactoryUtil.getPortalPreferences(request);

String portletResource = ParamUtil.getString(request, "portletResource");

Portlet selPortlet = PortletLocalServiceUtil.getPortletById(company.getCompanyId(), portletResource);

Calendar calendar = CalendarFactoryUtil.getCalendar(timeZone, locale);

int timeZoneOffset = timeZone.getOffset(calendar.getTimeInMillis());

Format dateTimeFormat = FastDateFormatFactoryUtil.getDateTime(locale, timeZone);

StagingGroupHelper stagingGroupHelper = StagingGroupHelperUtil.getStagingGroupHelper();
%>

<%@ include file="/init-ext.jsp" %>