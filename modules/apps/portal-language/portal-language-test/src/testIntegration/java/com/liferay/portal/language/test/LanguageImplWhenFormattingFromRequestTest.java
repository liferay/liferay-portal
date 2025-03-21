/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.language.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageWrapper;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ProxyFactory;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.language.test.constants.LanguageImplTestConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.Locale;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Manuel de la Peña
 */
@RunWith(Arquillian.class)
public class LanguageImplWhenFormattingFromRequestTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testFormatWithOneArgument() {
		String value = _language.format(
			_createMockHttpServletRequest(LocaleUtil.US),
			LanguageImplTestConstants.LANG_KEY_WITH_ARGUMENT, "31");

		Assert.assertEquals("31 Hours", value);
	}

	@Test
	public void testFormatWithOneLanguageWrapper() {
		String value = _language.format(
			_createMockHttpServletRequest(LocaleUtil.US),
			LanguageImplTestConstants.LANG_KEY_WITH_ARGUMENT,
			new LanguageWrapper("a", "31", "a"));

		Assert.assertEquals("a31a Hours", value);
	}

	@Test
	public void testFormatWithOneNontranslatableAmericanArgument() {
		HttpServletRequest httpServletRequest = _createMockHttpServletRequest(
			LocaleUtil.US);

		String value = _language.format(
			httpServletRequest,
			LanguageImplTestConstants.LANG_KEY_WITH_ARGUMENT,
			LanguageImplTestConstants.BIG_INTEGER, false);

		Assert.assertEquals("1,234,567,890 Hours", value);

		value = _language.format(
			httpServletRequest,
			LanguageImplTestConstants.LANG_KEY_WITH_ARGUMENT,
			LanguageImplTestConstants.BIG_DOUBLE, false);

		Assert.assertEquals("1,234,567,890.12 Hours", value);

		value = _language.format(
			httpServletRequest,
			LanguageImplTestConstants.LANG_KEY_WITH_ARGUMENT,
			LanguageImplTestConstants.BIG_FLOAT, false);

		Assert.assertEquals("1,234,567.875 Hours", value);
	}

	@Test
	public void testFormatWithOneNontranslatableSpanishArgument() {
		HttpServletRequest httpServletRequest = _createMockHttpServletRequest(
			LocaleUtil.SPAIN);

		String value = _language.format(
			httpServletRequest,
			LanguageImplTestConstants.LANG_KEY_WITH_ARGUMENT,
			LanguageImplTestConstants.BIG_INTEGER, false);

		Assert.assertEquals("1.234.567.890 horas", value);

		value = _language.format(
			httpServletRequest,
			LanguageImplTestConstants.LANG_KEY_WITH_ARGUMENT,
			LanguageImplTestConstants.BIG_DOUBLE, false);

		Assert.assertEquals("1.234.567.890,12 horas", value);

		value = _language.format(
			httpServletRequest,
			LanguageImplTestConstants.LANG_KEY_WITH_ARGUMENT,
			LanguageImplTestConstants.BIG_FLOAT, false);

		Assert.assertEquals("1.234.567,875 horas", value);
	}

	@Test
	public void testFormatWithTwoArguments() {
		String value = _language.format(
			_createMockHttpServletRequest(LocaleUtil.US),
			LanguageImplTestConstants.LANG_KEY_WITH_ARGUMENTS,
			new Object[] {"A", "B"});

		Assert.assertEquals("A has invited you to join B.", value);
	}

	@Test
	public void testFormatWithTwoLanguageWrappers() {
		LanguageWrapper[] languageWrappers = new LanguageWrapper[2];

		languageWrappers[0] = new LanguageWrapper("a", "A", "a");
		languageWrappers[1] = new LanguageWrapper("b", "B", "b");

		String value = _language.format(
			_createMockHttpServletRequest(LocaleUtil.US),
			LanguageImplTestConstants.LANG_KEY_WITH_ARGUMENTS,
			languageWrappers);

		Assert.assertEquals("aAa has invited you to join bBb.", value);
	}

	private HttpServletRequest _createMockHttpServletRequest(Locale locale) {
		return new HttpServletRequestWrapper(
			ProxyFactory.newDummyInstance(HttpServletRequest.class)) {

			@Override
			public Object getAttribute(String name) {
				if (!name.equals(WebKeys.THEME_DISPLAY)) {
					return null;
				}

				ThemeDisplay themeDisplay = new ThemeDisplay();

				themeDisplay.setLocale(locale);

				return themeDisplay;
			}

		};
	}

	@Inject
	private static Language _language;

}