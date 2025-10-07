/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.upgrade.v3_10_2;

import com.liferay.dynamic.data.mapping.BaseDDMTestCase;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.IOException;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.skyscreamer.jsonassert.JSONAssert;

/**
 * @author Carolina Barbosa
 */
public class DDMFormInstanceReportUpgradeProcessTest extends BaseDDMTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testUpgradeDDMFormInstanceReportData()
		throws Exception, IOException {

		DDMFormInstanceReportUpgradeProcess
			ddmFormInstanceReportUpgradeProcess =
				new DDMFormInstanceReportUpgradeProcess(jsonFactory);

		JSONAssert.assertEquals(
			read("updated-ddm-form-instance-report-data.json"),
			ddmFormInstanceReportUpgradeProcess.
				upgradeDDMFormInstanceReportData(
					read("ddm-form-instance-report-data.json")),
			false);
	}

}