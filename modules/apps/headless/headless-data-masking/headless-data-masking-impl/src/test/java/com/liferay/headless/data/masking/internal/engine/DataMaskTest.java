/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.data.masking.internal.engine;

import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Jose Luis Navarro
 */
public class DataMaskTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testApply() {
		Assert.assertEquals(
			"Contact: [EMAIL_ADDRESS]",
			_apply(_emailMask(), "Contact: alice@example.com"));
		Assert.assertEquals(
			"From [EMAIL_ADDRESS] to [EMAIL_ADDRESS]",
			_apply(_emailMask(), "From a@b.com to c@d.org"));
		Assert.assertSame(
			"No email here at all.",
			_apply(_emailMask(), "No email here at all."));
		Assert.assertEquals("", _apply(_emailMask(), ""));
		Assert.assertEquals(
			"Connected from 192.168.1.0/24",
			_apply(
				new DataMask(
					_ipv4DetectionPattern, RandomTestUtil.randomString(),
					_ipv4ReplacementPattern, "$1.0/24"),
				"Connected from 192.168.1.42"));
		Assert.assertEquals(
			"value: [$1\\X]",
			_apply(
				new DataMask(
					_secretDetectionPattern, RandomTestUtil.randomString(),
					null, "[$1\\X]"),
				"value: secret"));
		Assert.assertEquals(
			"Connected from 2001:db8::/48",
			_apply(_ipv6Mask(), "Connected from 2001:db8::1"));
		Assert.assertEquals(
			"Connected from 2001:0db8:85a3::/48",
			_apply(
				_ipv6Mask(),
				"Connected from 2001:0db8:85a3:0000:0000:8a2e:0370:7334"));
	}

	private String _apply(DataMask dataMask, String text) {
		return dataMask.apply(text);
	}

	private DataMask _emailMask() {
		return new DataMask(
			_emailDetectionPattern, RandomTestUtil.randomString(), null,
			"[EMAIL_ADDRESS]");
	}

	private DataMask _ipv6Mask() {
		return new DataMask(
			_ipv6DetectionPattern, RandomTestUtil.randomString(),
			_ipv6ReplacementPattern, "::/48");
	}

	private static final Pattern _emailDetectionPattern = Pattern.compile(
		"[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}");
	private static final Pattern _ipv4DetectionPattern = Pattern.compile(
		"\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\b");
	private static final Pattern _ipv4ReplacementPattern = Pattern.compile(
		"(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})\\.\\d{1,3}");
	private static final Pattern _ipv6DetectionPattern = Pattern.compile(
		"(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}|(?:[0-9a-fA-F]{1,4}:)*" +
			"[0-9a-fA-F]{0,4}::(?:[0-9a-fA-F]{1,4}:)*[0-9a-fA-F]{0,4}");
	private static final Pattern _ipv6ReplacementPattern = Pattern.compile(
		"(::.*$)|(:[0-9a-fA-F]{1,4}(:[0-9a-fA-F]{1,4}){4}$)");
	private static final Pattern _secretDetectionPattern = Pattern.compile(
		"secret");

}