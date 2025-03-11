/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.SourceFormatterExcludes;
import com.liferay.source.formatter.util.FileUtil;
import com.liferay.source.formatter.util.GradleBuildFile;
import com.liferay.source.formatter.util.GradleDependency;
import com.liferay.source.formatter.util.SourceFormatterUtil;

import java.io.File;
import java.io.IOException;

import java.util.List;

/**
 * @author Alan Huang
 */
public class GradleMissingDependenciesForUpgradeJava21Check
	extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		if (!absolutePath.endsWith("/build.gradle")) {
			return content;
		}

		int x = absolutePath.lastIndexOf(CharPool.SLASH);

		File bndFile = new File(absolutePath.substring(0, x + 1) + "bnd.bnd");

		if (!bndFile.exists()) {
			return content;
		}

		String dirName = absolutePath.substring(0, x);

		int y = dirName.lastIndexOf(CharPool.SLASH);

		String shortDirName = dirName.substring(y + 1);

		List<String> javaFileNames = SourceFormatterUtil.scanForFileNames(
			absolutePath.substring(0, x + 1), new String[0],
			new String[] {"**/*.java"}, new SourceFormatterExcludes(), false);

		for (String javaFileName : javaFileNames) {
			String javaFileContent = FileUtil.read(new File(javaFileName));

			if (javaFileContent.contains("import jakarta.annotation.")) {
				if (shortDirName.endsWith("-test")) {
					content = _fixMissingDependencies(
						content, "testIntegrationImplementation",
						"jakarta.annotation", "jakarta.annotation-api",
						"2.1.1");
				}
				else {
					content = _fixMissingDependencies(
						content, "compileOnly", "jakarta.annotation",
						"jakarta.annotation-api", "2.1.1");
				}
			}

			if (javaFileContent.contains(
					"import jakarta.xml.bind.annotation.")) {

				if (shortDirName.endsWith("-test")) {
					content = _fixMissingDependencies(
						content, "testIntegrationImplementation",
						"jakarta.xml.bind", "jakarta.xml.bind-api", "4.0.2");
				}
				else {
					content = _fixMissingDependencies(
						content, "compileOnly", "jakarta.xml.bind",
						"jakarta.xml.bind-api", "4.0.2");
				}
			}
		}

		return content;
	}

	private String _fixMissingDependencies(
		String content, String configuration, String group, String name,
		String version) {

		GradleBuildFile gradleBuildFile = new GradleBuildFile(content);

		for (GradleDependency gradleDependency :
				gradleBuildFile.getGradleDependencies()) {

			String gradleDependencyConfiguration =
				gradleDependency.getConfiguration();
			String gradleDependencyGroup = gradleDependency.getGroup();
			String gradleDependencyName = gradleDependency.getName();

			if (StringUtil.equals(
					gradleDependencyConfiguration, configuration) &&
				StringUtil.equals(gradleDependencyGroup, group) &&
				StringUtil.equals(gradleDependencyName, name)) {

				return content;
			}
		}

		gradleBuildFile.insertGradleDependency(
			configuration, group, name, version);

		return gradleBuildFile.getSource();
	}

}