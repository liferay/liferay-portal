/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.internal.manager.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.cookies.configuration.CookiesPreferenceHandlingConfiguration;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.cookies.CookiesManagerUtil;
import com.liferay.portal.kernel.cookies.constants.CookiesConstants;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Tamas Molnar
 * @author Olivér Kecskeméty
 */
@RunWith(Arquillian.class)
public class CookiesManagerImplTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		ConfigurationTestUtil.saveConfiguration(
			CookiesPreferenceHandlingConfiguration.class.getName(),
			HashMapDictionaryBuilder.<String, Object>put(
				"enabled", true
			).build());
	}

	@Test
	public void testCookiePathIsCustomContextWhenUsingCustomContext()
		throws Exception {

		Cookie cookie = new Cookie(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		String customContextPath =
			StringPool.SLASH + RandomTestUtil.randomString();

		MockHttpServletRequest customContextMockHttpServletRequest =
			new MockHttpServletRequest() {

				@Override
				public String getContextPath() {
					return customContextPath;
				}

			};

		CookiesManagerUtil.addCookie(
			CookiesConstants.CONSENT_TYPE_NECESSARY, cookie,
			customContextMockHttpServletRequest, _mockHttpServletResponse);

		Assert.assertEquals(customContextPath, cookie.getPath());
	}

	@Test
	public void testCookiePathIsCustomContextWhenUsingCustomContextWithCustomModuleWebContextPath()
		throws Exception {

		Cookie cookie = new Cookie(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		String customContextPath =
			StringPool.SLASH + RandomTestUtil.randomString();

		MockHttpServletRequest customContextMockHttpServletRequest =
			new MockHttpServletRequest() {

				@Override
				public String getContextPath() {
					return customContextPath;
				}

			};

		HttpServletRequestWrapper httpServletRequestWrapper =
			new HttpServletRequestWrapper(customContextMockHttpServletRequest) {

				@Override
				public String getContextPath() {
					return PortalUtil.getPathModule() + StringPool.SLASH +
						RandomTestUtil.randomString();
				}

			};

		CookiesManagerUtil.addCookie(
			CookiesConstants.CONSENT_TYPE_NECESSARY, cookie,
			httpServletRequestWrapper, _mockHttpServletResponse);

		Assert.assertEquals(customContextPath, cookie.getPath());
	}

	@Test
	public void testCookiePathIsSlashWhenUsingRootContext() throws Exception {
		Cookie cookie = new Cookie(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		CookiesManagerUtil.addCookie(
			CookiesConstants.CONSENT_TYPE_NECESSARY, cookie,
			_mockHttpServletRequest, _mockHttpServletResponse);

		Assert.assertEquals(StringPool.SLASH, cookie.getPath());
	}

	@Test
	public void testCookiePathIsSlashWhenUsingRootContextWithCustomModuleWebContextPath()
		throws Exception {

		Cookie cookie = new Cookie(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		HttpServletRequestWrapper httpServletRequestWrapper =
			new HttpServletRequestWrapper(_mockHttpServletRequest) {

				@Override
				public String getContextPath() {
					return PortalUtil.getPathModule() + StringPool.SLASH +
						RandomTestUtil.randomString();
				}

			};

		CookiesManagerUtil.addCookie(
			CookiesConstants.CONSENT_TYPE_NECESSARY, cookie,
			httpServletRequestWrapper, _mockHttpServletResponse);

		Assert.assertEquals(StringPool.SLASH, cookie.getPath());
	}

	@Test
	public void testCookiesConsent() throws Exception {
		_testCookiesConsentType(CookiesConstants.CONSENT_TYPE_FUNCTIONAL);
		_testCookiesConsentType(CookiesConstants.CONSENT_TYPE_NECESSARY);
		_testCookiesConsentType(CookiesConstants.CONSENT_TYPE_PERFORMANCE);
		_testCookiesConsentType(CookiesConstants.CONSENT_TYPE_PERSONALIZATION);
	}

	@Test
	public void testCookiesWithoutConsentTypeShouldBeDeleted()
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.WARN)) {

			Cookie cookie = new Cookie(
				RandomTestUtil.randomString(), RandomTestUtil.randomString());

			CookiesManagerUtil.addCookie(
				cookie, _mockHttpServletRequest, _mockHttpServletResponse);

			Assert.assertNull(
				CookiesManagerUtil.getCookieValue(
					cookie.getName(), _mockHttpServletRequest));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 2, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"The following cookie is trying to be added without consent " +
					"type: " + cookie.getName(),
				logEntry.getMessage());

			logEntry = logEntries.get(1);

			Assert.assertEquals(
				"The cookie will be deleted. Use the API with explicitly " +
					"declared consent type.",
				logEntry.getMessage());
		}
	}

	@Test
	public void testDeleteRemainingCookieConsentCookiesWhenCookiesPreferenceHandlingIsDisabled()
		throws Exception {

		ConfigurationTestUtil.saveConfiguration(
			CookiesPreferenceHandlingConfiguration.class.getName(),
			HashMapDictionaryBuilder.<String, Object>put(
				"enabled", false
			).build());

		_addConsentCookie(false, CookiesConstants.CONSENT_TYPE_FUNCTIONAL);
		_addConsentCookie(true, CookiesConstants.CONSENT_TYPE_PERSONALIZATION);

		Cookie cookie = new Cookie(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		CookiesManagerUtil.addCookie(
			CookiesConstants.CONSENT_TYPE_PERFORMANCE, cookie,
			_mockHttpServletRequest, _mockHttpServletResponse);

		Assert.assertNotNull(
			CookiesManagerUtil.getCookieValue(
				cookie.getName(), _mockHttpServletRequest));

		Assert.assertNull(
			CookiesManagerUtil.getCookieValue(
				CookiesConstants.NAME_CONSENT_TYPE_FUNCTIONAL,
				_mockHttpServletRequest));
		Assert.assertNull(
			CookiesManagerUtil.getCookieValue(
				CookiesConstants.NAME_CONSENT_TYPE_PERSONALIZATION,
				_mockHttpServletRequest));
	}

	@Test
	public void testExplicitCookieConsentMode() throws Exception {
		ConfigurationTestUtil.saveConfiguration(
			CookiesPreferenceHandlingConfiguration.class.getName(),
			HashMapDictionaryBuilder.<String, Object>put(
				"enabled", true
			).put(
				"explicitConsentMode", true
			).build());

		Cookie cookie = new Cookie(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		CookiesManagerUtil.addCookie(
			CookiesConstants.CONSENT_TYPE_PERFORMANCE, cookie,
			_mockHttpServletRequest, _mockHttpServletResponse);

		Assert.assertNull(
			CookiesManagerUtil.getCookieValue(
				CookiesConstants.NAME_CONSENT_TYPE_PERFORMANCE,
				_mockHttpServletRequest));
		Assert.assertNull(
			CookiesManagerUtil.getCookieValue(
				cookie.getName(), _mockHttpServletRequest));
	}

	@Test
	public void testImplicitCookieConsentMode() throws Exception {
		ConfigurationTestUtil.saveConfiguration(
			CookiesPreferenceHandlingConfiguration.class.getName(),
			HashMapDictionaryBuilder.<String, Object>put(
				"enabled", true
			).put(
				"explicitConsentMode", false
			).build());

		Cookie cookie = new Cookie(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		CookiesManagerUtil.addCookie(
			CookiesConstants.CONSENT_TYPE_PERFORMANCE, cookie,
			_mockHttpServletRequest, _mockHttpServletResponse);

		Assert.assertNull(
			CookiesManagerUtil.getCookieValue(
				CookiesConstants.NAME_CONSENT_TYPE_PERFORMANCE,
				_mockHttpServletRequest));
		Assert.assertNotNull(
			CookiesManagerUtil.getCookieValue(
				cookie.getName(), _mockHttpServletRequest));
	}

	@Test
	public void testInternalCookiesAddedWithoutConsentType() throws Exception {
		_testInternalCookieWithoutConsentType(
			CookiesConstants.NAME_GUEST_LANGUAGE_ID,
			CookiesConstants.CONSENT_TYPE_FUNCTIONAL);
		_testInternalCookieWithoutConsentType(
			CookiesConstants.NAME_CONSENT_TYPE_FUNCTIONAL,
			CookiesConstants.CONSENT_TYPE_NECESSARY);
		_testInternalCookieWithoutConsentType(
			CookiesConstants.NAME_CONSENT_TYPE_NECESSARY,
			CookiesConstants.CONSENT_TYPE_NECESSARY);
		_testInternalCookieWithoutConsentType(
			CookiesConstants.NAME_CONSENT_TYPE_PERFORMANCE,
			CookiesConstants.CONSENT_TYPE_NECESSARY);
		_testInternalCookieWithoutConsentType(
			CookiesConstants.NAME_CONSENT_TYPE_PERSONALIZATION,
			CookiesConstants.CONSENT_TYPE_NECESSARY);
		_testInternalCookieWithoutConsentType(
			CookiesConstants.NAME_COOKIE_SUPPORT,
			CookiesConstants.CONSENT_TYPE_NECESSARY);
		_testInternalCookieWithoutConsentType(
			CookiesConstants.NAME_USER_CONSENT_CONFIGURED,
			CookiesConstants.CONSENT_TYPE_NECESSARY);
	}

	@Test
	public void testKnownCookiesAddedWithPreviouslyKnownConsentType()
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.WARN)) {

			_addConsentCookie(true, CookiesConstants.CONSENT_TYPE_PERFORMANCE);

			Cookie cookie = new Cookie(
				RandomTestUtil.randomString(), RandomTestUtil.randomString());

			CookiesManagerUtil.addCookie(
				CookiesConstants.CONSENT_TYPE_PERFORMANCE, cookie,
				_mockHttpServletRequest, _mockHttpServletResponse);

			Assert.assertEquals(
				cookie.getValue(),
				CookiesManagerUtil.getCookieValue(
					cookie.getName(), _mockHttpServletRequest));

			cookie.setValue(RandomTestUtil.randomString());

			CookiesManagerUtil.addCookie(
				cookie, _mockHttpServletRequest, _mockHttpServletResponse);

			Assert.assertEquals(
				cookie.getValue(),
				CookiesManagerUtil.getCookieValue(
					cookie.getName(), _mockHttpServletRequest));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 2, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"The following cookie is trying to be added without consent " +
					"type: " + cookie.getName(),
				logEntry.getMessage());

			logEntry = logEntries.get(1);

			Assert.assertEquals(
				"The cookie will be added with the consent type used " +
					"previously. Use the API with explicitly declared " +
						"consent type.",
				logEntry.getMessage());
		}
	}

	@Test
	public void testSetDifferentConsentTypeToAlreadyKnownCookie()
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.WARN)) {

			_addConsentCookie(true, CookiesConstants.CONSENT_TYPE_FUNCTIONAL);

			Cookie cookie = new Cookie(
				RandomTestUtil.randomString(), RandomTestUtil.randomString());

			CookiesManagerUtil.addCookie(
				CookiesConstants.CONSENT_TYPE_NECESSARY, cookie,
				_mockHttpServletRequest, _mockHttpServletResponse);

			Assert.assertNotNull(
				CookiesManagerUtil.getCookieValue(
					cookie.getName(), _mockHttpServletRequest));

			CookiesManagerUtil.addCookie(
				CookiesConstants.CONSENT_TYPE_FUNCTIONAL, cookie,
				_mockHttpServletRequest, _mockHttpServletResponse);

			Assert.assertNotNull(
				CookiesManagerUtil.getCookieValue(
					cookie.getName(), _mockHttpServletRequest));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				StringBundler.concat(
					"The ", cookie.getName(),
					" cookie was previously added with consent type ",
					CookiesConstants.CONSENT_TYPE_NECESSARY,
					" and will now be modified to consent type ",
					CookiesConstants.CONSENT_TYPE_FUNCTIONAL),
				logEntry.getMessage());
		}
	}

	@Test
	public void testSkipConsentTypeCheckWhenCookiesPreferenceHandlingIsDisabled()
		throws Exception {

		ConfigurationTestUtil.saveConfiguration(
			CookiesPreferenceHandlingConfiguration.class.getName(),
			HashMapDictionaryBuilder.<String, Object>put(
				"enabled", false
			).put(
				"explicitConsentMode", true
			).build());

		Cookie cookie = new Cookie(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		CookiesManagerUtil.addCookie(
			CookiesConstants.CONSENT_TYPE_PERSONALIZATION, cookie,
			_mockHttpServletRequest, _mockHttpServletResponse);

		Assert.assertNull(
			CookiesManagerUtil.getCookieValue(
				CookiesConstants.NAME_CONSENT_TYPE_PERSONALIZATION,
				_mockHttpServletRequest));
		Assert.assertNotNull(
			CookiesManagerUtil.getCookieValue(
				cookie.getName(), _mockHttpServletRequest));
	}

	private void _addConsentCookie(boolean accepted, int consentType) {
		if (consentType == CookiesConstants.CONSENT_TYPE_NECESSARY) {
			return;
		}

		String cookieName = StringPool.BLANK;

		if (consentType == CookiesConstants.CONSENT_TYPE_FUNCTIONAL) {
			cookieName = CookiesConstants.NAME_CONSENT_TYPE_FUNCTIONAL;
		}
		else if (consentType == CookiesConstants.CONSENT_TYPE_PERFORMANCE) {
			cookieName = CookiesConstants.NAME_CONSENT_TYPE_PERFORMANCE;
		}
		else if (consentType == CookiesConstants.CONSENT_TYPE_PERSONALIZATION) {
			cookieName = CookiesConstants.NAME_CONSENT_TYPE_PERSONALIZATION;
		}

		Cookie consentCookie = new Cookie(cookieName, String.valueOf(accepted));

		CookiesManagerUtil.addCookie(
			CookiesConstants.CONSENT_TYPE_NECESSARY, consentCookie,
			_mockHttpServletRequest, _mockHttpServletResponse);

		Assert.assertEquals(
			String.valueOf(accepted),
			CookiesManagerUtil.getCookieValue(
				consentCookie.getName(), _mockHttpServletRequest));
	}

	private void _testCookiesConsentType(int consentType) {
		_addConsentCookie(false, consentType);

		Cookie cookie = new Cookie(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		CookiesManagerUtil.addCookie(
			consentType, cookie, _mockHttpServletRequest,
			_mockHttpServletResponse);

		if (consentType == CookiesConstants.CONSENT_TYPE_NECESSARY) {
			Assert.assertNotNull(
				CookiesManagerUtil.getCookieValue(
					cookie.getName(), _mockHttpServletRequest));
		}
		else {
			Assert.assertNull(
				CookiesManagerUtil.getCookieValue(
					cookie.getName(), _mockHttpServletRequest));
		}

		_addConsentCookie(true, consentType);

		CookiesManagerUtil.addCookie(
			consentType, cookie, _mockHttpServletRequest,
			_mockHttpServletResponse);

		Assert.assertNotNull(
			CookiesManagerUtil.getCookieValue(
				cookie.getName(), _mockHttpServletRequest));
	}

	private void _testInternalCookieWithoutConsentType(
		String name, int consentType) {

		_addConsentCookie(false, consentType);

		Cookie cookie = new Cookie(name, RandomTestUtil.randomString());

		CookiesManagerUtil.addCookie(
			cookie, _mockHttpServletRequest, _mockHttpServletResponse);

		if (consentType == CookiesConstants.CONSENT_TYPE_NECESSARY) {
			Assert.assertNotNull(
				CookiesManagerUtil.getCookieValue(
					cookie.getName(), _mockHttpServletRequest));
		}
		else {
			Assert.assertNull(
				CookiesManagerUtil.getCookieValue(
					cookie.getName(), _mockHttpServletRequest));
		}

		_addConsentCookie(true, consentType);

		CookiesManagerUtil.addCookie(
			cookie, _mockHttpServletRequest, _mockHttpServletResponse);

		Assert.assertNotNull(
			CookiesManagerUtil.getCookieValue(
				cookie.getName(), _mockHttpServletRequest));
	}

	private static final String _CLASS_NAME =
		"com.liferay.cookies.internal.manager.CookiesManagerImpl";

	private final MockHttpServletRequest _mockHttpServletRequest =
		new MockHttpServletRequest();
	private final MockHttpServletResponse _mockHttpServletResponse =
		new MockHttpServletResponse();

}