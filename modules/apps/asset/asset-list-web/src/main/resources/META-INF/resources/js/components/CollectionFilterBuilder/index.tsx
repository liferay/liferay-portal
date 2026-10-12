/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {State} from '@liferay/frontend-js-state-web';
import {uuidv4} from 'frontend-js-web';
import React, {useCallback, useEffect, useMemo, useRef, useState} from 'react';

import {invalidFiltersAtom} from '../../atoms/invalidFiltersAtom';
import useTypeProperties from '../../hooks/useTypeProperties';
import {ConditionBuilder} from './ConditionBuilder';
import {Config, initializeConfig} from './config';
import getValueErrorMessage from './getValueErrorMessage';
import normalizeDateTime from './normalizeDateTime';
import {ASSET_FIELDS, RELATIVE_DATE_VALUES} from './operators';
import {getFilterPropertyKey} from './types';

import type {
	FilterCondition,
	FilterProperty,
	FilterPropertyGroup,
} from './types';

function createEmptyConditions(): FilterCondition[] {
	return [{id: uuidv4()}];
}

/**
 * A condition on a field of a specific item type only exists for that item
 * type. Conditions on the asset fields and on the common fields apply to every
 * item type, so they are worth keeping when the source changes.
 */
function isTypeSpecificCondition(condition: FilterCondition): boolean {
	return condition.objectDefinitionExternalReferenceCode !== undefined;
}

function serializeValue(
	property: FilterProperty | undefined,
	value: FilterCondition['value']
): FilterCondition['value'] {
	if (
		(property?.type === 'asset-categories' ||
			property?.type === 'asset-tags') &&
		Array.isArray(value)
	) {
		return (value as Array<{value: string}>).map((entry) => ({
			value: entry.value,
		}));
	}

	if (property?.type !== 'date-time' || value === null) {
		return value;
	}

	if (typeof value === 'string' && RELATIVE_DATE_VALUES.includes(value)) {
		return value;
	}

	if (Array.isArray(value)) {
		return value.map((entry) =>
			typeof entry === 'string' ? normalizeDateTime(entry) : entry
		);
	}

	return typeof value === 'string' ? normalizeDateTime(value) : value;
}

interface CollectionFilterBuilderProps extends Config {
	initialConditions?: Array<Omit<FilterCondition, 'id'>>;
	onChange?: (state: FilterCondition[]) => void;
	properties: FilterPropertyGroup[];
}

/**
 * Serializes the current value into a hidden input so the typeSettings handler
 * picks it up on form submit.
 */
export default function CollectionFilterBuilder({
	groupIds,
	initialConditions,
	namespace,
	onChange,
	properties: initialProperties,
	tagSelectorURL,
	vocabularyIds,
}: CollectionFilterBuilderProps) {
	initializeConfig({
		groupIds,
		namespace,
		tagSelectorURL,
		vocabularyIds,
	});

	const [conditions, setConditions] = useState<FilterCondition[]>(
		initialConditions?.length
			? initialConditions.map((condition) => ({
					...condition,
					id: uuidv4(),
				}))
			: createEmptyConditions()
	);

	const inputRef = useRef<HTMLInputElement>(null);

	const properties = useTypeProperties(initialProperties);

	const propertiesWithAssetFields = useMemo<FilterPropertyGroup[]>(
		() => [
			{
				items: ASSET_FIELDS,
				label: '',
			},
			...properties,
		],
		[properties]
	);

	const propertiesMap = useMemo(
		() =>
			new Map(
				propertiesWithAssetFields
					.flatMap((group) => group.items ?? [])
					.map((property) => [
						getFilterPropertyKey(
							property.objectDefinitionExternalReferenceCode,
							property.name
						),
						property,
					])
			),
		[propertiesWithAssetFields]
	);

	const getConditionProperty = (condition: Omit<FilterCondition, 'id'>) =>
		propertiesMap.get(
			getFilterPropertyKey(
				condition.objectDefinitionExternalReferenceCode,
				condition.propertyName
			)
		);

	const getConditionError = (condition: FilterCondition) =>
		getValueErrorMessage(
			getConditionProperty(condition),
			condition.operatorName,
			condition.value
		);

	const hasInvalidValue = conditions.some(getConditionError);

	const filterValuesAndOmitID = (conditions: FilterCondition[]) =>
		conditions
			.filter((condition) => !getConditionError(condition))
			.map(({id: _id, ...props}) => ({
				...props,
				value: serializeValue(getConditionProperty(props), props.value),
			}))
			.filter(({operatorName, propertyName, value}) => {
				if (!operatorName || !propertyName || !value) {
					return false;
				}

				if (Array.isArray(value)) {
					return value.every(Boolean) && !!value.length;
				}

				return true;
			});

	const handleChange = useCallback(
		(newConditions: FilterCondition[]) => {
			setConditions(newConditions);

			onChange?.(newConditions);
		},
		[onChange]
	);

	useEffect(() => {

		// Keep the conditions the new item type can still offer rather than
		// making the user build them again.

		const handleSourceChange = () =>
			setConditions((conditions) => {
				const keptConditions = conditions.filter(
					(condition) => !isTypeSpecificCondition(condition)
				);

				return keptConditions.length
					? keptConditions
					: createEmptyConditions();
			});

		Liferay.on(`${namespace}sourceChange`, handleSourceChange);

		return () => {
			Liferay.detach(`${namespace}sourceChange`, handleSourceChange);
		};
	}, [namespace]);

	useEffect(() => {
		State.write(invalidFiltersAtom, hasInvalidValue);
	}, [hasInvalidValue]);

	useEffect(() => () => State.write(invalidFiltersAtom, false), []);

	useEffect(() => {
		if (!hasInvalidValue) {
			return;
		}

		const handleSubmitForm = (event: {
			action?: string;
			form: {getDOM: () => HTMLElement};
			preventDefault: () => void;
		}) => {
			if (
				!event.action &&
				event.form.getDOM() === inputRef.current?.form
			) {
				event.preventDefault();
			}
		};

		Liferay.on('submitForm', handleSubmitForm);

		return () => {
			Liferay.detach('submitForm', handleSubmitForm);
		};
	}, [hasInvalidValue]);

	return (
		<>
			<ConditionBuilder
				conditions={conditions}
				onChange={handleChange}
				properties={propertiesWithAssetFields}
				propertiesMap={propertiesMap}
			/>

			<input
				name={`${namespace}TypeSettingsProperties--filters--`}
				ref={inputRef}
				type="hidden"
				value={JSON.stringify(filterValuesAndOmitID(conditions))}
			/>

			{process.env.NODE_ENV === 'development' && (
				<div className="mt-4">
					<div className="text-secondary">
						<code>{namespace}TypeSettingsProperties--filters</code>
					</div>

					<pre
						style={{
							background: 'var(--gray-100)',
							borderRadius: 4,
							fontSize: 11,
							marginTop: 8,
							padding: 12,
						}}
					>
						{JSON.stringify(
							filterValuesAndOmitID(conditions),
							null,
							2
						)}
					</pre>
				</div>
			)}
		</>
	);
}
