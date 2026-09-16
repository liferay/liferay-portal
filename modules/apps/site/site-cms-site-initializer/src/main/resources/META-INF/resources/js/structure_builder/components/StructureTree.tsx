/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {TreeView as ClayTreeView} from '@clayui/core';
import {ClayDropDownWithItems} from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {useEventListener} from '@liferay/frontend-js-react-web';
import classNames from 'classnames';
import {openToast} from 'frontend-js-components-web';
import React, {Key, useEffect, useMemo, useRef, useState} from 'react';

import getLocalizedValue from '../../common/utils/getLocalizedValue';
import {useCache} from '../contexts/CacheContext';
import {
	Action,
	Clipboard,
	State,
	useSelector,
	useStateDispatch,
} from '../contexts/StateContext';
import useIsBeingRenamed from '../hooks/useIsBeingRenamed';
import selectClipboard from '../selectors/selectClipboard';
import selectHistory from '../selectors/selectHistory';
import selectInvalids from '../selectors/selectInvalids';
import selectPublishedChildren from '../selectors/selectPublishedChildren';
import selectSelection from '../selectors/selectSelection';
import selectStructure from '../selectors/selectStructure';
import selectStructureChildren from '../selectors/selectStructureChildren';
import selectStructureLocalizedLabel from '../selectors/selectStructureLocalizedLabel';
import selectStructureUuid from '../selectors/selectStructureUuid';
import {
	ReferencedStructure,
	RelatedContent,
	RepeatableGroup,
	Structure,
	StructureChild,
} from '../types/Structure';
import {Uuid} from '../types/Uuid';
import {FIELD_TYPE_ICON, FieldType} from '../utils/field';
import handleAddRepeatableGroup from '../utils/handleAddRepeatableGroup';
import handleDeleteChildren from '../utils/handleDeleteChildren';
import handleMoveChildren from '../utils/handleMoveChildren';
import handlePaste from '../utils/handlePaste';
import handleUngroupRepeatableGroup from '../utils/handleUngroupRepeatableGroup';
import isCopyable from '../utils/isCopyable';
import isField from '../utils/isField';
import isLocked from '../utils/isLocked';
import isReferenced from '../utils/isReferenced';
import isRenamable from '../utils/isRenamable';
import AddChildDropdown from './AddChildDropdown';

type TreeItem = {
	actions?: Array<{
		disabled?: boolean;
		href?: string;
		label?: string;
		onClick?: () => void;
		symbolLeft?: string;
		symbolRight?: string;
		target?: string;
		type?: 'divider';
	}>;
	children?: TreeItem[];
	editURL?: string;
	erc?: string;
	icon: string;
	id: Uuid;
	invalid?: boolean;
	label: string;
	locked?: boolean;
	name?: string;
	type?:
		| FieldType
		| ReferencedStructure['type']
		| RelatedContent['type']
		| RepeatableGroup['type'];
};

export type SelectionMode = 'multiple' | 'range' | 'single';

export default function StructureTree({search}: {search: string}) {
	const dispatch = useStateDispatch();

	const isBeingRenamed = useIsBeingRenamed();

	const children = useSelector(selectStructureChildren);
	const clipboard = useSelector(selectClipboard);
	const history = useSelector(selectHistory);
	const invalids = useSelector(selectInvalids);
	const publishedChildren = useSelector(selectPublishedChildren);
	const selection = useSelector(selectSelection);
	const structureLabel = useSelector(selectStructureLocalizedLabel);
	const structureUuid = useSelector(selectStructureUuid);
	const structure = useSelector(selectStructure);

	const {load: loadObjectDefinitions, status: objectDefinitionsStatus} =
		useCache('object-definitions');

	const mode = useSelectionMode();

	const [expandedKeys, setExpandedKeys] = useState<Set<Key>>(
		new Set([structureUuid])
	);
	const [selectedKeys, setSelectedKeys] = useState<Set<Key>>(new Set());

	const hasReferencedStructure = useMemo(
		() => hasReferencedStructureChild(children),
		[children]
	);

	const items: TreeItem[] = useMemo(() => {
		if (hasReferencedStructure && objectDefinitionsStatus !== 'saved') {
			return [];
		}

		return [
			{
				actions: getRootActions({
					clipboard,
					dispatch,
					structure,
				}),
				children: buildItems({
					children,
					clipboard,
					dispatch,
					invalids,
					publishedChildren,
					search,
					structure,
				}),
				icon: 'edit-layout',
				id: structureUuid,
				invalid: invalids.has(structureUuid),
				label: structureLabel,
				uuid: structureUuid,
			},
		];
	}, [
		children,
		clipboard,
		dispatch,
		hasReferencedStructure,
		invalids,
		objectDefinitionsStatus,
		publishedChildren,
		search,
		structure,
		structureLabel,
		structureUuid,
	]);

	const onSelect = (item: TreeItem) => {
		let nextSelection: State['selection'] = selection;

		// Item is root

		if (item.id === structureUuid) {
			nextSelection = [structureUuid];
		}

		// Selecting with selection

		else if (mode === 'single') {
			nextSelection = [item.id];
		}

		// Selecting with range selection

		else if (mode === 'range') {
			nextSelection = getRangeItems({
				items,
				rootId: structureUuid,
				selection,
				targetId: item.id,
			});
		}

		// Selecting with multiple selection

		else if (mode === 'multiple' && !selection.includes(item.id)) {
			nextSelection = [
				...selection.filter((uuid) => uuid !== structureUuid),
				item.id,
			];
		}

		// Deselecting with multiple selection

		else if (
			mode === 'multiple' &&
			selection.includes(item.id) &&
			selection.length > 1
		) {
			nextSelection = selection.filter((uuid) => uuid !== item.id);
		}

		dispatch({
			selection: nextSelection,
			type: 'set-selection',
		});
	};

	useEffect(() => {
		if (objectDefinitionsStatus === 'stale' && hasReferencedStructure) {
			loadObjectDefinitions().then((objectDefinitions) =>
				dispatch({
					objectDefinitions,
					type: 'refresh-referenced-structures',
				})
			);
		}
	}, [
		dispatch,
		hasReferencedStructure,
		loadObjectDefinitions,
		objectDefinitionsStatus,
	]);

	useEventListener(
		'keydown',
		(event) => {
			const {key, shiftKey, target} = event as KeyboardEvent;

			if (!shiftKey || (key !== 'ArrowDown' && key !== 'ArrowUp')) {
				return;
			}

			const fromId = (target as HTMLElement | null)
				?.getAttribute('data-id')
				?.split(',')[1] as Uuid | undefined;

			const toId = (document.activeElement as HTMLElement | null)
				?.getAttribute('data-id')
				?.split(',')[1] as Uuid | undefined;

			if (!toId || toId === structureUuid || toId === fromId) {
				return;
			}

			dispatch({
				selection: selection.includes(toId)
					? selection.filter((uuid) => uuid !== fromId)
					: [...selection, toId],
				type: 'set-selection',
			});
		},
		false,

		// @ts-ignore

		window
	);

	useEventListener(
		'keydown',
		(event) => {
			if ((event as KeyboardEvent).key === 'Escape') {
				dispatch({
					selection: [],
					type: 'set-selection',
				});
			}
		},
		false,

		// @ts-ignore

		window
	);

	useEffect(() => {
		const added = selection.filter((uuid) => !selectedKeys.has(uuid));

		setSelectedKeys(new Set(selection));

		if (added.length) {
			setExpandedKeys((current) => new Set([...current, ...added]));
		}

		// eslint-disable-next-line
	}, [selection]);

	if (objectDefinitionsStatus === 'saving' && hasReferencedStructure) {
		return <ClayLoadingIndicator className="my-6" />;
	}

	return (
		<ClayTreeView
			className="px-4 structure-builder__tree"
			dragAndDrop
			dragAndDropMode="multiple"
			dragHandlerVisibility="keyboard"
			expandedKeys={expandedKeys}
			itemNameKey="label"
			items={items}
			nestedKey="children"
			onExpandedChange={setExpandedKeys}
			onItemHover={(ids, target, index, position) => {
				if (position !== 'middle') {
					return false;
				}

				if (target.id === structure.uuid) {
					return true;
				}

				if (target.type !== 'group') {
					return false;
				}

				if (isReferenced({root: structure, uuid: target.id})) {
					return false;
				}

				return true;
			}}
			onItemInvalidMove={() =>
				openToast({
					message: Liferay.Language.get(
						'items-could-not-be-moved-because-the-target-is-not-allowed'
					),
					type: 'danger',
				})
			}
			onItemMove={(ids, parent) => {
				handleMoveChildren({
					deletedChildren: history.deletedChildren,
					dispatch,
					publishedChildren,
					structure,
					targetUuid: parent.id,
					uuids: [...(ids as Set<Uuid>)],
				});

				return true;
			}}
			onSelect={onSelect}
			onSelectionChange={setSelectedKeys}
			selectedKeys={selectedKeys}
			selectionMode={mode === 'single' ? 'single' : 'multiple'}
			showExpanderOnHover={false}
		>
			{(item, selectedKeys) => (
				<ClayTreeView.Item
					actions={
						item.actions?.length ? (
							<ClayDropDownWithItems
								items={item.actions}
								trigger={
									<ClayButtonWithIcon
										aria-label={Liferay.Language.get(
											'options'
										)}
										borderless
										displayType="unstyled"
										size="sm"
										symbol="ellipsis-v"
									/>
								}
							/>
						) : undefined
					}
				>
					<ClayTreeView.ItemStack
						className={classNames({
							active: selectedKeys.has(item.id),
						})}
						expandOnClick={false}
					>
						<ClayIcon
							className={classNames({
								'structure-builder__tree-node--field-icon':
									isField(item),
								'structure-builder__tree-node--group-icon':
									item.type === 'group',
								'structure-builder__tree-node--structure-icon':
									item.type === 'referenced-structure',
							})}
							symbol={item.icon}
						/>

						<ItemContent item={item} />
					</ClayTreeView.ItemStack>

					<ClayTreeView.Group items={item.children}>
						{(childItem, selectedKeys) => (
							<ClayTreeView.Item
								actions={
									isBeingRenamed(childItem.id) ? undefined : (
										<>
											{childItem.type === 'group' &&
											!isReferenced({
												root: structure,
												uuid: childItem.id,
											}) ? (
												<AddChildDropdown
													className="component-action quick-action-item"
													displayType="unstyled"
													parentUuid={childItem.id}
												/>
											) : null}

											{childItem.actions?.length ? (
												<ClayDropDownWithItems
													items={childItem.actions}
													menuElementAttrs={{
														onKeyDown: (event) => {
															if (
																event.key ===
																	'Enter' ||
																event.key ===
																	' '
															) {
																event.stopPropagation();
															}
														},
													}}
													trigger={
														<ClayButtonWithIcon
															aria-label={Liferay.Language.get(
																'field-options'
															)}
															borderless
															disabled={
																selection.length >
																1
															}
															displayType="unstyled"
															size="sm"
															symbol="ellipsis-v"
														/>
													}
												/>
											) : undefined}
										</>
									)
								}
								className={classNames({
									active: selectedKeys.has(childItem.id),
								})}
							>
								<ClayIcon
									className={classNames({
										'structure-builder__tree-node--field-icon':
											childItem.type !==
											'related-content',
										'structure-builder__tree-node--related-content-icon':
											childItem.type ===
											'related-content',
									})}
									symbol={childItem.icon}
								/>

								<ItemContent item={childItem} />
							</ClayTreeView.Item>
						)}
					</ClayTreeView.Group>
				</ClayTreeView.Item>
			)}
		</ClayTreeView>
	);
}

function ItemContent({id, item}: {id?: string; item: TreeItem}) {
	const isBeingRenamed = useIsBeingRenamed();

	if (isBeingRenamed(item.id)) {
		return <ItemNameInput item={item} />;
	}

	return (
		<div className="align-items-center c-gap-2 d-flex ml-1">
			<span>
				<ItemLabel id={id} item={item} />

				<ItemStatus item={item} />
			</span>

			{item.type === 'referenced-structure' || item.type === 'group' ? (
				<ClayIcon
					className="mt-0"
					data-title={Liferay.Language.get('repeatable')}
					symbol="repeat"
				/>
			) : null}

			{item.locked ? (
				<ClayIcon
					className="mt-0"
					data-title={Liferay.Language.get('locked-field')}
					symbol="lock"
				/>
			) : null}

			{item.invalid ? (
				<ClayIcon
					className="mt-0 text-danger"
					data-title={Liferay.Language.get('invalid-element')}
					symbol="exclamation-full"
				/>
			) : null}
		</div>
	);
}

function ItemLabel({id, item}: {id?: string; item: TreeItem}) {
	const dispatch = useStateDispatch();

	const structure = useSelector(selectStructure);

	return (
		<span
			aria-label={
				item.label ? undefined : Liferay.Language.get('untitled')
			}
			id={id}
			onDoubleClick={() => {
				if (isRenamable({structure, uuid: item.id})) {
					dispatch({type: 'set-renaming-item-uuid', uuid: item.id});
				}
			}}
		>
			{item.label}
		</span>
	);
}

function ItemNameInput({item}: {item: TreeItem}) {
	const dispatch = useStateDispatch();

	const inputRef = useRef<HTMLInputElement>(null);

	const [name, setName] = useState(item.label);

	const onFinishRenaming = () =>
		dispatch({name, type: 'rename-item', uuid: item.id});

	useEffect(() => {
		if (inputRef.current) {
			inputRef.current.focus();
		}
	}, []);

	return (
		<input
			className="structure-builder__tree-node--input"
			onBlur={() => onFinishRenaming()}
			onChange={(event) => {
				setName(event.target.value);
			}}
			onFocus={() => {
				if (!inputRef.current) {
					return;
				}

				inputRef.current.setSelectionRange(0, name.length);
			}}
			onKeyDown={(event) => {
				if (
					event.key === 'Enter' ||
					event.key === 'Escape' ||
					event.key === 'Tab'
				) {
					onFinishRenaming();
				}

				if (!event.key.match(/[a-z0-9-_ ]/gi)) {
					event.preventDefault();
				}

				event.stopPropagation();
			}}
			ref={inputRef}
			type="text"
			value={name}
		/>
	);
}

function ItemStatus({item: {invalid, locked}}: {item: TreeItem}) {
	const messages = [];

	if (locked) {
		messages.push(Liferay.Language.get('locked-field'));
	}

	if (invalid) {
		messages.push(Liferay.Language.get('invalid-element'));
	}

	if (!messages.length) {
		return null;
	}

	return <span className="sr-only">{messages.join(' ')}</span>;
}

function useSelectionMode(): SelectionMode {
	const [mode, setMode] = useState<SelectionMode>('single');

	const isMultiSelectKey = (key: string) => {
		if (Liferay.Browser.isMac()) {
			return key === 'Meta';
		}

		return key === 'Control';
	};

	useEventListener(
		'keydown',
		(event) => {
			const {key} = event as KeyboardEvent;

			if (key === 'Shift') {
				setMode('range');
			}
			else if (isMultiSelectKey(key)) {
				setMode('multiple');
			}
		},
		false,

		// @ts-ignore

		window
	);

	useEventListener(
		'keyup',
		(event) => {
			const {key} = event as KeyboardEvent;

			if (key === 'Shift' || isMultiSelectKey(key)) {
				setMode('single');
			}
		},
		false,

		// @ts-ignore

		window
	);

	useEventListener(
		'blur',
		() => setMode('single'),
		false,

		// @ts-ignore

		window
	);

	return mode;
}

export function flatItemIds(
	items: Array<{children?: any[]; id: Uuid}>
): Uuid[] {
	return items.reduce((ids: Uuid[], item) => {
		ids.push(item.id);

		if (item.children?.length) {
			ids.push(...flatItemIds(item.children));
		}

		return ids;
	}, []);
}

export function getRangeItems({
	items,
	rootId,
	selection,
	targetId,
}: {
	items: Array<{children?: any[]; id: Uuid}>;
	rootId: Uuid;
	selection: State['selection'];
	targetId: Uuid;
}): State['selection'] {
	const ids = flatItemIds(items).filter((id) => id !== rootId);

	if (!ids.includes(targetId)) {
		return [targetId];
	}

	const anchorId = selection.at(-1);

	if (!anchorId || anchorId === rootId) {
		return [targetId];
	}

	const anchorIndex = ids.indexOf(anchorId);
	const targetIndex = ids.indexOf(targetId);

	const start = Math.min(anchorIndex, targetIndex);
	const end = Math.max(anchorIndex, targetIndex);

	return ids.slice(start, end + 1);
}

function buildItems({
	children,
	clipboard,
	dispatch,
	invalids,
	publishedChildren,
	search,
	structure,
}: {
	children: (ReferencedStructure | RepeatableGroup | Structure)['children'];
	clipboard: Clipboard | null;
	dispatch: React.Dispatch<Action>;
	invalids: State['invalids'];
	publishedChildren: State['publishedChildren'];
	search: string;
	structure: Structure;
}): TreeItem[] {
	return Array.from(children.values()).reduce(
		(items: TreeItem[], child: StructureChild) => {
			if (child.type === 'related-content') {
				const label = getLocalizedValue(child.label);

				if (match(label, search)) {
					items.push({
						actions: getItemActions({
							clipboard,
							dispatch,
							item: child,
							publishedChildren,
							structure,
						}),
						icon: 'select-from-list',
						id: child.uuid,
						invalid: invalids.has(child.uuid),
						label: getLocalizedValue(child.label),
						type: 'related-content',
					});
				}
			}
			else if (
				child.type === 'referenced-structure' ||
				child.type === 'group'
			) {
				const label = getLocalizedValue(child.label);

				const item: TreeItem = {
					actions: getItemActions({
						clipboard,
						dispatch,
						item: child,
						publishedChildren,
						structure,
					}),
					children: buildItems({
						children: child.children,
						clipboard,
						dispatch,
						invalids,
						publishedChildren,
						search,
						structure,
					}),
					erc: child.erc,
					icon: 'fieldset',
					id: child.uuid,
					invalid: invalids.has(child.uuid),
					label,
					type: child.type,
				};

				if (child.type === 'referenced-structure') {
					item.icon = 'edit-layout';
					item.editURL = child.editURL;
				}

				if (match(label, search) || item.children?.length) {
					items.push(item);
				}
			}
			else {
				const label = getLocalizedValue(child.label);

				if (match(label, search)) {
					items.push({
						actions: getItemActions({
							clipboard,
							dispatch,
							item: child,
							publishedChildren,
							structure,
						}),
						icon: FIELD_TYPE_ICON[child.type],
						id: child.uuid,
						invalid: invalids.has(child.uuid),
						label: getLocalizedValue(child.label),
						locked: child.locked,
						type: child.type,
					});
				}
			}

			return items;
		},
		[]
	);
}

function match(value: string, keyword: string) {
	if (!keyword) {
		return true;
	}

	return value.toLowerCase().includes(keyword.toLowerCase());
}

function getItemActions({
	clipboard,
	dispatch,
	item,
	publishedChildren,
	structure,
}: {
	clipboard: Clipboard | null;
	dispatch: React.Dispatch<Action>;
	item: StructureChild;
	publishedChildren: State['publishedChildren'];
	structure: Structure;
}) {
	if (
		isLocked({root: structure, uuid: item.uuid}) ||
		isReferenced({root: structure, uuid: item.uuid})
	) {
		return [];
	}

	const actions: TreeItem['actions'] = [];

	if (item.type === 'referenced-structure' && item.erc) {
		actions.push({
			href: item.editURL,
			label: Liferay.Language.get('edit'),
			symbolLeft: 'pencil',
			symbolRight: 'shortcut',
			target: '_blank',
		});
	}

	if (isField(item)) {
		actions.push({
			label: Liferay.Language.get('create-repeatable-group'),
			onClick: () =>
				handleAddRepeatableGroup({
					dispatch,
					publishedChildren,
					structure,
					uuids: [item.uuid],
				}),
			symbolLeft: 'repeat',
		});

		actions.push({type: 'divider' as const});
	}

	if (item.type === 'group') {
		actions.push({
			label: Liferay.Language.get('ungroup'),
			onClick: () =>
				handleUngroupRepeatableGroup({
					dispatch,
					publishedChildren,
					uuid: item.uuid,
				}),
		});
	}

	if (isCopyable({root: structure, uuid: item.uuid})) {
		actions.push({
			label: Liferay.Language.get('copy'),
			onClick: () =>
				dispatch({type: 'copy-children', uuids: [item.uuid]}),
			symbolLeft: 'copy',
		});
	}

	actions.push({
		label: Liferay.Language.get('duplicate'),
		onClick: () =>
			dispatch({type: 'duplicate-children', uuids: [item.uuid]}),
		symbolLeft: 'copy',
	});

	if (item.type === 'group') {
		actions.push({
			disabled: !clipboard?.items.length,
			label: Liferay.Language.get('paste'),
			onClick: () =>
				handlePaste({
					clipboard,
					dispatch,
					structure,
					targetUuid: item.uuid,
				}),
			symbolLeft: 'paste',
		});
	}

	actions.push({type: 'divider' as const});

	actions.push({
		label: Liferay.Language.get('delete-field'),
		onClick: () =>
			handleDeleteChildren({
				dispatch,
				publishedChildren,
				structure,
				uuids: [item.uuid],
			}),
		symbolLeft: 'trash',
	});

	return actions;
}

function getRootActions({
	clipboard,
	dispatch,
	structure,
}: {
	clipboard: Clipboard | null;
	dispatch: React.Dispatch<Action>;
	structure: Structure;
}): TreeItem['actions'] {
	return [
		{
			disabled: !clipboard?.items.length,
			label: Liferay.Language.get('paste'),
			onClick: () =>
				handlePaste({
					clipboard,
					dispatch,
					structure,
					targetUuid: structure.uuid,
				}),
			symbolLeft: 'paste',
		},
	];
}

function hasReferencedStructureChild(
	children: (RepeatableGroup | Structure)['children']
): boolean {
	for (const child of children.values()) {
		if (child.type === 'referenced-structure') {
			return true;
		}

		if (child.type === 'group') {
			return hasReferencedStructureChild(child.children);
		}
	}

	return false;
}
