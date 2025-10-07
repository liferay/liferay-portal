/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.opener.onedrive.web.internal.background.task;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.opener.constants.DLOpenerMimeTypes;
import com.liferay.document.library.opener.onedrive.web.internal.constants.DLOpenerOneDriveConstants;
import com.liferay.document.library.opener.onedrive.web.internal.constants.OneDriveBackgroundTaskConstants;
import com.liferay.document.library.opener.onedrive.web.internal.exception.mapper.GraphServiceExceptionPortalExceptionMapper;
import com.liferay.document.library.opener.onedrive.web.internal.graph.IAuthenticationProviderImpl;
import com.liferay.document.library.opener.onedrive.web.internal.oauth.AccessToken;
import com.liferay.document.library.opener.onedrive.web.internal.oauth.OAuth2Manager;
import com.liferay.document.library.opener.service.DLOpenerFileEntryReferenceLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskResult;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatusMessageSender;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskThreadLocal;
import com.liferay.portal.kernel.backgroundtask.BaseBackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.backgroundtask.display.BackgroundTaskDisplay;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;

import com.microsoft.graph.concurrency.ChunkedUploadProvider;
import com.microsoft.graph.concurrency.IProgressCallback;
import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.core.DefaultClientConfig;
import com.microsoft.graph.http.CustomRequest;
import com.microsoft.graph.http.GraphServiceException;
import com.microsoft.graph.models.extensions.DriveItem;
import com.microsoft.graph.models.extensions.DriveItemUploadableProperties;
import com.microsoft.graph.models.extensions.IGraphServiceClient;
import com.microsoft.graph.requests.extensions.GraphServiceClient;
import com.microsoft.graph.requests.extensions.IDriveItemCreateUploadSessionRequest;
import com.microsoft.graph.requests.extensions.IDriveItemStreamRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Uploads content to OneDrive in a background task.
 *
 * @author Cristina González
 * @review
 */
@Component(
	property = "background.task.executor.class.name=com.liferay.document.library.opener.onedrive.web.internal.background.task.UploadOneDriveDocumentBackgroundTaskExecutor",
	service = BackgroundTaskExecutor.class
)
public class UploadOneDriveDocumentBackgroundTaskExecutor
	extends BaseBackgroundTaskExecutor {

	public UploadOneDriveDocumentBackgroundTaskExecutor() {
		setBackgroundTaskStatusMessageTranslator(
			new UploadOneDriveDocumentBackgroundTaskStatusMessageTranslator());
		setIsolationLevel(BackgroundTaskConstants.ISOLATION_LEVEL_TASK_NAME);
	}

	@Override
	public BackgroundTaskExecutor clone() {
		return this;
	}

	@Override
	public BackgroundTaskResult execute(BackgroundTask backgroundTask)
		throws Exception {

		Map<String, Serializable> taskContextMap =
			backgroundTask.getTaskContextMap();

		_uploadFile(
			GetterUtil.getLong(
				taskContextMap.get(OneDriveBackgroundTaskConstants.USER_ID)),
			_dlAppLocalService.getFileEntry(
				GetterUtil.getLong(
					taskContextMap.get(
						OneDriveBackgroundTaskConstants.FILE_ENTRY_ID))),
			(Locale)taskContextMap.getOrDefault(
				OneDriveBackgroundTaskConstants.LOCALE,
				LocaleUtil.getDefault()));

		return BackgroundTaskResult.SUCCESS;
	}

	@Override
	public BackgroundTaskDisplay getBackgroundTaskDisplay(
		BackgroundTask backgroundTask) {

		return null;
	}

	@Override
	public String handleException(
		BackgroundTask backgroundTask, Exception exception) {

		Map<String, Serializable> taskContextMap =
			backgroundTask.getTaskContextMap();

		long fileEntryId = GetterUtil.getLong(
			taskContextMap.get(OneDriveBackgroundTaskConstants.FILE_ENTRY_ID));

		try {
			_dlOpenerFileEntryReferenceLocalService.
				deleteDLOpenerFileEntryReference(
					DLOpenerOneDriveConstants.ONE_DRIVE_REFERENCE_TYPE,
					_dlAppLocalService.getFileEntry(fileEntryId));
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return StringPool.BLANK;
	}

	@Reference
	protected Language language;

	private IProgressCallback _createIProgressCallback(FileEntry fileEntry) {
		return new IProgressCallback<DriveItem>() {

			@Override
			public void failure(ClientException clientException) {
				throw new RuntimeException(clientException);
			}

			@Override
			public void progress(long current, long max) {
				_sendStatusMessage(
					OneDriveBackgroundTaskConstants.PROGRESS, fileEntry,
					BackgroundTaskConstants.STATUS_IN_PROGRESS);
			}

			@Override
			public void success(DriveItem driveItem) {
			}

		};
	}

	private AccessToken _getAccessToken(long companyId, long userId)
		throws Exception {

		AccessToken accessToken = _oAuth2Manager.getAccessToken(
			companyId, userId);

		if (accessToken == null) {
			throw new PrincipalException(
				StringBundler.concat(
					"User ", userId,
					" does not have a valid OneDrive access token"));
		}

		return accessToken;
	}

	private void _sendStatusMessage(
		String phase, FileEntry fileEntry, int status) {

		Message message = new Message();

		message.put(
			BackgroundTaskConstants.MESSAGE_KEY_BACKGROUND_TASK_ID,
			BackgroundTaskThreadLocal.getBackgroundTaskId());
		message.put(
			OneDriveBackgroundTaskConstants.FILE_ENTRY_ID,
			fileEntry.getFileEntryId());
		message.put(OneDriveBackgroundTaskConstants.PHASE, phase);
		message.put("status", status);

		_backgroundTaskStatusMessageSender.sendBackgroundTaskStatusMessage(
			message);
	}

	private String _translate(Locale locale, String key) {
		return language.get(locale, key);
	}

	private void _uploadFile(long userId, FileEntry fileEntry, Locale locale)
		throws Exception {

		AccessToken accessToken = _getAccessToken(
			fileEntry.getCompanyId(), userId);

		IGraphServiceClient iGraphServiceClientBuilder =
			GraphServiceClient.fromConfig(
				DefaultClientConfig.createWithAuthenticationProvider(
					new IAuthenticationProviderImpl(accessToken)));

		CustomRequest<JsonObject> customRequest =
			iGraphServiceClientBuilder.customRequest(
				"/me/drive/root/children"
			).buildRequest();

		JsonObject jsonObject = new JsonObject();

		jsonObject.add(
			"@microsoft.graph.conflictBehavior", new JsonPrimitive("rename"));
		jsonObject.add("file", new JsonObject());
		jsonObject.add("name", new JsonPrimitive(fileEntry.getFileName()));

		JsonObject responseJsonObject = customRequest.post(jsonObject);

		JsonPrimitive jsonPrimitive = responseJsonObject.getAsJsonPrimitive(
			"id");

		if (fileEntry.getSize() > 0) {
			IDriveItemCreateUploadSessionRequest
				iDriveItemCreateUploadSessionRequest =
					iGraphServiceClientBuilder.me(
					).drive(
					).items(
						jsonPrimitive.getAsString()
					).createUploadSession(
						new DriveItemUploadableProperties()
					).buildRequest();

			ChunkedUploadProvider<DriveItem> chunkedUploadProvider =
				new ChunkedUploadProvider<>(
					iDriveItemCreateUploadSessionRequest.post(),
					iGraphServiceClientBuilder, fileEntry.getContentStream(),
					fileEntry.getSize(), DriveItem.class);

			try {
				_sendStatusMessage(
					OneDriveBackgroundTaskConstants.PORTAL_START, fileEntry,
					BackgroundTaskConstants.STATUS_IN_PROGRESS);

				chunkedUploadProvider.upload(
					null, _createIProgressCallback(fileEntry),
					new int[] {10 * 320 * 1024});

				_sendStatusMessage(
					OneDriveBackgroundTaskConstants.PORTAL_END, fileEntry,
					BackgroundTaskConstants.STATUS_IN_PROGRESS);
			}
			catch (IOException ioException) {
				throw new PortalException(ioException);
			}
		}
		else if (Objects.equals(
					DLOpenerMimeTypes.APPLICATION_VND_XLSX,
					fileEntry.getMimeType())) {

			IDriveItemStreamRequest iDriveItemStreamRequest =
				iGraphServiceClientBuilder.me(
				).drive(
				).items(
					jsonPrimitive.getAsString()
				).content(
				).buildRequest();

			try (ByteArrayOutputStream byteArrayOutputStream =
					new ByteArrayOutputStream();
				XSSFWorkbook xssfWorkbook = new XSSFWorkbook()) {

				xssfWorkbook.createSheet(
					_translate(locale, "onedrive-excel-sheet"));

				xssfWorkbook.write(byteArrayOutputStream);

				iDriveItemStreamRequest.put(
					byteArrayOutputStream.toByteArray());
			}
			catch (GraphServiceException graphServiceException) {
				throw GraphServiceExceptionPortalExceptionMapper.map(
					graphServiceException);
			}
			catch (IOException ioException) {
				throw new PortalException(ioException);
			}
		}

		_dlOpenerFileEntryReferenceLocalService.
			updateDLOpenerFileEntryReference(
				jsonPrimitive.getAsString(),
				DLOpenerOneDriveConstants.ONE_DRIVE_REFERENCE_TYPE, fileEntry);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UploadOneDriveDocumentBackgroundTaskExecutor.class);

	@Reference
	private BackgroundTaskStatusMessageSender
		_backgroundTaskStatusMessageSender;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DLOpenerFileEntryReferenceLocalService
		_dlOpenerFileEntryReferenceLocalService;

	@Reference
	private OAuth2Manager _oAuth2Manager;

}