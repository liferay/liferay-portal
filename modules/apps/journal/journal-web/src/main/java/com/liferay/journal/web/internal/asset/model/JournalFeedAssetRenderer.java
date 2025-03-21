/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.asset.model;

import com.liferay.asset.kernel.model.BaseAssetRenderer;
import com.liferay.journal.model.JournalFeed;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

/**
 * @author Georgel Pop
 */
public class JournalFeedAssetRenderer extends BaseAssetRenderer<JournalFeed> {

	public JournalFeedAssetRenderer(JournalFeed feed) {
		_feed = feed;
	}

	@Override
	public JournalFeed getAssetObject() {
		return _feed;
	}

	@Override
	public String getClassName() {
		return JournalFeed.class.getName();
	}

	@Override
	public long getClassPK() {
		return _feed.getId();
	}

	@Override
	public long getGroupId() {
		return _feed.getGroupId();
	}

	@Override
	public String getSummary(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		return _feed.getDescription();
	}

	@Override
	public String getTitle(Locale locale) {
		return _feed.getName();
	}

	@Override
	public long getUserId() {
		return _feed.getUserId();
	}

	@Override
	public String getUserName() {
		return _feed.getUserName();
	}

	@Override
	public String getUuid() {
		return _feed.getUuid();
	}

	@Override
	public boolean include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String template)
		throws Exception {

		return false;
	}

	private final JournalFeed _feed;

}