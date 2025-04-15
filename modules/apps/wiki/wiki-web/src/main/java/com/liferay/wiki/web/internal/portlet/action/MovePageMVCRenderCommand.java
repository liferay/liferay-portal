/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.constants.WikiWebKeys;
import com.liferay.wiki.exception.NoSuchNodeException;
import com.liferay.wiki.exception.NoSuchPageException;
import com.liferay.wiki.exception.PageTitleException;
import com.liferay.wiki.exception.PageVersionException;
import com.liferay.wiki.model.WikiPage;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Jorge Ferrer
 */
@Component(
	property = {
		"jakarta.portlet.name=" + WikiPortletKeys.WIKI,
		"jakarta.portlet.name=" + WikiPortletKeys.WIKI_ADMIN,
		"jakarta.portlet.name=" + WikiPortletKeys.WIKI_DISPLAY,
		"mvc.command.name=/wiki/move_page"
	},
	service = MVCRenderCommand.class
)
public class MovePageMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			renderRequest.setAttribute(
				WikiWebKeys.WIKI_NODE, ActionUtil.getNode(renderRequest));

			WikiPage page = ActionUtil.getPage(renderRequest);

			if (!page.isApproved()) {
				throw new PageVersionException();
			}

			renderRequest.setAttribute(WikiWebKeys.WIKI_PAGE, page);
		}
		catch (NoSuchNodeException | NoSuchPageException | PageTitleException |
			   PageVersionException | PrincipalException exception) {

			SessionErrors.add(renderRequest, exception.getClass());

			return "/wiki/error.jsp";
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}

		return "/wiki/move_page.jsp";
	}

}