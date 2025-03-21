/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.internal.trash;

import com.liferay.message.boards.internal.util.MBTrashUtil;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.service.MBCategoryLocalService;
import com.liferay.message.boards.service.MBThreadLocalService;
import com.liferay.petra.function.transform.TransformUtil;
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
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.trash.BaseTrashHandler;
import com.liferay.trash.TrashHelper;
import com.liferay.trash.constants.TrashActionKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Implements trash handling for the message boards category entity.
 *
 * @author Eduardo García
 */
@Component(
	property = "model.class.name=com.liferay.message.boards.model.MBCategory",
	service = TrashHandler.class
)
public class MBCategoryTrashHandler extends BaseTrashHandler {

	@Override
	public void deleteTrashEntry(long classPK) throws PortalException {
		_mbCategoryLocalService.deleteCategory(
			_mbCategoryLocalService.getCategory(classPK), false);
	}

	@Override
	public String getClassName() {
		return MBCategory.class.getName();
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

		MBCategory category = _mbCategoryLocalService.getCategory(classPK);

		return TransformUtil.transform(
			_mbCategoryLocalService.getCategories(
				category.getGroupId(), parentContainerModelId,
				WorkflowConstants.STATUS_APPROVED, start, end),
			curCategory -> curCategory);
	}

	@Override
	public int getContainerModelsCount(
			long classPK, long parentContainerModelId)
		throws PortalException {

		MBCategory category = _mbCategoryLocalService.getCategory(classPK);

		return _mbCategoryLocalService.getCategoriesCount(
			category.getGroupId(), parentContainerModelId,
			WorkflowConstants.STATUS_APPROVED);
	}

	@Override
	public String getDeleteMessage() {
		return "found-in-deleted-category-x";
	}

	@Override
	public List<ContainerModel> getParentContainerModels(long containerModelId)
		throws PortalException {

		List<ContainerModel> containerModels = new ArrayList<>();

		ContainerModel containerModel = getContainerModel(containerModelId);

		while (containerModel.getParentContainerModelId() > 0) {
			containerModel = getContainerModel(
				containerModel.getParentContainerModelId());

			if (containerModel == null) {
				break;
			}

			containerModels.add(containerModel);
		}

		return containerModels;
	}

	@Override
	public String getRestoreContainedModelLink(
			PortletRequest portletRequest, long classPK)
		throws PortalException {

		MBCategory category = _mbCategoryLocalService.getCategory(classPK);

		return PortletURLBuilder.create(
			getRestoreURL(portletRequest, classPK)
		).setParameter(
			"mbCategoryId", category.getCategoryId()
		).buildString();
	}

	@Override
	public String getRestoreContainerModelLink(
			PortletRequest portletRequest, long classPK)
		throws PortalException {

		MBCategory category = _mbCategoryLocalService.getCategory(classPK);

		return PortletURLBuilder.create(
			getRestoreURL(portletRequest, classPK)
		).setParameter(
			"mbCategoryId", category.getParentCategoryId()
		).buildString();
	}

	@Override
	public String getRestoreMessage(PortletRequest portletRequest, long classPK)
		throws PortalException {

		MBCategory category = _mbCategoryLocalService.getCategory(classPK);

		return MBTrashUtil.getAbsolutePath(
			portletRequest, category.getParentCategoryId());
	}

	@Override
	public String getRootContainerModelName() {
		return "category";
	}

	@Override
	public String getSubcontainerModelName() {
		return "category";
	}

	@Override
	public String getTrashContainedModelName() {
		return "threads";
	}

	@Override
	public int getTrashContainedModelsCount(long classPK)
		throws PortalException {

		MBCategory category = _mbCategoryLocalService.getCategory(classPK);

		return _mbThreadLocalService.getThreadsCount(
			category.getGroupId(), classPK, WorkflowConstants.STATUS_IN_TRASH);
	}

	@Override
	public String getTrashContainerModelName() {
		return "categories";
	}

	@Override
	public int getTrashContainerModelsCount(long classPK)
		throws PortalException {

		MBCategory category = _mbCategoryLocalService.getCategory(classPK);

		return _mbCategoryLocalService.getCategoriesCount(
			category.getGroupId(), classPK, WorkflowConstants.STATUS_IN_TRASH);
	}

	@Override
	public TrashedModel getTrashedModel(long classPK) {
		return _mbCategoryLocalService.fetchMBCategory(classPK);
	}

	@Override
	public int getTrashModelsCount(long classPK) throws PortalException {
		MBCategory category = _mbCategoryLocalService.getCategory(classPK);

		return _mbCategoryLocalService.getCategoriesAndThreadsCount(
			category.getGroupId(), classPK, WorkflowConstants.STATUS_IN_TRASH);
	}

	@Override
	public List<TrashedModel> getTrashModelTrashedModels(
			long classPK, int start, int end,
			OrderByComparator<?> orderByComparator)
		throws PortalException {

		MBCategory category = _mbCategoryLocalService.getCategory(classPK);

		List<Object> categoriesAndThreads =
			_mbCategoryLocalService.getCategoriesAndThreads(
				category.getGroupId(), classPK,
				WorkflowConstants.STATUS_IN_TRASH, start, end);

		List<TrashedModel> trashedModels = new ArrayList<>(
			categoriesAndThreads.size());

		for (Object categoryOrThread : categoriesAndThreads) {
			if (categoryOrThread instanceof MBThread) {
				MBThread mbThread = (MBThread)categoryOrThread;

				trashedModels.add(mbThread);
			}
			else if (categoryOrThread instanceof MBCategory) {
				MBCategory mbCategory = (MBCategory)categoryOrThread;

				trashedModels.add(mbCategory);
			}
			else {
				throw new IllegalStateException(
					"Expected MBThread or MBCategory, received " +
						categoryOrThread.getClass());
			}
		}

		return trashedModels;
	}

	@Override
	public TrashRenderer getTrashRenderer(long classPK) throws PortalException {
		return new MBCategoryTrashRenderer(
			_mbCategoryLocalService.getCategory(classPK));
	}

	@Override
	public boolean hasTrashPermission(
			PermissionChecker permissionChecker, long groupId, long classPK,
			String trashActionId)
		throws PortalException {

		if (trashActionId.equals(TrashActionKeys.MOVE)) {
			return ModelResourcePermissionUtil.contains(
				_categoryModelResourcePermission, permissionChecker, groupId,
				classPK, ActionKeys.ADD_CATEGORY);
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
		MBCategory category = _mbCategoryLocalService.getCategory(classPK);

		if (category.getParentCategoryId() > 0) {
			MBCategory parentCategory = _mbCategoryLocalService.fetchMBCategory(
				category.getParentCategoryId());

			if ((parentCategory == null) || parentCategory.isInTrash()) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isRestorable(long classPK) throws PortalException {
		MBCategory category = _mbCategoryLocalService.getCategory(classPK);

		if (category.getParentCategoryId() > 0) {
			MBCategory parentCategory = _mbCategoryLocalService.fetchMBCategory(
				category.getParentCategoryId());

			if (parentCategory == null) {
				return false;
			}
		}

		if (!hasTrashPermission(
				PermissionThreadLocal.getPermissionChecker(),
				category.getGroupId(), classPK, TrashActionKeys.RESTORE)) {

			return false;
		}

		return !_trashHelper.isInTrashContainer(category);
	}

	@Override
	public void moveEntry(
			long userId, long classPK, long containerModelId,
			ServiceContext serviceContext)
		throws PortalException {

		_mbCategoryLocalService.moveCategory(classPK, containerModelId, false);
	}

	@Override
	public void moveTrashEntry(
			long userId, long classPK, long containerModelId,
			ServiceContext serviceContext)
		throws PortalException {

		_mbCategoryLocalService.moveCategoryFromTrash(
			userId, classPK, containerModelId);
	}

	@Override
	public void restoreTrashEntry(long userId, long classPK)
		throws PortalException {

		_mbCategoryLocalService.restoreCategoryFromTrash(userId, classPK);
	}

	@Override
	public void updateTitle(long classPK, String name) throws PortalException {
		MBCategory category = _mbCategoryLocalService.getCategory(classPK);

		category.setName(name);

		_mbCategoryLocalService.updateMBCategory(category);
	}

	protected PortletURL getRestoreURL(
			PortletRequest portletRequest, long classPK)
		throws PortalException {

		PortletURL portletURL = null;

		MBCategory category = _mbCategoryLocalService.getCategory(classPK);
		String portletId = PortletProviderUtil.getPortletId(
			MBCategory.class.getName(), PortletProvider.Action.EDIT);

		long plid = _portal.getPlidFromPortletId(
			category.getGroupId(), portletId);

		if (plid == LayoutConstants.DEFAULT_PLID) {
			portletId = PortletProviderUtil.getPortletId(
				MBCategory.class.getName(), PortletProvider.Action.MANAGE);

			portletURL = _portal.getControlPanelPortletURL(
				portletRequest, portletId, PortletRequest.RENDER_PHASE);
		}
		else {
			portletURL = PortletURLFactoryUtil.create(
				portletRequest, portletId, plid, PortletRequest.RENDER_PHASE);
		}

		portletURL.setParameter(
			"mvcRenderCommandName", "/message_boards/view_category");

		return portletURL;
	}

	@Override
	protected boolean hasPermission(
			PermissionChecker permissionChecker, long classPK, String actionId)
		throws PortalException {

		return _categoryModelResourcePermission.contains(
			permissionChecker, _mbCategoryLocalService.getCategory(classPK),
			actionId);
	}

	@Reference(
		target = "(model.class.name=com.liferay.message.boards.model.MBCategory)"
	)
	private ModelResourcePermission<MBCategory>
		_categoryModelResourcePermission;

	@Reference
	private MBCategoryLocalService _mbCategoryLocalService;

	@Reference
	private MBThreadLocalService _mbThreadLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private TrashHelper _trashHelper;

}