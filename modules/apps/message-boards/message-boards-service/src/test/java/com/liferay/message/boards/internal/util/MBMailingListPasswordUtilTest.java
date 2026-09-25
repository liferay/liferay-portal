/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.internal.util;

import com.liferay.message.boards.model.MBMailingList;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
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
public class MBMailingListPasswordUtilTest {

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
	public void testGetPassword() {
		long categoryId = RandomTestUtil.randomLong();

		long companyId = RandomTestUtil.randomLong();
		String password = RandomTestUtil.randomString();

		_secretResolver = Mockito.mock(SecretResolver.class);

		Mockito.when(
			_secretResolver.resolve(Mockito.eq(companyId), Mockito.anyString())
		).thenReturn(
			password
		);

		Assert.assertEquals(
			password,
			MBMailingListPasswordUtil.getPassword(
				categoryId, companyId, "inPassword",
				_getKeyReferenceString(categoryId, companyId, "inPassword")));
		Assert.assertEquals(
			password,
			MBMailingListPasswordUtil.getPassword(
				categoryId, companyId, "outPassword",
				_getKeyReferenceString(categoryId, companyId, "outPassword")));
		Assert.assertEquals(
			password,
			MBMailingListPasswordUtil.getPassword(
				categoryId, companyId, "inPassword", password));

		String keyReferenceString = _getKeyReferenceString(
			categoryId + 1, companyId, "inPassword");

		Assert.assertEquals(
			keyReferenceString,
			MBMailingListPasswordUtil.getPassword(
				categoryId, companyId, "inPassword", keyReferenceString));

		keyReferenceString = _getKeyReferenceString(
			categoryId, companyId + 1, "inPassword");

		Assert.assertEquals(
			keyReferenceString,
			MBMailingListPasswordUtil.getPassword(
				categoryId, companyId, "inPassword", keyReferenceString));

		keyReferenceString = _getKeyReferenceString(
			categoryId, companyId, "inPassword");

		Assert.assertEquals(
			keyReferenceString,
			MBMailingListPasswordUtil.getPassword(
				categoryId, companyId, "outPassword", keyReferenceString));
	}

	private String _getKeyReferenceString(
		long categoryId, long companyId, String name) {

		return KeyReferenceUtil.toKeyReferenceString(
			new KeyReference(
				StringBundler.concat(
					MBMailingList.class.getSimpleName(), StringPool.SLASH,
					companyId, StringPool.SLASH, categoryId, StringPool.SLASH,
					name),
				RandomTestUtil.randomString(), KeyReference.Type.SECRET));
	}

	private SecretResolver _secretResolver;
	private Snapshot<SecretResolver> _secretResolverSnapshot;

}