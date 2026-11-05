/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.instance.lifecycle.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.persistence.CompanyUtil;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class PortalInstanceLifecycleListenerManagerImplTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testAddingService() throws Exception {
		_company = CompanyTestUtil.addCompany();

		long companyId = _company.getCompanyId();

		Bundle bundle = FrameworkUtil.getBundle(
			PortalInstanceLifecycleListenerManagerImplTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceRegistration<PortalInstanceLifecycleListener>
			serviceRegistration1 = bundleContext.registerService(
				PortalInstanceLifecycleListener.class,
				new PortalInstanceLifecycleListener() {

					@Override
					public void portalInstanceRegistered(Company company) {
					}

					@Override
					public void portalInstanceUnregistered(Company company) {
						if (company.getCompanyId() == companyId) {
							throw new Error();
						}
					}

				},
				MapUtil.singletonDictionary(
					"service.ranking", Integer.MAX_VALUE));

		_updateCompanyId(companyId, -companyId);

		List<Long> companyIds = new ArrayList<>();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.instance.lifecycle.internal." +
					"PortalInstanceLifecycleListenerManagerImpl",
				LoggerTestUtil.OFF)) {

			ServiceRegistration<PortalInstanceLifecycleListener>
				serviceRegistration2 = bundleContext.registerService(
					PortalInstanceLifecycleListener.class,
					new PortalInstanceLifecycleListener() {

						@Override
						public void portalInstanceRegistered(Company company) {
							companyIds.add(company.getCompanyId());
						}

						@Override
						public void portalInstanceUnregistered(
							Company company) {
						}

					},
					null);

			serviceRegistration2.unregister();
		}
		finally {
			serviceRegistration1.unregister();

			_updateCompanyId(-companyId, companyId);
		}

		Assert.assertTrue(companyIds.contains(TestPropsValues.getCompanyId()));
	}

	private void _updateCompanyId(long oldCompanyId, long newCompanyId)
		throws Exception {

		DB db = DBManagerUtil.getDB();

		db.runSQL(
			StringBundler.concat(
				"update Company set companyId = ", newCompanyId,
				" where companyId = ", oldCompanyId));

		CompanyUtil.clearCache(_company);
	}

	@DeleteAfterTestRun
	private Company _company;

}