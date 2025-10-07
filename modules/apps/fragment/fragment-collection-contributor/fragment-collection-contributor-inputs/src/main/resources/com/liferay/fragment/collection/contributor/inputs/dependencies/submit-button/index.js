const button = fragmentElement.querySelector('button');

const isMultistepButton = fragmentElement.querySelector(
	`#fragment-${fragmentElementId}-form-button`
);

button.addEventListener('click', ({target}) => {
	const isEditable =
		target.hasAttribute('data-lfr-editable-id') ||
		target.hasAttribute('contenteditable');

	if (isEditable && layoutMode === 'edit') {
		return;
	}

	if (isMultistepButton) {
		Liferay.fire('formFragment:changeStep', {
			emitter: fragmentElement,
			step: configuration.type,
		});
	}
	else {
		Liferay.fire('formFragment:submit');
	}
});
