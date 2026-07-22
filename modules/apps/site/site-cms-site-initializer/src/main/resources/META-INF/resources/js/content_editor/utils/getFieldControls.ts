/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export type FieldControl = HTMLElement;

const LOCALE_SUFFIX = /^(_[a-z]{2}_[A-Z]{2})?$/;

export default function getFieldControls(
	form: Element,
	fieldName: string
): FieldControl[] {
	const prefix = `ObjectField_${fieldName}`;

	const controls = Array.from(
		form.querySelectorAll<HTMLElement>(`[name^="${prefix}"]`)
	).filter((control) =>
		LOCALE_SUFFIX.test(control.getAttribute('name')!.slice(prefix.length))
	);

	if (controls.length) {
		return controls;
	}

	return Array.from(
		form.querySelectorAll<HTMLElement>(`[data-field-name="${prefix}"]`)
	);
}
