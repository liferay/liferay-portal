/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.source.formatter.SourceFormatterArgs;
import com.liferay.source.formatter.processor.SourceProcessor;

import java.util.regex.Pattern;

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

		if (!sourceFormatterArgs.isFormatCurrentBranch()) {
			return content;
		}

		checkMajorVersionBump(
			fileName, absolutePath, content,
			"the compatibilityVersion bumps up");

		return content;
	}

	@Override
	protected Pattern getVersionPattern() {
		return _versionPattern;
	}

	private static final Pattern _versionPattern = Pattern.compile(
		"^compatibilityVersion: *(.*)$", Pattern.MULTILINE);

}