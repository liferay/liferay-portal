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
import React, {Key, useEffect, useMemo, useState} from 'react';

import getLocalizedValue from '../../common/utils/getLocalizedValue';
import {useCache} from '../contexts/CacheContext';
import {
	Action,
	State,
	useSelector,
	useStateDispatch,
} from '../contexts/StateContext';
import selectInvalids from '../selectors/selectInvalids';
import selectSelection from '../selectors/selectSelection';
import selectStructure from '../selectors/selectStructure';
import selectStructureChildren from '../selectors/selectStructureChildren';
import selectStructureError from '../selectors/selectStructureError';
import selectStructureLocalizedLabel from '../selectors/selectStructureLocalizedLabel';
import selectStructureUuid from '../selectors/selectStructureUuid';
import {
	ReferencedStructure,
	RepeatableGroup,
	Structure,
	StructureChild,
} from '../types/Structure';
import {Uuid} from '../types/Uuid';
import {FIELD_TYPE_ICON, FieldType} from '../utils/field';
import isLocked from '../utils/isLocked';
import isReferenced from '../utils/isReferenced';

type TreeItem = {
	actions?: Array<{
		href?: string;
		label: string;
		onClick?: () => void;
		symbolLeft?: string;
		symbolRight?: string;
		target?: string;
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
	type?: FieldType | ReferencedStructure['type'] | RepeatableGroup['type'];
};

export default function StructureTree({search}: {search: string}) {
	const dispatch = useStateDispatch();

	const children = useSelector(selectStructureChildren);
	const invalids = useSelector(selectInvalids);
	const selection = useSelector(selectSelection);
	const structureLabel = useSelector(selectStructureLocalizedLabel);
	const structureUuid = useSelector(selectStructureUuid);
	const structureError = useSelector(selectStructureError);
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
				children: buildItems({
					children,
					dispatch,
					invalids,
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
		dispatch,
		hasReferencedStructure,
		invalids,
		objectDefinitionsStatus,
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

	useEffect(() => {
		for (const uuid of selection) {
			if (!selectedKeys.has(uuid)) {
				setSelectedKeys(new Set(selection));

				setExpandedKeys((current) => new Set([...current, uuid]));
			}
		}

		// eslint-disable-next-line
	}, [selection]);

	if (objectDefinitionsStatus === 'saving' && hasReferencedStructure) {
		return <ClayLoadingIndicator className="my-6" />;
	}

	return (
		<ClayTreeView
			className="px-4 structure-builder__tree"
			expandedKeys={expandedKeys}
			items={items}
			nestedKey="children"
			onExpandedChange={setExpandedKeys}
			onSelect={onSelect}
			onSelectionChange={setSelectedKeys}
			selectedKeys={selectedKeys}
			selectionMode={mode}
			showExpanderOnHover={false}
		>
			{(item, selectedKeys) => (
				<ClayTreeView.Item>
					<ClayTreeView.ItemStack
						className={classNames({
							active: selectedKeys.has(item.id),
						})}
						expandOnClick={false}
					>
						<ClayIcon
							className={classNames({
								'structure-builder__tree-node--field-icon':
									item.type &&
									item.type !== 'referenced-structure',
								'structure-builder__tree-node--group-icon':
									item.type === 'repeatable-group',
								'structure-builder__tree-node--structure-icon':
									item.type === 'referenced-structure',
							})}
							symbol={item.icon}
						/>

						<span className="ml-1">
							{item.label}

							<ItemStatus item={item} />
						</span>

						{item.type === 'referenced-structure' ||
						item.type === 'repeatable-group' ? (
							<ClayIcon
								className="ml-2"
								data-title={Liferay.Language.get('repeatable')}
								symbol="repeat"
							/>
						) : (
							<></>
						)}

						{item.invalid ||
						(item.id === structureUuid && structureError) ? (
							<ClayIcon
								className="ml-2 text-danger"
								data-title={Liferay.Language.get(
									'invalid-element'
								)}
								symbol="exclamation-full"
							/>
						) : (
							<></>
						)}
					</ClayTreeView.ItemStack>

					<ClayTreeView.Group items={item.children}>
						{(childItem, selectedKeys) => (
							<ClayTreeView.Item
								actions={
									childItem.actions?.length ? (
										<ClayDropDownWithItems
											items={childItem.actions}
											trigger={
												<ClayButtonWithIcon
													aria-label={Liferay.Language.get(
														'field-options'
													)}
													borderless
													disabled={
														selection.length > 1
													}
													displayType="unstyled"
													size="sm"
													symbol="ellipsis-v"
												/>
											}
										/>
									) : undefined
								}
								className={classNames({
									active: selectedKeys.has(childItem.id),
								})}
							>
								<ClayIcon
									className="structure-builder__tree-node--field-icon"
									symbol={childItem.icon}
								/>

								<span className="ml-1">
									{childItem.label}

									<ItemStatus item={childItem} />
								</span>

								{childItem.locked ? (
									<ClayIcon
										className="ml-2"
										data-title={Liferay.Language.get(
											'locked-field'
										)}
										symbol="lock"
									/>
								) : (
									<></>
								)}

								{childItem.invalid ? (
									<ClayIcon
										className="ml-2 text-danger"
										data-title={Liferay.Language.get(
											'invalid-element'
										)}
										symbol="exclamation-full"
									/>
								) : (
									<></>
								)}
							</ClayTreeView.Item>
						)}
					</ClayTreeView.Group>
				</ClayTreeView.Item>
			)}
		</ClayTreeView>
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

function useSelectionMode() {
	const [multiple, setMultiple] = useState(false);

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

			if (isMultiSelectKey(key) && !multiple) {
				setMultiple(true);
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

			if (isMultiSelectKey(key) && multiple) {
				setMultiple(false);
			}
		},
		false,

		// @ts-ignore

		window
	);

	useEventListener(
		'blur',
		() => setMultiple(false),
		false,

		// @ts-ignore

		window
	);

	return multiple ? 'multiple' : 'single';
}

function buildItems({
	children,
	dispatch,
	invalids,
	search,
	structure,
}: {
	children: (ReferencedStructure | RepeatableGroup | Structure)['children'];
	dispatch: React.Dispatch<Action>;
	invalids: State['invalids'];
	search: string;
	structure: Structure;
}): TreeItem[] {
	return Array.from(children.values()).reduce(
		(items: TreeItem[], child: StructureChild) => {
			if (
				child.type === 'referenced-structure' ||
				child.type === 'repeatable-group'
			) {
				const label = getLocalizedValue(child.label);

				const item: TreeItem = {
					actions: getItemActions({
						dispatch,
						item: child,
						structure,
					}),
					children: buildItems({
						children: child.children,
						dispatch,
						invalids,
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
							dispatch,
							item: child,
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
	dispatch,
	item,
	structure,
}: {
	dispatch: React.Dispatch<Action>;
	item: StructureChild;
	structure: Structure;
}) {
	if (isLocked(item)) {
		return [];
	}

	const actions = [];

	if (item.type === 'referenced-structure' && item.erc) {
		actions.push({
			href: item.editURL,
			label: Liferay.Language.get('edit'),
			symbolLeft: 'pencil',
			symbolRight: 'shortcut',
			target: '_blank',
		});
	}

	if (!isReferenced({item, root: structure})) {
		if (
			item.type !== 'referenced-structure' &&
			item.type !== 'repeatable-group'
		) {
			actions.push({
				label: Liferay.Language.get('create-repeatable-group'),
				onClick: () =>
					dispatch({
						type: 'add-repeatable-group',
						uuid: item.uuid,
					}),
				symbolLeft: 'repeat',
			});
		}

		if (item.type === 'repeatable-group') {
			actions.push({
				label: Liferay.Language.get('ungroup'),
				onClick: () =>
					dispatch({
						type: 'ungroup',
						uuid: item.uuid,
					}),
			});
		}

		actions.push({
			label: Liferay.Language.get('delete-field'),
			onClick: () =>
				dispatch({
					type: 'delete-child',
					uuid: item.uuid,
				}),
			symbolLeft: 'trash',
		});
	}

	return actions;
}

function hasReferencedStructureChild(
	children: (RepeatableGroup | Structure)['children']
): boolean {
	for (const child of children.values()) {
		if (child.type === 'referenced-structure') {
			return true;
		}

		if (child.type === 'repeatable-group') {
			return hasReferencedStructureChild(child.children);
		}
	}

	return false;
}
