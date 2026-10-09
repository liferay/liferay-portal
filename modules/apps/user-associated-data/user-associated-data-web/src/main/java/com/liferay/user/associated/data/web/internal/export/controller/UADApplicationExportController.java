/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.associated.data.web.internal.export.controller;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.zip.ZipReader;
import com.liferay.portal.kernel.zip.ZipReaderFactory;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.kernel.zip.ZipWriterFactory;
import com.liferay.user.associated.data.display.UADDisplay;
import com.liferay.user.associated.data.exporter.UADExporter;
import com.liferay.user.associated.data.web.internal.export.background.task.UADExportBackgroundTaskStatusMessageSender;
import com.liferay.user.associated.data.web.internal.registry.UADRegistry;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.net.URLEncoder;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(service = UADApplicationExportController.class)
public class UADApplicationExportController {

	public File export(String applicationKey, long userId) throws Exception {
		try {
			_uadExportBackgroundTaskStatusMessageSender.sendStatusMessage(
				"application", applicationKey,
				_getApplicationDataCount(applicationKey, userId));

			return _exportApplicationData(applicationKey, userId);
		}
		catch (Throwable throwable) {
			throw throwable;
		}
	}

	private File _exportApplicationData(String applicationKey, long userId)
		throws PortalException {

		ZipWriter zipWriter = _getZipWriter(applicationKey, userId);

		for (String uadRegistryKey :
				_getApplicationUADEntityRegistryKeys(applicationKey)) {

			UADExporter<?> uadExporter = _uadRegistry.getUADExporter(
				uadRegistryKey);

			File file = uadExporter.exportAll(userId, _zipWriterFactory);

			if (!file.exists()) {
				continue;
			}

			try (ZipReader zipReader = _zipReaderFactory.getZipReader(file)) {
				List<String> entries = zipReader.getEntries();

				for (String entry : entries) {
					zipWriter.addEntry(
						_getEntryPath(applicationKey, uadRegistryKey, entry),
						zipReader.getEntryAsInputStream(entry));

					_uadExportBackgroundTaskStatusMessageSender.
						sendStatusMessage("entity", uadRegistryKey);
				}
			}
			catch (IOException ioException) {
				throw new PortalException(ioException);
			}
		}

		return zipWriter.getFile();
	}

	private long _getApplicationDataCount(String applicationKey, long userId)
		throws PortalException {

		long totalCount = 0;

		for (String uadRegistryKey :
				_getApplicationUADEntityRegistryKeys(applicationKey)) {

			UADExporter<?> uadExporter = _uadRegistry.getUADExporter(
				uadRegistryKey);

			totalCount += uadExporter.getExportDataCount(userId);
		}

		return totalCount;
	}

	private List<String> _getApplicationUADEntityRegistryKeys(
		String applicationKey) {

		List<String> typeKeys = new ArrayList<>();

		for (UADDisplay<?> uadDisplay :
				_uadRegistry.getApplicationUADDisplays(applicationKey)) {

			typeKeys.add(uadDisplay.getTypeKey());
		}

		return typeKeys;
	}

	private String _getEntryPath(
		String applicationKey, String uadRegistryKey, String fileName) {

		return StringBundler.concat(
			applicationKey, StringPool.FORWARD_SLASH, uadRegistryKey,
			StringPool.FORWARD_SLASH, fileName);
	}

	private ZipWriter _getZipWriter(String applicationKey, long userId) {
		User user = _userLocalService.fetchUser(userId);

		StringBundler sb = new StringBundler(7);

		sb.append("UAD_");

		if (user != null) {
			String userName = null;

			try {
				userName = URLEncoder.encode(user.getFullName(), "UTF-8");
			}
			catch (UnsupportedEncodingException unsupportedEncodingException) {
				if (_log.isDebugEnabled()) {
					_log.debug(unsupportedEncodingException);
				}

				userName = String.valueOf(userId);
			}

			sb.append(userName);
		}
		else {
			sb.append(userId);
		}

		sb.append(StringPool.UNDERLINE);
		sb.append(applicationKey);
		sb.append(StringPool.UNDERLINE);
		sb.append(Time.getShortTimestamp());
		sb.append(".zip");

		String fileName = sb.toString();

		return _zipWriterFactory.getZipWriter(
			new File(
				SystemProperties.get(SystemProperties.TMP_DIR) +
					StringPool.SLASH + fileName));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UADApplicationExportController.class);

	@Reference
	private UADExportBackgroundTaskStatusMessageSender
		_uadExportBackgroundTaskStatusMessageSender;

	@Reference
	private UADRegistry _uadRegistry;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private ZipReaderFactory _zipReaderFactory;

	@Reference
	private ZipWriterFactory _zipWriterFactory;

}