/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectDefinition} from '../../common/types/ObjectDefinition';
import {State} from '../contexts/StateContext';
import {RepeatableGroup, Structure} from '../types/Structure';
import buildObjectDefinition from './buildObjectDefinition';
import isRepeatableGroup from './isRepeatableGroup';

export default function buildGroupObjectDefinitions({
	children,
	objectDefinitions = [],
	publishedChildren,
}: {
	children: (RepeatableGroup | Structure)['children'];
	objectDefinitions?: ObjectDefinition[];
	publishedChildren: State['publishedChildren'];
}) {
	let definitions: ObjectDefinition[] = [...objectDefinitions];

	for (const child of children.values()) {
		if (!isRepeatableGroup(child)) {
			continue;
		}

		// A repeatable group becomes a root descendant node only once it has
		// been published, and from then on the allowStandaloneObjectEntry
		// setting is required. Its entries always live under their parent, so
		// the value is always false. Sending it before the group is published
		// is rejected because the definition is not yet a root descendant.

		const objectDefinition = buildObjectDefinition({
			children: child.children,
			erc: child.erc,
			label: child.label,
			name: child.name,
			settings: publishedChildren.has(child.uuid)
				? {allowStandaloneObjectEntry: 'false'}
				: undefined,
			spaces: 'all',
			status: 'published',
		});

		objectDefinition.enableIndexSearch = false;

		objectDefinition.objectFolderExternalReferenceCode =
			'L_CMS_STRUCTURE_REPEATABLE_GROUPS';

		definitions = [
			...buildGroupObjectDefinitions({
				children: child.children,
				publishedChildren,
			}),
			objectDefinition,
			...definitions,
		];
	}

	return definitions;
}
