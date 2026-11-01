/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.language.override.internal.upgrade.v1_3_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.language.override.model.PLOEntry;
import com.liferay.portal.language.override.service.PLOEntryLocalService;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Thiago Buarque
 */
@RunWith(Arquillian.class)
public class PLOEntryUserNameUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpgrade() throws Exception {
		User user = TestPropsValues.getUser();

		PLOEntry ploEntry1 = _addPLOEntry(user.getUserId());

		User deletedUser = UserTestUtil.addUser();

		PLOEntry ploEntry2 = _addPLOEntry(deletedUser.getUserId());

		_userLocalService.deleteUser(deletedUser);

		_runUpgrade();

		CacheRegistryUtil.clear();

		_entityCache.clearCache();
		_finderCache.clearCache();

		ploEntry1 = _ploEntryLocalService.getPLOEntry(
			ploEntry1.getPloEntryId());

		Assert.assertEquals(user.getFullName(), ploEntry1.getUserName());

		ploEntry2 = _ploEntryLocalService.getPLOEntry(
			ploEntry2.getPloEntryId());

		Assert.assertEquals(StringPool.BLANK, ploEntry2.getUserName());
	}

	private PLOEntry _addPLOEntry(long userId) throws Exception {
		PLOEntry ploEntry = _ploEntryLocalService.addOrUpdatePLOEntry(
			null, TestPropsValues.getCompanyId(), userId,
			RandomTestUtil.randomString(), "en_US",
			RandomTestUtil.randomString());

		ploEntry.setUserName(null);

		return _ploEntryLocalService.updatePLOEntry(ploEntry);
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();
	}

	private static final String _CLASS_NAME =
		"com.liferay.portal.language.override.internal.upgrade.v1_3_0." +
			"PLOEntryUserNameUpgradeProcess";

	@Inject
	private EntityCache _entityCache;

	@Inject
	private FinderCache _finderCache;

	@Inject
	private PLOEntryLocalService _ploEntryLocalService;

	@Inject(
		filter = "component.name=com.liferay.portal.language.override.internal.upgrade.registry.PortalLanguageOverrideServiceUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

	@Inject
	private UserLocalService _userLocalService;

}