/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.portlet.action;

import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.security.PermissionsURLTag;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = {
		"jakarta.portlet.name=" + JournalPortletKeys.JOURNAL,
		"mvc.command.name=/journal/change_articles_permissions"
	},
	service = MVCActionCommand.class
)
public class ChangeArticlesPermissionsMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			actionRequest);

		String[] articleIds = ParamUtil.getStringValues(
			actionRequest, "articleIds");

		try {
			List<Long> resourcePrimKeys = TransformUtil.transformToList(
				articleIds,
				articleId -> {
					JournalArticle journalArticle =
						_journalArticleService.fetchArticle(
							themeDisplay.getScopeGroupId(), articleId);

					if (journalArticle != null) {
						return journalArticle.getResourcePrimKey();
					}

					return null;
				});

			String permissionsURL = PermissionsURLTag.doTag(
				StringPool.BLANK, JournalArticle.class.getName(),
				StringPool.BLANK, null, StringUtil.merge(resourcePrimKeys),
				LiferayWindowState.POP_UP.toString(), null, httpServletRequest);

			_hideDefaultSuccessMessage(actionRequest);

			sendRedirect(actionRequest, actionResponse, permissionsURL);
		}
		catch (Exception exception) {
			_log.error(exception);

			throw exception;
		}
	}

	private void _hideDefaultSuccessMessage(PortletRequest portletRequest) {
		SessionMessages.add(
			portletRequest,
			PortalUtil.getPortletId(portletRequest) +
				SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_SUCCESS_MESSAGE);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ChangeArticlesPermissionsMVCActionCommand.class);

	@Reference
	private JournalArticleService _journalArticleService;

	@Reference
	private Portal _portal;

}