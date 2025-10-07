/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {State} from '../contexts/StateContext';
import {ObjectDefinition, ObjectDefinitions} from '../types/ObjectDefinition';
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

	const structure = buildStructure({mainObjectDefinition, objectDefinitions});

	return {
		error: null,
		history: {
			deletedChildren: false,
		},
		invalids: new Map(),
		publishedChildren:
			structure.status === 'published'
				? getChildrenUuids({root: structure})
				: new Set(),
		selection: [],
		structure,
		unsavedChanges: false,
	};
}
