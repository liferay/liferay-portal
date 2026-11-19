/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.endpoint.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.cxf.message.Message;
import org.apache.cxf.rs.security.jose.jwt.JwtClaims;
import org.apache.cxf.rs.security.jose.jwt.JwtConstants;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

/**
 * @author Jorge García Jiménez
 */
public class OAuth2HttpRequestUtil {

	public static HttpServletRequest getHttpServletRequest() {
		return getHttpServletRequest(JAXRSUtils.getCurrentMessage());
	}

	public static HttpServletRequest getHttpServletRequest(Message message) {
		if (message == null) {
			return null;
		}

		return (HttpServletRequest)message.get(
			AbstractHTTPDestination.HTTP_REQUEST);
	}

	public static String getRequestURI() {
		HttpServletRequest httpServletRequest = getHttpServletRequest();

		if (httpServletRequest == null) {
			return StringPool.BLANK;
		}

		return httpServletRequest.getRequestURI();
	}

	public static void putExpectedJWTAudience(
		JwtClaims jwtClaims, Message message) {

		HttpServletRequest httpServletRequest = getHttpServletRequest(message);

		if (httpServletRequest == null) {
			return;
		}

		String audience =
			PortalUtil.getPortalURL(httpServletRequest) +
				httpServletRequest.getRequestURI();
		List<String> audiences = jwtClaims.getAudiences();

		if (audiences.contains(audience)) {
			message.put(JwtConstants.EXPECTED_CLAIM_AUDIENCE, audience);
		}
	}

}