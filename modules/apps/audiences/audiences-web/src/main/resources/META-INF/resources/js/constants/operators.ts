/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const DATE_OPERATOR_LABELS: Record<string, string> = {
	gt: Liferay.Language.get('is-after'),
	gte: Liferay.Language.get('is-on-or-after'),
	lt: Liferay.Language.get('is-before'),
	lte: Liferay.Language.get('is-on-or-before'),
};

const NUMBER_OPERATOR_LABELS: Record<string, string> = {
	gt: Liferay.Language.get('is-greater-than'),
	gte: Liferay.Language.get('is-greater-than-or-equal-to'),
	lt: Liferay.Language.get('is-less-than'),
	lte: Liferay.Language.get('is-less-than-or-equal-to'),
};

const SHARED_OPERATOR_LABELS: Record<string, string> = {
	eq: Liferay.Language.get('equals'),
	includes: Liferay.Language.get('contains'),
	not_eq: Liferay.Language.get('not-equals'),
	not_includes: Liferay.Language.get('does-not-contain'),
};

const EQUALITY_OPERATORS = ['eq', 'not_eq'];

const ORDERED_OPERATORS = ['eq', 'gt', 'gte', 'lt', 'lte', 'not_eq'];

const SET_OPERATORS = ['includes', 'not_includes'];

const TEXT_OPERATORS = ['eq', 'includes', 'not_eq', 'not_includes'];

export function getOperatorLabel(operator: string, inputType: string): string {
	return (
		SHARED_OPERATOR_LABELS[operator] ||
		(inputType === 'date'
			? DATE_OPERATOR_LABELS[operator]
			: NUMBER_OPERATOR_LABELS[operator]) ||
		operator
	);
}

export function getOperators(inputType: string, type: string): string[] {
	if (inputType === 'date') {
		return ORDERED_OPERATORS;
	}

	if (type === 'boolean') {
		return EQUALITY_OPERATORS;
	}

	if (type === 'set') {
		return SET_OPERATORS;
	}

	if (type === 'number') {
		return ORDERED_OPERATORS;
	}

	if (inputType === 'select') {
		return EQUALITY_OPERATORS;
	}

	return TEXT_OPERATORS;
}
