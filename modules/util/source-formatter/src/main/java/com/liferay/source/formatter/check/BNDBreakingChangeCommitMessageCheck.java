/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class BNDBreakingChangeCommitMessageCheck
	extends BaseBreakingChangesCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws Exception {

		if (!fileName.endsWith("/bnd.bnd") || absolutePath.contains("-test/")) {
			return content;
		}

		checkMajorVersionBump(
			fileName, absolutePath, content, "the major version bumps up");

		return content;
	}

	@Override
	protected Pattern getVersionPattern() {
		return _versionPattern;
	}

	private static final Pattern _versionPattern = Pattern.compile(
		"^Bundle-Version: *(.*)$", Pattern.MULTILINE);

}