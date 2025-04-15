/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.clay.servlet.taglib;

import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderRequest;

/**
 * @author Eudaldo Alonso
 */
public abstract class BaseUserCard
	extends BaseBaseClayCard implements UserCard {

	public BaseUserCard(
		BaseModel<User> baseModel, RenderRequest renderRequest,
		RowChecker rowChecker) {

		super(baseModel, rowChecker);

		this.renderRequest = renderRequest;

		user = (User)baseModel;

		themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	@Override
	public String getIcon() {
		return "user";
	}

	@Override
	public String getImageSrc() {
		if (user.getPortraitId() <= 0) {
			return null;
		}

		try {
			return user.getPortraitURL(themeDisplay);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	@Override
	public String getName() {
		return user.getFullName();
	}

	@Override
	public String getSubtitle() {
		return user.getScreenName();
	}

	@Override
	public String getUserColorClass() {
		return "primary";
	}

	protected final RenderRequest renderRequest;
	protected final ThemeDisplay themeDisplay;
	protected final User user;

	private static final Log _log = LogFactoryUtil.getLog(BaseUserCard.class);

}