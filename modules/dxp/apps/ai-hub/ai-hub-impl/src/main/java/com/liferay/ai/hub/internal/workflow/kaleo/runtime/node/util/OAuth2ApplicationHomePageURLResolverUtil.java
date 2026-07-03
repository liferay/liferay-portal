/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.workflow.kaleo.runtime.node.util;

import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.InetAddressUtil;
import com.liferay.portal.kernel.util.PortalRunMode;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * @author Pedro Leite
 */
public class OAuth2ApplicationHomePageURLResolverUtil {

	public static String resolve(long oAuth2ApplicationId)
		throws PortalException {

		OAuth2Application oAuth2Application =
			OAuth2ApplicationLocalServiceUtil.getOAuth2Application(
				oAuth2ApplicationId);

		String homePageURL = oAuth2Application.getHomePageURL();

		try {
			URL url = new URL(homePageURL);

			if (!PortalRunMode.isTestMode() &&
				InetAddressUtil.isLocalInetAddress(
					InetAddressUtil.getInetAddressByName(url.getHost()))) {

				throw new PortalException(
					"The OAuth2 application home page URL must not be local: " +
						homePageURL);
			}
		}
		catch (MalformedURLException | UnknownHostException exception) {
			throw new PortalException(
				"The OAuth2 application home page URL is invalid: " +
					homePageURL,
				exception);
		}

		return homePageURL;
	}

}