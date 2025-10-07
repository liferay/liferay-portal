/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Fields} from './Types';

type MetadataValues = Record<string, string>;

export const MetadataFieldsManager = {
	buildSelector(fieldName: string, languageId: string): string {
		return `[type="hidden"][data-field-name="${fieldName}"][data-languageid="${languageId}"]`;
	},

	getAllMetadataValues(fields: Fields, languageId: string): MetadataValues {
		const values: MetadataValues = {};

		Object.keys(fields).forEach((fieldName) => {
			const value = this.getMetadataValue(fieldName, languageId);
			if (value !== null) {
				values[fieldName] = value;
			}
		});

		return values;
	},

	getMetadataValue(fieldName: string, languageId: string): string | null {
		const selector = this.buildSelector(fieldName, languageId);

		return (
			document.querySelector<HTMLInputElement>(selector)?.value ?? null
		);
	},

	resetAllMetadataValues(fields: Fields, languageId: string): void {
		Object.keys(fields).forEach((fieldName) => {
			this.resetMetadataValue(fieldName, languageId);
		});
	},

	resetMetadataValue(fieldName: string, languageId: string): void {
		const selector = this.buildSelector(fieldName, languageId);
		const hiddenInputs =
			document.querySelectorAll<HTMLInputElement>(selector);

		hiddenInputs.forEach((input) => input.remove());
	},

	restoreAllMetadataValues(
		languageId: string,
		values: MetadataValues,
		defaultLanguageId: string
	): void {
		Object.entries(values).forEach(([fieldName, value]) => {
			this.setMetadataValue(
				fieldName,
				languageId,
				value,
				defaultLanguageId
			);
		});
	},

	setMetadataValue(
		fieldName: string,
		languageId: string,
		value: string,
		defaultLanguageId: string
	): void {
		const selector = this.buildSelector(fieldName, languageId);
		const hiddenInput = document.querySelector<HTMLInputElement>(selector);

		if (hiddenInput) {
			let fieldValue = value;

			if (!fieldValue) {
				const defaultInput = document.querySelector<HTMLInputElement>(
					this.buildSelector(fieldName, defaultLanguageId)
				);

				if (defaultInput) {
					fieldValue = defaultInput.value;
				}
			}

			hiddenInput.value = fieldValue;
		}
	},
};
