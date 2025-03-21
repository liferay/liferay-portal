/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.wiki.constants.WikiWebKeys;
import com.liferay.wiki.exception.NoSuchNodeException;
import com.liferay.wiki.exception.NoSuchPageException;
import com.liferay.wiki.model.WikiNode;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

/**
 * @author Brian Wing Shun Chan
 * @author Jorge Ferrer
 */
public abstract class BaseViewPageMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		long categoryId = ParamUtil.getLong(renderRequest, "categoryId");

		if (categoryId > 0) {
			return ActionUtil.viewNode(
				renderRequest, "/wiki/view_categorized_pages.jsp");
		}

		String tag = ParamUtil.getString(renderRequest, "tag");

		if (Validator.isNotNull(tag)) {
			return ActionUtil.viewNode(
				renderRequest, "/wiki/view_tagged_pages.jsp");
		}

		try {
			renderRequest.setAttribute(
				WikiWebKeys.WIKI_NODE, ActionUtil.getNode(renderRequest));

			renderRequest.setAttribute(
				WikiWebKeys.WIKI_PAGE, ActionUtil.getPage(renderRequest));
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchNodeException ||
				exception instanceof NoSuchPageException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(renderRequest, exception.getClass());

				return "/wiki/error.jsp";
			}

			throw new PortletException(exception);
		}

		return getPath();
	}

	protected void getNode(RenderRequest renderRequest) throws Exception {
		WikiNode node = ActionUtil.getNode(renderRequest);

		if (node == null) {
			node = ActionUtil.getFirstVisibleNode(renderRequest);
		}

		renderRequest.setAttribute(WikiWebKeys.WIKI_NODE, node);
	}

	protected abstract String getPath();

}