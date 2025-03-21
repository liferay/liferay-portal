/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.portlet.action;

import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.knowledge.base.exception.LockedKBArticleException;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.service.KBArticleService;
import com.liferay.knowledge.base.util.KnowledgeBaseUtil;
import com.liferay.knowledge.base.web.internal.constants.KBWebKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Galluzzi
 */
@Component(
	property = {
		"jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_ADMIN,
		"jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_ARTICLE,
		"jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_DISPLAY,
		"jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_SEARCH,
		"jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_SECTION,
		"mvc.command.name=/knowledge_base/edit_kb_article"
	},
	service = MVCRenderCommand.class
)
public class EditKBArticleMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		KBArticle kbArticle = (KBArticle)renderRequest.getAttribute(
			KBWebKeys.KNOWLEDGE_BASE_KB_ARTICLE);

		if (kbArticle != null) {
			if (ParamUtil.getBoolean(renderRequest, "forceLock")) {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)renderRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				try {
					_kbArticleService.forceLockKBArticle(
						themeDisplay.getScopeGroupId(),
						kbArticle.getResourcePrimKey());
				}
				catch (PortalException portalException) {
					throw new PortletException(portalException);
				}
			}

			try {
				_kbArticleService.lockKBArticle(kbArticle.getResourcePrimKey());
			}
			catch (PortalException portalException) {
				HttpServletRequest httpServletRequest =
					_portal.getOriginalServletRequest(
						_portal.getHttpServletRequest(renderRequest));

				if (portalException instanceof LockedKBArticleException) {
					_hideDefaultErrorMessage(httpServletRequest);

					LockedKBArticleException lockedKBArticleException =
						(LockedKBArticleException)portalException;

					lockedKBArticleException.setActionURL(
						KnowledgeBaseUtil.getKBArticleEditURL(
							_portal.getLiferayPortletRequest(renderRequest),
							true,
							ParamUtil.getString(renderRequest, "redirect"),
							kbArticle.getResourcePrimKey()));
					lockedKBArticleException.setCmd(Constants.EDIT);
				}

				SessionErrors.add(
					httpServletRequest, portalException.getClass(),
					portalException);

				_sendRedirect(renderRequest, renderResponse);

				return MVCRenderConstants.MVC_PATH_VALUE_SKIP_DISPATCH;
			}
		}

		return "/admin/common/edit_kb_article.jsp";
	}

	private void _hideDefaultErrorMessage(
		HttpServletRequest httpServletRequest) {

		SessionMessages.add(
			httpServletRequest,
			_portal.getPortletId(httpServletRequest) +
				SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE);
	}

	private void _sendRedirect(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			HttpServletResponse httpServletResponse =
				_portal.getHttpServletResponse(renderResponse);

			httpServletResponse.sendRedirect(
				ParamUtil.getString(renderRequest, "redirect"));
		}
		catch (IOException ioException) {
			throw new PortletException(ioException);
		}
	}

	@Reference
	private KBArticleService _kbArticleService;

	@Reference
	private Portal _portal;

}