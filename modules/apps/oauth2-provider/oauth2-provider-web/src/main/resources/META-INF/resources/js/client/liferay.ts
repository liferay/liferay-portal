/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	FromParameters,
	OAuth2Client,
	getAuthorizeURL,
	getTokenURL,
} from './OAuth2Client';

window.Liferay = window.Liferay || {};

window.Liferay.OAuth2Client = {

	// Definitions are declared in liferay.d.ts
	// @ts-ignore

	FromParameters,

	// Definitions are declared in liferay.d.ts
	// @ts-ignore

	FromUserAgentApplication(
		userAgentApplicationName: string,
		debug?: boolean
	) {
		const userAgentApplication = Liferay.OAuth2.getUserAgentApplication(
			userAgentApplicationName
		);

		if (!userAgentApplication) {
			throw new Error(
				`No Application User Agent profile found for ${userAgentApplicationName}`
			);
		}

		return new OAuth2Client({
			authorizeURL: getAuthorizeURL(),
			clientId: userAgentApplication.clientId,
			debug,
			homePageURL: userAgentApplication.homePageURL,
			redirectURIs: userAgentApplication.redirectURIs,
			tokenURL: getTokenURL(),
		});
	},
};
