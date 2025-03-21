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
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.security.PermissionsURLTag;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
	service = MVCRenderCommand.class
)
public class ChangeArticlesPermissionsMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			renderRequest);
		HttpServletResponse httpServletResponse =
			_portal.getHttpServletResponse(renderResponse);

		String[] articleIds = ParamUtil.getStringValues(
			renderRequest, "articleIds");

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

			httpServletResponse.sendRedirect(permissionsURL);
		}
		catch (Exception exception) {
			_log.error(exception);

			return "/error.jsp";
		}

		return MVCRenderConstants.MVC_PATH_VALUE_SKIP_DISPATCH;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ChangeArticlesPermissionsMVCRenderCommand.class);

	@Reference
	private JournalArticleService _journalArticleService;

	@Reference
	private Portal _portal;

}