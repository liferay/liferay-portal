/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.portlet.action;

import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.exception.InvalidDDMStructureException;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleService;
import com.liferay.journal.service.JournalFolderService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + JournalPortletKeys.JOURNAL,
		"mvc.command.name=/journal/move_articles_and_folders"
	},
	service = MVCActionCommand.class
)
public class MoveArticlesAndFoldersMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long newFolderId = ParamUtil.getLong(actionRequest, "newFolderId");

		long[] folderIds = ParamUtil.getLongValues(
			actionRequest, "rowIdsJournalFolder");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			JournalArticle.class.getName(), actionRequest);

		for (long folderId : folderIds) {
			_journalFolderService.moveFolder(
				folderId, newFolderId, serviceContext);
		}

		List<String> invalidArticleIds = new ArrayList<>();

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String[] articleIds = ParamUtil.getStringValues(
			actionRequest, "rowIdsJournalArticle");

		for (String articleId : articleIds) {
			try {
				_journalArticleService.moveArticle(
					themeDisplay.getScopeGroupId(),
					HtmlUtil.unescape(articleId), newFolderId, serviceContext);
			}
			catch (InvalidDDMStructureException invalidDDMStructureException) {
				if (_log.isWarnEnabled()) {
					_log.warn(invalidDDMStructureException);
				}

				invalidArticleIds.add(articleId);
			}
		}

		if (!invalidArticleIds.isEmpty()) {
			throw new InvalidDDMStructureException(
				StringBundler.concat(
					"Folder ", newFolderId,
					" does not allow the structures for articles: ",
					StringUtil.merge(invalidArticleIds)));
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MoveArticlesAndFoldersMVCActionCommand.class);

	@Reference
	private JournalArticleService _journalArticleService;

	@Reference
	private JournalFolderService _journalFolderService;

}