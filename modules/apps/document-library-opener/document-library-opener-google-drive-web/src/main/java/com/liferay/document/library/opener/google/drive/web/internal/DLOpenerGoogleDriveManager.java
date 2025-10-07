/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.opener.google.drive.web.internal;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;

import com.liferay.document.library.opener.constants.DLOpenerFileEntryReferenceConstants;
import com.liferay.document.library.opener.google.drive.web.internal.background.task.UploadGoogleDriveDocumentBackgroundTaskExecutor;
import com.liferay.document.library.opener.google.drive.web.internal.constants.DLOpenerGoogleDriveConstants;
import com.liferay.document.library.opener.google.drive.web.internal.constants.GoogleDriveBackgroundTaskConstants;
import com.liferay.document.library.opener.google.drive.web.internal.oauth.OAuth2Manager;
import com.liferay.document.library.opener.model.DLOpenerFileEntryReference;
import com.liferay.document.library.opener.service.DLOpenerFileEntryReferenceLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManager;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskContextMapConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.InetAddressUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;

import java.security.GeneralSecurityException;

import java.util.Map;
import java.util.function.Supplier;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(service = DLOpenerGoogleDriveManager.class)
public class DLOpenerGoogleDriveManager {

	public DLOpenerGoogleDriveFileReference checkOut(
			long userId, FileEntry fileEntry)
		throws PortalException {

		_dlOpenerFileEntryReferenceLocalService.
			addPlaceholderDLOpenerFileEntryReference(
				userId,
				DLOpenerGoogleDriveConstants.GOOGLE_DRIVE_REFERENCE_TYPE,
				fileEntry, DLOpenerFileEntryReferenceConstants.TYPE_EDIT);

		BackgroundTask backgroundTask = _addBackgroundTask(
			GoogleDriveBackgroundTaskConstants.CHECKOUT, fileEntry, userId);

		return new DLOpenerGoogleDriveFileReference(
			fileEntry.getFileEntryId(),
			new CachingSupplier<>(
				() -> _getGoogleDriveFileTitle(userId, fileEntry)),
			() -> _getContentFile(userId, fileEntry),
			backgroundTask.getBackgroundTaskId());
	}

	public DLOpenerGoogleDriveFileReference create(
			long userId, FileEntry fileEntry)
		throws PortalException {

		_dlOpenerFileEntryReferenceLocalService.
			addPlaceholderDLOpenerFileEntryReference(
				userId,
				DLOpenerGoogleDriveConstants.GOOGLE_DRIVE_REFERENCE_TYPE,
				fileEntry, DLOpenerFileEntryReferenceConstants.TYPE_NEW);

		BackgroundTask backgroundTask = _addBackgroundTask(
			GoogleDriveBackgroundTaskConstants.CREATE, fileEntry, userId);

		return new DLOpenerGoogleDriveFileReference(
			fileEntry.getFileEntryId(),
			new CachingSupplier<>(
				() -> _getGoogleDriveFileTitle(userId, fileEntry)),
			() -> _getContentFile(userId, fileEntry),
			backgroundTask.getBackgroundTaskId());
	}

	public void delete(long userId, FileEntry fileEntry)
		throws PortalException {

		try {
			Drive drive = new Drive.Builder(
				_netHttpTransport, _jsonFactory,
				_getCredential(fileEntry.getCompanyId(), userId)
			).build();

			Drive.Files driveFiles = drive.files();

			Drive.Files.Delete driveFilesDelete = driveFiles.delete(
				_getGoogleDriveFileId(fileEntry));

			driveFilesDelete.execute();

			_dlOpenerFileEntryReferenceLocalService.
				deleteDLOpenerFileEntryReference(
					DLOpenerGoogleDriveConstants.GOOGLE_DRIVE_REFERENCE_TYPE,
					fileEntry);
		}
		catch (GoogleJsonResponseException googleJsonResponseException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"The Google Drive file does not exist",
					googleJsonResponseException);
			}

			_dlOpenerFileEntryReferenceLocalService.
				deleteDLOpenerFileEntryReference(
					DLOpenerGoogleDriveConstants.GOOGLE_DRIVE_REFERENCE_TYPE,
					fileEntry);
		}
		catch (IOException ioException) {
			throw new PortalException(ioException);
		}
	}

	public String getAuthorizationURL(
			long companyId, String state, String redirectUri)
		throws PortalException {

		return _oAuth2Manager.getAuthorizationURL(
			companyId, state, redirectUri);
	}

	public DLOpenerGoogleDriveFileReference getDLOpenerGoogleDriveFileReference(
			long userId, FileEntry fileEntry)
		throws PortalException {

		if (Validator.isNull(_getGoogleDriveFileId(fileEntry))) {
			throw new IllegalArgumentException(
				StringBundler.concat(
					"File entry ", fileEntry.getFileEntryId(),
					" is not a Google Drive file"));
		}

		_checkCredential(fileEntry.getCompanyId(), userId);

		return new DLOpenerGoogleDriveFileReference(
			fileEntry.getFileEntryId(),
			new CachingSupplier<>(
				() -> _getGoogleDriveFileTitle(userId, fileEntry)),
			() -> _getContentFile(userId, fileEntry), 0);
	}

	public boolean hasGoogleDriveFile(long userId, FileEntry fileEntry) {
		try {
			Drive drive = new Drive.Builder(
				_netHttpTransport, _jsonFactory,
				_getCredential(fileEntry.getCompanyId(), userId)
			).build();

			Drive.Files driveFiles = drive.files();

			Drive.Files.Get driveFilesGet = driveFiles.get(
				_getGoogleDriveFileId(fileEntry));

			driveFilesGet.execute();
		}
		catch (IOException | PortalException exception) {
			if (_log.isDebugEnabled()) {
				_log.debug("The Google Drive file does not exist", exception);
			}

			return false;
		}

		return true;
	}

	public boolean hasValidCredential(long companyId, long userId)
		throws IOException, PortalException {

		Credential credential = _oAuth2Manager.getCredential(companyId, userId);

		if ((credential == null) ||
			((credential.getExpiresInSeconds() <= 0) &&
			 !credential.refreshToken())) {

			return false;
		}

		return true;
	}

	public boolean isConfigured(long companyId) {
		return _oAuth2Manager.isConfigured(companyId);
	}

	public boolean isGoogleDriveFile(FileEntry fileEntry) {
		DLOpenerFileEntryReference dlOpenerFileEntryReference =
			_dlOpenerFileEntryReferenceLocalService.
				fetchDLOpenerFileEntryReference(
					DLOpenerGoogleDriveConstants.GOOGLE_DRIVE_REFERENCE_TYPE,
					fileEntry);

		if (dlOpenerFileEntryReference != null) {
			return true;
		}

		return false;
	}

	public void requestAuthorizationToken(
			long companyId, long userId, String code, String redirectUri)
		throws IOException, PortalException {

		_oAuth2Manager.requestAuthorizationToken(
			companyId, userId, code, redirectUri);
	}

	public DLOpenerGoogleDriveFileReference requestEditAccess(
			long userId, FileEntry fileEntry)
		throws PortalException {

		if (hasGoogleDriveFile(userId, fileEntry)) {
			return getDLOpenerGoogleDriveFileReference(userId, fileEntry);
		}

		_dlOpenerFileEntryReferenceLocalService.
			deleteDLOpenerFileEntryReference(
				DLOpenerGoogleDriveConstants.GOOGLE_DRIVE_REFERENCE_TYPE,
				fileEntry);

		return checkOut(userId, fileEntry);
	}

	public void setAuthorizationToken(
			long companyId, long userId, String authorizationToken)
		throws IOException, PortalException {

		_oAuth2Manager.setAccessToken(companyId, userId, authorizationToken);
	}

	@Activate
	protected void activate() throws GeneralSecurityException, IOException {
		_jsonFactory = JacksonFactory.getDefaultInstance();
		_netHttpTransport = GoogleNetHttpTransport.newTrustedTransport();
	}

	private BackgroundTask _addBackgroundTask(
			String cmd, FileEntry fileEntry, long userId)
		throws PortalException {

		Map<String, Serializable> taskContextMap =
			HashMapBuilder.<String, Serializable>put(
				BackgroundTaskContextMapConstants.DELETE_ON_SUCCESS, true
			).put(
				GoogleDriveBackgroundTaskConstants.CMD, cmd
			).put(
				GoogleDriveBackgroundTaskConstants.FILE_ENTRY_ID,
				fileEntry.getFileEntryId()
			).put(
				GoogleDriveBackgroundTaskConstants.USER_ID, userId
			).build();

		return _backgroundTaskManager.addBackgroundTask(
			userId, BackgroundTaskConstants.GROUP_ID_DEFAULT,
			StringBundler.concat(
				DLOpenerGoogleDriveManager.class.getSimpleName(),
				StringPool.POUND, fileEntry.getFileEntryId()),
			UploadGoogleDriveDocumentBackgroundTaskExecutor.class.getName(),
			taskContextMap, new ServiceContext());
	}

	private void _checkCredential(long companyId, long userId)
		throws PortalException {

		_getCredential(companyId, userId);
	}

	private File _getContentFile(long userId, FileEntry fileEntry) {
		try {
			Credential credential = _getCredential(
				fileEntry.getCompanyId(), userId);

			Drive drive = new Drive.Builder(
				_netHttpTransport, _jsonFactory, credential
			).build();

			Drive.Files driveFiles = drive.files();

			Drive.Files.Get get = driveFiles.get(
				_getGoogleDriveFileId(fileEntry));

			get.setFields("exportLinks");

			com.google.api.services.drive.model.File file = get.execute();

			Map<String, String> exportLinks = file.getExportLinks();

			URL url = new URL(exportLinks.get(fileEntry.getMimeType()));

			if (!StringUtil.startsWith(url.getProtocol(), Http.HTTP)) {
				throw new SecurityException(
					"Only HTTP links are allowed: " + url);
			}

			if (InetAddressUtil.isLocalInetAddress(
					InetAddress.getByName(url.getHost()))) {

				throw new SecurityException(
					"Local links are not allowed: " + url);
			}

			URLConnection urlConnection = url.openConnection();

			urlConnection.setRequestProperty(
				"Authorization", "Bearer " + credential.getAccessToken());

			try (InputStream inputStream = urlConnection.getInputStream()) {
				return FileUtil.createTempFile(inputStream);
			}
		}
		catch (GoogleJsonResponseException googleJsonResponseException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"The Google Drive file does not exist",
					googleJsonResponseException);
			}

			return null;
		}
		catch (IOException | PortalException exception) {
			throw new RuntimeException(exception);
		}
	}

	private Credential _getCredential(long companyId, long userId)
		throws PortalException {

		Credential credential = _oAuth2Manager.getCredential(companyId, userId);

		if (credential == null) {
			throw new PrincipalException(
				StringBundler.concat(
					"User ", userId,
					" does not have a valid Google credential"));
		}

		return credential;
	}

	private String _getGoogleDriveFileId(FileEntry fileEntry)
		throws PortalException {

		DLOpenerFileEntryReference dlOpenerFileEntryReference =
			_dlOpenerFileEntryReferenceLocalService.
				getDLOpenerFileEntryReference(
					DLOpenerGoogleDriveConstants.GOOGLE_DRIVE_REFERENCE_TYPE,
					fileEntry);

		return dlOpenerFileEntryReference.getReferenceKey();
	}

	private String _getGoogleDriveFileTitle(long userId, FileEntry fileEntry) {
		try {
			Drive drive = new Drive.Builder(
				_netHttpTransport, _jsonFactory,
				_getCredential(fileEntry.getCompanyId(), userId)
			).build();

			Drive.Files driveFiles = drive.files();

			Drive.Files.Get driveFilesGet = driveFiles.get(
				_getGoogleDriveFileId(fileEntry));

			com.google.api.services.drive.model.File file =
				driveFilesGet.execute();

			return file.getName();
		}
		catch (IOException | PortalException exception) {
			throw new RuntimeException(exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DLOpenerGoogleDriveManager.class);

	@Reference
	private BackgroundTaskManager _backgroundTaskManager;

	@Reference
	private DLOpenerFileEntryReferenceLocalService
		_dlOpenerFileEntryReferenceLocalService;

	private JsonFactory _jsonFactory;
	private NetHttpTransport _netHttpTransport;

	@Reference
	private OAuth2Manager _oAuth2Manager;

	private static class CachingSupplier<T> implements Supplier<T> {

		public CachingSupplier(Supplier<T> supplier) {
			_supplier = supplier;
		}

		@Override
		public T get() {
			if (_value != null) {
				return _value;
			}

			_value = _supplier.get();

			return _value;
		}

		private final Supplier<T> _supplier;
		private T _value;

	}

}