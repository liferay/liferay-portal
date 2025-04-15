/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.provider;

import com.liferay.portal.kernel.model.Group;

import jakarta.portlet.PortletRequest;

/**
 * @author Julio Camarero
 */
public interface GroupURLProvider {

	public String getGroupAdministrationURL(
		Group group, PortletRequest portletRequest);

	public String getGroupLayoutsURL(
		Group group, boolean privateLayout, PortletRequest portletRequest);

	public String getGroupURL(Group group, PortletRequest portletRequest);

	public String getLiveGroupURL(Group group, PortletRequest portletRequest);

}