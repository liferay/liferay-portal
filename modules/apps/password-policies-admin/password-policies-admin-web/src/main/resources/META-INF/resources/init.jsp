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
taglib uri="http://liferay.com/tld/site-navigation" prefix="liferay-site-navigation" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/user" prefix="liferay-user" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.password.policies.admin.constants.PasswordPoliciesAdminPortletKeys" %><%@
page import="com.liferay.password.policies.admin.web.internal.configuration.PasswordPoliciesConfiguration" %><%@
page import="com.liferay.password.policies.admin.web.internal.display.context.EditPasswordPolicyAssignmentsManagementToolbarDisplayContext" %><%@
page import="com.liferay.password.policies.admin.web.internal.display.context.PasswordPolicyDisplayContext" %><%@
page import="com.liferay.password.policies.admin.web.internal.display.context.ViewPasswordPoliciesManagementToolbarDisplayContext" %><%@
page import="com.liferay.password.policies.admin.web.internal.frontend.taglib.clay.servlet.taglib.UserVerticalCard" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.bean.BeanParamUtil" %><%@
page import="com.liferay.portal.kernel.dao.search.RowChecker" %><%@
page import="com.liferay.portal.kernel.dao.search.SearchContainer" %><%@
page import="com.liferay.portal.kernel.exception.DuplicatePasswordPolicyException" %><%@
page import="com.liferay.portal.kernel.exception.NoSuchPasswordPolicyException" %><%@
page import="com.liferay.portal.kernel.exception.PasswordPolicyNameException" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.model.Organization" %><%@
page import="com.liferay.portal.kernel.model.PasswordPolicy" %><%@
page import="com.liferay.portal.kernel.model.PasswordPolicyRel" %><%@
page import="com.liferay.portal.kernel.model.User" %><%@
page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %><%@
page import="com.liferay.portal.kernel.portlet.PortalPreferences" %><%@
page import="com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil" %><%@
page import="com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder" %><%@
page import="com.liferay.portal.kernel.security.permission.ActionKeys" %><%@
page import="com.liferay.portal.kernel.service.PasswordPolicyLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.PasswordPolicyRelLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.portal.model.impl.PasswordPolicyImpl" %><%@
page import="com.liferay.portal.service.permission.PasswordPolicyPermissionUtil" %><%@
page import="com.liferay.portlet.usersadmin.util.UsersAdminUtil" %><%@
page import="com.liferay.site.navigation.taglib.servlet.taglib.util.BreadcrumbEntriesUtil" %><%@
page import="com.liferay.taglib.search.ResultRow" %>

<%@ page import="jakarta.portlet.PortletURL" %><%@
page import="jakarta.portlet.WindowState" %>

<%@ page import="java.util.Arrays" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
PasswordPolicyDisplayContext passwordPolicyDisplayContext = new PasswordPolicyDisplayContext(request, renderResponse);

PortalPreferences portalPreferences = PortletPreferencesFactoryUtil.getPortalPreferences(liferayPortletRequest);
%>

<%@ include file="/init-ext.jsp" %>