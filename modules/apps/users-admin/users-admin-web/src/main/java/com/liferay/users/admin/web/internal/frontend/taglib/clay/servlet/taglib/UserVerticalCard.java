/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.servlet.taglib.BaseUserCard;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.model.User;
import com.liferay.taglib.util.LexiconUtil;
import com.liferay.users.admin.web.internal.servlet.taglib.util.UserActionDropdownItems;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class UserVerticalCard extends BaseUserCard {

	public UserVerticalCard(
		RenderRequest renderRequest, RenderResponse renderResponse,
		RowChecker rowChecker, boolean showActions, User user) {

		super(user, renderRequest, rowChecker);

		_renderResponse = renderResponse;
		_showActions = showActions;
	}

	public UserVerticalCard(
		RenderRequest renderRequest, RowChecker rowChecker, User user) {

		super(user, renderRequest, rowChecker);

		_renderResponse = null;
		_showActions = false;
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		if (!_showActions) {
			return null;
		}

		UserActionDropdownItems userActionDropdownItems =
			new UserActionDropdownItems(renderRequest, _renderResponse, user);

		return userActionDropdownItems.getActionDropdownItems();
	}

	@Override
	public String getUserColorClass() {
		return "sticker-user-icon " + LexiconUtil.getUserColorCssClass(user);
	}

	private final RenderResponse _renderResponse;
	private final boolean _showActions;

}