/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {createContext, useContext, useReducer} from 'react';
import {Elements} from 'react-flow-renderer';

import {
	LeftSidebarItem,
	ObjectRelationshipEdgeData,
	RightSidebarType,
	TAction,
	TState,
} from '../types';
import {getObjectFolderName} from '../utils';
import {ObjectFolderReducer} from './objectFolderReducer';

interface ObjectFolderContextProps extends Array<TState | Function> {
	0: typeof initialState;
	1: React.Dispatch<React.ReducerAction<React.Reducer<TState, TAction>>>;
}

interface ObjectFolderContextProviderProps
	extends React.HTMLAttributes<HTMLElement> {
	value: {};
}

const ObjectFolderContext = createContext({} as ObjectFolderContextProps);

const initialState = {
	countries: [] as TState['countries'],
	deletedObjectDefinition: {} as DeletedObjectDefinition,
	elements: [] as Elements<
		ObjectDefinitionNodeData | ObjectRelationshipEdgeData[]
	>,
	hasUnsavedObjectFolderItemPositions: false,
	isLoadingObjectFolder: false,
	leftSidebarItems: [] as LeftSidebarItem[],
	modelBuilderModals: {} as ModelBuilderModals,
	nodeHandleConnectable: false,
	objectFolderName: getObjectFolderName(),
	objectFolders: [] as ObjectFolder[],
	rightSidebarType: 'empty' as RightSidebarType,
	selectedObjectFolder: {} as ObjectFolder,
	showChangesSaved: false,
	showSidebars: true,
} as TState;

export function ObjectFolderContextProvider({
	children,
	value,
}: ObjectFolderContextProviderProps) {
	const [state, dispatch] = useReducer<React.Reducer<TState, TAction>>(
		ObjectFolderReducer,
		{
			...initialState,
			...value,
		}
	);

	return (
		<ObjectFolderContext.Provider value={[state, dispatch]}>
			{children}
		</ObjectFolderContext.Provider>
	);
}

export function useObjectFolderContext() {
	return useContext(ObjectFolderContext);
}
