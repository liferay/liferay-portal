/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.model.ServiceComponent;
import com.liferay.portal.kernel.service.ServiceComponentLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.v7_4_x.UpgradeServiceComponent;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jorge Díaz
 */
@RunWith(Arquillian.class)
public class UpgradeServiceComponentTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	@TestInfo("LPD-57274")
	public void testUpgrade() throws Exception {
		ServiceComponent serviceComponent = _getServiceComponent();

		String originalData = serviceComponent.getData();

		try {
			serviceComponent.setData(RandomTestUtil.randomString());

			serviceComponent =
				_serviceComponentLocalService.updateServiceComponent(
					serviceComponent);

			UpgradeProcess upgradeProcess = new UpgradeServiceComponent();

			upgradeProcess.upgrade();

			CacheRegistryUtil.clear();

			serviceComponent =
				_serviceComponentLocalService.getServiceComponent(
					serviceComponent.getServiceComponentId());

			Assert.assertEquals(originalData, serviceComponent.getData());
		}
		finally {
			serviceComponent.setData(originalData);

			_serviceComponentLocalService.updateServiceComponent(
				serviceComponent);
		}
	}

	private ServiceComponent _getServiceComponent() {
		for (ServiceComponent serviceComponent :
				_serviceComponentLocalService.getLatestServiceComponents()) {

			String buildNamespace = serviceComponent.getBuildNamespace();

			if (buildNamespace.startsWith("com.liferay")) {
				return serviceComponent;
			}
		}

		return null;
	}

	@Inject
	private ServiceComponentLocalService _serviceComponentLocalService;

}