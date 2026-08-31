/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {isObject} from './utils';

const VARIABLES_FIELDS = [
	{
		field: 'inputVariables',
		isValid: Array.isArray,
		message: Liferay.Language.get(
			'input-variables-must-be-a-valid-json-array-in-the-x-node'
		),
	},
	{
		field: 'outputVariables',
		isValid: Array.isArray,
		message: Liferay.Language.get(
			'output-variables-must-be-a-valid-json-array-in-the-x-node'
		),
	},
	{
		field: 'rag',
		isValid: isObject,
		message: Liferay.Language.get(
			'retrieval-augmented-generation-must-be-a-valid-json-object-in-the-x-node'
		),
	},
	{
		field: 'tools',
		isValid: Array.isArray,
		message: Liferay.Language.get(
			'tools-must-be-a-valid-json-array-in-the-x-node'
		),
	},
];

export default function getInvalidVariables(elements, languageId) {
	for (const element of elements) {
		for (const {field, isValid, message} of VARIABLES_FIELDS) {
			const value = element.data?.[field];

			if (value !== undefined && !isValid(value)) {
				return {
					label: element.data?.label?.[languageId] || element.id,
					message,
				};
			}
		}
	}

	return null;
}
