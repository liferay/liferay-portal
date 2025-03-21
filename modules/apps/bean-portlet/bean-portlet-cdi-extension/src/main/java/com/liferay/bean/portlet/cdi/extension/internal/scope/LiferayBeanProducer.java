/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.cdi.extension.internal.scope;

import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.enterprise.inject.Produces;

import jakarta.inject.Inject;
import jakarta.inject.Named;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.annotations.PortletRequestScoped;

/**
 * @author Neil Griffin
 */
public class LiferayBeanProducer {

	@Named("themeDisplay")
	@PortletRequestScoped
	@Produces
	public ThemeDisplay getThemeDisplay() {
		if (_portletRequest == null) {
			return null;
		}

		return (ThemeDisplay)_portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	@Inject
	private PortletRequest _portletRequest;

}