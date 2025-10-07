/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openConfirmModal} from '@liferay/layout-js-components-web';
import React, {
	Dispatch,
	ReactNode,
	createContext,
	useContext,
	useReducer,
} from 'react';

import {Space} from '../../common/types/Space';
import {Workflow} from '../../common/types/Workflow';
import {ObjectDefinitions} from '../types/ObjectDefinition';
import {
	ReferencedStructure,
	RepeatableGroup,
	Structure,
} from '../types/Structure';
import {Uuid} from '../types/Uuid';
import actionGeneratesChanges from '../utils/actionGeneratesChanges';
import deleteChildren from '../utils/deleteChildren';
import {
	Field,
	MultiselectField,
	SingleSelectField,
	getDefaultField,
} from '../utils/field';
import findAvailableFieldName from '../utils/findAvailableFieldName';
import findChild from '../utils/findChild';
import {getChildrenUuids} from '../utils/getChildrenUuids';
import getRandomId from '../utils/getRandomId';
import getUuid from '../utils/getUuid';
import insertGroup from '../utils/insertGroup';
import isLocked from '../utils/isLocked';
import isReferenced from '../utils/isReferenced';
import normalizeName from '../utils/normalizeName';
import refreshReferencedStructures from '../utils/refreshReferencedStructures';
import sortChildren from '../utils/sortChildren';
import ungroup from '../utils/ungroup';
import updateChild from '../utils/updateChild';
import {
	ValidationError,
	validateField,
	validateRepeatableGroup,
	validateStructure,
} from '../utils/validation';

const DEFAULT_STRUCTURE_LABEL = Liferay.Language.get(
	'untitled-content-structure'
);

type History = {
	deletedChildren: boolean;
};

export type State = {
	error: string | null;
	history: History;
	invalids: Map<Uuid, Set<ValidationError>>;
	publishedChildren: Set<Uuid>;
	selection: Uuid[];
	structure: Structure;
	unsavedChanges: boolean;
};

const INITIAL_STATE: State = {
	error: null,
	history: {
		deletedChildren: false,
	},
	invalids: new Map(),
	publishedChildren: new Set(),
	selection: [],
	structure: {
		children: new Map(),
		erc: '',
		label: {
			[Liferay.ThemeDisplay.getDefaultLanguageId()]:
				DEFAULT_STRUCTURE_LABEL,
		},
		name: normalizeName(DEFAULT_STRUCTURE_LABEL),
		spaces: [],
		status: 'new',
		uuid: getUuid(),
		workflows: {},
	},
	unsavedChanges: false,
};

type AddFieldAction = {field: Field; type: 'add-field'};

type AddReferencedStructuresAction = {
	referencedStructures: ReferencedStructure[];
	type: 'add-referenced-structures';
};

type AddRepeatableGroup = {
	type: 'add-repeatable-group';
	uuid?: Uuid;
};

type AddValidationError = {
	error: ValidationError;
	type: 'add-validation-error';
	uuid: Uuid;
};

type ClearErrorAction = {
	type: 'clear-error';
};

type CreateStructureAction = {
	type: 'create-structure';
};

type DeleteChildAction = {type: 'delete-child'; uuid: Uuid};

type DeleteSelectionAction = {type: 'delete-selection'};

type PublishStructureAction = {type: 'publish-structure'};

type RefreshReferencedStructuresAction = {
	objectDefinitions: ObjectDefinitions;
	type: 'refresh-referenced-structures';
};

type SetErrorAction = {error: string | null; type: 'set-error'};

type SetSelection = {
	selection: State['selection'];
	type: 'set-selection';
};

type SetWorkflowAction = {
	name: Workflow['name'];
	spaceERC?: Space['externalReferenceCode'];
	type: 'set-workflow';
};

type UngroupAction = {
	type: 'ungroup';
	uuid: Uuid;
};

type UpdateFieldAction = {
	erc?: string;
	indexableConfig?: Field['indexableConfig'];
	label?: Liferay.Language.LocalizedValue<string>;
	localized?: boolean;
	name?: string;
	newName?: string;
	picklistId?: number;
	required?: boolean;
	settings?: Field['settings'];
	type: 'update-field';
	uuid: Uuid;
};

type UpdateRepeatableGroupAction = {
	label: Liferay.Language.LocalizedValue<string>;
	type: 'update-repeatable-group';
	uuid: Uuid;
};

type UpdateStructureAction = {
	erc?: string;
	label?: Liferay.Language.LocalizedValue<string>;
	name?: string;
	spaces?: Structure['spaces'];
	type: 'update-structure';
};

type ValidateAction = {
	invalids: State['invalids'];
	type: 'validate';
};

export type Action =
	| AddFieldAction
	| AddReferencedStructuresAction
	| AddRepeatableGroup
	| AddValidationError
	| ClearErrorAction
	| CreateStructureAction
	| DeleteChildAction
	| DeleteSelectionAction
	| PublishStructureAction
	| RefreshReferencedStructuresAction
	| SetErrorAction
	| SetSelection
	| SetWorkflowAction
	| UngroupAction
	| UpdateFieldAction
	| UpdateRepeatableGroupAction
	| UpdateStructureAction
	| ValidateAction;

function reducer(state: State, action: Action): State {
	if (actionGeneratesChanges(action.type)) {
		state = {...state, unsavedChanges: true};
	}

	switch (action.type) {
		case 'add-field': {
			const {field} = action;

			const {structure} = state;

			const name = findAvailableFieldName(structure.children, field.name);

			const children = new Map(structure.children);

			children.set(field.uuid, {...field, name});

			const sortedChildren = sortChildren(children);

			return {
				...state,
				selection: [field.uuid],
				structure: {
					...structure,
					children: sortedChildren,
				},
			};
		}
		case 'add-referenced-structures': {
			const {referencedStructures} = action;

			const {publishedChildren, structure} = state;

			const children = new Map(structure.children);

			let nextPublishedChildren = new Set(publishedChildren);

			let selection: State['selection'] = [];

			for (const [
				i,
				referencedStructure,
			] of referencedStructures.entries()) {
				children.set(referencedStructure.uuid, referencedStructure);

				nextPublishedChildren = new Set([
					...nextPublishedChildren,
					...getChildrenUuids({root: referencedStructure}),
				]);

				if (i === 0) {
					selection = [referencedStructure.uuid];
				}
			}

			const sortedChildren = sortChildren(children);

			return {
				...state,
				publishedChildren: nextPublishedChildren,
				selection,
				structure: {...structure, children: sortedChildren},
			};
		}
		case 'add-repeatable-group': {
			const {publishedChildren, selection, structure} = state;

			const {uuid} = action;

			const uuids = uuid ? [uuid] : selection;

			const groupChildren = uuids.map(
				(uuid) => findChild({root: structure, uuid})!
			);

			let parent: Structure | RepeatableGroup = structure;

			if (groupChildren[0].parent !== structure.uuid) {
				parent = findChild({
					root: structure,
					uuid: groupChildren[0].parent,
				})! as RepeatableGroup;
			}

			for (const child of groupChildren) {
				if (isLocked(child)) {
					showWarning({
						text: Liferay.Language.get(
							'the-repeatable-group-cannot-be-created-because-one-or-more-fields-of-the-selection-are-system-fields'
						),
						title: Liferay.Language.get(
							'repeatable-group-creation-not-allowed'
						),
					});

					return state;
				}

				if (
					publishedChildren.has(child.uuid) ||
					isReferenced({item: child, root: structure})
				) {
					showWarning({
						text: Liferay.Language.get(
							'the-repeatable-group-cannot-be-created-because-one-or-more-fields-of-the-selection-are-already-published'
						),
						title: Liferay.Language.get(
							'repeatable-group-creation-not-allowed'
						),
					});

					return state;
				}

				if (child.parent !== parent.uuid) {
					showWarning({
						text: Liferay.Language.get(
							'a-repeatable-group-requires-all-selected-items-to-be-at-the-same-hierarchy-level'
						),
						title: Liferay.Language.get(
							'repeatable-group-creation-not-allowed'
						),
					});

					return state;
				}
			}

			const parentFields = Array.from(parent.children.values()).filter(
				(child) =>
					child.type !== 'referenced-structure' &&
					child.type !== 'repeatable-group'
			);

			if (parentFields.length === groupChildren.length) {
				showWarning({
					text: Liferay.Language.get(
						'the-repeatable-group-cannot-be-created-because-at-least-one-field-is-required'
					),
					title: Liferay.Language.get(
						'repeatable-group-creation-not-allowed'
					),
				});

				return state;
			}

			const groupUuid = getUuid();

			const children = insertGroup({
				groupChildren,
				groupParent: parent.uuid,
				groupUuid,
				root: structure,
			});

			const sortedChildren = sortChildren(children);

			return {
				...state,
				selection: [groupUuid],
				structure: {...structure, children: sortedChildren},
			};
		}
		case 'add-validation-error': {
			const {error, uuid} = action;

			const invalids = new Map(state.invalids);

			const currentErrors = new Set(invalids.get(uuid));

			currentErrors.add(error);

			invalids.set(uuid, currentErrors);

			return {
				...state,
				invalids,
			};
		}
		case 'clear-error': {
			return {
				...state,
				error: INITIAL_STATE.error,
			};
		}
		case 'create-structure': {
			const {structure} = state;

			return {
				...state,
				error: INITIAL_STATE.error,
				structure: {
					...structure,
					status: 'draft' as Structure['status'],
				},
			};
		}
		case 'delete-child': {
			const {structure} = state;
			const {uuid} = action;

			const child = findChild({root: structure, uuid});

			if (!child) {
				return state;
			}

			const nextChildren = deleteChildren({
				root: structure,
				uuids: [child.uuid],
			});

			const invalids = new Map(state.invalids);

			invalids.delete(uuid);

			let nextState: State = {
				...state,
				invalids,
				structure: {...state.structure, children: nextChildren},
			};

			if (state.selection.includes(uuid)) {
				nextState = {
					...nextState,
					selection: INITIAL_STATE.selection,
				};
			}

			if (state.publishedChildren.has(uuid)) {
				nextState = {
					...nextState,
					history: {...nextState.history, deletedChildren: true},
				};
			}

			return nextState;
		}
		case 'delete-selection': {
			const {selection, structure} = state;

			const items = selection.map(
				(uuid) => findChild({root: structure, uuid})!
			);

			if (items.some((item) => isLocked(item))) {
				showWarning({
					text: Liferay.Language.get(
						'system-fields-cannot-be-deleted'
					),
					title: Liferay.Language.get(
						'some-fields-cannot-be-deleted'
					),
				});
			}
			else if (
				items.some((item) => isReferenced({item, root: structure}))
			) {
				showWarning({
					text: Liferay.Language.get(
						'referenced-content-structure-fields-cannot-be-deleted'
					),
					title: Liferay.Language.get(
						'some-fields-cannot-be-deleted'
					),
				});
			}

			const nextChildren = deleteChildren({
				root: structure,
				uuids: selection,
			});

			const undeletableItems = items.filter(
				(item) =>
					isLocked(item) || isReferenced({item, root: structure})
			);

			return {
				...state,
				selection: undeletableItems.map(({uuid}) => uuid),
				structure: {
					...structure,
					children: nextChildren,
				},
			};
		}
		case 'publish-structure': {
			const {structure} = state;

			const nextStructure = {
				...structure,
				status: 'published' as Structure['status'],
			};

			return {
				...state,
				error: INITIAL_STATE.error,
				history: INITIAL_STATE.history,
				publishedChildren: getChildrenUuids({root: structure}),
				structure: nextStructure,
				unsavedChanges: false,
			};
		}
		case 'refresh-referenced-structures': {
			const {structure} = state;

			const {objectDefinitions} = action;

			const nextChildren = refreshReferencedStructures({
				objectDefinitions,
				root: structure,
			});

			const nextStructure = {
				...structure,
				children: nextChildren,
			};

			return {...state, structure: nextStructure};
		}
		case 'set-error':
			return {
				...state,
				error: action.error,
				selection: [state.structure.uuid],
			};
		case 'set-selection': {
			const {selection} = action;

			return {...state, selection};
		}
		case 'set-workflow': {
			const {name, spaceERC} = action;

			const {structure} = state;

			const nextStructure = {
				...structure,
				workflows: {
					...structure.workflows,
					[spaceERC || '']: name,
				},
			};

			return {...state, structure: nextStructure};
		}
		case 'ungroup': {
			const {publishedChildren, structure} = state;

			const {uuid} = action;

			if (publishedChildren.has(uuid)) {
				showWarning({
					text: Liferay.Language.get(
						'the-ungroup-action-cannot-be-done-because-this-repeatable-group-is-already-published'
					),
					title: Liferay.Language.get('ungroup-action-not-allowed'),
				});

				return state;
			}

			const nextChildren = ungroup({root: structure, uuid});

			return {
				...state,
				structure: {
					...structure,
					children: nextChildren,
				},
			};
		}
		case 'update-field': {
			const {
				erc,
				indexableConfig,
				label,
				localized,
				name,
				picklistId,
				required,
				settings,
				uuid,
			} = action;

			const {structure} = state;

			const field = findChild({root: structure, uuid}) as Field;

			if (!field) {
				return state;
			}

			// Prepare updated field

			const nextField: Field = {
				...field,
				erc: erc ?? field.erc,
				indexableConfig: indexableConfig ?? field.indexableConfig,
				label: label ?? field.label,
				localized: localized ?? field.localized,
				name: name ?? field.name,
				required: required ?? field.required,
				settings: settings ?? field.settings,
			};

			if (picklistId) {
				(nextField as SingleSelectField | MultiselectField).picklistId =
					picklistId;
			}

			const nextChildren = updateChild({
				child: nextField,
				root: structure,
			});

			// Validate the data sent in the action

			const invalids = new Map(state.invalids);

			const {type: _, ...data} = action;

			const errors = validateField({
				currentErrors: invalids.get(nextField.uuid),
				data,
			});

			if (errors.size) {
				invalids.set(nextField.uuid, errors);
			}
			else {
				invalids.delete(nextField.uuid);
			}

			// Return new state

			return {
				...state,
				invalids,
				selection: [nextField.uuid],
				structure: {
					...structure,
					children: nextChildren,
				},
			};
		}
		case 'update-repeatable-group': {
			const {label, uuid} = action;

			const {structure} = state;

			const group = findChild({root: structure, uuid}) as RepeatableGroup;

			if (!group) {
				return state;
			}

			const nextGroup = {
				...group,
				label,
			};

			const nextChildren = updateChild({
				child: nextGroup,
				root: structure,
			});

			const nextState: State = {
				...state,
				structure: {
					...structure,
					children: nextChildren,
				},
			};

			// Validate the data sent in the action

			const invalids = new Map(state.invalids);

			const errors = validateRepeatableGroup({
				currentErrors: invalids.get(structure.uuid),
				data: {label},
			});

			if (errors.size) {
				invalids.set(structure.uuid, errors);
			}
			else {
				invalids.delete(structure.uuid);
			}

			// Return new state

			return {
				...nextState,
				invalids,
			};
		}
		case 'update-structure': {

			// Prepare updated state

			const {erc, label, name, spaces} = action;

			const {structure} = state;

			const nextState: State = {
				...state,
				structure: {
					...state.structure,
					erc: erc ?? structure.erc,
					label: label ?? structure.label,
					name: name ?? structure.name,
					spaces: spaces ?? structure.spaces,
				},
			};

			// Validate the data sent in the action

			const invalids = new Map(state.invalids);

			const errors = validateStructure({
				currentErrors: invalids.get(structure.uuid),
				data: {erc, label, name, spaces},
			});

			if (errors.size) {
				invalids.set(structure.uuid, errors);
			}
			else {
				invalids.delete(structure.uuid);
			}

			// Return new state

			return {
				...nextState,
				invalids,
			};
		}
		case 'validate': {
			const {invalids} = action;

			const [firstUuid] = [...invalids.keys()];

			return {
				...state,
				error: INITIAL_STATE.error,
				invalids,
				selection: [firstUuid],
			};
		}
		default:
			return state;
	}
}

function initState(state: State): State {
	const {structure} = state;

	if (structure.erc) {
		return state;
	}

	return {
		...state,
		structure: {
			...structure,
			children: getDefaultChildren(structure.uuid),
			erc: getRandomId(),
		},
	};
}

const StateContext = createContext<{
	dispatch: Dispatch<Action>;
	state: State;
}>({
	dispatch: () => {},
	state: INITIAL_STATE,
});

export default function StateContextProvider({
	children,
	initialState,
}: {
	children: ReactNode;
	initialState: State | null;
}) {
	const [state, dispatch] = useReducer<React.Reducer<State, Action>, State>(
		reducer,
		initialState ?? INITIAL_STATE,
		initState
	);

	return (
		<StateContext.Provider value={{dispatch, state}}>
			{children}
		</StateContext.Provider>
	);
}

function useSelector<T>(selector: (state: State) => T) {
	const {state} = useContext(StateContext);

	return selector(state);
}

function useStateDispatch() {
	return useContext(StateContext).dispatch;
}

function getDefaultChildren(structureUuid: Uuid) {
	const url = new URL(window.location.href);

	const type = url.searchParams.get('objectFolderExternalReferenceCode');

	const children = new Map();

	const title = getDefaultField({
		label: Liferay.Language.get('title'),
		locked: true,
		name: 'title',
		parent: structureUuid,
		required: true,
		type: 'text',
	});

	children.set(title.uuid, title);

	if (type === 'L_CMS_FILE_TYPES') {
		const file = getDefaultField({
			label: Liferay.Language.get('file'),
			locked: true,
			name: 'file',
			parent: structureUuid,
			required: true,
			type: 'upload',
		});

		children.set(file.uuid, file);
	}

	return children;
}

function showWarning({text, title}: {text: string; title: string}) {
	openConfirmModal({
		buttonLabel: Liferay.Language.get('done'),
		center: true,
		hideCancel: true,
		status: 'warning',
		text,
		title,
	});
}

export {StateContext, StateContextProvider, useSelector, useStateDispatch};
