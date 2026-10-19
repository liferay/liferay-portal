/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.creator.openai.web.internal.util;

import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import javax.crypto.spec.SecretKeySpec;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Mariano Álvaro Sáiz
 */
public class AICreatorOpenAIGenerationUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testIsValidSignature() throws Exception {
		byte[] bytes = RandomTestUtil.randomBytes();
		Company company = _mockCompany();
		long expirationTime = System.currentTimeMillis() + Time.HOUR;
		long userId = RandomTestUtil.randomLong();

		Assert.assertTrue(
			AICreatorOpenAIGenerationUtil.isValidSignature(
				bytes, company, expirationTime,
				AICreatorOpenAIGenerationUtil.sign(
					bytes, company, expirationTime, userId),
				userId));
	}

	@Test
	public void testIsValidSignatureWhenBytesAreDifferent() throws Exception {
		Company company = _mockCompany();
		long expirationTime = System.currentTimeMillis() + Time.HOUR;
		long userId = RandomTestUtil.randomLong();

		Assert.assertFalse(
			AICreatorOpenAIGenerationUtil.isValidSignature(
				RandomTestUtil.randomBytes(), company, expirationTime,
				AICreatorOpenAIGenerationUtil.sign(
					RandomTestUtil.randomBytes(), company, expirationTime,
					userId),
				userId));
	}

	@Test
	public void testIsValidSignatureWhenCompanyIsDifferent() throws Exception {
		byte[] bytes = RandomTestUtil.randomBytes();
		long expirationTime = System.currentTimeMillis() + Time.HOUR;
		long userId = RandomTestUtil.randomLong();

		Assert.assertFalse(
			AICreatorOpenAIGenerationUtil.isValidSignature(
				bytes, _mockCompany(), expirationTime,
				AICreatorOpenAIGenerationUtil.sign(
					bytes, _mockCompany(), expirationTime, userId),
				userId));
	}

	@Test
	public void testIsValidSignatureWhenExpirationTimeIsDifferent()
		throws Exception {

		byte[] bytes = RandomTestUtil.randomBytes();
		Company company = _mockCompany();
		long expirationTime = System.currentTimeMillis() + Time.HOUR;
		long userId = RandomTestUtil.randomLong();

		Assert.assertFalse(
			AICreatorOpenAIGenerationUtil.isValidSignature(
				bytes, company, expirationTime + Time.HOUR,
				AICreatorOpenAIGenerationUtil.sign(
					bytes, company, expirationTime, userId),
				userId));
	}

	@Test
	public void testIsValidSignatureWhenExpirationTimeIsPast()
		throws Exception {

		byte[] bytes = RandomTestUtil.randomBytes();
		Company company = _mockCompany();
		long expirationTime = System.currentTimeMillis() - Time.MINUTE;
		long userId = RandomTestUtil.randomLong();

		Assert.assertFalse(
			AICreatorOpenAIGenerationUtil.isValidSignature(
				bytes, company, expirationTime,
				AICreatorOpenAIGenerationUtil.sign(
					bytes, company, expirationTime, userId),
				userId));
	}

	@Test
	public void testIsValidSignatureWhenSignatureIsNull() throws Exception {
		Assert.assertFalse(
			AICreatorOpenAIGenerationUtil.isValidSignature(
				RandomTestUtil.randomBytes(), _mockCompany(),
				System.currentTimeMillis() + Time.HOUR, null,
				RandomTestUtil.randomLong()));
	}

	@Test
	public void testIsValidSignatureWhenUserIsDifferent() throws Exception {
		byte[] bytes = RandomTestUtil.randomBytes();
		Company company = _mockCompany();
		long expirationTime = System.currentTimeMillis() + Time.HOUR;

		Assert.assertFalse(
			AICreatorOpenAIGenerationUtil.isValidSignature(
				bytes, company, expirationTime,
				AICreatorOpenAIGenerationUtil.sign(
					bytes, company, expirationTime,
					RandomTestUtil.randomLong()),
				RandomTestUtil.randomLong()));
	}

	private Company _mockCompany() {
		Company company = Mockito.mock(Company.class);

		Mockito.when(
			company.getCompanyId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			company.getKeyObj()
		).thenReturn(
			new SecretKeySpec(RandomTestUtil.randomBytes(), "AES")
		);

		return company;
	}

}