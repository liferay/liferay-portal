/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.portlet.action;

import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.web.internal.helper.JournalRSSHelper;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseRSSMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(
	property = {
		"auth.token.ignore.mvc.action=true",
		"jakarta.portlet.name=" + JournalPortletKeys.JOURNAL,
		"mvc.command.name=/journal/rss",
		"portlet.add.default.resource.check.whitelist.mvc.action=true"
	},
	service = MVCResourceCommand.class
)
public class RSSMVCResourceCommand extends BaseRSSMVCResourceCommand {

	@Override
	protected byte[] getRSS(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		return _journalRSSHelper.getRSS(resourceRequest, resourceResponse);
	}

	protected void unsetJournalRSSHelper(JournalRSSHelper journalRSSHelper) {
		_journalRSSHelper = null;
	}

	@Reference
	private JournalRSSHelper _journalRSSHelper;

}