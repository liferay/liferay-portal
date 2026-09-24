/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Option, Picker} from '@clayui/core';
import {useField} from 'formik';
import {FieldBase, useId} from 'frontend-js-components-web';
import React from 'react';

export interface FrontendTokenSetOption {
	label: string;
	name: string;
}

interface TokenSetFieldProps {
	tokenSets: FrontendTokenSetOption[];
}

export default function TokenSetField({tokenSets}: TokenSetFieldProps) {
	const [field, meta, helpers] = useField<string>('tokenSetName');

	const id = useId();

	return (
		<FieldBase
			errorMessage={meta.touched ? meta.error : undefined}
			id={id}
			label={Liferay.Language.get('token-set')}
			required
		>
			<Picker
				id={id}
				items={tokenSets}
				messages={{
					itemDescribedby: Liferay.Language.get(
						'you-are-currently-on-a-text-element,-inside-of-a-list-box'
					),
					itemSelected: Liferay.Language.get('x-selected'),
					scrollToBottomAriaLabel:
						Liferay.Language.get('scroll-to-bottom'),
					scrollToTopAriaLabel: Liferay.Language.get('scroll-to-top'),
				}}
				onSelectionChange={(tokenSetName: React.Key) =>
					helpers.setValue(String(tokenSetName))
				}
				selectedKey={field.value}
			>
				{(item) => (
					<Option key={item.name} textValue={item.label}>
						{item.label}
					</Option>
				)}
			</Picker>
		</FieldBase>
	);
}
