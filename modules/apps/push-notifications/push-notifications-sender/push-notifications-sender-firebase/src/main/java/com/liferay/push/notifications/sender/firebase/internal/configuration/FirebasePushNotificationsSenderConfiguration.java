/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.push.notifications.sender.firebase.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedAttributeDefinition;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Bruno Farache
 */
@ExtendedObjectClassDefinition(category = "notifications")
@Meta.OCD(
	id = "com.liferay.push.notifications.sender.firebase.internal.configuration.FirebasePushNotificationsSenderConfiguration",
	localization = "content/Language",
	name = "firebase-push-notifications-sender-configuration-name"
)
public interface FirebasePushNotificationsSenderConfiguration {

	@Meta.AD(
		deflt = "https://fcm.googleapis.com",
		name = "firebase-cloud-messaging-url", required = false
	)
	public String firebaseCloudMessagingURL();

	@ExtendedAttributeDefinition(
		descriptionArguments = "https://firebase.google.com/docs/projects/learn-more#project-number"
	)
	@Meta.AD(
		description = "project-number-help", name = "project-number",
		required = false
	)
	public String projectNumber();

	@ExtendedAttributeDefinition(
		descriptionArguments = "https://cloud.google.com/iam/docs/creating-managing-service-account-keys"
	)
	@Meta.AD(
		description = "service-account-key-help", name = "service-account-key",
		required = false, type = Meta.Type.Password
	)
	public String serviceAccountKey();

}