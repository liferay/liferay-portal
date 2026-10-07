/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.secret;

import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.security.key.KeyReference;
import com.liferay.portal.security.key.KeyReferenceUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

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
		String value = _randomKeyReferenceString();

		SecretResolver secretResolver = Mockito.mock(SecretResolver.class);

		Mockito.when(
			secretResolver.resolve(companyId, value)
		).thenReturn(
			resolvedValue
		);

		_setUpSecretResolverSnapshot(secretResolver);

		Assert.assertSame(
			resolvedValue, SecretResolverUtil.resolve(companyId, value));

		KeyReference keyReference = KeyReferenceUtil.parseKeyReference(value);

		Assert.assertSame(
			resolvedValue,
			SecretResolverUtil.resolve(
				companyId, keyReference.getIdentifier(), value));

		Assert.assertSame(
			value,
			SecretResolverUtil.resolve(
				companyId, RandomTestUtil.randomString(), value));

		String plainValue = RandomTestUtil.randomString();

		Assert.assertSame(
			plainValue, SecretResolverUtil.resolve(companyId, plainValue));
		Assert.assertSame(
			plainValue,
			SecretResolverUtil.resolve(
				companyId, RandomTestUtil.randomString(), plainValue));

		Assert.assertNull(
			SecretResolverUtil.resolve(
				companyId, RandomTestUtil.randomString(), null));

		Mockito.verify(
			secretResolver, Mockito.never()
		).resolve(
			companyId, plainValue
		);
	}

	@Test
	public void testResolveWhenSecretResolverIsUnavailable() {
		_setUpSecretResolverSnapshot(null);

		Assert.assertThrows(
			IllegalStateException.class,
			() -> SecretResolverUtil.resolve(
				RandomTestUtil.randomLong(), _randomKeyReferenceString()));

		String plainValue = RandomTestUtil.randomString();

		Assert.assertSame(
			plainValue,
			SecretResolverUtil.resolve(
				RandomTestUtil.randomLong(), plainValue));

		Assert.assertNull(
			SecretResolverUtil.resolve(RandomTestUtil.randomLong(), null));
	}

	private String _randomKeyReferenceString() {
		return KeyReferenceUtil.toKeyReferenceString(
			new KeyReference(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				KeyReference.Type.SECRET));
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