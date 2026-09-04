/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {CountryInfo} from '@liferay/object-js-components-web';
import {ILearnResourceContext} from 'frontend-js-components-web';
import {Edge, Elements, Node} from 'react-flow-renderer';

import {TYPES} from './ModelBuilderContext/typesEnum';

declare type TDropDownType =
	| 'checkbox'
	| 'contextual'
	| 'group'
	| 'item'
	| 'radio'
	| 'radiogroup'
	| 'divider';

export type DropDownItems = {
	active?: boolean;
	checked?: boolean;
	disabled?: boolean;
	href?: string;
	items?: Array<IItem>;
	label?: string;
	name?: string;
	onChange?: Function;
	onClick?: (event: React.MouseEvent<HTMLElement, MouseEvent>) => void;
	symbolLeft?: string;
	symbolRight?: string;
	type?: TDropDownType;
	value?: string;
};

export type TAction =
	| {
			payload: {
				dbTableName: string;
				dispatch: React.Dispatch<TAction>;
				elements: Elements<
					ObjectDefinitionNodeData | ObjectRelationshipEdgeData[]
				>;
				leftSidebarItems: LeftSidebarItem[];
				newObjectDefinition: ObjectDefinition;
				objectFolders: ObjectFolder[];
				selectedObjectFolder: ObjectFolder;
			};
			type: TYPES.ADD_OBJECT_DEFINITION_TO_OBJECT_FOLDER;
	  }
	| {
			payload: {
				newObjectField: ObjectField;
				objectDefinitionExternalReferenceCode: string;
				objectDefinitionNodes: Node<ObjectDefinitionNodeData>[];
				objectRelationshipEdges: Edge<ObjectRelationshipEdgeData[]>[];
				selectedObjectDefinitionNode: Node<ObjectDefinitionNodeData>;
			};
			type: TYPES.ADD_OBJECT_FIELD;
	  }
	| {
			payload: {
				hiddenObjectFolderObjectDefinitionNodes: boolean;
				leftSidebarItem: LeftSidebarItem;
				objectDefinitionNodes: Node<ObjectDefinitionNodeData>[];
				objectRelationshipEdges: Edge<ObjectRelationshipEdgeData[]>[];
			};
			type: TYPES.BULK_CHANGE_NODE_VIEW;
	  }
	| {
			payload: {
				hiddenObjectDefinitionNode: boolean;
				objectDefinitionId: number;
				objectDefinitionName: string;
				objectDefinitionNodes: Node<ObjectDefinitionNodeData>[];
				objectRelationshipEdges: Edge<ObjectRelationshipEdgeData[]>[];
				selectedSidebarItem: LeftSidebarItem;
			};
			type: TYPES.CHANGE_NODE_VIEW;
	  }
	| {
			payload: {
				objectDefinitionNodes: Node<ObjectDefinitionNodeData>[];
				objectRelationshipEdges: Edge<ObjectRelationshipEdgeData[]>[];
				selectedObjectDefinitionNode: Node<ObjectDefinitionNodeData> | null;
				selectedObjectField: ObjectFieldNodeRow;
			};
			type: TYPES.DELETE_OBJECT_FIELD;
	  }
	| {
			payload: {
				dispatch: React.Dispatch<TAction>;
				objectFolders: ObjectFolder[];
				rightSidebarType?: RightSidebarType;
				selectedObjectFolderName: string;
				selectedObjectRelationshipId?: number;
			};
			type: TYPES.UPDATE_MODEL_BUILDER_STRUCTURE;
	  }
	| {
			payload: {
				deletedObjectDefinition: DeletedObjectDefinition | null;
			};
			type: TYPES.SET_DELETE_OBJECT_DEFINITION;
	  }
	| {
			payload: {
				newElements: Elements<
					ObjectDefinitionNodeData | ObjectRelationshipEdgeData[]
				>;
			};
			type: TYPES.SET_ELEMENTS;
	  }
	| {
			payload: {
				updatedHasUnsavedObjectFolderItemPositions: boolean;
			};
			type: TYPES.SET_HAS_UNSAVED_OBJECT_FOLDER_ITEM_POSITIONS;
	  }
	| {
			payload: {
				nodeHandleConnectable: boolean;
			};
			type: TYPES.SET_NODE_HANDLE_CONNECTION;
	  }
	| {
			payload: {
				isLoadingObjectFolder: boolean;
			};
			type: TYPES.SET_LOADING_OBJECT_FOLDER;
	  }
	| {
			payload: {
				movedObjectDefinitionId?: number;
			};
			type: TYPES.SET_MOVED_OBJECT_DEFINITION;
	  }
	| {
			payload: {
				objectFolderName: string;
			};
			type: TYPES.SET_OBJECT_FOLDER_NAME;
	  }
	| {
			payload: {
				objectDefinitionNodes: Node<ObjectDefinitionNodeData>[];
				objectRelationshipEdges: Edge<ObjectRelationshipEdgeData[]>[];
				selectedObjectDefinitionId: string;
			};
			type: TYPES.SET_SELECTED_OBJECT_DEFINITION_NODE;
	  }
	| {
			payload: {
				newObjectDefinitionNodePosition: {
					x: number;
					y: number;
				};
				objectDefinitionNodes: Node<ObjectDefinitionNodeData>[];
				objectRelationshipEdges: Edge<ObjectRelationshipEdgeData[]>[];
				updatedObjectDefinitionNodeId: number;
				updatedObjectFolder: ObjectFolder;
			};
			type: TYPES.SET_SELECTED_OBJECT_DEFINITION_NODE_POSITION;
	  }
	| {
			payload: {
				objectDefinitionNodes: Node<ObjectDefinitionNodeData>[];
				objectRelationshipEdges: Edge<ObjectRelationshipEdgeData[]>[];
				selectedObjectDefinitionId: number;
				selectedObjectField: ObjectFieldNodeRow;
				selectedObjectFieldName: string;
			};
			type: TYPES.SET_SELECTED_OBJECT_FIELD;
	  }
	| {
			payload: {
				updatedSelectedObjectFolder: ObjectFolder;
			};
			type: TYPES.SET_SELECTED_OBJECT_FOLDER_DETAILS;
	  }
	| {
			payload: {
				selectedObjectRelationshipId: number;
			};
			type: TYPES.SET_SELECTED_OBJECT_RELATIONSHIP_EDGE;
	  }
	| {
			payload: {
				objectDefinitionExternalReferenceCode: string;
				showAllObjectFields: boolean;
			};
			type: TYPES.SET_SHOW_ALL_OBJECT_FIELDS;
	  }
	| {
			payload: {
				updatedShowChangesSaved: boolean;
			};
			type: TYPES.SET_SHOW_CHANGES_SAVED;
	  }
	| {
			payload: {
				updatedShowSidebars: boolean;
			};
			type: TYPES.SET_SHOW_SIDEBARS;
	  }
	| {
			payload: {
				currentObjectFolderName: string;
				objectDefinitionNodes: Node<ObjectDefinitionNodeData>[];
				objectDefinitionRelationshipEdges: Edge<
					ObjectRelationshipEdgeData[]
				>[];
				updatedObjectDefinition: Partial<ObjectDefinition>;
			};
			type: TYPES.UPDATE_OBJECT_DEFINITION_NODE;
	  }
	| {
			payload: {
				objectDefinitionNodes: Node<ObjectDefinitionNodeData>[];
				objectRelationshipEdges: Edge<ObjectRelationshipEdgeData[]>[];
				selectedObjectDefinitionNode: Node<ObjectDefinitionNodeData> | null;
				updatedObjectField: ObjectField;
			};
			type: TYPES.UPDATE_OBJECT_FIELD_NODE_ROW;
	  }
	| {
			payload: {
				updatedModelBuilderModals: Partial<ModelBuilderModals>;
			};
			type: TYPES.UPDATE_VISIBILITY_MODEL_BUILDER_MODALS;
	  };

export type TState = {
	baseResourceURL: string;
	ckEditor5Config?: object;
	countries: CountryInfo[];
	decimalSeparator: string;
	deletedObjectDefinition: DeletedObjectDefinition | null;
	editObjectDefinitionURL: string;
	elements: Elements<ObjectDefinitionNodeData | ObjectRelationshipEdgeData[]>;
	filterOperators: TFilterOperators;
	forbiddenChars: string[];
	forbiddenLastChars: string[];
	forbiddenNames: string[];
	hasDepotEntry?: boolean;
	hasUnsavedObjectFolderItemPositions: boolean;
	isLoadingObjectFolder: boolean;
	isRootDescendantNode: boolean;
	learnResourceContext: ILearnResourceContext;
	leftSidebarItems: LeftSidebarItem[];
	metadataObjectFieldNames: string[];
	modelBuilderModals: ModelBuilderModals;
	movedObjectDefinitionId?: number;
	nodeHandleConnectable: boolean;
	objectDefinitionPermissionsURL: string;
	objectDefinitionsStorageTypes: LabelValueObject[];
	objectFolderName: string;
	objectFolders: ObjectFolder[];
	rightSidebarType: RightSidebarType;
	selectedObjectDefinitionNode: Node<ObjectDefinitionNodeData> | null;
	selectedObjectField?: ObjectFieldNodeRow;
	selectedObjectFolder: ObjectFolder;
	selectedObjectRelationship?: ObjectRelationshipEdgeData | null;
	showChangesSaved: boolean;
	showSidebars: boolean;
	workflowStatuses: LabelValueObject[];
};

export interface LeftSidebarItem {
	hiddenObjectFolderObjectDefinitionNodes: boolean;
	id?: string;
	leftSidebarObjectDefinitionItems?: LeftSidebarObjectDefinitionItem[];
	name: string;
	objectFolderName: string;
	type: 'objectFolder' | 'objectDefinition';
}

export type LeftSidebarKebabOption = {
	label?: string;
	onClick?: (event: React.MouseEvent<HTMLElement, MouseEvent>) => void;
	symbolLeft?: string;
	symbolRight?: string;
	type?:
		| 'checkbox'
		| 'contextual'
		| 'group'
		| 'item'
		| 'radio'
		| 'radiogroup'
		| 'divider';
};

export interface LeftSidebarObjectDefinitionItem {
	externalReferenceCode?: string;
	hiddenObjectDefinitionNode: boolean;
	id: number;
	kebabOptions: LeftSidebarKebabOption[];
	label: string;
	linked?: boolean;
	name: string;
	selected: boolean;
	type:
		| 'dummyObjectDefinition'
		| 'linkedObjectDefinition'
		| 'objectDefinition';
}

export interface ObjectRelationshipEdgeData {
	defaultLanguageId?: Liferay.Language.Locale;
	edge: boolean;
	id: number;
	label: string;
	markerEndId: string;
	markerStartId: string;
	name: string;
	selected: boolean;
	type: string;
}

export type nonRelationshipObjectFieldsInfo = {
	label: LocalizedValue<string>;
	name: string;
};

export type RightSidebarType =
	| 'empty'
	| 'objectFieldDetails'
	| 'objectDefinitionDetails'
	| 'objectRelationshipDetails';
