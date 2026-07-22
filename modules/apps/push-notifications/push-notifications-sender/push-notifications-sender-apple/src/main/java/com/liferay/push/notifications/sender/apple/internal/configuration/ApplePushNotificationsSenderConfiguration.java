/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.push.notifications.sender.apple.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Andrea Di Giorgi
 */
@ExtendedObjectClassDefinition(category = "notifications")
@Meta.OCD(
	id = "com.liferay.push.notifications.sender.apple.internal.configuration.ApplePushNotificationsSenderConfiguration",
	localization = "content/Language",
	name = "apple-push-notifications-sender-configuration-name"
)
public interface ApplePushNotificationsSenderConfiguration {

	@Meta.AD(
		description = "app-id-description", name = "app-id", required = false
	)
	public String appId();

	@Meta.AD(
		description = "certificate-password-description",
		name = "certificate-password-name", required = false,
		type = Meta.Type.Password
	)
	public String certificatePassword();

	@Meta.AD(
		description = "certificate-path-description",
		name = "certificate-path-name", required = false
	)
	public String certificatePath();

	@Meta.AD(
		description = "sandbox-description", name = "sandbox", required = false
	)
	public boolean sandbox();

}