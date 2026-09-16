/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Group, ReferencedStructure, RelatedContent} from '../types/Structure';
import {Field, FieldType} from './field';

export default function isField(item: {
	type?:
		| FieldType
		| ReferencedStructure['type']
		| RelatedContent['type']
		| Group['type'];
}): item is Field {
	return Boolean(
		item.type &&
			item.type !== 'referenced-structure' &&
			item.type !== 'related-content' &&
			item.type !== 'group'
	);
}
