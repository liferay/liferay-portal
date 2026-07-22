/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.push.notifications.sender.sms.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Andrea Di Giorgi
 */
@ExtendedObjectClassDefinition(category = "notifications")
@Meta.OCD(
	id = "com.liferay.push.notifications.sender.sms.internal.configuration.SMSPushNotificationsSenderConfiguration",
	localization = "content/Language",
	name = "sms-push-notifications-sender-configuration-name"
)
public interface SMSPushNotificationsSenderConfiguration {

	@Meta.AD(name = "account.sid.name", required = false)
	public String accountSID();

	@Meta.AD(
		name = "authentication-token-name", required = false,
		type = Meta.Type.Password
	)
	public String authToken();

	@Meta.AD(name = "number", required = false)
	public String number();

	@Meta.AD(name = "status.callback.name", required = false)
	public String statusCallback();

}