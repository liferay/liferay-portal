/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.configuration.persistence.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Dictionary;
import java.util.Locale;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alejandro Tardín
 */
@RunWith(Arquillian.class)
public class HeadlessAPICacheCompanyConfigurationModelListenerTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		_locale = LocaleThreadLocal.getThemeDisplayLocale();

		LocaleThreadLocal.setThemeDisplayLocale(LocaleUtil.ENGLISH);
	}

	@After
	public void tearDown() {
		LocaleThreadLocal.setThemeDisplayLocale(_locale);
	}

	@Test
	public void testOnBeforeSave() throws Exception {
		_configurationModelListener.onBeforeSave(
			StringPool.BLANK, _createDictionary("private", 0));
		_configurationModelListener.onBeforeSave(
			StringPool.BLANK, _createDictionary("public", 0));
	}

	@Test
	public void testOnBeforeSaveWithInvalidCacheControl() throws Exception {
		_assertInvalidCacheControl(StringPool.BLANK);
		_assertInvalidCacheControl("no-store");
		_assertInvalidCacheControl("public, immutable");
		_assertInvalidCacheControl("Public");
	}

	@Test
	public void testOnBeforeSaveWithInvalidMaxAge() throws Exception {
		_assertInvalidMaxAge(-1);
		_assertInvalidMaxAge(86401);
	}

	@Test
	public void testOnBeforeSaveWithInvalidPath() throws Exception {
		_assertInvalidPath(StringPool.BLANK);
	}

	@Test
	public void testOnBeforeSaveWithPathMissingLeadingSlash() throws Exception {
		try {
			_configurationModelListener.onBeforeSave(
				StringPool.BLANK,
				HashMapDictionaryBuilder.<String, Object>put(
					"cacheControl", "public"
				).put(
					"maxAge", 0
				).put(
					"path", "captcha/v1.0/captcha/challenge"
				).build());

			Assert.fail();
		}
		catch (ConfigurationModelListenerException
					configurationModelListenerException) {

			String message = configurationModelListenerException.getMessage();

			Assert.assertTrue(
				message,
				message.contains(
					_language.get(
						LocaleUtil.US,
						"headless-api-cacheable-endpoint-path-must-start-" +
							"with-a-slash")));
		}
	}

	@Test
	public void testOnBeforeSaveWithoutCacheControl() throws Exception {
		_configurationModelListener.onBeforeSave(
			StringPool.BLANK,
			HashMapDictionaryBuilder.<String, Object>put(
				"path", StringPool.SLASH + RandomTestUtil.randomString()
			).build());
	}

	@Test
	public void testOnBeforeSaveWithoutThemeDisplayLocale() throws Exception {
		LocaleThreadLocal.setThemeDisplayLocale(null);

		_assertInvalidCacheControl("no-store");
	}

	private void _assertInvalidCacheControl(String cacheControl)
		throws Exception {

		try {
			_configurationModelListener.onBeforeSave(
				StringPool.BLANK, _createDictionary(cacheControl, 0));

			Assert.fail();
		}
		catch (ConfigurationModelListenerException
					configurationModelListenerException) {

			String message = configurationModelListenerException.getMessage();

			Assert.assertTrue(
				message,
				message.contains(
					_language.get(
						LocaleUtil.US,
						"cache-control-must-be-public-or-private")));
		}
	}

	private void _assertInvalidMaxAge(int maxAge) throws Exception {
		try {
			_configurationModelListener.onBeforeSave(
				StringPool.BLANK, _createDictionary("public", maxAge));

			Assert.fail();
		}
		catch (ConfigurationModelListenerException
					configurationModelListenerException) {

			String message = configurationModelListenerException.getMessage();

			Assert.assertTrue(
				message,
				message.contains(
					_language.get(
						LocaleUtil.US,
						"headless-api-cache-max-age-out-of-range")));
		}
	}

	private void _assertInvalidPath(String path) throws Exception {
		try {
			_configurationModelListener.onBeforeSave(
				StringPool.BLANK,
				HashMapDictionaryBuilder.<String, Object>put(
					"cacheControl", "public"
				).put(
					"maxAge", 0
				).put(
					"path", path
				).build());

			Assert.fail();
		}
		catch (ConfigurationModelListenerException
					configurationModelListenerException) {

			String message = configurationModelListenerException.getMessage();

			Assert.assertTrue(
				message,
				message.contains(
					_language.get(
						LocaleUtil.US,
						"headless-api-cacheable-endpoint-path-required")));
		}
	}

	private Dictionary<String, Object> _createDictionary(
		String cacheControl, int maxAge) {

		return HashMapDictionaryBuilder.<String, Object>put(
			"cacheControl", cacheControl
		).put(
			"maxAge", maxAge
		).put(
			"path", StringPool.SLASH + RandomTestUtil.randomString()
		).build();
	}

	@Inject(
		filter = "model.class.name=com.liferay.portal.vulcan.internal.configuration.HeadlessAPICacheCompanyConfiguration"
	)
	private ConfigurationModelListener _configurationModelListener;

	@Inject
	private Language _language;

	private Locale _locale;

}