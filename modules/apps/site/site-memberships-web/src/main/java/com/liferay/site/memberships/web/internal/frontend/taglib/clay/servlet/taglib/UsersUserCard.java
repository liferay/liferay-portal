/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.memberships.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.servlet.taglib.BaseUserCard;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.site.memberships.web.internal.servlet.taglib.util.UserActionDropdownItemsProvider;
import com.liferay.taglib.util.LexiconUtil;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class UsersUserCard extends BaseUserCard {

	public UsersUserCard(
		User user, RenderRequest renderRequest, RenderResponse renderResponse,
		RowChecker rowChecker) {

		super(user, renderRequest, rowChecker);

		_renderResponse = renderResponse;
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		try {
			UserActionDropdownItemsProvider userActionDropdownItemsProvider =
				new UserActionDropdownItemsProvider(
					user, renderRequest, _renderResponse);

			return userActionDropdownItemsProvider.getActionDropdownItems();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	@Override
	public String getUserColorClass() {
		return "sticker-user-icon " + LexiconUtil.getUserColorCssClass(user);
	}

	private static final Log _log = LogFactoryUtil.getLog(UsersUserCard.class);

	private final RenderResponse _renderResponse;

}