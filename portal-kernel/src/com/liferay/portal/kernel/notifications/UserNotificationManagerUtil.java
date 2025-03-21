/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.notifications;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Jonathan Lee
 * @author Roberto Díaz
 */
public class UserNotificationManagerUtil {

	public static UserNotificationDefinition fetchUserNotificationDefinition(
		String portletId, long classNameId, int notificationType) {

		List<UserNotificationDefinition> userNotificationDefinitions =
			_userNotificationDefinitions.getService(portletId);

		if (userNotificationDefinitions == null) {
			return null;
		}

		for (UserNotificationDefinition userNotificationDefinition :
				userNotificationDefinitions) {

			if ((userNotificationDefinition.getClassNameId() == classNameId) &&
				(userNotificationDefinition.getNotificationType() ==
					notificationType)) {

				return userNotificationDefinition;
			}
		}

		return null;
	}

	public static Map<String, List<UserNotificationDefinition>>
		getActiveUserNotificationDefinitions() {

		return _getUserNotificationDefinitions(true);
	}

	public static Map<String, List<UserNotificationDefinition>>
		getUserNotificationDefinitions() {

		return _getUserNotificationDefinitions(false);
	}

	public static Map<String, Map<String, UserNotificationHandler>>
		getUserNotificationHandlers() {

		return Collections.unmodifiableMap(_userNotificationHandlers);
	}

	public static UserNotificationFeedEntry interpret(
			String selector, UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext)
		throws PortalException {

		Map<String, UserNotificationHandler> userNotificationHandlers =
			_userNotificationHandlers.get(selector);

		if (userNotificationHandlers == null) {
			return null;
		}

		UserNotificationHandler userNotificationHandler =
			userNotificationHandlers.get(userNotificationEvent.getType());

		if (userNotificationHandler == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("No interpreter found for " + userNotificationEvent);
			}

			return null;
		}

		return userNotificationHandler.interpret(
			userNotificationEvent, serviceContext);
	}

	public static boolean isDeliver(
			long userId, String portletId, long classNameId,
			int notificationType, int deliveryType)
		throws PortalException {

		return _isDeliver(
			userId, StringPool.BLANK, portletId, classNameId, notificationType,
			deliveryType, null);
	}

	public static boolean isDeliver(
			long userId, String selector, String portletId, long classNameId,
			int notificationType, int deliveryType,
			ServiceContext serviceContext)
		throws PortalException {

		return _isDeliver(
			userId, selector, portletId, classNameId, notificationType,
			deliveryType, serviceContext);
	}

	private static Map<String, List<UserNotificationDefinition>>
		_getUserNotificationDefinitions(boolean active) {

		Map<String, List<UserNotificationDefinition>>
			userNotificationDefinitionsMap = new ConcurrentHashMap<>();

		for (String portletId : _userNotificationDefinitions.keySet()) {
			if (active) {
				Portlet portlet = PortletLocalServiceUtil.getPortletById(
					portletId);

				if (portlet == null) {
					continue;
				}
			}

			userNotificationDefinitionsMap.put(
				portletId, _userNotificationDefinitions.getService(portletId));
		}

		return Collections.unmodifiableMap(userNotificationDefinitionsMap);
	}

	private static boolean _isDeliver(
			long userId, String selector, String portletId, long classNameId,
			int notificationType, int deliveryType,
			ServiceContext serviceContext)
		throws PortalException {

		Map<String, UserNotificationHandler> userNotificationHandlers =
			_userNotificationHandlers.get(selector);

		if (userNotificationHandlers == null) {
			return false;
		}

		UserNotificationHandler userNotificationHandler =
			userNotificationHandlers.get(portletId);

		if (userNotificationHandler == null) {
			if (deliveryType == UserNotificationDeliveryConstants.TYPE_EMAIL) {
				return true;
			}

			return false;
		}

		return userNotificationHandler.isDeliver(
			userId, classNameId, notificationType, deliveryType,
			serviceContext);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UserNotificationManagerUtil.class);

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();
	private static final ServiceTrackerMap
		<String, List<UserNotificationDefinition>>
			_userNotificationDefinitions =
				ServiceTrackerMapFactory.openMultiValueMap(
					_bundleContext, UserNotificationDefinition.class,
					"jakarta.portlet.name");
	private static final Map<String, Map<String, UserNotificationHandler>>
		_userNotificationHandlers = new ConcurrentHashMap<>();
	private static final ServiceTracker
		<UserNotificationHandler, UserNotificationHandler>
			_userNotificationHandlerServiceTracker;

	private static class UserNotificationHandlerServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<UserNotificationHandler, UserNotificationHandler> {

		@Override
		public UserNotificationHandler addingService(
			ServiceReference<UserNotificationHandler> serviceReference) {

			UserNotificationHandler userNotificationHandler =
				_bundleContext.getService(serviceReference);

			String selector = userNotificationHandler.getSelector();

			Map<String, UserNotificationHandler> userNotificationHandlers =
				_userNotificationHandlers.get(selector);

			if (userNotificationHandlers == null) {
				userNotificationHandlers = new HashMap<>();

				_userNotificationHandlers.put(
					selector, userNotificationHandlers);
			}

			userNotificationHandlers.put(
				userNotificationHandler.getPortletId(),
				userNotificationHandler);

			return userNotificationHandler;
		}

		@Override
		public void modifiedService(
			ServiceReference<UserNotificationHandler> serviceReference,
			UserNotificationHandler userNotificationHandler) {
		}

		@Override
		public void removedService(
			ServiceReference<UserNotificationHandler> serviceReference,
			UserNotificationHandler userNotificationHandler) {

			_bundleContext.ungetService(serviceReference);

			Map<String, UserNotificationHandler> userNotificationHandlers =
				_userNotificationHandlers.get(
					userNotificationHandler.getSelector());

			if (userNotificationHandlers == null) {
				return;
			}

			userNotificationHandlers.remove(
				userNotificationHandler.getPortletId());
		}

	}

	static {
		_userNotificationHandlerServiceTracker = new ServiceTracker<>(
			_bundleContext, UserNotificationHandler.class,
			new UserNotificationHandlerServiceTrackerCustomizer());

		_userNotificationHandlerServiceTracker.open();
	}

}