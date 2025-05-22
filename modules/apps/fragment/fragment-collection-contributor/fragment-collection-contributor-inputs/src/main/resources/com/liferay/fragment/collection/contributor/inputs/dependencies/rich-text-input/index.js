let wrapper = null;

const editorName = `${fragmentEntryLinkNamespace}-${input.name}`;

if (layoutMode !== 'edit') {
	const editorPromise = new Promise((resolve) => {
		if (Liferay.FeatureFlags['LPD-11235']) {
			wrapper = document.getElementById(
				`${fragmentEntryLinkNamespace}-wrapper`
			);

			Liferay.on('ckeditor:ready', ({editor}) => {
				if (editorName === editor.config.get('name')) {
					resolve(editor);
				}
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
								editor.config.contentsLangDirection =
									Liferay.Language.direction[languageId];

								editor.setData(value);
							});
						},
					});

					editorPromise.then((editor) => {
						editor.config.contentsLangDirection =
							Liferay.Language.direction[defaultLanguageId];

						editor.on('change', () => {
							const value = editor.getData();

							onChange(value);
						});
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
										wrapper.querySelector('.ck-editor');

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

								editor.config.contentsLangDirection =
									Liferay.Language.direction[languageId];

								editor.setData(editor.getData());

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
						editor.config.contentsLangDirection =
							Liferay.Language.direction[defaultLanguageId];
					});
				}
			}
		);
	}
}
