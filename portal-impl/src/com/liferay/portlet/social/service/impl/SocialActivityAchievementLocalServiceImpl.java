/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.social.service.impl;

import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portlet.social.service.base.SocialActivityAchievementLocalServiceBaseImpl;
import com.liferay.social.kernel.model.SocialAchievement;
import com.liferay.social.kernel.model.SocialActivityAchievement;
import com.liferay.social.kernel.service.SocialActivityCounterLocalService;

import java.util.List;

/**
 * @author Zsolt Berentey
 * @author Brian Wing Shun Chan
 */
public class SocialActivityAchievementLocalServiceImpl
	extends SocialActivityAchievementLocalServiceBaseImpl {

	@Override
	public void addActivityAchievement(
			long userId, long groupId, SocialAchievement achievement)
		throws PortalException {

		SocialActivityAchievement activityAchievement =
			socialActivityAchievementPersistence.fetchByG_U_N(
				groupId, userId, achievement.getName());

		if (activityAchievement != null) {
			return;
		}

		User user = _userPersistence.findByPrimaryKey(userId);

		long activityAchievementId = counterLocalService.increment();

		activityAchievement = socialActivityAchievementPersistence.create(
			activityAchievementId);

		activityAchievement.setGroupId(groupId);
		activityAchievement.setCompanyId(user.getCompanyId());
		activityAchievement.setUserId(userId);
		activityAchievement.setCreateDate(System.currentTimeMillis());

		int count = socialActivityAchievementPersistence.countByG_N(
			groupId, achievement.getName());

		if (count == 0) {
			activityAchievement.setFirstInGroup(true);
		}

		activityAchievement.setName(achievement.getName());

		socialActivityAchievementPersistence.update(activityAchievement);

		_socialActivityCounterLocalService.incrementUserAchievementCounter(
			userId, groupId);
	}

	@Override
	public SocialActivityAchievement fetchUserAchievement(
		long userId, long groupId, String name) {

		return socialActivityAchievementPersistence.fetchByG_U_N(
			groupId, userId, name);
	}

	@Override
	public List<SocialActivityAchievement> getGroupAchievements(
		long groupId, String name) {

		return socialActivityAchievementPersistence.findByG_N(groupId, name);
	}

	@Override
	public int getGroupAchievementsCount(long groupId, String name) {
		return socialActivityAchievementPersistence.countByG_N(groupId, name);
	}

	@Override
	public List<SocialActivityAchievement> getUserAchievements(
		long userId, long groupId) {

		return socialActivityAchievementPersistence.findByG_U(groupId, userId);
	}

	@Override
	public int getUserAchievementsCount(long userId, long groupId) {
		return socialActivityAchievementPersistence.countByG_U(groupId, userId);
	}

	@BeanReference(type = SocialActivityCounterLocalService.class)
	private SocialActivityCounterLocalService
		_socialActivityCounterLocalService;

	@BeanReference(type = UserPersistence.class)
	private UserPersistence _userPersistence;

}