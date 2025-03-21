/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.search;

import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.order.web.internal.security.permission.resource.CommerceOrderPermission;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;

import jakarta.portlet.PortletResponse;

/**
 * @author Andrea Di Giorgi
 */
public class CommerceOrderChecker extends EmptyOnClickRowChecker {

	public CommerceOrderChecker(PortletResponse portletResponse) {
		super(portletResponse);
	}

	@Override
	public boolean isDisabled(Object object) {
		CommerceOrder commerceOrder = (CommerceOrder)object;

		try {
			return !CommerceOrderPermission.contains(
				PermissionThreadLocal.getPermissionChecker(), commerceOrder,
				ActionKeys.DELETE);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return super.isDisabled(object);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceOrderChecker.class);

}