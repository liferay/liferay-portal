/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.language.override.internal.upgrade.v1_2_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.language.override.model.PLOEntry;
import com.liferay.portal.language.override.service.PLOEntryLocalService;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.util.Locale;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Thiago Buarque
 */
@RunWith(Arquillian.class)
public class PLOEntryLanguageIdUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testDoUpgrade() throws Exception {
		String key1 = RandomTestUtil.randomString();

		PLOEntry ploEntry1 = _addPLOEntry(key1, "en_US", "iw_IL");

		String key2 = RandomTestUtil.randomString();

		PLOEntry ploEntry2 = _addPLOEntry(key2, "en_US", "pt-BR");
		PLOEntry ploEntry3 = _addPLOEntry(key2, "en_CA", "pt_BR");

		_runUpgrade();

		CacheRegistryUtil.clear();

		_entityCache.clearCache();
		_finderCache.clearCache();

		ploEntry1 = _ploEntryLocalService.getPLOEntry(
			ploEntry1.getPloEntryId());

		Assert.assertEquals(
			LocaleUtil.toLanguageId(new Locale("iw", "IL")),
			ploEntry1.getLanguageId());

		Assert.assertNull(
			_ploEntryLocalService.fetchPLOEntry(ploEntry2.getPloEntryId()));

		PLOEntry upgradedPLOEntry3 = _ploEntryLocalService.getPLOEntry(
			ploEntry3.getPloEntryId());

		Assert.assertEquals("pt_BR", upgradedPLOEntry3.getLanguageId());
	}

	private PLOEntry _addPLOEntry(
			String key, String availableLanguageId, String languageId)
		throws Exception {

		PLOEntry ploEntry = _ploEntryLocalService.addOrUpdatePLOEntry(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			key, availableLanguageId, RandomTestUtil.randomString());

		ploEntry.setLanguageId(languageId);

		return _ploEntryLocalService.updatePLOEntry(ploEntry);
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();
	}

	private static final String _CLASS_NAME =
		"com.liferay.portal.language.override.internal.upgrade.v1_2_0." +
			"PLOEntryLanguageIdUpgradeProcess";

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

}