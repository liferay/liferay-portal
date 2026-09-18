/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.internal.web.cache;

import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.security.key.secret.SecretResolver;
import com.liferay.portal.security.key.secret.SecretResolverUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class DSAccessTokenWebCacheItemTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_secretResolverSnapshot = ReflectionTestUtil.getFieldValue(
			SecretResolverUtil.class, "_secretResolverSnapshot");

		ReflectionTestUtil.setFieldValue(
			SecretResolverUtil.class, "_secretResolverSnapshot",
			new Snapshot<SecretResolver>(
				SecretResolverUtil.class, SecretResolver.class) {

				@Override
				public SecretResolver get() {
					return (companyId, value) -> value;
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
	public void testGetPEMDoubleEscapedLineBreaks() {
		Assert.assertEquals(
			_PEM,
			_getPEM(
				"-----BEGIN RSA PRIVATE KEY-----\\\\nAAAABBBBCCCC\\\\n" +
					"DDDDEEEEFFFF\\\\n-----END RSA PRIVATE KEY-----"));
	}

	@Test
	public void testGetPEMEscapedLineBreaks() {
		Assert.assertEquals(
			_PEM,
			_getPEM(
				"-----BEGIN RSA PRIVATE KEY-----\\nAAAABBBBCCCC\\n" +
					"DDDDEEEEFFFF\\n-----END RSA PRIVATE KEY-----"));
	}

	@Test
	public void testGetPEMGluedBeginMarker() {
		Assert.assertEquals(
			_PEM,
			_getPEM(
				"-----BEGIN RSA PRIVATE KEY-----AAAABBBBCCCC\n" +
					"DDDDEEEEFFFF\n-----END RSA PRIVATE KEY-----"));
	}

	@Test
	public void testGetPEMIsUnchanged() {
		Assert.assertEquals(_PEM, _getPEM(_PEM));
	}

	@Test
	public void testGetPEMNullKey() {
		Assert.assertEquals("", _getPEM(null));
	}

	private String _getPEM(String rsaPrivateKey) {
		DSAccessTokenWebCacheItem dsAccessTokenWebCacheItem =
			new DSAccessTokenWebCacheItem(
				"api-username", CompanyConstants.SYSTEM, "sandbox",
				"integration-key", rsaPrivateKey);

		byte[] rsaPrivateKeyBytes = ReflectionTestUtil.getFieldValue(
			dsAccessTokenWebCacheItem, "_rsaPrivateKeyBytes");

		return new String(rsaPrivateKeyBytes);
	}

	private static final String _PEM =
		"-----BEGIN RSA PRIVATE KEY-----\nAAAABBBBCCCC\nDDDDEEEEFFFF" +
			"\n-----END RSA PRIVATE KEY-----";

	private Snapshot<SecretResolver> _secretResolverSnapshot;

}