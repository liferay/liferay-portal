/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.roles.admin.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.servlet.taglib.BaseUserCard;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.model.User;
import com.liferay.taglib.util.LexiconUtil;

import jakarta.portlet.RenderRequest;

/**
 * @author Eudaldo Alonso
 */
public class UserVerticalCard extends BaseUserCard {

	public UserVerticalCard(
		RenderRequest renderRequest, RowChecker rowChecker, User user) {

		super(user, renderRequest, rowChecker);
	}

	@Override
	public String getUserColorClass() {
		return "sticker-user-icon " + LexiconUtil.getUserColorCssClass(user);
	}

}