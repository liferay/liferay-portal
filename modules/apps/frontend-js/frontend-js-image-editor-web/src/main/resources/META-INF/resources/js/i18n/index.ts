/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import en from './en';

export type TranslationKey = keyof typeof en;

/**
 * In the portal the host builds this with one literal
 * `Liferay.Language.get` call per key: the language filter substitutes
 * literals only, never dynamic lookups.
 */
export type EditorMessages = Partial<Record<TranslationKey, string>>;

let dictionary: Record<string, string> = en;

export function setMessages(value: EditorMessages | null): void {
	if (value === null) {
		dictionary = en;

		return;
	}

	const merged: Record<string, string> = {...en};

	for (const [key, translation] of Object.entries(value)) {
		if (typeof translation === 'string') {
			merged[key] = translation;
		}
	}

	dictionary = merged;
}

export function t(
	key: TranslationKey,
	...args: Array<string | number>
): string {
	let value = dictionary[key] ?? key;

	args.forEach((arg, index) => {
		value = value.replaceAll(`{${index}}`, String(arg));
	});

	return value;
}
