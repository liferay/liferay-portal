const editorClass = '.ck-editor';
const editorName = `${fragmentEntryLinkNamespace}-${input.name}`;
const wrapper = document.getElementById(
	`${fragmentEntryLinkNamespace}-wrapper`
);

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
	else if (Liferay.FeatureFlags['LPD-37927']) {
		const inputContainer = document.getElementById(
			`${fragmentEntryLinkNamespace}-rich-text-input`
		);

		import('@liferay/fragment-impl/api').then(
			({registerLocalizedInput, registerUnlocalizedInput}) => {
				const defaultLanguageId = themeDisplay.getDefaultLanguageId();

				if (input.localizable) {
					const {onChange} = registerLocalizedInput({
						changeTextDirection: false,
						defaultLanguageId,
						initialValues: input.valueI18n,
						inputName: input.name,
						localizationInputsContainer: inputContainer,
						namespace: fragmentNamespace,
						onLocaleChange: ({languageId, value}) => {
							editorPromise.then((editor) => {
								changeLanguageDirection(editor, languageId);

								editor.setData(value);
							});
						},
					});

					editorPromise.then((editor) => {
						changeLanguageDirection(editor, defaultLanguageId);

						const updateData = () => {
							const value = editor.getData();

							onChange(value);
						};

						if (Liferay.FeatureFlags['LPD-11235']) {
							editor.model.document.on(
								'change:data',
								(event, source) => {
									if (source?.isTyping) {
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

									iframe =
										editorElement.querySelector('iframe');

									label = document.querySelector(
										`label[for="${editorName}"]`
									);
								}

								const isReadOnly =
									input.attributes.unlocalizedFieldsState ===
									'read-only';

								changeLanguageDirection(
									editor,
									languageId,
									() => editor.setData(editor.getData())
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

										if (
											!Liferay.FeatureFlags['LPD-11235']
										) {
											iframe.setAttribute(
												'tabindex',
												'0'
											);

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

										if (
											!Liferay.FeatureFlags['LPD-11235']
										) {
											iframe.setAttribute(
												'tabindex',
												'-1'
											);

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
							`${fragmentNamespace}-unlocalized-info`
						),
					});

					editorPromise.then((editor) => {
						changeLanguageDirection(editor, defaultLanguageId);
					});
				}
			}
		);
	}
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
