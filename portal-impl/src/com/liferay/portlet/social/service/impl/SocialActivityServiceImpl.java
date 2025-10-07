/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.social.service.impl;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portlet.social.service.base.SocialActivityServiceBaseImpl;
import com.liferay.social.kernel.model.SocialActivity;
import com.liferay.social.kernel.model.SocialActivityInterpreter;
import com.liferay.social.kernel.model.impl.SocialActivityInterpreterImpl;
import com.liferay.social.kernel.service.SocialActivityInterpreterLocalService;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides the remote service for accessing social activities. Its methods
 * include permission checks.
 *
 * @author Zsolt Berentey
 */
public class SocialActivityServiceImpl extends SocialActivityServiceBaseImpl {

	/**
	 * Returns a range of all the activities done on assets identified by the
	 * class name ID.
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
	 * @param  classNameId the target asset's class name ID
	 * @param  start the lower bound of the range of results
	 * @param  end the upper bound of the range of results (not inclusive)
	 * @return the range of matching activities
	 */
	@Override
	public List<SocialActivity> getActivities(
			long classNameId, int start, int end)
		throws PortalException {

		return filterActivities(
			socialActivityLocalService.getActivities(
				classNameId, 0,
				end + PropsValues.SOCIAL_ACTIVITY_FILTER_SEARCH_LIMIT),
			start, end);
	}

	/**
	 * Returns a range of all the activities done on the asset identified by the
	 * class name ID and class primary key that are mirrors of the activity
	 * identified by the mirror activity ID.
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
	 * @param  mirrorActivityId the primary key of the mirror activity
	 * @param  classNameId the target asset's class name ID
	 * @param  classPK the primary key of the target asset
	 * @param  start the lower bound of the range of results
	 * @param  end the upper bound of the range of results (not inclusive)
	 * @return the range of matching activities
	 */
	@Override
	public List<SocialActivity> getActivities(
			long mirrorActivityId, long classNameId, long classPK, int start,
			int end)
		throws PortalException {

		return filterActivities(
			socialActivityLocalService.getActivities(
				mirrorActivityId, classNameId, classPK, 0,
				end + PropsValues.SOCIAL_ACTIVITY_FILTER_SEARCH_LIMIT),
			start, end);
	}

	/**
	 * Returns a range of all the activities done on the asset identified by the
	 * class name and the class primary key that are mirrors of the activity
	 * identified by the mirror activity ID.
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
	 * @param  mirrorActivityId the primary key of the mirror activity
	 * @param  className the target asset's class name
	 * @param  classPK the primary key of the target asset
	 * @param  start the lower bound of the range of results
	 * @param  end the upper bound of the range of results (not inclusive)
	 * @return the range of matching activities
	 */
	@Override
	public List<SocialActivity> getActivities(
			long mirrorActivityId, String className, long classPK, int start,
			int end)
		throws PortalException {

		return filterActivities(
			socialActivityLocalService.getActivities(
				mirrorActivityId,
				_classNameLocalService.getClassNameId(className), classPK, 0,
				end + PropsValues.SOCIAL_ACTIVITY_FILTER_SEARCH_LIMIT),
			start, end);
	}

	/**
	 * Returns a range of all the activities done on assets identified by the
	 * class name.
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
	 * @param  className the target asset's class name
	 * @param  start the lower bound of the range of results
	 * @param  end the upper bound of the range of results (not inclusive)
	 * @return the range of matching activities
	 */
	@Override
	public List<SocialActivity> getActivities(
			String className, int start, int end)
		throws PortalException {

		return filterActivities(
			socialActivityLocalService.getActivities(
				_classNameLocalService.getClassNameId(className), 0,
				end + PropsValues.SOCIAL_ACTIVITY_FILTER_SEARCH_LIMIT),
			start, end);
	}

	/**
	 * Returns the number of activities done on assets identified by the class
	 * name ID.
	 *
	 * @param  classNameId the target asset's class name ID
	 * @return the number of matching activities
	 */
	@Override
	public int getActivitiesCount(long classNameId) {
		return socialActivityLocalService.getActivitiesCount(classNameId);
	}

	/**
	 * Returns the number of activities done on the asset identified by the
	 * class name ID and class primary key that are mirrors of the activity
	 * identified by the mirror activity ID.
	 *
	 * @param  mirrorActivityId the primary key of the mirror activity
	 * @param  classNameId the target asset's class name ID
	 * @param  classPK the primary key of the target asset
	 * @return the number of matching activities
	 */
	@Override
	public int getActivitiesCount(
		long mirrorActivityId, long classNameId, long classPK) {

		return socialActivityLocalService.getActivitiesCount(
			mirrorActivityId, classNameId, classPK);
	}

	/**
	 * Returns the number of activities done on the asset identified by the
	 * class name and class primary key that are mirrors of the activity
	 * identified by the mirror activity ID.
	 *
	 * @param  mirrorActivityId the primary key of the mirror activity
	 * @param  className the target asset's class name
	 * @param  classPK the primary key of the target asset
	 * @return the number of matching activities
	 */
	@Override
	public int getActivitiesCount(
		long mirrorActivityId, String className, long classPK) {

		return getActivitiesCount(
			mirrorActivityId, _classNameLocalService.getClassNameId(className),
			classPK);
	}

	/**
	 * Returns the number of activities done on assets identified by class name.
	 *
	 * @param  className the target asset's class name
	 * @return the number of matching activities
	 */
	@Override
	public int getActivitiesCount(String className) {
		return getActivitiesCount(
			_classNameLocalService.getClassNameId(className));
	}

	/**
	 * Returns the activity identified by its primary key.
	 *
	 * @param  activityId the primary key of the activity
	 * @return Returns the activity
	 */
	@Override
	public SocialActivity getActivity(long activityId) throws PortalException {
		SocialActivity activity = socialActivityLocalService.getActivity(
			activityId);

		if (!hasPermission(
				activity,
				_socialActivityInterpreterLocalService.getActivityInterpreters(
					StringPool.BLANK))) {

			throw new PrincipalException.MustHavePermission(
				0, SocialActivity.class.getName(), activityId);
		}

		return activity;
	}

	@Override
	public List<SocialActivity> getActivitySetActivities(
			long activitySetId, int start, int end)
		throws PortalException {

		List<SocialActivity> activities =
			socialActivityLocalService.getActivitySetActivities(
				activitySetId, start, end);

		return filterActivities(activities, start, end);
	}

	/**
	 * Returns a range of all the activities done in the group.
	 *
	 * <p>
	 * This method only finds activities without mirrors.
	 * </p>
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
	 * @param  groupId the primary key of the group
	 * @param  start the lower bound of the range of results
	 * @param  end the upper bound of the range of results (not inclusive)
	 * @return the range of matching activities
	 */
	@Override
	public List<SocialActivity> getGroupActivities(
			long groupId, int start, int end)
		throws PortalException {

		List<SocialActivity> activities =
			socialActivityLocalService.getGroupActivities(
				groupId, 0,
				end + PropsValues.SOCIAL_ACTIVITY_FILTER_SEARCH_LIMIT);

		return filterActivities(activities, start, end);
	}

	/**
	 * Returns the number of activities done in the group.
	 *
	 * <p>
	 * This method only counts activities without mirrors.
	 * </p>
	 *
	 * @param  groupId the primary key of the group
	 * @return the number of matching activities
	 */
	@Override
	public int getGroupActivitiesCount(long groupId) {
		return socialActivityLocalService.getGroupActivitiesCount(groupId);
	}

	/**
	 * Returns a range of activities done by users that are members of the
	 * group.
	 *
	 * <p>
	 * This method only finds activities without mirrors.
	 * </p>
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
	 * @param  groupId the primary key of the group
	 * @param  start the lower bound of the range of results
	 * @param  end the upper bound of the range of results (not inclusive)
	 * @return the range of matching activities
	 */
	@Override
	public List<SocialActivity> getGroupUsersActivities(
			long groupId, int start, int end)
		throws PortalException {

		List<SocialActivity> activities =
			socialActivityLocalService.getGroupUsersActivities(
				groupId, 0,
				end + PropsValues.SOCIAL_ACTIVITY_FILTER_SEARCH_LIMIT);

		return filterActivities(activities, start, end);
	}

	/**
	 * Returns the number of activities done by users that are members of the
	 * group.
	 *
	 * <p>
	 * This method only counts activities without mirrors.
	 * </p>
	 *
	 * @param  groupId the primary key of the group
	 * @return the number of matching activities
	 */
	@Override
	public int getGroupUsersActivitiesCount(long groupId) {
		return socialActivityLocalService.getGroupUsersActivitiesCount(groupId);
	}

	/**
	 * Returns the activity that has the mirror activity.
	 *
	 * @param  mirrorActivityId the primary key of the mirror activity
	 * @return Returns the mirror activity
	 */
	@Override
	public SocialActivity getMirrorActivity(long mirrorActivityId)
		throws PortalException {

		SocialActivity activity = socialActivityLocalService.getMirrorActivity(
			mirrorActivityId);

		if (!hasPermission(
				activity,
				_socialActivityInterpreterLocalService.getActivityInterpreters(
					StringPool.BLANK))) {

			throw new PrincipalException.MustHavePermission(
				0, SocialActivity.class.getName(), mirrorActivityId);
		}

		return activity;
	}

	/**
	 * Returns a range of all the activities done in the organization. This
	 * method only finds activities without mirrors.
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
	 * @param  organizationId the primary key of the organization
	 * @param  start the lower bound of the range of results
	 * @param  end the upper bound of the range of results (not inclusive)
	 * @return the range of matching activities
	 */
	@Override
	public List<SocialActivity> getOrganizationActivities(
			long organizationId, int start, int end)
		throws PortalException {

		List<SocialActivity> activities =
			socialActivityLocalService.getOrganizationActivities(
				organizationId, 0,
				end + PropsValues.SOCIAL_ACTIVITY_FILTER_SEARCH_LIMIT);

		return filterActivities(activities, start, end);
	}

	/**
	 * Returns the number of activities done in the organization. This method
	 * only counts activities without mirrors.
	 *
	 * @param  organizationId the primary key of the organization
	 * @return the number of matching activities
	 */
	@Override
	public int getOrganizationActivitiesCount(long organizationId) {
		return socialActivityLocalService.getOrganizationActivitiesCount(
			organizationId);
	}

	/**
	 * Returns a range of all the activities done by users of the organization.
	 * This method only finds activities without mirrors.
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
	 * @param  organizationId the primary key of the organization
	 * @param  start the lower bound of the range of results
	 * @param  end the upper bound of the range of results (not inclusive)
	 * @return the range of matching activities
	 */
	@Override
	public List<SocialActivity> getOrganizationUsersActivities(
			long organizationId, int start, int end)
		throws PortalException {

		List<SocialActivity> activities =
			socialActivityLocalService.getOrganizationUsersActivities(
				organizationId, 0,
				end + PropsValues.SOCIAL_ACTIVITY_FILTER_SEARCH_LIMIT);

		return filterActivities(activities, start, end);
	}

	/**
	 * Returns the number of activities done by users of the organization. This
	 * method only counts activities without mirrors.
	 *
	 * @param  organizationId the primary key of the organization
	 * @return the number of matching activities
	 */
	@Override
	public int getOrganizationUsersActivitiesCount(long organizationId) {
		return socialActivityLocalService.getOrganizationUsersActivitiesCount(
			organizationId);
	}

	/**
	 * Returns a range of all the activities done by users in a relationship
	 * with the user identified by the user ID.
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
	 * @param  start the lower bound of the range of results
	 * @param  end the upper bound of the range of results (not inclusive)
	 * @return the range of matching activities
	 */
	@Override
	public List<SocialActivity> getRelationActivities(
			long userId, int start, int end)
		throws PortalException {

		List<SocialActivity> activities =
			socialActivityLocalService.getRelationActivities(
				userId, 0,
				end + PropsValues.SOCIAL_ACTIVITY_FILTER_SEARCH_LIMIT);

		return filterActivities(activities, start, end);
	}

	/**
	 * Returns a range of all the activities done by users in a relationship of
	 * type <code>type</code> with the user identified by <code>userId</code>.
	 * This method only finds activities without mirrors.
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
	 * @param  type the relationship type
	 * @param  start the lower bound of the range of results
	 * @param  end the upper bound of the range of results (not inclusive)
	 * @return the range of matching activities
	 */
	@Override
	public List<SocialActivity> getRelationActivities(
			long userId, int type, int start, int end)
		throws PortalException {

		List<SocialActivity> activities =
			socialActivityLocalService.getRelationActivities(
				userId, type, 0,
				end + PropsValues.SOCIAL_ACTIVITY_FILTER_SEARCH_LIMIT);

		return filterActivities(activities, start, end);
	}

	/**
	 * Returns the number of activities done by users in a relationship with the
	 * user identified by userId.
	 *
	 * @param  userId the primary key of the user
	 * @return the number of matching activities
	 */
	@Override
	public int getRelationActivitiesCount(long userId) {
		return socialActivityLocalService.getRelationActivitiesCount(userId);
	}

	/**
	 * Returns the number of activities done by users in a relationship of type
	 * <code>type</code> with the user identified by <code>userId</code>. This
	 * method only counts activities without mirrors.
	 *
	 * @param  userId the primary key of the user
	 * @param  type the relationship type
	 * @return the number of matching activities
	 */
	@Override
	public int getRelationActivitiesCount(long userId, int type) {
		return socialActivityLocalService.getRelationActivitiesCount(
			userId, type);
	}

	/**
	 * Returns a range of all the activities done by the user.
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
	 * @param  start the lower bound of the range of results
	 * @param  end the upper bound of the range of results (not inclusive)
	 * @return the range of matching activities
	 */
	@Override
	public List<SocialActivity> getUserActivities(
			long userId, int start, int end)
		throws PortalException {

		List<SocialActivity> activities =
			socialActivityLocalService.getUserActivities(
				userId, 0,
				end + PropsValues.SOCIAL_ACTIVITY_FILTER_SEARCH_LIMIT);

		return filterActivities(activities, start, end);
	}

	/**
	 * Returns the number of activities done by the user.
	 *
	 * @param  userId the primary key of the user
	 * @return the number of matching activities
	 */
	@Override
	public int getUserActivitiesCount(long userId) {
		return socialActivityLocalService.getUserActivitiesCount(userId);
	}

	/**
	 * Returns a range of all the activities done in the user's groups. This
	 * method only finds activities without mirrors.
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
	 * @param  start the lower bound of the range of results
	 * @param  end the upper bound of the range of results (not inclusive)
	 * @return the range of matching activities
	 */
	@Override
	public List<SocialActivity> getUserGroupsActivities(
			long userId, int start, int end)
		throws PortalException {

		List<SocialActivity> activities =
			socialActivityLocalService.getUserGroupsActivities(
				userId, 0,
				end + PropsValues.SOCIAL_ACTIVITY_FILTER_SEARCH_LIMIT);

		return filterActivities(activities, start, end);
	}

	/**
	 * Returns the number of activities done in user's groups. This method only
	 * counts activities without mirrors.
	 *
	 * @param  userId the primary key of the user
	 * @return the number of matching activities
	 */
	@Override
	public int getUserGroupsActivitiesCount(long userId) {
		return socialActivityLocalService.getUserGroupsActivitiesCount(userId);
	}

	/**
	 * Returns a range of all the activities done in the user's groups and
	 * organizations. This method only finds activities without mirrors.
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
	 * @param  start the lower bound of the range of results
	 * @param  end the upper bound of the range of results (not inclusive)
	 * @return the range of matching activities
	 */
	@Override
	public List<SocialActivity> getUserGroupsAndOrganizationsActivities(
			long userId, int start, int end)
		throws PortalException {

		List<SocialActivity> activities =
			socialActivityLocalService.getUserGroupsAndOrganizationsActivities(
				userId, 0,
				end + PropsValues.SOCIAL_ACTIVITY_FILTER_SEARCH_LIMIT);

		return filterActivities(activities, start, end);
	}

	/**
	 * Returns the number of activities done in user's groups and organizations.
	 * This method only counts activities without mirrors.
	 *
	 * @param  userId the primary key of the user
	 * @return the number of matching activities
	 */
	@Override
	public int getUserGroupsAndOrganizationsActivitiesCount(long userId) {
		return socialActivityLocalService.
			getUserGroupsAndOrganizationsActivitiesCount(userId);
	}

	/**
	 * Returns a range of all activities done in the user's organizations. This
	 * method only finds activities without mirrors.
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
	 * @param  start the lower bound of the range of results
	 * @param  end the upper bound of the range of results (not inclusive)
	 * @return the range of matching activities
	 */
	@Override
	public List<SocialActivity> getUserOrganizationsActivities(
			long userId, int start, int end)
		throws PortalException {

		List<SocialActivity> activities =
			socialActivityLocalService.getUserOrganizationsActivities(
				userId, 0,
				end + PropsValues.SOCIAL_ACTIVITY_FILTER_SEARCH_LIMIT);

		return filterActivities(activities, start, end);
	}

	/**
	 * Returns the number of activities done in the user's organizations. This
	 * method only counts activities without mirrors.
	 *
	 * @param  userId the primary key of the user
	 * @return the number of matching activities
	 */
	@Override
	public int getUserOrganizationsActivitiesCount(long userId) {
		return socialActivityLocalService.getUserOrganizationsActivitiesCount(
			userId);
	}

	protected List<SocialActivity> filterActivities(
			List<SocialActivity> activities, int start, int end)
		throws PortalException {

		List<SocialActivity> filteredActivities = new ArrayList<>();

		List<SocialActivityInterpreter> activityInterpreters =
			_socialActivityInterpreterLocalService.getActivityInterpreters(
				StringPool.BLANK);

		for (SocialActivity activity : activities) {
			if (hasPermission(activity, activityInterpreters)) {
				filteredActivities.add(activity);
			}

			if ((end != QueryUtil.ALL_POS) &&
				(filteredActivities.size() > end)) {

				break;
			}
		}

		if ((end != QueryUtil.ALL_POS) && (start != QueryUtil.ALL_POS)) {
			if (end > filteredActivities.size()) {
				end = filteredActivities.size();
			}

			if (start > filteredActivities.size()) {
				start = filteredActivities.size();
			}

			filteredActivities = filteredActivities.subList(start, end);
		}

		return filteredActivities;
	}

	protected boolean hasPermission(
			SocialActivity activity,
			List<SocialActivityInterpreter> activityInterpreters)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();
		ServiceContext serviceContext = new ServiceContext();

		for (SocialActivityInterpreter activityInterpreter :
				activityInterpreters) {

			SocialActivityInterpreterImpl socialActivityInterpreterImpl =
				(SocialActivityInterpreterImpl)activityInterpreter;

			if (socialActivityInterpreterImpl.hasClassName(
					activity.getClassName())) {

				try {
					if (socialActivityInterpreterImpl.hasPermission(
							permissionChecker, activity, ActionKeys.VIEW,
							serviceContext)) {

						return true;
					}
				}
				catch (Exception exception) {
					if (_log.isDebugEnabled()) {
						_log.debug(exception);
					}
				}
			}
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SocialActivityServiceImpl.class);

	@BeanReference(type = ClassNameLocalService.class)
	private ClassNameLocalService _classNameLocalService;

	@BeanReference(type = SocialActivityInterpreterLocalService.class)
	private SocialActivityInterpreterLocalService
		_socialActivityInterpreterLocalService;

}