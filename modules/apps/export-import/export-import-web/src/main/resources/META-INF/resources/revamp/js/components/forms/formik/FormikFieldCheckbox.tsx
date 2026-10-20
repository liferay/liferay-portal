/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm from '@clayui/form';
import {FormikValues, useFormikContext} from 'formik';
import React from 'react';

import {FieldCheckbox} from '../FieldCheckbox';

interface FormikFieldCheckboxProps {
	description?: string;
	id?: string;
	label: string;
	name: string;
}

export function FormikFieldCheckbox({
	description,
	id,
	label,
	name,
}: FormikFieldCheckboxProps) {
	const {setFieldValue, values} = useFormikContext<FormikValues>();

	return (
		<ClayForm.Group>
			<FieldCheckbox
				bordered={false}
				checked={Boolean(values[name])}
				description={description}
				id={id}
				label={label}
				name={name}
				onChange={(checked) => setFieldValue(name, checked)}
			/>
		</ClayForm.Group>
	);
}
