/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.roles.admin.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.servlet.taglib.BaseVerticalCard;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.UserGroup;

import jakarta.portlet.RenderRequest;

/**
 * @author Eudaldo Alonso
 */
public class UserGroupVerticalCard extends BaseVerticalCard {

	public UserGroupVerticalCard(
		UserGroup userGroup, RenderRequest renderRequest,
		RowChecker rowChecker) {

		super(userGroup, renderRequest, rowChecker);

		_userGroup = userGroup;
	}

	@Override
	public String getIcon() {
		return "users";
	}

	@Override
	public String getInputValue() {
		try {
			return String.valueOf(_userGroup.getGroupId());
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return StringPool.BLANK;
	}

	@Override
	public String getSubtitle() {
		return _userGroup.getDescription();
	}

	@Override
	public String getTitle() {
		return _userGroup.getName();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UserGroupVerticalCard.class);

	private final UserGroup _userGroup;

}