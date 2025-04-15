/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.portlet.action;

import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleService;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
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
		"mvc.command.name=/journal/move_to_trash"
	},
	service = MVCActionCommand.class
)
public class MoveToTrashMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String[] deleteArticleIds = null;

		String articleId = ParamUtil.getString(actionRequest, "articleId");

		if (Validator.isNotNull(articleId)) {
			deleteArticleIds = new String[] {articleId};
		}
		else {
			deleteArticleIds = ParamUtil.getParameterValues(
				actionRequest, "rowIds");
		}

		List<TrashedModel> trashedModels = new ArrayList<>();

		for (String deleteArticleId : deleteArticleIds) {
			JournalArticle article = _journalArticleService.moveArticleToTrash(
				themeDisplay.getScopeGroupId(),
				HtmlUtil.unescape(deleteArticleId));

			trashedModels.add(article);
		}

		if (!trashedModels.isEmpty()) {
			SessionMessages.add(
				actionRequest,
				_portal.getPortletId(actionRequest) +
					SessionMessages.KEY_SUFFIX_DELETE_SUCCESS_DATA,
				HashMapBuilder.<String, Object>put(
					"trashedModels", trashedModels
				).build());

			hideDefaultSuccessMessage(actionRequest);
		}
	}

	@Reference
	private JournalArticleService _journalArticleService;

	@Reference
	private Portal _portal;

}