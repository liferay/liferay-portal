/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.internal.trash;

import com.liferay.knowledge.base.constants.KBActionKeys;
import com.liferay.knowledge.base.model.KBFolder;
import com.liferay.knowledge.base.util.KnowledgeBaseUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ContainerModel;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.trash.TrashHelper;
import com.liferay.trash.constants.TrashActionKeys;

import jakarta.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Galluzzi
 */
@Component(
	property = "model.class.name=com.liferay.knowledge.base.model.KBFolder",
	service = TrashHandler.class
)
public class KBFolderTrashHandler extends BaseKBTrashHandler {

	@Override
	public void deleteTrashEntry(long classPK) throws PortalException {
		kbFolderLocalService.deleteKBFolder(classPK, false);
	}

	@Override
	public String getClassName() {
		return KBFolder.class.getName();
	}

	@Override
	public String getDeleteMessage() {
		return "found-in-deleted-folder-x";
	}

	@Override
	public ContainerModel getParentContainerModel(long classPK)
		throws PortalException {

		KBFolder kbFolder = kbFolderLocalService.getKBFolder(classPK);

		return getContainerModel(kbFolder.getParentKBFolderId());
	}

	@Override
	public String getRestoreContainedModelLink(
			PortletRequest portletRequest, long classPK)
		throws PortalException {

		return KnowledgeBaseUtil.getKBFolderControlPanelLink(
			portletRequest, classPK);
	}

	@Override
	public String getRestoreContainerModelLink(
			PortletRequest portletRequest, long classPK)
		throws PortalException {

		KBFolder kbFolder = kbFolderLocalService.getKBFolder(classPK);

		return KnowledgeBaseUtil.getKBFolderControlPanelLink(
			portletRequest, kbFolder.getParentKBFolderId());
	}

	@Override
	public String getRestoreMessage(PortletRequest portletRequest, long classPK)
		throws PortalException {

		KBFolder kbFolder = kbFolderLocalService.getKBFolder(classPK);

		return KnowledgeBaseUtil.getKBFolderAbsolutePath(
			portletRequest, kbFolder.getParentKBFolderId());
	}

	@Override
	public TrashedModel getTrashedModel(long classPK) {
		return kbFolderLocalService.fetchKBFolder(classPK);
	}

	@Override
	public boolean hasTrashPermission(
			PermissionChecker permissionChecker, long groupId, long classPK,
			String trashActionId)
		throws PortalException {

		if (trashActionId.equals(TrashActionKeys.MOVE)) {
			return ModelResourcePermissionUtil.contains(
				_kbFolderModelResourcePermission, permissionChecker, groupId,
				classPK, KBActionKeys.ADD_KB_FOLDER);
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
		KBFolder kbFolder = kbFolderLocalService.getKBFolder(classPK);

		if (kbFolder.getParentKBFolderId() > 0) {
			KBFolder parentFolder = kbFolderLocalService.fetchKBFolder(
				kbFolder.getParentKBFolderId());

			if ((parentFolder == null) || parentFolder.isInTrash()) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isRestorable(long classPK) throws PortalException {
		KBFolder kbFolder = kbFolderLocalService.getKBFolder(classPK);

		if (kbFolder.getParentKBFolderId() > 0) {
			KBFolder parentFolder = kbFolderLocalService.fetchKBFolder(
				kbFolder.getParentKBFolderId());

			if (parentFolder == null) {
				return false;
			}
		}

		if (hasTrashPermission(
				PermissionThreadLocal.getPermissionChecker(),
				kbFolder.getGroupId(), classPK, TrashActionKeys.RESTORE) &&
			!_trashHelper.isInTrashContainer(kbFolder)) {

			return true;
		}

		return false;
	}

	@Override
	public void moveEntry(
			long userId, long classPK, long containerModelId,
			ServiceContext serviceContext)
		throws PortalException {

		kbFolderLocalService.moveKBFolder(classPK, containerModelId);
	}

	@Override
	public void moveTrashEntry(
			long userId, long classPK, long containerModelId,
			ServiceContext serviceContext)
		throws PortalException {

		kbFolderLocalService.moveKBFolderFromTrash(
			userId, classPK, containerModelId);
	}

	@Override
	public void restoreTrashEntry(long userId, long classPK)
		throws PortalException {

		kbFolderLocalService.restoreKBFolderFromTrash(userId, classPK);
	}

	@Override
	protected long getGroupId(long classPK) throws PortalException {
		KBFolder kbFolder = kbFolderLocalService.getKBFolder(classPK);

		return kbFolder.getGroupId();
	}

	@Override
	protected boolean hasPermission(
			PermissionChecker permissionChecker, long classPK, String actionId)
		throws PortalException {

		return _kbFolderModelResourcePermission.contains(
			permissionChecker, kbFolderLocalService.getKBFolder(classPK),
			actionId);
	}

	@Reference(
		target = "(model.class.name=com.liferay.knowledge.base.model.KBFolder)"
	)
	private ModelResourcePermission<KBFolder> _kbFolderModelResourcePermission;

	@Reference
	private TrashHelper _trashHelper;

}