/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	EVENT_INPUT_REGISTERED,
	EVENT_TRANSLATION_STATUS,
	getSelectedLanguageId,
} from './LocalizationSelect';
import {getTranslationInput} from './getTranslationInput';

type Args = {
	availableLanguageIds?: string[];
	changeTextDirection?: boolean;
	customLocaleChangeHandler?: boolean;
	defaultLanguageId: Liferay.Language.Locale;
	hasMultipleValues?: boolean;
	initialValues?: Record<string, any>;
	inputElement?: HTMLInputElement;
	inputName: string;
	localizationInputsContainer: HTMLElement;
	localizedTextContainer?: HTMLElement;
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
	availableLanguageIds,
	changeTextDirection = true,
	customLocaleChangeHandler = false,
	defaultLanguageId,
	hasMultipleValues = false,
	initialValues,
	inputElement,
	inputName,
	localizationInputsContainer,
	localizedTextContainer,
	namespace,
	onAutoTranslate,
	onLocaleChange,
	onMarkAsTranslated,
	onResetTranslation,
}: Args) {
	const setTranslationInputsValue = (
		languageId: string,
		value: string | string[] | null
	) => {
		if (hasMultipleValues) {

			// Remove all inputs for current language

			const inputs = localizationInputsContainer.querySelectorAll(
				`[data-language-id="${languageId}"]`
			);

			for (const input of inputs) {
				input.remove();
			}

			// Add empty input if no values

			const values = Array.isArray(value)
				? value
				: value
					? value.split(',')
					: null;

			if (values === null || !values.length) {
				getTranslationInput({
					inputId: inputElement?.id || inputName,
					inputName,
					languageId,
					localizationInputsContainer,
					namespace,
				});
			}

			// Add inputs with new values

			else {
				for (const [index, val] of values.entries()) {
					const input = getTranslationInput({
						inputId: inputElement?.id || inputName,
						inputName,
						languageId,
						localizationInputsContainer,
						namespace: `${namespace || ''}-${index}`,
						override: false,
					});

					setInputValue({input, value: val});
				}
			}
		}
		else {
			const input = getTranslationInput({
				inputId: inputElement?.id || inputName,
				inputName,
				languageId,
				localizationInputsContainer,
				namespace,
			});

			setInputValue({input, value: value as string});
		}
	};

	// Create hidden inputs for initial values if any

	if (initialValues) {
		Object.entries(initialValues).forEach(([languageId, value]) => {
			setTranslationInputsValue(languageId, value);
		});
	}

	const form = inputElement?.closest('.lfr-layout-structure-item-form');

	let currentLanguageId =
		getSelectedLanguageId(form?.id) ||
		(availableLanguageIds?.includes(Liferay.ThemeDisplay.getLanguageId())
			? Liferay.ThemeDisplay.getLanguageId()
			: defaultLanguageId);

	if (changeTextDirection) {
		inputElement?.setAttribute(
			'dir',
			Liferay.Language.direction[currentLanguageId]!
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

			// Auto translation won't work for inputs with multiple values

			if (hasMultipleValues) {
				return;
			}

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

				if (languageId === currentLanguageId) {
					setInputValue({
						input: inputElement,
						value,
					});
				}

				setInputValue({
					input: translationInput,
					value,
				});
			}

			Liferay.fire(EVENT_TRANSLATION_STATUS, {
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
			localizedTextContainer?.classList.toggle(
				'd-none',
				languageId === defaultLanguageId
			);

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

			Liferay.fire(EVENT_TRANSLATION_STATUS, {
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

				setTranslationInputsValue(languageId, null);
			}

			Liferay.fire(EVENT_TRANSLATION_STATUS, {
				languageId: currentLanguageId,
			});
		}
	);

	Liferay.fire(EVENT_INPUT_REGISTERED);

	return {
		onBlur: (value: string | null = null) => {
			if (
				localizedTextContainer &&
				currentLanguageId === defaultLanguageId
			) {
				const hasValue = Boolean(value);

				localizedTextContainer.innerText =
					value ||
					Liferay.Language.get(
						'there-is-no-default-value-to-localize'
					);

				localizedTextContainer.classList.toggle('text-info', !hasValue);
				localizedTextContainer.classList.toggle(
					'text-italic',
					hasValue
				);
				localizedTextContainer.classList.toggle(
					'text-secondary',
					hasValue
				);
			}
		},
		onChange: (value: string | string[] | null = null) => {
			if (value !== null) {
				setTranslationInputsValue(currentLanguageId, value);
			}

			Liferay.fire(EVENT_TRANSLATION_STATUS, {
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
	value: string | boolean | null;
}) {
	if (!input) {
		return;
	}

	if (input.type === 'checkbox') {
		input.checked = value === 'true';
	}
	else if (value !== null) {
		input.value = String(value);
	}
	else {
		input.removeAttribute('value');
	}
}
