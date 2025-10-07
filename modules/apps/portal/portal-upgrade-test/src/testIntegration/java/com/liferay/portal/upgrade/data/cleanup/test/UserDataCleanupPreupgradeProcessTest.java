/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.randomizerbumpers.NumericStringRandomizerBumper;
import com.liferay.portal.kernel.test.randomizerbumpers.UniqueStringRandomizerBumper;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.upgrade.data.cleanup.DataCleanupPreupgradeProcess;
import com.liferay.portal.kernel.upgrade.util.UpgradeProcessUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.data.cleanup.GroupDataCleanupPreupgradeProcess;
import com.liferay.portal.upgrade.data.cleanup.UserDataCleanupPreupgradeProcess;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Luis Ortiz
 */
@DataGuard(autoDelete = false, scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class UserDataCleanupPreupgradeProcessTest
	extends UserDataCleanupPreupgradeProcess {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpgrade() throws Exception {
		long companyId = TestPropsValues.getCompanyId();

		String languageId = UpgradeProcessUtil.getDefaultLanguageId(companyId);

		User user = UserTestUtil.addUser(
			companyId, TestPropsValues.getUserId(),
			RandomTestUtil.randomString(),
			RandomTestUtil.randomString() + RandomTestUtil.nextLong() +
				"@liferay.com",
			RandomTestUtil.randomString(
				NumericStringRandomizerBumper.INSTANCE,
				UniqueStringRandomizerBumper.INSTANCE),
			LocaleUtil.fromLanguageId(languageId),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			new long[] {TestPropsValues.getGroupId()},
			ServiceContextTestUtil.getServiceContext());

		runSQL("delete from User_ where userId = " + user.getUserId());

		upgrade();

		DataCleanupPreupgradeProcess dataCleanupPreupgradeProcess =
			new GroupDataCleanupPreupgradeProcess();

		dataCleanupPreupgradeProcess.upgrade();
	}

	@Inject
	private UserLocalService _userLocalService;

}