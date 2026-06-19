/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.data.masking.internal.engine.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.data.masking.engine.DataMaskingEngine;
import com.liferay.headless.data.masking.test.util.DataMaskTestUtil;
import com.liferay.object.model.ObjectEntry;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Arrays;

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

	@FeatureFlags(featureFlags = @FeatureFlag("LPD-90204"))
	@Test
	public void testRedact() throws Exception {
		long companyId = TestPropsValues.getCompanyId();

		String redactedText = _dataMaskingEngine.redact(
			companyId,
			Arrays.asList(
				"L_DATA_MASK_CREDIT_CARD_NUMBER", "L_DATA_MASK_EMAIL_ADDRESS",
				"L_DATA_MASK_IBAN", "L_DATA_MASK_IPV4", "L_DATA_MASK_IPV6",
				"L_DATA_MASK_NATIONAL_ID_BSN",
				"L_DATA_MASK_NATIONAL_ID_DNI_NIF",
				"L_DATA_MASK_NATIONAL_ID_SSN", "L_DATA_MASK_PHONE_NUMBER"),
			StringBundler.concat(
				"Credit card: ", _SAMPLE_CREDIT_CARD, ". Email: ",
				_SAMPLE_EMAIL_ALT, ". IBAN: ", _SAMPLE_IBAN, ". IPv4: ",
				_SAMPLE_IPV4, ". IPv6: ", _SAMPLE_IPV6, ". BSN: ", _SAMPLE_BSN,
				". DNI: ", _SAMPLE_DNI, ". SSN: ", _SAMPLE_SSN, ". Phone: ",
				_SAMPLE_PHONE_INTL, "."));

		Assert.assertThat(
			redactedText, CoreMatchers.containsString("[BANK_ACCOUNT_NUMBER]"));
		Assert.assertThat(
			redactedText, CoreMatchers.containsString("[CREDIT_CARD_NUMBER]"));
		Assert.assertThat(
			redactedText, CoreMatchers.containsString("[EMAIL_ADDRESS]"));
		Assert.assertThat(
			redactedText, CoreMatchers.containsString("[NATIONAL_ID]"));
		Assert.assertThat(
			redactedText, CoreMatchers.containsString("[PHONE_NUMBER]"));
		Assert.assertThat(
			redactedText, CoreMatchers.containsString("192.168.1.0/24"));
		Assert.assertThat(
			redactedText, CoreMatchers.containsString("2001:0db8:85a3::/48"));
		Assert.assertThat(
			redactedText,
			CoreMatchers.not(CoreMatchers.containsString(_SAMPLE_BSN)));
		Assert.assertThat(
			redactedText,
			CoreMatchers.not(CoreMatchers.containsString(_SAMPLE_CREDIT_CARD)));
		Assert.assertThat(
			redactedText,
			CoreMatchers.not(CoreMatchers.containsString(_SAMPLE_DNI)));
		Assert.assertThat(
			redactedText,
			CoreMatchers.not(CoreMatchers.containsString(_SAMPLE_EMAIL_ALT)));
		Assert.assertThat(
			redactedText,
			CoreMatchers.not(CoreMatchers.containsString(_SAMPLE_IBAN)));
		Assert.assertThat(
			redactedText,
			CoreMatchers.not(CoreMatchers.containsString(_SAMPLE_IPV4)));
		Assert.assertThat(
			redactedText,
			CoreMatchers.not(CoreMatchers.containsString(_SAMPLE_IPV6)));
		Assert.assertThat(
			redactedText,
			CoreMatchers.not(CoreMatchers.containsString(_SAMPLE_PHONE_INTL)));
		Assert.assertThat(
			redactedText,
			CoreMatchers.not(CoreMatchers.containsString(_SAMPLE_SSN)));

		ObjectEntry domainMaskObjectEntry = DataMaskTestUtil.addCustomMask(
			RandomTestUtil.randomString(), "example\\.com", "[DOMAIN]");

		Assert.assertEquals(
			"Contact: contact@[DOMAIN]",
			_dataMaskingEngine.redact(
				companyId,
				Arrays.asList(
					domainMaskObjectEntry.getExternalReferenceCode(),
					"L_DATA_MASK_EMAIL_ADDRESS"),
				"Contact: contact@example.com"));

		ObjectEntry badMaskObjectEntry = DataMaskTestUtil.addCustomMask(
			RandomTestUtil.randomString(), "Contact", "$5-no-such-group");

		redactedText = _dataMaskingEngine.redact(
			companyId,
			Arrays.asList(
				badMaskObjectEntry.getExternalReferenceCode(),
				"L_DATA_MASK_EMAIL_ADDRESS"),
			"Contact: " + _SAMPLE_EMAIL);

		Assert.assertThat(
			redactedText, CoreMatchers.containsString("[EMAIL_ADDRESS]"));
		Assert.assertThat(
			redactedText,
			CoreMatchers.not(CoreMatchers.containsString(_SAMPLE_EMAIL)));

		String text = "Contact: " + _SAMPLE_EMAIL;

		Assert.assertEquals(
			text,
			_dataMaskingEngine.redact(
				companyId, Arrays.asList("L_UNKNOWN_DATA_MASK"), text));

		Assert.assertEquals(
			"Cards: [CREDIT_CARD_NUMBER], [CREDIT_CARD_NUMBER], " +
				"[CREDIT_CARD_NUMBER].",
			_dataMaskingEngine.redact(
				companyId, Arrays.asList("L_DATA_MASK_CREDIT_CARD_NUMBER"),
				"Cards: 4111111111111111, 4111 1111 1111 1111, " +
					"4111-1111-1111-1111."));
		Assert.assertEquals(
			"Emails: [EMAIL_ADDRESS] and [EMAIL_ADDRESS].",
			_dataMaskingEngine.redact(
				companyId, Arrays.asList("L_DATA_MASK_EMAIL_ADDRESS"),
				"Emails: a.b+tag@sub.example.co.uk and USER@EXAMPLE.COM."));
		Assert.assertEquals(
			"IBANs: [BANK_ACCOUNT_NUMBER], [BANK_ACCOUNT_NUMBER], " +
				"[BANK_ACCOUNT_NUMBER].",
			_dataMaskingEngine.redact(
				companyId, Arrays.asList("L_DATA_MASK_IBAN"),
				"IBANs: DE89 3704 0044 0532 0130 00, NL91ABNA0417164300, " +
					"GB29NWBK60161331926819."));

		redactedText = _dataMaskingEngine.redact(
			companyId, Arrays.asList("L_DATA_MASK_PHONE_NUMBER"),
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
		featureFlags = @FeatureFlag(enable = false, value = "LPD-90204")
	)
	@Test
	public void testRedactWhenFeatureFlagIsDisabled() throws Exception {
		String text = "Contact: " + _SAMPLE_EMAIL;

		Assert.assertEquals(
			text,
			_dataMaskingEngine.redact(
				TestPropsValues.getCompanyId(),
				Arrays.asList("L_DATA_MASK_EMAIL_ADDRESS"), text));
	}

	private static final String _SAMPLE_BSN = "123456789";

	private static final String _SAMPLE_CREDIT_CARD = "4111-1111-1111-1111";

	private static final String _SAMPLE_DNI = "12345678A";

	private static final String _SAMPLE_EMAIL = "contact@example.com";

	private static final String _SAMPLE_EMAIL_ALT = "alice@example.com";

	private static final String _SAMPLE_IBAN = "DE89370400440532013000";

	private static final String _SAMPLE_IPV4 = "192.168.1.42";

	private static final String _SAMPLE_IPV6 =
		"2001:0db8:85a3:0000:0000:8a2e:0370:7334";

	private static final String _SAMPLE_PHONE_INTL = "+34-600-123-456";

	private static final String _SAMPLE_SSN = "123-45-6789";

	@Inject
	private DataMaskingEngine _dataMaskingEngine;

}