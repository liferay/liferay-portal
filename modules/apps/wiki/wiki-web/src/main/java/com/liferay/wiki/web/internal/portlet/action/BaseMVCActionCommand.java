/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.portlet.action;

import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.repository.capabilities.TrashCapability;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.wiki.constants.WikiConstants;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiPageService;

import jakarta.portlet.ActionRequest;

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Regisson Aguiar
 */
public abstract class BaseMVCActionCommand
	extends com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand {

	protected void addAttachments(ActionRequest actionRequest)
		throws Exception {

		UploadPortletRequest uploadPortletRequest =
			portal.getUploadPortletRequest(actionRequest);

		long nodeId = ParamUtil.getLong(actionRequest, "nodeId");
		String title = ParamUtil.getString(actionRequest, "title");

		int numOfFiles = ParamUtil.getInteger(actionRequest, "numOfFiles");

		List<ObjectValuePair<String, InputStream>> inputStreamOVPs =
			new ArrayList<>();
		List<FileEntry> tempFileEntries = new ArrayList<>();

		try {
			if (numOfFiles == 0) {
				InputStream inputStream = uploadPortletRequest.getFileAsStream(
					"file");

				if (inputStream != null) {
					ObjectValuePair<String, InputStream> inputStreamOVP =
						new ObjectValuePair<>(
							uploadPortletRequest.getFileName("file"),
							inputStream);

					inputStreamOVPs.add(inputStreamOVP);
				}
			}
			else {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)actionRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				String[] selectUploadedFiles = ParamUtil.getParameterValues(
					actionRequest, "selectUploadedFile");

				for (String selectUploadedFile : selectUploadedFiles) {
					FileEntry tempFileEntry =
						TempFileEntryUtil.getTempFileEntry(
							themeDisplay.getScopeGroupId(),
							themeDisplay.getUserId(),
							WikiConstants.TEMP_FOLDER_NAME, selectUploadedFile);

					WikiPage wikiPage = wikiPageService.getPage(nodeId, title);

					String uniqueFileName = DLUtil.getUniqueFileName(
						wikiPage.getGroupId(),
						wikiPage.getAttachmentsFolderId(),
						TempFileEntryUtil.getOriginalTempFileName(
							tempFileEntry.getFileName()),
						false);

					ObjectValuePair<String, InputStream> inputStreamOVP =
						new ObjectValuePair<>(
							uniqueFileName, tempFileEntry.getContentStream());

					inputStreamOVPs.add(inputStreamOVP);

					tempFileEntries.add(tempFileEntry);
				}
			}

			if (ListUtil.isNotEmpty(inputStreamOVPs)) {
				wikiPageService.addPageAttachments(
					nodeId, title, inputStreamOVPs);
			}
		}
		finally {
			for (ObjectValuePair<String, InputStream> inputStreamOVP :
					inputStreamOVPs) {

				try (InputStream inputStream = inputStreamOVP.getValue()) {
				}
				catch (IOException ioException) {
					if (_log.isWarnEnabled()) {
						_log.warn(ioException);
					}
				}
			}

			for (FileEntry tempFileEntry : tempFileEntries) {
				TempFileEntryUtil.deleteTempFileEntry(
					tempFileEntry.getFileEntryId());
			}
		}
	}

	protected TrashedModel deleteAttachment(
			ActionRequest actionRequest, boolean moveToTrash)
		throws Exception {

		long nodeId = ParamUtil.getLong(actionRequest, "nodeId");
		String title = ParamUtil.getString(actionRequest, "title");
		String attachment = ParamUtil.getString(actionRequest, "fileName");

		TrashedModel trashedModel = null;

		if (moveToTrash) {
			FileEntry fileEntry = wikiPageService.movePageAttachmentToTrash(
				nodeId, title, attachment);

			if (fileEntry.isRepositoryCapabilityProvided(
					TrashCapability.class)) {

				trashedModel = (TrashedModel)fileEntry.getModel();
			}
		}
		else {
			wikiPageService.deletePageAttachment(nodeId, title, attachment);
		}

		return trashedModel;
	}

	protected void emptyTrash(ActionRequest actionRequest) throws Exception {
		long nodeId = ParamUtil.getLong(actionRequest, "nodeId");
		String title = ParamUtil.getString(actionRequest, "title");

		wikiPageService.deleteTrashPageAttachments(nodeId, title);
	}

	protected void restoreEntries(ActionRequest actionRequest)
		throws Exception {

		long nodeId = ParamUtil.getLong(actionRequest, "nodeId");
		String title = ParamUtil.getString(actionRequest, "title");
		String fileName = ParamUtil.getString(actionRequest, "fileName");

		wikiPageService.restorePageAttachmentFromTrash(nodeId, title, fileName);
	}

	@Reference
	protected Portal portal;

	@Reference
	protected WikiPageService wikiPageService;

	private static final Log _log = LogFactoryUtil.getLog(
		BaseMVCActionCommand.class);

}