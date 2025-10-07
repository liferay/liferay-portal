/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayCheckbox} from '@clayui/form';
import ClayMultiSelect from '@clayui/multi-select';
import React, {useCallback, useEffect, useState} from 'react';

import {MultiSelectItem, MultipleSelectBaseProps} from './select.d';

const MultipleSelectBase = ({
	displayErrors,
	errorMessage,
	id,
	label,
	name,
	onChange,
	options,
	readOnly,
	required,
	tip,
	valid,
	value: values,
}: MultipleSelectBaseProps<string[] | string>) => {
	const [items, setItems] = useState<MultiSelectItem[]>([]);

	const accessibleProps = {
		...(label && {
			'aria-labelledby': `${id ?? name}_fieldLabel`,
		}),
		...((errorMessage || tip) && {
			'aria-describedby': `${id ?? name}_fieldFeedback`,
		}),
		...(displayErrors && !valid && {'aria-invalid': true}),
		'aria-required': required,
	};

	const messages = {
		hotkeys: Liferay.Language.get(
			'press-backspace-to-delete-the-current-row'
		),
		labelAdded: Liferay.Language.get('label-x-was-added-to-the-list'),
		labelRemoved: Liferay.Language.get('label-x-was-removed-from-the-list'),
		listCount: Liferay.Language.get('there-is-x-option-available'),
		listCountPlural: Liferay.Language.get('there-are-x-options-available'),
		loading: `${Liferay.Language.get('loading')}...`,
		notFound: `${Liferay.Language.get('no-results-found')}.`,
	};

	useEffect(() => {
		const newItems = options.filter((option) => {
			if (values?.includes(option.value)) {
				return {label: option.label};
			}
		});

		setItems(newItems);
	}, [options, values]);

	const handleAsyncOptions = useCallback(() => {
		return new Promise((resolve) => {
			resolve(options);
		});
	}, [options]);

	return (
		<ClayMultiSelect
			{...accessibleProps}
			clearAllTitle={Liferay.Language.get('clear-all')}
			disabled={readOnly}
			items={items}
			messages={messages}
			onItemsChange={(itemsChanged: MultiSelectItem[]) => {
				const uniqueItems = [
					...new Set(itemsChanged.map((item) => item.value)),
				];

				if (itemsChanged.length > uniqueItems.length) {
					uniqueItems.pop();
				}

				onChange({}, uniqueItems);
			}}
			onKeyDown={(event) => {
				if (event.key === 'Enter') {
					event.preventDefault();
				}
			}}
			onLoadMore={handleAsyncOptions}
			placeholder={
				!items.length ? Liferay.Language.get('choose-options') : ''
			}
			sourceItems={options}
		>
			{(item) => (
				<ClayMultiSelect.Item key={item.value} textValue={item.label}>
					<div className="auto autofit-row-center fit-row">
						<ClayCheckbox
							checked={values?.includes(item.value)!}
							data-itemValue={item.value}
							data-option-reference={item.reference}
							data-testid={`labelItem-${item.value}`}
							label={item.label}
							onChange={({target: {checked}}) => {
								let newValues = values as string[];

								if (checked) {
									options.forEach((option) => {
										if (option.value === item.value) {
											newValues.push(option.value);
										}
									});
								}
								else {
									options.forEach((option) => {
										if (option.value === item.value) {
											newValues = (
												values as string[]
											).filter(
												(value) => value !== item.value
											);
										}
									});
								}

								setItems(
									newValues.map((newValue) => {
										return {
											label: newValue,
											reference: null,
											value: newValue,
										};
									})
								);

								onChange({}, newValues);
							}}
						/>
					</div>
				</ClayMultiSelect.Item>
			)}
		</ClayMultiSelect>
	);
};

export {MultipleSelectBase};
