const currentLength = document.getElementById(
	`${fragmentNamespace}-current-length`
);
const errorMessage = document.getElementById(
	`${fragmentNamespace}-video-previewer-error-message`
);
const formGroup = document.getElementById(`${fragmentNamespace}-form-group`);
const inputElement = document.getElementById(
	`${fragmentNamespace}-video-previewer-input`
);
const lengthInfo = document.getElementById(`${fragmentNamespace}-length-info`);
const lengthWarning = document.getElementById(
	`${fragmentNamespace}-length-warning`
);
const lengthWarningText = document.getElementById(
	`${fragmentNamespace}-length-warning-text`
);
const videoPreview = document.getElementById(
	`${fragmentNamespace}-video-preview`
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
				updateDLVideo,
			}) => {
				let previousUrl = null;

				const onUpdate = (html, title) => {
					videoPreview.innerHTML = html;

					if (html) {
						const iframe = videoPreview.querySelector('iframe');

						iframe.title =
							configuration.videoTitle ||
							title ||
							configuration.previewLabel;
					}
				};

				const updateVideoPreview = (url) => {
					if (previousUrl !== url) {
						updateDLVideo({onUpdate, url});
					}

					previousUrl = url;
				};

				if (input.value) {
					updateVideoPreview(input.value);
				}

				inputElement.addEventListener('blur', (event) => {
					updateVideoPreview(event.target.value);
				});

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
						namespace: fragmentNamespace,
						onLocaleChange: ({value}) => {
							updateVideoPreview(value);
						},
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
							`${fragmentNamespace}-video-previewer-readonly`
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
