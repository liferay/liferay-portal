/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.subscription.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.subscription.model.Subscription;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for Subscription. This utility wraps
 * <code>com.liferay.subscription.service.impl.SubscriptionLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see SubscriptionLocalService
 * @generated
 */
public class SubscriptionLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.subscription.service.impl.SubscriptionLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

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
	public static Subscription addSubscription(
			long userId, long groupId, String className, long classPK)
		throws PortalException {

		return getService().addSubscription(
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
	public static Subscription addSubscription(
			long userId, long groupId, String className, long classPK,
			String frequency)
		throws PortalException {

		return getService().addSubscription(
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
	public static Subscription addSubscription(Subscription subscription) {
		return getService().addSubscription(subscription);
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
	 * Creates a new subscription with the primary key. Does not add the subscription to the database.
	 *
	 * @param subscriptionId the primary key for the new subscription
	 * @return the new subscription
	 */
	public static Subscription createSubscription(long subscriptionId) {
		return getService().createSubscription(subscriptionId);
	}

	public static void deleteGroupSubscriptions(long groupId) {
		getService().deleteGroupSubscriptions(groupId);
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
	public static Subscription deleteSubscription(long subscriptionId)
		throws PortalException {

		return getService().deleteSubscription(subscriptionId);
	}

	/**
	 * Deletes the user's subscription to the entity. A social activity with the
	 * unsubscribe action is created.
	 *
	 * @param userId the primary key of the user
	 * @param className the entity's class name
	 * @param classPK the primary key of the entity's instance
	 */
	public static void deleteSubscription(
			long userId, String className, long classPK)
		throws PortalException {

		getService().deleteSubscription(userId, className, classPK);
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
	public static Subscription deleteSubscription(Subscription subscription)
		throws PortalException {

		return getService().deleteSubscription(subscription);
	}

	/**
	 * Deletes all the subscriptions of the user.
	 *
	 * @param userId the primary key of the user
	 */
	public static void deleteSubscriptions(long userId) throws PortalException {
		getService().deleteSubscriptions(userId);
	}

	public static void deleteSubscriptions(long userId, long groupId)
		throws PortalException {

		getService().deleteSubscriptions(userId, groupId);
	}

	public static void deleteSubscriptions(long companyId, String className)
		throws PortalException {

		getService().deleteSubscriptions(companyId, className);
	}

	/**
	 * Deletes all the subscriptions to the entity.
	 *
	 * @param companyId the primary key of the company
	 * @param className the entity's class name
	 * @param classPK the primary key of the entity's instance
	 */
	public static void deleteSubscriptions(
			long companyId, String className, long classPK)
		throws PortalException {

		getService().deleteSubscriptions(companyId, className, classPK);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.subscription.model.impl.SubscriptionModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.subscription.model.impl.SubscriptionModelImpl</code>.
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

	public static Subscription fetchSubscription(long subscriptionId) {
		return getService().fetchSubscription(subscriptionId);
	}

	public static Subscription fetchSubscription(
		long companyId, long userId, String className, long classPK) {

		return getService().fetchSubscription(
			companyId, userId, className, classPK);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
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
	 * Returns the subscription with the primary key.
	 *
	 * @param subscriptionId the primary key of the subscription
	 * @return the subscription
	 * @throws PortalException if a subscription with the primary key could not be found
	 */
	public static Subscription getSubscription(long subscriptionId)
		throws PortalException {

		return getService().getSubscription(subscriptionId);
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
	public static Subscription getSubscription(
			long companyId, long userId, String className, long classPK)
		throws PortalException {

		return getService().getSubscription(
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
	public static List<Subscription> getSubscriptions(int start, int end) {
		return getService().getSubscriptions(start, end);
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
	public static List<Subscription> getSubscriptions(
		long companyId, long userId, String className, long[] classPKs) {

		return getService().getSubscriptions(
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
	public static List<Subscription> getSubscriptions(
		long companyId, String className, long classPK) {

		return getService().getSubscriptions(companyId, className, classPK);
	}

	/**
	 * @param className the entity's class name
	 * @return the subscriptions to the class name
	 * @deprecated As of Athanasius (7.3.x), with no direct replacement
	 */
	@Deprecated
	public static List<Subscription> getSubscriptions(String className) {
		return getService().getSubscriptions(className);
	}

	/**
	 * Returns the number of subscriptions.
	 *
	 * @return the number of subscriptions
	 */
	public static int getSubscriptionsCount() {
		return getService().getSubscriptionsCount();
	}

	/**
	 * @param className the entity's class name
	 * @return the subscriptions to the class name
	 * @deprecated As of Athanasius (7.3.x), with no direct replacement
	 */
	@Deprecated
	public static int getSubscriptionsCount(String className) {
		return getService().getSubscriptionsCount(className);
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
	public static List<Subscription> getUserSubscriptions(
		long userId, int start, int end,
		OrderByComparator<Subscription> orderByComparator) {

		return getService().getUserSubscriptions(
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
	public static List<Subscription> getUserSubscriptions(
		long userId, String className) {

		return getService().getUserSubscriptions(userId, className);
	}

	/**
	 * Returns the number of subscriptions of the user.
	 *
	 * @param userId the primary key of the user
	 * @return the number of subscriptions of the user
	 */
	public static int getUserSubscriptionsCount(long userId) {
		return getService().getUserSubscriptionsCount(userId);
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
	public static boolean isSubscribed(
		long companyId, long userId, String className, long classPK) {

		return getService().isSubscribed(companyId, userId, className, classPK);
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
	public static boolean isSubscribed(
		long companyId, long userId, String className, long[] classPKs) {

		return getService().isSubscribed(
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
	public static Subscription updateSubscription(Subscription subscription) {
		return getService().updateSubscription(subscription);
	}

	public static void updateSubscriptions(
		long companyId, long classNameId, long oldClassPK, long newClassPK) {

		getService().updateSubscriptions(
			companyId, classNameId, oldClassPK, newClassPK);
	}

	public static SubscriptionLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<SubscriptionLocalService> _serviceSnapshot =
		new Snapshot<>(
			SubscriptionLocalServiceUtil.class, SubscriptionLocalService.class);

}