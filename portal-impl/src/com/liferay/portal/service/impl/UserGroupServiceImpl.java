/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.SortFactoryUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.service.permission.PortalPermissionUtil;
import com.liferay.portal.kernel.service.permission.TeamPermissionUtil;
import com.liferay.portal.kernel.service.permission.UserPermissionUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.comparator.UserGroupIdComparator;
import com.liferay.portal.security.membershippolicy.UserGroupMembershipPolicyUtil;
import com.liferay.portal.service.base.UserGroupServiceBaseImpl;
import com.liferay.portal.service.permission.UserGroupPermissionUtil;
import com.liferay.portal.service.persistence.constants.UserGroupFinderConstants;
import com.liferay.portlet.usersadmin.util.UsersAdminUtil;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Provides the remote service for accessing, adding, deleting, and updating
 * user groups. Its methods include permission checks.
 *
 * @author Charles May
 */
public class UserGroupServiceImpl extends UserGroupServiceBaseImpl {

	/**
	 * Adds the user groups to the group.
	 *
	 * @param groupId the primary key of the group
	 * @param userGroupIds the primary keys of the user groups
	 */
	@Override
	public void addGroupUserGroups(long groupId, long[] userGroupIds)
		throws PortalException {

		GroupPermissionUtil.check(
			getPermissionChecker(), groupId, ActionKeys.ASSIGN_MEMBERS);

		userGroupLocalService.addGroupUserGroups(groupId, userGroupIds);
	}

	@Override
	public UserGroup addOrUpdateUserGroup(
			String externalReferenceCode, String name, String description,
			ServiceContext serviceContext)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		UserGroup userGroup =
			userGroupLocalService.fetchUserGroupByExternalReferenceCode(
				externalReferenceCode, permissionChecker.getCompanyId());

		if (userGroup == null) {
			PortalPermissionUtil.check(
				permissionChecker, ActionKeys.ADD_USER_GROUP);
		}
		else {
			UserGroupPermissionUtil.check(
				permissionChecker, userGroup.getUserGroupId(),
				ActionKeys.UPDATE);
		}

		return userGroupLocalService.addOrUpdateUserGroup(
			externalReferenceCode, permissionChecker.getUserId(),
			permissionChecker.getCompanyId(), name, description,
			serviceContext);
	}

	/**
	 * Adds the user groups to the team
	 *
	 * @param teamId the primary key of the team
	 * @param userGroupIds the primary keys of the user groups
	 */
	@Override
	public void addTeamUserGroups(long teamId, long[] userGroupIds)
		throws PortalException {

		TeamPermissionUtil.check(
			getPermissionChecker(), teamId, ActionKeys.ASSIGN_MEMBERS);

		userGroupLocalService.addTeamUserGroups(teamId, userGroupIds);
	}

	/**
	 * Adds a user group.
	 *
	 * <p>
	 * This method handles the creation and bookkeeping of the user group,
	 * including its resources, metadata, and internal data structures.
	 * </p>
	 *
	 * @param  externalReferenceCode the user group's external reference code
	 * @param  name the user group's name
	 * @param  description the user group's description
	 * @param  serviceContext the service context to be applied (optionally
	 *         <code>null</code>). Can set expando bridge attributes for the
	 *         user group.
	 * @return the user group
	 */
	@Override
	public UserGroup addUserGroup(
			String externalReferenceCode, String name, String description,
			ServiceContext serviceContext)
		throws PortalException {

		PortalPermissionUtil.check(
			getPermissionChecker(), ActionKeys.ADD_USER_GROUP);

		User user = getUser();

		UserGroup userGroup = userGroupLocalService.addUserGroup(
			externalReferenceCode, user.getUserId(), user.getCompanyId(), name,
			description, serviceContext);

		UserGroupMembershipPolicyUtil.verifyPolicy(userGroup);

		return userGroup;
	}

	/**
	 * Deletes the user group.
	 *
	 * @param userGroupId the primary key of the user group
	 */
	@Override
	public void deleteUserGroup(long userGroupId) throws PortalException {
		UserGroupPermissionUtil.check(
			getPermissionChecker(), userGroupId, ActionKeys.DELETE);

		userGroupLocalService.deleteUserGroup(userGroupId);
	}

	/**
	 * Fetches the user group with the primary key.
	 *
	 * @param  userGroupId the primary key of the user group
	 * @return the user group with the primary key
	 */
	@Override
	public UserGroup fetchUserGroup(long userGroupId) throws PortalException {
		UserGroup userGroup = userGroupLocalService.fetchUserGroup(userGroupId);

		if (userGroup != null) {
			UserGroupPermissionUtil.check(
				getPermissionChecker(), userGroupId, ActionKeys.VIEW);
		}

		return userGroup;
	}

	@Override
	public UserGroup fetchUserGroupByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		UserGroup userGroup =
			userGroupLocalService.fetchUserGroupByExternalReferenceCode(
				externalReferenceCode, companyId);

		if (userGroup != null) {
			UserGroupPermissionUtil.check(
				getPermissionChecker(), userGroup.getUserGroupId(),
				ActionKeys.VIEW);
		}

		return userGroup;
	}

	@Override
	public List<UserGroup> getGtUserGroups(
		long gtUserGroupId, long companyId, long parentUserGroupId, int size) {

		return userGroupPersistence.filterFindByGtU_C_P(
			gtUserGroupId, companyId, parentUserGroupId, 0, size,
			UserGroupIdComparator.getInstance(true));
	}

	/**
	 * Returns the user group with the primary key.
	 *
	 * @param  userGroupId the primary key of the user group
	 * @return the user group with the primary key
	 */
	@Override
	public UserGroup getUserGroup(long userGroupId) throws PortalException {
		UserGroup userGroup = userGroupLocalService.getUserGroup(userGroupId);

		UserGroupPermissionUtil.check(
			getPermissionChecker(), userGroupId, ActionKeys.VIEW);

		return userGroup;
	}

	/**
	 * Returns the user group with the name.
	 *
	 * @param  name the user group's name
	 * @return the user group with the name
	 */
	@Override
	public UserGroup getUserGroup(String name) throws PortalException {
		User user = getUser();

		UserGroup userGroup = userGroupLocalService.getUserGroup(
			user.getCompanyId(), name);

		UserGroupPermissionUtil.check(
			getPermissionChecker(), userGroup.getUserGroupId(),
			ActionKeys.VIEW);

		return userGroup;
	}

	@Override
	public UserGroup getUserGroupByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		UserGroup userGroup =
			userGroupLocalService.getUserGroupByExternalReferenceCode(
				externalReferenceCode, companyId);

		UserGroupPermissionUtil.check(
			getPermissionChecker(), userGroup.getUserGroupId(),
			ActionKeys.VIEW);

		return userGroup;
	}

	@Override
	public List<UserGroup> getUserGroups(long companyId)
		throws PortalException {

		return filterUserGroups(userGroupLocalService.getUserGroups(companyId));
	}

	@Override
	public List<UserGroup> getUserGroups(
		long companyId, String name, int start, int end) {

		if (Validator.isNull(name)) {
			return userGroupPersistence.filterFindByCompanyId(
				companyId, start, end);
		}

		return userGroupPersistence.filterFindByC_LikeN(
			companyId, name, start, end);
	}

	@Override
	public int getUserGroupsCount(long companyId, String name) {
		if (Validator.isNull(name)) {
			return userGroupPersistence.filterCountByCompanyId(companyId);
		}

		return userGroupPersistence.filterCountByC_LikeN(companyId, name);
	}

	/**
	 * Returns all the user groups to which the user belongs.
	 *
	 * @param  userId the primary key of the user
	 * @return the user groups to which the user belongs
	 */
	@Override
	public List<UserGroup> getUserUserGroups(long userId)
		throws PortalException {

		UserPermissionUtil.check(
			getPermissionChecker(), userId, ActionKeys.VIEW);

		List<UserGroup> userGroups = userGroupLocalService.getUserUserGroups(
			userId);

		return filterUserGroups(userGroups);
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
	 * @param  companyId the primary key of the user group's company
	 * @param  keywords the keywords (space separated), which may occur in the
	 *         user group's name or description (optionally <code>null</code>)
	 * @param  params the finder params (optionally <code>null</code>). For more
	 *         information see {@link
	 *         com.liferay.portal.kernel.service.persistence.UserGroupFinder}
	 * @param  start the lower bound of the range of user groups to return
	 * @param  end the upper bound of the range of user groups to return (not
	 *         inclusive)
	 * @param  orderByComparator the comparator to order the user groups
	 *         (optionally <code>null</code>)
	 * @return the matching user groups ordered by comparator
	 *         <code>orderByComparator</code>
	 * @see    com.liferay.portal.kernel.service.persistence.UserGroupFinder
	 */
	@Override
	public List<UserGroup> search(
		long companyId, String keywords, LinkedHashMap<String, Object> params,
		int start, int end, OrderByComparator<UserGroup> orderByComparator) {

		if (isUseCustomSQL(params)) {
			return userGroupFinder.filterFindByKeywords(
				companyId, keywords, params, start, end, orderByComparator);
		}

		String orderByCol = orderByComparator.getOrderByFields()[0];

		String orderByType = "asc";

		if (!orderByComparator.isAscending()) {
			orderByType = "desc";
		}

		Sort sort = SortFactoryUtil.getSort(
			UserGroup.class, orderByCol, orderByType);

		try {
			return UsersAdminUtil.getUserGroups(
				userGroupLocalService.search(
					companyId, keywords, params, start, end, sort));
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
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
	 * @param  companyId the primary key of the user group's company
	 * @param  name the user group's name (optionally <code>null</code>)
	 * @param  description the user group's description (optionally
	 *         <code>null</code>)
	 * @param  params the finder params (optionally <code>null</code>). For more
	 *         information see {@link
	 *         com.liferay.portal.kernel.service.persistence.UserGroupFinder}
	 * @param  andOperator whether every field must match its keywords or just
	 *         one field
	 * @param  start the lower bound of the range of user groups to return
	 * @param  end the upper bound of the range of user groups to return (not
	 *         inclusive)
	 * @param  orderByComparator the comparator to order the user groups
	 *         (optionally <code>null</code>)
	 * @return the matching user groups ordered by comparator
	 *         <code>orderByComparator</code>
	 * @see    com.liferay.portal.kernel.service.persistence.UserGroupFinder
	 */
	@Override
	public List<UserGroup> search(
		long companyId, String name, String description,
		LinkedHashMap<String, Object> params, boolean andOperator, int start,
		int end, OrderByComparator<UserGroup> orderByComparator) {

		if (isUseCustomSQL(params)) {
			return userGroupFinder.filterFindByC_N_D(
				companyId, name, description, params, andOperator, start, end,
				orderByComparator);
		}

		String orderByCol = orderByComparator.getOrderByFields()[0];

		String orderByType = "asc";

		if (!orderByComparator.isAscending()) {
			orderByType = "desc";
		}

		Sort sort = SortFactoryUtil.getSort(
			UserGroup.class, orderByCol, orderByType);

		try {
			return UsersAdminUtil.getUserGroups(
				userGroupLocalService.search(
					companyId, name, description, params, andOperator, start,
					end, sort));
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	/**
	 * Returns the number of user groups that match the keywords
	 *
	 * @param  companyId the primary key of the user group's company
	 * @param  keywords the keywords (space separated), which may occur in the
	 *         user group's name or description (optionally <code>null</code>)
	 * @param  params the finder params (optionally <code>null</code>). For more
	 *         information see {@link
	 *         com.liferay.portal.kernel.service.persistence.UserGroupFinder}
	 * @return the number of matching user groups
	 * @see    com.liferay.portal.kernel.service.persistence.UserGroupFinder
	 */
	@Override
	public int searchCount(
		long companyId, String keywords, LinkedHashMap<String, Object> params) {

		if (isUseCustomSQL(params)) {
			return userGroupFinder.filterCountByKeywords(
				companyId, keywords, params);
		}

		return userGroupLocalService.searchCount(companyId, keywords, params);
	}

	/**
	 * Returns the number of user groups that match the name and description.
	 *
	 * @param  companyId the primary key of the user group's company
	 * @param  name the user group's name (optionally <code>null</code>)
	 * @param  description the user group's description (optionally
	 *         <code>null</code>)
	 * @param  params the finder params (optionally <code>null</code>). For more
	 *         information see {@link
	 *         com.liferay.portal.kernel.service.persistence.UserGroupFinder}
	 * @param  andOperator whether every field must match its keywords or just
	 *         one field
	 * @return the number of matching user groups
	 * @see    com.liferay.portal.kernel.service.persistence.UserGroupFinder
	 */
	@Override
	public int searchCount(
		long companyId, String name, String description,
		LinkedHashMap<String, Object> params, boolean andOperator) {

		if (isUseCustomSQL(params)) {
			return userGroupFinder.filterCountByC_N_D(
				companyId, name, description, params, andOperator);
		}

		return userGroupLocalService.searchCount(
			companyId, name, description, params, andOperator);
	}

	/**
	 * Removes the user groups from the group.
	 *
	 * @param groupId the primary key of the group
	 * @param userGroupIds the primary keys of the user groups
	 */
	@Override
	public void unsetGroupUserGroups(long groupId, long[] userGroupIds)
		throws PortalException {

		GroupPermissionUtil.check(
			getPermissionChecker(), groupId, ActionKeys.ASSIGN_MEMBERS);

		userGroupLocalService.unsetGroupUserGroups(groupId, userGroupIds);
	}

	/**
	 * Removes the user groups from the team.
	 *
	 * @param teamId the primary key of the team
	 * @param userGroupIds the primary keys of the user groups
	 */
	@Override
	public void unsetTeamUserGroups(long teamId, long[] userGroupIds)
		throws PortalException {

		TeamPermissionUtil.check(
			getPermissionChecker(), teamId, ActionKeys.ASSIGN_MEMBERS);

		userGroupLocalService.unsetTeamUserGroups(teamId, userGroupIds);
	}

	@Override
	public UserGroup updateExternalReferenceCode(
			UserGroup userGroup, String externalReferenceCode)
		throws PortalException {

		UserGroupPermissionUtil.check(
			getPermissionChecker(), userGroup.getUserGroupId(),
			ActionKeys.UPDATE);

		return userGroupLocalService.updateExternalReferenceCode(
			userGroup, externalReferenceCode);
	}

	/**
	 * Updates the user group.
	 *
	 * @param  externalReferenceCode the user group's external reference code
	 * @param  userGroupId the primary key of the user group
	 * @param  name the user group's name
	 * @param  description the the user group's description
	 * @param  serviceContext the service context to be applied (optionally
	 *         <code>null</code>). Can set expando bridge attributes for the
	 *         user group.
	 * @return the user group
	 */
	@Override
	public UserGroup updateUserGroup(
			String externalReferenceCode, long userGroupId, String name,
			String description, ServiceContext serviceContext)
		throws PortalException {

		UserGroupPermissionUtil.check(
			getPermissionChecker(), userGroupId, ActionKeys.UPDATE);

		User user = getUser();

		return userGroupLocalService.updateUserGroup(
			externalReferenceCode, user.getCompanyId(), userGroupId, name,
			description, serviceContext);
	}

	protected List<UserGroup> filterUserGroups(List<UserGroup> userGroups)
		throws PortalException {

		return TransformUtil.transform(
			userGroups,
			userGroup -> {
				if (UserGroupPermissionUtil.contains(
						getPermissionChecker(), userGroup.getUserGroupId(),
						ActionKeys.VIEW)) {

					return userGroup;
				}

				return null;
			});
	}

	/**
	 * @see UserGroupLocalServiceImpl#isUseCustomSQL
	 */
	protected boolean isUseCustomSQL(LinkedHashMap<String, Object> params) {
		if (MapUtil.isEmpty(params)) {
			return false;
		}

		Indexer<?> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			UserGroup.class);

		if (!indexer.isIndexerEnabled() ||
			!PropsValues.USER_GROUPS_SEARCH_WITH_INDEX) {

			return true;
		}

		if (MapUtil.isEmpty(params)) {
			return false;
		}

		for (String key : params.keySet()) {
			if (ArrayUtil.contains(UserGroupFinderConstants.PARAM_KEYS, key)) {
				return true;
			}
		}

		return false;
	}

}