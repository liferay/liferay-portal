/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.constants;

/**
 * @author Carolina Barbosa
 */
public class NotificationRecipientSettingConstants {

	public static final String NAME_BCC = "bcc";

	public static final String NAME_BCC_TYPE = "bccType";

	public static final String NAME_CC = "cc";

	public static final String NAME_CC_TYPE = "ccType";

	public static final String NAME_FROM = "from";

	public static final String NAME_FROM_NAME = "fromName";

	public static final String NAME_ROLE_NAME = "roleName";

	public static final String NAME_SINGLE_RECIPIENT = "singleRecipient";

	public static final String NAME_TERM = "term";

	public static final String NAME_TO = "to";

	public static final String NAME_TO_TYPE = "toType";

	public static final String NAME_USER_GROUP_NAME = "userGroupName";

	public static final String NAME_USER_SCREEN_NAME = "userScreenName";

	public static String getRecipientTypeName(String recipientName) {
		if (recipientName.equals(
				NotificationRecipientSettingConstants.NAME_BCC)) {

			return NotificationRecipientSettingConstants.NAME_BCC_TYPE;
		}
		else if (recipientName.equals(
					NotificationRecipientSettingConstants.NAME_CC)) {

			return NotificationRecipientSettingConstants.NAME_CC_TYPE;
		}
		else if (recipientName.equals(
					NotificationRecipientSettingConstants.NAME_TO)) {

			return NotificationRecipientSettingConstants.NAME_TO_TYPE;
		}

		return null;
	}

}