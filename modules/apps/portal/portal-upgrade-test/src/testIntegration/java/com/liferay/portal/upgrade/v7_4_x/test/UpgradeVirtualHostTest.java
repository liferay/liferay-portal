/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.test.util.BaseCTUpgradeProcessTestCase;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.VirtualHost;
import com.liferay.portal.kernel.model.change.tracking.CTModel;
import com.liferay.portal.kernel.service.VirtualHostLocalService;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.TreeMapBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.v7_4_x.UpgradeVirtualHost;

import java.util.List;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * @author David Truong
 */
@RunWith(Arquillian.class)
public class UpgradeVirtualHostTest extends BaseCTUpgradeProcessTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Override
	protected CTModel<?> addCTModel() throws Exception {
		List<VirtualHost> virtualHosts =
			_virtualHostLocalService.updateVirtualHosts(
				PortalUtil.getDefaultCompanyId(), 0,
				TreeMapBuilder.put(
					"localhost", StringPool.BLANK
				).build());

		return virtualHosts.get(0);
	}

	@Override
	protected void deleteCTModel(long primaryKey) {
	}

	@Override
	protected CTService<?> getCTService() {
		return _virtualHostLocalService;
	}

	@Override
	protected void runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = new UpgradeVirtualHost();

		upgradeProcess.upgrade();
	}

	@Override
	protected CTModel<?> updateCTModel(CTModel<?> ctModel) throws Exception {
		VirtualHost virtualHost = (VirtualHost)ctModel;

		virtualHost.setHostname(RandomTestUtil.randomString());

		return _virtualHostLocalService.updateVirtualHost(virtualHost);
	}

	@Inject
	private VirtualHostLocalService _virtualHostLocalService;

}