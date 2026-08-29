/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.service;

import com.liferay.notification.context.NotificationContext;
import com.liferay.notification.model.NotificationTemplate;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.service.BaseService;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the remote service interface for NotificationTemplate. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Gabriel Albuquerque
 * @see NotificationTemplateServiceUtil
 * @generated
 */
@AccessControlled
@JSONWebService
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface NotificationTemplateService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.notification.service.impl.NotificationTemplateServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the notification template remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link NotificationTemplateServiceUtil} if injection and service tracking are not available.
	 */
	public NotificationTemplate addNotificationTemplate(
			NotificationContext notificationContext)
		throws PortalException;

	public NotificationTemplate deleteNotificationTemplate(
			long notificationTemplateId)
		throws PortalException;

	public NotificationTemplate deleteNotificationTemplate(
			NotificationTemplate notificationTemplate)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public NotificationTemplate
			fetchNotificationTemplateByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public NotificationTemplate getNotificationTemplate(
			long notificationTemplateId)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	public NotificationTemplate updateNotificationTemplate(
			NotificationContext notificationContext)
		throws PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1470619479