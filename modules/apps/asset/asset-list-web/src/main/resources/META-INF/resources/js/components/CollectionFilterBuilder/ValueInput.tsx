/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Option, Picker} from '@clayui/core';
import ClayForm, {ClayInput} from '@clayui/form';
import MultiSelect from '@clayui/multi-select';
import {
	AssetTagsSelector,
	AssetVocabularyCategoriesSelector,
} from 'asset-taglib';
import classNames from 'classnames';
import React from 'react';

import {TriggerLabel} from './ConditionBuilder';
import DateValueInput from './DateValueInput';
import {config} from './config';
import getValueErrorMessage from './getValueErrorMessage';

import type {FilterProperty} from './types';

interface SelectedItem {
	label: string;
	value: string;
}

function handlePreventEnterSubmit(event: React.KeyboardEvent) {
	if (event.key === 'Enter') {
		event.preventDefault();
	}
}

interface ValueInputContainerProps
	extends React.HTMLAttributes<HTMLDivElement> {
	children: React.ReactNode;
	errorMessage?: string | null;
}

export function ValueInputContainer({
	children,
	className,
	errorMessage,
	...otherProps
}: ValueInputContainerProps) {
	return (
		<div
			className={classNames(
				'condition-builder__value-input flex-grow-1 form-group mb-0',
				{'has-error': errorMessage},
				className
			)}
			{...otherProps}
		>
			<div className="c-gap-2 d-flex">{children}</div>

			{errorMessage && (
				<ClayForm.FeedbackGroup role="alert">
					<ClayForm.FeedbackItem>
						{errorMessage}
					</ClayForm.FeedbackItem>
				</ClayForm.FeedbackGroup>
			)}
		</div>
	);
}

export interface ValueInputProps {
	index: number;
	onChange: (value: string | Array<string | object>) => void;
	operator: string;
	property: FilterProperty;
	value: string | Array<string | object> | undefined;
}

export default function ValueInput({
	index,
	onChange,
	operator,
	property,
	value,
}: ValueInputProps) {
	const {groupIds, namespace, tagSelectorURL, vocabularyIds} = config;

	const {options, type} = property;

	if (type === 'asset-tags') {
		return (
			<ValueInputContainer onKeyDown={handlePreventEnterSubmit}>
				<AssetTagsSelector
					formGroupClassName="condition-builder__asset-selector mb-0"
					groupIds={groupIds}
					inputName={`${namespace}queryTagNames`}
					onSelectedItemsChange={(selectedItems: SelectedItem[]) =>
						onChange(
							selectedItems.map(({label, value}) => ({
								label,
								value,
							}))
						)
					}
					portletURL={tagSelectorURL}
					selectedItems={value}
					showLabel={false}
					showSelectButton
					showSubtitle={false}
				/>
			</ValueInputContainer>
		);
	}

	if (type === 'asset-categories') {
		return (
			<ValueInputContainer onKeyDown={handlePreventEnterSubmit}>
				<AssetVocabularyCategoriesSelector
					formGroupClassName="condition-builder__asset-selector mb-0"
					groupIds={groupIds}
					inputName={`${namespace}queryCategoryIds`}
					onSelectedItemsChange={(selectedItems: SelectedItem[]) =>
						onChange(
							selectedItems.map(({label, value}) => ({
								label,
								value,
							}))
						)
					}
					selectedItems={value}
					sourceItemsVocabularyIds={vocabularyIds}
				/>
			</ValueInputContainer>
		);
	}

	if (type === 'boolean') {
		const items = [
			{label: Liferay.Language.get('is-true'), value: 'true'},
			{label: Liferay.Language.get('is-false'), value: 'false'},
		];

		return (
			<ValueInputContainer>
				<Picker
					aria-label={Liferay.Language.get('value')}
					as={TriggerLabel}
					items={items}
					onSelectionChange={(key) => onChange(key as string)}
					placeholder={Liferay.Language.get('select')}
					selectedKey={value as string}
				>
					{(item) => <Option key={item.value}>{item.label}</Option>}
				</Picker>
			</ValueInputContainer>
		);
	}

	if (type === 'picklist' && options?.length) {
		const items = (Array.isArray(value) ? value : []) as SelectedItem[];

		return (
			<ValueInputContainer>
				<MultiSelect
					allowsCustomLabel={false}
					aria-label={Liferay.Language.get('value')}
					inputName={`${namespace}${property.name}-${index}`}
					items={items}
					onItemsChange={(newItems) => {
						const uniqueNewItems = newItems
							.filter(
								(item, index, self) =>
									index ===
									self.findIndex(
										(p) => p.value === item.value
									)
							)
							.map(({label, value}) => ({label, value}));

						onChange(uniqueNewItems as Array<string | object>);
					}}
					onKeyDown={handlePreventEnterSubmit}
					size="sm"
					sourceItems={options}
				/>
			</ValueInputContainer>
		);
	}

	if (type === 'integer' || type === 'numeric' || type === 'decimal') {
		if (operator === 'between') {
			const [from, to] = (value as string[]) || [];

			return (
				<ValueInputContainer
					errorMessage={getValueErrorMessage(
						property,
						operator,
						value
					)}
				>
					<ClayInput
						aria-label={Liferay.Language.get('from')}
						className="form-control-sm"
						id={`${property.name}-from-${index}`}
						onChange={(event) =>
							onChange([event.target.value, to] as string[])
						}
						onKeyDown={handlePreventEnterSubmit}
						placeholder={Liferay.Language.get('from')}
						step={type === 'decimal' ? '0.001' : '1'}
						type="number"
						value={from}
					/>

					<ClayInput
						aria-label={Liferay.Language.get('to[date-time]')}
						className="form-control-sm"
						id={`${property.name}-to-${index}`}
						onChange={(event) =>
							onChange([from, event.target.value] as string[])
						}
						onKeyDown={handlePreventEnterSubmit}
						placeholder={Liferay.Language.get('to[date-time]')}
						step={type === 'decimal' ? '0.001' : '1'}
						type="number"
						value={to}
					/>
				</ValueInputContainer>
			);
		}

		return (
			<ValueInputContainer
				errorMessage={getValueErrorMessage(property, operator, value)}
			>
				<ClayInput
					aria-label={Liferay.Language.get('value')}
					className="form-control-sm"
					id={`${property.name}-${index}`}
					onChange={(event) => onChange(event.target.value)}
					onKeyDown={handlePreventEnterSubmit}
					placeholder={Liferay.Language.get('enter-value')}
					step={type === 'decimal' ? '0.001' : '1'}
					type="number"
					value={(value as string) ?? ''}
				/>
			</ValueInputContainer>
		);
	}

	if (type === 'date' || type === 'date-time') {
		return (
			<DateValueInput
				index={index}
				onChange={onChange}
				operator={operator}
				property={property}
				value={value}
			/>
		);
	}

	return (
		<ValueInputContainer>
			<ClayInput
				aria-label={Liferay.Language.get('value')}
				className="form-control-sm"
				id={`${property.name}-${index}`}
				onChange={(event) => onChange(event.target.value)}
				onKeyDown={handlePreventEnterSubmit}
				placeholder={Liferay.Language.get('enter-value')}
				type="text"
				value={(value as string) ?? ''}
			/>
		</ValueInputContainer>
	);
}
