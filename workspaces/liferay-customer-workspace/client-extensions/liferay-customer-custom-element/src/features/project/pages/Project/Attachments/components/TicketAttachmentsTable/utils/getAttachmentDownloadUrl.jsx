/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export default async function getAttachmentDownloadUrl(
	ticketAttachmentId
) {
	return await Liferay.OAuth2Client.FromUserAgentApplication(
		'liferay-customer-etc-spring-boot-oaua'
	).fetch(
		"/ticket-attachments/by-id/" + ticketAttachmentId + "/download"
	).then(
		(response) => response.text()
	);
}
