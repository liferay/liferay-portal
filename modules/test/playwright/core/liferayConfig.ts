/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import * as dotenv from 'dotenv';

dotenv.config();
dotenv.config({override: true, path: '.env.local'});

const baseUrl = process.env.PORTAL_URL || 'http://localhost:8080';

const liferayConfig = {
	environment: {
		baseUrl,
		password: process.env.LIFERAY_USER_PASSWORD || 'test',
		port: new URL(baseUrl).port || '8080',
	},
};

export {liferayConfig};
