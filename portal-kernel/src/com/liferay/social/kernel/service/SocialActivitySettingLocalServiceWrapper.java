/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.social.kernel.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.social.kernel.model.SocialActivitySetting;

/**
 * Provides a wrapper for {@link SocialActivitySettingLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see SocialActivitySettingLocalService
 * @generated
 */
public class SocialActivitySettingLocalServiceWrapper
	implements ServiceWrapper<SocialActivitySettingLocalService>,
			   SocialActivitySettingLocalService {

	public SocialActivitySettingLocalServiceWrapper() {
		this(null);
	}

	public SocialActivitySettingLocalServiceWrapper(
		SocialActivitySettingLocalService socialActivitySettingLocalService) {

		_socialActivitySettingLocalService = socialActivitySettingLocalService;
	}

	/**
	 * Adds the social activity setting to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SocialActivitySettingLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param socialActivitySetting the social activity setting
	 * @return the social activity setting that was added
	 */
	@Override
	public SocialActivitySetting addSocialActivitySetting(
		SocialActivitySetting socialActivitySetting) {

		return _socialActivitySettingLocalService.addSocialActivitySetting(
			socialActivitySetting);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _socialActivitySettingLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Creates a new social activity setting with the primary key. Does not add the social activity setting to the database.
	 *
	 * @param activitySettingId the primary key for the new social activity setting
	 * @return the new social activity setting
	 */
	@Override
	public SocialActivitySetting createSocialActivitySetting(
		long activitySettingId) {

		return _socialActivitySettingLocalService.createSocialActivitySetting(
			activitySettingId);
	}

	@Override
	public void deleteActivitySetting(
		long groupId, String className, long classPK) {

		_socialActivitySettingLocalService.deleteActivitySetting(
			groupId, className, classPK);
	}

	@Override
	public void deleteActivitySettings(long groupId) {
		_socialActivitySettingLocalService.deleteActivitySettings(groupId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _socialActivitySettingLocalService.deletePersistedModel(
			persistedModel);
	}

	/**
	 * Deletes the social activity setting with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SocialActivitySettingLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param activitySettingId the primary key of the social activity setting
	 * @return the social activity setting that was removed
	 * @throws PortalException if a social activity setting with the primary key could not be found
	 */
	@Override
	public SocialActivitySetting deleteSocialActivitySetting(
			long activitySettingId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _socialActivitySettingLocalService.deleteSocialActivitySetting(
			activitySettingId);
	}

	/**
	 * Deletes the social activity setting from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SocialActivitySettingLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param socialActivitySetting the social activity setting
	 * @return the social activity setting that was removed
	 */
	@Override
	public SocialActivitySetting deleteSocialActivitySetting(
		SocialActivitySetting socialActivitySetting) {

		return _socialActivitySettingLocalService.deleteSocialActivitySetting(
			socialActivitySetting);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _socialActivitySettingLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _socialActivitySettingLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _socialActivitySettingLocalService.dynamicQuery();
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

		return _socialActivitySettingLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivitySettingModelImpl</code>.
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

		return _socialActivitySettingLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivitySettingModelImpl</code>.
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

		return _socialActivitySettingLocalService.dynamicQuery(
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

		return _socialActivitySettingLocalService.dynamicQueryCount(
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

		return _socialActivitySettingLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public SocialActivitySetting fetchSocialActivitySetting(
		long activitySettingId) {

		return _socialActivitySettingLocalService.fetchSocialActivitySetting(
			activitySettingId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _socialActivitySettingLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.social.kernel.model.SocialActivityDefinition
		getActivityDefinition(
			long groupId, String className, int activityType) {

		return _socialActivitySettingLocalService.getActivityDefinition(
			groupId, className, activityType);
	}

	@Override
	public java.util.List
		<com.liferay.social.kernel.model.SocialActivityDefinition>
			getActivityDefinitions(long groupId, String className) {

		return _socialActivitySettingLocalService.getActivityDefinitions(
			groupId, className);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _socialActivitySettingLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _socialActivitySettingLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _socialActivitySettingLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Returns the social activity setting with the primary key.
	 *
	 * @param activitySettingId the primary key of the social activity setting
	 * @return the social activity setting
	 * @throws PortalException if a social activity setting with the primary key could not be found
	 */
	@Override
	public SocialActivitySetting getSocialActivitySetting(
			long activitySettingId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _socialActivitySettingLocalService.getSocialActivitySetting(
			activitySettingId);
	}

	/**
	 * Returns a range of all the social activity settings.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivitySettingModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of social activity settings
	 * @param end the upper bound of the range of social activity settings (not inclusive)
	 * @return the range of social activity settings
	 */
	@Override
	public java.util.List<SocialActivitySetting> getSocialActivitySettings(
		int start, int end) {

		return _socialActivitySettingLocalService.getSocialActivitySettings(
			start, end);
	}

	/**
	 * Returns the number of social activity settings.
	 *
	 * @return the number of social activity settings
	 */
	@Override
	public int getSocialActivitySettingsCount() {
		return _socialActivitySettingLocalService.
			getSocialActivitySettingsCount();
	}

	@Override
	public boolean isEnabled(long groupId, long classNameId) {
		return _socialActivitySettingLocalService.isEnabled(
			groupId, classNameId);
	}

	@Override
	public boolean isEnabled(long groupId, long classNameId, long classPK) {
		return _socialActivitySettingLocalService.isEnabled(
			groupId, classNameId, classPK);
	}

	@Override
	public void updateActivitySetting(
			long groupId, String className, boolean enabled)
		throws com.liferay.portal.kernel.exception.PortalException {

		_socialActivitySettingLocalService.updateActivitySetting(
			groupId, className, enabled);
	}

	@Override
	public void updateActivitySetting(
			long groupId, String className, int activityType,
			com.liferay.social.kernel.model.SocialActivityCounterDefinition
				activityCounterDefinition)
		throws com.liferay.portal.kernel.exception.PortalException {

		_socialActivitySettingLocalService.updateActivitySetting(
			groupId, className, activityType, activityCounterDefinition);
	}

	@Override
	public void updateActivitySetting(
			long groupId, String className, long classPK, boolean enabled)
		throws com.liferay.portal.kernel.exception.PortalException {

		_socialActivitySettingLocalService.updateActivitySetting(
			groupId, className, classPK, enabled);
	}

	@Override
	public void updateActivitySettings(
			long groupId, String className, int activityType,
			java.util.List
				<com.liferay.social.kernel.model.
					SocialActivityCounterDefinition> activityCounterDefinitions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_socialActivitySettingLocalService.updateActivitySettings(
			groupId, className, activityType, activityCounterDefinitions);
	}

	/**
	 * Updates the social activity setting in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SocialActivitySettingLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param socialActivitySetting the social activity setting
	 * @return the social activity setting that was updated
	 */
	@Override
	public SocialActivitySetting updateSocialActivitySetting(
		SocialActivitySetting socialActivitySetting) {

		return _socialActivitySettingLocalService.updateSocialActivitySetting(
			socialActivitySetting);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _socialActivitySettingLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<SocialActivitySetting> getCTPersistence() {
		return _socialActivitySettingLocalService.getCTPersistence();
	}

	@Override
	public Class<SocialActivitySetting> getModelClass() {
		return _socialActivitySettingLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<SocialActivitySetting>, R, E>
				updateUnsafeFunction)
		throws E {

		return _socialActivitySettingLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public SocialActivitySettingLocalService getWrappedService() {
		return _socialActivitySettingLocalService;
	}

	@Override
	public void setWrappedService(
		SocialActivitySettingLocalService socialActivitySettingLocalService) {

		_socialActivitySettingLocalService = socialActivitySettingLocalService;
	}

	private SocialActivitySettingLocalService
		_socialActivitySettingLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1064811105