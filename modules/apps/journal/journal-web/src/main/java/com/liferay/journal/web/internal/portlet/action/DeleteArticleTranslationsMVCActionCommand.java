/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.portlet.action;

import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleService;
import com.liferay.journal.util.JournalContent;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(
	property = {
		"jakarta.portlet.name=" + JournalPortletKeys.JOURNAL,
		"mvc.command.name=/journal/delete_article_translations"
	},
	service = MVCActionCommand.class
)
public class DeleteArticleTranslationsMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long id = ParamUtil.getLong(actionRequest, "id");

		JournalArticle article = _journalArticleService.getArticle(id);

		String[] languageIds = ParamUtil.getStringValues(
			actionRequest, "rowIds");

		for (String languageId : languageIds) {
			if (StringUtil.equalsIgnoreCase(
					languageId, article.getDefaultLanguageId())) {

				continue;
			}

			_journalArticleService.removeArticleLocale(
				article.getGroupId(), article.getArticleId(),
				article.getVersion(), languageId);
		}

		_journalContent.clearCache(
			article.getGroupId(), article.getArticleId(),
			article.getDDMTemplateKey());
	}

	@Reference
	private JournalArticleService _journalArticleService;

	@Reference
	private JournalContent _journalContent;

}