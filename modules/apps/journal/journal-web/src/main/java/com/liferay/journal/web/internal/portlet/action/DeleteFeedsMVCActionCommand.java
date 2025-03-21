/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.portlet.action;

import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.service.JournalFeedService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + JournalPortletKeys.JOURNAL,
		"mvc.command.name=/journal/delete_feeds"
	},
	service = MVCActionCommand.class
)
public class DeleteFeedsMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long[] deleteFeedIds = null;

		long deleteFeedId = ParamUtil.getLong(actionRequest, "deleteFeedId");

		if (deleteFeedId > 0) {
			deleteFeedIds = new long[] {deleteFeedId};
		}
		else {
			deleteFeedIds = ParamUtil.getLongValues(actionRequest, "rowIds");
		}

		for (long curDeleteFeedId : deleteFeedIds) {
			_journalFeedService.deleteFeed(
				themeDisplay.getScopeGroupId(),
				String.valueOf(curDeleteFeedId));
		}
	}

	@Reference
	private JournalFeedService _journalFeedService;

}