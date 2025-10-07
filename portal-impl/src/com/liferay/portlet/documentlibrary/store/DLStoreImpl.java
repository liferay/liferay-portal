/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.documentlibrary.store;

import com.liferay.document.library.kernel.antivirus.AntivirusScannerUtil;
import com.liferay.document.library.kernel.exception.AccessDeniedException;
import com.liferay.document.library.kernel.exception.DirectoryNameException;
import com.liferay.document.library.kernel.store.DLStore;
import com.liferay.document.library.kernel.store.DLStoreRequest;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.document.library.kernel.store.StoreArea;
import com.liferay.document.library.kernel.store.StoreAreaAwareStoreWrapper;
import com.liferay.document.library.kernel.store.StoreAreaProcessor;
import com.liferay.document.library.kernel.util.DLValidatorUtil;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.io.ByteArrayFileInputStream;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBusUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PropsValues;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Brian Wing Shun Chan
 * @author Alexander Chow
 * @author Edward Han
 * @author Raymond Augé
 */
public class DLStoreImpl implements DLStore {

	public static void setStore(Store store) {
		_wrappedStore = new StoreAreaAwareStoreWrapper(
			() -> store, _storeAreaProcessorSnapshot::get);
	}

	@Override
	public void addFile(DLStoreRequest dlStoreRequest, byte[] bytes)
		throws PortalException {

		try (DLStoreFileProvider dlStoreFileProvider = new DLStoreFileProvider(
				bytes)) {

			_validate(
				dlStoreRequest.getFileName(), null, null,
				dlStoreRequest.isValidateFileExtension(), null);

			_addFile(dlStoreRequest, dlStoreFileProvider);
		}
	}

	@Override
	public void addFile(DLStoreRequest dlStoreRequest, File file)
		throws PortalException {

		try (DLStoreFileProvider dlStoreFileProvider = new DLStoreFileProvider(
				file)) {

			_validate(
				dlStoreRequest.getFileName(), null, null,
				dlStoreRequest.isValidateFileExtension(), null);

			_addFile(dlStoreRequest, dlStoreFileProvider);
		}
	}

	@Override
	public void addFile(DLStoreRequest dlStoreRequest, InputStream inputStream)
		throws PortalException {

		try (DLStoreFileProvider dlStoreFileProvider = new DLStoreFileProvider(
				inputStream)) {

			_validate(
				dlStoreRequest.getFileName(), null, null,
				dlStoreRequest.isValidateFileExtension(), null);

			_addFile(dlStoreRequest, dlStoreFileProvider);
		}
	}

	@Override
	public void copyFileVersion(
			long companyId, long repositoryId, String fileName,
			String fromVersionLabel, String toVersionLabel)
		throws PortalException {

		if (_isStoreAreaSupported()) {
			StoreAreaProcessor storeAreaProcessor =
				_storeAreaProcessorSnapshot.get();

			StoreArea.tryRunWithStoreAreas(
				sourceStoreArea -> storeAreaProcessor.copy(
					sourceStoreArea.getPath(
						companyId, repositoryId, fileName, fromVersionLabel),
					StoreArea.NEW.getPath(
						companyId, repositoryId, fileName, toVersionLabel)),
				StoreArea.LIVE, StoreArea.NEW, StoreArea.DELETED);
		}
		else {
			_wrappedStore.addFile(
				companyId, repositoryId, fileName, toVersionLabel,
				_getNullSafeInputStream(
					_wrappedStore.getFileAsStream(
						companyId, repositoryId, fileName, fromVersionLabel)));
		}
	}

	@Override
	public void deleteDirectory(
			long companyId, long repositoryId, String dirName)
		throws PortalException {

		Message message = new Message();

		message.setValues(
			HashMapBuilder.<String, Object>put(
				"companyId", companyId
			).put(
				"dirName", dirName
			).put(
				"repositoryId", repositoryId
			).build());

		MessageBusUtil.sendMessage(
			DestinationNames.DOCUMENT_LIBRARY_DELETION, message);
	}

	@Override
	public void deleteFile(long companyId, long repositoryId, String fileName)
		throws PortalException {

		_validate(fileName, null, null, false, StringPool.BLANK);

		for (String versionLabel :
				_wrappedStore.getFileVersions(
					companyId, repositoryId, fileName)) {

			_wrappedStore.deleteFile(
				companyId, repositoryId, fileName, versionLabel);
		}
	}

	@Override
	public void deleteFile(
			long companyId, long repositoryId, String fileName,
			String versionLabel)
		throws PortalException {

		_validate(fileName, null, null, false, versionLabel);

		try {
			_wrappedStore.deleteFile(
				companyId, repositoryId, fileName, versionLabel);
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

		return _wrappedStore.getFileAsStream(
			companyId, repositoryId, fileName, versionLabel);
	}

	@Override
	public String[] getFileNames(
			long companyId, long repositoryId, String dirName)
		throws PortalException {

		if (!DLValidatorUtil.isValidName(dirName)) {
			throw new DirectoryNameException(dirName);
		}

		return _wrappedStore.getFileNames(companyId, repositoryId, dirName);
	}

	@Override
	public long getFileSize(long companyId, long repositoryId, String fileName)
		throws PortalException {

		_validate(fileName, null, null, false, null);

		return _wrappedStore.getFileSize(
			companyId, repositoryId, fileName, StringPool.BLANK);
	}

	@Override
	public boolean hasFile(
			long companyId, long repositoryId, String fileName,
			String versionLabel)
		throws PortalException {

		_validate(fileName, null, null, false, versionLabel);

		return _wrappedStore.hasFile(
			companyId, repositoryId, fileName, versionLabel);
	}

	@Override
	public void updateFile(DLStoreRequest dlStoreRequest, File file)
		throws PortalException {

		try (DLStoreFileProvider dlStoreFileProvider = new DLStoreFileProvider(
				file)) {

			_validate(
				dlStoreRequest.getFileName(), dlStoreRequest.getFileExtension(),
				dlStoreRequest.getSourceFileName(),
				dlStoreRequest.isValidateFileExtension(),
				dlStoreRequest.getVersionLabel());

			_addFile(dlStoreRequest, dlStoreFileProvider);
		}
	}

	@Override
	public void updateFile(
			DLStoreRequest dlStoreRequest, InputStream inputStream)
		throws PortalException {

		try (DLStoreFileProvider dlStoreFileProvider = new DLStoreFileProvider(
				inputStream)) {

			_validate(
				dlStoreRequest.getFileName(), dlStoreRequest.getFileExtension(),
				dlStoreRequest.getSourceFileName(),
				dlStoreRequest.isValidateFileExtension(),
				dlStoreRequest.getVersionLabel());

			_addFile(dlStoreRequest, dlStoreFileProvider);
		}
	}

	@Override
	public void updateFile(
			long companyId, long repositoryId, long newRepositoryId,
			String fileName)
		throws PortalException {

		for (String versionLabel :
				_wrappedStore.getFileVersions(
					companyId, repositoryId, fileName)) {

			if (_isStoreAreaSupported()) {
				StoreAreaProcessor storeAreaProcessor =
					_storeAreaProcessorSnapshot.get();

				StoreArea.tryRunWithStoreAreas(
					sourceStoreArea -> storeAreaProcessor.copy(
						sourceStoreArea.getPath(
							companyId, repositoryId, fileName, versionLabel),
						StoreArea.NEW.getPath(
							companyId, newRepositoryId, fileName,
							versionLabel)),
					StoreArea.LIVE, StoreArea.NEW, StoreArea.DELETED);
			}
			else {
				_wrappedStore.addFile(
					companyId, newRepositoryId, fileName, versionLabel,
					_wrappedStore.getFileAsStream(
						companyId, repositoryId, fileName, versionLabel));
			}

			_wrappedStore.deleteFile(
				companyId, repositoryId, fileName, versionLabel);
		}
	}

	@Override
	public void updateFileVersion(
			long companyId, long repositoryId, String fileName,
			String fromVersionLabel, String toVersionLabel)
		throws PortalException {

		if (_isStoreAreaSupported()) {
			StoreAreaProcessor storeAreaProcessor =
				_storeAreaProcessorSnapshot.get();

			StoreArea.tryRunWithStoreAreas(
				sourceStoreArea -> storeAreaProcessor.copy(
					sourceStoreArea.getPath(
						companyId, repositoryId, fileName, fromVersionLabel),
					StoreArea.NEW.getPath(
						companyId, repositoryId, fileName, toVersionLabel)),
				StoreArea.LIVE, StoreArea.NEW, StoreArea.DELETED);
		}
		else {
			_wrappedStore.addFile(
				companyId, repositoryId, fileName, toVersionLabel,
				_getNullSafeInputStream(
					_wrappedStore.getFileAsStream(
						companyId, repositoryId, fileName, fromVersionLabel)));
		}

		_wrappedStore.deleteFile(
			companyId, repositoryId, fileName, fromVersionLabel);
	}

	private void _addFile(
			DLStoreRequest dlStoreRequest,
			DLStoreFileProvider dlStoreFileProvider)
		throws PortalException {

		try {
			if (PropsValues.DL_STORE_ANTIVIRUS_ENABLED &&
				AntivirusScannerUtil.isActive()) {

				AntivirusScannerUtil.scan(dlStoreFileProvider.getFile());
			}

			try (InputStream inputStream =
					dlStoreFileProvider.getInputStream()) {

				_wrappedStore.addFile(
					dlStoreRequest.getCompanyId(),
					dlStoreRequest.getRepositoryId(),
					dlStoreRequest.getFileName(),
					dlStoreRequest.getVersionLabel(), inputStream);
			}
		}
		catch (AccessDeniedException accessDeniedException) {
			throw new PrincipalException(accessDeniedException);
		}
		catch (IOException ioException) {
			throw new SystemException(
				"Unable to scan file " + dlStoreRequest.getFileName(),
				ioException);
		}
	}

	private InputStream _getNullSafeInputStream(InputStream inputStream) {
		if (inputStream == null) {
			return new UnsyncByteArrayInputStream(new byte[0]);
		}

		return inputStream;
	}

	private boolean _isStoreAreaSupported() {
		if (!FeatureFlagManagerUtil.isEnabled("LPS-174816")) {
			return false;
		}

		if (_storeAreaProcessorSnapshot.get() != null) {
			return true;
		}

		return false;
	}

	private void _validate(
			String fileName, String fileExtension, String sourceFileName,
			boolean validateFileExtension, String versionLabel)
		throws PortalException {

		DLValidatorUtil.validateFileName(fileName);

		if (validateFileExtension) {
			DLValidatorUtil.validateFileExtension(fileName);
		}

		DLValidatorUtil.validateSourceFileExtension(
			fileExtension, sourceFileName);

		DLValidatorUtil.validateVersionLabel(versionLabel);
	}

	private static final Snapshot<StoreAreaProcessor>
		_storeAreaProcessorSnapshot = new Snapshot<>(
			DLStoreImpl.class, StoreAreaProcessor.class,
			"(store.type=" + PropsValues.DL_STORE_IMPL + ")");
	private static final Snapshot<Store> _storeSnapshot = new Snapshot<>(
		DLStoreImpl.class, Store.class, "(default=true)", true);
	private static Store _wrappedStore = new StoreAreaAwareStoreWrapper(
		_storeSnapshot::get, _storeAreaProcessorSnapshot::get);

	private static class DLStoreFileProvider implements SafeCloseable {

		public DLStoreFileProvider(byte[] bytes) {
			this(bytes, null, null);
		}

		public DLStoreFileProvider(File file) {
			this(null, file, null);
		}

		public DLStoreFileProvider(InputStream inputStream) {
			this(null, null, inputStream);
		}

		@Override
		public void close() {
			if (_tempFile != null) {
				_tempFile.delete();
			}
		}

		public File getFile() throws IOException {
			if (_file != null) {
				return _file;
			}

			if (_tempFile != null) {
				return _tempFile;
			}

			if (_bytes != null) {
				_tempFile = FileUtil.createTempFile(_bytes);
			}
			else {
				_tempFile = FileUtil.createTempFile(_inputStream);
			}

			return _tempFile;
		}

		public InputStream getInputStream() throws FileNotFoundException {
			if (_file != null) {
				return new FileInputStream(_file);
			}

			if (_tempFile != null) {
				return new FileInputStream(_tempFile);
			}

			if (_bytes != null) {
				return new UnsyncByteArrayInputStream(_bytes);
			}

			return _inputStream;
		}

		private DLStoreFileProvider(
			byte[] bytes, File file, InputStream inputStream) {

			if (inputStream instanceof ByteArrayFileInputStream) {
				_bytes = null;

				ByteArrayFileInputStream byteArrayFileInputStream =
					(ByteArrayFileInputStream)inputStream;

				_file = byteArrayFileInputStream.getFile();

				_inputStream = null;
			}
			else {
				_bytes = bytes;
				_file = file;
				_inputStream = inputStream;
			}
		}

		private final byte[] _bytes;
		private final File _file;
		private final InputStream _inputStream;
		private File _tempFile;

	}

}