const currentLength = document.getElementById(
	`${fragmentElementId}-current-length`
);
const errorMessage = document.getElementById(
	`${fragmentElementId}-textarea-error-message`
);
const formGroup = document.getElementById(`${fragmentElementId}-form-group`);
const lengthInfo = document.getElementById(`${fragmentElementId}-length-info`);
const lengthWarning = document.getElementById(
	`${fragmentElementId}-length-warning`
);
const lengthWarningText = document.getElementById(
	`${fragmentElementId}-length-warning-text`
);
const textarea = document.getElementById(`${fragmentElementId}-textarea`);

function main() {
	if (layoutMode === 'edit' && textarea) {
		textarea.setAttribute('disabled', true);
	}
	else {
		import('@liferay/fragment-impl/api').then(
			({
				handleInputLengthError,
				hideLengthError,
				registerLocalizedInput,
				registerUnlocalizedInput,
			}) => {
				currentLength.innerText = textarea.value.length;

				if (
					!errorMessage &&
					textarea.value.length > input.attributes.maxLength
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

				textarea.addEventListener('keyup', onKeyup);

				const defaultLanguageId = themeDisplay.getDefaultLanguageId();

				if (input.localizable) {
					const {onChange} = registerLocalizedInput({
						defaultLanguageId,
						initialValues: input.valueI18n,
						inputElement: textarea,
						inputName: input.name,
						localizationInputsContainer: textarea.parentNode,
						namespace: fragmentElementId,
					});

					textarea.addEventListener('change', (event) => {
						onChange(event.target.value);
					});
				}
				else {
					registerUnlocalizedInput({
						defaultLanguageId,
						inputElement: textarea,
						readOnlyInputLabel: document.getElementById(
							`${fragmentElementId}-textarea-readonly`
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
