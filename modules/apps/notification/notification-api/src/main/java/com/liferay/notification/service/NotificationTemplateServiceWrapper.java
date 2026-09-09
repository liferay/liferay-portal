/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link NotificationTemplateService}.
 *
 * @author Gabriel Albuquerque
 * @see NotificationTemplateService
 * @generated
 */
public class NotificationTemplateServiceWrapper
	implements NotificationTemplateService,
			   ServiceWrapper<NotificationTemplateService> {

	public NotificationTemplateServiceWrapper() {
		this(null);
	}

	public NotificationTemplateServiceWrapper(
		NotificationTemplateService notificationTemplateService) {

		_notificationTemplateService = notificationTemplateService;
	}

	@Override
	public com.liferay.notification.model.NotificationTemplate
			addNotificationTemplate(
				com.liferay.notification.context.NotificationContext
					notificationContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _notificationTemplateService.addNotificationTemplate(
			notificationContext);
	}

	@Override
	public com.liferay.notification.model.NotificationTemplate
			deleteNotificationTemplate(long notificationTemplateId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _notificationTemplateService.deleteNotificationTemplate(
			notificationTemplateId);
	}

	@Override
	public com.liferay.notification.model.NotificationTemplate
			deleteNotificationTemplate(
				com.liferay.notification.model.NotificationTemplate
					notificationTemplate)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _notificationTemplateService.deleteNotificationTemplate(
			notificationTemplate);
	}

	@Override
	public com.liferay.notification.model.NotificationTemplate
			fetchNotificationTemplateByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _notificationTemplateService.
			fetchNotificationTemplateByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public com.liferay.notification.model.NotificationTemplate
			getNotificationTemplate(long notificationTemplateId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _notificationTemplateService.getNotificationTemplate(
			notificationTemplateId);
	}

	@Override
	public com.liferay.notification.model.NotificationTemplate
			getNotificationTemplateByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _notificationTemplateService.
			getNotificationTemplateByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _notificationTemplateService.getOSGiServiceIdentifier();
	}

	@Override
	public com.liferay.notification.model.NotificationTemplate
			updateNotificationTemplate(
				com.liferay.notification.context.NotificationContext
					notificationContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _notificationTemplateService.updateNotificationTemplate(
			notificationContext);
	}

	@Override
	public NotificationTemplateService getWrappedService() {
		return _notificationTemplateService;
	}

	@Override
	public void setWrappedService(
		NotificationTemplateService notificationTemplateService) {

		_notificationTemplateService = notificationTemplateService;
	}

	private NotificationTemplateService _notificationTemplateService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1580272897