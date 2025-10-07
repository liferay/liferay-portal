/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IView} from '../utils/types';
import getViewComponent from './getViewComponent';

export enum EViewsActionTypes {
	ADD_OR_UPDATE_CUSTOM_VIEW = 'ADD_OR_UPDATE_CUSTOM_VIEW',
	BATCH_UPDATE = 'BATCH_UPDATE',
	DELETE_CUSTOM_VIEW = 'DELETE_CUSTOM_VIEW',
	RENAME_ACTIVE_CUSTOM_VIEW = 'RENAME_ACTIVE_CUSTOM_VIEW',
	RESET_TO_DEFAULT_VIEW = 'RESET_TO_DEFAULT_VIEW',
	UPDATE_ACTIVE_CUSTOM_VIEW = 'UPDATE_ACTIVE_CUSTOM_VIEW',
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
	[EViewsActionTypes.ADD_OR_UPDATE_CUSTOM_VIEW]: (state, value) => {
		const {customViews} = state;

		const {id, viewState} = value;

		return {
			...state,
			activeCustomViewId: id,
			customViews: {
				...customViews,
				[id]: viewState,
			},
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
	[EViewsActionTypes.DELETE_CUSTOM_VIEW]: (state, value) => {
		const {customViews, defaultView} = state;

		const {[value.id]: _unusedVar, ...remainingCustomViews} = customViews;

		return {
			...state,
			...defaultView,
			activeCustomViewId: null,
			customViews: remainingCustomViews,
			viewUpdated: false,
		};
	},
	[EViewsActionTypes.RENAME_ACTIVE_CUSTOM_VIEW]: (state, value) => {
		const {activeCustomViewId, customViews} = state;

		const customView = customViews[activeCustomViewId];

		customView.customViewLabel = value.label;

		return {
			...state,
			customViews: {
				...customViews,
				[activeCustomViewId]: customView,
			},
		};
	},
	[EViewsActionTypes.RESET_TO_DEFAULT_VIEW]: (state) => {
		const {defaultView} = state;

		return {
			...state,
			...defaultView,
			activeCustomViewId: null,
			modifiedFields: {},
			viewUpdated: false,
		};
	},
	[EViewsActionTypes.UPDATE_ACTIVE_CUSTOM_VIEW]: (state, value) => {
		const {customViews, defaultView} = state;

		const activeCustomView = customViews[value];

		if (!activeCustomView) {
			return state;
		}

		if (!activeCustomView.activeView) {
			activeCustomView.activeView = defaultView.activeView;
		}

		activeCustomView.activeView.component =
			getViewComponent(activeCustomView.activeView) ??
			getViewComponent(defaultView.activeView);

		return {
			...state,
			...activeCustomView,
			activeCustomViewId: value,
			modifiedFields: {},
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
		const {modifiedFields} = state;

		const {name} = value;

		const fieldAttributes = modifiedFields[name] ?? {};

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
			viewUpdated: false,
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
			viewUpdated: false,
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
