/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.action;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.exportimport.changeset.Changeset;
import com.liferay.exportimport.changeset.portlet.action.ExportImportChangesetMVCActionCommandHelper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Máté Thurzó
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY_ADMIN,
		"mvc.command.name=/document_library/publish_file_entry"
	},
	service = MVCActionCommand.class
)
public class PublishFileEntryMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long fileEntryId = ParamUtil.getLong(actionRequest, "fileEntryId");

		Changeset.Builder builder = Changeset.create();

		Changeset changeset = builder.addStagedModel(
			() -> _fetchFileEntry(fileEntryId)
		).build();

		_exportImportChangesetMVCActionCommandHelper.publish(
			actionRequest, actionResponse, changeset);
	}

	private FileEntry _fetchFileEntry(long fileEntryId) {
		try {
			return _dlAppLocalService.getFileEntry(fileEntryId);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to get file entry " + fileEntryId, portalException);
			}
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PublishFileEntryMVCActionCommand.class);

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private ExportImportChangesetMVCActionCommandHelper
		_exportImportChangesetMVCActionCommandHelper;

}