/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.configuration.persistence.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Dictionary;
import java.util.Locale;

import org.junit.After;
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
		_assertInvalidCacheControl("Public");
		_assertInvalidCacheControl("no-store");
		_assertInvalidCacheControl("public, immutable");
	}

	@Test
	public void testOnBeforeSaveWithInvalidMaxAge() throws Exception {
		_assertInvalidMaxAge(-1);
		_assertInvalidMaxAge(86401);
	}

	@Test
	public void testOnBeforeSaveWithInvalidPath() throws Exception {
		_assertInvalidPath(
			"headless-api-cacheable-endpoint-path-required", StringPool.BLANK);
	}

	@Test
	public void testOnBeforeSaveWithPathContainingEmptySegment()
		throws Exception {

		_assertInvalidPath(
			"headless-api-cacheable-endpoint-path-segment-must-be-a-literal-" +
				"or-an-asterisk",
			"/captcha//v1.0/captcha/challenge");
	}

	@Test
	public void testOnBeforeSaveWithPathContainingFragment() throws Exception {
		_assertInvalidPath(
			"headless-api-cacheable-endpoint-path-must-not-contain-a-query-" +
				"string-or-a-fragment",
			"/captcha/v1.0/captcha/challenge#fragment");
	}

	@Test
	public void testOnBeforeSaveWithPathContainingPartialWildcard()
		throws Exception {

		_assertInvalidPath(
			"headless-api-cacheable-endpoint-path-segment-must-be-a-literal-" +
				"or-an-asterisk",
			"/captcha/v1.0/**/challenge");
		_assertInvalidPath(
			"headless-api-cacheable-endpoint-path-segment-must-be-a-literal-" +
				"or-an-asterisk",
			"/captcha/v1.0/captcha/chal*");
	}

	@Test
	public void testOnBeforeSaveWithPathContainingQueryString()
		throws Exception {

		_assertInvalidPath(
			"headless-api-cacheable-endpoint-path-must-not-contain-a-query-" +
				"string-or-a-fragment",
			"/captcha/v1.0/captcha/challenge?pageSize=1");
	}

	@Test
	public void testOnBeforeSaveWithPathContainingWildcards() throws Exception {
		_configurationModelListener.onBeforeSave(
			StringPool.BLANK, _createDictionary("public", 0));
		_configurationModelListener.onBeforeSave(
			StringPool.BLANK,
			_createDictionary(
				"public", 0, "/headless-delivery/v1.0/blog-postings/*/"));
		_configurationModelListener.onBeforeSave(
			StringPool.BLANK,
			_createDictionary(
				"public", 0, "/headless-delivery/v1.0/sites/*/blog-postings"));
	}

	@Test
	public void testOnBeforeSaveWithPathMissingLeadingSlash() throws Exception {
		_assertInvalidPath(
			"headless-api-cacheable-endpoint-path-must-start-with-a-slash",
			"captcha/v1.0/captcha/challenge");
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

	private void _assertFailure(
		Dictionary<String, Object> dictionary, String key) {

		AssertUtils.assertFailure(
			ConfigurationModelListenerException.class,
			StringBundler.concat(
				"The listener com.liferay.portal.vulcan.internal.",
				"configuration.persistence.listener.",
				"HeadlessAPICacheCompanyConfigurationModelListener was unable ",
				"to save configuration com.liferay.portal.vulcan.internal.",
				"configuration.HeadlessAPICacheCompanyConfiguration: ",
				_language.get(LocaleUtil.US, key)),
			() -> _configurationModelListener.onBeforeSave(
				StringPool.BLANK, dictionary));
	}

	private void _assertInvalidCacheControl(String cacheControl) {
		_assertFailure(
			_createDictionary(cacheControl, 0),
			"cache-control-must-be-public-or-private");
	}

	private void _assertInvalidMaxAge(int maxAge) {
		_assertFailure(
			_createDictionary("public", maxAge),
			"headless-api-cache-max-age-out-of-range");
	}

	private void _assertInvalidPath(String key, String path) {
		_assertFailure(_createDictionary("public", 0, path), key);
	}

	private Dictionary<String, Object> _createDictionary(
		String cacheControl, int maxAge) {

		return _createDictionary(
			cacheControl, maxAge,
			StringPool.SLASH + RandomTestUtil.randomString());
	}

	private Dictionary<String, Object> _createDictionary(
		String cacheControl, int maxAge, String path) {

		return HashMapDictionaryBuilder.<String, Object>put(
			"cacheControl", cacheControl
		).put(
			"maxAge", maxAge
		).put(
			"path", path
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