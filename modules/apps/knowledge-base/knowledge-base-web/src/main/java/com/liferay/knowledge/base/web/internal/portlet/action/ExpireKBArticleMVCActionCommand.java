/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.portlet.action;

import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.knowledge.base.exception.LockedKBArticleException;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.service.KBArticleService;
import com.liferay.knowledge.base.util.KnowledgeBaseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alicia García
 */
@Component(
	property = {
		"jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_ADMIN,
		"mvc.command.name=/knowledge_base/expire_kb_article"
	},
	service = MVCActionCommand.class
)
public class ExpireKBArticleMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long resourcePrimKey = ParamUtil.getLong(
			actionRequest, "resourcePrimKey");

		if (ParamUtil.getBoolean(actionRequest, "forceLock")) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			_kbArticleService.forceLockKBArticle(
				themeDisplay.getScopeGroupId(), resourcePrimKey);
		}

		try {
			_kbArticleService.expireKBArticle(
				resourcePrimKey,
				ServiceContextFactory.getInstance(
					KBArticle.class.getName(), actionRequest));
		}
		catch (LockedKBArticleException lockedKBArticleException) {
			hideDefaultErrorMessage(actionRequest);

			lockedKBArticleException.setActionURL(
				KnowledgeBaseUtil.getKBArticleExpireURL(
					_portal.getLiferayPortletResponse(actionResponse), true,
					KnowledgeBaseUtil.getRedirect(actionRequest),
					resourcePrimKey));
			lockedKBArticleException.setCmd(Constants.EXPIRE);

			throw lockedKBArticleException;
		}
	}

	@Reference
	private KBArticleService _kbArticleService;

	@Reference
	private Portal _portal;

}