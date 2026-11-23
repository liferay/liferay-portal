/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for UserGroup. This utility wraps
 * <code>com.liferay.portal.service.impl.UserGroupLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see UserGroupLocalService
 * @generated
 */
public class UserGroupLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.service.impl.UserGroupLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static boolean addGroupUserGroup(long groupId, long userGroupId) {
		return getService().addGroupUserGroup(groupId, userGroupId);
	}

	public static boolean addGroupUserGroup(long groupId, UserGroup userGroup) {
		return getService().addGroupUserGroup(groupId, userGroup);
	}

	public static boolean addGroupUserGroups(
		long groupId, List<UserGroup> userGroups) {

		return getService().addGroupUserGroups(groupId, userGroups);
	}

	public static boolean addGroupUserGroups(
		long groupId, long[] userGroupIds) {

		return getService().addGroupUserGroups(groupId, userGroupIds);
	}

	public static UserGroup addOrUpdateUserGroup(
			String externalReferenceCode, long userId, long companyId,
			String name, String description, ServiceContext serviceContext)
		throws PortalException {

		return getService().addOrUpdateUserGroup(
			externalReferenceCode, userId, companyId, name, description,
			serviceContext);
	}

	public static boolean addTeamUserGroup(long teamId, long userGroupId) {
		return getService().addTeamUserGroup(teamId, userGroupId);
	}

	public static boolean addTeamUserGroup(long teamId, UserGroup userGroup) {
		return getService().addTeamUserGroup(teamId, userGroup);
	}

	public static boolean addTeamUserGroups(
		long teamId, List<UserGroup> userGroups) {

		return getService().addTeamUserGroups(teamId, userGroups);
	}

	public static boolean addTeamUserGroups(long teamId, long[] userGroupIds) {
		return getService().addTeamUserGroups(teamId, userGroupIds);
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
	public static UserGroup addUserGroup(
			String externalReferenceCode, long userId, long companyId,
			String name, String description, ServiceContext serviceContext)
		throws PortalException {

		return getService().addUserGroup(
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
	public static UserGroup addUserGroup(UserGroup userGroup) {
		return getService().addUserGroup(userGroup);
	}

	/**
	 * @throws PortalException
	 */
	public static boolean addUserUserGroup(long userId, long userGroupId)
		throws PortalException {

		return getService().addUserUserGroup(userId, userGroupId);
	}

	/**
	 * @throws PortalException
	 */
	public static boolean addUserUserGroup(long userId, UserGroup userGroup)
		throws PortalException {

		return getService().addUserUserGroup(userId, userGroup);
	}

	/**
	 * @throws PortalException
	 */
	public static boolean addUserUserGroups(
			long userId, List<UserGroup> userGroups)
		throws PortalException {

		return getService().addUserUserGroups(userId, userGroups);
	}

	/**
	 * @throws PortalException
	 */
	public static boolean addUserUserGroups(long userId, long[] userGroupIds)
		throws PortalException {

		return getService().addUserUserGroups(userId, userGroupIds);
	}

	public static void clearGroupUserGroups(long groupId) {
		getService().clearGroupUserGroups(groupId);
	}

	public static void clearTeamUserGroups(long teamId) {
		getService().clearTeamUserGroups(teamId);
	}

	public static void clearUserUserGroups(long userId) {
		getService().clearUserUserGroups(userId);
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
	 * Creates a new user group with the primary key. Does not add the user group to the database.
	 *
	 * @param userGroupId the primary key for the new user group
	 * @return the new user group
	 */
	public static UserGroup createUserGroup(long userGroupId) {
		return getService().createUserGroup(userGroupId);
	}

	public static void deleteGroupUserGroup(long groupId, long userGroupId) {
		getService().deleteGroupUserGroup(groupId, userGroupId);
	}

	public static void deleteGroupUserGroup(long groupId, UserGroup userGroup) {
		getService().deleteGroupUserGroup(groupId, userGroup);
	}

	public static void deleteGroupUserGroups(
		long groupId, List<UserGroup> userGroups) {

		getService().deleteGroupUserGroups(groupId, userGroups);
	}

	public static void deleteGroupUserGroups(
		long groupId, long[] userGroupIds) {

		getService().deleteGroupUserGroups(groupId, userGroupIds);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static void deleteTeamUserGroup(long teamId, long userGroupId) {
		getService().deleteTeamUserGroup(teamId, userGroupId);
	}

	public static void deleteTeamUserGroup(long teamId, UserGroup userGroup) {
		getService().deleteTeamUserGroup(teamId, userGroup);
	}

	public static void deleteTeamUserGroups(
		long teamId, List<UserGroup> userGroups) {

		getService().deleteTeamUserGroups(teamId, userGroups);
	}

	public static void deleteTeamUserGroups(long teamId, long[] userGroupIds) {
		getService().deleteTeamUserGroups(teamId, userGroupIds);
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
	public static UserGroup deleteUserGroup(long userGroupId)
		throws PortalException {

		return getService().deleteUserGroup(userGroupId);
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
	public static UserGroup deleteUserGroup(UserGroup userGroup)
		throws PortalException {

		return getService().deleteUserGroup(userGroup);
	}

	public static void deleteUserGroups(long companyId) throws PortalException {
		getService().deleteUserGroups(companyId);
	}

	public static void deleteUserUserGroup(long userId, long userGroupId) {
		getService().deleteUserUserGroup(userId, userGroupId);
	}

	public static void deleteUserUserGroup(long userId, UserGroup userGroup) {
		getService().deleteUserUserGroup(userId, userGroup);
	}

	public static void deleteUserUserGroups(
		long userId, List<UserGroup> userGroups) {

		getService().deleteUserUserGroups(userId, userGroups);
	}

	public static void deleteUserUserGroups(long userId, long[] userGroupIds) {
		getService().deleteUserUserGroups(userId, userGroupIds);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserGroupModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserGroupModelImpl</code>.
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

	public static UserGroup fetchUserGroup(long userGroupId) {
		return getService().fetchUserGroup(userGroupId);
	}

	public static UserGroup fetchUserGroup(long companyId, String name) {
		return getService().fetchUserGroup(companyId, name);
	}

	public static UserGroup fetchUserGroupByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return getService().fetchUserGroupByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the user group with the matching UUID and company.
	 *
	 * @param uuid the user group's UUID
	 * @param companyId the primary key of the company
	 * @return the matching user group, or <code>null</code> if a matching user group could not be found
	 */
	public static UserGroup fetchUserGroupByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().fetchUserGroupByUuidAndCompanyId(uuid, companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	/**
	 * Returns the groupIds of the groups associated with the user group.
	 *
	 * @param userGroupId the userGroupId of the user group
	 * @return long[] the groupIds of groups associated with the user group
	 */
	public static long[] getGroupPrimaryKeys(long userGroupId) {
		return getService().getGroupPrimaryKeys(userGroupId);
	}

	public static List<UserGroup> getGroupUserGroups(long groupId) {
		return getService().getGroupUserGroups(groupId);
	}

	public static List<UserGroup> getGroupUserGroups(
		long groupId, int start, int end) {

		return getService().getGroupUserGroups(groupId, start, end);
	}

	public static List<UserGroup> getGroupUserGroups(
		long groupId, int start, int end,
		OrderByComparator<UserGroup> orderByComparator) {

		return getService().getGroupUserGroups(
			groupId, start, end, orderByComparator);
	}

	public static int getGroupUserGroupsCount(long groupId) {
		return getService().getGroupUserGroupsCount(groupId);
	}

	public static List<UserGroup> getGroupUserUserGroups(
			long groupId, long userId)
		throws PortalException {

		return getService().getGroupUserUserGroups(groupId, userId);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	public static UserGroup getOrAddEmptyUserGroup(
			String externalReferenceCode, long companyId, long userId,
			String name)
		throws PortalException {

		return getService().getOrAddEmptyUserGroup(
			externalReferenceCode, companyId, userId, name);
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
	 * Returns the teamIds of the teams associated with the user group.
	 *
	 * @param userGroupId the userGroupId of the user group
	 * @return long[] the teamIds of teams associated with the user group
	 */
	public static long[] getTeamPrimaryKeys(long userGroupId) {
		return getService().getTeamPrimaryKeys(userGroupId);
	}

	public static List<UserGroup> getTeamUserGroups(long teamId) {
		return getService().getTeamUserGroups(teamId);
	}

	public static List<UserGroup> getTeamUserGroups(
		long teamId, int start, int end) {

		return getService().getTeamUserGroups(teamId, start, end);
	}

	public static List<UserGroup> getTeamUserGroups(
		long teamId, int start, int end,
		OrderByComparator<UserGroup> orderByComparator) {

		return getService().getTeamUserGroups(
			teamId, start, end, orderByComparator);
	}

	public static int getTeamUserGroupsCount(long teamId) {
		return getService().getTeamUserGroupsCount(teamId);
	}

	/**
	 * Returns the user group with the primary key.
	 *
	 * @param userGroupId the primary key of the user group
	 * @return the user group
	 * @throws PortalException if a user group with the primary key could not be found
	 */
	public static UserGroup getUserGroup(long userGroupId)
		throws PortalException {

		return getService().getUserGroup(userGroupId);
	}

	/**
	 * Returns the user group with the name.
	 *
	 * @param companyId the primary key of the user group's company
	 * @param name the user group's name
	 * @return Returns the user group with the name
	 */
	public static UserGroup getUserGroup(long companyId, String name)
		throws PortalException {

		return getService().getUserGroup(companyId, name);
	}

	public static UserGroup getUserGroupByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getUserGroupByExternalReferenceCode(
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
	public static UserGroup getUserGroupByUuidAndCompanyId(
			String uuid, long companyId)
		throws PortalException {

		return getService().getUserGroupByUuidAndCompanyId(uuid, companyId);
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
	public static List<UserGroup> getUserGroups(int start, int end) {
		return getService().getUserGroups(start, end);
	}

	/**
	 * Returns all the user groups belonging to the company.
	 *
	 * @param companyId the primary key of the user groups' company
	 * @return the user groups belonging to the company
	 */
	public static List<UserGroup> getUserGroups(long companyId) {
		return getService().getUserGroups(companyId);
	}

	/**
	 * Returns all the user groups with the primary keys.
	 *
	 * @param userGroupIds the primary keys of the user groups
	 * @return the user groups with the primary keys
	 */
	public static List<UserGroup> getUserGroups(long[] userGroupIds)
		throws PortalException {

		return getService().getUserGroups(userGroupIds);
	}

	/**
	 * Returns the number of user groups.
	 *
	 * @return the number of user groups
	 */
	public static int getUserGroupsCount() {
		return getService().getUserGroupsCount();
	}

	/**
	 * Returns the userIds of the users associated with the user group.
	 *
	 * @param userGroupId the userGroupId of the user group
	 * @return long[] the userIds of users associated with the user group
	 */
	public static long[] getUserPrimaryKeys(long userGroupId) {
		return getService().getUserPrimaryKeys(userGroupId);
	}

	public static List<UserGroup> getUserUserGroups(long userId) {
		return getService().getUserUserGroups(userId);
	}

	public static List<UserGroup> getUserUserGroups(
		long userId, int start, int end) {

		return getService().getUserUserGroups(userId, start, end);
	}

	public static List<UserGroup> getUserUserGroups(
		long userId, int start, int end,
		OrderByComparator<UserGroup> orderByComparator) {

		return getService().getUserUserGroups(
			userId, start, end, orderByComparator);
	}

	public static int getUserUserGroupsCount(long userId) {
		return getService().getUserUserGroupsCount(userId);
	}

	public static boolean hasGroupUserGroup(long groupId, long userGroupId) {
		return getService().hasGroupUserGroup(groupId, userGroupId);
	}

	public static boolean hasGroupUserGroups(long groupId) {
		return getService().hasGroupUserGroups(groupId);
	}

	public static boolean hasTeamUserGroup(long teamId, long userGroupId) {
		return getService().hasTeamUserGroup(teamId, userGroupId);
	}

	public static boolean hasTeamUserGroups(long teamId) {
		return getService().hasTeamUserGroups(teamId);
	}

	public static boolean hasUserUserGroup(long userId, long userGroupId) {
		return getService().hasUserUserGroup(userId, userGroupId);
	}

	public static boolean hasUserUserGroups(long userId) {
		return getService().hasUserUserGroups(userId);
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
	public static List<UserGroup> search(
		long companyId, String keywords,
		java.util.LinkedHashMap<String, Object> params, int start, int end,
		OrderByComparator<UserGroup> orderByComparator) {

		return getService().search(
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
	public static com.liferay.portal.kernel.search.Hits search(
		long companyId, String keywords,
		java.util.LinkedHashMap<String, Object> params, int start, int end,
		com.liferay.portal.kernel.search.Sort sort) {

		return getService().search(
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
	public static List<UserGroup> search(
		long companyId, String name, String description,
		java.util.LinkedHashMap<String, Object> params, boolean andOperator,
		int start, int end, OrderByComparator<UserGroup> orderByComparator) {

		return getService().search(
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
	public static com.liferay.portal.kernel.search.Hits search(
		long companyId, String name, String description,
		java.util.LinkedHashMap<String, Object> params, boolean andSearch,
		int start, int end, com.liferay.portal.kernel.search.Sort sort) {

		return getService().search(
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
	public static int searchCount(
		long companyId, String keywords,
		java.util.LinkedHashMap<String, Object> params) {

		return getService().searchCount(companyId, keywords, params);
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
	public static int searchCount(
		long companyId, String name, String description,
		java.util.LinkedHashMap<String, Object> params, boolean andOperator) {

		return getService().searchCount(
			companyId, name, description, params, andOperator);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<UserGroup> searchUserGroups(
				long companyId, String keywords,
				java.util.LinkedHashMap<String, Object> params, int start,
				int end, com.liferay.portal.kernel.search.Sort sort)
			throws PortalException {

		return getService().searchUserGroups(
			companyId, keywords, params, start, end, sort);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<UserGroup> searchUserGroups(
				long companyId, String name, String description,
				java.util.LinkedHashMap<String, Object> params,
				boolean andSearch, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
			throws PortalException {

		return getService().searchUserGroups(
			companyId, name, description, params, andSearch, start, end, sort);
	}

	public static void setGroupUserGroups(long groupId, long[] userGroupIds) {
		getService().setGroupUserGroups(groupId, userGroupIds);
	}

	public static void setTeamUserGroups(long teamId, long[] userGroupIds) {
		getService().setTeamUserGroups(teamId, userGroupIds);
	}

	/**
	 * @throws PortalException
	 */
	public static void setUserUserGroups(long userId, long[] userGroupIds)
		throws PortalException {

		getService().setUserUserGroups(userId, userGroupIds);
	}

	/**
	 * Removes the user groups from the group.
	 *
	 * @param groupId the primary key of the group
	 * @param userGroupIds the primary keys of the user groups
	 */
	public static void unsetGroupUserGroups(long groupId, long[] userGroupIds) {
		getService().unsetGroupUserGroups(groupId, userGroupIds);
	}

	/**
	 * Removes the user groups from the team.
	 *
	 * @param teamId the primary key of the team
	 * @param userGroupIds the primary keys of the user groups
	 */
	public static void unsetTeamUserGroups(long teamId, long[] userGroupIds) {
		getService().unsetTeamUserGroups(teamId, userGroupIds);
	}

	public static UserGroup updateExternalReferenceCode(
			UserGroup userGroup, String externalReferenceCode)
		throws PortalException {

		return getService().updateExternalReferenceCode(
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
	public static UserGroup updateUserGroup(
			String externalReferenceCode, long companyId, long userGroupId,
			String name, String description, ServiceContext serviceContext)
		throws PortalException {

		return getService().updateUserGroup(
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
	public static UserGroup updateUserGroup(UserGroup userGroup) {
		return getService().updateUserGroup(userGroup);
	}

	public static UserGroupLocalService getService() {
		return _service;
	}

	public static void setService(UserGroupLocalService service) {
		_service = service;
	}

	private static volatile UserGroupLocalService _service;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1238354933