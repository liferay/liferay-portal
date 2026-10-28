/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.talend.web.internal.process;

import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class TalendProcessCallableTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testCall() throws Exception {
		int exitCode = RandomTestUtil.randomInt();
		String stdErr = RandomTestUtil.randomString();
		String stdOut = RandomTestUtil.randomString();

		TalendProcessCallable talendProcessCallable = new TalendProcessCallable(
			new String[] {String.valueOf(exitCode), stdErr, stdOut},
			TestTalendJob.class.getName());

		TalendProcessOutput talendProcessOutput = talendProcessCallable.call();

		Assert.assertEquals(exitCode, talendProcessOutput.getExitCode());
		Assert.assertEquals(stdErr, talendProcessOutput.getStdErr());
		Assert.assertEquals(stdOut, talendProcessOutput.getStdOut());
	}

	public static class TestTalendJob {

		public int runJobInTOS(String[] args) {
			System.err.println(args[1]);
			System.out.println(args[2]);

			return GetterUtil.getInteger(args[0]);
		}

	}

}