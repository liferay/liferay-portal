/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.suite;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.job.property.JobProperty;
import com.liferay.jenkins.results.parser.test.batch.JUnitTestBatch;
import com.liferay.jenkins.results.parser.test.batch.JUnitTestSelector;
import com.liferay.jenkins.results.parser.test.batch.PlaywrightTestBatch;
import com.liferay.jenkins.results.parser.test.batch.PlaywrightTestSelector;
import com.liferay.jenkins.results.parser.test.batch.PoshiTestBatch;
import com.liferay.jenkins.results.parser.test.batch.PoshiTestSelector;
import com.liferay.jenkins.results.parser.test.batch.TestBatch;

import java.io.File;
import java.io.IOException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Kenji Heigel
 */
public class RelevantTestSuiteTest extends BaseRelevantRuleTestCase {

	@Test
	public void testJUnitTestSelectorMerge() throws IOException {
		getRelevantRuleEngine();

		RelevantTestSuite relevantTestSuite = new RelevantTestSuite(
			getPortalAcceptancePullRequestJob());

		relevantTestSuite.setModifiedFiles(
			Arrays.asList(
				new File(getBaseDir(), "text_file_0.txt"),
				new File(getBaseDir(), "modules/module-1/text_file_1.txt")));

		JUnitTestBatch jUnitTestBatch = null;

		for (TestBatch testBatch : relevantTestSuite.getTestBatches(false)) {
			if (testBatch instanceof JUnitTestBatch) {
				jUnitTestBatch = (JUnitTestBatch)testBatch;

				break;
			}
		}

		JUnitTestSelector jUnitTestSelector = jUnitTestBatch.getTestSelector();

		List<JobProperty> includesJobProperties =
			jUnitTestSelector.getIncludesJobProperties();

		String globs = JenkinsResultsParserUtil.read(
			new File(getBaseDir(), "modules/module-1/text_file_1.txt"));

		int globCount = 0;

		for (JobProperty jobProperty : includesJobProperties) {
			String jobPropertyValue = jobProperty.getValue();

			for (String glob : jobPropertyValue.split(",")) {
				Assert.assertTrue(globs.contains(glob));

				globCount++;
			}
		}

		Assert.assertEquals(5, globCount);
	}

	@Test
	public void testPlaywrightTestSelectorMerge() {
		getRelevantRuleEngine();

		RelevantTestSuite relevantTestSuite = new RelevantTestSuite(
			getPortalAcceptancePullRequestJob());

		relevantTestSuite.setModifiedFiles(
			Arrays.asList(
				new File(getBaseDir(), "modules/module-1/text_file_1.txt"),
				new File(getBaseDir(), "modules/module-2/text_file_2.txt")));

		PlaywrightTestBatch playwrightTestBatch = null;

		for (TestBatch testBatch : relevantTestSuite.getTestBatches(false)) {
			if (testBatch instanceof PlaywrightTestBatch) {
				playwrightTestBatch = (PlaywrightTestBatch)testBatch;

				break;
			}
		}

		Set<String> actualPlaywrightProjectNames = new HashSet<>();

		PlaywrightTestSelector playwrightTestSelector =
			playwrightTestBatch.getTestSelector();

		for (JobProperty jobProperty :
				playwrightTestSelector.getPlaywrightJobProperties()) {

			actualPlaywrightProjectNames.add(jobProperty.getValue());
		}

		Set<String> expectedPlaywrightProjectNames = new HashSet<>(
			Arrays.asList(
				"module-1-playwright-project", "module-2-playwright-project"));

		Assert.assertEquals(
			expectedPlaywrightProjectNames, actualPlaywrightProjectNames);
	}

	@Test
	public void testPoshiTestSelectorMerge() throws IOException {
		getRelevantRuleEngine();

		RelevantTestSuite relevantTestSuite = new RelevantTestSuite(
			getPortalAcceptancePullRequestJob());

		relevantTestSuite.setModifiedFiles(
			Arrays.asList(
				new File(getBaseDir(), "test.properties"),
				new File(getBaseDir(), "modules/module-2/text_file_2.txt")));

		PoshiTestBatch poshiTestBatch = null;

		for (TestBatch testBatch : relevantTestSuite.getTestBatches(false)) {
			if (!(testBatch instanceof PoshiTestBatch)) {
				continue;
			}

			poshiTestBatch = (PoshiTestBatch)testBatch;

			PoshiTestSelector poshiTestSelector =
				poshiTestBatch.getTestSelector();

			String pql = JenkinsResultsParserUtil.read(
				new File(getBaseDir(), "modules/module-2/text_file_2.txt"));

			Assert.assertEquals(pql, poshiTestSelector.getPoshiQuery(false));

			pql = JenkinsResultsParserUtil.combine(
				"((portal.acceptance != quarantine)) AND (", pql, ")");

			Assert.assertEquals(pql, poshiTestSelector.getPoshiQuery());
		}
	}

	@Test
	public void testStableRuleBatchBypassesWhitelist() {
		getRelevantRuleEngine();

		RelevantTestSuite relevantTestSuite = new RelevantTestSuite(
			getPortalAcceptancePullRequestJob());

		relevantTestSuite.setModifiedFiles(
			Arrays.asList(new File(getBaseDir(), "stable/trigger.txt")));

		Set<String> batchNames = new HashSet<>();

		for (TestBatch testBatch : relevantTestSuite.getTestBatches(false)) {
			batchNames.add(testBatch.getName());
		}

		Assert.assertTrue(
			batchNames.contains("rest-builder-and-service-builder"));
	}

}