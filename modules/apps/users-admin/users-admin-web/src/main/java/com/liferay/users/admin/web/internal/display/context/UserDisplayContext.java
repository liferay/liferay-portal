/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.display.context;

import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.criteria.GroupItemSelectorReturnType;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.organizations.item.selector.OrganizationItemSelectorCriterion;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.PasswordPolicy;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.UserGroupGroupRole;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.OrganizationLocalServiceUtil;
import com.liferay.portal.kernel.service.OrganizationServiceUtil;
import com.liferay.portal.kernel.service.PasswordPolicyLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserGroupGroupRoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.membershippolicy.RoleMembershipPolicyUtil;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.usersadmin.util.UsersAdminUtil;
import com.liferay.site.item.selector.SiteItemSelectorCriterion;
import com.liferay.user.groups.admin.item.selector.UserGroupItemSelectorCriterion;

import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Pei-Jung Lan
 */
public class UserDisplayContext {

	public UserDisplayContext(
			HttpServletRequest httpServletRequest,
			InitDisplayContext initDisplayContext,
			LiferayPortletResponse liferayPortletResponse)
		throws PortalException {

		_httpServletRequest = httpServletRequest;
		_initDisplayContext = initDisplayContext;
		_liferayPortletResponse = liferayPortletResponse;

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		_permissionChecker = themeDisplay.getPermissionChecker();

		_renderResponse = (RenderResponse)httpServletRequest.getAttribute(
			JavaConstants.JAVAX_PORTLET_RESPONSE);

		_selUser = PortalUtil.getSelectedUser(httpServletRequest);
		_themeDisplay = themeDisplay;
	}

	public Contact getContact() throws PortalException {
		if (_selUser != null) {
			return _selUser.getContact();
		}

		return null;
	}

	public String getGroupItemSelectorURL() {
		ItemSelector itemSelector =
			(ItemSelector)_httpServletRequest.getAttribute(
				ItemSelector.class.getName());

		SiteItemSelectorCriterion siteItemSelectorCriterion =
			new SiteItemSelectorCriterion();

		siteItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new GroupItemSelectorReturnType());
		siteItemSelectorCriterion.setIncludeCompany(false);
		siteItemSelectorCriterion.setIncludeRecentSites(false);

		return String.valueOf(
			itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(_httpServletRequest),
				_liferayPortletResponse.getNamespace() + "selectGroup",
				siteItemSelectorCriterion));
	}

	public List<Group> getGroups() throws PortalException {
		if (_selUser == null) {
			return Collections.emptyList();
		}

		if (!_initDisplayContext.isFilterManageableGroups()) {
			return _selUser.getGroups();
		}

		return UsersAdminUtil.filterGroups(
			_themeDisplay.getPermissionChecker(), _selUser.getGroups());
	}

	public List<Group> getInheritedSiteGroups() throws PortalException {
		SortedSet<Group> inheritedSiteGroupsSet = new TreeSet<>();

		inheritedSiteGroupsSet.addAll(
			ListUtil.filter(
				GroupLocalServiceUtil.getUserGroupsRelatedGroups(
					getUserGroups()),
				group -> !group.isDepot()));
		inheritedSiteGroupsSet.addAll(
			ListUtil.filter(
				_getOrganizationRelatedGroups(), group -> !group.isDepot()));

		return ListUtil.fromCollection(inheritedSiteGroupsSet);
	}

	public List<UserGroupGroupRole> getInheritedSiteRoles() {
		if (_selUser == null) {
			return Collections.emptyList();
		}

		return UserGroupGroupRoleLocalServiceUtil.getUserGroupGroupRolesByUser(
			_selUser.getUserId());
	}

	public String getOrganizationItemSelectorURL(boolean multiSelection)
		throws PortalException {

		ItemSelector itemSelector =
			(ItemSelector)_httpServletRequest.getAttribute(
				ItemSelector.class.getName());

		OrganizationItemSelectorCriterion organizationItemSelectorCriterion =
			new OrganizationItemSelectorCriterion();

		organizationItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new UUIDItemSelectorReturnType());
		organizationItemSelectorCriterion.setMultiSelection(multiSelection);
		organizationItemSelectorCriterion.setSelectedOrganizationIds(
			_getSelectedOrganizationIds());

		return String.valueOf(
			itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(_httpServletRequest),
				_liferayPortletResponse.getNamespace() + "selectOrganization",
				organizationItemSelectorCriterion));
	}

	public List<UserGroupRole> getOrganizationRoles() throws PortalException {
		return ListUtil.filter(
			_getUserGroupRoles(), UserGroupRole::hasOrganizationRole);
	}

	public List<Organization> getOrganizations() throws PortalException {
		if (_selUser != null) {
			List<Organization> organizations = _selUser.getOrganizations();

			if (!PropsValues.ORGANIZATIONS_MEMBERSHIP_STRICT) {
				organizations.addAll(_getParentOrganizations(organizations));
			}

			if (!_initDisplayContext.isFilterManageableOrganizations()) {
				return organizations;
			}

			return UsersAdminUtil.filterOrganizations(
				_themeDisplay.getPermissionChecker(), organizations);
		}

		String organizationIds = ParamUtil.getString(
			_httpServletRequest, "organizationsSearchContainerPrimaryKeys");

		if (Validator.isNull(organizationIds)) {
			return Collections.emptyList();
		}

		long[] organizationIdsArray = StringUtil.split(organizationIds, 0L);

		return OrganizationLocalServiceUtil.getOrganizations(
			organizationIdsArray);
	}

	public PasswordPolicy getPasswordPolicy() throws PortalException {
		if (_selUser != null) {
			return _selUser.getPasswordPolicy();
		}

		return PasswordPolicyLocalServiceUtil.getDefaultPasswordPolicy(
			_themeDisplay.getCompanyId());
	}

	public List<Group> getRoleGroups() throws PortalException {
		return ListUtil.filter(
			_getAllGroups(),
			group -> RoleLocalServiceUtil.hasGroupRoles(group.getGroupId()));
	}

	public List<Role> getRoles() {
		if (_selUser == null) {
			return Collections.emptyList();
		}

		if (!_initDisplayContext.isFilterManageableRoles()) {
			return _selUser.getRoles();
		}

		return UsersAdminUtil.filterRoles(
			_permissionChecker, _selUser.getRoles());
	}

	public User getSelectedUser() {
		return _selUser;
	}

	public List<Group> getSiteGroups() throws PortalException {
		if (_selUser == null) {
			return Collections.emptyList();
		}

		if (!_initDisplayContext.isFilterManageableGroups()) {
			return _selUser.getSiteGroups();
		}

		return UsersAdminUtil.filterGroups(
			_themeDisplay.getPermissionChecker(), _selUser.getSiteGroups());
	}

	public List<UserGroupRole> getSiteRoles() throws PortalException {
		return ListUtil.filter(
			_getUserGroupRoles(), UserGroupRole::hasSiteRole);
	}

	public String getUserGroupItemSelectorURL() {
		ItemSelector itemSelector =
			(ItemSelector)_httpServletRequest.getAttribute(
				ItemSelector.class.getName());

		UserGroupItemSelectorCriterion userGroupItemSelectorCriterion =
			new UserGroupItemSelectorCriterion();

		userGroupItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new UUIDItemSelectorReturnType());
		userGroupItemSelectorCriterion.setFilterManageableUserGroups(
			_initDisplayContext.isFilterManageableUserGroups());

		return String.valueOf(
			itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(_httpServletRequest),
				_liferayPortletResponse.getNamespace() + "selectUserGroup",
				userGroupItemSelectorCriterion));
	}

	public List<UserGroup> getUserGroups() {
		if (_selUser == null) {
			return Collections.emptyList();
		}

		if (!_initDisplayContext.isFilterManageableUserGroups()) {
			return _selUser.getUserGroups();
		}

		return UsersAdminUtil.filterUserGroups(
			_permissionChecker, _selUser.getUserGroups());
	}

	public boolean isAllowRemoveRole(Role role) throws PortalException {
		User selUser = getSelectedUser();

		if (RoleMembershipPolicyUtil.isRoleRequired(
				selUser.getUserId(), role.getRoleId())) {

			return false;
		}

		if (!Objects.equals(RoleConstants.ADMINISTRATOR, role.getName())) {
			return true;
		}

		if (UserLocalServiceUtil.getRoleUsersCount(role.getRoleId()) == 1) {
			return false;
		}

		return true;
	}

	private List<Group> _getAllGroups() throws PortalException {
		List<Group> allGroups = new ArrayList<>();

		allGroups.addAll(getGroups());
		allGroups.addAll(getInheritedSiteGroups());
		allGroups.addAll(
			GroupLocalServiceUtil.getOrganizationsGroups(getOrganizations()));
		allGroups.addAll(
			GroupLocalServiceUtil.getUserGroupsGroups(getUserGroups()));

		return allGroups;
	}

	private List<Group> _getOrganizationRelatedGroups() throws PortalException {
		List<Organization> organizations = getOrganizations();

		if (organizations.isEmpty()) {
			return Collections.emptyList();
		}

		return GroupLocalServiceUtil.getOrganizationsRelatedGroups(
			organizations);
	}

	private List<Organization> _getParentOrganizations(
			List<Organization> organizations)
		throws PortalException {

		List<Organization> parentOrganizations = new ArrayList<>();

		for (Organization organization : organizations) {
			Organization parentOrganization =
				organization.getParentOrganization();

			if ((parentOrganization != null) &&
				!organizations.contains(parentOrganization) &&
				!parentOrganizations.contains(parentOrganization)) {

				parentOrganizations.add(parentOrganization);
			}
		}

		return parentOrganizations;
	}

	private long[] _getSelectedOrganizationIds() throws PortalException {
		long[] selectedOrganizationIds = new long[0];

		if (_selUser != null) {
			selectedOrganizationIds = _selUser.getOrganizationIds();
		}

		long organizationId = ParamUtil.getLong(
			_httpServletRequest, "organizationId");

		Organization organization = OrganizationServiceUtil.fetchOrganization(
			organizationId);

		if (organization == null) {
			return selectedOrganizationIds;
		}

		return ArrayUtil.append(
			selectedOrganizationIds, organization.getOrganizationId());
	}

	private List<UserGroupRole> _getUserGroupRoles() throws PortalException {
		if (_selUser == null) {
			return Collections.emptyList();
		}

		List<UserGroupRole> userGroupRoles = _selUser.getUserGroupRoles();

		if (!_initDisplayContext.isFilterManageableUserGroupRoles()) {
			return userGroupRoles;
		}

		return UsersAdminUtil.filterUserGroupRoles(
			_permissionChecker, userGroupRoles);
	}

	private final HttpServletRequest _httpServletRequest;
	private final InitDisplayContext _initDisplayContext;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final PermissionChecker _permissionChecker;
	private final RenderResponse _renderResponse;
	private final User _selUser;
	private final ThemeDisplay _themeDisplay;

}