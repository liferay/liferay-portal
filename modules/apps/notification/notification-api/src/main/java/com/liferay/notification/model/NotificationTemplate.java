/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the NotificationTemplate service. Represents a row in the &quot;NotificationTemplate&quot; database table, with each column mapped to a property of this class.
 *
 * @author Gabriel Albuquerque
 * @see NotificationTemplateModel
 * @generated
 */
@ImplementationClassName(
	"com.liferay.notification.model.impl.NotificationTemplateImpl"
)
@ProviderType
public interface NotificationTemplate
	extends NotificationTemplateModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.notification.model.impl.NotificationTemplateImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<NotificationTemplate, Long>
		NOTIFICATION_TEMPLATE_ID_ACCESSOR =
			new Accessor<NotificationTemplate, Long>() {

				@Override
				public Long get(NotificationTemplate notificationTemplate) {
					return notificationTemplate.getNotificationTemplateId();
				}

				@Override
				public Class<Long> getAttributeClass() {
					return Long.class;
				}

				@Override
				public Class<NotificationTemplate> getTypeClass() {
					return NotificationTemplate.class;
				}

			};

	public NotificationRecipient fetchNotificationRecipient();

	public NotificationRecipient getNotificationRecipient()
		throws com.liferay.portal.kernel.exception.PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:995283146