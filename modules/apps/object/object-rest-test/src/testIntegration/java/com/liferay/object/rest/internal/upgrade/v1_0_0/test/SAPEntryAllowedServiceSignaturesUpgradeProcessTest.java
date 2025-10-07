/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.upgrade.v1_0_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.util.UpgradeProcessUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.security.service.access.policy.model.SAPEntry;
import com.liferay.portal.security.service.access.policy.service.SAPEntryLocalService;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mauricio Valdivia
 */
@RunWith(Arquillian.class)
public class SAPEntryAllowedServiceSignaturesUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testUpgradeAllowedServiceSignatures() throws Exception {
		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					TestPropsValues.getCompanyId())) {

			String allowedServiceSignatures = StringBundler.concat(
				"com.liferay.object.rest.internal.resource.v1_0.",
				"ObjectEntryResourceImpl#getByExternalReferenceCode\n",
				"com.liferay.object.rest.internal.resource.v1_0.",
				"ObjectEntryResourceImpl#getObjectEntriesPage\n",
				"com.liferay.object.rest.internal.resource.v1_0.",
				"ObjectEntryResourceImpl#postObjectEntry");

			SAPEntry sapEntry = _sapEntryLocalService.addSAPEntry(
				TestPropsValues.getUserId(), allowedServiceSignatures, false,
				true, RandomTestUtil.randomString(),
				HashMapBuilder.put(
					LocaleUtil.fromLanguageId(
						UpgradeProcessUtil.getDefaultLanguageId(
							TestPropsValues.getCompanyId())),
					RandomTestUtil.randomString()
				).build(),
				_createServiceContext());

			_runUpgrade();

			sapEntry = _sapEntryLocalService.getSAPEntry(
				sapEntry.getSapEntryId());

			Assert.assertEquals(
				allowedServiceSignatures,
				sapEntry.getAllowedServiceSignatures());

			sapEntry = _sapEntryLocalService.addSAPEntry(
				TestPropsValues.getUserId(),
				StringBundler.concat(
					"com.liferay.object.rest.internal.resource.v1_0.",
					"ObjectEntryResourceImpl#",
					"putByExternalReferenceCodeCurrentExternalReference",
					"CodeObjectRelationshipNameRelatedExternalReferenceCode\n",
					"com.liferay.object.rest.internal.resource.v1_0.",
					"ObjectEntryResourceImpl#getByExternalReferenceCode\n",
					"com.liferay.object.rest.internal.resource.v1_0.",
					"ObjectEntryResourceImpl#getObjectEntriesPage\n",
					"com.liferay.object.rest.internal.resource.v1_0.",
					"ObjectEntryResourceImpl#getObjectEntry\n",
					"com.liferay.object.rest.internal.resource.v1_0.",
					"ObjectEntryResourceImpl#postObjectEntry\n",
					"com.liferay.object.rest.internal.resource.v1_0.",
					"ObjectEntryResourceImpl#postScopeScopeKey\n"),
				false, true, RandomTestUtil.randomString(),
				HashMapBuilder.put(
					LocaleUtil.fromLanguageId(
						UpgradeProcessUtil.getDefaultLanguageId(
							TestPropsValues.getCompanyId())),
					RandomTestUtil.randomString()
				).build(),
				_createServiceContext());

			_runUpgrade();

			sapEntry = _sapEntryLocalService.getSAPEntry(
				sapEntry.getSapEntryId());

			String newAllowedServiceSignatures =
				sapEntry.getAllowedServiceSignatures();

			Assert.assertEquals(
				StringBundler.concat(
					"com.liferay.object.rest.internal.resource.v1_0.",
					"ObjectEntryResourceImpl#getByExternalReferenceCode\n",
					"com.liferay.object.rest.internal.resource.v1_0.",
					"ObjectEntryResourceImpl#getObjectEntriesPage\n",
					"com.liferay.object.rest.internal.resource.v1_0.",
					"ObjectEntryResourceImpl#getObjectEntry\n",
					"com.liferay.object.rest.internal.resource.v1_0.",
					"ObjectEntryResourceImpl#postObjectEntry\n",
					"com.liferay.object.rest.internal.resource.v1_0.",
					"ObjectEntryResourceImpl#postScopeScopeKey\n",
					"com.liferay.object.rest.internal.resource.v1_0.",
					"ObjectEntryRelatedObjectsResourceImpl#putByExternal",
					"ReferenceCodeCurrentExternalReferenceCodeObject",
					"RelationshipNameRelatedExternalReferenceCode"),
				newAllowedServiceSignatures);

			_runUpgrade();

			sapEntry = _sapEntryLocalService.getSAPEntry(
				sapEntry.getSapEntryId());

			Assert.assertEquals(
				newAllowedServiceSignatures,
				sapEntry.getAllowedServiceSignatures());
		}
	}

	private ServiceContext _createServiceContext() throws Exception {
		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(TestPropsValues.getCompanyId());
		serviceContext.setUserId(TestPropsValues.getUserId());

		return serviceContext;
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess[] upgradeProcesses = UpgradeTestUtil.getUpgradeSteps(
			_upgradeStepRegistrator, new Version(1, 0, 0));

		for (UpgradeProcess upgradeProcess : upgradeProcesses) {
			upgradeProcess.upgrade();
		}

		CacheRegistryUtil.clear();
	}

	@Inject(
		filter = "(&(component.name=com.liferay.object.rest.internal.upgrade.registry.ObjectRESTImplUpgradeStepRegistrator))"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	@Inject
	private SAPEntryLocalService _sapEntryLocalService;

}