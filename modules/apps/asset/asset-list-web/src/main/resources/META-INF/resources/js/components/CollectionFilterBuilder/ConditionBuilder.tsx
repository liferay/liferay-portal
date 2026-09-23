/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {Option, Picker} from '@clayui/core';
import DropDown from '@clayui/drop-down';
import {RowBuilder} from '@liferay/layout-js-components-web';
import React, {useCallback, useMemo} from 'react';
import {v4 as uuidv4} from 'uuid';

import './ConditionBuilder.scss';
import ValueInput from './ValueInput';
import {
	ASSET_FIELD_NAMES,
	getCollectionOperators,
	getCollectionQuantifierOptions,
} from './operators';
import {getCombinationKey, getPropertyKey} from './types';

import type {
	FilterCondition,
	FilterOperator,
	FilterProperty,
	FilterPropertyGroup,
} from './types';

export const TriggerLabel = React.forwardRef<HTMLButtonElement, any>(
	({children, className: _className, onClick, ...otherProps}, ref) => (
		<ClayButton
			className="form-control form-control-select form-control-sm"
			displayType="secondary"
			onClick={onClick}
			ref={ref}
			size="sm"
			{...otherProps}
		>
			{children}
		</ClayButton>
	)
);

interface ConditionBuilderProps {
	conditions: FilterCondition[];
	onChange: (conditions: FilterCondition[]) => void;
	properties: Array<FilterProperty | FilterPropertyGroup>;
	propertiesMap: Map<string, FilterProperty>;
}

type ConditionRowProps = Omit<ConditionBuilderProps, 'onChange'> & {
	condition: FilterCondition;
	index: number;
	onChange: (condition: FilterCondition) => void;
};

/**
 * Only the asset fields are collected, because each resolves to a single
 * AssetEntryQuery slot per operator and quantifier pair. A condition holds a
 * combination as soon as it names one, even before a value is picked. Waiting
 * for the value would let two identical rows be built and only collide on
 * submit.
 */
function getUsedCombinationKeys(
	conditions: FilterCondition[],
	currentConditionId: string
): Set<string> {
	const usedCombinationKeys = new Set<string>();

	for (const condition of conditions) {
		if (
			condition.id === currentConditionId ||
			condition.classNameId !== undefined ||
			condition.classTypeId !== undefined ||
			!ASSET_FIELD_NAMES.has(condition.propertyName ?? '') ||
			!condition.operatorName ||
			!condition.quantifier
		) {
			continue;
		}

		usedCombinationKeys.add(
			getCombinationKey(
				getPropertyKey(undefined, undefined, condition.propertyName),
				condition.operatorName,
				condition.quantifier
			)
		);
	}

	return usedCombinationKeys;
}

/**
 * Returns true once every operator and quantifier pairing of the property is
 * spoken for.
 */
function isExhausted(
	usedCombinationKeys: Set<string>,
	propertyKey: string,
	operators: FilterOperator[],
	quantifierOptions: FilterOperator[] | null
): boolean {
	if (!operators.length || !quantifierOptions?.length) {
		return false;
	}

	return operators.every(({value: operatorName}) =>
		quantifierOptions.every(({value: quantifier}) =>
			usedCombinationKeys.has(
				getCombinationKey(propertyKey, operatorName, quantifier)
			)
		)
	);
}

function isPropertyGroup(
	input: FilterProperty | FilterPropertyGroup
): input is FilterPropertyGroup {
	return 'items' in input;
}

function renderOption({
	disabled,
	key,
	label,
}: {
	disabled: boolean;
	key: string;
	label: string;
}) {
	return (
		<Option disabled={disabled} key={key} textValue={label}>
			{label}

			{disabled && (
				<span className="ml-auto pl-4 text-2 text-secondary">
					{Liferay.Language.get('already-used')}
				</span>
			)}
		</Option>
	);
}

function ConditionRow({
	condition,
	conditions,
	index,
	onChange,
	properties,
	propertiesMap,
}: ConditionRowProps) {
	const conditionKey = getPropertyKey(
		condition.classNameId,
		condition.classTypeId,
		condition.propertyName
	);

	const selectedProperty = propertiesMap.get(conditionKey);

	const operators = selectedProperty
		? getCollectionOperators(selectedProperty)
		: [];
	const quantifierOptions = selectedProperty
		? getCollectionQuantifierOptions(selectedProperty)
		: null;

	const handleValueChange = useCallback(
		(value: string | Array<string | object>) => {
			onChange({...condition, value});
		},
		[condition, onChange]
	);

	const usedCombinationKeys = useMemo(
		() => getUsedCombinationKeys(conditions, condition.id),
		[condition.id, conditions]
	);

	const renderPropertyOption = (property: FilterProperty) => {
		const propertyKey = getPropertyKey(
			property.classNameId,
			property.classTypeId,
			property.name
		);

		return renderOption({
			disabled: isExhausted(
				usedCombinationKeys,
				propertyKey,
				getCollectionOperators(property),
				getCollectionQuantifierOptions(property)
			),
			key: propertyKey,
			label: property.label,
		});
	};

	return (
		<>
			<div className="condition-builder__select form-group mb-0">
				<Picker
					aria-label={Liferay.Language.get('field')}
					as={TriggerLabel}
					items={properties}
					onSelectionChange={(key) => {
						const newProperty = propertiesMap.get(key as string);

						const operators = newProperty
							? getCollectionOperators(newProperty)
							: null;

						onChange({
							classNameId: newProperty?.classNameId,
							classTypeId: newProperty?.classTypeId,
							id: condition.id,
							operatorName:
								operators?.length === 0 ? 'eq' : undefined,
							propertyName: newProperty?.name,
							quantifier: undefined,
							value: undefined,
						});
					}}
					placeholder={Liferay.Language.get('select')}
					selectedKey={selectedProperty ? conditionKey : ''}
				>
					{(item) =>
						isPropertyGroup(item) ? (
							<DropDown.Group
								header={item.label}
								items={item.items}
							>
								{renderPropertyOption}
							</DropDown.Group>
						) : (
							renderPropertyOption(item)
						)
					}
				</Picker>
			</div>

			{!!operators.length && (
				<div className="condition-builder__select form-group mb-0">
					<Picker
						aria-label={Liferay.Language.get('operator')}
						as={TriggerLabel}
						disabled={!selectedProperty}
						items={operators.map(({label, value}) => ({
							label,
							value,
						}))}
						onSelectionChange={(key) => {
							const operatorName = (key as string) || undefined;

							onChange({
								...condition,
								operatorName,
								quantifier: usedCombinationKeys.has(
									getCombinationKey(
										conditionKey,
										operatorName,
										condition.quantifier
									)
								)
									? undefined
									: condition.quantifier,
								value: undefined,
							});
						}}
						placeholder={Liferay.Language.get('select')}
						selectedKey={condition.operatorName ?? ''}
					>
						{(item) =>
							renderOption({
								disabled: isExhausted(
									usedCombinationKeys,
									conditionKey,
									[item],
									quantifierOptions
								),
								key: item.value,
								label: item.label,
							})
						}
					</Picker>
				</div>
			)}

			{!!quantifierOptions?.length && condition.operatorName && (
				<div className="condition-builder__select form-group mb-0">
					<Picker
						aria-label={Liferay.Language.get('quantifier')}
						as={TriggerLabel}
						disabled={!selectedProperty}
						items={quantifierOptions.map(({label, value}) => ({
							label,
							value,
						}))}
						onSelectionChange={(key) =>
							onChange({
								...condition,
								quantifier: (key as string) || undefined,
							})
						}
						placeholder={Liferay.Language.get('select')}
						selectedKey={condition.quantifier ?? ''}
					>
						{(item) =>
							renderOption({
								disabled: usedCombinationKeys.has(
									getCombinationKey(
										conditionKey,
										condition.operatorName,
										item.value
									)
								),
								key: item.value,
								label: item.label,
							})
						}
					</Picker>
				</div>
			)}

			{selectedProperty &&
			condition.operatorName &&
			(!quantifierOptions?.length || condition.quantifier) ? (
				<ValueInput
					index={index}
					onChange={handleValueChange}
					operator={condition.operatorName}
					property={selectedProperty}
					value={condition.value}
				/>
			) : null}
		</>
	);
}

export function ConditionBuilder({
	conditions,
	onChange,
	properties,
	propertiesMap,
}: ConditionBuilderProps) {
	return (
		<div className="condition-builder">
			<RowBuilder<FilterCondition>
				canDelete={(
					condition: FilterCondition,
					_index: number,
					items: FilterCondition[]
				) => items.length > 1 || !!condition.propertyName}
				createItem={() => ({id: uuidv4()})}
				itemClassName="condition-builder__row"
				items={conditions}
				labels={{
					add: Liferay.Language.get('add-filter'),
					addedAnnouncement: Liferay.Language.get('condition-added'),
					delete: Liferay.Language.get('delete-filter'),
					deletedAnnouncement:
						Liferay.Language.get('condition-deleted'),
					list: Liferay.Language.get('filters'),
				}}
				renderItem={({
					index,
					item,
					onChange: onItemChange,
				}: {
					index: number;
					item: FilterCondition;
					onChange: (condition: FilterCondition) => void;
				}) => (
					<ConditionRow
						condition={item}
						conditions={conditions}
						index={index}
						onChange={onItemChange}
						properties={properties}
						propertiesMap={propertiesMap}
					/>
				)}
				setItems={onChange}
			/>
		</div>
	);
}
