/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.personal.menu;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

/**
 * Provides an interface that defines the entries to be used by a {@code
 * liferay-product-navigation:user-personal-menu} tag instance to render a new
 * user personal menu entry.
 *
 * <p>
 * Implementations must be registered in the OSGi Registry. The user personal
 * menu entry order is determined by the {@code
 * product.navigation.personal.menu.entry.order} property value. The entry's
 * section placement in the menu is determined by the {@code
 * product.navigation.personal.menu.group} property value.
 * </p>
 *
 * @author Pei-Jung Lan
 */
public interface PersonalMenuEntry {

	/**
	 * Returns the icon name to display in the entry.
	 *
	 * @param  portletRequest the portlet request
	 * @return the icon name to display in the entry
	 */
	public default String getIcon(PortletRequest portletRequest) {
		return StringPool.BLANK;
	}

	public default JSONObject getJSOnClickConfigJSONObject(
		HttpServletRequest httpServletRequest) {

		return null;
	}

	/**
	 * Returns the label that is displayed in the user personal menu.
	 *
	 * @param  locale the label's retrieved locale
	 * @return the label of the user personal menu entry
	 */
	public String getLabel(Locale locale);

	public default String getOnClickESModule(
		HttpServletRequest httpServletRequest) {

		return null;
	}

	/**
	 * Returns the URL used to render a portlet based on the servlet request
	 * attributes.
	 *
	 * @param  httpServletRequest the servlet request used to create a portlet's
	 *         URL
	 * @return the portlet's URL used to render a portlet
	 * @throws PortalException if a portal exception occurred
	 */
	public String getPortletURL(HttpServletRequest httpServletRequest)
		throws PortalException;

	/**
	 * Returns {@code true} if the entry is the current active entry.
	 *
	 * @param  portletRequest the portlet request
	 * @param  portletId the portlet's ID
	 * @return {@code true} if the entry is the current active entry; {@code
	 *         false} otherwise
	 */
	public default boolean isActive(
			PortletRequest portletRequest, String portletId)
		throws PortalException {

		return false;
	}

	/**
	 * Returns {@code true} if the entry should be displayed in the user
	 * personal menu.
	 *
	 * @param  portletRequest the portlet request
	 * @param  permissionChecker the permission checker
	 * @return {@code true} if the entry should be displayed in the user
	 *         personal menu; {@code false} otherwise
	 */
	public default boolean isShow(
			PortletRequest portletRequest, PermissionChecker permissionChecker)
		throws PortalException {

		return true;
	}

}