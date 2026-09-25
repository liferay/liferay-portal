/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.reports.engine.console.internal.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.reports.engine.console.model.Source;
import com.liferay.portal.security.key.KeyReference;
import com.liferay.portal.security.key.KeyReferenceUtil;
import com.liferay.portal.security.key.secret.SecretResolver;
import com.liferay.portal.security.key.secret.SecretResolverUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Caio Farias
 */
public class SourceDriverPasswordUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_secretResolverSnapshot = ReflectionTestUtil.getAndSetFieldValue(
			SecretResolverUtil.class, "_secretResolverSnapshot",
			new Snapshot<SecretResolver>(
				SecretResolverUtil.class, SecretResolver.class) {

				@Override
				public SecretResolver get() {
					return _secretResolver;
				}

			});
	}

	@After
	public void tearDown() {
		ReflectionTestUtil.setFieldValue(
			SecretResolverUtil.class, "_secretResolverSnapshot",
			_secretResolverSnapshot);
	}

	@Test
	public void testGetDriverPassword() {
		long companyId = RandomTestUtil.randomLong();
		String driverPassword = RandomTestUtil.randomString();
		long sourceId = RandomTestUtil.randomLong();

		_secretResolver = Mockito.mock(SecretResolver.class);

		Mockito.when(
			_secretResolver.resolve(Mockito.eq(companyId), Mockito.anyString())
		).thenReturn(
			driverPassword
		);

		Assert.assertEquals(
			driverPassword,
			SourceDriverPasswordUtil.getDriverPassword(
				companyId, _getKeyReferenceString(companyId, sourceId),
				sourceId));
		Assert.assertEquals(
			driverPassword,
			SourceDriverPasswordUtil.getDriverPassword(
				companyId, driverPassword, sourceId));

		String keyReferenceString = _getKeyReferenceString(
			companyId + 1, sourceId);

		Assert.assertEquals(
			keyReferenceString,
			SourceDriverPasswordUtil.getDriverPassword(
				companyId, keyReferenceString, sourceId));

		keyReferenceString = _getKeyReferenceString(companyId, sourceId + 1);

		Assert.assertEquals(
			keyReferenceString,
			SourceDriverPasswordUtil.getDriverPassword(
				companyId, keyReferenceString, sourceId));
	}

	private String _getKeyReferenceString(long companyId, long sourceId) {
		return KeyReferenceUtil.toKeyReferenceString(
			new KeyReference(
				StringBundler.concat(
					Source.class.getSimpleName(), StringPool.SLASH, companyId,
					StringPool.SLASH, sourceId),
				RandomTestUtil.randomString(), KeyReference.Type.SECRET));
	}

	private SecretResolver _secretResolver;
	private Snapshot<SecretResolver> _secretResolverSnapshot;

}