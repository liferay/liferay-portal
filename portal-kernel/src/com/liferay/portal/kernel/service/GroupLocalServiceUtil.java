/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for Group. This utility wraps
 * <code>com.liferay.portal.service.impl.GroupLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see GroupLocalService
 * @generated
 */
public class GroupLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.service.impl.GroupLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the group to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect GroupLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param group the group
	 * @return the group that was added
	 */
	public static Group addGroup(Group group) {
		return getService().addGroup(group);
	}

	public static Group addGroup(
			long userId, long parentGroupId, String className, long classPK,
			long liveGroupId, Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap, int type,
			boolean manualMembership, int membershipRestriction,
			String friendlyURL, boolean site, boolean inheritContent,
			boolean active, ServiceContext serviceContext)
		throws PortalException {

		return getService().addGroup(
			userId, parentGroupId, className, classPK, liveGroupId, nameMap,
			descriptionMap, type, manualMembership, membershipRestriction,
			friendlyURL, site, inheritContent, active, serviceContext);
	}

	public static Group addGroup(
			long userId, long parentGroupId, String className, long classPK,
			long liveGroupId, Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap, int type,
			boolean manualMembership, int membershipRestriction,
			String friendlyURL, boolean site, boolean active,
			ServiceContext serviceContext)
		throws PortalException {

		return getService().addGroup(
			userId, parentGroupId, className, classPK, liveGroupId, nameMap,
			descriptionMap, type, manualMembership, membershipRestriction,
			friendlyURL, site, active, serviceContext);
	}

	public static boolean addOrganizationGroup(
		long organizationId, Group group) {

		return getService().addOrganizationGroup(organizationId, group);
	}

	public static boolean addOrganizationGroup(
		long organizationId, long groupId) {

		return getService().addOrganizationGroup(organizationId, groupId);
	}

	public static boolean addOrganizationGroups(
		long organizationId, List<Group> groups) {

		return getService().addOrganizationGroups(organizationId, groups);
	}

	public static boolean addOrganizationGroups(
		long organizationId, long[] groupIds) {

		return getService().addOrganizationGroups(organizationId, groupIds);
	}

	public static Group addOrUpdateGroup(
			String externalReferenceCode, long userId, long parentGroupId,
			String className, long classPK, long liveGroupId,
			Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap, int type,
			boolean manualMembership, int membershipRestriction,
			String friendlyURL, boolean site, boolean inheritContent,
			boolean active, ServiceContext serviceContext)
		throws Exception {

		return getService().addOrUpdateGroup(
			externalReferenceCode, userId, parentGroupId, className, classPK,
			liveGroupId, nameMap, descriptionMap, type, manualMembership,
			membershipRestriction, friendlyURL, site, inheritContent, active,
			serviceContext);
	}

	public static boolean addRoleGroup(long roleId, Group group) {
		return getService().addRoleGroup(roleId, group);
	}

	public static boolean addRoleGroup(long roleId, long groupId) {
		return getService().addRoleGroup(roleId, groupId);
	}

	public static boolean addRoleGroups(long roleId, List<Group> groups) {
		return getService().addRoleGroups(roleId, groups);
	}

	public static boolean addRoleGroups(long roleId, long[] groupIds) {
		return getService().addRoleGroups(roleId, groupIds);
	}

	public static boolean addUserGroup(long userId, Group group) {
		return getService().addUserGroup(userId, group);
	}

	public static boolean addUserGroup(long userId, long groupId) {
		return getService().addUserGroup(userId, groupId);
	}

	public static boolean addUserGroupGroup(long userGroupId, Group group) {
		return getService().addUserGroupGroup(userGroupId, group);
	}

	public static boolean addUserGroupGroup(long userGroupId, long groupId) {
		return getService().addUserGroupGroup(userGroupId, groupId);
	}

	public static boolean addUserGroupGroups(
		long userGroupId, List<Group> groups) {

		return getService().addUserGroupGroups(userGroupId, groups);
	}

	public static boolean addUserGroupGroups(
		long userGroupId, long[] groupIds) {

		return getService().addUserGroupGroups(userGroupId, groupIds);
	}

	public static boolean addUserGroups(long userId, List<Group> groups) {
		return getService().addUserGroups(userId, groups);
	}

	public static boolean addUserGroups(long userId, long[] groupIds) {
		return getService().addUserGroups(userId, groupIds);
	}

	public static Group checkScopeGroup(
			com.liferay.portal.kernel.model.Layout layout, long userId)
		throws PortalException {

		return getService().checkScopeGroup(layout, userId);
	}

	/**
	 * Creates systems groups and other related data needed by the system on the
	 * very first startup. Also takes care of creating the Control Panel groups
	 * and layouts.
	 *
	 * @param companyId the primary key of the company
	 * @throws PortalException if a portal exception occurred
	 */
	public static void checkSystemGroups(long companyId)
		throws PortalException {

		getService().checkSystemGroups(companyId);
	}

	public static void clearOrganizationGroups(long organizationId) {
		getService().clearOrganizationGroups(organizationId);
	}

	public static void clearRoleGroups(long roleId) {
		getService().clearRoleGroups(roleId);
	}

	public static void clearUserGroupGroups(long userGroupId) {
		getService().clearUserGroupGroups(userGroupId);
	}

	public static void clearUserGroups(long userId) {
		getService().clearUserGroups(userId);
	}

	/**
	 * Creates a new group with the primary key. Does not add the group to the database.
	 *
	 * @param groupId the primary key for the new group
	 * @return the new group
	 */
	public static Group createGroup(long groupId) {
		return getService().createGroup(groupId);
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
	 * Deletes the group from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect GroupLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param group the group
	 * @return the group that was removed
	 * @throws PortalException
	 */
	public static Group deleteGroup(Group group) throws PortalException {
		return getService().deleteGroup(group);
	}

	/**
	 * Deletes the group with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect GroupLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param groupId the primary key of the group
	 * @return the group that was removed
	 * @throws PortalException if a group with the primary key could not be found
	 */
	public static Group deleteGroup(long groupId) throws PortalException {
		return getService().deleteGroup(groupId);
	}

	public static void deleteOrganizationGroup(
		long organizationId, Group group) {

		getService().deleteOrganizationGroup(organizationId, group);
	}

	public static void deleteOrganizationGroup(
		long organizationId, long groupId) {

		getService().deleteOrganizationGroup(organizationId, groupId);
	}

	public static void deleteOrganizationGroups(
		long organizationId, List<Group> groups) {

		getService().deleteOrganizationGroups(organizationId, groups);
	}

	public static void deleteOrganizationGroups(
		long organizationId, long[] groupIds) {

		getService().deleteOrganizationGroups(organizationId, groupIds);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static void deleteRoleGroup(long roleId, Group group) {
		getService().deleteRoleGroup(roleId, group);
	}

	public static void deleteRoleGroup(long roleId, long groupId) {
		getService().deleteRoleGroup(roleId, groupId);
	}

	public static void deleteRoleGroups(long roleId, List<Group> groups) {
		getService().deleteRoleGroups(roleId, groups);
	}

	public static void deleteRoleGroups(long roleId, long[] groupIds) {
		getService().deleteRoleGroups(roleId, groupIds);
	}

	public static void deleteUserGroup(long userId, Group group) {
		getService().deleteUserGroup(userId, group);
	}

	public static void deleteUserGroup(long userId, long groupId) {
		getService().deleteUserGroup(userId, groupId);
	}

	public static void deleteUserGroupGroup(long userGroupId, Group group) {
		getService().deleteUserGroupGroup(userGroupId, group);
	}

	public static void deleteUserGroupGroup(long userGroupId, long groupId) {
		getService().deleteUserGroupGroup(userGroupId, groupId);
	}

	public static void deleteUserGroupGroups(
		long userGroupId, List<Group> groups) {

		getService().deleteUserGroupGroups(userGroupId, groups);
	}

	public static void deleteUserGroupGroups(
		long userGroupId, long[] groupIds) {

		getService().deleteUserGroupGroups(userGroupId, groupIds);
	}

	public static void deleteUserGroups(long userId, List<Group> groups) {
		getService().deleteUserGroups(userId, groups);
	}

	public static void deleteUserGroups(long userId, long[] groupIds) {
		getService().deleteUserGroups(userId, groupIds);
	}

	public static void disableStaging(long groupId) throws PortalException {
		getService().disableStaging(groupId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
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

	public static void enableStaging(long groupId) throws PortalException {
		getService().enableStaging(groupId);
	}

	/**
	 * Returns the company's group.
	 *
	 * @param companyId the primary key of the company
	 * @return the company's group, or <code>null</code> if a matching group
	 could not be found
	 */
	public static Group fetchCompanyGroup(long companyId) {
		return getService().fetchCompanyGroup(companyId);
	}

	/**
	 * Returns the group with the matching friendly URL.
	 *
	 * @param companyId the primary key of the company
	 * @param friendlyURL the friendly URL
	 * @return the group with the friendly URL, or <code>null</code> if a
	 matching group could not be found
	 */
	public static Group fetchFriendlyURLGroup(
		long companyId, String friendlyURL) {

		return getService().fetchFriendlyURLGroup(companyId, friendlyURL);
	}

	public static Group fetchGroup(long groupId) {
		return getService().fetchGroup(groupId);
	}

	public static Group fetchGroup(
		long companyId, long classNameId, long classPK) {

		return getService().fetchGroup(companyId, classNameId, classPK);
	}

	/**
	 * Returns the group with the matching group key by first searching the
	 * system groups and then using the finder cache.
	 *
	 * @param companyId the primary key of the company
	 * @param groupKey the group key
	 * @return the group with the group key and associated company, or
	 <code>null</code> if a matching group could not be found
	 */
	public static Group fetchGroup(long companyId, String groupKey) {
		return getService().fetchGroup(companyId, groupKey);
	}

	public static Group fetchGroupByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return getService().fetchGroupByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the group with the matching UUID and company.
	 *
	 * @param uuid the group's UUID
	 * @param companyId the primary key of the company
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	public static Group fetchGroupByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().fetchGroupByUuidAndCompanyId(uuid, companyId);
	}

	public static Group fetchStagingGroup(long liveGroupId) {
		return getService().fetchStagingGroup(liveGroupId);
	}

	public static Group fetchUserGroup(long companyId, long userId) {
		return getService().fetchUserGroup(companyId, userId);
	}

	/**
	 * Returns the default user's personal site group.
	 *
	 * @param companyId the primary key of the company
	 * @return the default user's personal site group, or <code>null</code> if a
	 matching group could not be found
	 * @throws PortalException if a portal exception occurred
	 */
	public static Group fetchUserPersonalSiteGroup(long companyId)
		throws PortalException {

		return getService().fetchUserPersonalSiteGroup(companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static List<Long> getActiveGroupIds(long userId) {
		return getService().getActiveGroupIds(userId);
	}

	/**
	 * Returns all the active or inactive groups associated with the company.
	 *
	 * @param companyId the primary key of the company
	 * @param active whether to return only active groups or only inactive
	 groups
	 * @return the active or inactive groups
	 */
	public static List<Group> getActiveGroups(long companyId, boolean active) {
		return getService().getActiveGroups(companyId, active);
	}

	/**
	 * Returns the active or inactive groups associated with the company and,
	 * optionally, the main site.
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
	 * @param companyId the primary key of the company
	 * @param site whether the group is associated with a main site
	 * @param active whether to return only active groups or only inactive
	 groups
	 * @param start the lower bound of the range of groups to return
	 * @param end the upper bound of the range of groups to return (not
	 inclusive)
	 * @param orderByComparator the comparator to order the groups (optionally
	 <code>null</code>)
	 * @return the active or inactive groups
	 */
	public static List<Group> getActiveGroups(
		long companyId, boolean site, boolean active, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return getService().getActiveGroups(
			companyId, site, active, start, end, orderByComparator);
	}

	/**
	 * Returns the active or inactive groups associated with the company.
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
	 * @param companyId the primary key of the company
	 * @param active whether to return only active groups or only inactive
	 groups
	 * @param start the lower bound of the range of groups to return
	 * @param end the upper bound of the range of groups to return (not
	 inclusive)
	 * @param orderByComparator the comparator to order the groups (optionally
	 <code>null</code>)
	 * @return the active or inactive groups
	 */
	public static List<Group> getActiveGroups(
		long companyId, boolean active, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return getService().getActiveGroups(
			companyId, active, start, end, orderByComparator);
	}

	/**
	 * Returns the number of active or inactive groups associated with the
	 * company.
	 *
	 * @param companyId the primary key of the company
	 * @param active whether to count only active groups or only inactive
	 groups
	 * @return the number of active or inactive groups
	 */
	public static int getActiveGroupsCount(long companyId, boolean active) {
		return getService().getActiveGroupsCount(companyId, active);
	}

	/**
	 * Returns the number of active or inactive groups associated with the
	 * company.
	 *
	 * @param companyId the primary key of the company
	 * @param active whether to count only active groups or only inactive
	 groups
	 * @param site whether the group is to be associated with a main site
	 * @return the number of active or inactive groups
	 */
	public static int getActiveGroupsCount(
		long companyId, boolean active, boolean site) {

		return getService().getActiveGroupsCount(companyId, active, site);
	}

	/**
	 * Returns the company group.
	 *
	 * @param companyId the primary key of the company
	 * @return the group associated with the company
	 * @throws PortalException if a portal exception occurred
	 */
	public static Group getCompanyGroup(long companyId) throws PortalException {
		return getService().getCompanyGroup(companyId);
	}

	/**
	 * Returns a range of all the groups associated with the company.
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
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of groups to return
	 * @param end the upper bound of the range of groups to return (not
	 inclusive)
	 * @return the range of groups associated with the company
	 */
	public static List<Group> getCompanyGroups(
		long companyId, int start, int end) {

		return getService().getCompanyGroups(companyId, start, end);
	}

	/**
	 * Returns the number of groups associated with the company.
	 *
	 * @param companyId the primary key of the company
	 * @return the number of groups associated with the company
	 */
	public static int getCompanyGroupsCount(long companyId) {
		return getService().getCompanyGroupsCount(companyId);
	}

	/**
	 * Returns the group with the matching friendly URL.
	 *
	 * @param companyId the primary key of the company
	 * @param friendlyURL the group's friendlyURL
	 * @return the group with the friendly URL
	 * @throws PortalException if a portal exception occurred
	 */
	public static Group getFriendlyURLGroup(long companyId, String friendlyURL)
		throws PortalException {

		return getService().getFriendlyURLGroup(companyId, friendlyURL);
	}

	/**
	 * Returns the group with the primary key.
	 *
	 * @param groupId the primary key of the group
	 * @return the group
	 * @throws PortalException if a group with the primary key could not be found
	 */
	public static Group getGroup(long groupId) throws PortalException {
		return getService().getGroup(groupId);
	}

	/**
	 * Returns the group with the matching group key.
	 *
	 * @param companyId the primary key of the company
	 * @param groupKey the group key
	 * @return the group with the group key
	 * @throws PortalException if a portal exception occurred
	 */
	public static Group getGroup(long companyId, String groupKey)
		throws PortalException {

		return getService().getGroup(companyId, groupKey);
	}

	public static Group getGroupByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getGroupByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the group with the matching UUID and company.
	 *
	 * @param uuid the group's UUID
	 * @param companyId the primary key of the company
	 * @return the matching group
	 * @throws PortalException if a matching group could not be found
	 */
	public static Group getGroupByUuidAndCompanyId(String uuid, long companyId)
		throws PortalException {

		return getService().getGroupByUuidAndCompanyId(uuid, companyId);
	}

	public static List<Long> getGroupIds(long companyId, boolean active) {
		return getService().getGroupIds(companyId, active);
	}

	/**
	 * Returns a range of all the groups.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of groups
	 */
	public static List<Group> getGroups(int start, int end) {
		return getService().getGroups(start, end);
	}

	/**
	 * Returns all the groups that are direct children of the parent group.
	 *
	 * @param companyId the primary key of the company
	 * @param parentGroupId the primary key of the parent group
	 * @param site whether the group is to be associated with a main site
	 * @return the matching groups, or <code>null</code> if no matches were
	 found
	 */
	public static List<Group> getGroups(
		long companyId, long parentGroupId, boolean site) {

		return getService().getGroups(companyId, parentGroupId, site);
	}

	public static List<Group> getGroups(
		long companyId, long parentGroupId, boolean site,
		boolean inheritContent) {

		return getService().getGroups(
			companyId, parentGroupId, site, inheritContent);
	}

	public static List<Group> getGroups(
		long companyId, long parentGroupId, boolean site, int start, int end) {

		return getService().getGroups(
			companyId, parentGroupId, site, start, end);
	}

	public static List<Group> getGroups(
		long companyId, long parentGroupId, String name, boolean site,
		int start, int end) {

		return getService().getGroups(
			companyId, parentGroupId, name, site, start, end);
	}

	public static List<Group> getGroups(
		long companyId, String treePath, boolean site) {

		return getService().getGroups(companyId, treePath, site);
	}

	/**
	 * Returns all the groups that are direct children of the parent group with
	 * the matching className.
	 *
	 * @param companyId the primary key of the company
	 * @param className the class name of the group
	 * @param parentGroupId the primary key of the parent group
	 * @return the matching groups, or <code>null</code> if no matches were
	 found
	 */
	public static List<Group> getGroups(
		long companyId, String className, long parentGroupId) {

		return getService().getGroups(companyId, className, parentGroupId);
	}

	/**
	 * Returns a range of all the groups that are direct children of the parent
	 * group with the matching className.
	 *
	 * @param companyId the primary key of the company
	 * @param className the class name of the group
	 * @param parentGroupId the primary key of the parent group
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the range of matching groups
	 */
	public static List<Group> getGroups(
		long companyId, String className, long parentGroupId, int start,
		int end) {

		return getService().getGroups(
			companyId, className, parentGroupId, start, end);
	}

	/**
	 * Returns the groups with the matching primary keys.
	 *
	 * @param groupIds the primary keys of the groups
	 * @return the groups with the primary keys
	 * @throws PortalException if a portal exception occurred
	 */
	public static List<Group> getGroups(long[] groupIds)
		throws PortalException {

		return getService().getGroups(groupIds);
	}

	/**
	 * Returns the number of groups.
	 *
	 * @return the number of groups
	 */
	public static int getGroupsCount() {
		return getService().getGroupsCount();
	}

	/**
	 * Returns the number of groups that are direct children of the parent
	 * group.
	 *
	 * @param companyId the primary key of the company
	 * @param parentGroupId the primary key of the parent group
	 * @param site whether the group is to be associated with a main site
	 * @return the number of matching groups
	 */
	public static int getGroupsCount(
		long companyId, long parentGroupId, boolean site) {

		return getService().getGroupsCount(companyId, parentGroupId, site);
	}

	public static int getGroupsCount(
		long companyId, long parentGroupId, String name, boolean site) {

		return getService().getGroupsCount(
			companyId, parentGroupId, name, site);
	}

	/**
	 * Returns the number of groups that are direct children of the parent group
	 * with the matching className.
	 *
	 * @param companyId the primary key of the company
	 * @param className the class name of the group
	 * @param parentGroupId the primary key of the parent group
	 * @return the number of matching groups
	 */
	public static int getGroupsCount(
		long companyId, String className, long parentGroupId) {

		return getService().getGroupsCount(companyId, className, parentGroupId);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the group associated with the layout.
	 *
	 * @param companyId the primary key of the company
	 * @param plid the primary key of the layout
	 * @return the group associated with the layout
	 * @throws PortalException if a portal exception occurred
	 */
	public static Group getLayoutGroup(long companyId, long plid)
		throws PortalException {

		return getService().getLayoutGroup(companyId, plid);
	}

	/**
	 * Returns the group associated with the layout prototype.
	 *
	 * @param companyId the primary key of the company
	 * @param layoutPrototypeId the primary key of the layout prototype
	 * @return the group associated with the layout prototype
	 * @throws PortalException if a portal exception occurred
	 */
	public static Group getLayoutPrototypeGroup(
			long companyId, long layoutPrototypeId)
		throws PortalException {

		return getService().getLayoutPrototypeGroup(
			companyId, layoutPrototypeId);
	}

	/**
	 * Returns the group associated with the layout set prototype.
	 *
	 * @param companyId the primary key of the company
	 * @param layoutSetPrototypeId the primary key of the layout set prototype
	 * @return the group associated with the layout set prototype
	 * @throws PortalException if a portal exception occurred
	 */
	public static Group getLayoutSetPrototypeGroup(
			long companyId, long layoutSetPrototypeId)
		throws PortalException {

		return getService().getLayoutSetPrototypeGroup(
			companyId, layoutSetPrototypeId);
	}

	/**
	 * Returns a range of all groups that are children of the parent group and
	 * that have at least one layout.
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
	 * @param companyId the primary key of the company
	 * @param parentGroupId the primary key of the parent group
	 * @param site whether the group is to be associated with a main site
	 * @param active whether to return only active groups, or only inactive
	 groups
	 * @param start the lower bound of the range of groups to return
	 * @param end the upper bound of the range of groups to return (not
	 inclusive)
	 * @param orderByComparator the comparator to order the groups (optionally
	 <code>null</code>)
	 * @return the range of matching groups ordered by comparator
	 <code>orderByComparator</code>
	 */
	public static List<Group> getLayoutsGroups(
		long companyId, long parentGroupId, boolean site, boolean active,
		int start, int end, OrderByComparator<Group> orderByComparator) {

		return getService().getLayoutsGroups(
			companyId, parentGroupId, site, active, start, end,
			orderByComparator);
	}

	/**
	 * Returns a range of all groups that are children of the parent group and
	 * that have at least one layout.
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
	 * @param companyId the primary key of the company
	 * @param parentGroupId the primary key of the parent group
	 * @param site whether the group is to be associated with a main site
	 * @param start the lower bound of the range of groups to return
	 * @param end the upper bound of the range of groups to return (not
	 inclusive)
	 * @param orderByComparator the comparator to order the groups (optionally
	 <code>null</code>)
	 * @return the range of matching groups ordered by comparator
	 <code>orderByComparator</code>
	 */
	public static List<Group> getLayoutsGroups(
		long companyId, long parentGroupId, boolean site, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return getService().getLayoutsGroups(
			companyId, parentGroupId, site, start, end, orderByComparator);
	}

	/**
	 * Returns the number of groups that are children or the parent group and
	 * that have at least one layout
	 *
	 * @param companyId the primary key of the company
	 * @param parentGroupId the primary key of the parent group
	 * @param site whether the group is to be associated with a main site
	 * @return the number of matching groups
	 */
	public static int getLayoutsGroupsCount(
		long companyId, long parentGroupId, boolean site) {

		return getService().getLayoutsGroupsCount(
			companyId, parentGroupId, site);
	}

	/**
	 * Returns the number of groups that are children or the parent group and
	 * that have at least one layout
	 *
	 * @param companyId the primary key of the company
	 * @param parentGroupId the primary key of the parent group
	 * @param site whether the group is to be associated with a main site
	 * @param active whether to return only active groups, or only inactive
	 groups
	 * @return the number of matching groups
	 */
	public static int getLayoutsGroupsCount(
		long companyId, long parentGroupId, boolean site, boolean active) {

		return getService().getLayoutsGroupsCount(
			companyId, parentGroupId, site, active);
	}

	/**
	 * Returns all live groups.
	 *
	 * @return all live groups
	 */
	public static List<Group> getLiveGroups() {
		return getService().getLiveGroups();
	}

	/**
	 * Returns the specified organization group.
	 *
	 * @param companyId the primary key of the company
	 * @param organizationId the primary key of the organization
	 * @return the group associated with the organization
	 * @throws PortalException if a portal exception occurred
	 */
	public static Group getOrganizationGroup(
			long companyId, long organizationId)
		throws PortalException {

		return getService().getOrganizationGroup(companyId, organizationId);
	}

	public static List<Group> getOrganizationGroups(long organizationId) {
		return getService().getOrganizationGroups(organizationId);
	}

	public static List<Group> getOrganizationGroups(
		long organizationId, int start, int end) {

		return getService().getOrganizationGroups(organizationId, start, end);
	}

	public static List<Group> getOrganizationGroups(
		long organizationId, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return getService().getOrganizationGroups(
			organizationId, start, end, orderByComparator);
	}

	public static int getOrganizationGroupsCount(long organizationId) {
		return getService().getOrganizationGroupsCount(organizationId);
	}

	/**
	 * Returns the organizationIds of the organizations associated with the group.
	 *
	 * @param groupId the groupId of the group
	 * @return long[] the organizationIds of organizations associated with the group
	 */
	public static long[] getOrganizationPrimaryKeys(long groupId) {
		return getService().getOrganizationPrimaryKeys(groupId);
	}

	/**
	 * Returns the specified organization groups.
	 *
	 * @param organizations the organizations
	 * @return the groups associated with the organizations
	 */
	public static List<Group> getOrganizationsGroups(
		List<com.liferay.portal.kernel.model.Organization> organizations) {

		return getService().getOrganizationsGroups(organizations);
	}

	/**
	 * Returns all the groups related to the organizations.
	 *
	 * @param organizations the organizations
	 * @return the groups related to the organizations
	 */
	public static List<Group> getOrganizationsRelatedGroups(
		List<com.liferay.portal.kernel.model.Organization> organizations) {

		return getService().getOrganizationsRelatedGroups(organizations);
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
	 * Returns the group followed by all its parent groups ordered by closest
	 * ancestor.
	 *
	 * @param groupId the primary key of the group
	 * @return the group followed by all its parent groups ordered by closest
	 ancestor
	 * @throws PortalException if a portal exception occurred
	 */
	public static List<Group> getParentGroups(long groupId)
		throws PortalException {

		return getService().getParentGroups(groupId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	public static List<Group> getRoleGroups(long roleId) {
		return getService().getRoleGroups(roleId);
	}

	public static List<Group> getRoleGroups(long roleId, int start, int end) {
		return getService().getRoleGroups(roleId, start, end);
	}

	public static List<Group> getRoleGroups(
		long roleId, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return getService().getRoleGroups(
			roleId, start, end, orderByComparator);
	}

	public static int getRoleGroupsCount(long roleId) {
		return getService().getRoleGroupsCount(roleId);
	}

	/**
	 * Returns the roleIds of the roles associated with the group.
	 *
	 * @param groupId the groupId of the group
	 * @return long[] the roleIds of roles associated with the group
	 */
	public static long[] getRolePrimaryKeys(long groupId) {
		return getService().getRolePrimaryKeys(groupId);
	}

	public static List<Group> getStagedSites() {
		return getService().getStagedSites();
	}

	/**
	 * Returns the group directly associated with the user.
	 *
	 * @param companyId the primary key of the company
	 * @param userId the primary key of the user
	 * @return the group directly associated with the user
	 * @throws PortalException if a portal exception occurred
	 */
	public static Group getUserGroup(long companyId, long userId)
		throws PortalException {

		return getService().getUserGroup(companyId, userId);
	}

	/**
	 * Returns the specified "user group" group. That is, the group that
	 * represents the {@link UserGroup} entity.
	 *
	 * @param companyId the primary key of the company
	 * @param userGroupId the primary key of the user group
	 * @return the group associated with the user group
	 * @throws PortalException if a portal exception occurred
	 */
	public static Group getUserGroupGroup(long companyId, long userGroupId)
		throws PortalException {

		return getService().getUserGroupGroup(companyId, userGroupId);
	}

	public static List<Group> getUserGroupGroups(long userGroupId) {
		return getService().getUserGroupGroups(userGroupId);
	}

	public static List<Group> getUserGroupGroups(
		long userGroupId, int start, int end) {

		return getService().getUserGroupGroups(userGroupId, start, end);
	}

	public static List<Group> getUserGroupGroups(
		long userGroupId, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return getService().getUserGroupGroups(
			userGroupId, start, end, orderByComparator);
	}

	public static int getUserGroupGroupsCount(long userGroupId) {
		return getService().getUserGroupGroupsCount(userGroupId);
	}

	/**
	 * Returns the userGroupIds of the user groups associated with the group.
	 *
	 * @param groupId the groupId of the group
	 * @return long[] the userGroupIds of user groups associated with the group
	 */
	public static long[] getUserGroupPrimaryKeys(long groupId) {
		return getService().getUserGroupPrimaryKeys(groupId);
	}

	public static List<Group> getUserGroups(long userId) {
		return getService().getUserGroups(userId);
	}

	/**
	 * Returns all the user's site groups and immediate organization groups,
	 * optionally including the user's inherited organization groups and user
	 * groups. System and staged groups are not included.
	 *
	 * @param userId the primary key of the user
	 * @param inherit whether to include the user's inherited organization
	 groups and user groups
	 * @return the user's groups and immediate organization groups
	 * @throws PortalException if a portal exception occurred
	 */
	public static List<Group> getUserGroups(long userId, boolean inherit)
		throws PortalException {

		return getService().getUserGroups(userId, inherit);
	}

	/**
	 * Returns an ordered range of all the user's site groups and immediate
	 * organization groups, optionally including the user's inherited
	 * organization groups and user groups. System and staged groups are not
	 * included.
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
	 * @param userId the primary key of the user
	 * @param inherit whether to include the user's inherited organization
	 groups and user groups
	 * @param start the lower bound of the range of groups to return
	 * @param end the upper bound of the range of groups to return (not
	 inclusive)
	 * @return the range of the user's groups and immediate organization groups
	 ordered by name
	 * @throws PortalException if a portal exception occurred
	 */
	public static List<Group> getUserGroups(
			long userId, boolean inherit, int start, int end)
		throws PortalException {

		return getService().getUserGroups(userId, inherit, start, end);
	}

	public static List<Group> getUserGroups(long userId, int start, int end) {
		return getService().getUserGroups(userId, start, end);
	}

	/**
	 * @throws PortalException
	 */
	public static List<Group> getUserGroups(
			long userId, int start, int end,
			OrderByComparator<Group> orderByComparator)
		throws PortalException {

		return getService().getUserGroups(
			userId, start, end, orderByComparator);
	}

	public static int getUserGroupsCount(long userId) {
		return getService().getUserGroupsCount(userId);
	}

	/**
	 * Returns the groups associated with the user groups.
	 *
	 * @param userGroups the user groups
	 * @return the groups associated with the user groups
	 * @throws PortalException if a portal exception occurred
	 */
	public static List<Group> getUserGroupsGroups(
			List<com.liferay.portal.kernel.model.UserGroup> userGroups)
		throws PortalException {

		return getService().getUserGroupsGroups(userGroups);
	}

	/**
	 * Returns all the groups related to the user groups.
	 *
	 * @param userGroups the user groups
	 * @return the groups related to the user groups
	 */
	public static List<Group> getUserGroupsRelatedGroups(
		List<com.liferay.portal.kernel.model.UserGroup> userGroups) {

		return getService().getUserGroupsRelatedGroups(userGroups);
	}

	/**
	 * Returns the range of all groups associated with the user's organization
	 * groups, including the ancestors of the organization groups, unless portal
	 * property <code>organizations.membership.strict</code> is set to
	 * <code>true</code>.
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
	 * @param userId the primary key of the user
	 * @param start the lower bound of the range of groups to consider
	 * @param end the upper bound of the range of groups to consider (not
	 inclusive)
	 * @return the range of groups associated with the user's organization
	 groups
	 * @throws PortalException if a portal exception occurred
	 */
	public static List<Group> getUserOrganizationsGroups(
			long userId, int start, int end)
		throws PortalException {

		return getService().getUserOrganizationsGroups(userId, start, end);
	}

	/**
	 * Returns the default user's personal site group.
	 *
	 * @param companyId the primary key of the company
	 * @return the default user's personal site group
	 * @throws PortalException if a portal exception occurred
	 */
	public static Group getUserPersonalSiteGroup(long companyId)
		throws PortalException {

		return getService().getUserPersonalSiteGroup(companyId);
	}

	/**
	 * Returns the userIds of the users associated with the group.
	 *
	 * @param groupId the groupId of the group
	 * @return long[] the userIds of users associated with the group
	 */
	public static long[] getUserPrimaryKeys(long groupId) {
		return getService().getUserPrimaryKeys(groupId);
	}

	public static List<Group> getUserSitesGroups(long userId)
		throws PortalException {

		return getService().getUserSitesGroups(userId);
	}

	public static List<Group> getUserSitesGroups(
			long userId, boolean includeAdministrative)
		throws PortalException {

		return getService().getUserSitesGroups(userId, includeAdministrative);
	}

	public static List<Group> getUserSitesGroups(
			long userId, int start, int end)
		throws PortalException {

		return getService().getUserSitesGroups(userId, start, end);
	}

	public static boolean hasOrganizationGroup(
		long organizationId, long groupId) {

		return getService().hasOrganizationGroup(organizationId, groupId);
	}

	public static boolean hasOrganizationGroups(long organizationId) {
		return getService().hasOrganizationGroups(organizationId);
	}

	public static boolean hasRoleGroup(long roleId, long groupId) {
		return getService().hasRoleGroup(roleId, groupId);
	}

	public static boolean hasRoleGroups(long roleId) {
		return getService().hasRoleGroups(roleId);
	}

	public static boolean hasUserGroup(long userId, long groupId) {
		return getService().hasUserGroup(userId, groupId);
	}

	/**
	 * Returns <code>true</code> if the user is immediately associated with the
	 * group, or optionally if the user is associated with the group via the
	 * user's organizations, inherited organizations, or user groups.
	 *
	 * @param userId the primary key of the user
	 * @param groupId the primary key of the group
	 * @param inherit whether to include organization groups and user groups to
	 which the user belongs in the determination
	 * @return <code>true</code> if the user is associated with the group;
	 <code>false</code> otherwise
	 */
	public static boolean hasUserGroup(
		long userId, long groupId, boolean inherit) {

		return getService().hasUserGroup(userId, groupId, inherit);
	}

	public static boolean hasUserGroupGroup(long userGroupId, long groupId) {
		return getService().hasUserGroupGroup(userGroupId, groupId);
	}

	public static boolean hasUserGroupGroups(long userGroupId) {
		return getService().hasUserGroupGroups(userGroupId);
	}

	public static boolean hasUserGroups(long userId) {
		return getService().hasUserGroups(userId);
	}

	public static boolean isLiveGroupActive(Group group) {
		return getService().isLiveGroupActive(group);
	}

	/**
	 * Returns the group with the matching group key by first searching the
	 * system groups and then using the finder cache.
	 *
	 * @param companyId the primary key of the company
	 * @param groupKey the group key
	 * @return the group with the group key and associated company, or
	 <code>null</code> if a matching group could not be found
	 */
	public static Group loadFetchGroup(long companyId, String groupKey) {
		return getService().loadFetchGroup(companyId, groupKey);
	}

	/**
	 * Returns the group with the matching group key.
	 *
	 * @param companyId the primary key of the company
	 * @param groupKey the group key
	 * @return the group with the group key and associated company
	 * @throws PortalException if a portal exception occurred
	 */
	public static Group loadGetGroup(long companyId, String groupKey)
		throws PortalException {

		return getService().loadGetGroup(companyId, groupKey);
	}

	/**
	 * Rebuilds the group tree.
	 *
	 * <p>
	 * Only call this method if the tree has become stale through operations
	 * other than normal CRUD. Under normal circumstances the tree is
	 * automatically rebuilt whenever necessary.
	 * </p>
	 *
	 * @param companyId the primary key of the group's company
	 * @throws PortalException if a portal exception occurred
	 */
	public static void rebuildTree(long companyId) throws PortalException {
		getService().rebuildTree(companyId);
	}

	/**
	 * Returns an ordered range of all the company's groups, optionally
	 * including the user's inherited organization groups and user groups.
	 * System and staged groups are not included.
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
	 * @param companyId the primary key of the company
	 * @param params the finder params (optionally <code>null</code>). To
	 include a user's organizations, inherited organizations, and user
	 groups in the search, add an entry with key
	 &quot;usersGroups&quot; mapped to the user's ID and an entry with
	 key &quot;inherit&quot; mapped to a non-<code>null</code> object.
	 For more information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param start the lower bound of the range of groups to return
	 * @param end the upper bound of the range of groups to return (not
	 inclusive)
	 * @return the matching groups ordered by name
	 */
	public static List<Group> search(
		long companyId, java.util.LinkedHashMap<String, Object> params,
		int start, int end) {

		return getService().search(companyId, params, start, end);
	}

	/**
	 * Returns an ordered range of all the groups belonging to the parent group
	 * that match the keywords, optionally including the user's inherited
	 * organization groups and user groups. System and staged groups are not
	 * included.
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
	 * @param companyId the primary key of the company
	 * @param parentGroupId the primary key of the parent group
	 * @param keywords the keywords (space separated), which may occur in the
	 sites's name, or description (optionally <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). To
	 include the user's inherited organizations and user groups in the
	 search, add entries having &quot;usersGroups&quot; and
	 &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param start the lower bound of the range of groups to return
	 * @param end the upper bound of the range of groups to return (not
	 inclusive)
	 * @return the matching groups ordered by name
	 */
	public static List<Group> search(
		long companyId, long parentGroupId, String keywords,
		java.util.LinkedHashMap<String, Object> params, int start, int end) {

		return getService().search(
			companyId, parentGroupId, keywords, params, start, end);
	}

	/**
	 * Returns an ordered range of all the groups belonging to the parent group
	 * that match the keywords, optionally including the user's inherited
	 * organization groups and user groups. System and staged groups are not
	 * included.
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
	 * @param companyId the primary key of the company
	 * @param parentGroupId the primary key of the parent group
	 * @param keywords the keywords (space separated), which may occur in the
	 sites's name, or description (optionally <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). To
	 include the user's inherited organizations and user groups in the
	 search, add entries having &quot;usersGroups&quot; and
	 &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param start the lower bound of the range of groups to return
	 * @param end the upper bound of the range of groups to return (not
	 inclusive)
	 * @param orderByComparator the comparator to order the groups (optionally
	 <code>null</code>)
	 * @return the matching groups ordered by comparator
	 <code>orderByComparator</code>
	 */
	public static List<Group> search(
		long companyId, long parentGroupId, String keywords,
		java.util.LinkedHashMap<String, Object> params, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return getService().search(
			companyId, parentGroupId, keywords, params, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the site groups belonging to the parent
	 * group and organization groups that match the name and description,
	 * optionally including the user's inherited organization groups and user
	 * groups. System and staged groups are not included.
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
	 * @param companyId the primary key of the company
	 * @param parentGroupId the primary key of the parent group
	 * @param name the group's name (optionally <code>null</code>)
	 * @param description the group's description (optionally
	 <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). To
	 include the user's inherited organizations and user groups in the
	 search, add entries having &quot;usersGroups&quot; and
	 &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param andOperator whether every field must match its keywords, or just
	 one field.
	 * @param start the lower bound of the range of groups to return
	 * @param end the upper bound of the range of groups to return (not
	 inclusive)
	 * @return the matching groups ordered by name
	 */
	public static List<Group> search(
		long companyId, long parentGroupId, String name, String description,
		java.util.LinkedHashMap<String, Object> params, boolean andOperator,
		int start, int end) {

		return getService().search(
			companyId, parentGroupId, name, description, params, andOperator,
			start, end);
	}

	/**
	 * Returns an ordered range of all the site groups belonging to the parent
	 * group and organization groups that match the name and description,
	 * optionally including the user's inherited organization groups and user
	 * groups. System and staged groups are not included.
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
	 * @param companyId the primary key of the company
	 * @param parentGroupId the primary key of the parent group
	 * @param name the group's name (optionally <code>null</code>)
	 * @param description the group's description (optionally
	 <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). To
	 include the user's inherited organizations and user groups in the
	 search, add entries having &quot;usersGroups&quot; and
	 &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param andOperator whether every field must match its keywords, or just
	 one field.
	 * @param start the lower bound of the range of groups to return
	 * @param end the upper bound of the range of groups to return (not
	 inclusive)
	 * @param orderByComparator the comparator to order the groups (optionally
	 <code>null</code>)
	 * @return the matching groups ordered by comparator
	 <code>orderByComparator</code>
	 */
	public static List<Group> search(
		long companyId, long parentGroupId, String name, String description,
		java.util.LinkedHashMap<String, Object> params, boolean andOperator,
		int start, int end, OrderByComparator<Group> orderByComparator) {

		return getService().search(
			companyId, parentGroupId, name, description, params, andOperator,
			start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the groups belonging to the parent group
	 * that match the class name IDs and keywords, optionally including the
	 * user's inherited organization groups and user groups. System and staged
	 * groups are not included.
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
	 * @param companyId the primary key of the company
	 * @param classNameIds the primary keys of the class names of the entities
	 the groups are related to (optionally <code>null</code>)
	 * @param parentGroupId the primary key of the parent group
	 * @param keywords the keywords (space separated), which may occur in the
	 sites's name, or description (optionally <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). To
	 include a user's organizations, inherited organizations, and user
	 groups in the search, add an entry with key
	 &quot;usersGroups&quot; mapped to the user's ID and an entry with
	 key &quot;inherit&quot; mapped to a non-<code>null</code> object.
	 For more information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param start the lower bound of the range of groups to return
	 * @param end the upper bound of the range of groups to return (not
	 inclusive)
	 * @return the matching groups ordered by name
	 */
	public static List<Group> search(
		long companyId, long[] classNameIds, long parentGroupId,
		String keywords, java.util.LinkedHashMap<String, Object> params,
		int start, int end) {

		return getService().search(
			companyId, classNameIds, parentGroupId, keywords, params, start,
			end);
	}

	/**
	 * Returns an ordered range of all the groups belonging to the parent group
	 * that match the class name IDs and keywords, optionally including the
	 * user's inherited organization groups and user groups. System and staged
	 * groups are not included.
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
	 * @param companyId the primary key of the company
	 * @param classNameIds the primary keys of the class names of the entities
	 the groups are related to (optionally <code>null</code>)
	 * @param parentGroupId the primary key of the parent group
	 * @param keywords the keywords (space separated), which may occur in the
	 sites's name, or description (optionally <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). To
	 include a user's organizations, inherited organizations, and user
	 groups in the search, add an entry with key
	 &quot;usersGroups&quot; mapped to the user's ID and an entry with
	 key &quot;inherit&quot; mapped to a non-<code>null</code> object.
	 For more information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param start the lower bound of the range of groups to return
	 * @param end the upper bound of the range of groups to return (not
	 inclusive)
	 * @param orderByComparator the comparator to order the groups (optionally
	 <code>null</code>)
	 * @return the matching groups ordered by comparator
	 <code>orderByComparator</code>
	 */
	public static List<Group> search(
		long companyId, long[] classNameIds, long parentGroupId,
		String keywords, java.util.LinkedHashMap<String, Object> params,
		int start, int end, OrderByComparator<Group> orderByComparator) {

		return getService().search(
			companyId, classNameIds, parentGroupId, keywords, params, start,
			end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the groups belonging to the parent group
	 * that match the class name IDs, name, and description, optionally
	 * including the user's inherited organization groups and user groups.
	 * System and staged groups are not included.
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
	 * @param companyId the primary key of the company
	 * @param classNameIds the primary keys of the class names of the entities
	 the groups are related to (optionally <code>null</code>)
	 * @param parentGroupId the primary key of the parent group
	 * @param name the group's name (optionally <code>null</code>)
	 * @param description the group's description (optionally
	 <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). To
	 include a user's organizations, inherited organizations, and user
	 groups in the search, add an entry with key
	 &quot;usersGroups&quot; mapped to the user's ID and an entry with
	 key &quot;inherit&quot; mapped to a non-<code>null</code> object.
	 For more information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param andOperator whether every field must match its keywords, or just
	 one field.
	 * @param start the lower bound of the range of groups to return
	 * @param end the upper bound of the range of groups to return (not
	 inclusive)
	 * @return the matching groups ordered by name
	 */
	public static List<Group> search(
		long companyId, long[] classNameIds, long parentGroupId, String name,
		String description, java.util.LinkedHashMap<String, Object> params,
		boolean andOperator, int start, int end) {

		return getService().search(
			companyId, classNameIds, parentGroupId, name, description, params,
			andOperator, start, end);
	}

	/**
	 * Returns an ordered range of all the groups belonging to the parent group
	 * that match the class name IDs, name, and description, optionally
	 * including the user's inherited organization groups and user groups.
	 * System and staged groups are not included.
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
	 * @param companyId the primary key of the company
	 * @param classNameIds the primary keys of the class names of the entities
	 the groups are related to (optionally <code>null</code>)
	 * @param parentGroupId the primary key of the parent group
	 * @param name the group's name (optionally <code>null</code>)
	 * @param description the group's description (optionally
	 <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). To
	 include a user's organizations, inherited organizations, and user
	 groups in the search, add an entry with key
	 &quot;usersGroups&quot; mapped to the user's ID and an entry with
	 key &quot;inherit&quot; mapped to a non-<code>null</code> object.
	 For more information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param andOperator whether every field must match its keywords, or just
	 one field.
	 * @param start the lower bound of the range of groups to return
	 * @param end the upper bound of the range of groups to return (not
	 inclusive)
	 * @param orderByComparator the comparator to order the groups (optionally
	 <code>null</code>)
	 * @return the matching groups ordered by comparator
	 <code>orderByComparator</code>
	 */
	public static List<Group> search(
		long companyId, long[] classNameIds, long parentGroupId, String name,
		String description, java.util.LinkedHashMap<String, Object> params,
		boolean andOperator, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return getService().search(
			companyId, classNameIds, parentGroupId, name, description, params,
			andOperator, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the groups that match the class name IDs
	 * and keywords, optionally including the user's inherited organization
	 * groups and user groups. System and staged groups are not included.
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
	 * @param companyId the primary key of the company
	 * @param classNameIds the primary keys of the class names of the entities
	 the groups are related to (optionally <code>null</code>)
	 * @param keywords the keywords (space separated), which may occur in the
	 sites's name, or description (optionally <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). To
	 include a user's organizations, inherited organizations, and user
	 groups in the search, add an entry with key
	 &quot;usersGroups&quot; mapped to the user's ID and an entry with
	 key &quot;inherit&quot; mapped to a non-<code>null</code> object.
	 For more information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param start the lower bound of the range of groups to return
	 * @param end the upper bound of the range of groups to return (not
	 inclusive)
	 * @return the matching groups ordered by name
	 */
	public static List<Group> search(
		long companyId, long[] classNameIds, String keywords,
		java.util.LinkedHashMap<String, Object> params, int start, int end) {

		return getService().search(
			companyId, classNameIds, keywords, params, start, end);
	}

	/**
	 * Returns an ordered range of all the groups that match the class name IDs
	 * and keywords, optionally including the user's inherited organization
	 * groups and user groups. System and staged groups are not included.
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
	 * @param companyId the primary key of the company
	 * @param classNameIds the primary keys of the class names of the entities
	 the groups are related to (optionally <code>null</code>)
	 * @param keywords the keywords (space separated), which may occur in the
	 sites's name, or description (optionally <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). To
	 include a user's organizations, inherited organizations, and user
	 groups in the search, add an entry with key
	 &quot;usersGroups&quot; mapped to the user's ID and an entry with
	 key &quot;inherit&quot; mapped to a non-<code>null</code> object.
	 For more information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param start the lower bound of the range of groups to return
	 * @param end the upper bound of the range of groups to return (not
	 inclusive)
	 * @param orderByComparator the comparator to order the groups (optionally
	 <code>null</code>)
	 * @return the matching groups ordered by comparator
	 <code>orderByComparator</code>
	 */
	public static List<Group> search(
		long companyId, long[] classNameIds, String keywords,
		java.util.LinkedHashMap<String, Object> params, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return getService().search(
			companyId, classNameIds, keywords, params, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the groups that match the class name IDs,
	 * name, and description, optionally including the user's inherited
	 * organization groups and user groups. System and staged groups are not
	 * included.
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
	 * @param companyId the primary key of the company
	 * @param classNameIds the primary keys of the class names of the entities
	 the groups are related to (optionally <code>null</code>)
	 * @param name the group's name (optionally <code>null</code>)
	 * @param description the group's description (optionally
	 <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). To
	 include a user's organizations, inherited organizations, and user
	 groups in the search, add an entry with key
	 &quot;usersGroups&quot; mapped to the user's ID and an entry with
	 key &quot;inherit&quot; mapped to a non-<code>null</code> object.
	 For more information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param andOperator whether every field must match its keywords, or just
	 one field.
	 * @param start the lower bound of the range of groups to return
	 * @param end the upper bound of the range of groups to return (not
	 inclusive)
	 * @return the matching groups ordered by name
	 */
	public static List<Group> search(
		long companyId, long[] classNameIds, String name, String description,
		java.util.LinkedHashMap<String, Object> params, boolean andOperator,
		int start, int end) {

		return getService().search(
			companyId, classNameIds, name, description, params, andOperator,
			start, end);
	}

	/**
	 * Returns an ordered range of all the groups that match the class name IDs,
	 * name, and description, optionally including the user's inherited
	 * organization groups and user groups. System and staged groups are not
	 * included.
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
	 * @param companyId the primary key of the company
	 * @param classNameIds the primary keys of the class names of the entities
	 the groups are related to (optionally <code>null</code>)
	 * @param name the group's name (optionally <code>null</code>)
	 * @param description the group's description (optionally
	 <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). To
	 include a user's organizations, inherited organizations, and user
	 groups in the search, add an entry with key
	 &quot;usersGroups&quot; mapped to the user's ID and an entry with
	 key &quot;inherit&quot; mapped to a non-<code>null</code> object.
	 For more information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param andOperator whether every field must match its keywords, or just
	 one field.
	 * @param start the lower bound of the range of groups to return
	 * @param end the upper bound of the range of groups to return (not
	 inclusive)
	 * @param orderByComparator the comparator to order the groups (optionally
	 <code>null</code>)
	 * @return the matching groups ordered by comparator
	 <code>orderByComparator</code>
	 */
	public static List<Group> search(
		long companyId, long[] classNameIds, String name, String description,
		java.util.LinkedHashMap<String, Object> params, boolean andOperator,
		int start, int end, OrderByComparator<Group> orderByComparator) {

		return getService().search(
			companyId, classNameIds, name, description, params, andOperator,
			start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the groups that match the keywords,
	 * optionally including the user's inherited organization groups and user
	 * groups. System and staged groups are not included.
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
	 * @param companyId the primary key of the company
	 * @param keywords the keywords (space separated), which may occur in the
	 sites's name, or description (optionally <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). To
	 include the user's inherited organizations and user groups in the
	 search, add entries having &quot;usersGroups&quot; and
	 &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param start the lower bound of the range of groups to return
	 * @param end the upper bound of the range of groups to return (not
	 inclusive)
	 * @return the matching groups ordered by name
	 */
	public static List<Group> search(
		long companyId, String keywords,
		java.util.LinkedHashMap<String, Object> params, int start, int end) {

		return getService().search(companyId, keywords, params, start, end);
	}

	/**
	 * Returns an ordered range of all the groups that match the keywords,
	 * optionally including the user's inherited organization groups and user
	 * groups. System and staged groups are not included.
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
	 * @param companyId the primary key of the company
	 * @param keywords the keywords (space separated), which may occur in the
	 sites's name, or description (optionally <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). To
	 include the user's inherited organizations and user groups in the
	 search, add entries having &quot;usersGroups&quot; and
	 &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param start the lower bound of the range of groups to return
	 * @param end the upper bound of the range of groups to return (not
	 inclusive)
	 * @param orderByComparator the comparator to order the groups (optionally
	 <code>null</code>)
	 * @return the matching groups ordered by comparator
	 <code>orderByComparator</code>
	 */
	public static List<Group> search(
		long companyId, String keywords,
		java.util.LinkedHashMap<String, Object> params, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return getService().search(
			companyId, keywords, params, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the site groups and organization groups
	 * that match the name and description, optionally including the user's
	 * inherited organization groups and user groups. System and staged groups
	 * are not included.
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
	 * @param companyId the primary key of the company
	 * @param name the group's name (optionally <code>null</code>)
	 * @param description the group's description (optionally
	 <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). To
	 include the user's inherited organizations and user groups in the
	 search, add entries having &quot;usersGroups&quot; and
	 &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param andOperator whether every field must match its keywords, or just
	 one field.
	 * @param start the lower bound of the range of groups to return
	 * @param end the upper bound of the range of groups to return (not
	 inclusive)
	 * @return the matching groups ordered by name
	 */
	public static List<Group> search(
		long companyId, String name, String description,
		java.util.LinkedHashMap<String, Object> params, boolean andOperator,
		int start, int end) {

		return getService().search(
			companyId, name, description, params, andOperator, start, end);
	}

	/**
	 * Returns an ordered range of all the site groups and organization groups
	 * that match the name and description, optionally including the user's
	 * inherited organization groups and user groups. System and staged groups
	 * are not included.
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
	 * @param companyId the primary key of the company
	 * @param name the group's name (optionally <code>null</code>)
	 * @param description the group's description (optionally
	 <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). To
	 include the user's inherited organizations and user groups in the
	 search, add entries having &quot;usersGroups&quot; and
	 &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param andOperator whether every field must match its keywords, or just
	 one field.
	 * @param start the lower bound of the range of groups to return
	 * @param end the upper bound of the range of groups to return (not
	 inclusive)
	 * @param orderByComparator the comparator to order the groups (optionally
	 <code>null</code>)
	 * @return the matching groups ordered by comparator
	 <code>orderByComparator</code>
	 */
	public static List<Group> search(
		long companyId, String name, String description,
		java.util.LinkedHashMap<String, Object> params, boolean andOperator,
		int start, int end, OrderByComparator<Group> orderByComparator) {

		return getService().search(
			companyId, name, description, params, andOperator, start, end,
			orderByComparator);
	}

	/**
	 * Returns the number of groups belonging to the parent group that match the
	 * keywords, optionally including the user's inherited organization groups
	 * and user groups. System and staged groups are not included.
	 *
	 * @param companyId the primary key of the company
	 * @param parentGroupId the primary key of the parent group
	 * @param keywords the keywords (space separated), which may occur in the
	 sites's name, or description (optionally <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). To
	 include the user's inherited organization groups and user groups
	 in the search, add entries having &quot;usersGroups&quot; and
	 &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @return the number of matching groups
	 */
	public static int searchCount(
		long companyId, long parentGroupId, String keywords,
		java.util.LinkedHashMap<String, Object> params) {

		return getService().searchCount(
			companyId, parentGroupId, keywords, params);
	}

	/**
	 * Returns the number of groups belonging to the parent group and immediate
	 * organization groups that match the name and description, optionally
	 * including the user's inherited organization groups and user groups.
	 * System and staged groups are not included.
	 *
	 * @param companyId the primary key of the company
	 * @param parentGroupId the primary key of the parent group
	 * @param name the group's name (optionally <code>null</code>)
	 * @param description the group's description (optionally
	 <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). To
	 include the user's inherited organization groups and user groups
	 in the search, add entries having &quot;usersGroups&quot; and
	 &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param andOperator whether every field must match its keywords, or just
	 one field.
	 * @return the number of matching groups
	 */
	public static int searchCount(
		long companyId, long parentGroupId, String name, String description,
		java.util.LinkedHashMap<String, Object> params, boolean andOperator) {

		return getService().searchCount(
			companyId, parentGroupId, name, description, params, andOperator);
	}

	/**
	 * Returns the number of groups belonging to the parent group that match the
	 * class name IDs, and keywords, optionally including the user's inherited
	 * organization groups and user groups. System and staged groups are not
	 * included.
	 *
	 * @param companyId the primary key of the company
	 * @param classNameIds the primary keys of the class names of the entities
	 the groups are related to (optionally <code>null</code>)
	 * @param parentGroupId the primary key of the parent group
	 * @param keywords the keywords (space separated), which may occur in the
	 sites's name, or description (optionally <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). To
	 include the user's inherited organization groups and user groups
	 in the search, add entries having &quot;usersGroups&quot; and
	 &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @return the number of matching groups
	 */
	public static int searchCount(
		long companyId, long[] classNameIds, long parentGroupId,
		String keywords, java.util.LinkedHashMap<String, Object> params) {

		return getService().searchCount(
			companyId, classNameIds, parentGroupId, keywords, params);
	}

	/**
	 * Returns the number of groups belonging to the parent group that match the
	 * class name IDs, name, and description, optionally including the user's
	 * inherited organization groups and user groups. System and staged groups
	 * are not included.
	 *
	 * @param companyId the primary key of the company
	 * @param classNameIds the primary keys of the class names of the entities
	 the groups are related to (optionally <code>null</code>)
	 * @param parentGroupId the primary key of the parent group
	 * @param name the group's name (optionally <code>null</code>)
	 * @param description the group's description (optionally
	 <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). To
	 include the user's inherited organization groups and user groups
	 in the search, add entries having &quot;usersGroups&quot; and
	 &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param andOperator whether every field must match its keywords, or just
	 one field.
	 * @return the number of matching groups
	 */
	public static int searchCount(
		long companyId, long[] classNameIds, long parentGroupId, String name,
		String description, java.util.LinkedHashMap<String, Object> params,
		boolean andOperator) {

		return getService().searchCount(
			companyId, classNameIds, parentGroupId, name, description, params,
			andOperator);
	}

	/**
	 * Returns the number of groups that match the class name IDs, and keywords,
	 * optionally including the user's inherited organization groups and user
	 * groups. System and staged groups are not included.
	 *
	 * @param companyId the primary key of the company
	 * @param classNameIds the primary keys of the class names of the entities
	 the groups are related to (optionally <code>null</code>)
	 * @param keywords the keywords (space separated), which may occur in the
	 sites's name, or description (optionally <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). To
	 include the user's inherited organization groups and user groups
	 in the search, add entries having &quot;usersGroups&quot; and
	 &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @return the number of matching groups
	 */
	public static int searchCount(
		long companyId, long[] classNameIds, String keywords,
		java.util.LinkedHashMap<String, Object> params) {

		return getService().searchCount(
			companyId, classNameIds, keywords, params);
	}

	/**
	 * Returns the number of groups that match the class name IDs, name, and
	 * description, optionally including the user's inherited organization
	 * groups and user groups. System and staged groups are not included.
	 *
	 * @param companyId the primary key of the company
	 * @param classNameIds the primary keys of the class names of the entities
	 the groups are related to (optionally <code>null</code>)
	 * @param name the group's name (optionally <code>null</code>)
	 * @param description the group's description (optionally
	 <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). To
	 include the user's inherited organization groups and user groups
	 in the search, add entries having &quot;usersGroups&quot; and
	 &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param andOperator whether every field must match its keywords, or just
	 one field.
	 * @return the number of matching groups
	 */
	public static int searchCount(
		long companyId, long[] classNameIds, String name, String description,
		java.util.LinkedHashMap<String, Object> params, boolean andOperator) {

		return getService().searchCount(
			companyId, classNameIds, name, description, params, andOperator);
	}

	/**
	 * Returns the number of groups that match the keywords, optionally
	 * including the user's inherited organization groups and user groups.
	 * System and staged groups are not included.
	 *
	 * @param companyId the primary key of the company
	 * @param keywords the keywords (space separated), which may occur in the
	 sites's name, or description (optionally <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). To
	 include the user's inherited organization groups and user groups
	 in the search, add entries having &quot;usersGroups&quot; and
	 &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @return the number of matching groups
	 */
	public static int searchCount(
		long companyId, String keywords,
		java.util.LinkedHashMap<String, Object> params) {

		return getService().searchCount(companyId, keywords, params);
	}

	/**
	 * Returns the number of groups and immediate organization groups that match
	 * the name and description, optionally including the user's inherited
	 * organization groups and user groups. System and staged groups are not
	 * included.
	 *
	 * @param companyId the primary key of the company
	 * @param name the group's name (optionally <code>null</code>)
	 * @param description the group's description (optionally
	 <code>null</code>)
	 * @param params the finder params (optionally <code>null</code>). To
	 include the user's inherited organization groups and user groups
	 in the search, add entries having &quot;usersGroups&quot; and
	 &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 information see {@link
	 com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param andOperator whether every field must match its keywords, or just
	 one field.
	 * @return the number of matching groups
	 */
	public static int searchCount(
		long companyId, String name, String description,
		java.util.LinkedHashMap<String, Object> params, boolean andOperator) {

		return getService().searchCount(
			companyId, name, description, params, andOperator);
	}

	public static void setOrganizationGroups(
		long organizationId, long[] groupIds) {

		getService().setOrganizationGroups(organizationId, groupIds);
	}

	public static void setRoleGroups(long roleId, long[] groupIds) {
		getService().setRoleGroups(roleId, groupIds);
	}

	public static void setUserGroupGroups(long userGroupId, long[] groupIds) {
		getService().setUserGroupGroups(userGroupId, groupIds);
	}

	public static void setUserGroups(long userId, long[] groupIds) {
		getService().setUserGroups(userId, groupIds);
	}

	/**
	 * Removes the groups from the role.
	 *
	 * @param roleId the primary key of the role
	 * @param groupIds the primary keys of the groups
	 */
	public static void unsetRoleGroups(long roleId, long[] groupIds) {
		getService().unsetRoleGroups(roleId, groupIds);
	}

	/**
	 * Removes the user from the groups.
	 *
	 * @param userId the primary key of the user
	 * @param groupIds the primary keys of the groups
	 */
	public static void unsetUserGroups(long userId, long[] groupIds) {
		getService().unsetUserGroups(userId, groupIds);
	}

	/**
	 * Updates the group's asset replacing categories and tag names.
	 *
	 * @param userId the primary key of the user
	 * @param group the group
	 * @param assetCategoryIds the primary keys of the asset categories
	 (optionally <code>null</code>)
	 * @param assetTagNames the asset tag names (optionally <code>null</code>)
	 * @throws PortalException if a portal exception occurred
	 */
	public static void updateAsset(
			long userId, Group group, long[] assetCategoryIds,
			String[] assetTagNames)
		throws PortalException {

		getService().updateAsset(
			userId, group, assetCategoryIds, assetTagNames);
	}

	/**
	 * Updates the group's friendly URL.
	 *
	 * @param groupId the primary key of the group
	 * @param friendlyURL the group's new friendlyURL (optionally
	 <code>null</code>)
	 * @return the group
	 * @throws PortalException if a portal exception occurred
	 */
	public static Group updateFriendlyURL(long groupId, String friendlyURL)
		throws PortalException {

		return getService().updateFriendlyURL(groupId, friendlyURL);
	}

	/**
	 * Updates the group in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect GroupLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param group the group
	 * @return the group that was updated
	 */
	public static Group updateGroup(Group group) {
		return getService().updateGroup(group);
	}

	public static Group updateGroup(
			long groupId, long parentGroupId,
			Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap, int type,
			boolean manualMembership, int membershipRestriction,
			String friendlyURL, boolean inheritContent, boolean active,
			ServiceContext serviceContext)
		throws PortalException {

		return getService().updateGroup(
			groupId, parentGroupId, nameMap, descriptionMap, type,
			manualMembership, membershipRestriction, friendlyURL,
			inheritContent, active, serviceContext);
	}

	/**
	 * Updates the group's type settings.
	 *
	 * @param groupId the primary key of the group
	 * @param typeSettings the group's new type settings (optionally
	 <code>null</code>)
	 * @return the group
	 * @throws PortalException if a portal exception occurred
	 */
	public static Group updateGroup(long groupId, String typeSettings)
		throws PortalException {

		return getService().updateGroup(groupId, typeSettings);
	}

	/**
	 * Associates the group with a main site if the group is an organization.
	 *
	 * @param groupId the primary key of the group
	 * @param site whether the group is to be associated with a main site
	 * @return the group
	 * @throws PortalException if a portal exception occurred
	 */
	public static Group updateSite(long groupId, boolean site)
		throws PortalException {

		return getService().updateSite(groupId, site);
	}

	public static void validateRemote(
			long groupId, String remoteAddress, int remotePort,
			String remotePathContext, boolean secureConnection,
			long remoteGroupId)
		throws PortalException {

		getService().validateRemote(
			groupId, remoteAddress, remotePort, remotePathContext,
			secureConnection, remoteGroupId);
	}

	public static GroupLocalService getService() {
		return _service;
	}

	public static void setService(GroupLocalService service) {
		_service = service;
	}

	private static volatile GroupLocalService _service;

}