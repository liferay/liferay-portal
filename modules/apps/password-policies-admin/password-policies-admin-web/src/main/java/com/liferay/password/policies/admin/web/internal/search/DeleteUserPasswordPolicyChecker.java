/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.password.policies.admin.web.internal.search;

import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.PasswordPolicy;
import com.liferay.portal.kernel.model.PasswordPolicyRel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.PasswordPolicyRelLocalServiceUtil;

import jakarta.portlet.RenderResponse;

/**
 * @author Scott Lee
 */
public class DeleteUserPasswordPolicyChecker extends EmptyOnClickRowChecker {

	public DeleteUserPasswordPolicyChecker(
		RenderResponse renderResponse, PasswordPolicy passwordPolicy) {

		super(renderResponse);

		_passwordPolicy = passwordPolicy;
	}

	@Override
	public boolean isDisabled(Object object) {
		User user = (User)object;

		try {
			PasswordPolicyRel passwordPolicyRel =
				PasswordPolicyRelLocalServiceUtil.fetchPasswordPolicyRel(
					User.class.getName(), user.getUserId());

			if ((passwordPolicyRel != null) &&
				(passwordPolicyRel.getPasswordPolicyId() !=
					_passwordPolicy.getPasswordPolicyId())) {

				return true;
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DeleteUserPasswordPolicyChecker.class);

	private final PasswordPolicy _passwordPolicy;

}