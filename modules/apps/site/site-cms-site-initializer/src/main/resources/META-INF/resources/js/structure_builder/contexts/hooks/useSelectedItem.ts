/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import selectSelection from '../../selectors/selectSelection';
import selectStructureFields from '../../selectors/selectStructureFields';
import {ReferencedStructure, Structures} from '../../types/Structure';
import {Uuid} from '../../types/Uuid';
import {Field} from '../../utils/field';
import {useCache} from '../CacheContext';
import {useSelector} from '../StateContext';

type SelectedField =
	| {field: Field; type: 'field'}
	| {field: Field; type: 'referenced-field'}
	| {referencedStructure: ReferencedStructure; type: 'referenced-structure'};

type SelectedItem =
	| {type: 'main-structure'}
	| {type: 'multiselection'}
	| SelectedField;

export default function useSelectedItem(): SelectedItem {
	const selection = useSelector(selectSelection);
	const fields = useSelector(selectStructureFields);

	const {data: structures} = useCache('structures');

	const [uuid] = selection;

	if (!uuid) {
		return {type: 'main-structure'};
	}

	if (selection.length > 1) {
		return {type: 'multiselection'};
	}

	const field = findField(uuid, fields, structures);

	if (field) {
		return field;
	}

	return {type: 'main-structure'};
}

function findField(
	uuid: Uuid,
	fields: (Field | ReferencedStructure)[],
	structures: Structures,
	parentType: SelectedItem['type'] = 'main-structure'
): SelectedField | null {
	for (const field of fields) {
		if (field.uuid === uuid) {
			if (field.type === 'referenced-structure') {
				return {
					referencedStructure: field,
					type: 'referenced-structure',
				};
			}
			else {
				return {
					field,
					type:
						parentType === 'main-structure'
							? 'field'
							: 'referenced-field',
				};
			}
		}

		if (field.type === 'referenced-structure') {
			const structure = structures.get(field.erc);

			if (structure) {
				const child = findField(
					uuid,
					selectStructureFields(structure),
					structures,
					field.type
				);

				if (child) {
					return child;
				}
			}
		}
	}

	return null;
}
