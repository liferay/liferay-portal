/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {generateObjectFields} from './generateObjectFields';

/**
 * Generates the pair of numeric object fields a formula operates on together
 * with the formula field itself, whose script applies the operator to the two
 * of them in the order they are returned.
 */
export function generateFormulaObjectFields({
	objectFieldBusinessType,
	operator,
	output,
}: {
	objectFieldBusinessType: 'Decimal' | 'Integer';
	operator: '*' | '+' | '-' | '/';
	output: 'Decimal' | 'Integer';
}) {
	const [firstObjectField, secondObjectField] = generateObjectFields({
		objectFieldBusinessTypes: [
			objectFieldBusinessType,
			objectFieldBusinessType,
		],
	});

	const [formulaObjectField] = generateObjectFields({
		objectFieldBusinessTypes: [
			{
				businessType: 'Formula',
				objectFieldSettings: [
					{name: 'output', value: output},
					{
						name: 'script',
						value: `${firstObjectField.name} ${operator} ${secondObjectField.name}`,
					},
				],
			},
		],
	});

	return {
		firstObjectField,
		formulaObjectField,
		objectFields: [firstObjectField, secondObjectField, formulaObjectField],
		secondObjectField,
	};
}
