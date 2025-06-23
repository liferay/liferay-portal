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
else if (layoutMode === 'edit') {
	allInputs.forEach((input) => {
		input.setAttribute('disabled', true);
	});

	button.setAttribute('disabled', true);
}
else {
	import('@liferay/fragment-impl/api').then(
		({
			getOrCreateTranslationInput,
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
							const input = getOrCreateTranslationInput(
								inputElement.id,
								inputElement.name,
								languageId,
								inputElement.parentNode,
								fragmentNamespace
							);

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
					onLocaleChange: ({languageId}) => {
						currentLanguageId = languageId;

						allInputs.forEach((input) => {
							const translationInput =
								getOrCreateTranslationInput(
									input.id,
									input.name,
									languageId,
									input.parentNode,
									fragmentNamespace
								);

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
									getOrCreateTranslationInput(
										input.id,
										input.name,
										defaultLanguageId,
										input.parentNode,
										fragmentNamespace
									);

								if (defaultLanguageInput) {
									input.checked = Boolean(
										defaultLanguageInput.value
									);
								}
							}
						});
					},
				});

				fieldSet.addEventListener('change', () => {
					allInputs.forEach((input) => {
						const translationInput = getOrCreateTranslationInput(
							input.id,
							input.name,
							currentLanguageId,
							input.parentNode,
							fragmentNamespace
						);

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
						`${fragmentNamespace}-multiselect-list-read-only`
					),
					unlocalizedFieldsState,
					unlocalizedMessageContainer: document.getElementById(
						`${fragmentNamespace}-unlocalized-info`
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
			input.id = `${fragmentEntryLinkNamespace}-checkbox-${option.value}`;

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
				`${fragmentEntryLinkNamespace}-checkbox-${option.value}`
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
