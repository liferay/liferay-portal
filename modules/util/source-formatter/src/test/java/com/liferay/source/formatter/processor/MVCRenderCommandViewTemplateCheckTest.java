/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.processor;

import com.liferay.source.formatter.SourceFormatterArgs;

import java.util.Arrays;

import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class MVCRenderCommandViewTemplateCheckTest
	extends BaseSourceProcessorTestCase {

	@Test
	public void testOtherName() throws Exception {
		test("MVCViewNonRoot.testjava");
	}

	@Test
	public void testRootAndName() throws Exception {
		test("MVCViewRootAndName.testjava", _MESSAGE, 21);
	}

	@Test
	public void testRootOnly() throws Exception {
		test("MVCViewRootOnly.testjava", _MESSAGE, 20);
	}

	@Test
	public void testRootWithWork() throws Exception {
		test("MVCViewRootWithWork.testjava");
	}

	@Override
	protected SourceFormatterArgs getSourceFormatterArgs() {
		SourceFormatterArgs sourceFormatterArgs =
			super.getSourceFormatterArgs();

		sourceFormatterArgs.setCheckNames(
			Arrays.asList("MVCRenderCommandViewTemplateCheck"));

		return sourceFormatterArgs;
	}

	private static final String _MESSAGE =
		"The MVC render command only maps \"/\" to \"/view.jsp\"; set " +
			"\"jakarta.portlet.init-param.view-template=/view.jsp\" on the " +
				"portlet and remove the command";

}