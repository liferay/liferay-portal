/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.display.context.helper;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.wiki.configuration.WikiGroupServiceConfiguration;
import com.liferay.wiki.model.WikiNode;

import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderResponse;

/**
 * @author Adolfo Pérez
 */
public class WikiURLHelper {

	public WikiURLHelper(
		WikiRequestHelper wikiRequestHelper, PortletResponse portletResponse,
		WikiGroupServiceConfiguration wikiGroupServiceConfiguration) {

		_wikiRequestHelper = wikiRequestHelper;
		_wikiGroupServiceConfiguration = wikiGroupServiceConfiguration;

		_liferayPortletResponse = PortalUtil.getLiferayPortletResponse(
			portletResponse);
	}

	public WikiURLHelper(
		WikiRequestHelper wikiRequestHelper, RenderResponse renderResponse,
		WikiGroupServiceConfiguration wikiGroupServiceConfiguration) {

		_wikiRequestHelper = wikiRequestHelper;
		_wikiGroupServiceConfiguration = wikiGroupServiceConfiguration;

		_liferayPortletResponse = PortalUtil.getLiferayPortletResponse(
			renderResponse);
	}

	public PortletURL getBackToNodeURL(WikiNode wikiNode) {
		return _getWikiNodeBaseURL(wikiNode);
	}

	public PortletURL getBackToViewPagesURL(WikiNode node) {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCRenderCommandName(
			"/wiki/view_pages"
		).setNavigation(
			"all-pages"
		).setParameter(
			"nodeId", node.getNodeId()
		).buildPortletURL();
	}

	public PortletURL getFrontPageURL(WikiNode wikiNode) {
		return PortletURLBuilder.create(
			_getWikiNodeBaseURL(wikiNode)
		).setMVCRenderCommandName(
			"/wiki/view"
		).setParameter(
			"tag", StringPool.BLANK
		).setParameter(
			"title", _wikiGroupServiceConfiguration.frontPageName()
		).buildPortletURL();
	}

	public PortletURL getSearchURL() {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCRenderCommandName(
			"/wiki/search"
		).buildPortletURL();
	}

	public PortletURL getUndoTrashURL() {
		return PortletURLBuilder.createActionURL(
			_liferayPortletResponse
		).setActionName(
			"/wiki/edit_page"
		).setCMD(
			Constants.RESTORE
		).buildPortletURL();
	}

	public PortletURL getViewDraftPagesURL(WikiNode wikiNode) {
		return PortletURLBuilder.create(
			_getWikiNodeBaseURL(wikiNode)
		).setMVCRenderCommandName(
			"/wiki/view_draft_pages"
		).buildPortletURL();
	}

	public PortletURL getViewFrontPagePageURL(WikiNode wikiNode) {
		return getViewPageURL(
			wikiNode, _wikiGroupServiceConfiguration.frontPageName());
	}

	public PortletURL getViewOrphanPagesURL(WikiNode wikiNode) {
		return PortletURLBuilder.create(
			_getWikiNodeBaseURL(wikiNode)
		).setMVCRenderCommandName(
			"/wiki/view_orphan_pages"
		).buildPortletURL();
	}

	public PortletURL getViewPagesURL(WikiNode wikiNode) {
		return PortletURLBuilder.create(
			_getWikiNodeBaseURL(wikiNode)
		).setMVCRenderCommandName(
			"/wiki/view_pages"
		).buildPortletURL();
	}

	public PortletURL getViewPageURL(WikiNode wikiNode, String title) {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCRenderCommandName(
			"/wiki/view"
		).setParameter(
			"nodeName", wikiNode.getName()
		).setParameter(
			"title", title
		).buildPortletURL();
	}

	public PortletURL getViewRecentChangesURL(WikiNode wikiNode) {
		return PortletURLBuilder.create(
			_getWikiNodeBaseURL(wikiNode)
		).setMVCRenderCommandName(
			"/wiki/view_recent_changes"
		).buildPortletURL();
	}

	private PortletURL _getWikiNodeBaseURL(WikiNode node) {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setParameter(
			"categoryId",
			() -> {
				long categoryId = _wikiRequestHelper.getCategoryId();

				if (categoryId > 0) {
					return "0";
				}

				return null;
			}
		).setParameter(
			"nodeName", node.getName()
		).buildPortletURL();
	}

	private final LiferayPortletResponse _liferayPortletResponse;
	private final WikiGroupServiceConfiguration _wikiGroupServiceConfiguration;
	private final WikiRequestHelper _wikiRequestHelper;

}