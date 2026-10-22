/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link UserGroupRoleLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see UserGroupRoleLocalService
 * @generated
 */
public class UserGroupRoleLocalServiceWrapper
	implements ServiceWrapper<UserGroupRoleLocalService>,
			   UserGroupRoleLocalService {

	public UserGroupRoleLocalServiceWrapper() {
		this(null);
	}

	public UserGroupRoleLocalServiceWrapper(
		UserGroupRoleLocalService userGroupRoleLocalService) {

		_userGroupRoleLocalService = userGroupRoleLocalService;
	}

	@Override
	public UserGroupRole addUserGroupRole(
		long userId, long groupId, long roleId) {

		return _userGroupRoleLocalService.addUserGroupRole(
			userId, groupId, roleId);
	}

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
	@Override
	public UserGroupRole addUserGroupRole(UserGroupRole userGroupRole) {
		return _userGroupRoleLocalService.addUserGroupRole(userGroupRole);
	}

	@Override
	public java.util.List<UserGroupRole> addUserGroupRoles(
		long userId, long groupId, long[] roleIds) {

		return _userGroupRoleLocalService.addUserGroupRoles(
			userId, groupId, roleIds);
	}

	@Override
	public java.util.List<UserGroupRole> addUserGroupRoles(
		long[] userIds, long groupId, long roleId) {

		return _userGroupRoleLocalService.addUserGroupRoles(
			userIds, groupId, roleId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userGroupRoleLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Creates a new user group role with the primary key. Does not add the user group role to the database.
	 *
	 * @param userGroupRoleId the primary key for the new user group role
	 * @return the new user group role
	 */
	@Override
	public UserGroupRole createUserGroupRole(long userGroupRoleId) {
		return _userGroupRoleLocalService.createUserGroupRole(userGroupRoleId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userGroupRoleLocalService.deletePersistedModel(persistedModel);
	}

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
	@Override
	public UserGroupRole deleteUserGroupRole(long userGroupRoleId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userGroupRoleLocalService.deleteUserGroupRole(userGroupRoleId);
	}

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
	@Override
	public UserGroupRole deleteUserGroupRole(UserGroupRole userGroupRole) {
		return _userGroupRoleLocalService.deleteUserGroupRole(userGroupRole);
	}

	@Override
	public void deleteUserGroupRoles(long groupId, int roleType) {
		_userGroupRoleLocalService.deleteUserGroupRoles(groupId, roleType);
	}

	@Override
	public void deleteUserGroupRoles(
		long userId, long groupId, long[] roleIds) {

		_userGroupRoleLocalService.deleteUserGroupRoles(
			userId, groupId, roleIds);
	}

	@Override
	public void deleteUserGroupRoles(long userId, long[] groupIds) {
		_userGroupRoleLocalService.deleteUserGroupRoles(userId, groupIds);
	}

	@Override
	public void deleteUserGroupRoles(long[] userIds, long groupId) {
		_userGroupRoleLocalService.deleteUserGroupRoles(userIds, groupId);
	}

	@Override
	public void deleteUserGroupRoles(
		long[] userIds, long groupId, int roleType) {

		_userGroupRoleLocalService.deleteUserGroupRoles(
			userIds, groupId, roleType);
	}

	@Override
	public void deleteUserGroupRoles(
		long[] userIds, long groupId, long roleId) {

		_userGroupRoleLocalService.deleteUserGroupRoles(
			userIds, groupId, roleId);
	}

	@Override
	public void deleteUserGroupRolesByGroupId(long groupId) {
		_userGroupRoleLocalService.deleteUserGroupRolesByGroupId(groupId);
	}

	@Override
	public void deleteUserGroupRolesByRoleId(long roleId) {
		_userGroupRoleLocalService.deleteUserGroupRolesByRoleId(roleId);
	}

	@Override
	public void deleteUserGroupRolesByUserId(long userId) {
		_userGroupRoleLocalService.deleteUserGroupRolesByUserId(userId);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _userGroupRoleLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _userGroupRoleLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _userGroupRoleLocalService.dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _userGroupRoleLocalService.dynamicQuery(dynamicQuery);
	}

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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _userGroupRoleLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _userGroupRoleLocalService.dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _userGroupRoleLocalService.dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return _userGroupRoleLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public UserGroupRole fetchUserGroupRole(long userGroupRoleId) {
		return _userGroupRoleLocalService.fetchUserGroupRole(userGroupRoleId);
	}

	@Override
	public UserGroupRole fetchUserGroupRole(
		long userId, long groupId, long roleId) {

		return _userGroupRoleLocalService.fetchUserGroupRole(
			userId, groupId, roleId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _userGroupRoleLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _userGroupRoleLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _userGroupRoleLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userGroupRoleLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns the user group role with the primary key.
	 *
	 * @param userGroupRoleId the primary key of the user group role
	 * @return the user group role
	 * @throws PortalException if a user group role with the primary key could not be found
	 */
	@Override
	public UserGroupRole getUserGroupRole(long userGroupRoleId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userGroupRoleLocalService.getUserGroupRole(userGroupRoleId);
	}

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
	@Override
	public java.util.List<UserGroupRole> getUserGroupRoles(int start, int end) {
		return _userGroupRoleLocalService.getUserGroupRoles(start, end);
	}

	@Override
	public java.util.List<UserGroupRole> getUserGroupRoles(long userId) {
		return _userGroupRoleLocalService.getUserGroupRoles(userId);
	}

	@Override
	public java.util.List<UserGroupRole> getUserGroupRoles(
		long userId, long groupId) {

		return _userGroupRoleLocalService.getUserGroupRoles(userId, groupId);
	}

	@Override
	public java.util.List<UserGroupRole> getUserGroupRoles(
		long userId, long groupId, int start, int end) {

		return _userGroupRoleLocalService.getUserGroupRoles(
			userId, groupId, start, end);
	}

	@Override
	public java.util.List<UserGroupRole> getUserGroupRolesByGroup(
		long groupId) {

		return _userGroupRoleLocalService.getUserGroupRolesByGroup(groupId);
	}

	@Override
	public java.util.List<UserGroupRole> getUserGroupRolesByGroupAndRole(
		long groupId, long roleId) {

		return _userGroupRoleLocalService.getUserGroupRolesByGroupAndRole(
			groupId, roleId);
	}

	/**
	 * Returns the number of user group roles.
	 *
	 * @return the number of user group roles
	 */
	@Override
	public int getUserGroupRolesCount() {
		return _userGroupRoleLocalService.getUserGroupRolesCount();
	}

	@Override
	public int getUserGroupRolesCount(long userId, long groupId) {
		return _userGroupRoleLocalService.getUserGroupRolesCount(
			userId, groupId);
	}

	@Override
	public boolean hasUserGroupRole(long userId, long groupId, long roleId) {
		return _userGroupRoleLocalService.hasUserGroupRole(
			userId, groupId, roleId);
	}

	@Override
	public boolean hasUserGroupRole(
		long userId, long groupId, long roleId, boolean inherit) {

		return _userGroupRoleLocalService.hasUserGroupRole(
			userId, groupId, roleId, inherit);
	}

	@Override
	public boolean hasUserGroupRole(long userId, long groupId, String roleName)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userGroupRoleLocalService.hasUserGroupRole(
			userId, groupId, roleName);
	}

	@Override
	public boolean hasUserGroupRole(
			long userId, long groupId, String roleName, boolean inherit)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userGroupRoleLocalService.hasUserGroupRole(
			userId, groupId, roleName, inherit);
	}

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
	@Override
	public UserGroupRole updateUserGroupRole(UserGroupRole userGroupRole) {
		return _userGroupRoleLocalService.updateUserGroupRole(userGroupRole);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _userGroupRoleLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<UserGroupRole> getCTPersistence() {
		return _userGroupRoleLocalService.getCTPersistence();
	}

	@Override
	public Class<UserGroupRole> getModelClass() {
		return _userGroupRoleLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<UserGroupRole>, R, E>
				updateUnsafeFunction)
		throws E {

		return _userGroupRoleLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public UserGroupRoleLocalService getWrappedService() {
		return _userGroupRoleLocalService;
	}

	@Override
	public void setWrappedService(
		UserGroupRoleLocalService userGroupRoleLocalService) {

		_userGroupRoleLocalService = userGroupRoleLocalService;
	}

	private UserGroupRoleLocalService _userGroupRoleLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:230443407