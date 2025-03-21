/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.trash.TrashHelper;
import com.liferay.trash.util.TrashWebKeys;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.constants.WikiWebKeys;
import com.liferay.wiki.engine.WikiEngineRenderer;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(
	property = {
		"jakarta.portlet.name=" + WikiPortletKeys.WIKI_ADMIN,
		"mvc.command.name=/wiki/page_info_panel"
	},
	service = MVCResourceCommand.class
)
public class PageInfoPanelMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	public void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		resourceRequest.setAttribute(TrashWebKeys.TRASH_HELPER, _trashHelper);
		resourceRequest.setAttribute(
			WikiWebKeys.WIKI_ENGINE_RENDERER, _wikiEngineRenderer);
		resourceRequest.setAttribute(
			WikiWebKeys.WIKI_NODE, ActionUtil.getNode(resourceRequest));
		resourceRequest.setAttribute(
			WikiWebKeys.WIKI_PAGES, ActionUtil.getPages(resourceRequest));

		include(
			resourceRequest, resourceResponse,
			"/wiki_admin/page_info_panel.jsp");
	}

	@Reference
	private TrashHelper _trashHelper;

	@Reference
	private WikiEngineRenderer _wikiEngineRenderer;

}