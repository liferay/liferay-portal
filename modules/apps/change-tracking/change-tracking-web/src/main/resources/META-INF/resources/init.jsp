<%--
/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/frontend-data-set" prefix="frontend-data-set" %><%@
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/react" prefix="react" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/user" prefix="liferay-user" %>

<%@ page import="com.liferay.change.tracking.constants.CTActionKeys" %><%@
page import="com.liferay.change.tracking.constants.CTConstants" %><%@
page import="com.liferay.change.tracking.exception.CTCollectionStatusException" %><%@
page import="com.liferay.change.tracking.exception.CTLocalizedException" %><%@
page import="com.liferay.change.tracking.exception.CTPublishConflictException" %><%@
page import="com.liferay.change.tracking.model.CTCollection" %><%@
page import="com.liferay.change.tracking.model.CTCollectionTemplate" %><%@
page import="com.liferay.change.tracking.model.CTRemote" %><%@
page import="com.liferay.change.tracking.service.CTCollectionTemplateLocalServiceUtil" %><%@
page import="com.liferay.change.tracking.spi.display.CTDisplayRendererRegistry" %><%@
page import="com.liferay.change.tracking.web.internal.constants.CTWebKeys" %><%@
page import="com.liferay.change.tracking.web.internal.constants.PublicationsFDSNames" %><%@
page import="com.liferay.change.tracking.web.internal.display.context.PublicationsConfigurationDisplayContext" %><%@
page import="com.liferay.change.tracking.web.internal.display.context.PublicationsDisplayContext" %><%@
page import="com.liferay.change.tracking.web.internal.display.context.ReschedulePublicationDisplayContext" %><%@
page import="com.liferay.change.tracking.web.internal.display.context.ViewCTRemotesDisplayContext" %><%@
page import="com.liferay.change.tracking.web.internal.display.context.ViewChangesDisplayContext" %><%@
page import="com.liferay.change.tracking.web.internal.display.context.ViewConflictsDisplayContext" %><%@
page import="com.liferay.change.tracking.web.internal.display.context.ViewHistoryDisplayContext" %><%@
page import="com.liferay.change.tracking.web.internal.display.context.ViewRelatedEntriesDisplayContext" %><%@
page import="com.liferay.change.tracking.web.internal.display.context.ViewScheduledDisplayContext" %><%@
page import="com.liferay.change.tracking.web.internal.display.context.ViewTemplatesDisplayContext" %><%@
page import="com.liferay.change.tracking.web.internal.display.context.ViewTemplatesManagementToolbarDisplayContext" %><%@
page import="com.liferay.change.tracking.web.internal.display.context.ViewTimelineHistoryDisplayContext" %><%@
page import="com.liferay.change.tracking.web.internal.security.permission.resource.CTCollectionPermission" %><%@
page import="com.liferay.petra.string.StringBundler" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.background.task.model.BackgroundTask" %><%@
page import="com.liferay.portal.background.task.service.BackgroundTaskLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.dao.search.ResultRow" %><%@
page import="com.liferay.portal.kernel.dao.search.SearchContainer" %><%@
page import="com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.model.ModelHintsUtil" %><%@
page import="com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder" %><%@
page import="com.liferay.portal.kernel.portlet.url.builder.ResourceURLBuilder" %><%@
page import="com.liferay.portal.kernel.servlet.MultiSessionErrors" %><%@
page import="com.liferay.portal.kernel.servlet.SessionErrors" %><%@
page import="com.liferay.portal.kernel.util.GetterUtil" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %>

<%@ page import="jakarta.portlet.RenderResponse" %>

<%@ page import="java.util.Date" %><%@
page import="java.util.Objects" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
portletDisplay.setShowStagingIcon(false);
%>