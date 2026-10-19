/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.processor;

import org.junit.Test;

/**
 * @author Alan Huang
 */
public class JSONSourceProcessorTest extends BaseSourceProcessorTestCase {

	@Test
	public void testCdataValueStyle() throws Exception {
		test("CdataValueStyle.testjson");
	}

	@Test
	public void testCheckMissingScripts() throws Exception {
		/*
		test(
			"CheckMissingScripts1/package.testjson",
			new String[] {
				"When using \"liferay-npm-scripts\", a script for \"csf\" is " +
					"required",
				"When using \"liferay-npm-scripts\", a script for \"format\" " +
					"is required"
			});

		test(
			"CheckMissingScripts2/package.testjson",
			new String[] {
				"When using \"liferay-npm-scripts\", a script for \"csf\" is " +
					"required",
				"When using \"liferay-npm-scripts\", a script for \"format\" " +
					"is required"
			});

		test(
			"CheckMissingScripts3/package.testjson",
			new String[] {
				"When using \"liferay-npm-scripts\", a script for \"csf\" is " +
					"required",
				"When using \"liferay-npm-scripts\", a script for \"format\" " +
					"is required"
			});

		test(
			"CheckMissingScripts4/package.testjson",
			new String[] {
				"When using \"liferay-npm-scripts\", a script for \"format\" " +
					"is required"
			});
		*/
	}

	@Test
	public void testIncorrectEmptyLines() throws Exception {
		test("IncorrectEmptyLines.testjson");
	}

	@Test
	public void testJSONDeprecatedPackages() throws Exception {
		test(
			SourceProcessorTestParameters.create(
				"JSONDeprecatedPackages/package.testjson"
			).addExpectedMessage(
				"Do not use deprecated package " +
					"\"liferay-module-config-generator\"",
				4
			).addExpectedMessage(
				"Do not use deprecated package \"metal-cli\"", 5
			));
	}

	@Test
	public void testRemoveJSONComments() throws Exception {
		test("RemoveJSONComments.testjson");
	}

}