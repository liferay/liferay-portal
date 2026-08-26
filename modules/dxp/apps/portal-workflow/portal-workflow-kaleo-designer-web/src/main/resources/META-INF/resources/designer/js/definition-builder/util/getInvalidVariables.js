/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const VARIABLES_FIELDS = ['inputVariables', 'outputVariables'];

export default function getInvalidVariables(elements, languageId) {
	for (const element of elements) {
		for (const field of VARIABLES_FIELDS) {
			const value = element.data?.[field];

			if (value !== undefined && !Array.isArray(value)) {
				return {
					field,
					label: element.data?.label?.[languageId] || element.id,
				};
			}
		}
	}

	return null;
}
