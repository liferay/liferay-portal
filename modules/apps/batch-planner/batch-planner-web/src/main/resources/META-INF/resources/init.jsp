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
taglib uri="http://liferay.com/tld/security" prefix="liferay-security" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.batch.planner.constants.BatchPlannerPlanConstants" %><%@
page import="com.liferay.batch.planner.exception.BatchPlannerPlanInternalClassNameException" %><%@
page import="com.liferay.batch.planner.model.BatchPlannerPlan" %><%@
page import="com.liferay.batch.planner.web.internal.display.BatchPlannerPlanDisplay" %><%@
page import="com.liferay.batch.planner.web.internal.display.BatchPlannerPlanTemplateDisplay" %><%@
page import="com.liferay.batch.planner.web.internal.display.context.BatchPlannerPlanDisplayContext" %><%@
page import="com.liferay.batch.planner.web.internal.display.context.BatchPlannerPlanManagementToolbarDisplayContext" %><%@
page import="com.liferay.batch.planner.web.internal.display.context.BatchPlannerPlanTemplateDisplayContext" %><%@
page import="com.liferay.batch.planner.web.internal.display.context.BatchPlannerPlanTemplateManagementToolbarDisplayContext" %><%@
page import="com.liferay.batch.planner.web.internal.display.context.EditBatchPlannerPlanDisplayContext" %><%@
page import="com.liferay.batch.planner.web.internal.security.permission.resource.BatchPlannerPlanPermission" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.petra.string.StringUtil" %><%@
page import="com.liferay.portal.kernel.dao.search.ResultRow" %><%@
page import="com.liferay.portal.kernel.dao.search.SearchContainer" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %><%@
page import="com.liferay.portal.kernel.portlet.url.builder.ActionURLBuilder" %><%@
page import="com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder" %><%@
page import="com.liferay.portal.kernel.portlet.url.builder.ResourceURLBuilder" %><%@
page import="com.liferay.portal.kernel.security.permission.ActionKeys" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.FastDateFormatFactoryUtil" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %>

<%@ page import="java.text.Format" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
Format dateTimeFormat = FastDateFormatFactoryUtil.getDateTime(locale, timeZone);
%>