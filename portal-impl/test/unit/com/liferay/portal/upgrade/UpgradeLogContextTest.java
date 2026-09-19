/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade;

import com.liferay.portal.dao.db.MySQLDB;
import com.liferay.portal.kernel.log.LogContext;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.upgrade.BaseAdminPortletsUpgradeProcess;
import com.liferay.portal.kernel.upgrade.BasePortletIdUpgradeProcess;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.tools.DBUpgrader;
import com.liferay.portal.upgrade.log.UpgradeLogContext;
import com.liferay.portal.verify.PreupgradeVerifyProperties;

import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Tina Tian
 */
public class UpgradeLogContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() throws Exception {
		_logContext = UpgradeLogContext.getInstance();
	}

	@Test
	public void testNonupgradeClassWithContext() {
		Assert.assertSame(
			Collections.emptyMap(),
			_logContext.getContext(CompanyTestUtil.class.getName()));

		try {
			UpgradeLogContext.setContext(CompanyTestUtil.class.getName());

			Assert.assertSame(
				Collections.emptyMap(),
				_logContext.getContext(CompanyTestUtil.class.getName()));
		}
		finally {
			UpgradeLogContext.clearContext();

			Assert.assertSame(
				Collections.emptyMap(),
				_logContext.getContext(CompanyTestUtil.class.getName()));
		}
	}

	@Test
	public void testUpgradeClassWithContext() {
		Assert.assertEquals(
			_defaultContext,
			_logContext.getContext(DBUpgrader.class.getName()));

		try {
			UpgradeLogContext.setContext("Test");

			Assert.assertEquals(
				Collections.singletonMap("component", "Test"),
				_logContext.getContext(DBUpgrader.class.getName()));
		}
		finally {
			UpgradeLogContext.clearContext();

			Assert.assertEquals(
				_defaultContext,
				_logContext.getContext(DBUpgrader.class.getName()));
		}
	}

	@Test
	public void testUpgradeClassesDefaultContext() {
		Assert.assertEquals(
			_defaultContext,
			_logContext.getContext(
				BaseAdminPortletsUpgradeProcess.class.getName()));
		Assert.assertEquals(
			_defaultContext,
			_logContext.getContext(
				BasePortletIdUpgradeProcess.class.getName()));
		Assert.assertEquals(
			_defaultContext,
			_logContext.getContext(DBUpgrader.class.getName()));
		Assert.assertEquals(
			_defaultContext,
			_logContext.getContext(LoggingTimer.class.getName()));
		Assert.assertEquals(
			_defaultContext, _logContext.getContext(MySQLDB.class.getName()));
		Assert.assertEquals(
			_defaultContext,
			_logContext.getContext(PreupgradeVerifyProperties.class.getName()));
		Assert.assertEquals(
			_defaultContext,
			_logContext.getContext(
				"com.liferay.portal.upgrade.internal.executor." +
					"UpgradeExecutor"));
		Assert.assertEquals(
			_defaultContext,
			_logContext.getContext(
				"com.liferay.portal.upgrade.internal.release." +
					"ReleaseManagerImpl"));
	}

	private static final Map<String, String> _defaultContext =
		Collections.singletonMap("component", "framework");
	private static LogContext _logContext;

}