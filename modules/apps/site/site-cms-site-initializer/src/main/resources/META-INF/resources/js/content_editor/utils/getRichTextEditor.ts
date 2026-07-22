/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

interface RichTextEditorModelWriter {
	createRangeIn: (element: unknown) => unknown;
	remove: (range: unknown) => void;
}

export interface RichTextEditor {
	data?: {
		processor: {toView: (data: string) => unknown};
		toModel: (viewFragment: unknown) => unknown;
	};
	getData: () => string;
	model?: {
		change: (callback: (writer: RichTextEditorModelWriter) => void) => void;
		document: {getRoot: () => unknown};
		insertContent: (content: unknown, selectable: unknown) => void;
	};
	setData: (data: string) => void;
}

export default function getRichTextEditor(
	control: Element
): RichTextEditor | null {
	const container =
		control.closest('[data-field-name]') ??
		control.closest('.form-group') ??
		control;

	const host = Array.from(
		container.querySelectorAll<
			HTMLElement & {ckeditorInstance?: RichTextEditor}
		>('*')
	).find((node) => node.ckeditorInstance);

	return host?.ckeditorInstance ?? null;
}
