/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import getFieldControls, {FieldControl} from './getFieldControls';
import getRichTextEditor from './getRichTextEditor';

function setControlValue(control: FieldControl, value: string) {
	if (control instanceof HTMLSelectElement) {
		return;
	}

	control.value = value;

	control.dispatchEvent(new Event('input', {bubbles: true}));
}

export default function applyFieldValues(
	form: Element,
	values: Record<string, string>
): void {
	Object.entries(values).forEach(([name, value]) => {
		const controls = getFieldControls(form, name);

		if (!controls.length) {
			return;
		}

		const editor = getRichTextEditor(controls[0]);

		if (editor) {
			editor.setData(value);

			return;
		}

		setControlValue(controls[0], value);
	});
}
