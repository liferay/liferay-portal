/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.associated.data.web.internal.export.background.task;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManager;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskResult;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatusMessageSender;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatusMessageTranslator;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskThreadLocal;
import com.liferay.portal.kernel.backgroundtask.BaseBackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.backgroundtask.display.BackgroundTaskDisplay;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
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
import com.liferay.user.associated.data.web.internal.registry.UADRegistry;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import java.net.URLEncoder;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = "background.task.executor.class.name=com.liferay.user.associated.data.web.internal.export.background.task.UADExportBackgroundTaskExecutor",
	service = BackgroundTaskExecutor.class
)
public class UADExportBackgroundTaskExecutor
	extends BaseBackgroundTaskExecutor {

	@Override
	public BackgroundTaskExecutor clone() {
		return this;
	}

	@Override
	public BackgroundTaskResult execute(BackgroundTask backgroundTask)
		throws Exception {

		Map<String, Serializable> taskContextMap =
			backgroundTask.getTaskContextMap();

		String applicationKey = (String)taskContextMap.get("applicationKey");

		long userId = Long.valueOf(backgroundTask.getName());

		_sendStatusMessage(
			"application", applicationKey,
			_getApplicationDataCount(applicationKey, userId));

		File file = _exportApplicationData(applicationKey, userId);

		_backgroundTaskManager.addBackgroundTaskAttachment(
			backgroundTask.getUserId(), backgroundTask.getBackgroundTaskId(),
			file.getName(), file);

		return BackgroundTaskResult.SUCCESS;
	}

	@Override
	public BackgroundTaskDisplay getBackgroundTaskDisplay(
		BackgroundTask backgroundTask) {

		return null;
	}

	@Override
	public BackgroundTaskStatusMessageTranslator
		getBackgroundTaskStatusMessageTranslator() {

		if (_backgroundTaskStatusMessageTranslator == null) {
			_backgroundTaskStatusMessageTranslator =
				new UADExportBackgroundTaskStatusMessageTranslator();
		}

		return _backgroundTaskStatusMessageTranslator;
	}

	private File _exportApplicationData(String applicationKey, long userId)
		throws Exception {

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

					_sendStatusMessage("entity", uadRegistryKey);
				}
			}
			catch (IOException ioException) {
				throw new PortalException(ioException);
			}
		}

		return zipWriter.getFile();
	}

	private long _getApplicationDataCount(String applicationKey, long userId)
		throws Exception {

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

		return TransformUtil.transform(
			_uadRegistry.getApplicationUADDisplays(applicationKey),
			UADDisplay::getTypeKey);
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

	private void _sendStatusMessage(String messageType, String entityName) {
		Message message = new Message();

		message.put(
			BackgroundTaskConstants.MESSAGE_KEY_BACKGROUND_TASK_ID,
			BackgroundTaskThreadLocal.getBackgroundTaskId());
		message.put("entityName", entityName);
		message.put("messageType", messageType);

		_backgroundTaskStatusMessageSender.sendBackgroundTaskStatusMessage(
			message);
	}

	private void _sendStatusMessage(
		String messageType, String applicationKey, long total) {

		Message message = new Message();

		message.put(
			BackgroundTaskConstants.MESSAGE_KEY_BACKGROUND_TASK_ID,
			BackgroundTaskThreadLocal.getBackgroundTaskId());
		message.put("applicationDataTotal", total);
		message.put("applicationKey", applicationKey);
		message.put("messageType", messageType);

		_backgroundTaskStatusMessageSender.sendBackgroundTaskStatusMessage(
			message);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UADExportBackgroundTaskExecutor.class);

	@Reference
	private BackgroundTaskManager _backgroundTaskManager;

	@Reference
	private BackgroundTaskStatusMessageSender
		_backgroundTaskStatusMessageSender;

	private BackgroundTaskStatusMessageTranslator
		_backgroundTaskStatusMessageTranslator;

	@Reference
	private UADRegistry _uadRegistry;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private ZipReaderFactory _zipReaderFactory;

	@Reference
	private ZipWriterFactory _zipWriterFactory;

}