/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.portlet.toolbar.contributor.PortletToolbarContributor;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.constants.WikiWebKeys;
import com.liferay.wiki.engine.WikiEngineRenderer;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(
	property = {
		"jakarta.portlet.name=" + WikiPortletKeys.WIKI,
		"jakarta.portlet.name=" + WikiPortletKeys.WIKI_ADMIN,
		"jakarta.portlet.name=" + WikiPortletKeys.WIKI_DISPLAY,
		"mvc.command.name=/wiki/view_pages"
	},
	service = MVCRenderCommand.class
)
public class ViewPagesMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		if (Objects.equals(
				_getPortletId(renderRequest), WikiPortletKeys.WIKI_ADMIN)) {

			renderRequest.setAttribute(
				WikiWebKeys.WIKI_ENGINE_RENDERER, _wikiEngineRenderer);
			renderRequest.setAttribute(
				WikiWebKeys.WIKI_PORTLET_TOOLBAR_CONTRIBUTOR,
				_wikiPortletToolbarContributor);

			return ActionUtil.viewNode(
				renderRequest, "/wiki_admin/view_pages.jsp");
		}

		return ActionUtil.viewNode(renderRequest, "/wiki/view_all_pages.jsp");
	}

	private String _getPortletId(RenderRequest renderRequest) {
		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		return portletDisplay.getPortletName();
	}

	@Reference
	private WikiEngineRenderer _wikiEngineRenderer;

	@Reference(
		target = "(component.name=com.liferay.wiki.web.internal.portlet.toolbar.item.WikiPortletToolbarContributor)"
	)
	private PortletToolbarContributor _wikiPortletToolbarContributor;

}