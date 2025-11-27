/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.persistence.service.impl;

import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.security.sso.openid.connect.constants.OpenIdConnectWebKeys;
import com.liferay.portal.security.sso.openid.connect.persistence.model.OpenIdConnectSession;
import com.liferay.portal.security.sso.openid.connect.persistence.service.base.OpenIdConnectSessionLocalServiceBaseImpl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Arthur Chan
 */
@Component(
	property = "model.class.name=com.liferay.portal.security.sso.openid.connect.persistence.model.OpenIdConnectSession",
	service = AopService.class
)
public class OpenIdConnectSessionLocalServiceImpl
	extends OpenIdConnectSessionLocalServiceBaseImpl {

	@Override
	public void deleteOpenIdConnectSessions(long userId) {
		openIdConnectSessionPersistence.removeByUserId(userId);
	}

	@Override
	public void deleteOpenIdConnectSessions(
		long companyId, String authServerWellKnownURI, String clientId) {

		openIdConnectSessionPersistence.removeByC_A_C(
			companyId, authServerWellKnownURI, clientId);
	}

	@Override
	public OpenIdConnectSession fetchCurrentOpenIdConnectSession() {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext == null) {
			return null;
		}

		HttpServletRequest httpServletRequest = serviceContext.getRequest();

		if (httpServletRequest == null) {
			return null;
		}

		HttpSession httpSession = httpServletRequest.getSession(false);

		if (httpSession == null) {
			return null;
		}

		return openIdConnectSessionPersistence.fetchByPrimaryKey(
			GetterUtil.getLong(
				httpSession.getAttribute(
					OpenIdConnectWebKeys.OPEN_ID_CONNECT_SESSION_ID)));
	}

	@Override
	public OpenIdConnectSession fetchOpenIdConnectSession(
		long userId, String authServerWellKnownURI, String clientId) {

		return openIdConnectSessionPersistence.fetchByU_A_C(
			userId, authServerWellKnownURI, clientId);
	}

	@Override
	public OpenIdConnectSession fetchOpenIdConnectSession(
		String authServerWellKnownURI, String sessionId) {

		return openIdConnectSessionPersistence.fetchByA_S(
			authServerWellKnownURI, sessionId);
	}

	@Override
	public List<OpenIdConnectSession>
		getAccessTokenExpirationDateOpenIdConnectSessions(
			Date ltAccessTokenExpirationDate, int start, int end) {

		return openIdConnectSessionPersistence.
			findByLtAccessTokenExpirationDate(
				ltAccessTokenExpirationDate, start, end);
	}

}