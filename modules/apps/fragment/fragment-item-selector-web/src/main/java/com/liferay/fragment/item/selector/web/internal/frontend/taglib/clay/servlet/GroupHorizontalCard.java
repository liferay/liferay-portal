/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.item.selector.web.internal.frontend.taglib.clay.servlet;

import com.liferay.frontend.taglib.clay.servlet.taglib.HorizontalCard;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;

import jakarta.portlet.PortletURL;

/**
 * @author Víctor Galán
 */
public class GroupHorizontalCard implements HorizontalCard {

	public GroupHorizontalCard(
		Group group, PortletURL portletURL, String title) {

		_group = group;
		_portletURL = portletURL;
		_title = title;
	}

	@Override
	public String getHref() {
		return PortletURLBuilder.create(
			_portletURL
		).setParameter(
			"selectedGroupId", _group.getGroupId()
		).buildString();
	}

	@Override
	public String getIcon() {
		return "books";
	}

	@Override
	public String getTitle() {
		return _title;
	}

	@Override
	public boolean isSelectable() {
		return false;
	}

	private final Group _group;
	private final PortletURL _portletURL;
	private final String _title;

}