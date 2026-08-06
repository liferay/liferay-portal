/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.data.mask.internal.engine;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Jose Luis Navarro
 * @author Alejandro Tardín
 */
public class RedactUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testEvict() {
		String detectionRegex = "zzz\\d+zzz";

		Map<String, Pattern> patterns = ReflectionTestUtil.getFieldValue(
			RedactUtil.class, "_patterns");

		RedactUtil.redact(detectionRegex, null, "[X]", "zzz123zzz");

		Assert.assertTrue(patterns.containsKey(detectionRegex));

		RedactUtil.evict(detectionRegex);

		Assert.assertFalse(patterns.containsKey(detectionRegex));
	}

	@Test
	public void testRedact() {
		Assert.assertEquals(
			"BSN: [BSN].",
			RedactUtil.redact(
				"\\b[1-9]\\d{8}\\b", null, "[BSN]", "BSN: 123456789."));
		Assert.assertEquals(
			"Cards: [CREDIT_CARD_NUMBER], [CREDIT_CARD_NUMBER], " +
				"[CREDIT_CARD_NUMBER].",
			RedactUtil.redact(
				"\\b(?:\\d[ \\-]?){13,19}\\b", null, "[CREDIT_CARD_NUMBER]",
				"Cards: 4111111111111111, 4111 1111 1111 1111, " +
					"4111-1111-1111-1111."));
		Assert.assertEquals(
			"DNI: [DNI].",
			RedactUtil.redact(
				"\\b\\d{8}[A-Z]\\b", null, "[DNI]", "DNI: 12345678A."));
		Assert.assertEquals(
			"Emails: [EMAIL_ADDRESS] and [EMAIL_ADDRESS].",
			RedactUtil.redact(
				_REGEX_EMAIL_ADDRESS, null, "[EMAIL_ADDRESS]",
				"Emails: a.b+tag@sub.example.co.uk and USER@EXAMPLE.COM."));
		Assert.assertEquals(
			"Hosts: 10.0.0.0/24 and 192.168.1.0/24.",
			RedactUtil.redact(
				"\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b", "\\.\\d{1,3}$", ".0/24",
				"Hosts: 10.0.0.42 and 192.168.1.99."));
		Assert.assertEquals(
			"Hosts: 2001:db8::/48 and 2001:0db8:85a3::/48.",
			RedactUtil.redact(
				"(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}|" +
					"(?:[0-9a-fA-F]{1,4}:)*[0-9a-fA-F]{0,4}::" +
						"(?:[0-9a-fA-F]{1,4}:)*[0-9a-fA-F]{0,4}",
				"(::.*$)|(:[0-9a-fA-F]{1,4}(:[0-9a-fA-F]{1,4}){4}$)", "::/48",
				"Hosts: 2001:db8::1 and " +
					"2001:0db8:85a3:0000:0000:8a2e:0370:7334."));
		Assert.assertEquals(
			"IBANs: [BANK_ACCOUNT_NUMBER], [BANK_ACCOUNT_NUMBER], " +
				"[BANK_ACCOUNT_NUMBER].",
			RedactUtil.redact(
				_REGEX_BANK_ACCOUNT_NUMBER, null, "[BANK_ACCOUNT_NUMBER]",
				"IBANs: DE89 3704 0044 0532 0130 00, NL91ABNA0417164300, " +
					"GB29NWBK60161331926819."));
		Assert.assertEquals(
			"No sensitive data here.",
			RedactUtil.redact(
				_REGEX_SSN, null, "[NATIONAL_ID]", "No sensitive data here."));
		Assert.assertEquals(
			"Phones: [PHONE_NUMBER], [PHONE_NUMBER]and [PHONE_NUMBER]",
			RedactUtil.redact(
				"(?<!\\d)\\+\\d[\\d\\s\\-().]{6,19}(?!\\d)", null,
				"[PHONE_NUMBER]",
				"Phones: +1 (202) 555-0199, +34600123456 and +44 20 7946 " +
					"0958."));
		Assert.assertEquals(
			"SSN: 123-45-6789.",
			RedactUtil.redact(null, null, "[X]", "SSN: 123-45-6789."));
		Assert.assertEquals(
			"SSN: [SSN].",
			RedactUtil.redact(_REGEX_SSN, null, "[SSN]", "SSN: 123-45-6789."));
		Assert.assertEquals(
			"value: [$1\\X]",
			RedactUtil.redact("secret", null, "[$1\\X]", "value: secret"));
		Assert.assertNull(RedactUtil.redact("\\d+", null, "[X]", null));

		Assert.assertEquals(
			"[X]" + _TEXT_LONG,
			RedactUtil.redact(
				RedactUtil.newDeadline(), "www\\d+www", null, "[X]",
				"www123www" + _TEXT_LONG));

		RedactException redactException = Assert.assertThrows(
			RedactException.class,
			() -> RedactUtil.redact(
				0, _REGEX_SIMPLE, null, RandomTestUtil.randomString(),
				_TEXT_LONG));

		Assert.assertEquals(_MESSAGE_TIMEOUT, redactException.getMessage());

		redactException = Assert.assertThrows(
			RedactException.class,
			() -> RedactUtil.redact(
				RedactUtil.newDeadline(), _REGEX_CATASTROPHIC, null,
				RandomTestUtil.randomString(), _TEXT_CATASTROPHIC));

		Assert.assertEquals(_MESSAGE_TIMEOUT, redactException.getMessage());

		redactException = Assert.assertThrows(
			RedactException.class,
			() -> RedactUtil.redact(
				RedactUtil.newDeadline(), _REGEX_SIMPLE, _REGEX_CATASTROPHIC,
				RandomTestUtil.randomString(), _TEXT_CATASTROPHIC));

		Assert.assertEquals(_MESSAGE_TIMEOUT, redactException.getMessage());

		redactException = Assert.assertThrows(
			RedactException.class,
			() -> RedactUtil.redact(
				RedactUtil.newDeadline(), "(a|aa)+$", null,
				RandomTestUtil.randomString(), "a".repeat(100000) + "b"));

		Assert.assertEquals(
			"Redaction overflowed the stack", redactException.getMessage());

		Assert.assertThrows(
			PatternSyntaxException.class,
			() -> RedactUtil.redact(
				"[", null, RandomTestUtil.randomString(), "anything"));
	}

	@Test
	public void testRedactSequentially() {
		String text =
			"Email alice@example.com, IBAN DE89370400440532013000, SSN " +
				"123-45-6789.";

		text = RedactUtil.redact(
			_REGEX_BANK_ACCOUNT_NUMBER, null, "[BANK_ACCOUNT_NUMBER]", text);
		text = RedactUtil.redact(
			_REGEX_EMAIL_ADDRESS, null, "[EMAIL_ADDRESS]", text);
		text = RedactUtil.redact(_REGEX_SSN, null, "[SSN]", text);

		Assert.assertEquals(
			"Email [EMAIL_ADDRESS], IBAN [BANK_ACCOUNT_NUMBER], SSN [SSN].",
			text);
	}

	@Test
	public void testRedactWithoutCaching() {
		String detectionRegex = "yyy\\d+yyy";

		Map<String, Pattern> patterns = ReflectionTestUtil.getFieldValue(
			RedactUtil.class, "_patterns");

		Assert.assertEquals(
			"[X]",
			RedactUtil.redactWithoutCaching(
				detectionRegex, null, "[X]", "yyy123yyy"));
		Assert.assertFalse(patterns.containsKey(detectionRegex));

		Assert.assertThrows(
			RedactException.class,
			() -> RedactUtil.redactWithoutCaching(
				_REGEX_CATASTROPHIC, null, RandomTestUtil.randomString(),
				_TEXT_CATASTROPHIC));
	}

	private static final String _MESSAGE_TIMEOUT =
		"Redaction exceeded the timeout of 1000 milliseconds";

	private static final String _REGEX_BANK_ACCOUNT_NUMBER =
		"\\b[A-Z]{2}\\d{2}(?:\\s?[A-Z0-9]){11,30}\\b";

	private static final String _REGEX_CATASTROPHIC = "(.*a){40}";

	private static final String _REGEX_EMAIL_ADDRESS =
		"\\b[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}\\b";

	private static final String _REGEX_SIMPLE = "a+";

	private static final String _REGEX_SSN = "\\b\\d{3}-\\d{2}-\\d{4}\\b";

	private static final String _TEXT_CATASTROPHIC = "a".repeat(40);

	private static final String _TEXT_LONG = "b".repeat(2000);

}