/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet.configuration.icon.locator;

import jakarta.portlet.PortletRequest;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public interface PortletConfigurationIconLocator {

	public List<String> getDefaultViews(String portletId);

	public String getPath(PortletRequest portletRequest);

}