/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.field.type.internal.search.location;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.dynamic.data.mapping.annotations.DDMForm;
import com.liferay.dynamic.data.mapping.annotations.DDMFormField;
import com.liferay.dynamic.data.mapping.annotations.DDMFormFieldProperty;
import com.liferay.dynamic.data.mapping.annotations.DDMFormLayout;
import com.liferay.dynamic.data.mapping.annotations.DDMFormLayoutColumn;
import com.liferay.dynamic.data.mapping.annotations.DDMFormLayoutPage;
import com.liferay.dynamic.data.mapping.annotations.DDMFormLayoutRow;
import com.liferay.dynamic.data.mapping.annotations.DDMFormRule;
import com.liferay.dynamic.data.mapping.form.field.type.DefaultDDMFormFieldTypeSettings;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;

/**
 * @author Marcela Cunha
 */
@DDMForm(
	rules = {
		@DDMFormRule(
			actions = {
				"setVisible('fieldReference', hasGooglePlacesAPIKey())",
				"setVisible('label', hasGooglePlacesAPIKey())",
				"setVisible('layout', hasGooglePlacesAPIKey() AND (contains(getValue('visibleFields'), \"city\") OR contains(getValue('visibleFields'), \"country\") OR contains(getValue('visibleFields'), \"postal-code\") OR contains(getValue('visibleFields'), \"state\")))",
				"setVisible('placeholder', hasGooglePlacesAPIKey())",
				"setVisible('redirectButton', NOT(hasGooglePlacesAPIKey()))",
				"setVisible('required', hasGooglePlacesAPIKey())",
				"setVisible('requiredErrorMessage', hasGooglePlacesAPIKey() AND getValue('required'))",
				"setVisible('tip', hasGooglePlacesAPIKey())",
				"setVisible('visibleFields', hasGooglePlacesAPIKey())"
			},
			condition = "TRUE"
		),
		@DDMFormRule(
			actions = "jumpPage(0, 2)",
			condition = "NOT(hasGooglePlacesAPIKey())"
		)
	}
)
@DDMFormLayout(
	paginationMode = com.liferay.dynamic.data.mapping.model.DDMFormLayout.TABBED_MODE,
	value = {
		@DDMFormLayoutPage(
			title = "%basic",
			value = {
				@DDMFormLayoutRow(
					{
						@DDMFormLayoutColumn(
							size = 12,
							value = {
								"label", "placeholder", "tip", "required",
								"requiredErrorMessage", "visibleFields",
								"layout", "redirectButton"
							}
						)
					}
				)
			}
		),
		@DDMFormLayoutPage(
			title = "%advanced",
			value = {
				@DDMFormLayoutRow(
					{
						@DDMFormLayoutColumn(
							size = 12,
							value = {
								"dataType", "fieldReference", "name",
								"showLabel", "repeatable", "readOnly",
								"rulesActionDisabled", "rulesConditionDisabled"
							}
						)
					}
				)
			}
		)
	}
)
public interface SearchLocationDDMFormFieldTypeSettings
	extends DefaultDDMFormFieldTypeSettings {

	@DDMFormField(
		label = "%layout", optionLabels = {"%one-column", "%two-columns"},
		optionValues = {"one-column", "two-columns"},
		predefinedValue = "[\"one-column\"]",
		properties = "showEmptyOption=false", type = "select"
	)
	public LocalizedValue layout();

	@DDMFormField(
		dataType = "string", label = "%placeholder-text",
		properties = {
			"tooltip=%enter-text-that-assists-the-user-but-is-not-submitted-as-a-field-value",
			"visualProperty=true"
		},
		type = "text"
	)
	public LocalizedValue placeholder();

	@DDMFormField(
		dataType = "",
		ddmFormFieldProperties = {
			@DDMFormFieldProperty(name = "buttonLabel", value = "%maps"),
			@DDMFormFieldProperty(
				name = "messageArguments", value = {"<strong>", "</strong>"}
			),
			@DDMFormFieldProperty(
				name = "messageKey",
				value = "a-google-places-api-key-is-required-to-use-this-field"
			),
			@DDMFormFieldProperty(
				name = "mvcRenderCommandName",
				value = "/configuration_admin/view_configuration_screen"
			),
			@DDMFormFieldProperty(
				name = "parameters",
				value = "configurationScreenKey=google-places-site-settings"
			),
			@DDMFormFieldProperty(
				name = "portletId",
				value = ConfigurationAdminPortletKeys.SITE_SETTINGS
			),
			@DDMFormFieldProperty(
				name = "title", value = "%the-google-places-api-key-is-not-set"
			)
		},
		type = DDMFormFieldTypeConstants.REDIRECT_BUTTON
	)
	public void redirectButton();

	@DDMFormField(
		label = "%visible-fields",
		optionLabels = {
			"%address", "%city", "%state[region]", "%postal-code", "%country"
		},
		optionValues = {"address", "city", "state", "postal-code", "country"},
		properties = {
			"initialValue=%[\"address\",\"city\",\"state\",\"postal-code\",\"country\"]",
			"multiple=true"
		},
		type = DDMFormFieldTypeConstants.MULTI_LANGUAGE_OPTION_SELECT
	)
	public LocalizedValue visibleFields();

}