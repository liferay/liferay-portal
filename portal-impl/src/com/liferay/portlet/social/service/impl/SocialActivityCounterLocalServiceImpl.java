/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.social.service.impl;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.persistence.AssetEntryPersistence;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.lock.LockProtectedAction;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.persistence.GroupPersistence;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.portlet.social.model.impl.SocialActivityImpl;
import com.liferay.portlet.social.service.base.SocialActivityCounterLocalServiceBaseImpl;
import com.liferay.social.kernel.model.SocialAchievement;
import com.liferay.social.kernel.model.SocialActivity;
import com.liferay.social.kernel.model.SocialActivityConstants;
import com.liferay.social.kernel.model.SocialActivityCounter;
import com.liferay.social.kernel.model.SocialActivityCounterConstants;
import com.liferay.social.kernel.model.SocialActivityCounterDefinition;
import com.liferay.social.kernel.model.SocialActivityDefinition;
import com.liferay.social.kernel.model.SocialActivityLimit;
import com.liferay.social.kernel.model.SocialActivityProcessor;
import com.liferay.social.kernel.service.SocialActivityLimitLocalService;
import com.liferay.social.kernel.service.SocialActivitySettingLocalService;
import com.liferay.social.kernel.service.persistence.SocialActivityCounterFinder;
import com.liferay.social.kernel.service.persistence.SocialActivityLimitPersistence;
import com.liferay.social.kernel.util.SocialCounterPeriodUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The social activity counter local service. This service is responsible for
 * creating and/or incrementing counters in response to an activity. It also
 * provides methods for querying activity counters within a time period.
 *
 * <p>
 * Under normal circumstances only the {@link
 * #addActivityCounters(SocialActivity)} should be called directly and even that
 * is usually not necessary as it is automatically called by the social activity
 * service.
 * </p>
 *
 * @author Zsolt Berentey
 * @author Shuyang Zhou
 */
public class SocialActivityCounterLocalServiceImpl
	extends SocialActivityCounterLocalServiceBaseImpl {

	/**
	 * Adds an activity counter specifying a previous activity and period
	 * length.
	 *
	 * <p>
	 * This method uses the lock service to guard against multiple threads
	 * trying to insert the same counter because this service is called
	 * asynchronously from the social activity service.
	 * </p>
	 *
	 * @param  groupId the primary key of the group
	 * @param  classNameId the primary key of the entity's class this counter
	 *         belongs to
	 * @param  classPK the primary key of the entity this counter belongs to
	 * @param  name the counter name
	 * @param  ownerType the counter's owner type. Acceptable values are
	 *         <code>TYPE_ACTOR</code>, <code>TYPE_ASSET</code> and
	 *         <code>TYPE_CREATOR</code> defined in {@link
	 *         SocialActivityCounterConstants}.
	 * @param  totalValue the counter's total value (optionally <code>0</code>)
	 * @param  previousActivityCounterId the primary key of the activity counter
	 *         for the previous time period (optionally <code>0</code>, if this
	 *         is the first)
	 * @param  periodLength the period length in days,
	 *         <code>PERIOD_LENGTH_INFINITE</code> for never ending counters or
	 *         <code>PERIOD_LENGTH_SYSTEM</code> for the period length defined
	 *         in <code>portal-ext.properties</code>. For more information see
	 *         {@link SocialActivityCounterConstants}.
	 * @return the added activity counter
	 */
	@Override
	public SocialActivityCounter addActivityCounter(
			long groupId, long classNameId, long classPK, String name,
			int ownerType, int totalValue, long previousActivityCounterId,
			int periodLength)
		throws PortalException {

		SocialActivityCounter activityCounter = null;

		if (previousActivityCounterId != 0) {
			activityCounter = socialActivityCounterPersistence.findByPrimaryKey(
				previousActivityCounterId);

			if (periodLength ==
					SocialActivityCounterConstants.PERIOD_LENGTH_SYSTEM) {

				activityCounter.setEndPeriod(
					SocialCounterPeriodUtil.getStartPeriod() - 1);
			}
			else {
				activityCounter.setEndPeriod(
					activityCounter.getStartPeriod() + periodLength - 1);
			}

			activityCounter = socialActivityCounterPersistence.update(
				activityCounter);
		}

		activityCounter = socialActivityCounterPersistence.fetchByG_C_C_N_O_E(
			groupId, classNameId, classPK, name, ownerType,
			SocialActivityCounterConstants.END_PERIOD_UNDEFINED, false);

		if (activityCounter != null) {
			return activityCounter;
		}

		Group group = _groupPersistence.findByPrimaryKey(groupId);

		long activityCounterId = counterLocalService.increment();

		activityCounter = socialActivityCounterPersistence.create(
			activityCounterId);

		activityCounter.setGroupId(groupId);
		activityCounter.setCompanyId(group.getCompanyId());
		activityCounter.setClassNameId(classNameId);
		activityCounter.setClassPK(classPK);
		activityCounter.setName(name);
		activityCounter.setOwnerType(ownerType);
		activityCounter.setTotalValue(totalValue);

		if (periodLength ==
				SocialActivityCounterConstants.PERIOD_LENGTH_SYSTEM) {

			activityCounter.setStartPeriod(
				SocialCounterPeriodUtil.getStartPeriod());
		}
		else {
			activityCounter.setStartPeriod(
				SocialCounterPeriodUtil.getActivityDay());
		}

		activityCounter.setEndPeriod(
			SocialActivityCounterConstants.END_PERIOD_UNDEFINED);
		activityCounter.setActive(true);

		return socialActivityCounterPersistence.update(activityCounter);
	}

	/**
	 * Adds or increments activity counters related to an activity.
	 *
	 * <p>
	 * This method is called asynchronously from the social activity service
	 * when the user performs an activity defined in
	 * <code>liferay-social.xml</code>.
	 * </p>
	 *
	 * <p>
	 * This method first calls the activity processor class, if there is one
	 * defined for the activity, checks for limits and increments all the
	 * counters that belong to the activity. Afterwards, it processes the
	 * activity with respect to achievement classes, if any. Lastly it
	 * increments the built-in <code>user.activities</code> and
	 * <code>asset.activities</code> counters.
	 * </p>
	 *
	 * @param activity the social activity
	 */
	@Override
	public void addActivityCounters(SocialActivity activity)
		throws PortalException {

		if (!_socialActivitySettingLocalService.isEnabled(
				activity.getGroupId(), activity.getClassNameId()) ||
			!_socialActivitySettingLocalService.isEnabled(
				activity.getGroupId(), activity.getClassNameId(),
				activity.getClassPK())) {

			return;
		}

		if ((activity.getType() ==
				SocialActivityConstants.TYPE_MOVE_ATTACHMENT_TO_TRASH) ||
			(activity.getType() ==
				SocialActivityConstants.TYPE_MOVE_TO_TRASH)) {

			disableActivityCounters(
				activity.getClassNameId(), activity.getClassPK());

			return;
		}

		if ((activity.getType() ==
				SocialActivityConstants.TYPE_RESTORE_ATTACHMENT_FROM_TRASH) ||
			(activity.getType() ==
				SocialActivityConstants.TYPE_RESTORE_FROM_TRASH)) {

			enableActivityCounters(
				activity.getClassNameId(), activity.getClassPK());

			return;
		}

		SocialActivityDefinition activityDefinition =
			_socialActivitySettingLocalService.getActivityDefinition(
				activity.getGroupId(), activity.getClassName(),
				activity.getType());

		if ((activityDefinition == null) ||
			!activityDefinition.isCountersEnabled()) {

			return;
		}

		SocialActivityProcessor activityProcessor =
			activityDefinition.getActivityProcessor();

		if (activityProcessor != null) {
			activityProcessor.processActivity(activity);
		}

		User user = _userPersistence.findByPrimaryKey(activity.getUserId());

		AssetEntry assetEntry = activity.getAssetEntry();

		User assetEntryUser = _userPersistence.findByPrimaryKey(
			assetEntry.getUserId());

		List<SocialActivityCounter> activityCounters = new ArrayList<>();

		for (SocialActivityCounterDefinition activityCounterDefinition :
				activityDefinition.getActivityCounterDefinitions()) {

			if (isAddActivityCounter(
					user, assetEntryUser, assetEntry,
					activityCounterDefinition)) {

				SocialActivityCounter activityCounter = addActivityCounter(
					activity.getGroupId(), user, activity,
					activityCounterDefinition);

				activityCounters.add(activityCounter);
			}
		}

		SocialActivityCounter assetActivitiesCounter = null;

		if (!assetEntryUser.isGuestUser() && assetEntryUser.isActive() &&
			assetEntry.isVisible()) {

			assetActivitiesCounter = addAssetActivitiesCounter(activity);
		}

		SocialActivityCounter userActivitiesCounter = null;

		if (!user.isGuestUser() && user.isActive()) {
			userActivitiesCounter = addUserActivitiesCounter(activity);
		}

		for (SocialActivityCounter activityCounter : activityCounters) {
			SocialActivityCounterDefinition activityCounterDefinition =
				activityDefinition.getActivityCounterDefinition(
					activityCounter.getName());

			if (checkActivityLimit(user, activity, activityCounterDefinition)) {
				incrementActivityCounter(
					activityCounter, activityCounterDefinition);
			}
		}

		if (assetActivitiesCounter != null) {
			incrementActivityCounter(
				assetActivitiesCounter,
				_assetActivitiesActivityCounterDefinition);
		}

		if (userActivitiesCounter != null) {
			incrementActivityCounter(
				userActivitiesCounter,
				_userActivitiesActivityCounterDefinition);
		}

		for (SocialAchievement achievement :
				activityDefinition.getAchievements()) {

			achievement.processActivity(activity);
		}
	}

	/**
	 * Deletes all activity counters, limits, and settings related to the asset.
	 *
	 * <p>
	 * This method subtracts the asset's popularity from the owner's
	 * contribution points. It also creates a new contribution period if the
	 * latest one does not belong to the current period.
	 * </p>
	 *
	 * @param assetEntry the asset entry
	 */
	@Override
	public void deleteActivityCounters(AssetEntry assetEntry)
		throws PortalException {

		if (assetEntry == null) {
			return;
		}

		adjustUserContribution(assetEntry, false);

		socialActivityCounterPersistence.removeByC_C(
			assetEntry.getClassNameId(), assetEntry.getClassPK());

		_socialActivityLimitPersistence.removeByC_C(
			assetEntry.getClassNameId(), assetEntry.getClassPK());

		_socialActivitySettingLocalService.deleteActivitySetting(
			assetEntry.getGroupId(), assetEntry.getClassName(),
			assetEntry.getClassPK());

		clearFinderCache();
	}

	/**
	 * Deletes all activity counters, limits, and settings related to the entity
	 * identified by the class name ID and class primary key.
	 *
	 * @param classNameId the primary key of the entity's class
	 * @param classPK the primary key of the entity
	 */
	@Override
	public void deleteActivityCounters(long classNameId, long classPK)
		throws PortalException {

		String className = PortalUtil.getClassName(classNameId);

		if (!className.equals(User.class.getName())) {
			AssetEntry assetEntry = _assetEntryPersistence.fetchByC_C(
				classNameId, classPK);

			deleteActivityCounters(assetEntry);
		}
		else {
			socialActivityCounterPersistence.removeByC_C(classNameId, classPK);

			_socialActivityLimitPersistence.removeByUserId(classPK);
		}

		clearFinderCache();
	}

	/**
	 * Deletes all activity counters for the entity identified by the class name
	 * and class primary key.
	 *
	 * @param className the entity's class name
	 * @param classPK the primary key of the entity
	 */
	@Override
	public void deleteActivityCounters(String className, long classPK)
		throws PortalException {

		long classNameId = _classNameLocalService.getClassNameId(className);

		if (!className.equals(User.class.getName())) {
			AssetEntry assetEntry = _assetEntryPersistence.fetchByC_C(
				classNameId, classPK);

			deleteActivityCounters(assetEntry);
		}
		else {
			socialActivityCounterPersistence.removeByC_C(classNameId, classPK);

			_socialActivityLimitPersistence.removeByUserId(classPK);
		}

		clearFinderCache();
	}

	/**
	 * Disables all the counters of an asset identified by the class name ID and
	 * class primary key.
	 *
	 * <p>
	 * This method is used by the recycle bin to disable all counters of assets
	 * put into the recycle bin. It adjusts the owner's contribution score.
	 * </p>
	 *
	 * @param classNameId the primary key of the asset's class
	 * @param classPK the primary key of the asset
	 */
	@Override
	public void disableActivityCounters(long classNameId, long classPK)
		throws PortalException {

		List<SocialActivityCounter> activityCounters =
			socialActivityCounterPersistence.findByC_C(classNameId, classPK);

		if (activityCounters.isEmpty()) {
			return;
		}

		AssetEntry assetEntry = _assetEntryPersistence.fetchByC_C(
			classNameId, classPK);

		if (assetEntry == null) {
			return;
		}

		adjustUserContribution(assetEntry, false);

		for (SocialActivityCounter activityCounter : activityCounters) {
			if (activityCounter.isActive()) {
				activityCounter.setActive(false);

				socialActivityCounterPersistence.update(activityCounter);
			}
		}

		clearFinderCache();
	}

	/**
	 * Disables all the counters of an asset identified by the class name and
	 * class primary key.
	 *
	 * <p>
	 * This method is used by the recycle bin to disable all counters of assets
	 * put into the recycle bin. It adjusts the owner's contribution score.
	 * </p>
	 *
	 * @param className the asset's class name
	 * @param classPK the primary key of the asset
	 */
	@Override
	public void disableActivityCounters(String className, long classPK)
		throws PortalException {

		disableActivityCounters(
			_classNameLocalService.getClassNameId(className), classPK);
	}

	/**
	 * Enables all activity counters of an asset identified by the class name ID
	 * and class primary key.
	 *
	 * <p>
	 * This method is used by the recycle bin to enable all counters of assets
	 * restored from the recycle bin. It adjusts the owner's contribution score.
	 * </p>
	 *
	 * @param classNameId the primary key of the asset's class
	 * @param classPK the primary key of the asset
	 */
	@Override
	public void enableActivityCounters(long classNameId, long classPK)
		throws PortalException {

		List<SocialActivityCounter> activityCounters =
			socialActivityCounterPersistence.findByC_C(classNameId, classPK);

		if (activityCounters.isEmpty()) {
			return;
		}

		AssetEntry assetEntry = _assetEntryPersistence.fetchByC_C(
			classNameId, classPK);

		if (assetEntry == null) {
			return;
		}

		adjustUserContribution(assetEntry, true);

		for (SocialActivityCounter activityCounter : activityCounters) {
			if (!activityCounter.isActive()) {
				activityCounter.setActive(true);

				socialActivityCounterPersistence.update(activityCounter);
			}
		}

		clearFinderCache();
	}

	/**
	 * Enables all the counters of an asset identified by the class name and
	 * class primary key.
	 *
	 * <p>
	 * This method is used by the recycle bin to enable all counters of assets
	 * restored from the recycle bin. It adjusts the owner's contribution score.
	 * </p>
	 *
	 * @param className the asset's class name
	 * @param classPK the primary key of the asset
	 */
	@Override
	public void enableActivityCounters(String className, long classPK)
		throws PortalException {

		enableActivityCounters(
			_classNameLocalService.getClassNameId(className), classPK);
	}

	/**
	 * Returns the activity counter with the given name, owner, and end period
	 * that belong to the given entity.
	 *
	 * @param  groupId the primary key of the group
	 * @param  classNameId the primary key of the entity's class
	 * @param  classPK the primary key of the entity
	 * @param  name the counter name
	 * @param  ownerType the owner type
	 * @param  endPeriod the end period, <code>-1</code> for the latest one
	 * @return the matching activity counter
	 */
	@Override
	public SocialActivityCounter fetchActivityCounterByEndPeriod(
		long groupId, long classNameId, long classPK, String name,
		int ownerType, int endPeriod) {

		return socialActivityCounterPersistence.fetchByG_C_C_N_O_E(
			groupId, classNameId, classPK, name, ownerType, endPeriod);
	}

	/**
	 * Returns the activity counter with the given name, owner, and start period
	 * that belong to the given entity.
	 *
	 * @param  groupId the primary key of the group
	 * @param  classNameId the primary key of the entity's class
	 * @param  classPK the primary key of the entity
	 * @param  name the counter name
	 * @param  ownerType the owner type
	 * @param  startPeriod the start period
	 * @return the matching activity counter
	 */
	@Override
	public SocialActivityCounter fetchActivityCounterByStartPeriod(
		long groupId, long classNameId, long classPK, String name,
		int ownerType, int startPeriod) {

		return socialActivityCounterPersistence.fetchByG_C_C_N_O_S(
			groupId, classNameId, classPK, name, ownerType, startPeriod);
	}

	/**
	 * Returns the latest activity counter with the given name and owner that
	 * belong to the given entity.
	 *
	 * @param  groupId the primary key of the group
	 * @param  classNameId the primary key of the entity's class
	 * @param  classPK the primary key of the entity
	 * @param  name the counter name
	 * @param  ownerType the owner type
	 * @return the matching activity counter
	 */
	@Override
	public SocialActivityCounter fetchLatestActivityCounter(
		long groupId, long classNameId, long classPK, String name,
		int ownerType) {

		return socialActivityCounterPersistence.fetchByG_C_C_N_O_E(
			groupId, classNameId, classPK, name, ownerType,
			SocialActivityCounterConstants.END_PERIOD_UNDEFINED);
	}

	/**
	 * Returns all the activity counters with the given name and period offsets.
	 *
	 * <p>
	 * The start and end offsets can belong to different periods. This method
	 * groups the counters by name and returns the sum of their current values.
	 * </p>
	 *
	 * @param  groupId the primary key of the group
	 * @param  name the counter name
	 * @param  startOffset the offset for the start period
	 * @param  endOffset the offset for the end period
	 * @return the matching activity counters
	 */
	@Override
	public List<SocialActivityCounter> getOffsetActivityCounters(
		long groupId, String name, int startOffset, int endOffset) {

		return getPeriodActivityCounters(
			groupId, name, SocialCounterPeriodUtil.getStartPeriod(startOffset),
			SocialCounterPeriodUtil.getEndPeriod(endOffset));
	}

	/**
	 * Returns the distribution of the activity counters with the given name and
	 * period offsets.
	 *
	 * <p>
	 * The start and end offsets can belong to different periods. This method
	 * groups the counters by their owner entity (usually some asset) and
	 * returns a counter for each entity class with the sum of the counters'
	 * current values.
	 * </p>
	 *
	 * @param  groupId the primary key of the group
	 * @param  name the counter name
	 * @param  startOffset the offset for the start period
	 * @param  endOffset the offset for the end period
	 * @return the distribution of matching activity counters
	 */
	@Override
	public List<SocialActivityCounter> getOffsetDistributionActivityCounters(
		long groupId, String name, int startOffset, int endOffset) {

		return getPeriodDistributionActivityCounters(
			groupId, name, SocialCounterPeriodUtil.getStartPeriod(startOffset),
			SocialCounterPeriodUtil.getEndPeriod(endOffset));
	}

	/**
	 * Returns all the activity counters with the given name and time period.
	 *
	 * <p>
	 * The start and end period values can belong to different periods. This
	 * method groups the counters by name and returns the sum of their current
	 * values.
	 * </p>
	 *
	 * @param  groupId the primary key of the group
	 * @param  name the counter name
	 * @param  startPeriod the start period
	 * @param  endPeriod the end period
	 * @return the matching activity counters
	 */
	@Override
	public List<SocialActivityCounter> getPeriodActivityCounters(
		long groupId, String name, int startPeriod, int endPeriod) {

		if (endPeriod == SocialActivityCounterConstants.END_PERIOD_UNDEFINED) {
			endPeriod = SocialCounterPeriodUtil.getEndPeriod();
		}

		int periodLength = SocialCounterPeriodUtil.getPeriodLength(
			SocialCounterPeriodUtil.getOffset(endPeriod));

		return socialActivityCounterFinder.findAC_ByG_N_S_E_1(
			groupId, name, startPeriod, endPeriod, periodLength);
	}

	/**
	 * Returns the distribution of activity counters with the given name and
	 * time period.
	 *
	 * <p>
	 * The start and end period values can belong to different periods. This
	 * method groups the counters by their owner entity (usually some asset) and
	 * returns a counter for each entity class with the sum of the counters'
	 * current values.
	 * </p>
	 *
	 * @param  groupId the primary key of the group
	 * @param  name the counter name
	 * @param  startPeriod the start period
	 * @param  endPeriod the end period
	 * @return the distribution of matching activity counters
	 */
	@Override
	public List<SocialActivityCounter> getPeriodDistributionActivityCounters(
		long groupId, String name, int startPeriod, int endPeriod) {

		int periodLength = SocialCounterPeriodUtil.getPeriodLength(
			SocialCounterPeriodUtil.getOffset(endPeriod));

		return socialActivityCounterFinder.findAC_ByG_N_S_E_2(
			groupId, name, startPeriod, endPeriod, periodLength);
	}

	/**
	 * Returns the range of tuples that contain users and a list of activity
	 * counters.
	 *
	 * <p>
	 * The counters returned for each user are passed to this method in the
	 * selectedNames array. The method also accepts an array of counter names
	 * that are used to rank the users.
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
	 * @param  rankingNames the ranking counter names
	 * @param  selectedNames the counter names that will be returned with each
	 *         user
	 * @param  start the lower bound of the range of results
	 * @param  end the upper bound of the range of results (not inclusive)
	 * @return the range of matching tuples
	 */
	@Override
	public List<Tuple> getUserActivityCounters(
		long groupId, String[] rankingNames, String[] selectedNames, int start,
		int end) {

		List<Long> userIds = socialActivityCounterFinder.findU_ByG_N(
			groupId, rankingNames, start, end);

		if (userIds.isEmpty()) {
			return Collections.emptyList();
		}

		Tuple[] userActivityCounters = new Tuple[userIds.size()];

		List<SocialActivityCounter> activityCounters =
			socialActivityCounterFinder.findAC_By_G_C_C_N_S_E(
				groupId, userIds, selectedNames, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		long userId = 0;
		Map<String, SocialActivityCounter> activityCountersMap = null;

		for (SocialActivityCounter activityCounter : activityCounters) {
			if (userId != activityCounter.getClassPK()) {
				userId = activityCounter.getClassPK();
				activityCountersMap = new HashMap<>();

				Tuple userActivityCounter = new Tuple(
					userId, activityCountersMap);

				for (int i = 0; i < userIds.size(); i++) {
					long curUserId = userIds.get(i);

					if (userId == curUserId) {
						userActivityCounters[i] = userActivityCounter;

						break;
					}
				}
			}

			activityCountersMap.put(activityCounter.getName(), activityCounter);
		}

		return Arrays.asList(userActivityCounters);
	}

	/**
	 * Returns the number of users having a rank based on the given counters.
	 *
	 * @param  groupId the primary key of the group
	 * @param  rankingNames the ranking counter names
	 * @return the number of matching users
	 */
	@Override
	public int getUserActivityCountersCount(
		long groupId, String[] rankingNames) {

		return socialActivityCounterFinder.countU_ByG_N(groupId, rankingNames);
	}

	/**
	 * Increments the <code>user.achievements</code> counter for a user.
	 *
	 * <p>
	 * This method should be used by an external achievement class when the
	 * users unlocks an achievement.
	 * </p>
	 *
	 * @param userId the primary key of the user
	 * @param groupId the primary key of the group
	 */
	@Override
	public void incrementUserAchievementCounter(long userId, long groupId)
		throws PortalException {

		User user = _userPersistence.findByPrimaryKey(userId);

		SocialActivityCounter activityCounter = addActivityCounter(
			groupId, user, new SocialActivityImpl(),
			_userAchievementsActivityCounterDefinition);

		incrementActivityCounter(
			activityCounter, _userAchievementsActivityCounterDefinition);
	}

	protected SocialActivityCounter addActivityCounter(
			long groupId, User user, SocialActivity activity,
			SocialActivityCounterDefinition activityCounterDefinition)
		throws PortalException {

		int ownerType = activityCounterDefinition.getOwnerType();

		long classNameId = getClassNameId(activity.getAssetEntry(), ownerType);
		long classPK = getClassPK(user, activity.getAssetEntry(), ownerType);

		SocialActivityCounter activityCounter = fetchLatestActivityCounter(
			groupId, classNameId, classPK, activityCounterDefinition.getName(),
			ownerType);

		if (activityCounter == null) {
			activityCounter = lockProtectedAddActivityCounter(
				groupId, classNameId, classPK,
				activityCounterDefinition.getName(), ownerType, 0, 0,
				activityCounterDefinition.getPeriodLength());
		}
		else if (!activityCounter.isActivePeriod(
					activityCounterDefinition.getPeriodLength())) {

			activityCounter = lockProtectedAddActivityCounter(
				groupId, classNameId, classPK,
				activityCounterDefinition.getName(), ownerType,
				activityCounter.getTotalValue(),
				activityCounter.getActivityCounterId(),
				activityCounterDefinition.getPeriodLength());
		}

		if (activityCounterDefinition.getLimitValue() > 0) {
			SocialActivityLimit activityLimit =
				_socialActivityLimitPersistence.fetchByG_U_C_C_A_A(
					groupId, user.getUserId(), activity.getClassNameId(),
					getLimitClassPK(activity, activityCounterDefinition),
					activity.getType(), activityCounterDefinition.getName());

			if (activityLimit == null) {
				lockProtectedGetActivityLimit(
					groupId, user, activity, activityCounterDefinition);
			}
		}

		return activityCounter;
	}

	protected SocialActivityCounter addAssetActivitiesCounter(
			SocialActivity activity)
		throws PortalException {

		User user = _userPersistence.findByPrimaryKey(activity.getUserId());

		return addActivityCounter(
			activity.getGroupId(), user, activity,
			_assetActivitiesActivityCounterDefinition);
	}

	protected SocialActivityCounter addUserActivitiesCounter(
			SocialActivity activity)
		throws PortalException {

		User user = _userPersistence.findByPrimaryKey(activity.getUserId());

		return addActivityCounter(
			activity.getGroupId(), user, activity,
			_userActivitiesActivityCounterDefinition);
	}

	protected void adjustUserContribution(AssetEntry assetEntry, boolean enable)
		throws PortalException {

		if (assetEntry == null) {
			return;
		}

		SocialActivityCounter latestPopularityActivityCounter =
			fetchLatestActivityCounter(
				assetEntry.getGroupId(), assetEntry.getClassNameId(),
				assetEntry.getClassPK(),
				SocialActivityCounterConstants.NAME_POPULARITY,
				SocialActivityCounterConstants.TYPE_ASSET);

		if ((latestPopularityActivityCounter == null) ||
			(enable && latestPopularityActivityCounter.isActive()) ||
			(!enable && !latestPopularityActivityCounter.isActive())) {

			return;
		}

		int factor = -1;

		if (enable) {
			factor = 1;
		}

		SocialActivityCounter latestContributionActivityCounter =
			fetchLatestActivityCounter(
				assetEntry.getGroupId(),
				_classNameLocalService.getClassNameId(User.class.getName()),
				assetEntry.getUserId(),
				SocialActivityCounterConstants.NAME_CONTRIBUTION,
				SocialActivityCounterConstants.TYPE_CREATOR);

		if (latestContributionActivityCounter == null) {
			return;
		}

		int startPeriod = SocialCounterPeriodUtil.getStartPeriod();

		if (latestContributionActivityCounter.getStartPeriod() != startPeriod) {
			latestContributionActivityCounter = addActivityCounter(
				latestContributionActivityCounter.getGroupId(),
				latestContributionActivityCounter.getClassNameId(),
				latestContributionActivityCounter.getClassPK(),
				latestContributionActivityCounter.getName(),
				latestContributionActivityCounter.getOwnerType(),
				latestContributionActivityCounter.getTotalValue(),
				latestContributionActivityCounter.getActivityCounterId(),
				SocialActivityCounterConstants.PERIOD_LENGTH_SYSTEM);
		}

		if (latestPopularityActivityCounter.getStartPeriod() == startPeriod) {
			int latestPopularityActivityCounterCurrentValue =
				latestPopularityActivityCounter.getCurrentValue();

			latestContributionActivityCounter.setCurrentValue(
				latestContributionActivityCounter.getCurrentValue() +
					(latestPopularityActivityCounterCurrentValue * factor));
		}

		latestContributionActivityCounter.setTotalValue(
			latestContributionActivityCounter.getTotalValue() +
				(latestPopularityActivityCounter.getTotalValue() * factor));

		socialActivityCounterPersistence.update(
			latestContributionActivityCounter);
	}

	protected boolean checkActivityLimit(
			User user, SocialActivity activity,
			SocialActivityCounterDefinition activityCounterDefinition)
		throws PortalException {

		if (activityCounterDefinition.getLimitValue() == 0) {
			return true;
		}

		long classPK = activity.getClassPK();

		String name = activityCounterDefinition.getName();

		if (name.equals(SocialActivityCounterConstants.NAME_PARTICIPATION)) {
			classPK = 0;
		}

		SocialActivityLimit activityLimit =
			_socialActivityLimitPersistence.findByG_U_C_C_A_A(
				activity.getGroupId(), user.getUserId(),
				activity.getClassNameId(), classPK, activity.getType(), name);

		int count = activityLimit.getCount(
			activityCounterDefinition.getLimitPeriod());

		if (count < activityCounterDefinition.getLimitValue()) {
			activityLimit.setCount(
				activityCounterDefinition.getLimitPeriod(), count + 1);

			_socialActivityLimitPersistence.update(activityLimit);

			return true;
		}

		return false;
	}

	protected void clearFinderCache() {
		PortalCache<String, SocialActivityCounter> portalCache =
			PortalCacheHelperUtil.getPortalCache(
				PortalCacheManagerNames.MULTI_VM,
				SocialActivityCounterFinder.class.getName());

		portalCache.removeAll();
	}

	protected long getClassNameId(AssetEntry assetEntry, int ownerType) {
		if (ownerType == SocialActivityCounterConstants.TYPE_ASSET) {
			return assetEntry.getClassNameId();
		}

		return _classNameLocalService.getClassNameId(User.class.getName());
	}

	protected long getClassPK(User user, AssetEntry assetEntry, int ownerType) {
		if (ownerType == SocialActivityCounterConstants.TYPE_ACTOR) {
			return user.getUserId();
		}

		if (ownerType == SocialActivityCounterConstants.TYPE_ASSET) {
			return assetEntry.getClassPK();
		}

		return assetEntry.getUserId();
	}

	protected long getLimitClassPK(
		SocialActivity activity,
		SocialActivityCounterDefinition activityCounterDefinition) {

		String name = activityCounterDefinition.getName();

		if (name.equals(SocialActivityCounterConstants.NAME_PARTICIPATION)) {
			return 0;
		}

		return activity.getClassPK();
	}

	protected void incrementActivityCounter(
		SocialActivityCounter activityCounter,
		SocialActivityCounterDefinition activityCounterDefinition) {

		activityCounter.setCurrentValue(
			activityCounter.getCurrentValue() +
				activityCounterDefinition.getIncrement());
		activityCounter.setTotalValue(
			activityCounter.getTotalValue() +
				activityCounterDefinition.getIncrement());

		activityCounter = socialActivityCounterPersistence.update(
			activityCounter);

		socialActivityCounterPersistence.clearCache(activityCounter);
	}

	protected boolean isAddActivityCounter(
		User user, User assetEntryUser, AssetEntry assetEntry,
		SocialActivityCounterDefinition activityCounterDefinition) {

		if ((user.isGuestUser() || !user.isActive()) &&
			(activityCounterDefinition.getOwnerType() !=
				SocialActivityCounterConstants.TYPE_ASSET)) {

			return false;
		}

		if ((assetEntryUser.isGuestUser() || !assetEntryUser.isActive()) &&
			(activityCounterDefinition.getOwnerType() !=
				SocialActivityCounterConstants.TYPE_ACTOR)) {

			return false;
		}

		if (!activityCounterDefinition.isEnabled() ||
			(activityCounterDefinition.getIncrement() == 0)) {

			return false;
		}

		String name = activityCounterDefinition.getName();

		if ((user.getUserId() == assetEntryUser.getUserId()) &&
			(name.equals(SocialActivityCounterConstants.NAME_CONTRIBUTION) ||
			 name.equals(SocialActivityCounterConstants.NAME_POPULARITY))) {

			return false;
		}

		if ((activityCounterDefinition.getOwnerType() ==
				SocialActivityCounterConstants.TYPE_ASSET) &&
			!assetEntry.isVisible()) {

			return false;
		}

		return true;
	}

	protected SocialActivityCounter lockProtectedAddActivityCounter(
			final long groupId, final long classNameId, final long classPK,
			final String name, final int ownerType, final int totalValue,
			final long previousActivityCounterId, final int periodLength)
		throws PortalException {

		String lockKey = StringUtil.merge(
			new Object[] {groupId, classNameId, classPK, name, ownerType},
			StringPool.POUND);

		LockProtectedAction<SocialActivityCounter> lockProtectedAction =
			new LockProtectedAction<SocialActivityCounter>(
				SocialActivityCounter.class, lockKey,
				PropsValues.SOCIAL_ACTIVITY_LOCK_TIMEOUT,
				PropsValues.SOCIAL_ACTIVITY_LOCK_RETRY_DELAY) {

				@Override
				protected SocialActivityCounter performProtectedAction()
					throws PortalException {

					return socialActivityCounterLocalService.addActivityCounter(
						groupId, classNameId, classPK, name, ownerType,
						totalValue, previousActivityCounterId, periodLength);
				}

			};

		lockProtectedAction.performAction();

		return lockProtectedAction.getReturnValue();
	}

	protected void lockProtectedGetActivityLimit(
			final long groupId, final User user, final SocialActivity activity,
			final SocialActivityCounterDefinition activityCounterDefinition)
		throws PortalException {

		final long classPK = getLimitClassPK(
			activity, activityCounterDefinition);

		String lockKey = StringUtil.merge(
			new Object[] {
				groupId, user.getUserId(), activity.getClassNameId(), classPK,
				activity.getType(), activityCounterDefinition.getName()
			},
			StringPool.POUND);

		LockProtectedAction<SocialActivityLimit> lockProtectedAction =
			new LockProtectedAction<SocialActivityLimit>(
				SocialActivityLimit.class, lockKey,
				PropsValues.SOCIAL_ACTIVITY_LOCK_TIMEOUT,
				PropsValues.SOCIAL_ACTIVITY_LOCK_RETRY_DELAY) {

				@Override
				protected SocialActivityLimit performProtectedAction()
					throws PortalException {

					SocialActivityLimit activityLimit =
						_socialActivityLimitPersistence.fetchByG_U_C_C_A_A(
							groupId, user.getUserId(),
							activity.getClassNameId(), classPK,
							activity.getType(),
							activityCounterDefinition.getName());

					if (activityLimit == null) {
						activityLimit =
							_socialActivityLimitLocalService.addActivityLimit(
								user.getUserId(), activity.getGroupId(),
								activity.getClassNameId(), classPK,
								activity.getType(),
								activityCounterDefinition.getName(),
								activityCounterDefinition.getLimitPeriod());
					}

					return activityLimit;
				}

			};

		lockProtectedAction.performAction();

		_socialActivityLimitPersistence.cacheResult(
			lockProtectedAction.getReturnValue());
	}

	private final SocialActivityCounterDefinition
		_assetActivitiesActivityCounterDefinition =
			new SocialActivityCounterDefinition(
				SocialActivityCounterConstants.NAME_ASSET_ACTIVITIES,
				SocialActivityCounterConstants.TYPE_ASSET);

	@BeanReference(type = AssetEntryPersistence.class)
	private AssetEntryPersistence _assetEntryPersistence;

	@BeanReference(type = ClassNameLocalService.class)
	private ClassNameLocalService _classNameLocalService;

	@BeanReference(type = GroupPersistence.class)
	private GroupPersistence _groupPersistence;

	@BeanReference(type = SocialActivityLimitLocalService.class)
	private SocialActivityLimitLocalService _socialActivityLimitLocalService;

	@BeanReference(type = SocialActivityLimitPersistence.class)
	private SocialActivityLimitPersistence _socialActivityLimitPersistence;

	@BeanReference(type = SocialActivitySettingLocalService.class)
	private SocialActivitySettingLocalService
		_socialActivitySettingLocalService;

	private final SocialActivityCounterDefinition
		_userAchievementsActivityCounterDefinition =
			new SocialActivityCounterDefinition(
				SocialActivityCounterConstants.NAME_USER_ACHIEVEMENTS,
				SocialActivityCounterConstants.TYPE_ACTOR);
	private final SocialActivityCounterDefinition
		_userActivitiesActivityCounterDefinition =
			new SocialActivityCounterDefinition(
				SocialActivityCounterConstants.NAME_USER_ACTIVITIES,
				SocialActivityCounterConstants.TYPE_ACTOR);

	@BeanReference(type = UserPersistence.class)
	private UserPersistence _userPersistence;

}