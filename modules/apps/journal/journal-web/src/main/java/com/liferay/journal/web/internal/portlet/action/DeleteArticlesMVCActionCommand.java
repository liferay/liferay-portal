/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.portlet.action;

import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.service.JournalArticleService;
import com.liferay.journal.web.internal.portlet.JournalPortlet;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.trash.TrashHelper;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + JournalPortletKeys.JOURNAL,
		"mvc.command.name=/journal/delete_article",
		"mvc.command.name=/journal/delete_articles"
	},
	service = MVCActionCommand.class
)
public class DeleteArticlesMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String[] deleteArticleIds = null;

		String articleId = ParamUtil.getString(actionRequest, "articleId");

		if (Validator.isNotNull(articleId)) {
			deleteArticleIds = new String[] {articleId};
		}
		else {
			deleteArticleIds = ParamUtil.getParameterValues(
				actionRequest, "rowIds");
		}

		if (ArrayUtil.isEmpty(deleteArticleIds)) {
			return;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		boolean trashEnabled = _trashHelper.isTrashEnabled(
			themeDisplay.getScopeGroupId());

		List<TrashedModel> trashedModels = new ArrayList<>();

		Arrays.sort(deleteArticleIds);

		for (String deleteArticleId : deleteArticleIds) {
			int pos = deleteArticleId.lastIndexOf(
				JournalPortlet.VERSION_SEPARATOR);

			if ((pos >= 0) && trashEnabled) {
				String originalArticleId = deleteArticleId.substring(0, pos);

				int count = _journalArticleLocalService.getArticlesCount(
					themeDisplay.getScopeGroupId(), originalArticleId);

				if (count == 1) {
					JournalArticle article =
						_journalArticleService.moveArticleToTrash(
							themeDisplay.getScopeGroupId(),
							HtmlUtil.unescape(originalArticleId));

					trashedModels.add(article);

					continue;
				}
			}

			ActionUtil.deleteArticle(
				actionRequest, HtmlUtil.unescape(deleteArticleId));
		}

		if (_hasArticle(actionRequest)) {
			return;
		}

		if (!trashedModels.isEmpty()) {
			addDeleteSuccessData(
				actionRequest,
				HashMapBuilder.<String, Object>put(
					"trashedModels", trashedModels
				).build());
		}

		PortletURL portletURL = PortletURLFactoryUtil.create(
			actionRequest, themeDisplay.getPpid(), PortletRequest.RENDER_PHASE);

		actionRequest.setAttribute(WebKeys.REDIRECT, portletURL.toString());
	}

	private boolean _hasArticle(ActionRequest actionRequest) {
		String articleId = ParamUtil.getString(actionRequest, "articleId");

		if (Validator.isNull(articleId)) {
			String[] articleIds = StringUtil.split(
				ParamUtil.getString(actionRequest, "rowIds"));

			if (articleIds.length <= 0) {
				return false;
			}

			articleId = articleIds[0];
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		int pos = articleId.lastIndexOf(JournalPortlet.VERSION_SEPARATOR);

		if (pos != -1) {
			articleId = articleId.substring(0, pos);
		}

		JournalArticle article = _journalArticleLocalService.fetchArticle(
			themeDisplay.getScopeGroupId(), articleId);

		if (article == null) {
			return false;
		}

		return true;
	}

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private JournalArticleService _journalArticleService;

	@Reference
	private TrashHelper _trashHelper;

}