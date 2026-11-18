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
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class TouchpointNavigationDisplayContext {

	public TouchpointNavigationDisplayContext(
		HttpServletRequest httpServletRequest) {

		_httpServletRequest = httpServletRequest;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public NavigationItemList getNavigationItems() throws Exception {
		Layout layout = _themeDisplay.getLayout();

		boolean ldpEnabled = FaroNavigationUtil.isLDPEnabled(
			_themeDisplay.getScopeGroupId());

		return NavigationItemListBuilder.add(
			navigationItem -> {
				navigationItem.setActive(
					Objects.equals(layout.getFriendlyURL(), "/page-overview"));
				navigationItem.setHref(_getLayoutURL("/page-overview"));
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "overview"));
			}
		).add(
			navigationItem -> {
				navigationItem.setActive(
					Objects.equals(layout.getFriendlyURL(), "/page-path"));
				navigationItem.setHref(_getLayoutURL("/page-path"));
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "path"));
			}
		).add(
			() -> ldpEnabled,
			navigationItem -> {
				navigationItem.setActive(
					Objects.equals(layout.getFriendlyURL(), "/page-accounts"));
				navigationItem.setHref(_getLayoutURL("/page-accounts"));
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "visitors"));
			}
		).add(
			() -> !ldpEnabled,
			navigationItem -> {
				navigationItem.setActive(
					Objects.equals(
						layout.getFriendlyURL(), "/page-known-individuals"));
				navigationItem.setHref(
					_getLayoutURL("/page-known-individuals"));
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "known-individuals"));
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
			"rangeEnd", _getRangeEnd()
		).addParameter(
			"rangeKey", _getRangeKey()
		).addParameter(
			"rangeStart", _getRangeStart()
		).addParameter(
			"segmentId", _getSegmentId()
		).addParameter(
			"segmentName", _getSegmentName()
		).addParameter(
			"title", _getTitle()
		).addParameter(
			"touchpoint", _getTouchpoint()
		).build();
	}

	private String _getRangeEnd() {
		if (_rangeEnd != null) {
			return _rangeEnd;
		}

		_rangeEnd = ParamUtil.getString(_httpServletRequest, "rangeEnd");

		return _rangeEnd;
	}

	private String _getRangeKey() {
		if (_rangeKey != null) {
			return _rangeKey;
		}

		_rangeKey = ParamUtil.getString(_httpServletRequest, "rangeKey");

		return _rangeKey;
	}

	private String _getRangeStart() {
		if (_rangeStart != null) {
			return _rangeStart;
		}

		_rangeStart = ParamUtil.getString(_httpServletRequest, "rangeStart");

		return _rangeStart;
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

	private String _getTitle() {
		if (_title != null) {
			return _title;
		}

		_title = ParamUtil.getString(_httpServletRequest, "title");

		return _title;
	}

	private String _getTouchpoint() {
		if (_touchpoint != null) {
			return _touchpoint;
		}

		_touchpoint = ParamUtil.getString(_httpServletRequest, "touchpoint");

		return _touchpoint;
	}

	private String _accountId;
	private String _accountName;
	private final HttpServletRequest _httpServletRequest;
	private String _rangeEnd;
	private String _rangeKey;
	private String _rangeStart;
	private String _segmentId;
	private String _segmentName;
	private final ThemeDisplay _themeDisplay;
	private String _title;
	private String _touchpoint;

}