/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {Option, Picker} from '@clayui/core';
import DropDown from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import React, {useEffect, useMemo, useState} from 'react';

import useTypeProperties from '../../hooks/useTypeProperties';
import {getPropertyKey} from '../CollectionFilterBuilder/types';

import type {
	FilterProperty,
	FilterPropertyGroup,
} from '../CollectionFilterBuilder/types';

const DEFAULT_ORDER_BY_COLUMN_1 = 'modified';
const DEFAULT_ORDER_BY_COLUMN_2 = 'title';

type OrderByType = 'ASC' | 'DESC';

interface OrderBySelection {
	classNameId?: number;
	classTypeId?: number;
	propertyName: string;
}

function isGroup(
	item: FilterProperty | FilterPropertyGroup
): item is FilterPropertyGroup {
	return 'items' in item;
}

function isTypeSpecificProperty(value: OrderBySelection): boolean {
	return value.classNameId !== undefined && value.classTypeId !== undefined;
}

function resetOrderByColumn(defaultPropertyName: string): OrderBySelection {
	return {
		classNameId: undefined,
		classTypeId: undefined,
		propertyName: defaultPropertyName,
	};
}

/**
 * Common fields are offered for every item type, so a column set to one stays
 * valid. A column set to a type specific field only exists for the item type it
 * came from, so fall back to the default rather than submit a column the new
 * item type does not have.
 */
function resetIfTypeSpecific(defaultPropertyName: string) {
	return (orderByColumn: OrderBySelection): OrderBySelection =>
		isTypeSpecificProperty(orderByColumn)
			? resetOrderByColumn(defaultPropertyName)
			: orderByColumn;
}

function parseInitialOrderByColumn(
	value: string | null | undefined,
	defaultPropertyName: string
): OrderBySelection {
	if (value && value.startsWith('{')) {
		try {
			const parsed = JSON.parse(value);

			if (parsed && typeof parsed === 'object') {
				return {
					classNameId: parsed.classNameId,
					classTypeId: parsed.classTypeId,
					propertyName: parsed.propertyName,
				};
			}
		}
		catch (error) {
			if (process.env.NODE_ENV === 'development') {
				console.error('Failed to parse initial selection: ', error);
			}
		}
	}

	return resetOrderByColumn(value || defaultPropertyName);
}

interface OrderByFieldProps {
	columnName: string;
	columnValue: OrderBySelection;
	items: Array<FilterProperty | FilterPropertyGroup>;
	label: string;
	onColumnChange: (selection: OrderBySelection) => void;
	onTypeChange: (type: OrderByType) => void;
	propertiesMap: Map<string, OrderBySelection>;
	typeName: string;
	typeValue: OrderByType;
}

function OrderByField({
	columnName,
	columnValue,
	items,
	label,
	onColumnChange,
	onTypeChange,
	propertiesMap,
	typeName,
	typeValue,
}: OrderByFieldProps) {
	const ascending = typeValue === 'ASC';
	const selectedKey = getPropertyKey(
		columnValue.classNameId,
		columnValue.classTypeId,
		columnValue.propertyName
	);

	return (
		<div className="col-md-6">
			<div className="h5">{label}</div>

			<div className="d-inline-flex">
				<Picker
					aria-label={label}
					items={items}
					onSelectionChange={(key) => {
						const selection = propertiesMap.get(key as string);

						if (selection) {
							onColumnChange(selection);
						}
					}}
					selectedKey={selectedKey}
				>
					{(item) =>
						isGroup(item) ? (
							<DropDown.Group
								header={item.label}
								items={item.items}
							>
								{(option) => (
									<Option
										key={getPropertyKey(
											option.classNameId,
											option.classTypeId,
											option.name
										)}
									>
										{option.label}
									</Option>
								)}
							</DropDown.Group>
						) : (
							<Option
								key={getPropertyKey(
									item.classNameId,
									item.classTypeId,
									item.name
								)}
							>
								{item.label}
							</Option>
						)
					}
				</Picker>
			</div>

			<input
				name={columnName}
				type="hidden"
				value={
					isTypeSpecificProperty(columnValue)
						? JSON.stringify(columnValue)
						: columnValue.propertyName
				}
			/>

			<div className="d-inline-flex">
				<ClayButton
					aria-label={
						ascending
							? Liferay.Language.get('descending')
							: Liferay.Language.get('ascending')
					}
					borderless
					displayType="secondary"
					monospaced
					onClick={() => onTypeChange(ascending ? 'DESC' : 'ASC')}
					title={
						ascending
							? Liferay.Language.get('descending')
							: Liferay.Language.get('ascending')
					}
				>
					<ClayIcon
						symbol={ascending ? 'order-list-up' : 'order-list-down'}
					/>
				</ClayButton>
			</div>

			<input name={typeName} type="hidden" value={typeValue} />

			{process.env.NODE_ENV === 'development' && (
				<>
					<div className="mt-4">
						<code>{columnName}</code>

						<pre
							style={{
								background: 'var(--gray-100)',
								borderRadius: 4,
								fontSize: 11,
								marginTop: 8,
								padding: 12,
							}}
						>
							{isTypeSpecificProperty(columnValue)
								? JSON.stringify(columnValue)
								: columnValue.propertyName}
						</pre>
					</div>

					<div className="mt-4">
						<code>{typeName}</code>

						<pre
							style={{
								background: 'var(--gray-100)',
								borderRadius: 4,
								fontSize: 11,
								marginTop: 8,
								padding: 12,
							}}
						>
							{typeValue}
						</pre>
					</div>
				</>
			)}
		</div>
	);
}

interface CollectionOrderingProps {
	initialOrderByColumn1?: string;
	initialOrderByColumn2?: string;
	initialOrderByType1?: OrderByType;
	initialOrderByType2?: OrderByType;
	namespace: string;
	properties?: FilterPropertyGroup[];
}

export default function CollectionOrdering({
	initialOrderByColumn1 = '',
	initialOrderByColumn2 = '',
	initialOrderByType1 = 'ASC',
	initialOrderByType2 = 'ASC',
	namespace,
	properties: initialProperties,
}: CollectionOrderingProps) {
	const [orderByColumn1, setOrderByColumn1] = useState<OrderBySelection>(() =>
		parseInitialOrderByColumn(
			initialOrderByColumn1,
			DEFAULT_ORDER_BY_COLUMN_1
		)
	);
	const [orderByColumn2, setOrderByColumn2] = useState<OrderBySelection>(() =>
		parseInitialOrderByColumn(
			initialOrderByColumn2,
			DEFAULT_ORDER_BY_COLUMN_2
		)
	);
	const [orderByType1, setOrderByType1] =
		useState<OrderByType>(initialOrderByType1);
	const [orderByType2, setOrderByType2] =
		useState<OrderByType>(initialOrderByType2);

	const properties = useTypeProperties(initialProperties);

	useEffect(() => {
		const handleSourceChange = () => {
			setOrderByColumn1(resetIfTypeSpecific(DEFAULT_ORDER_BY_COLUMN_1));
			setOrderByColumn2(resetIfTypeSpecific(DEFAULT_ORDER_BY_COLUMN_2));
		};

		Liferay.on(`${namespace}sourceChange`, handleSourceChange);

		return () => {
			Liferay.detach(`${namespace}sourceChange`, handleSourceChange);
		};
	}, [namespace]);

	const items = useMemo<Array<FilterProperty | FilterPropertyGroup>>(() => {
		return properties
			.map(({items, label}) => ({
				items: (items ?? []).filter(
					(property) => property.sortable !== false
				),
				label,
			}))
			.filter(({items}) => items.length);
	}, [properties]);

	const propertiesMap = useMemo(() => {
		const map = new Map<string, OrderBySelection>();

		properties
			.flatMap((group) => group.items ?? [])
			.forEach(({classNameId, classTypeId, name}) => {
				map.set(getPropertyKey(classNameId, classTypeId, name), {
					classNameId,
					classTypeId,
					propertyName: name,
				});
			});

		return map;
	}, [properties]);

	return (
		<div className="row">
			<OrderByField
				columnName={`${namespace}TypeSettingsProperties--orderByColumn1--`}
				columnValue={orderByColumn1}
				items={items}
				label={Liferay.Language.get('order-by')}
				onColumnChange={setOrderByColumn1}
				onTypeChange={setOrderByType1}
				propertiesMap={propertiesMap}
				typeName={`${namespace}TypeSettingsProperties--orderByType1--`}
				typeValue={orderByType1}
			/>

			<OrderByField
				columnName={`${namespace}TypeSettingsProperties--orderByColumn2--`}
				columnValue={orderByColumn2}
				items={items}
				label={Liferay.Language.get('and-then-by')}
				onColumnChange={setOrderByColumn2}
				onTypeChange={setOrderByType2}
				propertiesMap={propertiesMap}
				typeName={`${namespace}TypeSettingsProperties--orderByType2--`}
				typeValue={orderByType2}
			/>
		</div>
	);
}
