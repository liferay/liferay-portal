const currentLength = document.getElementById(
	`${fragmentNamespace}-current-length`
);
const errorMessage = document.getElementById(
	`${fragmentNamespace}-textarea-error-message`
);
const formGroup = document.getElementById(`${fragmentNamespace}-form-group`);
const lengthInfo = document.getElementById(`${fragmentNamespace}-length-info`);
const lengthWarning = document.getElementById(
	`${fragmentNamespace}-length-warning`
);
const lengthWarningText = document.getElementById(
	`${fragmentNamespace}-length-warning-text`
);
const textarea = document.getElementById(`${fragmentNamespace}-textarea`);

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
						namespace: fragmentNamespace,
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
							`${fragmentNamespace}-textarea-readonly`
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

main();
