/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.display.context;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Lourdes Fernández Besada
 */
public class LockedLayoutDisplayContext {

	public LockedLayoutDisplayContext(
		Language language, HttpServletRequest originalHttpServletRequest,
		Portal portal, RenderRequest renderRequest) {

		_language = language;
		_originalHttpServletRequest = originalHttpServletRequest;
		_portal = portal;
		_renderRequest = renderRequest;

		_themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public String getBackURL() {
		if (_backURL != null) {
			return _backURL;
		}

		_backURL = ParamUtil.getString(
			_renderRequest, "backURL",
			ParamUtil.getString(_originalHttpServletRequest, "p_l_back_url"));

		return _backURL;
	}

	public String getBackURLLabel() {
		if (_backURLLabel != null) {
			return _backURLLabel;
		}

		if (Validator.isNull(getBackURL())) {
			_backURLLabel = StringPool.BLANK;

			return _backURLLabel;
		}

		String backURLTitle = ParamUtil.getString(
			_originalHttpServletRequest, "p_l_back_url_title");

		if (Validator.isNotNull(backURLTitle)) {
			_backURLLabel = _language.format(
				_themeDisplay.getLocale(), "go-to-x",
				new String[] {HtmlUtil.escape(backURLTitle)});
		}
		else {
			_backURLLabel = _language.get(_themeDisplay.getLocale(), "go-back");
		}

		return _backURLLabel;
	}

	public String getImagesPath() {
		return _portal.getPathContext(_renderRequest) + "/images";
	}

	public boolean isShowGoBackButton() {
		return Validator.isNotNull(getBackURL());
	}

	private String _backURL;
	private String _backURLLabel;
	private final Language _language;
	private final HttpServletRequest _originalHttpServletRequest;
	private final Portal _portal;
	private final RenderRequest _renderRequest;
	private final ThemeDisplay _themeDisplay;

}