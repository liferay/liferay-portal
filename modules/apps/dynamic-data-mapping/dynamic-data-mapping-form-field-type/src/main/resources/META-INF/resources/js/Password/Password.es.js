/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ReactFieldBase as FieldBase} from 'dynamic-data-mapping-form-field-type/api';
import React, {useState} from 'react';

const Password = ({
	id,
	name,
	onBlur,
	onChange,
	onFocus,
	placeholder,
	predefinedValue,
	readOnly: disabled,
	value: initialValue,
	...otherProps
}) => {
	const [value, setValue] = useState(
		initialValue ? initialValue : predefinedValue
	);

	return (
		<FieldBase {...otherProps} id={id} name={name} readOnly={disabled}>
			<input
				className="ddm-field-text form-control"
				disabled={disabled}
				id={id ?? name}
				name={name}
				onBlur={onBlur}
				onFocus={onFocus}
				onInput={(event) => {
					onChange(event);
					setValue(event.target.value);
				}}
				placeholder={placeholder}
				type="password"
				value={value}
			/>
		</FieldBase>
	);
};

export default Password;
