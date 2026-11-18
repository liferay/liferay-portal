/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemListBuilder;
import com.liferay.osb.faro.web.internal.util.FaroNavigationUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.url.URLBuilder;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class SitesNavigationDisplayContext {

	public SitesNavigationDisplayContext(
		HttpServletRequest httpServletRequest) {

		_httpServletRequest = httpServletRequest;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public NavigationItemList getNavigationItems() throws Exception {
		Layout layout = _themeDisplay.getLayout();

		return NavigationItemListBuilder.add(
			navigationItem -> {
				navigationItem.setActive(
					Objects.equals(layout.getFriendlyURL(), "/sites"));
				navigationItem.setHref(_getLayoutURL("/sites"));
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "overview"));
			}
		).add(
			navigationItem -> {
				navigationItem.setActive(
					Objects.equals(layout.getFriendlyURL(), "/pages"));
				navigationItem.setHref(_getLayoutURL("/pages"));
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "pages"));
			}
		).add(
			navigationItem -> {
				navigationItem.setActive(
					ArrayUtil.contains(
						new String[] {"/site-interest", "/site-interests"},
						layout.getFriendlyURL()));
				navigationItem.setHref(_getLayoutURL("/site-interests"));
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "interests"));
			}
		).add(
			navigationItem -> {
				navigationItem.setActive(
					Objects.equals(layout.getFriendlyURL(), "/search-terms"));
				navigationItem.setHref(_getLayoutURL("/search-terms"));
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "search-terms"));
			}
		).build();
	}

	private String _getAccountId() {
		if (_accountId != null) {
			return _accountId;
		}

		_accountId = ParamUtil.getString(_httpServletRequest, "accountId");

		return _accountId;
	}

	private String _getAccountName() {
		if (_accountName != null) {
			return _accountName;
		}

		_accountName = ParamUtil.getString(_httpServletRequest, "accountName");

		return _accountName;
	}

	private String _getLayoutURL(String friendlyURL) throws Exception {
		return URLBuilder.create(
			FaroNavigationUtil.fetchLayoutURL(friendlyURL, _themeDisplay)
		).addParameter(
			"accountId", _getAccountId()
		).addParameter(
			"accountName", _getAccountName()
		).addParameter(
			"segmentId", _getSegmentId()
		).addParameter(
			"segmentName", _getSegmentName()
		).build();
	}

	private String _getSegmentId() {
		if (_segmentId != null) {
			return _segmentId;
		}

		_segmentId = ParamUtil.getString(_httpServletRequest, "segmentId");

		return _segmentId;
	}

	private String _getSegmentName() {
		if (_segmentName != null) {
			return _segmentName;
		}

		_segmentName = ParamUtil.getString(_httpServletRequest, "segmentName");

		return _segmentName;
	}

	private String _accountId;
	private String _accountName;
	private final HttpServletRequest _httpServletRequest;
	private String _segmentId;
	private String _segmentName;
	private final ThemeDisplay _themeDisplay;

}