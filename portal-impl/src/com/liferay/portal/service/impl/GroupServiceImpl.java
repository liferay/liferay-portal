/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.UserBag;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.service.permission.PortalPermissionUtil;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.service.permission.RolePermissionUtil;
import com.liferay.portal.kernel.service.permission.UserPermissionUtil;
import com.liferay.portal.kernel.service.persistence.CompanyPersistence;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.comparator.GroupIdComparator;
import com.liferay.portal.security.membershippolicy.SiteMembershipPolicyUtil;
import com.liferay.portal.security.permission.UserBagFactoryUtil;
import com.liferay.portal.service.base.GroupServiceBaseImpl;
import com.liferay.ratings.kernel.transformer.RatingsDataTransformerUtil;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Provides the remote service for accessing, adding, deleting, and updating
 * groups. Its methods include permission checks. Groups are mostly used in
 * Liferay as a resource container for permissioning and content scoping
 * purposes.
 *
 * @author Brian Wing Shun Chan
 * @see    GroupLocalServiceImpl
 */
public class GroupServiceImpl extends GroupServiceBaseImpl {

	@Override
	public Group addGroup(
			long parentGroupId, long liveGroupId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, int type,
			boolean manualMembership, int membershipRestriction,
			String friendlyURL, boolean site, boolean inheritContent,
			boolean active, ServiceContext serviceContext)
		throws PortalException {

		if (parentGroupId == GroupConstants.DEFAULT_PARENT_GROUP_ID) {
			PortalPermissionUtil.check(
				getPermissionChecker(), ActionKeys.ADD_COMMUNITY);
		}
		else {
			GroupPermissionUtil.check(
				getPermissionChecker(), parentGroupId,
				ActionKeys.ADD_COMMUNITY);
		}

		Group group = groupLocalService.addGroup(
			getUserId(), parentGroupId, null, 0, liveGroupId, nameMap,
			descriptionMap, type, manualMembership, membershipRestriction,
			friendlyURL, site, inheritContent, active, serviceContext);

		if (site) {
			SiteMembershipPolicyUtil.verifyPolicy(group);
		}

		return group;
	}

	@Override
	public Group addGroup(
			long parentGroupId, long liveGroupId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, int type,
			boolean manualMembership, int membershipRestriction,
			String friendlyURL, boolean site, boolean active,
			ServiceContext serviceContext)
		throws PortalException {

		return addGroup(
			parentGroupId, liveGroupId, nameMap, descriptionMap, type,
			manualMembership, membershipRestriction, friendlyURL, site, false,
			active, serviceContext);
	}

	@Override
	public Group addOrUpdateGroup(
			String externalReferenceCode, long parentGroupId, long liveGroupId,
			Map<Locale, String> nameMap, Map<Locale, String> descriptionMap,
			int type, boolean manualMembership, int membershipRestriction,
			String friendlyURL, boolean site, boolean inheritContent,
			boolean active, ServiceContext serviceContext)
		throws Exception {

		User user = getUser();

		Group group = groupPersistence.fetchByERC_C(
			externalReferenceCode, user.getCompanyId());

		if (group == null) {
			group = addGroup(
				parentGroupId, liveGroupId, nameMap, descriptionMap, type,
				manualMembership, membershipRestriction, friendlyURL, site,
				inheritContent, active, serviceContext);

			group.setExternalReferenceCode(externalReferenceCode);

			group = groupPersistence.update(group);
		}
		else {
			group = updateGroup(
				group.getGroupId(), parentGroupId, nameMap, descriptionMap,
				type, manualMembership, membershipRestriction, friendlyURL,
				inheritContent, active, serviceContext);
		}

		return group;
	}

	/**
	 * Adds the groups to the role.
	 *
	 * @param  roleId the primary key of the role
	 * @param  groupIds the primary keys of the groups
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void addRoleGroups(long roleId, long[] groupIds)
		throws PortalException {

		RolePermissionUtil.check(
			getPermissionChecker(), roleId, ActionKeys.ASSIGN_MEMBERS);

		groupLocalService.addRoleGroups(roleId, groupIds);
	}

	/**
	 * Checks that the current user is permitted to use the group for Remote
	 * Staging.
	 *
	 * @param  groupId the primary key of the group
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void checkRemoteStagingGroup(long groupId) throws PortalException {
		Group group = getGroup(groupId);

		PermissionChecker permissionChecker = getPermissionChecker();

		if (group.getCompanyId() != permissionChecker.getCompanyId()) {
			throw new NoSuchGroupException(
				StringBundler.concat(
					"Group ", groupId, " does not belong in company ",
					permissionChecker.getCompanyId()));
		}
	}

	/**
	 * Deletes the group.
	 *
	 * <p>
	 * The group is unstaged and its assets and resources including layouts,
	 * membership requests, subscriptions, teams, blogs, bookmarks, calendar
	 * events, image gallery, journals, message boards, polls, and wikis are
	 * also deleted.
	 * </p>
	 *
	 * @param  groupId the primary key of the group
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void deleteGroup(long groupId) throws PortalException {
		GroupPermissionUtil.check(
			getPermissionChecker(), groupId, ActionKeys.DELETE);

		groupLocalService.deleteGroup(groupId);
	}

	@Override
	public void disableStaging(long groupId) throws PortalException {
		GroupPermissionUtil.check(
			getPermissionChecker(), groupLocalService.getGroup(groupId),
			ActionKeys.UPDATE);

		groupLocalService.disableStaging(groupId);
	}

	@Override
	public void enableStaging(long groupId) throws PortalException {
		GroupPermissionUtil.check(
			getPermissionChecker(), groupLocalService.getGroup(groupId),
			ActionKeys.UPDATE);

		groupLocalService.enableStaging(groupId);
	}

	@Override
	public Group fetchGroupByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		Group group = groupLocalService.fetchGroupByExternalReferenceCode(
			externalReferenceCode, companyId);

		if (group != null) {
			GroupPermissionUtil.check(
				getPermissionChecker(), group, ActionKeys.VIEW);
		}

		return group;
	}

	/**
	 * Returns the company group.
	 *
	 * @param  companyId the primary key of the company
	 * @return the group associated with the company
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Group getCompanyGroup(long companyId) throws PortalException {
		Group group = groupLocalService.getCompanyGroup(companyId);

		GroupPermissionUtil.check(
			getPermissionChecker(), group, ActionKeys.VIEW);

		return group;
	}

	/**
	 * Returns the group with the primary key.
	 *
	 * @param  groupId the primary key of the group
	 * @return the group with the primary key
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Group getGroup(long groupId) throws PortalException {
		Group group = groupLocalService.getGroup(groupId);

		GroupPermissionUtil.check(
			getPermissionChecker(), group, ActionKeys.VIEW);

		return group;
	}

	/**
	 * Returns the group with the name.
	 *
	 * @param  companyId the primary key of the company
	 * @param  groupKey the group key
	 * @return the group with the group key
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Group getGroup(long companyId, String groupKey)
		throws PortalException {

		Group group = groupLocalService.getGroup(companyId, groupKey);

		GroupPermissionUtil.check(
			getPermissionChecker(), group, ActionKeys.VIEW);

		return group;
	}

	/**
	 * Returns the group's display URL.
	 *
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout set is private to the group
	 * @param  secureConnection whether the generated URL uses a secure
	 *         connection
	 * @return the group's display URL
	 * @throws PortalException if a group with the primary key could not be
	 *         found or if a portal exception occurred
	 */
	@Override
	public String getGroupDisplayURL(
			long groupId, boolean privateLayout, boolean secureConnection)
		throws PortalException {

		Group group = groupLocalService.getGroup(groupId);

		GroupPermissionUtil.check(
			getPermissionChecker(), group, ActionKeys.VIEW);

		if (!privateLayout && (group.getPublicLayoutsPageCount() > 0)) {
			return PortalUtil.getLayoutSetDisplayURL(
				group.getPublicLayoutSet(), secureConnection);
		}
		else if (privateLayout && (group.getPrivateLayoutsPageCount() > 0)) {
			return PortalUtil.getLayoutSetDisplayURL(
				group.getPrivateLayoutSet(), secureConnection);
		}

		GroupPermissionUtil.check(
			getPermissionChecker(), group, ActionKeys.UPDATE);

		return PortalUtil.getControlPanelFullURL(
			groupId,
			PortletProviderUtil.getPortletId(
				Layout.class.getName(), PortletProvider.Action.EDIT),
			null);
	}

	/**
	 * Returns all the groups that are direct children of the parent group.
	 *
	 * @param  companyId the primary key of the company
	 * @param  parentGroupId the primary key of the parent group
	 * @param  site whether the group is to be associated with a main site
	 * @return the matching groups, or <code>null</code> if no matches were
	 *         found
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public List<Group> getGroups(
			long companyId, long parentGroupId, boolean site)
		throws PortalException {

		return filterGroups(
			groupLocalService.getGroups(companyId, parentGroupId, site));
	}

	/**
	 * Returns all the groups that are direct children of the parent group.
	 *
	 * @param  companyId the primary key of the company
	 * @param  parentGroupId the primary key of the parent group
	 * @param  site whether the group is to be associated with a main site
	 * @param  start the lower bound of the range of results
	 * @param  end the upper bound of the range of results (not inclusive)
	 * @return the matching groups, or <code>null</code> if no matches were
	 *         found
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public List<Group> getGroups(
			long companyId, long parentGroupId, boolean site, int start,
			int end)
		throws PortalException {

		return filterGroups(
			groupLocalService.getGroups(
				companyId, parentGroupId, site, start, end));
	}

	@Override
	public List<Group> getGroups(
			long companyId, long parentGroupId, String name, boolean site,
			int start, int end)
		throws PortalException {

		return filterGroups(
			groupLocalService.getGroups(
				companyId, parentGroupId, name, site, start, end));
	}

	/**
	 * Returns the number of groups that are direct children of the parent
	 * group.
	 *
	 * @param  companyId the primary key of the company
	 * @param  parentGroupId the primary key of the parent group
	 * @param  site whether the group is to be associated with a main site
	 * @return the number of matching groups
	 */
	@Override
	public int getGroupsCount(long companyId, long parentGroupId, boolean site)
		throws PortalException {

		if (parentGroupId > 0) {
			GroupPermissionUtil.check(
				getPermissionChecker(), parentGroupId, ActionKeys.VIEW);
		}
		else {
			GroupPermissionUtil.check(getPermissionChecker(), ActionKeys.VIEW);
		}

		return groupLocalService.getGroupsCount(companyId, parentGroupId, site);
	}

	@Override
	public int getGroupsCount(
			long companyId, long parentGroupId, String name, boolean site)
		throws PortalException {

		if (parentGroupId > 0) {
			GroupPermissionUtil.check(
				getPermissionChecker(), parentGroupId, ActionKeys.VIEW);
		}
		else {
			GroupPermissionUtil.check(getPermissionChecker(), ActionKeys.VIEW);
		}

		return groupLocalService.getGroupsCount(
			companyId, parentGroupId, name, site);
	}

	/**
	 * Returns the number of groups that are direct children of the parent group
	 * with the matching className.
	 *
	 * @param  companyId the primary key of the company
	 * @param  className the class name of the group
	 * @param  parentGroupId the primary key of the parent group
	 * @return the number of matching groups
	 */
	@Override
	public int getGroupsCount(
			long companyId, String className, long parentGroupId)
		throws PortalException {

		if (parentGroupId > 0) {
			GroupPermissionUtil.check(
				getPermissionChecker(), parentGroupId, ActionKeys.VIEW);
		}
		else {
			GroupPermissionUtil.check(getPermissionChecker(), ActionKeys.VIEW);
		}

		return groupLocalService.getGroupsCount(
			companyId, className, parentGroupId);
	}

	@Override
	public List<Group> getGtGroups(
			long gtGroupId, long companyId, long parentGroupId, boolean site,
			int size)
		throws PortalException {

		GroupPermissionUtil.check(getPermissionChecker(), ActionKeys.VIEW);

		return groupPersistence.findByGtG_C_P_S(
			gtGroupId, companyId, parentGroupId, site, 0, size,
			GroupIdComparator.getInstance(true));
	}

	/**
	 * Returns a range of all the site groups for which the user has control
	 * panel access.
	 *
	 * @param  portlets the portlets to manage
	 * @param  max the upper bound of the range of groups to consider (not
	 *         inclusive)
	 * @return the range of site groups for which the user has Control Panel
	 *         access
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public List<Group> getManageableSiteGroups(
			Collection<Portlet> portlets, int max)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		if (permissionChecker.isCompanyAdmin()) {
			return ListUtil.unique(
				groupLocalService.search(
					permissionChecker.getCompanyId(), null, null, null,
					LinkedHashMapBuilder.<String, Object>put(
						"site", Boolean.TRUE
					).build(),
					true, 0, max));
		}

		Set<Group> groups = new LinkedHashSet<>();

		List<Group> userSitesGroups = getUserSitesGroups(null, max);

		Iterator<Group> iterator = userSitesGroups.iterator();

		while (iterator.hasNext()) {
			Group group = iterator.next();

			if (group.isSite() &&
				PortletPermissionUtil.hasControlPanelAccessPermission(
					permissionChecker, group.getGroupId(), portlets)) {

				groups.add(group);
			}
		}

		return new ArrayList<>(groups);
	}

	/**
	 * Returns the groups associated with the organizations.
	 *
	 * @param  organizations the organizations
	 * @return the groups associated with the organizations
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public List<Group> getOrganizationsGroups(List<Organization> organizations)
		throws PortalException {

		List<Group> groups = groupLocalService.getOrganizationsGroups(
			organizations);

		return filterGroups(groups);
	}

	/**
	 * Returns the group directly associated with the user.
	 *
	 * @param  companyId the primary key of the company
	 * @param  userId the primary key of the user
	 * @return the group directly associated with the user
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Group getUserGroup(long companyId, long userId)
		throws PortalException {

		Group group = groupLocalService.getUserGroup(companyId, userId);

		GroupPermissionUtil.check(
			getPermissionChecker(), group, ActionKeys.VIEW);

		return group;
	}

	/**
	 * Returns the groups associated with the user groups.
	 *
	 * @param  userGroups the user groups
	 * @return the groups associated with the user groups
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public List<Group> getUserGroupsGroups(List<UserGroup> userGroups)
		throws PortalException {

		List<Group> groups = groupLocalService.getUserGroupsGroups(userGroups);

		return filterGroups(groups);
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
	 * @param  userId the primary key of the user
	 * @param  start the lower bound of the range of groups to consider
	 * @param  end the upper bound of the range of groups to consider (not
	 *         inclusive)
	 * @return the range of groups associated with the user's organizations
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public List<Group> getUserOrganizationsGroups(
			long userId, int start, int end)
		throws PortalException {

		List<Group> groups = groupLocalService.getUserOrganizationsGroups(
			userId, start, end);

		return filterGroups(groups);
	}

	@Override
	public List<Group> getUserSitesGroups() throws PortalException {
		return getUserSitesGroups(null, QueryUtil.ALL_POS);
	}

	@Override
	public List<Group> getUserSitesGroups(long userId, int start, int end)
		throws PortalException {

		List<Group> groups = groupLocalService.getUserSitesGroups(
			userId, start, end);

		return filterGroups(groups);
	}

	/**
	 * Returns the user's groups &quot;sites&quot; associated with the group
	 * entity class names, including the Control Panel group if the user is
	 * permitted to view the Control Panel.
	 *
	 * <ul>
	 * <li>
	 * Class name &quot;User&quot; includes the user's layout set
	 * group.
	 * </li>
	 * <li>
	 * Class name &quot;Organization&quot; includes the user's
	 * immediate organization groups and inherited organization groups.
	 * </li>
	 * <li>
	 * Class name &quot;Group&quot; includes the user's immediate
	 * organization groups and site groups.
	 * </li>
	 * <li>
	 * A <code>classNames</code>
	 * value of <code>null</code> includes the user's layout set group,
	 * organization groups, inherited organization groups, and site groups.
	 * </li>
	 * </ul>
	 *
	 * @param  userId the primary key of the user
	 * @param  classNames the group entity class names (optionally
	 *         <code>null</code>). For more information see {@link
	 *         #getUserSitesGroups(long, String[], int)}.
	 * @param  max the maximum number of groups to return
	 * @return the user's groups &quot;sites&quot;
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public List<Group> getUserSitesGroups(
			long userId, String[] classNames, int max)
		throws PortalException {

		User user = _userPersistence.findByPrimaryKey(userId);

		boolean checkPermissions = true;

		if (userId == getUserId()) {
			checkPermissions = false;
		}

		if (checkPermissions) {
			UserPermissionUtil.check(
				getPermissionChecker(), userId, ActionKeys.VIEW);
		}

		if (user.isGuestUser()) {
			return Collections.emptyList();
		}

		Set<Group> userSiteGroups = new LinkedHashSet<>();

		if (classNames == null) {
			classNames = new String[] {
				Company.class.getName(), Group.class.getName(),
				Organization.class.getName(), User.class.getName()
			};
		}

		if (ArrayUtil.contains(classNames, User.class.getName()) &&
			(PrefsPropsUtil.getBoolean(
				user.getCompanyId(),
				PropsKeys.LAYOUT_USER_PRIVATE_LAYOUTS_ENABLED) ||
			 PrefsPropsUtil.getBoolean(
				 user.getCompanyId(),
				 PropsKeys.LAYOUT_USER_PUBLIC_LAYOUTS_ENABLED))) {

			userSiteGroups.add(user.getGroup());

			if (userSiteGroups.size() == max) {
				if (checkPermissions) {
					return filterGroups(new ArrayList<>(userSiteGroups));
				}

				return new ArrayList<>(userSiteGroups);
			}
		}

		if (ArrayUtil.contains(classNames, Company.class.getName())) {
			Company company = _companyPersistence.fetchByPrimaryKey(
				user.getCompanyId());

			Group companyGroup = company.getGroup();

			if (GroupPermissionUtil.contains(
					getPermissionChecker(), companyGroup,
					ActionKeys.VIEW_SITE_ADMINISTRATION)) {

				userSiteGroups.add(companyGroup);

				if (userSiteGroups.size() == max) {
					return new ArrayList<>(userSiteGroups);
				}
			}
		}

		if (ArrayUtil.contains(classNames, Group.class.getName()) ||
			ArrayUtil.contains(classNames, Organization.class.getName())) {

			UserBag userBag = UserBagFactoryUtil.create(user);

			if (ArrayUtil.contains(classNames, Group.class.getName())) {
				for (Group group : userBag.getUserGroups()) {
					if (groupLocalService.isLiveGroupActive(group) &&
						group.isSite() && userSiteGroups.add(group) &&
						(userSiteGroups.size() == max)) {

						if (checkPermissions) {
							return filterGroups(
								new ArrayList<>(userSiteGroups));
						}

						return new ArrayList<>(userSiteGroups);
					}
				}
			}

			if (ArrayUtil.contains(classNames, Organization.class.getName())) {
				for (Group group : userBag.getUserOrgGroups()) {
					if (groupLocalService.isLiveGroupActive(group) &&
						group.isSite() && userSiteGroups.add(group) &&
						(userSiteGroups.size() == max)) {

						if (checkPermissions) {
							return filterGroups(
								new ArrayList<>(userSiteGroups));
						}

						return new ArrayList<>(userSiteGroups);
					}
				}
			}
		}

		if (checkPermissions) {
			return filterGroups(new ArrayList<>(userSiteGroups));
		}

		return new ArrayList<>(userSiteGroups);
	}

	/**
	 * Returns the guest or current user's groups &quot;sites&quot; associated
	 * with the group entity class names, including the Control Panel group if
	 * the user is permitted to view the Control Panel.
	 *
	 * <ul>
	 * <li>
	 * Class name &quot;User&quot; includes the user's layout set
	 * group.
	 * </li>
	 * <li>
	 * Class name &quot;Organization&quot; includes the user's
	 * immediate organization groups and inherited organization groups.
	 * </li>
	 * <li>
	 * Class name &quot;Group&quot; includes the user's immediate
	 * organization groups and site groups.
	 * </li>
	 * <li>
	 * A <code>classNames</code>
	 * value of <code>null</code> includes the user's layout set group,
	 * organization groups, inherited organization groups, and site groups.
	 * </li>
	 * </ul>
	 *
	 * @param  classNames the group entity class names (optionally
	 *         <code>null</code>). For more information see {@link
	 *         #getUserSitesGroups(long, String[], int)}.
	 * @param  max the maximum number of groups to return
	 * @return the user's groups &quot;sites&quot;
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public List<Group> getUserSitesGroups(String[] classNames, int max)
		throws PortalException {

		return getUserSitesGroups(getGuestOrUserId(), classNames, max);
	}

	/**
	 * Returns the number of the guest or current user's groups
	 * &quot;sites&quot; associated with the group entity class names, including
	 * the Control Panel group if the user is permitted to view the Control
	 * Panel.
	 *
	 * @return the number of user's groups &quot;sites&quot;
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public int getUserSitesGroupsCount() throws PortalException {
		List<Group> userSitesGroups = getUserSitesGroups(
			getGuestOrUserId(), null, QueryUtil.ALL_POS);

		return userSitesGroups.size();
	}

	/**
	 * Returns <code>true</code> if the user is associated with the group,
	 * including the user's inherited organizations and user groups. System and
	 * staged groups are not included.
	 *
	 * @param  userId the primary key of the user
	 * @param  groupId the primary key of the group
	 * @return <code>true</code> if the user is associated with the group;
	 *         <code>false</code> otherwise
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public boolean hasUserGroup(long userId, long groupId)
		throws PortalException {

		try {
			UserPermissionUtil.check(
				getPermissionChecker(), userId, ActionKeys.VIEW);
		}
		catch (PrincipalException principalException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(principalException);
			}

			GroupPermissionUtil.check(
				getPermissionChecker(), groupId, ActionKeys.VIEW_MEMBERS);
		}

		return groupLocalService.hasUserGroup(userId, groupId);
	}

	@Override
	public List<Group> search(
			long companyId, long[] classNameIds, String keywords,
			LinkedHashMap<String, Object> params, int start, int end,
			OrderByComparator<Group> orderByComparator)
		throws PortalException {

		List<Group> groups = groupLocalService.search(
			companyId, classNameIds, keywords, params, start, end,
			orderByComparator);

		return filterGroups(groups);
	}

	@Override
	public List<Group> search(
			long companyId, long[] classNameIds, String name,
			String description, LinkedHashMap<String, Object> params,
			boolean andOperator, int start, int end,
			OrderByComparator<Group> orderByComparator)
		throws PortalException {

		List<Group> groups = groupLocalService.search(
			companyId, classNameIds, name, description, params, andOperator,
			start, end, orderByComparator);

		return filterGroups(groups);
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
	 * @param  companyId the primary key of the company
	 * @param  name the group's name (optionally <code>null</code>)
	 * @param  description the group's description (optionally
	 *         <code>null</code>)
	 * @param  params the finder params (optionally <code>null</code>). To
	 *         include the user's inherited organizations and user groups in the
	 *         search, add entries having &quot;usersGroups&quot; and
	 *         &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 *         information see {@link
	 *         com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @param  start the lower bound of the range of groups to return
	 * @param  end the upper bound of the range of groups to return (not
	 *         inclusive)
	 * @return the matching groups ordered by name
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public List<Group> search(
			long companyId, String name, String description, String[] params,
			int start, int end)
		throws PortalException {

		if (params == null) {
			params = new String[0];
		}

		LinkedHashMap<String, Object> paramsObj = MapUtil.toLinkedHashMap(
			params);

		List<Group> groups = groupLocalService.search(
			companyId, name, description, paramsObj, true, start, end);

		return filterGroups(groups);
	}

	@Override
	public int searchCount(
		long companyId, long[] classNameIds, String keywords,
		LinkedHashMap<String, Object> params) {

		return groupLocalService.searchCount(
			companyId, classNameIds, keywords, params);
	}

	/**
	 * Returns the number of groups and organization groups that match the name
	 * and description, optionally including the user's inherited organizations
	 * and user groups. System and staged groups are not included.
	 *
	 * @param  companyId the primary key of the company
	 * @param  name the group's name (optionally <code>null</code>)
	 * @param  description the group's description (optionally
	 *         <code>null</code>)
	 * @param  params the finder params (optionally <code>null</code>). To
	 *         include the user's inherited organizations and user groups in the
	 *         search, add entries having &quot;usersGroups&quot; and
	 *         &quot;inherit&quot; as keys mapped to the the user's ID. For more
	 *         information see {@link
	 *         com.liferay.portal.kernel.service.persistence.GroupFinder}.
	 * @return the number of matching groups
	 */
	@Override
	public int searchCount(
		long companyId, String name, String description, String[] params) {

		if (params == null) {
			params = new String[0];
		}

		LinkedHashMap<String, Object> paramsObj = MapUtil.toLinkedHashMap(
			params);

		return groupLocalService.searchCount(
			companyId, name, description, paramsObj, true);
	}

	/**
	 * Sets the groups associated with the role, removing and adding
	 * associations as necessary.
	 *
	 * @param  roleId the primary key of the role
	 * @param  groupIds the primary keys of the groups
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void setRoleGroups(long roleId, long[] groupIds)
		throws PortalException {

		RolePermissionUtil.check(
			getPermissionChecker(), roleId, ActionKeys.ASSIGN_MEMBERS);

		groupLocalService.setRoleGroups(roleId, groupIds);
	}

	/**
	 * Removes the groups from the role.
	 *
	 * @param  roleId the primary key of the role
	 * @param  groupIds the primary keys of the groups
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void unsetRoleGroups(long roleId, long[] groupIds)
		throws PortalException {

		RolePermissionUtil.check(
			getPermissionChecker(), roleId, ActionKeys.ASSIGN_MEMBERS);

		groupLocalService.unsetRoleGroups(roleId, groupIds);
	}

	/**
	 * Updates the group's friendly URL.
	 *
	 * @param  groupId the primary key of the group
	 * @param  friendlyURL the group's new friendlyURL (optionally
	 *         <code>null</code>)
	 * @return the group
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Group updateFriendlyURL(long groupId, String friendlyURL)
		throws PortalException {

		GroupPermissionUtil.check(
			getPermissionChecker(), groupId, ActionKeys.UPDATE);

		return groupLocalService.updateFriendlyURL(groupId, friendlyURL);
	}

	@Override
	public Group updateGroup(
			long groupId, long parentGroupId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, int type,
			boolean manualMembership, int membershipRestriction,
			String friendlyURL, boolean inheritContent, boolean active,
			ServiceContext serviceContext)
		throws PortalException {

		Group group = groupPersistence.findByPrimaryKey(groupId);

		GroupPermissionUtil.check(
			getPermissionChecker(), group, ActionKeys.UPDATE);

		if (group.getParentGroupId() != parentGroupId) {
			if (parentGroupId == GroupConstants.DEFAULT_PARENT_GROUP_ID) {
				PortalPermissionUtil.check(
					getPermissionChecker(), ActionKeys.ADD_COMMUNITY);
			}
			else {
				GroupPermissionUtil.check(
					getPermissionChecker(), parentGroupId,
					ActionKeys.ADD_COMMUNITY);
			}
		}

		if (group.isSite()) {
			Group oldGroup = group;

			List<AssetCategory> oldAssetCategories =
				_assetCategoryLocalService.getCategories(
					Group.class.getName(), groupId);

			List<AssetTag> oldAssetTags = _assetTagLocalService.getTags(
				Group.class.getName(), groupId);

			ExpandoBridge oldExpandoBridge = oldGroup.getExpandoBridge();

			Map<String, Serializable> oldExpandoAttributes =
				oldExpandoBridge.getAttributes();

			group = groupLocalService.updateGroup(
				groupId, parentGroupId, nameMap, descriptionMap, type,
				manualMembership, membershipRestriction, friendlyURL,
				inheritContent, active, serviceContext);

			SiteMembershipPolicyUtil.verifyPolicy(
				group, oldGroup, oldAssetCategories, oldAssetTags,
				oldExpandoAttributes, null);

			return group;
		}

		return groupLocalService.updateGroup(
			groupId, parentGroupId, nameMap, descriptionMap, type,
			manualMembership, membershipRestriction, friendlyURL,
			inheritContent, active, serviceContext);
	}

	/**
	 * Updates the group's type settings.
	 *
	 * @param  groupId the primary key of the group
	 * @param  typeSettings the group's new type settings (optionally
	 *         <code>null</code>)
	 * @return the group
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Group updateGroup(long groupId, String typeSettings)
		throws PortalException {

		Group group = groupPersistence.findByPrimaryKey(groupId);

		GroupPermissionUtil.check(
			getPermissionChecker(), group, ActionKeys.UPDATE);

		if (group.isSite()) {
			Group oldGroup = group;

			UnicodeProperties oldTypeSettingsUnicodeProperties =
				oldGroup.getTypeSettingsProperties();

			group = groupLocalService.updateGroup(groupId, typeSettings);

			RatingsDataTransformerUtil.transformGroupRatingsData(
				groupId, oldTypeSettingsUnicodeProperties,
				group.getTypeSettingsProperties());

			SiteMembershipPolicyUtil.verifyPolicy(
				group, oldGroup, null, null, null,
				oldTypeSettingsUnicodeProperties);

			return group;
		}

		return groupLocalService.updateGroup(groupId, typeSettings);
	}

	@Override
	public void updateStagedPortlets(
			long groupId, Map<String, String> stagedPortletIds)
		throws PortalException {

		Group group = groupPersistence.findByPrimaryKey(groupId);

		GroupPermissionUtil.check(
			getPermissionChecker(), group, ActionKeys.UPDATE);

		UnicodeProperties typeSettingsUnicodeProperties =
			group.getTypeSettingsProperties();

		for (Map.Entry<String, String> entry : stagedPortletIds.entrySet()) {
			typeSettingsUnicodeProperties.setProperty(
				StagingUtil.getStagedPortletId(entry.getKey()),
				entry.getValue());
		}

		groupLocalService.updateGroup(group);
	}

	protected List<Group> filterGroups(List<Group> groups)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		return TransformUtil.transform(
			groups,
			group -> {
				if (GroupPermissionUtil.contains(
						permissionChecker, group, ActionKeys.VIEW)) {

					return group;
				}

				return null;
			});
	}

	protected Map<Locale, String> getLocalizationMap(String value) {
		return HashMapBuilder.put(
			LocaleUtil.getDefault(), value
		).build();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GroupServiceImpl.class);

	@BeanReference(type = AssetCategoryLocalService.class)
	private AssetCategoryLocalService _assetCategoryLocalService;

	@BeanReference(type = AssetTagLocalService.class)
	private AssetTagLocalService _assetTagLocalService;

	@BeanReference(type = CompanyPersistence.class)
	private CompanyPersistence _companyPersistence;

	@BeanReference(type = UserPersistence.class)
	private UserPersistence _userPersistence;

}