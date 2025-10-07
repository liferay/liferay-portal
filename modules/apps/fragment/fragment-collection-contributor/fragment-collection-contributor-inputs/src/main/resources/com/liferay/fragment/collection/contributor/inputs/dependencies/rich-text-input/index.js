const CKEditorRequiredInput = document.getElementById(
	`${fragmentElementId}-ckeditor-required`
);
const editorClass = '.ck-editor';
const editorName = `${fragmentElementId}-${input.name}`;
const errorMessage = document.getElementById(
	`${fragmentElementId}-error-message`
);
const errorMessageTextId = `${fragmentElementId}-error-message-text`;
const errorMessageText = document.getElementById(errorMessageTextId);
const wrapper = document.getElementById(`${fragmentElementId}-wrapper`);

if (layoutMode === 'edit') {
	if (Liferay.FeatureFlags['LPD-11235']) {
		initEditorWhenReady(() => {
			wrapper
				.querySelector(editorClass)
				.classList.add('rich-text-input--disabled');
		});
	}
}
else {
	const editorPromise = new Promise((resolve) => {
		if (Liferay.FeatureFlags['LPD-11235']) {
			initEditorWhenReady((editor) => {
				resolve(editor);
			});
		}
		else {
			const editor = document.getElementById(editorName);

			editor.name = input.name;

			CKEDITOR.on('instanceReady', ({editor}) => {
				if (editor.name === editorName) {
					resolve(editor);
				}
			});
		}
	});

	// Whenever the field is required, we validate if the CKEditorRequiredInput
	// is valid on submit. If it is not valid, the error message will be shown
	// and the field will be focused.

	if (input.required) {
		CKEditorRequiredInput.addEventListener('invalid', (event) => {
			event.preventDefault();

			errorMessage.classList.remove('d-none');
			errorMessageText.textContent =
				errorMessageText.dataset.requiredError;

			editorPromise.then((editor) => {
				if (Liferay.FeatureFlags['LPD-11235']) {
					editor.editing.view.focus();

					editor.ui.view.editable.element.setAttribute(
						'aria-describedby',
						errorMessageTextId
					);
				}
				else {
					document
						.getElementById(`cke_${editorName}`)
						.querySelector('iframe')
						.contentDocument.body.focus();
				}
			});
		});
	}

	if (input.readOnly || input.attributes?.disabled) {
		editorPromise.then((editor) => {
			if (Liferay.FeatureFlags['LPD-11235']) {
				editor.enableReadOnlyMode('read-only');
			}
			else {
				editor.setReadOnly(true);
			}
		});
	}

	const inputContainer = document.getElementById(
		`${fragmentElementId}-rich-text-input`
	);

	const defaultLanguageId = themeDisplay.getDefaultLanguageId();

	let currentLanguageId = defaultLanguageId;

	import('@liferay/fragment-impl/api').then(
		({
			getTranslationInput,
			registerLocalizedInput,
			registerUnlocalizedInput,
		}) => {
			const defaultLanguageId = themeDisplay.getDefaultLanguageId();

			if (input.localizable) {
				const {onChange} = registerLocalizedInput({
					changeTextDirection: false,
					defaultLanguageId,
					initialValues: input.valueI18n,
					inputElement: wrapper,
					inputName: input.name,
					localizationInputsContainer: inputContainer,
					namespace: fragmentElementId,
					onAutoTranslate: ({languageId, value}) => {
						editorPromise.then((editor) => {
							changeLanguageDirection(editor, languageId);

							editor.setData(value);
						});

						const translationInput = getTranslationInput({
							inputId: wrapper.id,
							inputName: input.name,
							languageId,
							localizationInputsContainer: wrapper.parentNode,
							namespace: fragmentElementId,
						});

						translationInput.value = value;
					},
					onLocaleChange: ({languageId, value}) => {
						currentLanguageId = languageId;

						editorPromise.then((editor) => {
							changeLanguageDirection(editor, languageId);

							editor.setData(value);
						});
					},
					onMarkAsTranslated: () => {
						const defaultLanguageInput = getTranslationInput({
							inputId: wrapper.id,
							inputName: input.name,
							languageId: defaultLanguageId,
							localizationInputsContainer: wrapper.parentNode,
							namespace: fragmentElementId,
						});

						editorPromise.then((editor) =>
							editor.setData(defaultLanguageInput.value)
						);

						const translationInput = getTranslationInput({
							inputId: wrapper.id,
							inputName: input.name,
							languageId: currentLanguageId,
							localizationInputsContainer: wrapper.parentNode,
							namespace: fragmentElementId,
						});

						translationInput.value = defaultLanguageInput.value;
					},
					onResetTranslation: () => {
						const defaultLanguageInput = getTranslationInput({
							inputId: wrapper.id,
							inputName: input.name,
							languageId: defaultLanguageId,
							localizationInputsContainer: wrapper.parentNode,
							namespace: fragmentElementId,
						});

						editorPromise.then((editor) =>
							editor.setData(defaultLanguageInput.value)
						);

						const translationInput = getTranslationInput({
							inputId: wrapper.id,
							inputName: input.name,
							languageId: currentLanguageId,
							localizationInputsContainer: wrapper.parentNode,
							namespace: fragmentElementId,
						});

						translationInput.removeAttribute('value');
					},
				});

				editorPromise.then((editor) => {
					changeLanguageDirection(editor, defaultLanguageId);

					const updateData = () => {
						const value = editor.getData();

						onChange(value);

						if (
							input.required &&
							currentLanguageId === defaultLanguageId
						) {
							updateCKEditorRequired(value);
						}
					};

					if (Liferay.FeatureFlags['LPD-11235']) {
						editor.model.document.on(
							'change:data',
							(event, source) => {
								if (source?.isTyping || source?.isUndoable) {
									updateData();
								}
							}
						);
					}
					else {
						editor.on('change', () => {
							updateData();
						});
					}
				});
			}
			else {
				registerUnlocalizedInput({
					changeTextDirection: false,
					customLocaleChangeHandler: true,
					defaultLanguageId,
					onLocaleChange: (languageId) => {
						editorPromise.then((editor) => {
							let editorElement = null;
							let iframe = null;
							let label = null;

							if (Liferay.FeatureFlags['LPD-11235']) {
								editorElement =
									wrapper.querySelector(editorClass);

								label = wrapper.querySelector('label');
							}
							else {
								editorElement = document.getElementById(
									`cke_${editorName}`
								);

								iframe = editorElement.querySelector('iframe');

								label = document.querySelector(
									`label[for="${editorName}"]`
								);
							}

							const isReadOnly =
								input.attributes.unlocalizedFieldsState ===
								'read-only';

							changeLanguageDirection(editor, languageId, () =>
								editor.setData(editor.getData())
							);

							if (languageId === defaultLanguageId) {
								if (Liferay.FeatureFlags['LPD-11235']) {
									editor.disableReadOnlyMode('read-only');
								}
								else {
									editor.setReadOnly(false);
								}

								if (isReadOnly) {
									label.innerHTML = input.label;
								}
								else {
									editorElement.classList.remove(
										'rich-text-input--disabled'
									);

									if (!Liferay.FeatureFlags['LPD-11235']) {
										iframe.setAttribute('tabindex', '0');

										iframe.contentDocument.body.removeAttribute(
											'aria-disabled'
										);
									}
								}
							}
							else {
								if (Liferay.FeatureFlags['LPD-11235']) {
									editor.enableReadOnlyMode('read-only');
								}
								else {
									editor.setReadOnly(true);
								}

								if (isReadOnly) {
									label.innerHTML =
										inputContainer.dataset.readonlyLabel;
								}
								else {
									editorElement.classList.add(
										'rich-text-input--disabled'
									);

									if (!Liferay.FeatureFlags['LPD-11235']) {
										iframe.setAttribute('tabindex', '-1');

										iframe.contentDocument.body.setAttribute(
											'aria-disabled',
											'true'
										);
									}
								}
							}
						});
					},
					unlocalizedFieldsState:
						input.attributes.unlocalizedFieldsState,
					unlocalizedMessageContainer: document.getElementById(
						`${fragmentElementId}-unlocalized-info`
					),
				});

				editorPromise.then((editor) => {
					changeLanguageDirection(editor, defaultLanguageId);

					if (Liferay.FeatureFlags['LPD-11235']) {
						const hiddenInput = document.createElement('input');

						hiddenInput.type = 'hidden';
						hiddenInput.name = input.name;

						if (input.value) {
							hiddenInput.value = input.value;
						}

						inputContainer.appendChild(hiddenInput);

						editor.model.document.on(
							'change:data',
							(event, source) => {
								if (source?.isTyping || source?.isUndoable) {
									const value = editor.getData();

									hiddenInput.value = value;

									if (input.required) {
										updateCKEditorRequired(value);
									}
								}
							}
						);
					}
					else if (input.required) {
						editor.on('change', () => {
							updateCKEditorRequired(editor.getData());
						});
					}
				});
			}
		}
	);
}

function changeLanguageDirection(editor, languageId, onChange) {
	if (Liferay.FeatureFlags['LPD-11235']) {
		const root = editor.editing.view.document.getRoot();

		editor.editing.view.change((element) => {
			element.setAttribute(
				'dir',
				Liferay.Language.direction[languageId],
				root
			);

			element.setAttribute('lang', languageId.substr(0, 2), root);
		});
	}
	else {
		editor.config.contentsLangDirection =
			Liferay.Language.direction[languageId];

		onChange?.();
	}
}

function initEditorWhenReady(onReady) {
	Liferay.on('ckeditor:ready', ({editor}) => {
		if (editorName === editor.config.get('name')) {
			onReady(editor);
		}
	});
}

function updateCKEditorRequired(value) {
	CKEditorRequiredInput.value = value;

	if (value) {
		errorMessage.classList.add('d-none');
		errorMessageText.textContent = '';
	}
}
