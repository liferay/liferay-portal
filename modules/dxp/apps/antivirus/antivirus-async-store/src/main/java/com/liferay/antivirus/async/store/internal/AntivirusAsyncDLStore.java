/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.antivirus.async.store.internal;

import com.liferay.antivirus.async.store.constants.AntivirusAsyncDestinationNames;
import com.liferay.antivirus.async.store.internal.event.AntivirusAsyncEventListenerManager;
import com.liferay.antivirus.async.store.util.AntivirusAsyncUtil;
import com.liferay.document.library.kernel.exception.AccessDeniedException;
import com.liferay.document.library.kernel.exception.DirectoryNameException;
import com.liferay.document.library.kernel.store.DLStore;
import com.liferay.document.library.kernel.store.DLStoreRequest;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.document.library.kernel.util.DLValidator;
import com.liferay.petra.io.ByteArrayFileInputStream;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.transaction.TransactionCallbackUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Raymond Augé
 */
@Component(
	configurationPid = "com.liferay.antivirus.async.store.configuration.AntivirusAsyncConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	property = "service.ranking:Integer=1000", service = DLStore.class
)
public class AntivirusAsyncDLStore implements DLStore {

	@Override
	public void addFile(DLStoreRequest dlStoreRequest, byte[] bytes)
		throws PortalException {

		_validate(
			dlStoreRequest.getFileName(), null, null,
			dlStoreRequest.isValidateFileExtension(), null);

		try {
			_store.addFile(
				dlStoreRequest.getCompanyId(), dlStoreRequest.getRepositoryId(),
				dlStoreRequest.getFileName(), dlStoreRequest.getVersionLabel(),
				new UnsyncByteArrayInputStream(bytes));

			_registerCallback(dlStoreRequest);
		}
		catch (AccessDeniedException accessDeniedException) {
			throw new PrincipalException(accessDeniedException);
		}
	}

	@Override
	public void addFile(DLStoreRequest dlStoreRequest, File file)
		throws PortalException {

		_validate(
			dlStoreRequest.getFileName(), null, null,
			dlStoreRequest.isValidateFileExtension(), null);

		try (InputStream inputStream = new FileInputStream(file)) {
			_store.addFile(
				dlStoreRequest.getCompanyId(), dlStoreRequest.getRepositoryId(),
				dlStoreRequest.getFileName(), dlStoreRequest.getVersionLabel(),
				inputStream);

			_registerCallback(dlStoreRequest);
		}
		catch (AccessDeniedException accessDeniedException) {
			throw new PrincipalException(accessDeniedException);
		}
		catch (IOException ioException) {
			throw new SystemException(ioException);
		}
	}

	@Override
	public void addFile(DLStoreRequest dlStoreRequest, InputStream inputStream1)
		throws PortalException {

		if (inputStream1 instanceof ByteArrayFileInputStream) {
			ByteArrayFileInputStream byteArrayFileInputStream =
				(ByteArrayFileInputStream)inputStream1;

			addFile(dlStoreRequest, byteArrayFileInputStream.getFile());

			return;
		}

		_validate(
			dlStoreRequest.getFileName(), null, null,
			dlStoreRequest.isValidateFileExtension(), null);

		File tempFile = null;

		try {
			tempFile = _file.createTempFile();

			_file.write(tempFile, inputStream1);

			try (InputStream inputStream2 = new FileInputStream(tempFile)) {
				_store.addFile(
					dlStoreRequest.getCompanyId(),
					dlStoreRequest.getRepositoryId(),
					dlStoreRequest.getFileName(),
					dlStoreRequest.getVersionLabel(), inputStream2);
			}

			_registerCallback(dlStoreRequest);
		}
		catch (AccessDeniedException accessDeniedException) {
			throw new PrincipalException(accessDeniedException);
		}
		catch (IOException ioException) {
			throw new SystemException(
				"Unable to scan file " + dlStoreRequest.getFileName(),
				ioException);
		}
		finally {
			if (tempFile != null) {
				tempFile.delete();
			}
		}
	}

	@Override
	public void copyFileVersion(
			DLStoreRequest dlStoreRequest, String toVersionLabel)
		throws PortalException {

		InputStream inputStream = _store.getFileAsStream(
			dlStoreRequest.getCompanyId(), dlStoreRequest.getRepositoryId(),
			dlStoreRequest.getFileName(), dlStoreRequest.getVersionLabel());

		if (inputStream == null) {
			inputStream = new UnsyncByteArrayInputStream(new byte[0]);
		}

		addFile(
			DLStoreRequest.builder(
				dlStoreRequest.getCompanyId(), dlStoreRequest.getRepositoryId(),
				dlStoreRequest.getFileName()
			).className(
				dlStoreRequest.getClassName()
			).classPK(
				dlStoreRequest.getClassPK()
			).fileExtension(
				dlStoreRequest.getFileExtension()
			).sourceFileName(
				dlStoreRequest.getSourceFileName()
			).validateFileExtension(
				false
			).versionLabel(
				toVersionLabel
			).build(),
			inputStream);
	}

	@Override
	public void copyFileVersion(
			long companyId, long repositoryId, String fileName,
			String fromVersionLabel, String toVersionLabel)
		throws PortalException {

		InputStream inputStream = _store.getFileAsStream(
			companyId, repositoryId, fileName, fromVersionLabel);

		if (inputStream == null) {
			inputStream = new UnsyncByteArrayInputStream(new byte[0]);
		}

		_store.addFile(
			companyId, repositoryId, fileName, toVersionLabel, inputStream);
	}

	@Override
	public void deleteDirectory(
		long companyId, long repositoryId, String dirName) {

		_store.deleteDirectory(companyId, repositoryId, dirName);
	}

	@Override
	public void deleteFile(long companyId, long repositoryId, String fileName)
		throws PortalException {

		_validate(fileName, null, null, false, StringPool.BLANK);

		for (String versionLabel :
				_store.getFileVersions(companyId, repositoryId, fileName)) {

			_store.deleteFile(companyId, repositoryId, fileName, versionLabel);
		}
	}

	@Override
	public void deleteFile(
			long companyId, long repositoryId, String fileName,
			String versionLabel)
		throws PortalException {

		_validate(fileName, null, null, false, versionLabel);

		try {
			_store.deleteFile(companyId, repositoryId, fileName, versionLabel);
		}
		catch (AccessDeniedException accessDeniedException) {
			throw new PrincipalException(accessDeniedException);
		}
	}

	@Override
	public InputStream getFileAsStream(
			long companyId, long repositoryId, String fileName,
			String versionLabel)
		throws PortalException {

		_validate(fileName, null, null, false, versionLabel);

		return _store.getFileAsStream(
			companyId, repositoryId, fileName, versionLabel);
	}

	@Override
	public String[] getFileNames(
			long companyId, long repositoryId, String dirName)
		throws PortalException {

		if (!_dlValidator.isValidName(dirName)) {
			throw new DirectoryNameException(dirName);
		}

		return _store.getFileNames(companyId, repositoryId, dirName);
	}

	@Override
	public long getFileSize(long companyId, long repositoryId, String fileName)
		throws PortalException {

		_validate(fileName, null, null, false, null);

		return _store.getFileSize(
			companyId, repositoryId, fileName, StringPool.BLANK);
	}

	@Override
	public boolean hasFile(
			long companyId, long repositoryId, String fileName,
			String versionLabel)
		throws PortalException {

		_validate(fileName, null, null, false, versionLabel);

		return _store.hasFile(companyId, repositoryId, fileName, versionLabel);
	}

	@Override
	public void updateFile(DLStoreRequest dlStoreRequest, File file)
		throws PortalException {

		_validate(
			dlStoreRequest.getFileName(), dlStoreRequest.getFileExtension(),
			dlStoreRequest.getSourceFileName(),
			dlStoreRequest.isValidateFileExtension(),
			dlStoreRequest.getVersionLabel());

		try (InputStream inputStream = new FileInputStream(file)) {
			_store.addFile(
				dlStoreRequest.getCompanyId(), dlStoreRequest.getRepositoryId(),
				dlStoreRequest.getFileName(), dlStoreRequest.getVersionLabel(),
				inputStream);

			_registerCallback(dlStoreRequest);
		}
		catch (AccessDeniedException accessDeniedException) {
			throw new PrincipalException(accessDeniedException);
		}
		catch (IOException ioException) {
			throw new SystemException(ioException);
		}
	}

	@Override
	public void updateFile(
			DLStoreRequest dlStoreRequest, InputStream inputStream)
		throws PortalException {

		_validate(
			dlStoreRequest.getFileName(), dlStoreRequest.getFileExtension(),
			dlStoreRequest.getSourceFileName(),
			dlStoreRequest.isValidateFileExtension(),
			dlStoreRequest.getVersionLabel());

		try {
			_store.addFile(
				dlStoreRequest.getCompanyId(), dlStoreRequest.getRepositoryId(),
				dlStoreRequest.getFileName(), dlStoreRequest.getVersionLabel(),
				inputStream);

			_registerCallback(dlStoreRequest);
		}
		catch (AccessDeniedException accessDeniedException) {
			throw new PrincipalException(accessDeniedException);
		}
	}

	@Override
	public void updateFile(
			long companyId, long repositoryId, long newRepositoryId,
			String fileName)
		throws PortalException {

		for (String versionLabel :
				_store.getFileVersions(companyId, repositoryId, fileName)) {

			_store.addFile(
				companyId, newRepositoryId, fileName, versionLabel,
				_store.getFileAsStream(
					companyId, repositoryId, fileName, versionLabel));

			_store.deleteFile(companyId, repositoryId, fileName, versionLabel);
		}
	}

	@Override
	public void updateFileVersion(
			long companyId, long repositoryId, String fileName,
			String fromVersionLabel, String toVersionLabel)
		throws PortalException {

		InputStream inputStream = _store.getFileAsStream(
			companyId, repositoryId, fileName, fromVersionLabel);

		if (inputStream == null) {
			inputStream = new UnsyncByteArrayInputStream(new byte[0]);
		}

		_store.addFile(
			companyId, repositoryId, fileName, toVersionLabel, inputStream);

		_store.deleteFile(companyId, repositoryId, fileName, fromVersionLabel);
	}

	private void _registerCallback(DLStoreRequest dlStoreRequest)
		throws PortalException {

		Message message = new Message();

		message.put("className", dlStoreRequest.getClassName());
		message.put("classPK", dlStoreRequest.getClassPK());
		message.put("companyId", dlStoreRequest.getCompanyId());
		message.put("entryURL", dlStoreRequest.getEntryURL());
		message.put("fileExtension", dlStoreRequest.getFileExtension());
		message.put("fileName", dlStoreRequest.getFileName());
		message.put(
			"jobName",
			AntivirusAsyncUtil.getJobName(
				dlStoreRequest.getCompanyId(), dlStoreRequest.getRepositoryId(),
				dlStoreRequest.getFileName(),
				dlStoreRequest.getVersionLabel()));
		message.put("repositoryId", dlStoreRequest.getRepositoryId());
		message.put("size", dlStoreRequest.getSize());
		message.put("sourceFileName", dlStoreRequest.getSourceFileName());

		long userId = 0;

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (permissionChecker != null) {
			userId = permissionChecker.getUserId();
		}

		message.put("userId", userId);

		message.put("versionLabel", dlStoreRequest.getVersionLabel());

		_antivirusAsyncEventListenerManager.onPrepare(message);

		TransactionCallbackUtil.registerCommitCallback(
			() -> {
				_messageBus.sendMessage(
					AntivirusAsyncDestinationNames.ANTIVIRUS, message);

				return null;
			});
	}

	private void _validate(
			String fileName, String fileExtension, String sourceFileName,
			boolean validateFileExtension, String versionLabel)
		throws PortalException {

		_dlValidator.validateFileName(fileName);

		if (validateFileExtension) {
			_dlValidator.validateFileExtension(fileName);
		}

		_dlValidator.validateSourceFileExtension(fileExtension, sourceFileName);

		_dlValidator.validateVersionLabel(versionLabel);
	}

	@Reference
	private AntivirusAsyncEventListenerManager
		_antivirusAsyncEventListenerManager;

	@Reference
	private DLValidator _dlValidator;

	@Reference
	private com.liferay.portal.kernel.util.File _file;

	@Reference
	private MessageBus _messageBus;

	@Reference(target = "(default=true)")
	private Store _store;

}