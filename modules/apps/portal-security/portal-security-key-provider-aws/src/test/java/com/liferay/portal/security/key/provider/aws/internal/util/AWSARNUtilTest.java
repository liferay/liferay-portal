/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.provider.aws.internal.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Christopher Kian
 */
public class AWSARNUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testResolveCompanyTemplate() {
		long companyId = RandomTestUtil.randomLong();
		String identifier = RandomTestUtil.randomString();
		String prefix = RandomTestUtil.randomString() + StringPool.SLASH;

		Assert.assertEquals(
			StringBundler.concat(
				prefix, companyId, StringPool.SLASH, identifier),
			AWSARNUtil.resolve(
				prefix + "{companyId}/{identifier}",
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				companyId, identifier));
	}

	@Test
	public void testResolveNullAccountIdWithoutPlaceholder() {
		String identifier = RandomTestUtil.randomString();
		String prefix = RandomTestUtil.randomString() + StringPool.SLASH;

		Assert.assertEquals(
			prefix + identifier,
			AWSARNUtil.resolve(
				prefix + "{identifier}", null, RandomTestUtil.randomString(),
				RandomTestUtil.randomLong(), identifier));
	}

	@Test
	public void testResolveNullTemplateFallsBackToIdentifier() {
		String identifier = RandomTestUtil.randomString();

		Assert.assertEquals(
			identifier,
			AWSARNUtil.resolve(
				null, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomLong(),
				identifier));
	}

	@Test
	public void testResolvePassesThroughAliasIdentifier() {
		String identifier = "alias/" + RandomTestUtil.randomString();

		Assert.assertEquals(
			identifier,
			AWSARNUtil.resolve(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomLong(),
				identifier));
	}

	@Test
	public void testResolvePassesThroughARNIdentifier() {
		String identifier = "arn:" + RandomTestUtil.randomString();

		Assert.assertEquals(
			identifier,
			AWSARNUtil.resolve(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomLong(),
				identifier));
	}

	@Test
	public void testResolveSystemTemplate() {
		String awsAccountId = RandomTestUtil.randomString();
		String awsRegion = RandomTestUtil.randomString();
		String identifier = RandomTestUtil.randomString();
		String prefix = RandomTestUtil.randomString() + StringPool.SLASH;

		Assert.assertEquals(
			StringBundler.concat(
				prefix, awsRegion, StringPool.SLASH, awsAccountId,
				StringPool.SLASH, identifier),
			AWSARNUtil.resolve(
				prefix + "{region}/{accountId}/{identifier}", awsAccountId,
				awsRegion, RandomTestUtil.randomLong(), identifier));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testResolveThrowsForBlankAccountIdWithPlaceholder() {
		AWSARNUtil.resolve(
			RandomTestUtil.randomString() + "/{accountId}/{identifier}",
			StringPool.BLANK, RandomTestUtil.randomString(),
			RandomTestUtil.randomLong(), RandomTestUtil.randomString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testResolveThrowsForNullAccountIdWithPlaceholder() {
		AWSARNUtil.resolve(
			RandomTestUtil.randomString() + "/{accountId}/{identifier}", null,
			RandomTestUtil.randomString(), RandomTestUtil.randomLong(),
			RandomTestUtil.randomString());
	}

}