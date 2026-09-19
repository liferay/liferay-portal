/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.form;

import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldSet;
import com.liferay.info.field.InfoFieldSetEntry;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.MapUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * @author Jorge Ferrer
 */
public class InfoForm {

	public static Builder builder() {
		return new Builder();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof InfoForm)) {
			return false;
		}

		InfoForm infoForm = (InfoForm)object;

		if (Objects.equals(
				_builder._descriptionInfoLocalizedValue,
				infoForm._builder._descriptionInfoLocalizedValue) &&
			Objects.equals(
				_builder._labelInfoLocalizedValue,
				infoForm._builder._labelInfoLocalizedValue) &&
			Objects.equals(_builder._name, infoForm._builder._name)) {

			return true;
		}

		return false;
	}

	public List<InfoField<?>> getAllInfoFields() {
		return new ArrayList<>(_builder._infoFieldsByExternalUniqueId.values());
	}

	public InfoLocalizedValue<String> getDescriptionInfoLocalizedValue() {
		return _builder._descriptionInfoLocalizedValue;
	}

	public InfoField<?> getInfoField(String name) {
		InfoField<?> infoField = _builder._infoFieldsByExternalUniqueId.get(
			name);

		if (infoField != null) {
			return infoField;
		}

		infoField = _builder._infoFieldsByUniqueId.get(name);

		if (infoField != null) {
			return infoField;
		}

		return _builder._infoFieldsByName.get(name);
	}

	public List<InfoFieldSetEntry> getInfoFieldSetEntries() {
		return new ArrayList<>(_builder._infoFieldSetEntriesByName.values());
	}

	public InfoFieldSetEntry getInfoFieldSetEntry(String name) {
		return _builder._infoFieldSetEntriesByName.get(name);
	}

	public String getLabel(Locale locale) {
		if (_builder._labelInfoLocalizedValue == null) {
			return _builder._name;
		}

		return _builder._labelInfoLocalizedValue.getValue(locale);
	}

	public InfoLocalizedValue<String> getLabelInfoLocalizedValue() {
		return _builder._labelInfoLocalizedValue;
	}

	public String getName() {
		return _builder._name;
	}

	@Override
	public int hashCode() {
		int hash = HashUtil.hash(0, _builder._descriptionInfoLocalizedValue);

		hash = HashUtil.hash(hash, _builder._labelInfoLocalizedValue);

		return HashUtil.hash(hash, _builder._name);
	}

	@Override
	public String toString() {
		return StringBundler.concat(
			"{_infoFieldSetEntriesByName: ",
			MapUtil.toString(_builder._infoFieldSetEntriesByName), ", name: ",
			_builder._name, "}");
	}

	public static class Builder {

		public InfoForm build() {
			return new InfoForm(this);
		}

		public Builder descriptionInfoLocalizedValue(
			InfoLocalizedValue<String> descriptionInfoLocalizedValue) {

			_descriptionInfoLocalizedValue = descriptionInfoLocalizedValue;

			return this;
		}

		public Builder infoFieldSetEntries(
			Collection<InfoFieldSetEntry> infoFieldSetEntries) {

			for (InfoFieldSetEntry infoFieldSetEntry : infoFieldSetEntries) {
				infoFieldSetEntry(infoFieldSetEntry);
			}

			return this;
		}

		public Builder infoFieldSetEntry(InfoFieldSet infoFieldSet) {
			InfoFieldSetEntry existingInfoFieldSetEntry =
				_infoFieldSetEntriesByName.get(infoFieldSet.getName());

			if (existingInfoFieldSetEntry instanceof InfoFieldSet) {
				InfoFieldSet existingInfoFieldSet =
					(InfoFieldSet)existingInfoFieldSetEntry;

				_infoFieldSetEntriesByName.put(
					infoFieldSet.getName(),
					InfoFieldSet.builder(
					).infoFieldSetEntries(
						existingInfoFieldSet.getInfoFieldSetEntries()
					).infoFieldSetEntries(
						infoFieldSet.getInfoFieldSetEntries()
					).labelInfoLocalizedValue(
						existingInfoFieldSet.getLabelInfoLocalizedValue()
					).name(
						existingInfoFieldSet.getName()
					).build());
			}
			else {
				_infoFieldSetEntriesByName.put(
					infoFieldSet.getName(), infoFieldSet);
			}

			_populateInfoFieldsMaps(infoFieldSet);

			return this;
		}

		public Builder infoFieldSetEntry(InfoFieldSetEntry infoFieldSetEntry) {
			_infoFieldSetEntriesByName.put(
				infoFieldSetEntry.getName(), infoFieldSetEntry);

			_populateInfoFieldsMaps(infoFieldSetEntry);

			return this;
		}

		public <T extends Throwable> Builder infoFieldSetEntry(
				UnsafeConsumer<UnsafeConsumer<InfoFieldSetEntry, T>, T>
					unsafeConsumer)
			throws T {

			unsafeConsumer.accept(this::infoFieldSetEntry);

			return this;
		}

		public Builder labelInfoLocalizedValue(
			InfoLocalizedValue<String> labelInfoLocalizedValue) {

			_labelInfoLocalizedValue = labelInfoLocalizedValue;

			return this;
		}

		public Builder name(String name) {
			_name = name;

			return this;
		}

		private void _populateInfoFieldsMaps(
			InfoFieldSetEntry infoFieldSetEntry) {

			if (infoFieldSetEntry == null) {
				return;
			}

			if (infoFieldSetEntry instanceof InfoField) {
				_infoFieldsByExternalUniqueId.put(
					infoFieldSetEntry.getExternalUniqueId(),
					(InfoField<?>)infoFieldSetEntry);
				_infoFieldsByName.put(
					infoFieldSetEntry.getName(),
					(InfoField<?>)infoFieldSetEntry);
				_infoFieldsByUniqueId.put(
					infoFieldSetEntry.getUniqueId(),
					(InfoField<?>)infoFieldSetEntry);

				return;
			}

			InfoFieldSet infoFieldSet = (InfoFieldSet)infoFieldSetEntry;

			for (InfoField<?> infoField : infoFieldSet.getAllInfoFields()) {
				_infoFieldsByExternalUniqueId.put(
					infoField.getExternalUniqueId(), infoField);
				_infoFieldsByName.put(infoField.getName(), infoField);
				_infoFieldsByUniqueId.put(infoField.getUniqueId(), infoField);
			}
		}

		private InfoLocalizedValue<String> _descriptionInfoLocalizedValue;
		private final Map<String, InfoFieldSetEntry>
			_infoFieldSetEntriesByName = new LinkedHashMap<>();
		private final Map<String, InfoField<?>> _infoFieldsByExternalUniqueId =
			new LinkedHashMap<>();
		private final Map<String, InfoField<?>> _infoFieldsByName =
			new LinkedHashMap<>();
		private final Map<String, InfoField<?>> _infoFieldsByUniqueId =
			new LinkedHashMap<>();
		private InfoLocalizedValue<String> _labelInfoLocalizedValue;
		private String _name;

	}

	private InfoForm(Builder builder) {
		_builder = builder;
	}

	private final Builder _builder;

}