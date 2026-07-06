/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.social.service.impl;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.mass.delete.MassDeleteCacheThreadLocal;
import com.liferay.portal.kernel.messaging.async.Async;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.kernel.transaction.TransactionCallbackUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portlet.asset.util.DeletedAssetEntryThreadLocal;
import com.liferay.portlet.social.service.base.SocialActivityLocalServiceBaseImpl;
import com.liferay.portlet.social.util.SocialActivityHierarchyEntry;
import com.liferay.portlet.social.util.SocialActivityHierarchyEntryThreadLocal;
import com.liferay.social.kernel.model.SocialActivity;
import com.liferay.social.kernel.model.SocialActivityConstants;
import com.liferay.social.kernel.model.SocialActivityDefinition;
import com.liferay.social.kernel.model.SocialActivityTable;
import com.liferay.social.kernel.service.SocialActivityCounterLocalService;
import com.liferay.social.kernel.service.SocialActivityInterpreterLocalService;
import com.liferay.social.kernel.service.SocialActivitySetLocalService;
import com.liferay.social.kernel.service.SocialActivitySettingLocalService;
import com.liferay.social.kernel.service.persistence.SocialActivityCounterPersistence;
import com.liferay.social.kernel.service.persistence.SocialActivityLimitPersistence;
import com.liferay.social.kernel.service.persistence.SocialActivitySetPersistence;
import com.liferay.social.kernel.service.persistence.SocialActivitySettingPersistence;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * The social activity local service. This service provides the means to record
 * and list social activities in groups and organizations.
 *
 * <p>
 * Social activities are identified by their type and the type of asset they are
 * done on. Each activity records the exact time of the action as well as human
 * readable information needed for activity feeds.
 * </p>
 *
 * <p>
 * Most of the <i>get-</i> methods in this service order activities in
 * descending order by their execution times, so the most recent activities are
 * listed first.
 * </p>
 *
 * @author Brian Wing Shun Chan
 */
public class SocialActivityLocalServiceImpl
	extends SocialActivityLocalServiceBaseImpl {

	/**
	 * Records an activity with the given time in the database.
	 *
	 * <p>
	 * This method records a social activity done on an asset, identified by its
	 * class name and class primary key, in the database. Additional information
	 * (such as the original message ID for a reply to a forum post) is passed
	 * in via the <code>extraData</code> in JSON format. For activities
	 * affecting another user, a mirror activity is generated that describes the
	 * action from the user's point of view. The target user's ID is passed in
	 * via the <code>receiverUserId</code>.
	 * </p>
	 *
	 * <p>
	 * Example for a mirrored activity:<br> When a user replies to a message
	 * boards post, the reply action is stored in the database with the
	 * <code>receiverUserId</code> being the ID of the author of the original
	 * message. The <code>extraData</code> contains the ID of the original
	 * message in JSON format. A mirror activity is generated with the values of
	 * the <code>userId</code> and the <code>receiverUserId</code> swapped. This
	 * mirror activity basically describes a "replied to" event.
	 * </p>
	 *
	 * <p>
	 * Mirror activities are most often used in relation to friend requests and
	 * activities.
	 * </p>
	 *
	 * @param userId the primary key of the acting user
	 * @param groupId the primary key of the group
	 * @param createDate the activity's date
	 * @param className the target asset's class name
	 * @param classPK the primary key of the target asset
	 * @param type the activity's type
	 * @param extraData any extra data regarding the activity
	 * @param receiverUserId the primary key of the receiving user
	 */
	@Override
	public void addActivity(
			long userId, long groupId, Date createDate, String className,
			long classPK, int type, String extraData, long receiverUserId)
		throws PortalException {

		if (ExportImportThreadLocal.isImportInProcess()) {
			return;
		}

		User user = _userPersistence.findByPrimaryKey(userId);
		long classNameId = _classNameLocalService.getClassNameId(className);

		if (groupId > 0) {
			Group group = _groupLocalService.getGroup(groupId);

			if (group.isLayout()) {
				Layout layout = _layoutLocalService.getLayout(
					group.getClassPK());

				groupId = layout.getGroupId();
			}
		}

		final SocialActivity activity = socialActivityPersistence.create(0);

		activity.setGroupId(groupId);
		activity.setCompanyId(user.getCompanyId());
		activity.setUserId(user.getUserId());
		activity.setCreateDate(createDate.getTime());
		activity.setMirrorActivityId(0);
		activity.setClassNameId(classNameId);
		activity.setClassPK(classPK);

		SocialActivityHierarchyEntry activityHierarchyEntry =
			SocialActivityHierarchyEntryThreadLocal.peek();

		if (activityHierarchyEntry != null) {
			activity.setParentClassNameId(
				activityHierarchyEntry.getClassNameId());
			activity.setParentClassPK(activityHierarchyEntry.getClassPK());
		}

		activity.setType(type);
		activity.setExtraData(extraData);
		activity.setReceiverUserId(receiverUserId);

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			className, classPK);

		activity.setAssetEntry(assetEntry);

		SocialActivity mirrorActivity = null;

		if ((receiverUserId > 0) && (userId != receiverUserId)) {
			mirrorActivity = socialActivityPersistence.create(0);

			mirrorActivity.setGroupId(groupId);
			mirrorActivity.setCompanyId(user.getCompanyId());
			mirrorActivity.setUserId(receiverUserId);
			mirrorActivity.setCreateDate(createDate.getTime());
			mirrorActivity.setClassNameId(classNameId);
			mirrorActivity.setClassPK(classPK);

			if (activityHierarchyEntry != null) {
				mirrorActivity.setParentClassNameId(
					activityHierarchyEntry.getClassNameId());
				mirrorActivity.setParentClassPK(
					activityHierarchyEntry.getClassPK());
			}

			mirrorActivity.setType(type);
			mirrorActivity.setExtraData(extraData);
			mirrorActivity.setReceiverUserId(user.getUserId());
			mirrorActivity.setAssetEntry(assetEntry);
		}

		final SocialActivity finalMirrorActivity = mirrorActivity;

		Callable<Void> callable = new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				socialActivityLocalService.addActivity(
					activity, finalMirrorActivity);

				return null;
			}

		};

		TransactionCallbackUtil.registerCommitCallback(callable);
	}

	/**
	 * Records an activity in the database, using a time based on the current
	 * time in an attempt to make the activity's time unique.
	 *
	 * @param userId the primary key of the acting user
	 * @param groupId the primary key of the group
	 * @param className the target asset's class name
	 * @param classPK the primary key of the target asset
	 * @param type the activity's type
	 * @param extraData any extra data regarding the activity
	 * @param receiverUserId the primary key of the receiving user
	 */
	@Override
	public void addActivity(
			long userId, long groupId, String className, long classPK, int type,
			String extraData, long receiverUserId)
		throws PortalException {

		addActivity(
			userId, groupId, new Date(), className, classPK, type, extraData,
			receiverUserId);
	}

	@Async
	@Override
	public void addActivity(
			SocialActivity activity, SocialActivity mirrorActivity)
		throws PortalException {

		if (ExportImportThreadLocal.isImportInProcess()) {
			return;
		}

		if ((activity.getActivityId() > 0) ||
			((mirrorActivity != null) &&
			 (mirrorActivity.getActivityId() > 0))) {

			throw new PortalException(
				"Activity and mirror activity must not have primary keys set");
		}

		if (isLogActivity(activity)) {
			activity.setActivityId(
				counterLocalService.increment(SocialActivity.class.getName()));

			activity = socialActivityPersistence.update(activity);

			if (mirrorActivity != null) {
				long mirrorActivityId = counterLocalService.increment(
					SocialActivity.class.getName());

				mirrorActivity.setActivityId(mirrorActivityId);

				mirrorActivity.setMirrorActivityId(activity.getPrimaryKey());

				socialActivityPersistence.update(mirrorActivity);
			}

			_socialActivityInterpreterLocalService.updateActivitySet(
				activity.getActivityId());
		}

		_socialActivityCounterLocalService.addActivityCounters(activity);
	}

	/**
	 * Records an activity in the database, but only if there isn't already an
	 * activity with the same parameters.
	 *
	 * <p>
	 * For the main functionality see {@link #addActivity(long, long, Date,
	 * String, long, int, String, long)}
	 * </p>
	 *
	 * @param userId the primary key of the acting user
	 * @param groupId the primary key of the group
	 * @param createDate the activity's date
	 * @param className the target asset's class name
	 * @param classPK the primary key of the target asset
	 * @param type the activity's type
	 * @param extraData any extra data regarding the activity
	 * @param receiverUserId the primary key of the receiving user
	 */
	@Override
	public void addUniqueActivity(
			long userId, long groupId, Date createDate, String className,
			long classPK, int type, String extraData, long receiverUserId)
		throws PortalException {

		SocialActivity socialActivity =
			socialActivityPersistence.fetchByG_U_CD_C_C_T_R(
				groupId, userId, createDate.getTime(),
				_classNameLocalService.getClassNameId(className), classPK, type,
				receiverUserId);

		if (socialActivity != null) {
			return;
		}

		addActivity(
			userId, groupId, createDate, className, classPK, type, extraData,
			receiverUserId);
	}

	/**
	 * Records an activity with the current time in the database, but only if
	 * there isn't one with the same parameters.
	 *
	 * <p>
	 * For the main functionality see {@link #addActivity(long, long, Date,
	 * String, long, int, String, long)}
	 * </p>
	 *
	 * @param userId the primary key of the acting user
	 * @param groupId the primary key of the group
	 * @param className the target asset's class name
	 * @param classPK the primary key of the target asset
	 * @param type the activity's type
	 * @param extraData any extra data regarding the activity
	 * @param receiverUserId the primary key of the receiving user
	 */
	@Override
	public void addUniqueActivity(
			long userId, long groupId, String className, long classPK, int type,
			String extraData, long receiverUserId)
		throws PortalException {

		int count = socialActivityPersistence.countByG_U_C_C_T_R(
			groupId, userId, _classNameLocalService.getClassNameId(className),
			classPK, type, receiverUserId);

		if (count > 0) {
			return;
		}

		addActivity(
			userId, groupId, new Date(), className, classPK, type, extraData,
			receiverUserId);
	}

	/**
	 * Removes stored activities for the asset.
	 *
	 * @param assetEntry the asset from which to remove stored activities
	 */
	@Override
	public void deleteActivities(AssetEntry assetEntry) throws PortalException {
		deleteActivities(assetEntry.getClassName(), assetEntry.getClassPK());
	}

	@Override
	public void deleteActivities(long groupId) {
		_socialActivitySetPersistence.removeByGroupId(groupId);

		socialActivityPersistence.removeByGroupId(groupId);

		_socialActivityCounterPersistence.removeByGroupId(groupId);

		_socialActivityLimitPersistence.removeByGroupId(groupId);

		_socialActivitySettingPersistence.removeByGroupId(groupId);
	}

	/**
	 * Removes stored activities for the asset identified by the class name and
	 * class primary key.
	 *
	 * @param className the target asset's class name
	 * @param classPK the primary key of the target asset
	 */
	@Override
	public void deleteActivities(String className, long classPK)
		throws PortalException {

		deleteActivities(
			_classNameLocalService.getClassNameId(className), classPK);
	}

	/**
	 * Removes the stored activity from the database.
	 *
	 * @param activityId the primary key of the stored activity
	 */
	@Override
	public void deleteActivity(long activityId) throws PortalException {
		SocialActivity activity = socialActivityPersistence.findByPrimaryKey(
			activityId);

		deleteActivity(activity);
	}

	/**
	 * Removes the stored activity and its mirror activity from the database.
	 *
	 * @param activity the activity to be removed
	 */
	@Override
	public void deleteActivity(SocialActivity activity) throws PortalException {
		_socialActivitySetLocalService.decrementActivityCount(
			activity.getActivitySetId());

		socialActivityPersistence.remove(activity);

		SocialActivity mirrorActivity =
			socialActivityPersistence.fetchByMirrorActivityId(
				activity.getActivityId());

		if (mirrorActivity != null) {
			socialActivityPersistence.remove(mirrorActivity);
		}
	}

	/**
	 * Removes the user's stored activities from the database.
	 *
	 * <p>
	 * This method removes all activities where the user is either the actor or
	 * the receiver.
	 * </p>
	 *
	 * @param userId the primary key of the user
	 */
	@Override
	public void deleteUserActivities(long userId) throws PortalException {
		List<SocialActivity> activities =
			socialActivityPersistence.findByUserId(
				userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (SocialActivity activity : activities) {
			_socialActivitySetLocalService.decrementActivityCount(
				activity.getActivitySetId());

			socialActivityPersistence.remove(activity);
		}

		activities = socialActivityPersistence.findByReceiverUserId(
			userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (SocialActivity activity : activities) {
			_socialActivitySetLocalService.decrementActivityCount(
				activity.getActivitySetId());

			socialActivityPersistence.remove(activity);
		}

		_socialActivityCounterLocalService.deleteActivityCounters(
			User.class.getName(), userId);
	}

	@Override
	public SocialActivity fetchFirstActivity(
		String className, long classPK, int type) {

		return socialActivityPersistence.fetchByC_C_T_First(
			_classNameLocalService.getClassNameId(className), classPK, type,
			null);
	}

	/**
	 * @param      classNameId the target asset's class name ID
	 * @param      start the lower bound of the range of results
	 * @param      end the upper bound of the range of results (not inclusive)
	 * @return     the range of matching activities
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             #getActivities(long, String, int, int)}  Returns a range of
	 *             all the activities done on assets identified by the class
	 *             name ID.  <p> Useful when paginating results. Returns a
	 *             maximum of <code>end - start</code> instances.
	 *             <code>start</code> and <code>end</code> are not primary keys,
	 *             they are indexes in the result set. Thus, <code>0</code>
	 *             refers to the first result in the set. Setting both
	 *             <code>start</code> and <code>end</code> to {@link
	 *             QueryUtil#ALL_POS} will return the full result set.</p>
	 */
	@Deprecated
	@Override
	public List<SocialActivity> getActivities(
		long classNameId, int start, int end) {

		DynamicQuery dynamicQuery = dynamicQuery();

		Property classNameIdProperty = PropertyFactoryUtil.forName(
			"classNameId");

		dynamicQuery.add(classNameIdProperty.eq(classNameId));

		return dynamicQuery(dynamicQuery);
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
		int end) {

		return socialActivityPersistence.findByM_C_C(
			mirrorActivityId, classNameId, classPK, start, end);
	}

	/**
	 * Returns a range of all the activities done on assets identified by the
	 * company ID and class name.
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
	 * @param  className the target asset's class name
	 * @param  start the lower bound of the range of results
	 * @param  end the upper bound of the range of results (not inclusive)
	 * @return the range of matching activities
	 */
	@Override
	public List<SocialActivity> getActivities(
		long companyId, String className, int start, int end) {

		DSLQuery dslQuery = DSLQueryFactoryUtil.select(
			SocialActivityTable.INSTANCE
		).from(
			SocialActivityTable.INSTANCE
		).where(
			SocialActivityTable.INSTANCE.companyId.eq(
				companyId
			).and(
				SocialActivityTable.INSTANCE.classNameId.eq(
					_classNameLocalService.getClassNameId(className))
			)
		).limit(
			start, end
		);

		return socialActivityPersistence.dslQuery(dslQuery);
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
		int end) {

		return getActivities(
			mirrorActivityId, _classNameLocalService.getClassNameId(className),
			classPK, start, end);
	}

	/**
	 * @param      classNameId the target asset's class name ID
	 * @return     the number of matching activities
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             #getActivitiesCount(long, String)}
	 */
	@Deprecated
	@Override
	public int getActivitiesCount(long classNameId) {
		DynamicQuery dynamicQuery = dynamicQuery();

		Property classNameIdProperty = PropertyFactoryUtil.forName(
			"classNameId");

		dynamicQuery.add(classNameIdProperty.eq(classNameId));

		Long count = dynamicQueryCount(dynamicQuery);

		return count.intValue();
	}

	@Override
	public int getActivitiesCount(
		long userId, long groupId, Date createDate, String className,
		long classPK, int type, long receiverUserId) {

		return socialActivityPersistence.countByG_U_CD_C_C_T_R(
			groupId, userId, createDate.getTime(),
			_classNameLocalService.getClassNameId(className), classPK, type,
			receiverUserId);
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

		return socialActivityPersistence.countByM_C_C(
			mirrorActivityId, classNameId, classPK);
	}

	/**
	 * Returns the number of activities done on assets identified by company ID
	 * and class name.
	 *
	 * @param  companyId the primary key of the company
	 * @param  className the target asset's class name
	 * @return the number of matching activities
	 */
	@Override
	public int getActivitiesCount(long companyId, String className) {
		DSLQuery dslQuery = DSLQueryFactoryUtil.count(
		).from(
			SocialActivityTable.INSTANCE
		).where(
			SocialActivityTable.INSTANCE.companyId.eq(
				companyId
			).and(
				SocialActivityTable.INSTANCE.classNameId.eq(
					_classNameLocalService.getClassNameId(className))
			)
		);

		return socialActivityPersistence.dslQueryCount(dslQuery);
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
	 * Returns the activity identified by its primary key.
	 *
	 * @param  activityId the primary key of the activity
	 * @return Returns the activity
	 */
	@Override
	public SocialActivity getActivity(long activityId) throws PortalException {
		return socialActivityPersistence.findByPrimaryKey(activityId);
	}

	@Override
	public List<SocialActivity> getActivitySetActivities(
		long activitySetId, int start, int end) {

		return socialActivityPersistence.findByActivitySetId(
			activitySetId, start, end);
	}

	@Override
	public List<SocialActivity> getApprovedActivities(
		long classPK, double version) {

		String versionString = String.valueOf(version);

		if (Math.floor(version) == version) {
			versionString = String.valueOf((int)version);
		}

		return dslQuery(
			DSLQueryFactoryUtil.select(
				SocialActivityTable.INSTANCE
			).from(
				SocialActivityTable.INSTANCE
			).where(
				SocialActivityTable.INSTANCE.classPK.eq(
					classPK
				).and(
					SocialActivityTable.INSTANCE.type.notIn(
						new Integer[] {
							SocialActivityConstants.TYPE_ADD_ATTACHMENT,
							SocialActivityConstants.
								TYPE_MOVE_ATTACHMENT_TO_TRASH,
							SocialActivityConstants.
								TYPE_RESTORE_ATTACHMENT_FROM_TRASH
						}
					).or(
						SocialActivityTable.INSTANCE.extraData.notLike(
							"%version\":" + versionString + ",%")
					).withParentheses()
				)
			));
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
		long groupId, int start, int end) {

		return socialActivityFinder.findByGroupId(groupId, start, end);
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
		return socialActivityFinder.countByGroupId(groupId);
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
		long groupId, int start, int end) {

		return socialActivityFinder.findByGroupUsers(groupId, start, end);
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
		return socialActivityFinder.countByGroupUsers(groupId);
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

		return socialActivityPersistence.findByMirrorActivityId(
			mirrorActivityId);
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
		long organizationId, int start, int end) {

		return socialActivityFinder.findByOrganizationId(
			organizationId, start, end);
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
		return socialActivityFinder.countByOrganizationId(organizationId);
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
		long organizationId, int start, int end) {

		return socialActivityFinder.findByOrganizationUsers(
			organizationId, start, end);
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
		return socialActivityFinder.countByOrganizationUsers(organizationId);
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
		long userId, int start, int end) {

		return socialActivityFinder.findByRelation(userId, start, end);
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
		long userId, int type, int start, int end) {

		return socialActivityFinder.findByRelationType(
			userId, type, start, end);
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
		return socialActivityFinder.countByRelation(userId);
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
		return socialActivityFinder.countByRelationType(userId, type);
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
		long userId, int start, int end) {

		return socialActivityPersistence.findByUserId(userId, start, end);
	}

	/**
	 * Returns the number of activities done by the user.
	 *
	 * @param  userId the primary key of the user
	 * @return the number of matching activities
	 */
	@Override
	public int getUserActivitiesCount(long userId) {
		return socialActivityPersistence.countByUserId(userId);
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
		long userId, int start, int end) {

		return socialActivityFinder.findByUserGroups(userId, start, end);
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
		return socialActivityFinder.countByUserGroups(userId);
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
		long userId, int start, int end) {

		return socialActivityFinder.findByUserGroupsAndOrganizations(
			userId, start, end);
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
		return socialActivityFinder.countByUserGroupsAndOrganizations(userId);
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
		long userId, int start, int end) {

		return socialActivityFinder.findByUserOrganizations(userId, start, end);
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
		return socialActivityFinder.countByUserOrganizations(userId);
	}

	protected void deleteActivities(long classNameId, long classPK)
		throws PortalException {

		Map<Long, List<SocialActivity>> partitionSocialActivities =
			MassDeleteCacheThreadLocal.getMassDeleteCache(
				SocialActivityLocalServiceImpl.class.getName() +
					".deleteActivities#" + classNameId,
				() -> MapUtil.toPartitionMap(
					socialActivityPersistence.findByC_CN(
						CompanyThreadLocal.getCompanyId(), classNameId),
					SocialActivity::getClassPK));

		if (partitionSocialActivities == null) {
			_socialActivitySetLocalService.decrementActivityCount(
				classNameId, classPK);

			socialActivityPersistence.removeByC_C(classNameId, classPK);
		}
		else {
			List<SocialActivity> socialActivities =
				partitionSocialActivities.remove(classPK);

			if (socialActivities != null) {
				for (SocialActivity socialActivity : socialActivities) {
					_socialActivitySetLocalService.decrementActivityCount(
						classNameId, classPK);

					socialActivityPersistence.remove(socialActivity);
				}
			}
		}

		if (!DeletedAssetEntryThreadLocal.isDeletedAssetEntry(
				classNameId, classPK) &&
			!Objects.equals(
				User.class.getName(), PortalUtil.getClassName(classNameId))) {

			_socialActivityCounterLocalService.deleteActivityCounters(
				classNameId, classPK);
		}
	}

	protected boolean isLogActivity(SocialActivity activity) {
		if (activity.getType() == SocialActivityConstants.TYPE_DELETE) {
			if (activity.getParentClassPK() == 0) {
				return true;
			}

			return false;
		}

		SocialActivityDefinition activityDefinition =
			_socialActivitySettingLocalService.getActivityDefinition(
				activity.getGroupId(), activity.getClassName(),
				activity.getType());

		if (activityDefinition != null) {
			return activityDefinition.isLogActivity();
		}

		if (activity.getType() < SocialActivityConstants.TYPE_VIEW) {
			return true;
		}

		return false;
	}

	@BeanReference(type = AssetEntryLocalService.class)
	private AssetEntryLocalService _assetEntryLocalService;

	@BeanReference(type = ClassNameLocalService.class)
	private ClassNameLocalService _classNameLocalService;

	@BeanReference(type = GroupLocalService.class)
	private GroupLocalService _groupLocalService;

	@BeanReference(type = LayoutLocalService.class)
	private LayoutLocalService _layoutLocalService;

	@BeanReference(type = SocialActivityCounterLocalService.class)
	private SocialActivityCounterLocalService
		_socialActivityCounterLocalService;

	@BeanReference(type = SocialActivityCounterPersistence.class)
	private SocialActivityCounterPersistence _socialActivityCounterPersistence;

	@BeanReference(type = SocialActivityInterpreterLocalService.class)
	private SocialActivityInterpreterLocalService
		_socialActivityInterpreterLocalService;

	@BeanReference(type = SocialActivityLimitPersistence.class)
	private SocialActivityLimitPersistence _socialActivityLimitPersistence;

	@BeanReference(type = SocialActivitySetLocalService.class)
	private SocialActivitySetLocalService _socialActivitySetLocalService;

	@BeanReference(type = SocialActivitySetPersistence.class)
	private SocialActivitySetPersistence _socialActivitySetPersistence;

	@BeanReference(type = SocialActivitySettingLocalService.class)
	private SocialActivitySettingLocalService
		_socialActivitySettingLocalService;

	@BeanReference(type = SocialActivitySettingPersistence.class)
	private SocialActivitySettingPersistence _socialActivitySettingPersistence;

	@BeanReference(type = UserPersistence.class)
	private UserPersistence _userPersistence;

}