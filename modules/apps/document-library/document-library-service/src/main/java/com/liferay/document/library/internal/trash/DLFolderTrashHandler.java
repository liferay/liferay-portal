/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.trash;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ContainerModel;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.repository.DocumentRepository;
import com.liferay.portal.kernel.repository.LocalRepository;
import com.liferay.portal.kernel.repository.RepositoryProviderUtil;
import com.liferay.portal.kernel.repository.capabilities.TrashCapability;
import com.liferay.portal.kernel.repository.capabilities.UnsupportedCapabilityException;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.trash.TrashRenderer;
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
 * Implements trash handling for the folder entity.
 *
 * @author Alexander Chow
 * @author Zsolt Berentey
 */
@Component(
	property = "model.class.name=com.liferay.document.library.kernel.model.DLFolder",
	service = TrashHandler.class
)
public class DLFolderTrashHandler extends BaseDLTrashHandler {

	@Override
	public void checkRestorableEntry(
			long classPK, long containerModelId, String newName)
		throws PortalException {

		DLFolder dlFolder = getDLFolder(classPK);

		checkRestorableEntry(
			classPK, 0, containerModelId, dlFolder.getName(), newName);
	}

	@Override
	public void checkRestorableEntry(
			TrashEntry trashEntry, long containerModelId, String newName)
		throws PortalException {

		checkRestorableEntry(
			trashEntry.getClassPK(), trashEntry.getEntryId(), containerModelId,
			trashEntry.getTypeSettingsProperty("title"), newName);
	}

	@Override
	public void deleteTrashEntry(long classPK) throws PortalException {
		DocumentRepository documentRepository = getDocumentRepository(classPK);

		TrashCapability trashCapability = documentRepository.getCapability(
			TrashCapability.class);

		trashCapability.deleteFolder(documentRepository.getFolder(classPK));
	}

	@Override
	public String getClassName() {
		return DLFolder.class.getName();
	}

	@Override
	public String getDeleteMessage() {
		return "found-in-deleted-folder-x";
	}

	@Override
	public ContainerModel getParentContainerModel(long classPK)
		throws PortalException {

		DLFolder dlFolder = getDLFolder(classPK);

		long parentFolderId = dlFolder.getParentFolderId();

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
	public String getRestoreContainedModelLink(
			PortletRequest portletRequest, long classPK)
		throws PortalException {

		DLFolder dlFolder = getDLFolder(classPK);

		return _dlURLHelper.getFolderControlPanelLink(
			portletRequest, dlFolder.getFolderId());
	}

	@Override
	public String getRestoreContainerModelLink(
			PortletRequest portletRequest, long classPK)
		throws PortalException {

		DLFolder dlFolder = getDLFolder(classPK);

		return _dlURLHelper.getFolderControlPanelLink(
			portletRequest, dlFolder.getParentFolderId());
	}

	@Override
	public String getRestoreMessage(PortletRequest portletRequest, long classPK)
		throws PortalException {

		DLFolder dlFolder = getDLFolder(classPK);

		return DLUtil.getAbsolutePath(
			portletRequest, DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			dlFolder.getParentFolderId());
	}

	@Override
	public String getSubcontainerModelName() {
		return "folder";
	}

	@Override
	public String getSystemEventClassName() {
		return DLFolderConstants.getClassName();
	}

	@Override
	public TrashedModel getTrashedModel(long classPK) {
		try {
			return getDLFolder(classPK);
		}
		catch (PortalException | UnsupportedCapabilityException exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return null;
		}
	}

	@Override
	public TrashRenderer getTrashRenderer(long classPK) throws PortalException {
		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				DLFolder.class.getName());

		return (TrashRenderer)assetRendererFactory.getAssetRenderer(classPK);
	}

	@Override
	public boolean hasTrashPermission(
			PermissionChecker permissionChecker, long groupId, long classPK,
			String trashActionId)
		throws PortalException {

		if (trashActionId.equals(TrashActionKeys.MOVE)) {
			return ModelResourcePermissionUtil.contains(
				_folderModelResourcePermission, permissionChecker, groupId,
				classPK, ActionKeys.ADD_FOLDER);
		}

		return super.hasTrashPermission(
			permissionChecker, groupId, classPK, trashActionId);
	}

	@Override
	public boolean isContainerModel() {
		return true;
	}

	@Override
	public boolean isMovable(long classPK) throws PortalException {
		DLFolder dlFolder = fetchDLFolder(classPK);

		if (dlFolder.getParentFolderId() > 0) {
			DLFolder parentFolder = _dlFolderLocalService.fetchFolder(
				dlFolder.getParentFolderId());

			if ((parentFolder == null) || parentFolder.isInTrash()) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isRestorable(long classPK) throws PortalException {
		DLFolder dlFolder = fetchDLFolder(classPK);

		if ((dlFolder == null) ||
			((dlFolder.getParentFolderId() > 0) &&
			 (_dlFolderLocalService.fetchFolder(dlFolder.getParentFolderId()) ==
				 null))) {

			return false;
		}

		if (!hasTrashPermission(
				PermissionThreadLocal.getPermissionChecker(),
				dlFolder.getGroupId(), classPK, TrashActionKeys.RESTORE)) {

			return false;
		}

		return !_trashHelper.isInTrashContainer(dlFolder);
	}

	@Override
	public void moveEntry(
			long userId, long classPK, long containerModelId,
			ServiceContext serviceContext)
		throws PortalException {

		_dlAppLocalService.moveFolder(
			userId, classPK, containerModelId, serviceContext);
	}

	@Override
	public void moveTrashEntry(
			long userId, long classPK, long containerModelId,
			ServiceContext serviceContext)
		throws PortalException {

		DocumentRepository documentRepository = getDocumentRepository(classPK);

		TrashCapability trashCapability = documentRepository.getCapability(
			TrashCapability.class);

		Folder folder = documentRepository.getFolder(classPK);

		Folder destinationFolder = null;

		if (containerModelId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			destinationFolder = documentRepository.getFolder(containerModelId);
		}

		trashCapability.moveFolderFromTrash(
			userId, folder, destinationFolder, serviceContext);
	}

	@Override
	public void restoreTrashEntry(long userId, long classPK)
		throws PortalException {

		DocumentRepository documentRepository = getDocumentRepository(classPK);

		TrashCapability trashCapability = documentRepository.getCapability(
			TrashCapability.class);

		trashCapability.restoreFolderFromTrash(
			userId, documentRepository.getFolder(classPK));
	}

	@Override
	public void updateTitle(long classPK, String name) throws PortalException {
		DLFolder dlFolder = getDLFolder(classPK);

		dlFolder.setName(name);

		_dlFolderLocalService.updateDLFolder(dlFolder);
	}

	protected void checkRestorableEntry(
			long classPK, long trashEntryId, long containerModelId,
			String originalTitle, String newName)
		throws PortalException {

		DLFolder dlFolder = getDLFolder(classPK);

		if (containerModelId == TrashEntryConstants.DEFAULT_CONTAINER_ID) {
			containerModelId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;
		}

		if (Validator.isNotNull(newName)) {
			originalTitle = newName;
		}

		DLFolder duplicateDLFolder = _dlFolderLocalService.fetchFolder(
			dlFolder.getGroupId(), containerModelId, originalTitle);

		if (duplicateDLFolder != null) {
			RestoreEntryException restoreEntryException =
				new RestoreEntryException(RestoreEntryException.DUPLICATE);

			restoreEntryException.setDuplicateEntryId(
				duplicateDLFolder.getFolderId());
			restoreEntryException.setOldName(duplicateDLFolder.getName());
			restoreEntryException.setTrashEntryId(trashEntryId);

			throw restoreEntryException;
		}

		DLFileEntry duplicateDLFileEntry =
			_dlFileEntryLocalService.fetchFileEntry(
				dlFolder.getGroupId(), containerModelId, originalTitle);

		if (duplicateDLFileEntry != null) {
			RestoreEntryException restoreEntryException =
				new RestoreEntryException(RestoreEntryException.DUPLICATE);

			restoreEntryException.setDuplicateEntryId(
				duplicateDLFileEntry.getFileEntryId());
			restoreEntryException.setOldName(duplicateDLFileEntry.getTitle());
			restoreEntryException.setOverridable(false);
			restoreEntryException.setTrashEntryId(trashEntryId);

			throw restoreEntryException;
		}
	}

	@Override
	protected DocumentRepository getDocumentRepository(long classPK)
		throws PortalException {

		LocalRepository localRepository =
			RepositoryProviderUtil.getFolderLocalRepository(classPK);

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

		DLFolder dlFolder = getDLFolder(classPK);

		if (dlFolder.isInHiddenFolder() && actionId.equals(ActionKeys.VIEW)) {
			return false;
		}

		return _dlFolderModelResourcePermission.contains(
			permissionChecker, dlFolder, actionId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DLFolderTrashHandler.class);

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Reference
	private DLFolderLocalService _dlFolderLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.document.library.kernel.model.DLFolder)"
	)
	private ModelResourcePermission<DLFolder> _dlFolderModelResourcePermission;

	@Reference
	private DLURLHelper _dlURLHelper;

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.repository.model.Folder)"
	)
	private ModelResourcePermission<Folder> _folderModelResourcePermission;

	@Reference
	private TrashHelper _trashHelper;

}