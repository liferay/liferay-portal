/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.trash;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.exception.InvalidDDMStructureException;
import com.liferay.journal.internal.util.JournalUtil;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalFolderLocalService;
import com.liferay.journal.util.JournalHelper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ContainerModel;
import com.liferay.portal.kernel.model.TrashedModel;
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
 * Implements trash handling for the journal folder entity.
 *
 * @author Eudaldo Alonso
 */
@Component(
	property = "model.class.name=com.liferay.journal.model.JournalFolder",
	service = TrashHandler.class
)
public class JournalFolderTrashHandler extends BaseJournalTrashHandler {

	@Override
	public void checkRestorableEntry(
			long classPK, long containerModelId, String newName)
		throws PortalException {

		JournalFolder folder = _journalFolderLocalService.getFolder(classPK);

		checkRestorableEntry(
			classPK, 0, containerModelId, folder.getName(), newName);
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
		_journalFolderLocalService.deleteFolder(classPK, false);
	}

	@Override
	public String getClassName() {
		return JournalFolder.class.getName();
	}

	@Override
	public String getDeleteMessage() {
		return "found-in-deleted-folder-x";
	}

	@Override
	public ContainerModel getParentContainerModel(long classPK)
		throws PortalException {

		JournalFolder folder = _getJournalFolder(classPK);

		long parentFolderId = folder.getParentFolderId();

		if (parentFolderId <= 0) {
			return null;
		}

		JournalFolder parentFolder = _journalFolderLocalService.fetchFolder(
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

		JournalFolder folder = _journalFolderLocalService.getFolder(classPK);

		return JournalUtil.getJournalControlPanelLink(
			portletRequest, folder.getFolderId());
	}

	@Override
	public String getRestoreContainerModelLink(
			PortletRequest portletRequest, long classPK)
		throws PortalException {

		JournalFolder folder = _journalFolderLocalService.getFolder(classPK);

		return JournalUtil.getJournalControlPanelLink(
			portletRequest, folder.getParentFolderId());
	}

	@Override
	public String getRestoreMessage(PortletRequest portletRequest, long classPK)
		throws PortalException {

		JournalFolder folder = _journalFolderLocalService.getFolder(classPK);

		return _journalHelper.getAbsolutePath(
			portletRequest, folder.getParentFolderId());
	}

	@Override
	public TrashedModel getTrashedModel(long classPK) {
		return _journalFolderLocalService.fetchFolder(classPK);
	}

	@Override
	public TrashRenderer getTrashRenderer(long classPK) throws PortalException {
		AssetRendererFactory<JournalFolder> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClass(
				JournalFolder.class);

		return (TrashRenderer)assetRendererFactory.getAssetRenderer(classPK);
	}

	@Override
	public boolean hasTrashPermission(
			PermissionChecker permissionChecker, long groupId, long classPK,
			String trashActionId)
		throws PortalException {

		if (trashActionId.equals(TrashActionKeys.MOVE)) {
			return ModelResourcePermissionUtil.contains(
				_journalFolderModelResourcePermission, permissionChecker,
				groupId, classPK, ActionKeys.ADD_FOLDER);
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
		JournalFolder folder = _getJournalFolder(classPK);

		if (folder.getParentFolderId() > 0) {
			JournalFolder parentFolder = _journalFolderLocalService.fetchFolder(
				folder.getParentFolderId());

			if ((parentFolder == null) || parentFolder.isInTrash()) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isRestorable(long classPK) throws PortalException {
		JournalFolder folder = _getJournalFolder(classPK);

		if (folder.getParentFolderId() > 0) {
			JournalFolder parentFolder = _journalFolderLocalService.fetchFolder(
				folder.getParentFolderId());

			if (parentFolder == null) {
				return false;
			}
		}

		if (!hasTrashPermission(
				PermissionThreadLocal.getPermissionChecker(),
				folder.getGroupId(), classPK, TrashActionKeys.RESTORE)) {

			return false;
		}

		return !_trashHelper.isInTrashContainer(folder);
	}

	@Override
	public void moveEntry(
			long userId, long classPK, long containerModelId,
			ServiceContext serviceContext)
		throws PortalException {

		_journalFolderLocalService.moveFolder(
			classPK, containerModelId, serviceContext);
	}

	@Override
	public void moveTrashEntry(
			long userId, long classPK, long containerModelId,
			ServiceContext serviceContext)
		throws PortalException {

		_journalFolderLocalService.moveFolderFromTrash(
			userId, classPK, containerModelId, serviceContext);
	}

	@Override
	public void restoreTrashEntry(long userId, long classPK)
		throws PortalException {

		_journalFolderLocalService.restoreFolderFromTrash(userId, classPK);
	}

	@Override
	public void updateTitle(long classPK, String name) throws PortalException {
		JournalFolder folder = _journalFolderLocalService.getFolder(classPK);

		folder.setName(name);

		_journalFolderLocalService.updateJournalFolder(folder);
	}

	protected void checkDuplicateEntry(
			long classPK, long trashEntryId, long containerModelId,
			String originalTitle, String newName)
		throws PortalException {

		JournalFolder folder = _journalFolderLocalService.getFolder(classPK);

		if (containerModelId == TrashEntryConstants.DEFAULT_CONTAINER_ID) {
			containerModelId = JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID;
		}

		if (Validator.isNotNull(newName)) {
			originalTitle = newName;
		}

		JournalFolder duplicateFolder = _journalFolderLocalService.fetchFolder(
			folder.getGroupId(), containerModelId, originalTitle);

		if (duplicateFolder != null) {
			RestoreEntryException restoreEntryException =
				new RestoreEntryException(RestoreEntryException.DUPLICATE);

			restoreEntryException.setDuplicateEntryId(
				duplicateFolder.getFolderId());
			restoreEntryException.setOldName(duplicateFolder.getName());
			restoreEntryException.setTrashEntryId(trashEntryId);

			throw restoreEntryException;
		}
	}

	protected void checkRestorableEntry(
			long classPK, long trashEntryId, long containerModelId,
			String originalTitle, String newName)
		throws PortalException {

		checkValidContainer(classPK, containerModelId);

		checkDuplicateEntry(
			classPK, trashEntryId, containerModelId, originalTitle, newName);
	}

	protected void checkValidContainer(long classPK, long containerModelId)
		throws PortalException {

		try {
			_journalFolderLocalService.validateFolderDDMStructures(
				classPK, containerModelId);
		}
		catch (InvalidDDMStructureException invalidDDMStructureException) {
			throw new RestoreEntryException(
				RestoreEntryException.INVALID_CONTAINER,
				invalidDDMStructureException);
		}
	}

	@Override
	protected long getGroupId(long classPK) throws PortalException {
		JournalFolder folder = _journalFolderLocalService.getFolder(classPK);

		return folder.getGroupId();
	}

	@Override
	protected boolean hasPermission(
			PermissionChecker permissionChecker, long classPK, String actionId)
		throws PortalException {

		return _journalFolderModelResourcePermission.contains(
			permissionChecker, classPK, actionId);
	}

	private JournalFolder _getJournalFolder(long classPK)
		throws PortalException {

		return _journalFolderLocalService.getFolder(classPK);
	}

	@Reference
	private JournalFolderLocalService _journalFolderLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.journal.model.JournalFolder)"
	)
	private ModelResourcePermission<JournalFolder>
		_journalFolderModelResourcePermission;

	@Reference
	private JournalHelper _journalHelper;

	@Reference
	private TrashHelper _trashHelper;

}