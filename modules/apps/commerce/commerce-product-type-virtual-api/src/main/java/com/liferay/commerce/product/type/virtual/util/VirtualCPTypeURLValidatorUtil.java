/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.util;

import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.InetAddressUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.configuration.URLFetchSecurityCompanyConfiguration;

import java.net.URI;

import java.util.Objects;

/**
 * @author Stefano Motta
 */
public class VirtualCPTypeURLValidatorUtil {

	public static boolean isValidURL(long companyId, String url) {
		try {
			URI uri = new URI(url);

			String scheme = StringUtil.toLowerCase(uri.getScheme());

			if (!Objects.equals(scheme, Http.HTTP) &&
				!Objects.equals(scheme, Http.HTTPS)) {

				return false;
			}

			String host = uri.getHost();

			if (Validator.isNull(host)) {
				return false;
			}

			URLFetchSecurityCompanyConfiguration
				urlFetchSecurityCompanyConfiguration =
					ConfigurationProviderUtil.getCompanyConfiguration(
						URLFetchSecurityCompanyConfiguration.class, companyId);

			String[] urlHostsAllowed =
				urlFetchSecurityCompanyConfiguration.urlHostsAllowed();

			if (ArrayUtil.isNotEmpty(urlHostsAllowed) &&
				!ArrayUtil.contains(urlHostsAllowed, host, true)) {

				return false;
			}

			if (urlFetchSecurityCompanyConfiguration.
					urlLocalNetworkAccessEnabled()) {

				return true;
			}

			return !InetAddressUtil.isLocalInetAddress(
				InetAddressUtil.getInetAddressByName(host));
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return false;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		VirtualCPTypeURLValidatorUtil.class);

}