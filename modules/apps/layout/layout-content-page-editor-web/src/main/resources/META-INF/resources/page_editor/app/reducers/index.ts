/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import baseReducer from './baseReducer';
import collectionsReducer from './collectionsReducer';
import draftReducer from './draftReducer';
import fragmentEntryLinksReducer from './fragmentEntryLinksReducer';
import fragmentsReducer from './fragmentsReducer';
import languageReducer from './languageReducer';
import layoutDataReducer from './layoutDataReducer';
import mappingFieldsReducer from './mappingFieldsReducer';
import masterLayoutReducer from './masterLayoutReducer';
import networkReducer from './networkReducer';
import permissionsReducer from './permissionsReducer';
import restrictedItemIdsReducer from './restrictedItemIdsReducer';
import selectedViewportSizeReducer from './selectedViewportSizeReducer';
import showResolvedCommentsReducer from './showResolvedCommentsReducer';
import sidebarReducer from './sidebarReducer';

// Temporary disable undoReducer types until we have defined an state store
// type and migrated all thunks to ts.
// @ts-ignore

import undoReducer from './undoReducer';
import widgetsReducer from './widgetsReducer';

const REDUCER_MAP = {
	collections: collectionsReducer,
	draft: draftReducer,
	fragmentEntryLinks: fragmentEntryLinksReducer,
	fragments: fragmentsReducer,
	languageId: languageReducer,
	layoutData: layoutDataReducer,
	mappingFields: mappingFieldsReducer,
	masterLayout: masterLayoutReducer,
	network: networkReducer,
	permissions: permissionsReducer,
	reducers: baseReducer,
	restrictedItemIds: restrictedItemIdsReducer,
	selectedViewportSize: selectedViewportSizeReducer,
	showResolvedComments: showResolvedCommentsReducer,
	sidebar: sidebarReducer,
	widgets: widgetsReducer,
} as const;

export type Action = Parameters<
	(typeof REDUCER_MAP)[keyof typeof REDUCER_MAP]
>[1];

export type State = {
	[key in keyof typeof REDUCER_MAP]: ReturnType<(typeof REDUCER_MAP)[key]>;
} & {
	segmentsExperienceId: string;
	segmentsExperimentStatus?: {label: string; value: string};
};

const combinedReducer = (state: State, action: Action): State =>
	Object.entries(REDUCER_MAP).reduce(
		(nextState, [namespace, reducer]) => ({
			...nextState,
			[namespace]: (reducer as any)(
				nextState[namespace as keyof typeof nextState],
				action
			),
		}),
		state
	);

/**
 * Runs the base reducer plus any dynamically loaded reducers that have
 * been registered from plugins.
 */
export function reducer(state: State, action: Action): State {
	const nextState = undoReducer(state, action);

	return [combinedReducer, ...Object.values(state.reducers || {})].reduce(
		(nextState, nextReducer) => {
			return nextReducer(nextState, action);
		},
		nextState
	);
}
