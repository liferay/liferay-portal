/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const DATE_OPERATOR_LANGUAGE_KEYS: Record<string, string> = {
	gt: 'is-after',
	gte: 'is-on-or-after',
	lt: 'is-before',
	lte: 'is-on-or-before',
};

const NUMBER_OPERATOR_LANGUAGE_KEYS: Record<string, string> = {
	gt: 'is-greater-than',
	gte: 'is-greater-than-or-equal-to',
	lt: 'is-less-than',
	lte: 'is-less-than-or-equal-to',
};

const SHARED_OPERATOR_LANGUAGE_KEYS: Record<string, string> = {
	eq: 'is',
	includes: 'contains',
	not_eq: 'is-not',
	not_includes: 'does-not-contain',
};

export function getOperatorLabel(operator: string, type: string): string {
	const languageKey =
		SHARED_OPERATOR_LANGUAGE_KEYS[operator] ||
		(type === 'date'
			? DATE_OPERATOR_LANGUAGE_KEYS[operator]
			: NUMBER_OPERATOR_LANGUAGE_KEYS[operator]);

	return languageKey ? Liferay.Language.get(languageKey) : operator;
}
