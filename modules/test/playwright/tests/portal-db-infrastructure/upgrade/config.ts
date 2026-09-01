/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export const config = {
	name: 'portal-db-infrastructure.upgrade',
	testDir: 'tests/portal-db-infrastructure/upgrade',
	timeout: 480 * 1000,
	use: {
		testIdAttribute: 'data-qa-id',
	},
};
