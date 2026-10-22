/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for UserGroupRole. This utility wraps
 * <code>com.liferay.portal.service.impl.UserGroupRoleLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see UserGroupRoleLocalService
 * @generated
 */
public class UserGroupRoleLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.service.impl.UserGroupRoleLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static UserGroupRole addUserGroupRole(
		long userId, long groupId, long roleId) {

		return getService().addUserGroupRole(userId, groupId, roleId);
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
	public static UserGroupRole addUserGroupRole(UserGroupRole userGroupRole) {
		return getService().addUserGroupRole(userGroupRole);
	}

	public static List<UserGroupRole> addUserGroupRoles(
		long userId, long groupId, long[] roleIds) {

		return getService().addUserGroupRoles(userId, groupId, roleIds);
	}

	public static List<UserGroupRole> addUserGroupRoles(
		long[] userIds, long groupId, long roleId) {

		return getService().addUserGroupRoles(userIds, groupId, roleId);
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
	 * Creates a new user group role with the primary key. Does not add the user group role to the database.
	 *
	 * @param userGroupRoleId the primary key for the new user group role
	 * @return the new user group role
	 */
	public static UserGroupRole createUserGroupRole(long userGroupRoleId) {
		return getService().createUserGroupRole(userGroupRoleId);
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
	public static UserGroupRole deleteUserGroupRole(long userGroupRoleId)
		throws PortalException {

		return getService().deleteUserGroupRole(userGroupRoleId);
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
	public static UserGroupRole deleteUserGroupRole(
		UserGroupRole userGroupRole) {

		return getService().deleteUserGroupRole(userGroupRole);
	}

	public static void deleteUserGroupRoles(long groupId, int roleType) {
		getService().deleteUserGroupRoles(groupId, roleType);
	}

	public static void deleteUserGroupRoles(
		long userId, long groupId, long[] roleIds) {

		getService().deleteUserGroupRoles(userId, groupId, roleIds);
	}

	public static void deleteUserGroupRoles(long userId, long[] groupIds) {
		getService().deleteUserGroupRoles(userId, groupIds);
	}

	public static void deleteUserGroupRoles(long[] userIds, long groupId) {
		getService().deleteUserGroupRoles(userIds, groupId);
	}

	public static void deleteUserGroupRoles(
		long[] userIds, long groupId, int roleType) {

		getService().deleteUserGroupRoles(userIds, groupId, roleType);
	}

	public static void deleteUserGroupRoles(
		long[] userIds, long groupId, long roleId) {

		getService().deleteUserGroupRoles(userIds, groupId, roleId);
	}

	public static void deleteUserGroupRolesByGroupId(long groupId) {
		getService().deleteUserGroupRolesByGroupId(groupId);
	}

	public static void deleteUserGroupRolesByRoleId(long roleId) {
		getService().deleteUserGroupRolesByRoleId(roleId);
	}

	public static void deleteUserGroupRolesByUserId(long userId) {
		getService().deleteUserGroupRolesByUserId(userId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserGroupRoleModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserGroupRoleModelImpl</code>.
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

	public static UserGroupRole fetchUserGroupRole(long userGroupRoleId) {
		return getService().fetchUserGroupRole(userGroupRoleId);
	}

	public static UserGroupRole fetchUserGroupRole(
		long userId, long groupId, long roleId) {

		return getService().fetchUserGroupRole(userId, groupId, roleId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
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
	 * Returns the user group role with the primary key.
	 *
	 * @param userGroupRoleId the primary key of the user group role
	 * @return the user group role
	 * @throws PortalException if a user group role with the primary key could not be found
	 */
	public static UserGroupRole getUserGroupRole(long userGroupRoleId)
		throws PortalException {

		return getService().getUserGroupRole(userGroupRoleId);
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
	public static List<UserGroupRole> getUserGroupRoles(int start, int end) {
		return getService().getUserGroupRoles(start, end);
	}

	public static List<UserGroupRole> getUserGroupRoles(long userId) {
		return getService().getUserGroupRoles(userId);
	}

	public static List<UserGroupRole> getUserGroupRoles(
		long userId, long groupId) {

		return getService().getUserGroupRoles(userId, groupId);
	}

	public static List<UserGroupRole> getUserGroupRoles(
		long userId, long groupId, int start, int end) {

		return getService().getUserGroupRoles(userId, groupId, start, end);
	}

	public static List<UserGroupRole> getUserGroupRolesByGroup(long groupId) {
		return getService().getUserGroupRolesByGroup(groupId);
	}

	public static List<UserGroupRole> getUserGroupRolesByGroupAndRole(
		long groupId, long roleId) {

		return getService().getUserGroupRolesByGroupAndRole(groupId, roleId);
	}

	/**
	 * Returns the number of user group roles.
	 *
	 * @return the number of user group roles
	 */
	public static int getUserGroupRolesCount() {
		return getService().getUserGroupRolesCount();
	}

	public static int getUserGroupRolesCount(long userId, long groupId) {
		return getService().getUserGroupRolesCount(userId, groupId);
	}

	public static boolean hasUserGroupRole(
		long userId, long groupId, long roleId) {

		return getService().hasUserGroupRole(userId, groupId, roleId);
	}

	public static boolean hasUserGroupRole(
		long userId, long groupId, long roleId, boolean inherit) {

		return getService().hasUserGroupRole(userId, groupId, roleId, inherit);
	}

	public static boolean hasUserGroupRole(
			long userId, long groupId, String roleName)
		throws PortalException {

		return getService().hasUserGroupRole(userId, groupId, roleName);
	}

	public static boolean hasUserGroupRole(
			long userId, long groupId, String roleName, boolean inherit)
		throws PortalException {

		return getService().hasUserGroupRole(
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
	public static UserGroupRole updateUserGroupRole(
		UserGroupRole userGroupRole) {

		return getService().updateUserGroupRole(userGroupRole);
	}

	public static UserGroupRoleLocalService getService() {
		return _service;
	}

	public static void setService(UserGroupRoleLocalService service) {
		_service = service;
	}

	private static volatile UserGroupRoleLocalService _service;

}
// LIFERAY-SERVICE-BUILDER-HASH:758510539