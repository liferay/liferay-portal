/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.processor;

import com.liferay.source.formatter.SourceFormatterArgs;

import java.util.Arrays;

import org.junit.Test;

/**
 * @author Alan Huang
 */
public class JakartaTransformSourceProcessorTest
	extends BaseSourceProcessorTestCase {

	@Test
	public void testBNDJakartaTransform() throws Exception {
		test("jakartatransform/JakartaTransform.testbnd");
	}

	@Test
	public void testFTLJakartaTransform() throws Exception {
		test("jakartatransform/JakartaTransform.testftl");
	}

	@Test
	public void testGradleJakartaTransform() throws Exception {
		test("jakartatransform/JakartaTransform.testgradle");
	}

	@Test
	public void testJavaJakartaTransform() throws Exception {
		test("jakartatransform/JakartaTransform.testjava");
	}

	@Test
	public void testJSPJakartaTransform() throws Exception {
		test("jakartatransform/JakartaTransform.testjsp");
	}

	@Test
	public void testXMLJakartaTransform() throws Exception {
		test("jakartatransform/JakartaTransform.testxml");
	}

	@Override
	protected SourceFormatterArgs getSourceFormatterArgs() {
		SourceFormatterArgs sourceFormatterArgs =
			super.getSourceFormatterArgs();

		sourceFormatterArgs.setCheckCategoryNames(
			Arrays.asList("JakartaTransform"));

		return sourceFormatterArgs;
	}

}