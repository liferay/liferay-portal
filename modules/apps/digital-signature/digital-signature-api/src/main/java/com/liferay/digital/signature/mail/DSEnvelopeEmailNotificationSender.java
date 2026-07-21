/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.mail;

import com.liferay.digital.signature.model.DSRecipient;

/**
 * @author Danny Situ
 */
public interface DSEnvelopeEmailNotificationSender {

	public void sendNotification(
		long companyId, long groupId, String dsEnvelopeId,
		DSRecipient dsRecipient, String emailSubject, String emailMessage);

}