/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/**
 * The validator only removes properties with the value undefined to allow
 * the initialProps to be added.
 */
const validator = (props) => {
	const newProps = {};

	for (const key in props) {
		const value = props[key];

		if (value !== undefined) {
			newProps[key] = value;
		}
	}

	return newProps;
};

export function parseProps({
	activePage,
	allowInvalidAvailableLocalesForProperty,
	allowNestedFields,
	autocompleteUserURL,
	cancelLabel,
	config = {},
	containerId,
	contentType,
	context: {sidebarPanels, ...otherContext} = {},
	dataDefinitionId,
	dataEngineModule,
	dataLayoutBuilderElementId,
	dataLayoutBuilderId,
	dataLayoutId,
	dataProviderInstanceParameterSettingsURL,
	dataProviderInstancesURL,
	defaultSiteLanguageId,
	disableFieldRepetition,
	displayChartAsTable,
	displayFieldName,
	fieldSetDefinitionURL,
	fieldTypes,
	formInstanceId,
	formReportDataURL,
	functionsMetadata,
	functionsURL,
	groupId,
	portletNamespace,
	publishFormInstanceURL,
	published,
	redirectURL,
	rolesURL,
	rules,
	shareFormInstanceURL,
	sharedFormURL,
	showCancelButton,
	showPartialResultsToRespondents,
	showPublishAlert,
	showSubmitButton,
	spritemap,
	submitButtonId,
	submitLabel,
	submittable,
	validateCSRFTokenURL,
	view,
	...otherProps
}) {
	return {
		config: validator({
			allowInvalidAvailableLocalesForProperty,
			allowNestedFields,
			autocompleteUserURL,
			cancelLabel,
			...config,
			containerId,
			contentType,
			dataDefinitionId,
			dataEngineModule,
			dataLayoutBuilderElementId,
			dataLayoutBuilderId,
			dataLayoutId,
			dataProviderInstanceParameterSettingsURL,
			dataProviderInstancesURL,
			defaultSiteLanguageId,
			disableFieldRepetition,
			displayChartAsTable,
			displayFieldName,
			fieldSetDefinitionURL,
			fieldTypes,
			formInstanceId,
			formReportDataURL,
			functionsMetadata,
			functionsURL,
			groupId,
			portletNamespace,
			publishFormInstanceURL,
			published,
			redirectURL,
			rolesURL,
			shareFormInstanceURL,
			sharedFormURL,
			showCancelButton,
			showPartialResultsToRespondents,
			showPublishAlert,
			showSubmitButton,
			sidebarPanels,
			spritemap,
			submitButtonId,
			submitLabel,
			submittable,
			validateCSRFTokenURL,
			view,
		}),
		state: validator({
			...otherProps,
			...otherContext,
			activePage: activePage ?? 0,
			rules,
		}),
	};
}
