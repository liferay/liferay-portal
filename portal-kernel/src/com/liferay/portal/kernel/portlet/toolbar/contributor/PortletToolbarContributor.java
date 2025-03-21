/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet.toolbar.contributor;

import com.liferay.portal.kernel.servlet.taglib.ui.Menu;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import java.util.List;

/**
 * Provides an interface responsible for extending the portlet toolbar by adding
 * more elements.
 *
 * <p>
 * Implementations of this class must be OSGI components that are registered in
 * the OSGI Registry. The way that this component is registered in the OSGI
 * Registry must be consistent with the way the {@link
 * com.liferay.portal.kernel.portlet.toolbar.contributor.locator.PortletToolbarContributorLocator}
 * implementations search for it in the registry.
 * </p>
 *
 * @author Sergio González
 */
public interface PortletToolbarContributor {

	/**
	 * Returns menus to be rendered in the portlet toolbar.
	 *
	 * @param  portletRequest the portlet request
	 * @return menus to be rendered in the portlet toolbar
	 */
	public List<Menu> getPortletTitleMenus(
		PortletRequest portletRequest, PortletResponse portletResponse);

	public default boolean isShowInEditMode() {
		return false;
	}

}