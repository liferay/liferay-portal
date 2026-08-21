const container = document.getElementById(
	`${fragmentElementId}-date-input-picker`
);

// `input.errorMessage` reaches this script empty, so read the rendered error.

const error = document.getElementById(`${fragmentElementId}-date-input-error`);

if (container) {
	import('@liferay/fragment-impl/api').then(({renderDateInput}) => {
		renderDateInput(container, {
			availableLanguageIds: input.attributes.availableLanguageIds,
			defaultLanguageId: input.attributes.defaultLanguageId,
			disabled:
				layoutMode === 'edit' || input.attributes.disabled === true,
			focus: Boolean(error),
			localizable: input.localizable,
			name: input.name,
			namespace: fragmentElementId,
			readOnly: input.readOnly,
			readOnlyLabelId: `${fragmentElementId}-date-time-read-only`,
			required: input.required,
			time: true,
			unlocalizedFieldsState: input.attributes.unlocalizedFieldsState,
			value: input.value,
			valueI18n: input.valueI18n,
		});
	});
}
