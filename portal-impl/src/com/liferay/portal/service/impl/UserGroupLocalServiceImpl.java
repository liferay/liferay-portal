/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.expando.kernel.service.ExpandoRowLocalService;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.ExportImportHelperUtil;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.lar.UserIdStrategy;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.kernel.service.ExportImportLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.DuplicateUserGroupException;
import com.liferay.portal.kernel.exception.DuplicateUserGroupExternalReferenceCodeException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.RequiredUserGroupException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.exception.UserGroupNameException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.Team;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.UserGroupConstants;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.SortFactoryUtil;
import com.liferay.portal.kernel.search.reindexer.ReindexerBridge;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.exportimport.UserGroupImportTransactionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.persistence.GroupPersistence;
import com.liferay.portal.kernel.service.persistence.TeamPersistence;
import com.liferay.portal.kernel.service.persistence.UserFinder;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.TransactionCommitCallbackUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.service.base.UserGroupLocalServiceBaseImpl;
import com.liferay.portal.service.persistence.constants.UserGroupFinderConstants;
import com.liferay.portal.util.PortalInstances;
import com.liferay.portlet.usersadmin.util.UsersAdminUtil;

import java.io.File;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Provides the local service for accessing, adding, deleting, and updating user
 * groups.
 *
 * @author Charles May
 */
public class UserGroupLocalServiceImpl extends UserGroupLocalServiceBaseImpl {

	/**
	 * Adds the user group to the group.
	 *
	 * @param  groupId the primary key of the group
	 * @param  userGroupId the primary key of the user group
	 * @return <code>true</code> if the association between the ${groupId} and
	 *         ${userGroupId} is added; <code>false</code> if it was already
	 *         added
	 */
	@Override
	public boolean addGroupUserGroup(long groupId, long userGroupId) {
		if (!super.addGroupUserGroup(groupId, userGroupId)) {
			return false;
		}

		try {
			reindexUserGroup(getUserGroup(userGroupId));
			reindexUsers(userGroupId);
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}

		return true;
	}

	/**
	 * Adds the user group to the group.
	 *
	 * @param  groupId the primary key of the group
	 * @param  userGroup the user group
	 * @return <code>true</code> if the association between the ${groupId} and
	 *         ${userGroup} is added; <code>false</code> if it was already added
	 */
	@Override
	public boolean addGroupUserGroup(long groupId, UserGroup userGroup) {
		if (!super.addGroupUserGroup(groupId, userGroup)) {
			return false;
		}

		try {
			reindexUserGroup(userGroup);
			reindexUsers(userGroup);
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}

		return true;
	}

	/**
	 * Adds the user groups to the group.
	 *
	 * @param  groupId the primary key of the group
	 * @param  userGroups the user groups
	 * @return <code>true</code> if at least an association between the
	 *         ${groupId} and the ${userGroups} is added; <code>false</code> if
	 *         all were already added
	 */
	@Override
	public boolean addGroupUserGroups(
		long groupId, List<UserGroup> userGroups) {

		if (!super.addGroupUserGroups(groupId, userGroups)) {
			return false;
		}

		try {
			for (UserGroup userGroup : userGroups) {
				reindexUserGroup(userGroup);
			}

			reindexUsers(userGroups);
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}

		return true;
	}

	/**
	 * Adds the user groups to the group.
	 *
	 * @param  groupId the primary key of the group
	 * @param  userGroupIds the primary keys of the user groups
	 * @return <code>true</code> if at least an association between the
	 *         ${groupId} and the ${userGroupIds} is added; <code>false</code>
	 *         if all were already added
	 */
	@Override
	public boolean addGroupUserGroups(long groupId, long[] userGroupIds) {
		if (!super.addGroupUserGroups(groupId, userGroupIds)) {
			return false;
		}

		try {
			for (long userGroupId : userGroupIds) {
				reindexUserGroup(getUserGroup(userGroupId));
			}

			reindexUsers(userGroupIds);
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}

		return true;
	}

	@Override
	public UserGroup addOrUpdateUserGroup(
			String externalReferenceCode, long userId, long companyId,
			String name, String description, ServiceContext serviceContext)
		throws PortalException {

		UserGroup userGroup = fetchUserGroupByExternalReferenceCode(
			externalReferenceCode, companyId);

		if (userGroup != null) {
			return userGroupLocalService.updateUserGroup(
				externalReferenceCode, companyId, userGroup.getUserGroupId(),
				name, description, serviceContext);
		}

		return userGroupLocalService.addUserGroup(
			externalReferenceCode, userId, companyId, name, description,
			serviceContext);
	}

	@Override
	public boolean addTeamUserGroup(long teamId, UserGroup userGroup) {
		if (!super.addTeamUserGroup(teamId, userGroup)) {
			return false;
		}

		try {
			reindexUsers(userGroup);
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}

		return true;
	}

	@Override
	public boolean addTeamUserGroups(long teamId, long[] userGroupIds) {
		if (!super.addTeamUserGroups(teamId, userGroupIds)) {
			return false;
		}

		try {
			reindexUsers(userGroupIds);
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}

		return true;
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
	 * @param  userId the primary key of the user
	 * @param  companyId the primary key of the user group's company
	 * @param  name the user group's name
	 * @param  description the user group's description
	 * @param  serviceContext the service context to be applied (optionally
	 *         <code>null</code>). Can set expando bridge attributes for the
	 *         user group.
	 * @return the user group
	 */
	@Override
	public UserGroup addUserGroup(
			String externalReferenceCode, long userId, long companyId,
			String name, String description, ServiceContext serviceContext)
		throws PortalException {

		// User group

		validate(0, companyId, name);

		User user = _userPersistence.findByPrimaryKey(userId);

		long userGroupId = counterLocalService.increment();

		UserGroup userGroup = userGroupPersistence.create(userGroupId);

		if (serviceContext != null) {
			userGroup.setUuid(serviceContext.getUuid());
		}

		userGroup.setExternalReferenceCode(externalReferenceCode);
		userGroup.setCompanyId(companyId);
		userGroup.setUserId(user.getUserId());
		userGroup.setUserName(user.getFullName());
		userGroup.setParentUserGroupId(
			UserGroupConstants.DEFAULT_PARENT_USER_GROUP_ID);
		userGroup.setName(name);
		userGroup.setDescription(description);
		userGroup.setAddedByLDAPImport(
			UserGroupImportTransactionThreadLocal.isOriginatesFromImport());
		userGroup.setExpandoBridgeAttributes(serviceContext);

		userGroup = userGroupPersistence.update(userGroup);

		// Group

		_groupLocalService.addGroup(
			userId, GroupConstants.DEFAULT_PARENT_GROUP_ID,
			UserGroup.class.getName(), userGroup.getUserGroupId(),
			GroupConstants.DEFAULT_LIVE_GROUP_ID,
			getLocalizationMap(String.valueOf(userGroupId)), null, 0, true,
			GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION, null, false, true,
			null);

		// Resources

		_resourceLocalService.addResources(
			companyId, 0, userId, UserGroup.class.getName(),
			userGroup.getUserGroupId(), false, false, false);

		// Indexer

		reindexUserGroup(userGroup);

		return userGroup;
	}

	@Override
	public boolean addUserUserGroup(long userId, long userGroupId)
		throws PortalException {

		if (!super.addUserUserGroup(userId, userGroupId)) {
			return false;
		}

		reindexUserGroup(getUserGroup(userGroupId));

		return true;
	}

	@Override
	public boolean addUserUserGroup(long userId, UserGroup userGroup)
		throws PortalException {

		if (!super.addUserUserGroup(userId, userGroup)) {
			return false;
		}

		reindexUserGroup(userGroup);

		return true;
	}

	@Override
	public boolean addUserUserGroups(long userId, List<UserGroup> userGroups)
		throws PortalException {

		if (!super.addUserUserGroups(userId, userGroups)) {
			return false;
		}

		for (UserGroup userGroup : userGroups) {
			reindexUserGroup(userGroup);
		}

		return true;
	}

	@Override
	public boolean addUserUserGroups(long userId, long[] userGroupIds)
		throws PortalException {

		if (!super.addUserUserGroups(userId, userGroupIds)) {
			return false;
		}

		for (long userGroupId : userGroupIds) {
			reindexUserGroup(getUserGroup(userGroupId));
		}

		return true;
	}

	/**
	 * Deletes the user group.
	 *
	 * @param  userGroupId the primary key of the user group
	 * @return the deleted user group
	 */
	@Override
	public UserGroup deleteUserGroup(long userGroupId) throws PortalException {
		UserGroup userGroup = userGroupPersistence.findByPrimaryKey(
			userGroupId);

		return userGroupLocalService.deleteUserGroup(userGroup);
	}

	/**
	 * Deletes the user group.
	 *
	 * @param  userGroup the user group
	 * @return the deleted user group
	 */
	@Override
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public UserGroup deleteUserGroup(UserGroup userGroup)
		throws PortalException {

		if (!PortalInstances.isCurrentCompanyInDeletionProcess()) {
			int count = _userFinder.countByKeywords(
				userGroup.getCompanyId(), null,
				WorkflowConstants.STATUS_APPROVED,
				LinkedHashMapBuilder.<String, Object>put(
					"usersUserGroups", Long.valueOf(userGroup.getUserGroupId())
				).build());

			if (count > 0) {
				throw new RequiredUserGroupException();
			}
		}

		// Expando

		_expandoRowLocalService.deleteRows(userGroup.getUserGroupId());

		// Group

		_groupLocalService.deleteGroup(userGroup.getGroup());

		// User group roles

		_userGroupGroupRoleLocalService.deleteUserGroupGroupRolesByUserGroupId(
			userGroup.getUserGroupId());

		// Resources

		_resourceLocalService.deleteResource(
			userGroup.getCompanyId(), UserGroup.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL, userGroup.getUserGroupId());

		// User group

		userGroupPersistence.remove(userGroup);

		return userGroup;
	}

	@Override
	public void deleteUserGroups(long companyId) throws PortalException {
		List<UserGroup> userGroups = userGroupPersistence.findByCompanyId(
			companyId);

		for (UserGroup userGroup : userGroups) {
			userGroupLocalService.deleteUserGroup(userGroup);
		}
	}

	@Override
	public UserGroup fetchUserGroup(long companyId, String name) {
		return userGroupPersistence.fetchByC_N(companyId, name);
	}

	@Override
	public List<UserGroup> getGroupUserUserGroups(long groupId, long userId)
		throws PortalException {

		long[] groupUserGroupIds = _groupPersistence.getUserGroupPrimaryKeys(
			groupId);

		if (groupUserGroupIds.length == 0) {
			return Collections.emptyList();
		}

		long[] userUserGroupIds = _userPersistence.getUserGroupPrimaryKeys(
			userId);

		if (userUserGroupIds.length == 0) {
			return Collections.emptyList();
		}

		Set<Long> userGroupIds = SetUtil.intersect(
			groupUserGroupIds, userUserGroupIds);

		if (userGroupIds.isEmpty()) {
			return Collections.emptyList();
		}

		List<UserGroup> userGroups = new ArrayList<>(userGroupIds.size());

		for (Long userGroupId : userGroupIds) {
			userGroups.add(userGroupPersistence.findByPrimaryKey(userGroupId));
		}

		return userGroups;
	}

	/**
	 * Returns the user group with the name.
	 *
	 * @param  companyId the primary key of the user group's company
	 * @param  name the user group's name
	 * @return Returns the user group with the name
	 */
	@Override
	public UserGroup getUserGroup(long companyId, String name)
		throws PortalException {

		return userGroupPersistence.findByC_N(companyId, name);
	}

	/**
	 * Returns all the user groups belonging to the company.
	 *
	 * @param  companyId the primary key of the user groups' company
	 * @return the user groups belonging to the company
	 */
	@Override
	public List<UserGroup> getUserGroups(long companyId) {
		return userGroupPersistence.findByCompanyId(companyId);
	}

	@Override
	public List<UserGroup> getUserGroups(
		long companyId, String name, int start, int end) {

		if (Validator.isNull(name)) {
			return userGroupPersistence.findByCompanyId(companyId, start, end);
		}

		return userGroupPersistence.findByC_LikeN(companyId, name, start, end);
	}

	@Override
	public List<UserGroup> getUserGroups(
		long companyId, String name, int start, int end,
		OrderByComparator<UserGroup> orderByComparator) {

		if (Validator.isNull(name)) {
			return userGroupPersistence.findByCompanyId(
				companyId, start, end, orderByComparator);
		}

		return userGroupPersistence.findByC_LikeN(
			companyId, StringUtil.quote(name, StringPool.PERCENT), start, end,
			orderByComparator);
	}

	/**
	 * Returns all the user groups with the primary keys.
	 *
	 * @param  userGroupIds the primary keys of the user groups
	 * @return the user groups with the primary keys
	 */
	@Override
	public List<UserGroup> getUserGroups(long[] userGroupIds)
		throws PortalException {

		List<UserGroup> userGroups = new ArrayList<>(userGroupIds.length);

		for (long userGroupId : userGroupIds) {
			userGroups.add(getUserGroup(userGroupId));
		}

		return userGroups;
	}

	@Override
	public int getUserGroupsCount(long companyId, String name) {
		if (Validator.isNull(name)) {
			return userGroupPersistence.countByCompanyId(companyId);
		}

		return userGroupPersistence.countByC_LikeN(
			companyId, StringUtil.quote(name, StringPool.PERCENT));
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
			return userGroupFinder.findByKeywords(
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
				search(companyId, keywords, params, start, end, sort));
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
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
	 * @param  companyId the primary key of the user group's company
	 * @param  keywords the keywords (space separated), which may occur in the
	 *         user group's name or description (optionally <code>null</code>)
	 * @param  params the finder params (optionally <code>null</code>). For more
	 *         information see {@link
	 *         com.liferay.user.groups.admin.web.search.UserGroupIndexer}
	 * @param  start the lower bound of the range of user groups to return
	 * @param  end the upper bound of the range of user groups to return (not
	 *         inclusive)
	 * @param  sort the field and direction by which to sort (optionally
	 *         <code>null</code>)
	 * @return the matching user groups ordered by sort
	 * @see    com.liferay.user.groups.admin.web.search.UserGroupIndexer
	 */
	@Override
	public Hits search(
		long companyId, String keywords, LinkedHashMap<String, Object> params,
		int start, int end, Sort sort) {

		String name = null;
		String description = null;
		boolean andOperator = false;

		if (Validator.isNotNull(keywords)) {
			name = keywords;
			description = keywords;
		}
		else {
			andOperator = true;
		}

		if (params != null) {
			params.put("keywords", keywords);
		}

		return search(
			companyId, name, description, params, andOperator, start, end,
			sort);
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
			return userGroupFinder.findByC_N_D(
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
				search(
					companyId, name, description, params, andOperator, start,
					end, sort));
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
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
	 * @param  companyId the primary key of the user group's company
	 * @param  name the user group's name (optionally <code>null</code>)
	 * @param  description the user group's description (optionally
	 *         <code>null</code>)
	 * @param  params the finder params (optionally <code>null</code>). For more
	 *         information see {@link
	 *         com.liferay.user.groups.admin.web.search.UserGroupIndexer}
	 * @param  andSearch whether every field must match its keywords or just one
	 *         field
	 * @param  start the lower bound of the range of user groups to return
	 * @param  end the upper bound of the range of user groups to return (not
	 *         inclusive)
	 * @param  sort the field and direction by which to sort (optionally
	 *         <code>null</code>)
	 * @return the matching user groups ordered by sort
	 * @see    com.liferay.portal.kernel.service.persistence.UserGroupFinder
	 */
	@Override
	public Hits search(
		long companyId, String name, String description,
		LinkedHashMap<String, Object> params, boolean andSearch, int start,
		int end, Sort sort) {

		try {
			Indexer<UserGroup> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
				UserGroup.class);

			SearchContext searchContext = buildSearchContext(
				companyId, name, description, params, andSearch, start, end,
				sort);

			return indexer.search(searchContext);
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
			return userGroupFinder.countByKeywords(companyId, keywords, params);
		}

		String name = null;
		String description = null;
		boolean andOperator = false;

		if (Validator.isNotNull(keywords)) {
			name = keywords;
			description = keywords;
		}
		else {
			andOperator = true;
		}

		if (params != null) {
			params.put("keywords", keywords);
		}

		try {
			SearchContext searchContext = buildSearchContext(
				companyId, name, description, params, andOperator,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

			Indexer<?> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
				UserGroup.class);

			return (int)indexer.searchCount(searchContext);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
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
			return userGroupFinder.countByC_N_D(
				companyId, name, description, params, andOperator);
		}

		try {
			SearchContext searchContext = buildSearchContext(
				companyId, name, description, params, true, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

			Indexer<?> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
				UserGroup.class);

			return (int)indexer.searchCount(searchContext);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	@Override
	public BaseModelSearchResult<UserGroup> searchUserGroups(
			long companyId, String keywords,
			LinkedHashMap<String, Object> params, int start, int end, Sort sort)
		throws PortalException {

		String name = null;
		String description = null;
		boolean andOperator = false;

		if (Validator.isNotNull(keywords)) {
			name = keywords;
			description = keywords;
		}
		else {
			andOperator = true;
		}

		if (params != null) {
			params.put("keywords", keywords);
		}

		return searchUserGroups(
			companyId, name, description, params, andOperator, start, end,
			sort);
	}

	@Override
	public BaseModelSearchResult<UserGroup> searchUserGroups(
			long companyId, String name, String description,
			LinkedHashMap<String, Object> params, boolean andSearch, int start,
			int end, Sort sort)
		throws PortalException {

		Indexer<UserGroup> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			UserGroup.class);

		SearchContext searchContext = buildSearchContext(
			companyId, name, description, params, andSearch, start, end, sort);

		for (int i = 0; i < 10; i++) {
			Hits hits = indexer.search(searchContext);

			List<UserGroup> userGroups = UsersAdminUtil.getUserGroups(hits);

			if (userGroups != null) {
				return new BaseModelSearchResult<>(
					userGroups, hits.getLength());
			}
		}

		throw new SearchException(
			"Unable to fix the search index after 10 attempts");
	}

	/**
	 * Sets the user groups associated with the user copying the user group
	 * layouts and removing and adding user group associations for the user as
	 * necessary.
	 *
	 * @param userId the primary key of the user
	 * @param userGroupIds the primary keys of the user groups
	 */
	@Override
	public void setUserUserGroups(long userId, long[] userGroupIds)
		throws PortalException {

		_userPersistence.setUserGroups(userId, userGroupIds);

		for (long userGroupId : userGroupIds) {
			reindexUserGroup(getUserGroup(userGroupId));
		}

		Indexer<User> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			User.class);

		indexer.reindex(_userLocalService.fetchUser(userId));
	}

	/**
	 * Removes the user groups from the group.
	 *
	 * @param groupId the primary key of the group
	 * @param userGroupIds the primary keys of the user groups
	 */
	@Override
	public void unsetGroupUserGroups(long groupId, long[] userGroupIds) {
		List<Team> teams = _teamPersistence.findByGroupId(groupId);

		for (Team team : teams) {
			_teamPersistence.removeUserGroups(team.getTeamId(), userGroupIds);
		}

		_userGroupGroupRoleLocalService.deleteUserGroupGroupRoles(
			userGroupIds, groupId);

		_groupPersistence.removeUserGroups(groupId, userGroupIds);

		try {
			for (long userGroupId : userGroupIds) {
				reindexUserGroup(getUserGroup(userGroupId));
			}

			reindexUsers(userGroupIds);
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}
	}

	/**
	 * Removes the user groups from the team.
	 *
	 * @param teamId the primary key of the team
	 * @param userGroupIds the primary keys of the user groups
	 */
	@Override
	public void unsetTeamUserGroups(long teamId, long[] userGroupIds) {
		_teamPersistence.removeUserGroups(teamId, userGroupIds);

		try {
			reindexUsers(userGroupIds);
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public UserGroup updateExternalReferenceCode(
			UserGroup userGroup, String externalReferenceCode)
		throws PortalException {

		if (Objects.equals(
				userGroup.getExternalReferenceCode(), externalReferenceCode)) {

			return userGroup;
		}

		_validateExternalReferenceCode(
			userGroup.getUserGroupId(), externalReferenceCode);

		userGroup.setExternalReferenceCode(externalReferenceCode);

		return updateUserGroup(userGroup);
	}

	/**
	 * Updates the user group.
	 *
	 * @param  externalReferenceCode the user group's external reference code
	 * @param  companyId the primary key of the user group's company
	 * @param  userGroupId the primary key of the user group
	 * @param  name the user group's name
	 * @param  description the user group's description
	 * @param  serviceContext the service context to be applied (optionally
	 *         <code>null</code>). Can set expando bridge attributes for the
	 *         user group.
	 * @return the user group
	 */
	@Override
	public UserGroup updateUserGroup(
			String externalReferenceCode, long companyId, long userGroupId,
			String name, String description, ServiceContext serviceContext)
		throws PortalException {

		// User group

		validate(userGroupId, companyId, name);

		UserGroup userGroup = userGroupPersistence.findByPrimaryKey(
			userGroupId);

		userGroup.setExternalReferenceCode(externalReferenceCode);
		userGroup.setName(name);
		userGroup.setDescription(description);
		userGroup.setExpandoBridgeAttributes(serviceContext);

		userGroup = userGroupPersistence.update(userGroup);

		// Indexer

		reindexUserGroup(userGroup);

		return userGroup;
	}

	protected SearchContext buildSearchContext(
		long companyId, String name, String description,
		LinkedHashMap<String, Object> params, boolean andSearch, int start,
		int end, Sort sort) {

		SearchContext searchContext = new SearchContext();

		searchContext.setAndSearch(andSearch);

		Map<String, Serializable> attributes = new HashMap<>();

		attributes.put("description", description);
		attributes.put("name", name);

		if (params != null) {
			long[] userIds = (long[])params.get("userIds");

			if (ArrayUtil.isNotEmpty(userIds)) {
				attributes.put("userIds", userIds);
			}
		}

		searchContext.setAttributes(attributes);

		searchContext.setCompanyId(companyId);
		searchContext.setEnd(end);

		if (params != null) {
			String keywords = (String)params.remove("keywords");

			if (Validator.isNotNull(keywords)) {
				searchContext.setKeywords(keywords);
			}
		}

		if (sort != null) {
			searchContext.setSorts(sort);
		}

		searchContext.setStart(start);

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(false);
		queryConfig.setScoreEnabled(false);

		return searchContext;
	}

	protected File[] exportLayouts(
			long userGroupId, Map<String, String[]> parameterMap)
		throws PortalException {

		File[] files = new File[2];

		UserGroup userGroup = userGroupPersistence.findByPrimaryKey(
			userGroupId);

		User user = _userLocalService.getUser(
			GetterUtil.getLong(PrincipalThreadLocal.getName()));

		Group group = userGroup.getGroup();

		if (userGroup.hasPrivateLayouts()) {
			Map<String, Serializable> exportLayoutSettingsMap =
				ExportImportConfigurationSettingsMapFactoryUtil.
					buildExportLayoutSettingsMap(
						user, group.getGroupId(), true,
						ExportImportHelperUtil.getAllLayoutIds(
							group.getGroupId(), true),
						parameterMap);

			ExportImportConfiguration exportImportConfiguration =
				_exportImportConfigurationLocalService.
					addDraftExportImportConfiguration(
						user.getUserId(),
						ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT,
						exportLayoutSettingsMap);

			files[0] = _exportImportLocalService.exportLayoutsAsFile(
				exportImportConfiguration);
		}

		if (userGroup.hasPublicLayouts()) {
			Map<String, Serializable> exportLayoutSettingsMap =
				ExportImportConfigurationSettingsMapFactoryUtil.
					buildExportLayoutSettingsMap(
						user, group.getGroupId(), false,
						ExportImportHelperUtil.getAllLayoutIds(
							group.getGroupId(), false),
						parameterMap);

			ExportImportConfiguration exportImportConfiguration =
				_exportImportConfigurationLocalService.
					addDraftExportImportConfiguration(
						user.getUserId(),
						ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT,
						exportLayoutSettingsMap);

			files[1] = _exportImportLocalService.exportLayoutsAsFile(
				exportImportConfiguration);
		}

		return files;
	}

	protected Map<String, String[]> getLayoutTemplatesParameters() {
		return LinkedHashMapBuilder.put(
			PortletDataHandlerKeys.DATA_STRATEGY,
			new String[] {PortletDataHandlerKeys.DATA_STRATEGY_MIRROR}
		).put(
			PortletDataHandlerKeys.DELETE_MISSING_LAYOUTS,
			new String[] {Boolean.FALSE.toString()}
		).put(
			PortletDataHandlerKeys.DELETE_PORTLET_DATA,
			new String[] {Boolean.FALSE.toString()}
		).put(
			PortletDataHandlerKeys.LAYOUT_SET_SETTINGS,
			new String[] {Boolean.FALSE.toString()}
		).put(
			PortletDataHandlerKeys.LAYOUTS_IMPORT_MODE,
			new String[] {
				PortletDataHandlerKeys.LAYOUTS_IMPORT_MODE_MERGE_BY_LAYOUT_NAME
			}
		).put(
			PortletDataHandlerKeys.LOGO, new String[] {Boolean.FALSE.toString()}
		).put(
			PortletDataHandlerKeys.PERMISSIONS,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_CONFIGURATION,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_CONFIGURATION_ALL,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_DATA,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_DATA_ALL,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_SETUP_ALL,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLETS_MERGE_MODE,
			new String[] {
				PortletDataHandlerKeys.PORTLETS_MERGE_MODE_ADD_TO_BOTTOM
			}
		).put(
			PortletDataHandlerKeys.THEME_REFERENCE,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.UPDATE_LAST_PUBLISH_DATE,
			new String[] {Boolean.FALSE.toString()}
		).put(
			PortletDataHandlerKeys.USER_ID_STRATEGY,
			new String[] {UserIdStrategy.CURRENT_USER_ID}
		).build();
	}

	protected void importLayouts(
			long userId, Map<String, String[]> parameterMap,
			File privateLayoutsFile, File publicLayoutsFile)
		throws PortalException {

		User user = _userPersistence.findByPrimaryKey(userId);

		long groupId = user.getGroupId();

		if (privateLayoutsFile != null) {
			Map<String, Serializable> importLayoutSettingsMap =
				ExportImportConfigurationSettingsMapFactoryUtil.
					buildImportLayoutSettingsMap(
						user, groupId, true, null, parameterMap);

			ExportImportConfiguration exportImportConfiguration =
				_exportImportConfigurationLocalService.
					addDraftExportImportConfiguration(
						user.getUserId(),
						ExportImportConfigurationConstants.TYPE_IMPORT_LAYOUT,
						importLayoutSettingsMap);

			_exportImportLocalService.importLayouts(
				exportImportConfiguration, privateLayoutsFile);
		}

		if (publicLayoutsFile != null) {
			Map<String, Serializable> importLayoutSettingsMap =
				ExportImportConfigurationSettingsMapFactoryUtil.
					buildImportLayoutSettingsMap(
						user, groupId, false, null, parameterMap);

			ExportImportConfiguration exportImportConfiguration =
				_exportImportConfigurationLocalService.
					addDraftExportImportConfiguration(
						user.getUserId(),
						ExportImportConfigurationConstants.TYPE_IMPORT_LAYOUT,
						importLayoutSettingsMap);

			_exportImportLocalService.importLayouts(
				exportImportConfiguration, publicLayoutsFile);
		}
	}

	/**
	 * @see UserGroupServiceImpl#isUseCustomSQL
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

	protected void reindex(long companyId, long[] userIds)
		throws PortalException {

		ReindexerBridge reindexerBridge = _reindexerBridgeSnapshot.get();

		reindexerBridge.reindex(companyId, User.class.getName(), userIds);
	}

	protected void reindexUserGroup(UserGroup userGroup)
		throws PortalException {

		Indexer<UserGroup> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			UserGroup.class);

		indexer.reindex(userGroup);
	}

	protected void reindexUsers(List<UserGroup> userGroups)
		throws PortalException {

		Map<Long, List<UserGroup>> map = new HashMap<>();

		for (UserGroup userGroup : userGroups) {
			List<UserGroup> companyUserGroups = map.computeIfAbsent(
				userGroup.getCompanyId(), companyId -> new ArrayList<>());

			companyUserGroups.add(userGroup);
		}

		for (Map.Entry<Long, List<UserGroup>> entry : map.entrySet()) {
			TransactionCommitCallbackUtil.registerCallback(
				() -> {
					Set<Long> userIdsSet = new LinkedHashSet<>();

					for (long userGroupId :
							TransformUtil.transform(
								entry.getValue(), UserGroup::getUserGroupId)) {

						for (long userId : getUserPrimaryKeys(userGroupId)) {
							userIdsSet.add(userId);
						}
					}

					if (!userIdsSet.isEmpty()) {
						reindex(
							entry.getKey(),
							ArrayUtil.toArray(userIdsSet.toArray(new Long[0])));
					}

					return null;
				});
		}
	}

	protected void reindexUsers(long userGroupId) throws PortalException {
		reindexUsers(getUserGroup(userGroupId));
	}

	protected void reindexUsers(long[] userGroupIds) throws PortalException {
		reindexUsers(
			TransformUtil.transformToList(userGroupIds, this::getUserGroup));
	}

	protected void reindexUsers(UserGroup userGroup) throws PortalException {
		long companyId = userGroup.getCompanyId();
		long userGroupId = userGroup.getUserGroupId();

		TransactionCommitCallbackUtil.registerCallback(
			() -> {
				long[] userIds = getUserPrimaryKeys(userGroupId);

				if (ArrayUtil.isNotEmpty(userIds)) {
					reindex(companyId, userIds);
				}

				return null;
			});
	}

	protected void validate(long userGroupId, long companyId, String name)
		throws PortalException {

		if (Validator.isNull(name) || (name.indexOf(CharPool.COMMA) != -1) ||
			(name.indexOf(CharPool.STAR) != -1)) {

			throw new UserGroupNameException();
		}

		if (Validator.isNumber(name) &&
			!PropsValues.USER_GROUPS_NAME_ALLOW_NUMERIC) {

			throw new UserGroupNameException();
		}

		UserGroup userGroup = fetchUserGroup(companyId, name);

		if ((userGroup != null) &&
			(userGroup.getUserGroupId() != userGroupId)) {

			throw new DuplicateUserGroupException("{name=" + name + "}");
		}
	}

	private void _validateExternalReferenceCode(
			long userGroupId, String externalReferenceCode)
		throws PortalException {

		if (Validator.isNull(externalReferenceCode)) {
			return;
		}

		UserGroup userGroup = getUserGroup(userGroupId);

		userGroup = fetchUserGroupByExternalReferenceCode(
			externalReferenceCode, userGroup.getCompanyId());

		if (userGroup == null) {
			return;
		}

		if (userGroup.getUserGroupId() != userGroupId) {
			throw new DuplicateUserGroupExternalReferenceCodeException();
		}
	}

	private static final Snapshot<ReindexerBridge> _reindexerBridgeSnapshot =
		new Snapshot<>(UserGroupLocalServiceImpl.class, ReindexerBridge.class);

	@BeanReference(type = ExpandoRowLocalService.class)
	private ExpandoRowLocalService _expandoRowLocalService;

	@BeanReference(type = ExportImportConfigurationLocalService.class)
	private ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;

	@BeanReference(type = ExportImportLocalService.class)
	private ExportImportLocalService _exportImportLocalService;

	@BeanReference(type = GroupLocalService.class)
	private GroupLocalService _groupLocalService;

	@BeanReference(type = GroupPersistence.class)
	private GroupPersistence _groupPersistence;

	@BeanReference(type = ResourceLocalService.class)
	private ResourceLocalService _resourceLocalService;

	@BeanReference(type = TeamPersistence.class)
	private TeamPersistence _teamPersistence;

	@BeanReference(type = UserFinder.class)
	private UserFinder _userFinder;

	@BeanReference(type = UserGroupGroupRoleLocalService.class)
	private UserGroupGroupRoleLocalService _userGroupGroupRoleLocalService;

	@BeanReference(type = UserLocalService.class)
	private UserLocalService _userLocalService;

	@BeanReference(type = UserPersistence.class)
	private UserPersistence _userPersistence;

}