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
import com.liferay.social.kernel.model.SocialActivityAchievement;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for SocialActivityAchievement. This utility wraps
 * <code>com.liferay.portlet.social.service.impl.SocialActivityAchievementLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see SocialActivityAchievementLocalService
 * @generated
 */
public class SocialActivityAchievementLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portlet.social.service.impl.SocialActivityAchievementLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static void addActivityAchievement(
			long userId, long groupId,
			com.liferay.social.kernel.model.SocialAchievement achievement)
		throws PortalException {

		getService().addActivityAchievement(userId, groupId, achievement);
	}

	/**
	 * Adds the social activity achievement to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SocialActivityAchievementLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param socialActivityAchievement the social activity achievement
	 * @return the social activity achievement that was added
	 */
	public static SocialActivityAchievement addSocialActivityAchievement(
		SocialActivityAchievement socialActivityAchievement) {

		return getService().addSocialActivityAchievement(
			socialActivityAchievement);
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
	 * Creates a new social activity achievement with the primary key. Does not add the social activity achievement to the database.
	 *
	 * @param activityAchievementId the primary key for the new social activity achievement
	 * @return the new social activity achievement
	 */
	public static SocialActivityAchievement createSocialActivityAchievement(
		long activityAchievementId) {

		return getService().createSocialActivityAchievement(
			activityAchievementId);
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
	 * Deletes the social activity achievement with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SocialActivityAchievementLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param activityAchievementId the primary key of the social activity achievement
	 * @return the social activity achievement that was removed
	 * @throws PortalException if a social activity achievement with the primary key could not be found
	 */
	public static SocialActivityAchievement deleteSocialActivityAchievement(
			long activityAchievementId)
		throws PortalException {

		return getService().deleteSocialActivityAchievement(
			activityAchievementId);
	}

	/**
	 * Deletes the social activity achievement from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SocialActivityAchievementLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param socialActivityAchievement the social activity achievement
	 * @return the social activity achievement that was removed
	 */
	public static SocialActivityAchievement deleteSocialActivityAchievement(
		SocialActivityAchievement socialActivityAchievement) {

		return getService().deleteSocialActivityAchievement(
			socialActivityAchievement);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityAchievementModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityAchievementModelImpl</code>.
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

	public static SocialActivityAchievement fetchSocialActivityAchievement(
		long activityAchievementId) {

		return getService().fetchSocialActivityAchievement(
			activityAchievementId);
	}

	public static SocialActivityAchievement fetchUserAchievement(
		long userId, long groupId, String name) {

		return getService().fetchUserAchievement(userId, groupId, name);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static List<SocialActivityAchievement> getGroupAchievements(
		long groupId, String name) {

		return getService().getGroupAchievements(groupId, name);
	}

	public static int getGroupAchievementsCount(long groupId, String name) {
		return getService().getGroupAchievementsCount(groupId, name);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
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
	 * Returns the social activity achievement with the primary key.
	 *
	 * @param activityAchievementId the primary key of the social activity achievement
	 * @return the social activity achievement
	 * @throws PortalException if a social activity achievement with the primary key could not be found
	 */
	public static SocialActivityAchievement getSocialActivityAchievement(
			long activityAchievementId)
		throws PortalException {

		return getService().getSocialActivityAchievement(activityAchievementId);
	}

	/**
	 * Returns a range of all the social activity achievements.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityAchievementModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of social activity achievements
	 * @param end the upper bound of the range of social activity achievements (not inclusive)
	 * @return the range of social activity achievements
	 */
	public static List<SocialActivityAchievement> getSocialActivityAchievements(
		int start, int end) {

		return getService().getSocialActivityAchievements(start, end);
	}

	/**
	 * Returns the number of social activity achievements.
	 *
	 * @return the number of social activity achievements
	 */
	public static int getSocialActivityAchievementsCount() {
		return getService().getSocialActivityAchievementsCount();
	}

	public static List<SocialActivityAchievement> getUserAchievements(
		long userId, long groupId) {

		return getService().getUserAchievements(userId, groupId);
	}

	public static int getUserAchievementsCount(long userId, long groupId) {
		return getService().getUserAchievementsCount(userId, groupId);
	}

	/**
	 * Updates the social activity achievement in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SocialActivityAchievementLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param socialActivityAchievement the social activity achievement
	 * @return the social activity achievement that was updated
	 */
	public static SocialActivityAchievement updateSocialActivityAchievement(
		SocialActivityAchievement socialActivityAchievement) {

		return getService().updateSocialActivityAchievement(
			socialActivityAchievement);
	}

	public static SocialActivityAchievementLocalService getService() {
		return _service;
	}

	public static void setService(
		SocialActivityAchievementLocalService service) {

		_service = service;
	}

	private static volatile SocialActivityAchievementLocalService _service;

}
// LIFERAY-SERVICE-BUILDER-HASH:1616206332