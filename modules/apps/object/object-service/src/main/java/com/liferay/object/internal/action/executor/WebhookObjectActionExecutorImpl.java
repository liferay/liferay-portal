/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.action.executor;

import com.liferay.object.action.executor.ObjectActionExecutor;
import com.liferay.object.constants.ObjectActionExecutorConstants;
import com.liferay.object.exception.ObjectActionParametersException;
import com.liferay.object.model.ObjectAction;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.InetAddressUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.key.secret.SecretResolver;

import java.net.URI;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(service = ObjectActionExecutor.class)
public class WebhookObjectActionExecutorImpl implements ObjectActionExecutor {

	@Override
	public void execute(
			long companyId, long objectActionId,
			UnicodeProperties parametersUnicodeProperties,
			JSONObject payloadJSONObject, long userId)
		throws Exception {

		String url = parametersUnicodeProperties.get("url");

		if (!_isAllowedURL(parametersUnicodeProperties, url)) {
			throw new ObjectActionParametersException(
				"The URL is not allowed for object action " + objectActionId);
		}

		Http.Options options = new Http.Options();

		options.addHeader(
			HttpHeaders.CONTENT_TYPE, ContentTypes.APPLICATION_JSON);
		options.addHeader(
			"x-api-key",
			_secretResolver.resolve(
				companyId,
				StringBundler.concat(
					ObjectAction.class.getSimpleName(), StringPool.SLASH,
					companyId, StringPool.SLASH, objectActionId, "/secret"),
				parametersUnicodeProperties.get("secret")));
		options.setBody(
			payloadJSONObject.toString(), ContentTypes.APPLICATION_JSON,
			StringPool.UTF8);
		options.setFollowRedirects(false);
		options.setLocation(url);
		options.setPost(true);

		_http.URLtoString(options);
	}

	@Override
	public String getKey() {
		return ObjectActionExecutorConstants.KEY_WEBHOOK;
	}

	private boolean _isAllowedURL(
		UnicodeProperties parametersUnicodeProperties, String url) {

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

			if (!GetterUtil.getBoolean(
					parametersUnicodeProperties.get(
						"urlLocalNetworkAccessEnabled"))) {

				return !InetAddressUtil.isLocalInetAddress(
					InetAddressUtil.getInetAddressByName(host));
			}

			String[] urlHostsAllowed = StringUtil.split(
				parametersUnicodeProperties.get("urlHostsAllowed"));

			if (ArrayUtil.isNotEmpty(urlHostsAllowed) &&
				!ArrayUtil.contains(urlHostsAllowed, host, true)) {

				return false;
			}

			return true;
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return false;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		WebhookObjectActionExecutorImpl.class);

	@Reference
	private Http _http;

	@Reference
	private SecretResolver _secretResolver;

}