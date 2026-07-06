/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Brittney Nguyen
 */
public class CheckTest extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testExecute() {
		CheckConfig checkConfig = new CheckConfig(
			"masters", "http-endpoint", CheckConfig.Severity.HIGH, 900, null,
			null);

		CheckResult checkResult = new CheckResult(
			CheckResult.Status.OK, "ok", null, 1L);

		Check check = new Check() {

			@Override
			public CheckResult execute() {
				return checkResult;
			}

			@Override
			public CheckConfig getCheckConfig() {
				return checkConfig;
			}

			@Override
			public String getID() {
				return checkConfig.getID();
			}

		};

		Assert.assertSame(checkResult, check.execute());
		Assert.assertEquals("masters", check.getID());
		Assert.assertSame(checkConfig, check.getCheckConfig());
	}

}