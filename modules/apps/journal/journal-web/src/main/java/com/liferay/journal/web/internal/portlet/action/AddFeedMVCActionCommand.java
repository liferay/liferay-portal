/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.portlet.action;

import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalFeed;
import com.liferay.journal.service.JournalFeedService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.rss.util.RSSUtil;

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
		"mvc.command.name=/journal/add_feed"
	},
	service = MVCActionCommand.class
)
public class AddFeedMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long groupId = ParamUtil.getLong(actionRequest, "groupId");

		String feedId = ParamUtil.getString(actionRequest, "feedId");

		boolean autoFeedId = ParamUtil.getBoolean(actionRequest, "autoFeedId");
		String name = ParamUtil.getString(actionRequest, "name");
		String description = ParamUtil.getString(actionRequest, "description");
		long ddmStructureId = ParamUtil.getLong(
			actionRequest, "ddmStructureId");
		String ddmTemplateKey = ParamUtil.getString(
			actionRequest, "ddmTemplateKey");
		String ddmRendererTemplateKey = ParamUtil.getString(
			actionRequest, "ddmRendererTemplateKey");
		int delta = ParamUtil.getInteger(actionRequest, "delta");
		String orderByCol = ParamUtil.getString(actionRequest, "orderByCol");
		String orderByType = ParamUtil.getString(actionRequest, "orderByType");
		String targetLayoutFriendlyUrl = ParamUtil.getString(
			actionRequest, "targetLayoutFriendlyUrl");
		String targetPortletId = ParamUtil.getString(
			actionRequest, "targetPortletId");
		String contentField = ParamUtil.getString(
			actionRequest, "contentField");

		String feedType = ParamUtil.getString(
			actionRequest, "feedType", RSSUtil.FEED_TYPE_DEFAULT);

		String feedFormat = RSSUtil.getFeedTypeFormat(feedType);
		double feedVersion = RSSUtil.getFeedTypeVersion(feedType);

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			JournalFeed.class.getName(), actionRequest);

		_journalFeedService.addFeed(
			groupId, feedId, autoFeedId, name, description, ddmStructureId,
			ddmTemplateKey, ddmRendererTemplateKey, delta, orderByCol,
			orderByType, targetLayoutFriendlyUrl, targetPortletId, contentField,
			feedFormat, feedVersion, serviceContext);
	}

	@Reference
	private JournalFeedService _journalFeedService;

}