/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.repository.capabilities;

import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.versioning.VersionPurger;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.LocalRepository;
import com.liferay.portal.kernel.repository.Repository;
import com.liferay.portal.kernel.repository.capabilities.Capability;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.transaction.TransactionCallbackUtil;
import com.liferay.portal.repository.capabilities.util.DLAppServiceAdapter;
import com.liferay.portal.repository.util.LocalRepositoryWrapper;
import com.liferay.portal.repository.util.RepositoryWrapper;
import com.liferay.portal.repository.util.RepositoryWrapperAware;

import java.io.File;
import java.io.InputStream;

import java.util.Date;
import java.util.concurrent.Callable;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = "repository.class.name=com.liferay.portal.repository.liferayrepository.LiferayRepository",
	service = Capability.class
)
public class LiferayVersioningCapability
	implements Capability, RepositoryWrapperAware {

	@Override
	public LocalRepository wrapLocalRepository(
		LocalRepository localRepository) {

		DLAppServiceAdapter dlAppServiceAdapter = DLAppServiceAdapter.create(
			localRepository);

		return new LocalRepositoryWrapper(localRepository) {

			@Override
			public void checkInFileEntry(
					long userId, long fileEntryId,
					DLVersionNumberIncrease dlVersionNumberIncrease,
					String changeLog, ServiceContext serviceContext)
				throws PortalException {

				super.checkInFileEntry(
					userId, fileEntryId, dlVersionNumberIncrease, changeLog,
					serviceContext);

				_purgeVersions(
					dlAppServiceAdapter, super.getFileEntry(fileEntryId));
			}

			@Override
			public FileEntry updateFileEntry(
					long userId, long fileEntryId, String sourceFileName,
					String mimeType, String title, String urlTitle,
					String description, String changeLog,
					DLVersionNumberIncrease dlVersionNumberIncrease, File file,
					Date displayDate, Date expirationDate, Date reviewDate,
					ServiceContext serviceContext)
				throws PortalException {

				return _purgeVersions(
					dlAppServiceAdapter,
					super.updateFileEntry(
						userId, fileEntryId, sourceFileName, mimeType, title,
						urlTitle, description, changeLog,
						dlVersionNumberIncrease, file, displayDate,
						expirationDate, reviewDate, serviceContext));
			}

			@Override
			public FileEntry updateFileEntry(
					long userId, long fileEntryId, String sourceFileName,
					String mimeType, String title, String urlTitle,
					String description, String changeLog,
					DLVersionNumberIncrease dlVersionNumberIncrease,
					InputStream inputStream, long size, Date displayDate,
					Date expirationDate, Date reviewDate,
					ServiceContext serviceContext)
				throws PortalException {

				return _purgeVersions(
					dlAppServiceAdapter,
					super.updateFileEntry(
						userId, fileEntryId, sourceFileName, mimeType, title,
						urlTitle, description, changeLog,
						dlVersionNumberIncrease, inputStream, size, displayDate,
						expirationDate, reviewDate, serviceContext));
			}

		};
	}

	@Override
	public Repository wrapRepository(Repository repository) {
		DLAppServiceAdapter dlAppServiceAdapter = DLAppServiceAdapter.create(
			repository);

		return new RepositoryWrapper(repository) {

			@Override
			public void checkInFileEntry(
					long userId, long fileEntryId,
					DLVersionNumberIncrease dlVersionNumberIncrease,
					String changeLog, ServiceContext serviceContext)
				throws PortalException {

				super.checkInFileEntry(
					userId, fileEntryId, dlVersionNumberIncrease, changeLog,
					serviceContext);

				_purgeVersions(
					dlAppServiceAdapter, super.getFileEntry(fileEntryId));
			}

			@Override
			public FileEntry updateFileEntry(
					long userId, long fileEntryId, String sourceFileName,
					String mimeType, String title, String urlTitle,
					String description, String changeLog,
					DLVersionNumberIncrease dlVersionNumberIncrease, File file,
					Date displayDate, Date expirationDate, Date reviewDate,
					ServiceContext serviceContext)
				throws PortalException {

				return _purgeVersions(
					dlAppServiceAdapter,
					super.updateFileEntry(
						userId, fileEntryId, sourceFileName, mimeType, title,
						urlTitle, description, changeLog,
						dlVersionNumberIncrease, file, displayDate,
						expirationDate, reviewDate, serviceContext));
			}

			@Override
			public FileEntry updateFileEntry(
					long userId, long fileEntryId, String sourceFileName,
					String mimeType, String title, String urlTitle,
					String description, String changeLog,
					DLVersionNumberIncrease dlVersionNumberIncrease,
					InputStream inputStream, long size, Date displayDate,
					Date expirationDate, Date reviewDate,
					ServiceContext serviceContext)
				throws PortalException {

				return _purgeVersions(
					dlAppServiceAdapter,
					super.updateFileEntry(
						userId, fileEntryId, sourceFileName, mimeType, title,
						urlTitle, description, changeLog,
						dlVersionNumberIncrease, inputStream, size, displayDate,
						expirationDate, reviewDate, serviceContext));
			}

		};
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_versionPurgedListeners = ServiceTrackerListFactory.open(
			bundleContext, VersionPurger.VersionPurgedListener.class);
	}

	@Deactivate
	protected void deactivate() {
		_versionPurgedListeners.close();
	}

	private FileEntry _purgeVersions(
		DLAppServiceAdapter dlAppServiceAdapter, FileEntry fileEntry) {

		if ((_versionPurger == null) || fileEntry.isCheckedOut()) {
			return fileEntry;
		}

		TransactionCallbackUtil.registerCommitCallback(
			(Callable<Void>)() -> {
				for (FileVersion fileVersion :
						_versionPurger.getToPurgeFileVersions(fileEntry)) {

					for (VersionPurger.VersionPurgedListener
							versionPurgedListener : _versionPurgedListeners) {

						versionPurgedListener.versionPurged(fileVersion);
					}

					dlAppServiceAdapter.deleteFileVersion(
						fileVersion.getFileVersionId());
				}

				return null;
			});

		return fileEntry;
	}

	private ServiceTrackerList<VersionPurger.VersionPurgedListener>
		_versionPurgedListeners;

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	private volatile VersionPurger _versionPurger;

}