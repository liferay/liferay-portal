/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const liferayConfig = {
	environment: {
		baseUrl: process.env.PORTAL_URL
			? process.env.PORTAL_URL
			: 'http://localhost:8080',
		password: process.env.LIFERAY_USER_PASSWORD
			? process.env.LIFERAY_USER_PASSWORD
			: 'Test',
	},
};

export {liferayConfig};
