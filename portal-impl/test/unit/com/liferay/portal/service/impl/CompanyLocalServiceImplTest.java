/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.VirtualHost;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.service.VirtualHostLocalServiceUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.model.impl.CompanyImpl;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.osgi.framework.BundleContext;

/**
 * @author Raymond Augé
 */
public class CompanyLocalServiceImplTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		BundleContext bundleContext = Mockito.mock(BundleContext.class);

		_systemBundleUtilMockedStatic = Mockito.mockStatic(
			SystemBundleUtil.class);

		_systemBundleUtilMockedStatic.when(
			SystemBundleUtil::getBundleContext
		).thenReturn(
			bundleContext
		);

		VirtualHost virtualHost = Mockito.mock(VirtualHost.class);

		_virtualHostLocalServiceUtilMockedStatic = Mockito.mockStatic(
			VirtualHostLocalServiceUtil.class);

		_virtualHostLocalServiceUtilMockedStatic.when(
			() -> VirtualHostLocalServiceUtil.fetchCompanyDefaultVirtualHost(
				Mockito.anyLong())
		).thenReturn(
			virtualHost
		);
	}

	@After
	public void tearDown() {
		_systemBundleUtilMockedStatic.close();
		_virtualHostLocalServiceUtilMockedStatic.close();
	}

	@Test
	public void testSyncVirtualHost() throws Exception {
		String domain = RandomTestUtil.randomString();

		Company company = new CompanyImpl();

		company.setWebId("liferay.com");

		CompanyLocalServiceImpl companyLocalServiceImpl =
			new CompanyLocalServiceImpl() {

				@Override
				public Company checkCompany(String webId)
					throws PortalException {

					return syncVirtualHost(getCompanyByWebId(webId));
				}

				@Override
				public Company getCompanyByWebId(String webId)
					throws PortalException {

					return company;
				}

				@Override
				public Company updateCompany(
						long companyId, String virtualHostname, String mx,
						int maxUsers, boolean active)
					throws PortalException {

					company.setCompanyId(companyId);
					company.setMx(mx);
					company.setMaxUsers(maxUsers);
					company.setActive(active);
					company.setVirtualHostname(virtualHostname);

					return company;
				}

			};

		try (SafeCloseable safeCloseable1 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"COMPANY_DEFAULT_VIRTUAL_HOST_MAIL_DOMAIN", domain);
			SafeCloseable safeCloseable2 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"COMPANY_DEFAULT_VIRTUAL_HOST_NAME", domain);
			SafeCloseable safeCloseable3 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"COMPANY_DEFAULT_VIRTUAL_HOST_SYNC_ON_STARTUP", true)) {

			Company checkedCompany = companyLocalServiceImpl.checkCompany(
				PropsValues.COMPANY_DEFAULT_WEB_ID);

			Assert.assertEquals(domain, checkedCompany.getMx());
			Assert.assertEquals(domain, checkedCompany.getVirtualHostname());
		}
	}

	private MockedStatic<SystemBundleUtil> _systemBundleUtilMockedStatic;
	private MockedStatic<VirtualHostLocalServiceUtil>
		_virtualHostLocalServiceUtilMockedStatic;

}