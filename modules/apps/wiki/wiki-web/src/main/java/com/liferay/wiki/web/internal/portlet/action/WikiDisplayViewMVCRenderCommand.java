/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.portlet.action;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.wiki.configuration.WikiGroupServiceConfiguration;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.constants.WikiWebKeys;
import com.liferay.wiki.engine.WikiEngineRenderer;
import com.liferay.wiki.exception.NoSuchNodeException;
import com.liferay.wiki.exception.NoSuchPageException;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiNodeService;
import com.liferay.wiki.service.WikiPageService;
import com.liferay.wiki.web.internal.util.WikiWebComponentProvider;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletPreferences;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = {
		"jakarta.portlet.name=" + WikiPortletKeys.WIKI_DISPLAY,
		"mvc.command.name=/", "mvc.command.name=/wiki_display/view"
	},
	service = MVCRenderCommand.class
)
public class WikiDisplayViewMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)renderRequest.getAttribute(WebKeys.THEME_DISPLAY);

			WikiNode node = getNode(renderRequest);

			if (node.getGroupId() != themeDisplay.getScopeGroupId()) {
				throw new NoSuchNodeException(
					"{nodeId=" + node.getNodeId() + "}");
			}

			PortletPreferences portletPreferences =
				renderRequest.getPreferences();

			WikiWebComponentProvider wikiWebComponentProvider =
				WikiWebComponentProvider.getWikiWebComponentProvider();

			WikiGroupServiceConfiguration wikiGroupServiceConfiguration =
				wikiWebComponentProvider.getWikiGroupServiceConfiguration();

			String title = ParamUtil.getString(
				renderRequest, "title",
				portletPreferences.getValue(
					"title", wikiGroupServiceConfiguration.frontPageName()));

			double version = ParamUtil.getDouble(renderRequest, "version");

			WikiPage page = _wikiPageService.fetchPage(
				node.getNodeId(), title, version);

			if ((page == null) || page.isInTrash()) {
				page = _wikiPageService.getPage(
					node.getNodeId(),
					wikiGroupServiceConfiguration.frontPageName());
			}

			renderRequest.setAttribute(
				WikiWebKeys.WIKI_ENGINE_RENDERER, _wikiEngineRenderer);
			renderRequest.setAttribute(WikiWebKeys.WIKI_NODE, node);
			renderRequest.setAttribute(WikiWebKeys.WIKI_PAGE, page);

			return "/wiki_display/view.jsp";
		}
		catch (NoSuchNodeException noSuchNodeException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchNodeException);
			}

			return "/wiki_display/portlet_not_setup.jsp";
		}
		catch (NoSuchPageException noSuchPageException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchPageException);
			}

			return "/wiki_display/portlet_not_setup.jsp";
		}
		catch (PortalException portalException) {
			SessionErrors.add(renderRequest, portalException.getClass());

			return "/wiki/error.jsp";
		}
	}

	protected WikiNode getNode(RenderRequest renderRequest)
		throws PortalException {

		String nodeName = ParamUtil.getString(renderRequest, "nodeName");

		if (Validator.isNotNull(nodeName)) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)renderRequest.getAttribute(WebKeys.THEME_DISPLAY);

			return _wikiNodeService.getNode(
				themeDisplay.getScopeGroupId(), nodeName);
		}

		PortletPreferences portletPreferences = renderRequest.getPreferences();

		long nodeId = GetterUtil.getLong(
			portletPreferences.getValue("nodeId", StringPool.BLANK));

		return _wikiNodeService.getNode(nodeId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		WikiDisplayViewMVCRenderCommand.class);

	@Reference
	private WikiEngineRenderer _wikiEngineRenderer;

	@Reference
	private WikiNodeService _wikiNodeService;

	@Reference
	private WikiPageService _wikiPageService;

}