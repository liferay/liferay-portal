/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.display.context.helper;

import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

/**
 * @author Iván Zaera
 */
public abstract class BaseRequestHelper {

	public BaseRequestHelper(HttpServletRequest httpServletRequest) {
		_httpServletRequest = httpServletRequest;
	}

	public Company getCompany() {
		if (_company == null) {
			ThemeDisplay themeDisplay = getThemeDisplay();

			_company = themeDisplay.getCompany();
		}

		return _company;
	}

	public long getCompanyId() {
		if (_companyId == null) {
			ThemeDisplay themeDisplay = getThemeDisplay();

			_companyId = themeDisplay.getCompanyId();
		}

		return _companyId;
	}

	public String getCurrentURL() {
		if (_currentURL == null) {
			PortletURL portletURL = PortletURLUtil.getCurrent(
				getLiferayPortletRequest(), getLiferayPortletResponse());

			_currentURL = portletURL.toString();
		}

		return _currentURL;
	}

	public Layout getLayout() {
		if (_layout == null) {
			ThemeDisplay themeDisplay = getThemeDisplay();

			_layout = themeDisplay.getLayout();
		}

		return _layout;
	}

	public LiferayPortletRequest getLiferayPortletRequest() {
		if (_liferayPortletRequest == null) {
			PortletRequest portletRequest =
				(PortletRequest)_httpServletRequest.getAttribute(
					JavaConstants.JAKARTA_PORTLET_REQUEST);

			_liferayPortletRequest = PortalUtil.getLiferayPortletRequest(
				portletRequest);
		}

		return _liferayPortletRequest;
	}

	public LiferayPortletResponse getLiferayPortletResponse() {
		if (_liferayPortletResponse == null) {
			PortletResponse portletResponse =
				(PortletResponse)_httpServletRequest.getAttribute(
					JavaConstants.JAKARTA_PORTLET_RESPONSE);

			_liferayPortletResponse = PortalUtil.getLiferayPortletResponse(
				portletResponse);
		}

		return _liferayPortletResponse;
	}

	public Locale getLocale() {
		if (_locale == null) {
			ThemeDisplay themeDisplay = getThemeDisplay();

			_locale = themeDisplay.getLocale();
		}

		return _locale;
	}

	public PermissionChecker getPermissionChecker() {
		if (_permissionChecker == null) {
			ThemeDisplay themeDisplay = getThemeDisplay();

			_permissionChecker = themeDisplay.getPermissionChecker();
		}

		return _permissionChecker;
	}

	public PortletDisplay getPortletDisplay() {
		if (_portletDisplay == null) {
			ThemeDisplay themeDisplay = getThemeDisplay();

			_portletDisplay = themeDisplay.getPortletDisplay();
		}

		return _portletDisplay;
	}

	public String getPortletId() {
		if (_portletId == null) {
			PortletDisplay portletDisplay = getPortletDisplay();

			_portletId = portletDisplay.getId();
		}

		return _portletId;
	}

	public String getPortletName() {
		if (_portletName == null) {
			PortletDisplay portletDisplay = getPortletDisplay();

			_portletName = portletDisplay.getPortletName();
		}

		return _portletName;
	}

	public String getPortletResource() {
		if (_portletResource == null) {
			PortletDisplay portletDisplay = getPortletDisplay();

			_portletResource = portletDisplay.getPortletResource();
		}

		return _portletResource;
	}

	public String getPortletTitle() {
		if (_portletTitle == null) {
			PortletDisplay portletDisplay = getPortletDisplay();

			_portletTitle = portletDisplay.getTitle();
		}

		return _portletTitle;
	}

	public HttpServletRequest getRequest() {
		return _httpServletRequest;
	}

	public String getResourcePortletId() {
		if (_resourcePortletId == null) {
			if (Validator.isNotNull(getPortletResource())) {
				_resourcePortletId = getPortletResource();
			}
			else {
				_resourcePortletId = getPortletId();
			}
		}

		return _resourcePortletId;
	}

	public String getResourcePortletName() {
		if (_resourcePortletName == null) {
			String portletResource = getPortletResource();

			if (Validator.isNotNull(portletResource)) {
				_resourcePortletName = portletResource;
			}
			else {
				_resourcePortletName = getPortletName();
			}
		}

		return _resourcePortletName;
	}

	public long getScopeGroupId() {
		if (_scopeGroupId == null) {
			ThemeDisplay themeDisplay = getThemeDisplay();

			_scopeGroupId = themeDisplay.getScopeGroupId();
		}

		return _scopeGroupId;
	}

	public long getSiteGroupId() {
		if (_siteGroupId == null) {
			ThemeDisplay themeDisplay = getThemeDisplay();

			_siteGroupId = themeDisplay.getSiteGroupId();
		}

		return _siteGroupId;
	}

	public ThemeDisplay getThemeDisplay() {
		if (_themeDisplay == null) {
			_themeDisplay = (ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);
		}

		return _themeDisplay;
	}

	public User getUser() {
		if (_user == null) {
			ThemeDisplay themeDisplay = getThemeDisplay();

			_user = themeDisplay.getUser();
		}

		return _user;
	}

	public long getUserId() {
		if (_user == null) {
			ThemeDisplay themeDisplay = getThemeDisplay();

			_user = themeDisplay.getUser();
		}

		return _user.getUserId();
	}

	private Company _company;
	private Long _companyId;
	private String _currentURL;
	private final HttpServletRequest _httpServletRequest;
	private Layout _layout;
	private LiferayPortletRequest _liferayPortletRequest;
	private LiferayPortletResponse _liferayPortletResponse;
	private Locale _locale;
	private PermissionChecker _permissionChecker;
	private PortletDisplay _portletDisplay;
	private String _portletId;
	private String _portletName;
	private String _portletResource;
	private String _portletTitle;
	private String _resourcePortletId;
	private String _resourcePortletName;
	private Long _scopeGroupId;
	private Long _siteGroupId;
	private ThemeDisplay _themeDisplay;
	private User _user;

}