/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notifications.web.internal.portlet;

import com.liferay.bulk.selection.BulkSelection;
import com.liferay.bulk.selection.BulkSelectionFactory;
import com.liferay.notifications.web.internal.constants.NotificationsPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.model.UserNotificationDelivery;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.notifications.UserNotificationDefinition;
import com.liferay.portal.kernel.notifications.UserNotificationDeliveryType;
import com.liferay.portal.kernel.notifications.UserNotificationManagerUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.UserNotificationDeliveryLocalService;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.subscription.model.Subscription;
import com.liferay.subscription.service.SubscriptionLocalService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sergio González
 * @author Roberto Díaz
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=notifications-portlet",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Notifications",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.add-process-action-success-action=false",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/notifications/view.jsp",
		"jakarta.portlet.name=" + NotificationsPortletKeys.NOTIFICATIONS,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=administrator,guest,power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class NotificationsPortlet extends MVCPortlet {

	public void delete(
			ActionRequest actionRequest, ActionResponse actionResponse,
			String actionName)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		BulkSelection<UserNotificationEvent>
			userNotificationEventBulkSelection =
				_userNotificationEventBulkSelectionFactory.create(
					_getParameterMap(actionRequest, themeDisplay));

		userNotificationEventBulkSelection.forEach(
			userNotificationEvent -> _deleteUserNotificationEvent(
				themeDisplay.getUserId(),
				userNotificationEvent.getUserNotificationEventId()));

		String message = "notification-was-deleted-successfully";

		if (actionName.equals("deleteNotifications")) {
			message = "notifications-were-deleted-successfully";
		}

		_addSuccessMessage(actionRequest, message);

		_sendRedirect(actionRequest, actionResponse);
	}

	public void markAsRead(
			ActionRequest actionRequest, ActionResponse actionResponse,
			String actionName)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		BulkSelection<UserNotificationEvent>
			userNotificationEventBulkSelection =
				_userNotificationEventBulkSelectionFactory.create(
					_getParameterMap(actionRequest, themeDisplay));

		userNotificationEventBulkSelection.forEach(
			userNotificationEvent -> _updateArchived(
				themeDisplay.getUserId(),
				userNotificationEvent.getUserNotificationEventId(), true));

		String message = "notification-was-marked-as-read-successfully";

		if (actionName.equals("markAllNotificationsAsRead") ||
			ParamUtil.getBoolean(actionRequest, "selectAll")) {

			message = "all-notifications-were-marked-as-read-successfully";
		}
		else if (actionName.equals("markNotificationsAsRead")) {
			message = "notifications-were-marked-as-read-successfully";
		}

		_addSuccessMessage(actionRequest, message);

		_sendRedirect(actionRequest, actionResponse);
	}

	public void markAsUnread(
			ActionRequest actionRequest, ActionResponse actionResponse,
			String actionName)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		BulkSelection<UserNotificationEvent>
			userNotificationEventBulkSelection =
				_userNotificationEventBulkSelectionFactory.create(
					_getParameterMap(actionRequest, themeDisplay));

		userNotificationEventBulkSelection.forEach(
			userNotificationEvent -> _updateArchived(
				themeDisplay.getUserId(),
				userNotificationEvent.getUserNotificationEventId(), false));

		String message = "notification-was-marked-as-unread-successfully";

		if (actionName.equals("markNotificationsAsUnread")) {
			message = "notifications-were-marked-as-unread-successfully";
		}

		_addSuccessMessage(actionRequest, message);

		_sendRedirect(actionRequest, actionResponse);
	}

	@Override
	public void processAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws PortletException {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (!themeDisplay.isSignedIn()) {
			return;
		}

		try {
			String actionName = ParamUtil.getString(
				actionRequest, ActionRequest.ACTION_NAME);

			if (actionName.equals("deleteNotifications") ||
				actionName.equals("deleteUserNotificationEvent")) {

				delete(actionRequest, actionResponse, actionName);
			}
			else if (actionName.equals("markAllNotificationsAsRead") ||
					 actionName.equals("markNotificationAsRead") ||
					 actionName.equals("markNotificationsAsRead")) {

				markAsRead(actionRequest, actionResponse, actionName);
			}
			else if (actionName.equals("markNotificationAsUnread") ||
					 actionName.equals("markNotificationsAsUnread")) {

				markAsUnread(actionRequest, actionResponse, actionName);
			}
			else if (actionName.equals("unsubscribe")) {
				unsubscribe(actionRequest, actionResponse);
			}
			else if (actionName.equals("updateUserNotificationDelivery")) {
				updateUserNotificationDelivery(actionRequest, actionResponse);
			}
			else {
				super.processAction(actionRequest, actionResponse);
			}
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}
	}

	public void unsubscribe(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long subscriptionId = ParamUtil.getLong(
			actionRequest, "subscriptionId");
		long userNotificationEventId = ParamUtil.getLong(
			actionRequest, "userNotificationEventId");

		_deleteSubscription(themeDisplay.getUserId(), subscriptionId);

		UserNotificationEvent userNotificationEvent =
			_userNotificationEventLocalService.fetchUserNotificationEvent(
				userNotificationEventId);

		if ((userNotificationEvent != null) &&
			!userNotificationEvent.isArchived()) {

			_updateArchived(
				themeDisplay.getUserId(), userNotificationEventId, true);
		}

		_addSuccessMessage(
			actionRequest,
			"you-have-unsubscribed-from-this-asset-successfully");
	}

	public void updateUserNotificationDelivery(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long[] userNotificationDeliveryIds = ParamUtil.getLongValues(
			actionRequest, "userNotificationDeliveryIds");

		for (long userNotificationDeliveryId : userNotificationDeliveryIds) {
			boolean deliver = ParamUtil.getBoolean(
				actionRequest, String.valueOf(userNotificationDeliveryId));

			_updateUserNotificationDelivery(
				themeDisplay.getUserId(), userNotificationDeliveryId, deliver);
		}

		_addSuccessMessage(
			actionRequest, "your-configuration-was-saved-successfully");

		_sendRedirect(actionRequest, actionResponse);
	}

	private void _addSuccessMessage(
		ActionRequest actionRequest, String message) {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		SessionMessages.add(
			actionRequest, "requestProcessed",
			_language.get(themeDisplay.getLocale(), message));
	}

	private void _deleteSubscription(long userId, long subscriptionId)
		throws Exception {

		Subscription subscription = _subscriptionLocalService.fetchSubscription(
			subscriptionId);

		if (subscription == null) {
			return;
		}

		if (subscription.getUserId() != userId) {
			throw new PrincipalException();
		}

		_subscriptionLocalService.deleteSubscription(subscriptionId);
	}

	private void _deleteUserNotificationEvent(
			long userId, long userNotificationEventId)
		throws PortalException {

		UserNotificationEvent userNotificationEvent =
			_userNotificationEventLocalService.fetchUserNotificationEvent(
				userNotificationEventId);

		if (userNotificationEvent == null) {
			return;
		}

		if (userNotificationEvent.getUserId() != userId) {
			throw new PrincipalException();
		}

		_userNotificationEventLocalService.deleteUserNotificationEvent(
			userNotificationEvent);
	}

	private Map<String, String[]> _getParameterMap(
		ActionRequest actionRequest, ThemeDisplay themeDisplay) {

		return HashMapBuilder.create(
			actionRequest.getParameterMap()
		).put(
			"userId", new String[] {String.valueOf(themeDisplay.getUserId())}
		).build();
	}

	private void _sendRedirect(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String redirect = ParamUtil.getString(actionRequest, "redirect");

		if (Validator.isNotNull(redirect)) {
			actionResponse.sendRedirect(_portal.escapeRedirect(redirect));
		}
	}

	private void _updateArchived(
			long userId, long userNotificationEventId, boolean archived)
		throws PortalException {

		UserNotificationEvent userNotificationEvent =
			_userNotificationEventLocalService.fetchUserNotificationEvent(
				userNotificationEventId);

		if (userNotificationEvent == null) {
			return;
		}

		if (userNotificationEvent.getUserId() != userId) {
			throw new PrincipalException();
		}

		userNotificationEvent.setArchived(archived);

		_userNotificationEventLocalService.updateUserNotificationEvent(
			userNotificationEvent);
	}

	private void _updateUserNotificationDelivery(
			long userId, long userNotificationDeliveryId, boolean deliver)
		throws Exception {

		UserNotificationDelivery userNotificationDelivery =
			_userNotificationDeliveryLocalService.fetchUserNotificationDelivery(
				userNotificationDeliveryId);

		if (userNotificationDelivery == null) {
			return;
		}

		if (userNotificationDelivery.getUserId() != userId) {
			throw new PrincipalException();
		}

		UserNotificationDefinition userNotificationDefinition =
			UserNotificationManagerUtil.fetchUserNotificationDefinition(
				userNotificationDelivery.getPortletId(),
				userNotificationDelivery.getClassNameId(),
				userNotificationDelivery.getNotificationType());

		if (userNotificationDefinition == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					String.format(
						"No user notification definition found for class " +
							"name ID %d, notification type %d, and portlet %s",
						userNotificationDelivery.getClassNameId(),
						userNotificationDelivery.getNotificationType(),
						userNotificationDelivery.getPortletId()));
			}

			return;
		}

		UserNotificationDeliveryType userNotificationDeliveryType =
			userNotificationDefinition.getUserNotificationDeliveryType(
				userNotificationDelivery.getDeliveryType());

		if (!userNotificationDeliveryType.isModifiable()) {
			return;
		}

		_userNotificationDeliveryLocalService.updateUserNotificationDelivery(
			userNotificationDeliveryId, deliver);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		NotificationsPortlet.class);

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.notifications.web)(&(release.schema.version>=2.1.0)(!(release.schema.version>=3.0.0))))"
	)
	private Release _release;

	@Reference
	private SubscriptionLocalService _subscriptionLocalService;

	@Reference
	private UserNotificationDeliveryLocalService
		_userNotificationDeliveryLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.model.UserNotificationEvent)"
	)
	private BulkSelectionFactory<UserNotificationEvent>
		_userNotificationEventBulkSelectionFactory;

	@Reference
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

}