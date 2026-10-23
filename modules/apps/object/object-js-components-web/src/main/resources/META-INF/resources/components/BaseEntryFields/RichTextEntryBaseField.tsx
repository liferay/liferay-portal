/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	CKEditor5ClassicEditor,
	LiferayEditorConfig,
	TEditor,
} from 'frontend-editor-ckeditor-web';
import {FieldBase} from 'frontend-js-components-web';
import React from 'react';

interface RichTextEntryBaseFieldProps {
	ckEditor5Config?: LiferayEditorConfig;
	error?: string;
	label: string;
	onChange: (event: Event, editor: TEditor) => void;
	required?: boolean;
	value: string;
}

export function RichTextEntryBaseField({
	ckEditor5Config,
	error,
	label,
	onChange,
	required,
	value,
}: RichTextEntryBaseFieldProps) {
	return (
		<FieldBase errorMessage={error} label={label} required={required}>
			<CKEditor5ClassicEditor
				className="w-100"
				config={ckEditor5Config}
				data={value}
				onChange={(event, editor) => onChange(event, editor)}
			/>
		</FieldBase>
	);
}
