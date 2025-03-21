/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.action;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.exportimport.changeset.Changeset;
import com.liferay.exportimport.changeset.portlet.action.ExportImportChangesetMVCActionCommandHelper;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerRegistryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Máté Thurzó
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY_ADMIN,
		"mvc.command.name=/document_library/publish_folder"
	},
	service = MVCActionCommand.class
)
public class PublishFolderMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long folderId = ParamUtil.getLong(actionRequest, "folderId");

		Folder folder = _dlAppLocalService.getFolder(folderId);

		Changeset.Builder builder = Changeset.create();

		Changeset changeset = builder.addStagedModel(
			() -> _getFolder(folderId)
		).addMultipleStagedModel(
			() -> _getFoldersAndFileEntriesAndFileShortcuts(folder)
		).build();

		_exportImportChangesetMVCActionCommandHelper.publish(
			actionRequest, actionResponse, changeset);
	}

	private Folder _getFolder(long folderId) {
		try {
			return _dlAppLocalService.getFolder(folderId);
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to get folder " + folderId, portalException);
			}

			return null;
		}
	}

	private List<StagedModel> _getFoldersAndFileEntriesAndFileShortcuts(
		Folder folder) {

		if (folder == null) {
			return Collections.emptyList();
		}

		List<StagedModel> stagedModels = new ArrayList<>();

		try {
			List<Object> childObjects =
				_dlAppService.getFoldersAndFileEntriesAndFileShortcuts(
					folder.getGroupId(), folder.getFolderId(),
					WorkflowConstants.STATUS_ANY, false, -1, -1);

			for (Object childObject : childObjects) {
				if (childObject instanceof Folder) {
					Folder childFolder = (Folder)childObject;

					stagedModels.add(childFolder);

					stagedModels.addAll(
						_getFoldersAndFileEntriesAndFileShortcuts(childFolder));
				}
				else if (childObject instanceof FileEntry) {
					FileEntry fileEntry = (FileEntry)childObject;

					FileVersion fileVersion = fileEntry.getFileVersion();

					StagedModelDataHandler<FileEntry> stagedModelDataHandler =
						_getStagedModelDataHandler();

					if (ArrayUtil.contains(
							stagedModelDataHandler.getExportableStatuses(),
							fileVersion.getStatus())) {

						stagedModels.add((StagedModel)childObject);
					}
				}
				else {
					stagedModels.add((StagedModel)childObject);
				}
			}
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get folders, file entries, and file shortcuts " +
						"for folder " + folder.getFolderId(),
					portalException);
			}
		}

		return stagedModels;
	}

	private StagedModelDataHandler<FileEntry> _getStagedModelDataHandler() {
		return (StagedModelDataHandler<FileEntry>)
			StagedModelDataHandlerRegistryUtil.getStagedModelDataHandler(
				FileEntry.class.getName());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PublishFolderMVCActionCommand.class);

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private ExportImportChangesetMVCActionCommandHelper
		_exportImportChangesetMVCActionCommandHelper;

}