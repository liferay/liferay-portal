/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// based on FDS code
// https://github.com/liferay/liferay-portal/blob/master/modules/apps/frontend-data-set/frontend-data-set-web/src/main/resources/META-INF/resources/cell_renderers/DateTimeRenderer.tsx

export default function dateFormat(value: string) {
	if (!value) {
		return null;
	}

	const locale = Liferay.ThemeDisplay.getBCP47LanguageId();

	const formattedDate = new Intl.DateTimeFormat(locale, {
		day: 'numeric',
		hour: 'numeric',
		minute: 'numeric',
		month: 'short',
		second: 'numeric',
		timeZone: 'UTC',
		year: 'numeric',
	}).format(new Date(value));

	return formattedDate;
}
