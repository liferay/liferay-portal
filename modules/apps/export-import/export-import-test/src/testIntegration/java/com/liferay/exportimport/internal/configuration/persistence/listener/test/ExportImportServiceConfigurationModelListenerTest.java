/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.configuration.persistence.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Locale;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jaime Leon
 */
@RunWith(Arquillian.class)
public class ExportImportServiceConfigurationModelListenerTest {

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
	@TestInfo("LPS-184978")
	public void testOnBeforeSave() throws Exception {
		_testOnBeforeSaveFailure(
			"please-enter-a-relative-url-that-begins-with-a-slash", "dl/*");
		_testOnBeforeSaveFailure(
			"please-enter-a-relative-url-that-does-not-end-with-a-slash",
			"/dl/");
		_testOnBeforeSaveFailure(
			"please-enter-a-relative-url-that-does-not-have-adjacent-slashes",
			"/dl//entries");
		_testOnBeforeSaveFailure(
			"please-enter-a-relative-url-with-valid-characters",
			"/dl/entries?id=1");

		_configurationModelListener.onBeforeSave(
			StringPool.BLANK,
			HashMapDictionaryBuilder.<String, Object>put(
				"validateLayoutReferencesWhitelistedURLPatterns",
				new String[] {
					StringPool.BLANK, StringPool.STAR, "/dl/*",
					StringPool.SLASH + RandomTestUtil.randomString()
				}
			).build());
	}

	private void _testOnBeforeSaveFailure(
		String messageKey, String whitelistedURLPattern) {

		ConfigurationModelListenerException
			configurationModelListenerException = Assert.assertThrows(
				ConfigurationModelListenerException.class,
				() -> _configurationModelListener.onBeforeSave(
					StringPool.BLANK,
					HashMapDictionaryBuilder.<String, Object>put(
						"validateLayoutReferencesWhitelistedURLPatterns",
						new String[] {whitelistedURLPattern}
					).build()));

		String message = configurationModelListenerException.getMessage();

		Assert.assertTrue(
			message.contains(_language.get(LocaleUtil.US, messageKey)));
	}

	@Inject(
		filter = "model.class.name=com.liferay.exportimport.configuration.ExportImportServiceConfiguration"
	)
	private ConfigurationModelListener _configurationModelListener;

	@Inject
	private Language _language;

	private Locale _locale;

}