/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.data.masking.internal.engine.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.data.masking.engine.DataMaskingEngine;
import com.liferay.headless.data.masking.test.util.DataMaskTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.performance.PerformanceTimer;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jose Luis Navarro
 */
@FeatureFlags(featureFlags = @FeatureFlag("LPD-90204"))
@RunWith(Arquillian.class)
public class DataMaskingEnginePerformanceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		DataMaskTestUtil.processBatchEngineUnits();
	}

	@Test
	@TestInfo("LPD-91092")
	public void testRedact() throws Exception {
		long companyId = TestPropsValues.getCompanyId();

		List<String> maskExternalReferenceCodes = Arrays.asList(
			"L_DATA_MASK_CREDIT_CARD_NUMBER", "L_DATA_MASK_EMAIL_ADDRESS",
			"L_DATA_MASK_IBAN", "L_DATA_MASK_IPV4", "L_DATA_MASK_IPV6",
			"L_DATA_MASK_NATIONAL_ID_BSN", "L_DATA_MASK_NATIONAL_ID_DNI_NIF",
			"L_DATA_MASK_NATIONAL_ID_SSN", "L_DATA_MASK_PHONE_NUMBER");

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
			_dataMaskingEngine.redact(
				companyId, maskExternalReferenceCodes, payload);
		}

		int iterations = 50;

		try (PerformanceTimer performanceTimer = new PerformanceTimer(
				DataMaskingEnginePerformanceTest.class,
				_MAX_OVERHEAD_MILLISECONDS * iterations, "redact")) {

			for (int i = 0; i < iterations; i++) {
				_dataMaskingEngine.redact(
					companyId, maskExternalReferenceCodes, payload);
			}
		}
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

	@Inject
	private DataMaskingEngine _dataMaskingEngine;

}