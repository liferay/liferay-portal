/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Michael Prigge
 */
public class PatcherBuildUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetDownloadPathPreservesFileNameWithoutHotfixSegment() {
		Assert.assertEquals(
			"liferay/liferay-dxp-2024.q1.0.zip",
			PatcherBuildUtil.getDownloadPath(
				"liferay/liferay-dxp-2024.q1.0.zip"));
	}

	@Test
	public void testGetDownloadPathStripsHotfixSegment() {
		Assert.assertEquals(
			"liferay/liferay-dxp-2024.q1.0.zip",
			PatcherBuildUtil.getDownloadPath(
				"liferay/hotfix/liferay-dxp-2024.q1.0.zip"));
	}

	@Test
	public void testGetDownloadPathWhenFileNameIsBlank() {
		Assert.assertEquals(
			StringPool.BLANK,
			PatcherBuildUtil.getDownloadPath(StringPool.BLANK));
	}

	@Test
	public void testGetDownloadPathWhenFileNameIsNull() {
		Assert.assertEquals(
			StringPool.BLANK, PatcherBuildUtil.getDownloadPath(null));
	}

}