/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IView} from '../utils/types';
import {ISnapshot} from './ViewsContext';
import getViewComponent from './getViewComponent';

export enum EViewsActionTypes {
	ADD_OR_UPDATE_SNAPSHOT = 'ADD_OR_UPDATE_SNAPSHOT',
	BATCH_UPDATE = 'BATCH_UPDATE',
	DELETE_SNAPSHOT = 'DELETE_SNAPSHOT',
	RENAME_ACTIVE_SNAPSHOT = 'RENAME_ACTIVE_SNAPSHOT',
	RESET_TO_DEFAULT_SNAPSHOT = 'RESET_TO_DEFAULT_SNAPSHOT',
	UPDATE_ACTIVE_SNAPSHOT = 'UPDATE_ACTIVE_SNAPSHOT',
	UPDATE_ACTIVE_VIEW = 'UPDATE_ACTIVE_VIEW',
	UPDATE_FIELD = 'UPDATE_FIELD',
	UPDATE_FILTERS = 'UPDATE_FILTERS',
	UPDATE_PAGE_NUMBER = 'UPDATE_PAGE_NUMBER',
	UPDATE_PAGINATION_DELTA = 'UPDATE_PAGINATION_DELTA',
	UPDATE_SEARCH_PARAM = 'UPDATE_SEARCH_PARAM',
	UPDATE_SORTING = 'UPDATE_SORTING',
	UPDATE_VIEW_COMPONENT = 'UPDATE_VIEW_COMPONENT',
	UPDATE_VISIBLE_FIELD_NAMES = 'UPDATE_VISIBLE_FIELD_NAMES',
}

type TViewsActions = {
	[K in EViewsActionTypes]: (state: any, value: any) => object;
};

const viewsActions: TViewsActions = {
	[EViewsActionTypes.ADD_OR_UPDATE_SNAPSHOT]: (state, value) => {
		const {snapshots} = state;

		const {snapshotConfig, snapshotERC} = value;

		const existentSnapshot = snapshots.find(
			(snapshot: ISnapshot) => snapshot.snapshotERC === snapshotERC
		);

		let updatedSnapshots;

		if (!existentSnapshot) {
			updatedSnapshots = snapshots.concat([value]);
		}
		else {
			updatedSnapshots = snapshots.map((snapshot: ISnapshot) => {
				if (snapshot.snapshotERC === snapshotERC) {
					snapshot.snapshotConfig = snapshotConfig;
				}

				return snapshot;
			});
		}

		return {
			...state,
			activeSnapshotId: snapshotERC,
			snapshots: updatedSnapshots,
			viewUpdated: false,
		};
	},
	[EViewsActionTypes.BATCH_UPDATE]: (state, stateUpdates) => {
		if (!Array.isArray(stateUpdates) || !stateUpdates.length) {
			return state;
		}

		return stateUpdates.reduce((acc, current) => {
			const {type, value}: {type: keyof typeof viewsActions; value: any} =
				current;
			if (!viewsActions[type]) {
				return acc;
			}

			return viewsActions[type](acc, value);
		}, state);
	},
	[EViewsActionTypes.DELETE_SNAPSHOT]: (state, value) => {
		const {defaultView, snapshots} = state;

		const remainingSnapshots = snapshots.filter(
			(snapshot: ISnapshot) => snapshot.snapshotERC !== value.id
		);

		return {
			...state,
			...defaultView,
			activeSnapshotId: null,
			snapshots: remainingSnapshots,
			viewUpdated: false,
		};
	},
	[EViewsActionTypes.RENAME_ACTIVE_SNAPSHOT]: (state, value) => {
		const {activeSnapshotId, snapshots} = state;

		const updatedSnapshots = snapshots.map((snapshot: ISnapshot) => {
			if (snapshot.snapshotERC === activeSnapshotId) {
				snapshot.snapshotLabel = value.label;
			}

			return snapshot;
		});

		return {
			...state,
			snapshots: [...updatedSnapshots],
		};
	},
	[EViewsActionTypes.RESET_TO_DEFAULT_SNAPSHOT]: (state) => {
		const {defaultView} = state;

		return {
			...state,
			...defaultView,
			activeSnapshotId: null,
			viewUpdated: false,
		};
	},
	[EViewsActionTypes.UPDATE_ACTIVE_SNAPSHOT]: (state, value) => {
		const {defaultView, snapshots} = state;

		const activeSnapshot = snapshots.find(
			(view: ISnapshot) => view.snapshotERC === value
		);

		if (!activeSnapshot) {
			return state;
		}

		if (!activeSnapshot.snapshotConfig.activeView) {
			activeSnapshot.snapshotConfig.activeView = defaultView.activeView;
		}

		activeSnapshot.snapshotConfig.activeView.component =
			getViewComponent(activeSnapshot.snapshotConfig.activeView) ??
			getViewComponent(defaultView.activeView);

		return {
			...state,
			...activeSnapshot.snapshotConfig,
			activeSnapshotId: value,
			viewUpdated: false,
		};
	},
	[EViewsActionTypes.UPDATE_ACTIVE_VIEW]: (state, value) => {
		const {views} = state;

		const activeView = views.find(
			({name}: {name: string}) => name === value
		);

		if (activeView) {
			activeView.component = getViewComponent(activeView);
		}

		return {
			...state,
			activeView,
			viewUpdated: true,
		};
	},
	[EViewsActionTypes.UPDATE_FILTERS]: (state, value) => {
		return {
			...state,
			filters: value,
			viewUpdated: true,
		};
	},
	[EViewsActionTypes.UPDATE_FIELD]: (state, value) => {
		const {defaultView, modifiedFields} = state;

		const {name} = value;

		const fieldAttributes = modifiedFields[name] ?? {};

		if (!defaultView.modifiedFields[name]) {
			defaultView.modifiedFields[name] = {...fieldAttributes, ...value};
		}

		return {
			...state,
			modifiedFields: {
				...modifiedFields,
				[name]: {...fieldAttributes, ...value},
			},
		};
	},
	[EViewsActionTypes.UPDATE_PAGE_NUMBER]: (state, value) => {
		return {
			...state,
			pageNumber: value,
		};
	},
	[EViewsActionTypes.UPDATE_PAGINATION_DELTA]: (state, value) => {
		return {
			...state,
			paginationDelta: value,
			viewUpdated: true,
		};
	},
	[EViewsActionTypes.UPDATE_SEARCH_PARAM]: (state, value) => {
		return {
			...state,
			searchParam: value,
		};
	},
	[EViewsActionTypes.UPDATE_SORTING]: (state, value) => {
		return {
			...state,
			sorts: value,
			viewUpdated: true,
		};
	},

	[EViewsActionTypes.UPDATE_VIEW_COMPONENT]: (state, value) => {
		const {activeView, views} = state;

		const {component, name} = value;

		return {
			...state,
			activeView:
				name === activeView?.name
					? {
							...activeView,
							component,
						}
					: activeView,
			views: views.map((view: IView) =>
				view.name === name
					? {
							...view,
							component,
						}
					: view
			),
		};
	},
	[EViewsActionTypes.UPDATE_VISIBLE_FIELD_NAMES]: (state, value) => {
		const {modifiedFields} = state;

		const fieldNames = Object.keys(value);

		const fields: {[key: string]: any} = {};

		fieldNames.forEach((fieldName) => {
			const fieldAttributes = modifiedFields[fieldName] ?? {};

			fieldAttributes.visible = value[fieldName];
			fieldAttributes.width = null;

			fields[fieldName] = fieldAttributes;
		});

		return {
			...state,
			modifiedFields: fields,
			viewUpdated: true,
			visibleFieldNames: value,
		};
	},
};

const viewsReducer = (
	state: any,
	{type, value}: {type: keyof TViewsActions; value: any}
) => {
	if (viewsActions[type]) {
		return viewsActions[type](state, value);
	}

	return state;
};

export default viewsReducer;
