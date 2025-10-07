/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.type.controller.empty.internal.model;

import com.liferay.layout.type.controller.model.BaseLayoutTypeAccessPolicy;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutTypeAccessPolicy;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

import org.osgi.service.component.annotations.Component;

/**
 * @author Brooke Dalton
 */
@Component(
	property = "layout.type=" + LayoutConstants.TYPE_EMPTY,
	service = LayoutTypeAccessPolicy.class
)
public class EmptyLayoutTypeAccessPolicy extends BaseLayoutTypeAccessPolicy {

	@Override
	public boolean isCustomizeLayoutAllowed(
		PermissionChecker permissionChecker, Layout layout) {

		return false;
	}

	@Override
	public boolean isUpdateLayoutAllowed(
		PermissionChecker permissionChecker, Layout layout) {

		return false;
	}

	@Override
	public boolean isViewLayoutAllowed(
			PermissionChecker permissionChecker, Layout layout)
		throws PortalException {

		return false;
	}

}