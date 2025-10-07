/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayRadio, ClayRadioGroup} from '@clayui/form';
import {ReactFieldBase as FieldBase} from 'dynamic-data-mapping-form-field-type/api';
import React, {useMemo} from 'react';

import {useSyncValue} from '../hooks/useSyncValue.es';
import {setJSONArrayValue} from '../util/setters.es';

import './Radio.scss';

const KEYCODES = {
	TAB: 9,
};

const Radio = ({
	displayErrors,
	editingLanguageId,
	inline,
	name,
	onBlur,
	onChange,
	onFocus,
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
	predefinedValue,
	readOnly: disabled,
	valid,
	value: initialValue,
	...otherProps
}) => {
	const accessibleProps = {
		...((otherProps.errorMessage || otherProps.tip) && {
			'aria-describedby': `${otherProps.id ?? name}_fieldFeedback`,
		}),
		...(displayErrors && !valid && {'aria-invalid': true}),
		'aria-required': otherProps.required,
	};

	const predefinedValueMemo = useMemo(() => {
		if (typeof predefinedValue === 'string') {
			return predefinedValue;
		}

		const predefinedValueJSONArray =
			setJSONArrayValue(predefinedValue) || [];

		return predefinedValueJSONArray[0];
	}, [predefinedValue]);

	const [currentValue, setCurrentValue] = useSyncValue(
		initialValue !== undefined &&
			initialValue !== null &&
			initialValue !== ''
			? initialValue.toString()
			: predefinedValueMemo,
		true,
		editingLanguageId
	);

	return (
		<FieldBase
			{...otherProps}
			displayErrors={displayErrors}
			name={name}
			readOnly={disabled}
			valid={valid}
		>
			<div className="ddm__radio" onBlur={onBlur} onFocus={onFocus}>
				<ClayRadioGroup
					inline={inline}
					name={name}
					onChange={(value) => {
						setCurrentValue(value);
						onChange({target: {value}});
					}}
					onKeyUp={(event) => {
						if (!currentValue && event.keyCode === KEYCODES.TAB) {
							const value = options[0].value;

							setCurrentValue(value);
							onChange({target: {value}});
						}
					}}
					value={currentValue}
				>
					{options.map((option) => (
						<ClayRadio
							{...accessibleProps}
							containerProps={{
								'data-checked': currentValue === option.value,
							}}
							data-option-reference={option.reference}
							disabled={disabled}
							key={option.value}
							label={option.label}
							value={option.value}
						/>
					))}
				</ClayRadioGroup>
			</div>

			<input name={name} type="hidden" value={currentValue} />
		</FieldBase>
	);
};

export default Radio;
