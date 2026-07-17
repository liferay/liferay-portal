/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.exportimport.data.handler;

import com.liferay.asset.display.page.model.AssetDisplayPageEntry;
import com.liferay.asset.display.page.service.AssetDisplayPageEntryLocalService;
import com.liferay.document.library.exportimport.data.handler.DLPluggableContentDataHandler;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.processor.DLProcessorThreadLocal;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryMetadataLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.document.library.kernel.service.DLFileVersionLocalService;
import com.liferay.document.library.kernel.service.DLTrashService;
import com.liferay.document.library.kernel.store.DLStoreUtil;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.document.library.util.DLFileEntryTypeUtil;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesDeserializerDeserializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesDeserializerDeserializeResponse;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesSerializer;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesSerializerSerializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesSerializerSerializeResponse;
import com.liferay.dynamic.data.mapping.kernel.DDMFormValues;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.storage.DDMStorageEngineManager;
import com.liferay.dynamic.data.mapping.util.DDMBeanTranslatorUtil;
import com.liferay.exportimport.content.processor.ExportImportContentProcessor;
import com.liferay.exportimport.data.handler.base.BaseStagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.lar.StagedModelModifiedDateComparator;
import com.liferay.exportimport.portlet.data.handler.util.ExportImportGroupedModelUtil;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.io.DummyOutputStream;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.RepositoryLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.trash.TrashHandlerRegistryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.repository.liferayrepository.model.LiferayFileEntry;
import com.liferay.portal.repository.portletrepository.PortletRepository;
import com.liferay.portal.util.RepositoryUtil;
import com.liferay.portlet.documentlibrary.lar.FileEntryUtil;
import com.liferay.trash.TrashHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Máté Thurzó
 */
@Component(service = StagedModelDataHandler.class)
public class FileEntryStagedModelDataHandler
	extends BaseStagedModelDataHandler<FileEntry> {

	public static final String[] CLASS_NAMES = {
		DLFileEntry.class.getName(), FileEntry.class.getName(),
		LiferayFileEntry.class.getName()
	};

	@Override
	public void deleteStagedModel(FileEntry fileEntry) throws PortalException {
		_dlAppLocalService.deleteFileEntry(fileEntry.getFileEntryId());
	}

	@Override
	public void deleteStagedModel(
			String uuid, long groupId, String className, String extraData)
		throws PortalException {

		FileEntry fileEntry = fetchStagedModelByUuidAndGroupId(uuid, groupId);

		if (fileEntry != null) {
			deleteStagedModel(fileEntry);
		}
	}

	@Override
	public FileEntry fetchStagedModelByExternalReferenceCodeAndGroupId(
		String externalReferenceCode, long groupId) {

		try {
			return _dlAppLocalService.fetchFileEntryByExternalReferenceCode(
				groupId, externalReferenceCode);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return null;
		}
	}

	@Override
	public FileEntry fetchStagedModelByUuidAndGroupId(
		String uuid, long groupId) {

		try {
			return _dlAppLocalService.fetchFileEntryByUuidAndGroupId(
				uuid, groupId);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return null;
		}
	}

	@Override
	public List<FileEntry> fetchStagedModelsByUuidAndCompanyId(
		String uuid, long companyId) {

		return TransformUtil.transform(
			_dlFileEntryLocalService.getDLFileEntriesByUuidAndCompanyId(
				uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				new StagedModelModifiedDateComparator<>()),
			dlFileEntry -> new LiferayFileEntry(dlFileEntry));
	}

	@Override
	public String[] getClassNames() {
		return CLASS_NAMES;
	}

	@Override
	public String getDisplayName(FileEntry fileEntry) {
		if (fileEntry.isInTrash()) {
			return _trashHelper.getOriginalTitle(fileEntry.getTitle());
		}

		return fileEntry.getTitle();
	}

	@Override
	public void importStagedModel(
			PortletDataContext portletDataContext, FileEntry fileEntry)
		throws PortletDataException {

		boolean dlProcessorEnabled = DLProcessorThreadLocal.isEnabled();

		try {
			DLProcessorThreadLocal.setEnabled(false);

			super.importStagedModel(portletDataContext, fileEntry);
		}
		finally {
			DLProcessorThreadLocal.setEnabled(dlProcessorEnabled);
		}
	}

	@Override
	public void restoreStagedModel(
			PortletDataContext portletDataContext, FileEntry stagedModel)
		throws PortletDataException {

		try {
			doRestoreStagedModel(portletDataContext, stagedModel);
		}
		catch (PortletDataException portletDataException) {
			throw portletDataException;
		}
		catch (Exception exception) {
			throw new PortletDataException(exception);
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerList = ServiceTrackerListFactory.open(
			bundleContext,
			(Class<DLPluggableContentDataHandler<?>>)
				(Class<?>)DLPluggableContentDataHandler.class,
			"(model.class.name=" + FileEntry.class.getName() + ")");
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerList.close();
	}

	@Override
	protected void doExportStagedModel(
			PortletDataContext portletDataContext, FileEntry fileEntry)
		throws Exception {

		Element fileEntryElement = portletDataContext.getExportDataElement(
			fileEntry);

		String fileEntryPath = ExportImportPathUtil.getModelPath(fileEntry);

		if (!fileEntry.isDefaultRepository()) {
			Repository repository = _repositoryLocalService.getRepository(
				fileEntry.getRepositoryId());

			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, fileEntry, repository,
				PortletDataContext.REFERENCE_TYPE_STRONG);

			long portletRepositoryClassNameId = _portal.getClassNameId(
				PortletRepository.class.getName());

			if (repository.getClassNameId() != portletRepositoryClassNameId) {
				return;
			}
		}

		if (fileEntry.getFolderId() !=
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {

			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, fileEntry, fileEntry.getFolder(),
				PortletDataContext.REFERENCE_TYPE_PARENT);
		}

		FileVersion fileVersion = fileEntry.getFileVersion();

		fileEntryElement.addAttribute("fileVersionUuid", fileVersion.getUuid());

		fileEntryElement.addAttribute("version", fileEntry.getVersion());

		LiferayFileEntry liferayFileEntry = (LiferayFileEntry)fileEntry;

		liferayFileEntry.setCachedFileVersion(fileEntry.getFileVersion());

		if (!portletDataContext.isPerformDirectBinaryImport()) {
			InputStream inputStream = null;

			try {
				inputStream = FileEntryUtil.getContentStream(fileEntry);
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to retrieve content for file entry " +
							fileEntry.getFileEntryId(),
						exception);
				}
			}

			if (inputStream == null) {
				fileEntryElement.detach();

				return;
			}

			try {
				String binPath = ExportImportPathUtil.getModelPath(
					fileEntry, fileEntry.getVersion());

				portletDataContext.addZipEntry(binPath, inputStream);

				fileEntryElement.addAttribute("bin-path", binPath);
			}
			finally {
				try {
					inputStream.close();
				}
				catch (IOException ioException) {
					_log.error(ioException);
				}
			}
		}

		for (DLPluggableContentDataHandler<?> dlPluggableContentDataHandler :
				_serviceTrackerList) {

			DLPluggableContentDataHandler<FileEntry>
				fileEntryDLPluggableContentDataHandler =
					(DLPluggableContentDataHandler<FileEntry>)
						dlPluggableContentDataHandler;

			fileEntryDLPluggableContentDataHandler.exportContent(
				portletDataContext, fileEntryElement, fileEntry);
		}

		_exportMetaData(portletDataContext, fileEntryElement, fileEntry);

		_exportAssetDisplayPage(portletDataContext, fileEntry);
		_exportFriendlyURLEntries(portletDataContext, fileEntry);

		portletDataContext.addClassedModel(
			fileEntryElement, fileEntryPath, liferayFileEntry,
			DLFileEntry.class);
	}

	@Override
	protected void doImportMissingReference(
		PortletDataContext portletDataContext, String uuid, long groupId,
		long fileEntryId) {

		FileEntry existingFileEntry = fetchMissingReference(uuid, groupId);

		if (existingFileEntry == null) {
			return;
		}

		Map<Long, Long> dlFileEntryIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				DLFileEntry.class);

		dlFileEntryIds.put(fileEntryId, existingFileEntry.getFileEntryId());

		Map<Long, Long> fileEntryIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				FileEntry.class);

		fileEntryIds.put(fileEntryId, existingFileEntry.getFileEntryId());
	}

	@Override
	protected void doImportStagedModel(
			PortletDataContext portletDataContext, FileEntry fileEntry)
		throws Exception {

		if (RepositoryUtil.isExternalRepository(fileEntry.getRepositoryId())) {

			// References has been automatically imported, nothing to do here

			return;
		}

		Map<Long, Long> repositoryIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				Repository.class);

		long repositoryId = MapUtil.getLong(
			repositoryIds, fileEntry.getRepositoryId(),
			portletDataContext.getScopeGroupId());

		Map<Long, Long> folderIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				Folder.class);

		long folderId = MapUtil.getLong(
			folderIds, fileEntry.getFolderId(), fileEntry.getFolderId());

		long[] assetCategoryIds = portletDataContext.getAssetCategoryIds(
			DLFileEntry.class, fileEntry.getFileEntryId());
		String[] assetTagNames = portletDataContext.getAssetTagNames(
			DLFileEntry.class, fileEntry.getFileEntryId());

		ServiceContext serviceContext = portletDataContext.createServiceContext(
			fileEntry, DLFileEntry.class);

		serviceContext.setAttribute(
			"sourceFileName", "A." + fileEntry.getExtension());

		long userId = portletDataContext.getUserId(fileEntry.getUserUuid());

		serviceContext.setUserId(userId);

		Element fileEntryElement = portletDataContext.getImportDataElement(
			fileEntry);

		String binPath = fileEntryElement.attributeValue("bin-path");
		String fileVersionUuid = fileEntryElement.attributeValue(
			"fileVersionUuid");
		String version = fileEntryElement.attributeValue("version");

		Serializable validateDDMFormValues = serviceContext.getAttribute(
			"validateDDMFormValues");

		serviceContext.setAttribute("validateDDMFormValues", Boolean.FALSE);

		InputStream inputStream = null;

		try {
			if (Validator.isNull(binPath) &&
				portletDataContext.isPerformDirectBinaryImport()) {

				try {
					inputStream = FileEntryUtil.getContentStream(fileEntry);
				}
				catch (Exception exception) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to retrieve content for file entry " +
								fileEntry.getFileEntryId(),
							exception);
					}

					return;
				}
			}
			else {
				inputStream = portletDataContext.getZipEntryAsInputStream(
					binPath);
			}

			if (inputStream == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"No file found for file entry " +
							fileEntry.getFileEntryId());
				}

				return;
			}

			boolean updateFileEntry = _importMetaData(
				portletDataContext, fileEntryElement, fileEntry,
				serviceContext);

			FileEntry importedFileEntry = null;

			if (portletDataContext.isDataStrategyMirror()) {
				FileEntry existingFileEntry = fetchExistingStagedModel(
					fileEntry, portletDataContext.getScopeGroupId());

				if (existingFileEntry == null) {
					if (portletDataContext.
							isDataStrategyMirrorWithOverwriting()) {

						FileEntry existingTitleFileEntry =
							FileEntryUtil.fetchByR_F_T(
								portletDataContext.getScopeGroupId(), folderId,
								fileEntry.getTitle());

						if (existingTitleFileEntry == null) {
							existingTitleFileEntry =
								FileEntryUtil.fetchByR_F_FN(
									portletDataContext.getScopeGroupId(),
									folderId, fileEntry.getFileName());
						}

						if (existingTitleFileEntry != null) {
							_dlAppLocalService.deleteFileEntry(
								existingTitleFileEntry.getFileEntryId());
						}
					}

					serviceContext.setAttribute(
						"fileVersionUuid", fileVersionUuid);
					serviceContext.setUuid(fileEntry.getUuid());

					importedFileEntry = _dlAppLocalService.addFileEntry(
						fileEntry.getExternalReferenceCode(), userId,
						repositoryId, folderId,
						DLUtil.getUniqueFileName(
							portletDataContext.getScopeGroupId(), folderId,
							fileEntry.getFileName(), false),
						fileEntry.getMimeType(),
						_dlFileEntryLocalService.getUniqueTitle(
							portletDataContext.getScopeGroupId(), folderId, 0,
							fileEntry.getTitle(), fileEntry.getExtension()),
						StringPool.BLANK, fileEntry.getDescription(), null,
						inputStream, fileEntry.getSize(),
						fileEntry.getDisplayDate(),
						fileEntry.getExpirationDate(),
						fileEntry.getReviewDate(), serviceContext);

					if (fileEntry.isInTrash()) {
						importedFileEntry =
							_dlTrashService.moveFileEntryToTrash(
								importedFileEntry.getFileEntryId());
					}
				}
				else {
					FileVersion latestExistingFileVersion =
						existingFileEntry.getLatestFileVersion(true);

					boolean indexEnabled = serviceContext.isIndexingEnabled();

					boolean deleteFileEntry = false;

					if (!Objects.equals(
							fileVersionUuid,
							latestExistingFileVersion.getUuid())) {

						deleteFileEntry = true;
						updateFileEntry = true;
					}
					else {
						try (InputStream existingFileVersionInputStream =
								latestExistingFileVersion.getContentStream(
									false)) {

							if (existingFileVersionInputStream == null) {
								updateFileEntry = true;
							}
							else {
								existingFileVersionInputStream.transferTo(
									new DummyOutputStream());
							}
						}
						catch (Exception exception) {
							if (_log.isDebugEnabled()) {
								_log.debug(exception);
							}

							updateFileEntry = true;
						}
					}

					try {
						serviceContext.setIndexingEnabled(false);

						if (updateFileEntry) {
							DLFileVersion alreadyExistingFileVersion =
								_dlFileVersionLocalService.
									getFileVersionByUuidAndGroupId(
										fileVersionUuid,
										existingFileEntry.getGroupId());

							if (alreadyExistingFileVersion != null) {
								serviceContext.setAttribute(
									"existingDLFileVersionId",
									alreadyExistingFileVersion.
										getFileVersionId());
							}

							serviceContext.setUuid(fileVersionUuid);

							String fileEntryTitle =
								_dlFileEntryLocalService.getUniqueTitle(
									portletDataContext.getScopeGroupId(),
									existingFileEntry.getFolderId(),
									existingFileEntry.getFileEntryId(),
									fileEntry.getTitle(),
									fileEntry.getExtension());

							importedFileEntry =
								_dlAppLocalService.updateFileEntry(
									userId, existingFileEntry.getFileEntryId(),
									fileEntry.getFileName(),
									fileEntry.getMimeType(), fileEntryTitle,
									StringPool.BLANK,
									fileEntry.getDescription(), null,
									DLVersionNumberIncrease.MINOR, inputStream,
									fileEntry.getSize(),
									fileEntry.getDisplayDate(),
									fileEntry.getExpirationDate(),
									fileEntry.getReviewDate(), serviceContext);
						}
						else {
							_dlAppLocalService.updateAsset(
								userId, existingFileEntry,
								latestExistingFileVersion, assetCategoryIds,
								assetTagNames, null);

							importedFileEntry = existingFileEntry;
						}

						if (importedFileEntry.getFolderId() != folderId) {
							importedFileEntry =
								_dlAppLocalService.moveFileEntry(
									userId, importedFileEntry.getFileEntryId(),
									folderId, serviceContext);
						}

						DLFileEntry importedDLFileEntry =
							_dlFileEntryLocalService.fetchDLFileEntry(
								importedFileEntry.getFileEntryId());

						importedDLFileEntry.setUuid(fileEntry.getUuid());

						_dlFileEntryLocalService.updateDLFileEntry(
							importedDLFileEntry);

						if (importedFileEntry instanceof LiferayFileEntry) {
							LiferayFileEntry liferayFileEntry =
								(LiferayFileEntry)importedFileEntry;

							Indexer<DLFileEntry> indexer =
								IndexerRegistryUtil.nullSafeGetIndexer(
									DLFileEntry.class);

							indexer.reindex(
								(DLFileEntry)liferayFileEntry.getModel());
						}

						if (deleteFileEntry &&
							ExportImportThreadLocal.isStagingInProcess()) {

							String latestExistingVersion =
								latestExistingFileVersion.getVersion();

							if (!latestExistingVersion.equals(
									importedFileEntry.getVersion()) &&
								!latestExistingVersion.equals(
									DLFileEntryConstants.
										PRIVATE_WORKING_COPY_VERSION)) {

								_dlFileEntryLocalService.deleteFileVersion(
									userId,
									latestExistingFileVersion.getFileEntryId(),
									latestExistingFileVersion.getVersion());
							}
						}
					}
					finally {
						serviceContext.setIndexingEnabled(indexEnabled);
					}
				}

				if (ExportImportThreadLocal.isStagingInProcess()) {
					importedFileEntry = _overrideFileVersion(
						importedFileEntry, version, serviceContext);
				}
			}
			else {
				importedFileEntry = _dlAppLocalService.addFileEntry(
					fileEntry.getExternalReferenceCode(), userId, repositoryId,
					folderId,
					DLUtil.getUniqueFileName(
						portletDataContext.getScopeGroupId(), folderId,
						fileEntry.getFileName(), false),
					fileEntry.getMimeType(),
					_dlFileEntryLocalService.getUniqueTitle(
						portletDataContext.getScopeGroupId(), folderId, 0,
						fileEntry.getTitle(), fileEntry.getExtension()),
					StringPool.BLANK, fileEntry.getDescription(), null,
					inputStream, fileEntry.getSize(),
					fileEntry.getDisplayDate(), fileEntry.getExpirationDate(),
					fileEntry.getReviewDate(), serviceContext);
			}

			for (DLPluggableContentDataHandler<?>
					dlPluggableContentDataHandler : _serviceTrackerList) {

				DLPluggableContentDataHandler<FileEntry>
					fileEntryDLPluggableContentDataHandler =
						(DLPluggableContentDataHandler<FileEntry>)
							dlPluggableContentDataHandler;

				fileEntryDLPluggableContentDataHandler.importContent(
					portletDataContext, fileEntryElement, fileEntry,
					importedFileEntry);
			}

			portletDataContext.importClassedModel(
				fileEntry, importedFileEntry, DLFileEntry.class);

			Map<Long, Long> fileEntryIds =
				(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
					FileEntry.class);

			fileEntryIds.put(
				fileEntry.getFileEntryId(), importedFileEntry.getFileEntryId());

			_importAssetDisplayPage(
				portletDataContext, fileEntry, importedFileEntry);
			_importFriendlyURLEntries(
				portletDataContext, fileEntry, importedFileEntry,
				serviceContext);
		}
		finally {
			serviceContext.setAttribute(
				"validateDDMFormValues", validateDDMFormValues);

			try {
				if (inputStream != null) {
					inputStream.close();
				}
			}
			catch (IOException ioException) {
				_log.error(ioException);
			}
		}
	}

	@Override
	protected void doRestoreStagedModel(
			PortletDataContext portletDataContext, FileEntry fileEntry)
		throws Exception {

		FileEntry existingFileEntry = fetchStagedModelByUuidAndGroupId(
			fileEntry.getUuid(), portletDataContext.getScopeGroupId());

		if ((existingFileEntry == null) || !existingFileEntry.isInTrash()) {
			return;
		}

		TrashHandler trashHandler = TrashHandlerRegistryUtil.getTrashHandler(
			DLFileEntry.class.getName());

		if (trashHandler.isRestorable(existingFileEntry.getFileEntryId())) {
			trashHandler.restoreTrashEntry(
				portletDataContext.getUserId(fileEntry.getUserUuid()),
				existingFileEntry.getFileEntryId());
		}
	}

	@Override
	protected String[] getSkipImportReferenceStagedModelNames() {
		return new String[] {
			AssetDisplayPageEntry.class.getName(),
			FriendlyURLEntry.class.getName()
		};
	}

	@Override
	protected boolean isStagedModelInTrash(FileEntry fileEntry) {
		return fileEntry.isInTrash();
	}

	@Override
	protected void validateExport(
			PortletDataContext portletDataContext, FileEntry fileEntry)
		throws PortletDataException {

		if ((fileEntry.getGroupId() != portletDataContext.getGroupId()) &&
			(fileEntry.getGroupId() != portletDataContext.getScopeGroupId()) &&
			!ExportImportGroupedModelUtil.
				isReferenceInLayoutGroupWithinExportScope(
					portletDataContext, fileEntry)) {

			PortletDataException portletDataException =
				new PortletDataException(PortletDataException.INVALID_GROUP);

			portletDataException.setStagedModelDisplayName(
				getDisplayName(fileEntry));
			portletDataException.setStagedModelClassName(
				fileEntry.getModelClassName());
			portletDataException.setStagedModelClassPK(
				GetterUtil.getString(fileEntry.getFileEntryId()));

			throw portletDataException;
		}

		try {
			FileVersion fileVersion = fileEntry.getFileVersion();

			if (!portletDataContext.isInitialPublication() &&
				!ArrayUtil.contains(
					getExportableStatuses(), fileVersion.getStatus())) {

				PortletDataException portletDataException =
					new PortletDataException(
						PortletDataException.STATUS_UNAVAILABLE);

				portletDataException.setStagedModelDisplayName(
					getDisplayName(fileEntry));
				portletDataException.setStagedModelClassName(
					fileVersion.getModelClassName());
				portletDataException.setStagedModelClassPK(
					GetterUtil.getString(fileVersion.getFileVersionId()));

				throw portletDataException;
			}
		}
		catch (PortletDataException portletDataException) {
			throw portletDataException;
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
			else if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to check workflow status for file entry " +
						fileEntry.getFileEntryId());
			}
		}

		if (fileEntry.isInTrash() || fileEntry.isInTrashContainer()) {
			PortletDataException portletDataException =
				new PortletDataException(PortletDataException.STATUS_IN_TRASH);

			portletDataException.setStagedModel(fileEntry);

			throw portletDataException;
		}
	}

	private com.liferay.dynamic.data.mapping.storage.DDMFormValues _deserialize(
		String content, DDMForm ddmForm) {

		DDMFormValuesDeserializerDeserializeRequest.Builder builder =
			DDMFormValuesDeserializerDeserializeRequest.Builder.newBuilder(
				content, ddmForm);

		DDMFormValuesDeserializerDeserializeResponse
			ddmFormValuesDeserializerDeserializeResponse =
				_jsonDDMFormValuesDeserializer.deserialize(builder.build());

		return ddmFormValuesDeserializerDeserializeResponse.getDDMFormValues();
	}

	private void _exportAssetDisplayPage(
			PortletDataContext portletDataContext, FileEntry fileEntry)
		throws Exception {

		AssetDisplayPageEntry assetDisplayPageEntry =
			_assetDisplayPageEntryLocalService.fetchAssetDisplayPageEntry(
				fileEntry.getGroupId(),
				_portal.getClassNameId(FileEntry.class.getName()),
				fileEntry.getFileEntryId());

		if (assetDisplayPageEntry != null) {
			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, fileEntry, assetDisplayPageEntry,
				PortletDataContext.REFERENCE_TYPE_DEPENDENCY);
		}
	}

	private void _exportDDMFormValues(
			PortletDataContext portletDataContext, DDMStructure ddmStructure,
			FileEntry fileEntry, Element fileEntryElement)
		throws Exception {

		FileVersion fileVersion = fileEntry.getFileVersion();

		DLFileEntryMetadata dlFileEntryMetadata =
			_dlFileEntryMetadataLocalService.fetchFileEntryMetadata(
				ddmStructure.getStructureId(), fileVersion.getFileVersionId());

		if (dlFileEntryMetadata == null) {
			return;
		}

		Element structureFields = fileEntryElement.addElement(
			"structure-fields");

		String ddmFormValuesPath = ExportImportPathUtil.getModelPath(
			ddmStructure,
			String.valueOf(dlFileEntryMetadata.getDDMStorageId()));

		structureFields.addAttribute("ddm-form-values-path", ddmFormValuesPath);

		structureFields.addAttribute(
			"structureKey", ddmStructure.getStructureKey());
		structureFields.addAttribute("structureUuid", ddmStructure.getUuid());

		com.liferay.dynamic.data.mapping.storage.DDMFormValues ddmFormValues =
			_ddmStorageEngineManager.getDDMFormValues(
				dlFileEntryMetadata.getDDMStorageId());

		ddmFormValues =
			_ddmFormValuesExportImportContentProcessor.
				replaceExportContentReferences(
					portletDataContext, fileEntry, ddmFormValues, true, false);

		portletDataContext.addZipEntry(
			ddmFormValuesPath, _serialize(ddmFormValues));
	}

	private void _exportFriendlyURLEntries(
			PortletDataContext portletDataContext, FileEntry fileEntry)
		throws Exception {

		List<FriendlyURLEntry> friendlyURLEntries =
			_friendlyURLEntryLocalService.getFriendlyURLEntries(
				fileEntry.getGroupId(), _portal.getClassNameId(FileEntry.class),
				fileEntry.getFileEntryId());

		for (FriendlyURLEntry friendlyURLEntry : friendlyURLEntries) {
			StagedModelDataHandlerUtil.exportStagedModel(
				portletDataContext, friendlyURLEntry);

			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, fileEntry, friendlyURLEntry,
				PortletDataContext.REFERENCE_TYPE_DEPENDENCY);
		}
	}

	private void _exportMetaData(
			PortletDataContext portletDataContext, Element fileEntryElement,
			FileEntry fileEntry)
		throws Exception {

		LiferayFileEntry liferayFileEntry = (LiferayFileEntry)fileEntry;

		DLFileEntry dlFileEntry = liferayFileEntry.getDLFileEntry();

		DLFileEntryType dlFileEntryType =
			_dlFileEntryTypeLocalService.fetchFileEntryType(
				dlFileEntry.getFileEntryTypeId());

		if ((dlFileEntryType == null) || !dlFileEntryType.isExportable()) {
			return;
		}

		StagedModelDataHandlerUtil.exportReferenceStagedModel(
			portletDataContext, fileEntry, dlFileEntryType,
			PortletDataContext.REFERENCE_TYPE_STRONG);

		List<DDMStructure> ddmStructures = DLFileEntryTypeUtil.getDDMStructures(
			dlFileEntryType);

		for (DDMStructure ddmStructure : ddmStructures) {
			_exportDDMFormValues(
				portletDataContext, ddmStructure, fileEntry, fileEntryElement);
		}
	}

	private DDMFormValues _getImportDDMFormValues(
			PortletDataContext portletDataContext,
			Element structureFieldsElement, DDMStructure ddmStructure)
		throws Exception {

		String ddmFormValuesPath = structureFieldsElement.attributeValue(
			"ddm-form-values-path");

		String serializedDDMFormValues = portletDataContext.getZipEntryAsString(
			ddmFormValuesPath);

		com.liferay.dynamic.data.mapping.storage.DDMFormValues ddmFormValues =
			_deserialize(serializedDDMFormValues, ddmStructure.getDDMForm());

		ddmFormValues =
			_ddmFormValuesExportImportContentProcessor.
				replaceImportContentReferences(
					portletDataContext, ddmStructure, ddmFormValues);

		return DDMBeanTranslatorUtil.translate(ddmFormValues);
	}

	private void _importAssetDisplayPage(
			PortletDataContext portletDataContext, FileEntry fileEntry,
			FileEntry importedFileEntry)
		throws PortalException {

		List<Element> assetDisplayPageEntryElements =
			portletDataContext.getReferenceDataElements(
				fileEntry, AssetDisplayPageEntry.class);

		Map<Long, Long> fileEntryNewPrimaryKeys =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				DLFileEntry.class);

		fileEntryNewPrimaryKeys.put(
			fileEntry.getFileEntryId(), importedFileEntry.getFileEntryId());

		if (ListUtil.isEmpty(assetDisplayPageEntryElements)) {
			AssetDisplayPageEntry existingAssetDisplayPageEntry =
				_assetDisplayPageEntryLocalService.fetchAssetDisplayPageEntry(
					importedFileEntry.getGroupId(),
					_portal.getClassNameId(FileEntry.class.getName()),
					importedFileEntry.getFileEntryId());

			if (existingAssetDisplayPageEntry != null) {
				_assetDisplayPageEntryLocalService.deleteAssetDisplayPageEntry(
					existingAssetDisplayPageEntry);
			}

			return;
		}

		for (Element assetDisplayPageEntryElement :
				assetDisplayPageEntryElements) {

			String path = assetDisplayPageEntryElement.attributeValue("path");

			AssetDisplayPageEntry assetDisplayPageEntry =
				(AssetDisplayPageEntry)portletDataContext.getZipEntryAsObject(
					path);

			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, assetDisplayPageEntryElement);

			Map<Long, Long> assetDisplayPageEntries =
				(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
					AssetDisplayPageEntry.class);

			long assetDisplayPageEntryId = MapUtil.getLong(
				assetDisplayPageEntries,
				assetDisplayPageEntry.getAssetDisplayPageEntryId(),
				assetDisplayPageEntry.getAssetDisplayPageEntryId());

			AssetDisplayPageEntry existingAssetDisplayPageEntry =
				_assetDisplayPageEntryLocalService.fetchAssetDisplayPageEntry(
					assetDisplayPageEntryId);

			if (existingAssetDisplayPageEntry != null) {
				existingAssetDisplayPageEntry.setClassPK(
					importedFileEntry.getFileEntryId());

				_assetDisplayPageEntryLocalService.updateAssetDisplayPageEntry(
					existingAssetDisplayPageEntry);
			}
		}
	}

	private void _importFriendlyURLEntries(
			PortletDataContext portletDataContext, FileEntry fileEntry,
			FileEntry importedFileEntry, ServiceContext serviceContext)
		throws PortalException {

		List<Element> friendlyURLEntryElements =
			portletDataContext.getReferenceDataElements(
				fileEntry, FriendlyURLEntry.class);

		Map<Long, Long> fileEntryNewPrimaryKeys =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				FileEntry.class);

		fileEntryNewPrimaryKeys.put(
			fileEntry.getFileEntryId(), importedFileEntry.getFileEntryId());

		for (Element friendlyURLEntryElement : friendlyURLEntryElements) {
			String path = friendlyURLEntryElement.attributeValue("path");

			FriendlyURLEntry friendlyURLEntry =
				(FriendlyURLEntry)portletDataContext.getZipEntryAsObject(path);

			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, friendlyURLEntryElement);

			Map<Long, Long> friendlyURLEntries =
				(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
					FriendlyURLEntry.class);

			long friendlyURLEntryId = MapUtil.getLong(
				friendlyURLEntries, friendlyURLEntry.getFriendlyURLEntryId(),
				friendlyURLEntry.getFriendlyURLEntryId());

			FriendlyURLEntry existingFriendlyURLEntry =
				_friendlyURLEntryLocalService.fetchFriendlyURLEntry(
					friendlyURLEntryId);

			if (existingFriendlyURLEntry != null) {
				existingFriendlyURLEntry.setClassPK(
					importedFileEntry.getFileEntryId());

				_friendlyURLEntryLocalService.updateFriendlyURLEntry(
					existingFriendlyURLEntry);
			}
		}

		if (ExportImportThreadLocal.isStagingInProcess() &&
			!ExportImportThreadLocal.isStagingInProcessOnRemoteLive()) {

			_updateFriendlyURLEntries(
				fileEntry, importedFileEntry, serviceContext);
		}
	}

	private boolean _importMetaData(
			PortletDataContext portletDataContext, Element fileEntryElement,
			FileEntry fileEntry, ServiceContext serviceContext)
		throws Exception {

		LiferayFileEntry liferayFileEntry = (LiferayFileEntry)fileEntry;

		DLFileEntry dlFileEntry = liferayFileEntry.getDLFileEntry();

		Map<Long, Long> dlFileEntryTypeIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				DLFileEntryType.class);

		long dlFileEntryTypeId = MapUtil.getLong(
			dlFileEntryTypeIds, dlFileEntry.getFileEntryTypeId(),
			dlFileEntry.getFileEntryTypeId());

		DLFileEntryType existingDLFileEntryType =
			_dlFileEntryTypeLocalService.fetchDLFileEntryType(
				dlFileEntryTypeId);

		if (existingDLFileEntryType == null) {
			serviceContext.setAttribute("fileEntryTypeId", -1);

			return false;
		}

		serviceContext.setAttribute(
			"fileEntryTypeId", existingDLFileEntryType.getFileEntryTypeId());

		boolean updateFileEntry = false;

		List<DDMStructure> ddmStructures = DLFileEntryTypeUtil.getDDMStructures(
			existingDLFileEntryType);

		for (DDMStructure ddmStructure : ddmStructures) {
			Element structureFieldsElement =
				(Element)fileEntryElement.selectSingleNode(
					StringBundler.concat(
						"structure-fields[@structureUuid='",
						ddmStructure.getUuid(), "']"));

			if (structureFieldsElement == null) {
				structureFieldsElement =
					(Element)fileEntryElement.selectSingleNode(
						StringBundler.concat(
							"structure-fields[@structureKey='",
							ddmStructure.getStructureKey(), "']"));
			}

			if (structureFieldsElement == null) {
				continue;
			}

			DDMFormValues ddmFormValues = _getImportDDMFormValues(
				portletDataContext, structureFieldsElement, ddmStructure);

			serviceContext.setAttribute(
				DDMFormValues.class.getName() + StringPool.POUND +
					ddmStructure.getStructureId(),
				ddmFormValues);

			updateFileEntry = true;
		}

		return updateFileEntry;
	}

	private FileEntry _overrideFileVersion(
			FileEntry importedFileEntry, String version,
			ServiceContext serviceContext)
		throws PortalException {

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		try {
			return TransactionInvokerUtil.invoke(
				_transactionConfig,
				() -> {
					DLFileEntry dlFileEntry =
						_dlFileEntryLocalService.getDLFileEntry(
							importedFileEntry.getFileEntryId());

					if (version.equals(dlFileEntry.getVersion())) {
						return importedFileEntry;
					}

					DLFileVersion dlFileVersion = dlFileEntry.getFileVersion();

					String oldStoreFileName = dlFileVersion.getStoreFileName();

					dlFileVersion.setVersion(version);
					dlFileVersion.setStoreUUID(
						String.valueOf(UUID.randomUUID()));

					dlFileVersion =
						_dlFileVersionLocalService.updateDLFileVersion(
							dlFileVersion);

					dlFileEntry.setVersion(version);

					dlFileEntry = _dlFileEntryLocalService.updateDLFileEntry(
						dlFileEntry);

					if (DLStoreUtil.hasFile(
							dlFileEntry.getCompanyId(),
							dlFileEntry.getDataRepositoryId(),
							dlFileEntry.getName(), oldStoreFileName)) {

						DLStoreUtil.updateFileVersion(
							dlFileEntry.getCompanyId(),
							dlFileEntry.getDataRepositoryId(),
							dlFileEntry.getName(), oldStoreFileName,
							dlFileVersion.getStoreFileName());
					}

					return _dlAppLocalService.getFileEntry(
						dlFileEntry.getFileEntryId());
				});
		}
		catch (PortalException | SystemException exception) {
			throw exception;
		}
		catch (Throwable throwable) {
			throw new PortalException(throwable);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	private String _serialize(
		com.liferay.dynamic.data.mapping.storage.DDMFormValues ddmFormValues) {

		DDMFormValuesSerializerSerializeRequest.Builder builder =
			DDMFormValuesSerializerSerializeRequest.Builder.newBuilder(
				ddmFormValues);

		DDMFormValuesSerializerSerializeResponse
			ddmFormValuesSerializerSerializeResponse =
				_jsonDDMFormValuesSerializer.serialize(builder.build());

		return ddmFormValuesSerializerSerializeResponse.getContent();
	}

	private void _updateFriendlyURLEntries(
			FileEntry fileEntry, FileEntry importedFileEntry,
			ServiceContext serviceContext)
		throws PortalException {

		List<String> urlTitles = TransformUtil.transform(
			_friendlyURLEntryLocalService.getFriendlyURLEntries(
				fileEntry.getGroupId(), _portal.getClassNameId(FileEntry.class),
				fileEntry.getFileEntryId()),
			FriendlyURLEntry::getUrlTitle);

		List<FriendlyURLEntry> importedFriendlyURLEntries =
			_friendlyURLEntryLocalService.getFriendlyURLEntries(
				importedFileEntry.getGroupId(),
				_portal.getClassNameId(FileEntry.class),
				importedFileEntry.getFileEntryId());

		for (FriendlyURLEntry importedFriendlyURLEntry :
				importedFriendlyURLEntries) {

			if (!urlTitles.contains(importedFriendlyURLEntry.getUrlTitle())) {
				_friendlyURLEntryLocalService.deleteFriendlyURLEntry(
					importedFriendlyURLEntry.getFriendlyURLEntryId());
			}
			else {
				urlTitles.remove(importedFriendlyURLEntry.getUrlTitle());
			}
		}

		for (String urlTitle : urlTitles) {
			if (Validator.isBlank(urlTitle)) {
				continue;
			}

			_friendlyURLEntryLocalService.addFriendlyURLEntry(
				importedFileEntry.getGroupId(),
				_classNameLocalService.getClassNameId(FileEntry.class),
				importedFileEntry.getFileEntryId(), urlTitle, serviceContext);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FileEntryStagedModelDataHandler.class);

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRED, new Class<?>[] {Exception.class});

	@Reference
	private AssetDisplayPageEntryLocalService
		_assetDisplayPageEntryLocalService;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.dynamic.data.mapping.storage.DDMFormValues)"
	)
	private ExportImportContentProcessor
		<com.liferay.dynamic.data.mapping.storage.DDMFormValues>
			_ddmFormValuesExportImportContentProcessor;

	@Reference
	private DDMStorageEngineManager _ddmStorageEngineManager;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Reference
	private DLFileEntryMetadataLocalService _dlFileEntryMetadataLocalService;

	@Reference
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	@Reference
	private DLFileVersionLocalService _dlFileVersionLocalService;

	@Reference
	private DLTrashService _dlTrashService;

	@Reference
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Reference(target = "(ddm.form.values.deserializer.type=json)")
	private DDMFormValuesDeserializer _jsonDDMFormValuesDeserializer;

	@Reference(target = "(ddm.form.values.serializer.type=json)")
	private DDMFormValuesSerializer _jsonDDMFormValuesSerializer;

	@Reference
	private Portal _portal;

	@Reference
	private RepositoryLocalService _repositoryLocalService;

	private ServiceTrackerList<DLPluggableContentDataHandler<?>>
		_serviceTrackerList;

	@Reference
	private TrashHelper _trashHelper;

}