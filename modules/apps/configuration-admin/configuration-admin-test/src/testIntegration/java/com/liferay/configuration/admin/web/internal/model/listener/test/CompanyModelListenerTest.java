/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.web.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class CompanyModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testOnAfterRemove() throws Exception {
		String factoryPid = RandomTestUtil.randomString();

		String defaultPid = ConfigurationTestUtil.createFactoryConfiguration(
			factoryPid,
			MapUtil.singletonDictionary(
				"dxp.lxc.liferay.com.virtualInstanceId", "default"));

		Company company = CompanyTestUtil.addCompany();

		String pid = ConfigurationTestUtil.createFactoryConfiguration(
			factoryPid,
			MapUtil.singletonDictionary(
				"dxp.lxc.liferay.com.virtualInstanceId", company.getWebId()));

		try {
			_companyLocalService.deleteCompany(company);

			Assert.assertNotNull(_getConfigurations(defaultPid));
			Assert.assertNull(_getConfigurations(pid));
		}
		finally {
			ConfigurationTestUtil.deleteFactoryConfiguration(
				defaultPid, factoryPid);
			ConfigurationTestUtil.deleteFactoryConfiguration(pid, factoryPid);
		}
	}

	private Configuration[] _getConfigurations(String pid) throws Exception {
		return _configurationAdmin.listConfigurations(
			"(service.pid=" + SystemBundleUtil.escapeFilterValue(pid) + ")");
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private ConfigurationAdmin _configurationAdmin;

}