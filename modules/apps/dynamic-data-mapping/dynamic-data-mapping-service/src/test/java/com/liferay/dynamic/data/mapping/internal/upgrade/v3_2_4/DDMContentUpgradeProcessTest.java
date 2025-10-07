/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.upgrade.v3_2_4;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Carolina Barbosa
 */
public class DDMContentUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		LanguageUtil languageUtil = new LanguageUtil();

		Language language = Mockito.mock(Language.class);

		Mockito.when(
			language.isAvailableLocale(LocaleUtil.BRAZIL)
		).thenReturn(
			true
		);

		languageUtil.setLanguage(language);
	}

	@Test
	public void testUpgradeDDMContentData() throws Exception {
		DDMContentUpgradeProcess ddmContentUpgradeProcess =
			new DDMContentUpgradeProcess(null);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				DDMContentUpgradeProcess.class.getName(),
				LoggerTestUtil.WARN)) {

			JSONArray fieldValuesJSONArray = JSONUtil.putAll(
				JSONUtil.put(
					"name", "numeric"
				).put(
					"value",
					JSONUtil.put(
						"en_US", ""
					).put(
						"pt_BR", "1.1"
					)
				));

			Assert.assertTrue(
				ddmContentUpgradeProcess.upgradeDDMContentData(
					fieldValuesJSONArray,
					JSONUtil.putAll(
						JSONUtil.put(
							"name", "numeric"
						).put(
							"type", "numeric"
						))));

			Assert.assertEquals(
				"",
				JSONUtil.getValue(
					fieldValuesJSONArray.getJSONObject(0), "JSONObject/value",
					"Object/en_US"));
			Assert.assertEquals(
				"1,1",
				JSONUtil.getValue(
					fieldValuesJSONArray.getJSONObject(0), "JSONObject/value",
					"Object/pt_BR"));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 0, logEntries.size());
		}
	}

}