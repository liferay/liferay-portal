/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.data.masking.internal.engine.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.data.masking.engine.DataMaskingEngine;
import com.liferay.headless.data.masking.test.util.DataMaskTestUtil;
import com.liferay.object.model.ObjectEntry;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Arrays;
import java.util.List;

import org.hamcrest.CoreMatchers;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jose Luis Navarro
 */
@FeatureFlags(featureFlags = @FeatureFlag("LPD-63311"))
@RunWith(Arquillian.class)
public class DataMaskingEngineTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		DataMaskTestUtil.processBatchEngineUnits();
	}

	@Test
	public void testRedactBankAccountNumbers() throws Exception {
		Assert.assertEquals(
			"IBANs: [BANK_ACCOUNT_NUMBER], [BANK_ACCOUNT_NUMBER], " +
				"[BANK_ACCOUNT_NUMBER].",
			_redact(
				Arrays.asList("L_DATA_MASK_IBAN"),
				"IBANs: DE89 3704 0044 0532 0130 00, NL91ABNA0417164300, " +
					"GB29NWBK60161331926819."));
	}

	@Test
	public void testRedactComposedSystemMasks() throws Exception {
		Assert.assertEquals(
			"Email [EMAIL_ADDRESS], IBAN [BANK_ACCOUNT_NUMBER], SSN " +
				"[NATIONAL_ID].",
			_redact(
				Arrays.asList(
					"L_DATA_MASK_EMAIL_ADDRESS", "L_DATA_MASK_IBAN",
					"L_DATA_MASK_NATIONAL_ID_SSN"),
				"Email alice@example.com, IBAN DE89370400440532013000, SSN " +
					"123-45-6789."));
	}

	@Test
	public void testRedactCreditCardNumbers() throws Exception {
		Assert.assertEquals(
			"Cards: [CREDIT_CARD_NUMBER], [CREDIT_CARD_NUMBER], " +
				"[CREDIT_CARD_NUMBER].",
			_redact(
				Arrays.asList("L_DATA_MASK_CREDIT_CARD_NUMBER"),
				"Cards: 4111111111111111, 4111 1111 1111 1111, " +
					"4111-1111-1111-1111."));
	}

	@Test
	public void testRedactEmailAddresses() throws Exception {
		Assert.assertEquals(
			"Emails: [EMAIL_ADDRESS] and [EMAIL_ADDRESS].",
			_redact(
				Arrays.asList("L_DATA_MASK_EMAIL_ADDRESS"),
				"Emails: a.b+tag@sub.example.co.uk and USER@EXAMPLE.COM."));
	}

	@Test
	public void testRedactEscapesReplacementValue() throws Exception {
		ObjectEntry escapedMaskObjectEntry = DataMaskTestUtil.addDataMask(
			RandomTestUtil.randomString(), "secret", "[$1\\X]");

		Assert.assertEquals(
			"value: [$1\\X]",
			_redact(
				Arrays.asList(
					escapedMaskObjectEntry.getExternalReferenceCode()),
				"value: secret"));
	}

	@Test
	public void testRedactIPv4Addresses() throws Exception {
		Assert.assertEquals(
			"Hosts: 10.0.0.0/24 and 192.168.1.0/24.",
			_redact(
				Arrays.asList("L_DATA_MASK_IPV4"),
				"Hosts: 10.0.0.42 and 192.168.1.99."));
	}

	@Test
	public void testRedactIPv6Addresses() throws Exception {
		Assert.assertEquals(
			"Hosts: 2001:db8::/48 and 2001:0db8:85a3::/48.",
			_redact(
				Arrays.asList("L_DATA_MASK_IPV6"),
				"Hosts: 2001:db8::1 and " +
					"2001:0db8:85a3:0000:0000:8a2e:0370:7334."));
	}

	@Test
	public void testRedactNationalIds() throws Exception {
		Assert.assertEquals(
			"BSN: [NATIONAL_ID].",
			_redact(
				Arrays.asList("L_DATA_MASK_NATIONAL_ID_BSN"),
				"BSN: 123456789."));
		Assert.assertEquals(
			"DNI: [NATIONAL_ID].",
			_redact(
				Arrays.asList("L_DATA_MASK_NATIONAL_ID_DNI_NIF"),
				"DNI: 12345678A."));
		Assert.assertEquals(
			"SSN: [NATIONAL_ID].",
			_redact(
				Arrays.asList("L_DATA_MASK_NATIONAL_ID_SSN"),
				"SSN: 123-45-6789."));
	}

	@Test
	public void testRedactPhoneNumbers() throws Exception {
		String redactedText = _redact(
			Arrays.asList("L_DATA_MASK_PHONE_NUMBER"),
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

	@FeatureFlags(
		featureFlags = @FeatureFlag(enable = false, value = "LPD-63311")
	)
	@Test
	public void testRedactWhenFeatureFlagIsDisabled() throws Exception {
		String text = "Contact: " + _SAMPLE_EMAIL;

		Assert.assertEquals(
			text, _redact(Arrays.asList("L_DATA_MASK_EMAIL_ADDRESS"), text));
	}

	@Test
	public void testRedactWhenMaskCodeIsUnknown() throws Exception {
		String text = "Contact: " + _SAMPLE_EMAIL;

		Assert.assertEquals(
			text, _redact(Arrays.asList("L_UNKNOWN_DATA_MASK"), text));
	}

	private String _redact(List<String> maskExternalReferenceCodes, String text)
		throws Exception {

		return _dataMaskingEngine.redact(
			TestPropsValues.getCompanyId(), maskExternalReferenceCodes, text);
	}

	private static final String _SAMPLE_EMAIL = "contact@example.com";

	@Inject
	private DataMaskingEngine _dataMaskingEngine;

}