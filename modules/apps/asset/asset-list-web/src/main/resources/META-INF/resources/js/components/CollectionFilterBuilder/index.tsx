/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useCallback, useEffect, useMemo, useState} from 'react';
import {v4 as uuidv4} from 'uuid';

import useTypeProperties from '../../hooks/useTypeProperties';
import {ConditionBuilder} from './ConditionBuilder';
import {Config, initializeConfig} from './config';
import {RELATIVE_DATE_VALUES} from './operators';
import {getPropertyKey} from './types';

import type {
	FilterCondition,
	FilterProperty,
	FilterPropertyGroup,
} from './types';

function normalizeDateTime(value: string) {
	if (!value) {
		return value;
	}

	// The picker emits "--:--" when the time portion isn't filled. Default
	// to midnight so a date-only entry still serializes cleanly.

	const normalized = value.replace('--:--', '00:00');

	const date = new Date(normalized.replace(' ', 'T'));

	return Number.isNaN(date.getTime()) ? '' : normalized;
}

function createEmptyConditions(): FilterCondition[] {
	return [{id: uuidv4()}];
}

/**
 * A condition on a field of a specific item type only exists for that item
 * type. Conditions on the asset fields and on the common fields apply to every
 * item type, so they are worth keeping when the source changes.
 */
function isTypeSpecificCondition(condition: FilterCondition): boolean {
	return (
		condition.classNameId !== undefined &&
		condition.classTypeId !== undefined
	);
}

function serializeValue(
	property: FilterProperty | undefined,
	value: FilterCondition['value']
): FilterCondition['value'] {
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
	categorySelectorURL,
	groupIds,
	initialConditions,
	namespace,
	onChange,
	properties: initialProperties,
	tagSelectorURL,
	vocabularyIds,
}: CollectionFilterBuilderProps) {
	initializeConfig({
		categorySelectorURL,
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

	const properties = useTypeProperties(initialProperties);

	const propertiesWithAssetFields = useMemo<FilterPropertyGroup[]>(
		() => [
			{
				items: [
					{
						label: Liferay.Language.get('tags'),
						name: 'assetTags',
						type: 'asset-tags',
					},
					{
						label: Liferay.Language.get('categories'),
						name: 'assetCategories',
						type: 'asset-categories',
					},
					{
						label: Liferay.Language.get('keywords'),
						name: 'keywords',
						type: 'text',
					},
				],
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
						getPropertyKey(
							property.classNameId,
							property.classTypeId,
							property.name
						),
						property,
					])
			),
		[propertiesWithAssetFields]
	);

	const filterValuesAndOmitID = (conditions: FilterCondition[]) =>
		conditions
			.map(({id: _id, ...props}) => {
				const property = propertiesMap.get(
					getPropertyKey(
						props.classNameId,
						props.classTypeId,
						props.propertyName
					)
				);

				return {...props, value: serializeValue(property, props.value)};
			})
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

		// An item type that uses the asset filter builder instead displays none
		// of this, and the type settings are merged rather than replaced on save,
		// so drop everything and let the empty value clear what was stored.
		// Otherwise keep the conditions the new item type can still offer rather
		// than making the user build them again.

		const handleFilterVisibilityChange = ({
			showCollection,
		}: {
			showCollection: boolean;
		}) =>
			setConditions((conditions) => {
				if (!showCollection) {
					return createEmptyConditions();
				}

				const keptConditions = conditions.filter(
					(condition) => !isTypeSpecificCondition(condition)
				);

				return keptConditions.length
					? keptConditions
					: createEmptyConditions();
			});

		Liferay.on(
			`${namespace}filterVisibilityChange`,
			handleFilterVisibilityChange
		);

		return () => {
			Liferay.detach(
				`${namespace}filterVisibilityChange`,
				handleFilterVisibilityChange
			);
		};
	}, [namespace]);

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
