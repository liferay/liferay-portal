/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const wrapper = fragmentElement;

const fileInput = document.getElementById(`${fragmentNamespace}-file-upload`);
const fileName = wrapper.querySelector('.forms-file-upload-file-name');
const hiddenFileInput = document.getElementById(
	`${fragmentNamespace}-file-upload-hidden`
);
const removeButton = document.getElementById(
	`${fragmentNamespace}-file-upload-remove-button`
);
const selectButton = document.getElementById(
	`${fragmentNamespace}-file-upload-button-label`
);

function showRemoveButton() {
	removeButton.classList.remove('d-none');
	removeButton.addEventListener('click', onRemoveFile);
}

if (
	!input.attributes.selectFromDocumentLibrary &&
	input.required &&
	input.value
) {
	fileInput.required = false;
}

let previousFiles = null;

function onInputChange() {
	if (!fileInput.files.length && previousFiles) {
		const dataTransfer = new DataTransfer();

		dataTransfer.items.add(previousFiles);

		fileInput.files = dataTransfer.files;

		if (input.required) {
			fileInput.required = true;
		}
	}

	fileName.innerText = fileInput.files[0].name;
	fileInput.setAttribute('name', input.name);

	hiddenFileInput.setAttribute('name', '');
	hiddenFileInput.value = '';

	showRemoveButton();
}

function onRemoveFile() {
	previousFiles = null;

	fileInput.value = '';
	fileName.innerText = '';

	if (input.required) {
		fileInput.required = true;
	}

	hiddenFileInput.value = '';

	removeButton.classList.add('d-none');
	removeButton.removeEventListener('click', onRemoveFile);
}

function onSelectFile(event, onChange, setTranslationInputValue) {
	event.preventDefault();

	Liferay.Util.openSelectionModal({
		onSelect(selectedItem) {
			const {fileEntryId, title} = JSON.parse(selectedItem.value);

			if (onChange) {
				setTranslationInputValue({
					fileName: title,
					value: fileEntryId,
				});

				onChange();
			}

			fileInput.value = fileEntryId;
			fileName.innerText = title;

			showRemoveButton();
		},
		selectEventName: `${fragmentNamespace}selectFileEntry`,
		url: input.attributes.selectFromDocumentLibraryURL,
	});
}

const onSelectFromUserComputer = () => {
	previousFiles = fileInput.files[0] || null;

	fileInput.click();
};

function getTranslationInput(namespace, languageId, inputId) {
	return document.getElementById(`${namespace}${inputId}_${languageId}`);
}

const setFileName = (input) => {
	if (!input) {
		fileName.innerText = '';
	}
	else {
		fileName.innerText = input.dataset.fileName || '';
	}

	if (fileName.innerText) {
		removeButton.classList.remove('d-none');
	}
	else {
		removeButton.classList.add('d-none');
	}
};

if (layoutMode === 'edit') {
	selectButton.classList.add('disabled');
}
else {
	let selectFileEvent = onSelectFromUserComputer;

	if (input.attributes.selectFromDocumentLibrary) {
		selectFileEvent = onSelectFile;
	}

	fileInput.addEventListener('change', onInputChange);

	if (fileName.innerText !== '') {
		showRemoveButton();
	}

	const defaultLanguageId = themeDisplay.getDefaultLanguageId();
	const inputElement = fileInput;

	let currentLanguageId = defaultLanguageId;

	import('@liferay/fragment-impl/api').then(
		({
			getOrCreateTranslationInput,
			registerLocalizedInput,
			registerUnlocalizedInput,
		}) => {
			if (input.localizable) {

				// Set initial values

				const initialValues = Object.keys(input.valueI18n).map(
					(key) => [
						key,
						{
							fileEntryId: input.valueI18n[key],
							name: input.attributes.fileNameI18n[key] || '',
						},
					]
				);

				initialValues.forEach(([languageId, value]) => {
					const translationInput = getOrCreateTranslationInput(
						inputElement.id,
						input.name,
						languageId,
						inputElement.parentNode,
						fragmentNamespace
					);

					translationInput.value = value.fileEntryId;
					translationInput.dataset.fileName = value.name;
				});

				const isFromDocumentLibrary =
					input.attributes.selectFromDocumentLibrary;

				const {onChange} = registerLocalizedInput({
					changeTextDirection: false,
					customLocaleChangeHandler: true,
					defaultLanguageId,
					onLocaleChange: ({languageId}) => {
						currentLanguageId = languageId;

						const translationInput = getTranslationInput(
							fragmentNamespace,
							languageId,
							inputElement.id
						);

						if (translationInput) {
							setFileName(translationInput);
						}
						else {
							const defaultTranslationInput = getTranslationInput(
								fragmentNamespace,
								defaultLanguageId,
								inputElement.id
							);

							setFileName(defaultTranslationInput);
						}
					},
				});

				const setTranslationInputValue = ({fileName, value}) => {
					const type =
						isFromDocumentLibrary === false ? 'file' : 'hidden';

					const translationInput = getOrCreateTranslationInput(
						inputElement.id,
						input.name,
						currentLanguageId,
						inputElement.parentNode,
						fragmentNamespace,
						type
					);

					if (isFromDocumentLibrary) {
						translationInput.value = value;
						translationInput.dataset.fileName = fileName;
					}
					else {
						const files = value;

						if (files?.length) {
							const dataTransfer = new DataTransfer();

							if (files?.length) {
								[...files].forEach((file) => {
									dataTransfer.items.add(file);
								});
							}

							translationInput.files = dataTransfer.files;
							translationInput.dataset.fileName =
								dataTransfer.files[0].name;
						}
					}
				};

				if (isFromDocumentLibrary) {
					selectButton.addEventListener('click', (event) => {
						onSelectFile(event, onChange, setTranslationInputValue);
					});
				}
				else {
					inputElement.addEventListener('change', (event) => {
						setTranslationInputValue({
							value: event.target.files,
						});

						onChange();
					});

					selectButton.addEventListener(
						'click',
						onSelectFromUserComputer
					);
				}

				removeButton.addEventListener('click', () => {
					fileName.innerText = '';

					removeButton.classList.add('d-none');

					const translationInput = getOrCreateTranslationInput(
						inputElement.id,
						input.name,
						currentLanguageId,
						inputElement.parentNode,
						fragmentNamespace
					);

					translationInput.value = '';
					translationInput.dataset.fileName = '';
				});
			}
			else {
				const unlocalizedFieldsState =
					input.attributes.unlocalizedFieldsState;

				registerUnlocalizedInput({
					changeTextDirection: false,
					customLocaleChangeHandler: true,
					defaultLanguageId,
					inputElement,
					onLocaleChange: (languageId) => {
						if (defaultLanguageId !== languageId) {
							if (unlocalizedFieldsState === 'read-only') {
								selectButton.classList.add('d-none');

								fileName.setAttribute('readonly', 'true');
								fileName.setAttribute('tabindex', '0');
								fileName.classList.add('form-control');

								if (!fileName.innerText) {
									fileName.innerText =
										fileName.dataset.placeholder;
								}
							}
							else {
								selectButton.setAttribute('disabled', true);

								fileName.classList.add('text-secondary');
							}

							removeButton.classList.add('d-none');
						}
						else {
							if (unlocalizedFieldsState === 'read-only') {
								selectButton.classList.remove('d-none');

								fileName.removeAttribute('readonly');
								fileName.removeAttribute('tabindex');
								fileName.classList.remove('form-control');

								if (
									fileName.innerText ===
									fileName.dataset.placeholder
								) {
									fileName.innerText = '';
								}
							}
							else {
								selectButton.removeAttribute('disabled');

								fileName.classList.remove('text-secondary');
							}

							if (fileName.innerText) {
								removeButton.classList.remove('d-none');
							}
						}
					},
					readOnlyInputLabel: document.getElementById(
						`${fragmentNamespace}-file-upload-read-only`
					),
					unlocalizedFieldsState,
					unlocalizedMessageContainer: document.getElementById(
						`${fragmentNamespace}-unlocalized-info`
					),
				});

				selectButton.addEventListener('click', selectFileEvent);
			}
		}
	);
}
