/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.builder.internal.context;

import com.liferay.dynamic.data.mapping.form.builder.context.DDMFormContextDeserializer;
import com.liferay.dynamic.data.mapping.form.builder.context.DDMFormContextDeserializerRequest;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutColumn;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutPage;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutRow;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(
	property = "dynamic.data.mapping.form.builder.context.deserializer.type=formLayout",
	service = DDMFormContextDeserializer.class
)
public class DDMFormContextToDDMFormLayout
	implements DDMFormContextDeserializer<DDMFormLayout> {

	@Override
	public DDMFormLayout deserialize(
			DDMFormContextDeserializerRequest ddmFormContextDeserializerRequest)
		throws PortalException {

		String serializedFormContext =
			ddmFormContextDeserializerRequest.getProperty(
				"serializedFormContext");

		if (Validator.isNull(serializedFormContext)) {
			throw new IllegalStateException(
				"The property \"serializedFormContext\" is required");
		}

		return deserialize(serializedFormContext);
	}

	protected DDMFormLayout deserialize(String serializedFormContext)
		throws PortalException {

		JSONObject jsonObject = jsonFactory.createJSONObject(
			serializedFormContext);

		DDMFormLayout ddmFormLayout = new DDMFormLayout();

		_setDDMFormLayoutAvailableLocales(
			jsonObject.getJSONArray("availableLanguageIds"), ddmFormLayout);
		_setDDMFormLayoutDefaultLocale(
			jsonObject.getString("defaultLanguageId"), ddmFormLayout);
		_setDDMFormLayoutPages(jsonObject.getJSONArray("pages"), ddmFormLayout);
		_setDDMFormLayoutPaginationMode(
			jsonObject.getString("paginationMode", DDMFormLayout.WIZARD_MODE),
			ddmFormLayout);

		return ddmFormLayout;
	}

	protected Set<Locale> getAvailableLocales(JSONArray jsonArray) {
		Set<Locale> availableLocales = new HashSet<>();

		for (int i = 0; i < jsonArray.length(); i++) {
			availableLocales.add(
				LocaleUtil.fromLanguageId(jsonArray.getString(i)));
		}

		return availableLocales;
	}

	protected Locale getDefaultLocale(String defaultLanguageId) {
		return LocaleUtil.fromLanguageId(defaultLanguageId);
	}

	protected LocalizedValue getLocalizedValue(
		JSONObject jsonObject, Set<Locale> availableLocales,
		Locale defaultLocale) {

		LocalizedValue localizedValue = new LocalizedValue(defaultLocale);

		String defaultValueString = jsonObject.getString(
			LocaleUtil.toLanguageId(defaultLocale));

		for (Locale availableLocale : availableLocales) {
			String valueString = jsonObject.getString(
				LocaleUtil.toLanguageId(availableLocale), defaultValueString);

			localizedValue.addString(availableLocale, valueString);
		}

		return localizedValue;
	}

	@Reference
	protected JSONFactory jsonFactory;

	private DDMFormLayoutColumn _getDDMFormLayoutColumn(JSONObject jsonObject) {
		DDMFormLayoutColumn ddmFormLayoutColumn = new DDMFormLayoutColumn(
			jsonObject.getInt("size"));

		_setDDMFormLayoutColumnFieldNames(
			jsonObject.getJSONArray("fields"), ddmFormLayoutColumn);

		return ddmFormLayoutColumn;
	}

	private List<DDMFormLayoutColumn> _getDDMFormLayoutColumns(
		JSONArray jsonArray) {

		List<DDMFormLayoutColumn> ddmFormLayoutColumns = new ArrayList<>();

		for (int i = 0; i < jsonArray.length(); i++) {
			ddmFormLayoutColumns.add(
				_getDDMFormLayoutColumn(jsonArray.getJSONObject(i)));
		}

		return ddmFormLayoutColumns;
	}

	private DDMFormLayoutPage _getDDMFormLayoutPage(
		JSONObject jsonObject, Set<Locale> availableLocales,
		Locale defaultLocale) {

		DDMFormLayoutPage ddmFormLayoutPage = new DDMFormLayoutPage();

		_setDDMFormLayoutPageDescription(
			jsonObject.getJSONObject("description"), availableLocales,
			defaultLocale, ddmFormLayoutPage);
		_setDDMFormLayoutPageRows(
			jsonObject.getJSONArray("rows"), ddmFormLayoutPage);
		_setDDMFormLayoutPageTitle(
			jsonObject.getJSONObject("title"), availableLocales, defaultLocale,
			ddmFormLayoutPage);

		return ddmFormLayoutPage;
	}

	private List<DDMFormLayoutPage> _getDDMFormLayoutPages(
		JSONArray jsonArray, Set<Locale> availableLocales,
		Locale defaultLocale) {

		List<DDMFormLayoutPage> ddmFormLayoutPages = new ArrayList<>();

		for (int i = 0; i < jsonArray.length(); i++) {
			ddmFormLayoutPages.add(
				_getDDMFormLayoutPage(
					jsonArray.getJSONObject(i), availableLocales,
					defaultLocale));
		}

		return ddmFormLayoutPages;
	}

	private DDMFormLayoutRow _getDDMFormLayoutRow(JSONObject jsonObject) {
		DDMFormLayoutRow ddmFormLayoutRow = new DDMFormLayoutRow();

		_setDDMFormLayoutRowColumns(
			jsonObject.getJSONArray("columns"), ddmFormLayoutRow);

		return ddmFormLayoutRow;
	}

	private List<DDMFormLayoutRow> _getDDMFormLayoutRows(JSONArray jsonArray) {
		List<DDMFormLayoutRow> ddmFormLayoutRows = new ArrayList<>();

		for (int i = 0; i < jsonArray.length(); i++) {
			ddmFormLayoutRows.add(
				_getDDMFormLayoutRow(jsonArray.getJSONObject(i)));
		}

		return ddmFormLayoutRows;
	}

	private void _setDDMFormLayoutAvailableLocales(
		JSONArray jsonArray, DDMFormLayout ddmFormLayout) {

		ddmFormLayout.setAvailableLocales(getAvailableLocales(jsonArray));
	}

	private void _setDDMFormLayoutColumnFieldNames(
		JSONArray jsonArray, DDMFormLayoutColumn ddmFormLayoutColumn) {

		List<String> ddmFormFieldNames = new ArrayList<>(jsonArray.length());

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			ddmFormFieldNames.add(jsonObject.getString("fieldName"));
		}

		ddmFormLayoutColumn.setDDMFormFieldNames(ddmFormFieldNames);
	}

	private void _setDDMFormLayoutDefaultLocale(
		String defaultLanguageId, DDMFormLayout ddmFormLayout) {

		ddmFormLayout.setDefaultLocale(
			LocaleUtil.fromLanguageId(defaultLanguageId));
	}

	private void _setDDMFormLayoutPageDescription(
		JSONObject jsonObject, Set<Locale> availableLocales,
		Locale defaultLocale, DDMFormLayoutPage ddmFormLayoutPage) {

		ddmFormLayoutPage.setDescription(
			getLocalizedValue(jsonObject, availableLocales, defaultLocale));
	}

	private void _setDDMFormLayoutPageRows(
		JSONArray jsonArray, DDMFormLayoutPage ddmFormLayoutPage) {

		ddmFormLayoutPage.setDDMFormLayoutRows(
			_getDDMFormLayoutRows(jsonArray));
	}

	private void _setDDMFormLayoutPageTitle(
		JSONObject jsonObject, Set<Locale> availableLocales,
		Locale defaultLocale, DDMFormLayoutPage ddmFormLayoutPage) {

		ddmFormLayoutPage.setTitle(
			getLocalizedValue(jsonObject, availableLocales, defaultLocale));
	}

	private void _setDDMFormLayoutPages(
		JSONArray jsonArray, DDMFormLayout ddmFormLayout) {

		List<DDMFormLayoutPage> ddmFormLayoutPages = _getDDMFormLayoutPages(
			jsonArray, ddmFormLayout.getAvailableLocales(),
			ddmFormLayout.getDefaultLocale());

		ddmFormLayout.setDDMFormLayoutPages(ddmFormLayoutPages);
	}

	private void _setDDMFormLayoutPaginationMode(
		String paginationMode, DDMFormLayout ddmFormLayout) {

		ddmFormLayout.setPaginationMode(paginationMode);
	}

	private void _setDDMFormLayoutRowColumns(
		JSONArray jsonArray, DDMFormLayoutRow ddmFormLayoutRow) {

		ddmFormLayoutRow.setDDMFormLayoutColumns(
			_getDDMFormLayoutColumns(jsonArray));
	}

}