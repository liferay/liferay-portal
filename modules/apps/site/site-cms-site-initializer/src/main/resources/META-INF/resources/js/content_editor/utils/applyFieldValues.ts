/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import getFieldControls, {FieldControl} from './getFieldControls';
import getRichTextEditor, {RichTextEditor} from './getRichTextEditor';

function setRichTextEditorData(editor: RichTextEditor, value: string) {
	const {data, model} = editor;

	if (!data || !model) {
		editor.setData(value);

		return;
	}

	const modelFragment = data.toModel(data.processor.toView(value));

	const root = model.document.getRoot();

	model.change((writer) => {
		writer.remove(writer.createRangeIn(root));

		model.insertContent(modelFragment, root);
	});
}

function setControlValue(control: FieldControl, value: string) {
	if (
		!(control instanceof HTMLInputElement) &&
		!(control instanceof HTMLTextAreaElement)
	) {
		return;
	}

	const prototype =
		control instanceof HTMLTextAreaElement
			? HTMLTextAreaElement.prototype
			: HTMLInputElement.prototype;

	const valueSetter = Object.getOwnPropertyDescriptor(
		prototype,
		'value'
	)?.set;

	valueSetter?.call(control, value);

	control.dispatchEvent(new Event('input', {bubbles: true}));
	control.dispatchEvent(new Event('change', {bubbles: true}));
}

function getLocaleControl(
	controls: FieldControl[],
	languageId?: string
): FieldControl {
	if (!languageId || controls.length < 2) {
		return controls[0];
	}

	return (
		controls.find((control) =>
			control.getAttribute('name')?.endsWith(`_${languageId}`)
		) ?? controls[0]
	);
}

export default function applyFieldValues(
	form: Element,
	values: Record<string, string>,
	languageId?: string
): void {
	Object.entries(values).forEach(([name, value]) => {
		const controls = getFieldControls(form, name);

		if (!controls.length) {
			return;
		}

		const editor = getRichTextEditor(controls[0]);

		if (editor) {
			setRichTextEditorData(editor, value);

			return;
		}

		setControlValue(getLocaleControl(controls, languageId), value);
	});
}
