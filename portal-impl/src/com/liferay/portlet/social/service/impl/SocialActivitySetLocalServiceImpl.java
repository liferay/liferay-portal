/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.social.service.impl;

import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portlet.social.service.base.SocialActivitySetLocalServiceBaseImpl;
import com.liferay.social.kernel.model.SocialActivity;
import com.liferay.social.kernel.model.SocialActivitySet;
import com.liferay.social.kernel.service.persistence.SocialActivityPersistence;
import com.liferay.social.kernel.util.comparator.SocialActivitySetModifiedDateComparator;

import java.util.List;

/**
 * @author Matthew Kong
 */
public class SocialActivitySetLocalServiceImpl
	extends SocialActivitySetLocalServiceBaseImpl {

	@Override
	public SocialActivitySet addActivitySet(long activityId)
		throws PortalException {

		// Activity set

		SocialActivity activity = _socialActivityPersistence.findByPrimaryKey(
			activityId);

		long activitySetId = counterLocalService.increment();

		SocialActivitySet activitySet = socialActivitySetPersistence.create(
			activitySetId);

		activitySet.setGroupId(activity.getGroupId());
		activitySet.setCompanyId(activity.getCompanyId());
		activitySet.setUserId(activity.getUserId());
		activitySet.setCreateDate(activity.getCreateDate());
		activitySet.setModifiedDate(activity.getCreateDate());
		activitySet.setClassName(activity.getClassName());
		activitySet.setClassPK(activity.getClassPK());
		activitySet.setType(activity.getType());
		activitySet.setActivityCount(1);

		activitySet = socialActivitySetPersistence.update(activitySet);

		// Activity

		activity.setActivitySetId(activitySetId);

		_socialActivityPersistence.update(activity);

		return activitySet;
	}

	@Override
	public void decrementActivityCount(long activitySetId)
		throws PortalException {

		if (activitySetId == 0) {
			return;
		}

		SocialActivitySet activitySet =
			socialActivitySetPersistence.fetchByPrimaryKey(activitySetId);

		if (activitySet == null) {
			return;
		}

		if (activitySet.getActivityCount() == 1) {
			socialActivitySetPersistence.remove(activitySetId);

			return;
		}

		activitySet.setActivityCount(activitySet.getActivityCount() - 1);

		socialActivitySetPersistence.update(activitySet);
	}

	@Override
	public void decrementActivityCount(long classNameId, long classPK)
		throws PortalException {

		List<SocialActivity> activities = _socialActivityPersistence.findByC_C(
			classNameId, classPK);

		for (SocialActivity activity : activities) {
			decrementActivityCount(activity.getActivitySetId());
		}
	}

	@Override
	public SocialActivitySet getClassActivitySet(
			long classNameId, long classPK, int type)
		throws PortalException {

		return socialActivitySetPersistence.findByC_C_T_First(
			classNameId, classPK, type,
			SocialActivitySetModifiedDateComparator.getInstance(false));
	}

	@Override
	public SocialActivitySet getClassActivitySet(
			long userId, long classNameId, long classPK, int type)
		throws PortalException {

		return socialActivitySetPersistence.findByU_C_C_T_First(
			userId, classNameId, classPK, type,
			SocialActivitySetModifiedDateComparator.getInstance(false));
	}

	@Override
	public List<SocialActivitySet> getGroupActivitySets(
		long groupId, int start, int end) {

		return socialActivitySetPersistence.findByGroupId(
			groupId, start, end,
			SocialActivitySetModifiedDateComparator.getInstance(false));
	}

	@Override
	public int getGroupActivitySetsCount(long groupId) {
		return socialActivitySetPersistence.countByGroupId(groupId);
	}

	@Override
	public List<SocialActivitySet> getOrganizationActivitySets(
		long organizationId, int start, int end) {

		return socialActivitySetFinder.findByOrganizationId(
			organizationId, start, end);
	}

	@Override
	public int getOrganizationActivitySetsCount(long organizationId) {
		return socialActivitySetFinder.countByOrganizationId(organizationId);
	}

	@Override
	public List<SocialActivitySet> getRelationActivitySets(
		long userId, int start, int end) {

		return socialActivitySetFinder.findByRelation(userId, start, end);
	}

	@Override
	public List<SocialActivitySet> getRelationActivitySets(
		long userId, int type, int start, int end) {

		return socialActivitySetFinder.findByRelationType(
			userId, type, start, end);
	}

	@Override
	public int getRelationActivitySetsCount(long userId) {
		return socialActivitySetFinder.countByRelation(userId);
	}

	@Override
	public int getRelationActivitySetsCount(long userId, int type) {
		return socialActivitySetFinder.countByRelationType(userId, type);
	}

	@Override
	public SocialActivitySet getUserActivitySet(
			long groupId, long userId, int type)
		throws PortalException {

		return socialActivitySetPersistence.findByG_U_T_First(
			groupId, userId, type,
			SocialActivitySetModifiedDateComparator.getInstance(false));
	}

	@Override
	public SocialActivitySet getUserActivitySet(
			long groupId, long userId, long classNameId, int type)
		throws PortalException {

		return socialActivitySetPersistence.findByG_U_C_T_First(
			groupId, userId, classNameId, type,
			SocialActivitySetModifiedDateComparator.getInstance(false));
	}

	@Override
	public List<SocialActivitySet> getUserActivitySets(
		long userId, int start, int end) {

		return socialActivitySetPersistence.findByUserId(userId, start, end);
	}

	@Override
	public int getUserActivitySetsCount(long userId) {
		return socialActivitySetPersistence.countByUserId(userId);
	}

	@Override
	public List<SocialActivitySet> getUserGroupsActivitySets(
		long userId, int start, int end) {

		return socialActivitySetFinder.findByUserGroups(userId, start, end);
	}

	@Override
	public int getUserGroupsActivitySetsCount(long userId) {
		return socialActivitySetFinder.countByUserGroups(userId);
	}

	@Override
	public List<SocialActivitySet> getUserViewableActivitySets(
		long userId, int start, int end) {

		return socialActivitySetFinder.findByUser(userId, start, end);
	}

	@Override
	public int getUserViewableActivitySetsCount(long userId) {
		return socialActivitySetFinder.countByUser(userId);
	}

	@Override
	public void incrementActivityCount(long activitySetId, long activityId)
		throws PortalException {

		// Activity set

		SocialActivitySet activitySet =
			socialActivitySetPersistence.findByPrimaryKey(activitySetId);

		SocialActivity activity = _socialActivityPersistence.findByPrimaryKey(
			activityId);

		activitySet.setUserId(activity.getUserId());
		activitySet.setModifiedDate(activity.getCreateDate());

		activitySet.setActivityCount(activitySet.getActivityCount() + 1);

		socialActivitySetPersistence.update(activitySet);

		// Activity

		activity.setActivitySetId(activitySetId);

		_socialActivityPersistence.update(activity);
	}

	@BeanReference(type = SocialActivityPersistence.class)
	private SocialActivityPersistence _socialActivityPersistence;

}