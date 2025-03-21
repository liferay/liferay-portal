/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.redirect;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;

/**
 * @author Raymond Augé
 */
public class OAuth2RedirectURIInterpolator {

	public static final String TOKEN_PORT_WITH_COLON = "@port-with-colon@";

	public static final String TOKEN_PROTOCOL = "@protocol@";

	public static List<String> interpolateRedirectURIsList(
		HttpServletRequest httpServletRequest, List<String> redirectURIsList,
		Portal portal) {

		return TransformUtil.transform(
			redirectURIsList,
			redirectURI -> {
				boolean secure = false;

				if (httpServletRequest != null) {
					secure = portal.isSecure(httpServletRequest);
				}
				else if (Http.HTTPS.equals(
							PropsUtil.get(
								PropsKeys.PORTAL_INSTANCE_PROTOCOL)) ||
						 Http.HTTPS.equals(
							 PropsUtil.get(PropsKeys.WEB_SERVER_PROTOCOL))) {

					secure = true;
				}

				String protocol = Http.HTTP;

				if (secure) {
					protocol = Http.HTTPS;
				}

				String portWithColon = ":" + portal.getPortalLocalPort(secure);

				if (httpServletRequest != null) {
					portWithColon =
						":" + portal.getForwardedPort(httpServletRequest);
				}

				if (Objects.equals(portWithColon, ":80") ||
					(secure && Objects.equals(portWithColon, ":443"))) {

					portWithColon = StringPool.BLANK;
				}

				return StringUtil.replace(
					redirectURI,
					new String[] {TOKEN_PORT_WITH_COLON, TOKEN_PROTOCOL},
					new String[] {portWithColon, protocol});
			});
	}

}