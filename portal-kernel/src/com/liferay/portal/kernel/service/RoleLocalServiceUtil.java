/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for Role. This utility wraps
 * <code>com.liferay.portal.service.impl.RoleLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see RoleLocalService
 * @generated
 */
public class RoleLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.service.impl.RoleLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static boolean addGroupRole(long groupId, long roleId) {
		return getService().addGroupRole(groupId, roleId);
	}

	public static boolean addGroupRole(long groupId, Role role) {
		return getService().addGroupRole(groupId, role);
	}

	public static boolean addGroupRoles(long groupId, List<Role> roles) {
		return getService().addGroupRoles(groupId, roles);
	}

	public static boolean addGroupRoles(long groupId, long[] roleIds) {
		return getService().addGroupRoles(groupId, roleIds);
	}

	/**
	 * Adds the role to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RoleLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param role the role
	 * @return the role that was added
	 */
	public static Role addRole(Role role) {
		return getService().addRole(role);
	}

	public static Role addRole(
			String externalReferenceCode, long userId, String className,
			long classPK, String name, Map<java.util.Locale, String> titleMap,
			Map<java.util.Locale, String> descriptionMap, int type,
			String subtype, ServiceContext serviceContext)
		throws PortalException {

		return getService().addRole(
			externalReferenceCode, userId, className, classPK, name, titleMap,
			descriptionMap, type, subtype, serviceContext);
	}

	/**
	 * @throws PortalException
	 */
	public static boolean addUserRole(long userId, long roleId)
		throws PortalException {

		return getService().addUserRole(userId, roleId);
	}

	/**
	 * @throws PortalException
	 */
	public static boolean addUserRole(long userId, Role role)
		throws PortalException {

		return getService().addUserRole(userId, role);
	}

	/**
	 * @throws PortalException
	 */
	public static boolean addUserRoles(long userId, List<Role> roles)
		throws PortalException {

		return getService().addUserRoles(userId, roles);
	}

	/**
	 * @throws PortalException
	 */
	public static boolean addUserRoles(long userId, long[] roleIds)
		throws PortalException {

		return getService().addUserRoles(userId, roleIds);
	}

	/**
	 * Checks to ensure that the system roles map has appropriate default roles
	 * in each company.
	 */
	public static void checkSystemRoles() throws PortalException {
		getService().checkSystemRoles();
	}

	/**
	 * Checks to ensure that the system roles map has appropriate default roles
	 * in the company.
	 *
	 * @param companyId the primary key of the company
	 */
	public static void checkSystemRoles(long companyId) throws PortalException {
		getService().checkSystemRoles(companyId);
	}

	public static void clearGroupRoles(long groupId) {
		getService().clearGroupRoles(groupId);
	}

	/**
	 * @throws PortalException
	 */
	public static void clearUserRoles(long userId) throws PortalException {
		getService().clearUserRoles(userId);
	}

	public static Role copyRole(
			long userId, String name, long sourceRoleId,
			ServiceContext serviceContext)
		throws PortalException {

		return getService().copyRole(
			userId, name, sourceRoleId, serviceContext);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	/**
	 * Creates a new role with the primary key. Does not add the role to the database.
	 *
	 * @param roleId the primary key for the new role
	 * @return the new role
	 */
	public static Role createRole(long roleId) {
		return getService().createRole(roleId);
	}

	public static void deleteGroupRole(long groupId, long roleId) {
		getService().deleteGroupRole(groupId, roleId);
	}

	public static void deleteGroupRole(long groupId, Role role) {
		getService().deleteGroupRole(groupId, role);
	}

	public static void deleteGroupRoles(long groupId, List<Role> roles) {
		getService().deleteGroupRoles(groupId, roles);
	}

	public static void deleteGroupRoles(long groupId, long[] roleIds) {
		getService().deleteGroupRoles(groupId, roleIds);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	/**
	 * Deletes the role with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RoleLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param roleId the primary key of the role
	 * @return the role that was removed
	 * @throws PortalException if a role with the primary key could not be found
	 */
	public static Role deleteRole(long roleId) throws PortalException {
		return getService().deleteRole(roleId);
	}

	/**
	 * Deletes the role from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RoleLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param role the role
	 * @return the role that was removed
	 * @throws PortalException
	 */
	public static Role deleteRole(Role role) throws PortalException {
		return getService().deleteRole(role);
	}

	/**
	 * @throws PortalException
	 */
	public static void deleteUserRole(long userId, long roleId)
		throws PortalException {

		getService().deleteUserRole(userId, roleId);
	}

	/**
	 * @throws PortalException
	 */
	public static void deleteUserRole(long userId, Role role)
		throws PortalException {

		getService().deleteUserRole(userId, role);
	}

	/**
	 * @throws PortalException
	 */
	public static void deleteUserRoles(long userId, List<Role> roles)
		throws PortalException {

		getService().deleteUserRoles(userId, roles);
	}

	/**
	 * @throws PortalException
	 */
	public static void deleteUserRoles(long userId, long[] roleIds)
		throws PortalException {

		getService().deleteUserRoles(userId, roleIds);
	}

	public static <T> T dslQuery(DSLQuery dslQuery) {
		return getService().dslQuery(dslQuery);
	}

	public static int dslQueryCount(DSLQuery dslQuery) {
		return getService().dslQueryCount(dslQuery);
	}

	public static DynamicQuery dynamicQuery() {
		return getService().dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	public static <T> List<T> dynamicQuery(DynamicQuery dynamicQuery) {
		return getService().dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RoleModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getService().dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RoleModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator) {

		return getService().dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(DynamicQuery dynamicQuery) {
		return getService().dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return getService().dynamicQueryCount(dynamicQuery, projection);
	}

	public static Role fetchRole(long roleId) {
		return getService().fetchRole(roleId);
	}

	/**
	 * Returns the role with the name in the company.
	 *
	 * <p>
	 * The method searches the system roles map first for default roles. If a
	 * role with the name is not found, then the method will query the database.
	 * </p>
	 *
	 * @param companyId the primary key of the company
	 * @param name the role's name
	 * @return Returns the role with the name or <code>null</code> if a role
	 with the name could not be found in the company
	 */
	public static Role fetchRole(long companyId, String name) {
		return getService().fetchRole(companyId, name);
	}

	public static Role fetchRoleByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return getService().fetchRoleByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the role with the matching UUID and company.
	 *
	 * @param uuid the role's UUID
	 * @param companyId the primary key of the company
	 * @return the matching role, or <code>null</code> if a matching role could not be found
	 */
	public static Role fetchRoleByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().fetchRoleByUuidAndCompanyId(uuid, companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static int getAssigneesTotal(long roleId) throws PortalException {
		return getService().getAssigneesTotal(roleId);
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
	 * @param groupId the primary key of the group
	 * @return the default role for the group with the primary key
	 */
	public static Role getDefaultGroupRole(long groupId)
		throws PortalException {

		return getService().getDefaultGroupRole(groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	/**
	 * Returns the groupIds of the groups associated with the role.
	 *
	 * @param roleId the roleId of the role
	 * @return long[] the groupIds of groups associated with the role
	 */
	public static long[] getGroupPrimaryKeys(long roleId) {
		return getService().getGroupPrimaryKeys(roleId);
	}

	public static List<Role> getGroupRelatedRoles(long groupId)
		throws PortalException {

		return getService().getGroupRelatedRoles(groupId);
	}

	public static List<Role> getGroupRoles(long groupId) {
		return getService().getGroupRoles(groupId);
	}

	public static List<Role> getGroupRoles(long groupId, int start, int end) {
		return getService().getGroupRoles(groupId, start, end);
	}

	public static List<Role> getGroupRoles(
		long groupId, int start, int end,
		OrderByComparator<Role> orderByComparator) {

		return getService().getGroupRoles(
			groupId, start, end, orderByComparator);
	}

	public static List<Role> getGroupRolesAndTeamRoles(
		long companyId, String name, List<String> excludedNames, String title,
		String description, int[] types, long excludedTeamRoleId,
		long teamGroupId, int start, int end) {

		return getService().getGroupRolesAndTeamRoles(
			companyId, name, excludedNames, title, description, types,
			excludedTeamRoleId, teamGroupId, start, end);
	}

	public static int getGroupRolesAndTeamRolesCount(
		long companyId, String name, List<String> excludedNames, String title,
		String description, int[] types, long excludedTeamRoleId,
		long teamGroupId) {

		return getService().getGroupRolesAndTeamRolesCount(
			companyId, name, excludedNames, title, description, types,
			excludedTeamRoleId, teamGroupId);
	}

	public static int getGroupRolesCount(long groupId) {
		return getService().getGroupRolesCount(groupId);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	public static Role getOrAddEmptyRole(
			String externalReferenceCode, long companyId, long userId,
			String className, long classPK, String name, int type)
		throws PortalException {

		return getService().getOrAddEmptyRole(
			externalReferenceCode, companyId, userId, className, classPK, name,
			type);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns a map of role names to associated action IDs for the named
	 * resource in the company within the permission scope.
	 *
	 * @param companyId the primary key of the company
	 * @param name the resource name
	 * @param scope the permission scope
	 * @param primKey the primary key of the resource's class
	 * @return the role names and action IDs
	 */
	public static Map<String, List<String>> getResourceRoles(
		long companyId, String name, int scope, String primKey) {

		return getService().getResourceRoles(companyId, name, scope, primKey);
	}

	/**
	 * Returns all the roles associated with the action ID in the company within
	 * the permission scope.
	 *
	 * @param companyId the primary key of the company
	 * @param name the resource name
	 * @param scope the permission scope
	 * @param primKey the primary key of the resource's class
	 * @param actionId the name of the resource action
	 * @return the roles
	 */
	public static List<Role> getResourceRoles(
		long companyId, String name, int scope, String primKey,
		String actionId) {

		return getService().getResourceRoles(
			companyId, name, scope, primKey, actionId);
	}

	/**
	 * Returns the role with the primary key.
	 *
	 * @param roleId the primary key of the role
	 * @return the role
	 * @throws PortalException if a role with the primary key could not be found
	 */
	public static Role getRole(long roleId) throws PortalException {
		return getService().getRole(roleId);
	}

	/**
	 * Returns the role with the name in the company.
	 *
	 * <p>
	 * The method searches the system roles map first for default roles. If a
	 * role with the name is not found, then the method will query the database.
	 * </p>
	 *
	 * @param companyId the primary key of the company
	 * @param name the role's name
	 * @return the role with the name
	 */
	public static Role getRole(long companyId, String name)
		throws PortalException {

		return getService().getRole(companyId, name);
	}

	public static Role getRoleByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getRoleByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the role with the matching UUID and company.
	 *
	 * @param uuid the role's UUID
	 * @param companyId the primary key of the company
	 * @return the matching role
	 * @throws PortalException if a matching role could not be found
	 */
	public static Role getRoleByUuidAndCompanyId(String uuid, long companyId)
		throws PortalException {

		return getService().getRoleByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of all the roles.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RoleModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of roles
	 * @param end the upper bound of the range of roles (not inclusive)
	 * @return the range of roles
	 */
	public static List<Role> getRoles(int start, int end) {
		return getService().getRoles(start, end);
	}

	/**
	 * Returns all the roles of the type and subtype.
	 *
	 * @param type the role's type (optionally <code>0</code>)
	 * @param subtype the role's subtype (optionally <code>null</code>)
	 * @return the roles of the type and subtype
	 */
	public static List<Role> getRoles(int type, String subtype) {
		return getService().getRoles(type, subtype);
	}

	/**
	 * Returns all the roles in the company.
	 *
	 * @param companyId the primary key of the company
	 * @return the roles in the company
	 */
	public static List<Role> getRoles(long companyId) {
		return getService().getRoles(companyId);
	}

	/**
	 * Returns all the roles with the types.
	 *
	 * @param companyId the primary key of the company
	 * @param types the role types (optionally <code>null</code>)
	 * @return the roles with the types
	 */
	public static List<Role> getRoles(long companyId, int[] types) {
		return getService().getRoles(companyId, types);
	}

	public static List<Role> getRoles(
		long companyId, long classNameId, long[] classPKs, int type) {

		return getService().getRoles(companyId, classNameId, classPKs, type);
	}

	/**
	 * Returns all the roles with the primary keys.
	 *
	 * @param roleIds the primary keys of the roles
	 * @return the roles with the primary keys
	 */
	public static List<Role> getRoles(long[] roleIds) throws PortalException {
		return getService().getRoles(roleIds);
	}

	/**
	 * Returns the number of roles.
	 *
	 * @return the number of roles
	 */
	public static int getRolesCount() {
		return getService().getRolesCount();
	}

	/**
	 * Returns all the roles of the subtype.
	 *
	 * @param subtype the role's subtype (optionally <code>null</code>)
	 * @return the roles of the subtype
	 */
	public static List<Role> getSubtypeRoles(String subtype) {
		return getService().getSubtypeRoles(subtype);
	}

	/**
	 * Returns the number of roles of the subtype.
	 *
	 * @param subtype the role's subtype (optionally <code>null</code>)
	 * @return the number of roles of the subtype
	 */
	public static int getSubtypeRolesCount(String subtype) {
		return getService().getSubtypeRolesCount(subtype);
	}

	/**
	 * Returns the team role in the company.
	 *
	 * @param companyId the primary key of the company
	 * @param teamId the primary key of the team
	 * @return the team role in the company
	 */
	public static Role getTeamRole(long companyId, long teamId)
		throws PortalException {

		return getService().getTeamRole(companyId, teamId);
	}

	/**
	 * Returns the team role map for the group.
	 *
	 * @param groupId the primary key of the group
	 * @return the team role map for the group
	 */
	public static Map<com.liferay.portal.kernel.model.Team, Role>
			getTeamRoleMap(long groupId)
		throws PortalException {

		return getService().getTeamRoleMap(groupId);
	}

	/**
	 * Returns the team roles in the group.
	 *
	 * @param groupId the primary key of the group
	 * @return the team roles in the group
	 */
	public static List<Role> getTeamRoles(long groupId) throws PortalException {
		return getService().getTeamRoles(groupId);
	}

	/**
	 * Returns the team roles in the group, excluding the specified role IDs.
	 *
	 * @param groupId the primary key of the group
	 * @param excludedRoleIds the primary keys of the roles to exclude
	 (optionally <code>null</code>)
	 * @return the team roles in the group, excluding the specified role IDs
	 */
	public static List<Role> getTeamRoles(long groupId, long[] excludedRoleIds)
		throws PortalException {

		return getService().getTeamRoles(groupId, excludedRoleIds);
	}

	/**
	 * Returns the team roles in the company.
	 *
	 * @param companyId the primary key of the company
	 * @param teamIds the primary keys of the teams
	 * @return the team roles in the company
	 */
	public static List<Role> getTeamsRoles(long companyId, long[] teamIds)
		throws PortalException {

		return getService().getTeamsRoles(companyId, teamIds);
	}

	/**
	 * Returns all the roles of the type.
	 *
	 * @param type the role's type (optionally <code>0</code>)
	 * @return the range of the roles of the type
	 */
	public static List<Role> getTypeRoles(int type) {
		return getService().getTypeRoles(type);
	}

	/**
	 * Returns a range of all the roles of the type.
	 *
	 * @param type the role's type (optionally <code>0</code>)
	 * @param start the lower bound of the range of roles to return
	 * @param end the upper bound of the range of roles to return (not
	 inclusive)
	 * @return the range of the roles of the type
	 */
	public static List<Role> getTypeRoles(int type, int start, int end) {
		return getService().getTypeRoles(type, start, end);
	}

	/**
	 * Returns the number of roles of the type.
	 *
	 * @param type the role's type (optionally <code>0</code>)
	 * @return the number of roles of the type
	 */
	public static int getTypeRolesCount(int type) {
		return getService().getTypeRolesCount(type);
	}

	/**
	 * Returns all the user's roles within the user group.
	 *
	 * @param userId the primary key of the user
	 * @param groupId the primary key of the group
	 * @return the user's roles within the user group
	 */
	public static List<Role> getUserGroupGroupRoles(long userId, long groupId) {
		return getService().getUserGroupGroupRoles(userId, groupId);
	}

	public static List<Role> getUserGroupGroupRoles(
		long userId, long groupId, int start, int end) {

		return getService().getUserGroupGroupRoles(userId, groupId, start, end);
	}

	public static int getUserGroupGroupRolesCount(long userId, long groupId) {
		return getService().getUserGroupGroupRolesCount(userId, groupId);
	}

	/**
	 * Returns all the user's roles within the user group.
	 *
	 * @param userId the primary key of the user
	 * @param groupId the primary key of the group
	 * @return the user's roles within the user group
	 */
	public static List<Role> getUserGroupRoles(long userId, long groupId) {
		return getService().getUserGroupRoles(userId, groupId);
	}

	/**
	 * Returns the userIds of the users associated with the role.
	 *
	 * @param roleId the roleId of the role
	 * @return long[] the userIds of users associated with the role
	 */
	public static long[] getUserPrimaryKeys(long roleId) {
		return getService().getUserPrimaryKeys(roleId);
	}

	/**
	 * Returns the union of all the user's roles within the groups. If no
	 * groups are provided, only the user's directly assigned roles are
	 * returned.
	 *
	 * @param userId the primary key of the user
	 * @param groups the groups (optionally <code>null</code>)
	 * @return the union of all the user's roles within the groups
	 */
	public static List<Role> getUserRelatedRoles(
		long userId, List<com.liferay.portal.kernel.model.Group> groups) {

		return getService().getUserRelatedRoles(userId, groups);
	}

	/**
	 * Returns all the user's roles within the group.
	 *
	 * @param userId the primary key of the user
	 * @param groupId the primary key of the group
	 * @return the user's roles within the group
	 */
	public static List<Role> getUserRelatedRoles(long userId, long groupId) {
		return getService().getUserRelatedRoles(userId, groupId);
	}

	/**
	 * Returns the union of all the user's roles within the groups. If no
	 * groupIds are provided, only the user's directly assigned roles are
	 * returned.
	 *
	 * @param userId the primary key of the user
	 * @param groupIds the primary keys of the groups
	 * @return the union of all the user's roles within the groups
	 */
	public static List<Role> getUserRelatedRoles(long userId, long[] groupIds) {
		return getService().getUserRelatedRoles(userId, groupIds);
	}

	public static List<Role> getUserRoles(long userId) {
		return getService().getUserRoles(userId);
	}

	public static List<Role> getUserRoles(long userId, int start, int end) {
		return getService().getUserRoles(userId, start, end);
	}

	public static List<Role> getUserRoles(
		long userId, int start, int end,
		OrderByComparator<Role> orderByComparator) {

		return getService().getUserRoles(userId, start, end, orderByComparator);
	}

	public static int getUserRolesCount(long userId) {
		return getService().getUserRolesCount(userId);
	}

	public static List<Role> getUserTeamRoles(long userId, long groupId) {
		return getService().getUserTeamRoles(userId, groupId);
	}

	public static boolean hasGroupRole(long groupId, long roleId) {
		return getService().hasGroupRole(groupId, roleId);
	}

	public static boolean hasGroupRoles(long groupId) {
		return getService().hasGroupRoles(groupId);
	}

	public static boolean hasUserRole(long userId, long roleId) {
		return getService().hasUserRole(userId, roleId);
	}

	/**
	 * Returns <code>true</code> if the user is associated with the named
	 * regular role.
	 *
	 * @param userId the primary key of the user
	 * @param companyId the primary key of the company
	 * @param name the name of the role
	 * @param inherited whether to include the user's inherited roles in the
	 search
	 * @return <code>true</code> if the user is associated with the regular
	 role; <code>false</code> otherwise
	 */
	public static boolean hasUserRole(
			long userId, long companyId, String name, boolean inherited)
		throws PortalException {

		return getService().hasUserRole(userId, companyId, name, inherited);
	}

	public static boolean hasUserRoles(long userId) {
		return getService().hasUserRoles(userId);
	}

	/**
	 * Returns <code>true</code> if the user has any one of the named regular
	 * roles.
	 *
	 * @param userId the primary key of the user
	 * @param companyId the primary key of the company
	 * @param names the names of the roles
	 * @param inherited whether to include the user's inherited roles in the
	 search
	 * @return <code>true</code> if the user has any one of the regular roles;
	 <code>false</code> otherwise
	 */
	public static boolean hasUserRoles(
			long userId, long companyId, String[] names, boolean inherited)
		throws PortalException {

		return getService().hasUserRoles(userId, companyId, names, inherited);
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
	 * @param companyId the primary key of the company
	 * @param keywords the keywords (space separated), which may occur in the
	 role's name or description (optionally <code>null</code>)
	 * @param types the role types (optionally <code>null</code>)
	 * @param start the lower bound of the range of roles to return
	 * @param end the upper bound of the range of roles to return (not
	 inclusive)
	 * @param orderByComparator the comparator to order the roles (optionally
	 <code>null</code>)
	 * @return the ordered range of the matching roles, ordered by
	 <code>orderByComparator</code>
	 * @see com.liferay.portal.kernel.service.persistence.RoleFinder
	 */
	public static List<Role> search(
		long companyId, String keywords, Integer[] types, int start, int end,
		OrderByComparator<Role> orderByComparator) {

		return getService().search(
			companyId, keywords, types, start, end, orderByComparator);
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
	 * @param companyId the primary key of the company
	 * @param keywords the keywords (space separated), which may occur in the
	 role's name or description (optionally <code>null</code>)
	 * @param types the role types (optionally <code>null</code>)
	 * @param params the finder parameters. Can specify values for the
	 "usersRoles" key. For more information, see {@link
	 com.liferay.portal.kernel.service.persistence.RoleFinder}
	 * @param start the lower bound of the range of roles to return
	 * @param end the upper bound of the range of roles to return (not
	 inclusive)
	 * @param orderByComparator the comparator to order the roles (optionally
	 <code>null</code>)
	 * @return the ordered range of the matching roles, ordered by
	 <code>orderByComparator</code>
	 * @see com.liferay.portal.kernel.service.persistence.RoleFinder
	 */
	public static List<Role> search(
		long companyId, String keywords, Integer[] types,
		java.util.LinkedHashMap<String, Object> params, int start, int end,
		OrderByComparator<Role> orderByComparator) {

		return getService().search(
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
	 * @param companyId the primary key of the company
	 * @param name the role's name (optionally <code>null</code>)
	 * @param description the role's description (optionally <code>null</code>)
	 * @param types the role types (optionally <code>null</code>)
	 * @param start the lower bound of the range of the roles to return
	 * @param end the upper bound of the range of the roles to return (not
	 inclusive)
	 * @param orderByComparator the comparator to order the roles (optionally
	 <code>null</code>)
	 * @return the ordered range of the matching roles, ordered by
	 <code>orderByComparator</code>
	 * @see com.liferay.portal.kernel.service.persistence.RoleFinder
	 */
	public static List<Role> search(
		long companyId, String name, String description, Integer[] types,
		int start, int end, OrderByComparator<Role> orderByComparator) {

		return getService().search(
			companyId, name, description, types, start, end, orderByComparator);
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
	 * @param companyId the primary key of the company
	 * @param name the role's name (optionally <code>null</code>)
	 * @param description the role's description (optionally <code>null</code>)
	 * @param types the role types (optionally <code>null</code>)
	 * @param params the finder's parameters. Can specify values for the
	 "usersRoles" key. For more information, see {@link
	 com.liferay.portal.kernel.service.persistence.RoleFinder}
	 * @param start the lower bound of the range of the roles to return
	 * @param end the upper bound of the range of the roles to return (not
	 inclusive)
	 * @param orderByComparator the comparator to order the roles (optionally
	 <code>null</code>)
	 * @return the ordered range of the matching roles, ordered by
	 <code>orderByComparator</code>
	 * @see com.liferay.portal.kernel.service.persistence.RoleFinder
	 */
	public static List<Role> search(
		long companyId, String name, String description, Integer[] types,
		java.util.LinkedHashMap<String, Object> params, int start, int end,
		OrderByComparator<Role> orderByComparator) {

		return getService().search(
			companyId, name, description, types, params, start, end,
			orderByComparator);
	}

	/**
	 * Returns the number of roles that match the keywords and types.
	 *
	 * @param companyId the primary key of the company
	 * @param keywords the keywords (space separated), which may occur in the
	 role's name or description (optionally <code>null</code>)
	 * @param types the role types (optionally <code>null</code>)
	 * @return the number of matching roles
	 */
	public static int searchCount(
		long companyId, String keywords, Integer[] types) {

		return getService().searchCount(companyId, keywords, types);
	}

	/**
	 * Returns the number of roles that match the keywords, types and params.
	 *
	 * @param companyId the primary key of the company
	 * @param keywords the keywords (space separated), which may occur in the
	 role's name or description (optionally <code>null</code>)
	 * @param types the role types (optionally <code>null</code>)
	 * @param params the finder parameters. For more information, see {@link
	 com.liferay.portal.kernel.service.persistence.RoleFinder}
	 * @return the number of matching roles
	 */
	public static int searchCount(
		long companyId, String keywords, Integer[] types,
		java.util.LinkedHashMap<String, Object> params) {

		return getService().searchCount(companyId, keywords, types, params);
	}

	/**
	 * Returns the number of roles that match the name, description, and types.
	 *
	 * @param companyId the primary key of the company
	 * @param name the role's name (optionally <code>null</code>)
	 * @param description the role's description (optionally <code>null</code>)
	 * @param types the role types (optionally <code>null</code>)
	 * @return the number of matching roles
	 */
	public static int searchCount(
		long companyId, String name, String description, Integer[] types) {

		return getService().searchCount(companyId, name, description, types);
	}

	/**
	 * Returns the number of roles that match the name, description, types, and
	 * params.
	 *
	 * @param companyId the primary key of the company
	 * @param name the role's name (optionally <code>null</code>)
	 * @param description the role's description (optionally <code>null</code>)
	 * @param types the role types (optionally <code>null</code>)
	 * @param params the finder parameters. Can specify values for the
	 "usersRoles" key. For more information, see {@link
	 com.liferay.portal.kernel.service.persistence.RoleFinder}
	 * @return the number of matching roles
	 */
	public static int searchCount(
		long companyId, String name, String description, Integer[] types,
		java.util.LinkedHashMap<String, Object> params) {

		return getService().searchCount(
			companyId, name, description, types, params);
	}

	public static void setGroupRoles(long groupId, long[] roleIds) {
		getService().setGroupRoles(groupId, roleIds);
	}

	/**
	 * @throws PortalException
	 */
	public static void setUserRoles(long userId, long[] roleIds)
		throws PortalException {

		getService().setUserRoles(userId, roleIds);
	}

	/**
	 * Removes the matching roles associated with the user. The user is
	 * reindexed after the roles are removed.
	 *
	 * @param userId the primary key of the user
	 * @param roleIds the primary keys of the roles
	 */
	public static void unsetUserRoles(long userId, long[] roleIds)
		throws PortalException {

		getService().unsetUserRoles(userId, roleIds);
	}

	public static Role updateExternalReferenceCode(
			long roleId, String externalReferenceCode)
		throws PortalException {

		return getService().updateExternalReferenceCode(
			roleId, externalReferenceCode);
	}

	public static Role updateExternalReferenceCode(
			Role role, String externalReferenceCode)
		throws PortalException {

		return getService().updateExternalReferenceCode(
			role, externalReferenceCode);
	}

	/**
	 * Updates the role in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RoleLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param role the role
	 * @return the role that was updated
	 */
	public static Role updateRole(Role role) {
		return getService().updateRole(role);
	}

	/**
	 * Updates the role with the primary key.
	 *
	 * @param roleId the primary key of the role
	 * @param name the role's new name
	 * @param titleMap the new localized titles (optionally <code>null</code>)
	 to replace those existing for the role
	 * @param descriptionMap the new localized descriptions (optionally
	 <code>null</code>) to replace those existing for the role
	 * @param subtype the role's new subtype (optionally <code>null</code>)
	 * @param serviceContext the service context to be applied (optionally
	 <code>null</code>). Can set expando bridge attributes for the
	 role.
	 * @return the role with the primary key
	 */
	public static Role updateRole(
			String externalReferenceCode, long roleId, String name,
			Map<java.util.Locale, String> titleMap,
			Map<java.util.Locale, String> descriptionMap, String subtype,
			ServiceContext serviceContext)
		throws PortalException {

		return getService().updateRole(
			externalReferenceCode, roleId, name, titleMap, descriptionMap,
			subtype, serviceContext);
	}

	public static void validateName(String name) throws PortalException {
		getService().validateName(name);
	}

	public static RoleLocalService getService() {
		return _service;
	}

	public static void setService(RoleLocalService service) {
		_service = service;
	}

	private static volatile RoleLocalService _service;

}