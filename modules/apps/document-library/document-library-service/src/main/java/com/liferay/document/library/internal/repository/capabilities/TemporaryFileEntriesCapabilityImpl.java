/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.repository.capabilities;

import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalServiceUtil;
import com.liferay.document.library.kernel.exception.NoSuchFolderException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppHelperLocalServiceUtil;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.change.tracking.CTModel;
import com.liferay.portal.kernel.repository.DocumentRepository;
import com.liferay.portal.kernel.repository.capabilities.BulkOperationCapability;
import com.liferay.portal.kernel.repository.capabilities.ConfigurationCapability;
import com.liferay.portal.kernel.repository.capabilities.TemporaryFileEntriesCapability;
import com.liferay.portal.kernel.repository.capabilities.TemporaryFileEntriesScope;
import com.liferay.portal.kernel.repository.model.BaseRepositoryModelOperation;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.systemevent.SystemEventHierarchyEntryThreadLocal;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author Iván Zaera
 */
public class TemporaryFileEntriesCapabilityImpl
	implements TemporaryFileEntriesCapability {

	public TemporaryFileEntriesCapabilityImpl(
		DocumentRepository documentRepository) {

		_documentRepository = documentRepository;
	}

	@Override
	public FileEntry addTemporaryFileEntry(
			TemporaryFileEntriesScope temporaryFileEntriesScope,
			String fileName, String mimeType, InputStream inputStream)
		throws PortalException {

		return addTemporaryFileEntry(
			temporaryFileEntriesScope, null, fileName, mimeType, inputStream);
	}

	@Override
	public FileEntry addTemporaryFileEntry(
			TemporaryFileEntriesScope temporaryFileEntriesScope,
			String externalReferenceCode, String fileName, String mimeType,
			InputStream inputStream)
		throws PortalException {

		Folder folder = _addTempFolder(temporaryFileEntriesScope);

		File file = null;

		try {
			if (inputStream == null) {
				inputStream = new UnsyncByteArrayInputStream(new byte[0]);
			}

			file = FileUtil.createTempFile(inputStream);

			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setAddGroupPermissions(true);
			serviceContext.setAddGuestPermissions(true);

			return _documentRepository.addFileEntry(
				externalReferenceCode, temporaryFileEntriesScope.getUserId(),
				folder.getFolderId(), fileName, mimeType, fileName, fileName,
				StringPool.BLANK, StringPool.BLANK, file, null, null, null,
				serviceContext);
		}
		catch (IOException ioException) {
			throw new SystemException(
				"Unable to write temporary file", ioException);
		}
		finally {
			FileUtil.delete(file);
		}
	}

	@Override
	public void deleteExpiredTemporaryFileEntries() throws PortalException {
		Folder folder = _documentRepository.getFolder(
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		for (CTCollection ctCollection :
				_getPublicationCTCollections(folder.getCompanyId())) {

			_deleteExpiredTemporaryFileEntries(
				ctCollection.getCtCollectionId());
		}

		_deleteExpiredTemporaryFileEntries(
			CTCollectionThreadLocal.CT_COLLECTION_ID_PRODUCTION);
	}

	@Override
	public void deleteTemporaryFileEntry(
			TemporaryFileEntriesScope temporaryFileEntriesScope,
			String fileName)
		throws PortalException {

		_runWithoutSystemEvents(
			() -> {
				try {
					FileEntry fileEntry = getTemporaryFileEntry(
						temporaryFileEntriesScope, fileName);

					_documentRepository.deleteFileEntry(
						fileEntry.getFileEntryId());
				}
				catch (NoSuchModelException noSuchModelException) {

					// LPS-52675

					if (_log.isDebugEnabled()) {
						_log.debug(noSuchModelException);
					}
				}

				return null;
			});
	}

	@Override
	public List<FileEntry> getTemporaryFileEntries(
			TemporaryFileEntriesScope temporaryFileEntriesScope)
		throws PortalException {

		try {
			Folder folder = _addTempFolder(temporaryFileEntriesScope);

			return _documentRepository.getRepositoryFileEntries(
				temporaryFileEntriesScope.getUserId(), folder.getFolderId(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
		}
		catch (NoSuchModelException noSuchModelException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(noSuchModelException);
			}

			return Collections.emptyList();
		}
	}

	@Override
	public long getTemporaryFileEntriesTimeout() {
		ConfigurationCapability configurationCapability =
			_documentRepository.getCapability(ConfigurationCapability.class);

		String temporaryFileEntriesTimeout =
			configurationCapability.getProperty(
				getClass(), _PROPERTY_TEMPORARY_FILE_ENTRIES_TIMEOUT);

		if (temporaryFileEntriesTimeout == null) {
			return _TEMPORARY_FILE_ENTRIES_TIMEOUT_DEFAULT;
		}

		return GetterUtil.getLong(temporaryFileEntriesTimeout);
	}

	@Override
	public FileEntry getTemporaryFileEntry(
			TemporaryFileEntriesScope temporaryFileEntriesScope,
			String fileName)
		throws PortalException {

		Folder folder = _getTempFolder(temporaryFileEntriesScope);

		return _documentRepository.getFileEntry(folder.getFolderId(), fileName);
	}

	@Override
	public void setTemporaryFileEntriesTimeout(
		long temporaryFileEntriesTimeout) {

		ConfigurationCapability configurationCapability =
			_documentRepository.getCapability(ConfigurationCapability.class);

		configurationCapability.setProperty(
			getClass(), _PROPERTY_TEMPORARY_FILE_ENTRIES_TIMEOUT,
			String.valueOf(temporaryFileEntriesTimeout));
	}

	private Folder _addFolder(
			long userId, long parentFolderId, String folderName,
			ServiceContext serviceContext)
		throws PortalException {

		try {
			return _documentRepository.getFolder(parentFolderId, folderName);
		}
		catch (NoSuchFolderException noSuchFolderException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(noSuchFolderException);
			}

			return _documentRepository.addFolder(
				null, userId, parentFolderId, folderName, StringPool.BLANK,
				serviceContext);
		}
	}

	private Folder _addFolders(
			long userId, long folderId, String folderPath,
			ServiceContext serviceContext)
		throws PortalException {

		Folder folder = null;

		String[] folderNames = StringUtil.split(folderPath, StringPool.SLASH);

		for (String folderName : folderNames) {
			folder = _addFolder(userId, folderId, folderName, serviceContext);

			folderId = folder.getFolderId();
		}

		return folder;
	}

	private Folder _addTempFolder(
			TemporaryFileEntriesScope temporaryFileEntriesScope)
		throws PortalException {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);

		return _addFolders(
			temporaryFileEntriesScope.getUserId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			_getFolderPath(temporaryFileEntriesScope), serviceContext);
	}

	private void _deleteExpiredTemporaryFileEntries(long ctCollectionId) {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			BulkOperationCapability bulkOperationCapability =
				_documentRepository.getCapability(
					BulkOperationCapability.class);

			BulkOperationCapability.Filter<Date> bulkFilter =
				new BulkOperationCapability.Filter<>(
					BulkOperationCapability.Field.CreateDate.class,
					BulkOperationCapability.Operator.LT,
					new Date(
						System.currentTimeMillis() -
							getTemporaryFileEntriesTimeout()));

			_runWithoutSystemEvents(
				() -> {
					bulkOperationCapability.execute(
						bulkFilter,
						new DeleteExpiredTemporaryFilesRepositoryModelOperation());

					return null;
				});
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to delete expired temporary file entries in " +
						"change tracking collection " + ctCollectionId,
					portalException);
			}
		}
	}

	private Folder _getDeepestFolder(long parentFolderId, String folderPath)
		throws PortalException {

		Folder folder = null;

		String[] folderNames = StringUtil.split(folderPath, StringPool.SLASH);

		for (String folderName : folderNames) {
			folder = _documentRepository.getFolder(parentFolderId, folderName);

			parentFolderId = folder.getFolderId();
		}

		return folder;
	}

	private String _getFolderPath(
		TemporaryFileEntriesScope temporaryFileEntriesScope) {

		return StringBundler.concat(
			_FOLDER_NAME_TEMP, StringPool.SLASH,
			temporaryFileEntriesScope.getCallerUuid(), StringPool.SLASH,
			temporaryFileEntriesScope.getUserId(), StringPool.SLASH,
			temporaryFileEntriesScope.getFolderPath());
	}

	private List<CTCollection> _getPublicationCTCollections(long companyId) {
		return CTCollectionLocalServiceUtil.getCTCollections(
			companyId,
			new int[] {
				WorkflowConstants.STATUS_DRAFT,
				WorkflowConstants.STATUS_SCHEDULED
			},
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	private Folder _getTempFolder(
			TemporaryFileEntriesScope temporaryFileEntriesScope)
		throws PortalException {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);

		return _getDeepestFolder(
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			_getFolderPath(temporaryFileEntriesScope));
	}

	private boolean _hasPublicationFileEntries(long companyId, long folderId)
		throws PortalException {

		for (CTCollection ctCollection :
				_getPublicationCTCollections(companyId)) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						ctCollection.getCtCollectionId())) {

				int count = _documentRepository.getFileEntriesCount(
					folderId, WorkflowConstants.STATUS_ANY);

				if (count != 0) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean _isOwnedByCurrentCTCollection(Object model) {
		if (!(model instanceof CTModel<?> ctModel) ||
			(ctModel.getCtCollectionId() ==
				CTCollectionThreadLocal.getCTCollectionId())) {

			return true;
		}

		return false;
	}

	private void _runWithoutSystemEvents(
			UnsafeSupplier<Void, PortalException> unsafeSupplier)
		throws PortalException {

		SystemEventHierarchyEntryThreadLocal.push(DLFileEntry.class);

		try {
			unsafeSupplier.get();
		}
		finally {
			SystemEventHierarchyEntryThreadLocal.pop(DLFileEntry.class);
		}
	}

	private static final String _FOLDER_NAME_TEMP = "temp";

	private static final String _PROPERTY_TEMPORARY_FILE_ENTRIES_TIMEOUT =
		"temporaryFilesTimeout";

	private static final long _TEMPORARY_FILE_ENTRIES_TIMEOUT_DEFAULT =
		12 * 60 * 60 * 1000;

	private static final Log _log = LogFactoryUtil.getLog(
		TemporaryFileEntriesCapabilityImpl.class);

	private final DocumentRepository _documentRepository;

	private class DeleteExpiredTemporaryFilesRepositoryModelOperation
		extends BaseRepositoryModelOperation {

		@Override
		public void execute(FileEntry fileEntry) throws PortalException {
			if (!_isOwnedByCurrentCTCollection(fileEntry.getModel())) {
				return;
			}

			DLAppHelperLocalServiceUtil.deleteFileEntry(fileEntry);

			_documentRepository.deleteFileEntry(fileEntry.getFileEntryId());

			Folder folder = fileEntry.getFolder();

			Folder mountFolder = _documentRepository.getFolder(
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

			while (folder.getFolderId() != mountFolder.getFolderId()) {
				long folderId = folder.getFolderId();

				int count = _documentRepository.getFileEntriesCount(
					folderId, WorkflowConstants.STATUS_ANY);

				if (count != 0) {
					break;
				}

				if (CTCollectionThreadLocal.isProductionMode()) {
					if (_hasPublicationFileEntries(
							folder.getCompanyId(), folderId)) {

						break;
					}
				}
				else if (!_isOwnedByCurrentCTCollection(folder.getModel())) {
					break;
				}

				DLAppHelperLocalServiceUtil.deleteFolder(folder);

				folder = folder.getParentFolder();

				_documentRepository.deleteFolder(folderId);
			}
		}

	}

}