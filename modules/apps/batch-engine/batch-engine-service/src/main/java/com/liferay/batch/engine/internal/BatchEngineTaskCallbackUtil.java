/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.batch.engine.configuration.BatchEngineTaskCompanyConfiguration;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.InetAddressUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.net.URI;

import java.util.Collections;
import java.util.Objects;

/**
 * @author Ivica Cardic
 */
public class BatchEngineTaskCallbackUtil {

	public static void sendCallback(
		String callbackURL, long companyId, String executeStatus, long id) {

		if (Validator.isBlank(callbackURL)) {
			return;
		}

		if (!_isAllowedCallbackURL(callbackURL, companyId)) {
			if (_log.isWarnEnabled()) {
				_log.warn("Skipping callback to disallowed URL " + callbackURL);
			}

			return;
		}

		try {
			Http.Options options = new Http.Options();

			options.addHeader(
				HttpHeaders.CONTENT_TYPE, ContentTypes.APPLICATION_JSON);
			options.setBody(
				_objectMapper.writeValueAsString(
					Collections.singletonMap(id, executeStatus)),
				ContentTypes.APPLICATION_JSON, StringPool.UTF8);
			options.setFollowRedirects(false);
			options.setLocation(callbackURL);
			options.setPost(true);

			HttpUtil.URLtoString(options);
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private static boolean _isAllowedCallbackURL(
		String callbackURL, long companyId) {

		try {
			URI uri = new URI(callbackURL);

			String scheme = StringUtil.toLowerCase(uri.getScheme());

			if (!Objects.equals(scheme, "http") &&
				!Objects.equals(scheme, "https")) {

				return false;
			}

			String host = uri.getHost();

			if (Validator.isNull(host)) {
				return false;
			}

			BatchEngineTaskCompanyConfiguration
				batchEngineTaskCompanyConfiguration =
					ConfigurationProviderUtil.getCompanyConfiguration(
						BatchEngineTaskCompanyConfiguration.class, companyId);

			String[] callbackURLHostsAllowed =
				batchEngineTaskCompanyConfiguration.callbackURLHostsAllowed();

			if (ArrayUtil.isNotEmpty(callbackURLHostsAllowed) &&
				!ArrayUtil.contains(callbackURLHostsAllowed, host, true)) {

				return false;
			}

			if (batchEngineTaskCompanyConfiguration.
					callbackURLLocalNetworkAccessEnabled()) {

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
		BatchEngineTaskCallbackUtil.class);

	private static final ObjectMapper _objectMapper = new ObjectMapper();

}