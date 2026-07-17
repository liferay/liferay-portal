/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.internal.exporter;

import com.liferay.dynamic.data.lists.exporter.DDLExporter;
import com.liferay.dynamic.data.lists.model.DDLRecord;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.lists.model.DDLRecordVersion;
import com.liferay.dynamic.data.lists.service.DDLRecordLocalService;
import com.liferay.dynamic.data.lists.service.DDLRecordSetService;
import com.liferay.dynamic.data.lists.service.DDLRecordSetVersionService;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeServicesRegistry;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMStorageLink;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldValueRendererRegistry;
import com.liferay.dynamic.data.mapping.service.DDMStorageLinkLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureVersionLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMStorageEngineManager;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.CSVUtil;
import com.liferay.portal.kernel.util.HtmlParser;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.time.format.DateTimeFormatter;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 * @author Manuel de la Peña
 */
@Component(service = DDLExporter.class)
public class DDLCSVExporter extends BaseDDLExporter {

	@Override
	public String getFormat() {
		return "csv";
	}

	@Override
	protected byte[] doExport(
			long recordSetId, int status, int start, int end,
			OrderByComparator<DDLRecord> orderByComparator)
		throws Exception {

		StringBundler sb = new StringBundler();

		DDLRecordSet recordSet = _ddlRecordSetService.getRecordSet(recordSetId);

		Map<String, DDMFormField> ddmFormFields = getDistinctFields(
			recordSetId);

		Locale locale = getLocale();

		for (DDMFormField ddmFormField : ddmFormFields.values()) {
			LocalizedValue label = ddmFormField.getLabel();

			sb.append(CSVUtil.encode(label.getString(locale)));

			sb.append(CharPool.COMMA);
		}

		sb.append(_language.get(locale, "status"));
		sb.append(CharPool.COMMA);
		sb.append(_language.get(locale, "modified-date"));
		sb.append(CharPool.COMMA);
		sb.append(_language.get(locale, "author"));
		sb.append(StringPool.NEW_LINE);

		List<DDLRecord> ddlRecords = _ddlRecordLocalService.getRecords(
			recordSetId, status, start, end, orderByComparator);

		Iterator<DDLRecord> iterator = ddlRecords.iterator();

		DateTimeFormatter dateTimeFormatter = getDateTimeFormatter();

		while (iterator.hasNext()) {
			DDLRecord record = iterator.next();

			DDLRecordVersion recordVersion = record.getRecordVersion();

			DDMStorageLink ddmStorageLink =
				_ddmStorageLinkLocalService.getClassStorageLink(
					recordVersion.getDDMStorageId());

			DDMStructureVersion ddmStructureVersion =
				_ddmStructureVersionLocalService.getDDMStructureVersion(
					ddmStorageLink.getStructureVersionId());

			Map<String, DDMFormFieldRenderedValue> values = getRenderedValues(
				recordSet.getScope(), ddmFormFields.values(),
				_ddmStorageEngineManager.getDDMFormValues(
					recordVersion.getDDMStorageId(),
					ddmStructureVersion.getDDMForm()),
				_htmlParser);

			for (Map.Entry<String, DDMFormField> entry :
					ddmFormFields.entrySet()) {

				DDMFormFieldRenderedValue ddmFormFieldRenderedValue =
					values.get(entry.getKey());

				if (ddmFormFieldRenderedValue != null) {
					sb.append(
						CSVUtil.encode(ddmFormFieldRenderedValue.getValue()));
				}
				else {
					sb.append(StringPool.BLANK);
				}

				sb.append(CharPool.COMMA);
			}

			sb.append(getStatusMessage(recordVersion.getStatus()));

			sb.append(CharPool.COMMA);

			sb.append(
				formatDate(recordVersion.getStatusDate(), dateTimeFormatter));

			sb.append(CharPool.COMMA);

			sb.append(CSVUtil.encode(recordVersion.getUserName()));

			if (iterator.hasNext()) {
				sb.append(StringPool.NEW_LINE);
			}
		}

		String csv = sb.toString();

		return csv.getBytes();
	}

	@Override
	protected DDLRecordSetVersionService getDDLRecordSetVersionService() {
		return _ddlRecordSetVersionService;
	}

	@Override
	protected DDMFormFieldTypeServicesRegistry
		getDDMFormFieldTypeServicesRegistry() {

		return _ddmFormFieldTypeServicesRegistry;
	}

	@Override
	protected DDMFormFieldValueRendererRegistry
		getDDMFormFieldValueRendererRegistry() {

		return _ddmFormFieldValueRendererRegistry;
	}

	@Reference
	private DDLRecordLocalService _ddlRecordLocalService;

	@Reference
	private DDLRecordSetService _ddlRecordSetService;

	@Reference
	private DDLRecordSetVersionService _ddlRecordSetVersionService;

	@Reference
	private DDMFormFieldTypeServicesRegistry _ddmFormFieldTypeServicesRegistry;

	@Reference
	private DDMFormFieldValueRendererRegistry
		_ddmFormFieldValueRendererRegistry;

	@Reference
	private DDMStorageEngineManager _ddmStorageEngineManager;

	@Reference
	private DDMStorageLinkLocalService _ddmStorageLinkLocalService;

	@Reference
	private DDMStructureVersionLocalService _ddmStructureVersionLocalService;

	@Reference
	private HtmlParser _htmlParser;

	@Reference
	private Language _language;

}