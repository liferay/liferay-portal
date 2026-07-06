/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.data.mask.internal.engine;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.hamcrest.CoreMatchers;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Jose Luis Navarro
 */
public class DataMaskEngineUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testEvict() {
		String detectionRegex = "zzz\\d+zzz";

		Map<String, Pattern> patterns = ReflectionTestUtil.getFieldValue(
			DataMaskEngineUtil.class, "_patterns");

		DataMaskEngineUtil.redact(detectionRegex, null, "[X]", "zzz123zzz");

		Assert.assertTrue(patterns.containsKey(detectionRegex));

		DataMaskEngineUtil.evict(detectionRegex);

		Assert.assertFalse(patterns.containsKey(detectionRegex));
	}

	@Test
	public void testRedactAppliesMasksInSequence() {
		String text =
			"Email alice@example.com, IBAN DE89370400440532013000, SSN " +
				"123-45-6789.";

		text = DataMaskEngineUtil.redact(
			"\\b[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}\\b", null,
			"[EMAIL_ADDRESS]", text);
		text = DataMaskEngineUtil.redact(
			"\\b[A-Z]{2}\\d{2}(?:\\s?[A-Z0-9]){11,30}\\b", null,
			"[BANK_ACCOUNT_NUMBER]", text);
		text = DataMaskEngineUtil.redact(
			"\\b\\d{3}-\\d{2}-\\d{4}\\b", null, "[NATIONAL_ID]", text);

		Assert.assertEquals(
			"Email [EMAIL_ADDRESS], IBAN [BANK_ACCOUNT_NUMBER], SSN " +
				"[NATIONAL_ID].",
			text);
	}

	@Test
	public void testRedactBankAccountNumbers() {
		Assert.assertEquals(
			"IBANs: [BANK_ACCOUNT_NUMBER], [BANK_ACCOUNT_NUMBER], " +
				"[BANK_ACCOUNT_NUMBER].",
			DataMaskEngineUtil.redact(
				"\\b[A-Z]{2}\\d{2}(?:\\s?[A-Z0-9]){11,30}\\b", null,
				"[BANK_ACCOUNT_NUMBER]",
				"IBANs: DE89 3704 0044 0532 0130 00, NL91ABNA0417164300, " +
					"GB29NWBK60161331926819."));
	}

	@Test
	public void testRedactCreditCardNumbers() {
		Assert.assertEquals(
			"Cards: [CREDIT_CARD_NUMBER], [CREDIT_CARD_NUMBER], " +
				"[CREDIT_CARD_NUMBER].",
			DataMaskEngineUtil.redact(
				"\\b(?:\\d[ \\-]?){13,19}\\b", null, "[CREDIT_CARD_NUMBER]",
				"Cards: 4111111111111111, 4111 1111 1111 1111, " +
					"4111-1111-1111-1111."));
	}

	@Test
	public void testRedactEmailAddresses() {
		Assert.assertEquals(
			"Emails: [EMAIL_ADDRESS] and [EMAIL_ADDRESS].",
			DataMaskEngineUtil.redact(
				"\\b[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}\\b",
				null, "[EMAIL_ADDRESS]",
				"Emails: a.b+tag@sub.example.co.uk and USER@EXAMPLE.COM."));
	}

	@Test
	public void testRedactEscapesReplacementValue() {
		Assert.assertEquals(
			"value: [$1\\X]",
			DataMaskEngineUtil.redact(
				"secret", null, "[$1\\X]", "value: secret"));
	}

	@Test
	public void testRedactIPv4Addresses() {
		Assert.assertEquals(
			"Hosts: 10.0.0.0/24 and 192.168.1.0/24.",
			DataMaskEngineUtil.redact(
				"\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b", "\\.\\d{1,3}$", ".0/24",
				"Hosts: 10.0.0.42 and 192.168.1.99."));
	}

	@Test
	public void testRedactIPv6Addresses() {
		Assert.assertEquals(
			"Hosts: 2001:db8::/48 and 2001:0db8:85a3::/48.",
			DataMaskEngineUtil.redact(
				"(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}|" +
					"(?:[0-9a-fA-F]{1,4}:)*[0-9a-fA-F]{0,4}::" +
						"(?:[0-9a-fA-F]{1,4}:)*[0-9a-fA-F]{0,4}",
				"(::.*$)|(:[0-9a-fA-F]{1,4}(:[0-9a-fA-F]{1,4}){4}$)", "::/48",
				"Hosts: 2001:db8::1 and " +
					"2001:0db8:85a3:0000:0000:8a2e:0370:7334."));
	}

	@Test
	public void testRedactNationalIds() {
		Assert.assertEquals(
			"BSN: [NATIONAL_ID].",
			DataMaskEngineUtil.redact(
				"\\b[1-9]\\d{8}\\b", null, "[NATIONAL_ID]", "BSN: 123456789."));

		Assert.assertEquals(
			"DNI: [NATIONAL_ID].",
			DataMaskEngineUtil.redact(
				"\\b\\d{8}[A-Z]\\b", null, "[NATIONAL_ID]", "DNI: 12345678A."));

		Assert.assertEquals(
			"SSN: [NATIONAL_ID].",
			DataMaskEngineUtil.redact(
				"\\b\\d{3}-\\d{2}-\\d{4}\\b", null, "[NATIONAL_ID]",
				"SSN: 123-45-6789."));
	}

	@Test
	public void testRedactPhoneNumbers() {
		String redactedText = DataMaskEngineUtil.redact(
			"(?<!\\d)\\+\\d[\\d\\s\\-().]{6,19}(?!\\d)", null, "[PHONE_NUMBER]",
			"Phones: +1 (202) 555-0199, +34600123456 and +44 20 7946 0958.");

		Assert.assertThat(
			redactedText, CoreMatchers.containsString("[PHONE_NUMBER]"));
		Assert.assertThat(
			redactedText,
			CoreMatchers.not(CoreMatchers.containsString("0199")));
		Assert.assertThat(
			redactedText,
			CoreMatchers.not(CoreMatchers.containsString("34600123456")));
		Assert.assertThat(
			redactedText,
			CoreMatchers.not(CoreMatchers.containsString("0958")));
	}

	@Test
	public void testRedactThrowsWhenDetectionRegexIsInvalid() {
		try {
			DataMaskEngineUtil.redact("[", null, "[X]", "anything");

			Assert.fail();
		}
		catch (PatternSyntaxException patternSyntaxException) {
		}
	}

	@Test
	public void testRedactWhenDetectionRegexIsNull() {
		String text = "SSN: 123-45-6789.";

		Assert.assertEquals(
			text, DataMaskEngineUtil.redact(null, null, "[X]", text));
	}

	@Test
	public void testRedactWhenNoMatch() {
		String text = "No sensitive data here.";

		Assert.assertEquals(
			text,
			DataMaskEngineUtil.redact(
				"\\b\\d{3}-\\d{2}-\\d{4}\\b", null, "[NATIONAL_ID]", text));
	}

	@Test
	public void testRedactWhenTextIsNull() {
		Assert.assertNull(DataMaskEngineUtil.redact("\\d+", null, "[X]", null));
	}

}