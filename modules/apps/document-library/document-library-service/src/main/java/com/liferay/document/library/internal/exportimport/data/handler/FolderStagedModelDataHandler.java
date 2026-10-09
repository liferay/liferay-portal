/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.exportimport.data.handler;

import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.exportimport.data.handler.base.BaseStagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.lar.StagedModelModifiedDateComparator;
import com.liferay.exportimport.portlet.data.handler.util.ExportImportGroupedModelUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.RepositoryEntry;
import com.liferay.portal.kernel.repository.capabilities.TrashCapability;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.RepositoryLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.trash.TrashHandlerRegistryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.repository.liferayrepository.model.LiferayFolder;
import com.liferay.portal.repository.portletrepository.PortletRepository;
import com.liferay.portal.util.RepositoryUtil;
import com.liferay.trash.TrashHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Máté Thurzó
 */
@Component(service = StagedModelDataHandler.class)
public class FolderStagedModelDataHandler
	extends BaseStagedModelDataHandler<Folder> {

	public static final String[] CLASS_NAMES = {
		DLFolder.class.getName(), Folder.class.getName(),
		LiferayFolder.class.getName()
	};

	@Override
	public void deleteStagedModel(Folder folder) throws PortalException {
		_dlAppLocalService.deleteFolder(folder.getFolderId());
	}

	@Override
	public void deleteStagedModel(
			String uuid, long groupId, String className, String extraData)
		throws PortalException {

		Folder folder = fetchStagedModelByUuidAndGroupId(uuid, groupId);

		if (folder != null) {
			deleteStagedModel(folder);
		}
	}

	@Override
	public Folder fetchStagedModelByExternalReferenceCodeAndGroupId(
		String externalReferenceCode, long groupId) {

		DLFolder dlFolder =
			_dlFolderLocalService.fetchDLFolderByExternalReferenceCode(
				externalReferenceCode, groupId);

		if (dlFolder != null) {
			return new LiferayFolder(dlFolder);
		}

		return null;
	}

	@Override
	public Folder fetchStagedModelByUuidAndGroupId(String uuid, long groupId) {
		DLFolder dlFolder = _dlFolderLocalService.fetchFolder(uuid, groupId);

		if (dlFolder != null) {
			return new LiferayFolder(dlFolder);
		}

		return null;
	}

	@Override
	public List<Folder> fetchStagedModelsByUuidAndCompanyId(
		String uuid, long companyId) {

		return TransformUtil.transform(
			_dlFolderLocalService.getDLFoldersByUuidAndCompanyId(
				uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				new StagedModelModifiedDateComparator<>()),
			dlFolder -> new LiferayFolder(dlFolder));
	}

	@Override
	public String[] getClassNames() {
		return CLASS_NAMES;
	}

	@Override
	public String getDisplayName(Folder folder) {
		return folder.getName();
	}

	@Override
	public void restoreStagedModel(
			PortletDataContext portletDataContext, Folder stagedModel)
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

	@Override
	protected void doExportStagedModel(
			PortletDataContext portletDataContext, Folder folder)
		throws Exception {

		Element folderElement = portletDataContext.getExportDataElement(folder);

		String folderPath = ExportImportPathUtil.getModelPath(folder);

		if (!folder.isDefaultRepository()) {
			Repository repository = _repositoryLocalService.getRepository(
				folder.getRepositoryId());

			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, folder, repository,
				PortletDataContext.REFERENCE_TYPE_STRONG);

			boolean rootFolder = false;

			if (folder.getFolderId() == repository.getDlFolderId()) {
				rootFolder = true;
			}

			folderElement.addAttribute(
				"rootFolder", String.valueOf(rootFolder));

			long portletRepositoryClassNameId = _portal.getClassNameId(
				PortletRepository.class.getName());

			if (repository.getClassNameId() != portletRepositoryClassNameId) {
				return;
			}
		}

		if (folder.getParentFolderId() !=
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {

			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, folder, folder.getParentFolder(),
				PortletDataContext.REFERENCE_TYPE_PARENT);
		}

		_exportFolderFileEntryTypes(portletDataContext, folderElement, folder);

		portletDataContext.addClassedModel(
			folderElement, folderPath, folder, DLFolder.class);
	}

	@Override
	protected void doImportMissingReference(
		PortletDataContext portletDataContext, String uuid, long groupId,
		long folderId) {

		Folder existingFolder = fetchMissingReference(uuid, groupId);

		if (existingFolder == null) {
			return;
		}

		Map<Long, Long> folderIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				Folder.class);

		folderIds.put(folderId, existingFolder.getFolderId());
	}

	@Override
	protected void doImportStagedModel(
			PortletDataContext portletDataContext, Folder folder)
		throws Exception {

		Map<Long, Long> folderIdsAndRepositoryEntryIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				Folder.class + ".folderIdsAndRepositoryEntryIds");

		if (RepositoryUtil.isExternalRepository(folder.getRepositoryId())) {
			Map<Long, Long> repositoryEntryIds =
				(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
					RepositoryEntry.class);

			folderIdsAndRepositoryEntryIds.put(
				folder.getFolderId(),
				repositoryEntryIds.get(folder.getFolderId()));

			return;
		}

		long userId = portletDataContext.getUserId(folder.getUserUuid());

		Map<Long, Long> repositoryIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				Repository.class);

		long repositoryId = MapUtil.getLong(
			repositoryIds, folder.getRepositoryId(),
			portletDataContext.getScopeGroupId());

		Map<Long, Long> folderIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				Folder.class);

		long parentFolderId = MapUtil.getLong(
			folderIds, folder.getParentFolderId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		ServiceContext serviceContext = portletDataContext.createServiceContext(
			folder, DLFolder.class);

		serviceContext.setUserId(userId);

		Element folderElement = portletDataContext.getImportDataElement(folder);

		Folder importedFolder = null;

		boolean rootFolder = GetterUtil.getBoolean(
			folderElement.attributeValue("rootFolder"));

		if (rootFolder) {
			Repository repository = _repositoryLocalService.getRepository(
				repositoryId);

			importedFolder = _dlAppLocalService.getFolder(
				repository.getDlFolderId());
		}
		else {
			Folder existingFolder = fetchExistingStagedModel(
				folder, portletDataContext.getScopeGroupId());

			if ((existingFolder == null) ||
				!portletDataContext.isDataStrategyMirror()) {

				String uuid = null;

				if (portletDataContext.isDataStrategyMirror()) {
					uuid = folder.getUuid();

					serviceContext.setUuid(uuid);
				}

				String name = _dlFolderLocalService.getUniqueFolderName(
					uuid, portletDataContext.getScopeGroupId(), parentFolderId,
					folder.getName(), 2);

				importedFolder = _dlAppLocalService.addFolder(
					folder.getExternalReferenceCode(), userId, repositoryId,
					parentFolderId, name, folder.getDescription(),
					serviceContext);
			}
			else {
				String name = _dlFolderLocalService.getUniqueFolderName(
					folder.getUuid(), portletDataContext.getScopeGroupId(),
					parentFolderId, folder.getName(), 2);

				importedFolder = _dlAppLocalService.updateFolder(
					existingFolder.getFolderId(), parentFolderId, name,
					folder.getDescription(), serviceContext);

				DLFolder importedDLFolder = _dlFolderLocalService.fetchDLFolder(
					importedFolder.getFolderId());

				importedDLFolder.setUuid(folder.getUuid());

				_dlFolderLocalService.updateDLFolder(importedDLFolder);
			}
		}

		_importFolderFileEntryTypes(
			portletDataContext, folderElement, folder, importedFolder,
			serviceContext);

		portletDataContext.importClassedModel(
			folder, importedFolder, DLFolder.class);

		folderIds.put(folder.getFolderId(), importedFolder.getFolderId());
		folderIdsAndRepositoryEntryIds.put(
			folder.getFolderId(), importedFolder.getFolderId());
	}

	@Override
	protected void doRestoreStagedModel(
			PortletDataContext portletDataContext, Folder folder)
		throws Exception {

		Folder existingFolder = fetchStagedModelByUuidAndGroupId(
			folder.getUuid(), portletDataContext.getScopeGroupId());

		if ((existingFolder == null) ||
			!existingFolder.isRepositoryCapabilityProvided(
				TrashCapability.class)) {

			return;
		}

		TrashCapability trashCapability =
			existingFolder.getRepositoryCapability(TrashCapability.class);

		if (!trashCapability.isInTrash(existingFolder)) {
			return;
		}

		TrashHandler trashHandler = TrashHandlerRegistryUtil.getTrashHandler(
			DLFolder.class.getName());

		if (trashHandler.isRestorable(existingFolder.getFolderId())) {
			trashHandler.restoreTrashEntry(
				portletDataContext.getUserId(folder.getUserUuid()),
				existingFolder.getFolderId());
		}
	}

	@Override
	protected void validateExport(
			PortletDataContext portletDataContext, Folder folder)
		throws PortletDataException {

		if ((folder.getGroupId() != portletDataContext.getGroupId()) &&
			(folder.getGroupId() != portletDataContext.getScopeGroupId()) &&
			!ExportImportGroupedModelUtil.
				isReferenceInLayoutGroupWithinExportScope(
					portletDataContext, folder)) {

			PortletDataException portletDataException =
				new PortletDataException(PortletDataException.INVALID_GROUP);

			portletDataException.setStagedModelDisplayName(folder.getName());
			portletDataException.setStagedModelClassName(
				folder.getModelClassName());
			portletDataException.setStagedModelClassPK(
				GetterUtil.getString(folder.getFolderId()));

			throw portletDataException;
		}

		if (folder instanceof LiferayFolder) {
			LiferayFolder liferayFolder = (LiferayFolder)folder;

			DLFolder dlFolder = (DLFolder)liferayFolder.getModel();

			if (dlFolder.isInTrash() ||
				_trashHelper.isInTrashContainer(dlFolder)) {

				PortletDataException portletDataException =
					new PortletDataException(
						PortletDataException.STATUS_IN_TRASH);

				portletDataException.setStagedModelDisplayName(
					folder.getName());
				portletDataException.setStagedModelClassName(
					folder.getModelClassName());
				portletDataException.setStagedModelClassPK(
					GetterUtil.getString(folder.getFolderId()));

				throw portletDataException;
			}
		}
	}

	private void _exportFolderFileEntryTypes(
			PortletDataContext portletDataContext, Element folderElement,
			Folder folder)
		throws Exception {

		if (!folder.isDefaultRepository()) {
			return;
		}

		List<DLFileEntryType> dlFileEntryTypes =
			_dlFileEntryTypeLocalService.getFolderFileEntryTypes(
				new long[] {
					portletDataContext.getCompanyGroupId(),
					portletDataContext.getScopeGroupId()
				},
				folder.getFolderId(), false);

		long defaultFileEntryTypeId =
			_dlFileEntryTypeLocalService.getDefaultFileEntryTypeId(
				folder.getFolderId());

		String defaultFileEntryTypeUuid = StringPool.BLANK;

		for (DLFileEntryType dlFileEntryType : dlFileEntryTypes) {
			if (dlFileEntryType.getFileEntryTypeId() ==
					DLFileEntryTypeConstants.
						FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT) {

				folderElement.addAttribute("basic-document", "true");

				continue;
			}

			if (defaultFileEntryTypeId ==
					dlFileEntryType.getFileEntryTypeId()) {

				defaultFileEntryTypeUuid = dlFileEntryType.getUuid();
			}

			if (!dlFileEntryType.isExportable()) {
				continue;
			}

			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, folder, dlFileEntryType,
				PortletDataContext.REFERENCE_TYPE_STRONG);
		}

		folderElement.addAttribute(
			"defaultFileEntryTypeUuid", defaultFileEntryTypeUuid);
	}

	private void _importFolderFileEntryTypes(
			PortletDataContext portletDataContext, Element folderElement,
			Folder folder, Folder importedFolder, ServiceContext serviceContext)
		throws Exception {

		if (!folder.isDefaultRepository()) {
			return;
		}

		List<Long> currentFolderFileEntryTypeIds = new ArrayList<>();

		String defaultFileEntryTypeUuid = GetterUtil.getString(
			folderElement.attributeValue("defaultFileEntryTypeUuid"));

		long defaultFileEntryTypeId = 0;

		List<Element> referenceElements =
			portletDataContext.getReferenceElements(
				folder, DLFileEntryType.class);

		for (Element referenceElement : referenceElements) {
			long referenceDLFileEntryTypeId = GetterUtil.getLong(
				referenceElement.attributeValue("class-pk"));

			Map<Long, Long> dlFileEntryTypeIds =
				(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
					DLFileEntryType.class);

			long dlFileEntryTypeId = MapUtil.getLong(
				dlFileEntryTypeIds, referenceDLFileEntryTypeId,
				referenceDLFileEntryTypeId);

			DLFileEntryType existingDLFileEntryType =
				_dlFileEntryTypeLocalService.fetchDLFileEntryType(
					dlFileEntryTypeId);

			if (existingDLFileEntryType == null) {
				continue;
			}

			currentFolderFileEntryTypeIds.add(
				existingDLFileEntryType.getFileEntryTypeId());

			if (!defaultFileEntryTypeUuid.equals(
					referenceElement.attributeValue("uuid"))) {

				continue;
			}

			defaultFileEntryTypeId =
				existingDLFileEntryType.getFileEntryTypeId();
		}

		if (GetterUtil.getBoolean(
				folderElement.attributeValue("basic-document"))) {

			currentFolderFileEntryTypeIds.add(
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT);
		}

		if (!currentFolderFileEntryTypeIds.isEmpty()) {
			DLFolder dlFolder = _dlFolderLocalService.getDLFolder(
				importedFolder.getFolderId());

			dlFolder.setDefaultFileEntryTypeId(defaultFileEntryTypeId);
			dlFolder.setRestrictionType(
				DLFolderConstants.
					RESTRICTION_TYPE_FILE_ENTRY_TYPES_AND_WORKFLOW);

			dlFolder = _dlFolderLocalService.updateDLFolder(dlFolder);

			_dlFileEntryTypeLocalService.updateFolderFileEntryTypes(
				dlFolder, currentFolderFileEntryTypeIds, defaultFileEntryTypeId,
				serviceContext);
		}
	}

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	@Reference
	private DLFolderLocalService _dlFolderLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private RepositoryLocalService _repositoryLocalService;

	@Reference
	private TrashHelper _trashHelper;

}