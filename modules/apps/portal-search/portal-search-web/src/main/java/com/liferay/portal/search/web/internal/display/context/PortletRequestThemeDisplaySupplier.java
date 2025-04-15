/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.display.context;

import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

/**
 * @author André de Oliveira
 */
public class PortletRequestThemeDisplaySupplier
	implements ThemeDisplaySupplier {

	public PortletRequestThemeDisplaySupplier(PortletRequest portletRequest) {
		_portletRequest = portletRequest;
	}

	@Override
	public ThemeDisplay getThemeDisplay() {
		return (ThemeDisplay)_portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	private final PortletRequest _portletRequest;

}