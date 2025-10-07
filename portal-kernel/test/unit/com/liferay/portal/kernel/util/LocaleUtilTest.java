/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.ExpectedLog;
import com.liferay.portal.test.rule.ExpectedLogs;
import com.liferay.portal.test.rule.ExpectedType;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Wesley Gong
 */
public class LocaleUtilTest {

	@Test
	public void testFromBCP47LangTag() {
		LanguageUtil languageUtil = new LanguageUtil();

		Language language = Mockito.mock(Language.class);

		languageUtil.setLanguage(language);

		Mockito.when(
			language.isAvailableLocale(Locale.US)
		).thenReturn(
			true
		);

		Assert.assertEquals(
			Locale.US.toLanguageTag(), LocaleUtil.toBCP47LangTag(Locale.US));

		Mockito.when(
			language.isAvailableLocale(Locale.SIMPLIFIED_CHINESE)
		).thenReturn(
			true
		);

		Assert.assertEquals(
			"zh-Hans", LocaleUtil.toBCP47LangTag(Locale.SIMPLIFIED_CHINESE));

		Mockito.when(
			language.isAvailableLocale(Locale.TRADITIONAL_CHINESE)
		).thenReturn(
			true
		);

		Assert.assertEquals(
			"zh-Hant", LocaleUtil.toBCP47LangTag(Locale.TRADITIONAL_CHINESE));
	}

	@Test
	public void testFromLanguageId() {
		LanguageUtil languageUtil = new LanguageUtil();

		Language language = Mockito.mock(Language.class);

		languageUtil.setLanguage(language);

		Mockito.when(
			language.isAvailableLocale(Locale.US)
		).thenReturn(
			true
		);

		Mockito.when(
			language.isAvailableLanguageCode("en")
		).thenReturn(
			false
		);

		Mockito.when(
			language.isAvailableLanguageCode("fr")
		).thenReturn(
			true
		);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				LocaleUtil.class.getName(), LoggerTestUtil.WARN)) {

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(Locale.US, LocaleUtil.fromLanguageId("en_US"));
			Assert.assertEquals(logEntries.toString(), 0, logEntries.size());

			logEntries.clear();

			LocaleUtil.fromLanguageId("en");

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"en is not a valid language id", logEntry.getMessage());

			logEntries.clear();

			Assert.assertEquals(Locale.FRENCH, LocaleUtil.fromLanguageId("fr"));
			Assert.assertEquals(logEntries.toString(), 0, logEntries.size());
		}
	}

	@Test
	public void testFromLanguageIdBCP47() {
		LanguageUtil languageUtil = new LanguageUtil();

		Language language = Mockito.mock(Language.class);

		languageUtil.setLanguage(language);

		Mockito.when(
			language.isAvailableLocale(Locale.US)
		).thenReturn(
			true
		);

		Assert.assertEquals(Locale.US, LocaleUtil.fromLanguageId("en-US"));

		Mockito.when(
			language.isAvailableLocale(Locale.SIMPLIFIED_CHINESE)
		).thenReturn(
			true
		);

		Assert.assertEquals(
			Locale.SIMPLIFIED_CHINESE, LocaleUtil.fromLanguageId("zh-Hans-CN"));

		Mockito.when(
			language.isAvailableLocale(Locale.TRADITIONAL_CHINESE)
		).thenReturn(
			true
		);

		Assert.assertEquals(
			Locale.TRADITIONAL_CHINESE,
			LocaleUtil.fromLanguageId("zh-Hant-TW"));
	}

	@Test
	public void testFromLanguageIdLocaleIsCreatedAndRetrievableWhenNoValidationDone() {
		LanguageUtil languageUtil = new LanguageUtil();

		Language language = Mockito.mock(Language.class);

		languageUtil.setLanguage(language);

		Mockito.when(
			language.isAvailableLocale(Locale.ITALY)
		).thenReturn(
			false
		);

		Assert.assertNotNull(LocaleUtil.fromLanguageId("it_IT", false));

		Assert.assertSame(
			LocaleUtil.fromLanguageId("it_IT", false),
			LocaleUtil.fromLanguageId("it_IT", false));
	}

	@ExpectedLogs(
		expectedLogs = {
			@ExpectedLog(
				expectedLog = "invalid is a not a valid language id",
				expectedType = ExpectedType.EXACT
			),
			@ExpectedLog(
				expectedLog = "invalid- is a not a valid language id",
				expectedType = ExpectedType.EXACT
			),
			@ExpectedLog(
				expectedLog = "invalid_ is a not a valid language id",
				expectedType = ExpectedType.EXACT
			)
		},
		level = "WARN", loggerClass = LocaleUtil.class
	)
	@Test
	public void testFromLanguageIdValidationWithInvalidInput() {
		Assert.assertNull(LocaleUtil.fromLanguageId("invalid", true, false));
		Assert.assertNull(LocaleUtil.fromLanguageId("invalid-", true, false));
		Assert.assertNull(LocaleUtil.fromLanguageId("invalid_", true, false));
	}

	@Test
	public void testFromLanguageValidation() {
		LanguageUtil languageUtil = new LanguageUtil();

		Language language = Mockito.mock(Language.class);

		languageUtil.setLanguage(language);

		Mockito.when(
			language.isAvailableLocale(Locale.GERMANY)
		).thenReturn(
			false
		);

		Assert.assertEquals(
			Locale.GERMANY, LocaleUtil.fromLanguageId("de_DE", false, false));
		Assert.assertNull(LocaleUtil.fromLanguageId("de_DE", true, false));
	}

	@Test
	public void testGetLocaleDisplayName() {
		LanguageUtil languageUtil = new LanguageUtil();

		Language language = Mockito.mock(Language.class);

		languageUtil.setLanguage(language);

		Mockito.when(
			language.get(Locale.US, "language.en")
		).thenReturn(
			"English"
		);

		Mockito.when(
			language.get(Locale.US, "language.ca")
		).thenReturn(
			"Catalan"
		);

		Assert.assertEquals(
			"English (United States)",
			LocaleUtil.getLocaleDisplayName(Locale.US, Locale.US));

		Locale catalanLocale = new Locale("ca", "ES");

		Assert.assertEquals(
			"Catalan (Spain)",
			LocaleUtil.getLocaleDisplayName(catalanLocale, Locale.US));

		Locale catalanValenciaLocale = new Locale("ca", "ES", "VALENCIA");

		Assert.assertEquals(
			"Catalan (Spain, Valencian)",
			LocaleUtil.getLocaleDisplayName(catalanValenciaLocale, Locale.US));
	}

	@Test
	public void testGetLongDisplayName() {
		LanguageUtil languageUtil = new LanguageUtil();

		Language language = Mockito.mock(Language.class);

		languageUtil.setLanguage(language);

		Mockito.when(
			language.isBetaLocale(Mockito.any())
		).thenReturn(
			false
		);

		Set<String> duplicateLanguages = Collections.singleton("ca");

		Assert.assertEquals(
			"English",
			LocaleUtil.getLongDisplayName(Locale.US, duplicateLanguages));

		Locale catalanLocale = new Locale("ca", "ES");

		Assert.assertEquals(
			"catal\u00e0 (Espanya)",
			LocaleUtil.getLongDisplayName(catalanLocale, duplicateLanguages));

		Locale catalanValenciaLocale = new Locale("ca", "ES", "VALENCIA");

		Assert.assertEquals(
			"catal\u00e0 (Espanya, valenci\u00e0)",
			LocaleUtil.getLongDisplayName(
				catalanValenciaLocale, duplicateLanguages));
	}

}