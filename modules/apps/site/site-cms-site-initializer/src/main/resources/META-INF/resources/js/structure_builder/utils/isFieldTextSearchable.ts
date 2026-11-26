/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Field} from './field';

export function isFieldTextSearchable(field: Field) {
	switch (field.type) {
		case 'email':
		case 'location':
		case 'long-text':
		case 'phone-number':
		case 'rich-text':
		case 'select-from-list':
		case 'text':
		case 'upload':
			return true;
		case 'boolean':
		case 'date':
		case 'datetime':
		case 'decimal':
		case 'integer':
			return false;
		default: {
			const exhaustiveCheck: never = field;
			throw new Error(`Unhandled field type case: ${exhaustiveCheck}`);
		}
	}
}
