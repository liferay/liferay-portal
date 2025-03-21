/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.token.internal.security.auth;

import com.liferay.portal.security.sso.token.security.auth.TokenLocation;
import com.liferay.portal.security.sso.token.security.auth.TokenRetriever;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

/**
 * @author Michael C. Han
 */
@Component(
	property = "token.location=" + TokenLocation.REQUEST_HEADER,
	service = TokenRetriever.class
)
public class RequestHeaderTokenRetriever implements TokenRetriever {

	@Override
	public String getLoginToken(
		HttpServletRequest httpServletRequest, String userTokenName) {

		return httpServletRequest.getHeader(userTokenName);
	}

}