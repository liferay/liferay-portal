/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export function getTranslationInput(options: {
	inputId: string;
	inputName: string;
	languageId: string;
	localizationInputsContainer: HTMLElement;
	namespace: string;
	type?: 'file' | 'hidden';
}): HTMLInputElement;

export function getTranslationInput(options: {
	createIfMissing?: false;
	inputId: string;
	inputName: string;
	languageId: string;
	localizationInputsContainer: HTMLElement;
	namespace: string;
	type?: 'file' | 'hidden';
}): HTMLInputElement | null;

export function getTranslationInput({
	createIfMissing = true,
	inputId,
	inputName,
	languageId,
	localizationInputsContainer,
	namespace,
	type = 'hidden',
}: {
	createIfMissing?: boolean;
	inputId: string;
	inputName: string;
	languageId: string;
	localizationInputsContainer: HTMLElement;
	namespace: string;
	type?: 'file' | 'hidden';
}): HTMLInputElement | null {
	const id = `${namespace}${inputId}_${languageId}`;

	let translationInput = document.getElementById(id) as HTMLInputElement;

	// Return null if it does not exist and createIfMissing is false

	if (!translationInput && !createIfMissing) {
		return null;
	}

	// Create the input if it does not exist

	if (!translationInput) {
		translationInput = document.createElement('input');
		translationInput.type = type;
		translationInput.id = id;
		translationInput.name = `${inputName}_${languageId}`;
		localizationInputsContainer.appendChild(translationInput);

		if (translationInput.type !== 'hidden') {
			translationInput.className = 'd-none';
		}
	}

	// When a file upload input (files from computer) has initial values and
	// then a file is selected from the computer. The inputs with initial
	// values has a hidden type becomes a file type.

	else if (translationInput.type === 'hidden' && type === 'file') {
		translationInput.value = '';
		translationInput.type = 'file';
	}

	return translationInput;
}
