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
import getLocalizedValue from '../../common/utils/getLocalizedValue';
import {ObjectDefinitions} from '../types/ObjectDefinition';
import {
	ReferencedStructure,
	RepeatableGroup,
	Structure,
	StructureChild,
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
	ErrorMap,
	ValidationError,
	ValidationProperty,
	validateField,
	validateRepeatableGroup,
	validateStructure,
} from '../utils/validation';

type UndeletableReason = 'is-locked' | 'is-referenced' | 'causes-invalid-group';

type History = {
	deletedChildren: boolean;
	modifiedNames: Set<Uuid>;
};

export type State = {
	history: History;
	invalids: Map<Uuid, ErrorMap>;
	publishedChildren: Set<Uuid>;
	selection: Uuid[];
	structure: Structure;
	unsavedChanges: boolean;
};

const INITIAL_STATE: State = {
	history: {
		deletedChildren: false,
		modifiedNames: new Set(),
	},
	invalids: new Map(),
	publishedChildren: new Set(),
	selection: [],
	structure: {
		children: new Map(),
		erc: '',
		label: {},
		name: '',
		spaces: 'all',
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

type AddRepeatableGroupAction = {
	type: 'add-repeatable-group';
	uuid?: Uuid;
};

type AddErrorAction = {
	error: ValidationError;
	property: ValidationProperty;
	type: 'add-error';
	uuid: Uuid;
};

type ClearErrorsAction = {
	type: 'clear-errors';
};

type CreateStructureAction = {
	id: number;
	type: 'create-structure';
};

type DeleteChildAction = {type: 'delete-child'; uuid: Uuid};

type DeleteSelectionAction = {type: 'delete-selection'};

type PublishStructureAction = {id?: number; type: 'publish-structure'};

type RefreshReferencedStructuresAction = {
	objectDefinitions: ObjectDefinitions;
	type: 'refresh-referenced-structures';
};

type SetSelectionAction = {
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
	objectDefinitions?: ObjectDefinitions;
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
	| AddRepeatableGroupAction
	| AddErrorAction
	| ClearErrorsAction
	| CreateStructureAction
	| DeleteChildAction
	| DeleteSelectionAction
	| PublishStructureAction
	| RefreshReferencedStructuresAction
	| SetSelectionAction
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

			const items = uuids.map(
				(uuid) => findChild({root: structure, uuid})!
			);

			const undeletables = getUndeletableItems(items, structure);

			const reasons = [...undeletables.values()];

			if (reasons.includes('is-locked')) {
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
				reasons.includes('is-referenced') ||
				items.some(({uuid}) => publishedChildren.has(uuid))
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

			if (reasons.includes('causes-invalid-group')) {
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

			const parents = items.map(
				(item) =>
					findChild({
						root: structure,
						uuid: item.parent,
					}) || structure
			);

			const isSameParent = new Set(parents).size === 1;

			if (!isSameParent) {
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

			const groupUuid = getUuid();

			const children = insertGroup({
				groupChildren: items,
				groupParent: parents[0].uuid,
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
		case 'add-error': {
			const {error, property, uuid} = action;

			const invalids = new Map(state.invalids);

			const errors = new Map(invalids.get(uuid));

			errors.set(property, error);

			invalids.set(uuid, errors);

			return {
				...state,
				invalids,
			};
		}
		case 'clear-errors': {
			return {
				...state,
				invalids: new Map(),
			};
		}
		case 'create-structure': {
			const {structure} = state;

			return {
				...state,
				invalids: new Map(),
				structure: {
					...structure,
					id: action.id,
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

			const undeletables = getUndeletableItems([child], structure);

			if (undeletables.get(child.uuid) === 'causes-invalid-group') {
				showWarning({
					text: Liferay.Language.get(
						'you-must-keep-at-least-one-field-in-a-repeatable-group'
					),
					title: Liferay.Language.get('deletion-not-allowed'),
				});

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

			const undeletables = getUndeletableItems(items, structure);

			if (undeletables.size) {
				showWarning({
					text: Liferay.Language.get(
						'one-or-more-selected-fields-are-system-or-referenced-fields'
					),
					title: Liferay.Language.get(
						'some-fields-could-not-be-deleted'
					),
				});
			}

			const nextChildren = deleteChildren({
				root: structure,
				uuids: selection.filter((uuid) => !undeletables.has(uuid)),
			});

			return {
				...state,
				selection: [...undeletables.keys()],
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
				id: action.id || structure.id,
				status: 'published' as Structure['status'],
			};

			return {
				...state,
				history: INITIAL_STATE.history,
				invalids: new Map(),
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

			const {history, publishedChildren, structure} = state;

			const field = findChild({root: structure, uuid}) as Field;

			if (!field) {
				return state;
			}

			// If name is being updated manually, mark it

			const modifiedNames = new Set(history.modifiedNames);

			if (name && name !== field.name) {
				modifiedNames.add(field.uuid);
			}

			// Calculate new name

			let nextName = field.name;

			if (!publishedChildren.has(field.uuid)) {
				nextName = getNextName({action, item: field, modifiedNames});
			}

			// Prepare updated field

			const nextField: Field = {
				...field,
				erc: erc ?? field.erc,
				indexableConfig: indexableConfig ?? field.indexableConfig,
				label: label ?? field.label,
				localized: localized ?? field.localized,
				name: nextName,
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
				children: structure.children,
				currentErrors: invalids.get(nextField.uuid),
				data: {
					...data,
					name: nextName,
				},
				uuid: nextField.uuid,
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
				history: {
					...history,
					modifiedNames,
				},
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
				invalids.set(group.uuid, errors);
			}
			else {
				invalids.delete(group.uuid);
			}

			// Return new state

			return {
				...nextState,
				invalids,
			};
		}
		case 'update-structure': {

			// Prepare updated state

			const {erc, label, name, objectDefinitions, spaces} = action;

			const {history, structure} = state;

			// If name is being updated manually, mark it

			const modifiedNames = new Set(history.modifiedNames);

			if (name && name !== structure.name) {
				modifiedNames.add(structure.uuid);
			}

			// Calculate new name

			let nextName = structure.name;

			if (structure.status !== 'published') {
				nextName = getNextName({
					action,
					item: structure,
					modifiedNames,
				});
			}

			const nextState: State = {
				...state,
				history: {
					...history,
					modifiedNames,
				},
				structure: {
					...state.structure,
					erc: erc ?? structure.erc,
					label: label ?? structure.label,
					name: nextName,
					spaces: spaces ?? structure.spaces,
				},
			};

			// Validate the data sent in the action

			const invalids = new Map(state.invalids);

			const errors = validateStructure({
				currentErrors: invalids.get(structure.uuid),
				data: {erc, label, name: nextName, spaces},
				objectDefinitions,
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

function getNextName({
	action,
	item,
	modifiedNames,
}: {
	action: UpdateStructureAction | UpdateFieldAction;
	item: Structure | Field;
	modifiedNames: State['history']['modifiedNames'];
}): string {
	if ('name' in action) {
		return action.name!;
	}

	if (!action.label || modifiedNames.has(item.uuid)) {
		return item.name;
	}

	const localizedLabel = getLocalizedValue(action.label);

	return normalizeName(localizedLabel, {
		style: 'type' in item ? 'camel' : 'pascal',
	});
}

function getUndeletableItems(
	items: StructureChild[],
	structure: Structure
): Map<Uuid, UndeletableReason> {
	const undeletables = new Map<Uuid, UndeletableReason>();

	for (const item of items) {
		if (isLocked(item)) {
			undeletables.set(item.uuid, 'is-locked');
		}

		if (isReferenced({item, root: structure})) {
			undeletables.set(item.uuid, 'is-referenced');
		}

		const parent = findChild({
			root: structure,
			uuid: item.parent,
		});

		if (parent?.type === 'repeatable-group') {
			const groupFields = Array.from(parent.children.values()).filter(
				(child) =>
					child.type !== 'referenced-structure' &&
					child.type !== 'repeatable-group'
			);

			const fields = items.filter(
				(item) =>
					item.type !== 'referenced-structure' &&
					item.type !== 'repeatable-group'
			);

			if (
				groupFields.every(({uuid}) =>
					fields.some((field) => field.uuid === uuid)
				)
			) {
				groupFields.forEach((field) => {
					undeletables.set(field.uuid, 'causes-invalid-group');
				});
			}
		}
	}

	return undeletables;
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
