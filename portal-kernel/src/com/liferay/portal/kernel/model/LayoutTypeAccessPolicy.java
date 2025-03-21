/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.model;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

/**
 * @author Adolfo Pérez
 */
public interface LayoutTypeAccessPolicy extends Serializable {

	public void checkAccessAllowedToPortlet(
			HttpServletRequest httpServletRequest, Layout layout,
			Portlet portlet)
		throws PortalException;

	public boolean isAddLayoutAllowed(
			PermissionChecker permissionChecker, Layout layout)
		throws PortalException;

	public boolean isCustomizeLayoutAllowed(
			PermissionChecker permissionChecker, Layout layout)
		throws PortalException;

	public boolean isDeleteLayoutAllowed(
			PermissionChecker permissionChecker, Layout layout)
		throws PortalException;

	public boolean isUpdateLayoutAllowed(
			PermissionChecker permissionChecker, Layout layout)
		throws PortalException;

	public boolean isViewLayoutAllowed(
			PermissionChecker permissionChecker, Layout layout)
		throws PortalException;

}