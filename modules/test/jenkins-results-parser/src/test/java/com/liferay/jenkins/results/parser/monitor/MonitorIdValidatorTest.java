/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import com.liferay.jenkins.results.parser.RandomTestUtil;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Brittney Nguyen
 */
public class MonitorIdValidatorTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testValidate() {
		MonitorIdValidator.validate(Collections.<Monitor>emptyList());

		MonitorIdValidator.validate(
			Arrays.<Monitor>asList(_newMonitor("a"), _newMonitor("b")));

		try {
			MonitorIdValidator.validate(
				Arrays.<Monitor>asList(_newMonitor("a"), _newMonitor("a")));

			Assert.fail("Expected IllegalArgumentException");
		}
		catch (IllegalArgumentException illegalArgumentException) {
		}
	}

	private Monitor _newMonitor(String id) {
		return new TestMonitor(
			new MonitorConfig(
				id, RandomTestUtil.randomLong(), null,
				MonitorConfig.Severity.MEDIUM, null,
				RandomTestUtil.randomLong(), RandomTestUtil.randomString()));
	}

}