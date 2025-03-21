/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.menu;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import java.util.Dictionary;
import java.util.Locale;

/**
 * @author Drew Brokke  Contributes a menu item to be displayed in the
 *         auto-generated of a particular configuration. Implementations must be
 *         registered as a ConfigurationMenuItem service, and must have the
 *         property "configuration.pid" whose value matches the ID of the
 *         corresponding configuration interface (usually the fully qualified
 *         class name).
 * @review
 */
public interface ConfigurationMenuItem {

	public String getLabel(Locale locale);

	public String getURL(
		PortletRequest portletRequest, PortletResponse portletResponse,
		String pid, String factoryPid, Dictionary<String, Object> properties);

}