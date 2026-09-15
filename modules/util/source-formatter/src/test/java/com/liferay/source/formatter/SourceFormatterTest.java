/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter;

import com.liferay.portal.kernel.test.rule.TimeoutTestRule;

import java.lang.reflect.Method;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

/**
 * @author Hugo Huijser
 */
public class SourceFormatterTest {

	@Test
	public void testFileNameWithIncorrectExtension() throws Exception {
		SourceFormatterArgs sourceFormatterArgs = new SourceFormatterArgs();

		sourceFormatterArgs.setAutoFix(false);
		sourceFormatterArgs.setFailOnAutoFix(false);
		sourceFormatterArgs.setFailOnHasWarning(false);
		sourceFormatterArgs.setPrintErrors(false);

		String fileName =
			"src/test/resources/com/liferay/source/formatter/dependencies" +
				"/wrong.foo";

		sourceFormatterArgs.setFileNames(Collections.singletonList(fileName));

		SourceFormatter sourceFormatter = new SourceFormatter(
			sourceFormatterArgs);

		sourceFormatter.format();

		List<String> modifiedFileNames = sourceFormatter.getModifiedFileNames();

		Assert.assertTrue(
			modifiedFileNames.toString(), modifiedFileNames.isEmpty());
	}

	@Test
	public void testGetNestedUnsupportedClassVersionErrorWhenAbsent()
		throws Exception {

		Assert.assertNull(
			_getNestedUnsupportedClassVersionError(
				new Exception("Found 1 formatting issue")));
	}

	@Test
	public void testGetNestedUnsupportedClassVersionErrorWhenNested()
		throws Exception {

		UnsupportedClassVersionError unsupportedClassVersionError =
			new UnsupportedClassVersionError(
				"org/slf4j/impl/StaticLoggerBinder has been compiled by a " +
					"more recent version of the Java Runtime");

		Assert.assertSame(
			unsupportedClassVersionError,
			_getNestedUnsupportedClassVersionError(
				new ExecutionException(
					new ExecutionException(
						new RuntimeException(
							"Unable to format Test.macro",
							unsupportedClassVersionError)))));
	}

	@Rule
	public final TestRule testRule = TimeoutTestRule.INSTANCE;

	private UnsupportedClassVersionError _getNestedUnsupportedClassVersionError(
			Throwable throwable)
		throws Exception {

		Method method = SourceFormatter.class.getDeclaredMethod(
			"_getNestedUnsupportedClassVersionError", Throwable.class);

		method.setAccessible(true);

		return (UnsupportedClassVersionError)method.invoke(null, throwable);
	}

}