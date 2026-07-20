/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {AUTO_TRANSLATABLE_TYPES} from './constants';

export function getPageContext(sourceLanguageId: string) {
	const fields: Record<string, string> = {};
	const html: Record<string, boolean> = {};

	for (const {isHtml, type} of AUTO_TRANSLATABLE_TYPES) {
		const inputs = document.querySelectorAll<HTMLInputElement>(
			`[data-localizable="true"][data-field-type="${type}"] [type="hidden"][name$="_${sourceLanguageId}"]`
		);

		for (const input of inputs) {
			const name = input.name.replace(/_[a-z]{2}_[A-Z]{2}$/, '');

			fields[name] = input.value;
			html[name] = isHtml;
		}
	}

	return {fields, html};
}
