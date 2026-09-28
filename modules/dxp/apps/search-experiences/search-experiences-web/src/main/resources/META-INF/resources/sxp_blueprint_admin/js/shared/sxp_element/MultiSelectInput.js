/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayMultiSelect from '@clayui/multi-select';
import React, {useState} from 'react';

function MultiSelectInput({
	disabled,
	id,
	label,
	name,
	setFieldTouched,
	setFieldValue,
	value,
}) {
	const [inputValue, setInputValue] = useState('');

	const _handleKeyDown = (event) => {
		if (event.key === 'Enter' && !inputValue.trim()) {
			event.preventDefault();
		}
	};

	const _handleItemsChange = (items) => {
		setFieldValue(
			name,
			items.map((item) => ({
				...item,
				label: item.label.trim(),
				value: item.value.trim(),
			}))
		);
	};

	return (
		<ClayMultiSelect
			aria-label={label}
			disabled={disabled}
			id={id}
			items={value || []}
			onBlur={() => {
				setFieldTouched(name);

				if (inputValue.trim()) {
					_handleItemsChange([
						...value,
						{label: inputValue, value: inputValue},
					]);

					setInputValue('');
				}
			}}
			onChange={setInputValue}
			onItemsChange={_handleItemsChange}
			onKeyDown={_handleKeyDown}
			value={inputValue}
		/>
	);
}

export default MultiSelectInput;
