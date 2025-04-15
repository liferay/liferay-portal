/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;

import jakarta.servlet.http.Cookie;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class CookieUtilTest {

	@ClassRule
	public static final CodeCoverageAssertor codeCoverageAssertor =
		CodeCoverageAssertor.INSTANCE;

	@Test
	public void testConstructor() {
		new CookieUtil();
	}

	@Test
	public void testEquals() {

		// Domain

		Cookie cookie1 = new Cookie("name", null);

		cookie1.setDomain("domain");

		Cookie cookie2 = new Cookie("name2", null);

		cookie2.setDomain("domain2");

		Assert.assertFalse(CookieUtil.equals(cookie1, cookie2));

		cookie2.setDomain("domain");

		// Max age

		cookie1.setMaxAge(1);
		cookie2.setMaxAge(2);

		Assert.assertFalse(CookieUtil.equals(cookie1, cookie2));

		cookie2.setMaxAge(1);

		// Name

		Assert.assertFalse(CookieUtil.equals(cookie1, cookie2));

		cookie2 = new Cookie("name", null);

		cookie2.setDomain("domain");
		cookie2.setMaxAge(1);

		// Path

		cookie1.setPath("path");
		cookie2.setPath("path2");

		Assert.assertFalse(CookieUtil.equals(cookie1, cookie2));

		cookie2.setPath("path");

		// Secure

		cookie1.setSecure(true);
		cookie2.setSecure(false);

		Assert.assertFalse(CookieUtil.equals(cookie1, cookie2));

		cookie2.setSecure(true);

		// Value

		cookie1.setValue("value");
		cookie2.setValue("value2");

		Assert.assertFalse(CookieUtil.equals(cookie1, cookie2));

		cookie2.setValue("value");

		// HTTP only

		cookie1.setHttpOnly(true);
		cookie2.setHttpOnly(false);

		Assert.assertFalse(CookieUtil.equals(cookie1, cookie2));

		cookie2.setHttpOnly(true);

		// Equals

		Assert.assertTrue(CookieUtil.equals(cookie1, cookie2));
	}

	@Test
	public void testSerializationAndDeserialization() {
		Cookie cookie1 = new Cookie("name1", null);

		byte[] bytes = CookieUtil.serialize(cookie1);

		Assert.assertTrue(
			CookieUtil.equals(cookie1, CookieUtil.deserialize(bytes)));

		Cookie cookie2 = new Cookie("name2", "value");

		cookie2.setDomain("domain");
		cookie2.setHttpOnly(true);
		cookie2.setMaxAge(1);
		cookie2.setPath("path");
		cookie2.setSecure(true);

		bytes = CookieUtil.serialize(cookie2);

		Assert.assertTrue(
			CookieUtil.equals(cookie2, CookieUtil.deserialize(bytes)));
	}

	@Test
	public void testToString() {
		Cookie cookie = new Cookie("name", "value");

		cookie.setDomain("domain");
		cookie.setHttpOnly(true);
		cookie.setMaxAge(1);
		cookie.setPath("path");
		cookie.setSecure(true);

		Assert.assertEquals(
			"{domain=domain, httpOnly=true, maxAge=1, name=name, path=path, " +
				"secure=true, value=value}",
			CookieUtil.toString(cookie));
	}

}