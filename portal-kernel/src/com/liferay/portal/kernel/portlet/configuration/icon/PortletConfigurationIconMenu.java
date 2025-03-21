/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet.configuration.icon;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import java.util.Comparator;
import java.util.List;

/**
 * @author Eduardo García
 */
public class PortletConfigurationIconMenu {

	public static final PortletConfigurationIconMenu INSTANCE =
		new PortletConfigurationIconMenu();

	public List<PortletConfigurationIcon> getPortletConfigurationIcons(
		String portletId, PortletRequest portletRequest,
		PortletResponse portletResponse) {

		return PortletConfigurationIconTracker.getPortletConfigurationIcons(
			portletId, portletRequest, _comparator);
	}

	public void setComparator(Comparator<?> comparator) {
		_comparator = comparator;
	}

	private PortletConfigurationIconMenu() {
	}

	private Comparator<?> _comparator;

}