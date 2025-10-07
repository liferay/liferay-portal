const inputElement = document.getElementById(
	`${fragmentElementId}-friendly-url-input`
);

function main() {
	if (layoutMode === 'edit' && inputElement) {
		inputElement.setAttribute('disabled', true);
	}
	else {
		import('@liferay/fragment-impl/api').then(
			({registerLocalizedInput}) => {
				const {onChange} = registerLocalizedInput({
					defaultLanguageId: themeDisplay.getDefaultLanguageId(),
					initialValues: input.valueI18n,
					inputElement,
					inputName: input.name,
					localizationInputsContainer: inputElement.parentNode,
					namespace: fragmentElementId,
				});

				inputElement.addEventListener('change', (event) => {
					onChange(event.target.value);
				});
			}
		);
	}
}

main();
