/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.password.policies.admin.web.internal.search;

import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.PasswordPolicy;
import com.liferay.portal.kernel.model.PasswordPolicyRel;
import com.liferay.portal.kernel.service.OrganizationLocalServiceUtil;
import com.liferay.portal.kernel.service.PasswordPolicyRelLocalServiceUtil;

import jakarta.portlet.RenderResponse;

/**
 * @author Scott Lee
 */
public class AddOrganizationPasswordPolicyChecker
	extends EmptyOnClickRowChecker {

	public AddOrganizationPasswordPolicyChecker(
		RenderResponse renderResponse, PasswordPolicy passwordPolicy) {

		super(renderResponse);

		_passwordPolicy = passwordPolicy;
	}

	@Override
	public boolean isChecked(Object object) {
		Organization organization = (Organization)object;

		try {
			return OrganizationLocalServiceUtil.hasPasswordPolicyOrganization(
				_passwordPolicy.getPasswordPolicyId(),
				organization.getOrganizationId());
		}
		catch (Exception exception) {
			_log.error(exception);

			return false;
		}
	}

	@Override
	public boolean isDisabled(Object object) {
		Organization organization = (Organization)object;

		if (isChecked(object)) {
			return true;
		}

		try {
			PasswordPolicyRel passwordPolicyRel =
				PasswordPolicyRelLocalServiceUtil.fetchPasswordPolicyRel(
					Organization.class.getName(),
					organization.getOrganizationId());

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
		AddOrganizationPasswordPolicyChecker.class);

	private final PasswordPolicy _passwordPolicy;

}