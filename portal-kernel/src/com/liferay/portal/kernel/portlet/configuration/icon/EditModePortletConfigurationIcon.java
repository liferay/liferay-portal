/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet.configuration.icon;

import com.liferay.petra.string.StringPool;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Eudaldo Alonso
 */
@ProviderType
public interface EditModePortletConfigurationIcon {

	public static final int PORTLET_CONFIGURATION_ICON_GROUP_BASIC_OPERATIONS =
		1;

	public static final int PORTLET_CONFIGURATION_ICON_GROUP_CONFIGURATION = 2;

	public default String getIcon() {
		return StringPool.BLANK;
	}

	public default int getPortletConfigurationIconGroup() {
		return PORTLET_CONFIGURATION_ICON_GROUP_BASIC_OPERATIONS;
	}

	public String getTitle(HttpServletRequest httpServletRequest);

	public String getURL(
		HttpServletRequest httpServletRequest, String portletResource);

	public boolean isShow(
		HttpServletRequest httpServletRequest, String portletResource);

}