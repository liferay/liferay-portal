/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.kernel;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author Pablo Carvalho
 */
public class DDMForm implements Serializable {

	public void addAvailableLocale(Locale locale) {
		_availableLocales.add(locale);
	}

	public void addDDMFormField(DDMFormField ddmFormField) {
		ddmFormField.setDDMForm(this);

		_ddmFormFields.add(ddmFormField);
	}

	public Set<Locale> getAvailableLocales() {
		return _availableLocales;
	}

	public List<DDMFormField> getDDMFormFields() {
		return _ddmFormFields;
	}

	public Map<String, DDMFormField> getDDMFormFieldsMap(
		boolean includeNestedDDMFormFields) {

		Map<String, DDMFormField> ddmFormFieldsMap = new LinkedHashMap<>();

		for (DDMFormField ddmFormField : _ddmFormFields) {
			ddmFormFieldsMap.put(ddmFormField.getName(), ddmFormField);

			if (!includeNestedDDMFormFields) {
				continue;
			}

			ddmFormFieldsMap.putAll(ddmFormField.getNestedDDMFormFieldsMap());
		}

		return ddmFormFieldsMap;
	}

	public Locale getDefaultLocale() {
		return _defaultLocale;
	}

	public void setAvailableLocales(Set<Locale> availableLocales) {
		_availableLocales = availableLocales;
	}

	public void setDDMFormFields(List<DDMFormField> ddmFormFields) {
		for (DDMFormField ddmFormField : ddmFormFields) {
			ddmFormField.setDDMForm(this);
		}

		_ddmFormFields = ddmFormFields;
	}

	public void setDefaultLocale(Locale defaultLocale) {
		_defaultLocale = defaultLocale;
	}

	private Set<Locale> _availableLocales = new LinkedHashSet<>();
	private List<DDMFormField> _ddmFormFields = new ArrayList<>();
	private Locale _defaultLocale;

}