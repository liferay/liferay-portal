/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.opensso.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.cookies.CookiesManagerUtil;
import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.security.sso.OpenSSO;
import com.liferay.portal.kernel.settings.CompanyServiceSettingsLocator;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.sso.opensso.configuration.OpenSSOConfiguration;
import com.liferay.portal.security.sso.opensso.constants.OpenSSOConfigurationKeys;
import com.liferay.portal.security.sso.opensso.constants.OpenSSOConstants;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 * @author Marta Medio
 */
@Component(service = OpenSSO.class)
public class OpenSSOImpl implements OpenSSO {

	@Override
	public Map<String, String> getAttributes(
		HttpServletRequest httpServletRequest, String serviceURL) {

		Map<String, String> nameValues = new HashMap<>();

		String url = serviceURL.concat(_GET_ATTRIBUTES);

		try {
			URL urlObj = new URL(url);

			HttpURLConnection httpURLConnection =
				(HttpURLConnection)urlObj.openConnection();

			httpURLConnection.setDoOutput(true);
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setRequestProperty(
				"Content-type", "application/x-www-form-urlencoded");

			setCookieProperty(
				httpServletRequest, httpURLConnection,
				getCookieNames(serviceURL));

			OutputStreamWriter osw = new OutputStreamWriter(
				httpURLConnection.getOutputStream());

			osw.write("dummy");

			osw.flush();

			int responseCode = httpURLConnection.getResponseCode();

			if (responseCode == HttpURLConnection.HTTP_OK) {
				InputStream inputStream =
					(InputStream)httpURLConnection.getContent();

				UnsyncBufferedReader unsyncBufferedReader =
					new UnsyncBufferedReader(
						new InputStreamReader(inputStream));

				String line = null;

				while ((line = unsyncBufferedReader.readLine()) != null) {
					if (line.startsWith("userdetails.attribute.name=")) {
						String name = line.replaceFirst(
							"userdetails.attribute.name=", "");

						line = unsyncBufferedReader.readLine();

						if (line.startsWith("userdetails.attribute.value=")) {
							String value = line.replaceFirst(
								"userdetails.attribute.value=", "");

							nameValues.put(name, value);
						}
					}
				}
			}
			else if (_log.isDebugEnabled()) {
				_log.debug("Attributes response code " + responseCode);
			}
		}
		catch (MalformedURLException malformedURLException) {
			_log.error(malformedURLException);

			if (_log.isDebugEnabled()) {
				_log.debug(malformedURLException);
			}
		}
		catch (IOException ioException) {
			_log.error(ioException);

			if (_log.isDebugEnabled()) {
				_log.debug(ioException);
			}
		}

		return nameValues;
	}

	public String[] getCookieNames(String serviceURL) {
		String[] cookieNames = _cookieNamesMap.get(serviceURL);

		if (cookieNames != null) {
			return cookieNames;
		}

		List<String> cookieNamesList = new ArrayList<>();

		try {
			String cookieName = null;

			String url = serviceURL.concat(_GET_COOKIE_NAME);

			URL urlObj = new URL(url);

			HttpURLConnection httpURLConnection =
				(HttpURLConnection)urlObj.openConnection();

			InputStream inputStream =
				(InputStream)httpURLConnection.getContent();

			UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new InputStreamReader(inputStream));

			int responseCode = httpURLConnection.getResponseCode();

			if (responseCode != HttpURLConnection.HTTP_OK) {
				if (_log.isDebugEnabled()) {
					_log.debug(url + " has response code " + responseCode);
				}
			}
			else {
				String line = null;

				while ((line = unsyncBufferedReader.readLine()) != null) {
					if (line.startsWith("string=")) {
						line = line.replaceFirst("string=", "");

						cookieName = line;
					}
				}
			}

			url = serviceURL.concat(_GET_COOKIE_NAMES);

			urlObj = new URL(url);

			httpURLConnection = (HttpURLConnection)urlObj.openConnection();

			if (httpURLConnection.getResponseCode() !=
					HttpURLConnection.HTTP_OK) {

				if (_log.isDebugEnabled()) {
					_log.debug(url + " has response code " + responseCode);
				}
			}
			else {
				inputStream = (InputStream)httpURLConnection.getContent();

				unsyncBufferedReader = new UnsyncBufferedReader(
					new InputStreamReader(inputStream));

				String line = null;

				while ((line = unsyncBufferedReader.readLine()) != null) {
					if (line.startsWith("string=")) {
						line = line.replaceFirst("string=", "");

						if (cookieName.equals(line)) {
							cookieNamesList.add(0, cookieName);
						}
						else {
							cookieNamesList.add(line);
						}
					}
				}
			}
		}
		catch (IOException ioException) {
			if (_log.isWarnEnabled()) {
				_log.warn(ioException);
			}
		}

		cookieNames = cookieNamesList.toArray(new String[0]);

		if (cookieNames.length > 0) {
			_cookieNamesMap.put(serviceURL, cookieNames);
		}

		return cookieNames;
	}

	@Override
	public String getSubjectId(
		HttpServletRequest httpServletRequest, String serviceURL) {

		String cookieName = getCookieNames(serviceURL)[0];

		return CookiesManagerUtil.getCookieValue(
			cookieName, httpServletRequest);
	}

	@Override
	public boolean isAuthenticated(
			HttpServletRequest httpServletRequest, String serviceURL)
		throws IOException {

		String[] cookieNames = getCookieNames(serviceURL);

		if (!_hasCookieNames(httpServletRequest, cookieNames)) {
			return false;
		}

		String version = OpenSSOConfigurationKeys.VERSION_OPENAM_12;

		try {
			OpenSSOConfiguration openSSOConfiguration =
				_configurationProvider.getConfiguration(
					OpenSSOConfiguration.class,
					new CompanyServiceSettingsLocator(
						_portal.getCompanyId(httpServletRequest),
						OpenSSOConstants.SERVICE_NAME));

			version = openSSOConfiguration.version();
		}
		catch (ConfigurationException configurationException) {
			if (_log.isWarnEnabled()) {
				_log.warn(configurationException);
			}
		}

		if (version.equals(OpenSSOConfigurationKeys.VERSION_OPENAM_13)) {
			String subjectId = getSubjectId(httpServletRequest, serviceURL);

			if (subjectId != null) {
				String url = serviceURL.concat(
					StringUtil.replace(
						_VALIDATE_TOKEN_VERSION_13, "{#subjectId}",
						URLCodec.encodeURL(subjectId)));

				String json = _http.URLtoString(url, true);

				try {
					JSONObject jsonObject = _jsonFactory.createJSONObject(json);

					String realm = jsonObject.getString("realm");
					String uid = jsonObject.getString("uid");
					boolean valid = jsonObject.getBoolean("valid");

					if ((realm != null) && (uid != null) && valid) {
						return true;
					}
					else if (_log.isDebugEnabled()) {
						_log.debug("Invalid authentication: " + json);
					}
				}
				catch (JSONException jsonException) {
					throw new IOException(jsonException);
				}
			}
		}
		else {
			String url = serviceURL.concat(_VALIDATE_TOKEN_VERSION_12);

			URL urlObj = new URL(url);

			HttpURLConnection httpURLConnection =
				(HttpURLConnection)urlObj.openConnection();

			httpURLConnection.setDoOutput(true);
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setRequestProperty(
				"Content-type", "application/x-www-form-urlencoded");

			setCookieProperty(
				httpServletRequest, httpURLConnection, cookieNames);

			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
				httpURLConnection.getOutputStream());

			outputStreamWriter.write("dummy");

			outputStreamWriter.flush();

			int responseCode = httpURLConnection.getResponseCode();

			if (responseCode == HttpURLConnection.HTTP_OK) {
				String data = StringUtil.toLowerCase(
					StringUtil.read(httpURLConnection.getInputStream()));

				if (data.contains("boolean=true")) {
					return true;
				}
			}
			else if (_log.isDebugEnabled()) {
				_log.debug("Authentication response code " + responseCode);
			}
		}

		return false;
	}

	@Override
	public boolean isValidServiceUrl(String serviceURL) {
		if (Validator.isNull(serviceURL)) {
			return false;
		}

		String[] cookieNames = getCookieNames(serviceURL);

		if (cookieNames.length == 0) {
			return false;
		}

		return true;
	}

	@Override
	public boolean isValidUrl(String url) {
		if (Validator.isNull(url)) {
			return false;
		}

		try {
			URL urlObj = new URL(url);

			HttpURLConnection httpURLConnection =
				(HttpURLConnection)urlObj.openConnection();

			int responseCode = httpURLConnection.getResponseCode();

			if (!((responseCode == HttpURLConnection.HTTP_OK) ||
				  ((responseCode >= HttpURLConnection.HTTP_MULT_CHOICE) &&
				   (responseCode <= HttpURLConnection.HTTP_NOT_MODIFIED)))) {

				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"URL ", url, " is invalid with response code ",
							responseCode));
				}

				return false;
			}

			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"URL ", url, " is valid with response code ",
						responseCode));
			}
		}
		catch (IOException ioException) {
			if (_log.isWarnEnabled()) {
				_log.warn(ioException);
			}

			return false;
		}

		return true;
	}

	@Override
	public boolean isValidUrls(String[] urls) {
		for (String url : urls) {
			if (!isValidUrl(url)) {
				return false;
			}
		}

		return true;
	}

	public void setCookieProperty(
		HttpServletRequest httpServletRequest, HttpURLConnection urlc,
		String[] cookieNames) {

		if (cookieNames.length == 0) {
			return;
		}

		StringBundler sb = new StringBundler(cookieNames.length * 6);

		for (String cookieName : cookieNames) {
			String cookieValue = CookiesManagerUtil.getCookieValue(
				cookieName, httpServletRequest);

			sb.append(cookieName);
			sb.append(StringPool.EQUAL);
			sb.append(StringPool.QUOTE);
			sb.append(cookieValue);
			sb.append(StringPool.QUOTE);
			sb.append(StringPool.SEMICOLON);
		}

		urlc.setRequestProperty("Cookie", sb.toString());
	}

	private boolean _hasCookieNames(
		HttpServletRequest httpServletRequest, String[] cookieNames) {

		for (String cookieName : cookieNames) {
			String cookieValue = CookiesManagerUtil.getCookieValue(
				cookieName, httpServletRequest);

			if (cookieValue != null) {
				return true;
			}
		}

		if (_log.isInfoEnabled()) {
			_log.info("No OpenSSO cookies: " + StringUtil.merge(cookieNames));
		}

		return false;
	}

	private static final String _GET_ATTRIBUTES = "/identity/attributes";

	private static final String _GET_COOKIE_NAME =
		"/identity/getCookieNameForToken";

	private static final String _GET_COOKIE_NAMES =
		"/identity/getCookieNamesToForward";

	private static final String _VALIDATE_TOKEN_VERSION_12 =
		"/identity/isTokenValid";

	private static final String _VALIDATE_TOKEN_VERSION_13 =
		"/json/sessions/{#subjectId}?_action=validate";

	private static final Log _log = LogFactoryUtil.getLog(OpenSSOImpl.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	private final Map<String, String[]> _cookieNamesMap =
		new ConcurrentHashMap<>();

	@Reference
	private Http _http;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

}