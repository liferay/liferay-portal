/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.search;

import com.liferay.portal.kernel.dao.search.DisplayTerms;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

/**
 * @author Raymond Augé
 */
public class FeedDisplayTerms extends DisplayTerms {

	public static final String DESCRIPTION = "description";

	public static final String FEED_ID = "searchFeedId";

	public static final String GROUP_ID = "groupId";

	public static final String NAME = "name";

	public FeedDisplayTerms(PortletRequest portletRequest) {
		super(portletRequest);

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		description = ParamUtil.getString(portletRequest, DESCRIPTION);
		feedId = ParamUtil.getString(portletRequest, FEED_ID);
		groupId = ParamUtil.getLong(
			portletRequest, GROUP_ID, themeDisplay.getScopeGroupId());
		name = ParamUtil.getString(portletRequest, NAME);
	}

	public String getDescription() {
		return description;
	}

	public String getFeedId() {
		return feedId;
	}

	public long getGroupId() {
		return groupId;
	}

	public String getName() {
		return name;
	}

	protected String description;
	protected String feedId;
	protected long groupId;
	protected String name;

}