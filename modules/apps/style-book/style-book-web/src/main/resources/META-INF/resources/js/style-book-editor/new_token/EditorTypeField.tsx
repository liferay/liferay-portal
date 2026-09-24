/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClaySelectWithOption} from '@clayui/form';
import {useField} from 'formik';
import {FieldBase, useId} from 'frontend-js-components-web';
import React from 'react';

import {FRONTEND_TOKEN_EDITOR_TYPES} from '../constants/frontendTokenEditorTypes';

const EDITOR_TYPE_OPTIONS: {label: string; value: string}[] = [
	{
		label: Liferay.Language.get('default'),
		value: FRONTEND_TOKEN_EDITOR_TYPES.default,
	},
	{
		label: Liferay.Language.get('color-picker'),
		value: FRONTEND_TOKEN_EDITOR_TYPES.colorPicker,
	},
	{
		label: Liferay.Language.get('length'),
		value: FRONTEND_TOKEN_EDITOR_TYPES.length,
	},
];

export default function EditorTypeField() {
	const [field] = useField<string>('editorType');

	const id = useId();

	return (
		<FieldBase id={id} label={Liferay.Language.get('editor-type')} required>
			<ClaySelectWithOption
				{...field}
				id={id}
				options={EDITOR_TYPE_OPTIONS}
			/>
		</FieldBase>
	);
}
