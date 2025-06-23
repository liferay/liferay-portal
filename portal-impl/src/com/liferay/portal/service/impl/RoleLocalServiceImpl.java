/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.admin.kernel.util.PortalMyAccountApplicationType;
import com.liferay.expando.kernel.service.ExpandoRowLocalService;
import com.liferay.exportimport.kernel.incomplete.model.IncompleteModelManagerUtil;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.JoinStep;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.cache.thread.local.Lifecycle;
import com.liferay.portal.kernel.cache.thread.local.ThreadLocalCachable;
import com.liferay.portal.kernel.cache.thread.local.ThreadLocalCache;
import com.liferay.portal.kernel.cache.thread.local.ThreadLocalCacheManager;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.DuplicateRoleException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.RequiredRoleException;
import com.liferay.portal.kernel.exception.RoleNameException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupTable;
import com.liferay.portal.kernel.model.Groups_OrgsTable;
import com.liferay.portal.kernel.model.Groups_RolesTable;
import com.liferay.portal.kernel.model.Groups_UserGroupsTable;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourceActionTable;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.ResourcePermissionTable;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleTable;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.Team;
import com.liferay.portal.kernel.model.TeamTable;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.UserGroupGroupRoleTable;
import com.liferay.portal.kernel.model.UserGroupRoleTable;
import com.liferay.portal.kernel.model.UserGroups_TeamsTable;
import com.liferay.portal.kernel.model.Users_GroupsTable;
import com.liferay.portal.kernel.model.Users_OrgsTable;
import com.liferay.portal.kernel.model.Users_RolesTable;
import com.liferay.portal.kernel.model.Users_TeamsTable;
import com.liferay.portal.kernel.model.Users_UserGroupsTable;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.persistence.GroupPersistence;
import com.liferay.portal.kernel.service.persistence.ResourcePermissionPersistence;
import com.liferay.portal.kernel.service.persistence.TeamPersistence;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.security.permission.PermissionCacheUtil;
import com.liferay.portal.service.base.RoleLocalServiceBaseImpl;
import com.liferay.portal.util.PortalInstances;
import com.liferay.portal.util.PropsUtil;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.usersadmin.util.UsersAdminUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Provides the local service for accessing, adding, checking, deleting, and
 * updating roles.
 *
 * @author Brian Wing Shun Chan
 * @author Marcellus Tavares
 */
public class RoleLocalServiceImpl extends RoleLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public Role addRole(
			String externalReferenceCode, long userId, String className,
			long classPK, String name, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, int type, String subtype,
			ServiceContext serviceContext)
		throws PortalException {

		// Role

		User user = _userPersistence.findByPrimaryKey(userId);

		className = GetterUtil.getString(className);

		long classNameId = _classNameLocalService.getClassNameId(className);

		long roleId = counterLocalService.increment();

		if ((classNameId <= 0) || className.equals(Role.class.getName())) {
			classNameId = _classNameLocalService.getClassNameId(Role.class);
			classPK = roleId;
		}

		validate(0, user.getCompanyId(), classNameId, name);

		Role role = rolePersistence.create(roleId);

		if (serviceContext != null) {
			role.setUuid(serviceContext.getUuid());
		}

		role.setExternalReferenceCode(externalReferenceCode);
		role.setCompanyId(user.getCompanyId());
		role.setUserId(user.getUserId());
		role.setUserName(user.getFullName());
		role.setClassNameId(classNameId);
		role.setClassPK(classPK);
		role.setName(name);
		role.setTitleMap(titleMap);
		role.setDescriptionMap(descriptionMap);
		role.setType(type);
		role.setSubtype(subtype);

		if (IncompleteModelManagerUtil.isIncompleteModel()) {
			role.setStatus(WorkflowConstants.STATUS_INCOMPLETE);
		}
		else {
			role.setStatus(WorkflowConstants.STATUS_APPROVED);
		}

		role.setExpandoBridgeAttributes(serviceContext);

		role = rolePersistence.update(role);

		// Resources

		long ownerId = userId;

		if (user.isGuestUser()) {
			ownerId = 0;
		}

		_resourceLocalService.addResources(
			user.getCompanyId(), 0, ownerId, Role.class.getName(),
			role.getRoleId(), false, false, false);

		if (!user.isGuestUser()) {
			_resourceLocalService.addResources(
				user.getCompanyId(), 0, userId, Role.class.getName(),
				role.getRoleId(), false, false, false);

			if (!ExportImportThreadLocal.isImportInProcess()) {
				reindex(userId);
			}
		}

		return role;
	}

	/**
	 * Adds the role to the user. The user is reindexed after the role is added.
	 *
	 * @param userId the primary key of the user
	 * @param roleId the primary key of the role
	 * @return <code>true</code> if the association between the ${userId} and ${roleId} is added; <code>false</code> if it was already added
	 * @see   UserPersistence#addRole(
	 *        long, long)
	 */
	@Override
	public boolean addUserRole(long userId, long roleId)
		throws PortalException {

		if (!super.addUserRole(userId, roleId)) {
			return false;
		}

		reindex(userId);

		return true;
	}

	/**
	 * Adds the role to the user. The user is reindexed after the role is added.
	 *
	 * @param userId the primary key of the user
	 * @param role the role
	 * @return <code>true</code> if the association between the ${userId} and ${role} is added; <code>false</code> if it was already added
	 * @see   UserPersistence#addRole(
	 *        long, Role)
	 */
	@Override
	public boolean addUserRole(long userId, Role role) throws PortalException {
		return addUserRole(userId, role.getRoleId());
	}

	/**
	 * Adds the roles to the user. The user is reindexed after the roles are
	 * added.
	 *
	 * @param userId the primary key of the user
	 * @param roles the roles
	 * @return <code>true</code> if at least an association between the ${userId} and the ${roles} is added; <code>false</code> if all were already added
	 * @see   UserPersistence#addRoles(
	 *        long, List)
	 */
	@Override
	public boolean addUserRoles(long userId, List<Role> roles)
		throws PortalException {

		if (!super.addUserRoles(userId, roles)) {
			return false;
		}

		reindex(userId);

		return true;
	}

	/**
	 * Adds the roles to the user. The user is reindexed after the roles are
	 * added.
	 *
	 * @param userId the primary key of the user
	 * @param roleIds the primary keys of the roles
	 * @return <code>true</code> if at least an association between the ${userId} and the ${roleIds} is added; <code>false</code> if all were already added
	 * @see   UserPersistence#addRoles(
	 *        long, long[])
	 */
	@Override
	public boolean addUserRoles(long userId, long[] roleIds)
		throws PortalException {

		if (!super.addUserRoles(userId, roleIds)) {
			return false;
		}

		reindex(userId);

		return true;
	}

	/**
	 * Checks to ensure that the system roles map has appropriate default roles
	 * in each company.
	 */
	@Override
	public void checkSystemRoles() throws PortalException {
		_companyLocalService.forEachCompanyId(
			companyId -> checkSystemRoles(companyId));
	}

	/**
	 * Checks to ensure that the system roles map has appropriate default roles
	 * in the company.
	 *
	 * @param companyId the primary key of the company
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public void checkSystemRoles(long companyId) throws PortalException {
		Map<String, Role> companyRolesMap = new HashMap<>();

		for (Role role : rolePersistence.findByCompanyId(companyId)) {
			companyRolesMap.put(role.getName(), role);
		}

		String defaultLanguageId = LocaleUtil.toLanguageId(
			LocaleUtil.getDefault());

		// Regular roles

		String[] systemRoles = PortalUtil.getSystemRoles();

		for (String name : systemRoles) {
			String description = LocalizationUtil.getXml(
				Collections.singletonMap(
					defaultLanguageId,
					_getPropertyDescription(name, "system.role.")),
				defaultLanguageId, "Description");

			int type = RoleConstants.TYPE_REGULAR;

			checkSystemRole(
				companyRolesMap, companyId, name, description, type);
		}

		// Organization roles

		String[] systemOrganizationRoles =
			PortalUtil.getSystemOrganizationRoles();

		for (String name : systemOrganizationRoles) {
			String description = LocalizationUtil.getXml(
				Collections.singletonMap(
					defaultLanguageId,
					_getPropertyDescription(name, "system.organization.role.")),
				defaultLanguageId, "Description");

			int type = RoleConstants.TYPE_ORGANIZATION;

			checkSystemRole(
				companyRolesMap, companyId, name, description, type);
		}

		// Site roles

		String[] systemSiteRoles = PortalUtil.getSystemSiteRoles();

		for (String name : systemSiteRoles) {
			String description = LocalizationUtil.getXml(
				Collections.singletonMap(
					defaultLanguageId,
					_getPropertyDescription(name, "system.site.role.")),
				defaultLanguageId, "Description");

			int type = RoleConstants.TYPE_SITE;

			checkSystemRole(
				companyRolesMap, companyId, name, description, type);
		}

		List<String> primKeys = new ArrayList<>();

		String[] allSystemRoles = ArrayUtil.append(
			systemRoles, systemOrganizationRoles, systemSiteRoles);

		for (String roleName : allSystemRoles) {
			Role role = getRole(companyId, roleName);

			primKeys.add(String.valueOf(role.getRoleId()));
		}

		if (!primKeys.isEmpty()) {
			_resourceLocalService.addResources(
				companyId, 0, 0, Role.class.getName(),
				primKeys.toArray(new String[0]), false, false, false);
		}

		// All users should be able to view all system roles by default

		Role userRole = getRole(companyId, RoleConstants.USER);

		for (String roleName : allSystemRoles) {
			if (companyRolesMap.containsKey(roleName)) {
				continue;
			}

			Role role = getRole(companyId, roleName);

			_resourcePermissionLocalService.setResourcePermissions(
				companyId, Role.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(role.getRoleId()), userRole.getRoleId(),
				new String[] {ActionKeys.VIEW});
		}
	}

	/**
	 * Removes every role from the user. The user is reindexed after the roles
	 * are removed.
	 *
	 * @param userId the primary key of the user
	 * @see   UserPersistence#clearRoles(
	 *        long)
	 */
	@Override
	public void clearUserRoles(long userId) throws PortalException {
		_userPersistence.clearRoles(userId);

		reindex(userId);
	}

	/**
	 * Deletes the role with the primary key and its associated permissions.
	 *
	 * @param  roleId the primary key of the role
	 * @return the deleted role
	 */
	@Override
	public Role deleteRole(long roleId) throws PortalException {
		Role role = rolePersistence.findByPrimaryKey(roleId);

		return roleLocalService.deleteRole(role);
	}

	/**
	 * Deletes the role and its associated permissions.
	 *
	 * @param  role the role
	 * @return the deleted role
	 */
	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public Role deleteRole(Role role) throws PortalException {
		if (role.isSystem() &&
			!PortalInstances.isCurrentCompanyInDeletionProcess()) {

			throw new RequiredRoleException();
		}

		// Resources

		List<ResourcePermission> resourcePermissions =
			_resourcePermissionPersistence.findByRoleId(role.getRoleId());

		for (ResourcePermission resourcePermission : resourcePermissions) {
			_resourcePermissionLocalService.deleteResourcePermission(
				resourcePermission);
		}

		_resourceLocalService.deleteResource(
			role.getCompanyId(), Role.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL, role.getRoleId());

		if ((role.getType() == RoleConstants.TYPE_DEPOT) ||
			(role.getType() == RoleConstants.TYPE_ORGANIZATION) ||
			(role.getType() == RoleConstants.TYPE_SITE)) {

			List<Group> groups = _groupPersistence.findByC_S(
				role.getCompanyId(), true);

			for (Group group : groups) {
				UnicodeProperties typeSettingsUnicodeProperties =
					group.getTypeSettingsProperties();

				List<Long> defaultSiteRoleIds = ListUtil.fromArray(
					StringUtil.split(
						typeSettingsUnicodeProperties.getProperty(
							"defaultSiteRoleIds"),
						0L));

				if (defaultSiteRoleIds.contains(role.getRoleId())) {
					defaultSiteRoleIds.remove(role.getRoleId());

					typeSettingsUnicodeProperties.setProperty(
						"defaultSiteRoleIds",
						ListUtil.toString(
							defaultSiteRoleIds, StringPool.BLANK));

					_groupLocalService.updateGroup(
						group.getGroupId(),
						typeSettingsUnicodeProperties.toString());
				}
			}

			_userGroupRoleLocalService.deleteUserGroupRolesByRoleId(
				role.getRoleId());

			_userGroupGroupRoleLocalService.deleteUserGroupGroupRolesByRoleId(
				role.getRoleId());
		}

		// Role

		rolePersistence.remove(role);

		// Expando

		_expandoRowLocalService.deleteRows(role.getRoleId());

		return role;
	}

	/**
	 * Removes the role from the user. The user is reindexed after the role is
	 * removed.
	 *
	 * @param userId the primary key of the user
	 * @param roleId the primary key of the role
	 * @see   UserPersistence#removeRole(
	 *        long, long)
	 */
	@Override
	public void deleteUserRole(long userId, long roleId)
		throws PortalException {

		_userPersistence.removeRole(userId, roleId);

		reindex(userId);
	}

	/**
	 * Removes the role from the user. The user is reindexed after the role is
	 * removed.
	 *
	 * @param userId the primary key of the user
	 * @param role the role
	 * @see   UserPersistence#removeRole(
	 *        long, Role)
	 */
	@Override
	public void deleteUserRole(long userId, Role role) throws PortalException {
		_userPersistence.removeRole(userId, role);

		reindex(userId);
	}

	/**
	 * Removes the roles from the user. The user is reindexed after the roles
	 * are removed.
	 *
	 * @param userId the primary key of the user
	 * @param roles the roles
	 * @see   UserPersistence#removeRoles(
	 *        long, List)
	 */
	@Override
	public void deleteUserRoles(long userId, List<Role> roles)
		throws PortalException {

		_userPersistence.removeRoles(userId, roles);

		reindex(userId);
	}

	/**
	 * Removes the roles from the user. The user is reindexed after the roles
	 * are removed.
	 *
	 * @param userId the primary key of the user
	 * @param roleIds the primary keys of the roles
	 * @see   UserPersistence#removeRoles(
	 *        long, long[])
	 */
	@Override
	public void deleteUserRoles(long userId, long[] roleIds)
		throws PortalException {

		_userPersistence.removeRoles(userId, roleIds);

		reindex(userId);
	}

	/**
	 * Returns the role with the name in the company.
	 *
	 * <p>
	 * The method searches the system roles map first for default roles. If a
	 * role with the name is not found, then the method will query the database.
	 * </p>
	 *
	 * @param  companyId the primary key of the company
	 * @param  name the role's name
	 * @return Returns the role with the name or <code>null</code> if a role
	 *         with the name could not be found in the company
	 */
	@Override
	public Role fetchRole(long companyId, String name) {
		return rolePersistence.fetchByC_N(companyId, name);
	}

	@Override
	public int getAssigneesTotal(long roleId) throws PortalException {
		int assigneesTotal = 0;

		Role role = getRole(roleId);

		int type = role.getType();

		if (type == RoleConstants.TYPE_REGULAR) {
			assigneesTotal += _groupLocalService.getRoleGroupsCount(roleId);
			assigneesTotal += _userLocalService.getRoleUsersCount(
				roleId, WorkflowConstants.STATUS_APPROVED);
		}

		if ((type == RoleConstants.TYPE_DEPOT) ||
			(type == RoleConstants.TYPE_SITE)) {

			DynamicQuery userGroupGroupRoleDynamicQuery =
				_userGroupGroupRoleLocalService.dynamicQuery();

			Property property = PropertyFactoryUtil.forName("roleId");

			userGroupGroupRoleDynamicQuery.add(property.eq(roleId));

			userGroupGroupRoleDynamicQuery.setProjection(
				ProjectionFactoryUtil.countDistinct("userGroupId"));

			List<?> list = _userGroupRoleLocalService.dynamicQuery(
				userGroupGroupRoleDynamicQuery);

			Long count = (Long)list.get(0);

			assigneesTotal += count.intValue();
		}

		if ((type == RoleConstants.TYPE_DEPOT) ||
			(type == RoleConstants.TYPE_ORGANIZATION) ||
			(type == RoleConstants.TYPE_SITE)) {

			DynamicQuery userDynamicQuery = _userLocalService.dynamicQuery();

			Property statusProperty = PropertyFactoryUtil.forName("status");

			userDynamicQuery.add(
				statusProperty.eq(WorkflowConstants.STATUS_APPROVED));

			userDynamicQuery.setProjection(
				ProjectionFactoryUtil.property("userId"));

			DynamicQuery userGroupRoleDynamicQuery =
				_userGroupRoleLocalService.dynamicQuery();

			Property userIdProperty = PropertyFactoryUtil.forName("userId");

			userGroupRoleDynamicQuery.add(userIdProperty.in(userDynamicQuery));

			Property roleIdProperty = PropertyFactoryUtil.forName("roleId");

			userGroupRoleDynamicQuery.add(roleIdProperty.eq(roleId));

			userGroupRoleDynamicQuery.setProjection(
				ProjectionFactoryUtil.countDistinct("userId"));

			List<?> list = _userGroupRoleLocalService.dynamicQuery(
				userGroupRoleDynamicQuery);

			Long count = (Long)list.get(0);

			assigneesTotal += count.intValue();
		}

		return assigneesTotal;
	}

	/**
	 * Returns the default role for the group with the primary key.
	 *
	 * <p>
	 * If the group is a site, then the default role is {@link
	 * RoleConstants#SITE_MEMBER}. If the group is an organization, then the
	 * default role is {@link RoleConstants#ORGANIZATION_USER}. If the group is
	 * a user or user group, then the default role is {@link
	 * RoleConstants#POWER_USER}. For all other group types, the default role is
	 * {@link RoleConstants#USER}.
	 * </p>
	 *
	 * @param  groupId the primary key of the group
	 * @return the default role for the group with the primary key
	 */
	@Override
	public Role getDefaultGroupRole(long groupId) throws PortalException {
		Group group = _groupPersistence.findByPrimaryKey(groupId);

		if (group.isLayout()) {
			Layout layout = _layoutLocalService.getLayout(group.getClassPK());

			group = layout.getGroup();
		}

		if (group.isStagingGroup()) {
			group = group.getLiveGroup();
		}

		Role role = null;

		if (group.isCompany()) {
			role = getRole(group.getCompanyId(), RoleConstants.USER);
		}
		else if (group.isLayoutPrototype() || group.isLayoutSetPrototype() ||
				 group.isRegularSite() || group.isSite()) {

			role = getRole(group.getCompanyId(), RoleConstants.SITE_MEMBER);
		}
		else if (group.isOrganization()) {
			role = getRole(
				group.getCompanyId(), RoleConstants.ORGANIZATION_USER);
		}
		else {
			role = getRole(group.getCompanyId(), RoleConstants.USER);
		}

		return role;
	}

	@Override
	public List<Role> getGroupRelatedRoles(long groupId)
		throws PortalException {

		List<Role> roles = new ArrayList<>();

		Group group = _groupLocalService.getGroup(groupId);

		if (group.isStagingGroup()) {
			group = group.getLiveGroup();
		}

		int[] types = RoleConstants.TYPES_REGULAR;

		if (group.isOrganization()) {
			if (group.isSite()) {
				types = RoleConstants.TYPES_ORGANIZATION_AND_REGULAR_AND_SITE;
			}
			else {
				types = RoleConstants.TYPES_ORGANIZATION_AND_REGULAR;
			}
		}
		else if (group.isLayout() || group.isLayoutSetPrototype() ||
				 group.isSite() || group.isUser()) {

			types = RoleConstants.TYPES_REGULAR_AND_SITE;
		}

		roles.addAll(getRoles(group.getCompanyId(), types));

		roles.addAll(getTeamRoles(groupId));

		return roles;
	}

	@Override
	public List<Role> getGroupRolesAndTeamRoles(
		long companyId, String name, List<String> excludedNames, String title,
		String description, int[] types, long excludedTeamRoleId,
		long teamGroupId, int start, int end) {

		return roleFinder.findByGroupRoleAndTeamRole(
			companyId, name, excludedNames, title, description, types,
			excludedTeamRoleId, teamGroupId, start, end);
	}

	@Override
	public int getGroupRolesAndTeamRolesCount(
		long companyId, String name, List<String> excludedNames, String title,
		String description, int[] types, long excludedTeamRoleId,
		long teamGroupId) {

		return roleFinder.countByGroupRoleAndTeamRole(
			companyId, name, excludedNames, title, description, types,
			excludedTeamRoleId, teamGroupId);
	}

	@Override
	public Role getOrAddIncompleteRole(
			String externalReferenceCode, long companyId, long userId,
			String className, long classPK, String name, int type)
		throws Exception {

		return IncompleteModelManagerUtil.getOrAddIncompleteModel(
			Role.class, companyId, externalReferenceCode,
			this::fetchRoleByExternalReferenceCode,
			this::getRoleByExternalReferenceCode,
			() -> roleLocalService.addRole(
				externalReferenceCode, userId, className, classPK,
				(fetchRole(companyId, name) != null) ? externalReferenceCode :
					name,
				null, null, type, StringPool.BLANK, new ServiceContext()));
	}

	/**
	 * Returns a map of role names to associated action IDs for the named
	 * resource in the company within the permission scope.
	 *
	 * @param  companyId the primary key of the company
	 * @param  name the resource name
	 * @param  scope the permission scope
	 * @param  primKey the primary key of the resource's class
	 * @return the role names and action IDs
	 */
	@Override
	public Map<String, List<String>> getResourceRoles(
		long companyId, String name, int scope, String primKey) {

		Map<String, List<String>> rolesMap = new HashMap<>();

		for (Object[] array :
				rolePersistence.<List<Object[]>>dslQuery(
					DSLQueryFactoryUtil.select(
						RoleTable.INSTANCE.name.as("roleName"),
						ResourceActionTable.INSTANCE.actionId.as("actionId")
					).from(
						ResourcePermissionTable.INSTANCE
					).innerJoinON(
						RoleTable.INSTANCE,
						RoleTable.INSTANCE.roleId.eq(
							ResourcePermissionTable.INSTANCE.roleId)
					).innerJoinON(
						ResourceActionTable.INSTANCE,
						ResourceActionTable.INSTANCE.name.eq(
							ResourcePermissionTable.INSTANCE.name
						).and(
							ResourceActionTable.INSTANCE.bitwiseValue.eq(
								DSLFunctionFactoryUtil.bitAnd(
									ResourcePermissionTable.INSTANCE.actionIds,
									ResourceActionTable.INSTANCE.bitwiseValue))
						)
					).where(
						ResourcePermissionTable.INSTANCE.companyId.eq(
							companyId
						).and(
							ResourcePermissionTable.INSTANCE.name.eq(name)
						).and(
							ResourcePermissionTable.INSTANCE.scope.eq(scope)
						).and(
							ResourcePermissionTable.INSTANCE.primKey.eq(primKey)
						)
					))) {

			String roleName = (String)array[0];
			String actionId = (String)array[1];

			List<String> roles = rolesMap.computeIfAbsent(
				roleName, key -> new ArrayList<>());

			roles.add(actionId);
		}

		return rolesMap;
	}

	/**
	 * Returns all the roles associated with the action ID in the company within
	 * the permission scope.
	 *
	 * @param  companyId the primary key of the company
	 * @param  name the resource name
	 * @param  scope the permission scope
	 * @param  primKey the primary key of the resource's class
	 * @param  actionId the name of the resource action
	 * @return the roles
	 */
	@Override
	public List<Role> getResourceRoles(
		long companyId, String name, int scope, String primKey,
		String actionId) {

		ResourceAction resourceAction =
			_resourceActionLocalService.fetchResourceAction(name, actionId);

		if (resourceAction == null) {
			return Collections.emptyList();
		}

		return rolePersistence.dslQuery(
			DSLQueryFactoryUtil.select(
				RoleTable.INSTANCE
			).from(
				RoleTable.INSTANCE
			).innerJoinON(
				ResourcePermissionTable.INSTANCE,
				ResourcePermissionTable.INSTANCE.roleId.eq(
					RoleTable.INSTANCE.roleId)
			).where(
				ResourcePermissionTable.INSTANCE.companyId.eq(
					companyId
				).and(
					ResourcePermissionTable.INSTANCE.name.eq(name)
				).and(
					ResourcePermissionTable.INSTANCE.scope.eq(scope)
				).and(
					ResourcePermissionTable.INSTANCE.primKey.eq(primKey)
				).and(
					DSLFunctionFactoryUtil.bitAnd(
						ResourcePermissionTable.INSTANCE.actionIds,
						resourceAction.getBitwiseValue()
					).eq(
						resourceAction.getBitwiseValue()
					)
				)
			).orderBy(
				RoleTable.INSTANCE.name.ascending()
			));
	}

	/**
	 * Returns the role with the name in the company.
	 *
	 * <p>
	 * The method searches the system roles map first for default roles. If a
	 * role with the name is not found, then the method will query the database.
	 * </p>
	 *
	 * @param  companyId the primary key of the company
	 * @param  name the role's name
	 * @return the role with the name
	 */
	@Override
	public Role getRole(long companyId, String name) throws PortalException {
		return rolePersistence.findByC_N(companyId, name);
	}

	/**
	 * Returns all the roles of the type and subtype.
	 *
	 * @param  type the role's type (optionally <code>0</code>)
	 * @param  subtype the role's subtype (optionally <code>null</code>)
	 * @return the roles of the type and subtype
	 */
	@Override
	public List<Role> getRoles(int type, String subtype) {
		return rolePersistence.findByT_S(type, subtype);
	}

	/**
	 * Returns all the roles in the company.
	 *
	 * @param  companyId the primary key of the company
	 * @return the roles in the company
	 */
	@Override
	public List<Role> getRoles(long companyId) {
		return rolePersistence.findByCompanyId(companyId);
	}

	/**
	 * Returns all the roles with the types.
	 *
	 * @param  companyId the primary key of the company
	 * @param  types the role types (optionally <code>null</code>)
	 * @return the roles with the types
	 */
	@Override
	public List<Role> getRoles(long companyId, int[] types) {
		return rolePersistence.findByC_T(companyId, types);
	}

	@Override
	public List<Role> getRoles(
		long companyId, long classNameId, long[] classPKs, int type) {

		return rolePersistence.findByC_C_C_T(
			companyId, classNameId, classPKs, type);
	}

	/**
	 * Returns all the roles with the primary keys.
	 *
	 * @param  roleIds the primary keys of the roles
	 * @return the roles with the primary keys
	 */
	@Override
	public List<Role> getRoles(long[] roleIds) throws PortalException {
		List<Role> roles = new ArrayList<>(roleIds.length);

		for (long roleId : roleIds) {
			roles.add(getRole(roleId));
		}

		return roles;
	}

	/**
	 * Returns all the roles of the subtype.
	 *
	 * @param  subtype the role's subtype (optionally <code>null</code>)
	 * @return the roles of the subtype
	 */
	@Override
	public List<Role> getSubtypeRoles(String subtype) {
		return rolePersistence.findBySubtype(subtype);
	}

	/**
	 * Returns the number of roles of the subtype.
	 *
	 * @param  subtype the role's subtype (optionally <code>null</code>)
	 * @return the number of roles of the subtype
	 */
	@Override
	public int getSubtypeRolesCount(String subtype) {
		return rolePersistence.countBySubtype(subtype);
	}

	/**
	 * Returns the team role in the company.
	 *
	 * @param  companyId the primary key of the company
	 * @param  teamId the primary key of the team
	 * @return the team role in the company
	 */
	@Override
	public Role getTeamRole(long companyId, long teamId)
		throws PortalException {

		return rolePersistence.findByC_C_C(
			companyId, _classNameLocalService.getClassNameId(Team.class),
			teamId);
	}

	/**
	 * Returns the team role map for the group.
	 *
	 * @param  groupId the primary key of the group
	 * @return the team role map for the group
	 */
	@Override
	public Map<Team, Role> getTeamRoleMap(long groupId) throws PortalException {
		return getTeamRoleMap(groupId, null);
	}

	/**
	 * Returns the team roles in the group.
	 *
	 * @param  groupId the primary key of the group
	 * @return the team roles in the group
	 */
	@Override
	public List<Role> getTeamRoles(long groupId) throws PortalException {
		return getTeamRoles(groupId, null);
	}

	/**
	 * Returns the team roles in the group, excluding the specified role IDs.
	 *
	 * @param  groupId the primary key of the group
	 * @param  excludedRoleIds the primary keys of the roles to exclude
	 *         (optionally <code>null</code>)
	 * @return the team roles in the group, excluding the specified role IDs
	 */
	@Override
	public List<Role> getTeamRoles(long groupId, long[] excludedRoleIds)
		throws PortalException {

		Map<Team, Role> teamRoleMap = getTeamRoleMap(groupId, excludedRoleIds);

		Collection<Role> roles = teamRoleMap.values();

		return ListUtil.fromCollection(roles);
	}

	/**
	 * Returns the team roles in the company.
	 *
	 * @param  companyId the primary key of the company
	 * @param  teamIds the primary keys of the teams
	 * @return the team roles in the company
	 */
	@Override
	public List<Role> getTeamsRoles(long companyId, long[] teamIds)
		throws PortalException {

		return rolePersistence.findByC_C_C(
			companyId, _classNameLocalService.getClassNameId(Team.class),
			teamIds);
	}

	/**
	 * Returns all the roles of the type.
	 *
	 * @param  type the role's type (optionally <code>0</code>)
	 * @return the range of the roles of the type
	 */
	@Override
	public List<Role> getTypeRoles(int type) {
		return rolePersistence.findByType(type);
	}

	/**
	 * Returns a range of all the roles of the type.
	 *
	 * @param  type the role's type (optionally <code>0</code>)
	 * @param  start the lower bound of the range of roles to return
	 * @param  end the upper bound of the range of roles to return (not
	 *         inclusive)
	 * @return the range of the roles of the type
	 */
	@Override
	public List<Role> getTypeRoles(int type, int start, int end) {
		return rolePersistence.findByType(type, start, end);
	}

	/**
	 * Returns the number of roles of the type.
	 *
	 * @param  type the role's type (optionally <code>0</code>)
	 * @return the number of roles of the type
	 */
	@Override
	public int getTypeRolesCount(int type) {
		return rolePersistence.countByType(type);
	}

	/**
	 * Returns all the user's roles within the user group.
	 *
	 * @param  userId the primary key of the user
	 * @param  groupId the primary key of the group
	 * @return the user's roles within the user group
	 */
	@Override
	public List<Role> getUserGroupGroupRoles(long userId, long groupId) {
		return getUserGroupGroupRoles(
			userId, groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	@Override
	public List<Role> getUserGroupGroupRoles(
		long userId, long groupId, int start, int end) {

		return rolePersistence.dslQuery(
			DSLQueryFactoryUtil.select(
				RoleTable.INSTANCE
			).from(
				RoleTable.INSTANCE
			).innerJoinON(
				UserGroupGroupRoleTable.INSTANCE,
				UserGroupGroupRoleTable.INSTANCE.roleId.eq(
					RoleTable.INSTANCE.roleId)
			).innerJoinON(
				Users_UserGroupsTable.INSTANCE,
				Users_UserGroupsTable.INSTANCE.userGroupId.eq(
					UserGroupGroupRoleTable.INSTANCE.userGroupId)
			).where(
				Users_UserGroupsTable.INSTANCE.userId.eq(
					userId
				).and(
					UserGroupGroupRoleTable.INSTANCE.groupId.eq(groupId)
				)
			).limit(
				start, end
			));
	}

	@Override
	public int getUserGroupGroupRolesCount(long userId, long groupId) {
		return rolePersistence.dslQueryCount(
			DSLQueryFactoryUtil.count(
			).from(
				RoleTable.INSTANCE
			).innerJoinON(
				UserGroupGroupRoleTable.INSTANCE,
				UserGroupGroupRoleTable.INSTANCE.roleId.eq(
					RoleTable.INSTANCE.roleId)
			).innerJoinON(
				Users_UserGroupsTable.INSTANCE,
				Users_UserGroupsTable.INSTANCE.userGroupId.eq(
					UserGroupGroupRoleTable.INSTANCE.userGroupId)
			).where(
				Users_UserGroupsTable.INSTANCE.userId.eq(
					userId
				).and(
					UserGroupGroupRoleTable.INSTANCE.groupId.eq(groupId)
				)
			));
	}

	/**
	 * Returns all the user's roles within the user group.
	 *
	 * @param  userId the primary key of the user
	 * @param  groupId the primary key of the group
	 * @return the user's roles within the user group
	 */
	@Override
	public List<Role> getUserGroupRoles(long userId, long groupId) {
		return rolePersistence.dslQuery(
			DSLQueryFactoryUtil.select(
				RoleTable.INSTANCE
			).from(
				RoleTable.INSTANCE
			).innerJoinON(
				UserGroupRoleTable.INSTANCE,
				UserGroupRoleTable.INSTANCE.roleId.eq(RoleTable.INSTANCE.roleId)
			).where(
				UserGroupRoleTable.INSTANCE.userId.eq(
					userId
				).and(
					UserGroupRoleTable.INSTANCE.groupId.eq(groupId)
				)
			));
	}

	/**
	 * Returns the union of all the user's roles within the groups. If no
	 * groups are provided, only the user's directly assigned roles are
	 * returned.
	 *
	 * @param  userId the primary key of the user
	 * @param  groups the groups (optionally <code>null</code>)
	 * @return the union of all the user's roles within the groups
	 */
	@Override
	public List<Role> getUserRelatedRoles(long userId, List<Group> groups) {
		return getUserRelatedRoles(
			userId, ListUtil.toLongArray(groups, Group.GROUP_ID_ACCESSOR));
	}

	/**
	 * Returns all the user's roles within the group.
	 *
	 * @param  userId the primary key of the user
	 * @param  groupId the primary key of the group
	 * @return the user's roles within the group
	 */
	@Override
	public List<Role> getUserRelatedRoles(long userId, long groupId) {
		return getUserRelatedRoles(userId, new long[] {groupId});
	}

	/**
	 * Returns the union of all the user's roles within the groups. If no
	 * groupIds are provided, only the user's directly assigned roles are
	 * returned.
	 *
	 * @param  userId the primary key of the user
	 * @param  groupIds the primary keys of the groups
	 * @return the union of all the user's roles within the groups
	 */
	@Override
	public List<Role> getUserRelatedRoles(long userId, long[] groupIds) {
		if (ArrayUtil.isEmpty(groupIds)) {
			return userPersistence.getRoles(userId);
		}

		Set<Role> roles = new HashSet<>(userPersistence.getRoles(userId));

		if (groupIds.length <= _CACHEABLE_QUERY_LIMIT_LPD_38877) {
			for (long groupId : groupIds) {
				roles.addAll(groupPersistence.getRoles(groupId));
			}

			return new ArrayList<>(roles);
		}

		JoinStep joinStep = DSLQueryFactoryUtil.select(
			RoleTable.INSTANCE
		).from(
			RoleTable.INSTANCE
		).innerJoinON(
			Groups_RolesTable.INSTANCE,
			Groups_RolesTable.INSTANCE.roleId.eq(RoleTable.INSTANCE.roleId)
		);

		List<Role> groupRoles = new ArrayList<>();

		int chunk = 2000;

		for (int i = 0; i < groupIds.length; i += chunk) {

			// We cannot use an "in" clause because more than 1000 items in
			// a list causes a syntax error in Oracle. See LPS-173475 and
			// ORA-01795.

			/*groupRoles.addAll(
				dslQuery(
					joinStep.where(
						Groups_RolesTable.INSTANCE.groupId.in(
							ArrayUtil.toLongArray(
								Arrays.copyOfRange(
									groupIds, i, i + chunk))))));*/

			Predicate predicate = null;

			long[] curGroupIds = Arrays.copyOfRange(
				groupIds, i, Math.min(groupIds.length, i + chunk));

			for (long curGroupId : curGroupIds) {
				predicate = Predicate.or(
					predicate,
					Groups_RolesTable.INSTANCE.groupId.eq(curGroupId));
			}

			if (predicate != null) {
				groupRoles.addAll(
					dslQuery(joinStep.where(predicate.withParentheses())));
			}
		}

		if (!groupRoles.isEmpty()) {
			roles.addAll(groupRoles);
		}

		return new ArrayList<>(roles);
	}

	@Override
	public List<Role> getUserTeamRoles(long userId, long groupId) {
		Set<Role> roles = new LinkedHashSet<>();

		long classNameId = _classNameLocalService.getClassNameId(Team.class);

		List<Role> teamRoles = rolePersistence.dslQuery(
			DSLQueryFactoryUtil.select(
				RoleTable.INSTANCE
			).from(
				RoleTable.INSTANCE
			).innerJoinON(
				TeamTable.INSTANCE,
				TeamTable.INSTANCE.companyId.eq(
					RoleTable.INSTANCE.companyId
				).and(
					TeamTable.INSTANCE.teamId.eq(RoleTable.INSTANCE.classPK)
				)
			).innerJoinON(
				Users_TeamsTable.INSTANCE,
				Users_TeamsTable.INSTANCE.teamId.eq(TeamTable.INSTANCE.teamId)
			).where(
				RoleTable.INSTANCE.classNameId.eq(
					classNameId
				).and(
					TeamTable.INSTANCE.groupId.eq(groupId)
				).and(
					Users_TeamsTable.INSTANCE.userId.eq(userId)
				)
			));

		roles.addAll(teamRoles);

		List<Role> userGroupRoles = rolePersistence.dslQuery(
			DSLQueryFactoryUtil.select(
				RoleTable.INSTANCE
			).from(
				RoleTable.INSTANCE
			).innerJoinON(
				TeamTable.INSTANCE,
				TeamTable.INSTANCE.companyId.eq(
					RoleTable.INSTANCE.companyId
				).and(
					TeamTable.INSTANCE.teamId.eq(RoleTable.INSTANCE.classPK)
				)
			).innerJoinON(
				UserGroups_TeamsTable.INSTANCE,
				UserGroups_TeamsTable.INSTANCE.teamId.eq(
					TeamTable.INSTANCE.teamId)
			).innerJoinON(
				Users_UserGroupsTable.INSTANCE,
				Users_UserGroupsTable.INSTANCE.userGroupId.eq(
					UserGroups_TeamsTable.INSTANCE.userGroupId)
			).where(
				RoleTable.INSTANCE.classNameId.eq(
					classNameId
				).and(
					TeamTable.INSTANCE.groupId.eq(groupId)
				).and(
					Users_UserGroupsTable.INSTANCE.userId.eq(userId)
				)
			));

		roles.addAll(userGroupRoles);

		return new ArrayList<>(roles);
	}

	/**
	 * Returns <code>true</code> if the user is associated with the named
	 * regular role.
	 *
	 * @param  userId the primary key of the user
	 * @param  companyId the primary key of the company
	 * @param  name the name of the role
	 * @param  inherited whether to include the user's inherited roles in the
	 *         search
	 * @return <code>true</code> if the user is associated with the regular
	 *         role; <code>false</code> otherwise
	 */
	@Override
	@ThreadLocalCachable
	public boolean hasUserRole(
			long userId, long companyId, String name, boolean inherited)
		throws PortalException {

		Role role = rolePersistence.fetchByC_N(companyId, name);

		if (role == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Role ", name, " with company ID ", companyId,
						" does not exist"));
			}

			return false;
		}

		if (role.getType() != RoleConstants.TYPE_REGULAR) {
			throw new IllegalArgumentException(name + " is not a regular role");
		}

		long guestUserId = _userLocalService.getGuestUserId(companyId);

		if (userId == guestUserId) {
			return name.equals(RoleConstants.GUEST);
		}

		if (inherited) {
			if (_userPersistence.containsRole(userId, role.getRoleId())) {
				return true;
			}

			ThreadLocalCache<Boolean> threadLocalCache =
				ThreadLocalCacheManager.getThreadLocalCache(
					Lifecycle.REQUEST, RoleLocalServiceImpl.class.getName());

			String roleId = String.valueOf(role.getRoleId());

			String key = roleId.concat(String.valueOf(userId));

			Boolean value = threadLocalCache.get(key);

			if (value != null) {
				return value;
			}

			value = PermissionCacheUtil.getUserRole(userId, role);

			if (value == null) {
				List<Long> roleIds = rolePersistence.dslQuery(
					DSLQueryFactoryUtil.select(
						Groups_RolesTable.INSTANCE.roleId
					).from(
						Groups_RolesTable.INSTANCE
					).innerJoinON(
						GroupTable.INSTANCE,
						GroupTable.INSTANCE.groupId.eq(
							Groups_RolesTable.INSTANCE.groupId)
					).innerJoinON(
						Users_OrgsTable.INSTANCE,
						Users_OrgsTable.INSTANCE.organizationId.eq(
							GroupTable.INSTANCE.classPK)
					).where(
						Groups_RolesTable.INSTANCE.roleId.eq(
							role.getRoleId()
						).and(
							Users_OrgsTable.INSTANCE.userId.eq(userId)
						)
					).union(
						DSLQueryFactoryUtil.select(
							Groups_RolesTable.INSTANCE.roleId
						).from(
							Groups_RolesTable.INSTANCE
						).innerJoinON(
							Groups_OrgsTable.INSTANCE,
							Groups_OrgsTable.INSTANCE.groupId.eq(
								Groups_RolesTable.INSTANCE.groupId)
						).innerJoinON(
							Users_OrgsTable.INSTANCE,
							Users_OrgsTable.INSTANCE.organizationId.eq(
								Groups_OrgsTable.INSTANCE.organizationId)
						).where(
							Groups_RolesTable.INSTANCE.roleId.eq(
								role.getRoleId()
							).and(
								Users_OrgsTable.INSTANCE.userId.eq(userId)
							)
						)
					).union(
						DSLQueryFactoryUtil.select(
							Groups_RolesTable.INSTANCE.roleId
						).from(
							Groups_RolesTable.INSTANCE
						).innerJoinON(
							Users_GroupsTable.INSTANCE,
							Users_GroupsTable.INSTANCE.groupId.eq(
								Groups_RolesTable.INSTANCE.groupId)
						).where(
							Groups_RolesTable.INSTANCE.roleId.eq(
								role.getRoleId()
							).and(
								Users_GroupsTable.INSTANCE.userId.eq(userId)
							)
						)
					).union(
						DSLQueryFactoryUtil.select(
							Users_RolesTable.INSTANCE.roleId
						).from(
							Users_RolesTable.INSTANCE
						).where(
							Users_RolesTable.INSTANCE.roleId.eq(
								role.getRoleId()
							).and(
								Users_RolesTable.INSTANCE.userId.eq(userId)
							)
						)
					).union(
						DSLQueryFactoryUtil.select(
							Groups_RolesTable.INSTANCE.roleId
						).from(
							Groups_RolesTable.INSTANCE
						).innerJoinON(
							GroupTable.INSTANCE,
							GroupTable.INSTANCE.groupId.eq(
								Groups_RolesTable.INSTANCE.groupId)
						).innerJoinON(
							Users_UserGroupsTable.INSTANCE,
							Users_UserGroupsTable.INSTANCE.userGroupId.eq(
								GroupTable.INSTANCE.classPK)
						).where(
							Groups_RolesTable.INSTANCE.roleId.eq(
								role.getRoleId()
							).and(
								Users_UserGroupsTable.INSTANCE.userId.eq(userId)
							)
						)
					).union(
						DSLQueryFactoryUtil.select(
							Groups_RolesTable.INSTANCE.roleId
						).from(
							Groups_RolesTable.INSTANCE
						).innerJoinON(
							Groups_UserGroupsTable.INSTANCE,
							Groups_UserGroupsTable.INSTANCE.groupId.eq(
								Groups_RolesTable.INSTANCE.groupId)
						).innerJoinON(
							Users_UserGroupsTable.INSTANCE,
							Users_UserGroupsTable.INSTANCE.userGroupId.eq(
								Groups_UserGroupsTable.INSTANCE.userGroupId)
						).where(
							Groups_RolesTable.INSTANCE.roleId.eq(
								role.getRoleId()
							).and(
								Users_UserGroupsTable.INSTANCE.userId.eq(userId)
							)
						)
					));

				if (!roleIds.isEmpty()) {
					value = true;
				}
				else {
					value = false;
				}

				PermissionCacheUtil.putUserRole(userId, role, value);
			}

			threadLocalCache.put(key, value);

			return value;
		}

		return _userPersistence.containsRole(userId, role.getRoleId());
	}

	/**
	 * Returns <code>true</code> if the user has any one of the named regular
	 * roles.
	 *
	 * @param  userId the primary key of the user
	 * @param  companyId the primary key of the company
	 * @param  names the names of the roles
	 * @param  inherited whether to include the user's inherited roles in the
	 *         search
	 * @return <code>true</code> if the user has any one of the regular roles;
	 *         <code>false</code> otherwise
	 */
	@Override
	public boolean hasUserRoles(
			long userId, long companyId, String[] names, boolean inherited)
		throws PortalException {

		for (String name : names) {
			if (hasUserRole(userId, companyId, name, inherited)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns an ordered range of all the roles that match the keywords and
	 * types.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link
	 * QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  companyId the primary key of the company
	 * @param  keywords the keywords (space separated), which may occur in the
	 *         role's name or description (optionally <code>null</code>)
	 * @param  types the role types (optionally <code>null</code>)
	 * @param  start the lower bound of the range of roles to return
	 * @param  end the upper bound of the range of roles to return (not
	 *         inclusive)
	 * @param  orderByComparator the comparator to order the roles (optionally
	 *         <code>null</code>)
	 * @return the ordered range of the matching roles, ordered by
	 *         <code>orderByComparator</code>
	 * @see    com.liferay.portal.kernel.service.persistence.RoleFinder
	 */
	@Override
	public List<Role> search(
		long companyId, String keywords, Integer[] types, int start, int end,
		OrderByComparator<Role> orderByComparator) {

		return search(
			companyId, keywords, types, new LinkedHashMap<String, Object>(),
			start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the roles that match the keywords, types,
	 * and params.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link
	 * QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  companyId the primary key of the company
	 * @param  keywords the keywords (space separated), which may occur in the
	 *         role's name or description (optionally <code>null</code>)
	 * @param  types the role types (optionally <code>null</code>)
	 * @param  params the finder parameters. Can specify values for the
	 *         "usersRoles" key. For more information, see {@link
	 *         com.liferay.portal.kernel.service.persistence.RoleFinder}
	 * @param  start the lower bound of the range of roles to return
	 * @param  end the upper bound of the range of roles to return (not
	 *         inclusive)
	 * @param  orderByComparator the comparator to order the roles (optionally
	 *         <code>null</code>)
	 * @return the ordered range of the matching roles, ordered by
	 *         <code>orderByComparator</code>
	 * @see    com.liferay.portal.kernel.service.persistence.RoleFinder
	 */
	@Override
	public List<Role> search(
		long companyId, String keywords, Integer[] types,
		LinkedHashMap<String, Object> params, int start, int end,
		OrderByComparator<Role> orderByComparator) {

		return roleFinder.findByKeywords(
			companyId, keywords, types, params, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the roles that match the name,
	 * description, and types.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link
	 * QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  companyId the primary key of the company
	 * @param  name the role's name (optionally <code>null</code>)
	 * @param  description the role's description (optionally <code>null</code>)
	 * @param  types the role types (optionally <code>null</code>)
	 * @param  start the lower bound of the range of the roles to return
	 * @param  end the upper bound of the range of the roles to return (not
	 *         inclusive)
	 * @param  orderByComparator the comparator to order the roles (optionally
	 *         <code>null</code>)
	 * @return the ordered range of the matching roles, ordered by
	 *         <code>orderByComparator</code>
	 * @see    com.liferay.portal.kernel.service.persistence.RoleFinder
	 */
	@Override
	public List<Role> search(
		long companyId, String name, String description, Integer[] types,
		int start, int end, OrderByComparator<Role> orderByComparator) {

		return search(
			companyId, name, description, types,
			new LinkedHashMap<String, Object>(), start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the roles that match the name,
	 * description, types, and params.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link
	 * QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  companyId the primary key of the company
	 * @param  name the role's name (optionally <code>null</code>)
	 * @param  description the role's description (optionally <code>null</code>)
	 * @param  types the role types (optionally <code>null</code>)
	 * @param  params the finder's parameters. Can specify values for the
	 *         "usersRoles" key. For more information, see {@link
	 *         com.liferay.portal.kernel.service.persistence.RoleFinder}
	 * @param  start the lower bound of the range of the roles to return
	 * @param  end the upper bound of the range of the roles to return (not
	 *         inclusive)
	 * @param  orderByComparator the comparator to order the roles (optionally
	 *         <code>null</code>)
	 * @return the ordered range of the matching roles, ordered by
	 *         <code>orderByComparator</code>
	 * @see    com.liferay.portal.kernel.service.persistence.RoleFinder
	 */
	@Override
	public List<Role> search(
		long companyId, String name, String description, Integer[] types,
		LinkedHashMap<String, Object> params, int start, int end,
		OrderByComparator<Role> orderByComparator) {

		return roleFinder.findByC_N_D_T(
			companyId, name, description, types, params, true, start, end,
			orderByComparator);
	}

	/**
	 * Returns the number of roles that match the keywords and types.
	 *
	 * @param  companyId the primary key of the company
	 * @param  keywords the keywords (space separated), which may occur in the
	 *         role's name or description (optionally <code>null</code>)
	 * @param  types the role types (optionally <code>null</code>)
	 * @return the number of matching roles
	 */
	@Override
	public int searchCount(long companyId, String keywords, Integer[] types) {
		return searchCount(
			companyId, keywords, types, new LinkedHashMap<String, Object>());
	}

	/**
	 * Returns the number of roles that match the keywords, types and params.
	 *
	 * @param  companyId the primary key of the company
	 * @param  keywords the keywords (space separated), which may occur in the
	 *         role's name or description (optionally <code>null</code>)
	 * @param  types the role types (optionally <code>null</code>)
	 * @param  params the finder parameters. For more information, see {@link
	 *         com.liferay.portal.kernel.service.persistence.RoleFinder}
	 * @return the number of matching roles
	 */
	@Override
	public int searchCount(
		long companyId, String keywords, Integer[] types,
		LinkedHashMap<String, Object> params) {

		return roleFinder.countByKeywords(companyId, keywords, types, params);
	}

	/**
	 * Returns the number of roles that match the name, description, and types.
	 *
	 * @param  companyId the primary key of the company
	 * @param  name the role's name (optionally <code>null</code>)
	 * @param  description the role's description (optionally <code>null</code>)
	 * @param  types the role types (optionally <code>null</code>)
	 * @return the number of matching roles
	 */
	@Override
	public int searchCount(
		long companyId, String name, String description, Integer[] types) {

		return searchCount(
			companyId, name, description, types,
			new LinkedHashMap<String, Object>());
	}

	/**
	 * Returns the number of roles that match the name, description, types, and
	 * params.
	 *
	 * @param  companyId the primary key of the company
	 * @param  name the role's name (optionally <code>null</code>)
	 * @param  description the role's description (optionally <code>null</code>)
	 * @param  types the role types (optionally <code>null</code>)
	 * @param  params the finder parameters. Can specify values for the
	 *         "usersRoles" key. For more information, see {@link
	 *         com.liferay.portal.kernel.service.persistence.RoleFinder}
	 * @return the number of matching roles
	 */
	@Override
	public int searchCount(
		long companyId, String name, String description, Integer[] types,
		LinkedHashMap<String, Object> params) {

		return roleFinder.countByC_N_D_T(
			companyId, name, description, types, params, true);
	}

	/**
	 * Sets the roles associated with the user, replacing the user's existing
	 * roles. The user is reindexed after the roles are set.
	 *
	 * @param userId the primary key of the user
	 * @param roleIds the primary keys of the roles
	 */
	@Override
	public void setUserRoles(long userId, long[] roleIds)
		throws PortalException {

		roleIds = UsersAdminUtil.addRequiredRoles(userId, roleIds);

		Arrays.sort(roleIds);

		long[] currentRoleIds = _userPersistence.getRolePrimaryKeys(userId);

		Arrays.sort(currentRoleIds);

		if (!Arrays.equals(currentRoleIds, roleIds)) {
			_userPersistence.setRoles(userId, roleIds);

			reindex(userId);
		}
	}

	/**
	 * Removes the matching roles associated with the user. The user is
	 * reindexed after the roles are removed.
	 *
	 * @param userId the primary key of the user
	 * @param roleIds the primary keys of the roles
	 */
	@Override
	public void unsetUserRoles(long userId, long[] roleIds)
		throws PortalException {

		roleIds = UsersAdminUtil.removeRequiredRoles(userId, roleIds);

		_userPersistence.removeRoles(userId, roleIds);

		reindex(userId);
	}

	@Override
	public Role updateExternalReferenceCode(
			long roleId, String externalReferenceCode)
		throws PortalException {

		return updateExternalReferenceCode(
			getRole(roleId), externalReferenceCode);
	}

	@Override
	public Role updateExternalReferenceCode(
			Role role, String externalReferenceCode)
		throws PortalException {

		if (Objects.equals(
				role.getExternalReferenceCode(), externalReferenceCode)) {

			return role;
		}

		role.setExternalReferenceCode(externalReferenceCode);

		return updateRole(role);
	}

	/**
	 * Updates the role with the primary key.
	 *
	 * @param  roleId the primary key of the role
	 * @param  name the role's new name
	 * @param  titleMap the new localized titles (optionally <code>null</code>)
	 *         to replace those existing for the role
	 * @param  descriptionMap the new localized descriptions (optionally
	 *         <code>null</code>) to replace those existing for the role
	 * @param  subtype the role's new subtype (optionally <code>null</code>)
	 * @param  serviceContext the service context to be applied (optionally
	 *         <code>null</code>). Can set expando bridge attributes for the
	 *         role.
	 * @return the role with the primary key
	 */
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public Role updateRole(
			String externalReferenceCode, long roleId, String name,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			String subtype, ServiceContext serviceContext)
		throws PortalException {

		Role role = rolePersistence.findByPrimaryKey(roleId);

		validate(roleId, role.getCompanyId(), role.getClassNameId(), name);

		if (role.isSystem()) {
			name = role.getName();
			subtype = null;
		}

		role.setExternalReferenceCode(externalReferenceCode);
		role.setName(name);
		role.setTitleMap(titleMap);
		role.setDescriptionMap(descriptionMap);
		role.setSubtype(subtype);

		if (role.getStatus() == WorkflowConstants.STATUS_INCOMPLETE) {
			role.setStatus(WorkflowConstants.STATUS_APPROVED);
		}

		role.setExpandoBridgeAttributes(serviceContext);

		return rolePersistence.update(role);
	}

	@Override
	public void validateName(String name) throws PortalException {
		String[] invalidCharacters = StringUtil.split(
			RoleConstants.NAME_INVALID_CHARACTERS, StringPool.SPACE);

		if (Validator.isNull(name) ||
			(StringUtil.indexOfAny(name, invalidCharacters) > -1)) {

			throw new RoleNameException();
		}

		if (!PropsValues.ROLES_NAME_ALLOW_NUMERIC && Validator.isNumber(name)) {
			throw new RoleNameException();
		}
	}

	protected void checkSystemRole(
			Map<String, Role> companyRolesMap, long companyId, String name,
			String description, int type)
		throws PortalException {

		Role role = companyRolesMap.get(name);

		if (role == null) {
			role = rolePersistence.fetchByC_N(companyId, name);
		}

		if (role == null) {
			User user = _userLocalService.getGuestUser(companyId);

			PermissionThreadLocal.setAddResource(false);

			try {
				role = roleLocalService.addRole(
					null, user.getUserId(), null, 0, name, null,
					LocalizationUtil.getLocalizationMap(description), type,
					null, null);
			}
			finally {
				PermissionThreadLocal.setAddResource(true);
			}

			if (name.equals(RoleConstants.ANALYTICS_ADMINISTRATOR)) {
				initAnalyticsAdministratorViewPermissions(role);
			}
			else if (name.equals(RoleConstants.PUBLICATIONS_USER)) {
				initPublicationsUserViewPermissions(role);
			}
			else if (name.equals(RoleConstants.USER)) {
				initPersonalControlPanelPortletsPermissions(role);
			}
		}
		else if (!description.equals(role.getDescription())) {
			role.setDescription(description);

			roleLocalService.updateRole(role);
		}
	}

	protected String[] getDefaultControlPanelPortlets() {
		String myAccountPortletId = PortletProviderUtil.getPortletId(
			PortalMyAccountApplicationType.MyAccount.CLASS_NAME,
			PortletProvider.Action.VIEW);

		return new String[] {
			myAccountPortletId, PortletKeys.MY_PAGES,
			PortletKeys.MY_WORKFLOW_INSTANCE, PortletKeys.MY_WORKFLOW_TASK
		};
	}

	protected Map<Team, Role> getTeamRoleMap(
			long groupId, long[] excludedRoleIds)
		throws PortalException {

		Group group = _groupPersistence.findByPrimaryKey(groupId);

		if (group.isLayout()) {
			group = group.getParentGroup();
		}

		List<Team> teams = _teamPersistence.findByGroupId(group.getGroupId());

		if (teams.isEmpty()) {
			return Collections.emptyMap();
		}

		Set<Long> roleIds = SetUtil.fromArray(excludedRoleIds);

		Map<Team, Role> teamRoleMap = new LinkedHashMap<>();

		for (Team team : teams) {
			Role role = getTeamRole(team.getCompanyId(), team.getTeamId());

			if (roleIds.contains(role.getRoleId())) {
				continue;
			}

			teamRoleMap.put(team, role);
		}

		return teamRoleMap;
	}

	protected void initAnalyticsAdministratorViewPermissions(Role role)
		throws PortalException {

		_resourcePermissionLocalService.addResourcePermission(
			role.getCompanyId(), Group.class.getName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(role.getCompanyId()), role.getRoleId(),
			ActionKeys.VIEW);

		_resourcePermissionLocalService.addResourcePermission(
			role.getCompanyId(), User.class.getName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(role.getCompanyId()), role.getRoleId(),
			ActionKeys.VIEW);

		_resourcePermissionLocalService.addResourcePermission(
			role.getCompanyId(), Organization.class.getName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(role.getCompanyId()), role.getRoleId(),
			ActionKeys.VIEW_MEMBERS);

		_resourcePermissionLocalService.addResourcePermission(
			role.getCompanyId(), UserGroup.class.getName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(role.getCompanyId()), role.getRoleId(),
			ActionKeys.VIEW_MEMBERS);
	}

	protected void initPersonalControlPanelPortletsPermissions(Role role)
		throws PortalException {

		for (String portletId : getDefaultControlPanelPortlets()) {
			int count = _resourcePermissionPersistence.countByC_N_S_P_R(
				role.getCompanyId(), portletId, ResourceConstants.SCOPE_COMPANY,
				String.valueOf(role.getCompanyId()), role.getRoleId());

			if (count > 0) {
				continue;
			}

			ResourceAction resourceAction =
				_resourceActionLocalService.fetchResourceAction(
					portletId, ActionKeys.ACCESS_IN_CONTROL_PANEL);

			if (resourceAction == null) {
				continue;
			}

			setRolePermissions(
				role, portletId,
				new String[] {ActionKeys.ACCESS_IN_CONTROL_PANEL});
		}
	}

	protected void initPublicationsUserViewPermissions(Role role)
		throws PortalException {

		_resourcePermissionLocalService.addResourcePermission(
			role.getCompanyId(), PortletKeys.PORTAL,
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(role.getCompanyId()), role.getRoleId(),
			ActionKeys.VIEW_CONTROL_PANEL);
	}

	protected void reindex(long userId) throws SearchException {
		Indexer<User> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			User.class);

		indexer.reindex(_userLocalService.fetchUser(userId));
	}

	protected void setRolePermissions(
			Role role, String name, String[] actionIds)
		throws PortalException {

		_resourcePermissionLocalService.setResourcePermissions(
			role.getCompanyId(), name, ResourceConstants.SCOPE_COMPANY,
			String.valueOf(role.getCompanyId()), role.getRoleId(), actionIds);
	}

	protected void validate(
			long roleId, long companyId, long classNameId, String name)
		throws PortalException {

		if (classNameId == _classNameLocalService.getClassNameId(Role.class)) {
			validateName(name);
		}

		int count = rolePersistence.dslQueryCount(
			DSLQueryFactoryUtil.count(
			).from(
				RoleTable.INSTANCE
			).where(
				RoleTable.INSTANCE.companyId.eq(
					companyId
				).and(
					DSLFunctionFactoryUtil.lower(
						RoleTable.INSTANCE.name
					).eq(
						StringUtil.lowerCase(name)
					)
				).and(
					RoleTable.INSTANCE.roleId.neq(roleId)
				)
			));

		if (count > 0) {
			throw new DuplicateRoleException("{roleId=" + roleId + "}");
		}

		if (name.equals(RoleConstants.PLACEHOLDER_DEFAULT_GROUP_ROLE)) {
			throw new RoleNameException(
				RoleConstants.PLACEHOLDER_DEFAULT_GROUP_ROLE +
					" is a temporary placeholder that must not be persisted");
		}
	}

	private String _getPropertyDescription(String name, String property) {
		String propertyDescription = PropsUtil.get(
			StringBundler.concat(
				property,
				StringUtil.replace(name, CharPool.SPACE, CharPool.PERIOD),
				".description"));

		if (Validator.isNull(propertyDescription)) {
			return StringPool.BLANK;
		}

		return propertyDescription;
	}

	private static final int _CACHEABLE_QUERY_LIMIT_LPD_38877 =
		GetterUtil.getInteger(PropsUtil.get("cacheable.query.limit.LPD-38877"));

	private static final Log _log = LogFactoryUtil.getLog(
		RoleLocalServiceImpl.class);

	@BeanReference(type = ClassNameLocalService.class)
	private ClassNameLocalService _classNameLocalService;

	@BeanReference(type = CompanyLocalService.class)
	private CompanyLocalService _companyLocalService;

	@BeanReference(type = ExpandoRowLocalService.class)
	private ExpandoRowLocalService _expandoRowLocalService;

	@BeanReference(type = GroupLocalService.class)
	private GroupLocalService _groupLocalService;

	@BeanReference(type = GroupPersistence.class)
	private GroupPersistence _groupPersistence;

	@BeanReference(type = LayoutLocalService.class)
	private LayoutLocalService _layoutLocalService;

	@BeanReference(type = ResourceActionLocalService.class)
	private ResourceActionLocalService _resourceActionLocalService;

	@BeanReference(type = ResourceLocalService.class)
	private ResourceLocalService _resourceLocalService;

	@BeanReference(type = ResourcePermissionLocalService.class)
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@BeanReference(type = ResourcePermissionPersistence.class)
	private ResourcePermissionPersistence _resourcePermissionPersistence;

	@BeanReference(type = TeamPersistence.class)
	private TeamPersistence _teamPersistence;

	@BeanReference(type = UserGroupGroupRoleLocalService.class)
	private UserGroupGroupRoleLocalService _userGroupGroupRoleLocalService;

	@BeanReference(type = UserGroupRoleLocalService.class)
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@BeanReference(type = UserLocalService.class)
	private UserLocalService _userLocalService;

	@BeanReference(type = UserPersistence.class)
	private UserPersistence _userPersistence;

}