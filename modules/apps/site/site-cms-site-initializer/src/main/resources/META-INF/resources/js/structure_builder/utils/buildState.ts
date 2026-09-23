/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectDefinition,
	ObjectDefinitions,
} from '../../common/types/ObjectDefinition';
import {DefaultLanguageLabels} from '../../common/utils/defaultLanguageLabels';
import {State} from '../contexts/StateContext';
import {SystemFieldNames} from '../types/SystemFieldNames';
import buildStructure from './buildStructure';
import {getChildrenUuids} from './getChildrenUuids';

export default function buildState({
	defaultLanguageLabels,
	mainObjectDefinition,
	objectDefinitions,
	systemFieldNames,
}: {
	defaultLanguageLabels: DefaultLanguageLabels;
	mainObjectDefinition: ObjectDefinition;
	objectDefinitions: ObjectDefinitions;
	systemFieldNames: SystemFieldNames;
}): State | null {
	if (!mainObjectDefinition) {
		return null;
	}

	const structure = buildStructure({
		mainObjectDefinition,
		objectDefinitions,
		systemFieldNames,
	});

	return {
		clipboard: null,
		defaultLanguageLabels,
		history: {
			deletedChildren: [],
			deletedGroupERCs: [],
			deletedRelationships: [],
			modifiedNames: new Set(),
			modifiedSlugs: new Set(),
		},
		invalids: new Map(),
		operation: null,
		publishedChildren:
			structure.status === 'published'
				? getChildrenUuids({root: structure})
				: new Set(),
		renamingItemUuid: null,
		savedChildren: getChildrenUuids({root: structure}),
		selection: [],
		structure,
		systemFieldNames,
		unsavedChanges: false,
	};
}
