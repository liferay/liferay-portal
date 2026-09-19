/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.suite;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.PortalAcceptancePullRequestJob;
import com.liferay.jenkins.results.parser.PortalGitWorkingDirectory;
import com.liferay.jenkins.results.parser.job.property.JobProperty;
import com.liferay.jenkins.results.parser.test.batch.TestBatch;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author Kenji Heigel
 */
public class RelevantTestSuite {

	public RelevantTestSuite(
		PortalAcceptancePullRequestJob portalAcceptancePullRequestJob) {

		PortalGitWorkingDirectory portalGitWorkingDirectory =
			portalAcceptancePullRequestJob.getPortalGitWorkingDirectory();

		_modifiedFiles = portalGitWorkingDirectory.getModifiedFilesList();
		_portalGitWorkingDirectory = portalGitWorkingDirectory;

		_relevantRuleEngine = RelevantRuleEngine.getInstance(
			portalAcceptancePullRequestJob);
		_testSuiteName = portalAcceptancePullRequestJob.getTestSuiteName();
	}

	public Set<JobProperty> getTestBatchNamesJobProperties() {
		return _testBatchNamesJobProperties;
	}

	public List<TestBatch> getTestBatches(boolean validateAllRules) {
		File baseTestPropertiesFile = new File(
			_relevantRuleEngine.getBaseDir(), "test.properties");

		String testBatchNamesPropertyValue =
			JenkinsResultsParserUtil.getProperty(
				JenkinsResultsParserUtil.getProperties(baseTestPropertiesFile),
				"relevant.batch.names.whitelist", _testSuiteName);

		if (testBatchNamesPropertyValue == null) {
			testBatchNamesPropertyValue = JenkinsResultsParserUtil.getProperty(
				JenkinsResultsParserUtil.getProperties(baseTestPropertiesFile),
				"test.batch.names", _testSuiteName);

			if (testBatchNamesPropertyValue == null) {
				throw new RuntimeException(
					"Please set relevant.batch.names.whitelist or " +
						"test.batch.names[relevant] in " +
							baseTestPropertiesFile);
			}
		}

		List<RelevantRule> relevantRules =
			_relevantRuleEngine.getMatchingRelevantRules(_modifiedFiles);

		Collections.sort(relevantRules);

		try {
			if (validateAllRules) {
				RelevantRuleValidation.validate(
					_portalGitWorkingDirectory.getGitRepositoryName(),
					_portalGitWorkingDirectory.getUpstreamBranchName());
			}
			else {
				RelevantRuleValidation.validate(relevantRules);
			}
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		List<String> validTestBatchRegexes = Arrays.asList(
			testBatchNamesPropertyValue.split(","));

		List<TestBatch> testBatches = new ArrayList<>();

		System.out.println(
			"There are " + relevantRules.size() + " matching relevant rules: " +
				_relevantRuleEngine.getRelevantRuleNames(relevantRules));

		for (RelevantRule relevantRule : relevantRules) {
			boolean stableRule = Objects.equals(
				relevantRule.getName(), "stable-rule");

			for (TestBatch testBatch : relevantRule.getTestBatches()) {
				if (testBatches.contains(testBatch)) {
					TestBatch existingTestBatch = testBatches.get(
						testBatches.indexOf(testBatch));

					existingTestBatch.merge(testBatch);

					continue;
				}

				if (stableRule ||
					isValidTestBatch(
						validTestBatchRegexes, testBatch.getName())) {

					testBatches.add(testBatch);
				}
				else {
					System.out.println(
						JenkinsResultsParserUtil.combine(
							testBatch.getName(),
							" is not a valid test batch in relevant. Check ",
							"the property \"relevant.batch.names.whitelist\" ",
							"in the base test.properties file and set the ",
							"batch name.\n"));
				}
			}

			_testBatchNamesJobProperties.addAll(
				relevantRule.getTestBatchNamesJobProperties());
		}

		Collections.sort(testBatches);

		return testBatches;
	}

	public Boolean isValidTestBatch(
		List<String> validTestBatchRegexes, String testBatchName) {

		for (String validTestBatchRegex : validTestBatchRegexes) {
			if (testBatchName.matches(validTestBatchRegex)) {
				return true;
			}
		}

		return false;
	}

	public void setModifiedFiles(List<File> modifiedFiles) {
		_modifiedFiles = modifiedFiles;
	}

	private List<File> _modifiedFiles;
	private final PortalGitWorkingDirectory _portalGitWorkingDirectory;
	private final RelevantRuleEngine _relevantRuleEngine;
	private final Set<JobProperty> _testBatchNamesJobProperties =
		new HashSet<>();
	private final String _testSuiteName;

}