/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectDefinition,
	ObjectDefinitions,
} from '../../common/types/ObjectDefinition';
import {State} from '../contexts/StateContext';
import buildStructure from './buildStructure';
import {getChildrenUuids} from './getChildrenUuids';

export default function buildState({
	mainObjectDefinition,
	objectDefinitions,
}: {
	mainObjectDefinition: ObjectDefinition;
	objectDefinitions: ObjectDefinitions;
}): State | null {
	if (!mainObjectDefinition) {
		return null;
	}

	const structure = buildStructure({
		mainObjectDefinition,
		objectDefinitions,
	});

	return {
		clipboard: null,
		history: {
			deletedChildren: [],
			deletedGroupERCs: [],
			deletedRelationships: [],
			modifiedNames: new Set(),
			modifiedSlugs: new Set(),
		},
		invalids: new Map(),
		publishedChildren:
			structure.status === 'published'
				? getChildrenUuids({root: structure})
				: new Set(),
		renamingItemUuid: null,
		selection: [],
		structure,
		unsavedChanges: false,
	};
}
