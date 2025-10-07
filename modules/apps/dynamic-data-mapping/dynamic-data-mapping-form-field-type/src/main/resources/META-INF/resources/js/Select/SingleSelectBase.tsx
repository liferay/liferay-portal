/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Option, Picker} from '@clayui/core';
import DropDown from '@clayui/drop-down';
import {useFormState} from 'data-engine-js-components-web';
import React, {useEffect, useState} from 'react';

import {getTooltipTitle} from '../util/tooltip';

import type {SelectMainProps} from './select.d';

interface SingleSelectBaseProps
	extends Omit<SelectMainProps, 'onChange' | 'selectedKey' | 'value'> {
	selectedKey?: string;
	viewMode?: unknown;
}

export default function SingleSelectBase({
	className,
	displayErrors,
	errorMessage,
	id,
	label,
	name,
	onSelectionChange,
	options,
	placeholder,
	predefinedValue,
	readOnly,
	required,
	selectedKey,
	showEmptyOption,
	tip,
	valid,
	viewMode,
}: SingleSelectBaseProps) {
	const {activeTabTitle} = useFormState();
	const [loading, setLoading] = useState<boolean>();
	const [selectedLabel, setSelectedLabel] = useState('');
	let newSelectedKey = selectedKey;

	if (!selectedKey?.length && showEmptyOption) {
		newSelectedKey = 'chooseAnOption';
	}

	if (Array.isArray(selectedKey) && selectedKey?.[0] === '') {
		newSelectedKey = undefined;
	}

	let selectedItem: string | string[] | undefined = newSelectedKey;

	if (newSelectedKey?.[0] !== undefined) {
		selectedItem =
			newSelectedKey ??
			(predefinedValue?.length ? predefinedValue : undefined);
	}
	else if (
		(newSelectedKey === 'chooseAnOption' ||
			newSelectedKey?.[0] === undefined) &&
		predefinedValue?.[0] &&
		!viewMode
	) {
		selectedItem = predefinedValue?.[0];
	}
	else if (viewMode) {
		selectedItem = selectedItem ?? predefinedValue;
	}

	if (typeof selectedItem !== 'string') {
		selectedItem = selectedItem?.[0];
	}

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

	useEffect(() => {
		if (
			!readOnly &&
			activeTabTitle !== Liferay.Language.get('advanced') &&
			!viewMode &&
			name?.includes('predefinedValue')
		) {
			setLoading(true);
			setTimeout(() => setLoading(false), 200);
		}
	}, [activeTabTitle, name, options, readOnly, viewMode]);

	useEffect(() => {
		const selectedOption = options.find(
			(option) => option.value === selectedItem
		);

		if (selectedOption) {
			setSelectedLabel(selectedOption.label);
		}
		else {
			setSelectedLabel(Liferay.Language.get('choose-an-option'));
		}
	}, [options, selectedKey, selectedItem]);

	return (
		<div
			data-tooltip-align="top"
			{...getTooltipTitle({
				placeholder: Liferay.Language.get('choose-an-option'),
				value: selectedLabel,
			})}
			{...(className && {className})}
		>
			{!loading && (
				<Picker
					{...accessibleProps}
					data-testid={id}
					disabled={readOnly}
					items={[{items: options, label}]}
					onSelectionChange={onSelectionChange}
					placeholder={placeholder}
					selectedKey={selectedItem ?? 'chooseAnOption'}
				>
					{(group) => (
						<DropDown.Group
							header={group.label}
							items={group.items}
						>
							{(item) => (
								<Option
									data-option-reference={item.reference}
									disabled={item.disabled}
									key={item.value}
								>
									{item.label}
								</Option>
							)}
						</DropDown.Group>
					)}
				</Picker>
			)}
		</div>
	);
}
