/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.search;

import com.liferay.dynamic.data.mapping.form.web.internal.security.permission.resource.DDMStructurePermission;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;

import jakarta.portlet.PortletResponse;

/**
 * @author Rafael Praxedes
 */
public class FieldSetRowChecker extends EmptyOnClickRowChecker {

	public FieldSetRowChecker(PortletResponse portletResponse) {
		super(portletResponse);
	}

	@Override
	public boolean isDisabled(Object object) {
		DDMStructure structure = (DDMStructure)object;

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			if (!DDMStructurePermission.contains(
					permissionChecker, structure, ActionKeys.DELETE)) {

				return true;
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return super.isDisabled(object);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FieldSetRowChecker.class);

}