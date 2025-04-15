/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.internal.trash;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.knowledge.base.constants.KBActionKeys;
import com.liferay.knowledge.base.constants.KBFolderConstants;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.model.KBFolder;
import com.liferay.knowledge.base.util.KnowledgeBaseUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ContainerModel;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.trash.TrashRenderer;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.trash.TrashHelper;
import com.liferay.trash.constants.TrashActionKeys;

import jakarta.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Galluzzi
 */
@Component(
	property = "model.class.name=com.liferay.knowledge.base.model.KBArticle",
	service = TrashHandler.class
)
public class KBArticleTrashHandler extends BaseKBTrashHandler {

	@Override
	public void deleteTrashEntry(long classPK) throws PortalException {
		kbArticleLocalService.deleteKBArticle(
			kbArticleLocalService.getLatestKBArticle(classPK));
	}

	@Override
	public String getClassName() {
		return KBArticle.class.getName();
	}

	@Override
	public ContainerModel getParentContainerModel(long classPK)
		throws PortalException {

		KBArticle kbArticle = kbArticleLocalService.getLatestKBArticle(classPK);

		long parentKBFolderId = kbArticle.getKbFolderId();

		if (parentKBFolderId <= 0) {
			return null;
		}

		return getContainerModel(parentKBFolderId);
	}

	@Override
	public ContainerModel getParentContainerModel(TrashedModel trashedModel)
		throws PortalException {

		KBArticle kbArticle = (KBArticle)trashedModel;

		return getContainerModel(kbArticle.getKbFolderId());
	}

	@Override
	public String getRestoreContainerModelLink(
			PortletRequest portletRequest, long classPK)
		throws PortalException {

		KBArticle kbArticle = kbArticleLocalService.getLatestKBArticle(classPK);

		if (!kbArticle.hasParentKBArticle()) {
			return KnowledgeBaseUtil.getKBFolderControlPanelLink(
				portletRequest, kbArticle.getKbFolderId());
		}

		return KnowledgeBaseUtil.getKBArticleControlPanelLink(
			portletRequest, kbArticle.getParentResourcePrimKey());
	}

	@Override
	public String getRestoreMessage(PortletRequest portletRequest, long classPK)
		throws PortalException {

		return KnowledgeBaseUtil.getKBArticleAbsolutePath(
			portletRequest, classPK);
	}

	@Override
	public TrashedModel getTrashedModel(long classPK) {
		return kbArticleLocalService.fetchLatestKBArticle(
			classPK, WorkflowConstants.STATUS_ANY);
	}

	@Override
	public TrashRenderer getTrashRenderer(long classPK) throws PortalException {
		AssetRendererFactory<KBArticle> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				getClassName());

		return (TrashRenderer)assetRendererFactory.getAssetRenderer(
			(KBArticle)getTrashedModel(classPK),
			AssetRendererFactory.TYPE_LATEST_APPROVED);
	}

	@Override
	public boolean hasTrashPermission(
			PermissionChecker permissionChecker, long groupId, long classPK,
			String trashActionId)
		throws PortalException {

		if (trashActionId.equals(TrashActionKeys.MOVE)) {
			return ModelResourcePermissionUtil.contains(
				_kbFolderModelResourcePermission, permissionChecker, groupId,
				classPK, KBActionKeys.ADD_KB_ARTICLE);
		}

		return super.hasTrashPermission(
			permissionChecker, groupId, classPK, trashActionId);
	}

	@Override
	public boolean isMovable(long classPK) throws PortalException {
		KBArticle kbArticle = kbArticleLocalService.getLatestKBArticle(classPK);

		if (kbArticle.getKbFolderId() > 0) {
			KBFolder parentKBFolder = kbFolderLocalService.fetchKBFolder(
				kbArticle.getKbFolderId());

			if ((parentKBFolder == null) || parentKBFolder.isInTrash()) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isRestorable(long classPK) throws PortalException {
		KBArticle kbArticle = kbArticleLocalService.getLatestKBArticle(classPK);

		if (kbArticle.getKbFolderId() > 0) {
			KBFolder kbFolder = kbFolderLocalService.fetchKBFolder(
				kbArticle.getKbFolderId());

			if (kbFolder == null) {
				return false;
			}
		}

		if (hasTrashPermission(
				PermissionThreadLocal.getPermissionChecker(),
				kbArticle.getGroupId(), classPK, TrashActionKeys.RESTORE) &&
			!_trashHelper.isInTrashContainer(kbArticle)) {

			return true;
		}

		return false;
	}

	@Override
	public void moveEntry(
			long userId, long classPK, long containerModelId,
			ServiceContext serviceContext)
		throws PortalException {

		KBArticle kbArticle = kbArticleLocalService.getLatestKBArticle(classPK);

		kbArticleLocalService.moveKBArticle(
			userId, kbArticle.getResourcePrimKey(),
			_classNameLocalService.getClassNameId(
				KBFolderConstants.getClassName()),
			containerModelId, kbArticle.getPriority());
	}

	@Override
	public void moveTrashEntry(
			long userId, long classPK, long containerId,
			ServiceContext serviceContext)
		throws PortalException {

		kbArticleLocalService.moveKBArticleFromTrash(
			userId, classPK,
			_classNameLocalService.getClassNameId(
				KBFolderConstants.getClassName()),
			containerId);
	}

	@Override
	public void restoreTrashEntry(long userId, long classPK)
		throws PortalException {

		kbArticleLocalService.restoreKBArticleFromTrash(userId, classPK);
	}

	@Override
	protected long getGroupId(long classPK) throws PortalException {
		KBArticle kbArticle = kbArticleLocalService.getLatestKBArticle(classPK);

		return kbArticle.getGroupId();
	}

	@Override
	protected boolean hasPermission(
			PermissionChecker permissionChecker, long classPK, String actionId)
		throws PortalException {

		return _kbArticleModelResourcePermission.contains(
			permissionChecker,
			kbArticleLocalService.getLatestKBArticle(classPK), actionId);
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.knowledge.base.model.KBArticle)"
	)
	private ModelResourcePermission<KBArticle>
		_kbArticleModelResourcePermission;

	@Reference(
		target = "(model.class.name=com.liferay.knowledge.base.model.KBFolder)"
	)
	private ModelResourcePermission<KBFolder> _kbFolderModelResourcePermission;

	@Reference
	private TrashHelper _trashHelper;

}