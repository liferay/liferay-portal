/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.auth;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.security.auth.DefaultScreenNameValidator;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Drew Brokke
 */
public class DefaultScreenNameValidatorTest extends DefaultScreenNameValidator {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetJSEscapedSpecialChars() {
		PropsUtil.set(PropsKeys.USERS_SCREEN_NAME_SPECIAL_CHARACTERS, "-._'");

		String expected = HtmlUtil.escapeJS(_escapeRegexCharacters("-._'"));

		Assert.assertEquals(expected, getJSEscapedSpecialChars());
	}

	@Test
	public void testValidateSpecialCharacters() {
		for (String regexReservedCharacter : _REGEX_RESERVED_CHARACTERS) {
			PropsUtil.set(PropsKeys.USERS_SCREEN_NAME_SPECIAL_CHARACTERS, "");

			Assert.assertFalse(validate(0, regexReservedCharacter));

			PropsUtil.set(
				PropsKeys.USERS_SCREEN_NAME_SPECIAL_CHARACTERS,
				regexReservedCharacter);

			Assert.assertTrue(validate(0, regexReservedCharacter));
		}
	}

	@Override
	protected String getSpecialChars() {
		String specialChars = PropsUtil.get(
			PropsKeys.USERS_SCREEN_NAME_SPECIAL_CHARACTERS);

		return StringUtil.removeChar(specialChars, CharPool.SLASH);
	}

	@Override
	protected String getSpecialCharsRegex() {
		return _escapeRegexCharacters(getSpecialChars());
	}

	private String _escapeRegexCharacters(String characters) {
		Matcher matcher = _escapeRegexPattern.matcher(characters);

		return matcher.replaceAll("\\\\$0");
	}

	private static final String[] _REGEX_RESERVED_CHARACTERS = {
		".", "^", "$", "*", "+", "-", "?", "(", ")", "[", "]", "{", "}", "\\",
		"|"
	};

	private static final Pattern _escapeRegexPattern = Pattern.compile(
		"[-+\\\\\\[\\]]");

}