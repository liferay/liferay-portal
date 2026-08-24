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
			StringPool.BLANK, _createDictionary("private"));
		_configurationModelListener.onBeforeSave(
			StringPool.BLANK, _createDictionary("public"));
	}

	@Test
	public void testOnBeforeSaveWithInvalidCacheControl() throws Exception {
		_assertInvalidCacheControl(StringPool.BLANK);
		_assertInvalidCacheControl("no-store");
		_assertInvalidCacheControl("public, immutable");
		_assertInvalidCacheControl("Public");
	}

	@Test
	public void testOnBeforeSaveWithoutCacheControl() throws Exception {
		_configurationModelListener.onBeforeSave(
			StringPool.BLANK,
			HashMapDictionaryBuilder.<String, Object>put(
				"path", "/test-vulcan-cache/test"
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
				StringPool.BLANK, _createDictionary(cacheControl));

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

	private Dictionary<String, Object> _createDictionary(String cacheControl) {
		return HashMapDictionaryBuilder.<String, Object>put(
			"cacheControl", cacheControl
		).put(
			"path", "/test-vulcan-cache/test"
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