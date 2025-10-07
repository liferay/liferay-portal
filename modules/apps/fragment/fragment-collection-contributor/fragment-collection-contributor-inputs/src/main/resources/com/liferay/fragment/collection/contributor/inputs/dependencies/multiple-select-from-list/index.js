const numberOfOptions = configuration.numberOfOptions;
const options = input.attributes.options || [];
const values = input.value.split(',');

const button = fragmentElement.querySelector('.multiselect-list-button');
const fieldSet = fragmentElement.querySelector('.multiselect-list-fieldset');

const allInputs = Array.from(
	fragmentElement.querySelectorAll('.custom-control-input')
);

const updateInputStatus = () => {
	if (!input.required) {
		return;
	}

	const someInputIsChecked = allInputs.some((input) => input.checked);

	if (someInputIsChecked) {
		allInputs.forEach((input) => input.removeAttribute('required'));
	}
	else {
		allInputs.forEach((input) => input.setAttribute('required', true));
	}
};

const preventClick = (event) => event.preventDefault();

if (input.attributes?.readOnly) {
	allInputs.forEach((input) => {
		input.addEventListener('click', preventClick);
	});
}

if (layoutMode === 'edit') {
	allInputs.forEach((input) => {
		input.setAttribute('disabled', true);
	});

	button.setAttribute('disabled', true);
}
else {
	import('@liferay/fragment-impl/api').then(
		({
			getTranslationInput,
			registerLocalizedInput,
			registerUnlocalizedInput,
		}) => {
			const defaultLanguageId = themeDisplay.getDefaultLanguageId();

			let currentLanguageId = defaultLanguageId;

			if (input.localizable) {

				// Set initial values

				allInputs.forEach((inputElement) => {
					Object.entries(input.valueI18n).forEach(
						([languageId, value]) => {
							const input = getTranslationInput({
								inputId: inputElement.id,
								inputName: inputElement.name,
								languageId,
								localizationInputsContainer:
									inputElement.parentNode,
								namespace: fragmentElementId,
							});

							input.value = value.includes(inputElement.value)
								? inputElement.value
								: '';
						}
					);
				});

				const {onChange} = registerLocalizedInput({
					changeTextDirection: false,
					customLocaleChangeHandler: true,
					defaultLanguageId,
					inputName: input.name,
					localizationInputsContainer: fieldSet,
					namespace: fragmentElementId,
					onLocaleChange: ({languageId}) => {
						currentLanguageId = languageId;

						allInputs.forEach((input) => {
							const translationInput = getTranslationInput({
								inputId: input.id,
								inputName: input.name,
								languageId,
								localizationInputsContainer: input.parentNode,
								namespace: fragmentElementId,
							});

							if (translationInput) {
								if (
									translationInput.getAttribute('value') !==
									null
								) {
									input.checked = Boolean(
										translationInput.value
									);
								}
							}
							else {
								const defaultLanguageInput =
									getTranslationInput({
										inputId: input.id,
										inputName: input.name,
										languageId: defaultLanguageId,
										localizationInputsContainer:
											input.parentNode,
										namespace: fragmentElementId,
									});

								if (defaultLanguageInput) {
									input.checked = Boolean(
										defaultLanguageInput.value
									);
								}
							}
						});
					},
					onMarkAsTranslated: () => {
						allInputs.forEach((input) => {
							const defaultLanguageInput = getTranslationInput({
								inputId: input.id,
								inputName: input.name,
								languageId: defaultLanguageId,
								localizationInputsContainer: input.parentNode,
								namespace: fragmentElementId,
							});

							const translationInput = getTranslationInput({
								inputId: input.id,
								inputName: input.name,
								languageId: currentLanguageId,
								localizationInputsContainer: input.parentNode,
								namespace: fragmentElementId,
							});

							input.checked = Boolean(defaultLanguageInput.value);

							translationInput.value = defaultLanguageInput.value;
						});
					},
					onResetTranslation: () => {
						allInputs.forEach((input) => {
							const defaultLanguageInput = getTranslationInput({
								inputId: input.id,
								inputName: input.name,
								languageId: defaultLanguageId,
								localizationInputsContainer: input.parentNode,
								namespace: fragmentElementId,
							});

							const translationInput = getTranslationInput({
								inputId: input.id,
								inputName: input.name,
								languageId: currentLanguageId,
								localizationInputsContainer: input.parentNode,
								namespace: fragmentElementId,
							});

							input.checked = Boolean(defaultLanguageInput.value);

							translationInput.value = '';
						});
					},
				});

				fieldSet.addEventListener('change', () => {
					allInputs.forEach((input) => {
						const translationInput = getTranslationInput({
							inputId: input.id,
							inputName: input.name,
							languageId: currentLanguageId,
							localizationInputsContainer: input.parentNode,
							namespace: fragmentElementId,
						});

						translationInput.value = input.checked
							? input.value
							: '';
					});

					onChange();
				});
			}
			else {
				const unlocalizedFieldsState =
					input.attributes.unlocalizedFieldsState;

				registerUnlocalizedInput({
					changeTextDirection: false,
					customLocaleChangeHandler: true,
					defaultLanguageId,
					onLocaleChange: (languageId) => {
						const editingDefaultLanguage =
							defaultLanguageId === languageId;
						const isReadOnlyFieldState =
							unlocalizedFieldsState === 'read-only';

						allInputs.forEach((inputElement) => {
							if (editingDefaultLanguage) {
								inputElement?.removeAttribute(
									isReadOnlyFieldState
										? 'readonly'
										: 'disabled'
								);
							}
							else {
								inputElement?.setAttribute(
									isReadOnlyFieldState
										? 'readonly'
										: 'disabled',
									''
								);
							}

							inputElement.addEventListener('click', (event) => {
								if (
									!editingDefaultLanguage &&
									isReadOnlyFieldState
								) {
									event.preventDefault();
								}
							});
						});
					},
					readOnlyInputLabel: document.getElementById(
						`${fragmentElementId}-multiselect-list-read-only`
					),
					unlocalizedFieldsState,
					unlocalizedMessageContainer: document.getElementById(
						`${fragmentElementId}-unlocalized-info`
					),
				});
			}
		}
	);
}

updateInputStatus();

if (numberOfOptions < options.length) {
	const missionOptions = options.slice(numberOfOptions);

	const template = fragmentElement.querySelector(
		'.multiselect-list-option-template'
	);

	button.addEventListener('click', () => {
		missionOptions.forEach((option) => {
			const node = template.content.cloneNode(true);

			const input = node.querySelector('input');
			input.value = option.value;

			// eslint-disable-next-line no-undef
			input.id = `${fragmentElementId}-checkbox-${option.value}`;

			if (values.includes(option.value)) {
				input.checked = true;
			}

			if (layoutMode === 'edit') {
				input.setAttribute('disabled', true);
			}

			const label = node.querySelector('label');

			label.setAttribute(
				'for',

				// eslint-disable-next-line no-undef
				`${fragmentElementId}-checkbox-${option.value}`
			);

			const text = node.querySelector('.custom-control-label-text');
			text.textContent = option.label;

			fieldSet.appendChild(node);
			allInputs.push(input);
		});

		fieldSet.removeChild(button);

		updateInputStatus();
	});
}
