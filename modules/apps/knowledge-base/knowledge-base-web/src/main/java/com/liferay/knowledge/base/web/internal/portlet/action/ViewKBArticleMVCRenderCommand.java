/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.portlet.action;

import com.liferay.change.tracking.spi.history.util.CTTimelineUtil;
import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.web.internal.configuration.KBSearchPortletInstanceConfiguration;
import com.liferay.knowledge.base.web.internal.configuration.KBSectionPortletInstanceConfiguration;
import com.liferay.knowledge.base.web.internal.constants.KBWebKeys;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

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
		"mvc.command.name=/knowledge_base/view_kb_article"
	},
	service = MVCRenderCommand.class
)
public class ViewKBArticleMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		String rootPortletId = _getRootPortletId(renderRequest);

		KBArticle kbArticle = (KBArticle)renderRequest.getAttribute(
			KBWebKeys.KNOWLEDGE_BASE_KB_ARTICLE);

		if (kbArticle != null) {
			CTTimelineUtil.setCTTimelineKeys(
				renderRequest, KBArticle.class, kbArticle.getKbArticleId());
		}

		if (rootPortletId.equals(KBPortletKeys.KNOWLEDGE_BASE_ADMIN)) {
			return "/admin/view_kb_article.jsp";
		}

		if (rootPortletId.equals(KBPortletKeys.KNOWLEDGE_BASE_ARTICLE)) {
			return "/article/view_kb_article.jsp";
		}

		if (rootPortletId.equals(KBPortletKeys.KNOWLEDGE_BASE_DISPLAY)) {
			return "/display/view_kb_article.jsp";
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			renderRequest);

		if (rootPortletId.equals(KBPortletKeys.KNOWLEDGE_BASE_SEARCH)) {
			try {
				KBSearchPortletInstanceConfiguration
					kbSearchPortletInstanceConfiguration =
						_configurationProvider.getPortletInstanceConfiguration(
							KBSearchPortletInstanceConfiguration.class,
							themeDisplay);

				httpServletRequest.setAttribute(
					"init.jsp-enableKBArticleDescription",
					kbSearchPortletInstanceConfiguration.
						enableKBArticleDescription());
			}
			catch (ConfigurationException configurationException) {
				throw new RuntimeException(configurationException);
			}
		}

		if (rootPortletId.equals(KBPortletKeys.KNOWLEDGE_BASE_SECTION)) {
			try {
				KBSectionPortletInstanceConfiguration
					kbSectionPortletInstanceConfiguration =
						_configurationProvider.getPortletInstanceConfiguration(
							KBSectionPortletInstanceConfiguration.class,
							themeDisplay);

				httpServletRequest.setAttribute(
					"init.jsp-enableKBArticleDescription",
					kbSectionPortletInstanceConfiguration.
						enableKBArticleDescription());
			}
			catch (ConfigurationException configurationException) {
				throw new RuntimeException(configurationException);
			}
		}

		return "/admin/common/view_kb_article.jsp";
	}

	private String _getRootPortletId(RenderRequest renderRequest) {
		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		return portletDisplay.getRootPortletId();
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Portal _portal;

}