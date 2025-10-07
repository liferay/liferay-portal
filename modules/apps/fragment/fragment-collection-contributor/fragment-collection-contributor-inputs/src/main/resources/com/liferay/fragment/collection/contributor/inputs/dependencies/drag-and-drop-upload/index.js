/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const changeButton = document.getElementById(
	`${fragmentElementId}-drag-and-drop-upload-change-button`
);
const dropzone = document.getElementById(
	`${fragmentElementId}-drag-and-drop-upload-dropzone`
);
const dropzoneText = document.getElementById(
	`${fragmentElementId}-drag-and-drop-upload-dropzone-text`
);
const defaultDropzone = dropzone.querySelector('.dropzone-default-content');
const fileInput = document.getElementById(
	`${fragmentElementId}-drag-and-drop-upload`
);
const fileNameLabel = document.getElementById(
	`${fragmentElementId}-drag-and-drop-upload-file-name-label`
);
const helpText = document.getElementById(
	`${fragmentElementId}-drag-and-drop-upload-help-text`
);
const hiddenFileInput = document.getElementById(
	`${fragmentElementId}-drag-and-drop-upload-hidden`
);
const noPreviewDropzone = dropzone.querySelector('.dropzone-no-preview');
const previewContainer = document.getElementById(
	`${fragmentElementId}-drag-and-drop-upload-preview`
);
const previewContent = document.getElementById(
	`${fragmentElementId}-drag-and-drop-upload-preview-content`
);
const removeButton = document.getElementById(
	`${fragmentElementId}-drag-and-drop-upload-remove-button`
);
const selectButton = document.getElementById(
	`${fragmentElementId}-drag-and-drop-upload-button`
);
const previewButtons = document.getElementById(
	`${fragmentElementId}-drag-and-drop-upload-preview-buttons`
);

let hasSelectedFile = false;

const DROP_ZONE_CONTAINER_TYPE = {
	DEFAULT: 1,
	NO_PREVIEW: 2,
	PREVIEW: 3,
};

function showDropzone(dropzoneContainerType) {
	let container = defaultDropzone;

	if (dropzoneContainerType === DROP_ZONE_CONTAINER_TYPE.NO_PREVIEW) {
		container = noPreviewDropzone;
	}
	else if (dropzoneContainerType === DROP_ZONE_CONTAINER_TYPE.PREVIEW) {
		container = previewContainer;
	}

	container.classList.remove('d-none');

	if (dropzoneContainerType === DROP_ZONE_CONTAINER_TYPE.NO_PREVIEW) {
		previewContainer.classList.add('d-none');
		defaultDropzone.classList.add('d-none');
	}
	else if (dropzoneContainerType === DROP_ZONE_CONTAINER_TYPE.PREVIEW) {
		defaultDropzone.classList.add('d-none');
		noPreviewDropzone.classList.add('d-none');
	}
	else {
		previewContainer.classList.add('d-none');
		noPreviewDropzone.classList.add('d-none');
		previewButtons.classList.add('d-none');
		updateFileNameLabel('');
		selectButton.focus();
	}
}

function showPreview(fileOrUrl, fileName) {
	hasSelectedFile = true;

	if (!fileOrUrl) {
		showDropzone(DROP_ZONE_CONTAINER_TYPE.DEFAULT);

		return;
	}

	let imageURL = null;

	if (fileOrUrl instanceof File && fileOrUrl.type?.startsWith('image/')) {
		imageURL = URL.createObjectURL(fileOrUrl);
	}
	else {
		imageURL = fileOrUrl;
	}

	if (imageURL) {
		showDropzone(DROP_ZONE_CONTAINER_TYPE.PREVIEW);

		previewContent.innerHTML = '';

		const image = document.createElement('img');
		image.src = imageURL;
		image.alt = '';
		image.style.width = '100%';

		previewContent.appendChild(image);
	}
	else {
		showDropzone(DROP_ZONE_CONTAINER_TYPE.NO_PREVIEW);
	}

	previewButtons.classList.remove('d-none');

	changeButton.setAttribute(
		'aria-label',
		Liferay.Util.sub(previewButtons.dataset.changeLabel, fileName)
	);

	removeButton.setAttribute(
		'aria-label',
		Liferay.Util.sub(previewButtons.dataset.removeLabel, fileName)
	);

	updateFileNameLabel(fileName);
}

function updateFileNameLabel(fileName) {
	if (fileNameLabel) {
		if (fileName) {
			fileNameLabel.textContent = fileName;
		}
		else {
			fileNameLabel.textContent =
				Liferay.Language.get('no-file-selected');
		}
	}
}

function onInputChange() {
	const file = fileInput.files[0];

	showPreview(file, file?.name);
	fileInput.setAttribute('name', input.name);

	hiddenFileInput.setAttribute('name', '');
	hiddenFileInput.value = '';

	changeButton.focus();
}

function getFragmentTranslationInput(namespace, languageId, inputId) {
	return document.getElementById(`${namespace}${inputId}_${languageId}`);
}

function onSelectFile(event, onChange, setTranslationInputValue) {
	event.preventDefault();

	Liferay.Util.openSelectionModal({
		onSelect(selectedItem) {
			const {fileEntryId, title, url} = JSON.parse(selectedItem.value);

			if (onChange) {
				setTranslationInputValue({
					previewURL: url,
					title: title,
					value: fileEntryId,
				});

				onChange();
			}

			fileInput.value = fileEntryId;

			showPreview(url, title);
		},
		selectEventName: `${fragmentNamespace}selectFileEntry`,
		url: input.attributes.selectFromDocumentLibraryURL,
	});
}

function onSelectFromUserComputer() {
	fileInput.click();
}

let selectFileEvent = onSelectFromUserComputer;

if (layoutMode === 'edit') {
	selectButton.classList.add('disabled');
}
else {
	if (input.attributes.selectFromDocumentLibrary) {
		selectFileEvent = onSelectFile;
	}

	const defaultLanguageId = themeDisplay.getDefaultLanguageId();
	const inputElement = fileInput;

	let currentLanguageId = defaultLanguageId;

	import('@liferay/fragment-impl/api').then(
		({
			getTranslationInput,
			registerLocalizedInput,
			registerUnlocalizedInput,
		}) => {
			const isFromDocumentLibrary =
				input.attributes.selectFromDocumentLibrary;

			defaultDropzone.addEventListener('dragover', (event) => {
				if (!isFromDocumentLibrary) {
					event.preventDefault();
					defaultDropzone.classList.add('dropzone-hover');
				}
			});

			defaultDropzone.addEventListener('dragleave', () => {
				if (!isFromDocumentLibrary) {
					defaultDropzone.classList.remove('dropzone-hover');
				}
			});

			if (input.localizable) {

				// Set initial values

				const initialValues = Object.keys(input.valueI18n).map(
					(key) => [
						key,
						{
							fileEntryId: input.valueI18n[key],
							fileName: input.attributes.fileNameI18n[key] || '',
							previewURL:
								input.attributes.previewURLI18n[key] || '',
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
					translationInput.dataset.fileName = value.fileName;
					translationInput.dataset.previewURL = value.previewURL;
				});

				if (input.attributes?.previewURL) {
					showPreview(
						input.attributes.previewURL,
						input.attributes.fileName
					);
				}

				const {onChange} = registerLocalizedInput({
					changeTextDirection: false,
					customLocaleChangeHandler: true,
					defaultLanguageId,
					inputElement: fileInput,
					inputName: input.name,
					localizationInputsContainer: inputElement.parentNode,
					namespace: fragmentElementId,
					onLocaleChange: ({languageId}) => {
						currentLanguageId = languageId;

						const translationInput = getFragmentTranslationInput(
							fragmentElementId,
							languageId,
							inputElement.id
						);

						let previewURL = translationInput?.dataset?.previewURL;

						const fileName =
							translationInput?.dataset?.fileName || '';

						if (previewURL) {
							showPreview(previewURL, fileName);
						}
						else {
							const defaultInput = getTranslationInput({
								inputId: inputElement.id,
								inputName: input.name,
								languageId: defaultLanguageId,
								localizationInputsContainer:
									inputElement.parentNode,
								namespace: fragmentElementId,
							});

							previewURL = defaultInput?.dataset?.previewURL;

							if (previewURL) {
								showPreview(
									defaultInput?.dataset?.previewURL,
									defaultInput?.dataset?.fileName
								);
							}
							else {
								showDropzone(DROP_ZONE_CONTAINER_TYPE.DEFAULT);
							}
						}
					},
					onMarkAsTranslated: () => {
						const defaultTranslationInput =
							getFragmentTranslationInput(
								fragmentElementId,
								defaultLanguageId,
								inputElement.id
							);

						if (defaultTranslationInput.type === 'file') {
							setTranslationInputValue({
								previewURL:
									defaultTranslationInput.dataset.previewURL,
								title: defaultTranslationInput.dataset.fileName,
								type: 'file',
								value: defaultTranslationInput.files[0],
							});
						}
						else {
							setTranslationInputValue({
								previewURL:
									defaultTranslationInput.dataset.previewURL,
								title: defaultTranslationInput.dataset.fileName,
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

						const previewURL =
							defaultTranslationInput?.dataset?.previewURL;

						if (previewURL) {
							showPreview(
								defaultTranslationInput?.dataset?.previewURL,
								defaultTranslationInput?.dataset?.fileName
							);
						}

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
					const {previewURL, title, value} = props;

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
						translationInput.dataset.previewURL = previewURL;
						translationInput.dataset.fileName = title;
					}
					else {
						const dataTransfer = new DataTransfer();

						dataTransfer.items.add(value);

						translationInput.files = dataTransfer.files;
						translationInput.dataset.previewURL =
							URL.createObjectURL(dataTransfer.files[0]);
						translationInput.dataset.fileName = title;
					}

					showPreview(translationInput.dataset.previewURL, title);
				};

				if (isFromDocumentLibrary) {
					dropzoneText.classList.add('d-none');

					changeButton.addEventListener('click', (event) => {
						onSelectFile(event, onChange, setTranslationInputValue);
					});

					selectButton.addEventListener('click', (event) => {
						onSelectFile(event, onChange, setTranslationInputValue);
					});
				}
				else {
					dropzoneText.classList.remove('d-none');

					defaultDropzone.addEventListener('drop', (event) => {
						event.preventDefault();
						defaultDropzone.classList.remove('dropzone-hover');

						onInputChange();

						setTranslationInputValue({
							title: event.dataTransfer.files[0].name,
							value: event.dataTransfer.files[0],
						});

						onChange();
					});

					inputElement.addEventListener('change', (event) => {
						setTranslationInputValue({
							title: event.target.files[0].name,
							value: event.target.files[0],
						});

						onChange();
					});

					selectButton.addEventListener(
						'click',
						onSelectFromUserComputer
					);

					changeButton.addEventListener(
						'click',
						onSelectFromUserComputer
					);
				}

				removeButton.addEventListener('click', () => {
					hasSelectedFile = false;

					fileInput.value = '';
					hiddenFileInput.value = '';

					const translationInput = getTranslationInput({
						inputId: inputElement.id,
						inputName: input.name,
						languageId: currentLanguageId,
						localizationInputsContainer: inputElement.parentNode,
						namespace: fragmentElementId,
					});

					translationInput.value = '';
					translationInput.dataset.previewURL = '';

					if (currentLanguageId === defaultLanguageId) {
						showDropzone(DROP_ZONE_CONTAINER_TYPE.DEFAULT);
					}
					else {
						const defaultInput = getTranslationInput({
							inputId: inputElement.id,
							inputName: input.name,
							languageId: defaultLanguageId,
							localizationInputsContainer:
								inputElement.parentNode,
							namespace: fragmentElementId,
						});

						if (defaultInput.dataset?.previewURL) {
							showPreview(
								defaultInput.dataset?.previewURL,
								defaultInput.dataset.fileName
							);
						}
					}
				});
			}
			else {
				fileInput.addEventListener('change', onInputChange);

				if (isFromDocumentLibrary) {
					dropzoneText.classList.add('d-none');
				}
				else {
					dropzoneText.classList.remove('d-none');
				}

				const unlocalizedFieldsState =
					input.attributes.unlocalizedFieldsState;

				if (input.attributes?.previewURL) {
					showPreview(
						input.attributes.previewURL,
						input.attributes.fileName
					);
				}

				if (input.attributes?.fileName) {
					changeButton.setAttribute(
						'aria-label',
						Liferay.Util.sub(
							previewButtons.dataset.changeLabel,
							input.attributes?.fileName
						)
					);

					removeButton.setAttribute(
						'aria-label',
						Liferay.Util.sub(
							previewButtons.dataset.removeLabel,
							input.attributes?.fileName
						)
					);
				}

				registerUnlocalizedInput({
					changeTextDirection: false,
					customLocaleChangeHandler: true,
					defaultLanguageId,
					inputElement,
					onLocaleChange: (languageId) => {
						currentLanguageId = languageId;

						if (defaultLanguageId !== languageId) {
							selectButton.setAttribute('disabled', true);

							if (hasSelectedFile) {
								changeButton.setAttribute('disabled', true);
								removeButton.setAttribute('disabled', true);
								fileNameLabel.classList.remove('d-none');
							}
							else {
								dropzone.style.opacity = '0.4';
							}

							if (unlocalizedFieldsState === 'disabled') {
								dropzone.style.opacity = '0.4';
								helpText.style.opacity = '0.4';

								fileNameLabel.classList.add('d-none');
							}

							if (input.attributes?.fileName) {
								updateFileNameLabel(input.attributes.fileName);
							}
						}
						else {
							selectButton.removeAttribute('disabled');

							dropzone.style.opacity = '1';
							helpText.style.opacity = '1';

							if (hasSelectedFile) {
								previewButtons.classList.remove('d-none');

								changeButton.removeAttribute('disabled');
								removeButton.removeAttribute('disabled');
							}
						}
					},
					readOnlyInputLabel: document.getElementById(
						`${fragmentElementId}-drag-and-drop-upload-read-only`
					),
					unlocalizedFieldsState,
					unlocalizedMessageContainer: document.getElementById(
						`${fragmentElementId}-unlocalized-info`
					),
				});

				selectButton.addEventListener('click', selectFileEvent);

				changeButton.addEventListener('click', selectFileEvent);

				removeButton.addEventListener('click', () => {
					showDropzone(DROP_ZONE_CONTAINER_TYPE.DEFAULT);

					hasSelectedFile = false;

					fileInput.value = '';
					hiddenFileInput.value = '';
				});

				defaultDropzone.addEventListener('drop', (event) => {
					event.preventDefault();
					defaultDropzone.classList.remove('dropzone-hover');

					const files = event.dataTransfer.files;

					if (files.length) {
						const dataTransfer = new DataTransfer();
						[...files].forEach((file) =>
							dataTransfer.items.add(file)
						);
						fileInput.files = dataTransfer.files;

						onInputChange();
					}
				});
			}
		}
	);
}
