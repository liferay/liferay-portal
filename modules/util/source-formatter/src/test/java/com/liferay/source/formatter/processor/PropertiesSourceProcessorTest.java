/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.processor;

import com.liferay.petra.string.StringBundler;

import org.junit.Test;

/**
 * @author Alan Huang
 */
public class PropertiesSourceProcessorTest extends BaseSourceProcessorTestCase {

	@Test
	public void testIncorrectWhitespaceCheck() throws Exception {
		test("IncorrectWhitespaceCheck.testproperties");
	}

	@Test
	public void testLanguageKeysContext() throws Exception {
		test(
			SourceProcessorTestParameters.create(
				"content/Language.testproperties"
			).addExpectedMessage(
				StringBundler.concat(
					"The single-word key \"abstract\" should include a word ",
					"of context at the end, within a [], to indicate specific ",
					"meaning"),
				1
			).addExpectedMessage(
				StringBundler.concat(
					"The single-word key \"order\" should include a word of ",
					"context at the end, within a [], to indicate specific ",
					"meaning"),
				6
			).addExpectedMessage(
				"The context \"...\" is invalid in the key \"order[...]\"", 7
			).addExpectedMessage(
				"The context \"\" is invalid in the key \"order[]\"", 8
			).addExpectedMessage(
				"The context \"0\" is invalid in the key \"order[0]\"", 9
			).addExpectedMessage(
				"The context \"123\" is invalid in the key \"order[123]\"", 10
			).addExpectedMessage(
				"The context \"abc\" is invalid in the key \"order[abc]\"", 11
			).addExpectedMessage(
				"The context \"x\" is invalid in the key \"order[x]\"", 13
			).addExpectedMessage(
				"The context \"xyz\" is invalid in the key \"order[xyz]\"", 14
			).addExpectedMessage(
				StringBundler.concat(
					"The single-word key \"view\" should include a word of ",
					"context at the end, within a [], like [noun] or [verb] ",
					"to indicate specific meaning"),
				15
			));
	}

	@Test
	public void testSortDefinitionKeys() throws Exception {
		test("FormatProperties1/liferay-plugin-package.testproperties");
		test("FormatProperties1/TLiferayBatchFileProperties.testproperties");
	}

	@Test
	public void testStylingCheck() throws Exception {
		test("StylingCheck.testproperties");
	}

}