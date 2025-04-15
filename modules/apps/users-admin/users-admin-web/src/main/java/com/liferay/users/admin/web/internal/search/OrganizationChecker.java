/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.search;

import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.permission.OrganizationPermissionUtil;

import jakarta.portlet.RenderResponse;

/**
 * @author Pei-Jung Lan
 */
public class OrganizationChecker extends EmptyOnClickRowChecker {

	public OrganizationChecker(RenderResponse renderResponse) {
		super(renderResponse);
	}

	@Override
	public boolean isDisabled(Object object) {
		Organization organization = (Organization)object;

		try {
			if (!OrganizationPermissionUtil.contains(
					PermissionThreadLocal.getPermissionChecker(), organization,
					ActionKeys.DELETE)) {

				return true;
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return super.isDisabled(object);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OrganizationChecker.class);

}