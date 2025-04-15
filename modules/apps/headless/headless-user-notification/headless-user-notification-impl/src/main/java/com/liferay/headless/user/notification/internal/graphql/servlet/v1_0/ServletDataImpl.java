/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.user.notification.internal.graphql.servlet.v1_0;

import com.liferay.headless.user.notification.internal.graphql.mutation.v1_0.Mutation;
import com.liferay.headless.user.notification.internal.graphql.query.v1_0.Query;
import com.liferay.headless.user.notification.internal.resource.v1_0.UserNotificationResourceImpl;
import com.liferay.headless.user.notification.resource.v1_0.UserNotificationResource;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.vulcan.graphql.servlet.ServletData;

import jakarta.annotation.Generated;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceScope;

/**
 * @author Carlos Correa
 * @generated
 */
@Component(service = ServletData.class)
@Generated("")
public class ServletDataImpl implements ServletData {

	@Activate
	public void activate(BundleContext bundleContext) {
		Mutation.setUserNotificationResourceComponentServiceObjects(
			_userNotificationResourceComponentServiceObjects);

		Query.setUserNotificationResourceComponentServiceObjects(
			_userNotificationResourceComponentServiceObjects);
	}

	public String getApplicationName() {
		return "Liferay.Headless.User.Notification";
	}

	@Override
	public Mutation getMutation() {
		return new Mutation();
	}

	@Override
	public String getPath() {
		return "/headless-user-notification-graphql/v1_0";
	}

	@Override
	public Query getQuery() {
		return new Query();
	}

	public ObjectValuePair<Class<?>, String> getResourceMethodObjectValuePair(
		String methodName, boolean mutation) {

		if (mutation) {
			return _resourceMethodObjectValuePairs.get(
				"mutation#" + methodName);
		}

		return _resourceMethodObjectValuePairs.get("query#" + methodName);
	}

	private static final Map<String, ObjectValuePair<Class<?>, String>>
		_resourceMethodObjectValuePairs =
			new HashMap<String, ObjectValuePair<Class<?>, String>>() {
				{
					put(
						"mutation#updateUserNotificationRead",
						new ObjectValuePair<>(
							UserNotificationResourceImpl.class,
							"putUserNotificationRead"));
					put(
						"mutation#updateUserNotificationUnread",
						new ObjectValuePair<>(
							UserNotificationResourceImpl.class,
							"putUserNotificationUnread"));

					put(
						"query#myUserNotifications",
						new ObjectValuePair<>(
							UserNotificationResourceImpl.class,
							"getMyUserNotificationsPage"));
					put(
						"query#userAccountUserNotifications",
						new ObjectValuePair<>(
							UserNotificationResourceImpl.class,
							"getUserAccountUserNotificationsPage"));
					put(
						"query#userNotification",
						new ObjectValuePair<>(
							UserNotificationResourceImpl.class,
							"getUserNotification"));
				}
			};

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<UserNotificationResource>
		_userNotificationResourceComponentServiceObjects;

}