/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.portlet.action;

import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.knowledge.base.exception.LockedKBArticleException;
import com.liferay.knowledge.base.service.KBArticleService;
import com.liferay.knowledge.base.util.KnowledgeBaseUtil;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = {
		"jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_ADMIN,
		"jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_ARTICLE,
		"jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_DISPLAY,
		"jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_SEARCH,
		"jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_SECTION,
		"mvc.command.name=/knowledge_base/delete_kb_article"
	},
	service = MVCActionCommand.class
)
public class DeleteKBArticleMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		long resourcePrimKey = ParamUtil.getLong(
			actionRequest, "resourcePrimKey");

		if (ParamUtil.getBoolean(actionRequest, "forceLock")) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			_kbArticleService.forceLockKBArticle(
				themeDisplay.getScopeGroupId(), resourcePrimKey);
		}

		try {
			if (cmd.equals(Constants.MOVE_TO_TRASH)) {
				addDeleteSuccessData(
					actionRequest,
					HashMapBuilder.<String, Object>put(
						"trashedModels",
						ListUtil.toList(
							(TrashedModel)
								_kbArticleService.moveKBArticleToTrash(
									resourcePrimKey))
					).build());
			}
			else {
				_kbArticleService.deleteKBArticle(resourcePrimKey);
			}
		}
		catch (LockedKBArticleException lockedKBArticleException) {
			hideDefaultErrorMessage(actionRequest);

			lockedKBArticleException.setActionURL(
				KnowledgeBaseUtil.getKBArticleDeleteURL(
					_portal.getLiferayPortletResponse(actionResponse), cmd,
					true, KnowledgeBaseUtil.getRedirect(actionRequest),
					resourcePrimKey));
			lockedKBArticleException.setCmd(Constants.DELETE);

			throw lockedKBArticleException;
		}
	}

	@Reference
	private KBArticleService _kbArticleService;

	@Reference
	private Portal _portal;

}