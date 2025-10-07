/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayCheckbox, ClayToggle} from '@clayui/form';
import classNames from 'classnames';
import {ReactFieldBase as FieldBase} from 'dynamic-data-mapping-form-field-type/api';
import React, {useEffect, useState} from 'react';

import {setJSONArrayValue} from '../util/setters.es';

const Switcher = ({checked, inline, ...otherProps}) => {
	return (
		<div
			className={classNames('lfr-ddm-form-field-checkbox-switch', {
				'lfr-ddm-form-field-checkbox-switch-inline': inline,
			})}
		>
			<ClayToggle toggled={checked} {...otherProps} />
		</div>
	);
};

const CheckboxMultiple = ({
	accessibleProps,
	disabled,
	inline,
	isSwitcher,
	localizedValueEdited,
	name,
	onBlur,
	onChange,
	onFocus,
	options,
	predefinedValue,
	value: initialValue,
}) => {
	const [value, setValue] = useState(initialValue ?? predefinedValue);

	useEffect(() => {
		if (initialValue?.length > 0) {
			setValue(initialValue);
		}
	}, [initialValue]);

	const displayValues =
		value?.length || (value?.length === 0 && localizedValueEdited)
			? value
			: predefinedValue;
	const Toggle = isSwitcher ? Switcher : ClayCheckbox;

	const handleChange = (event) => {
		const {target} = event;
		const newValue = value.filter(
			(currentValue) => currentValue !== target.value
		);

		if (target.checked) {
			newValue.push(target.value);
		}

		setValue(newValue);
		onChange(event, newValue);
	};

	return (
		<div className="lfr-ddm-checkbox-multiple">
			{options.map((option, index) => (
				<Toggle
					{...accessibleProps}
					checked={displayValues.includes(option.value)}
					data-option-reference={option.reference}
					disabled={disabled}
					inline={inline}
					key={option.value}
					label={option.label}
					name={`${name}_${index}`}
					onBlur={onBlur}
					onChange={handleChange}
					onFocus={onFocus}
					value={option.value}
				/>
			))}

			<input name={name} type="hidden" value={value} />
		</div>
	);
};

const Main = ({
	displayErrors,
	inline,
	name,
	options = [
		{
			label: 'Option 1',
			value: 'option1',
		},
		{
			label: 'Option 2',
			value: 'option2',
		},
	],
	onBlur,
	onChange,
	onFocus,
	predefinedValue,
	readOnly,
	showAsSwitcher = true,
	valid,
	value,
	localizedValueEdited,
	...otherProps
}) => (
	<FieldBase
		displayErrors={displayErrors}
		name={name}
		readOnly={readOnly}
		valid={valid}
		{...otherProps}
	>
		<CheckboxMultiple
			accessibleProps={{
				...((otherProps.errorMessage || otherProps.tip) && {
					'aria-describedby': `${otherProps.id ?? name}_fieldFeedback`,
				}),
				...(displayErrors && !valid && {'aria-invalid': true}),
				'aria-required': otherProps.required,
			}}
			disabled={readOnly}
			inline={inline}
			isSwitcher={showAsSwitcher}
			localizedValueEdited={localizedValueEdited}
			name={name}
			onBlur={onBlur}
			onChange={onChange}
			onFocus={onFocus}
			options={options}
			predefinedValue={setJSONArrayValue(predefinedValue)}
			value={setJSONArrayValue(value)}
		/>
	</FieldBase>
);

Main.displayName = 'CheckboxMultiple';

export default Main;
