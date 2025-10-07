/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {isNullOrUndefined} from '@liferay/layout-js-components-web';
import {useCallback} from 'react';

import focusInvalidElement from '../../common/utils/focusInvalidElement';
import {State, useSelector, useStateDispatch} from '../contexts/StateContext';
import selectState from '../selectors/selectState';
import selectStructureChildren from '../selectors/selectStructureChildren';
import {RepeatableGroup, Structure, StructureChild} from '../types/Structure';
import {Field, MultiselectField, SingleSelectField} from './field';

export type ValidationError =
	| 'no-erc'
	| 'no-label'
	| 'no-name'
	| 'no-picklist'
	| 'no-space';

export function validateField({
	currentErrors,
	data,
}: {
	currentErrors?: Set<ValidationError>;
	data: {
		erc?: Field['erc'];
		label?: Field['label'];
		name?: Field['name'];
		picklistId?:
			| SingleSelectField['picklistId']
			| MultiselectField['picklistId'];
	};
}): Set<ValidationError> {
	const {erc, label, name, picklistId} = data;

	const errors = new Set(currentErrors);

	if (!isNullOrUndefined(erc)) {
		erc ? errors.delete('no-erc') : errors.add('no-erc');
	}

	if (!isNullOrUndefined(name)) {
		name ? errors.delete('no-name') : errors.add('no-name');
	}

	if (!isNullOrUndefined(label)) {
		Object.values(label ?? {}).every(Boolean)
			? errors.delete('no-label')
			: errors.add('no-label');
	}

	if (!isNullOrUndefined(picklistId)) {
		picklistId ? errors.delete('no-picklist') : errors.add('no-picklist');
	}

	return errors;
}

export function validateRepeatableGroup({
	currentErrors,
	data,
}: {
	currentErrors?: Set<ValidationError>;
	data: Partial<RepeatableGroup>;
}): Set<ValidationError> {
	const {label} = data;

	const errors = new Set(currentErrors);

	if (!isNullOrUndefined(label)) {
		Object.values(label ?? {}).every(Boolean)
			? errors.delete('no-label')
			: errors.add('no-label');
	}

	return errors;
}

export function validateStructure({
	currentErrors,
	data,
}: {
	currentErrors?: Set<ValidationError>;
	data: Partial<Structure>;
}): Set<ValidationError> {
	const {erc, label, name, spaces} = data;

	const errors = new Set(currentErrors);

	if (!isNullOrUndefined(erc)) {
		erc ? errors.delete('no-erc') : errors.add('no-erc');
	}

	if (!isNullOrUndefined(name)) {
		name ? errors.delete('no-name') : errors.add('no-name');
	}

	if (!isNullOrUndefined(label)) {
		Object.values(label ?? {}).every(Boolean)
			? errors.delete('no-label')
			: errors.add('no-label');
	}

	if (!isNullOrUndefined(spaces)) {
		spaces.length ? errors.delete('no-space') : errors.add('no-space');
	}

	return errors;
}

export function useValidate() {
	const dispatch = useStateDispatch();
	const children = useSelector(selectStructureChildren);
	const state = useSelector(selectState);

	const {structure} = state;

	const validateChild = useCallback(
		(child: StructureChild, invalids: State['invalids']) => {
			let errors: Set<ValidationError> = new Set();

			if (child.type === 'repeatable-group') {
				errors = validateRepeatableGroup({data: child});

				if (errors.size) {
					invalids.set(child.uuid, errors);
				}

				for (const grandChild of child.children.values()) {
					if (grandChild.type === 'referenced-structure') {
						continue;
					}

					validateChild(grandChild, invalids);
				}
			}
			else {
				errors = validateField({data: child as Field});

				if (errors.size) {
					invalids.set(child.uuid, errors);
				}
			}
		},
		[]
	);

	return useCallback(() => {

		// Check at least one child is added

		const fields = Array.from(children.values()).filter(
			(field) =>
				field.type !== 'referenced-structure' &&
				field.type !== 'repeatable-group'
		);

		if (!fields.length) {
			dispatch({
				error: Liferay.Language.get(
					'at-least-one-field-must-be-added-to-save-or-publish-the-content-structure'
				),
				type: 'set-error',
			});

			return false;
		}

		// Validate structure

		let errors: Set<ValidationError> = new Set();

		const invalids = new Map(state.invalids);

		errors = validateStructure({data: structure});

		if (errors.size) {
			invalids.set(structure.uuid, errors);
		}

		// Validate children

		for (const child of children.values()) {
			validateChild(child, invalids);
		}

		// If there's some invalid, dispatch validate action

		if (invalids.size) {
			dispatch({
				invalids,
				type: 'validate',
			});

			focusInvalidElement();

			return false;
		}

		// It's valid

		return true;
	}, [children, dispatch, state.invalids, structure, validateChild]);
}
