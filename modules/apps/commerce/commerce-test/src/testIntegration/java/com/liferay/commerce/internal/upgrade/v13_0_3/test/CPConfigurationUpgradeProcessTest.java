/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.upgrade.v13_0_3.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.configuration.CTSettingsConfiguration;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.commerce.product.model.CPConfigurationEntry;
import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPConfigurationEntryLocalService;
import com.liferay.commerce.product.service.CPConfigurationListLocalService;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.type.simple.constants.SimpleCPTypeConstants;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Matyas Wollner
 */
@RunWith(Arquillian.class)
public class CPConfigurationUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testUpdateCPConfiguration() throws Exception {
		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				LocaleUtil.US.getDisplayLanguage(),
				ServiceContextTestUtil.getServiceContext());

		CPTestUtil.addCPDefinitionFromCatalog(
			commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
			false);
		CPTestUtil.addCPDefinitionFromCatalog(
			commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
			false);

		_cpConfigurationListLocalService.deleteCPConfigurationList(
			_cpConfigurationListLocalService.getMasterCPConfigurationList(
				commerceCatalog.getGroupId()),
			true);

		_runUpgrade();

		EntityCacheUtil.clearCache();

		CPConfigurationList cpConfigurationList =
			_cpConfigurationListLocalService.getMasterCPConfigurationList(
				commerceCatalog.getGroupId());

		Assert.assertNotNull(cpConfigurationList);

		List<CPConfigurationEntry> cpConfigurationEntries =
			CPTestUtil.getCPConfigurationEntries(
				cpConfigurationList.getCPConfigurationListId());

		Assert.assertFalse(cpConfigurationEntries.isEmpty());
		Assert.assertEquals(
			cpConfigurationEntries.toString(), 3,
			cpConfigurationEntries.size());
	}

	@Test
	public void testUpdateCPConfigurationWithCTCollection() throws Exception {
		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				LocaleUtil.US.getDisplayLanguage(),
				ServiceContextTestUtil.getServiceContext());

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
			false);

		double depth1 = cpDefinition.getDepth();

		double depth2 = depth1 + 100.0;

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						CTSettingsConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"enabled", true
						).build())) {

			CTCollection ctCollection =
				_ctCollectionLocalService.addCTCollection(
					null, TestPropsValues.getCompanyId(),
					TestPropsValues.getUserId(), 0,
					CPConfigurationUpgradeProcessTest.class.getSimpleName(),
					null);

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						ctCollection.getCtCollectionId())) {

				cpDefinition.setDepth(depth2);

				cpDefinition = _cpDefinitionLocalService.updateCPDefinition(
					cpDefinition);
			}

			_cpConfigurationListLocalService.deleteCPConfigurationList(
				_cpConfigurationListLocalService.getMasterCPConfigurationList(
					commerceCatalog.getGroupId()),
				true);

			_runUpgrade();

			EntityCacheUtil.clearCache();

			CPConfigurationList cpConfigurationList =
				_cpConfigurationListLocalService.getMasterCPConfigurationList(
					commerceCatalog.getGroupId());

			Assert.assertNotNull(cpConfigurationList);

			List<CPConfigurationEntry> cpConfigurationEntries =
				CPTestUtil.getCPConfigurationEntries(
					cpConfigurationList.getCPConfigurationListId());

			Assert.assertFalse(cpConfigurationEntries.isEmpty());
			Assert.assertEquals(
				cpConfigurationEntries.toString(), 2,
				cpConfigurationEntries.size());

			Map<Long, Double> expectedDepths = HashMapBuilder.put(
				0L, depth1
			).put(
				ctCollection.getCtCollectionId(), depth2
			).build();

			for (Long ctCollectionId : expectedDepths.keySet()) {
				try (SafeCloseable safeCloseable =
						CTCollectionThreadLocal.
							setCTCollectionIdWithSafeCloseable(
								ctCollectionId)) {

					CPConfigurationEntry cpConfigurationEntry =
						_cpConfigurationEntryLocalService.
							getCPConfigurationEntry(
								_portal.getClassNameId(CPDefinition.class),
								cpDefinition.getCPDefinitionId(),
								cpConfigurationList.getCPConfigurationListId());

					Assert.assertTrue(
						expectedDepths.containsKey(
							cpConfigurationEntry.getCtCollectionId()));
					Assert.assertEquals(
						expectedDepths.get(
							cpConfigurationEntry.getCtCollectionId()),
						cpConfigurationEntry.getDepth(), 0.0);
				}
			}
		}
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();
	}

	private static final String _CLASS_NAME =
		"com.liferay.commerce.internal.upgrade.v13_0_3." +
			"CPConfigurationUpgradeProcess";

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@Inject
	private CPConfigurationEntryLocalService _cpConfigurationEntryLocalService;

	@Inject
	private CPConfigurationListLocalService _cpConfigurationListLocalService;

	@Inject
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private Portal _portal;

	@Inject(
		filter = "(&(component.name=com.liferay.commerce.internal.upgrade.registry.CommerceServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}