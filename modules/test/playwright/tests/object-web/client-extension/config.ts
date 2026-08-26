/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export const config = {
	name: 'object-web.client-extension',
	retries: 0,
	testDir: 'tests/object-web/client-extension',
	use: {
		testIdAttribute: 'data-qa-id',
	},
};
