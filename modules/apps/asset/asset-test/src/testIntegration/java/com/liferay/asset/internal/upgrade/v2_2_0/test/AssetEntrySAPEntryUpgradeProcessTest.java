/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.internal.upgrade.v2_2_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
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
 * @author Christopher Kian
 */
@RunWith(Arquillian.class)
public class AssetEntrySAPEntryUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testUpgradeQualifiesAssetEntryDefaultSignature()
		throws Exception {

		Company company = CompanyTestUtil.addCompany();

		try (SafeCloseable safeCloseable = CompanyThreadLocal.lock(
				company.getCompanyId())) {

			SAPEntry sapEntry = _sapEntryLocalService.fetchSAPEntry(
				company.getCompanyId(), _SAP_ENTRY_NAME);

			sapEntry.setAllowedServiceSignatures(
				_ALLOWED_SERVICE_SIGNATURES_OLD);

			_sapEntryLocalService.updateSAPEntry(sapEntry);

			_runUpgrade();

			EntityCacheUtil.clearCache();
			FinderCacheUtil.clearCache();

			sapEntry = _sapEntryLocalService.fetchSAPEntry(
				company.getCompanyId(), _SAP_ENTRY_NAME);

			Assert.assertEquals(
				_ALLOWED_SERVICE_SIGNATURES,
				sapEntry.getAllowedServiceSignatures());
		}
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess[] upgradeProcesses = UpgradeTestUtil.getUpgradeSteps(
			_upgradeStepRegistrator, Version.parseVersion("2.2.0"));

		upgradeProcesses[0].upgrade();
	}

	private static final String _ALLOWED_SERVICE_SIGNATURES =
		"com.liferay.asset.kernel.service.AssetEntryService" +
			"#incrementViewCounter(long,java.lang.String,long)";

	private static final String _ALLOWED_SERVICE_SIGNATURES_OLD =
		"com.liferay.asset.kernel.service.AssetEntryService" +
			"#incrementViewCounter";

	private static final String _SAP_ENTRY_NAME = "ASSET_ENTRY_DEFAULT";

	@Inject
	private SAPEntryLocalService _sapEntryLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.asset.internal.upgrade.registry.AssetServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}