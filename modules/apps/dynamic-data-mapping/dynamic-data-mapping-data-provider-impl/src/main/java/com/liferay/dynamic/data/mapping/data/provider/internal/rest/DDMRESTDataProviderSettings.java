/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.data.provider.internal.rest;

import com.liferay.dynamic.data.mapping.annotations.DDMForm;
import com.liferay.dynamic.data.mapping.annotations.DDMFormField;
import com.liferay.dynamic.data.mapping.annotations.DDMFormLayout;
import com.liferay.dynamic.data.mapping.annotations.DDMFormLayoutColumn;
import com.liferay.dynamic.data.mapping.annotations.DDMFormLayoutPage;
import com.liferay.dynamic.data.mapping.annotations.DDMFormLayoutRow;
import com.liferay.dynamic.data.mapping.annotations.DDMFormRule;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderParameterSettings;

/**
 * @author Marcellus Tavares
 */
@DDMForm(
	rules = {
		@DDMFormRule(
			actions = {
				"setVisible('filterParameterName', true)",
				"setRequired('filterParameterName', true)"
			},
			condition = "equals(getValue('filterable'), true)"
		),
		@DDMFormRule(
			actions = {
				"setVisible('pagination', false)",
				"setVisible('paginationStartParameterName', false)",
				"setVisible('paginationEndParameterName', false)",
				"setRequired('paginationStartParameterName', equals(getValue('pagination'), true))",
				"setRequired('paginationEndParameterName', equals(getValue('pagination'), true))"
			},
			condition = "TRUE"
		)
	}
)
@DDMFormLayout(
	{
		@DDMFormLayoutPage(
			{
				@DDMFormLayoutRow(
					{
						@DDMFormLayoutColumn(
							size = 12,
							value = {
								"url", "username", "password", "filterable",
								"filterParameterName", "cacheable",
								"pagination", "paginationStartParameterName",
								"paginationEndParameterName", "timeout",
								"inputParameters", "outputParameters"
							}
						)
					}
				)
			}
		)
	}
)
public interface DDMRESTDataProviderSettings
	extends DDMDataProviderParameterSettings {

	@DDMFormField(
		label = "%cache-data-on-the-first-request",
		properties = "showAsSwitcher=true"
	)
	public boolean cacheable();

	@DDMFormField(
		label = "%filter-parameter-name",
		properties = {
			"placeholder=%enter-a-name-that-matches-one-of-the-rest-providers-parameters",
			"tooltip=%the-parameter-whose-value-will-be-used-as-a-filter-by-the-rest-provider"
		}
	)
	public String filterParameterName();

	@DDMFormField(
		label = "%support-filtering-by-keyword",
		properties = "showAsSwitcher=true"
	)
	public boolean filterable();

	@DDMFormField(
		label = "%support-pagination", predefinedValue = "false",
		properties = "showAsSwitcher=true"
	)
	public boolean pagination();

	@DDMFormField(
		label = "%end-parameter-name", predefinedValue = "end",
		properties = {
			"placeholder=%enter-a-name-that-matches-one-of-the-rest-providers-parameters",
			"tooltip=%the-parameter-whose-value-will-be-used-as-an-end-by-the-rest-provider"
		}
	)
	public String paginationEndParameterName();

	@DDMFormField(
		label = "%start-parameter-name", predefinedValue = "start",
		properties = {
			"placeholder=%enter-a-name-that-matches-one-of-the-rest-providers-parameters",
			"tooltip=%the-parameter-whose-value-will-be-used-as-a-start-by-the-rest-provider"
		}
	)
	public String paginationStartParameterName();

	@DDMFormField(
		label = "%password",
		properties = {
			"placeholder=%enter-a-password",
			"tooltip=%provide-the-password-for-authenticating-to-the-rest-provider"
		},
		type = "password"
	)
	public String password();

	@DDMFormField(
		dataType = "integer", label = "%timeout", predefinedValue = "1000",
		properties = {
			"placeholder=%enter-time-in-milliseconds",
			"tooltip=%data-provider-timeout-message"
		},
		required = true, type = "numeric",
		validationErrorMessage = "%please-enter-an-integer-between-1000-and-30000-milliseconds",
		validationExpression = "(timeout >= 1000) && (timeout <= 30000)"
	)
	public String timeout();

	@DDMFormField(
		label = "%url", properties = "placeholder=%enter-the-rest-service-url",
		required = true, validationErrorMessage = "%please-enter-a-valid-url",
		validationExpression = "isURL(url)"
	)
	public String url();

	@DDMFormField(
		label = "%user-name",
		properties = {
			"placeholder=%enter-a-user-name",
			"tooltip=%provide-the-user-name-for-authenticating-to-the-rest-provider"
		}
	)
	public String username();

}