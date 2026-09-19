/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.internal.trash;

import com.liferay.knowledge.base.constants.KBFolderConstants;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.model.KBFolder;
import com.liferay.knowledge.base.service.KBArticleLocalService;
import com.liferay.knowledge.base.service.KBFolderLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ContainerModel;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.trash.BaseTrashHandler;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Galluzzi
 */
public abstract class BaseKBTrashHandler extends BaseTrashHandler {

	@Override
	public ContainerModel getContainerModel(long containerModelId)
		throws PortalException {

		if (containerModelId == KBFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return null;
		}

		return kbFolderLocalService.getKBFolder(containerModelId);
	}

	@Override
	public String getContainerModelClassName(long classPK) {
		return KBFolder.class.getName();
	}

	@Override
	public String getContainerModelName() {
		return "folder";
	}

	@Override
	public List<ContainerModel> getContainerModels(
			long classPK, long containerModelId, int start, int end)
		throws PortalException {

		List<KBFolder> kbFolders = kbFolderLocalService.getKBFolders(
			getGroupId(classPK), containerModelId, start, end);

		List<ContainerModel> containerModels = new ArrayList<>(
			kbFolders.size());

		containerModels.addAll(kbFolders);

		return containerModels;
	}

	@Override
	public int getContainerModelsCount(long classPK, long containerModelId)
		throws PortalException {

		return kbFolderLocalService.getKBFoldersCount(
			getGroupId(classPK), containerModelId);
	}

	@Override
	public List<ContainerModel> getParentContainerModels(long classPK)
		throws PortalException {

		List<ContainerModel> containerModels = new ArrayList<>();

		ContainerModel containerModel = getParentContainerModel(classPK);

		if (containerModel == null) {
			return containerModels;
		}

		containerModels.add(containerModel);

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
	public String getRootContainerModelName() {
		return "home";
	}

	@Override
	public String getSubcontainerModelName() {
		return "folder";
	}

	@Override
	public String getTrashContainedModelName() {
		return "kb-articles";
	}

	@Override
	public int getTrashContainedModelsCount(long classPK)
		throws PortalException {

		KBFolder kbFolder = kbFolderLocalService.getKBFolder(classPK);

		return kbArticleLocalService.getKBArticlesCount(
			kbFolder.getGroupId(), classPK, WorkflowConstants.STATUS_IN_TRASH);
	}

	@Override
	public String getTrashContainerModelName() {
		return "folders";
	}

	@Override
	public int getTrashContainerModelsCount(long classPK)
		throws PortalException {

		KBFolder kbFolder = kbFolderLocalService.getKBFolder(classPK);

		return kbFolderLocalService.getKBFoldersCount(
			kbFolder.getGroupId(), classPK, WorkflowConstants.STATUS_IN_TRASH);
	}

	@Override
	public List<TrashedModel> getTrashModelTrashedModels(
			long classPK, int start, int end,
			OrderByComparator<?> orderByComparator)
		throws PortalException {

		KBFolder kbFolder = kbFolderLocalService.getKBFolder(classPK);

		return TransformUtil.transform(
			kbFolderLocalService.getKBFoldersAndKBArticles(
				kbFolder.getGroupId(), classPK,
				WorkflowConstants.STATUS_IN_TRASH, start, end,
				orderByComparator),
			object -> {
				if (object instanceof KBFolder) {
					return (KBFolder)object;
				}

				return (KBArticle)object;
			});
	}

	@Override
	public int getTrashModelsCount(long classPK) throws PortalException {
		KBFolder kbFolder = kbFolderLocalService.getKBFolder(classPK);

		return kbFolderLocalService.getKBFoldersAndKBArticlesCount(
			kbFolder.getGroupId(), classPK, WorkflowConstants.STATUS_IN_TRASH);
	}

	protected abstract long getGroupId(long classPK) throws PortalException;

	@Reference
	protected KBArticleLocalService kbArticleLocalService;

	@Reference
	protected KBFolderLocalService kbFolderLocalService;

}