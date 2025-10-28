/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {createContext, useContext, useReducer} from 'react';

import {
	findObjectFieldIndexById,
	findObjectFieldIndexByName,
	findObjectLayoutRowIndex,
} from '../../utils/layout';
import {BoxesVisitor, RowsVisitor} from '../../utils/visitor';
import {
	BoxType,
	TObjectField,
	TObjectLayout,
	TObjectRelationship,
} from './types';

type TState = {
	creationLanguageId: Liferay.Language.Locale;
	enableCategorization: boolean;
	enableFriendlyURLCustomization: boolean;
	isViewOnly: boolean;
	objectFieldBusinessTypes: ObjectFieldBusinessType[];
	objectFields: TObjectField[];
	objectLayout: TObjectLayout;
	objectLayoutId: string;
	objectRelationships: TObjectRelationship[];
};

export type TAction =
	| {
			payload: {
				creationLanguageId: Liferay.Language.Locale;
				enableCategorization: boolean;
				enableFriendlyURLCustomization: boolean;
				objectLayout: TObjectLayout;
				objectRelationships: TObjectRelationship[];
			};
			type: TYPES.ADD_OBJECT_LAYOUT;
	  }
	| {
			payload: {
				name: LocalizedValue<string>;
				objectRelationshipId: number;
			};
			type: TYPES.ADD_OBJECT_LAYOUT_TAB;
	  }
	| {
			payload: {
				name: LocalizedValue<string>;
				tabIndex?: number;
				type: BoxType;
			};
			type: TYPES.ADD_OBJECT_LAYOUT_BOX;
	  }
	| {
			payload: {objectFields: TObjectField[]};
			type: TYPES.ADD_OBJECT_FIELDS;
	  }
	| {
			payload: {
				boxIndex: number;
				objectFieldName: string;
				objectFieldSize: number;
				tabIndex: number;
			};
			type: TYPES.ADD_OBJECT_LAYOUT_FIELD;
	  }
	| {
			payload: {name: LocalizedValue<string>};
			type: TYPES.CHANGE_OBJECT_LAYOUT_NAME;
	  }
	| {
			payload: {checked: boolean};
			type: TYPES.SET_OBJECT_LAYOUT_AS_DEFAULT;
	  }
	| {
			payload: {
				attribute: {
					key: 'collapsable';
					value: boolean;
				};
				boxIndex: number;
				tabIndex: number;
			};
			type: TYPES.CHANGE_OBJECT_LAYOUT_BOX_ATTRIBUTE;
	  }
	| {
			payload: {
				boxIndex: number;
				tabIndex: number;
			};
			type: TYPES.DELETE_OBJECT_LAYOUT_BOX;
	  }
	| {
			payload: {
				boxIndex: number;
				columnIndex: number;
				objectFieldName: string;
				rowIndex: number;
				tabIndex: number;
			};
			type: TYPES.DELETE_OBJECT_LAYOUT_FIELD;
	  }
	| {
			payload: {
				tabIndex: number;
			};
			type: TYPES.DELETE_OBJECT_LAYOUT_TAB;
	  };

interface ILayoutContextProps extends Array<TState | Function> {
	0: typeof initialState;
	1: React.Dispatch<React.ReducerAction<React.Reducer<TState, TAction>>>;
}

const LayoutContext = createContext({} as ILayoutContextProps);

export enum TYPES {
	ADD_OBJECT_FIELDS = 'ADD_OBJECT_FIELDS',
	ADD_OBJECT_LAYOUT = 'ADD_OBJECT_LAYOUT',
	ADD_OBJECT_LAYOUT_BOX = 'ADD_OBJECT_LAYOUT_BOX',
	ADD_OBJECT_LAYOUT_FIELD = 'ADD_OBJECT_LAYOUT_FIELD',
	ADD_OBJECT_LAYOUT_TAB = 'ADD_OBJECT_LAYOUT_TAB',
	CHANGE_OBJECT_LAYOUT_BOX_ATTRIBUTE = 'CHANGE_OBJECT_LAYOUT_BOX_ATTRIBUTE',
	CHANGE_OBJECT_LAYOUT_NAME = 'CHANGE_OBJECT_LAYOUT_NAME',
	DELETE_OBJECT_LAYOUT_BOX = 'DELETE_OBJECT_LAYOUT_BOX',
	DELETE_OBJECT_LAYOUT_FIELD = 'DELETE_OBJECT_LAYOUT_FIELD',
	DELETE_OBJECT_LAYOUT_TAB = 'DELETE_OBJECT_LAYOUT_TAB',
	SET_OBJECT_LAYOUT_AS_DEFAULT = 'SET_OBJECT_LAYOUT_AS_DEFAULT',
}

const initialState = {
	objectFields: [] as TObjectField[],
	objectLayout: {} as TObjectLayout,
	objectRelationships: [] as TObjectRelationship[],
} as TState;

export function layoutReducer(state: TState, action: TAction) {
	switch (action.type) {
		case TYPES.ADD_OBJECT_LAYOUT: {
			const {
				creationLanguageId,
				enableCategorization,
				enableFriendlyURLCustomization,
				objectLayout,
				objectRelationships,
			} = action.payload;

			return {
				...state,
				creationLanguageId,
				enableCategorization,
				enableFriendlyURLCustomization,
				objectLayout,
				objectRelationships,
			};
		}
		case TYPES.ADD_OBJECT_LAYOUT_TAB: {
			const {name, objectRelationshipId} = action.payload;

			const newState = {...state};

			const newObjectLayoutTab = {
				name,
				objectLayoutBoxes: [],
				objectRelationshipId,
				priority: 0,
			};

			if (objectRelationshipId) {
				newState.objectRelationships[
					findObjectFieldIndexById(
						newState.objectRelationships,
						objectRelationshipId
					)
				].inLayout = true;
			}

			if (state.objectLayout.objectLayoutTabs.length) {
				return {
					...newState,
					objectLayout: {
						...newState.objectLayout,
						objectLayoutTabs: [
							...newState.objectLayout.objectLayoutTabs,
							newObjectLayoutTab,
						],
					},
				};
			}

			return {
				...newState,
				objectLayout: {
					...newState.objectLayout,
					objectLayoutTabs: [newObjectLayoutTab],
				},
			};
		}
		case TYPES.ADD_OBJECT_LAYOUT_BOX: {
			const {name, tabIndex, type} = action.payload;

			const newState = {...state};

			const objectLayoutBoxes =
				newState.objectLayout.objectLayoutTabs[tabIndex as number]
					.objectLayoutBoxes;

			const newBox = {
				collapsable: false,
				name,
				objectLayoutRows: [],
				priority: 0,
				type,
			};

			const getPriority = (boxType: BoxType) => {
				switch (boxType) {
					case 'regular':
						return 1;
					case 'categorization':
						return 2;
					case 'seo':
						return 3;
					default:
						return 4;
				}
			};

			const newBoxPriority = getPriority(type);

			const insertionIndex = objectLayoutBoxes.findIndex(
				(box) => getPriority(box.type) > newBoxPriority
			);

			if (insertionIndex >= 0) {
				objectLayoutBoxes.splice(insertionIndex, 0, newBox);
			}
			else {
				objectLayoutBoxes.push(newBox);
			}

			return newState;
		}
		case TYPES.ADD_OBJECT_FIELDS: {
			const {objectFields} = action.payload;

			return {
				...state,
				objectFields,
			};
		}
		case TYPES.ADD_OBJECT_LAYOUT_FIELD: {
			const {boxIndex, objectFieldName, objectFieldSize, tabIndex} =
				action.payload;

			const newState = {...state};

			const newField = {
				objectFieldName,
				priority: 0,
				size: objectFieldSize,
			};

			const objectLayoutBox =
				newState.objectLayout.objectLayoutTabs[tabIndex]
					.objectLayoutBoxes[boxIndex];

			const objectLayoutRowIndex = findObjectLayoutRowIndex(
				objectLayoutBox.objectLayoutRows,
				objectFieldSize
			);

			if (objectLayoutRowIndex > -1) {
				objectLayoutBox.objectLayoutRows[
					objectLayoutRowIndex
				].objectLayoutColumns.push(newField);
			}
			else {
				objectLayoutBox.objectLayoutRows.push({
					objectLayoutColumns: [newField],
					priority: 0,
				});
			}

			newState.objectFields[
				findObjectFieldIndexByName(
					newState.objectFields,
					objectFieldName
				)
			].inLayout = true;

			return newState;
		}
		case TYPES.CHANGE_OBJECT_LAYOUT_NAME: {
			const {name} = action.payload;

			return {
				...state,
				objectLayout: {
					...state.objectLayout,
					name,
				},
			};
		}
		case TYPES.SET_OBJECT_LAYOUT_AS_DEFAULT: {
			const {checked} = action.payload;

			return {
				...state,
				objectLayout: {
					...state.objectLayout,
					defaultObjectLayout: checked,
				},
			};
		}
		case TYPES.CHANGE_OBJECT_LAYOUT_BOX_ATTRIBUTE: {
			type TObjectLayoutBoxAttribute = {
				key: keyof {collapsable: boolean};
				value: boolean;
			};

			const {attribute, boxIndex, tabIndex} = action.payload;
			const {key, value}: TObjectLayoutBoxAttribute = attribute;

			const newState = {...state};

			const objectLayoutBox =
				newState.objectLayout.objectLayoutTabs[tabIndex]
					.objectLayoutBoxes[boxIndex];

			objectLayoutBox[key] = value;

			return newState;
		}
		case TYPES.DELETE_OBJECT_LAYOUT_BOX: {
			const {boxIndex, tabIndex} = action.payload;

			const newState = {...state};

			// Change object field inLayout attribute to false to be visible when add field again.

			const objectFieldNames = newState.objectFields.map(
				({name}) => name
			);
			const visitor = new RowsVisitor(
				newState.objectLayout.objectLayoutTabs[
					tabIndex
				].objectLayoutBoxes[boxIndex]
			);

			visitor.mapFields((field) => {
				const objectIndex = objectFieldNames.indexOf(
					field.objectFieldName
				);
				newState.objectFields[objectIndex].inLayout = false;
			});

			// Delete object layout box

			newState.objectLayout.objectLayoutTabs[
				tabIndex
			].objectLayoutBoxes.splice(boxIndex, 1);

			return newState;
		}
		case TYPES.DELETE_OBJECT_LAYOUT_FIELD: {
			const {boxIndex, columnIndex, objectFieldName, rowIndex, tabIndex} =
				action.payload;

			const newState = {...state};

			const objectLayoutBox =
				newState.objectLayout.objectLayoutTabs[tabIndex]
					.objectLayoutBoxes[boxIndex];

			const objectLayoutRow = objectLayoutBox.objectLayoutRows[rowIndex];

			objectLayoutRow.objectLayoutColumns.splice(columnIndex, 1);

			if (!objectLayoutRow.objectLayoutColumns.length) {
				objectLayoutBox.objectLayoutRows.splice(rowIndex, 1);
			}

			const objectFieldIndex = findObjectFieldIndexByName(
				newState.objectFields,
				objectFieldName
			);

			newState.objectFields[objectFieldIndex].inLayout = false;

			return newState;
		}
		case TYPES.DELETE_OBJECT_LAYOUT_TAB: {
			const {tabIndex} = action.payload;

			const newState = {...state};

			const objectRelationshipId =
				newState.objectLayout.objectLayoutTabs[tabIndex]
					.objectRelationshipId;

			if (objectRelationshipId) {
				const objectRelationshipIds = newState.objectRelationships.map(
					({id}) => id
				);
				const objectRelationshipIndex =
					objectRelationshipIds.indexOf(objectRelationshipId);

				newState.objectRelationships[objectRelationshipIndex].inLayout =
					false;
			}

			// Change object field inLayout attribute to false to be visible when add field again.

			const objectFieldNames = newState.objectFields.map(
				({name}) => name
			);
			const visitor = new BoxesVisitor(
				newState.objectLayout.objectLayoutTabs[tabIndex]
			);

			visitor.mapFields((field) => {
				if (field.objectFieldName) {
					const objectFieldIndex = objectFieldNames.indexOf(
						field.objectFieldName
					);
					newState.objectFields[objectFieldIndex].inLayout = false;
				}
			});

			// Delete object layout tab

			newState.objectLayout.objectLayoutTabs.splice(tabIndex, 1);

			return newState;
		}
		default:
			return state;
	}
}

interface ILayoutContextProviderProps
	extends React.HTMLAttributes<HTMLElement> {
	value: {
		isViewOnly: boolean;
		objectFieldBusinessTypes: ObjectFieldBusinessType[];
		objectLayoutId: string;
	};
}

export function LayoutContextProvider({
	children,
	value,
}: ILayoutContextProviderProps) {
	const [state, dispatch] = useReducer<React.Reducer<TState, TAction>>(
		layoutReducer,
		{
			...initialState,
			...value,
		}
	);

	return (
		<LayoutContext.Provider value={[state, dispatch]}>
			{children}
		</LayoutContext.Provider>
	);
}

export function useLayoutContext() {
	return useContext(LayoutContext);
}
