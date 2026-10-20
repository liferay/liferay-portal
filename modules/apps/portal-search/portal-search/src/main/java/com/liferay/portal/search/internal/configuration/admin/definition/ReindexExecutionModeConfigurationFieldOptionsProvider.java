/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.configuration.admin.definition;

import com.liferay.configuration.admin.definition.ConfigurationFieldOptionsProvider;
import com.liferay.portal.kernel.language.Language;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bryan Engler
 */
@Component(
	property = {
		"configuration.field.name=defaultReindexExecutionMode",
		"configuration.pid=com.liferay.portal.search.configuration.ReindexConfiguration"
	},
	service = ConfigurationFieldOptionsProvider.class
)
public class ReindexExecutionModeConfigurationFieldOptionsProvider
	implements ConfigurationFieldOptionsProvider {

	@Override
	public List<Option> getOptions() {
		List<Option> options = new ArrayList<>();

		List<String> executionModes = new ArrayList<>();

		executionModes.add("concurrent");
		executionModes.add("full");
		executionModes.add("sync");

		for (String executionMode : executionModes) {
			Option option = new Option() {

				@Override
				public String getLabel(Locale locale) {
					if (executionMode.equals("sync")) {
						return _language.get(locale, "sync[mode]");
					}

					return _language.get(locale, executionMode);
				}

				@Override
				public String getValue() {
					return executionMode;
				}

			};

			options.add(option);
		}

		return options;
	}

	@Reference
	private Language _language;

}