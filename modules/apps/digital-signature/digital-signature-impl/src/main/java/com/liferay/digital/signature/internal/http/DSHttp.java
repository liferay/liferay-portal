/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.internal.http;

import com.liferay.digital.signature.configuration.DigitalSignatureConfiguration;
import com.liferay.digital.signature.configuration.DigitalSignatureConfigurationUtil;
import com.liferay.digital.signature.internal.web.cache.DSAccessTokenWebCacheItem;
import com.liferay.petra.function.RetryableUnsafeSupplier;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.Validator;

import java.net.SocketTimeoutException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(service = DSHttp.class)
public class DSHttp {

	public JSONObject get(long companyId, long groupId, String location) {
		try {
			return _invoke(companyId, groupId, location, Http.Method.GET, null);
		}
		catch (Exception exception) {
			return ReflectionUtil.throwException(exception);
		}
	}

	public byte[] getAsBytes(long companyId, long groupId, String location) {
		try {
			return _invokeAsBytes(
				companyId, groupId, location, Http.Method.GET, null);
		}
		catch (Exception exception) {
			return ReflectionUtil.throwException(exception);
		}
	}

	public JSONObject post(
		long companyId, long groupId, String location,
		JSONObject bodyJSONObject) {

		try {
			return _invoke(
				companyId, groupId, location, Http.Method.POST, bodyJSONObject);
		}
		catch (Exception exception) {
			return ReflectionUtil.throwException(exception);
		}
	}

	public JSONObject put(
		long companyId, long groupId, String location,
		JSONObject bodyJSONObject) {

		try {
			return _invoke(
				companyId, groupId, location, Http.Method.PUT, bodyJSONObject);
		}
		catch (Exception exception) {
			return ReflectionUtil.throwException(exception);
		}
	}

	private String _getDocuSignAccessToken(
			DigitalSignatureConfiguration digitalSignatureConfiguration)
		throws Exception {

		JSONObject jsonObject = DSAccessTokenWebCacheItem.get(
			digitalSignatureConfiguration.apiUsername(),
			digitalSignatureConfiguration.environment(),
			digitalSignatureConfiguration.integrationKey(),
			digitalSignatureConfiguration.rsaPrivateKey());

		if (Validator.isNotNull(jsonObject.getString("error"))) {
			throw new PortalException(jsonObject.getString("error"));
		}

		return jsonObject.getString("access_token");
	}

	private int _getMaxRetries(int httpTimeout) {
		return Math.max(
			0, Math.min(_MAX_RETRIES, (httpTimeout / _MINIMUM_TIMEOUT) - 1));
	}

	private JSONObject _invoke(
			long companyId, long groupId, String location, Http.Method method,
			JSONObject bodyJSONObject)
		throws Exception {

		byte[] bytes = _invokeAsBytes(
			companyId, groupId, location, method, bodyJSONObject);

		if (bytes == null) {
			return _jsonFactory.createJSONObject();
		}

		return _jsonFactory.createJSONObject(new String(bytes));
	}

	private byte[] _invokeAsBytes(
			long companyId, long groupId, String location, Http.Method method,
			JSONObject bodyJSONObject)
		throws Exception {

		Http.Options options = new Http.Options();

		if (bodyJSONObject != null) {
			options.addHeader(
				HttpHeaders.CONTENT_TYPE, ContentTypes.APPLICATION_JSON);
		}

		DigitalSignatureConfiguration digitalSignatureConfiguration =
			DigitalSignatureConfigurationUtil.getDigitalSignatureConfiguration(
				companyId, groupId);

		options.addHeader(
			"Authorization",
			"Bearer " + _getDocuSignAccessToken(digitalSignatureConfiguration));

		if (bodyJSONObject != null) {
			options.setBody(
				bodyJSONObject.toString(), ContentTypes.APPLICATION_JSON,
				StringPool.UTF8);
		}

		options.setLocation(
			StringBundler.concat(
				digitalSignatureConfiguration.accountBaseURI(),
				"/restapi/v2.1/accounts/",
				digitalSignatureConfiguration.apiAccountId(), "/", location));
		options.setMethod(method);

		int maxRetries = _getMaxRetries(
			digitalSignatureConfiguration.httpTimeout());

		options.setTimeout(
			digitalSignatureConfiguration.httpTimeout() / (maxRetries + 1));

		RetryableUnsafeSupplier<byte[], Exception> retryableUnsafeSupplier =
			new RetryableUnsafeSupplier<>(
				(exception, curMaxRetries, retryCount) -> {
					if (!_isSocketTimeout(exception)) {
						ReflectionUtil.throwException(exception);
					}

					if (_log.isWarnEnabled()) {
						_log.warn(
							StringBundler.concat(
								"Retrying DocuSign call ",
								options.getLocation(), " after attempt ",
								retryCount, " of ", curMaxRetries + 1,
								" timed out"));
					}
				},
				true, maxRetries, 0, () -> _http.URLtoByteArray(options));

		return retryableUnsafeSupplier.get();
	}

	private boolean _isSocketTimeout(Throwable throwable) {
		while (throwable != null) {
			if (throwable instanceof SocketTimeoutException) {
				return true;
			}

			throwable = throwable.getCause();
		}

		return false;
	}

	private static final int _MAX_RETRIES = 2;

	private static final int _MINIMUM_TIMEOUT = 20000;

	private static final Log _log = LogFactoryUtil.getLog(DSHttp.class);

	@Reference
	private Http _http;

	@Reference
	private JSONFactory _jsonFactory;

}