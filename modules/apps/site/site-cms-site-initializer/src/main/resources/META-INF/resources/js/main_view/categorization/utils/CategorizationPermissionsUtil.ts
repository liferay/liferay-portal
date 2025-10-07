/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export const DEFAULT_PERMISSIONS = [
	{
		actionIds: ['VIEW'],
		roleName: 'Guest',
	},
	{
		actionIds: ['VIEW'],
		roleName: 'Site Member',
	},
	{
		actionIds: ['DELETE', 'PERMISSIONS', 'UPDATE', 'VIEW'],
		roleName: 'Owner',
	},
];

export const DEFAULT_ROLES = [
	{
		label: Liferay.Language.get('guest'),
		name: 'Guest',
	},
	{
		label: Liferay.Language.get('site-member'),
		name: 'Site Member',
	},
];
