/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export default function focusInvalidElement() {
	const element: HTMLInputElement | null = document.querySelector(
		'.form-group.has-error input:not([type="hidden"]), .form-group.has-error button'
	);

	if (element) {
		element.focus();
	}
}
