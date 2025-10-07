/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.cache.thread.local.ThreadLocalCachable;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.Team;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.kernel.spring.osgi.OSGiBeanProperties;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for Role. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see RoleLocalServiceUtil
 * @generated
 */
@CTAware
@OSGiBeanProperties(
	property = {"model.class.name=com.liferay.portal.kernel.model.Role"}
)
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface RoleLocalService
	extends BaseLocalService, CTService<Role>, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portal.service.impl.RoleLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the role local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link RoleLocalServiceUtil} if injection and service tracking are not available.
	 */
	public boolean addGroupRole(long groupId, long roleId);

	public boolean addGroupRole(long groupId, Role role);

	public boolean addGroupRoles(long groupId, List<Role> roles);

	public boolean addGroupRoles(long groupId, long[] roleIds);

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
	@Indexable(type = IndexableType.REINDEX)
	public Role addRole(Role role);

	@Indexable(type = IndexableType.REINDEX)
	public Role addRole(
			String externalReferenceCode, long userId, String className,
			long classPK, String name, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, int type, String subtype,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * @throws PortalException
	 */
	public boolean addUserRole(long userId, long roleId) throws PortalException;

	/**
	 * @throws PortalException
	 */
	public boolean addUserRole(long userId, Role role) throws PortalException;

	/**
	 * @throws PortalException
	 */
	public boolean addUserRoles(long userId, List<Role> roles)
		throws PortalException;

	/**
	 * @throws PortalException
	 */
	public boolean addUserRoles(long userId, long[] roleIds)
		throws PortalException;

	/**
	 * Checks to ensure that the system roles map has appropriate default roles
	 * in each company.
	 */
	public void checkSystemRoles() throws PortalException;

	/**
	 * Checks to ensure that the system roles map has appropriate default roles
	 * in the company.
	 *
	 * @param companyId the primary key of the company
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public void checkSystemRoles(long companyId) throws PortalException;

	public void clearGroupRoles(long groupId);

	/**
	 * @throws PortalException
	 */
	public void clearUserRoles(long userId) throws PortalException;

	public Role copyRole(
			long userId, String name, long sourceRoleId,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Creates a new role with the primary key. Does not add the role to the database.
	 *
	 * @param roleId the primary key for the new role
	 * @return the new role
	 */
	@Transactional(enabled = false)
	public Role createRole(long roleId);

	public void deleteGroupRole(long groupId, long roleId);

	public void deleteGroupRole(long groupId, Role role);

	public void deleteGroupRoles(long groupId, List<Role> roles);

	public void deleteGroupRoles(long groupId, long[] roleIds);

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

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
	@Indexable(type = IndexableType.DELETE)
	public Role deleteRole(long roleId) throws PortalException;

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
	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public Role deleteRole(Role role) throws PortalException;

	/**
	 * @throws PortalException
	 */
	public void deleteUserRole(long userId, long roleId) throws PortalException;

	/**
	 * @throws PortalException
	 */
	public void deleteUserRole(long userId, Role role) throws PortalException;

	/**
	 * @throws PortalException
	 */
	public void deleteUserRoles(long userId, List<Role> roles)
		throws PortalException;

	/**
	 * @throws PortalException
	 */
	public void deleteUserRoles(long userId, long[] roleIds)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> T dslQuery(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int dslQueryCount(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DynamicQuery dynamicQuery();

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(DynamicQuery dynamicQuery);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(DynamicQuery dynamicQuery);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(
		DynamicQuery dynamicQuery, Projection projection);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Role fetchRole(long roleId);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Role fetchRole(long companyId, String name);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Role fetchRoleByExternalReferenceCode(
		String externalReferenceCode, long companyId);

	/**
	 * Returns the role with the matching UUID and company.
	 *
	 * @param uuid the role's UUID
	 * @param companyId the primary key of the company
	 * @return the matching role, or <code>null</code> if a matching role could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Role fetchRoleByUuidAndCompanyId(String uuid, long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getAssigneesTotal(long roleId) throws PortalException;

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Role getDefaultGroupRole(long groupId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	/**
	 * Returns the groupIds of the groups associated with the role.
	 *
	 * @param roleId the roleId of the role
	 * @return long[] the groupIds of groups associated with the role
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long[] getGroupPrimaryKeys(long roleId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Role> getGroupRelatedRoles(long groupId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Role> getGroupRoles(long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Role> getGroupRoles(long groupId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Role> getGroupRoles(
		long groupId, int start, int end,
		OrderByComparator<Role> orderByComparator);

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
	public int getGroupRolesCount(long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Role getOrAddEmptyRole(
			String externalReferenceCode, long companyId, long userId,
			String className, long classPK, String name, int type)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	/**
	 * @throws PortalException
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Map<String, List<String>> getResourceRoles(
		long companyId, String name, int scope, String primKey);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Role> getResourceRoles(
		long companyId, String name, int scope, String primKey,
		String actionId);

	/**
	 * Returns the role with the primary key.
	 *
	 * @param roleId the primary key of the role
	 * @return the role
	 * @throws PortalException if a role with the primary key could not be found
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

	/**
	 * Returns the role with the matching UUID and company.
	 *
	 * @param uuid the role's UUID
	 * @param companyId the primary key of the company
	 * @return the matching role
	 * @throws PortalException if a matching role could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Role getRoleByUuidAndCompanyId(String uuid, long companyId)
		throws PortalException;

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Role> getRoles(int start, int end);

	/**
	 * Returns all the roles of the type and subtype.
	 *
	 * @param type the role's type (optionally <code>0</code>)
	 * @param subtype the role's subtype (optionally <code>null</code>)
	 * @return the roles of the type and subtype
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Role> getRoles(int type, String subtype);

	/**
	 * Returns all the roles in the company.
	 *
	 * @param companyId the primary key of the company
	 * @return the roles in the company
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Role> getRoles(long companyId);

	/**
	 * Returns all the roles with the types.
	 *
	 * @param companyId the primary key of the company
	 * @param types the role types (optionally <code>null</code>)
	 * @return the roles with the types
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Role> getRoles(long companyId, int[] types);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Role> getRoles(
		long companyId, long classNameId, long[] classPKs, int type);

	/**
	 * Returns all the roles with the primary keys.
	 *
	 * @param roleIds the primary keys of the roles
	 * @return the roles with the primary keys
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Role> getRoles(long[] roleIds) throws PortalException;

	/**
	 * Returns the number of roles.
	 *
	 * @return the number of roles
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getRolesCount();

	/**
	 * Returns all the roles of the subtype.
	 *
	 * @param subtype the role's subtype (optionally <code>null</code>)
	 * @return the roles of the subtype
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Role> getSubtypeRoles(String subtype);

	/**
	 * Returns the number of roles of the subtype.
	 *
	 * @param subtype the role's subtype (optionally <code>null</code>)
	 * @return the number of roles of the subtype
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getSubtypeRolesCount(String subtype);

	/**
	 * Returns the team role in the company.
	 *
	 * @param companyId the primary key of the company
	 * @param teamId the primary key of the team
	 * @return the team role in the company
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Role getTeamRole(long companyId, long teamId) throws PortalException;

	/**
	 * Returns the team role map for the group.
	 *
	 * @param groupId the primary key of the group
	 * @return the team role map for the group
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Map<Team, Role> getTeamRoleMap(long groupId) throws PortalException;

	/**
	 * Returns the team roles in the group.
	 *
	 * @param groupId the primary key of the group
	 * @return the team roles in the group
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Role> getTeamRoles(long groupId) throws PortalException;

	/**
	 * Returns the team roles in the group, excluding the specified role IDs.
	 *
	 * @param groupId the primary key of the group
	 * @param excludedRoleIds the primary keys of the roles to exclude
	 (optionally <code>null</code>)
	 * @return the team roles in the group, excluding the specified role IDs
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Role> getTeamRoles(long groupId, long[] excludedRoleIds)
		throws PortalException;

	/**
	 * Returns the team roles in the company.
	 *
	 * @param companyId the primary key of the company
	 * @param teamIds the primary keys of the teams
	 * @return the team roles in the company
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Role> getTeamsRoles(long companyId, long[] teamIds)
		throws PortalException;

	/**
	 * Returns all the roles of the type.
	 *
	 * @param type the role's type (optionally <code>0</code>)
	 * @return the range of the roles of the type
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Role> getTypeRoles(int type);

	/**
	 * Returns a range of all the roles of the type.
	 *
	 * @param type the role's type (optionally <code>0</code>)
	 * @param start the lower bound of the range of roles to return
	 * @param end the upper bound of the range of roles to return (not
	 inclusive)
	 * @return the range of the roles of the type
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Role> getTypeRoles(int type, int start, int end);

	/**
	 * Returns the number of roles of the type.
	 *
	 * @param type the role's type (optionally <code>0</code>)
	 * @return the number of roles of the type
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getTypeRolesCount(int type);

	/**
	 * Returns all the user's roles within the user group.
	 *
	 * @param userId the primary key of the user
	 * @param groupId the primary key of the group
	 * @return the user's roles within the user group
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Role> getUserGroupGroupRoles(long userId, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Role> getUserGroupGroupRoles(
		long userId, long groupId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getUserGroupGroupRolesCount(long userId, long groupId);

	/**
	 * Returns all the user's roles within the user group.
	 *
	 * @param userId the primary key of the user
	 * @param groupId the primary key of the group
	 * @return the user's roles within the user group
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Role> getUserGroupRoles(long userId, long groupId);

	/**
	 * Returns the userIds of the users associated with the role.
	 *
	 * @param roleId the roleId of the role
	 * @return long[] the userIds of users associated with the role
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long[] getUserPrimaryKeys(long roleId);

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
	public List<Role> getUserRelatedRoles(long userId, List<Group> groups);

	/**
	 * Returns all the user's roles within the group.
	 *
	 * @param userId the primary key of the user
	 * @param groupId the primary key of the group
	 * @return the user's roles within the group
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Role> getUserRelatedRoles(long userId, long groupId);

	/**
	 * Returns the union of all the user's roles within the groups. If no
	 * groupIds are provided, only the user's directly assigned roles are
	 * returned.
	 *
	 * @param userId the primary key of the user
	 * @param groupIds the primary keys of the groups
	 * @return the union of all the user's roles within the groups
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Role> getUserRelatedRoles(long userId, long[] groupIds);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Role> getUserRoles(long userId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Role> getUserRoles(long userId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Role> getUserRoles(
		long userId, int start, int end,
		OrderByComparator<Role> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getUserRolesCount(long userId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Role> getUserTeamRoles(long userId, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasGroupRole(long groupId, long roleId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasGroupRoles(long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasUserRole(long userId, long roleId);

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
	@ThreadLocalCachable
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasUserRole(
			long userId, long companyId, String name, boolean inherited)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasUserRoles(long userId);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Role> search(
		long companyId, String keywords, Integer[] types, int start, int end,
		OrderByComparator<Role> orderByComparator);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Role> search(
		long companyId, String keywords, Integer[] types,
		LinkedHashMap<String, Object> params, int start, int end,
		OrderByComparator<Role> orderByComparator);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Role> search(
		long companyId, String name, String description, Integer[] types,
		int start, int end, OrderByComparator<Role> orderByComparator);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Role> search(
		long companyId, String name, String description, Integer[] types,
		LinkedHashMap<String, Object> params, int start, int end,
		OrderByComparator<Role> orderByComparator);

	/**
	 * Returns the number of roles that match the keywords and types.
	 *
	 * @param companyId the primary key of the company
	 * @param keywords the keywords (space separated), which may occur in the
	 role's name or description (optionally <code>null</code>)
	 * @param types the role types (optionally <code>null</code>)
	 * @return the number of matching roles
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int searchCount(long companyId, String keywords, Integer[] types);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int searchCount(
		long companyId, String keywords, Integer[] types,
		LinkedHashMap<String, Object> params);

	/**
	 * Returns the number of roles that match the name, description, and types.
	 *
	 * @param companyId the primary key of the company
	 * @param name the role's name (optionally <code>null</code>)
	 * @param description the role's description (optionally <code>null</code>)
	 * @param types the role types (optionally <code>null</code>)
	 * @return the number of matching roles
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int searchCount(
		long companyId, String name, String description, Integer[] types);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int searchCount(
		long companyId, String name, String description, Integer[] types,
		LinkedHashMap<String, Object> params);

	public void setGroupRoles(long groupId, long[] roleIds);

	/**
	 * @throws PortalException
	 */
	public void setUserRoles(long userId, long[] roleIds)
		throws PortalException;

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
	 * Updates the role in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RoleLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param role the role
	 * @return the role that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public Role updateRole(Role role);

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
	@Indexable(type = IndexableType.REINDEX)
	public Role updateRole(
			String externalReferenceCode, long roleId, String name,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			String subtype, ServiceContext serviceContext)
		throws PortalException;

	public void validateName(String name) throws PortalException;

	@Override
	@Transactional(enabled = false)
	public CTPersistence<Role> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<Role> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<Role>, R, E> updateUnsafeFunction)
		throws E;

}