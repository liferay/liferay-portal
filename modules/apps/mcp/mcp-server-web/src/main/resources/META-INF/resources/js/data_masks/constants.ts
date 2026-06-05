/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export const API_BASE = '/o/mcp/server-data-masks';

export const PROFILE_DATA_MASKS_API = '/o/mcp/server-profile-data-masks';

export const PROFILES_API = '/o/mcp/server-profiles';

export const VALIDATE_API = '/o/headless-data-masking/v1.0/data-masks/validate';

export const DATA_MASK_FK_FIELD =
	'r_dataMaskToProfileDataMasks_mcpServerDataMaskId';

export const DATA_SET_ID = 'com.liferay.mcp.server.web.data-masks';

export const FILTERS = [
	{
		entityFieldType: 'string',
		id: 'maskType/key',
		itemKey: 'value',
		itemLabel: 'label',
		items: [
			{label: Liferay.Language.get('system'), value: 'system'},
			{label: Liferay.Language.get('custom'), value: 'custom'},
		],
		label: Liferay.Language.get('type'),
		multiple: true,
		type: 'selection',
	},
];

export const SORTS = [
	{
		direction: 'asc' as const,
		key: 'name',
		label: Liferay.Language.get('title'),
	},
	{
		direction: 'desc' as const,
		key: 'dateModified',
		label: Liferay.Language.get('last-modified'),
	},
];

export const VIEWS = [
	{
		contentRenderer: 'table',
		default: true,
		label: Liferay.Language.get('table'),
		name: 'table',
		schema: {
			fields: [
				{
					contentRenderer: 'nameLink',
					expand: true,
					fieldName: 'name',
					label: Liferay.Language.get('title'),
					sortable: true,
				},
				{
					fieldName: 'maskType.name',
					label: Liferay.Language.get('type'),
				},
				{
					fieldName: 'description',
					label: Liferay.Language.get('description'),
				},
				{
					contentRenderer: 'dateTime',
					fieldName: 'dateModified',
					label: Liferay.Language.get('last-modified'),
					sortable: true,
				},
			],
		},
		thumbnail: 'table',
	},
];
