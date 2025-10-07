/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.data.cleanup.DataCleanupPreupgradeException;
import com.liferay.portal.kernel.upgrade.data.cleanup.DataCleanupPreupgradeProcess;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Luis Ortiz
 */
@RunWith(Arquillian.class)
public class DataCleanupPreupgradeProcessTest
	extends DataCleanupPreupgradeProcess {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpgrade() {
		_testUpgrade(() -> upgrade());
		_testUpgrade(() -> upgrade(this));
	}

	@Override
	protected void doUpgrade() throws Exception {
		throw new Exception(_EXCEPTION_MESSAGE);
	}

	private void _testUpgrade(UnsafeRunnable<Exception> unsafeRunnable) {
		try {
			unsafeRunnable.run();

			Assert.fail();
		}
		catch (Exception exception) {
			String message = exception.getMessage();

			Assert.assertTrue(message.contains(_EXCEPTION_MESSAGE));

			Assert.assertTrue(
				exception instanceof DataCleanupPreupgradeException);
		}
	}

	private static final String _EXCEPTION_MESSAGE =
		RandomTestUtil.randomString();

}