/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.uad.display;

import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.user.associated.data.display.UADDisplay;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.model.WikiNode;

import jakarta.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(service = UADDisplay.class)
public class WikiNodeUADDisplay extends BaseWikiNodeUADDisplay {

	@Override
	public String getEditURL(
			WikiNode wikiNode, LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws Exception {

		return PortletURLBuilder.createLiferayPortletURL(
			liferayPortletResponse,
			portal.getControlPanelPlid(liferayPortletRequest),
			WikiPortletKeys.WIKI_ADMIN, PortletRequest.RENDER_PHASE
		).setMVCRenderCommandName(
			"/wiki/edit_node"
		).setRedirect(
			portal.getCurrentURL(liferayPortletRequest)
		).setParameter(
			"nodeId", wikiNode.getNodeId()
		).buildString();
	}

	@Reference
	protected Portal portal;

}