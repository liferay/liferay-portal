const inputElement = document.getElementById(`${fragmentNamespace}-checkbox`);

const preventClick = (event) => event.preventDefault();

if (inputElement) {
	if (input.readOnly) {
		inputElement.addEventListener('click', preventClick);
	}
	else if (layoutMode === 'edit') {
		inputElement.setAttribute('disabled', true);
	}
	else {
		const defaultLanguageId = themeDisplay.getDefaultLanguageId();

		import('@liferay/fragment-impl/api').then(
			({registerLocalizedInput, registerUnlocalizedInput}) => {
				if (input.localizable) {
					const {onChange} = registerLocalizedInput({
						changeTextDirection: false,
						defaultLanguageId,
						initialValues: input.valueI18n,
						inputElement,
						inputName: input.name,
						localizationInputsContainer: inputElement.parentNode,
						namespace: fragmentNamespace,
					});

					inputElement.addEventListener('change', (event) => {
						onChange(event.target.checked);
					});
				}
				else {
					const unlocalizedFieldsState =
						input.attributes.unlocalizedFieldsState;

					registerUnlocalizedInput({
						changeTextDirection: false,
						defaultLanguageId,
						inputElement,
						onLocaleChange: (languageId) => {
							if (
								defaultLanguageId !== languageId &&
								unlocalizedFieldsState === 'read-only'
							) {
								inputElement.addEventListener(
									'click',
									preventClick
								);
							}
							else {
								inputElement.removeEventListener(
									'click',
									preventClick
								);
							}
						},
						readOnlyInputLabel: document.getElementById(
							`${fragmentNamespace}-checkbox-read-only`
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
}
