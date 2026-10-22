/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.kernel.spring.osgi.OSGiBeanProperties;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for UserGroupRole. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see UserGroupRoleLocalServiceUtil
 * @generated
 */
@CTAware
@OSGiBeanProperties(
	property = {
		"model.class.name=com.liferay.portal.kernel.model.UserGroupRole"
	}
)
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface UserGroupRoleLocalService
	extends BaseLocalService, CTService<UserGroupRole>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portal.service.impl.UserGroupRoleLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the user group role local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link UserGroupRoleLocalServiceUtil} if injection and service tracking are not available.
	 */
	public UserGroupRole addUserGroupRole(
		long userId, long groupId, long roleId);

	/**
	 * Adds the user group role to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect UserGroupRoleLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param userGroupRole the user group role
	 * @return the user group role that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public UserGroupRole addUserGroupRole(UserGroupRole userGroupRole);

	public List<UserGroupRole> addUserGroupRoles(
		long userId, long groupId, long[] roleIds);

	public List<UserGroupRole> addUserGroupRoles(
		long[] userIds, long groupId, long roleId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Creates a new user group role with the primary key. Does not add the user group role to the database.
	 *
	 * @param userGroupRoleId the primary key for the new user group role
	 * @return the new user group role
	 */
	@Transactional(enabled = false)
	public UserGroupRole createUserGroupRole(long userGroupRoleId);

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	/**
	 * Deletes the user group role with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect UserGroupRoleLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param userGroupRoleId the primary key of the user group role
	 * @return the user group role that was removed
	 * @throws PortalException if a user group role with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public UserGroupRole deleteUserGroupRole(long userGroupRoleId)
		throws PortalException;

	/**
	 * Deletes the user group role from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect UserGroupRoleLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param userGroupRole the user group role
	 * @return the user group role that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public UserGroupRole deleteUserGroupRole(UserGroupRole userGroupRole);

	public void deleteUserGroupRoles(long groupId, int roleType);

	public void deleteUserGroupRoles(long userId, long groupId, long[] roleIds);

	public void deleteUserGroupRoles(long userId, long[] groupIds);

	public void deleteUserGroupRoles(long[] userIds, long groupId);

	public void deleteUserGroupRoles(
		long[] userIds, long groupId, int roleType);

	public void deleteUserGroupRoles(long[] userIds, long groupId, long roleId);

	public void deleteUserGroupRolesByGroupId(long groupId);

	public void deleteUserGroupRolesByRoleId(long roleId);

	public void deleteUserGroupRolesByUserId(long userId);

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserGroupRoleModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserGroupRoleModelImpl</code>.
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
	public UserGroupRole fetchUserGroupRole(long userGroupRoleId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public UserGroupRole fetchUserGroupRole(
		long userId, long groupId, long roleId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

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
	 * Returns the user group role with the primary key.
	 *
	 * @param userGroupRoleId the primary key of the user group role
	 * @return the user group role
	 * @throws PortalException if a user group role with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public UserGroupRole getUserGroupRole(long userGroupRoleId)
		throws PortalException;

	/**
	 * Returns a range of all the user group roles.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserGroupRoleModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of user group roles
	 * @param end the upper bound of the range of user group roles (not inclusive)
	 * @return the range of user group roles
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<UserGroupRole> getUserGroupRoles(int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<UserGroupRole> getUserGroupRoles(long userId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<UserGroupRole> getUserGroupRoles(long userId, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<UserGroupRole> getUserGroupRoles(
		long userId, long groupId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<UserGroupRole> getUserGroupRolesByGroup(long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<UserGroupRole> getUserGroupRolesByGroupAndRole(
		long groupId, long roleId);

	/**
	 * Returns the number of user group roles.
	 *
	 * @return the number of user group roles
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getUserGroupRolesCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getUserGroupRolesCount(long userId, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasUserGroupRole(long userId, long groupId, long roleId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasUserGroupRole(
		long userId, long groupId, long roleId, boolean inherit);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasUserGroupRole(long userId, long groupId, String roleName)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasUserGroupRole(
			long userId, long groupId, String roleName, boolean inherit)
		throws PortalException;

	/**
	 * Updates the user group role in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect UserGroupRoleLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param userGroupRole the user group role
	 * @return the user group role that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public UserGroupRole updateUserGroupRole(UserGroupRole userGroupRole);

	@Override
	@Transactional(enabled = false)
	public CTPersistence<UserGroupRole> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<UserGroupRole> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<UserGroupRole>, R, E>
				updateUnsafeFunction)
		throws E;

}
// LIFERAY-SERVICE-BUILDER-HASH:35246021