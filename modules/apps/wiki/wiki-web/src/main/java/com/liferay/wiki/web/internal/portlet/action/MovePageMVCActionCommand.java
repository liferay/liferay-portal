/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.exception.DuplicatePageException;
import com.liferay.wiki.exception.NoSuchNodeException;
import com.liferay.wiki.exception.NoSuchPageException;
import com.liferay.wiki.exception.PageContentException;
import com.liferay.wiki.exception.PageTitleException;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiPageService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

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
	service = MVCActionCommand.class
)
public class MovePageMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.CHANGE_PARENT)) {
				_changeParentPage(actionRequest);
			}
			else if (cmd.equals(Constants.RENAME)) {
				_renamePage(actionRequest);
			}

			if (Validator.isNotNull(cmd)) {
				sendRedirect(actionRequest, actionResponse);
			}
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchNodeException ||
				exception instanceof NoSuchPageException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());
			}
			else if (exception instanceof DuplicatePageException ||
					 exception instanceof PageContentException ||
					 exception instanceof PageTitleException) {

				SessionErrors.add(actionRequest, exception.getClass());
			}
			else {
				throw exception;
			}
		}
	}

	private void _changeParentPage(ActionRequest actionRequest)
		throws Exception {

		long nodeId = ParamUtil.getLong(actionRequest, "nodeId");
		String title = ParamUtil.getString(actionRequest, "title");
		String newParentTitle = ParamUtil.getString(
			actionRequest, "newParentTitle");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			WikiPage.class.getName(), actionRequest);

		_wikiPageService.changeParent(
			nodeId, title, newParentTitle, serviceContext);
	}

	private void _renamePage(ActionRequest actionRequest) throws Exception {
		long nodeId = ParamUtil.getLong(actionRequest, "nodeId");
		String title = ParamUtil.getString(actionRequest, "title");
		String newTitle = ParamUtil.getString(actionRequest, "newTitle");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			WikiPage.class.getName(), actionRequest);

		_wikiPageService.renamePage(nodeId, title, newTitle, serviceContext);
	}

	@Reference
	private WikiPageService _wikiPageService;

}