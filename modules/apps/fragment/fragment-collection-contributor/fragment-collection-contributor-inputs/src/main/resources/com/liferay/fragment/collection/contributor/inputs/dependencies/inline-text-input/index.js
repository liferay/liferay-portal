const currentLength = document.getElementById(
	`${fragmentElementId}-current-length`
);
const errorMessage = document.getElementById(
	`${fragmentElementId}-inline-text-input-error-message`
);
const formGroup = document.getElementById(`${fragmentElementId}-form-group`);
const inputElement = document.getElementById(
	`${fragmentElementId}-inline-text-input`
);
const lengthInfo = document.getElementById(`${fragmentElementId}-length-info`);
const lengthWarning = document.getElementById(
	`${fragmentElementId}-length-warning`
);
const lengthWarningText = document.getElementById(
	`${fragmentElementId}-length-warning-text`
);

function main() {
	if (layoutMode === 'edit' && inputElement) {
		inputElement.setAttribute('disabled', true);
	}
	else {
		import('@liferay/fragment-impl/api').then(
			({
				handleInputLengthError,
				hideLengthError,
				registerLocalizedInput,
				registerUnlocalizedInput,
			}) => {
				currentLength.innerText = inputElement.value.length;

				if (
					!errorMessage &&
					inputElement.value.length > input.attributes.maxLength
				) {
					hideLengthError({
						configuration,
						formGroup,
						lengthInfo,
						lengthWarning,
						lengthWarningText,
					});
				}

				const onKeyup = (event) =>
					handleInputLengthError({
						configuration,
						currentLength,
						errorMessage,
						event,
						formGroup,
						input,
						lengthInfo,
						lengthWarning,
						lengthWarningText,
					});

				inputElement.addEventListener('keyup', onKeyup);

				const defaultLanguageId = themeDisplay.getDefaultLanguageId();

				if (input.localizable) {
					const {onChange} = registerLocalizedInput({
						defaultLanguageId,
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
				else {
					registerUnlocalizedInput({
						defaultLanguageId,
						inputElement,
						readOnlyInputLabel: document.getElementById(
							`${fragmentElementId}-inline-text-input-readonly`
						),
						unlocalizedFieldsState:
							input.attributes.unlocalizedFieldsState,
						unlocalizedMessageContainer: document.getElementById(
							`${fragmentElementId}-unlocalized-info`
						),
					});
				}
			}
		);
	}
}

main();
