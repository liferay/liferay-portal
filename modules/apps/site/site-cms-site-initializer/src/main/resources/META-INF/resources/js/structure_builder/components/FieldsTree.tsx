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

import {
	FIELD_TYPE_ICON,
	Field,
	FieldType,
} from '../../structure_builder/utils/field';
import {useCache} from '../contexts/CacheContext';
import {State, useSelector, useStateDispatch} from '../contexts/StateContext';
import selectInvalids from '../selectors/selectInvalids';
import selectSelection from '../selectors/selectSelection';
import selectStructureERC from '../selectors/selectStructureERC';
import selectStructureError from '../selectors/selectStructureError';
import selectStructureFields from '../selectors/selectStructureFields';
import selectStructureLocalizedLabel from '../selectors/selectStructureLocalizedLabel';
import selectStructureUuid from '../selectors/selectStructureUuid';
import {ReferencedStructure, Structure, Structures} from '../types/Structure';
import {Uuid} from '../types/Uuid';
import getReferencedStructureLabel from '../utils/getReferencedStructureLabel';
import getStructureEditURL from '../utils/getStructureEditURL';

type TreeItem = {
	children?: TreeItem[];
	erc?: string;
	icon: string;
	id: string;
	label: string;
	name?: string;
	type?: FieldType | 'referenced-structure';
	uuid: Uuid;
};

export default function FieldsTree({search}: {search: string}) {
	const dispatch = useStateDispatch();

	const fields = useSelector(selectStructureFields);
	const invalids = useSelector(selectInvalids);
	const selection = useSelector(selectSelection);
	const structureLabel = useSelector(selectStructureLocalizedLabel);
	const structureUuid = useSelector(selectStructureUuid);
	const structureError = useSelector(selectStructureError);
	const structureERC = useSelector(selectStructureERC);

	const {
		data: structures,
		load: loadStructures,
		status: structuresStatus,
	} = useCache('structures');

	const mode = useSelectionMode();

	const [expandedKeys, setExpandedKeys] = useState<Set<Key>>(
		new Set([structureUuid])
	);
	const [selectedKeys, setSelectedKeys] = useState<Set<Key>>(new Set());

	const hasReferencedStructure = fields.some(
		({type}) => type === 'referenced-structure'
	);

	const items: TreeItem[] = useMemo(() => {
		if (hasReferencedStructure && structuresStatus !== 'saved') {
			return [];
		}

		return [
			{
				children: buildItems(fields, structures, structureERC, search),
				icon: 'edit-layout',
				id: structureUuid,
				label: structureLabel,
				uuid: structureUuid,
			},
		];
	}, [
		fields,
		hasReferencedStructure,
		search,
		structureERC,
		structureLabel,
		structureUuid,
		structures,
		structuresStatus,
	]);

	const onSelect = (item: TreeItem) => {
		let nextSelection: State['selection'] = selection;

		// Item is root

		if (item.id === structureUuid) {
			nextSelection = [structureUuid];
		}

		// Selecting with selection

		else if (mode === 'single') {
			nextSelection = [item.uuid];
		}

		// Selecting with multiple selection

		else if (mode === 'multiple' && !selection.includes(item.uuid)) {
			nextSelection = [
				...selection.filter((uuid) => uuid !== structureUuid),
				item.uuid,
			];
		}

		// Deselecting with multiple selection

		else if (
			mode === 'multiple' &&
			selection.includes(item.uuid) &&
			selection.length > 1
		) {
			nextSelection = selection.filter((id) => id !== item.id);
		}

		dispatch({
			selection: nextSelection,
			type: 'set-selection',
		});
	};

	const deleteField = (uuid: Uuid) =>
		dispatch({
			type: 'delete-field',
			uuid,
		});

	useEffect(() => {
		if (structuresStatus === 'stale' && hasReferencedStructure) {
			loadStructures();
		}
	}, [hasReferencedStructure, loadStructures, structuresStatus]);

	if (structuresStatus === 'saving' && hasReferencedStructure) {
		return <ClayLoadingIndicator className="my-6" />;
	}

	return (
		<ClayTreeView
			className="px-4 structure-builder__fields-tree"
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
								'structure-builder__fields-tree-node--field-icon':
									item.type &&
									item.type !== 'referenced-structure',
								'structure-builder__fields-tree-node--structure-icon':
									item.type === 'referenced-structure',
							})}
							symbol={item.icon}
						/>

						<span className="ml-1">{item.label}</span>

						{invalids.has(item.uuid) ||
						(item.id === structureUuid && structureError) ? (
							<ClayIcon
								className="ml-2 text-danger"
								symbol="exclamation-full"
							/>
						) : (
							<></>
						)}
					</ClayTreeView.ItemStack>

					<ClayTreeView.Group items={item.children}>
						{(childItem, selectedKeys) => {
							const actions = getItemActions({
								item: childItem,
								onDelete: deleteField,
								parent: item,
								structures,
							});

							return (
								<ClayTreeView.Item
									actions={
										actions.length ? (
											<ClayDropDownWithItems
												items={actions}
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
										className="structure-builder__fields-tree-node--field-icon"
										symbol={childItem.icon}
									/>

									<span className="ml-1">
										{childItem.label}
									</span>

									{invalids.has(childItem.uuid) ? (
										<ClayIcon
											className="ml-2 text-danger"
											symbol="exclamation-full"
										/>
									) : (
										<></>
									)}
								</ClayTreeView.Item>
							);
						}}
					</ClayTreeView.Group>
				</ClayTreeView.Item>
			)}
		</ClayTreeView>
	);
}

function useSelectionMode() {
	const [multiple, setMultiple] = useState(false);

	useEventListener(
		'keydown',
		(event) => {
			const {key} = event as KeyboardEvent;

			if (key === 'Control' || key === 'Meta') {
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

			if (key === 'Control' || key === 'Meta') {
				setMultiple(false);
			}
		},
		false,

		// @ts-ignore

		window
	);

	return multiple ? 'multiple' : 'single';
}

function buildItems(
	fields: (Field | ReferencedStructure)[],
	structures: Structures,
	structureERC: Structure['erc'],
	search: string,
	path: string[] = []
): TreeItem[] {
	return fields.reduce(
		(items: TreeItem[], field: Field | ReferencedStructure) => {
			if (field.type === 'referenced-structure') {
				const structure = structures.get(field.erc)!;
				const label = getReferencedStructureLabel(
					field.erc,
					structures
				);

				const item = {
					children:
						field.erc === structureERC
							? []
							: buildItems(
									selectStructureFields(structure),
									structures,
									structureERC,
									search,
									[...path, field.name]
								),
					erc: field.erc,
					icon: 'edit-layout',
					id: buildId(path, field),
					label: getReferencedStructureLabel(field.erc, structures),
					type: field.type,
					uuid: field.uuid,
				};

				if (match(label, search) || item.children.length) {
					items.push(item);
				}
			}
			else {
				const label =
					field.label[Liferay.ThemeDisplay.getDefaultLanguageId()]!;

				if (match(label, search)) {
					items.push({
						icon: FIELD_TYPE_ICON[field.type],
						id: buildId(path, field),
						label: field.label[
							Liferay.ThemeDisplay.getDefaultLanguageId()
						]!,
						type: field.type,
						uuid: field.uuid,
					});
				}
			}

			return items;
		},
		[]
	);
}

function buildId(path: string[], field: Field | ReferencedStructure) {
	return [...path, field.name].join('_');
}

function match(value: string, keyword: string) {
	if (!keyword) {
		return true;
	}

	return value.toLowerCase().includes(keyword.toLowerCase());
}

function getItemActions({
	item,
	onDelete,
	parent,
	structures,
}: {
	item: TreeItem;
	onDelete: (id: Uuid) => void;
	parent: TreeItem;
	structures: Structures;
}) {
	const actions = [];

	if (item.type === 'referenced-structure' && item.erc) {
		const structure = structures.get(item.erc);

		if (structure) {
			actions.push({
				href: getStructureEditURL(structure),
				label: Liferay.Language.get('edit'),
				symbolLeft: 'pencil',
				symbolRight: 'shortcut',
				target: '_blank',
			});
		}
	}

	if (parent.type !== 'referenced-structure') {
		actions.push({
			label: Liferay.Language.get('delete-field'),
			onClick: () => onDelete(item.uuid),
			symbolLeft: 'trash',
		});
	}

	return actions;
}
