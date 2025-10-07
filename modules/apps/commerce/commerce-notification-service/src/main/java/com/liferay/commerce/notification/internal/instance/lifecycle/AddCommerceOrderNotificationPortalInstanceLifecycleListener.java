/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.notification.internal.instance.lifecycle;

import com.liferay.notification.context.NotificationContext;
import com.liferay.notification.rest.dto.v1_0.NotificationTemplate;
import com.liferay.notification.rest.dto.v1_0.util.NotificationUtil;
import com.liferay.notification.service.NotificationTemplateLocalService;
import com.liferay.notification.type.NotificationType;
import com.liferay.notification.type.NotificationTypeServiceTracker;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;

import java.net.URL;
import java.net.URLConnection;

import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 */
@Component(
	property = "service.ranking:Integer=" + Integer.MIN_VALUE,
	service = PortalInstanceLifecycleListener.class
)
public class AddCommerceOrderNotificationPortalInstanceLifecycleListener
	extends BasePortalInstanceLifecycleListener {

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		try {
			_verifyCommerceOrderNotificationTemplate(company.getCompanyId());
			_verifyCommerceOrderObjectAction(company.getCompanyId());
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}
	}

	private User _getAdminUser(long companyId) throws Exception {
		List<User> users = _userLocalService.getUsersByRoleName(
			companyId, RoleConstants.ADMINISTRATOR, 0, 1);

		if (users.isEmpty()) {
			throw new NoSuchUserException(
				StringBundler.concat(
					"No user exists in company ", companyId, " with role ",
					RoleConstants.ADMINISTRATOR));
		}

		return users.get(0);
	}

	private void _verifyCommerceOrderNotificationTemplate(long companyId)
		throws Exception {

		com.liferay.notification.model.NotificationTemplate
			serviceBuilderNotificationTemplate =
				_notificationTemplateLocalService.
					fetchNotificationTemplateByExternalReferenceCode(
						"L_COMMERCE_ORDER_TEMPLATE", companyId);

		if ((_notificationType == null) ||
			(serviceBuilderNotificationTemplate != null)) {

			return;
		}

		Class<?> clazz = getClass();

		URL url = clazz.getResource("dependencies/notification-template.json");

		URLConnection urlConnection = url.openConnection();

		String json = StringUtil.read(urlConnection.getInputStream());

		if (Validator.isNull(json)) {
			return;
		}

		NotificationTemplate notificationTemplate = NotificationTemplate.toDTO(
			json);

		NotificationContext notificationContext =
			NotificationUtil.toNotificationContext(
				notificationTemplate, _objectFieldLocalService);

		notificationContext.setCompanyId(companyId);

		User user = _getAdminUser(companyId);

		notificationContext.setNotificationRecipient(
			NotificationUtil.toNotificationRecipient(user, 0L));
		notificationContext.setNotificationRecipientSettings(
			NotificationUtil.toNotificationRecipientSetting(
				0L,
				_notificationTypeServiceTracker.getNotificationType(
					notificationTemplate.getType()),
				notificationTemplate.getRecipients(), user));
		notificationContext.setNotificationTemplate(
			NotificationUtil.toNotificationTemplate(
				0L, notificationTemplate, _objectDefinitionLocalService, user));

		_notificationTemplateLocalService.addNotificationTemplate(
			notificationContext);
	}

	private void _verifyCommerceOrderObjectAction(long companyId)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_COMMERCE_ORDER", companyId);

		if (objectDefinition == null) {
			return;
		}

		ObjectAction objectAction = _objectActionLocalService.fetchObjectAction(
			"L_COMMERCE_ORDER_NOTIFICATION",
			objectDefinition.getObjectDefinitionId());

		if (objectAction != null) {
			return;
		}

		User user = _getAdminUser(companyId);

		_objectActionLocalService.addObjectAction(
			"L_COMMERCE_ORDER_NOTIFICATION", user.getUserId(),
			objectDefinition.getObjectDefinitionId(), false, "orderStatus == 1",
			StringPool.BLANK, null,
			Collections.singletonMap(
				LocaleUtil.getDefault(), "Commerce Order Notification"),
			"commerceOrderNotification", "notification",
			"liferay/commerce_order_status",
			UnicodePropertiesBuilder.create(
				true
			).put(
				"notificationTemplateExternalReferenceCode",
				"L_COMMERCE_ORDER_TEMPLATE"
			).put(
				"type", "email"
			).build(),
			false);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AddCommerceOrderNotificationPortalInstanceLifecycleListener.class);

	@Reference
	private NotificationTemplateLocalService _notificationTemplateLocalService;

	@Reference(
		target = "(component.name=com.liferay.notification.internal.type.EmailNotificationType)"
	)
	private NotificationType _notificationType;

	@Reference
	private NotificationTypeServiceTracker _notificationTypeServiceTracker;

	@Reference
	private ObjectActionLocalService _objectActionLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private UserLocalService _userLocalService;

}