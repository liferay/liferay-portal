/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	AvailableLocale,
	EditingLocale,
	LocalizedValue,
	MultipleSelection,
	ReactFieldBase as FieldBase,
} from 'dynamic-data-mapping-form-field-type';
import React, {useEffect, useRef, useState} from 'react';

interface MultiselectOption {
	label: string;
	reference: string | null;
	value: string;
}

type Values = string[] | LocalizedValue<string[]>;

interface MultiselectPicklistProps {
	availableLocales: AvailableLocale[];
	defaultLanguageId: Liferay.Language.Locale;
	defaultLocale: EditingLocale;
	errorMessage: string;
	fieldName: string;
	id: string;
	label: string;
	localizedObjectField?: boolean;
	localizedValue?: Liferay.Language.FullyLocalizedValue<string> | {};
	name: string;
	onChange: Function;
	options: MultiselectOption[];
	placeholder?: string;
	readOnly: boolean;
	required: boolean;
	tip?: string;
	value: Values;
}

const normalizeValues = (value: Values | '') => {
	if (value === '') {
		return [];
	}
	else if (typeof value === 'string') {
		return JSON.parse(value);
	}

	return value;
};

export default function MultiselectPicklist({
	errorMessage,
	label,
	localizedObjectField,
	localizedValue = {},
	name,
	onChange,
	id,
	options = [],
	placeholder = Liferay.Language.get('choose-an-option'),
	readOnly = false,
	required,
	tip,
	value,
	...otherProps
}: MultiselectPicklistProps) {
	const normalizedValue = normalizeValues(value);

	const [localValues, setLocalValues] = useState(normalizedValue);

	const onChangeRef = useRef(onChange);

	useEffect(() => {
		onChangeRef.current = onChange;
	}, [onChange]);

	useEffect(() => {
		if (value) {
			onChangeRef.current({target: {value: normalizedValue}});
		}
	}, [normalizedValue, value]);

	const handleChange = (_: object, value: Values) => {
		const updatedValues = localizedObjectField
			? {...(value as LocalizedValue<string[]>)}
			: [...(value as string[])];

		onChangeRef.current({target: {value: updatedValues}});

		setLocalValues(updatedValues);
	};

	return (
		<FieldBase
			errorMessage={errorMessage}
			label={label}
			localizedValue={localizedValue}
			name={name}
			readOnly={readOnly}
			required={required}
			tip={tip}
			{...otherProps}
		>
			<MultipleSelection
				{...otherProps}
				errorMessage={errorMessage}
				id={id}
				label={label}
				localizedObjectField={localizedObjectField}
				name={name}
				onChange={handleChange}
				options={options}
				placeholder={placeholder}
				readOnly={readOnly}
				required={required}
				tip={tip}
				value={localValues}
			/>
		</FieldBase>
	);
}
