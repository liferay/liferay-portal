/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.permission.propagator;

import com.liferay.portal.kernel.exception.PortalException;

import jakarta.portlet.ActionRequest;

/**
 * @author Hugo Huijser
 */
public interface PermissionPropagator {

	public void propagateRolePermissions(
			ActionRequest actionRequest, String className, String primKey,
			long[] roleIds)
		throws PortalException;

}