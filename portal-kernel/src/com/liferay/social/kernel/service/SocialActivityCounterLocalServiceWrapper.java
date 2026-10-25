/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.social.kernel.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.social.kernel.model.SocialActivityCounter;

/**
 * Provides a wrapper for {@link SocialActivityCounterLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see SocialActivityCounterLocalService
 * @generated
 */
public class SocialActivityCounterLocalServiceWrapper
	implements ServiceWrapper<SocialActivityCounterLocalService>,
			   SocialActivityCounterLocalService {

	public SocialActivityCounterLocalServiceWrapper() {
		this(null);
	}

	public SocialActivityCounterLocalServiceWrapper(
		SocialActivityCounterLocalService socialActivityCounterLocalService) {

		_socialActivityCounterLocalService = socialActivityCounterLocalService;
	}

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
	 * @param groupId the primary key of the group
	 * @param classNameId the primary key of the entity's class this counter
	 belongs to
	 * @param classPK the primary key of the entity this counter belongs to
	 * @param name the counter name
	 * @param ownerType the counter's owner type. Acceptable values are
	 <code>TYPE_ACTOR</code>, <code>TYPE_ASSET</code> and
	 <code>TYPE_CREATOR</code> defined in {@link
	 SocialActivityCounterConstants}.
	 * @param totalValue the counter's total value (optionally <code>0</code>)
	 * @param previousActivityCounterId the primary key of the activity counter
	 for the previous time period (optionally <code>0</code>, if this
	 is the first)
	 * @param periodLength the period length in days,
	 <code>PERIOD_LENGTH_INFINITE</code> for never ending counters or
	 <code>PERIOD_LENGTH_SYSTEM</code> for the period length defined
	 in <code>portal-ext.properties</code>. For more information see
	 {@link SocialActivityCounterConstants}.
	 * @return the added activity counter
	 */
	@Override
	public SocialActivityCounter addActivityCounter(
			long groupId, long classNameId, long classPK, String name,
			int ownerType, int totalValue, long previousActivityCounterId,
			int periodLength)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _socialActivityCounterLocalService.addActivityCounter(
			groupId, classNameId, classPK, name, ownerType, totalValue,
			previousActivityCounterId, periodLength);
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
	public void addActivityCounters(
			com.liferay.social.kernel.model.SocialActivity activity)
		throws com.liferay.portal.kernel.exception.PortalException {

		_socialActivityCounterLocalService.addActivityCounters(activity);
	}

	/**
	 * Adds the social activity counter to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SocialActivityCounterLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param socialActivityCounter the social activity counter
	 * @return the social activity counter that was added
	 */
	@Override
	public SocialActivityCounter addSocialActivityCounter(
		SocialActivityCounter socialActivityCounter) {

		return _socialActivityCounterLocalService.addSocialActivityCounter(
			socialActivityCounter);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _socialActivityCounterLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Creates a new social activity counter with the primary key. Does not add the social activity counter to the database.
	 *
	 * @param activityCounterId the primary key for the new social activity counter
	 * @return the new social activity counter
	 */
	@Override
	public SocialActivityCounter createSocialActivityCounter(
		long activityCounterId) {

		return _socialActivityCounterLocalService.createSocialActivityCounter(
			activityCounterId);
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
	public void deleteActivityCounters(
			com.liferay.asset.kernel.model.AssetEntry assetEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		_socialActivityCounterLocalService.deleteActivityCounters(assetEntry);
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
		throws com.liferay.portal.kernel.exception.PortalException {

		_socialActivityCounterLocalService.deleteActivityCounters(
			classNameId, classPK);
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
		throws com.liferay.portal.kernel.exception.PortalException {

		_socialActivityCounterLocalService.deleteActivityCounters(
			className, classPK);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _socialActivityCounterLocalService.deletePersistedModel(
			persistedModel);
	}

	/**
	 * Deletes the social activity counter with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SocialActivityCounterLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param activityCounterId the primary key of the social activity counter
	 * @return the social activity counter that was removed
	 * @throws PortalException if a social activity counter with the primary key could not be found
	 */
	@Override
	public SocialActivityCounter deleteSocialActivityCounter(
			long activityCounterId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _socialActivityCounterLocalService.deleteSocialActivityCounter(
			activityCounterId);
	}

	/**
	 * Deletes the social activity counter from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SocialActivityCounterLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param socialActivityCounter the social activity counter
	 * @return the social activity counter that was removed
	 */
	@Override
	public SocialActivityCounter deleteSocialActivityCounter(
		SocialActivityCounter socialActivityCounter) {

		return _socialActivityCounterLocalService.deleteSocialActivityCounter(
			socialActivityCounter);
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
		throws com.liferay.portal.kernel.exception.PortalException {

		_socialActivityCounterLocalService.disableActivityCounters(
			classNameId, classPK);
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
		throws com.liferay.portal.kernel.exception.PortalException {

		_socialActivityCounterLocalService.disableActivityCounters(
			className, classPK);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _socialActivityCounterLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _socialActivityCounterLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _socialActivityCounterLocalService.dynamicQuery();
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

		return _socialActivityCounterLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityCounterModelImpl</code>.
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

		return _socialActivityCounterLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityCounterModelImpl</code>.
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

		return _socialActivityCounterLocalService.dynamicQuery(
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

		return _socialActivityCounterLocalService.dynamicQueryCount(
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

		return _socialActivityCounterLocalService.dynamicQueryCount(
			dynamicQuery, projection);
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
		throws com.liferay.portal.kernel.exception.PortalException {

		_socialActivityCounterLocalService.enableActivityCounters(
			classNameId, classPK);
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
		throws com.liferay.portal.kernel.exception.PortalException {

		_socialActivityCounterLocalService.enableActivityCounters(
			className, classPK);
	}

	/**
	 * Returns the activity counter with the given name, owner, and end period
	 * that belong to the given entity.
	 *
	 * @param groupId the primary key of the group
	 * @param classNameId the primary key of the entity's class
	 * @param classPK the primary key of the entity
	 * @param name the counter name
	 * @param ownerType the owner type
	 * @param endPeriod the end period, <code>-1</code> for the latest one
	 * @return the matching activity counter
	 */
	@Override
	public SocialActivityCounter fetchActivityCounterByEndPeriod(
		long groupId, long classNameId, long classPK, String name,
		int ownerType, int endPeriod) {

		return _socialActivityCounterLocalService.
			fetchActivityCounterByEndPeriod(
				groupId, classNameId, classPK, name, ownerType, endPeriod);
	}

	/**
	 * Returns the activity counter with the given name, owner, and start period
	 * that belong to the given entity.
	 *
	 * @param groupId the primary key of the group
	 * @param classNameId the primary key of the entity's class
	 * @param classPK the primary key of the entity
	 * @param name the counter name
	 * @param ownerType the owner type
	 * @param startPeriod the start period
	 * @return the matching activity counter
	 */
	@Override
	public SocialActivityCounter fetchActivityCounterByStartPeriod(
		long groupId, long classNameId, long classPK, String name,
		int ownerType, int startPeriod) {

		return _socialActivityCounterLocalService.
			fetchActivityCounterByStartPeriod(
				groupId, classNameId, classPK, name, ownerType, startPeriod);
	}

	/**
	 * Returns the latest activity counter with the given name and owner that
	 * belong to the given entity.
	 *
	 * @param groupId the primary key of the group
	 * @param classNameId the primary key of the entity's class
	 * @param classPK the primary key of the entity
	 * @param name the counter name
	 * @param ownerType the owner type
	 * @return the matching activity counter
	 */
	@Override
	public SocialActivityCounter fetchLatestActivityCounter(
		long groupId, long classNameId, long classPK, String name,
		int ownerType) {

		return _socialActivityCounterLocalService.fetchLatestActivityCounter(
			groupId, classNameId, classPK, name, ownerType);
	}

	@Override
	public SocialActivityCounter fetchSocialActivityCounter(
		long activityCounterId) {

		return _socialActivityCounterLocalService.fetchSocialActivityCounter(
			activityCounterId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _socialActivityCounterLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _socialActivityCounterLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _socialActivityCounterLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _socialActivityCounterLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Returns the social activity counter with the primary key.
	 *
	 * @param activityCounterId the primary key of the social activity counter
	 * @return the social activity counter
	 * @throws PortalException if a social activity counter with the primary key could not be found
	 */
	@Override
	public SocialActivityCounter getSocialActivityCounter(
			long activityCounterId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _socialActivityCounterLocalService.getSocialActivityCounter(
			activityCounterId);
	}

	/**
	 * Returns a range of all the social activity counters.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityCounterModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of social activity counters
	 * @param end the upper bound of the range of social activity counters (not inclusive)
	 * @return the range of social activity counters
	 */
	@Override
	public java.util.List<SocialActivityCounter> getSocialActivityCounters(
		int start, int end) {

		return _socialActivityCounterLocalService.getSocialActivityCounters(
			start, end);
	}

	/**
	 * Returns the number of social activity counters.
	 *
	 * @return the number of social activity counters
	 */
	@Override
	public int getSocialActivityCountersCount() {
		return _socialActivityCounterLocalService.
			getSocialActivityCountersCount();
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
		throws com.liferay.portal.kernel.exception.PortalException {

		_socialActivityCounterLocalService.incrementUserAchievementCounter(
			userId, groupId);
	}

	/**
	 * Updates the social activity counter in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SocialActivityCounterLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param socialActivityCounter the social activity counter
	 * @return the social activity counter that was updated
	 */
	@Override
	public SocialActivityCounter updateSocialActivityCounter(
		SocialActivityCounter socialActivityCounter) {

		return _socialActivityCounterLocalService.updateSocialActivityCounter(
			socialActivityCounter);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _socialActivityCounterLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<SocialActivityCounter> getCTPersistence() {
		return _socialActivityCounterLocalService.getCTPersistence();
	}

	@Override
	public Class<SocialActivityCounter> getModelClass() {
		return _socialActivityCounterLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<SocialActivityCounter>, R, E>
				updateUnsafeFunction)
		throws E {

		return _socialActivityCounterLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public SocialActivityCounterLocalService getWrappedService() {
		return _socialActivityCounterLocalService;
	}

	@Override
	public void setWrappedService(
		SocialActivityCounterLocalService socialActivityCounterLocalService) {

		_socialActivityCounterLocalService = socialActivityCounterLocalService;
	}

	private SocialActivityCounterLocalService
		_socialActivityCounterLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:176678587