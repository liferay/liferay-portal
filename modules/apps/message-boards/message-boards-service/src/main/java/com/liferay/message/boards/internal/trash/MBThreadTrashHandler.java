/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.internal.trash;

import com.liferay.asset.util.AssetHelper;
import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.internal.util.MBTrashUtil;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.service.MBCategoryLocalService;
import com.liferay.message.boards.service.MBThreadLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ContainerModel;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.trash.TrashRenderer;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.trash.BaseTrashHandler;
import com.liferay.trash.TrashHelper;
import com.liferay.trash.constants.TrashActionKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.ServletContext;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Implements trash handling for message boards thread entity.
 *
 * @author Zsolt Berentey
 */
@Component(
	property = "model.class.name=com.liferay.message.boards.model.MBThread",
	service = TrashHandler.class
)
public class MBThreadTrashHandler extends BaseTrashHandler {

	@Override
	public void deleteTrashEntry(long classPK) throws PortalException {
		_mbThreadLocalService.deleteThread(classPK);
	}

	@Override
	public String getClassName() {
		return MBThread.class.getName();
	}

	@Override
	public ContainerModel getContainerModel(long containerModelId)
		throws PortalException {

		return _mbCategoryLocalService.getCategory(containerModelId);
	}

	@Override
	public String getContainerModelClassName(long classPK) {
		return MBCategory.class.getName();
	}

	@Override
	public String getContainerModelName() {
		return "category";
	}

	@Override
	public List<ContainerModel> getContainerModels(
			long classPK, long parentContainerModelId, int start, int end)
		throws PortalException {

		List<ContainerModel> containerModels = new ArrayList<>();

		MBThread thread = _mbThreadLocalService.getThread(classPK);

		List<MBCategory> categories = _mbCategoryLocalService.getCategories(
			thread.getGroupId(), parentContainerModelId,
			WorkflowConstants.STATUS_APPROVED, start, end);

		for (MBCategory category : categories) {
			containerModels.add(category);
		}

		return containerModels;
	}

	@Override
	public int getContainerModelsCount(
			long classPK, long parentContainerModelId)
		throws PortalException {

		MBThread thread = _mbThreadLocalService.getThread(classPK);

		return _mbCategoryLocalService.getCategoriesCount(
			thread.getGroupId(), parentContainerModelId,
			WorkflowConstants.STATUS_APPROVED);
	}

	@Override
	public String getRestoreContainedModelLink(
			PortletRequest portletRequest, long classPK)
		throws PortalException {

		MBThread thread = _mbThreadLocalService.getThread(classPK);

		return PortletURLBuilder.create(
			getRestoreURL(portletRequest, classPK, false)
		).setParameter(
			"mbCategoryId", thread.getCategoryId()
		).setParameter(
			"messageId", thread.getRootMessageId()
		).buildString();
	}

	@Override
	public String getRestoreContainerModelLink(
			PortletRequest portletRequest, long classPK)
		throws PortalException {

		MBThread thread = _mbThreadLocalService.getThread(classPK);

		return PortletURLBuilder.create(
			getRestoreURL(portletRequest, classPK, true)
		).setParameter(
			"mbCategoryId", thread.getCategoryId()
		).buildString();
	}

	@Override
	public String getRestoreMessage(PortletRequest portletRequest, long classPK)
		throws PortalException {

		MBThread thread = _mbThreadLocalService.getThread(classPK);

		return MBTrashUtil.getAbsolutePath(
			portletRequest, thread.getCategoryId());
	}

	@Override
	public TrashedModel getTrashedModel(long classPK) {
		return _mbThreadLocalService.fetchMBThread(classPK);
	}

	@Override
	public TrashRenderer getTrashRenderer(long classPK) throws PortalException {
		MBThreadTrashRenderer mbThreadTrashRenderer = new MBThreadTrashRenderer(
			_mbThreadLocalService.getThread(classPK), _assetHelper);

		mbThreadTrashRenderer.setServletContext(_servletContext);

		return mbThreadTrashRenderer;
	}

	@Override
	public boolean hasTrashPermission(
			PermissionChecker permissionChecker, long groupId, long classPK,
			String trashActionId)
		throws PortalException {

		if (trashActionId.equals(TrashActionKeys.MOVE)) {
			return ModelResourcePermissionUtil.contains(
				_categoryModelResourcePermission, permissionChecker, groupId,
				classPK, ActionKeys.ADD_MESSAGE);
		}

		return super.hasTrashPermission(
			permissionChecker, groupId, classPK, trashActionId);
	}

	@Override
	public boolean isMovable(long classPK) throws PortalException {
		MBThread thread = _mbThreadLocalService.getThread(classPK);

		if (thread.getCategoryId() > 0) {
			MBCategory parentCategory = _mbCategoryLocalService.fetchMBCategory(
				thread.getCategoryId());

			if ((parentCategory == null) || parentCategory.isInTrash()) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isRestorable(long classPK) throws PortalException {
		MBThread thread = _mbThreadLocalService.getThread(classPK);

		if (((thread.getCategoryId() > 0) &&
			 (_mbCategoryLocalService.fetchMBCategory(thread.getCategoryId()) ==
				 null)) ||
			!hasTrashPermission(
				PermissionThreadLocal.getPermissionChecker(),
				thread.getGroupId(), classPK, TrashActionKeys.RESTORE)) {

			return false;
		}

		return !_trashHelper.isInTrashContainer(thread);
	}

	@Override
	public void moveEntry(
			long userId, long classPK, long containerModelId,
			ServiceContext serviceContext)
		throws PortalException {

		_mbThreadLocalService.moveThread(userId, containerModelId, classPK);
	}

	@Override
	public void moveTrashEntry(
			long userId, long classPK, long containerModelId,
			ServiceContext serviceContext)
		throws PortalException {

		_mbThreadLocalService.moveThreadFromTrash(
			userId, containerModelId, classPK);
	}

	@Override
	public void restoreTrashEntry(long userId, long classPK)
		throws PortalException {

		_mbThreadLocalService.restoreThreadFromTrash(userId, classPK);
	}

	protected PortletURL getRestoreURL(
			PortletRequest portletRequest, long classPK, boolean containerModel)
		throws PortalException {

		PortletURL portletURL = null;

		MBThread thread = _mbThreadLocalService.getThread(classPK);
		String portletId = PortletProviderUtil.getPortletId(
			MBThread.class.getName(), PortletProvider.Action.EDIT);

		long plid = _portal.getPlidFromPortletId(
			thread.getGroupId(), portletId);

		if (plid == LayoutConstants.DEFAULT_PLID) {
			portletId = PortletProviderUtil.getPortletId(
				MBThread.class.getName(), PortletProvider.Action.MANAGE);

			portletURL = _portal.getControlPanelPortletURL(
				portletRequest, portletId, PortletRequest.RENDER_PHASE);
		}
		else {
			portletURL = PortletURLFactoryUtil.create(
				portletRequest, portletId, plid, PortletRequest.RENDER_PHASE);
		}

		if (containerModel) {
			String mvcRenderCommandName = "/message_boards/view";

			if (thread.getCategoryId() !=
					MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID) {

				mvcRenderCommandName = "/message_boards/view_category";
			}

			portletURL.setParameter(
				"mvcRenderCommandName", mvcRenderCommandName);
		}
		else {
			portletURL.setParameter(
				"mvcRenderCommandName", "/message_boards/view_message");
		}

		return portletURL;
	}

	@Override
	protected boolean hasPermission(
			PermissionChecker permissionChecker, long classPK, String actionId)
		throws PortalException {

		MBThread thread = _mbThreadLocalService.getThread(classPK);

		return _messageModelResourcePermission.contains(
			permissionChecker, thread.getRootMessageId(), actionId);
	}

	@Reference
	private AssetHelper _assetHelper;

	@Reference(
		target = "(model.class.name=com.liferay.message.boards.model.MBCategory)"
	)
	private ModelResourcePermission<MBCategory>
		_categoryModelResourcePermission;

	@Reference
	private MBCategoryLocalService _mbCategoryLocalService;

	@Reference
	private MBThreadLocalService _mbThreadLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.message.boards.model.MBMessage)"
	)
	private ModelResourcePermission<MBMessage> _messageModelResourcePermission;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.message.boards.web)"
	)
	private ServletContext _servletContext;

	@Reference
	private TrashHelper _trashHelper;

}