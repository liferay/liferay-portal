/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.service.impl;

import com.liferay.notification.constants.NotificationActionKeys;
import com.liferay.notification.constants.NotificationConstants;
import com.liferay.notification.context.NotificationContext;
import com.liferay.notification.model.NotificationTemplate;
import com.liferay.notification.service.base.NotificationTemplateServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gabriel Albuquerque
 * @author Gustavo Lima
 */
@Component(
	property = {
		"json.web.service.context.name=notification",
		"json.web.service.context.path=NotificationTemplate"
	},
	service = AopService.class
)
public class NotificationTemplateServiceImpl
	extends NotificationTemplateServiceBaseImpl {

	@Override
	public NotificationTemplate addNotificationTemplate(
			NotificationContext notificationContext)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), null,
			NotificationActionKeys.ADD_NOTIFICATION_TEMPLATE);

		return notificationTemplateLocalService.addNotificationTemplate(
			notificationContext);
	}

	@Override
	public NotificationTemplate deleteNotificationTemplate(
			long notificationTemplateId)
		throws PortalException {

		_notificationTemplateModelResourcePermission.check(
			getPermissionChecker(), notificationTemplateId, ActionKeys.DELETE);

		return notificationTemplateLocalService.deleteNotificationTemplate(
			notificationTemplateId);
	}

	@Override
	public NotificationTemplate deleteNotificationTemplate(
			NotificationTemplate notificationTemplate)
		throws PortalException {

		_notificationTemplateModelResourcePermission.check(
			getPermissionChecker(),
			notificationTemplate.getNotificationTemplateId(),
			ActionKeys.DELETE);

		return notificationTemplateLocalService.deleteNotificationTemplate(
			notificationTemplate);
	}

	@Override
	public NotificationTemplate
			fetchNotificationTemplateByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		NotificationTemplate notificationTemplate =
			notificationTemplateLocalService.
				fetchNotificationTemplateByExternalReferenceCode(
					externalReferenceCode, companyId);

		if (notificationTemplate != null) {
			_notificationTemplateModelResourcePermission.check(
				getPermissionChecker(), notificationTemplate, ActionKeys.VIEW);
		}

		return notificationTemplate;
	}

	@Override
	public NotificationTemplate getNotificationTemplate(
			long notificationTemplateId)
		throws PortalException {

		_notificationTemplateModelResourcePermission.check(
			getPermissionChecker(), notificationTemplateId, ActionKeys.VIEW);

		return notificationTemplateLocalService.getNotificationTemplate(
			notificationTemplateId);
	}

	@Override
	public NotificationTemplate updateNotificationTemplate(
			NotificationContext notificationContext)
		throws PortalException {

		NotificationTemplate notificationTemplate =
			notificationContext.getNotificationTemplate();

		_notificationTemplateModelResourcePermission.check(
			getPermissionChecker(),
			notificationTemplate.getNotificationTemplateId(),
			ActionKeys.UPDATE);

		return notificationTemplateLocalService.updateNotificationTemplate(
			notificationContext);
	}

	@Reference(
		target = "(model.class.name=com.liferay.notification.model.NotificationTemplate)"
	)
	private ModelResourcePermission<NotificationTemplate>
		_notificationTemplateModelResourcePermission;

	@Reference(
		target = "(resource.name=" + NotificationConstants.RESOURCE_NAME_NOTIFICATION_TEMPLATE + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}