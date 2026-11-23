/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.social.kernel.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.social.kernel.model.SocialActivity;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for SocialActivity. This utility wraps
 * <code>com.liferay.portlet.social.service.impl.SocialActivityLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see SocialActivityLocalService
 * @generated
 */
public class SocialActivityLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portlet.social.service.impl.SocialActivityLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

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
	public static void addActivity(
			long userId, long groupId, java.util.Date createDate,
			String className, long classPK, int type, String extraData,
			long receiverUserId)
		throws PortalException {

		getService().addActivity(
			userId, groupId, createDate, className, classPK, type, extraData,
			receiverUserId);
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
	public static void addActivity(
			long userId, long groupId, String className, long classPK, int type,
			String extraData, long receiverUserId)
		throws PortalException {

		getService().addActivity(
			userId, groupId, className, classPK, type, extraData,
			receiverUserId);
	}

	public static void addActivity(
			SocialActivity activity, SocialActivity mirrorActivity)
		throws PortalException {

		getService().addActivity(activity, mirrorActivity);
	}

	/**
	 * Adds the social activity to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SocialActivityLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param socialActivity the social activity
	 * @return the social activity that was added
	 */
	public static SocialActivity addSocialActivity(
		SocialActivity socialActivity) {

		return getService().addSocialActivity(socialActivity);
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
	public static void addUniqueActivity(
			long userId, long groupId, java.util.Date createDate,
			String className, long classPK, int type, String extraData,
			long receiverUserId)
		throws PortalException {

		getService().addUniqueActivity(
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
	public static void addUniqueActivity(
			long userId, long groupId, String className, long classPK, int type,
			String extraData, long receiverUserId)
		throws PortalException {

		getService().addUniqueActivity(
			userId, groupId, className, classPK, type, extraData,
			receiverUserId);
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
	 * Creates a new social activity with the primary key. Does not add the social activity to the database.
	 *
	 * @param activityId the primary key for the new social activity
	 * @return the new social activity
	 */
	public static SocialActivity createSocialActivity(long activityId) {
		return getService().createSocialActivity(activityId);
	}

	/**
	 * Removes stored activities for the asset.
	 *
	 * @param assetEntry the asset from which to remove stored activities
	 */
	public static void deleteActivities(
			com.liferay.asset.kernel.model.AssetEntry assetEntry)
		throws PortalException {

		getService().deleteActivities(assetEntry);
	}

	public static void deleteActivities(long groupId) {
		getService().deleteActivities(groupId);
	}

	/**
	 * Removes stored activities for the asset identified by the class name and
	 * class primary key.
	 *
	 * @param className the target asset's class name
	 * @param classPK the primary key of the target asset
	 */
	public static void deleteActivities(String className, long classPK)
		throws PortalException {

		getService().deleteActivities(className, classPK);
	}

	/**
	 * Removes the stored activity from the database.
	 *
	 * @param activityId the primary key of the stored activity
	 */
	public static void deleteActivity(long activityId) throws PortalException {
		getService().deleteActivity(activityId);
	}

	/**
	 * Removes the stored activity and its mirror activity from the database.
	 *
	 * @param activity the activity to be removed
	 */
	public static void deleteActivity(SocialActivity activity)
		throws PortalException {

		getService().deleteActivity(activity);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	/**
	 * Deletes the social activity with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SocialActivityLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param activityId the primary key of the social activity
	 * @return the social activity that was removed
	 * @throws PortalException if a social activity with the primary key could not be found
	 */
	public static SocialActivity deleteSocialActivity(long activityId)
		throws PortalException {

		return getService().deleteSocialActivity(activityId);
	}

	/**
	 * Deletes the social activity from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SocialActivityLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param socialActivity the social activity
	 * @return the social activity that was removed
	 */
	public static SocialActivity deleteSocialActivity(
		SocialActivity socialActivity) {

		return getService().deleteSocialActivity(socialActivity);
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
	public static void deleteUserActivities(long userId)
		throws PortalException {

		getService().deleteUserActivities(userId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
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

	public static SocialActivity fetchFirstActivity(
		String className, long classPK, int type) {

		return getService().fetchFirstActivity(className, classPK, type);
	}

	public static SocialActivity fetchSocialActivity(long activityId) {
		return getService().fetchSocialActivity(activityId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * @param classNameId the target asset's class name ID
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the range of matching activities
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 #getActivities(long, String, int, int)}  Returns a range of
	 all the activities done on assets identified by the class
	 name ID.  <p> Useful when paginating results. Returns a
	 maximum of <code>end - start</code> instances.
	 <code>start</code> and <code>end</code> are not primary keys,
	 they are indexes in the result set. Thus, <code>0</code>
	 refers to the first result in the set. Setting both
	 <code>start</code> and <code>end</code> to {@link
	 QueryUtil#ALL_POS} will return the full result set.</p>
	 */
	@Deprecated
	public static List<SocialActivity> getActivities(
		long classNameId, int start, int end) {

		return getService().getActivities(classNameId, start, end);
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
	 * @param mirrorActivityId the primary key of the mirror activity
	 * @param classNameId the target asset's class name ID
	 * @param classPK the primary key of the target asset
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the range of matching activities
	 */
	public static List<SocialActivity> getActivities(
		long mirrorActivityId, long classNameId, long classPK, int start,
		int end) {

		return getService().getActivities(
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
	 * @param companyId the primary key of the company
	 * @param className the target asset's class name
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the range of matching activities
	 */
	public static List<SocialActivity> getActivities(
		long companyId, String className, int start, int end) {

		return getService().getActivities(companyId, className, start, end);
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
	 * @param mirrorActivityId the primary key of the mirror activity
	 * @param className the target asset's class name
	 * @param classPK the primary key of the target asset
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the range of matching activities
	 */
	public static List<SocialActivity> getActivities(
		long mirrorActivityId, String className, long classPK, int start,
		int end) {

		return getService().getActivities(
			mirrorActivityId, className, classPK, start, end);
	}

	/**
	 * @param classNameId the target asset's class name ID
	 * @return the number of matching activities
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 #getActivitiesCount(long, String)}
	 */
	@Deprecated
	public static int getActivitiesCount(long classNameId) {
		return getService().getActivitiesCount(classNameId);
	}

	/**
	 * Returns the number of activities done on the asset identified by the
	 * class name ID and class primary key that are mirrors of the activity
	 * identified by the mirror activity ID.
	 *
	 * @param mirrorActivityId the primary key of the mirror activity
	 * @param classNameId the target asset's class name ID
	 * @param classPK the primary key of the target asset
	 * @return the number of matching activities
	 */
	public static int getActivitiesCount(
		long mirrorActivityId, long classNameId, long classPK) {

		return getService().getActivitiesCount(
			mirrorActivityId, classNameId, classPK);
	}

	/**
	 * Returns the number of activities done on assets identified by company ID
	 * and class name.
	 *
	 * @param companyId the primary key of the company
	 * @param className the target asset's class name
	 * @return the number of matching activities
	 */
	public static int getActivitiesCount(long companyId, String className) {
		return getService().getActivitiesCount(companyId, className);
	}

	/**
	 * Returns the number of activities done on the asset identified by the
	 * class name and class primary key that are mirrors of the activity
	 * identified by the mirror activity ID.
	 *
	 * @param mirrorActivityId the primary key of the mirror activity
	 * @param className the target asset's class name
	 * @param classPK the primary key of the target asset
	 * @return the number of matching activities
	 */
	public static int getActivitiesCount(
		long mirrorActivityId, String className, long classPK) {

		return getService().getActivitiesCount(
			mirrorActivityId, className, classPK);
	}

	/**
	 * Returns the activity identified by its primary key.
	 *
	 * @param activityId the primary key of the activity
	 * @return Returns the activity
	 */
	public static SocialActivity getActivity(long activityId)
		throws PortalException {

		return getService().getActivity(activityId);
	}

	public static List<SocialActivity> getActivitySetActivities(
		long activitySetId, int start, int end) {

		return getService().getActivitySetActivities(activitySetId, start, end);
	}

	public static List<SocialActivity> getApprovedActivities(
		long classPK, double version) {

		return getService().getApprovedActivities(classPK, version);
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
	 * @param groupId the primary key of the group
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the range of matching activities
	 */
	public static List<SocialActivity> getGroupActivities(
		long groupId, int start, int end) {

		return getService().getGroupActivities(groupId, start, end);
	}

	/**
	 * Returns the number of activities done in the group.
	 *
	 * <p>
	 * This method only counts activities without mirrors.
	 * </p>
	 *
	 * @param groupId the primary key of the group
	 * @return the number of matching activities
	 */
	public static int getGroupActivitiesCount(long groupId) {
		return getService().getGroupActivitiesCount(groupId);
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
	 * @param groupId the primary key of the group
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the range of matching activities
	 */
	public static List<SocialActivity> getGroupUsersActivities(
		long groupId, int start, int end) {

		return getService().getGroupUsersActivities(groupId, start, end);
	}

	/**
	 * Returns the number of activities done by users that are members of the
	 * group.
	 *
	 * <p>
	 * This method only counts activities without mirrors.
	 * </p>
	 *
	 * @param groupId the primary key of the group
	 * @return the number of matching activities
	 */
	public static int getGroupUsersActivitiesCount(long groupId) {
		return getService().getGroupUsersActivitiesCount(groupId);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
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
	 * @param organizationId the primary key of the organization
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the range of matching activities
	 */
	public static List<SocialActivity> getOrganizationActivities(
		long organizationId, int start, int end) {

		return getService().getOrganizationActivities(
			organizationId, start, end);
	}

	/**
	 * Returns the number of activities done in the organization. This method
	 * only counts activities without mirrors.
	 *
	 * @param organizationId the primary key of the organization
	 * @return the number of matching activities
	 */
	public static int getOrganizationActivitiesCount(long organizationId) {
		return getService().getOrganizationActivitiesCount(organizationId);
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
	 * @param organizationId the primary key of the organization
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the range of matching activities
	 */
	public static List<SocialActivity> getOrganizationUsersActivities(
		long organizationId, int start, int end) {

		return getService().getOrganizationUsersActivities(
			organizationId, start, end);
	}

	/**
	 * Returns the number of activities done by users of the organization. This
	 * method only counts activities without mirrors.
	 *
	 * @param organizationId the primary key of the organization
	 * @return the number of matching activities
	 */
	public static int getOrganizationUsersActivitiesCount(long organizationId) {
		return getService().getOrganizationUsersActivitiesCount(organizationId);
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
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
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
	 * @param userId the primary key of the user
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the range of matching activities
	 */
	public static List<SocialActivity> getRelationActivities(
		long userId, int start, int end) {

		return getService().getRelationActivities(userId, start, end);
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
	 * @param userId the primary key of the user
	 * @param type the relationship type
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the range of matching activities
	 */
	public static List<SocialActivity> getRelationActivities(
		long userId, int type, int start, int end) {

		return getService().getRelationActivities(userId, type, start, end);
	}

	/**
	 * Returns the number of activities done by users in a relationship with the
	 * user identified by userId.
	 *
	 * @param userId the primary key of the user
	 * @return the number of matching activities
	 */
	public static int getRelationActivitiesCount(long userId) {
		return getService().getRelationActivitiesCount(userId);
	}

	/**
	 * Returns the number of activities done by users in a relationship of type
	 * <code>type</code> with the user identified by <code>userId</code>. This
	 * method only counts activities without mirrors.
	 *
	 * @param userId the primary key of the user
	 * @param type the relationship type
	 * @return the number of matching activities
	 */
	public static int getRelationActivitiesCount(long userId, int type) {
		return getService().getRelationActivitiesCount(userId, type);
	}

	/**
	 * Returns a range of all the social activities.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @return the range of social activities
	 */
	public static List<SocialActivity> getSocialActivities(int start, int end) {
		return getService().getSocialActivities(start, end);
	}

	/**
	 * Returns the number of social activities.
	 *
	 * @return the number of social activities
	 */
	public static int getSocialActivitiesCount() {
		return getService().getSocialActivitiesCount();
	}

	/**
	 * Returns the social activity with the primary key.
	 *
	 * @param activityId the primary key of the social activity
	 * @return the social activity
	 * @throws PortalException if a social activity with the primary key could not be found
	 */
	public static SocialActivity getSocialActivity(long activityId)
		throws PortalException {

		return getService().getSocialActivity(activityId);
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
	 * @param userId the primary key of the user
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the range of matching activities
	 */
	public static List<SocialActivity> getUserActivities(
		long userId, int start, int end) {

		return getService().getUserActivities(userId, start, end);
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
	 * @param userId the primary key of the user
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the range of matching activities
	 */
	public static List<SocialActivity> getUserGroupsActivities(
		long userId, int start, int end) {

		return getService().getUserGroupsActivities(userId, start, end);
	}

	/**
	 * Returns the number of activities done in user's groups. This method only
	 * counts activities without mirrors.
	 *
	 * @param userId the primary key of the user
	 * @return the number of matching activities
	 */
	public static int getUserGroupsActivitiesCount(long userId) {
		return getService().getUserGroupsActivitiesCount(userId);
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
	 * @param userId the primary key of the user
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the range of matching activities
	 */
	public static List<SocialActivity> getUserGroupsAndOrganizationsActivities(
		long userId, int start, int end) {

		return getService().getUserGroupsAndOrganizationsActivities(
			userId, start, end);
	}

	/**
	 * Returns the number of activities done in user's groups and organizations.
	 * This method only counts activities without mirrors.
	 *
	 * @param userId the primary key of the user
	 * @return the number of matching activities
	 */
	public static int getUserGroupsAndOrganizationsActivitiesCount(
		long userId) {

		return getService().getUserGroupsAndOrganizationsActivitiesCount(
			userId);
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
	 * @param userId the primary key of the user
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the range of matching activities
	 */
	public static List<SocialActivity> getUserOrganizationsActivities(
		long userId, int start, int end) {

		return getService().getUserOrganizationsActivities(userId, start, end);
	}

	/**
	 * Returns the number of activities done in the user's organizations. This
	 * method only counts activities without mirrors.
	 *
	 * @param userId the primary key of the user
	 * @return the number of matching activities
	 */
	public static int getUserOrganizationsActivitiesCount(long userId) {
		return getService().getUserOrganizationsActivitiesCount(userId);
	}

	/**
	 * Updates the social activity in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SocialActivityLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param socialActivity the social activity
	 * @return the social activity that was updated
	 */
	public static SocialActivity updateSocialActivity(
		SocialActivity socialActivity) {

		return getService().updateSocialActivity(socialActivity);
	}

	public static SocialActivityLocalService getService() {
		return _service;
	}

	public static void setService(SocialActivityLocalService service) {
		_service = service;
	}

	private static volatile SocialActivityLocalService _service;

}
// LIFERAY-SERVICE-BUILDER-HASH:577722006