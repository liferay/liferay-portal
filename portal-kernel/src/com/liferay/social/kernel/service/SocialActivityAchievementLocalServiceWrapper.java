/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.social.kernel.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.social.kernel.model.SocialActivityAchievement;

/**
 * Provides a wrapper for {@link SocialActivityAchievementLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see SocialActivityAchievementLocalService
 * @generated
 */
public class SocialActivityAchievementLocalServiceWrapper
	implements ServiceWrapper<SocialActivityAchievementLocalService>,
			   SocialActivityAchievementLocalService {

	public SocialActivityAchievementLocalServiceWrapper() {
		this(null);
	}

	public SocialActivityAchievementLocalServiceWrapper(
		SocialActivityAchievementLocalService
			socialActivityAchievementLocalService) {

		_socialActivityAchievementLocalService =
			socialActivityAchievementLocalService;
	}

	@Override
	public void addActivityAchievement(
			long userId, long groupId,
			com.liferay.social.kernel.model.SocialAchievement achievement)
		throws com.liferay.portal.kernel.exception.PortalException {

		_socialActivityAchievementLocalService.addActivityAchievement(
			userId, groupId, achievement);
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
	@Override
	public SocialActivityAchievement addSocialActivityAchievement(
		SocialActivityAchievement socialActivityAchievement) {

		return _socialActivityAchievementLocalService.
			addSocialActivityAchievement(socialActivityAchievement);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _socialActivityAchievementLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Creates a new social activity achievement with the primary key. Does not add the social activity achievement to the database.
	 *
	 * @param activityAchievementId the primary key for the new social activity achievement
	 * @return the new social activity achievement
	 */
	@Override
	public SocialActivityAchievement createSocialActivityAchievement(
		long activityAchievementId) {

		return _socialActivityAchievementLocalService.
			createSocialActivityAchievement(activityAchievementId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _socialActivityAchievementLocalService.deletePersistedModel(
			persistedModel);
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
	@Override
	public SocialActivityAchievement deleteSocialActivityAchievement(
			long activityAchievementId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _socialActivityAchievementLocalService.
			deleteSocialActivityAchievement(activityAchievementId);
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
	@Override
	public SocialActivityAchievement deleteSocialActivityAchievement(
		SocialActivityAchievement socialActivityAchievement) {

		return _socialActivityAchievementLocalService.
			deleteSocialActivityAchievement(socialActivityAchievement);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _socialActivityAchievementLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _socialActivityAchievementLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _socialActivityAchievementLocalService.dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _socialActivityAchievementLocalService.dynamicQuery(
			dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _socialActivityAchievementLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _socialActivityAchievementLocalService.dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _socialActivityAchievementLocalService.dynamicQueryCount(
			dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return _socialActivityAchievementLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public SocialActivityAchievement fetchSocialActivityAchievement(
		long activityAchievementId) {

		return _socialActivityAchievementLocalService.
			fetchSocialActivityAchievement(activityAchievementId);
	}

	@Override
	public SocialActivityAchievement fetchUserAchievement(
		long userId, long groupId, String name) {

		return _socialActivityAchievementLocalService.fetchUserAchievement(
			userId, groupId, name);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _socialActivityAchievementLocalService.
			getActionableDynamicQuery();
	}

	@Override
	public java.util.List<SocialActivityAchievement> getGroupAchievements(
		long groupId, String name) {

		return _socialActivityAchievementLocalService.getGroupAchievements(
			groupId, name);
	}

	@Override
	public int getGroupAchievementsCount(long groupId, String name) {
		return _socialActivityAchievementLocalService.getGroupAchievementsCount(
			groupId, name);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _socialActivityAchievementLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _socialActivityAchievementLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _socialActivityAchievementLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Returns the social activity achievement with the primary key.
	 *
	 * @param activityAchievementId the primary key of the social activity achievement
	 * @return the social activity achievement
	 * @throws PortalException if a social activity achievement with the primary key could not be found
	 */
	@Override
	public SocialActivityAchievement getSocialActivityAchievement(
			long activityAchievementId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _socialActivityAchievementLocalService.
			getSocialActivityAchievement(activityAchievementId);
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
	@Override
	public java.util.List<SocialActivityAchievement>
		getSocialActivityAchievements(int start, int end) {

		return _socialActivityAchievementLocalService.
			getSocialActivityAchievements(start, end);
	}

	/**
	 * Returns the number of social activity achievements.
	 *
	 * @return the number of social activity achievements
	 */
	@Override
	public int getSocialActivityAchievementsCount() {
		return _socialActivityAchievementLocalService.
			getSocialActivityAchievementsCount();
	}

	@Override
	public java.util.List<SocialActivityAchievement> getUserAchievements(
		long userId, long groupId) {

		return _socialActivityAchievementLocalService.getUserAchievements(
			userId, groupId);
	}

	@Override
	public int getUserAchievementsCount(long userId, long groupId) {
		return _socialActivityAchievementLocalService.getUserAchievementsCount(
			userId, groupId);
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
	@Override
	public SocialActivityAchievement updateSocialActivityAchievement(
		SocialActivityAchievement socialActivityAchievement) {

		return _socialActivityAchievementLocalService.
			updateSocialActivityAchievement(socialActivityAchievement);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _socialActivityAchievementLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<SocialActivityAchievement> getCTPersistence() {
		return _socialActivityAchievementLocalService.getCTPersistence();
	}

	@Override
	public Class<SocialActivityAchievement> getModelClass() {
		return _socialActivityAchievementLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<SocialActivityAchievement>, R, E>
				updateUnsafeFunction)
		throws E {

		return _socialActivityAchievementLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public SocialActivityAchievementLocalService getWrappedService() {
		return _socialActivityAchievementLocalService;
	}

	@Override
	public void setWrappedService(
		SocialActivityAchievementLocalService
			socialActivityAchievementLocalService) {

		_socialActivityAchievementLocalService =
			socialActivityAchievementLocalService;
	}

	private SocialActivityAchievementLocalService
		_socialActivityAchievementLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1286381843