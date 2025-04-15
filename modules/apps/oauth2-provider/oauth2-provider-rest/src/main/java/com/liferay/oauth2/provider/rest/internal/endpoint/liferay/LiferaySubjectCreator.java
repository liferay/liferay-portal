/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.endpoint.liferay;

import com.liferay.oauth2.provider.rest.internal.endpoint.constants.OAuth2ProviderRESTEndpointConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.SecurityContext;

import java.security.Principal;

import java.util.Map;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.apache.cxf.rs.security.oauth2.provider.OAuthServiceException;
import org.apache.cxf.rs.security.oauth2.provider.SubjectCreator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carlos Sierra Andrés
 */
@Component(service = SubjectCreator.class)
public class LiferaySubjectCreator implements SubjectCreator {

	@Override
	public UserSubject createUserSubject(
			MessageContext messageContext,
			MultivaluedMap<String, String> params)
		throws OAuthServiceException {

		SecurityContext securityContext = messageContext.getSecurityContext();

		Principal userPrincipal = securityContext.getUserPrincipal();

		try {
			User user = _userLocalService.getUser(
				GetterUtil.getLong(userPrincipal.getName()));

			UserSubject userSubject = new UserSubject(
				user.getLogin(), String.valueOf(user.getUserId()));

			Map<String, String> properties = userSubject.getProperties();

			properties.put(
				OAuth2ProviderRESTEndpointConstants.PROPERTY_KEY_COMPANY_ID,
				String.valueOf(user.getCompanyId()));

			return userSubject;
		}
		catch (PortalException portalException) {
			throw new OAuthServiceException(portalException);
		}
	}

	@Reference
	private UserLocalService _userLocalService;

}