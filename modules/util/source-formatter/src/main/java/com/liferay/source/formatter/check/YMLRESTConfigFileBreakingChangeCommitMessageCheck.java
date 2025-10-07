/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.tools.GitUtil;
import com.liferay.source.formatter.SourceFormatterArgs;
import com.liferay.source.formatter.processor.SourceProcessor;

/**
 * @author Alan Huang
 */
public class YMLRESTConfigFileBreakingChangeCommitMessageCheck
	extends BaseBreakingChangesCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws Exception {

		if (!fileName.endsWith("/rest-config.yaml")) {
			return content;
		}

		SourceProcessor sourceProcessor = getSourceProcessor();

		SourceFormatterArgs sourceFormatterArgs =
			sourceProcessor.getSourceFormatterArgs();

		if (_hasCompatibilityVersionBump(absolutePath, sourceFormatterArgs)) {
			checkCommitMessages(
				fileName, absolutePath, sourceFormatterArgs,
				"compatibilityVersion bumps up");
		}

		return content;
	}

	private boolean _hasCompatibilityVersionBump(
			String absolutePath, SourceFormatterArgs sourceFormatterArgs)
		throws Exception {

		for (String currentBranchFileName :
				getCurrentBranchFileNames(sourceFormatterArgs)) {

			if (!absolutePath.endsWith(currentBranchFileName)) {
				continue;
			}

			String newCompatibilityVersion = null;
			String oldCompatibilityVersion = null;

			String[] lines = StringUtil.splitLines(
				GitUtil.getCurrentBranchFileDiff(
					sourceFormatterArgs.getBaseDirName(),
					sourceFormatterArgs.getGitWorkingBranchName(),
					absolutePath));

			for (String line : lines) {
				if (!line.contains("compatibilityVersion:")) {
					continue;
				}

				int pos = line.indexOf(":");

				String version = StringUtil.trim(line.substring(pos + 1));

				if (line.startsWith(StringPool.PLUS)) {
					newCompatibilityVersion = version;
				}
				else if (line.startsWith(StringPool.DASH)) {
					oldCompatibilityVersion = version;
				}
			}

			if (oldCompatibilityVersion == null) {
				continue;
			}

			return !StringUtil.equals(
				newCompatibilityVersion, oldCompatibilityVersion);
		}

		return false;
	}

}