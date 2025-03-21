/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.password.policies.admin.web.internal.search;

import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.model.PasswordPolicy;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.service.permission.PasswordPolicyPermissionUtil;

import jakarta.portlet.RenderResponse;

/**
 * @author Pei-Jung Lan
 */
public class PasswordPolicyChecker extends EmptyOnClickRowChecker {

	public PasswordPolicyChecker(RenderResponse renderResponse) {
		super(renderResponse);
	}

	@Override
	public boolean isDisabled(Object object) {
		PasswordPolicy passwordPolicy = (PasswordPolicy)object;

		if (passwordPolicy.isDefaultPolicy() ||
			!PasswordPolicyPermissionUtil.contains(
				PermissionThreadLocal.getPermissionChecker(),
				passwordPolicy.getPasswordPolicyId(), ActionKeys.DELETE)) {

			return true;
		}

		return super.isDisabled(object);
	}

}