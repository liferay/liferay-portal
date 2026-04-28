/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.ldap.internal.ssl;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.net.SocketFactory;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Jorge García Jiménez
 */
public class FIPSLDAPSSLSocketFactoryTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@After
	public void tearDown() {
		FIPSLDAPSSLSocketFactory.setCipherSuitesOverride(null);
	}

	@Test
	public void testAllowlistIsOnlyAEADSuites() {
		for (String suite :
				(String[])ReflectionTestUtil.getFieldValue(
					FIPSLDAPSSLSocketFactory.class,
					"_FIPS_CIPHER_SUITES_ALLOWLIST")) {

			Assert.assertTrue(
				"Expected AEAD (GCM) suite, got " + suite,
				suite.contains("_GCM_"));
		}
	}

	@Test
	public void testEnabledProtocolsAreTLS12AndTLS13() {
		Set<String> protocols = new HashSet<>(
			Arrays.asList(
				(String[])ReflectionTestUtil.getFieldValue(
					FIPSLDAPSSLSocketFactory.class, "_ENABLED_PROTOCOLS")));

		Assert.assertEquals(protocols.toString(), 2, protocols.size());
		Assert.assertTrue(protocols.contains("TLSv1.2"));
		Assert.assertTrue(protocols.contains("TLSv1.3"));
	}

	@Test
	public void testGetDefaultReturnsSameSingleton() {
		SocketFactory first = FIPSLDAPSSLSocketFactory.getDefault();
		SocketFactory second = FIPSLDAPSSLSocketFactory.getDefault();

		Assert.assertSame(first, second);
	}

	@Test
	public void testIntersectKeepsOnlySupportedDesiredValues() {
		String[] desired = {"A", "B", "C"};
		String[] supported = {"B", "D", "A"};

		String[] intersection = ReflectionTestUtil.invoke(
			FIPSLDAPSSLSocketFactory.getDefault(), "_intersect",
			new Class<?>[] {String[].class, String[].class}, desired,
			supported);

		Assert.assertArrayEquals(new String[] {"A", "B"}, intersection);
	}

	@Test
	public void testIntersectPreservesDesiredOrder() {
		String[] desired = {"C", "B", "A"};
		String[] supported = {"A", "B", "C"};

		String[] intersection = ReflectionTestUtil.invoke(
			FIPSLDAPSSLSocketFactory.getDefault(), "_intersect",
			new Class<?>[] {String[].class, String[].class}, desired,
			supported);

		Assert.assertArrayEquals(new String[] {"C", "B", "A"}, intersection);
	}

	@Test
	public void testIntersectReturnsEmptyWhenNoneMatch() {
		String[] desired = {"A", "B"};
		String[] supported = {"X", "Y"};

		String[] intersection = ReflectionTestUtil.invoke(
			FIPSLDAPSSLSocketFactory.getDefault(), "_intersect",
			new Class<?>[] {String[].class, String[].class}, desired,
			supported);

		Assert.assertEquals(
			Arrays.toString(intersection), 0, intersection.length);
	}

	@Test
	public void testSetCipherSuitesOverrideClearsOnNullOrEmpty() {
		String[] override = {"TLS_AES_256_GCM_SHA384"};

		FIPSLDAPSSLSocketFactory.setCipherSuitesOverride(override);

		FIPSLDAPSSLSocketFactory.setCipherSuitesOverride(null);

		FIPSLDAPSSLSocketFactory.setCipherSuitesOverride(new String[0]);
	}

}