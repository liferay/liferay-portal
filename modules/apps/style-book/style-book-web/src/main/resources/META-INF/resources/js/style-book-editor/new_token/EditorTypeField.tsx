/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClaySelectWithOption} from '@clayui/form';
import {useField} from 'formik';
import {FieldBase, useId} from 'frontend-js-components-web';
import React from 'react';

const EDITOR_TYPE_OPTIONS: {label: string; value: string}[] = [
	{label: Liferay.Language.get('default'), value: 'Default'},
	{label: Liferay.Language.get('color-picker'), value: 'ColorPicker'},
	{label: Liferay.Language.get('length'), value: 'Length'},
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
