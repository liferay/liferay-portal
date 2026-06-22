/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.helper;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFileVersionLocalService;
import com.liferay.osb.faro.contacts.model.constants.ContactsConstants;
import com.liferay.osb.faro.engine.client.model.DataSource;
import com.liferay.osb.faro.engine.client.model.DataSourceField;
import com.liferay.osb.faro.web.internal.exception.FaroException;
import com.liferay.osb.faro.web.internal.util.CharsetDetector;
import com.liferay.petra.io.unsync.UnsyncBufferedReader;
import com.liferay.petra.io.unsync.UnsyncBufferedWriter;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import com.univocity.parsers.common.record.RecordMetaData;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shinn Lok
 */
@Component(service = ContactsCSVHelper.class)
public class ContactsCSVHelper {

	public long addContactsCSV(
			String dataSourceId, long groupId, long userId, String fileName,
			File file)
		throws Exception {

		CharsetDetector charsetDetector = new CharsetDetector();

		Charset charset = charsetDetector.detect(file);

		if (charset == null) {
			charset = StandardCharsets.UTF_8;
		}

		File tempFile = FileUtil.createTempFile();

		tempFile.deleteOnExit();

		try (Reader reader = new InputStreamReader(
				new FileInputStream(file), charset);

			UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(reader);

			Writer writer = new OutputStreamWriter(
				new FileOutputStream(tempFile), StandardCharsets.UTF_8);

			UnsyncBufferedWriter unsyncBufferedWriter =
				new UnsyncBufferedWriter(writer)) {

			List<String> previousHeaders = new ArrayList<>();

			String[] headers = StringUtil.split(
				unsyncBufferedReader.readLine());

			for (int i = 0; i < headers.length; i++) {
				unsyncBufferedWriter.write(
					getNextHeaderName(previousHeaders, headers[i]));

				if (i != (headers.length - 1)) {
					unsyncBufferedWriter.write(StringPool.COMMA);
				}

				previousHeaders.add(headers[i]);
			}

			String line = unsyncBufferedReader.readLine();

			while (line != null) {
				unsyncBufferedWriter.newLine();
				unsyncBufferedWriter.write(line);

				line = unsyncBufferedReader.readLine();
			}

			unsyncBufferedWriter.flush();
		}

		DLFileVersion dlFileVersion = addDLFileVersion(
			dataSourceId, userId, groupId, fileName, tempFile);

		return dlFileVersion.getFileVersionId();
	}

	public void deleteFileEntry(long groupId, String dataSourceId)
		throws Exception {

		Repository repository = _portletFileRepository.getPortletRepository(
			groupId, ContactsConstants.SERVICE_NAME);

		_dlFileEntryLocalService.deleteFileEntry(
			_dlFileEntryLocalService.getFileEntry(
				groupId, repository.getDlFolderId(), dataSourceId));
	}

	public List<DataSourceField> getDataSourceFields(
		long fileVersionId, String fieldName, int count, boolean skipNull) {

		Map<String, DataSourceField> dataSourceFieldMap = new LinkedHashMap<>();

		CsvParserSettings csvParserSettings = new CsvParserSettings();

		csvParserSettings.setHeaderExtractionEnabled(true);
		csvParserSettings.setLineSeparatorDetectionEnabled(true);

		if (Validator.isNotNull(fieldName)) {
			csvParserSettings.selectFields(fieldName);
		}

		CsvParser csvParser = new CsvParser(csvParserSettings);

		try {
			DLFileVersion dlFileVersion =
				_dlFileVersionLocalService.getDLFileVersion(fileVersionId);

			csvParser.beginParsing(dlFileVersion.getContentStream(false));

			RecordMetaData record = csvParser.getRecordMetadata();

			String[] headers = null;

			if (Validator.isNotNull(fieldName)) {
				headers = new String[] {fieldName};
			}
			else {
				headers = record.headers();
			}

			List<String> processedHeaders = new ArrayList<>();

			while (processedHeaders.size() != headers.length) {
				String[] values = csvParser.parseNext();

				if (values == null) {
					break;
				}

				for (int i = 0; i < headers.length; i++) {
					String header = headers[i];

					if (processedHeaders.contains(header)) {
						continue;
					}

					DataSourceField dataSourceField =
						dataSourceFieldMap.computeIfAbsent(
							header,
							key -> new DataSourceField(key, new ArrayList<>()));

					List<String> fields = dataSourceField.getValues();

					String value = values[i];

					if (!skipNull || Validator.isNotNull(value)) {
						fields.add(value);

						if (fields.size() == count) {
							processedHeaders.add(header);
						}
					}

					dataSourceFieldMap.put(header, dataSourceField);
				}
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}
		finally {
			csvParser.stopParsing();
		}

		return new ArrayList<>(dataSourceFieldMap.values());
	}

	public List<Map<String, Object>> getIndividualFieldsMaps(long fileVersionId)
		throws Exception {

		List<Map<String, Object>> individualMaps = new ArrayList<>();

		CsvParserSettings csvParserSettings = new CsvParserSettings();

		csvParserSettings.setHeaderExtractionEnabled(true);
		csvParserSettings.setLineSeparatorDetectionEnabled(true);

		CsvParser csvParser = new CsvParser(csvParserSettings);

		DLFileVersion dlFileVersion =
			_dlFileVersionLocalService.getDLFileVersion(fileVersionId);

		List<String[]> rows = csvParser.parseAll(
			dlFileVersion.getContentStream(false));

		RecordMetaData record = csvParser.getRecordMetadata();

		String[] headers = record.headers();

		for (String[] row : rows) {
			Map<String, Object> individualMap = new HashMap<>();

			for (int i = 0; i < headers.length; i++) {
				if (row[i] != null) {
					individualMap.put(headers[i], row[i]);
				}
			}

			individualMaps.add(individualMap);
		}

		return individualMaps;
	}

	public void updateFileEntry(
			long userId, long fileVersionId, String dataSourceId)
		throws Exception {

		DLFileEntry dlFileEntry = _dlFileEntryLocalService.updateStatus(
			userId, fileVersionId, WorkflowConstants.STATUS_APPROVED,
			new ServiceContext(), Collections.emptyMap());

		dlFileEntry.setTitle(dataSourceId);

		_dlFileEntryLocalService.updateDLFileEntry(dlFileEntry);
	}

	public void validateCSV(File file) {
		CsvParserSettings csvParserSettings = new CsvParserSettings();

		csvParserSettings.setHeaderExtractionEnabled(true);
		csvParserSettings.setLineSeparatorDetectionEnabled(true);

		CsvParser csvParser = new CsvParser(csvParserSettings);

		List<String[]> rows = csvParser.parseAll(file);

		RecordMetaData record = csvParser.getRecordMetadata();

		String[] headers = record.headers();

		if (rows.isEmpty() || (headers.length == 0)) {
			throw new FaroException("The CSV file is empty");
		}

		for (String[] row : rows) {
			if (row.length != headers.length) {
				throw new FaroException(
					"Not every row has the same number of columns");
			}
		}
	}

	protected DLFileVersion addDLFileVersion(
			String dataSourceId, long userId, long groupId, String fileName,
			File file)
		throws Exception {

		DLFileEntry dlFileEntry = null;

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(false);
		serviceContext.setAttribute("className", DataSource.class.getName());

		if (Validator.isNotNull(dataSourceId)) {
			dlFileEntry = _dlFileEntryLocalService.getFileEntry(
				groupId, 0, dataSourceId);

			DLFileVersion dlFileVersion = dlFileEntry.getLatestFileVersion(
				true);

			dlFileEntry = _dlFileEntryLocalService.updateFileEntry(
				userId, dlFileEntry.getFileEntryId(), fileName,
				dlFileEntry.getMimeType(), dlFileEntry.getTitle(), fileName,
				dlFileEntry.getDescription(), dlFileVersion.getChangeLog(),
				DLVersionNumberIncrease.AUTOMATIC,
				dlFileEntry.getFileEntryTypeId(),
				dlFileEntry.getDDMFormValuesMap(
					dlFileVersion.getFileVersionId()),
				file, null, file.length(), dlFileEntry.getDisplayDate(),
				dlFileEntry.getExpirationDate(), dlFileEntry.getReviewDate(),
				serviceContext);
		}
		else {
			Repository repository = _portletFileRepository.addPortletRepository(
				groupId, ContactsConstants.SERVICE_NAME, serviceContext);

			dlFileEntry = _dlFileEntryLocalService.addFileEntry(
				null, userId, groupId, repository.getRepositoryId(),
				repository.getDlFolderId(), fileName, ContentTypes.TEXT_CSV,
				file.getName(), fileName, repository.getDescription(), null,
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_ALL, null, file,
				null, file.length(), null, null, null, serviceContext);
		}

		return dlFileEntry.getLatestFileVersion(true);
	}

	protected String getNextHeaderName(List<String> headers, String header) {
		if (!headers.contains(header)) {
			return header;
		}

		for (int i = 2;; i++) {
			StringBundler sb = new StringBundler(5);

			sb.append(header);
			sb.append(StringPool.SPACE);
			sb.append(StringPool.OPEN_PARENTHESIS);
			sb.append(i);
			sb.append(StringPool.CLOSE_PARENTHESIS);

			if (!headers.contains(sb.toString())) {
				return sb.toString();
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ContactsCSVHelper.class);

	@Reference
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Reference
	private DLFileVersionLocalService _dlFileVersionLocalService;

	@Reference
	private PortletFileRepository _portletFileRepository;

}