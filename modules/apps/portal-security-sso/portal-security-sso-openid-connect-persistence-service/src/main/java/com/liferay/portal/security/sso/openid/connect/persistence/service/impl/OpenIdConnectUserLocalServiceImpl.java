/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.persistence.service.impl;

import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.sso.openid.connect.persistence.exception.DuplicateOpenIdConnectUserException;
import com.liferay.portal.security.sso.openid.connect.persistence.exception.OpenIdConnectUserIssuerException;
import com.liferay.portal.security.sso.openid.connect.persistence.exception.OpenIdConnectUserSubjectException;
import com.liferay.portal.security.sso.openid.connect.persistence.model.OpenIdConnectUser;
import com.liferay.portal.security.sso.openid.connect.persistence.service.base.OpenIdConnectUserLocalServiceBaseImpl;

import java.util.Date;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Arthur Chan
 */
@Component(
	property = "model.class.name=com.liferay.portal.security.sso.openid.connect.persistence.model.OpenIdConnectUser",
	service = AopService.class
)
public class OpenIdConnectUserLocalServiceImpl
	extends OpenIdConnectUserLocalServiceBaseImpl {

	@Override
	public OpenIdConnectUser addOpenIdConnectUser(
			long userId, String issuer, String subject)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		OpenIdConnectUser openIdConnectUser =
			openIdConnectUserPersistence.fetchByC_I_S(
				user.getCompanyId(), issuer, subject);

		if (openIdConnectUser != null) {
			throw new DuplicateOpenIdConnectUserException();
		}

		if (Validator.isNull(issuer)) {
			throw new OpenIdConnectUserIssuerException("Issuer is null");
		}

		if (Validator.isNull(subject)) {
			throw new OpenIdConnectUserSubjectException("Subject is null");
		}

		long openIdConnectUserId = counterLocalService.increment();

		openIdConnectUser = openIdConnectUserPersistence.create(
			openIdConnectUserId);

		openIdConnectUser.setCompanyId(user.getCompanyId());
		openIdConnectUser.setUserId(user.getUserId());
		openIdConnectUser.setCreateDate(new Date());
		openIdConnectUser.setIssuer(issuer);
		openIdConnectUser.setSubject(subject);

		return openIdConnectUserPersistence.update(openIdConnectUser);
	}

	@Override
	public OpenIdConnectUser fetchOpenIdConnectUser(
		long companyId, String issuer, String subject) {

		return openIdConnectUserPersistence.fetchByC_I_S(
			companyId, issuer, subject);
	}

	@Reference
	private UserLocalService _userLocalService;

}