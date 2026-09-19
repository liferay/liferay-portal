/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.trash;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileShortcut;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ContainerModel;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.repository.DocumentRepository;
import com.liferay.portal.kernel.repository.Repository;
import com.liferay.portal.kernel.repository.RepositoryProviderUtil;
import com.liferay.portal.kernel.repository.capabilities.TrashCapability;
import com.liferay.portal.kernel.repository.capabilities.UnsupportedCapabilityException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileShortcut;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.trash.BaseTrashHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Zsolt Berentey
 */
public abstract class BaseDLTrashHandler extends BaseTrashHandler {

	@Override
	public ContainerModel getContainerModel(long containerModelId)
		throws PortalException {

		if (containerModelId == DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return null;
		}

		return getDLFolder(containerModelId);
	}

	@Override
	public String getContainerModelClassName(long classPK) {
		return DLFolder.class.getName();
	}

	@Override
	public String getContainerModelName() {
		return "folder";
	}

	@Override
	public List<ContainerModel> getContainerModels(
			long classPK, long parentContainerModelId, int start, int end)
		throws PortalException {

		DocumentRepository documentRepository = getDocumentRepository(classPK);

		return TransformUtil.transform(
			documentRepository.getFolders(
				parentContainerModelId, false, start, end, null),
			folder -> (ContainerModel)folder.getModel());
	}

	@Override
	public int getContainerModelsCount(
			long classPK, long parentContainerModelId)
		throws PortalException {

		DocumentRepository documentRepository = getDocumentRepository(classPK);

		return documentRepository.getFoldersCount(
			parentContainerModelId, false);
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
	public String getTrashContainedModelName() {
		return "documents";
	}

	@Override
	public int getTrashContainedModelsCount(long classPK)
		throws PortalException {

		DocumentRepository documentRepository = getDocumentRepository(classPK);

		return documentRepository.getFileEntriesAndFileShortcutsCount(
			classPK, WorkflowConstants.STATUS_IN_TRASH);
	}

	@Override
	public String getTrashContainerModelName() {
		return "folders";
	}

	@Override
	public int getTrashContainerModelsCount(long classPK)
		throws PortalException {

		DocumentRepository documentRepository = getDocumentRepository(classPK);

		return documentRepository.getFoldersCount(
			classPK, WorkflowConstants.STATUS_IN_TRASH, false);
	}

	@Override
	public List<TrashedModel> getTrashModelTrashedModels(
			long classPK, int start, int end,
			OrderByComparator<?> orderByComparator)
		throws PortalException {

		DocumentRepository documentRepository = getDocumentRepository(classPK);

		return TransformUtil.transform(
			documentRepository.getFoldersAndFileEntriesAndFileShortcuts(
				classPK, WorkflowConstants.STATUS_IN_TRASH, false, start, end,
				orderByComparator),
			repositoryEntry -> {
				if (repositoryEntry instanceof FileShortcut) {
					FileShortcut fileShortcut = (FileShortcut)repositoryEntry;

					return (DLFileShortcut)fileShortcut.getModel();
				}
				else if (repositoryEntry instanceof FileEntry) {
					FileEntry fileEntry = (FileEntry)repositoryEntry;

					return (DLFileEntry)fileEntry.getModel();
				}

				Folder folder = (Folder)repositoryEntry;

				return (DLFolder)folder.getModel();
			});
	}

	@Override
	public int getTrashModelsCount(long classPK) throws PortalException {
		DocumentRepository documentRepository = getDocumentRepository(classPK);

		return documentRepository.getFileEntriesAndFileShortcutsCount(
			classPK, WorkflowConstants.STATUS_IN_TRASH);
	}

	protected DLFolder fetchDLFolder(long classPK) throws PortalException {
		Repository repository = RepositoryProviderUtil.getFolderRepository(
			classPK);

		if (!repository.isCapabilityProvided(TrashCapability.class)) {
			return null;
		}

		Folder folder = repository.getFolder(classPK);

		return (DLFolder)folder.getModel();
	}

	protected DLFolder getDLFolder(long classPK) throws PortalException {
		Repository repository = RepositoryProviderUtil.getFolderRepository(
			classPK);

		if (!repository.isCapabilityProvided(TrashCapability.class)) {
			throw new UnsupportedCapabilityException(
				TrashCapability.class,
				"Repository " + repository.getRepositoryId());
		}

		Folder folder = repository.getFolder(classPK);

		return (DLFolder)folder.getModel();
	}

	protected abstract DocumentRepository getDocumentRepository(long classPK)
		throws PortalException;

}