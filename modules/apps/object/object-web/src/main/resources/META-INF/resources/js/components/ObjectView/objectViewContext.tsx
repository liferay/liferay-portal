/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {stringUtils} from '@liferay/object-js-components-web';
import React, {createContext, useContext, useReducer} from 'react';

import {defaultLanguageId} from '../../utils/constants';
import {
	TObjectView,
	TObjectViewColumn,
	TObjectViewFilterColumn,
	TObjectViewSortColumn,
	TState,
	TWorkflowStatus,
} from './types';

interface IViewContextProps extends Array<TState | Function> {
	0: typeof initialState;
	1: React.Dispatch<React.ReducerAction<React.Reducer<TState, TAction>>>;
}

interface TInitialFilterColumn extends TObjectViewFilterColumn {
	json: string;
	valueSummary: string;
}

const ViewContext = createContext({} as IViewContextProps);

export enum TYPES {
	ADD_OBJECT_FIELDS = 'ADD_OBJECT_FIELDS',
	ADD_OBJECT_VIEW = 'ADD_OBJECT_VIEW',
	ADD_OBJECT_CUSTOM_VIEW_FIELD = 'ADD_OBJECT_CUSTOM_VIEW_FIELD',
	ADD_OBJECT_VIEW_COLUMN = 'ADD_OBJECT_VIEW_COLUMN',
	ADD_OBJECT_VIEW_SORT_COLUMN = 'ADD_OBJECT_VIEW_SORT_COLUMN',
	ADD_OBJECT_VIEW_FILTER_COLUMN = 'ADD_OBJECT_VIEW_FILTER_COLUMN',
	CHANGE_OBJECT_VIEW_NAME = 'CHANGE_OBJECT_VIEW_NAME',
	CHANGE_OBJECT_VIEW_COLUMN_ORDER = 'CHANGE_OBJECT_VIEW_COLUMN_ORDER',
	CHANGE_OBJECT_VIEW_SORT_COLUMN_ORDER = 'CHANGE_OBJECT_VIEW_SORT_COLUMN_ORDER',
	DELETE_OBJECT_VIEW_COLUMN = 'DELETE_OBJECT_VIEW_COLUMN',
	DELETE_OBJECT_VIEW_SORT_COLUMN = 'DELETE_OBJECT_VIEW_SORT_COLUMN',
	DELETE_OBJECT_VIEW_FILTER_COLUMN = 'DELETE_OBJECT_VIEW_FILTER_COLUMN',
	DELETE_OBJECT_CUSTOM_VIEW_FIELD = 'DELETE_OBJECT_CUSTOM_VIEW_FIELD',
	EDIT_OBJECT_VIEW_COLUMN_LABEL = 'EDIT_OBJECT_VIEW_COLUMN_LABEL',
	EDIT_OBJECT_VIEW_FILTER_COLUMN = 'EDIT_OBJECT_VIEW_FILTER_COLUMN',
	EDIT_OBJECT_VIEW_SORT_COLUMN_SORT_ORDER = 'EDIT_OBJECT_VIEW_SORT_COLUMN_SORT_ORDER',
	SET_OBJECT_VIEW_AS_DEFAULT = 'SET_OBJECT_VIEW_AS_DEFAULT',
}

const initialState = {
	objectFields: [] as ObjectField[],
	objectView: {} as TObjectView,
} as TState;

const handleChangeColumnOrder = (
	draggedIndex: number,
	targetIndex: number,
	columns: TObjectViewSortColumn[]
) => {
	const dragged = columns[draggedIndex];

	columns.splice(draggedIndex, 1);
	columns.splice(targetIndex, 0, dragged);

	const newColumn = columns.map((sortColumn, index) => {
		return {
			...sortColumn,
			priority: index,
		};
	});

	return newColumn;
};

export type TAction =
	| {
			payload: {
				creationLanguageId: Liferay.Language.Locale;
				objectFields: ObjectField[];
				objectView: TObjectView;
			};
			type: TYPES.ADD_OBJECT_VIEW;
	  }
	| {
			payload: {
				creationLanguageId: Liferay.Language.Locale;
				selectedObjectFields: ObjectField[];
			};
			type: TYPES.ADD_OBJECT_VIEW_COLUMN;
	  }
	| {
			payload: {
				creationLanguageId: Liferay.Language.Locale;
				filterType?: string;
				objectFieldName: string;
				valueList?: IItem[];
			};
			type: TYPES.ADD_OBJECT_VIEW_FILTER_COLUMN;
	  }
	| {
			payload: {
				creationLanguageId: Liferay.Language.Locale;
				objectFieldName: string;
				objectFields: ObjectField[];
				objectViewSortColumns?: TObjectViewSortColumn[];
				selectedObjetSortValue: string;
			};
			type: TYPES.ADD_OBJECT_VIEW_SORT_COLUMN;
	  }
	| {
			payload: {
				newName: string;
			};
			type: TYPES.CHANGE_OBJECT_VIEW_NAME;
	  }
	| {
			payload: {
				draggedIndex: number;
				targetIndex: number;
			};
			type: TYPES.CHANGE_OBJECT_VIEW_COLUMN_ORDER;
	  }
	| {
			payload: {
				draggedIndex: number;
				targetIndex: number;
			};
			type: TYPES.CHANGE_OBJECT_VIEW_SORT_COLUMN_ORDER;
	  }
	| {
			payload: {
				objectFieldName?: string;
			};
			type: TYPES.DELETE_OBJECT_VIEW_COLUMN;
	  }
	| {
			payload: {
				objectFieldName?: string;
			};
			type: TYPES.DELETE_OBJECT_VIEW_FILTER_COLUMN;
	  }
	| {
			payload: {
				objectFieldName?: string;
			};
			type: TYPES.DELETE_OBJECT_VIEW_SORT_COLUMN;
	  }
	| {
			payload: {
				editingObjectFieldName: string;
				translations: LocalizedValue<string>;
			};
			type: TYPES.EDIT_OBJECT_VIEW_COLUMN_LABEL;
	  }
	| {
			payload: {
				filterType?: string;
				objectFieldName?: string;
				valueList?: IItem[];
			};
			type: TYPES.EDIT_OBJECT_VIEW_FILTER_COLUMN;
	  }
	| {
			payload: {
				editingObjectFieldName: string;
				selectedObjectSort: string;
			};
			type: TYPES.EDIT_OBJECT_VIEW_SORT_COLUMN_SORT_ORDER;
	  }
	| {
			payload: {
				checked: boolean;
			};
			type: TYPES.SET_OBJECT_VIEW_AS_DEFAULT;
	  };

export function viewReducer(state: TState, action: TAction) {
	switch (action.type) {
		case TYPES.ADD_OBJECT_VIEW: {
			const {creationLanguageId, objectFields, objectView} =
				action.payload;

			const {
				objectViewColumns,
				objectViewFilterColumns,
				objectViewSortColumns,
			} = objectView;

			const objectFieldsWithCheck = objectFields.map(
				(field: ObjectField) => {
					return {
						...field,
						checked: false,
						filtered: true,
					};
				}
			);

			const newObjectFields: ObjectFieldView[] = [];

			objectFieldsWithCheck.map((field: ObjectField) => {
				newObjectFields.push(field);
			});

			newObjectFields.forEach((field) => {
				objectViewColumns.forEach(
					(column: {objectFieldName?: string}) => {
						if (column.objectFieldName === field.name) {
							field.checked = true;
						}
					}
				);

				const existingFilter = objectViewFilterColumns.find(
					(filter: {objectFieldName?: string}) => {
						if (filter.objectFieldName === field.name) {
							return filter;
						}
					}
				);

				field.hasFilter = !!existingFilter;
			});

			const newObjectViewColumns: TObjectViewColumn[] = [];
			const newObjectViewSortColumns: TObjectViewSortColumn[] = [];

			objectViewColumns.forEach((viewColumn: TObjectViewColumn) => {
				newObjectFields.forEach((objectField: ObjectField) => {
					if (objectField.name === viewColumn.objectFieldName) {
						newObjectViewColumns.push({
							...viewColumn,
							defaultSort: false,
							fieldLabel: stringUtils.getLocalizableLabel({
								fallbackLabel: objectField.name,
								fallbackLanguageId: creationLanguageId,
								labels: objectField.label,
							}),
							label: viewColumn.label,
							objectFieldBusinessType: objectField.businessType,
						});
					}
				});
			});

			objectViewSortColumns.forEach(
				(sortColumn: TObjectViewSortColumn) => {
					newObjectFields.forEach((objectField: ObjectField) => {
						if (objectField.name === sortColumn.objectFieldName) {
							newObjectViewSortColumns.push({
								...sortColumn,
								fieldLabel: stringUtils.getLocalizableLabel({
									fallbackLabel: objectField.name,
									fallbackLanguageId: creationLanguageId,
									labels: objectField.label,
								}),
							});
						}
					});
				}
			);

			newObjectViewSortColumns.forEach(
				(sortColumn: TObjectViewSortColumn) => {
					newObjectViewColumns.forEach(
						(viewColumn: TObjectViewColumn) => {
							if (
								sortColumn.objectFieldName ===
								viewColumn.objectFieldName
							) {
								viewColumn.defaultSort = true;
							}
						}
					);
				}
			);

			const newObjectViewFilterColumns = (
				objectViewFilterColumns as TInitialFilterColumn[]
			).map((filterColumn) => {
				const definition = filterColumn.json
					? JSON.parse(filterColumn.json)
					: null;
				const filterType = filterColumn.filterType;
				const objectFieldName = filterColumn.objectFieldName;
				const objectField = newObjectFields.find(
					(field: ObjectField) => {
						if (field.name === objectFieldName) {
							return field;
						}
					}
				);
				const valueList = [];
				let valueSummary = filterColumn.valueSummary?.split(',');

				valueSummary = valueSummary?.map((item) => item.trim());

				if (valueSummary && filterType) {
					for (let i = 0; i < definition[filterType].length; i++) {
						valueList.push({
							label: valueSummary[i],
							value: definition[filterType][i],
						});
					}
				}

				return {
					...filterColumn,
					definition,
					fieldLabel: objectField
						? stringUtils.getLocalizableLabel({
								fallbackLabel: objectField.name,
								fallbackLanguageId: creationLanguageId,
								labels: objectField.label,
							})
						: '',
					filterBy: objectFieldName,
					filterType,
					objectFieldBusinessType: objectField?.businessType,
					valueList,
				};
			});

			let newObjectViewName = objectView.name;

			if (!objectView.name[defaultLanguageId]) {
				newObjectViewName = {
					...newObjectViewName,
					[defaultLanguageId]: objectView.name[creationLanguageId],
				};
			}

			const newObjectView = {
				...objectView,
				name: newObjectViewName,
				objectViewColumns: newObjectViewColumns,
				objectViewFilterColumns: newObjectViewFilterColumns,
				objectViewSortColumns: newObjectViewSortColumns,
			};

			return {
				...state,
				creationLanguageId,
				objectFields: newObjectFields,
				objectView: newObjectView,
			};
		}
		case TYPES.ADD_OBJECT_VIEW_COLUMN: {
			const {creationLanguageId, selectedObjectFields} = action.payload;

			const {objectView} = state;
			const {objectViewSortColumns} = objectView;

			const newObjectViewColumns = selectedObjectFields.map(
				(item: ObjectField, index: number) => {
					const defaultSortColumn = objectViewSortColumns.find(
						(sortColumn) => item.name === sortColumn.objectFieldName
					);

					return {
						...item,
						defaultSort: defaultSortColumn ? true : false,
						fieldLabel: stringUtils.getLocalizableLabel({
							fallbackLabel: item.name,
							fallbackLanguageId: creationLanguageId,
							labels: item.label,
						}),
						label: item.label,
						objectFieldBusinessType: item.businessType,
						objectFieldName: item.name,
						priority: index,
					};
				}
			);

			const newObjectView = {
				...objectView,
				objectViewColumns: newObjectViewColumns,
			};

			return {
				...state,
				objectView: newObjectView,
			};
		}
		case TYPES.ADD_OBJECT_VIEW_FILTER_COLUMN: {
			const {creationLanguageId, filterType, objectFieldName, valueList} =
				action.payload;

			const labels: LocalizedValue<string>[] = [];
			let objectFieldBusinessType;
			const {objectFields} = state;

			objectFields.forEach((objectField: ObjectFieldView) => {
				if (objectField.name === objectFieldName) {
					labels.push(objectField.label);
					objectField.hasFilter = true;
					objectFieldBusinessType = objectField.businessType;
				}
			});

			const [label] = labels;

			let filterTypeValue = filterType || null;

			if (!valueList) {
				filterTypeValue = null;
			}

			const newFilterColumnItem: TObjectViewFilterColumn = {
				definition: filterTypeValue
					? {
							[filterTypeValue]: valueList
								? valueList.map(
										(item: {
											label: string;
											value: string;
										}) => item.value
									)
								: [],
						}
					: null,
				fieldLabel: stringUtils.getLocalizableLabel({
					fallbackLanguageId: creationLanguageId,
					labels: label,
				}),
				filterBy: label[defaultLanguageId],
				filterType: filterTypeValue,
				label,
				objectFieldBusinessType,
				objectFieldName,
				valueList: filterTypeValue ? valueList : [],
			};

			const objectView = {...state.objectView};

			let newObjectView;

			const {objectViewFilterColumns} = state.objectView;

			if (!objectViewFilterColumns) {
				const filterColumns: TObjectViewFilterColumn[] = [];

				filterColumns.push(newFilterColumnItem);

				newObjectView = {
					...objectView,
					objectViewFilterColumns: filterColumns,
				};
			}
			else {
				objectViewFilterColumns.push(newFilterColumnItem);

				newObjectView = {
					...objectView,
					objectViewFilterColumns,
				};
			}

			return {
				...state,
				objectFields,
				objectView: newObjectView,
			};
		}
		case TYPES.ADD_OBJECT_VIEW_SORT_COLUMN: {
			const {
				creationLanguageId,
				objectFieldName,
				objectFields,
				objectViewSortColumns,
				selectedObjetSortValue,
			} = action.payload;

			const objectView = {...state.objectView};
			const objectViewColumns = objectView.objectViewColumns;

			objectViewColumns.forEach((viewColumn) => {
				if (viewColumn.objectFieldName === objectFieldName) {
					viewColumn.defaultSort = true;
				}
			});

			const labels: LocalizedValue<string>[] = [];
			objectFields.forEach((objectField: ObjectField) => {
				if (objectField.name === objectFieldName) {
					labels.push(objectField.label);
				}
			});
			const [label] = labels;

			const newSortColumnItem: TObjectViewSortColumn = {
				fieldLabel: stringUtils.getLocalizableLabel({
					fallbackLanguageId: creationLanguageId,
					labels: label,
				}),
				label,
				objectFieldName,
				sortOrder: selectedObjetSortValue,
			};

			if (!objectViewSortColumns) {
				const sortColumn: TObjectViewSortColumn[] = [];

				sortColumn.push(newSortColumnItem);

				const newSortColumn = sortColumn.map((sortColumn, index) => {
					return {
						...sortColumn,
						priority: index,
					};
				});

				const newObjectView = {
					...objectView,
					objectViewSortColumns: newSortColumn,
				};

				return {
					...state,
					objectView: newObjectView,
				};
			}

			objectViewSortColumns.push(newSortColumnItem);

			const newSortColumn = objectViewSortColumns.map(
				(sortColumn: TObjectViewSortColumn, index: number) => {
					return {
						...sortColumn,
						priority: index,
					};
				}
			);

			const newObjectView = {
				...objectView,
				objectViewSortColumns: newSortColumn,
			};

			return {
				...state,
				objectView: newObjectView,
			};
		}
		case TYPES.CHANGE_OBJECT_VIEW_NAME: {
			const {newName} = action.payload;

			const newObjectView = {
				...state.objectView,
				name: {
					...state.objectView.name,
					[defaultLanguageId]: newName,
				},
			};

			return {
				...state,
				objectView: newObjectView,
			};
		}
		case TYPES.CHANGE_OBJECT_VIEW_COLUMN_ORDER: {
			const {draggedIndex, targetIndex} = action.payload;

			const newState = {...state};

			const viewColumns = newState.objectView.objectViewColumns;

			const newViewColumn = handleChangeColumnOrder(
				draggedIndex,
				targetIndex,
				viewColumns
			);

			const newObjectView = {
				...state.objectView,
				objectViewColumns: newViewColumn,
			};

			return {
				...state,
				objectView: newObjectView,
			};
		}
		case TYPES.CHANGE_OBJECT_VIEW_SORT_COLUMN_ORDER: {
			const {draggedIndex, targetIndex} = action.payload;

			const newState = {...state};

			const sortColumns = newState.objectView.objectViewSortColumns;

			const newSortColumn = handleChangeColumnOrder(
				draggedIndex,
				targetIndex,
				sortColumns
			);

			const newObjectView = {
				...state.objectView,
				objectViewSortColumns: newSortColumn,
			};

			return {
				...state,
				objectView: newObjectView,
			};
		}
		case TYPES.DELETE_OBJECT_VIEW_COLUMN: {
			const {objectFieldName} = action.payload;

			const newState = {...state};

			const objectFields = newState.objectFields;

			const viewColumn = newState.objectView?.objectViewColumns.filter(
				(viewColumn) => viewColumn.objectFieldName !== objectFieldName
			);

			const newViewColumn = viewColumn.map((viewColumn, index) => {
				return {
					...viewColumn,
					priority: index,
				};
			});

			objectFields.forEach((field: ObjectFieldView) => {
				if (objectFieldName === field.name) {
					field.checked = false;
				}
			});

			const newObjectView = {
				...state.objectView,
				objectViewColumns: newViewColumn,
			};

			return {
				...state,
				objectFields,
				objectView: newObjectView,
			};
		}
		case TYPES.DELETE_OBJECT_VIEW_FILTER_COLUMN: {
			const {objectFieldName} = action.payload;

			const {objectViewFilterColumns} = state.objectView;
			const {objectFields} = state;

			objectFields.forEach((objectField: ObjectFieldView) => {
				if (objectField.name === objectFieldName) {
					objectField.hasFilter = false;
				}
			});

			const filterColumns = objectViewFilterColumns.filter(
				(filterColumn) =>
					filterColumn.objectFieldName !== objectFieldName
			);

			const newObjectView = {
				...state.objectView,
				objectViewFilterColumns: filterColumns,
			};

			return {
				...state,
				objectFields,
				objectView: newObjectView,
			};
		}
		case TYPES.DELETE_OBJECT_VIEW_SORT_COLUMN: {
			const {objectFieldName} = action.payload;

			const newState = {...state};

			const objectViewColumns = newState.objectView.objectViewColumns;

			objectViewColumns.forEach((viewColumn) => {
				if (viewColumn.objectFieldName === objectFieldName) {
					viewColumn.defaultSort = false;
				}
			});

			const sortColumn =
				newState.objectView?.objectViewSortColumns.filter(
					(sortColumn) =>
						sortColumn.objectFieldName !== objectFieldName
				);

			const newSortColumn = sortColumn.map((sortColumn, index) => {
				return {
					...sortColumn,
					priority: index,
				};
			});

			const newObjectView = {
				...state.objectView,
				objectViewColumns,
				objectViewSortColumns: newSortColumn,
			};

			return {
				...state,
				objectView: newObjectView,
			};
		}
		case TYPES.EDIT_OBJECT_VIEW_COLUMN_LABEL: {
			const {editingObjectFieldName, translations} = action.payload;

			const {objectViewColumns} = state.objectView;

			const newObjectViewColumns = objectViewColumns.map((viewColumn) => {
				if (viewColumn.objectFieldName === editingObjectFieldName) {
					return {
						...viewColumn,
						label: translations,
					};
				}

				return viewColumn;
			});

			const newObjectView = {
				...state.objectView,
				objectViewColumns: newObjectViewColumns,
			};

			return {
				...state,
				objectView: newObjectView,
			};
		}
		case TYPES.EDIT_OBJECT_VIEW_FILTER_COLUMN: {
			const {filterType, objectFieldName, valueList} = action.payload;

			const {objectViewFilterColumns} = state.objectView;

			let filterTypeValue = filterType || null;

			if (!valueList) {
				filterTypeValue = null;
			}

			const newObjectFilterColumns = objectViewFilterColumns.map(
				(filterColumn) => {
					if (filterColumn.objectFieldName === objectFieldName) {
						return {
							...filterColumn,
							definition: filterTypeValue
								? {
										[filterTypeValue]: valueList
											? valueList.map(
													(item: LabelValueObject) =>
														item.value
												)
											: [],
									}
								: null,
							filterType: filterTypeValue,
							valueList: filterTypeValue ? valueList : [],
						};
					}
					else {
						return filterColumn;
					}
				}
			);

			const newObjectView = {
				...state.objectView,
				objectViewFilterColumns: newObjectFilterColumns,
			};

			return {
				...state,
				objectView: newObjectView,
			};
		}
		case TYPES.EDIT_OBJECT_VIEW_SORT_COLUMN_SORT_ORDER: {
			const {editingObjectFieldName, selectedObjectSort} = action.payload;

			const objectView = {...state.objectView};

			const objectViewSortColumns = objectView.objectViewSortColumns;

			const newObjectViewSortColumns = objectViewSortColumns.map(
				(sortColumn) => {
					if (sortColumn.objectFieldName === editingObjectFieldName) {
						return {
							...sortColumn,
							sortOrder: selectedObjectSort,
						};
					}
					else {
						return sortColumn;
					}
				}
			);

			const newObjectView = {
				...objectView,
				objectViewSortColumns: newObjectViewSortColumns,
			};

			return {
				...state,
				objectView: newObjectView,
			};
		}
		case TYPES.SET_OBJECT_VIEW_AS_DEFAULT: {
			const {checked} = action.payload;

			const newObjectView = {
				...state.objectView,
				defaultObjectView: checked,
			};

			return {
				...state,
				objectView: newObjectView,
			};
		}
		default:
			return state;
	}
}

interface IViewContextProviderProps extends React.HTMLAttributes<HTMLElement> {
	value: {
		filterOperators: TFilterOperators;
		isViewOnly: boolean;
		objectDefinitionExternalReferenceCode: string;
		objectViewId: string;
		workflowStatuses: TWorkflowStatus[];
	};
}

export function ViewContextProvider({
	children,
	value,
}: IViewContextProviderProps) {
	const [state, dispatch] = useReducer<React.Reducer<TState, TAction>>(
		viewReducer,
		{
			...initialState,
			...value,
		}
	);

	return (
		<ViewContext.Provider value={[state, dispatch]}>
			{children}
		</ViewContext.Provider>
	);
}

export function useViewContext() {
	return useContext(ViewContext);
}
