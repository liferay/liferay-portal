/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.internal.upgrade.v2_4_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alicia García
 */
@RunWith(Arquillian.class)
public class TrashEntriesMaxAgeUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	@TestInfo("LPD-91035")
	public void testUpgrade() throws Exception {
		_testUpgradeWithAssetLibraryDepotEntry();
		_testUpgradeWithCustomTrashEntriesMaxAge();
		_testUpgradeWithMissingTrashEntriesMaxAge();
		_testUpgradeWithZeroTrashEntriesMaxAge();
	}

	private DepotEntry _addDepotEntry(int type) throws Exception {
		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.US, RandomTestUtil.randomString()),
			Collections.singletonMap(
				LocaleUtil.US, RandomTestUtil.randomString()),
			type, ServiceContextTestUtil.getServiceContext());

		_depotEntries.add(depotEntry);

		return depotEntry;
	}

	private int _getTrashEntriesMaxAgeCompany() throws Exception {
		return PrefsPropsUtil.getInteger(
			TestPropsValues.getCompanyId(), PropsKeys.TRASH_ENTRIES_MAX_AGE,
			PropsValues.TRASH_ENTRIES_MAX_AGE);
	}

	private String _getTrashEntriesMaxAgeGroup(DepotEntry depotEntry) {
		Group group = _groupLocalService.fetchGroup(depotEntry.getGroupId());

		UnicodeProperties typeSettingsUnicodeProperties =
			group.getTypeSettingsProperties();

		return typeSettingsUnicodeProperties.getProperty("trashEntriesMaxAge");
	}

	private void _setGroupTrashEntriesMaxAge(
			DepotEntry depotEntry, String trashEntriesMaxAge)
		throws Exception {

		Group group = _groupLocalService.fetchGroup(depotEntry.getGroupId());

		UnicodeProperties typeSettingsUnicodeProperties =
			group.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.setProperty(
			"trashEntriesMaxAge", trashEntriesMaxAge);

		_groupLocalService.updateGroup(
			group.getGroupId(), typeSettingsUnicodeProperties.toString());
	}

	private void _testUpgradeWithAssetLibraryDepotEntry() throws Exception {
		DepotEntry depotEntry = _addDepotEntry(
			DepotConstants.TYPE_ASSET_LIBRARY);

		_setGroupTrashEntriesMaxAge(depotEntry, "0");

		_upgrade();

		Assert.assertEquals("0", _getTrashEntriesMaxAgeGroup(depotEntry));
	}

	private void _testUpgradeWithCustomTrashEntriesMaxAge() throws Exception {
		DepotEntry depotEntry = _addDepotEntry(DepotConstants.TYPE_SPACE);

		_setGroupTrashEntriesMaxAge(depotEntry, "1440");

		_upgrade();

		Assert.assertEquals("1440", _getTrashEntriesMaxAgeGroup(depotEntry));
	}

	private void _testUpgradeWithMissingTrashEntriesMaxAge() throws Exception {
		DepotEntry depotEntry = _addDepotEntry(DepotConstants.TYPE_SPACE);

		_upgrade();

		Assert.assertEquals(
			String.valueOf(_getTrashEntriesMaxAgeCompany()),
			_getTrashEntriesMaxAgeGroup(depotEntry));
	}

	private void _testUpgradeWithZeroTrashEntriesMaxAge() throws Exception {
		DepotEntry depotEntry = _addDepotEntry(DepotConstants.TYPE_SPACE);

		_setGroupTrashEntriesMaxAge(depotEntry, "0");

		_upgrade();

		Assert.assertEquals(
			String.valueOf(_getTrashEntriesMaxAgeCompany()),
			_getTrashEntriesMaxAgeGroup(depotEntry));
	}

	private void _upgrade() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.OFF)) {

			UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
				_upgradeStepRegistrator, _CLASS_NAME);

			upgradeProcess.upgrade();

			_entityCache.clearCache();
			_multiVMPool.clear();

			CentralizedThreadLocal.clearLongLivedCentralizedThreadLocals();
		}
	}

	private static final String _CLASS_NAME =
		"com.liferay.depot.internal.upgrade.v2_4_0." +
			"TrashEntriesMaxAgeUpgradeProcess";

	@DeleteAfterTestRun
	private final List<DepotEntry> _depotEntries = new ArrayList<>();

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private EntityCache _entityCache;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject(
		filter = "component.name=com.liferay.depot.internal.upgrade.registry.DepotServiceUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}