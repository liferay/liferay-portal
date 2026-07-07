/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.data.mask.internal.engine;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.performance.PerformanceTimer;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Jose Luis Navarro
 */
public class RedactUtilPerformanceTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	@TestInfo("LPD-91092")
	public void testRedactPerformance() {
		String[][] dataMasks = {
			{"\\b(?:\\d[ \\-]?){13,19}\\b", null, "[CREDIT_CARD_NUMBER]"},
			{
				"\\b[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}\\b",
				null, "[EMAIL_ADDRESS]"
			},
			{
				"\\b[A-Z]{2}\\d{2}(?:\\s?[A-Z0-9]){11,30}\\b", null,
				"[BANK_ACCOUNT_NUMBER]"
			},
			{"\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b", "\\.\\d{1,3}$", ".0/24"},
			{
				"(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}|" +
					"(?:[0-9a-fA-F]{1,4}:)*[0-9a-fA-F]{0,4}::" +
						"(?:[0-9a-fA-F]{1,4}:)*[0-9a-fA-F]{0,4}",
				"(::.*$)|(:[0-9a-fA-F]{1,4}(:[0-9a-fA-F]{1,4}){4}$)", "::/48"
			},
			{"\\b[1-9]\\d{8}\\b", null, "[NATIONAL_ID]"},
			{"\\b\\d{8}[A-Z]\\b", null, "[NATIONAL_ID]"},
			{"\\b\\d{3}-\\d{2}-\\d{4}\\b", null, "[NATIONAL_ID]"},
			{
				"(?<!\\d)\\+\\d[\\d\\s\\-().]{6,19}(?!\\d)", null,
				"[PHONE_NUMBER]"
			}
		};

		StringBundler sb = new StringBundler();

		for (int i = 0; i < 20; i++) {
			sb.append(
				StringBundler.concat(
					"Record ", i, ": email ", _SAMPLE_EMAIL, ", phone ",
					_SAMPLE_PHONE_INTL, ", IBAN ", _SAMPLE_IBAN, ", card ",
					_SAMPLE_CREDIT_CARD, ", SSN ", _SAMPLE_SSN, ", IPv4 ",
					_SAMPLE_IPV4, ", IPv6 ", _SAMPLE_IPV6, ". "));
		}

		String payload = sb.toString();

		for (int i = 0; i < 5; i++) {
			_redact(dataMasks, payload);
		}

		int iterations = 50;

		try (PerformanceTimer performanceTimer = new PerformanceTimer(
				RedactUtilPerformanceTest.class,
				_MAX_OVERHEAD_MILLISECONDS * iterations, "redact")) {

			for (int i = 0; i < iterations; i++) {
				_redact(dataMasks, payload);
			}
		}
	}

	private String _redact(String[][] dataMasks, String text) {
		for (String[] dataMask : dataMasks) {
			text = RedactUtil.redact(
				dataMask[0], dataMask[1], dataMask[2], text);
		}

		return text;
	}

	private static final long _MAX_OVERHEAD_MILLISECONDS = 50;

	private static final String _SAMPLE_CREDIT_CARD = "4111-1111-1111-1111";

	private static final String _SAMPLE_EMAIL = "contact@example.com";

	private static final String _SAMPLE_IBAN = "DE89370400440532013000";

	private static final String _SAMPLE_IPV4 = "192.168.1.42";

	private static final String _SAMPLE_IPV6 =
		"2001:0db8:85a3:0000:0000:8a2e:0370:7334";

	private static final String _SAMPLE_PHONE_INTL = "+34-600-123-456";

	private static final String _SAMPLE_SSN = "123-45-6789";

}