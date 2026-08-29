/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.internal.http;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.IOException;

import java.net.SocketTimeoutException;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class DSHttpTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetMaxRetries() {
		Assert.assertEquals(0, _getMaxRetries(-1));
		Assert.assertEquals(0, _getMaxRetries(0));
		Assert.assertEquals(0, _getMaxRetries(19999));
		Assert.assertEquals(0, _getMaxRetries(20000));
		Assert.assertEquals(0, _getMaxRetries(39999));
		Assert.assertEquals(1, _getMaxRetries(40000));
		Assert.assertEquals(1, _getMaxRetries(59999));
		Assert.assertEquals(2, _getMaxRetries(60000));
		Assert.assertEquals(2, _getMaxRetries(Integer.MAX_VALUE));
	}

	@Test
	public void testIsSocketTimeout() {
		Assert.assertTrue(
			"A socket timeout is retryable",
			_isSocketTimeout(new SocketTimeoutException()));
		Assert.assertTrue(
			"A wrapped socket timeout is retryable",
			_isSocketTimeout(
				new IOException(
					"Wrapper",
					new IOException("Inner", new SocketTimeoutException()))));
	}

	@Test
	public void testIsSocketTimeoutIgnoresOtherFailures() {
		Assert.assertFalse(
			"A null throwable is not a socket timeout", _isSocketTimeout(null));
		Assert.assertFalse(
			"An unrelated cause chain is not a socket timeout",
			_isSocketTimeout(
				new IOException("Wrapper", new IllegalStateException())));
		Assert.assertFalse(
			"An unrelated failure is not a socket timeout",
			_isSocketTimeout(new IOException("Host name may not be null")));
	}

	private int _getMaxRetries(int httpTimeout) {
		return ReflectionTestUtil.invoke(
			_dsHttp, "_getMaxRetries", new Class<?>[] {int.class}, httpTimeout);
	}

	private boolean _isSocketTimeout(Throwable throwable) {
		return ReflectionTestUtil.invoke(
			_dsHttp, "_isSocketTimeout", new Class<?>[] {Throwable.class},
			throwable);
	}

	private final DSHttp _dsHttp = new DSHttp();

}