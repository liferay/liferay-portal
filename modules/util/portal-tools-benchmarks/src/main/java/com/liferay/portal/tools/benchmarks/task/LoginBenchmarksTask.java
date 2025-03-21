/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.benchmarks.task;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.tools.benchmarks.http.HttpResponse;
import com.liferay.portal.tools.benchmarks.http.HttpUtil;
import com.liferay.portal.tools.benchmarks.http.ThreadLocalCookieStore;

import java.net.URL;

import java.util.List;

import org.junit.Assert;

/**
 * @author Tina Tian
 */
public class LoginBenchmarksTask implements BenchmarksTask {

	public LoginBenchmarksTask(
		String emailAddress, String hostname, String password, int port) {

		_emailAddress = emailAddress;
		_hostname = hostname;
		_password = password;
		_port = port;
	}

	@Override
	public List<ObjectValuePair<String, Long>> execute() throws Exception {
		try (SafeCloseable safeCloseable =
				ThreadLocalCookieStore.withSafeCloseable()) {

			HttpResponse httpResponse = HttpUtil.doGet(
				null, _createURL(StringPool.FORWARD_SLASH));

			_assertContent(httpResponse, "Liferay.currentURL");

			return ListUtil.fromArray(
				new ObjectValuePair<>(
					"viewHomePage", httpResponse.getDuration()),
				new ObjectValuePair<>(
					"viewLoginPage",
					_viewLoginPage(httpResponse.getCSRFToken())),
				new ObjectValuePair<>(
					"login",
					_login(
						httpResponse.getCSRFToken(), _emailAddress, _password)),
				new ObjectValuePair<>("logout", _logout()));
		}
	}

	private void _assertContent(HttpResponse httpResponse, String key) {
		Assert.assertEquals(200, httpResponse.getStatusCode());

		String httpResponseString = httpResponse.toString();

		Assert.assertTrue(httpResponseString.contains(key));
	}

	private URL _assertRedirect(HttpResponse httpResponse, String redirect)
		throws Exception {

		return _assertRedirect(httpResponse, _createURL(redirect));
	}

	private URL _assertRedirect(HttpResponse httpResponse, URL url)
		throws Exception {

		Assert.assertEquals(url.toString(), httpResponse.getRedirect());
		Assert.assertEquals(302, httpResponse.getStatusCode());

		return url;
	}

	private URL _createURL(String... strings) throws Exception {
		return new URL("http", _hostname, _port, StringBundler.concat(strings));
	}

	private long _getDuration(HttpResponse... httpResponses) {
		long duration = 0;

		for (HttpResponse httpResponse : httpResponses) {
			duration += httpResponse.getDuration();
		}

		return duration;
	}

	private long _login(String csrfToken, String emailAddress, String password)
		throws Exception {

		HttpResponse httpResponse1 = HttpUtil.doPost(
			null, csrfToken,
			new String[][] {
				{_P_P_ID_NAMESPACE + "_checkboxNames", "rememberMe"},
				{_P_P_ID_NAMESPACE + "_doActionAfterLogin", StringPool.FALSE},
				{
					_P_P_ID_NAMESPACE + "_formDate",
					String.valueOf(System.currentTimeMillis())
				},
				{_P_P_ID_NAMESPACE + "_login", emailAddress},
				{_P_P_ID_NAMESPACE + "_password", password},
				{_P_P_ID_NAMESPACE + "_redirect", StringPool.BLANK},
				{_P_P_ID_NAMESPACE + "_saveLastPath", StringPool.FALSE}
			},
			_createURL(
				"/home?", _P_P_ID_NAMESPACE,
				"_jakarta.portlet.action=/login/login&", _P_P_ID_NAMESPACE,
				"_mvcRenderCommandName=/login/login&p_p_id=", _P_P_ID,
				"&p_p_lifecycle=1&p_p_mode=view&p_p_state=normal"));

		_assertRedirect(httpResponse1, "/c");

		HttpResponse httpResponse2 = HttpUtil.doGet(
			csrfToken, _createURL("/c"));

		_assertRedirect(httpResponse2, StringPool.SLASH);

		HttpResponse httpResponse3 = HttpUtil.doGet(
			csrfToken, _createURL(StringPool.SLASH));

		_assertContent(
			httpResponse3, "ProductNavigationUserPersonalBarPortlet");

		return _getDuration(httpResponse1, httpResponse2, httpResponse3);
	}

	private long _logout() throws Exception {
		HttpResponse httpResponse = HttpUtil.doGet(
			null, _createURL("/c/portal/logout"));

		return httpResponse.getDuration();
	}

	private long _viewLoginPage(String csrfToken) throws Exception {
		HttpResponse httpResponse1 = HttpUtil.doGet(
			csrfToken, _createURL("/c/portal/login?windowState=exclusive"));

		URL url = _assertRedirect(
			httpResponse1,
			StringBundler.concat(
				"/home?p_p_id=", _P_P_ID,
				"&p_p_lifecycle=0&p_p_state=exclusive&p_p_mode=view&",
				_P_P_ID_NAMESPACE,
				"_mvcRenderCommandName=/login/login&saveLastPath=false"));

		HttpResponse httpResponse2 = HttpUtil.doGet(csrfToken, url);

		_assertContent(httpResponse2, "Remember Me");

		return _getDuration(httpResponse1, httpResponse2);
	}

	private static final String _P_P_ID =
		"com_liferay_login_web_portlet_LoginPortlet";

	private static final String _P_P_ID_NAMESPACE =
		StringPool.UNDERLINE + _P_P_ID;

	private final String _emailAddress;
	private final String _hostname;
	private final String _password;
	private final int _port;

}