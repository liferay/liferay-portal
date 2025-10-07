/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.opener.google.drive.web.internal.background.task;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;

import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.opener.google.drive.web.internal.constants.DLOpenerGoogleDriveConstants;
import com.liferay.document.library.opener.google.drive.web.internal.constants.DLOpenerGoogleDriveMimeTypes;
import com.liferay.document.library.opener.google.drive.web.internal.constants.GoogleDriveBackgroundTaskConstants;
import com.liferay.document.library.opener.google.drive.web.internal.oauth.OAuth2Manager;
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
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Uploads content to Google Drive in a background task.
 *
 * @author Sergio González
 */
@Component(
	property = "background.task.executor.class.name=com.liferay.document.library.opener.google.drive.web.internal.background.task.UploadGoogleDriveDocumentBackgroundTaskExecutor",
	service = BackgroundTaskExecutor.class
)
public class UploadGoogleDriveDocumentBackgroundTaskExecutor
	extends BaseBackgroundTaskExecutor {

	public UploadGoogleDriveDocumentBackgroundTaskExecutor() {
		setBackgroundTaskStatusMessageTranslator(
			new UploadGoogleDriveDocumentBackgroundTaskStatusMessageTranslator());
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

		long fileEntryId = GetterUtil.getLong(
			taskContextMap.get(
				GoogleDriveBackgroundTaskConstants.FILE_ENTRY_ID));

		_sendStatusMessage(
			GoogleDriveBackgroundTaskConstants.PORTAL_START,
			backgroundTask.getCompanyId(), fileEntryId);

		String cmd = (String)taskContextMap.get(
			GoogleDriveBackgroundTaskConstants.CMD);

		long userId = GetterUtil.getLong(
			taskContextMap.get(GoogleDriveBackgroundTaskConstants.USER_ID));

		if (cmd.equals(GoogleDriveBackgroundTaskConstants.CHECKOUT)) {
			_uploadGoogleDriveDocument(
				backgroundTask.getCompanyId(), fileEntryId, userId, true);
		}
		else {
			_uploadGoogleDriveDocument(
				backgroundTask.getCompanyId(), fileEntryId, userId, false);
		}

		_sendStatusMessage(
			GoogleDriveBackgroundTaskConstants.PORTAL_END,
			backgroundTask.getCompanyId(), fileEntryId);

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
			taskContextMap.get(
				GoogleDriveBackgroundTaskConstants.FILE_ENTRY_ID));

		try {
			_dlOpenerFileEntryReferenceLocalService.
				deleteDLOpenerFileEntryReference(
					DLOpenerGoogleDriveConstants.GOOGLE_DRIVE_REFERENCE_TYPE,
					_dlAppLocalService.getFileEntry(fileEntryId));
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return StringPool.BLANK;
	}

	private Credential _getCredential(long companyId, long userId)
		throws Exception {

		Credential credential = _oAuth2Manager.getCredential(companyId, userId);

		if (credential == null) {
			throw new PrincipalException(
				StringBundler.concat(
					"User ", userId,
					" does not have a valid Google credential"));
		}

		return credential;
	}

	private File _getFileEntryFile(FileVersion fileVersion) throws Exception {
		try (InputStream inputStream = fileVersion.getContentStream(false)) {
			return FileUtil.createTempFile(inputStream);
		}
	}

	private void _sendStatusMessage(
		String phase, long companyId, long fileEntryId) {

		Message message = new Message();

		message.put(
			BackgroundTaskConstants.MESSAGE_KEY_BACKGROUND_TASK_ID,
			BackgroundTaskThreadLocal.getBackgroundTaskId());
		message.put(GoogleDriveBackgroundTaskConstants.COMPANY_ID, companyId);
		message.put(
			GoogleDriveBackgroundTaskConstants.FILE_ENTRY_ID, fileEntryId);
		message.put(GoogleDriveBackgroundTaskConstants.PHASE, phase);
		message.put("status", BackgroundTaskConstants.STATUS_IN_PROGRESS);

		_backgroundTaskStatusMessageSender.sendBackgroundTaskStatusMessage(
			message);
	}

	private void _uploadGoogleDriveDocument(
			long companyId, long fileEntryId, long userId, boolean add)
		throws Exception {

		com.google.api.services.drive.model.File file =
			new com.google.api.services.drive.model.File();

		FileEntry fileEntry = _dlAppLocalService.getFileEntry(fileEntryId);

		FileVersion fileVersion = fileEntry.getLatestFileVersion();

		file.setMimeType(
			DLOpenerGoogleDriveMimeTypes.getGoogleDocsMimeType(
				fileVersion.getMimeType()));
		file.setName(fileVersion.getTitle());

		Drive drive = new Drive.Builder(
			GoogleNetHttpTransport.newTrustedTransport(),
			JacksonFactory.getDefaultInstance(),
			_getCredential(companyId, userId)
		).build();

		Drive.Files driveFiles = drive.files();

		Drive.Files.Create driveFilesCreate = null;

		if (add) {
			FileContent fileContent = new FileContent(
				fileVersion.getMimeType(), _getFileEntryFile(fileVersion));

			driveFilesCreate = driveFiles.create(file, fileContent);

			MediaHttpUploader mediaHttpUploader =
				driveFilesCreate.getMediaHttpUploader();

			long backgroundTaskId =
				BackgroundTaskThreadLocal.getBackgroundTaskId();

			mediaHttpUploader.setProgressListener(
				curMediaHttpUploader -> {
					Message message = new Message();

					message.put(
						BackgroundTaskConstants.MESSAGE_KEY_BACKGROUND_TASK_ID,
						backgroundTaskId);

					message.put(
						"uploadState", curMediaHttpUploader.getUploadState());
					message.put(
						"status", BackgroundTaskConstants.STATUS_IN_PROGRESS);

					_backgroundTaskStatusMessageSender.
						sendBackgroundTaskStatusMessage(message);
				});
		}
		else {
			driveFilesCreate = driveFiles.create(file);
		}

		com.google.api.services.drive.model.File uploadedFile =
			driveFilesCreate.execute();

		_dlOpenerFileEntryReferenceLocalService.
			updateDLOpenerFileEntryReference(
				uploadedFile.getId(),
				DLOpenerGoogleDriveConstants.GOOGLE_DRIVE_REFERENCE_TYPE,
				fileEntry);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UploadGoogleDriveDocumentBackgroundTaskExecutor.class);

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