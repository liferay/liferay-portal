/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.repository.capabilities;

import com.liferay.document.library.kernel.processor.DLProcessorHelperUtil;
import com.liferay.document.library.security.io.InputStreamSanitizer;
import com.liferay.document.library.service.DLFileVersionPreviewLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.LocalRepository;
import com.liferay.portal.kernel.repository.Repository;
import com.liferay.portal.kernel.repository.capabilities.ProcessorCapability;
import com.liferay.portal.kernel.repository.event.RepositoryEventAware;
import com.liferay.portal.kernel.repository.event.RepositoryEventType;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileEntryWrapper;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.repository.model.FileVersionWrapper;
import com.liferay.portal.kernel.repository.registry.RepositoryEventRegistry;
import com.liferay.portal.kernel.transaction.TransactionCallbackUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.repository.liferayrepository.LiferayProcessorLocalRepositoryWrapper;
import com.liferay.portal.repository.liferayrepository.LiferayProcessorRepositoryWrapper;
import com.liferay.portal.repository.util.RepositoryWrapperAware;

import java.io.InputStream;

/**
 * @author Adolfo Pérez
 */
public class LiferayProcessorCapability
	implements ProcessorCapability, RepositoryEventAware,
			   RepositoryWrapperAware {

	public LiferayProcessorCapability(
		ResourceGenerationStrategy resourceGenerationStrategy,
		DLFileVersionPreviewLocalService dlFileVersionPreviewLocalService,
		InputStreamSanitizer inputStreamSanitizer) {

		_resourceGenerationStrategy = resourceGenerationStrategy;
		_dlFileVersionPreviewLocalService = dlFileVersionPreviewLocalService;
		_inputStreamSanitizer = inputStreamSanitizer;
	}

	@Override
	public void cleanUp(FileEntry fileEntry) {
		DLProcessorHelperUtil.cleanUp(fileEntry);
	}

	@Override
	public void cleanUp(FileVersion fileVersion) {
		DLProcessorHelperUtil.cleanUp(fileVersion);
	}

	@Override
	public void copy(FileEntry fileEntry, FileVersion fileVersion) {
		if (_resourceGenerationStrategy == ResourceGenerationStrategy.REUSE) {
			_registerDLProcessorCallback(fileEntry, fileVersion);
		}
		else {
			generateNew(fileEntry);
		}
	}

	@Override
	public void generateNew(FileEntry fileEntry) {
		_registerDLProcessorCallback(fileEntry, null);
	}

	@Override
	public void registerRepositoryEventListeners(
		RepositoryEventRegistry repositoryEventRegistry) {

		repositoryEventRegistry.registerRepositoryEventListener(
			RepositoryEventType.Delete.class, FileEntry.class,
			fileEntry -> {
				_dlFileVersionPreviewLocalService.
					deleteDLFileEntryFileVersionPreviews(
						fileEntry.getFileEntryId());

				cleanUp(fileEntry);
			});
	}

	@Override
	public LocalRepository wrapLocalRepository(
		LocalRepository localRepository) {

		return new LiferayProcessorLocalRepositoryWrapper(
			localRepository, this);
	}

	@Override
	public Repository wrapRepository(Repository repository) {
		return new LiferayProcessorRepositoryWrapper(repository, this);
	}

	private void _registerDLProcessorCallback(
		FileEntry fileEntry, FileVersion fileVersion) {

		TransactionCallbackUtil.registerCommitCallback(
			() -> {
				DLProcessorHelperUtil.trigger(
					fileEntry, _wrap(fileVersion), true);

				return null;
			});
	}

	private FileVersion _wrap(FileVersion fileVersion) {
		if (fileVersion == null) {
			return null;
		}

		if (ContentTypes.IMAGE_PNG.equals(fileVersion.getMimeType())) {
			return new SafeFileVersion(fileVersion);
		}

		return fileVersion;
	}

	private final DLFileVersionPreviewLocalService
		_dlFileVersionPreviewLocalService;
	private final InputStreamSanitizer _inputStreamSanitizer;
	private final ResourceGenerationStrategy _resourceGenerationStrategy;

	private class SafeFileEntry extends FileEntryWrapper {

		public SafeFileEntry(FileEntry fileEntry) {
			super(fileEntry);
		}

		@Override
		public InputStream getContentStream() throws PortalException {
			return _inputStreamSanitizer.sanitize(super.getContentStream());
		}

		@Override
		public InputStream getContentStream(String version)
			throws PortalException {

			return _inputStreamSanitizer.sanitize(
				super.getContentStream(version));
		}

		@Override
		public FileVersion getFileVersion() throws PortalException {
			return new SafeFileVersion(super.getFileVersion());
		}

		@Override
		public FileVersion getFileVersion(String version)
			throws PortalException {

			return new SafeFileVersion(super.getFileVersion(version));
		}

		@Override
		public FileVersion getLatestFileVersion() throws PortalException {
			return new SafeFileVersion(super.getLatestFileVersion());
		}

		@Override
		public FileVersion getLatestFileVersion(boolean trusted)
			throws PortalException {

			return new SafeFileVersion(super.getLatestFileVersion(trusted));
		}

	}

	private class SafeFileVersion extends FileVersionWrapper {

		public SafeFileVersion(FileVersion fileVersion) {
			super(fileVersion);
		}

		@Override
		public InputStream getContentStream(boolean incrementCounter)
			throws PortalException {

			return _inputStreamSanitizer.sanitize(
				super.getContentStream(incrementCounter));
		}

		@Override
		public FileEntry getFileEntry() throws PortalException {
			return new SafeFileEntry(super.getFileEntry());
		}

	}

}