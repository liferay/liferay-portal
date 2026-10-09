/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.storage;

import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.petra.lang.HashUtil;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Marcellus Tavares
 * @author Pablo Carvalho
 */
public class DDMFormValues implements Serializable {

	public static List<DDMFormFieldValue> getDDMFormFieldValues(
		List<DDMFormFieldValue> ddmFormFieldValues, String name,
		boolean includeNestedDDMFormFieldValues) {

		List<DDMFormFieldValue> matchedDDMFormFieldValues = new ArrayList<>();

		for (DDMFormFieldValue ddmFormFieldValue : ddmFormFieldValues) {
			if (Objects.equals(name, ddmFormFieldValue.getName())) {
				matchedDDMFormFieldValues.add(ddmFormFieldValue);
			}

			if (!includeNestedDDMFormFieldValues) {
				continue;
			}

			ddmFormFieldValue.populateNestedDDMFormFieldValues(
				name, matchedDDMFormFieldValues);
		}

		return matchedDDMFormFieldValues;
	}

	public DDMFormValues(DDMForm ddmForm) {
		_ddmForm = ddmForm;
	}

	public void addAvailableLocale(Locale locale) {
		_availableLocales.add(locale);
	}

	public void addDDMFormFieldValue(DDMFormFieldValue ddmFormFieldValue) {
		ddmFormFieldValue.setDDMFormValues(this);

		_ddmFormFieldValues.add(ddmFormFieldValue);

		_ddmFormFieldValuesMap.putIfAbsent(
			ddmFormFieldValue.getName(), ddmFormFieldValue);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DDMFormValues)) {
			return false;
		}

		DDMFormValues ddmFormValues = (DDMFormValues)object;

		if (Objects.equals(
				_availableLocales, ddmFormValues._availableLocales) &&
			Objects.equals(_defaultLocale, ddmFormValues._defaultLocale) &&
			Objects.equals(
				_ddmFormFieldValues, ddmFormValues._ddmFormFieldValues)) {

			return true;
		}

		return false;
	}

	public Set<Locale> getAvailableLocales() {
		return _availableLocales;
	}

	public DDMForm getDDMForm() {
		return _ddmForm;
	}

	public DDMFormFieldValue getDDMFormFieldValue(
		String name, boolean includeNestedDDMFormFieldValues) {

		DDMFormFieldValue ddmFormFieldValue = _ddmFormFieldValuesMap.get(name);

		if (!includeNestedDDMFormFieldValues || (ddmFormFieldValue != null)) {
			return ddmFormFieldValue;
		}

		for (DDMFormFieldValue curDDMFormFieldValue : _ddmFormFieldValues) {
			DDMFormFieldValue matchedDDMFormFieldValue =
				curDDMFormFieldValue.getNestedDDMFormFieldValue(name);

			if (matchedDDMFormFieldValue != null) {
				return matchedDDMFormFieldValue;
			}
		}

		return null;
	}

	public List<DDMFormFieldValue> getDDMFormFieldValues() {
		return _ddmFormFieldValues;
	}

	public List<DDMFormFieldValue> getDDMFormFieldValues(
		String name, boolean includeNestedDDMFormFieldValues) {

		return getDDMFormFieldValues(
			_ddmFormFieldValues, name, includeNestedDDMFormFieldValues);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             #getDDMFormFieldValuesMap(boolean)}
	 */
	@Deprecated
	public Map<String, List<DDMFormFieldValue>> getDDMFormFieldValuesMap() {
		return getDDMFormFieldValuesMap(false);
	}

	public Map<String, List<DDMFormFieldValue>> getDDMFormFieldValuesMap(
		boolean includeNestedDDMFormFieldValues) {

		Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap =
			new LinkedHashMap<>();

		for (DDMFormFieldValue ddmFormFieldValue : _ddmFormFieldValues) {
			List<DDMFormFieldValue> ddmFormFieldValues =
				ddmFormFieldValuesMap.get(ddmFormFieldValue.getName());

			if (ddmFormFieldValues == null) {
				ddmFormFieldValues = new ArrayList<>();

				ddmFormFieldValuesMap.put(
					ddmFormFieldValue.getName(), ddmFormFieldValues);
			}

			ddmFormFieldValues.add(ddmFormFieldValue);

			if (!includeNestedDDMFormFieldValues) {
				continue;
			}

			ddmFormFieldValue.populateNestedDDMFormFieldValuesMap(
				ddmFormFieldValuesMap);
		}

		return ddmFormFieldValuesMap;
	}

	public Map<String, List<DDMFormFieldValue>>
		getDDMFormFieldValuesReferencesMap(
			boolean includeNestedDDMFormFieldValues) {

		Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesReferencesMap =
			new LinkedHashMap<>();

		for (DDMFormFieldValue ddmFormFieldValue : _ddmFormFieldValues) {
			List<DDMFormFieldValue> ddmFormFieldValues =
				ddmFormFieldValuesReferencesMap.get(
					ddmFormFieldValue.getFieldReference());

			if (ddmFormFieldValues == null) {
				ddmFormFieldValues = new ArrayList<>();

				ddmFormFieldValuesReferencesMap.put(
					ddmFormFieldValue.getFieldReference(), ddmFormFieldValues);
			}

			ddmFormFieldValues.add(ddmFormFieldValue);

			if (!includeNestedDDMFormFieldValues) {
				continue;
			}

			ddmFormFieldValue.populateNestedDDMFormFieldValuesReferencesMap(
				ddmFormFieldValuesReferencesMap);
		}

		return ddmFormFieldValuesReferencesMap;
	}

	public Locale getDefaultLocale() {
		return _defaultLocale;
	}

	@Override
	public int hashCode() {
		int hash = HashUtil.hash(0, _availableLocales);

		hash = HashUtil.hash(hash, _defaultLocale);

		return HashUtil.hash(hash, _ddmFormFieldValues);
	}

	public void setAvailableLocales(Set<Locale> availableLocales) {
		_availableLocales = availableLocales;
	}

	public void setDDMFormFieldValues(
		List<DDMFormFieldValue> ddmFormFieldValues) {

		for (DDMFormFieldValue ddmFormFieldValue : ddmFormFieldValues) {
			ddmFormFieldValue.setDDMFormValues(this);
		}

		_ddmFormFieldValues = ddmFormFieldValues;

		for (DDMFormFieldValue ddmFormFieldValue : ddmFormFieldValues) {
			_ddmFormFieldValuesMap.putIfAbsent(
				ddmFormFieldValue.getName(), ddmFormFieldValue);
		}
	}

	public void setDefaultLocale(Locale defaultLocale) {
		_defaultLocale = defaultLocale;
	}

	private Set<Locale> _availableLocales = new LinkedHashSet<>();
	private final DDMForm _ddmForm;
	private List<DDMFormFieldValue> _ddmFormFieldValues = new ArrayList<>();
	private final Map<String, DDMFormFieldValue> _ddmFormFieldValuesMap =
		new HashMap<>();
	private Locale _defaultLocale;

}