/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export type DefaultLanguageLabels = {
	labels: Record<string, string>;
	locale: string;
};

export function getDefaultLanguageLabel({
	defaultLanguageLabels,
	key,
}: {
	defaultLanguageLabels: DefaultLanguageLabels;
	key: string;
}): string {
	return defaultLanguageLabels.labels?.[key] ?? Liferay.Language.get(key);
}
