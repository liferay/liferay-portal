/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayToggle} from '@clayui/form';
import {useField} from 'formik';
import React from 'react';

interface FormToggleProps {
	name: string;
}

export function FormToggle({name}: FormToggleProps) {
	const [field, , helpers] = useField<boolean>(name);

	return (
		<ClayToggle
			label={
				field.value
					? Liferay.Language.get('active')
					: Liferay.Language.get('inactive')
			}
			onToggle={(toggled) => helpers.setValue(toggled)}
			toggled={field.value}
		/>
	);
}
