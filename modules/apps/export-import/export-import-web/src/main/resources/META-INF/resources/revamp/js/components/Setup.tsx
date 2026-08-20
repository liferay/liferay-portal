/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import React from 'react';

import SectionHeader from './SectionHeader';
import {FormikFieldText} from './forms/formik';

export default function Setup({
	placeholder,
	subtitle,
	title,
}: {
	placeholder: string;
	subtitle: string;
	title: string;
}) {
	return (
		<>
			<SectionHeader subtitle={subtitle} title={title} />

			<ClayLayout.Sheet>
				<FormikFieldText
					label={Liferay.Language.get('name')}
					name="name"
					placeholder={placeholder}
					required
				/>
			</ClayLayout.Sheet>
		</>
	);
}
