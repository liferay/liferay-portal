/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {ReactNode} from 'react';

import PicklistService from '../../../../src/main/resources/META-INF/resources/js/common/services/PicklistService';
import SpaceService from '../../../../src/main/resources/META-INF/resources/js/common/services/SpaceService';
import {getWorkflowDefinitions} from '../../../../src/main/resources/META-INF/resources/js/common/services/WorkflowService';
import {Picklist} from '../../../../src/main/resources/META-INF/resources/js/common/types/Picklist';
import {Space} from '../../../../src/main/resources/META-INF/resources/js/common/types/Space';
import {Workflow} from '../../../../src/main/resources/META-INF/resources/js/common/types/Workflow';
import {CacheContext} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/contexts/CacheContext';
import ObjectDefinitionService from '../../../../src/main/resources/META-INF/resources/js/structure_builder/services/ObjectDefinitionService';
import {ObjectDefinitions} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/types/ObjectDefinition';

function getCache({
	objectDefinitions,
	picklists,
	spaces,
	workflows,
}: {
	objectDefinitions?: ObjectDefinitions;
	picklists?: Picklist[];
	spaces?: Space[];
	workflows?: Workflow[];
}) {
	return {
		'object-definitions': {
			data: objectDefinitions || {},
			fetcher: ObjectDefinitionService.getObjectDefinitions,
			status: objectDefinitions ? ('saved' as const) : ('idle' as const),
		},
		'picklists': {
			data: picklists || [],
			fetcher: PicklistService.getPicklists,
			status: picklists ? ('saved' as const) : ('idle' as const),
		},
		'spaces': {
			data: spaces || [],
			fetcher: SpaceService.getSpaces,
			status: spaces ? ('saved' as const) : ('idle' as const),
		},
		'workflows': {
			data: workflows || [],
			fetcher: getWorkflowDefinitions,
			status: spaces ? ('saved' as const) : ('idle' as const),
		},
	};
}

export function MockCacheProvider({
	children,
	objectDefinitions,
	picklists,
	spaces,
}: {
	children: ReactNode;
	objectDefinitions?: ObjectDefinitions;
	picklists?: Picklist[];
	spaces?: Space[];
}) {
	return (
		<CacheContext.Provider
			value={{
				cache: getCache({
					objectDefinitions,
					picklists,
					spaces,
				}),
				promisesRef: {current: {}},
				update: () => {},
			}}
		>
			{children}
		</CacheContext.Provider>
	);
}
