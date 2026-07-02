/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// @ts-nocheck

const LANGUAGE_MAP: Record<string, string> = {
	'x-of-x': '{0} of {1}',
};

(globalThis as any).Liferay = {
	...(globalThis.Liferay || {}),
	Language: {
		...(globalThis.Liferay.Language || {}),
		get: (key: string) => LANGUAGE_MAP[key] ?? key,
	},

	ThemeDisplay: {
		...(globalThis.Liferay.ThemeDisplay || {}),
	},
};
