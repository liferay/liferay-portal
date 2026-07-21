/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.web.internal.model;

import com.liferay.digital.signature.constants.DigitalSignaturePortletKeys;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypeAccessPolicy;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.impl.DefaultLayoutTypeAccessPolicyImpl;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.StringUtil;

import org.osgi.service.component.annotations.Component;

/**
 * @author Danny Situ
 */
@Component(
	property = "layout.type=" + DigitalSignaturePortletKeys.SIGN_DIGITAL_SIGNATURE,
	service = LayoutTypeAccessPolicy.class
)
public class SignDSPortletLayoutTypeAccessPolicy
	extends DefaultLayoutTypeAccessPolicyImpl {

	@Override
	public boolean isAddLayoutAllowed(
		PermissionChecker permissionChecker, Layout layout) {

		return false;
	}

	@Override
	public boolean isCustomizeLayoutAllowed(
		PermissionChecker permissionChecker, Layout layout) {

		return false;
	}

	@Override
	public boolean isDeleteLayoutAllowed(
		PermissionChecker permissionChecker, Layout layout) {

		return false;
	}

	@Override
	public boolean isUpdateLayoutAllowed(
		PermissionChecker permissionChecker, Layout layout) {

		return false;
	}

	@Override
	protected boolean isAccessGrantedByPortletOnPage(
		Layout layout, Portlet portlet) {

		return StringUtil.equalsIgnoreCase(
			portlet.getPortletId(),
			DigitalSignaturePortletKeys.SIGN_DIGITAL_SIGNATURE);
	}

}