/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.web.internal.search;

import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.model.Group;

import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Objects;
import java.util.Set;

/**
 * @author André Miranda
 */
public class GroupChecker extends EmptyOnClickRowChecker {

	public GroupChecker(
		RenderResponse renderResponse, String channelId, Set<String> ids,
		String mvcRenderCommandName) {

		super(renderResponse);

		_channelId = channelId;
		_ids = ids;
		_mvcRenderCommandName = mvcRenderCommandName;
	}

	@Override
	public boolean isChecked(Object object) {
		Group group = (Group)object;

		return Objects.equals(
			group.getTypeSettingsProperty("analyticsChannelId"), _channelId);
	}

	@Override
	protected String getRowCheckBox(
		HttpServletRequest httpServletRequest, boolean checked,
		boolean disabled, String name, String value, String checkBoxRowIds,
		String checkBoxAllRowIds, String checkBoxPostOnClick) {

		if (!checked && _ids.contains(value)) {
			disabled = true;
		}

		return super.getRowCheckBox(
			httpServletRequest, checked, disabled, name, value, checkBoxRowIds,
			checkBoxAllRowIds, checkBoxPostOnClick);
	}

	private final String _channelId;
	private final Set<String> _ids;
	private final String _mvcRenderCommandName;

}