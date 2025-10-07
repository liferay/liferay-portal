/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the remote service interface for Role. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see RoleServiceUtil
 * @generated
 */
@AccessControlled
@CTAware
@JSONWebService
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface RoleService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portal.service.impl.RoleServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the role remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link RoleServiceUtil} if injection and service tracking are not available.
	 */
	public Role addRole(
			String externalReferenceCode, String className, long classPK,
			String name, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, int type, String subtype,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Adds the roles to the user. The user is reindexed after the roles are
	 * added.
	 *
	 * @param userId the primary key of the user
	 * @param roleIds the primary keys of the roles
	 */
	public void addUserRoles(long userId, long[] roleIds)
		throws PortalException;

	public Role copyRole(
			long userId, String name, long sourceRoleId,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Deletes the role with the primary key and its associated permissions.
	 *
	 * @param roleId the primary key of the role
	 */
	public void deleteRole(long roleId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Role fetchRole(long roleId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Role fetchRole(long companyId, String name) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Role fetchRoleByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException;

	/**
	 * Returns all the roles associated with the group.
	 *
	 * @param groupId the primary key of the group
	 * @return the roles associated with the group
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Role> getGroupRoles(long groupId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Role> getGroupRolesAndTeamRoles(
		long companyId, String name, List<String> excludedNames, String title,
		String description, int[] types, long excludedTeamRoleId,
		long teamGroupId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getGroupRolesAndTeamRolesCount(
		long companyId, String name, List<String> excludedNames, String title,
		String description, int[] types, long excludedTeamRoleId,
		long teamGroupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Role getOrAddEmptyRole(
			String externalReferenceCode, String className, long classPK,
			String name, int type)
		throws Exception;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	/**
	 * Returns the role with the primary key.
	 *
	 * @param roleId the primary key of the role
	 * @return the role with the primary key
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Role getRole(long roleId) throws PortalException;

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Role getRole(long companyId, String name) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Role getRoleByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Role> getRoles(int type, String subtype) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Role> getRoles(long companyId, int[] types)
		throws PortalException;

	/**
	 * Returns all the user's roles within the user group.
	 *
	 * @param userId the primary key of the user
	 * @param groupId the primary key of the group
	 * @return the user's roles within the user group
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Role> getUserGroupGroupRoles(long userId, long groupId)
		throws PortalException;

	/**
	 * Returns all the user's roles within the user group.
	 *
	 * @param userId the primary key of the user
	 * @param groupId the primary key of the group
	 * @return the user's roles within the user group
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Role> getUserGroupRoles(long userId, long groupId)
		throws PortalException;

	/**
	 * Returns the union of all the user's roles within the groups. If no
	 * groups are provided, only the user's directly assigned roles are
	 * returned.
	 *
	 * @param userId the primary key of the user
	 * @param groups the groups (optionally <code>null</code>)
	 * @return the union of all the user's roles within the groups
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Role> getUserRelatedRoles(long userId, List<Group> groups)
		throws PortalException;

	/**
	 * Returns all the roles associated with the user.
	 *
	 * @param userId the primary key of the user
	 * @return the roles associated with the user
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Role> getUserRoles(long userId) throws PortalException;

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasUserRole(
			long userId, long companyId, String name, boolean inherited)
		throws PortalException;

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasUserRoles(
			long userId, long companyId, String[] names, boolean inherited)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Role> search(
		long companyId, String keywords, Integer[] types,
		LinkedHashMap<String, Object> params, int start, int end,
		OrderByComparator<Role> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int searchCount(
		long companyId, String keywords, Integer[] types,
		LinkedHashMap<String, Object> params);

	/**
	 * Removes the matching roles associated with the user. The user is
	 * reindexed after the roles are removed.
	 *
	 * @param userId the primary key of the user
	 * @param roleIds the primary keys of the roles
	 */
	public void unsetUserRoles(long userId, long[] roleIds)
		throws PortalException;

	public Role updateExternalReferenceCode(
			long roleId, String externalReferenceCode)
		throws PortalException;

	public Role updateExternalReferenceCode(
			Role role, String externalReferenceCode)
		throws PortalException;

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
	public Role updateRole(
			String externalReferenceCode, long roleId, String name,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			String subtype, ServiceContext serviceContext)
		throws PortalException;

}