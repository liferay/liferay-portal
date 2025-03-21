/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.portlet.action;

import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.service.JournalFolderService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + JournalPortletKeys.JOURNAL,
		"mvc.command.name=/journal/delete_articles_and_folders"
	},
	service = MVCActionCommand.class
)
public class DeleteArticlesAndFoldersMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long[] deleteFolderIds = ParamUtil.getLongValues(
			actionRequest, "rowIdsJournalFolder");

		for (long deleteFolderId : deleteFolderIds) {
			_journalFolderService.deleteFolder(deleteFolderId);
		}

		String[] deleteArticleIds = ParamUtil.getStringValues(
			actionRequest, "rowIdsJournalArticle");

		for (String deleteArticleId : deleteArticleIds) {
			ActionUtil.deleteArticle(
				actionRequest, HtmlUtil.unescape(deleteArticleId));
		}
	}

	@Reference
	private JournalFolderService _journalFolderService;

}