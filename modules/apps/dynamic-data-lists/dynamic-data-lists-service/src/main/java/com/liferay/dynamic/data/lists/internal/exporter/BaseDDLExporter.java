/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.internal.exporter;

import com.liferay.dynamic.data.lists.constants.DDLRecordSetConstants;
import com.liferay.dynamic.data.lists.exporter.DDLExporter;
import com.liferay.dynamic.data.lists.model.DDLRecord;
import com.liferay.dynamic.data.lists.model.DDLRecordSetVersion;
import com.liferay.dynamic.data.lists.service.DDLRecordSetVersionService;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeServicesRegistry;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldValueRenderer;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldType;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldValueRendererRegistry;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.HtmlParser;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * @author Marcellus Tavares
 * @author Manuel de la Peña
 */
public abstract class BaseDDLExporter implements DDLExporter {

	@Override
	public byte[] export(long recordSetId) throws Exception {
		return doExport(
			recordSetId, WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	@Override
	public byte[] export(long recordSetId, int status) throws Exception {
		return doExport(
			recordSetId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	@Override
	public byte[] export(long recordSetId, int status, int start, int end)
		throws Exception {

		return doExport(recordSetId, status, start, end, null);
	}

	@Override
	public byte[] export(
			long recordSetId, int status, int start, int end,
			OrderByComparator<DDLRecord> orderByComparator)
		throws Exception {

		return doExport(recordSetId, status, start, end, orderByComparator);
	}

	@Override
	public Locale getLocale() {
		if (_locale == null) {
			_locale = LocaleUtil.getSiteDefault();
		}

		return _locale;
	}

	@Override
	public void setLocale(Locale locale) {
		_locale = locale;
	}

	protected abstract byte[] doExport(
			long recordSetId, int status, int start, int end,
			OrderByComparator<DDLRecord> orderByComparator)
		throws Exception;

	protected String formatDate(
		Date date, DateTimeFormatter dateTimeFormatter) {

		return dateTimeFormatter.format(
			LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()));
	}

	protected abstract DDLRecordSetVersionService
		getDDLRecordSetVersionService();

	protected abstract DDMFormFieldTypeServicesRegistry
		getDDMFormFieldTypeServicesRegistry();

	protected abstract DDMFormFieldValueRendererRegistry
		getDDMFormFieldValueRendererRegistry();

	protected DateTimeFormatter getDateTimeFormatter() {
		DateTimeFormatter dateTimeFormatter =
			DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);

		return dateTimeFormatter.withLocale(getLocale());
	}

	protected Map<String, DDMFormField> getDistinctFields(long recordSetId)
		throws Exception {

		Map<String, DDMFormField> ddmFormFields = new LinkedHashMap<>();

		DDLRecordSetVersionService ddlRecordSetVersionService =
			getDDLRecordSetVersionService();

		for (DDMStructureVersion ddmStructureVersion :
				TransformUtil.transform(
					ddlRecordSetVersionService.getRecordSetVersions(
						recordSetId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
						null),
					DDLRecordSetVersion::getDDMStructureVersion)) {

			DDMForm ddmForm = ddmStructureVersion.getDDMForm();

			ddmFormFields.putAll(ddmForm.getDDMFormFieldsMap(true));
		}

		return ddmFormFields;
	}

	protected Map<String, DDMFormFieldRenderedValue> getRenderedValues(
			int scope, Collection<DDMFormField> ddmFormFields,
			DDMFormValues ddmFormValues, HtmlParser htmlParser)
		throws Exception {

		Map<String, DDMFormFieldRenderedValue> values = new HashMap<>();

		Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap =
			ddmFormValues.getDDMFormFieldValuesMap(false);

		for (DDMFormField ddmFormField : ddmFormFields) {
			if (!ddmFormFieldValuesMap.containsKey(ddmFormField.getName())) {
				continue;
			}

			DDMFormFieldRenderedValue ddmFormFieldRenderedValue =
				_getDDMFormFieldRenderedValue(
					scope, ddmFormField, ddmFormFieldValuesMap, htmlParser);

			values.put(
				ddmFormFieldRenderedValue.getFieldName(),
				ddmFormFieldRenderedValue);
		}

		return values;
	}

	protected String getStatusMessage(int status) {
		return LanguageUtil.get(
			_locale, WorkflowConstants.getStatusLabel(status));
	}

	protected static class DDMFormFieldRenderedValue {

		protected DDMFormFieldRenderedValue(
			String fieldName, LocalizedValue label, String value) {

			_fieldName = fieldName;
			_label = label;
			_value = value;
		}

		protected String getFieldName() {
			return _fieldName;
		}

		protected LocalizedValue getLabel() {
			return _label;
		}

		protected String getValue() {
			return _value;
		}

		private final String _fieldName;
		private final LocalizedValue _label;
		private final String _value;

	}

	private DDMFormFieldRenderedValue _getDDMFormFieldRenderedValue(
		int scope, DDMFormField ddmFormField,
		Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap,
		HtmlParser htmlParser) {

		List<DDMFormFieldValue> ddmFormFieldValues = ddmFormFieldValuesMap.get(
			ddmFormField.getName());

		String valueString = StringPool.BLANK;

		DDMFormFieldValue ddmFormFieldValue = ddmFormFieldValues.get(0);

		if (scope == DDLRecordSetConstants.SCOPE_FORMS) {
			DDMFormFieldValueRenderer ddmFormFieldValueRenderer =
				getDDMFormFieldTypeServicesRegistry().
					getDDMFormFieldValueRenderer(ddmFormFieldValue.getType());

			valueString = ddmFormFieldValueRenderer.render(
				ddmFormFieldValue, getLocale());
		}
		else {
			DDMFormFieldValueRendererRegistry
				ddmFormFieldValueRendererRegistry =
					getDDMFormFieldValueRendererRegistry();

			com.liferay.dynamic.data.mapping.render.DDMFormFieldValueRenderer
				ddmFormFieldValueRenderer =
					ddmFormFieldValueRendererRegistry.
						getDDMFormFieldValueRenderer(ddmFormField.getType());

			if (Objects.equals(
					DDMFormFieldType.TEXT_HTML,
					ddmFormFieldValueRenderer.getSupportedDDMFormFieldType())) {

				Value value = ddmFormFieldValue.getValue();

				valueString = HtmlUtil.escape(value.getString(getLocale()));
			}
			else {
				valueString = ddmFormFieldValueRenderer.render(
					ddmFormFieldValues, getLocale());
			}
		}

		valueString = htmlParser.render(valueString);

		return new DDMFormFieldRenderedValue(
			ddmFormField.getName(), ddmFormField.getLabel(), valueString);
	}

	private Locale _locale;

}