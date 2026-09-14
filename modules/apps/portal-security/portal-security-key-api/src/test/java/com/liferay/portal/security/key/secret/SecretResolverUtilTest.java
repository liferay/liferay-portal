/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.secret;

import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Pedro Victor Silvestre
 */
public class SecretResolverUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_secretResolverSnapshot = ReflectionTestUtil.getFieldValue(
			SecretResolverUtil.class, "_secretResolverSnapshot");
	}

	@After
	public void tearDown() {
		ReflectionTestUtil.setFieldValue(
			SecretResolverUtil.class, "_secretResolverSnapshot",
			_secretResolverSnapshot);
	}

	@Test
	public void testResolve() {
		long companyId = RandomTestUtil.randomLong();
		String resolvedValue = RandomTestUtil.randomString();
		String value = RandomTestUtil.randomString();

		_setUpSecretResolverSnapshot(
			(secretResolverCompanyId, secretResolverValue) -> {
				Assert.assertEquals(companyId, secretResolverCompanyId);
				Assert.assertSame(value, secretResolverValue);

				return resolvedValue;
			});

		Assert.assertSame(
			resolvedValue, SecretResolverUtil.resolve(companyId, value));
	}

	@Test
	public void testResolveWhenSecretResolverIsUnavailable() {
		_setUpSecretResolverSnapshot(null);

		Assert.assertThrows(
			IllegalStateException.class,
			() -> SecretResolverUtil.resolve(
				RandomTestUtil.randomLong(), RandomTestUtil.randomString()));
	}

	private void _setUpSecretResolverSnapshot(SecretResolver secretResolver) {
		ReflectionTestUtil.setFieldValue(
			SecretResolverUtil.class, "_secretResolverSnapshot",
			new Snapshot<SecretResolver>(
				SecretResolverUtil.class, SecretResolver.class) {

				@Override
				public SecretResolver get() {
					return secretResolver;
				}

			});
	}

	private Snapshot<SecretResolver> _secretResolverSnapshot;

}