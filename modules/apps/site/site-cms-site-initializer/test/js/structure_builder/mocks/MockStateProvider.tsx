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

const DEFAULT_STRUCTURE: Structure = {
	children: new Map(),
	erc: 'default-erc',
	label: 'untitled-structure' as any,
	name: 'UntitledStructure',
	spaces: [],
	status: 'new',
	uuid: getUuid(),
	workflows: {},
};

const DEFAULT_STATE: State = {
	error: null,
	history: {deletedChildren: false},
	invalids: new Map(),
	publishedChildren: new Set(),
	selection: [],
	structure: DEFAULT_STRUCTURE,
	unsavedChanges: false,
};

export type MockState = Omit<Partial<State>, 'structure'> & {
	structure?: Partial<State['structure']>;
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
