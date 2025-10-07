/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.subscription.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.subscription.model.Subscription;

import java.io.Serializable;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for Subscription. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see SubscriptionLocalServiceUtil
 * @generated
 */
@CTAware
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface SubscriptionLocalService
	extends BaseLocalService, CTService<Subscription>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.subscription.service.impl.SubscriptionLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the subscription local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link SubscriptionLocalServiceUtil} if injection and service tracking are not available.
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
	public Subscription addSubscription(
			long userId, long groupId, String className, long classPK)
		throws PortalException;

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
	public Subscription addSubscription(
			long userId, long groupId, String className, long classPK,
			String frequency)
		throws PortalException;

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
	@Indexable(type = IndexableType.REINDEX)
	public Subscription addSubscription(Subscription subscription);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Creates a new subscription with the primary key. Does not add the subscription to the database.
	 *
	 * @param subscriptionId the primary key for the new subscription
	 * @return the new subscription
	 */
	@Transactional(enabled = false)
	public Subscription createSubscription(long subscriptionId);

	public void deleteGroupSubscriptions(long groupId);

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

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
	@Indexable(type = IndexableType.DELETE)
	public Subscription deleteSubscription(long subscriptionId)
		throws PortalException;

	/**
	 * Deletes the user's subscription to the entity. A social activity with the
	 * unsubscribe action is created.
	 *
	 * @param userId the primary key of the user
	 * @param className the entity's class name
	 * @param classPK the primary key of the entity's instance
	 */
	public void deleteSubscription(long userId, String className, long classPK)
		throws PortalException;

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
	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public Subscription deleteSubscription(Subscription subscription)
		throws PortalException;

	/**
	 * Deletes all the subscriptions of the user.
	 *
	 * @param userId the primary key of the user
	 */
	public void deleteSubscriptions(long userId) throws PortalException;

	public void deleteSubscriptions(long userId, long groupId)
		throws PortalException;

	public void deleteSubscriptions(long companyId, String className)
		throws PortalException;

	/**
	 * Deletes all the subscriptions to the entity.
	 *
	 * @param companyId the primary key of the company
	 * @param className the entity's class name
	 * @param classPK the primary key of the entity's instance
	 */
	public void deleteSubscriptions(
			long companyId, String className, long classPK)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> T dslQuery(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int dslQueryCount(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DynamicQuery dynamicQuery();

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(DynamicQuery dynamicQuery);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(DynamicQuery dynamicQuery);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(
		DynamicQuery dynamicQuery, Projection projection);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Subscription fetchSubscription(long subscriptionId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Subscription fetchSubscription(
		long companyId, long userId, String className, long classPK);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	/**
	 * @throws PortalException
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Returns the subscription with the primary key.
	 *
	 * @param subscriptionId the primary key of the subscription
	 * @return the subscription
	 * @throws PortalException if a subscription with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Subscription getSubscription(long subscriptionId)
		throws PortalException;

	/**
	 * Returns the subscription of the user to the entity.
	 *
	 * @param companyId the primary key of the company
	 * @param userId the primary key of the user
	 * @param className the entity's class name
	 * @param classPK the primary key of the entity's instance
	 * @return the subscription of the user to the entity
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Subscription getSubscription(
			long companyId, long userId, String className, long classPK)
		throws PortalException;

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Subscription> getSubscriptions(int start, int end);

	/**
	 * Returns all the subscriptions of the user to the entities.
	 *
	 * @param companyId the primary key of the company
	 * @param userId the primary key of the user
	 * @param className the entity's class name
	 * @param classPKs the primary key of the entities
	 * @return the subscriptions of the user to the entities
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Subscription> getSubscriptions(
		long companyId, long userId, String className, long[] classPKs);

	/**
	 * Returns all the subscriptions to the entity.
	 *
	 * @param companyId the primary key of the company
	 * @param className the entity's class name
	 * @param classPK the primary key of the entity's instance
	 * @return the subscriptions to the entity
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Subscription> getSubscriptions(
		long companyId, String className, long classPK);

	/**
	 * @param className the entity's class name
	 * @return the subscriptions to the class name
	 * @deprecated As of Athanasius (7.3.x), with no direct replacement
	 */
	@Deprecated
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Subscription> getSubscriptions(String className);

	/**
	 * Returns the number of subscriptions.
	 *
	 * @return the number of subscriptions
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getSubscriptionsCount();

	/**
	 * @param className the entity's class name
	 * @return the subscriptions to the class name
	 * @deprecated As of Athanasius (7.3.x), with no direct replacement
	 */
	@Deprecated
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getSubscriptionsCount(String className);

	/**
	 * Returns an ordered range of all the subscriptions of the user.
	 *
	 * @param userId the primary key of the user
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @param orderByComparator the comparator to order the subscriptions
	 * @return the range of subscriptions of the user
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Subscription> getUserSubscriptions(
		long userId, int start, int end,
		OrderByComparator<Subscription> orderByComparator);

	/**
	 * Returns all the subscriptions of the user to the entities with the class
	 * name.
	 *
	 * @param userId the primary key of the user
	 * @param className the entity's class name
	 * @return the subscriptions of the user to the entities with the class name
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Subscription> getUserSubscriptions(
		long userId, String className);

	/**
	 * Returns the number of subscriptions of the user.
	 *
	 * @param userId the primary key of the user
	 * @return the number of subscriptions of the user
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getUserSubscriptionsCount(long userId);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean isSubscribed(
		long companyId, long userId, String className, long classPK);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean isSubscribed(
		long companyId, long userId, String className, long[] classPKs);

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
	@Indexable(type = IndexableType.REINDEX)
	public Subscription updateSubscription(Subscription subscription);

	public void updateSubscriptions(
		long companyId, long classNameId, long oldClassPK, long newClassPK);

	@Override
	@Transactional(enabled = false)
	public CTPersistence<Subscription> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<Subscription> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<Subscription>, R, E>
				updateUnsafeFunction)
		throws E;

}