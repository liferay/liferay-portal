/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.subscription.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.subscription.model.Subscription;

/**
 * Provides a wrapper for {@link SubscriptionLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see SubscriptionLocalService
 * @generated
 */
public class SubscriptionLocalServiceWrapper
	implements ServiceWrapper<SubscriptionLocalService>,
			   SubscriptionLocalService {

	public SubscriptionLocalServiceWrapper() {
		this(null);
	}

	public SubscriptionLocalServiceWrapper(
		SubscriptionLocalService subscriptionLocalService) {

		_subscriptionLocalService = subscriptionLocalService;
	}

	/**
	 * Subscribes the user to the entity, notifying him the instant the entity
	 * is created, deleted, or modified.
	 *
	 * <p>
	 * If there is no asset entry with the class name and class PK a new asset
	 * entry is created.
	 * </p>
	 *
	 * <p>
	 * A social activity for the subscription is created using the asset entry
	 * associated with the class name and class PK, or the newly created asset
	 * entry.
	 * </p>
	 *
	 * @param userId the primary key of the user
	 * @param groupId the primary key of the entity's group
	 * @param className the entity's class name
	 * @param classPK the primary key of the entity's instance
	 * @return the subscription
	 */
	@Override
	public Subscription addSubscription(
			long userId, long groupId, String className, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _subscriptionLocalService.addSubscription(
			userId, groupId, className, classPK);
	}

	/**
	 * Subscribes the user to the entity, notifying him at the given frequency.
	 *
	 * <p>
	 * If there is no asset entry with the class name and class PK a new asset
	 * entry is created.
	 * </p>
	 *
	 * <p>
	 * A social activity for the subscription is created using the asset entry
	 * associated with the class name and class PK, or the newly created asset
	 * entry.
	 * </p>
	 *
	 * @param userId the primary key of the user
	 * @param groupId the primary key of the entity's group
	 * @param className the entity's class name
	 * @param classPK the primary key of the entity's instance
	 * @param frequency the frequency for notifications
	 * @return the subscription
	 */
	@Override
	public Subscription addSubscription(
			long userId, long groupId, String className, long classPK,
			String frequency)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _subscriptionLocalService.addSubscription(
			userId, groupId, className, classPK, frequency);
	}

	/**
	 * Adds the subscription to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SubscriptionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param subscription the subscription
	 * @return the subscription that was added
	 */
	@Override
	public Subscription addSubscription(Subscription subscription) {
		return _subscriptionLocalService.addSubscription(subscription);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _subscriptionLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Creates a new subscription with the primary key. Does not add the subscription to the database.
	 *
	 * @param subscriptionId the primary key for the new subscription
	 * @return the new subscription
	 */
	@Override
	public Subscription createSubscription(long subscriptionId) {
		return _subscriptionLocalService.createSubscription(subscriptionId);
	}

	@Override
	public void deleteGroupSubscriptions(long groupId) {
		_subscriptionLocalService.deleteGroupSubscriptions(groupId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _subscriptionLocalService.deletePersistedModel(persistedModel);
	}

	/**
	 * Deletes the subscription with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SubscriptionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param subscriptionId the primary key of the subscription
	 * @return the subscription that was removed
	 * @throws PortalException if a subscription with the primary key could not be found
	 */
	@Override
	public Subscription deleteSubscription(long subscriptionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _subscriptionLocalService.deleteSubscription(subscriptionId);
	}

	/**
	 * Deletes the user's subscription to the entity. A social activity with the
	 * unsubscribe action is created.
	 *
	 * @param userId the primary key of the user
	 * @param className the entity's class name
	 * @param classPK the primary key of the entity's instance
	 */
	@Override
	public void deleteSubscription(long userId, String className, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		_subscriptionLocalService.deleteSubscription(
			userId, className, classPK);
	}

	/**
	 * Deletes the subscription from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SubscriptionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param subscription the subscription
	 * @return the subscription that was removed
	 * @throws PortalException
	 */
	@Override
	public Subscription deleteSubscription(Subscription subscription)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _subscriptionLocalService.deleteSubscription(subscription);
	}

	/**
	 * Deletes all the subscriptions of the user.
	 *
	 * @param userId the primary key of the user
	 */
	@Override
	public void deleteSubscriptions(long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_subscriptionLocalService.deleteSubscriptions(userId);
	}

	@Override
	public void deleteSubscriptions(long userId, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_subscriptionLocalService.deleteSubscriptions(userId, groupId);
	}

	@Override
	public void deleteSubscriptions(long companyId, String className)
		throws com.liferay.portal.kernel.exception.PortalException {

		_subscriptionLocalService.deleteSubscriptions(companyId, className);
	}

	/**
	 * Deletes all the subscriptions to the entity.
	 *
	 * @param companyId the primary key of the company
	 * @param className the entity's class name
	 * @param classPK the primary key of the entity's instance
	 */
	@Override
	public void deleteSubscriptions(
			long companyId, String className, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		_subscriptionLocalService.deleteSubscriptions(
			companyId, className, classPK);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _subscriptionLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _subscriptionLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _subscriptionLocalService.dynamicQuery();
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

		return _subscriptionLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.subscription.model.impl.SubscriptionModelImpl</code>.
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

		return _subscriptionLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.subscription.model.impl.SubscriptionModelImpl</code>.
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

		return _subscriptionLocalService.dynamicQuery(
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

		return _subscriptionLocalService.dynamicQueryCount(dynamicQuery);
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

		return _subscriptionLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public Subscription fetchSubscription(long subscriptionId) {
		return _subscriptionLocalService.fetchSubscription(subscriptionId);
	}

	@Override
	public Subscription fetchSubscription(
		long companyId, long userId, String className, long classPK) {

		return _subscriptionLocalService.fetchSubscription(
			companyId, userId, className, classPK);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _subscriptionLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _subscriptionLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _subscriptionLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _subscriptionLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns the subscription with the primary key.
	 *
	 * @param subscriptionId the primary key of the subscription
	 * @return the subscription
	 * @throws PortalException if a subscription with the primary key could not be found
	 */
	@Override
	public Subscription getSubscription(long subscriptionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _subscriptionLocalService.getSubscription(subscriptionId);
	}

	/**
	 * Returns the subscription of the user to the entity.
	 *
	 * @param companyId the primary key of the company
	 * @param userId the primary key of the user
	 * @param className the entity's class name
	 * @param classPK the primary key of the entity's instance
	 * @return the subscription of the user to the entity
	 */
	@Override
	public Subscription getSubscription(
			long companyId, long userId, String className, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _subscriptionLocalService.getSubscription(
			companyId, userId, className, classPK);
	}

	/**
	 * Returns a range of all the subscriptions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.subscription.model.impl.SubscriptionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of subscriptions
	 * @param end the upper bound of the range of subscriptions (not inclusive)
	 * @return the range of subscriptions
	 */
	@Override
	public java.util.List<Subscription> getSubscriptions(int start, int end) {
		return _subscriptionLocalService.getSubscriptions(start, end);
	}

	/**
	 * Returns all the subscriptions of the user to the entities.
	 *
	 * @param companyId the primary key of the company
	 * @param userId the primary key of the user
	 * @param className the entity's class name
	 * @param classPKs the primary key of the entities
	 * @return the subscriptions of the user to the entities
	 */
	@Override
	public java.util.List<Subscription> getSubscriptions(
		long companyId, long userId, String className, long[] classPKs) {

		return _subscriptionLocalService.getSubscriptions(
			companyId, userId, className, classPKs);
	}

	/**
	 * Returns all the subscriptions to the entity.
	 *
	 * @param companyId the primary key of the company
	 * @param className the entity's class name
	 * @param classPK the primary key of the entity's instance
	 * @return the subscriptions to the entity
	 */
	@Override
	public java.util.List<Subscription> getSubscriptions(
		long companyId, String className, long classPK) {

		return _subscriptionLocalService.getSubscriptions(
			companyId, className, classPK);
	}

	/**
	 * @param className the entity's class name
	 * @return the subscriptions to the class name
	 * @deprecated As of Athanasius (7.3.x), with no direct replacement
	 */
	@Deprecated
	@Override
	public java.util.List<Subscription> getSubscriptions(String className) {
		return _subscriptionLocalService.getSubscriptions(className);
	}

	/**
	 * Returns the number of subscriptions.
	 *
	 * @return the number of subscriptions
	 */
	@Override
	public int getSubscriptionsCount() {
		return _subscriptionLocalService.getSubscriptionsCount();
	}

	/**
	 * @param className the entity's class name
	 * @return the subscriptions to the class name
	 * @deprecated As of Athanasius (7.3.x), with no direct replacement
	 */
	@Deprecated
	@Override
	public int getSubscriptionsCount(String className) {
		return _subscriptionLocalService.getSubscriptionsCount(className);
	}

	/**
	 * Returns an ordered range of all the subscriptions of the user.
	 *
	 * @param userId the primary key of the user
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @param orderByComparator the comparator to order the subscriptions
	 * @return the range of subscriptions of the user
	 */
	@Override
	public java.util.List<Subscription> getUserSubscriptions(
		long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Subscription>
			orderByComparator) {

		return _subscriptionLocalService.getUserSubscriptions(
			userId, start, end, orderByComparator);
	}

	/**
	 * Returns all the subscriptions of the user to the entities with the class
	 * name.
	 *
	 * @param userId the primary key of the user
	 * @param className the entity's class name
	 * @return the subscriptions of the user to the entities with the class name
	 */
	@Override
	public java.util.List<Subscription> getUserSubscriptions(
		long userId, String className) {

		return _subscriptionLocalService.getUserSubscriptions(
			userId, className);
	}

	/**
	 * Returns the number of subscriptions of the user.
	 *
	 * @param userId the primary key of the user
	 * @return the number of subscriptions of the user
	 */
	@Override
	public int getUserSubscriptionsCount(long userId) {
		return _subscriptionLocalService.getUserSubscriptionsCount(userId);
	}

	/**
	 * Returns <code>true</code> if the user is subscribed to the entity.
	 *
	 * @param companyId the primary key of the company
	 * @param userId the primary key of the user
	 * @param className the entity's class name
	 * @param classPK the primary key of the entity's instance
	 * @return <code>true</code> if the user is subscribed to the entity;
	 <code>false</code> otherwise
	 */
	@Override
	public boolean isSubscribed(
		long companyId, long userId, String className, long classPK) {

		return _subscriptionLocalService.isSubscribed(
			companyId, userId, className, classPK);
	}

	/**
	 * Returns <code>true</code> if the user is subscribed to any of the
	 * entities.
	 *
	 * @param companyId the primary key of the company
	 * @param userId the primary key of the user
	 * @param className the entity's class name
	 * @param classPKs the primary key of the entities
	 * @return <code>true</code> if the user is subscribed to any of the
	 entities; <code>false</code> otherwise
	 */
	@Override
	public boolean isSubscribed(
		long companyId, long userId, String className, long[] classPKs) {

		return _subscriptionLocalService.isSubscribed(
			companyId, userId, className, classPKs);
	}

	/**
	 * Updates the subscription in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SubscriptionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param subscription the subscription
	 * @return the subscription that was updated
	 */
	@Override
	public Subscription updateSubscription(Subscription subscription) {
		return _subscriptionLocalService.updateSubscription(subscription);
	}

	@Override
	public void updateSubscriptions(
		long companyId, long classNameId, long oldClassPK, long newClassPK) {

		_subscriptionLocalService.updateSubscriptions(
			companyId, classNameId, oldClassPK, newClassPK);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _subscriptionLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<Subscription> getCTPersistence() {
		return _subscriptionLocalService.getCTPersistence();
	}

	@Override
	public Class<Subscription> getModelClass() {
		return _subscriptionLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<Subscription>, R, E>
				updateUnsafeFunction)
		throws E {

		return _subscriptionLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public SubscriptionLocalService getWrappedService() {
		return _subscriptionLocalService;
	}

	@Override
	public void setWrappedService(
		SubscriptionLocalService subscriptionLocalService) {

		_subscriptionLocalService = subscriptionLocalService;
	}

	private SubscriptionLocalService _subscriptionLocalService;

}