/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.accessibility.menu.web.internal.product.navigation.personal.menu;

import com.liferay.accessibility.menu.web.internal.constants.AccessibilityMenuPortletKeys;
import com.liferay.accessibility.menu.web.internal.util.AccessibilitySettingsUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilderFactory;
import com.liferay.product.navigation.personal.menu.BasePersonalMenuEntry;
import com.liferay.product.navigation.personal.menu.PersonalMenuEntry;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Thiago Buarque
 */
@Component(
	property = {
		"product.navigation.personal.menu.entry.order:Integer=50",
		"product.navigation.personal.menu.group:Integer=300"
	},
	service = PersonalMenuEntry.class
)
public class AccessibilityMenuPersonalMenuEntry extends BasePersonalMenuEntry {

	@Override
	public String getOnClickESModule(HttpServletRequest httpServletRequest) {
		AbsolutePortalURLBuilder absolutePortalURLBuilder =
			_absolutePortalURLBuilderFactory.getAbsolutePortalURLBuilder(
				httpServletRequest);

		String moduleURL = absolutePortalURLBuilder.forESModule(
			"accessibility-menu-web", "index.js"
		).build();

		return "{accessibilityMenuOpener} from " + moduleURL;
	}

	@Override
	public String getPortletId() {
		return AccessibilityMenuPortletKeys.ACCESSIBILITY_MENU;
	}

	@Override
	public String getPortletURL(HttpServletRequest httpServletRequest) {
		return null;
	}

	@Override
	public boolean isShow(
		PortletRequest portletRequest, PermissionChecker permissionChecker) {

		return AccessibilitySettingsUtil.isAccessibilityMenuEnabled(
			_portal.getHttpServletRequest(portletRequest),
			_configurationProvider);
	}

	@Reference
	private AbsolutePortalURLBuilderFactory _absolutePortalURLBuilderFactory;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Portal _portal;

}