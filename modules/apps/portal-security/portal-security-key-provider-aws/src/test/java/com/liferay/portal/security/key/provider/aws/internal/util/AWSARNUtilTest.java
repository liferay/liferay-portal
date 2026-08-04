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
				RandomTestUtil.randomString(),
				prefix + "{companyId}/{identifier}", companyId, identifier,
				RandomTestUtil.randomString()));
	}

	@Test
	public void testResolveNullAccountIdWithoutPlaceholder() {
		String identifier = RandomTestUtil.randomString();
		String prefix = RandomTestUtil.randomString() + StringPool.SLASH;

		Assert.assertEquals(
			prefix + identifier,
			AWSARNUtil.resolve(
				null, prefix + "{identifier}", RandomTestUtil.randomLong(),
				identifier, RandomTestUtil.randomString()));
	}

	@Test
	public void testResolveNullTemplateFallsBackToIdentifier() {
		String identifier = RandomTestUtil.randomString();

		Assert.assertEquals(
			identifier,
			AWSARNUtil.resolve(
				RandomTestUtil.randomString(), null,
				RandomTestUtil.randomLong(), identifier,
				RandomTestUtil.randomString()));
	}

	@Test
	public void testResolvePassesThroughAliasIdentifier() {
		String identifier = "alias/" + RandomTestUtil.randomString();

		Assert.assertEquals(
			identifier,
			AWSARNUtil.resolve(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomLong(), identifier,
				RandomTestUtil.randomString()));
	}

	@Test
	public void testResolvePassesThroughARNIdentifier() {
		String identifier = "arn:" + RandomTestUtil.randomString();

		Assert.assertEquals(
			identifier,
			AWSARNUtil.resolve(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomLong(), identifier,
				RandomTestUtil.randomString()));
	}

	@Test
	public void testResolveSystemTemplate() {
		String accountId = RandomTestUtil.randomString();
		String identifier = RandomTestUtil.randomString();
		String prefix = RandomTestUtil.randomString() + StringPool.SLASH;
		String region = RandomTestUtil.randomString();

		Assert.assertEquals(
			StringBundler.concat(
				prefix, region, StringPool.SLASH, accountId, StringPool.SLASH,
				identifier),
			AWSARNUtil.resolve(
				accountId, prefix + "{region}/{accountId}/{identifier}",
				RandomTestUtil.randomLong(), identifier, region));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testResolveThrowsForBlankAccountIdWithPlaceholder() {
		AWSARNUtil.resolve(
			StringPool.BLANK,
			RandomTestUtil.randomString() + "/{accountId}/{identifier}",
			RandomTestUtil.randomLong(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testResolveThrowsForNullAccountIdWithPlaceholder() {
		AWSARNUtil.resolve(
			null, RandomTestUtil.randomString() + "/{accountId}/{identifier}",
			RandomTestUtil.randomLong(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString());
	}

}