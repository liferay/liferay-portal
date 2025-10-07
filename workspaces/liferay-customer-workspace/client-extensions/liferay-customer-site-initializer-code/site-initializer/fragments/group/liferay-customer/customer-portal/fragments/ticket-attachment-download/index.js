/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const currentURL = window.location.href;
const identifier = currentURL.split('/#/')[1].split('/');
let redirectURL = '';

if (identifier[0] === 'id') {
	redirectURL = await Liferay.OAuth2Client.FromUserAgentApplication(
		'liferay-customer-etc-spring-boot-oaua'
	)
		.fetch(`/ticket-attachments/by-id/${identifier[1]}/download`)
		.then((response) => response.text());
}
else {
	redirectURL = await Liferay.OAuth2Client.FromUserAgentApplication(
		'liferay-customer-etc-spring-boot-oaua'
	)
		.fetch(
			`/ticket-attachments/by-external-reference-code/${identifier[1]}/download`
		)
		.then((response) => response.text());
}

window.open(redirectURL, '_blank');
