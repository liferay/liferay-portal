/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.announcements.web.internal.display.context.helper;

import com.liferay.portal.kernel.display.context.helper.BaseRequestHelper;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Adolfo Pérez
 */
public class AnnouncementsRequestHelper extends BaseRequestHelper {

	public AnnouncementsRequestHelper(HttpServletRequest httpServletRequest) {
		super(httpServletRequest);
	}

	public PortletPreferences getPortletPreferences() {
		if (_portletPreferences != null) {
			return _portletPreferences;
		}

		HttpServletRequest httpServletRequest = getRequest();

		PortletRequest portletRequest =
			(PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_REQUEST);

		_portletPreferences = portletRequest.getPreferences();

		return _portletPreferences;
	}

	public Group getScopeGroup() {
		if (_scopeGroup != null) {
			return _scopeGroup;
		}

		ThemeDisplay themeDisplay = getThemeDisplay();

		_scopeGroup = themeDisplay.getScopeGroup();

		return _scopeGroup;
	}

	public String getTabs1() {
		if (_tabs1 != null) {
			return _tabs1;
		}

		_tabs1 = ParamUtil.getString(getRequest(), "tabs1", "entries");

		return _tabs1;
	}

	private PortletPreferences _portletPreferences;
	private Group _scopeGroup;
	private String _tabs1;

}