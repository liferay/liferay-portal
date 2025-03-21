/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.change.tracking.spi.display;

import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.model.WikiNode;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Noor Najjar
 */
@Component(service = CTDisplayRenderer.class)
public class WikiNodeCTDisplayRenderer extends BaseCTDisplayRenderer<WikiNode> {

	@Override
	public String getEditURL(
		HttpServletRequest httpServletRequest, WikiNode wikiNode) {

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, WikiPortletKeys.WIKI_ADMIN,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/wiki/edit_node"
		).setRedirect(
			_portal.getCurrentURL(httpServletRequest)
		).setBackURL(
			ParamUtil.getString(httpServletRequest, "backURL")
		).setParameter(
			"nodeId", wikiNode.getNodeId()
		).buildString();
	}

	@Override
	public Class<WikiNode> getModelClass() {
		return WikiNode.class;
	}

	@Override
	public String getTitle(Locale locale, WikiNode wikiNode) {
		return wikiNode.getName();
	}

	@Override
	protected void buildDisplay(DisplayBuilder<WikiNode> displayBuilder) {
		WikiNode wikiNode = displayBuilder.getModel();

		displayBuilder.display(
			"name", wikiNode.getName()
		).display(
			"description", wikiNode.getDescription()
		).display(
			"created-by",
			() -> {
				String userName = wikiNode.getUserName();

				if (Validator.isNotNull(userName)) {
					return userName;
				}

				return null;
			}
		).display(
			"create-date", wikiNode.getCreateDate()
		).display(
			"last-modified", wikiNode.getModifiedDate()
		);
	}

	@Reference
	private Portal _portal;

}