/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {ReactNode} from 'react';

import {
	Action,
	State,
	StateContext,
} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/contexts/StateContext';
import {Structure} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/types/Structure';
import getUuid from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/getUuid';

export const DEFAULT_STRUCTURE: Structure = {
	children: new Map(),
	erc: 'default-erc',
	label: {
		[Liferay.ThemeDisplay.getDefaultLanguageId()]: 'untitled-structure',
	} as any,
	name: 'UntitledStructure',
	path: '',
	slug: '',
	spaces: [],
	status: 'new',
	system: false,
	type: 'L_CMS_CONTENT_STRUCTURES',
	uuid: getUuid(),
	workflows: {},
};

const DEFAULT_STATE: State = {
	clipboard: null,
	history: {
		deletedChildren: [],
		deletedGroupERCs: [],
		deletedRelationships: [],
		modifiedNames: new Set(),
		modifiedSlugs: new Set(),
	},
	invalids: new Map(),
	operation: null,
	publishedChildren: new Set(),
	renamingItemUuid: null,
	savedChildren: new Set(),
	selection: [],
	structure: DEFAULT_STRUCTURE,
	unsavedChanges: false,
};

export type MockState = Omit<Partial<State>, 'structure'> & {
	structure?: Partial<Structure>;
};

export function MockStateProvider({
	children,
	dispatch = jest.fn(),
	state = DEFAULT_STATE,
}: {
	children: ReactNode;
	dispatch?: React.Dispatch<Action>;
	state?: MockState;
}) {
	return (
		<StateContext.Provider
			value={{
				dispatch,
				state: {
					...DEFAULT_STATE,
					...state,
					structure: {...DEFAULT_STRUCTURE, ...state.structure},
				},
			}}
		>
			{children}
		</StateContext.Provider>
	);
}
