/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.ReleaseConstants;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Mariano Álvaro Sáiz
 */
public class UpgradeReleaseTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testUpgrade() throws Exception {
		StringBundler sb = new StringBundler();

		UpgradeRelease upgradeRelease = new UpgradeRelease() {

			@Override
			public void runSQL(String template) {
				sb.append(template);
			}

		};

		upgradeRelease.doUpgrade();

		String sql = sb.toString();

		Assert.assertFalse(
			sql.contains(
				StringPool.APOSTROPHE +
					ReleaseConstants.DEFAULT_SERVLET_CONTEXT_NAME +
						StringPool.APOSTROPHE));
	}

}