/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Subscription;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.service.base.SubscriptionLocalServiceBaseImpl;

import java.util.List;

/**
 * Provides the local service for accessing, adding, and deleting notification
 * subscriptions to entities. It handles subscriptions to entities found in many
 * different places in the portal, including message boards, blogs, and
 * documents and media.
 *
 * @author     Charles May
 * @author     Zsolt Berentey
 * @deprecated As of Judson (7.1.x), replaced by {@link
 *             com.liferay.subscription.service.impl.SubscriptionLocalServiceImpl}
 */
@Deprecated
public class SubscriptionLocalServiceImpl
	extends SubscriptionLocalServiceBaseImpl {

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
	 * @param  userId the primary key of the user
	 * @param  groupId the primary key of the entity's group
	 * @param  className the entity's class name
	 * @param  classPK the primary key of the entity's instance
	 * @return the subscription
	 */
	@Override
	public Subscription addSubscription(
			long userId, long groupId, String className, long classPK)
		throws PortalException {

		throw new UnsupportedOperationException(
			"This class is deprecated and replaced by " +
				"com.liferay.subscription.service.impl." +
					"SubscriptionLocalServiceImpl");
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
	 * @param  userId the primary key of the user
	 * @param  groupId the primary key of the entity's group
	 * @param  className the entity's class name
	 * @param  classPK the primary key of the entity's instance
	 * @param  frequency the frequency for notifications
	 * @return the subscription
	 */
	@Override
	public Subscription addSubscription(
			long userId, long groupId, String className, long classPK,
			String frequency)
		throws PortalException {

		throw new UnsupportedOperationException(
			"This class is deprecated and replaced by " +
				"com.liferay.subscription.service.impl." +
					"SubscriptionLocalServiceImpl");
	}

	/**
	 * Deletes the subscription with the primary key. A social activity with the
	 * unsubscribe action is created.
	 *
	 * @param  subscriptionId the primary key of the subscription
	 * @return the subscription that was removed
	 */
	@Override
	public Subscription deleteSubscription(long subscriptionId)
		throws PortalException {

		throw new UnsupportedOperationException(
			"This class is deprecated and replaced by " +
				"com.liferay.subscription.service.impl." +
					"SubscriptionLocalServiceImpl");
	}

	/**
	 * Deletes the subscription. A social activity with the unsubscribe action
	 * is created.
	 *
	 * @param  subscription the subscription
	 * @return the subscription that was removed
	 */
	@Override
	public Subscription deleteSubscription(Subscription subscription)
		throws PortalException {

		throw new UnsupportedOperationException(
			"This class is deprecated and replaced by " +
				"com.liferay.subscription.service.impl." +
					"SubscriptionLocalServiceImpl");
	}

	/**
	 * Returns all the subscriptions to the entity.
	 *
	 * @param  companyId the primary key of the company
	 * @param  className the entity's class name
	 * @param  classPK the primary key of the entity's instance
	 * @return the subscriptions to the entity
	 */
	@Override
	public List<Subscription> getSubscriptions(
		long companyId, String className, long classPK) {

		throw new UnsupportedOperationException(
			"This class is deprecated and replaced by " +
				"com.liferay.subscription.service.impl." +
					"SubscriptionLocalServiceImpl");
	}

	/**
	 * Returns an ordered range of all the subscriptions of the user.
	 *
	 * @param  userId the primary key of the user
	 * @param  start the lower bound of the range of results
	 * @param  end the upper bound of the range of results (not inclusive)
	 * @param  orderByComparator the comparator to order the subscriptions
	 * @return the range of subscriptions of the user
	 */
	@Override
	public List<Subscription> getUserSubscriptions(
		long userId, int start, int end,
		OrderByComparator<Subscription> orderByComparator) {

		throw new UnsupportedOperationException(
			"This class is deprecated and replaced by " +
				"com.liferay.subscription.service.impl." +
					"SubscriptionLocalServiceImpl");
	}

	/**
	 * Returns the number of subscriptions of the user.
	 *
	 * @param  userId the primary key of the user
	 * @return the number of subscriptions of the user
	 */
	@Override
	public int getUserSubscriptionsCount(long userId) {
		throw new UnsupportedOperationException(
			"This class is deprecated and replaced by " +
				"com.liferay.subscription.service.impl." +
					"SubscriptionLocalServiceImpl");
	}

	/**
	 * Returns <code>true</code> if the user is subscribed to the entity.
	 *
	 * @param  companyId the primary key of the company
	 * @param  userId the primary key of the user
	 * @param  className the entity's class name
	 * @param  classPK the primary key of the entity's instance
	 * @return <code>true</code> if the user is subscribed to the entity;
	 *         <code>false</code> otherwise
	 */
	@Override
	public boolean isSubscribed(
		long companyId, long userId, String className, long classPK) {

		throw new UnsupportedOperationException(
			"This class is deprecated and replaced by " +
				"com.liferay.subscription.service.impl." +
					"SubscriptionLocalServiceImpl");
	}

	/**
	 * Returns <code>true</code> if the user is subscribed to any of the
	 * entities.
	 *
	 * @param  companyId the primary key of the company
	 * @param  userId the primary key of the user
	 * @param  className the entity's class name
	 * @param  classPKs the primary key of the entities
	 * @return <code>true</code> if the user is subscribed to any of the
	 *         entities; <code>false</code> otherwise
	 */
	@Override
	public boolean isSubscribed(
		long companyId, long userId, String className, long[] classPKs) {

		throw new UnsupportedOperationException(
			"This class is deprecated and replaced by " +
				"com.liferay.subscription.service.impl." +
					"SubscriptionLocalServiceImpl");
	}

}