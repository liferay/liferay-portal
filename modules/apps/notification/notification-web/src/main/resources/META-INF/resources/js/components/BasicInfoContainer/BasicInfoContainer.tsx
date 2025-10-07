/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Card, FormError, Input} from '@liferay/object-js-components-web';
import React from 'react';

interface BasicInfoContainerProps {
	errors: FormError<NotificationTemplate>;
	setValues: (values: Partial<NotificationTemplate>) => void;
	values: NotificationTemplate;
}

export function BasicInfoContainer({
	errors,
	setValues,
	values,
}: BasicInfoContainerProps) {
	return (
		<Card title={Liferay.Language.get('basic-info')}>
			<Input
				error={errors.name}
				id="name"
				label={Liferay.Language.get('name')}
				name="name"
				onChange={({target}) =>
					setValues({
						...values,
						name: target.value,
					})
				}
				required
				value={values.name}
			/>

			<Input
				component="textarea"
				disabled={values.system}
				id="description"
				label={Liferay.Language.get('description')}
				name="description"
				onChange={({target}) =>
					setValues({
						...values,
						description: target.value,
					})
				}
				type="text"
				value={values.description}
			/>
		</Card>
	);
}
