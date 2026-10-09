/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.suite;

import com.liferay.jenkins.results.parser.GitWorkingDirectory;
import com.liferay.jenkins.results.parser.Job;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Kenji Heigel
 */
public class CompileModulesRelevantRule extends ModulesRelevantRule {

	public CompileModulesRelevantRule(
		String filePath, GitWorkingDirectory gitWorkingDirectory, Job job,
		String name, Properties properties) {

		super(filePath, gitWorkingDirectory, job, name, properties);
	}

	@Override
	public List<TestScriptCommand> getTestScriptCommands() {
		try {
			List<File> moduleDirs = new ArrayList<>();

			List<File> modifiedModuleProjectDirsList =
				getModifiedModuleProjectDirsList();

			if (modifiedModuleProjectDirsList.size() <= 5) {
				moduleDirs.addAll(modifiedModuleProjectDirsList);
			}
			else {
				for (File modifiedModuleProjectDir :
						modifiedModuleProjectDirsList) {

					File lfrbuildPortalFile = new File(
						modifiedModuleProjectDir, ".lfrbuild-portal");

					if (lfrbuildPortalFile.exists()) {
						continue;
					}

					moduleDirs.add(modifiedModuleProjectDir);
				}
			}

			if (moduleDirs.isEmpty()) {
				return new ArrayList<>();
			}

			List<TestScriptCommand> testScriptCommands = new ArrayList<>();

			testScriptCommands.add(
				new TestScriptCommand(
					"ant compile install-portal-snapshots", "."));

			StringBuilder sb = new StringBuilder();

			sb.append("../gradlew");

			for (File moduleDir : moduleDirs) {
				sb.append(" ");
				sb.append(getGradlePackageName(moduleDir));
				sb.append(":");
				sb.append(getTestScriptModuleGradleTaskName());
			}

			sb.append(" --parallel");

			testScriptCommands.add(
				new TestScriptCommand(sb.toString(), "modules"));

			return testScriptCommands;
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	@Override
	public boolean matches(File modifiedFile) {
		try {
			List<File> modifiedModuleProjectDirsList =
				getModifiedModuleProjectDirsList();

			if (modifiedModuleProjectDirsList.size() > 5) {
				boolean hasNonPortalModule = false;

				for (File modifiedModuleProjectDir :
						modifiedModuleProjectDirsList) {

					File lfrbuildPortalFile = new File(
						modifiedModuleProjectDir, ".lfrbuild-portal");

					if (!lfrbuildPortalFile.exists()) {
						hasNonPortalModule = true;

						break;
					}
				}

				if (!hasNonPortalModule) {
					return false;
				}
			}
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		return super.matches(modifiedFile);
	}

}