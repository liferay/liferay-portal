/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import selectSelection from '../../selectors/selectSelection';
import selectStructureChildren from '../../selectors/selectStructureChildren';
import {
	ReferencedStructure,
	RepeatableGroup,
	Structure,
} from '../../types/Structure';
import {Uuid} from '../../types/Uuid';
import {Field} from '../../utils/field';
import {useSelector} from '../StateContext';

type SelectedChild =
	| {field: Field; referenced: boolean; type: 'field'}
	| {referencedStructure: ReferencedStructure; type: 'referenced-structure'}
	| {group: RepeatableGroup; referenced: boolean; type: 'repeatable-group'};

type SelectedItem =
	| {type: 'main-structure'}
	| {type: 'multiselection'}
	| SelectedChild;

export default function useSelectedItem(): SelectedItem {
	const selection = useSelector(selectSelection);
	const children = useSelector(selectStructureChildren);

	const [uuid] = selection;

	if (!uuid) {
		return {type: 'main-structure'};
	}

	if (selection.length > 1) {
		return {type: 'multiselection'};
	}

	const child = findSelectedChild(uuid, children);

	if (child) {
		return child;
	}

	return {type: 'main-structure'};
}

function findSelectedChild(
	uuid: Uuid,
	children: (ReferencedStructure | RepeatableGroup | Structure)['children'],
	isReferenced: boolean = false
): SelectedChild | null {
	for (const child of children.values()) {
		if (child.uuid === uuid) {
			if (child.type === 'referenced-structure') {
				return {
					referencedStructure: child,
					type: 'referenced-structure',
				};
			}
			else if (child.type === 'repeatable-group') {
				return {
					group: child,
					referenced: isReferenced,
					type: 'repeatable-group',
				};
			}
			else {
				return {
					field: child,
					referenced: isReferenced,
					type: 'field',
				};
			}
		}
		else if (
			child.type === 'referenced-structure' ||
			child.type === 'repeatable-group'
		) {
			const group = findSelectedChild(
				uuid,
				child.children,
				isReferenced || child.type === 'referenced-structure'
			);

			if (group) {
				return group;
			}
		}
	}

	return null;
}
