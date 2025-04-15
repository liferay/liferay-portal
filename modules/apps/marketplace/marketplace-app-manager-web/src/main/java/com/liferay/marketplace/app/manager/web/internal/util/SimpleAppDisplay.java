/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.marketplace.app.manager.web.internal.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.MimeResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.framework.Version;

/**
 * @author Ryan Park
 */
public class SimpleAppDisplay extends BaseAppDisplay {

	public SimpleAppDisplay() {
		_title = APP_TITLE_UNCATEGORIZED;
		_description = StringPool.BLANK;
		_version = null;
	}

	public SimpleAppDisplay(String title, String description, Version version) {
		if (Validator.isNull(title)) {
			_title = APP_TITLE_UNCATEGORIZED;
		}
		else {
			_title = title;
		}

		_description = description;
		_version = version;
	}

	@Override
	public String getDescription() {
		return _description;
	}

	@Override
	public String getDisplayURL(MimeResponse mimeResponse) {
		return PortletURLBuilder.createRenderURL(
			mimeResponse
		).setMVCPath(
			"/view_modules.jsp"
		).setParameter(
			"app", _title
		).buildString();
	}

	@Override
	public String getIconURL(HttpServletRequest httpServletRequest) {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return themeDisplay.getPathThemeSpritemap() + "#apps";
	}

	@Override
	public String getStoreURL(HttpServletRequest httpServletRequest) {
		return StringPool.BLANK;
	}

	@Override
	public String getTitle() {
		return _title;
	}

	@Override
	public String getVersion() {
		return _version.toString();
	}

	@Override
	public boolean isRequired() {
		return false;
	}

	private final String _description;
	private final String _title;
	private final Version _version;

}