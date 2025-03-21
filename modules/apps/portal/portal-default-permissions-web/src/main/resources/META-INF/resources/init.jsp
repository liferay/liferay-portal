<%--
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/react" prefix="react" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem" %><%@
page import="com.liferay.petra.string.StringBundler" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.defaultpermissions.web.internal.constants.PortalDefaultPermissionsWebKeys" %><%@
page import="com.liferay.portal.defaultpermissions.web.internal.display.context.BaseViewPortalDefaultPermissionsConfigurationDisplayContext" %><%@
page import="com.liferay.portal.defaultpermissions.web.internal.display.context.EditPortalDefaultPermissionsConfigurationDisplayContext" %><%@
page import="com.liferay.portal.defaultpermissions.web.internal.display.context.PortalDefaultPermissionsManagementToolbarDisplayContext" %><%@
page import="com.liferay.portal.defaultpermissions.web.internal.portlet.action.EditPortalDefaultPermissionsConfigurationMVCActionCommand" %><%@
page import="com.liferay.portal.kernel.dao.search.SearchContainer" %><%@
page import="com.liferay.portal.kernel.defaultpermissions.configuration.manager.PortalDefaultPermissionsConfigurationManager" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.model.Role" %><%@
page import="com.liferay.portal.kernel.model.role.RoleConstants" %><%@
page import="com.liferay.portal.kernel.security.permission.ActionKeys" %><%@
page import="com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.roles.admin.constants.RolesAdminWebKeys" %><%@
page import="com.liferay.roles.admin.role.type.contributor.RoleTypeContributor" %><%@
page import="com.liferay.roles.admin.role.type.contributor.provider.RoleTypeContributorProvider" %>

<%@ page import="java.util.HashMap" %><%@
page import="java.util.List" %><%@
page import="java.util.Map" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
BaseViewPortalDefaultPermissionsConfigurationDisplayContext baseViewPortalDefaultPermissionsConfigurationDisplayContext = (BaseViewPortalDefaultPermissionsConfigurationDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);
%>