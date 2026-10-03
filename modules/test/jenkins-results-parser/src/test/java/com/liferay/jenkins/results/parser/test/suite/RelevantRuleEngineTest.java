/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.suite;

import com.liferay.jenkins.results.parser.job.property.JobProperty;
import com.liferay.jenkins.results.parser.test.batch.PlaywrightTestBatch;
import com.liferay.jenkins.results.parser.test.batch.PlaywrightTestSelector;
import com.liferay.jenkins.results.parser.test.batch.TestBatch;

import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Kenji Heigel
 */
public class RelevantRuleEngineTest extends BaseRelevantRuleTestCase {

	@Test
	public void testExcludedModifiedFileInModule1Dir() {
		RelevantRuleEngine relevantRuleEngine = getRelevantRuleEngine();

		List<RelevantRule> relevantRules =
			relevantRuleEngine.getMatchingRelevantRules(
				Arrays.asList(
					new File(getBaseDir(), "modules/module-1/file_1.excluded"),
					new File(getBaseDir(), "text_file_0.txt")));

		List<String> expectedRelevantRuleNames = Arrays.asList(
			"functional-smoke-0-rule");

		List<String> actualRelevantRuleNames = new ArrayList<>();

		for (RelevantRule relevantRule : relevantRules) {
			actualRelevantRuleNames.add(relevantRule.getName());
		}

		Collections.sort(actualRelevantRuleNames);
		Collections.sort(expectedRelevantRuleNames);

		Assert.assertEquals(expectedRelevantRuleNames, actualRelevantRuleNames);
	}

	@Test
	public void testIgnoreGlobalExcludes() {
		List<String> actualRelevantRuleNames = new ArrayList<>();

		RelevantRuleEngine relevantRuleEngine = getRelevantRuleEngine();

		List<RelevantRule> relevantRules =
			relevantRuleEngine.getMatchingRelevantRules(
				Collections.singletonList(
					new File(getBaseDir(), "modules/module-1/file_1.pw")));

		for (RelevantRule relevantRule : relevantRules) {
			actualRelevantRuleNames.add(relevantRule.getName());
		}

		Assert.assertEquals(
			Collections.singletonList("ignore-global-excludes-rule"),
			actualRelevantRuleNames);
	}

	@Test
	public void testModifiedFileForPlaywrightBatch() {
		RelevantRuleEngine relevantRuleEngine = getRelevantRuleEngine();

		List<RelevantRule> relevantRules =
			relevantRuleEngine.getMatchingRelevantRules(
				Collections.singletonList(
					new File(
						getBaseDir(), "modules/module-3/text_file_3.txt")));

		List<String> expectedRelevantRuleNames = Arrays.asList(
			"modules-integration-0-rule", "playwright-3-rule");

		List<String> actualRelevantRuleNames = new ArrayList<>();

		for (RelevantRule relevantRule : relevantRules) {
			actualRelevantRuleNames.add(relevantRule.getName());
		}

		Collections.sort(actualRelevantRuleNames);
		Collections.sort(expectedRelevantRuleNames);

		Assert.assertEquals(expectedRelevantRuleNames, actualRelevantRuleNames);

		PlaywrightTestBatch playwrightTestBatch = null;

		relevantRuleLoop:
		for (RelevantRule relevantRule : relevantRules) {
			for (TestBatch testBatch : relevantRule.getTestBatches()) {
				if (testBatch instanceof PlaywrightTestBatch) {
					playwrightTestBatch = (PlaywrightTestBatch)testBatch;

					break relevantRuleLoop;
				}
			}
		}

		Set<String> actualPlaywrightProjectNames = new HashSet<>();

		PlaywrightTestSelector playwrightTestSelector =
			playwrightTestBatch.getTestSelector();

		for (JobProperty jobProperty :
				playwrightTestSelector.getPlaywrightJobProperties()) {

			actualPlaywrightProjectNames.add(jobProperty.getValue());
		}

		Assert.assertEquals(
			Collections.singleton("module-3-playwright-project"),
			actualPlaywrightProjectNames);
	}

	@Test
	public void testModifiedFileInBaseDir() {
		RelevantRuleEngine relevantRuleEngine = getRelevantRuleEngine();

		List<RelevantRule> relevantRules =
			relevantRuleEngine.getMatchingRelevantRules(
				Collections.singletonList(
					new File(getBaseDir(), "text_file_0.txt")));

		List<String> actualRelevantRuleNames = new ArrayList<>();

		for (RelevantRule relevantRule : relevantRules) {
			actualRelevantRuleNames.add(relevantRule.getName());
		}

		List<String> expectedRelevantRuleNames = Collections.singletonList(
			"functional-smoke-0-rule");

		Assert.assertEquals(expectedRelevantRuleNames, actualRelevantRuleNames);
	}

	@Test
	public void testModifiedFileInBaseDirAndModule1Dir() {
		RelevantRuleEngine relevantRuleEngine = getRelevantRuleEngine();

		List<RelevantRule> relevantRules =
			relevantRuleEngine.getMatchingRelevantRules(
				Arrays.asList(
					new File(getBaseDir(), "modules/module-1/text_file_1.txt"),
					new File(getBaseDir(), "text_file_0.txt")));

		List<String> expectedRelevantRuleNames = Arrays.asList(
			"functional-smoke-0-rule", "modules-integration-0-rule",
			"modules-integration-1-rule", "playwright-1-rule");

		List<String> actualRelevantRuleNames = new ArrayList<>();

		for (RelevantRule relevantRule : relevantRules) {
			actualRelevantRuleNames.add(relevantRule.getName());
		}

		Collections.sort(actualRelevantRuleNames);
		Collections.sort(expectedRelevantRuleNames);

		Assert.assertEquals(expectedRelevantRuleNames, actualRelevantRuleNames);
	}

	@Test
	public void testModifiedFileInBaseDirAndModule2Dir() {
		RelevantRuleEngine relevantRuleEngine = getRelevantRuleEngine();

		List<RelevantRule> relevantRules =
			relevantRuleEngine.getMatchingRelevantRules(
				Arrays.asList(
					new File(getBaseDir(), "modules/module-2/text_file_2.txt"),
					new File(getBaseDir(), "text_file_0.txt")));

		List<String> expectedRelevantRuleNames = Arrays.asList(
			"functional-smoke-0-rule", "functional-smoke-2-rule",
			"modules-unit-0-rule", "playwright-2-rule");

		List<String> actualRelevantRuleNames = new ArrayList<>();

		for (RelevantRule relevantRule : relevantRules) {
			actualRelevantRuleNames.add(relevantRule.getName());
		}

		Collections.sort(actualRelevantRuleNames);
		Collections.sort(expectedRelevantRuleNames);

		Assert.assertEquals(expectedRelevantRuleNames, actualRelevantRuleNames);
	}

}