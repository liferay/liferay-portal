/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.contacts.demo.internal;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;

import java.io.FileInputStream;
import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.osgi.service.component.annotations.Component;

/**
 * @author Matthew Kong
 */
@Component(service = SnapshotDemoCreatorService.class)
public class SnapshotDemoCreatorService extends DemoCreatorService {

	@Override
	public void createData() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();

		try (InputStream inputStream = classLoader.getResourceAsStream(
				"/elasticsearch-snapshot-1.3.0.zip")) {

			Path tempDirectoryPath = Files.createTempDirectory("temp");

			ZipInputStream zipInputStream = new ZipInputStream(inputStream);

			ZipEntry zipEntry = zipInputStream.getNextEntry();

			int daysBetween = DateUtil.getDaysBetween(
				new Date(zipEntry.getTime()),
				new Date(System.currentTimeMillis()));

			long timeOffset = Time.DAY * daysBetween;

			List<Path> paths = new ArrayList<>();

			while (zipEntry != null) {
				if (StringUtil.endsWith(zipEntry.getName(), ".json")) {
					Path path = tempDirectoryPath.resolve(zipEntry.getName());

					Files.copy(
						zipInputStream, path,
						StandardCopyOption.REPLACE_EXISTING);

					paths.add(path);
				}

				zipEntry = zipInputStream.getNextEntry();
			}

			zipInputStream.close();

			paths.sort(
				Comparator.comparingInt(
					path -> {
						int index = _priorityFileNames.indexOf(
							String.valueOf(path.getFileName()));

						if (index == -1) {
							return Integer.MAX_VALUE;
						}

						return index;
					}));

			for (Path path : paths) {
				_processFile(path, timeOffset);

				Files.delete(path);
			}

			Files.delete(tempDirectoryPath);
		}

		contactsEngineClient.deleteData(
			faroProject, "osbasahfaroinfo", "run-logs");
	}

	private Object _addOffset(Object value, long timeOffset) {
		if (value instanceof Number) {
			long longValue = GetterUtil.getLong(value);

			if (longValue != 0) {
				return longValue + timeOffset;
			}

			return value;
		}

		if (value instanceof String) {
			try {
				String stringValue = (String)value;

				DateTimeFormatter dateTimeFormatter =
					DateTimeFormatter.ofPattern(
						com.liferay.osb.faro.util.DateUtil.PATTERN_DATE_TIME);

				LocalDateTime localDateTime = LocalDateTime.parse(
					stringValue, dateTimeFormatter);

				ZonedDateTime zonedDateTime = localDateTime.atZone(
					ZoneId.of("UTC"));

				Instant instant = zonedDateTime.toInstant();

				instant = instant.plusMillis(timeOffset);

				localDateTime = LocalDateTime.ofInstant(
					instant, ZoneId.of("UTC"));

				return localDateTime.format(dateTimeFormatter);
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}

		return value;
	}

	private void _adjustMarkers(
		String collectionName, List<Map<String, Object>> objects) {

		if (Objects.equals(collectionName, "OSBAsahMarkers")) {
			for (Map<String, Object> object : objects) {
				if (Objects.equals(object.get("id"), "Upgrade")) {
					objects.remove(object);

					break;
				}
			}
		}
	}

	private void _adjustTime(Object object, long timeOffset) {
		if (timeOffset == 0) {
			return;
		}

		if (object instanceof List) {
			for (Object childObject : (List)object) {
				_adjustTime(childObject, timeOffset);
			}
		}
		else if (object instanceof Map) {
			Map<String, Object> map = (Map<String, Object>)object;

			for (Map.Entry<String, Object> entry : map.entrySet()) {
				boolean dateField = _isDateField(
					StringUtil.toLowerCase(entry.getKey()));

				if (entry.getValue() instanceof Map ||
					entry.getValue() instanceof List) {

					if (dateField && (entry.getValue() instanceof List)) {
						entry.setValue(
							TransformUtil.transform(
								(List<String>)entry.getValue(),
								value -> _addOffset(value, timeOffset)));
					}
					else {
						_adjustTime(entry.getValue(), timeOffset);
					}
				}
				else if (dateField) {
					entry.setValue(_addOffset(entry.getValue(), timeOffset));
				}
			}
		}
	}

	private String _getCollectionName(String[] zipEntryNameParts) {
		String collectionName = zipEntryNameParts[1];

		if (collectionName.equals("osbasahmarkers")) {
			return "OSBAsahMarkers";
		}

		if (collectionName.equals("osbasahtasks")) {
			return "OSBAsahTasks";
		}

		String weDeployServiceName = zipEntryNameParts[0];

		if (weDeployServiceName.equals("osbasahsalesforceraw")) {
			if (collectionName.equals("account")) {
				return "Account";
			}

			if (collectionName.equals("contact")) {
				return "Contact";
			}

			if (collectionName.equals("lead")) {
				return "Lead";
			}
		}

		return collectionName;
	}

	private boolean _isDateField(String key) {
		if (ArrayUtil.contains(_EXCLUDE_DATE_FIELD_NAMES, key)) {
			return false;
		}

		if (key.contains("date") || key.contains("day") ||
			(key.contains("time") && !key.contains("timezone"))) {

			return true;
		}

		return false;
	}

	private void _processFile(Path path, long timeOffset) throws Exception {
		Path fileName = path.getFileName();

		if (fileName == null) {
			throw new Exception("File name is null");
		}

		String entryName = StringUtil.removeSubstring(
			fileName.toString(), ".json");

		String[] entryNameParts = StringUtil.split(
			entryName, StringPool.UNDERLINE);

		try (FileInputStream fileInputStream = new FileInputStream(
				path.toFile())) {

			List<Map<String, Object>> objects = _objectMapper.readValue(
				StringUtil.read(fileInputStream),
				new TypeReference<List<Map<String, Object>>>() {
				});

			_adjustTime(objects, timeOffset);

			String collectionName = _getCollectionName(entryNameParts);

			_adjustMarkers(collectionName, objects);

			contactsEngineClient.addData(
				faroProject, entryNameParts[0], collectionName, objects);

			if (log.isInfoEnabled()) {
				log.info(
					StringBundler.concat(
						"Created ", objects.size(), " objects in ", entryName));
			}
		}
	}

	private static final String[] _EXCLUDE_DATE_FIELD_NAMES = {
		"birthdate", "lastactivitydates"
	};

	private static final Log _log = LogFactoryUtil.getLog(
		SnapshotDemoCreatorService.class);

	private static final List<String> _priorityFileNames = Arrays.asList(
		"osbasahfaroinfo_channels_0.json",
		"osbasahfaroinfo_data-sources_0.json");

	private final ObjectMapper _objectMapper = new ObjectMapper();

}