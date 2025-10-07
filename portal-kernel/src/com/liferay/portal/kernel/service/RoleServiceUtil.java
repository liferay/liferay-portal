/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;
import java.util.Map;

/**
 * Provides the remote service utility for Role. This utility wraps
 * <code>com.liferay.portal.service.impl.RoleServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see RoleService
 * @generated
 */
public class RoleServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.service.impl.RoleServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static Role addRole(
			String externalReferenceCode, String className, long classPK,
			String name, Map<java.util.Locale, String> titleMap,
			Map<java.util.Locale, String> descriptionMap, int type,
			String subtype, ServiceContext serviceContext)
		throws PortalException {

		return getService().addRole(
			externalReferenceCode, className, classPK, name, titleMap,
			descriptionMap, type, subtype, serviceContext);
	}

	/**
	 * Adds the roles to the user. The user is reindexed after the roles are
	 * added.
	 *
	 * @param userId the primary key of the user
	 * @param roleIds the primary keys of the roles
	 */
	public static void addUserRoles(long userId, long[] roleIds)
		throws PortalException {

		getService().addUserRoles(userId, roleIds);
	}

	public static Role copyRole(
			long userId, String name, long sourceRoleId,
			ServiceContext serviceContext)
		throws PortalException {

		return getService().copyRole(
			userId, name, sourceRoleId, serviceContext);
	}

	/**
	 * Deletes the role with the primary key and its associated permissions.
	 *
	 * @param roleId the primary key of the role
	 */
	public static void deleteRole(long roleId) throws PortalException {
		getService().deleteRole(roleId);
	}

	public static Role fetchRole(long roleId) throws PortalException {
		return getService().fetchRole(roleId);
	}

	public static Role fetchRole(long companyId, String name)
		throws PortalException {

		return getService().fetchRole(companyId, name);
	}

	public static Role fetchRoleByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().fetchRoleByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns all the roles associated with the group.
	 *
	 * @param groupId the primary key of the group
	 * @return the roles associated with the group
	 */
	public static List<Role> getGroupRoles(long groupId)
		throws PortalException {

		return getService().getGroupRoles(groupId);
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

	public static Role getOrAddEmptyRole(
			String externalReferenceCode, String className, long classPK,
			String name, int type)
		throws Exception {

		return getService().getOrAddEmptyRole(
			externalReferenceCode, className, classPK, name, type);
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
	 * Returns the role with the primary key.
	 *
	 * @param roleId the primary key of the role
	 * @return the role with the primary key
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

	public static List<Role> getRoles(int type, String subtype)
		throws PortalException {

		return getService().getRoles(type, subtype);
	}

	public static List<Role> getRoles(long companyId, int[] types)
		throws PortalException {

		return getService().getRoles(companyId, types);
	}

	/**
	 * Returns all the user's roles within the user group.
	 *
	 * @param userId the primary key of the user
	 * @param groupId the primary key of the group
	 * @return the user's roles within the user group
	 */
	public static List<Role> getUserGroupGroupRoles(long userId, long groupId)
		throws PortalException {

		return getService().getUserGroupGroupRoles(userId, groupId);
	}

	/**
	 * Returns all the user's roles within the user group.
	 *
	 * @param userId the primary key of the user
	 * @param groupId the primary key of the group
	 * @return the user's roles within the user group
	 */
	public static List<Role> getUserGroupRoles(long userId, long groupId)
		throws PortalException {

		return getService().getUserGroupRoles(userId, groupId);
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
			long userId, List<com.liferay.portal.kernel.model.Group> groups)
		throws PortalException {

		return getService().getUserRelatedRoles(userId, groups);
	}

	/**
	 * Returns all the roles associated with the user.
	 *
	 * @param userId the primary key of the user
	 * @return the roles associated with the user
	 */
	public static List<Role> getUserRoles(long userId) throws PortalException {
		return getService().getUserRoles(userId);
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

	public static List<Role> search(
		long companyId, String keywords, Integer[] types,
		java.util.LinkedHashMap<String, Object> params, int start, int end,
		OrderByComparator<Role> orderByComparator) {

		return getService().search(
			companyId, keywords, types, params, start, end, orderByComparator);
	}

	public static int searchCount(
		long companyId, String keywords, Integer[] types,
		java.util.LinkedHashMap<String, Object> params) {

		return getService().searchCount(companyId, keywords, types, params);
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
	 <code>null</code>). Can set the expando bridge attributes for the
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

	public static RoleService getService() {
		return _service;
	}

	public static void setService(RoleService service) {
		_service = service;
	}

	private static volatile RoleService _service;

}