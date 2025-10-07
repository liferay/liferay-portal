/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectDefinition} from '../types/ObjectDefinition';
import {RepeatableGroup, Structure} from '../types/Structure';
import buildObjectDefinition from './buildObjectDefinition';
export default function buildGroupObjectDefinitions({
	children,
	objectDefinitions = [],
}: {
	children: (RepeatableGroup | Structure)['children'];
	objectDefinitions?: ObjectDefinition[];
}) {
	let definitions: ObjectDefinition[] = [...objectDefinitions];

	for (const child of children.values()) {
		if (child.type !== 'repeatable-group') {
			continue;
		}

		const objectDefinition = buildObjectDefinition({
			children: child.children,
			erc: child.erc,
			label: child.label,
			name: child.name,
			spaces: 'all',
			status: 'published',
		});

		objectDefinition.enableIndexSearch = false;

		objectDefinition.objectFolderExternalReferenceCode =
			'L_CMS_STRUCTURE_REPEATABLE_GROUPS';

		definitions = [
			...buildGroupObjectDefinitions({
				children: child.children,
			}),
			objectDefinition,
			...definitions,
		];
	}

	return definitions;
}
