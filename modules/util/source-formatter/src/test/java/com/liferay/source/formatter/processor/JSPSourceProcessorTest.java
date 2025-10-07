/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.processor;

import org.junit.Test;

/**
 * @author André de Oliveira
 */
public class JSPSourceProcessorTest extends BaseSourceProcessorTestCase {

	@Test
	public void testCombineJavaSourceBlocks() throws Exception {
		test(
			"CombineJavaSourceBlocks1.testjsp",
			"\"<%!...%>\" block should come after <%...%> block", 10);
		test(
			"CombineJavaSourceBlocks2.testjsp",
			"Combine <%!...%> blocks at line \"20\" and \"25\"");
	}

	@Test
	public void testFormatBooleanScriptlet() throws Exception {
		test("FormatBooleanScriptlet.testjsp");
	}

	@Test
	public void testFormatImportsAndTaglibs() throws Exception {
		test("FormatImportsAndTaglibs.testjsp");
	}

	@Test
	public void testFormatJSPExpressionTag() throws Exception {
		test("FormatJSPExpressionTag.testjsp");
	}

	@Test
	public void testFormatSelfClosingTags() throws Exception {
		test("FormatSelfClosingTags.testjsp");
	}

	@Test
	public void testFormatTagAttributes() throws Exception {
		test("FormatTagAttributes.testjsp");
	}

	@Test
	public void testFormatTaglibs() throws Exception {
		test("FormatTaglibs.testjsp");
	}

	@Test
	public void testFormatTagLineBreaks() throws Exception {
		test("FormatTagLineBreaks.testjsp");
	}

	@Test
	public void testGetStaticResourceURL() throws Exception {
		test("GetStaticResourceURL.testjsp");
	}

	@Test
	public void testIncorrectEmptyLine() throws Exception {
		test("IncorrectEmptyLine.testjsp");
	}

	@Test
	public void testIncorrectIndentation() throws Exception {
		test("IncorrectIndentation.testjsp");
	}

	@Test
	public void testIncorrectMethodCalls() throws Exception {
		test(
			SourceProcessorTestParameters.create(
				"IncorrectMethodCalls.testjsp"
			).addExpectedMessage(
				"Use type \"LiferayPortletResponse\" to call " +
					"\"getNamespace()\"",
				12
			).addExpectedMessage(
				"Use type \"LiferayPortletResponse\" to call " +
					"\"getNamespace()\"",
				19
			));
	}

	@Test
	public void testLanguageUtilCall() throws Exception {
		test(
			"LanguageUtilCall.testjsp",
			"Use <liferay-ui:message> tag instead of LanguageUtil.get", 8);
	}

	@Test
	public void testMisplacedImport() throws Exception {
		test("MisplacedImport.testjsp", "Move imports to init.jsp");
	}

	@Test
	public void testMissingTaglibAttributes() throws Exception {
		test(
			"MissingTaglibAttributes.testjsp",
			"When using <clay:dropdown-actions>, always specify one of the " +
				"following attributes: \"aria-label\", \"aria-labelledby\", " +
					"\"title\"",
			10);
	}

	@Test
	public void testMissingTaglibs() throws Exception {
		test(
			SourceProcessorTestParameters.create(
				"MissingTaglibs.testjsp"
			).addExpectedMessage(
				"Missing taglib for tag with prefix \"aui\""
			).addExpectedMessage(
				"Missing taglib for tag with prefix \"liferay-portlet\""
			).addExpectedMessage(
				"Missing taglib for tag with prefix \"liferay-ui\""
			));
	}

	@Test
	public void testSortTagAttributes() throws Exception {
		test("SortTagAttributes.testjsp");
	}

}