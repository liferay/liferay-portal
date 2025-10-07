/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.internal.security.access.control;

import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Mariano Álvaro Sáiz
 */
public class AllowedIPAddressesValidatorTest {

	@BeforeClass
	public static void setUpClass() throws Exception {
		_propsUtilMockedStatic.when(
			() -> PropsUtil.get(PropsKeys.DNS_SECURITY_ADDRESS_TIMEOUT_SECONDS)
		).thenReturn(
			String.valueOf(2)
		);

		_propsUtilMockedStatic.when(
			() -> PropsUtil.get(PropsKeys.DNS_SECURITY_THREAD_LIMIT)
		).thenReturn(
			String.valueOf(10)
		);

		_propsUtilMockedStatic.when(
			() -> PropsUtil.get(PropsKeys.DNS_SECURITY_THREAD_QUEUE_LIMIT)
		).thenReturn(
			String.valueOf(5)
		);
	}

	@AfterClass
	public static void tearDownClass() {
		_propsUtilMockedStatic.close();
	}

	@Test
	public void testIPv4AddressDoesNotMatchIPv6Address() {
		AllowedIPAddressesValidator allowedIPAddressesValidator =
			AllowedIPAddressesValidatorFactory.create(_ADDRESS_IP_V4);

		Assert.assertFalse(
			allowedIPAddressesValidator.isAllowedIPAddress(_ADDRESS_IP_V6));
	}

	@Test
	public void testIPv4AddressMatchesIPv4Address() {
		AllowedIPAddressesValidator allowedIPAddressesValidator =
			AllowedIPAddressesValidatorFactory.create(_ADDRESS_IP_V4);

		Assert.assertTrue(
			allowedIPAddressesValidator.isAllowedIPAddress(_ADDRESS_IP_V4));
	}

	@Test
	public void testIPv4CIDRNetmaskValidatesCorrectly() {
		AllowedIPAddressesValidator allowedIPAddressesValidator =
			AllowedIPAddressesValidatorFactory.create("192.168.1.0/24");

		Assert.assertTrue(
			allowedIPAddressesValidator.isAllowedIPAddress(_ADDRESS_IP_V4));

		allowedIPAddressesValidator = AllowedIPAddressesValidatorFactory.create(
			"192.168.1.128/25");

		Assert.assertFalse(
			allowedIPAddressesValidator.isAllowedIPAddress(_ADDRESS_IP_V4));

		Assert.assertTrue(
			allowedIPAddressesValidator.isAllowedIPAddress("192.168.1.159"));
	}

	@Test
	public void testIPv4DotNotationNetmaskValidatesCorrectly() {
		AllowedIPAddressesValidator allowedIPAddressesValidator =
			AllowedIPAddressesValidatorFactory.create(
				"192.168.1.0/255.255.255.0");

		Assert.assertTrue(
			allowedIPAddressesValidator.isAllowedIPAddress(_ADDRESS_IP_V4));

		allowedIPAddressesValidator = AllowedIPAddressesValidatorFactory.create(
			"192.168.1.128/255.255.255.128");

		Assert.assertFalse(
			allowedIPAddressesValidator.isAllowedIPAddress(_ADDRESS_IP_V4));

		Assert.assertTrue(
			allowedIPAddressesValidator.isAllowedIPAddress("192.168.1.159"));
	}

	@Test
	public void testIPv4InvalidConfigurationInvalidatesEverything() {
		AllowedIPAddressesValidator invalidIPAddressesValidator =
			AllowedIPAddressesValidatorFactory.create("192.168.0/24");

		Assert.assertFalse(
			invalidIPAddressesValidator.isAllowedIPAddress(_ADDRESS_IP_V4));

		AllowedIPAddressesValidator invalidNetmaskValidator =
			AllowedIPAddressesValidatorFactory.create("192.168.1.0/33");

		Assert.assertFalse(
			invalidNetmaskValidator.isAllowedIPAddress(_ADDRESS_IP_V4));
	}

	@Test
	public void testIPv6AddressDoesNotMatchIPv4Address() {
		AllowedIPAddressesValidator allowedIPAddressesValidator =
			AllowedIPAddressesValidatorFactory.create(_ADDRESS_IP_V6);

		Assert.assertFalse(
			allowedIPAddressesValidator.isAllowedIPAddress(_ADDRESS_IP_V4));
	}

	@Test
	public void testIPv6AddressMatchesIPv6Address() {
		AllowedIPAddressesValidator allowedIPAddressesValidator =
			AllowedIPAddressesValidatorFactory.create(_ADDRESS_IP_V6);

		Assert.assertTrue(
			allowedIPAddressesValidator.isAllowedIPAddress(_ADDRESS_IP_V6));
	}

	@Test
	public void testIPv6CIDRNetmaskValidatesCorrectly() {
		AllowedIPAddressesValidator allowedIPAddressesValidator =
			AllowedIPAddressesValidatorFactory.create("2001:AB9::/48");

		Assert.assertTrue(
			allowedIPAddressesValidator.isAllowedIPAddress(
				"2001:AB9:0:0:0:0:0:0"));

		Assert.assertTrue(
			allowedIPAddressesValidator.isAllowedIPAddress(
				"2001:AB9:0:0:0:0:0:1"));

		Assert.assertTrue(
			allowedIPAddressesValidator.isAllowedIPAddress(
				"2001:AB9:0:FFFF:FFFF:FFFF:FFFF:FFFF"));

		Assert.assertFalse(
			allowedIPAddressesValidator.isAllowedIPAddress(
				"2001:AB9:1:0:0:0:0:0"));
	}

	@Test
	public void testIPv6InvalidConfigurationInvalidatesEverything() {
		AllowedIPAddressesValidator invalidIPAddressesValidator =
			AllowedIPAddressesValidatorFactory.create("2001:AB9::/48");

		Assert.assertFalse(
			invalidIPAddressesValidator.isAllowedIPAddress(_ADDRESS_IP_V6));

		AllowedIPAddressesValidator invalidNetmaskValidator =
			AllowedIPAddressesValidatorFactory.create("2001:DB8::/130");

		Assert.assertFalse(
			invalidNetmaskValidator.isAllowedIPAddress(_ADDRESS_IP_V6));
	}

	@Test
	public void testZeroNetmaskValidatesEveryIP() {
		AllowedIPAddressesValidator allowedIPAddressesValidator =
			AllowedIPAddressesValidatorFactory.create("0.0.0.0/0");

		Assert.assertTrue(
			allowedIPAddressesValidator.isAllowedIPAddress("1.2.3.4"));

		Assert.assertTrue(
			allowedIPAddressesValidator.isAllowedIPAddress("192.168.0.159"));

		allowedIPAddressesValidator = AllowedIPAddressesValidatorFactory.create(
			"192.168.0.159/0");

		Assert.assertTrue(
			allowedIPAddressesValidator.isAllowedIPAddress("1.2.3.4"));

		Assert.assertTrue(
			allowedIPAddressesValidator.isAllowedIPAddress("192.168.0.159"));
	}

	private static final String _ADDRESS_IP_V4 = "192.168.1.104";

	private static final String _ADDRESS_IP_V6 =
		"2001:AC8:1234:0000:0000:C1C0:ABCD:0876";

	private static final MockedStatic<PropsUtil> _propsUtilMockedStatic =
		Mockito.mockStatic(PropsUtil.class);

}