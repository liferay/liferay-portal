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
taglib uri="http://liferay.com/tld/expando" prefix="liferay-expando" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/react" prefix="react" %><%@
taglib uri="http://liferay.com/tld/site" prefix="liferay-site" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/user" prefix="liferay-user" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.application.list.constants.ApplicationListWebKeys" %><%@
page import="com.liferay.application.list.constants.PanelCategoryKeys" %><%@
page import="com.liferay.application.list.display.context.logic.PanelCategoryHelper" %><%@
page import="com.liferay.expando.kernel.model.ExpandoBridge" %><%@
page import="com.liferay.expando.kernel.util.ExpandoBridgeFactoryUtil" %><%@
page import="com.liferay.item.selector.ItemSelector" %><%@
page import="com.liferay.item.selector.criteria.URLItemSelectorReturnType" %><%@
page import="com.liferay.item.selector.criteria.group.criterion.GroupItemSelectorCriterion" %><%@
page import="com.liferay.object.constants.ObjectDefinitionConstants" %><%@
page import="com.liferay.object.constants.ObjectPortletKeys" %><%@
page import="com.liferay.object.model.ObjectDefinition" %><%@
page import="com.liferay.object.service.ObjectDefinitionLocalServiceUtil" %><%@
page import="com.liferay.organizations.search.OrganizationSearch" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.bean.BeanParamUtil" %><%@
page import="com.liferay.portal.kernel.dao.orm.QueryUtil" %><%@
page import="com.liferay.portal.kernel.dao.search.SearchContainer" %><%@
page import="com.liferay.portal.kernel.exception.DataLimitExceededException" %><%@
page import="com.liferay.portal.kernel.exception.DuplicateRoleException" %><%@
page import="com.liferay.portal.kernel.exception.NoSuchRoleException" %><%@
page import="com.liferay.portal.kernel.exception.RequiredRoleException" %><%@
page import="com.liferay.portal.kernel.exception.RoleAssignmentException" %><%@
page import="com.liferay.portal.kernel.exception.RoleNameException" %><%@
page import="com.liferay.portal.kernel.exception.RolePermissionsException" %><%@
page import="com.liferay.portal.kernel.exception.RoleSubtypeException" %><%@
page import="com.liferay.portal.kernel.exception.SystemException" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.model.Group" %><%@
page import="com.liferay.portal.kernel.model.GroupConstants" %><%@
page import="com.liferay.portal.kernel.model.Organization" %><%@
page import="com.liferay.portal.kernel.model.OrganizationConstants" %><%@
page import="com.liferay.portal.kernel.model.Permission" %><%@
page import="com.liferay.portal.kernel.model.PermissionDisplay" %><%@
page import="com.liferay.portal.kernel.model.Portlet" %><%@
page import="com.liferay.portal.kernel.model.ResourceConstants" %><%@
page import="com.liferay.portal.kernel.model.Role" %><%@
page import="com.liferay.portal.kernel.model.User" %><%@
page import="com.liferay.portal.kernel.model.UserGroupRole" %><%@
page import="com.liferay.portal.kernel.model.role.RoleConstants" %><%@
page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %><%@
page import="com.liferay.portal.kernel.portlet.PortalPreferences" %><%@
page import="com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil" %><%@
page import="com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil" %><%@
page import="com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder" %><%@
page import="com.liferay.portal.kernel.security.membershippolicy.OrganizationMembershipPolicyUtil" %><%@
page import="com.liferay.portal.kernel.security.permission.ActionKeys" %><%@
page import="com.liferay.portal.kernel.security.permission.ResourceActionsUtil" %><%@
page import="com.liferay.portal.kernel.security.permission.comparator.ModelResourceWeightComparator" %><%@
page import="com.liferay.portal.kernel.service.GroupLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.GroupServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.OrganizationLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.OrganizationServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.PortletLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.RoleServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.UserGroupRoleLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.permission.RolePermissionUtil" %><%@
page import="com.liferay.portal.kernel.util.ArrayUtil" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.GetterUtil" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.LinkedHashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.ListUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.portal.kernel.util.PortletKeys" %><%@
page import="com.liferay.portal.kernel.util.PropsValues" %><%@
page import="com.liferay.portal.kernel.util.StringUtil" %><%@
page import="com.liferay.portal.kernel.util.UnicodeProperties" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.portal.kernel.workflow.WorkflowConstants" %><%@
page import="com.liferay.portal.security.membershippolicy.RoleMembershipPolicyUtil" %><%@
page import="com.liferay.portal.security.membershippolicy.SiteMembershipPolicyUtil" %><%@
page import="com.liferay.portlet.usersadmin.util.UsersAdminUtil" %><%@
page import="com.liferay.roles.admin.constants.RolesAdminPortletKeys" %><%@
page import="com.liferay.roles.admin.constants.RolesAdminWebKeys" %><%@
page import="com.liferay.roles.admin.role.type.contributor.RoleTypeContributor" %><%@
page import="com.liferay.roles.admin.web.internal.display.context.EditRoleAssignmentsManagementToolbarDisplayContext" %><%@
page import="com.liferay.roles.admin.web.internal.display.context.EditRolePermissionsFormDisplayContext" %><%@
page import="com.liferay.roles.admin.web.internal.display.context.EditRolePermissionsNavigationDisplayContext" %><%@
page import="com.liferay.roles.admin.web.internal.display.context.EditRolePermissionsResourceDisplayContext" %><%@
page import="com.liferay.roles.admin.web.internal.display.context.EditRolePermissionsSummaryDisplayContext" %><%@
page import="com.liferay.roles.admin.web.internal.display.context.RoleDisplayContext" %><%@
page import="com.liferay.roles.admin.web.internal.display.context.SelectRoleManagementToolbarDisplayContext" %><%@
page import="com.liferay.roles.admin.web.internal.display.context.ViewRolesManagementToolbarDisplayContext" %><%@
page import="com.liferay.roles.admin.web.internal.display.util.SegmentsEntryDisplayUtil" %><%@
page import="com.liferay.roles.admin.web.internal.frontend.taglib.clay.servlet.taglib.GroupVerticalCard" %><%@
page import="com.liferay.roles.admin.web.internal.frontend.taglib.clay.servlet.taglib.OrganizationVerticalCard" %><%@
page import="com.liferay.roles.admin.web.internal.frontend.taglib.clay.servlet.taglib.UserGroupVerticalCard" %><%@
page import="com.liferay.roles.admin.web.internal.frontend.taglib.clay.servlet.taglib.UserVerticalCard" %><%@
page import="com.liferay.roles.admin.web.internal.role.type.contributor.util.RoleTypeContributorRetrieverUtil" %><%@
page import="com.liferay.site.search.GroupSearch" %><%@
page import="com.liferay.taglib.search.ResultRow" %><%@
page import="com.liferay.template.constants.TemplatePortletKeys" %>

<%@ page import="jakarta.portlet.PortletURL" %>

<%@ page import="java.io.Serializable" %>

<%@ page import="java.util.List" %><%@
page import="java.util.Map" %><%@
page import="java.util.Objects" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
PortalPreferences portalPreferences = PortletPreferencesFactoryUtil.getPortalPreferences(liferayPortletRequest);

boolean filterManageableGroups = true;
boolean filterManageableOrganizations = true;
boolean filterManageableRoles = true;

if (permissionChecker.isCompanyAdmin()) {
	filterManageableGroups = false;
	filterManageableOrganizations = false;
}

RoleDisplayContext roleDisplayContext = new RoleDisplayContext(request, renderResponse);
%>

<%@ include file="/init-ext.jsp" %>