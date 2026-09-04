/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.service.access.policy.internal;

import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Mika Koivisto
 */
public class SAPAccessControlPolicyTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_sapAccessControlPolicy = new SAPAccessControlPolicy();
	}

	@Test
	public void testMatches() {
		Class<?>[] parameterTypes = {long.class};

		Assert.assertTrue(
			_sapAccessControlPolicy.matches(
				"*", "com.liferay.portal.kernel.service.UserService",
				"getUserById", parameterTypes));
		Assert.assertTrue(
			_sapAccessControlPolicy.matches(
				"com.liferay.portal.kernel.service.*",
				"com.liferay.portal.kernel.service.UserService", "getUserById",
				parameterTypes));
		Assert.assertTrue(
			_sapAccessControlPolicy.matches(
				"com.liferay.portal.kernel.service.UserService",
				"com.liferay.portal.kernel.service.UserService", "getUserById",
				parameterTypes));
		Assert.assertTrue(
			_sapAccessControlPolicy.matches(
				"com.liferay.portal.kernel.service.UserService#getUserById",
				"com.liferay.portal.kernel.service.UserService", "getUserById",
				parameterTypes));
		Assert.assertTrue(
			_sapAccessControlPolicy.matches(
				"com.liferay.portal.kernel.service.UserService#get*",
				"com.liferay.portal.kernel.service.UserService", "getUserById",
				parameterTypes));
		Assert.assertTrue(
			_sapAccessControlPolicy.matches(
				"com.liferay.portal.kernel.service.*#get*",
				"com.liferay.portal.kernel.service.UserService", "getUserById",
				parameterTypes));
		Assert.assertTrue(
			_sapAccessControlPolicy.matches(
				"#get*", "com.liferay.portal.kernel.service.UserService",
				"getUserById", parameterTypes));
		Assert.assertFalse(
			_sapAccessControlPolicy.matches(
				"com.liferay.portlet.*#get*",
				"com.liferay.portal.kernel.service.UserService", "getUserById",
				parameterTypes));
		Assert.assertFalse(
			_sapAccessControlPolicy.matches(
				"com.liferay.portal.service.*#update*",
				"com.liferay.portal.kernel.service.UserService", "getUserById",
				parameterTypes));
	}

	@Test
	public void testMatchesWithParameterTypes() {
		String className = "com.liferay.portal.kernel.service.UserService";

		Assert.assertTrue(
			_sapAccessControlPolicy.matches(
				className + "#addUser(long, java.lang.String)", className,
				"addUser", new Class<?>[] {long.class, String.class}));
		Assert.assertTrue(
			_sapAccessControlPolicy.matches(
				className + "#getGuestUser()", className, "getGuestUser",
				new Class<?>[0]));
		Assert.assertFalse(
			_sapAccessControlPolicy.matches(
				className + "#getGuestUser()", className, "getGuestUser",
				new Class<?>[] {long.class}));
		Assert.assertTrue(
			_sapAccessControlPolicy.matches(
				className + "#getUserById", className, "getUserById",
				new Class<?>[] {long.class}));
		Assert.assertTrue(
			_sapAccessControlPolicy.matches(
				className + "#getUserById", className, "getUserById",
				new Class<?>[] {String.class}));
		Assert.assertTrue(
			_sapAccessControlPolicy.matches(
				className + "#getUserById(long)", className, "getUserById",
				new Class<?>[] {long.class}));
		Assert.assertFalse(
			_sapAccessControlPolicy.matches(
				className + "#getUserById(long)", className, "getUserById",
				new Class<?>[] {String.class}));
		Assert.assertFalse(
			_sapAccessControlPolicy.matches(
				className + "#getUserById(long)", className, "getUserById",
				new Class<?>[] {long.class, String.class}));
	}

	private SAPAccessControlPolicy _sapAccessControlPolicy;

}