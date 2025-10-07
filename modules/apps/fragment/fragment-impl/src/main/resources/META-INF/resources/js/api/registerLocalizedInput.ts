/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getTranslationInput} from './getTranslationInput';

type Args = {
	changeTextDirection: boolean;
	customLocaleChangeHandler: boolean;
	defaultLanguageId: Liferay.Language.Locale;
	initialValues?: Record<string, any>;
	inputElement?: HTMLInputElement;
	inputName: string;
	localizationInputsContainer: HTMLElement;
	namespace: string;
	onAutoTranslate?: ({
		languageId,
		value,
	}: {
		languageId: string;
		value?: string;
	}) => void;
	onLocaleChange?: ({
		languageId,
		value,
	}: {
		languageId: string;
		value?: string;
	}) => void;
	onMarkAsTranslated?: () => void;
	onResetTranslation?: () => void;
};

export function registerLocalizedInput({
	changeTextDirection = true,
	customLocaleChangeHandler = false,
	defaultLanguageId,
	initialValues,
	inputElement,
	inputName,
	localizationInputsContainer,
	namespace,
	onAutoTranslate,
	onLocaleChange,
	onMarkAsTranslated,
	onResetTranslation,
}: Args) {

	// Create hidden inputs for initial values if any

	if (initialValues) {
		Object.entries(initialValues).forEach(([languageId, value]) => {
			const input = getTranslationInput({
				inputId: inputElement?.id || inputName,
				inputName,
				languageId,
				localizationInputsContainer,
				namespace,
			});

			input.value = value;
		});
	}

	let currentLanguageId = defaultLanguageId;

	if (changeTextDirection) {
		inputElement?.setAttribute(
			'dir',
			Liferay.Language.direction[defaultLanguageId]!
		);
	}

	Liferay.on(
		'localizationSelect:autoTranslate',
		({
			fields,
			formId,
			languageId,
		}: {
			fields: Record<string, string>;
			formId?: string;
			languageId: Liferay.Language.Locale;
		}) => {

			// Return if event is sent from a different form

			const form = inputElement?.closest(
				'.lfr-layout-structure-item-form'
			);

			if (form && formId && !form.classList.contains(formId)) {
				return;
			}

			// Return if this field was not translated

			const value = fields[inputName];

			if (!value) {
				return;
			}

			// Call custom auto translation handler if passed

			if (onAutoTranslate) {
				onAutoTranslate({languageId, value});
			}

			// Otherwise update both visible and hidden input with translated value

			else {
				const translationInput = getTranslationInput({
					inputId: inputElement?.id || inputName,
					inputName,
					languageId,
					localizationInputsContainer,
					namespace,
				});

				setInputValue({
					input: inputElement,
					value,
				});

				setInputValue({
					input: translationInput,
					value,
				});
			}

			Liferay.fire('localizationSelect:updateTranslationStatus', {
				languageId,
			});
		}
	);

	Liferay.on(
		'localizationSelect:localeChanged',
		({
			formId,
			languageId,
		}: {
			formId?: string;
			languageId: Liferay.Language.Locale;
		}) => {

			// Return if event is sent from a different form

			const form = inputElement?.closest(
				'.lfr-layout-structure-item-form'
			);

			if (form && formId && !form.classList.contains(formId)) {
				return;
			}

			currentLanguageId = languageId;

			if (changeTextDirection) {
				inputElement?.setAttribute(
					'dir',
					Liferay.Language.direction[languageId]!
				);
			}

			if (customLocaleChangeHandler) {
				onLocaleChange?.({languageId});

				return;
			}

			const translationInput = getTranslationInput({
				createIfMissing: false,
				inputId: inputElement?.id || inputName,
				inputName,
				languageId,
				localizationInputsContainer,
				namespace,
			});

			if (
				translationInput &&
				translationInput.getAttribute('value') !== null
			) {
				onLocaleChange?.({languageId, value: translationInput.value});

				setInputValue({
					input: inputElement,
					value: translationInput.value,
				});
			}
			else {
				const defaultLanguageInput = getTranslationInput({
					inputId: inputElement?.id || inputName,
					inputName,
					languageId: defaultLanguageId,
					localizationInputsContainer,
					namespace,
				});

				onLocaleChange?.({
					languageId,
					value: defaultLanguageInput.value,
				});

				if (!inputElement) {
					return;
				}

				inputElement.value = defaultLanguageInput.value;
			}
		}
	);

	Liferay.on(
		'localizationSelect:markAsTranslated',
		({
			formId,
			languageId,
		}: {
			formId?: string;
			languageId: Liferay.Language.Locale;
		}) => {

			// Return if event is sent from a different form

			const form = inputElement?.closest(
				'.lfr-layout-structure-item-form'
			);

			if (form && formId && !form.classList.contains(formId)) {
				return;
			}

			const defaultLanguageInput = getTranslationInput({
				inputId: inputElement?.id || inputName,
				inputName,
				languageId: defaultLanguageId,
				localizationInputsContainer,
				namespace,
			});

			const translationInput = getTranslationInput({
				inputId: inputElement?.id || inputName,
				inputName,
				languageId,
				localizationInputsContainer,
				namespace,
			});

			// Do nothing if it's already translated

			if (translationInput.getAttribute('value')) {
				return;
			}

			// Call custom value change handler if passed

			if (onMarkAsTranslated) {
				onMarkAsTranslated();
			}

			// Otherwise update both visible and hidden input manually

			else {
				setInputValue({
					input: inputElement,
					value: defaultLanguageInput.value,
				});

				setInputValue({
					input: translationInput,
					value: defaultLanguageInput.value,
				});
			}

			Liferay.fire('localizationSelect:updateTranslationStatus', {
				languageId: currentLanguageId,
			});
		}
	);

	Liferay.on(
		'localizationSelect:resetTranslation',
		({
			formId,
			languageId,
		}: {
			formId?: string;
			languageId: Liferay.Language.Locale;
		}) => {

			// Return if event is sent from a different form

			const form = inputElement?.closest(
				'.lfr-layout-structure-item-form'
			);

			if (form && formId && !form.classList.contains(formId)) {
				return;
			}

			const defaultLanguageInput = getTranslationInput({
				inputId: inputElement?.id || inputName,
				inputName,
				languageId: defaultLanguageId,
				localizationInputsContainer,
				namespace,
			});

			const translationInput = getTranslationInput({
				inputId: inputElement?.id || inputName,
				inputName,
				languageId,
				localizationInputsContainer,
				namespace,
			});

			// Call custom value change handler if passed

			if (onResetTranslation) {
				onResetTranslation();
			}

			// Otherwise update both visible and hidden input manually

			else {
				setInputValue({
					input: inputElement,
					value: defaultLanguageInput.value,
				});

				setInputValue({
					input: translationInput,
					value: null,
				});
			}

			Liferay.fire('localizationSelect:updateTranslationStatus', {
				languageId: currentLanguageId,
			});
		}
	);

	return {
		onChange: (value = null) => {
			if (value !== null) {
				const translationInput = getTranslationInput({
					inputId: inputElement?.id || inputName,
					inputName,
					languageId: currentLanguageId,
					localizationInputsContainer,
					namespace,
				});

				translationInput.value = value;
			}

			Liferay.fire('localizationSelect:updateTranslationStatus', {
				languageId: currentLanguageId,
			});
		},
	};
}

function setInputValue({
	input,
	value,
}: {
	input?: HTMLInputElement;
	value: string | null;
}) {
	if (!input) {
		return;
	}

	if (input.type === 'checkbox') {
		input.checked = value === 'true';
	}
	else if (value !== null) {
		input.value = value;
	}
	else {
		input.removeAttribute('value');
	}
}
