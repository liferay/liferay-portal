/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.internal.company;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.encryptor.CompanyKeyResolverUtil;
import com.liferay.portal.kernel.exception.CompanyKeyResolutionException;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.security.key.KeyReference;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Christopher Kian
 */
public class WrappedCompanyKeyTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testConstructor() {
		_testConstructor(_CIPHERTEXT, null);
		_testConstructor(
			_CIPHERTEXT, "alias/ke|k", RandomTestUtil.randomString());
		_testConstructor(
			_CIPHERTEXT, "alias/ke}k", RandomTestUtil.randomString());
		_testConstructor(
			_CIPHERTEXT, RandomTestUtil.randomString(), "provider:id");
		_testConstructor(
			_CIPHERTEXT, RandomTestUtil.randomString(), "provider|id");
		_testConstructor(
			_CIPHERTEXT, RandomTestUtil.randomString(), "provider}id");
		_testConstructor(_CIPHERTEXT, RandomTestUtil.randomString(), null);
		_testConstructor(_CIPHERTEXT, null, RandomTestUtil.randomString());
		_testConstructor(
			null, RandomTestUtil.randomString(), RandomTestUtil.randomString());
	}

	@Test
	public void testParse() {
		String identifier = "arn:aws:kms:us-east-1:123456789012:key/abc";
		String providerId = RandomTestUtil.randomString();

		WrappedCompanyKey wrappedCompanyKey = new WrappedCompanyKey(
			_CIPHERTEXT,
			new KeyReference(identifier, providerId, KeyReference.Type.CRYPTO));

		WrappedCompanyKey parsedWrappedCompanyKey = WrappedCompanyKey.parse(
			_COMPANY_ID, wrappedCompanyKey.toWrappedKey());

		Assert.assertArrayEquals(
			_CIPHERTEXT, parsedWrappedCompanyKey.getCiphertext());

		KeyReference keyReference = parsedWrappedCompanyKey.getKeyReference();

		Assert.assertEquals(identifier, keyReference.getIdentifier());
		Assert.assertEquals(providerId, keyReference.getProviderId());

		_testParse(_VERSION_PREFIX);
		_testParse(_VERSION_PREFIX + ":alias/kek|Y2lwaGVy}");
		_testParse(_VERSION_PREFIX + "provider:alias/kek|=}");
		_testParse(_VERSION_PREFIX + "provider:alias/kek|Y2lwaGVy");
		_testParse(_VERSION_PREFIX + "provider:alias/kek|not base64}");
		_testParse(_VERSION_PREFIX + "provider:alias/kek|}");
		_testParse(_VERSION_PREFIX + "provider:alias/kek}");
		_testParse(_VERSION_PREFIX + "provider:ali}as|Y2lwaGVy}");
		_testParse(_VERSION_PREFIX + "provider:|Y2lwaGVy}");
		_testParse(_VERSION_PREFIX + "pro}vider:alias|Y2lwaGVy}");
		_testParse(CompanyKeyResolverUtil.WRAPPED_KEY_PREFIX);
		_testParse(CompanyKeyResolverUtil.WRAPPED_KEY_PREFIX + "}");
		_testParse(
			CompanyKeyResolverUtil.WRAPPED_KEY_PREFIX +
				"v2:provider:alias/kek|Y2lwaGVy}");
		_testParse(RandomTestUtil.randomString());
	}

	@Test
	public void testToWrappedKey() {
		String identifier = RandomTestUtil.randomString();
		String providerId = RandomTestUtil.randomString();

		WrappedCompanyKey wrappedCompanyKey = new WrappedCompanyKey(
			_CIPHERTEXT,
			new KeyReference(identifier, providerId, KeyReference.Type.CRYPTO));

		String wrappedKey = wrappedCompanyKey.toWrappedKey();

		Assert.assertTrue(CompanyKeyResolverUtil.isWrappedKey(wrappedKey));

		WrappedCompanyKey parsedWrappedCompanyKey = WrappedCompanyKey.parse(
			_COMPANY_ID, wrappedKey);

		Assert.assertArrayEquals(
			_CIPHERTEXT, parsedWrappedCompanyKey.getCiphertext());

		KeyReference keyReference = parsedWrappedCompanyKey.getKeyReference();

		Assert.assertEquals(identifier, keyReference.getIdentifier());
		Assert.assertEquals(providerId, keyReference.getProviderId());

		Assert.assertEquals(wrappedKey, parsedWrappedCompanyKey.toWrappedKey());
	}

	private void _testConstructor(
		byte[] ciphertext, KeyReference keyReference) {

		try {
			new WrappedCompanyKey(ciphertext, keyReference);

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
		}
	}

	private void _testConstructor(
		byte[] ciphertext, String identifier, String providerId) {

		try {
			new WrappedCompanyKey(
				ciphertext,
				new KeyReference(
					identifier, providerId, KeyReference.Type.CRYPTO));

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
		}
	}

	private void _testParse(String wrappedKey) {
		try {
			WrappedCompanyKey.parse(_COMPANY_ID, wrappedKey);

			Assert.fail();
		}
		catch (CompanyKeyResolutionException companyKeyResolutionException) {
		}
	}

	private static final byte[] _CIPHERTEXT = RandomTestUtil.randomBytes();

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final String _VERSION_PREFIX =
		CompanyKeyResolverUtil.WRAPPED_KEY_PREFIX +
			CompanyKeyResolverUtil.WRAPPED_KEY_VERSION + StringPool.COLON;

}