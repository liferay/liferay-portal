/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.provider.aws.internal.fips;

import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.security.key.ServiceIndicator;
import com.liferay.portal.security.key.crypto.exception.CryptoException;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Christopher Kian
 */
public class AWSKMSFIPSValidatorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testIsFIPSApprovedKeyOrigin() {
		AWSKMSFIPSValidator awsKMSFIPSValidator = new AWSKMSFIPSValidator(
			RandomTestUtil.randomString(), true);

		Assert.assertTrue(
			awsKMSFIPSValidator.isFIPSApprovedKeyOrigin("AWS_CLOUDHSM"));
		Assert.assertTrue(
			awsKMSFIPSValidator.isFIPSApprovedKeyOrigin("AWS_KMS"));
		Assert.assertFalse(
			awsKMSFIPSValidator.isFIPSApprovedKeyOrigin("EXTERNAL"));
	}

	@Test
	public void testToServiceIndicator() throws Exception {
		AWSKMSFIPSValidator enforcedAWSKMSFIPSValidator =
			new AWSKMSFIPSValidator(RandomTestUtil.randomString(), true);

		ServiceIndicator approvedServiceIndicator =
			enforcedAWSKMSFIPSValidator.toServiceIndicator(
				true, "AWS.KMS.Encrypt");

		Assert.assertEquals(
			"AWS.KMS.Encrypt",
			approvedServiceIndicator.getSecurityFunctionName());
		Assert.assertTrue(approvedServiceIndicator.isApproved());

		Assert.assertThrows(
			CryptoException.class,
			() -> enforcedAWSKMSFIPSValidator.toServiceIndicator(
				false, RandomTestUtil.randomString()));

		AWSKMSFIPSValidator unenforcedAWSKMSFIPSValidator =
			new AWSKMSFIPSValidator(RandomTestUtil.randomString(), false);

		ServiceIndicator unapprovedServiceIndicator =
			unenforcedAWSKMSFIPSValidator.toServiceIndicator(
				false, RandomTestUtil.randomString());

		Assert.assertFalse(unapprovedServiceIndicator.isApproved());
	}

	@Test
	public void testValidateCipherMode() throws Exception {
		AWSKMSFIPSValidator awsKMSFIPSValidator = new AWSKMSFIPSValidator(
			"AES_256_GCM", true);

		awsKMSFIPSValidator.validateCipherMode();

		AWSKMSFIPSValidator cbcAWSKMSFIPSValidator = new AWSKMSFIPSValidator(
			"AES_CBC", true);

		Assert.assertThrows(
			CryptoException.class, cbcAWSKMSFIPSValidator::validateCipherMode);
	}

	@Test
	public void testValidateKeyOrigin() throws Exception {
		AWSKMSFIPSValidator enforcedAWSKMSFIPSValidator =
			new AWSKMSFIPSValidator(RandomTestUtil.randomString(), true);

		enforcedAWSKMSFIPSValidator.validateKeyOrigin("AWS_CLOUDHSM");

		Assert.assertThrows(
			CryptoException.class,
			() -> enforcedAWSKMSFIPSValidator.validateKeyOrigin("EXTERNAL"));

		AWSKMSFIPSValidator unenforcedAWSKMSFIPSValidator =
			new AWSKMSFIPSValidator(RandomTestUtil.randomString(), false);

		unenforcedAWSKMSFIPSValidator.validateKeyOrigin("EXTERNAL");
	}

}