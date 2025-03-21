/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet.toolbar.contributor.locator;

import com.liferay.portal.kernel.portlet.toolbar.contributor.PortletToolbarContributor;

import jakarta.portlet.PortletRequest;

import java.util.List;

/**
 * Provides an interface responsible for providing {@link
 * PortletToolbarContributor} instances that extend the portlet toolbar by
 * adding more elements.
 *
 * <p>
 * Implementations of this class must use the OSGI Registry to return {@link
 * PortletToolbarContributor} implementations. The way that the {@link
 * PortletToolbarContributor}s are registered in OSGI Registry must be
 * synchronized with the way that implementations of this class searches for
 * them.
 * </p>
 *
 * <p>
 * Typically, implementations of this class leverage the MVC pattern used the by
 * the portlet. This allows for different extensions to the portlet toolbar for
 * different views of the portlet.
 * </p>
 *
 * <p>
 * Implementations of this class must be OSGI components.
 * </p>
 *
 * @author Sergio González
 */
public interface PortletToolbarContributorLocator {

	/**
	 * Returns portlet toolbar contributors for a particular portlet and
	 * request.
	 *
	 * @param  portletId the portlet's ID
	 * @param  portletRequest the portlet request
	 * @return portlet toolbar contributors for a particular portlet and request
	 */
	public List<PortletToolbarContributor> getPortletToolbarContributors(
		String portletId, PortletRequest portletRequest);

}