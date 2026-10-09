/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.suite;

import com.liferay.jenkins.results.parser.GitWorkingDirectory;
import com.liferay.jenkins.results.parser.Job;

import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Kenji Heigel
 */
public class PortalCoreJavaUnitTestRelevantRule extends RelevantRule {

	public PortalCoreJavaUnitTestRelevantRule(
		String filePath, GitWorkingDirectory gitWorkingDirectory, Job job,
		String name, Properties properties) {

		super(filePath, gitWorkingDirectory, job, name, properties);
	}

	@Override
	public List<TestScriptCommand> getTestScriptCommands() {
		List<TestScriptCommand> testScriptCommands = new ArrayList<>();

		List<File> modifiedDirsList = getModifiedDirsList(
			getGitWorkingDirectory().getWorkingDirectory());

		Set<String> modifiedDirNames = new TreeSet<>();

		for (File modifiedDir : modifiedDirsList) {
			String modifiedDirName = modifiedDir.getName();

			if (!_portalCoreDirNames.contains(modifiedDirName)) {
				continue;
			}

			modifiedDirNames.add(modifiedDirName);
		}

		if (modifiedDirNames.isEmpty()) {
			return testScriptCommands;
		}

		testScriptCommands.add(new TestScriptCommand("ant compile-test", "."));

		for (String modifiedDirName : modifiedDirNames) {
			testScriptCommands.add(
				new TestScriptCommand("ant test-unit", modifiedDirName));
		}

		return testScriptCommands;
	}

	private static final Set<String> _portalCoreDirNames = new HashSet<>(
		Arrays.asList(
			"portal-impl", "portal-kernel", "portal-test", "portal-web",
			"support-tomcat", "util-bridges", "util-java", "util-slf4j",
			"util-taglib"));

}