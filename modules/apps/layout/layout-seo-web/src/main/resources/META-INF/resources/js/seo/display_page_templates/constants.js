/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export const FIELD_TYPES = {
	HTML: 'html',
	IMAGE: 'image',
	LONG_TEXT: 'long-text',
	TEXT: 'text',
};

export const TEXT_FIELD_TYPES = [
	FIELD_TYPES.HTML,
	FIELD_TYPES.LONG_TEXT,
	FIELD_TYPES.TEXT,
];

export const UNMAPPED_OPTION = {
	key: 'unmapped',
	label: `-- ${Liferay.Language.get('unmapped')} --`,
};
