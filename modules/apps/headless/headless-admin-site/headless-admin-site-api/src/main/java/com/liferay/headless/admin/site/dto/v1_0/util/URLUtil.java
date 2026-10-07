/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.dto.v1_0.util;

import com.liferay.exportimport.attachment.ExportImportAttachmentManagerUtil;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.InetAddressUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.configuration.URLFetchSecurityCompanyConfiguration;

import java.io.InputStream;

import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;

import java.util.Objects;

/**
 * @author Lourdes Fernández Besada
 */
public class URLUtil {

	public static byte[] getByteArray(String urlString) throws Exception {
		URL url = ExportImportAttachmentManagerUtil.getURL(urlString);

		String protocol = url.getProtocol();

		if (Objects.equals(protocol, Http.HTTP) ||
			Objects.equals(protocol, Http.HTTPS)) {

			return _getByteArray(url, urlString);
		}

		if (Objects.equals(protocol, "lar")) {
			URLConnection urlConnection = url.openConnection();

			try (InputStream inputStream = urlConnection.getInputStream()) {
				return StreamUtil.toByteArray(inputStream);
			}
		}

		throw new UnsupportedOperationException(
			StringBundler.concat(
				"Unable to download file from ", urlString,
				" because of unsupported protocol ", protocol));
	}

	private static byte[] _getByteArray(URL url, String urlString)
		throws Exception {

		String host = url.getHost();

		if (!_isAllowedHost(host)) {
			throw new UnsupportedOperationException(
				StringBundler.concat(
					"Unable to download file from ", urlString,
					" because of restricted host ", host));
		}

		Http.Options options = new Http.Options();

		options.setFollowRedirects(false);
		options.setLocation(url.toString());

		return HttpUtil.URLtoByteArray(options);
	}

	private static boolean _isAllowedHost(String host) {
		if (Validator.isNull(host)) {
			return false;
		}

		try {
			URLFetchSecurityCompanyConfiguration
				urlFetchSecurityCompanyConfiguration =
					ConfigurationProviderUtil.getCompanyConfiguration(
						URLFetchSecurityCompanyConfiguration.class,
						CompanyThreadLocal.getCompanyId());

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

			InetAddress inetAddress = InetAddressUtil.getInetAddressByName(
				host);

			if (inetAddress == null) {
				return false;
			}

			return !InetAddressUtil.isLocalInetAddress(inetAddress);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(URLUtil.class);

}