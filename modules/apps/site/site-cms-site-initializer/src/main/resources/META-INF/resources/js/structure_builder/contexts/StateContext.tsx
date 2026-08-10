/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {normalizeFriendlyURL} from 'frontend-js-web';
import React, {
	Dispatch,
	ReactNode,
	createContext,
	useContext,
	useReducer,
} from 'react';

import {ObjectDefinitions} from '../../common/types/ObjectDefinition';
import {Space} from '../../common/types/Space';
import {Workflow} from '../../common/types/Workflow';
import getLocalizedValue from '../../common/utils/getLocalizedValue';
import {
	ReferencedStructure,
	RelatedContent,
	RepeatableGroup,
	Structure,
	StructureChild,
	StructureType,
} from '../types/Structure';
import {Uuid} from '../types/Uuid';
import actionGeneratesChanges from '../utils/actionGeneratesChanges';
import {Field, SelectFromListField, getDefaultField} from '../utils/field';
import findAvailableFieldName from '../utils/findAvailableFieldName';
import findChild from '../utils/findChild';
import {getChildrenUuids} from '../utils/getChildrenUuids';
import getRandomId from '../utils/getRandomId';
import getUuid from '../utils/getUuid';
import normalizeString from '../utils/normalizeString';
import addChild from '../utils/state/addChild';
import addRepeatableGroup from '../utils/state/addRepeatableGroup';
import cloneChild from '../utils/state/cloneChild';
import deleteChildren from '../utils/state/deleteChildren';
import moveChildren from '../utils/state/moveChildren';
import refreshReferencedStructures from '../utils/state/refreshReferencedStructures';
import sortChildren from '../utils/state/sortChildren';
import ungroup from '../utils/state/ungroupRepeatableGroup';
import updateChild from '../utils/state/updateChild';
import updateHistory from '../utils/state/updateHistory';
import {
	ErrorMap,
	ValidationError,
	ValidationProperty,
	validateField,
	validateRelatedContent,
	validateRepeatableGroup,
	validateStructure,
} from '../utils/validation';

type History = {
	deletedChildren: Array<StructureChild>;
	deletedGroupERCs: Array<RepeatableGroup['erc']>;
	deletedRelationships: Array<{
		relationshipERC: string;
		structureERC: string;
	}>;
	modifiedNames: Set<Uuid>;
	modifiedSlugs: Set<Uuid>;
};

export type Clipboard = {
	items: StructureChild[];
};

export type State = {
	clipboard: Clipboard | null;
	history: History;
	invalids: Map<Uuid, ErrorMap>;
	publishedChildren: Set<Uuid>;
	renamingItemUuid: Uuid | null;
	savedChildren: Set<Uuid>;
	selection: Uuid[];
	structure: Structure;
	unsavedChanges: boolean;
};

const INITIAL_STATE: State = {
	clipboard: null,
	history: {
		deletedChildren: [],
		deletedGroupERCs: [],
		deletedRelationships: [],
		modifiedNames: new Set(),
		modifiedSlugs: new Set(),
	},
	invalids: new Map(),
	publishedChildren: new Set(),
	renamingItemUuid: null,
	savedChildren: new Set(),
	selection: [],
	structure: {
		children: new Map(),
		erc: '',
		label: {},
		name: '',
		path: '',
		settings: {},
		slug: '',
		spaces: 'all',
		status: 'new',
		system: false,
		type: 'L_CMS_CONTENT_STRUCTURES',
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

type AddRelatedContentAction = {
	relatedContent: RelatedContent;
	type: 'add-related-content';
};

type AddRepeatableGroupAction = {
	type: 'add-repeatable-group';
	uuids: Uuid[];
};

type AddErrorAction = {
	error: ValidationError;
	property: ValidationProperty;
	status?: Structure['status'];
	type: 'add-error';
	uuid: Uuid;
};

type ClearErrorsAction = {
	type: 'clear-errors';
};

type CopyChildrenAction = {type: 'copy-children'; uuids: Uuid[]};

type CreateStructureAction = {
	id: number;
	type: 'create-structure';
};

type DeleteChildrenAction = {type: 'delete-children'; uuids: Uuid[]};

type DuplicateChildrenAction = {type: 'duplicate-children'; uuids: Uuid[]};

type MoveChildrenAction = {
	items: StructureChild[];
	targetUuid: Uuid;
	type: 'move-children';
};

type PasteAction = {targetUuid: Uuid; type: 'paste'};

type PublishStructureAction = {id?: number; type: 'publish-structure'};

type RefreshReferencedStructuresAction = {
	objectDefinitions: ObjectDefinitions;
	type: 'refresh-referenced-structures';
};

type RenameItemAction = {
	name: string;
	type: 'rename-item';
	uuid: Uuid;
};

type SaveStructureAction = {type: 'save-structure'};

type SetRenamingItemUuidAction = {
	type: 'set-renaming-item-uuid';
	uuid: Uuid;
};

type SetSelectionAction = {
	selection: State['selection'];
	type: 'set-selection';
};

type SetStructureStatusAction = {
	status: Structure['status'];
	type: 'set-structure-status';
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
	multiselection?: boolean;
	name?: string;
	newName?: string;
	picklistId?: number;
	required?: boolean;
	settings?: Field['settings'];
	type: 'update-field';
	uuid: Uuid;
};

type UpdateRelatedContentAction = {
	erc?: string;
	label?: Liferay.Language.LocalizedValue<string>;
	multiselection?: boolean;
	relatedStructureERC?: string;
	type: 'update-related-content';
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
	slug?: string;
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
	| AddRelatedContentAction
	| AddRepeatableGroupAction
	| AddErrorAction
	| ClearErrorsAction
	| CopyChildrenAction
	| CreateStructureAction
	| DeleteChildrenAction
	| DuplicateChildrenAction
	| MoveChildrenAction
	| PasteAction
	| PublishStructureAction
	| RefreshReferencedStructuresAction
	| RenameItemAction
	| SaveStructureAction
	| SetRenamingItemUuidAction
	| SetSelectionAction
	| SetStructureStatusAction
	| SetWorkflowAction
	| UngroupAction
	| UpdateFieldAction
	| UpdateRelatedContentAction
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

			let parent: Structure | RepeatableGroup = structure;

			if (field.parent !== structure.uuid) {
				const item = findChild({root: structure, uuid: field.parent});

				if (item?.type === 'repeatable-group') {
					parent = item;
				}
			}

			const nextField = {
				...field,
				name: findAvailableFieldName(
					parent.children,
					state.history.deletedChildren,
					field.name
				),
			};

			const children = addChild({
				child: nextField,
				root: structure,
			});

			return {
				...state,
				selection: [field.uuid],
				structure: {
					...structure,
					children,
				},
			};
		}
		case 'add-referenced-structures': {
			const {referencedStructures} = action;

			const {publishedChildren, savedChildren, structure} = state;

			let children = new Map(structure.children);

			let nextSavedChildren = new Set(savedChildren);
			let nextPublishedChildren = new Set(publishedChildren);

			let selection: State['selection'] = [];

			for (const [
				i,
				referencedStructure,
			] of referencedStructures.entries()) {
				children = addChild({
					child: referencedStructure,
					root: {...structure, children},
				});

				const referencedStructureChildrenUuids = getChildrenUuids({
					root: referencedStructure,
				});

				nextSavedChildren = new Set([
					...nextSavedChildren,
					...referencedStructureChildrenUuids,
				]);
				nextPublishedChildren = new Set([
					...nextPublishedChildren,
					...referencedStructureChildrenUuids,
				]);

				if (i === 0) {
					selection = [referencedStructure.uuid];
				}
			}

			const sortedChildren = sortChildren(children);

			return {
				...state,
				publishedChildren: nextPublishedChildren,
				savedChildren: nextSavedChildren,
				selection,
				structure: {...structure, children: sortedChildren},
			};
		}
		case 'add-related-content': {
			const {relatedContent} = action;

			const {structure} = state;

			const children = addChild({
				child: relatedContent,
				root: structure,
			});

			const sortedChildren = sortChildren(children);

			return {
				...state,
				selection: [relatedContent.uuid],
				structure: {...structure, children: sortedChildren},
			};
		}
		case 'add-repeatable-group': {
			const {history, savedChildren, structure} = state;

			const {uuids} = action;

			const items = uuids.map(
				(uuid) => findChild({root: structure, uuid})!
			);

			const groupUuid = getUuid();

			const children = addRepeatableGroup({
				groupChildren: items,
				groupParent: items[0].parent,
				groupUuid,
				root: structure,
			});

			const deletedChildrenUuids = new Set<Uuid>();

			for (const item of items) {
				if (savedChildren.has(item.uuid)) {
					deletedChildrenUuids.add(item.uuid);
				}
			}

			return {
				...state,
				history: deletedChildrenUuids.size
					? updateHistory({
							deletedChildrenUuids,
							initialHistory: history,
							savedChildren,
							structure,
						})
					: history,
				selection: [groupUuid],
				structure: {...structure, children},
			};
		}
		case 'add-error': {
			const {error, property, status, uuid} = action;

			const invalids = new Map(state.invalids);

			const errors = new Map(invalids.get(uuid));

			errors.set(property, error);

			invalids.set(uuid, errors);

			return {
				...state,
				...(status && {structure: {...state.structure, status}}),
				invalids,
			};
		}
		case 'clear-errors': {
			return {
				...state,
				invalids: new Map(),
			};
		}
		case 'copy-children': {
			const items = action.uuids
				.map((uuid) => findChild({root: state.structure, uuid}))
				.filter((item): item is StructureChild => Boolean(item));

			if (!items.length) {
				return state;
			}

			return {
				...state,
				clipboard: {items},
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
		case 'delete-children': {
			const {uuids} = action;

			const {structure} = state;

			const {deletedChildrenUuids, updatedChildren: nextChildren} =
				deleteChildren({
					root: structure,
					uuids,
				});

			const invalids = new Map(state.invalids);

			for (const deletedChild of deletedChildrenUuids) {
				invalids.delete(deletedChild);
			}

			return {
				...state,
				history: updateHistory({
					deletedChildrenUuids,
					initialHistory: state.history,
					savedChildren: state.savedChildren,
					structure,
				}),
				invalids,
				selection: [],
				structure: {
					...structure,
					children: nextChildren,
				},
			};
		}
		case 'duplicate-children': {
			const {uuids} = action;

			let nextStructure = state.structure;

			const newSelection: Uuid[] = [];

			for (const uuid of uuids) {
				const child = findChild({root: nextStructure, uuid});

				if (!child) {
					continue;
				}

				const parent = (findChild({
					root: nextStructure,
					uuid: child.parent,
				}) || nextStructure) as Structure | RepeatableGroup;

				const copy = cloneChild({
					child,
					deletedChildren: state.history.deletedChildren,
					parent: parent.uuid,
					siblings: parent.children,
				});

				const updatedChildren = addChild({
					child: copy,
					root: nextStructure,
				});

				nextStructure = {...nextStructure, children: updatedChildren};

				newSelection.push(copy.uuid);
			}

			return {
				...state,
				selection: newSelection.length ? newSelection : state.selection,
				structure: nextStructure,
			};
		}
		case 'move-children': {
			const {items, targetUuid} = action;

			const {history, savedChildren, structure} = state;

			const children = moveChildren({
				items,
				root: structure,
				targetUuid,
			});

			const deletedChildrenUuids = new Set<Uuid>();

			for (const item of items) {
				if (savedChildren.has(item.uuid)) {
					deletedChildrenUuids.add(item.uuid);
				}
			}

			return {
				...state,
				history: deletedChildrenUuids.size
					? updateHistory({
							deletedChildrenUuids,
							initialHistory: history,
							savedChildren,
							structure,
						})
					: history,
				structure: {
					...structure,
					children,
				},
			};
		}
		case 'paste': {
			const {clipboard, history, structure} = state;

			const {targetUuid} = action;

			if (!clipboard?.items.length) {
				return state;
			}

			let nextStructure = structure;

			const newSelection: Uuid[] = [];

			for (const item of clipboard.items) {
				const clone = cloneChild({
					child: item,
					deletedChildren: history.deletedChildren,
					parent: targetUuid,
					siblings: getTargetChildren({
						structure: nextStructure,
						targetUuid,
					}),
				});

				const updatedChildren = addChild({
					child: clone,
					root: nextStructure,
				});

				nextStructure = {...nextStructure, children: updatedChildren};

				newSelection.push(clone.uuid);
			}

			return {
				...state,
				selection: newSelection,
				structure: nextStructure,
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
				savedChildren: getChildrenUuids({root: structure}),
				structure: nextStructure,
				unsavedChanges: false,
			};
		}
		case 'save-structure': {
			const {structure} = state;

			return {
				...state,
				history: INITIAL_STATE.history,
				savedChildren: getChildrenUuids({root: structure}),
				structure: {
					...structure,
					status: 'draft' as Structure['status'],
				},
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
		case 'rename-item': {
			const {name, uuid} = action;
			const {structure} = state;

			const languageId = Liferay.ThemeDisplay.getLanguageId();

			if (uuid === structure.uuid) {
				return {
					...state,
					renamingItemUuid: null,
					structure: {
						...structure,
						label: {...structure.label, [languageId]: name},
					},
				};
			}

			const child = findChild({root: structure, uuid});

			if (!child) {
				return state;
			}

			const children = updateChild({
				child: {
					...child,
					label: {...child.label, [languageId]: name},
				},
				root: structure,
			});

			return {
				...state,
				renamingItemUuid: null,
				structure: {
					...structure,
					children,
				},
			};
		}
		case 'set-renaming-item-uuid': {
			const {uuid} = action;

			return {...state, renamingItemUuid: uuid};
		}
		case 'set-selection': {
			const {selection} = action;

			return {...state, selection};
		}
		case 'set-structure-status': {
			const {status} = action;

			return {
				...state,
				structure: {
					...state.structure,
					status,
				},
			};
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
			const {structure} = state;

			const {uuid} = action;

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
				multiselection,
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

			if (multiselection !== undefined) {
				(nextField as SelectFromListField).multiselection =
					multiselection;
			}

			if (picklistId) {
				(nextField as SelectFromListField).picklistId = picklistId;
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
				deletedChildren: state.history.deletedChildren,
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
		case 'update-related-content': {
			const {erc, label, multiselection, relatedStructureERC, uuid} =
				action;

			const {structure} = state;

			const relatedContent = findChild({
				root: structure,
				uuid,
			}) as RelatedContent;

			if (!relatedContent) {
				return state;
			}

			// Prepare updated field

			const nextRelatedContent: RelatedContent = {
				...relatedContent,
				erc: erc ?? relatedContent.erc,
				label: label ?? relatedContent.label,
				multiselection: multiselection ?? relatedContent.multiselection,
				relatedStructureERC:
					relatedStructureERC ?? relatedContent.relatedStructureERC,
			};

			const nextChildren = updateChild({
				child: nextRelatedContent,
				root: structure,
			});

			// Validate the data sent in the action

			const invalids = new Map(state.invalids);

			const errors = validateRelatedContent({
				currentErrors: invalids.get(nextRelatedContent.uuid),
				data: {
					erc,
					label,
					relatedStructureERC,
				},
			});

			if (errors.size) {
				invalids.set(nextRelatedContent.uuid, errors);
			}
			else {
				invalids.delete(nextRelatedContent.uuid);
			}

			// Return new state

			return {
				...state,
				invalids,
				selection: [nextRelatedContent.uuid],
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

			const {erc, label, name, objectDefinitions, slug, spaces} = action;

			const {history, structure} = state;

			// If name is being updated manually, mark it

			const modifiedNames = new Set(history.modifiedNames);

			if (name && name !== structure.name) {
				modifiedNames.add(structure.uuid);
			}

			// If the slug is being updated manually, mark it so it stops
			// tracking the label. An empty value resumes auto-generation.

			const modifiedSlugs = new Set(history.modifiedSlugs);

			if (slug !== undefined) {
				if (slug) {
					modifiedSlugs.add(structure.uuid);
				}
				else {
					modifiedSlugs.delete(structure.uuid);
				}
			}

			// Calculate new name

			const isPublished = structure.status === 'published';
			let nextName = structure.name;

			if (!isPublished) {
				nextName = getNextName({
					action,
					item: structure,
					modifiedNames,
				});
			}

			// Calculate new slug

			const nextSlug = getNextSlug({
				action,
				isPublished,
				modifiedSlugs,
				structure,
			});

			const nextState: State = {
				...state,
				history: {
					...history,
					modifiedNames,
					modifiedSlugs,
				},
				structure: {
					...state.structure,
					erc: erc ?? structure.erc,
					label: label ?? structure.label,
					name: nextName,
					slug: nextSlug,
					spaces: spaces ?? structure.spaces,
				},
			};

			// Validate the data sent in the action

			const invalids = new Map(state.invalids);

			const errors = validateStructure({
				currentErrors: invalids.get(structure.uuid),
				data: {
					erc,
					label,
					spaces,
					...(!isPublished &&
						nextName !== structure.name && {name: nextName}),
				},
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
			type: getType(),
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
	const type = getType();

	const children = new Map();

	const title = getDefaultField({
		languageKey: 'title',
		locked: true,
		name: 'title',
		parent: structureUuid,
		required: true,
		type: 'text',
	});

	children.set(title.uuid, title);

	if (type === 'L_CMS_FILE_TYPES') {
		const file = getDefaultField({
			languageKey: 'file',
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

function getNextSlug({
	action,
	isPublished,
	modifiedSlugs,
	structure,
}: {
	action: UpdateStructureAction;
	isPublished: boolean;
	modifiedSlugs: State['history']['modifiedSlugs'];
	structure: Structure;
}): string {
	if ('slug' in action) {
		return action.slug!;
	}

	if (isPublished || !action.label || modifiedSlugs.has(structure.uuid)) {
		return structure.slug;
	}

	return normalizeFriendlyURL(getLocalizedValue(action.label));
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

	if (
		!action.label ||
		modifiedNames.has(item.uuid) ||
		('locked' in item && item.locked)
	) {
		return item.name;
	}

	const localizedLabel = getLocalizedValue(action.label);

	return normalizeString(localizedLabel, {
		style: 'status' in item ? 'pascal' : 'camel',
	});
}

function getTargetChildren({
	structure,
	targetUuid,
}: {
	structure: Structure;
	targetUuid: Uuid;
}): Structure['children'] {
	if (targetUuid === structure.uuid) {
		return structure.children;
	}

	const target = findChild({root: structure, uuid: targetUuid});

	if (
		target &&
		(target.type === 'repeatable-group' ||
			target.type === 'referenced-structure')
	) {
		return target.children;
	}

	return new Map();
}

function getType() {
	const url = new URL(window.location.href);

	return url.searchParams.get(
		'objectFolderExternalReferenceCode'
	) as StructureType;
}

export {StateContext, StateContextProvider, useSelector, useStateDispatch};
