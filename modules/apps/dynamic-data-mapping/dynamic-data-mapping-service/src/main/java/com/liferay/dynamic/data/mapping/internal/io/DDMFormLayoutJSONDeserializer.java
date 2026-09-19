/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.io;

import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeServicesRegistry;
import com.liferay.dynamic.data.mapping.internal.io.util.DDMFormFieldDeserializerUtil;
import com.liferay.dynamic.data.mapping.io.DDMFormLayoutDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormLayoutDeserializerDeserializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormLayoutDeserializerDeserializeResponse;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutColumn;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutPage;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutRow;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.util.LocalizedValueUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(
	property = "ddm.form.layout.deserializer.type=json",
	service = DDMFormLayoutDeserializer.class
)
public class DDMFormLayoutJSONDeserializer
	implements DDMFormLayoutDeserializer {

	@Override
	public DDMFormLayoutDeserializerDeserializeResponse deserialize(
		DDMFormLayoutDeserializerDeserializeRequest
			ddmFormLayoutDeserializerDeserializeRequest) {

		DDMFormLayout ddmFormLayout = new DDMFormLayout();

		DDMFormLayoutDeserializerDeserializeResponse.Builder builder =
			DDMFormLayoutDeserializerDeserializeResponse.Builder.newBuilder(
				ddmFormLayout);

		try {
			JSONObject jsonObject = _jsonFactory.createJSONObject(
				ddmFormLayoutDeserializerDeserializeRequest.getContent());

			JSONArray jsonArray = jsonObject.getJSONArray("fields");

			if (jsonArray == null) {
				jsonArray = _jsonFactory.createJSONArray();
			}

			ddmFormLayout.setDDMFormFields(
				DDMFormFieldDeserializerUtil.deserialize(
					_ddmFormFieldTypeServicesRegistry, jsonArray,
					_jsonFactory));

			if (Validator.isNotNull(
					jsonObject.getString("definitionSchemaVersion"))) {

				ddmFormLayout.setDefinitionSchemaVersion(
					jsonObject.getString("definitionSchemaVersion"));
			}

			_setDDMFormLayoutDefaultLocale(
				jsonObject.getString("defaultLanguageId"), ddmFormLayout);
			_setDDMFormLayoutPages(
				jsonObject.getJSONArray("pages"), ddmFormLayout);

			_setDDMFormLayoutPageTitlesDefaultLocale(ddmFormLayout);

			String paginationMode = jsonObject.getString("paginationMode");

			if (Validator.isNotNull(paginationMode)) {
				_setDDMFormLayoutPaginationMode(paginationMode, ddmFormLayout);
			}
			else {
				_setDDMFormLayoutPaginationMode(
					DDMFormLayout.WIZARD_MODE, ddmFormLayout);
			}

			setDDMFormRules(jsonObject.getJSONArray("rules"), ddmFormLayout);

			return builder.build();
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			builder = builder.exception(exception);
		}

		return builder.build();
	}

	protected static void setDDMFormRules(
		JSONArray jsonArray, DDMFormLayout ddmFormLayout) {

		if ((jsonArray == null) || (jsonArray.length() == 0)) {
			return;
		}

		ddmFormLayout.setDDMFormRules(
			DDMFormRuleJSONDeserializer.deserialize(jsonArray));
	}

	protected DDMFormLayoutColumn getDDMFormLayoutColumn(
		JSONObject jsonObject) {

		DDMFormLayoutColumn ddmFormLayoutColumn = new DDMFormLayoutColumn(
			jsonObject.getInt("size"));

		_setDDMFormLayouColumnFieldNames(
			jsonObject.getJSONArray("fieldNames"), ddmFormLayoutColumn);

		return ddmFormLayoutColumn;
	}

	protected List<DDMFormLayoutColumn> getDDMFormLayoutColumns(
		JSONArray jsonArray) {

		List<DDMFormLayoutColumn> ddmFormLayoutColumns = new ArrayList<>();

		for (int i = 0; i < jsonArray.length(); i++) {
			ddmFormLayoutColumns.add(
				getDDMFormLayoutColumn(jsonArray.getJSONObject(i)));
		}

		return ddmFormLayoutColumns;
	}

	protected DDMFormLayoutPage getDDMFormLayoutPage(JSONObject jsonObject) {
		DDMFormLayoutPage ddmFormLayoutPage = new DDMFormLayoutPage();

		_setDDMFormLayoutPageDescription(
			jsonObject.getJSONObject("description"), ddmFormLayoutPage);
		_setDDMFormLayoutPageRows(
			jsonObject.getJSONArray("rows"), ddmFormLayoutPage);
		_setDDMFormLayoutPageTitle(
			jsonObject.getJSONObject("title"), ddmFormLayoutPage);

		return ddmFormLayoutPage;
	}

	protected List<DDMFormLayoutPage> getDDMFormLayoutPages(
		JSONArray jsonArray) {

		List<DDMFormLayoutPage> ddmFormLayoutPages = new ArrayList<>();

		for (int i = 0; i < jsonArray.length(); i++) {
			ddmFormLayoutPages.add(
				getDDMFormLayoutPage(jsonArray.getJSONObject(i)));
		}

		return ddmFormLayoutPages;
	}

	protected List<DDMFormLayoutRow> getDDMFormLayoutRows(JSONArray jsonArray) {
		List<DDMFormLayoutRow> ddmFormLayoutRows = new ArrayList<>();

		for (int i = 0; i < jsonArray.length(); i++) {
			ddmFormLayoutRows.add(
				_getDDMFormLayoutRow(jsonArray.getJSONObject(i)));
		}

		return ddmFormLayoutRows;
	}

	private List<String> _getDDMFormLayoutColumnFieldNames(
		JSONArray jsonArray) {

		List<String> ddmFormFieldNames = new ArrayList<>();

		for (int i = 0; i < jsonArray.length(); i++) {
			ddmFormFieldNames.add(jsonArray.getString(i));
		}

		return ddmFormFieldNames;
	}

	private DDMFormLayoutRow _getDDMFormLayoutRow(JSONObject jsonObject) {
		DDMFormLayoutRow ddmFormLayoutRow = new DDMFormLayoutRow();

		_setDDMFormLayoutRowColumns(
			jsonObject.getJSONArray("columns"), ddmFormLayoutRow);

		return ddmFormLayoutRow;
	}

	private void _setDDMFormLayouColumnFieldNames(
		JSONArray jsonArray, DDMFormLayoutColumn ddmFormLayoutColumn) {

		List<String> ddmFormLayoutColumnNames =
			_getDDMFormLayoutColumnFieldNames(jsonArray);

		ddmFormLayoutColumn.setDDMFormFieldNames(ddmFormLayoutColumnNames);
	}

	private void _setDDMFormLayoutDefaultLocale(
		String defaultLanguageId, DDMFormLayout ddmFormLayout) {

		ddmFormLayout.setDefaultLocale(
			LocaleUtil.fromLanguageId(defaultLanguageId));
	}

	private void _setDDMFormLayoutPageDescription(
		JSONObject jsonObject, DDMFormLayoutPage ddmFormLayoutPage) {

		LocalizedValue description = LocalizedValueUtil.toLocalizedValue(
			jsonObject);

		if (description == null) {
			return;
		}

		ddmFormLayoutPage.setDescription(description);
	}

	private void _setDDMFormLayoutPageRows(
		JSONArray jsonArray, DDMFormLayoutPage ddmFormLayoutPage) {

		ddmFormLayoutPage.setDDMFormLayoutRows(getDDMFormLayoutRows(jsonArray));
	}

	private void _setDDMFormLayoutPageTitle(
		JSONObject jsonObject, DDMFormLayoutPage ddmFormLayoutPage) {

		LocalizedValue title = LocalizedValueUtil.toLocalizedValue(jsonObject);

		if (title == null) {
			return;
		}

		ddmFormLayoutPage.setTitle(title);
	}

	private void _setDDMFormLayoutPageTitlesDefaultLocale(
		DDMFormLayout ddmFormLayout) {

		for (DDMFormLayoutPage ddmFormLayoutPage :
				ddmFormLayout.getDDMFormLayoutPages()) {

			LocalizedValue title = ddmFormLayoutPage.getTitle();

			title.setDefaultLocale(ddmFormLayout.getDefaultLocale());
		}
	}

	private void _setDDMFormLayoutPages(
		JSONArray jsonArray, DDMFormLayout ddmFormLayout) {

		ddmFormLayout.setDDMFormLayoutPages(getDDMFormLayoutPages(jsonArray));
	}

	private void _setDDMFormLayoutPaginationMode(
		String paginationMode, DDMFormLayout ddmFormLayout) {

		ddmFormLayout.setPaginationMode(paginationMode);
	}

	private void _setDDMFormLayoutRowColumns(
		JSONArray jsonArray, DDMFormLayoutRow ddmFormLayoutRow) {

		ddmFormLayoutRow.setDDMFormLayoutColumns(
			getDDMFormLayoutColumns(jsonArray));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDMFormLayoutJSONDeserializer.class);

	@Reference
	private DDMFormFieldTypeServicesRegistry _ddmFormFieldTypeServicesRegistry;

	@Reference
	private JSONFactory _jsonFactory;

}