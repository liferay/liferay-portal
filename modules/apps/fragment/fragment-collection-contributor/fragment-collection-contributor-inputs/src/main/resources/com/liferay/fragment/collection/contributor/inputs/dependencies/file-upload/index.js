/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const wrapper = fragmentElement;

const fileInput = document.getElementById(`${fragmentElementId}-file-upload`);
const fileName = wrapper.querySelector('.forms-file-upload-file-name');
const fileSizeError = document.getElementById(
	`${fragmentElementId}-file-upload-error`
);
const fileSizeErrorMessage = document.getElementById(
	`${fragmentElementId}-file-upload-error-message`
);
const formGroup = wrapper.querySelector('.form-group');
const hiddenFileInput = document.getElementById(
	`${fragmentElementId}-file-upload-hidden`
);
const removeButton = document.getElementById(
	`${fragmentElementId}-file-upload-remove-button`
);
const selectButton = document.getElementById(
	`${fragmentElementId}-file-upload-button-label`
);

const aiAssistantContainer = wrapper.querySelector('.file-upload-ai-assistant');

if (aiAssistantContainer && Liferay.FeatureFlags['LPD-62272']) {
	import('@liferay/ai-hub-cell-js-components-web/renderAIAssistantChat').then(
		({default: renderAIAssistantChat}) => {
			renderAIAssistantChat(aiAssistantContainer, {
				hideTriggerLabel: true,
				instructionDefinitionScope: 'cms',
				triggerRound: true,
			});
		}
	);
}

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
let showInputError = null;

function mbToBytes(mb) {
	return mb * 1024 * 1024;
}

function onInputChange() {
	if (
		input.attributes.maxFileSize &&
		fileInput.files[0] &&
		fileInput.files[0].size > mbToBytes(input.attributes.maxFileSize)
	) {
		showInputError({
			errorContainer: fileSizeError,
			errorMessageContainer: fileSizeErrorMessage,
			formGroup,
			message: fileSizeErrorMessage.getAttribute(
				'data-file-size-feedback'
			),
		});

		fileInput.value = '';

		return;
	}
	else {
		fileSizeError.classList.add('sr-only');
		formGroup.classList.remove('has-error');
	}

	if (!fileInput.files.length && previousFiles) {
		const dataTransfer = new DataTransfer();

		dataTransfer.items.add(previousFiles);

		fileInput.files = dataTransfer.files;

		if (input.required) {
			fileInput.required = true;
		}
	}

	fileName.innerText = fileInput.files[0].name;

	if (!input.localizable) {
		fileInput.setAttribute('name', input.name);
	}

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

	const updateInputData = ({title, value}) => {
		if (onChange) {
			setTranslationInputValue({fileName: title, value});

			onChange();
		}

		fileInput.value = value;
		fileName.innerText = title;

		showRemoveButton();
	};

	if (input.attributes.isCMS) {
		import('@liferay/fragment-impl/api').then(
			({openCMSFileSelectorModal}) => {
				const items = [];

				if (fileInput.value) {
					items.push({
						embedded: {
							file: {
								id: Number(fileInput.value),
								name: fileName.innerText,
							},
						},
					});
				}

				openCMSFileSelectorModal({
					allowDragAndDrop: true,
					allowedExtensions: input.attributes.allowedFileExtensions,
					config: {
						items,
						locator: {
							id: 'embedded.file.id',
							label: 'embedded.file.name',
							value: 'embedded.file.id',
						},
					},
					folderMemoryKey: `cms-file-upload:${fileInput.id}`,
					groupId: input.attributes.groupId,
					maxFileSize: mbToBytes(input.attributes.maxFileSize),
					onSelect(items) {
						if (items.length) {
							const {file} = items[0].embedded;

							updateInputData({title: file.name, value: file.id});
						}
					},
				});
			}
		);

		return;
	}

	Liferay.Util.openSelectionModal({
		onSelect(selectedItem) {
			const {fileEntryId, title} = JSON.parse(selectedItem.value);

			updateInputData({title, value: fileEntryId});
		},
		selectEventName: `${fragmentNamespace}selectFileEntry`,
		url: input.attributes.selectFromDocumentLibraryURL,
	});
}

const onSelectFromUserComputer = () => {
	previousFiles = fileInput.files[0] || null;

	fileInput.click();
};

function getFragmentTranslationInput(namespace, languageId, inputId) {
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

	const defaultLanguageId = input.attributes.defaultLanguageId;
	const inputElement = fileInput;

	let currentLanguageId = defaultLanguageId;

	import('@liferay/fragment-impl/api').then(
		({
			getTranslationInput,
			registerLocalizedInput,
			registerUnlocalizedInput,
			showInputError: showInputErrorFn,
		}) => {
			showInputError = showInputErrorFn;

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
					const translationInput = getTranslationInput({
						inputId: inputElement.id,
						inputName: input.name,
						languageId,
						localizationInputsContainer: inputElement.parentNode,
						namespace: fragmentElementId,
					});

					translationInput.value = value.fileEntryId;
					translationInput.dataset.fileName = value.name;
				});

				const isFromDocumentLibrary =
					input.attributes.selectFromDocumentLibrary;

				const {onChange} = registerLocalizedInput({
					availableLanguageIds: input.attributes.availableLanguageIds,
					changeTextDirection: false,
					customLocaleChangeHandler: true,
					defaultLanguageId,
					inputElement: fileInput,
					inputName: input.name,
					localizationInputsContainer: inputElement.parentNode,
					namespace: fragmentElementId,
					onLocaleChange: ({languageId}) => {
						currentLanguageId = languageId;

						const defaultTranslationInput =
							getFragmentTranslationInput(
								fragmentElementId,
								defaultLanguageId,
								inputElement.id
							);

						const translationInput = getFragmentTranslationInput(
							fragmentElementId,
							languageId,
							inputElement.id
						);

						if (translationInput) {
							setFileName(translationInput);
						}
						else {
							setFileName(defaultTranslationInput);
						}
					},
					onMarkAsTranslated: () => {
						const defaultTranslationInput =
							getFragmentTranslationInput(
								fragmentElementId,
								defaultLanguageId,
								inputElement.id
							);

						setFileName(defaultTranslationInput);

						if (defaultTranslationInput.type === 'file') {
							setTranslationInputValue({
								fileName:
									defaultTranslationInput.dataset.fileName,
								type: 'file',
								value: defaultTranslationInput.files,
							});
						}
						else {
							setTranslationInputValue({
								fileName:
									defaultTranslationInput.dataset.fileName,
								type: 'document',
								value: defaultTranslationInput.value,
							});
						}
					},
					onResetTranslation: () => {
						const defaultTranslationInput =
							getFragmentTranslationInput(
								fragmentElementId,
								defaultLanguageId,
								inputElement.id
							);

						const translationInput = getFragmentTranslationInput(
							fragmentElementId,
							currentLanguageId,
							fileInput.id
						);

						setFileName(defaultTranslationInput);

						if (translationInput.type === 'file') {
							translationInput.parentNode.removeChild(
								translationInput
							);
						}
						else {
							translationInput.removeAttribute('data-file-name');
							translationInput.removeAttribute('value');
						}
					},
				});

				const setTranslationInputValue = (props) => {
					const {fileName, value} = props;

					const type =
						props.type ||
						(isFromDocumentLibrary ? 'document' : 'file');

					const translationInput = getTranslationInput({
						inputId: inputElement.id,
						inputName: input.name,
						languageId: currentLanguageId,
						localizationInputsContainer: inputElement.parentNode,
						namespace: fragmentElementId,
						type: type === 'file' ? 'file' : 'hidden',
					});

					if (type === 'document') {
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

					const translationInput = getTranslationInput({
						inputId: inputElement.id,
						inputName: input.name,
						languageId: currentLanguageId,
						localizationInputsContainer: inputElement.parentNode,
						namespace: fragmentElementId,
					});

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
						`${fragmentElementId}-file-upload-read-only`
					),
					unlocalizedFieldsState,
					unlocalizedMessageContainer: document.getElementById(
						`${fragmentElementId}-unlocalized-info`
					),
				});

				selectButton.addEventListener('click', selectFileEvent);
			}
		}
	);
}
