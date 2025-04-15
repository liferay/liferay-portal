/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.portlet.action;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.exception.NoSuchNodeException;
import com.liferay.wiki.exception.NoSuchPageException;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiPageLocalService;
import com.liferay.wiki.service.WikiPageService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + WikiPortletKeys.WIKI_ADMIN,
		"mvc.command.name=/wiki/edit_node_attachment"
	},
	service = MVCActionCommand.class
)
public class EditNodeAttachmentMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.EMPTY_TRASH)) {
				emptyTrash(actionRequest);
			}
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchNodeException ||
				exception instanceof NoSuchPageException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());
			}
			else {
				throw exception;
			}
		}
	}

	protected void emptyTrash(ActionRequest actionRequest) throws Exception {
		long nodeId = ParamUtil.getLong(actionRequest, "nodeId");

		List<WikiPage> wikiPages = _wikiPageLocalService.getPages(
			nodeId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (WikiPage wikiPage : wikiPages) {
			if (wikiPage.getDeletedAttachmentsFileEntriesCount() > 0) {
				_wikiPageService.deleteTrashPageAttachments(
					nodeId, wikiPage.getTitle());
			}
		}
	}

	@Reference
	private WikiPageLocalService _wikiPageLocalService;

	@Reference
	private WikiPageService _wikiPageService;

}