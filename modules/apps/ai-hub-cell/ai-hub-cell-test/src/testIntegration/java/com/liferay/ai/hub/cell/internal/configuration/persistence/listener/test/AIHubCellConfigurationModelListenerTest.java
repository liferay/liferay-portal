/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.cell.internal.configuration.persistence.listener.test;

import com.liferay.ai.hub.cell.configuration.AIHubCellConfiguration;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
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

import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Joao Victor
 */
@RunWith(Arquillian.class)
public class AIHubCellConfigurationModelListenerTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		_locale = LocaleThreadLocal.getThemeDisplayLocale();

		LocaleThreadLocal.setThemeDisplayLocale(LocaleUtil.US);
	}

	@After
	public void tearDown() {
		LocaleThreadLocal.setThemeDisplayLocale(_locale);
	}

	@Test
	public void testOnBeforeSave() throws Exception {
		AssertUtils.assertFailure(
			ConfigurationModelListenerException.class,
			StringBundler.concat(
				"The listener ",
				_configurationModelListener.getClass(
				).getName(),
				" was unable to save configuration ",
				AIHubCellConfiguration.class.getName(), ": ",
				_language.get(
					LocaleUtil.US,
					"please-enter-a-service-url-that-does-not-end-with-a-" +
						"slash")),
			() -> _configurationModelListener.onBeforeSave(
				RandomTestUtil.randomString(),
				HashMapDictionaryBuilder.<String, Object>put(
					"serviceURL", "https://example.com/"
				).build()));
	}

	@Inject(
		filter = "model.class.name=com.liferay.ai.hub.cell.configuration.AIHubCellConfiguration"
	)
	private ConfigurationModelListener _configurationModelListener;

	@Inject
	private Language _language;

	private Locale _locale;

}