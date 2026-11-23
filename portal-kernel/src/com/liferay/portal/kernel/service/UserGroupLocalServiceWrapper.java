/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link UserGroupLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see UserGroupLocalService
 * @generated
 */
public class UserGroupLocalServiceWrapper
	implements ServiceWrapper<UserGroupLocalService>, UserGroupLocalService {

	public UserGroupLocalServiceWrapper() {
		this(null);
	}

	public UserGroupLocalServiceWrapper(
		UserGroupLocalService userGroupLocalService) {

		_userGroupLocalService = userGroupLocalService;
	}

	@Override
	public boolean addGroupUserGroup(long groupId, long userGroupId) {
		return _userGroupLocalService.addGroupUserGroup(groupId, userGroupId);
	}

	@Override
	public boolean addGroupUserGroup(long groupId, UserGroup userGroup) {
		return _userGroupLocalService.addGroupUserGroup(groupId, userGroup);
	}

	@Override
	public boolean addGroupUserGroups(
		long groupId, java.util.List<UserGroup> userGroups) {

		return _userGroupLocalService.addGroupUserGroups(groupId, userGroups);
	}

	@Override
	public boolean addGroupUserGroups(long groupId, long[] userGroupIds) {
		return _userGroupLocalService.addGroupUserGroups(groupId, userGroupIds);
	}

	@Override
	public UserGroup addOrUpdateUserGroup(
			String externalReferenceCode, long userId, long companyId,
			String name, String description, ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userGroupLocalService.addOrUpdateUserGroup(
			externalReferenceCode, userId, companyId, name, description,
			serviceContext);
	}

	@Override
	public boolean addTeamUserGroup(long teamId, long userGroupId) {
		return _userGroupLocalService.addTeamUserGroup(teamId, userGroupId);
	}

	@Override
	public boolean addTeamUserGroup(long teamId, UserGroup userGroup) {
		return _userGroupLocalService.addTeamUserGroup(teamId, userGroup);
	}

	@Override
	public boolean addTeamUserGroups(
		long teamId, java.util.List<UserGroup> userGroups) {

		return _userGroupLocalService.addTeamUserGroups(teamId, userGroups);
	}

	@Override
	public boolean addTeamUserGroups(long teamId, long[] userGroupIds) {
		return _userGroupLocalService.addTeamUserGroups(teamId, userGroupIds);
	}

	/**
	 * Adds a user group.
	 *
	 * <p>
	 * This method handles the creation and bookkeeping of the user group,
	 * including its resources, metadata, and internal data structures. It is
	 * not necessary to make subsequent calls to setup default groups and
	 * resources for the user group.
	 * </p>
	 *
	 * @param userId the primary key of the user
	 * @param companyId the primary key of the user group's company
	 * @param name the user group's name
	 * @param description the user group's description
	 * @param serviceContext the service context to be applied (optionally
	 <code>null</code>). Can set expando bridge attributes for the
	 user group.
	 * @return the user group
	 */
	@Override
	public UserGroup addUserGroup(
			String externalReferenceCode, long userId, long companyId,
			String name, String description, ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userGroupLocalService.addUserGroup(
			externalReferenceCode, userId, companyId, name, description,
			serviceContext);
	}

	/**
	 * Adds the user group to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect UserGroupLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param userGroup the user group
	 * @return the user group that was added
	 */
	@Override
	public UserGroup addUserGroup(UserGroup userGroup) {
		return _userGroupLocalService.addUserGroup(userGroup);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public boolean addUserUserGroup(long userId, long userGroupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userGroupLocalService.addUserUserGroup(userId, userGroupId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public boolean addUserUserGroup(long userId, UserGroup userGroup)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userGroupLocalService.addUserUserGroup(userId, userGroup);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public boolean addUserUserGroups(
			long userId, java.util.List<UserGroup> userGroups)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userGroupLocalService.addUserUserGroups(userId, userGroups);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public boolean addUserUserGroups(long userId, long[] userGroupIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userGroupLocalService.addUserUserGroups(userId, userGroupIds);
	}

	@Override
	public void clearGroupUserGroups(long groupId) {
		_userGroupLocalService.clearGroupUserGroups(groupId);
	}

	@Override
	public void clearTeamUserGroups(long teamId) {
		_userGroupLocalService.clearTeamUserGroups(teamId);
	}

	@Override
	public void clearUserUserGroups(long userId) {
		_userGroupLocalService.clearUserUserGroups(userId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userGroupLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Creates a new user group with the primary key. Does not add the user group to the database.
	 *
	 * @param userGroupId the primary key for the new user group
	 * @return the new user group
	 */
	@Override
	public UserGroup createUserGroup(long userGroupId) {
		return _userGroupLocalService.createUserGroup(userGroupId);
	}

	@Override
	public void deleteGroupUserGroup(long groupId, long userGroupId) {
		_userGroupLocalService.deleteGroupUserGroup(groupId, userGroupId);
	}

	@Override
	public void deleteGroupUserGroup(long groupId, UserGroup userGroup) {
		_userGroupLocalService.deleteGroupUserGroup(groupId, userGroup);
	}

	@Override
	public void deleteGroupUserGroups(
		long groupId, java.util.List<UserGroup> userGroups) {

		_userGroupLocalService.deleteGroupUserGroups(groupId, userGroups);
	}

	@Override
	public void deleteGroupUserGroups(long groupId, long[] userGroupIds) {
		_userGroupLocalService.deleteGroupUserGroups(groupId, userGroupIds);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userGroupLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public void deleteTeamUserGroup(long teamId, long userGroupId) {
		_userGroupLocalService.deleteTeamUserGroup(teamId, userGroupId);
	}

	@Override
	public void deleteTeamUserGroup(long teamId, UserGroup userGroup) {
		_userGroupLocalService.deleteTeamUserGroup(teamId, userGroup);
	}

	@Override
	public void deleteTeamUserGroups(
		long teamId, java.util.List<UserGroup> userGroups) {

		_userGroupLocalService.deleteTeamUserGroups(teamId, userGroups);
	}

	@Override
	public void deleteTeamUserGroups(long teamId, long[] userGroupIds) {
		_userGroupLocalService.deleteTeamUserGroups(teamId, userGroupIds);
	}

	/**
	 * Deletes the user group with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect UserGroupLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param userGroupId the primary key of the user group
	 * @return the user group that was removed
	 * @throws PortalException if a user group with the primary key could not be found
	 */
	@Override
	public UserGroup deleteUserGroup(long userGroupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userGroupLocalService.deleteUserGroup(userGroupId);
	}

	/**
	 * Deletes the user group from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect UserGroupLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param userGroup the user group
	 * @return the user group that was removed
	 * @throws PortalException
	 */
	@Override
	public UserGroup deleteUserGroup(UserGroup userGroup)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userGroupLocalService.deleteUserGroup(userGroup);
	}

	@Override
	public void deleteUserGroups(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_userGroupLocalService.deleteUserGroups(companyId);
	}

	@Override
	public void deleteUserUserGroup(long userId, long userGroupId) {
		_userGroupLocalService.deleteUserUserGroup(userId, userGroupId);
	}

	@Override
	public void deleteUserUserGroup(long userId, UserGroup userGroup) {
		_userGroupLocalService.deleteUserUserGroup(userId, userGroup);
	}

	@Override
	public void deleteUserUserGroups(
		long userId, java.util.List<UserGroup> userGroups) {

		_userGroupLocalService.deleteUserUserGroups(userId, userGroups);
	}

	@Override
	public void deleteUserUserGroups(long userId, long[] userGroupIds) {
		_userGroupLocalService.deleteUserUserGroups(userId, userGroupIds);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _userGroupLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _userGroupLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _userGroupLocalService.dynamicQuery();
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

		return _userGroupLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserGroupModelImpl</code>.
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

		return _userGroupLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserGroupModelImpl</code>.
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

		return _userGroupLocalService.dynamicQuery(
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

		return _userGroupLocalService.dynamicQueryCount(dynamicQuery);
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

		return _userGroupLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public UserGroup fetchUserGroup(long userGroupId) {
		return _userGroupLocalService.fetchUserGroup(userGroupId);
	}

	@Override
	public UserGroup fetchUserGroup(long companyId, String name) {
		return _userGroupLocalService.fetchUserGroup(companyId, name);
	}

	@Override
	public UserGroup fetchUserGroupByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return _userGroupLocalService.fetchUserGroupByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the user group with the matching UUID and company.
	 *
	 * @param uuid the user group's UUID
	 * @param companyId the primary key of the company
	 * @return the matching user group, or <code>null</code> if a matching user group could not be found
	 */
	@Override
	public UserGroup fetchUserGroupByUuidAndCompanyId(
		String uuid, long companyId) {

		return _userGroupLocalService.fetchUserGroupByUuidAndCompanyId(
			uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _userGroupLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _userGroupLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	/**
	 * Returns the groupIds of the groups associated with the user group.
	 *
	 * @param userGroupId the userGroupId of the user group
	 * @return long[] the groupIds of groups associated with the user group
	 */
	@Override
	public long[] getGroupPrimaryKeys(long userGroupId) {
		return _userGroupLocalService.getGroupPrimaryKeys(userGroupId);
	}

	@Override
	public java.util.List<UserGroup> getGroupUserGroups(long groupId) {
		return _userGroupLocalService.getGroupUserGroups(groupId);
	}

	@Override
	public java.util.List<UserGroup> getGroupUserGroups(
		long groupId, int start, int end) {

		return _userGroupLocalService.getGroupUserGroups(groupId, start, end);
	}

	@Override
	public java.util.List<UserGroup> getGroupUserGroups(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<UserGroup>
			orderByComparator) {

		return _userGroupLocalService.getGroupUserGroups(
			groupId, start, end, orderByComparator);
	}

	@Override
	public int getGroupUserGroupsCount(long groupId) {
		return _userGroupLocalService.getGroupUserGroupsCount(groupId);
	}

	@Override
	public java.util.List<UserGroup> getGroupUserUserGroups(
			long groupId, long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userGroupLocalService.getGroupUserUserGroups(groupId, userId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _userGroupLocalService.getIndexableActionableDynamicQuery();
	}

	@Override
	public UserGroup getOrAddEmptyUserGroup(
			String externalReferenceCode, long companyId, long userId,
			String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userGroupLocalService.getOrAddEmptyUserGroup(
			externalReferenceCode, companyId, userId, name);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _userGroupLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userGroupLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns the teamIds of the teams associated with the user group.
	 *
	 * @param userGroupId the userGroupId of the user group
	 * @return long[] the teamIds of teams associated with the user group
	 */
	@Override
	public long[] getTeamPrimaryKeys(long userGroupId) {
		return _userGroupLocalService.getTeamPrimaryKeys(userGroupId);
	}

	@Override
	public java.util.List<UserGroup> getTeamUserGroups(long teamId) {
		return _userGroupLocalService.getTeamUserGroups(teamId);
	}

	@Override
	public java.util.List<UserGroup> getTeamUserGroups(
		long teamId, int start, int end) {

		return _userGroupLocalService.getTeamUserGroups(teamId, start, end);
	}

	@Override
	public java.util.List<UserGroup> getTeamUserGroups(
		long teamId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<UserGroup>
			orderByComparator) {

		return _userGroupLocalService.getTeamUserGroups(
			teamId, start, end, orderByComparator);
	}

	@Override
	public int getTeamUserGroupsCount(long teamId) {
		return _userGroupLocalService.getTeamUserGroupsCount(teamId);
	}

	/**
	 * Returns the user group with the primary key.
	 *
	 * @param userGroupId the primary key of the user group
	 * @return the user group
	 * @throws PortalException if a user group with the primary key could not be found
	 */
	@Override
	public UserGroup getUserGroup(long userGroupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userGroupLocalService.getUserGroup(userGroupId);
	}

	/**
	 * Returns the user group with the name.
	 *
	 * @param companyId the primary key of the user group's company
	 * @param name the user group's name
	 * @return Returns the user group with the name
	 */
	@Override
	public UserGroup getUserGroup(long companyId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userGroupLocalService.getUserGroup(companyId, name);
	}

	@Override
	public UserGroup getUserGroupByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userGroupLocalService.getUserGroupByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the user group with the matching UUID and company.
	 *
	 * @param uuid the user group's UUID
	 * @param companyId the primary key of the company
	 * @return the matching user group
	 * @throws PortalException if a matching user group could not be found
	 */
	@Override
	public UserGroup getUserGroupByUuidAndCompanyId(String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userGroupLocalService.getUserGroupByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of all the user groups.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @return the range of user groups
	 */
	@Override
	public java.util.List<UserGroup> getUserGroups(int start, int end) {
		return _userGroupLocalService.getUserGroups(start, end);
	}

	/**
	 * Returns all the user groups belonging to the company.
	 *
	 * @param companyId the primary key of the user groups' company
	 * @return the user groups belonging to the company
	 */
	@Override
	public java.util.List<UserGroup> getUserGroups(long companyId) {
		return _userGroupLocalService.getUserGroups(companyId);
	}

	/**
	 * Returns all the user groups with the primary keys.
	 *
	 * @param userGroupIds the primary keys of the user groups
	 * @return the user groups with the primary keys
	 */
	@Override
	public java.util.List<UserGroup> getUserGroups(long[] userGroupIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userGroupLocalService.getUserGroups(userGroupIds);
	}

	/**
	 * Returns the number of user groups.
	 *
	 * @return the number of user groups
	 */
	@Override
	public int getUserGroupsCount() {
		return _userGroupLocalService.getUserGroupsCount();
	}

	/**
	 * Returns the userIds of the users associated with the user group.
	 *
	 * @param userGroupId the userGroupId of the user group
	 * @return long[] the userIds of users associated with the user group
	 */
	@Override
	public long[] getUserPrimaryKeys(long userGroupId) {
		return _userGroupLocalService.getUserPrimaryKeys(userGroupId);
	}

	@Override
	public java.util.List<UserGroup> getUserUserGroups(long userId) {
		return _userGroupLocalService.getUserUserGroups(userId);
	}

	@Override
	public java.util.List<UserGroup> getUserUserGroups(
		long userId, int start, int end) {

		return _userGroupLocalService.getUserUserGroups(userId, start, end);
	}

	@Override
	public java.util.List<UserGroup> getUserUserGroups(
		long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<UserGroup>
			orderByComparator) {

		return _userGroupLocalService.getUserUserGroups(
			userId, start, end, orderByComparator);
	}

	@Override
	public int getUserUserGroupsCount(long userId) {
		return _userGroupLocalService.getUserUserGroupsCount(userId);
	}

	@Override
	public boolean hasGroupUserGroup(long groupId, long userGroupId) {
		return _userGroupLocalService.hasGroupUserGroup(groupId, userGroupId);
	}

	@Override
	public boolean hasGroupUserGroups(long groupId) {
		return _userGroupLocalService.hasGroupUserGroups(groupId);
	}

	@Override
	public boolean hasTeamUserGroup(long teamId, long userGroupId) {
		return _userGroupLocalService.hasTeamUserGroup(teamId, userGroupId);
	}

	@Override
	public boolean hasTeamUserGroups(long teamId) {
		return _userGroupLocalService.hasTeamUserGroups(teamId);
	}

	@Override
	public boolean hasUserUserGroup(long userId, long userGroupId) {
		return _userGroupLocalService.hasUserUserGroup(userId, userGroupId);
	}

	@Override
	public boolean hasUserUserGroups(long userId) {
		return _userGroupLocalService.hasUserUserGroups(userId);
	}

	/**
	 * Returns an ordered range of all the user groups that match the keywords.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param companyId the primary key of the user group's company
	 * @param keywords the keywords (space separated), which may occur in the
	 user group's name or description (optionally <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). For more
	 information see {@link
	 com.liferay.portal.kernel.service.persistence.UserGroupFinder}
	 * @param start the lower bound of the range of user groups to return
	 * @param end the upper bound of the range of user groups to return (not
	 inclusive)
	 * @param orderByComparator the comparator to order the user groups
	 (optionally <code>null</code>)
	 * @return the matching user groups ordered by comparator
	 <code>orderByComparator</code>
	 * @see com.liferay.portal.kernel.service.persistence.UserGroupFinder
	 */
	@Override
	public java.util.List<UserGroup> search(
		long companyId, String keywords,
		java.util.LinkedHashMap<String, Object> params, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<UserGroup>
			orderByComparator) {

		return _userGroupLocalService.search(
			companyId, keywords, params, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the user groups that match the keywords,
	 * using the indexer. It is preferable to use this method instead of the
	 * non-indexed version whenever possible for performance reasons.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param companyId the primary key of the user group's company
	 * @param keywords the keywords (space separated), which may occur in the
	 user group's name or description (optionally <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). For more
	 information see {@link
	 com.liferay.user.groups.admin.web.search.UserGroupIndexer}
	 * @param start the lower bound of the range of user groups to return
	 * @param end the upper bound of the range of user groups to return (not
	 inclusive)
	 * @param sort the field and direction by which to sort (optionally
	 <code>null</code>)
	 * @return the matching user groups ordered by sort
	 * @see com.liferay.user.groups.admin.web.search.UserGroupIndexer
	 */
	@Override
	public com.liferay.portal.kernel.search.Hits search(
		long companyId, String keywords,
		java.util.LinkedHashMap<String, Object> params, int start, int end,
		com.liferay.portal.kernel.search.Sort sort) {

		return _userGroupLocalService.search(
			companyId, keywords, params, start, end, sort);
	}

	/**
	 * Returns an ordered range of all the user groups that match the name and
	 * description.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param companyId the primary key of the user group's company
	 * @param name the user group's name (optionally <code>null</code>)
	 * @param description the user group's description (optionally
	 <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). For more
	 information see {@link
	 com.liferay.portal.kernel.service.persistence.UserGroupFinder}
	 * @param andOperator whether every field must match its keywords or just
	 one field
	 * @param start the lower bound of the range of user groups to return
	 * @param end the upper bound of the range of user groups to return (not
	 inclusive)
	 * @param orderByComparator the comparator to order the user groups
	 (optionally <code>null</code>)
	 * @return the matching user groups ordered by comparator
	 <code>orderByComparator</code>
	 * @see com.liferay.portal.kernel.service.persistence.UserGroupFinder
	 */
	@Override
	public java.util.List<UserGroup> search(
		long companyId, String name, String description,
		java.util.LinkedHashMap<String, Object> params, boolean andOperator,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<UserGroup>
			orderByComparator) {

		return _userGroupLocalService.search(
			companyId, name, description, params, andOperator, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the user groups that match the name and
	 * description. It is preferable to use this method instead of the
	 * non-indexed version whenever possible for performance reasons.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param companyId the primary key of the user group's company
	 * @param name the user group's name (optionally <code>null</code>)
	 * @param description the user group's description (optionally
	 <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). For more
	 information see {@link
	 com.liferay.user.groups.admin.web.search.UserGroupIndexer}
	 * @param andSearch whether every field must match its keywords or just one
	 field
	 * @param start the lower bound of the range of user groups to return
	 * @param end the upper bound of the range of user groups to return (not
	 inclusive)
	 * @param sort the field and direction by which to sort (optionally
	 <code>null</code>)
	 * @return the matching user groups ordered by sort
	 * @see com.liferay.portal.kernel.service.persistence.UserGroupFinder
	 */
	@Override
	public com.liferay.portal.kernel.search.Hits search(
		long companyId, String name, String description,
		java.util.LinkedHashMap<String, Object> params, boolean andSearch,
		int start, int end, com.liferay.portal.kernel.search.Sort sort) {

		return _userGroupLocalService.search(
			companyId, name, description, params, andSearch, start, end, sort);
	}

	/**
	 * Returns the number of user groups that match the keywords
	 *
	 * @param companyId the primary key of the user group's company
	 * @param keywords the keywords (space separated), which may occur in the
	 user group's name or description (optionally <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). For more
	 information see {@link
	 com.liferay.portal.kernel.service.persistence.UserGroupFinder}
	 * @return the number of matching user groups
	 * @see com.liferay.portal.kernel.service.persistence.UserGroupFinder
	 */
	@Override
	public int searchCount(
		long companyId, String keywords,
		java.util.LinkedHashMap<String, Object> params) {

		return _userGroupLocalService.searchCount(companyId, keywords, params);
	}

	/**
	 * Returns the number of user groups that match the name and description.
	 *
	 * @param companyId the primary key of the user group's company
	 * @param name the user group's name (optionally <code>null</code>)
	 * @param description the user group's description (optionally
	 <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). For more
	 information see {@link
	 com.liferay.portal.kernel.service.persistence.UserGroupFinder}
	 * @param andOperator whether every field must match its keywords or just
	 one field
	 * @return the number of matching user groups
	 * @see com.liferay.portal.kernel.service.persistence.UserGroupFinder
	 */
	@Override
	public int searchCount(
		long companyId, String name, String description,
		java.util.LinkedHashMap<String, Object> params, boolean andOperator) {

		return _userGroupLocalService.searchCount(
			companyId, name, description, params, andOperator);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult<UserGroup>
			searchUserGroups(
				long companyId, String keywords,
				java.util.LinkedHashMap<String, Object> params, int start,
				int end, com.liferay.portal.kernel.search.Sort sort)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userGroupLocalService.searchUserGroups(
			companyId, keywords, params, start, end, sort);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult<UserGroup>
			searchUserGroups(
				long companyId, String name, String description,
				java.util.LinkedHashMap<String, Object> params,
				boolean andSearch, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userGroupLocalService.searchUserGroups(
			companyId, name, description, params, andSearch, start, end, sort);
	}

	@Override
	public void setGroupUserGroups(long groupId, long[] userGroupIds) {
		_userGroupLocalService.setGroupUserGroups(groupId, userGroupIds);
	}

	@Override
	public void setTeamUserGroups(long teamId, long[] userGroupIds) {
		_userGroupLocalService.setTeamUserGroups(teamId, userGroupIds);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public void setUserUserGroups(long userId, long[] userGroupIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		_userGroupLocalService.setUserUserGroups(userId, userGroupIds);
	}

	/**
	 * Removes the user groups from the group.
	 *
	 * @param groupId the primary key of the group
	 * @param userGroupIds the primary keys of the user groups
	 */
	@Override
	public void unsetGroupUserGroups(long groupId, long[] userGroupIds) {
		_userGroupLocalService.unsetGroupUserGroups(groupId, userGroupIds);
	}

	/**
	 * Removes the user groups from the team.
	 *
	 * @param teamId the primary key of the team
	 * @param userGroupIds the primary keys of the user groups
	 */
	@Override
	public void unsetTeamUserGroups(long teamId, long[] userGroupIds) {
		_userGroupLocalService.unsetTeamUserGroups(teamId, userGroupIds);
	}

	@Override
	public UserGroup updateExternalReferenceCode(
			UserGroup userGroup, String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userGroupLocalService.updateExternalReferenceCode(
			userGroup, externalReferenceCode);
	}

	/**
	 * Updates the user group.
	 *
	 * @param externalReferenceCode the user group's external reference code
	 * @param companyId the primary key of the user group's company
	 * @param userGroupId the primary key of the user group
	 * @param name the user group's name
	 * @param description the user group's description
	 * @param serviceContext the service context to be applied (optionally
	 <code>null</code>). Can set expando bridge attributes for the
	 user group.
	 * @return the user group
	 */
	@Override
	public UserGroup updateUserGroup(
			String externalReferenceCode, long companyId, long userGroupId,
			String name, String description, ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userGroupLocalService.updateUserGroup(
			externalReferenceCode, companyId, userGroupId, name, description,
			serviceContext);
	}

	/**
	 * Updates the user group in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect UserGroupLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param userGroup the user group
	 * @return the user group that was updated
	 */
	@Override
	public UserGroup updateUserGroup(UserGroup userGroup) {
		return _userGroupLocalService.updateUserGroup(userGroup);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _userGroupLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<UserGroup> getCTPersistence() {
		return _userGroupLocalService.getCTPersistence();
	}

	@Override
	public Class<UserGroup> getModelClass() {
		return _userGroupLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<UserGroup>, R, E> updateUnsafeFunction)
		throws E {

		return _userGroupLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public UserGroupLocalService getWrappedService() {
		return _userGroupLocalService;
	}

	@Override
	public void setWrappedService(UserGroupLocalService userGroupLocalService) {
		_userGroupLocalService = userGroupLocalService;
	}

	private UserGroupLocalService _userGroupLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-699971003