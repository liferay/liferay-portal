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
public class CheckFactoryTest extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testNewCheckUnknownTypeFailsLoud() {
		CheckConfig checkConfig = new CheckConfig(
			"a", "unknown-type", CheckConfig.Severity.MEDIUM, 0, null, null);

		try {
			CheckFactory.newCheck(checkConfig);

			Assert.fail("Expected IllegalArgumentException");
		}
		catch (IllegalArgumentException illegalArgumentException) {
		}
	}

}