/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import java.util.regex.Pattern;

/**
 * @author Alejandro Tardín
 */
public class PackageinfoBreakingChangeCommitMessageCheck
	extends BaseBreakingChangesCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws Exception {

		checkMajorVersionBump(fileName, absolutePath, content);

		return content;
	}

	@Override
	protected Pattern getVersionPattern() {
		return _versionPattern;
	}

	private static final Pattern _versionPattern = Pattern.compile(
		"^version *(.*)$", Pattern.MULTILINE);

}