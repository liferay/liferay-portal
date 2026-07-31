/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.rule.TimeoutTestRule;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

/**
 * @author Alejandro Tardín
 */
public class PackageinfoBreakingChangeCommitMessageCheckTest {

	@Test
	public void testHasMajorVersionBump() {
		Assert.assertTrue(
			PackageinfoBreakingChangeCommitMessageCheck.hasMajorVersionBump(
				_getDiff("86.1.0", "87.0.0")));
	}

	@Test
	public void testHasMajorVersionBumpWithAddedPackageinfo() {
		Assert.assertFalse(
			PackageinfoBreakingChangeCommitMessageCheck.hasMajorVersionBump(
				StringBundler.concat(
					"diff --git a/", _FILE_NAME, " b/", _FILE_NAME,
					"\nnew file mode 100644\n@@ -0,0 +1 @@\n+version 1.0.0")));
	}

	@Test
	public void testHasMajorVersionBumpWithLoweredVersion() {
		Assert.assertFalse(
			PackageinfoBreakingChangeCommitMessageCheck.hasMajorVersionBump(
				_getDiff("87.0.0", "86.1.0")));
	}

	@Test
	public void testHasMajorVersionBumpWithMinorVersionBump() {
		Assert.assertFalse(
			PackageinfoBreakingChangeCommitMessageCheck.hasMajorVersionBump(
				_getDiff("86.1.0", "86.2.0")));
	}

	@Rule
	public final TestRule testRule = TimeoutTestRule.INSTANCE;

	private String _getDiff(String oldVersion, String newVersion) {
		return StringBundler.concat(
			"diff --git a/", _FILE_NAME, " b/", _FILE_NAME,
			"\nindex d96c0b8c06de5..682b435622a0a 100644\n--- a/", _FILE_NAME,
			"\n+++ b/", _FILE_NAME, "\n@@ -1 +1 @@\n-version ", oldVersion,
			"\n\\ No newline at end of file\n+version ", newVersion,
			"\n\\ No newline at end of file");
	}

	private static final String _FILE_NAME =
		"modules/apps/object/object-api/src/main/resources/com/liferay/object" +
			"/service/packageinfo";

}