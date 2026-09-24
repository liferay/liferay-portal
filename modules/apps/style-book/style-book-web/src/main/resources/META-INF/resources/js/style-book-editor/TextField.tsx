/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayInput} from '@clayui/form';
import {useField} from 'formik';
import {FieldBase, useId} from 'frontend-js-components-web';
import React from 'react';

interface TextFieldProps {
	className?: string;
	component?: 'input' | 'textarea';
	label: string;
	name: string;
	placeholder?: string;
	required?: boolean;
}

export default function TextField({
	className,
	component,
	label,
	name,
	placeholder,
	required,
}: TextFieldProps) {
	const [field, meta] = useField<string>(name);

	const id = useId();

	return (
		<FieldBase
			className={className}
			errorMessage={meta.touched ? meta.error : undefined}
			id={id}
			label={label}
			required={required}
		>
			<ClayInput
				{...field}
				component={component}
				id={id}
				placeholder={placeholder}
			/>
		</FieldBase>
	);
}
