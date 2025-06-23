const inputElement = document.getElementById(`${fragmentNamespace}-date-input`);

if (inputElement) {
	if (input.attributes?.readOnly) {
		inputElement.addEventListener('keydown', (event) => {
			if (event.code === 'Space') {
				event.preventDefault();
			}
		});
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
						defaultLanguageId,
						initialValues: input.valueI18n,
						inputElement,
						inputName: input.name,
						localizationInputsContainer: inputElement.parentNode,
						namespace: fragmentNamespace,
					});

					inputElement.addEventListener('change', (event) => {
						onChange(event.target.value);
					});
				}
				else {
					registerUnlocalizedInput({
						defaultLanguageId,
						inputElement,
						readOnlyInputLabel: document.getElementById(
							`${fragmentNamespace}-date-time-read-only`
						),
						unlocalizedFieldsState:
							input.attributes.unlocalizedFieldsState,
						unlocalizedMessageContainer: document.getElementById(
							`${fragmentNamespace}-unlocalized-info`
						),
					});
				}
			}
		);
	}
}
