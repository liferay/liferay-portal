/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Michael Hashimoto
 */
public class TestrayCaseResultTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetErrorsList() {
		List<String> errorsList = TestrayCaseResult.getErrorsList(
			"first\nsecond\nthird\nlast");

		Assert.assertEquals(errorsList.toString(), 3, errorsList.size());

		Assert.assertEquals("first\n...\nlast", errorsList.get(1));
		Assert.assertEquals("first\nsecond\nthird\nlast", errorsList.get(0));
	}

	@Test
	public void testGetErrorsListEmptyErrors() {
		for (String errors : new String[] {null, ""}) {
			List<String> errorsList = TestrayCaseResult.getErrorsList(errors);

			Assert.assertEquals(errorsList.toString(), 1, errorsList.size());

			Assert.assertEquals(errors, errorsList.get(0));
		}
	}

	@Test
	public void testGetErrorsListEndsWithRejectedMessage() {
		for (String errors :
				new String[] {
					"only line", "first\nlast", "first\nsecond\nlast",
					_SEM_VER_ERRORS
				}) {

			List<String> errorsList = TestrayCaseResult.getErrorsList(errors);

			String lastErrors = errorsList.get(errorsList.size() - 1);

			Assert.assertNotEquals(errors, lastErrors);

			Assert.assertTrue(
				lastErrors, lastErrors.contains("web application firewall"));
		}
	}

	@Test
	public void testGetErrorsListSkipsSummaryForShortErrors() {
		List<String> errorsList = TestrayCaseResult.getErrorsList(
			"first\nlast");

		Assert.assertEquals(errorsList.toString(), 2, errorsList.size());

		Assert.assertEquals("first\nlast", errorsList.get(0));
	}

	@Test
	public void testGetErrorsListSummarizesSemanticVersioning() {
		List<String> errorsList = TestrayCaseResult.getErrorsList(
			_SEM_VER_ERRORS);

		String summarizedErrors = errorsList.get(1);

		Assert.assertTrue(
			summarizedErrors,
			summarizedErrors.endsWith("Semantic versioning is incorrect"));

		Assert.assertTrue(
			summarizedErrors,
			summarizedErrors.startsWith("     [exec]   PACKAGE_NAME"));
	}

	private static final String _SEM_VER_ERRORS =
		JenkinsResultsParserUtil.combine(
			"     [exec]   PACKAGE_NAME   DELTA   CUR_VER\n",
			"     [exec] * com.liferay.portal.kernel.util   MINOR   102.0.0\n",
			"     [exec] \t\t\t+   return     java.lang.String\n",
			"     [exec] Semantic versioning is incorrect");

}