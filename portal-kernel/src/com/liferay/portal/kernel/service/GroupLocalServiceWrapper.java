/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link GroupLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see GroupLocalService
 * @generated
 */
public class GroupLocalServiceWrapper
	implements GroupLocalService, ServiceWrapper<GroupLocalService> {

	public GroupLocalServiceWrapper() {
		this(null);
	}

	public GroupLocalServiceWrapper(GroupLocalService groupLocalService) {
		_groupLocalService = groupLocalService;
	}

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
	@Override
	public Group addGroup(Group group) {
		return _groupLocalService.addGroup(group);
	}

	@Override
	public Group addGroup(
			long userId, long parentGroupId, String className, long classPK,
			long liveGroupId, java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap, int type,
			boolean manualMembership, int membershipRestriction,
			String friendlyURL, boolean site, boolean inheritContent,
			boolean active, ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _groupLocalService.addGroup(
			userId, parentGroupId, className, classPK, liveGroupId, nameMap,
			descriptionMap, type, manualMembership, membershipRestriction,
			friendlyURL, site, inheritContent, active, serviceContext);
	}

	@Override
	public Group addGroup(
			long userId, long parentGroupId, String className, long classPK,
			long liveGroupId, java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap, int type,
			boolean manualMembership, int membershipRestriction,
			String friendlyURL, boolean site, boolean active,
			ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _groupLocalService.addGroup(
			userId, parentGroupId, className, classPK, liveGroupId, nameMap,
			descriptionMap, type, manualMembership, membershipRestriction,
			friendlyURL, site, active, serviceContext);
	}

	@Override
	public boolean addOrganizationGroup(long organizationId, Group group) {
		return _groupLocalService.addOrganizationGroup(organizationId, group);
	}

	@Override
	public boolean addOrganizationGroup(long organizationId, long groupId) {
		return _groupLocalService.addOrganizationGroup(organizationId, groupId);
	}

	@Override
	public boolean addOrganizationGroups(
		long organizationId, java.util.List<Group> groups) {

		return _groupLocalService.addOrganizationGroups(organizationId, groups);
	}

	@Override
	public boolean addOrganizationGroups(long organizationId, long[] groupIds) {
		return _groupLocalService.addOrganizationGroups(
			organizationId, groupIds);
	}

	@Override
	public Group addOrUpdateGroup(
			String externalReferenceCode, long userId, long parentGroupId,
			String className, long classPK, long liveGroupId,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap, int type,
			boolean manualMembership, int membershipRestriction,
			String friendlyURL, boolean site, boolean inheritContent,
			boolean active, ServiceContext serviceContext)
		throws Exception {

		return _groupLocalService.addOrUpdateGroup(
			externalReferenceCode, userId, parentGroupId, className, classPK,
			liveGroupId, nameMap, descriptionMap, type, manualMembership,
			membershipRestriction, friendlyURL, site, inheritContent, active,
			serviceContext);
	}

	@Override
	public boolean addRoleGroup(long roleId, Group group) {
		return _groupLocalService.addRoleGroup(roleId, group);
	}

	@Override
	public boolean addRoleGroup(long roleId, long groupId) {
		return _groupLocalService.addRoleGroup(roleId, groupId);
	}

	@Override
	public boolean addRoleGroups(long roleId, java.util.List<Group> groups) {
		return _groupLocalService.addRoleGroups(roleId, groups);
	}

	@Override
	public boolean addRoleGroups(long roleId, long[] groupIds) {
		return _groupLocalService.addRoleGroups(roleId, groupIds);
	}

	@Override
	public boolean addUserGroup(long userId, Group group) {
		return _groupLocalService.addUserGroup(userId, group);
	}

	@Override
	public boolean addUserGroup(long userId, long groupId) {
		return _groupLocalService.addUserGroup(userId, groupId);
	}

	@Override
	public boolean addUserGroupGroup(long userGroupId, Group group) {
		return _groupLocalService.addUserGroupGroup(userGroupId, group);
	}

	@Override
	public boolean addUserGroupGroup(long userGroupId, long groupId) {
		return _groupLocalService.addUserGroupGroup(userGroupId, groupId);
	}

	@Override
	public boolean addUserGroupGroups(
		long userGroupId, java.util.List<Group> groups) {

		return _groupLocalService.addUserGroupGroups(userGroupId, groups);
	}

	@Override
	public boolean addUserGroupGroups(long userGroupId, long[] groupIds) {
		return _groupLocalService.addUserGroupGroups(userGroupId, groupIds);
	}

	@Override
	public boolean addUserGroups(long userId, java.util.List<Group> groups) {
		return _groupLocalService.addUserGroups(userId, groups);
	}

	@Override
	public boolean addUserGroups(long userId, long[] groupIds) {
		return _groupLocalService.addUserGroups(userId, groupIds);
	}

	@Override
	public Group checkScopeGroup(
			com.liferay.portal.kernel.model.Layout layout, long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _groupLocalService.checkScopeGroup(layout, userId);
	}

	/**
	 * Creates systems groups and other related data needed by the system on the
	 * very first startup. Also takes care of creating the Control Panel groups
	 * and layouts.
	 *
	 * @param companyId the primary key of the company
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void checkSystemGroups(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_groupLocalService.checkSystemGroups(companyId);
	}

	@Override
	public void clearOrganizationGroups(long organizationId) {
		_groupLocalService.clearOrganizationGroups(organizationId);
	}

	@Override
	public void clearRoleGroups(long roleId) {
		_groupLocalService.clearRoleGroups(roleId);
	}

	@Override
	public void clearUserGroupGroups(long userGroupId) {
		_groupLocalService.clearUserGroupGroups(userGroupId);
	}

	@Override
	public void clearUserGroups(long userId) {
		_groupLocalService.clearUserGroups(userId);
	}

	/**
	 * Creates a new group with the primary key. Does not add the group to the database.
	 *
	 * @param groupId the primary key for the new group
	 * @return the new group
	 */
	@Override
	public Group createGroup(long groupId) {
		return _groupLocalService.createGroup(groupId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _groupLocalService.createPersistedModel(primaryKeyObj);
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
	@Override
	public Group deleteGroup(Group group)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _groupLocalService.deleteGroup(group);
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
	@Override
	public Group deleteGroup(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _groupLocalService.deleteGroup(groupId);
	}

	@Override
	public void deleteOrganizationGroup(long organizationId, Group group) {
		_groupLocalService.deleteOrganizationGroup(organizationId, group);
	}

	@Override
	public void deleteOrganizationGroup(long organizationId, long groupId) {
		_groupLocalService.deleteOrganizationGroup(organizationId, groupId);
	}

	@Override
	public void deleteOrganizationGroups(
		long organizationId, java.util.List<Group> groups) {

		_groupLocalService.deleteOrganizationGroups(organizationId, groups);
	}

	@Override
	public void deleteOrganizationGroups(long organizationId, long[] groupIds) {
		_groupLocalService.deleteOrganizationGroups(organizationId, groupIds);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _groupLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public void deleteRoleGroup(long roleId, Group group) {
		_groupLocalService.deleteRoleGroup(roleId, group);
	}

	@Override
	public void deleteRoleGroup(long roleId, long groupId) {
		_groupLocalService.deleteRoleGroup(roleId, groupId);
	}

	@Override
	public void deleteRoleGroups(long roleId, java.util.List<Group> groups) {
		_groupLocalService.deleteRoleGroups(roleId, groups);
	}

	@Override
	public void deleteRoleGroups(long roleId, long[] groupIds) {
		_groupLocalService.deleteRoleGroups(roleId, groupIds);
	}

	@Override
	public void deleteUserGroup(long userId, Group group) {
		_groupLocalService.deleteUserGroup(userId, group);
	}

	@Override
	public void deleteUserGroup(long userId, long groupId) {
		_groupLocalService.deleteUserGroup(userId, groupId);
	}

	@Override
	public void deleteUserGroupGroup(long userGroupId, Group group) {
		_groupLocalService.deleteUserGroupGroup(userGroupId, group);
	}

	@Override
	public void deleteUserGroupGroup(long userGroupId, long groupId) {
		_groupLocalService.deleteUserGroupGroup(userGroupId, groupId);
	}

	@Override
	public void deleteUserGroupGroups(
		long userGroupId, java.util.List<Group> groups) {

		_groupLocalService.deleteUserGroupGroups(userGroupId, groups);
	}

	@Override
	public void deleteUserGroupGroups(long userGroupId, long[] groupIds) {
		_groupLocalService.deleteUserGroupGroups(userGroupId, groupIds);
	}

	@Override
	public void deleteUserGroups(long userId, java.util.List<Group> groups) {
		_groupLocalService.deleteUserGroups(userId, groups);
	}

	@Override
	public void deleteUserGroups(long userId, long[] groupIds) {
		_groupLocalService.deleteUserGroups(userId, groupIds);
	}

	@Override
	public void disableStaging(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_groupLocalService.disableStaging(groupId);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _groupLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _groupLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _groupLocalService.dynamicQuery();
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

		return _groupLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _groupLocalService.dynamicQuery(dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _groupLocalService.dynamicQuery(
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

		return _groupLocalService.dynamicQueryCount(dynamicQuery);
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

		return _groupLocalService.dynamicQueryCount(dynamicQuery, projection);
	}

	@Override
	public void enableStaging(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_groupLocalService.enableStaging(groupId);
	}

	/**
	 * Returns the company's group.
	 *
	 * @param companyId the primary key of the company
	 * @return the company's group, or <code>null</code> if a matching group
	 could not be found
	 */
	@Override
	public Group fetchCompanyGroup(long companyId) {
		return _groupLocalService.fetchCompanyGroup(companyId);
	}

	/**
	 * Returns the group with the matching friendly URL.
	 *
	 * @param companyId the primary key of the company
	 * @param friendlyURL the friendly URL
	 * @return the group with the friendly URL, or <code>null</code> if a
	 matching group could not be found
	 */
	@Override
	public Group fetchFriendlyURLGroup(long companyId, String friendlyURL) {
		return _groupLocalService.fetchFriendlyURLGroup(companyId, friendlyURL);
	}

	@Override
	public Group fetchGroup(long groupId) {
		return _groupLocalService.fetchGroup(groupId);
	}

	@Override
	public Group fetchGroup(long companyId, long classNameId, long classPK) {
		return _groupLocalService.fetchGroup(companyId, classNameId, classPK);
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
	@Override
	public Group fetchGroup(long companyId, String groupKey) {
		return _groupLocalService.fetchGroup(companyId, groupKey);
	}

	@Override
	public Group fetchGroupByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return _groupLocalService.fetchGroupByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the group with the matching UUID and company.
	 *
	 * @param uuid the group's UUID
	 * @param companyId the primary key of the company
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchGroupByUuidAndCompanyId(String uuid, long companyId) {
		return _groupLocalService.fetchGroupByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public Group fetchStagingGroup(long liveGroupId) {
		return _groupLocalService.fetchStagingGroup(liveGroupId);
	}

	@Override
	public Group fetchUserGroup(long companyId, long userId) {
		return _groupLocalService.fetchUserGroup(companyId, userId);
	}

	/**
	 * Returns the default user's personal site group.
	 *
	 * @param companyId the primary key of the company
	 * @return the default user's personal site group, or <code>null</code> if a
	 matching group could not be found
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Group fetchUserPersonalSiteGroup(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _groupLocalService.fetchUserPersonalSiteGroup(companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _groupLocalService.getActionableDynamicQuery();
	}

	@Override
	public java.util.List<Long> getActiveGroupIds(long userId) {
		return _groupLocalService.getActiveGroupIds(userId);
	}

	/**
	 * Returns all the active or inactive groups associated with the company.
	 *
	 * @param companyId the primary key of the company
	 * @param active whether to return only active groups or only inactive
	 groups
	 * @return the active or inactive groups
	 */
	@Override
	public java.util.List<Group> getActiveGroups(
		long companyId, boolean active) {

		return _groupLocalService.getActiveGroups(companyId, active);
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
	@Override
	public java.util.List<Group> getActiveGroups(
		long companyId, boolean site, boolean active, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator) {

		return _groupLocalService.getActiveGroups(
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
	@Override
	public java.util.List<Group> getActiveGroups(
		long companyId, boolean active, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator) {

		return _groupLocalService.getActiveGroups(
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
	@Override
	public int getActiveGroupsCount(long companyId, boolean active) {
		return _groupLocalService.getActiveGroupsCount(companyId, active);
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
	@Override
	public int getActiveGroupsCount(
		long companyId, boolean active, boolean site) {

		return _groupLocalService.getActiveGroupsCount(companyId, active, site);
	}

	/**
	 * Returns the company group.
	 *
	 * @param companyId the primary key of the company
	 * @return the group associated with the company
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Group getCompanyGroup(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _groupLocalService.getCompanyGroup(companyId);
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
	@Override
	public java.util.List<Group> getCompanyGroups(
		long companyId, int start, int end) {

		return _groupLocalService.getCompanyGroups(companyId, start, end);
	}

	/**
	 * Returns the number of groups associated with the company.
	 *
	 * @param companyId the primary key of the company
	 * @return the number of groups associated with the company
	 */
	@Override
	public int getCompanyGroupsCount(long companyId) {
		return _groupLocalService.getCompanyGroupsCount(companyId);
	}

	/**
	 * Returns the group with the matching friendly URL.
	 *
	 * @param companyId the primary key of the company
	 * @param friendlyURL the group's friendlyURL
	 * @return the group with the friendly URL
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Group getFriendlyURLGroup(long companyId, String friendlyURL)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _groupLocalService.getFriendlyURLGroup(companyId, friendlyURL);
	}

	/**
	 * Returns the group with the primary key.
	 *
	 * @param groupId the primary key of the group
	 * @return the group
	 * @throws PortalException if a group with the primary key could not be found
	 */
	@Override
	public Group getGroup(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _groupLocalService.getGroup(groupId);
	}

	/**
	 * Returns the group with the matching group key.
	 *
	 * @param companyId the primary key of the company
	 * @param groupKey the group key
	 * @return the group with the group key
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Group getGroup(long companyId, String groupKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _groupLocalService.getGroup(companyId, groupKey);
	}

	@Override
	public Group getGroupByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _groupLocalService.getGroupByExternalReferenceCode(
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
	@Override
	public Group getGroupByUuidAndCompanyId(String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _groupLocalService.getGroupByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public java.util.List<Long> getGroupIds(long companyId, boolean active) {
		return _groupLocalService.getGroupIds(companyId, active);
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
	@Override
	public java.util.List<Group> getGroups(int start, int end) {
		return _groupLocalService.getGroups(start, end);
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
	@Override
	public java.util.List<Group> getGroups(
		long companyId, long parentGroupId, boolean site) {

		return _groupLocalService.getGroups(companyId, parentGroupId, site);
	}

	@Override
	public java.util.List<Group> getGroups(
		long companyId, long parentGroupId, boolean site,
		boolean inheritContent) {

		return _groupLocalService.getGroups(
			companyId, parentGroupId, site, inheritContent);
	}

	@Override
	public java.util.List<Group> getGroups(
		long companyId, long parentGroupId, boolean site, int start, int end) {

		return _groupLocalService.getGroups(
			companyId, parentGroupId, site, start, end);
	}

	@Override
	public java.util.List<Group> getGroups(
		long companyId, long parentGroupId, String name, boolean site,
		int start, int end) {

		return _groupLocalService.getGroups(
			companyId, parentGroupId, name, site, start, end);
	}

	@Override
	public java.util.List<Group> getGroups(
		long companyId, String treePath, boolean site) {

		return _groupLocalService.getGroups(companyId, treePath, site);
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
	@Override
	public java.util.List<Group> getGroups(
		long companyId, String className, long parentGroupId) {

		return _groupLocalService.getGroups(
			companyId, className, parentGroupId);
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
	@Override
	public java.util.List<Group> getGroups(
		long companyId, String className, long parentGroupId, int start,
		int end) {

		return _groupLocalService.getGroups(
			companyId, className, parentGroupId, start, end);
	}

	/**
	 * Returns the groups with the matching primary keys.
	 *
	 * @param groupIds the primary keys of the groups
	 * @return the groups with the primary keys
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public java.util.List<Group> getGroups(long[] groupIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _groupLocalService.getGroups(groupIds);
	}

	/**
	 * Returns the number of groups.
	 *
	 * @return the number of groups
	 */
	@Override
	public int getGroupsCount() {
		return _groupLocalService.getGroupsCount();
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
	@Override
	public int getGroupsCount(
		long companyId, long parentGroupId, boolean site) {

		return _groupLocalService.getGroupsCount(
			companyId, parentGroupId, site);
	}

	@Override
	public int getGroupsCount(
		long companyId, long parentGroupId, String name, boolean site) {

		return _groupLocalService.getGroupsCount(
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
	@Override
	public int getGroupsCount(
		long companyId, String className, long parentGroupId) {

		return _groupLocalService.getGroupsCount(
			companyId, className, parentGroupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _groupLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the group associated with the layout.
	 *
	 * @param companyId the primary key of the company
	 * @param plid the primary key of the layout
	 * @return the group associated with the layout
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Group getLayoutGroup(long companyId, long plid)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _groupLocalService.getLayoutGroup(companyId, plid);
	}

	/**
	 * Returns the group associated with the layout prototype.
	 *
	 * @param companyId the primary key of the company
	 * @param layoutPrototypeId the primary key of the layout prototype
	 * @return the group associated with the layout prototype
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Group getLayoutPrototypeGroup(long companyId, long layoutPrototypeId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _groupLocalService.getLayoutPrototypeGroup(
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
	@Override
	public Group getLayoutSetPrototypeGroup(
			long companyId, long layoutSetPrototypeId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _groupLocalService.getLayoutSetPrototypeGroup(
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
	@Override
	public java.util.List<Group> getLayoutsGroups(
		long companyId, long parentGroupId, boolean site, boolean active,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator) {

		return _groupLocalService.getLayoutsGroups(
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
	@Override
	public java.util.List<Group> getLayoutsGroups(
		long companyId, long parentGroupId, boolean site, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator) {

		return _groupLocalService.getLayoutsGroups(
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
	@Override
	public int getLayoutsGroupsCount(
		long companyId, long parentGroupId, boolean site) {

		return _groupLocalService.getLayoutsGroupsCount(
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
	@Override
	public int getLayoutsGroupsCount(
		long companyId, long parentGroupId, boolean site, boolean active) {

		return _groupLocalService.getLayoutsGroupsCount(
			companyId, parentGroupId, site, active);
	}

	/**
	 * Returns all live groups.
	 *
	 * @return all live groups
	 */
	@Override
	public java.util.List<Group> getLiveGroups() {
		return _groupLocalService.getLiveGroups();
	}

	/**
	 * Returns the specified organization group.
	 *
	 * @param companyId the primary key of the company
	 * @param organizationId the primary key of the organization
	 * @return the group associated with the organization
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Group getOrganizationGroup(long companyId, long organizationId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _groupLocalService.getOrganizationGroup(
			companyId, organizationId);
	}

	@Override
	public java.util.List<Group> getOrganizationGroups(long organizationId) {
		return _groupLocalService.getOrganizationGroups(organizationId);
	}

	@Override
	public java.util.List<Group> getOrganizationGroups(
		long organizationId, int start, int end) {

		return _groupLocalService.getOrganizationGroups(
			organizationId, start, end);
	}

	@Override
	public java.util.List<Group> getOrganizationGroups(
		long organizationId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator) {

		return _groupLocalService.getOrganizationGroups(
			organizationId, start, end, orderByComparator);
	}

	@Override
	public int getOrganizationGroupsCount(long organizationId) {
		return _groupLocalService.getOrganizationGroupsCount(organizationId);
	}

	/**
	 * Returns the organizationIds of the organizations associated with the group.
	 *
	 * @param groupId the groupId of the group
	 * @return long[] the organizationIds of organizations associated with the group
	 */
	@Override
	public long[] getOrganizationPrimaryKeys(long groupId) {
		return _groupLocalService.getOrganizationPrimaryKeys(groupId);
	}

	/**
	 * Returns the specified organization groups.
	 *
	 * @param organizations the organizations
	 * @return the groups associated with the organizations
	 */
	@Override
	public java.util.List<Group> getOrganizationsGroups(
		java.util.List<com.liferay.portal.kernel.model.Organization>
			organizations) {

		return _groupLocalService.getOrganizationsGroups(organizations);
	}

	/**
	 * Returns all the groups related to the organizations.
	 *
	 * @param organizations the organizations
	 * @return the groups related to the organizations
	 */
	@Override
	public java.util.List<Group> getOrganizationsRelatedGroups(
		java.util.List<com.liferay.portal.kernel.model.Organization>
			organizations) {

		return _groupLocalService.getOrganizationsRelatedGroups(organizations);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _groupLocalService.getOSGiServiceIdentifier();
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
	@Override
	public java.util.List<Group> getParentGroups(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _groupLocalService.getParentGroups(groupId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _groupLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public java.util.List<Group> getRoleGroups(long roleId) {
		return _groupLocalService.getRoleGroups(roleId);
	}

	@Override
	public java.util.List<Group> getRoleGroups(
		long roleId, int start, int end) {

		return _groupLocalService.getRoleGroups(roleId, start, end);
	}

	@Override
	public java.util.List<Group> getRoleGroups(
		long roleId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator) {

		return _groupLocalService.getRoleGroups(
			roleId, start, end, orderByComparator);
	}

	@Override
	public int getRoleGroupsCount(long roleId) {
		return _groupLocalService.getRoleGroupsCount(roleId);
	}

	/**
	 * Returns the roleIds of the roles associated with the group.
	 *
	 * @param groupId the groupId of the group
	 * @return long[] the roleIds of roles associated with the group
	 */
	@Override
	public long[] getRolePrimaryKeys(long groupId) {
		return _groupLocalService.getRolePrimaryKeys(groupId);
	}

	@Override
	public java.util.List<Group> getStagedSites() {
		return _groupLocalService.getStagedSites();
	}

	/**
	 * Returns the group directly associated with the user.
	 *
	 * @param companyId the primary key of the company
	 * @param userId the primary key of the user
	 * @return the group directly associated with the user
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Group getUserGroup(long companyId, long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _groupLocalService.getUserGroup(companyId, userId);
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
	@Override
	public Group getUserGroupGroup(long companyId, long userGroupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _groupLocalService.getUserGroupGroup(companyId, userGroupId);
	}

	@Override
	public java.util.List<Group> getUserGroupGroups(long userGroupId) {
		return _groupLocalService.getUserGroupGroups(userGroupId);
	}

	@Override
	public java.util.List<Group> getUserGroupGroups(
		long userGroupId, int start, int end) {

		return _groupLocalService.getUserGroupGroups(userGroupId, start, end);
	}

	@Override
	public java.util.List<Group> getUserGroupGroups(
		long userGroupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator) {

		return _groupLocalService.getUserGroupGroups(
			userGroupId, start, end, orderByComparator);
	}

	@Override
	public int getUserGroupGroupsCount(long userGroupId) {
		return _groupLocalService.getUserGroupGroupsCount(userGroupId);
	}

	/**
	 * Returns the userGroupIds of the user groups associated with the group.
	 *
	 * @param groupId the groupId of the group
	 * @return long[] the userGroupIds of user groups associated with the group
	 */
	@Override
	public long[] getUserGroupPrimaryKeys(long groupId) {
		return _groupLocalService.getUserGroupPrimaryKeys(groupId);
	}

	@Override
	public java.util.List<Group> getUserGroups(long userId) {
		return _groupLocalService.getUserGroups(userId);
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
	@Override
	public java.util.List<Group> getUserGroups(long userId, boolean inherit)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _groupLocalService.getUserGroups(userId, inherit);
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
	@Override
	public java.util.List<Group> getUserGroups(
			long userId, boolean inherit, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _groupLocalService.getUserGroups(userId, inherit, start, end);
	}

	@Override
	public java.util.List<Group> getUserGroups(
		long userId, int start, int end) {

		return _groupLocalService.getUserGroups(userId, start, end);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public java.util.List<Group> getUserGroups(
			long userId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<Group>
				orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _groupLocalService.getUserGroups(
			userId, start, end, orderByComparator);
	}

	@Override
	public int getUserGroupsCount(long userId) {
		return _groupLocalService.getUserGroupsCount(userId);
	}

	/**
	 * Returns the groups associated with the user groups.
	 *
	 * @param userGroups the user groups
	 * @return the groups associated with the user groups
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public java.util.List<Group> getUserGroupsGroups(
			java.util.List<com.liferay.portal.kernel.model.UserGroup>
				userGroups)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _groupLocalService.getUserGroupsGroups(userGroups);
	}

	/**
	 * Returns all the groups related to the user groups.
	 *
	 * @param userGroups the user groups
	 * @return the groups related to the user groups
	 */
	@Override
	public java.util.List<Group> getUserGroupsRelatedGroups(
		java.util.List<com.liferay.portal.kernel.model.UserGroup> userGroups) {

		return _groupLocalService.getUserGroupsRelatedGroups(userGroups);
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
	@Override
	public java.util.List<Group> getUserOrganizationsGroups(
			long userId, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _groupLocalService.getUserOrganizationsGroups(
			userId, start, end);
	}

	/**
	 * Returns the default user's personal site group.
	 *
	 * @param companyId the primary key of the company
	 * @return the default user's personal site group
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Group getUserPersonalSiteGroup(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _groupLocalService.getUserPersonalSiteGroup(companyId);
	}

	/**
	 * Returns the userIds of the users associated with the group.
	 *
	 * @param groupId the groupId of the group
	 * @return long[] the userIds of users associated with the group
	 */
	@Override
	public long[] getUserPrimaryKeys(long groupId) {
		return _groupLocalService.getUserPrimaryKeys(groupId);
	}

	@Override
	public java.util.List<Group> getUserSitesGroups(long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _groupLocalService.getUserSitesGroups(userId);
	}

	@Override
	public java.util.List<Group> getUserSitesGroups(
			long userId, boolean includeAdministrative)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _groupLocalService.getUserSitesGroups(
			userId, includeAdministrative);
	}

	@Override
	public java.util.List<Group> getUserSitesGroups(
			long userId, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _groupLocalService.getUserSitesGroups(userId, start, end);
	}

	@Override
	public boolean hasOrganizationGroup(long organizationId, long groupId) {
		return _groupLocalService.hasOrganizationGroup(organizationId, groupId);
	}

	@Override
	public boolean hasOrganizationGroups(long organizationId) {
		return _groupLocalService.hasOrganizationGroups(organizationId);
	}

	@Override
	public boolean hasRoleGroup(long roleId, long groupId) {
		return _groupLocalService.hasRoleGroup(roleId, groupId);
	}

	@Override
	public boolean hasRoleGroups(long roleId) {
		return _groupLocalService.hasRoleGroups(roleId);
	}

	@Override
	public boolean hasUserGroup(long userId, long groupId) {
		return _groupLocalService.hasUserGroup(userId, groupId);
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
	@Override
	public boolean hasUserGroup(long userId, long groupId, boolean inherit) {
		return _groupLocalService.hasUserGroup(userId, groupId, inherit);
	}

	@Override
	public boolean hasUserGroupGroup(long userGroupId, long groupId) {
		return _groupLocalService.hasUserGroupGroup(userGroupId, groupId);
	}

	@Override
	public boolean hasUserGroupGroups(long userGroupId) {
		return _groupLocalService.hasUserGroupGroups(userGroupId);
	}

	@Override
	public boolean hasUserGroups(long userId) {
		return _groupLocalService.hasUserGroups(userId);
	}

	@Override
	public boolean isLiveGroupActive(Group group) {
		return _groupLocalService.isLiveGroupActive(group);
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
	@Override
	public Group loadFetchGroup(long companyId, String groupKey) {
		return _groupLocalService.loadFetchGroup(companyId, groupKey);
	}

	/**
	 * Returns the group with the matching group key.
	 *
	 * @param companyId the primary key of the company
	 * @param groupKey the group key
	 * @return the group with the group key and associated company
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Group loadGetGroup(long companyId, String groupKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _groupLocalService.loadGetGroup(companyId, groupKey);
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
	@Override
	public void rebuildTree(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_groupLocalService.rebuildTree(companyId);
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
	@Override
	public java.util.List<Group> search(
		long companyId, java.util.LinkedHashMap<String, Object> params,
		int start, int end) {

		return _groupLocalService.search(companyId, params, start, end);
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
	@Override
	public java.util.List<Group> search(
		long companyId, long parentGroupId, String keywords,
		java.util.LinkedHashMap<String, Object> params, int start, int end) {

		return _groupLocalService.search(
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
	@Override
	public java.util.List<Group> search(
		long companyId, long parentGroupId, String keywords,
		java.util.LinkedHashMap<String, Object> params, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator) {

		return _groupLocalService.search(
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
	@Override
	public java.util.List<Group> search(
		long companyId, long parentGroupId, String name, String description,
		java.util.LinkedHashMap<String, Object> params, boolean andOperator,
		int start, int end) {

		return _groupLocalService.search(
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
	@Override
	public java.util.List<Group> search(
		long companyId, long parentGroupId, String name, String description,
		java.util.LinkedHashMap<String, Object> params, boolean andOperator,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator) {

		return _groupLocalService.search(
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
	@Override
	public java.util.List<Group> search(
		long companyId, long[] classNameIds, long parentGroupId,
		String keywords, java.util.LinkedHashMap<String, Object> params,
		int start, int end) {

		return _groupLocalService.search(
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
	@Override
	public java.util.List<Group> search(
		long companyId, long[] classNameIds, long parentGroupId,
		String keywords, java.util.LinkedHashMap<String, Object> params,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator) {

		return _groupLocalService.search(
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
	@Override
	public java.util.List<Group> search(
		long companyId, long[] classNameIds, long parentGroupId, String name,
		String description, java.util.LinkedHashMap<String, Object> params,
		boolean andOperator, int start, int end) {

		return _groupLocalService.search(
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
	@Override
	public java.util.List<Group> search(
		long companyId, long[] classNameIds, long parentGroupId, String name,
		String description, java.util.LinkedHashMap<String, Object> params,
		boolean andOperator, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator) {

		return _groupLocalService.search(
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
	@Override
	public java.util.List<Group> search(
		long companyId, long[] classNameIds, String keywords,
		java.util.LinkedHashMap<String, Object> params, int start, int end) {

		return _groupLocalService.search(
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
	@Override
	public java.util.List<Group> search(
		long companyId, long[] classNameIds, String keywords,
		java.util.LinkedHashMap<String, Object> params, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator) {

		return _groupLocalService.search(
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
	@Override
	public java.util.List<Group> search(
		long companyId, long[] classNameIds, String name, String description,
		java.util.LinkedHashMap<String, Object> params, boolean andOperator,
		int start, int end) {

		return _groupLocalService.search(
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
	@Override
	public java.util.List<Group> search(
		long companyId, long[] classNameIds, String name, String description,
		java.util.LinkedHashMap<String, Object> params, boolean andOperator,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator) {

		return _groupLocalService.search(
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
	@Override
	public java.util.List<Group> search(
		long companyId, String keywords,
		java.util.LinkedHashMap<String, Object> params, int start, int end) {

		return _groupLocalService.search(
			companyId, keywords, params, start, end);
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
	@Override
	public java.util.List<Group> search(
		long companyId, String keywords,
		java.util.LinkedHashMap<String, Object> params, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator) {

		return _groupLocalService.search(
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
	@Override
	public java.util.List<Group> search(
		long companyId, String name, String description,
		java.util.LinkedHashMap<String, Object> params, boolean andOperator,
		int start, int end) {

		return _groupLocalService.search(
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
	@Override
	public java.util.List<Group> search(
		long companyId, String name, String description,
		java.util.LinkedHashMap<String, Object> params, boolean andOperator,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator) {

		return _groupLocalService.search(
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
	@Override
	public int searchCount(
		long companyId, long parentGroupId, String keywords,
		java.util.LinkedHashMap<String, Object> params) {

		return _groupLocalService.searchCount(
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
	@Override
	public int searchCount(
		long companyId, long parentGroupId, String name, String description,
		java.util.LinkedHashMap<String, Object> params, boolean andOperator) {

		return _groupLocalService.searchCount(
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
	@Override
	public int searchCount(
		long companyId, long[] classNameIds, long parentGroupId,
		String keywords, java.util.LinkedHashMap<String, Object> params) {

		return _groupLocalService.searchCount(
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
	@Override
	public int searchCount(
		long companyId, long[] classNameIds, long parentGroupId, String name,
		String description, java.util.LinkedHashMap<String, Object> params,
		boolean andOperator) {

		return _groupLocalService.searchCount(
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
	@Override
	public int searchCount(
		long companyId, long[] classNameIds, String keywords,
		java.util.LinkedHashMap<String, Object> params) {

		return _groupLocalService.searchCount(
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
	@Override
	public int searchCount(
		long companyId, long[] classNameIds, String name, String description,
		java.util.LinkedHashMap<String, Object> params, boolean andOperator) {

		return _groupLocalService.searchCount(
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
	@Override
	public int searchCount(
		long companyId, String keywords,
		java.util.LinkedHashMap<String, Object> params) {

		return _groupLocalService.searchCount(companyId, keywords, params);
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
	@Override
	public int searchCount(
		long companyId, String name, String description,
		java.util.LinkedHashMap<String, Object> params, boolean andOperator) {

		return _groupLocalService.searchCount(
			companyId, name, description, params, andOperator);
	}

	@Override
	public void setOrganizationGroups(long organizationId, long[] groupIds) {
		_groupLocalService.setOrganizationGroups(organizationId, groupIds);
	}

	@Override
	public void setRoleGroups(long roleId, long[] groupIds) {
		_groupLocalService.setRoleGroups(roleId, groupIds);
	}

	@Override
	public void setUserGroupGroups(long userGroupId, long[] groupIds) {
		_groupLocalService.setUserGroupGroups(userGroupId, groupIds);
	}

	@Override
	public void setUserGroups(long userId, long[] groupIds) {
		_groupLocalService.setUserGroups(userId, groupIds);
	}

	/**
	 * Removes the groups from the role.
	 *
	 * @param roleId the primary key of the role
	 * @param groupIds the primary keys of the groups
	 */
	@Override
	public void unsetRoleGroups(long roleId, long[] groupIds) {
		_groupLocalService.unsetRoleGroups(roleId, groupIds);
	}

	/**
	 * Removes the user from the groups.
	 *
	 * @param userId the primary key of the user
	 * @param groupIds the primary keys of the groups
	 */
	@Override
	public void unsetUserGroups(long userId, long[] groupIds) {
		_groupLocalService.unsetUserGroups(userId, groupIds);
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
	@Override
	public void updateAsset(
			long userId, Group group, long[] assetCategoryIds,
			String[] assetTagNames)
		throws com.liferay.portal.kernel.exception.PortalException {

		_groupLocalService.updateAsset(
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
	@Override
	public Group updateFriendlyURL(long groupId, String friendlyURL)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _groupLocalService.updateFriendlyURL(groupId, friendlyURL);
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
	@Override
	public Group updateGroup(Group group) {
		return _groupLocalService.updateGroup(group);
	}

	@Override
	public Group updateGroup(
			long groupId, long parentGroupId,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap, int type,
			boolean manualMembership, int membershipRestriction,
			String friendlyURL, boolean inheritContent, boolean active,
			ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _groupLocalService.updateGroup(
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
	@Override
	public Group updateGroup(long groupId, String typeSettings)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _groupLocalService.updateGroup(groupId, typeSettings);
	}

	/**
	 * Associates the group with a main site if the group is an organization.
	 *
	 * @param groupId the primary key of the group
	 * @param site whether the group is to be associated with a main site
	 * @return the group
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Group updateSite(long groupId, boolean site)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _groupLocalService.updateSite(groupId, site);
	}

	@Override
	public void validateRemote(
			long groupId, String remoteAddress, int remotePort,
			String remotePathContext, boolean secureConnection,
			long remoteGroupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_groupLocalService.validateRemote(
			groupId, remoteAddress, remotePort, remotePathContext,
			secureConnection, remoteGroupId);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _groupLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<Group> getCTPersistence() {
		return _groupLocalService.getCTPersistence();
	}

	@Override
	public Class<Group> getModelClass() {
		return _groupLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<Group>, R, E> updateUnsafeFunction)
		throws E {

		return _groupLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public GroupLocalService getWrappedService() {
		return _groupLocalService;
	}

	@Override
	public void setWrappedService(GroupLocalService groupLocalService) {
		_groupLocalService = groupLocalService;
	}

	private GroupLocalService _groupLocalService;

}