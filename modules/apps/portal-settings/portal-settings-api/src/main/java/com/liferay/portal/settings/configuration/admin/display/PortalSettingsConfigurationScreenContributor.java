/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.settings.configuration.admin.display;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

/**
 * @author Drew Brokke
 */
public interface PortalSettingsConfigurationScreenContributor {

	public String getCategoryKey();

	public default String getDeleteMVCActionCommandName() {
		return null;
	}

	public String getJspPath();

	public String getKey();

	public default String getName(Locale locale) {
		return getKey();
	}

	public String getSaveMVCActionCommandName();

	public ServletContext getServletContext();

	public default String getTestButtonLabel(Locale locale) {
		return null;
	}

	public default String getTestButtonOnClick(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		return null;
	}

	public default boolean isDeprecated() {
		return false;
	}

	public default boolean isVisible() {
		return true;
	}

	public default void setAttributes(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {
	}

}