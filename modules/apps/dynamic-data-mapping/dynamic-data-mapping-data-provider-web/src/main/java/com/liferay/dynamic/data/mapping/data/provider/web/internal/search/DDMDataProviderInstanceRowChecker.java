/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.data.provider.web.internal.search;

import com.liferay.dynamic.data.mapping.data.provider.web.internal.security.permission.resource.DDMDataProviderInstancePermission;
import com.liferay.dynamic.data.mapping.model.DDMDataProviderInstance;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;

import jakarta.portlet.PortletResponse;

/**
 * @author Lino Alves
 */
public class DDMDataProviderInstanceRowChecker extends EmptyOnClickRowChecker {

	public DDMDataProviderInstanceRowChecker(PortletResponse portletResponse) {
		super(portletResponse);
	}

	@Override
	public boolean isDisabled(Object object) {
		DDMDataProviderInstance ddmDataProviderInstance =
			(DDMDataProviderInstance)object;

		try {
			if (!DDMDataProviderInstancePermission.contains(
					PermissionThreadLocal.getPermissionChecker(),
					ddmDataProviderInstance, ActionKeys.DELETE)) {

				return true;
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return super.isDisabled(object);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDMDataProviderInstanceRowChecker.class);

}