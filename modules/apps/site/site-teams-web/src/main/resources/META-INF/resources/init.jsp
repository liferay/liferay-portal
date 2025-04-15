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
taglib uri="http://liferay.com/tld/security" prefix="liferay-security" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/user" prefix="liferay-user" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.dao.search.ResultRow" %><%@
page import="com.liferay.portal.kernel.exception.DuplicateTeamException" %><%@
page import="com.liferay.portal.kernel.exception.TeamNameException" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.model.Group" %><%@
page import="com.liferay.portal.kernel.model.Team" %><%@
page import="com.liferay.portal.kernel.model.UserGroup" %><%@
page import="com.liferay.portal.kernel.model.role.RoleConstants" %><%@
page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %><%@
page import="com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder" %><%@
page import="com.liferay.portal.kernel.security.permission.ActionKeys" %><%@
page import="com.liferay.portal.kernel.service.GroupLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.TeamLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.UserLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.permission.TeamPermissionUtil" %><%@
page import="com.liferay.portal.kernel.util.ArrayUtil" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.LinkedHashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.portal.kernel.workflow.WorkflowConstants" %><%@
page import="com.liferay.site.teams.web.internal.display.context.EditSiteTeamAssignmentsDisplayContext" %><%@
page import="com.liferay.site.teams.web.internal.display.context.EditSiteTeamAssignmentsUserGroupsDisplayContext" %><%@
page import="com.liferay.site.teams.web.internal.display.context.EditSiteTeamAssignmentsUserGroupsManagementToolbarDisplayContext" %><%@
page import="com.liferay.site.teams.web.internal.display.context.EditSiteTeamAssignmentsUsersDisplayContext" %><%@
page import="com.liferay.site.teams.web.internal.display.context.EditSiteTeamAssignmentsUsersManagementToolbarDisplayContext" %><%@
page import="com.liferay.site.teams.web.internal.display.context.SiteTeamsDisplayContext" %><%@
page import="com.liferay.site.teams.web.internal.display.context.SiteTeamsManagementToolbarDisplayContext" %><%@
page import="com.liferay.site.teams.web.internal.frontend.taglib.clay.servlet.taglib.UserUserCard" %><%@
page import="com.liferay.site.teams.web.internal.servlet.taglib.util.UserActionDropdownItemsProvider" %>

<%@ page import="jakarta.portlet.PortletURL" %>

<%@ page import="java.util.Objects" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%@ include file="/init-ext.jsp" %>