/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.util;

import java.io.IOException;

import java.util.concurrent.ExecutionException;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Calum Ragan
 */
public class ThrowableUtilTest {

	@Test(timeout = 5000)
	public void testGetNestedThrowableWhenCyclic() {
		Exception exception1 = new Exception();

		Exception exception2 = new Exception(exception1);

		exception1.initCause(exception2);

		Assert.assertNull(
			ThrowableUtil.getNestedThrowable(
				exception1, UnsupportedClassVersionError.class));
	}

	@Test
	public void testGetNestedThrowableWhenNested() {
		UnsupportedClassVersionError unsupportedClassVersionError =
			new UnsupportedClassVersionError(
				"org/slf4j/impl/StaticLoggerBinder has been compiled by a " +
					"more recent version of the Java Runtime");

		Assert.assertSame(
			unsupportedClassVersionError,
			ThrowableUtil.getNestedThrowable(
				new ExecutionException(
					new ExecutionException(
						new RuntimeException(
							"Unable to format Test.macro",
							unsupportedClassVersionError))),
				UnsupportedClassVersionError.class));
	}

	@Test
	public void testGetNestedThrowableWhenNoCause() {
		Assert.assertNull(
			ThrowableUtil.getNestedThrowable(
				new Exception("Found 1 formatting issue"),
				UnsupportedClassVersionError.class));
	}

	@Test
	public void testGetNestedThrowableWhenNoMatch() {
		Assert.assertNull(
			ThrowableUtil.getNestedThrowable(
				new ExecutionException(
					new ExecutionException(
						new RuntimeException(
							"Unable to format Test.java",
							new IOException("Read failed")))),
				UnsupportedClassVersionError.class));
	}

	@Test
	public void testGetNestedThrowableWhenSelf() {
		UnsupportedClassVersionError unsupportedClassVersionError =
			new UnsupportedClassVersionError("Bad class file version");

		Assert.assertSame(
			unsupportedClassVersionError,
			ThrowableUtil.getNestedThrowable(
				unsupportedClassVersionError,
				UnsupportedClassVersionError.class));
	}

}