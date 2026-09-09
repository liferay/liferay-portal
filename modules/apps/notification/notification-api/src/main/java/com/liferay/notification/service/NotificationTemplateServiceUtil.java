/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.service;

import com.liferay.notification.model.NotificationTemplate;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;

/**
 * Provides the remote service utility for NotificationTemplate. This utility wraps
 * <code>com.liferay.notification.service.impl.NotificationTemplateServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Gabriel Albuquerque
 * @see NotificationTemplateService
 * @generated
 */
public class NotificationTemplateServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.notification.service.impl.NotificationTemplateServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static NotificationTemplate addNotificationTemplate(
			com.liferay.notification.context.NotificationContext
				notificationContext)
		throws PortalException {

		return getService().addNotificationTemplate(notificationContext);
	}

	public static NotificationTemplate deleteNotificationTemplate(
			long notificationTemplateId)
		throws PortalException {

		return getService().deleteNotificationTemplate(notificationTemplateId);
	}

	public static NotificationTemplate deleteNotificationTemplate(
			NotificationTemplate notificationTemplate)
		throws PortalException {

		return getService().deleteNotificationTemplate(notificationTemplate);
	}

	public static NotificationTemplate
			fetchNotificationTemplateByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().fetchNotificationTemplateByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	public static NotificationTemplate getNotificationTemplate(
			long notificationTemplateId)
		throws PortalException {

		return getService().getNotificationTemplate(notificationTemplateId);
	}

	public static NotificationTemplate
			getNotificationTemplateByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getNotificationTemplateByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static NotificationTemplate updateNotificationTemplate(
			com.liferay.notification.context.NotificationContext
				notificationContext)
		throws PortalException {

		return getService().updateNotificationTemplate(notificationContext);
	}

	public static NotificationTemplateService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<NotificationTemplateService>
		_serviceSnapshot = new Snapshot<>(
			NotificationTemplateServiceUtil.class,
			NotificationTemplateService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1989646015