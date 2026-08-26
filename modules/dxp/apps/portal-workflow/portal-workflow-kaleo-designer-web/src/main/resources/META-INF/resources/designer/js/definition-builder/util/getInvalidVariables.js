/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const VARIABLES_FIELD_TITLES = {
	inputVariables: Liferay.Language.get('input-variables'),
	outputVariables: Liferay.Language.get('output-variables'),
};

export default function getInvalidVariables(elements, languageId) {
	for (const element of elements) {
		for (const [field, fieldTitle] of Object.entries(
			VARIABLES_FIELD_TITLES
		)) {
			const value = element.data?.[field];

			if (value !== undefined && !Array.isArray(value)) {
				return {
					fieldTitle,
					label: element.data?.label?.[languageId] || element.id,
				};
			}
		}
	}

	return null;
}
