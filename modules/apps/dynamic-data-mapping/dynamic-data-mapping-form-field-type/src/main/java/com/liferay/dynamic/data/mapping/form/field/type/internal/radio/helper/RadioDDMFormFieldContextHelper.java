/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.field.type.internal.radio.helper;

import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Marcellus Tavares
 */
public class RadioDDMFormFieldContextHelper {

	public RadioDDMFormFieldContextHelper(
		DDMFormFieldOptions ddmFormFieldOptions, Locale locale) {

		_ddmFormFieldOptions = ddmFormFieldOptions;
		_locale = locale;
	}

	public List<Object> getOptions() {
		List<Object> options = new ArrayList<>();

		for (String optionValue : _ddmFormFieldOptions.getOptionsValues()) {
			options.add(
				HashMapBuilder.put(
					"label",
					() -> {
						LocalizedValue optionLabel =
							_ddmFormFieldOptions.getOptionLabels(optionValue);

						return optionLabel.getString(_locale);
					}
				).put(
					"reference",
					_ddmFormFieldOptions.getOptionReference(optionValue)
				).put(
					"value", optionValue
				).build());
		}

		return options;
	}

	private final DDMFormFieldOptions _ddmFormFieldOptions;
	private final Locale _locale;

}