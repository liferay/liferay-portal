/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import getFieldControls from './getFieldControls';
import getRichTextEditor from './getRichTextEditor';

export default function getFieldValues(
	form: Element,
	objectFields: Array<{name: string}>
): Record<string, string> {
	const values: Record<string, string> = {};

	objectFields.forEach(({name}) => {
		const controls = getFieldControls(form, name);

		if (!controls.length) {
			return;
		}

		const editor = getRichTextEditor(controls[0]);

		if (editor) {
			values[name] = editor.getData();

			return;
		}

		const filledControl = controls.find((control) => control.value.trim());

		if (filledControl) {
			values[name] = filledControl.value;
		}
	});

	return values;
}
