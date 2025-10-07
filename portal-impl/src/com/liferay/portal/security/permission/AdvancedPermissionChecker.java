/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.permission;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.internal.security.permission.contributor.RoleCollectionImpl;
import com.liferay.portal.kernel.exception.NoSuchResourcePermissionException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.model.PortletConstants;
import com.liferay.portal.kernel.model.Resource;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroupGroupRole;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.security.permission.UserBag;
import com.liferay.portal.kernel.security.permission.contributor.RoleContributor;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.OrganizationLocalServiceUtil;
import com.liferay.portal.kernel.service.ResourceLocalServiceUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.TeamLocalServiceUtil;
import com.liferay.portal.kernel.service.UserGroupGroupRoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserGroupRoleLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.LayoutSetPrototypePermissionUtil;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.impl.ResourceImpl;
import com.liferay.portal.service.permission.LayoutPrototypePermissionUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.time.StopWatch;

/**
 * @author Charles May
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 * @author Wesley Gong
 * @author Connor McKay
 */
public class AdvancedPermissionChecker extends BasePermissionChecker {

	@Override
	public AdvancedPermissionChecker clone() {
		return new AdvancedPermissionChecker();
	}

	@Override
	public long[] getGuestUserRoleIds() {
		Map<Long, long[]> groupRoleIds =
			PermissionCacheUtil.getUserGroupRoleIds(guestUserId);

		long[] roleIds = groupRoleIds.get(
			GroupConstants.DEFAULT_PARENT_GROUP_ID);

		if (roleIds != null) {
			return roleIds;
		}

		List<Role> roles = RoleLocalServiceUtil.getUserRoles(guestUserId);

		roleIds = ListUtil.toLongArray(roles, Role.ROLE_ID_ACCESSOR);

		if (roleIds.length > 1) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"More than one role ID was returned for the guest user. " +
						"This may cause guest users to have more permissions " +
							"than intended.");
			}

			Arrays.sort(roleIds);
		}

		groupRoleIds.put(GroupConstants.DEFAULT_PARENT_GROUP_ID, roleIds);

		return roleIds;
	}

	@Override
	public long[] getRoleIds(long userId, long groupId) {
		try {
			return _applyRoleContributors(
				doGetRoleIds(userId, groupId), groupId);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return PermissionChecker.DEFAULT_ROLE_IDS;
		}
	}

	@Override
	public UserBag getUserBag() throws PortalException {
		return UserBagFactoryUtil.create(getUser());
	}

	@Override
	public boolean hasOwnerPermission(
		long companyId, String name, String primKey, long ownerId,
		String actionId) {

		if (ownerId != getUserId()) {
			return false;
		}

		boolean ownerIsGuestUser = false;

		if (ownerId == guestUserId) {
			ownerIsGuestUser = true;
		}

		if (ownerIsGuestUser) {
			List<String> guestUnsupportedActions;

			if (name.indexOf(CharPool.PERIOD) != -1) {
				guestUnsupportedActions =
					ResourceActionsUtil.getModelResourceGuestUnsupportedActions(
						name);
			}
			else {
				guestUnsupportedActions =
					ResourceActionsUtil.
						getPortletResourceGuestUnsupportedActions(name);
			}

			if (guestUnsupportedActions.contains(actionId)) {
				return false;
			}
		}

		try {
			long ownerRoleId = getOwnerRoleId();

			if (ownerIsGuestUser) {
				Role guestRole = RoleLocalServiceUtil.getRole(
					companyId, RoleConstants.GUEST);

				ownerRoleId = guestRole.getRoleId();
			}

			return ResourcePermissionLocalServiceUtil.hasResourcePermission(
				companyId, name, ResourceConstants.SCOPE_INDIVIDUAL, primKey,
				ownerRoleId, actionId);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return false;
	}

	@Override
	public boolean hasPermission(
		Group group, String name, String primKey, String actionId) {

		StopWatch stopWatch = new StopWatch();

		stopWatch.start();

		long groupId = 0;

		try {
			if (group != null) {

				// If the group is a scope group for a layout, check the
				// original group.

				if (group.isLayout()) {
					Layout layout = LayoutLocalServiceUtil.getLayout(
						group.getClassPK());

					group = layout.getGroup();
				}
				else if (group.isUserPersonalSite()) {
					return false;
				}

				// If the group is a personal site, check the "User Personal
				// Site" group.

				if (group.isUser() && (group.getClassPK() == getUserId())) {
					group = GroupLocalServiceUtil.getGroup(
						getCompanyId(), GroupConstants.USER_PERSONAL_SITE);
				}

				groupId = group.getGroupId();
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		long[] roleIds = getRoleIds(getUserId(), groupId);

		Boolean value = PermissionCacheUtil.getPermission(
			groupId, name, primKey, roleIds, actionId);

		if (value != null) {
			return value;
		}

		value = _hasPermissionImpl(group, name, primKey, roleIds, actionId);

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Checking permission for ", groupId, " ", name, " ",
					primKey, " ", actionId, " takes ", stopWatch.getTime(),
					" ms"));
		}

		PermissionCacheUtil.putPermission(
			groupId, name, primKey, roleIds, actionId, value);

		return value;
	}

	@Override
	public void init(User user, RoleContributor[] roleContributors) {
		init(user);

		_roleContributors = roleContributors;
	}

	@Override
	public boolean isCompanyAdmin() {
		try {
			return isCompanyAdminImpl(user.getCompanyId());
		}
		catch (Exception exception) {
			_log.error(exception);

			return false;
		}
	}

	@Override
	public boolean isCompanyAdmin(long companyId) {
		try {
			return isCompanyAdminImpl(companyId);
		}
		catch (Exception exception) {
			_log.error(exception);

			return false;
		}
	}

	@Override
	public boolean isContentReviewer(long companyId, long groupId) {
		try {
			return isContentReviewerImpl(companyId, groupId);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return false;
	}

	@Override
	public boolean isGroupAdmin(long groupId) {
		try {
			Group group = null;

			if (groupId > 0) {
				group = GroupLocalServiceUtil.fetchGroup(groupId);
			}

			return _isGroupAdminImpl(group);
		}
		catch (Exception exception) {
			_log.error(exception);

			return false;
		}
	}

	@Override
	public boolean isGroupMember(long groupId) {
		try {
			return isGroupMemberImpl(groupId);
		}
		catch (Exception exception) {
			_log.error(exception);

			return false;
		}
	}

	@Override
	public boolean isGroupOwner(long groupId) {
		try {
			return isGroupOwnerImpl(groupId);
		}
		catch (Exception exception) {
			_log.error(exception);

			return false;
		}
	}

	@Override
	public boolean isOrganizationAdmin(long organizationId) {
		try {
			return isOrganizationAdminImpl(organizationId);
		}
		catch (Exception exception) {
			_log.error(exception);

			return false;
		}
	}

	@Override
	public boolean isOrganizationOwner(long organizationId) {
		try {
			return isOrganizationOwnerImpl(organizationId);
		}
		catch (Exception exception) {
			_log.error(exception);

			return false;
		}
	}

	protected void addTeamRoles(long userId, Group group, Set<Long> roleIds)
		throws Exception {

		int count = TeamLocalServiceUtil.getGroupTeamsCount(group.getGroupId());

		List<Role> roles = null;

		if (count > 0) {
			roles = RoleLocalServiceUtil.getUserTeamRoles(
				userId, group.getGroupId());

			for (Role role : roles) {
				roleIds.add(role.getRoleId());
			}
		}

		if (group.isStaged() && !group.isStagedRemotely()) {
			Group stagingGroup = group.getStagingGroup();

			count = TeamLocalServiceUtil.getGroupTeamsCount(
				stagingGroup.getGroupId());

			if (count > 0) {
				roles = RoleLocalServiceUtil.getUserTeamRoles(
					userId, stagingGroup.getGroupId());

				for (Role role : roles) {
					roleIds.add(role.getRoleId());
				}
			}
		}
	}

	protected boolean doCheckPermission(
			long companyId, long groupId, String name, String primKey,
			long[] roleIds, String actionId, StopWatch stopWatch)
		throws Exception {

		logHasUserPermission(groupId, name, primKey, actionId, stopWatch, 1);

		List<Resource> resources = getResources(
			companyId, groupId, name, primKey, actionId);

		resources = fixMissingResources(
			companyId, groupId, name, primKey, actionId, resources);

		logHasUserPermission(groupId, name, primKey, actionId, stopWatch, 3);

		// Check if user has access to perform the action on the given resource
		// scopes. The resources are scoped to check first for an individual
		// class, then for the group that the class may belong to, and then for
		// the company that the class belongs to.

		boolean value = ResourceLocalServiceUtil.hasUserPermissions(
			user.getUserId(), groupId, resources, actionId, roleIds);

		logHasUserPermission(groupId, name, primKey, actionId, stopWatch, 4);

		return value;
	}

	protected long[] doGetRoleIds(long userId, long groupId) throws Exception {
		if (!signedIn) {
			return getGuestUserRoleIds();
		}

		Map<Long, long[]> groupRoleIds =
			PermissionCacheUtil.getUserGroupRoleIds(userId);

		long[] roleIds = groupRoleIds.get(groupId);

		if (roleIds != null) {
			return roleIds;
		}

		if (_contributedRoleIds != null) {
			_contributedRoleIds.remove(groupId);
		}

		Group group = null;

		long parentGroupId = 0;

		if (groupId > 0) {
			group = GroupLocalServiceUtil.getGroup(groupId);

			if (group.isLayout()) {
				parentGroupId = group.getParentGroupId();

				if (parentGroupId > 0) {
					group = GroupLocalServiceUtil.getGroup(parentGroupId);
				}
			}
		}

		UserBag userBag = getUserBag();

		Set<Long> roleIdsSet = SetUtil.fromArray(userBag.getRoleIds());

		List<UserGroupRole> userGroupRoles =
			UserGroupRoleLocalServiceUtil.getUserGroupRoles(userId);

		for (UserGroupRole userGroupRole : userGroupRoles) {
			if (userGroupRole.getGroupId() == groupId) {
				roleIdsSet.add(userGroupRole.getRoleId());
			}
		}

		if (parentGroupId > 0) {
			for (UserGroupRole userGroupRole : userGroupRoles) {
				if (userGroupRole.getGroupId() == parentGroupId) {
					roleIdsSet.add(userGroupRole.getRoleId());
				}
			}
		}

		long[] userUserGroupsIds = userBag.getUserUserGroupsIds();

		if (userUserGroupsIds.length > 0) {
			List<UserGroupGroupRole> userGroupGroupRoles =
				UserGroupGroupRoleLocalServiceUtil.getUserGroupGroupRolesByUser(
					userId, groupId);

			for (UserGroupGroupRole userGroupGroupRole : userGroupGroupRoles) {
				roleIdsSet.add(userGroupGroupRole.getRoleId());
			}

			if (parentGroupId > 0) {
				userGroupGroupRoles =
					UserGroupGroupRoleLocalServiceUtil.getUserGroupGroupRoles(
						userId, parentGroupId);

				for (UserGroupGroupRole userGroupGroupRole :
						userGroupGroupRoles) {

					roleIdsSet.add(userGroupGroupRole.getRoleId());
				}
			}
		}

		if (group != null) {
			if (group.isOrganization() && userBag.hasUserOrgGroup(group)) {
				Role organizationUserRole = RoleLocalServiceUtil.getRole(
					group.getCompanyId(), RoleConstants.ORGANIZATION_USER);

				roleIdsSet.add(organizationUserRole.getRoleId());
			}

			if (group.isSite() &&
				(userBag.hasUserGroup(group) ||
				 userBag.hasUserOrgGroup(group))) {

				Role siteMemberRole = RoleLocalServiceUtil.getRole(
					group.getCompanyId(), RoleConstants.SITE_MEMBER);

				roleIdsSet.add(siteMemberRole.getRoleId());
			}

			if (group.isUserPersonalSite()) {
				Role powerUserRole = RoleLocalServiceUtil.getRole(
					getCompanyId(), RoleConstants.POWER_USER);

				if (userBag.hasRole(powerUserRole)) {
					Role siteMemberRole = RoleLocalServiceUtil.getRole(
						group.getCompanyId(), RoleConstants.SITE_MEMBER);

					roleIdsSet.add(siteMemberRole.getRoleId());
				}
			}

			if ((group.isOrganization() && userBag.hasUserOrgGroup(group)) ||
				(group.isSite() && userBag.hasUserGroup(group))) {

				addTeamRoles(userId, group, roleIdsSet);
			}
		}

		if (checkGuest) {
			for (long roleId : getGuestUserRoleIds()) {
				roleIdsSet.add(roleId);
			}
		}

		roleIds = ArrayUtil.toLongArray(roleIdsSet);

		Arrays.sort(roleIds);

		groupRoleIds.put(groupId, roleIds);

		return roleIds;
	}

	protected List<Resource> fixMissingResources(
			long companyId, long groupId, String name, String primKey,
			String actionId, List<Resource> resources)
		throws Exception {

		int count =
			ResourcePermissionLocalServiceUtil.getResourcePermissionsCount(
				companyId, name, ResourceConstants.SCOPE_INDIVIDUAL, primKey);

		if (count > 0) {
			return resources;
		}

		String newIndividualResourcePrimKey = null;

		if (primKey.contains(PortletConstants.LAYOUT_SEPARATOR)) {

			// Using defaults because custom permissions defined for portlet
			// resource are not defined

			newIndividualResourcePrimKey = name;

			if (_log.isDebugEnabled()) {
				String message = StringBundler.concat(
					"Using defaults because custom permissions for portlet ",
					"resource ", name, " are not defined");

				_log.debug(message, new IllegalArgumentException(message));
			}
		}
		else if ((groupId > 0) &&
				 ResourceActionsUtil.isRootModelResource(name)) {

			// Using defaults because custom permissions defined for root model
			// resource are not defined

			newIndividualResourcePrimKey = name;

			if (_log.isDebugEnabled()) {
				String message = StringBundler.concat(
					"Using defaults because custom permissions for root model ",
					"resource ", name, " are not defined");

				_log.debug(message, new IllegalArgumentException(message));
			}
		}
		else if (primKey.equals("0") ||
				 primKey.equals(
					 String.valueOf(ResourceConstants.PRIMKEY_DNE)) ||
				 (primKey.equals(String.valueOf(companyId)) &&
				  !name.equals(Company.class.getName()))) {

			newIndividualResourcePrimKey = name;

			if (_log.isWarnEnabled()) {
				StringBundler sb = new StringBundler(9);

				sb.append("Using ");
				sb.append(name);
				sb.append(" as the primary key instead of the legacy primary ");
				sb.append("key ");
				sb.append(primKey);
				sb.append(" that was used for permission checking of ");
				sb.append(name);
				sb.append(" in company ");
				sb.append(companyId);

				_log.warn(
					sb.toString(), new IllegalArgumentException(sb.toString()));
			}
		}

		if (newIndividualResourcePrimKey != null) {
			Resource individualResource = resources.get(0);

			if (individualResource.getScope() !=
					ResourceConstants.SCOPE_INDIVIDUAL) {

				throw new IllegalArgumentException(
					"The first resource must be an individual scope");
			}

			individualResource.setPrimKey(name);
		}

		return resources;
	}

	/**
	 * Returns representations of the resource at each scope level.
	 *
	 * <p>
	 * For example, if the class name and primary key of a blog entry were
	 * passed to this method, it would return a resource for the blog entry
	 * itself (individual scope), a resource representing all blog entries
	 * within its group (group scope), a resource standing for all blog entries
	 * within a group the user has a suitable role in (group-template scope),
	 * and a resource signifying all blog entries within the company (company
	 * scope).
	 * </p>
	 *
	 * @param  companyId the primary key of the company
	 * @param  groupId the primary key of the group containing the resource
	 * @param  name the resource's name, which can be either a class name or a
	 *         portlet ID
	 * @param  primKey the primary key of the resource
	 * @param  actionId unused
	 * @return representations of the resource at each scope level
	 * @throws Exception if an exception occurred
	 */
	protected List<Resource> getResources(
			long companyId, long groupId, String name, String primKey,
			String actionId)
		throws Exception {

		// Individual

		List<Resource> resources = new ArrayList<>(4);

		Resource individualResource = _getResource(
			companyId, name, ResourceConstants.SCOPE_INDIVIDUAL, primKey);

		resources.add(individualResource);

		// Group

		if (groupId > 0) {
			Resource groupResource = _getResource(
				companyId, name, ResourceConstants.SCOPE_GROUP,
				String.valueOf(groupId));

			resources.add(groupResource);
		}

		// Group template

		if (signedIn && (groupId > 0)) {
			Resource groupTemplateResource = _getResource(
				companyId, name, ResourceConstants.SCOPE_GROUP_TEMPLATE,
				String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID));

			resources.add(groupTemplateResource);
		}

		// Company

		Resource companyResource = _getResource(
			companyId, name, ResourceConstants.SCOPE_COMPANY,
			String.valueOf(companyId));

		resources.add(companyResource);

		return resources;
	}

	protected boolean isCompanyAdminImpl(long companyId) throws Exception {
		if (!signedIn) {
			return false;
		}

		if (isOmniadmin()) {
			return true;
		}

		Boolean value = PermissionCacheUtil.getUserPrimaryKeyRole(
			getUserId(), companyId, RoleConstants.ADMINISTRATOR);

		if (value == null) {
			value = RoleLocalServiceUtil.hasUserRole(
				user.getUserId(), companyId, RoleConstants.ADMINISTRATOR, true);

			PermissionCacheUtil.putUserPrimaryKeyRole(
				getUserId(), companyId, RoleConstants.ADMINISTRATOR, value);
		}

		return value;
	}

	protected boolean isContentReviewerImpl(long groupId)
		throws PortalException {

		if (isCompanyAdmin() || isGroupAdmin(groupId)) {
			return true;
		}

		Group group = GroupLocalServiceUtil.getGroup(groupId);

		if (RoleLocalServiceUtil.hasUserRole(
				getUserId(), group.getCompanyId(),
				RoleConstants.PORTAL_CONTENT_REVIEWER, true) ||
			(group.isSite() &&
			 UserGroupRoleLocalServiceUtil.hasUserGroupRole(
				 getUserId(), groupId, RoleConstants.SITE_CONTENT_REVIEWER,
				 true))) {

			return true;
		}

		return false;
	}

	protected boolean isContentReviewerImpl(long companyId, long groupId)
		throws Exception {

		if (!signedIn) {
			return false;
		}

		if (isOmniadmin() || isCompanyAdmin(companyId)) {
			return true;
		}

		if (groupId <= 0) {
			return false;
		}

		if (isGroupAdmin(groupId)) {
			return true;
		}

		Boolean value = PermissionCacheUtil.getUserPrimaryKeyRole(
			getUserId(), groupId, RoleConstants.SITE_CONTENT_REVIEWER);

		if (value == null) {
			value = isContentReviewerImpl(groupId);

			PermissionCacheUtil.putUserPrimaryKeyRole(
				getUserId(), groupId, RoleConstants.SITE_CONTENT_REVIEWER,
				value);
		}

		return value;
	}

	protected boolean isGroupAdminImpl(Group group) throws Exception {
		if (group.isLayout()) {
			long parentGroupId = group.getParentGroupId();

			if (parentGroupId == GroupConstants.DEFAULT_PARENT_GROUP_ID) {
				return false;
			}

			group = GroupLocalServiceUtil.getGroup(parentGroupId);
		}

		if (group.isSite()) {
			if (UserGroupRoleLocalServiceUtil.hasUserGroupRole(
					getUserId(), group.getGroupId(),
					RoleConstants.SITE_ADMINISTRATOR, true) ||
				UserGroupRoleLocalServiceUtil.hasUserGroupRole(
					getUserId(), group.getGroupId(), RoleConstants.SITE_OWNER,
					true)) {

				return true;
			}

			StopWatch stopWatch = new StopWatch();

			stopWatch.start();

			Group parentGroup = group;

			while (!parentGroup.isRoot()) {
				parentGroup = parentGroup.getParentGroup();

				if (doCheckPermission(
						parentGroup.getCompanyId(), parentGroup.getGroupId(),
						Group.class.getName(),
						String.valueOf(parentGroup.getGroupId()),
						getRoleIds(getUserId(), parentGroup.getGroupId()),
						ActionKeys.MANAGE_SUBGROUPS, stopWatch)) {

					return true;
				}
			}
		}

		if (group.isCompany()) {
			return isCompanyAdmin();
		}
		else if (group.isLayoutPrototype()) {
			return LayoutPrototypePermissionUtil.contains(
				this, group.getClassPK(), ActionKeys.UPDATE);
		}
		else if (group.isLayoutSetPrototype()) {
			return LayoutSetPrototypePermissionUtil.contains(
				this, group.getClassPK(), ActionKeys.UPDATE);
		}
		else if (group.isOrganization()) {
			long organizationId = group.getOrganizationId();

			while (organizationId !=
						OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID) {

				Organization organization =
					OrganizationLocalServiceUtil.getOrganization(
						organizationId);

				long organizationGroupId = organization.getGroupId();

				if (UserGroupRoleLocalServiceUtil.hasUserGroupRole(
						getUserId(), organizationGroupId,
						RoleConstants.ORGANIZATION_ADMINISTRATOR, true) ||
					UserGroupRoleLocalServiceUtil.hasUserGroupRole(
						getUserId(), organizationGroupId,
						RoleConstants.ORGANIZATION_OWNER, true)) {

					return true;
				}

				organizationId = organization.getParentOrganizationId();
			}

			StopWatch stopWatch = new StopWatch();

			stopWatch.start();

			Organization organization =
				OrganizationLocalServiceUtil.getOrganization(
					group.getOrganizationId());

			while (!organization.isRoot()) {
				Organization parentOrganization =
					organization.getParentOrganization();

				Group parentGroup = parentOrganization.getGroup();

				if (doCheckPermission(
						parentGroup.getCompanyId(), parentGroup.getGroupId(),
						Organization.class.getName(),
						String.valueOf(parentOrganization.getOrganizationId()),
						getRoleIds(getUserId(), parentGroup.getGroupId()),
						ActionKeys.MANAGE_SUBORGANIZATIONS, stopWatch) ||
					doCheckPermission(
						parentGroup.getCompanyId(), parentGroup.getGroupId(),
						Organization.class.getName(),
						String.valueOf(parentOrganization.getOrganizationId()),
						getRoleIds(getUserId(), parentGroup.getGroupId()),
						ActionKeys.UPDATE_SUBORGANIZATIONS, stopWatch)) {

					return true;
				}

				organization = parentOrganization;
			}
		}

		return false;
	}

	protected boolean isGroupMemberImpl(long groupId) throws Exception {
		if (!signedIn || (groupId <= 0)) {
			return false;
		}

		Group group = GroupLocalServiceUtil.getGroup(groupId);

		Role role = RoleLocalServiceUtil.getRole(
			group.getCompanyId(), RoleConstants.SITE_MEMBER);

		int count = Arrays.binarySearch(
			getRoleIds(getUserId(), group.getGroupId()), role.getRoleId());

		if (count >= 0) {
			return true;
		}

		UserBag userBag = getUserBag();

		return userBag.hasUserGroup(group);
	}

	protected boolean isGroupOwnerImpl(Group group) throws PortalException {
		if (group.isSite() &&
			UserGroupRoleLocalServiceUtil.hasUserGroupRole(
				getUserId(), group.getGroupId(), RoleConstants.SITE_OWNER,
				true)) {

			return true;
		}

		if (group.isLayoutPrototype()) {
			return LayoutPrototypePermissionUtil.contains(
				this, group.getClassPK(), ActionKeys.UPDATE);
		}
		else if (group.isLayoutSetPrototype()) {
			return LayoutSetPrototypePermissionUtil.contains(
				this, group.getClassPK(), ActionKeys.UPDATE);
		}
		else if (group.isOrganization()) {
			long organizationId = group.getOrganizationId();

			while (organizationId !=
						OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID) {

				Organization organization =
					OrganizationLocalServiceUtil.getOrganization(
						organizationId);

				long organizationGroupId = organization.getGroupId();

				if (UserGroupRoleLocalServiceUtil.hasUserGroupRole(
						getUserId(), organizationGroupId,
						RoleConstants.ORGANIZATION_OWNER, true)) {

					return true;
				}

				organizationId = organization.getParentOrganizationId();
			}
		}
		else if (group.isUser()) {
			long groupUserId = group.getClassPK();

			if (getUserId() == groupUserId) {
				return true;
			}
		}

		return false;
	}

	protected boolean isGroupOwnerImpl(long groupId) throws Exception {
		if (!signedIn) {
			return false;
		}

		if (isOmniadmin()) {
			return true;
		}

		if (groupId <= 0) {
			return false;
		}

		Group group = GroupLocalServiceUtil.getGroup(groupId);

		if (isCompanyAdmin(group.getCompanyId())) {
			return true;
		}

		Boolean value = PermissionCacheUtil.getUserPrimaryKeyRole(
			getUserId(), group.getGroupId(), RoleConstants.SITE_OWNER);

		if (value == null) {
			value = isGroupOwnerImpl(group);

			PermissionCacheUtil.putUserPrimaryKeyRole(
				getUserId(), group.getGroupId(), RoleConstants.SITE_OWNER,
				value);
		}

		return value;
	}

	protected boolean isOrganizationAdminImpl(long organizationId)
		throws Exception {

		if (!signedIn) {
			return false;
		}

		if (isOmniadmin()) {
			return true;
		}

		if (organizationId <= 0) {
			return false;
		}

		Organization organization =
			OrganizationLocalServiceUtil.fetchOrganization(organizationId);

		if (organization == null) {
			return false;
		}

		if (isCompanyAdmin(organization.getCompanyId())) {
			return true;
		}

		Boolean value = PermissionCacheUtil.getUserPrimaryKeyRole(
			getUserId(), organization.getOrganizationId(),
			RoleConstants.ORGANIZATION_ADMINISTRATOR);

		if (value == null) {
			value = isOrganizationAdminImpl(organization);

			PermissionCacheUtil.putUserPrimaryKeyRole(
				getUserId(), organization.getOrganizationId(),
				RoleConstants.ORGANIZATION_ADMINISTRATOR, value);
		}

		return value;
	}

	protected boolean isOrganizationAdminImpl(Organization organization)
		throws PortalException {

		while (organization != null) {
			long organizationGroupId = organization.getGroupId();

			long userId = getUserId();

			if (UserGroupRoleLocalServiceUtil.hasUserGroupRole(
					userId, organizationGroupId,
					RoleConstants.ORGANIZATION_ADMINISTRATOR, true) ||
				UserGroupRoleLocalServiceUtil.hasUserGroupRole(
					userId, organizationGroupId,
					RoleConstants.ORGANIZATION_OWNER, true)) {

				return true;
			}

			organization = organization.getParentOrganization();
		}

		return false;
	}

	protected boolean isOrganizationOwnerImpl(long organizationId)
		throws Exception {

		if (!signedIn) {
			return false;
		}

		if (isOmniadmin()) {
			return true;
		}

		if (organizationId <= 0) {
			return false;
		}

		Organization organization =
			OrganizationLocalServiceUtil.fetchOrganization(organizationId);

		if (organization == null) {
			return false;
		}

		if (isCompanyAdmin(organization.getCompanyId())) {
			return true;
		}

		Boolean value = PermissionCacheUtil.getUserPrimaryKeyRole(
			getUserId(), organization.getOrganizationId(),
			RoleConstants.ORGANIZATION_OWNER);

		if (value == null) {
			value = isOrganizationOwnerImpl(organization);

			PermissionCacheUtil.putUserPrimaryKeyRole(
				getUserId(), organization.getOrganizationId(),
				RoleConstants.ORGANIZATION_OWNER, value);
		}

		return value;
	}

	protected boolean isOrganizationOwnerImpl(Organization organization)
		throws PortalException {

		while (organization != null) {
			long organizationGroupId = organization.getGroupId();

			if (UserGroupRoleLocalServiceUtil.hasUserGroupRole(
					getUserId(), organizationGroupId,
					RoleConstants.ORGANIZATION_OWNER, true)) {

				return true;
			}

			organization = organization.getParentOrganization();
		}

		return false;
	}

	protected void logHasUserPermission(
		long groupId, String name, String primKey, String actionId,
		StopWatch stopWatch, int block) {

		if (!_log.isDebugEnabled()) {
			return;
		}

		_log.debug(
			StringBundler.concat(
				"Checking user permission block ", block, " for ", groupId, " ",
				name, " ", primKey, " ", actionId, " takes ",
				stopWatch.getTime(), " ms"));
	}

	private long[] _applyRoleContributors(long[] roleIds, long groupId) {
		if (_roleContributors.length == 0) {
			return roleIds;
		}

		if (_contributedRoleIds == null) {
			_contributedRoleIds = new HashMap<>();
		}

		return _contributedRoleIds.computeIfAbsent(
			groupId,
			key -> {
				try {
					RoleCollectionImpl roleCollectionImpl =
						new RoleCollectionImpl(
							user, getUserBag(), roleIds, groupId, this);

					for (RoleContributor roleContributor : _roleContributors) {
						roleContributor.contribute(roleCollectionImpl);
					}

					return roleCollectionImpl.getRoleIds();
				}
				catch (PortalException portalException) {
					return ReflectionUtil.throwException(portalException);
				}
			});
	}

	private Resource _getResource(
		long companyId, String name, int scope, String primKey) {

		Resource resource = new ResourceImpl();

		resource.setCompanyId(companyId);
		resource.setName(name);
		resource.setScope(scope);
		resource.setPrimKey(primKey);

		return resource;
	}

	private boolean _hasGuestPermission(
		Group group, String name, String primKey, String actionId) {

		List<String> resourceActions = ResourceActionsUtil.getResourceActions(
			name);

		if (!resourceActions.contains(actionId)) {
			return false;
		}

		if (name.indexOf(CharPool.PERIOD) != -1) {

			// Check unsupported model actions

			List<String> actions =
				ResourceActionsUtil.getModelResourceGuestUnsupportedActions(
					name);

			if (actions.contains(actionId)) {
				return false;
			}
		}
		else {

			// Check unsupported portlet actions

			List<String> actions =
				ResourceActionsUtil.getPortletResourceGuestUnsupportedActions(
					name);

			if (actions.contains(actionId)) {
				return false;
			}
		}

		long companyId = user.getCompanyId();
		long groupId = 0;

		if (group != null) {
			companyId = group.getCompanyId();
			groupId = group.getGroupId();
		}

		try {
			List<Resource> resources = getResources(
				companyId, groupId, name, primKey, actionId);

			resources = fixMissingResources(
				companyId, groupId, name, primKey, actionId, resources);

			return ResourceLocalServiceUtil.hasUserPermissions(
				guestUserId, groupId, resources, actionId,
				_applyRoleContributors(getGuestUserRoleIds(), groupId));
		}
		catch (NoSuchResourcePermissionException
					noSuchResourcePermissionException) {

			throw new IllegalArgumentException(
				"Someone may be trying to circumvent the permission checker: " +
					noSuchResourcePermissionException.getMessage(),
				noSuchResourcePermissionException);
		}
		catch (Exception exception) {
			_log.error(exception);

			return false;
		}
	}

	private boolean _hasPermissionImpl(
		Group group, String name, String primKey, long[] roleIds,
		String actionId) {

		try {
			if (!signedIn) {
				return _hasGuestPermission(group, name, primKey, actionId);
			}

			return _hasUserPermissionImpl(
				group, name, primKey, roleIds, actionId);
		}
		catch (IllegalArgumentException illegalArgumentException) {
			throw illegalArgumentException;
		}
		catch (Exception exception) {
			_log.error(exception);

			return false;
		}
	}

	private boolean _hasUserPermissionImpl(
			Group group, String name, String primKey, long[] roleIds,
			String actionId)
		throws Exception {

		StopWatch stopWatch = new StopWatch();

		stopWatch.start();

		long companyId = user.getCompanyId();
		long groupId = 0;

		if (group != null) {
			companyId = group.getCompanyId();
			groupId = group.getGroupId();
		}

		try {
			boolean hasPermission = doCheckPermission(
				companyId, groupId, name, primKey, roleIds, actionId,
				stopWatch);

			if (hasPermission) {
				return true;
			}
		}
		catch (NoSuchResourcePermissionException
					noSuchResourcePermissionException) {

			throw new IllegalArgumentException(
				"Someone may be trying to circumvent the permission checker: " +
					noSuchResourcePermissionException.getMessage(),
				noSuchResourcePermissionException);
		}

		if (isOmniadmin() ||
			(name.equals(Organization.class.getName()) &&
			 isOrganizationAdminImpl(GetterUtil.getLong(primKey)))) {

			return true;
		}

		if (isCompanyAdminImpl(companyId)) {
			return true;
		}

		if (_isGroupAdminImpl(group)) {

			// Check if the layout manager has permission to do this action for
			// the current portlet

			if (Validator.isNull(name) || Validator.isNull(primKey) ||
				!primKey.contains(PortletConstants.LAYOUT_SEPARATOR) ||
				PortletPermissionUtil.hasLayoutManagerPermission(
					name, actionId)) {

				return true;
			}
		}

		// Allow read-only access to personal site assets. Group is user
		// personal site here only when current user is the personal site owner.

		if ((group != null) && group.isUserPersonalSite() &&
			ActionKeys.VIEW.equals(actionId)) {

			// The only check we can perform on top is for the Site Member role.
			// The Site Member role is derived from the Power User role. When a
			// user is missing the Power User role, then the Site Member role
			// is not granted and we do not check default actions granted to the
			// Site Member role. Hence, it is the only role left. All other
			// roles were already checked.

			Role siteMemberRole = RoleLocalServiceUtil.getRole(
				getCompanyId(), RoleConstants.SITE_MEMBER);

			if (!ArrayUtil.contains(roleIds, siteMemberRole.getRoleId())) {
				boolean hasPermission = doCheckPermission(
					companyId, groupId, name, primKey,
					new long[] {siteMemberRole.getRoleId()}, actionId,
					stopWatch);

				if (hasPermission) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean _isGroupAdminImpl(Group group) throws Exception {
		if (!signedIn) {
			return false;
		}

		if (isOmniadmin()) {
			return true;
		}

		if (group == null) {
			return false;
		}

		if (isCompanyAdmin(group.getCompanyId())) {
			return true;
		}

		Boolean value = PermissionCacheUtil.getUserPrimaryKeyRole(
			getUserId(), group.getGroupId(), RoleConstants.SITE_ADMINISTRATOR);

		if (value == null) {
			value = isGroupAdminImpl(group);

			PermissionCacheUtil.putUserPrimaryKeyRole(
				getUserId(), group.getGroupId(),
				RoleConstants.SITE_ADMINISTRATOR, value);
		}

		return value;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AdvancedPermissionChecker.class);

	private Map<Long, long[]> _contributedRoleIds;
	private RoleContributor[] _roleContributors;

}