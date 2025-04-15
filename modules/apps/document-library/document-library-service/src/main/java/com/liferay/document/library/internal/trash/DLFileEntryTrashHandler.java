/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.trash;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFileVersionLocalService;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.document.library.kernel.service.DLTrashLocalService;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.document.library.kernel.util.DLValidator;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ContainerModel;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.repository.DocumentRepository;
import com.liferay.portal.kernel.repository.LocalRepository;
import com.liferay.portal.kernel.repository.Repository;
import com.liferay.portal.kernel.repository.RepositoryProviderUtil;
import com.liferay.portal.kernel.repository.capabilities.TrashCapability;
import com.liferay.portal.kernel.repository.capabilities.UnsupportedCapabilityException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.trash.TrashHandlerRegistryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.trash.TrashHelper;
import com.liferay.trash.constants.TrashActionKeys;
import com.liferay.trash.constants.TrashEntryConstants;
import com.liferay.trash.exception.RestoreEntryException;
import com.liferay.trash.kernel.model.TrashEntry;

import jakarta.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Implements trash handling for the file entry entity.
 *
 * @author Alexander Chow
 * @author Manuel de la Peña
 * @author Zsolt Berentey
 */
@Component(
	property = "model.class.name=com.liferay.document.library.kernel.model.DLFileEntry",
	service = TrashHandler.class
)
public class DLFileEntryTrashHandler extends BaseDLTrashHandler {

	@Override
	public void checkRestorableEntry(
			long classPK, long containerModelId, String newName)
		throws PortalException {

		DLFileEntry dlFileEntry = _getDLFileEntry(classPK);

		checkRestorableEntry(
			classPK, 0, containerModelId, dlFileEntry.getFileName(),
			dlFileEntry.getTitle(), newName);
	}

	@Override
	public void checkRestorableEntry(
			TrashEntry trashEntry, long containerModelId, String newName)
		throws PortalException {

		checkRestorableEntry(
			trashEntry.getClassPK(), trashEntry.getEntryId(), containerModelId,
			trashEntry.getTypeSettingsProperty("fileName"),
			trashEntry.getTypeSettingsProperty("title"), newName);
	}

	@Override
	public void deleteTrashEntry(long classPK) throws PortalException {
		DocumentRepository documentRepository = getDocumentRepository(classPK);

		TrashCapability trashCapability = documentRepository.getCapability(
			TrashCapability.class);

		trashCapability.deleteFileEntry(
			documentRepository.getFileEntry(classPK));
	}

	@Override
	public String getClassName() {
		return DLFileEntry.class.getName();
	}

	@Override
	public Filter getExcludeFilter(SearchContext searchContext) {
		BooleanFilter excludeBooleanFilter = new BooleanFilter();

		excludeBooleanFilter.addRequiredTerm(
			Field.ENTRY_CLASS_NAME, DLFileEntryConstants.getClassName());
		excludeBooleanFilter.addRequiredTerm(Field.HIDDEN, true);

		return excludeBooleanFilter;
	}

	@Override
	public ContainerModel getParentContainerModel(long classPK)
		throws PortalException {

		DLFileEntry dlFileEntry = _getDLFileEntry(classPK);

		long parentFolderId = dlFileEntry.getFolderId();

		if (parentFolderId <= 0) {
			return null;
		}

		DLFolder parentFolder = _dlFolderLocalService.fetchDLFolder(
			parentFolderId);

		if (parentFolder == null) {
			return null;
		}

		return getContainerModel(parentFolderId);
	}

	@Override
	public ContainerModel getParentContainerModel(TrashedModel trashedModel)
		throws PortalException {

		DLFileEntry dlFileEntry = (DLFileEntry)trashedModel;

		return getContainerModel(dlFileEntry.getFolderId());
	}

	@Override
	public String getRestoreContainedModelLink(
			PortletRequest portletRequest, long classPK)
		throws PortalException {

		DLFileEntry dlFileEntry = _getDLFileEntry(classPK);

		return _dlURLHelper.getFileEntryControlPanelLink(
			portletRequest, dlFileEntry.getFileEntryId());
	}

	@Override
	public String getRestoreContainerModelLink(
			PortletRequest portletRequest, long classPK)
		throws PortalException {

		DLFileEntry dlFileEntry = _getDLFileEntry(classPK);

		return _dlURLHelper.getFolderControlPanelLink(
			portletRequest, dlFileEntry.getFolderId());
	}

	@Override
	public String getRestoreMessage(PortletRequest portletRequest, long classPK)
		throws PortalException {

		DLFileEntry dlFileEntry = _getDLFileEntry(classPK);

		DLFolder dlFolder = dlFileEntry.getFolder();

		return DLUtil.getAbsolutePath(
			portletRequest, DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			dlFolder.getFolderId());
	}

	@Override
	public String getSystemEventClassName() {
		return DLFileEntryConstants.getClassName();
	}

	@Override
	public TrashedModel getTrashedModel(long classPK) {
		try {
			return _getDLFileEntry(classPK);
		}
		catch (PortalException | UnsupportedCapabilityException exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return null;
		}
	}

	@Override
	public boolean hasTrashPermission(
			PermissionChecker permissionChecker, long groupId, long classPK,
			String trashActionId)
		throws PortalException {

		if (trashActionId.equals(TrashActionKeys.MOVE)) {
			return ModelResourcePermissionUtil.contains(
				_folderModelResourcePermission, permissionChecker, groupId,
				classPK, ActionKeys.ADD_DOCUMENT);
		}

		return super.hasTrashPermission(
			permissionChecker, groupId, classPK, trashActionId);
	}

	@Override
	public boolean isMovable(long classPK) throws PortalException {
		DLFileEntry dlFileEntry = _fetchDLFileEntry(classPK);

		if (dlFileEntry.getFolderId() > 0) {
			DLFolder parentFolder = _dlFolderLocalService.fetchFolder(
				dlFileEntry.getFolderId());

			if ((parentFolder == null) || parentFolder.isInTrash()) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isRestorable(long classPK) throws PortalException {
		DLFileEntry dlFileEntry = _fetchDLFileEntry(classPK);

		if ((dlFileEntry == null) ||
			((dlFileEntry.getFolderId() > 0) &&
			 (_dlFolderLocalService.fetchFolder(dlFileEntry.getFolderId()) ==
				 null))) {

			return false;
		}

		if (!hasTrashPermission(
				PermissionThreadLocal.getPermissionChecker(),
				dlFileEntry.getGroupId(), classPK, TrashActionKeys.RESTORE)) {

			return false;
		}

		return !_trashHelper.isInTrashContainer(dlFileEntry);
	}

	@Override
	public void moveEntry(
			long userId, long classPK, long containerModelId,
			ServiceContext serviceContext)
		throws PortalException {

		_dlAppLocalService.moveFileEntry(
			userId, classPK, containerModelId, serviceContext);
	}

	@Override
	public void moveTrashEntry(
			long userId, long classPK, long containerModelId,
			ServiceContext serviceContext)
		throws PortalException {

		DocumentRepository documentRepository = getDocumentRepository(classPK);

		_dlTrashLocalService.moveFileEntryFromTrash(
			userId, documentRepository.getRepositoryId(), classPK,
			containerModelId, serviceContext);
	}

	@Override
	public void restoreTrashEntry(long userId, long classPK)
		throws PortalException {

		DLFileEntry dlFileEntry = _getDLFileEntry(classPK);

		if ((dlFileEntry.getClassNameId() > 0) &&
			(dlFileEntry.getClassPK() > 0)) {

			TrashHandler trashHandler =
				TrashHandlerRegistryUtil.getTrashHandler(
					dlFileEntry.getClassName());

			trashHandler.restoreRelatedTrashEntry(getClassName(), classPK);

			return;
		}

		_dlTrashLocalService.restoreFileEntryFromTrash(
			userId, dlFileEntry.getRepositoryId(), classPK);
	}

	@Override
	public void updateTitle(long classPK, String name) throws PortalException {
		DLFileEntry dlFileEntry = _getDLFileEntry(classPK);

		String fileName = DLUtil.getSanitizedFileName(
			name, dlFileEntry.getExtension());

		dlFileEntry.setFileName(fileName);

		dlFileEntry.setTitle(name);

		dlFileEntry = _dlFileEntryLocalService.updateDLFileEntry(dlFileEntry);

		DLFileVersion dlFileVersion = dlFileEntry.getFileVersion();

		dlFileVersion.setFileName(fileName);
		dlFileVersion.setTitle(name);

		_dlFileVersionLocalService.updateDLFileVersion(dlFileVersion);
	}

	protected void checkRestorableEntry(
			long classPK, long entryId, long containerModelId,
			String originalFileName, String originalTitle, String newName)
		throws PortalException {

		if (Validator.isNotNull(newName) &&
			!_dlValidator.isValidName(newName)) {

			RestoreEntryException restoreEntryException =
				new RestoreEntryException(RestoreEntryException.INVALID_NAME);

			restoreEntryException.setErrorMessage("please-enter-a-valid-name");
			restoreEntryException.setTrashEntryId(entryId);

			throw restoreEntryException;
		}

		DLFileEntry dlFileEntry = _getDLFileEntry(classPK);

		if (containerModelId == TrashEntryConstants.DEFAULT_CONTAINER_ID) {
			containerModelId = dlFileEntry.getFolderId();
		}

		if (Validator.isNotNull(newName)) {
			originalFileName = DLUtil.getSanitizedFileName(
				newName, dlFileEntry.getExtension());
			originalTitle = newName;
		}

		DLFolder duplicateDLFolder = _dlFolderLocalService.fetchFolder(
			dlFileEntry.getGroupId(), containerModelId, originalTitle);

		if (duplicateDLFolder != null) {
			RestoreEntryException restoreEntryException =
				new RestoreEntryException(RestoreEntryException.DUPLICATE);

			restoreEntryException.setDuplicateEntryId(
				duplicateDLFolder.getFolderId());
			restoreEntryException.setOldName(duplicateDLFolder.getName());
			restoreEntryException.setOverridable(false);
			restoreEntryException.setTrashEntryId(entryId);

			throw restoreEntryException;
		}

		DLFileEntry duplicateDLFileEntry =
			_dlFileEntryLocalService.fetchFileEntry(
				dlFileEntry.getGroupId(), containerModelId, originalTitle);

		if (duplicateDLFileEntry == null) {
			duplicateDLFileEntry =
				_dlFileEntryLocalService.fetchFileEntryByFileName(
					dlFileEntry.getGroupId(), containerModelId,
					originalFileName);
		}

		if (duplicateDLFileEntry != null) {
			RestoreEntryException restoreEntryException =
				new RestoreEntryException(RestoreEntryException.DUPLICATE);

			restoreEntryException.setDuplicateEntryId(
				duplicateDLFileEntry.getFileEntryId());
			restoreEntryException.setOldName(duplicateDLFileEntry.getTitle());
			restoreEntryException.setTrashEntryId(entryId);

			throw restoreEntryException;
		}
	}

	@Override
	protected DocumentRepository getDocumentRepository(long classPK)
		throws PortalException {

		LocalRepository localRepository =
			RepositoryProviderUtil.getFileEntryLocalRepository(classPK);

		if (!localRepository.isCapabilityProvided(TrashCapability.class)) {
			throw new UnsupportedCapabilityException(
				TrashCapability.class,
				"Repository " + localRepository.getRepositoryId());
		}

		return localRepository;
	}

	@Override
	protected boolean hasPermission(
			PermissionChecker permissionChecker, long classPK, String actionId)
		throws PortalException {

		DLFileEntry dlFileEntry = _getDLFileEntry(classPK);

		if (dlFileEntry.isInHiddenFolder() &&
			actionId.equals(ActionKeys.VIEW)) {

			return false;
		}

		return _fileEntryModelResourcePermission.contains(
			permissionChecker, classPK, actionId);
	}

	private DLFileEntry _fetchDLFileEntry(long classPK) throws PortalException {
		Repository repository = RepositoryProviderUtil.getFileEntryRepository(
			classPK);

		if (!repository.isCapabilityProvided(TrashCapability.class)) {
			return null;
		}

		FileEntry fileEntry = repository.getFileEntry(classPK);

		return (DLFileEntry)fileEntry.getModel();
	}

	private DLFileEntry _getDLFileEntry(long classPK) throws PortalException {
		Repository repository = RepositoryProviderUtil.getFileEntryRepository(
			classPK);

		if (!repository.isCapabilityProvided(TrashCapability.class)) {
			throw new UnsupportedCapabilityException(
				TrashCapability.class,
				"Repository " + repository.getRepositoryId());
		}

		FileEntry fileEntry = repository.getFileEntry(classPK);

		return (DLFileEntry)fileEntry.getModel();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DLFileEntryTrashHandler.class);

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Reference
	private DLFileVersionLocalService _dlFileVersionLocalService;

	@Reference
	private DLFolderLocalService _dlFolderLocalService;

	@Reference
	private DLTrashLocalService _dlTrashLocalService;

	@Reference
	private DLURLHelper _dlURLHelper;

	@Reference
	private DLValidator _dlValidator;

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.repository.model.FileEntry)"
	)
	private ModelResourcePermission<FileEntry>
		_fileEntryModelResourcePermission;

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.repository.model.Folder)"
	)
	private ModelResourcePermission<Folder> _folderModelResourcePermission;

	@Reference
	private TrashHelper _trashHelper;

}