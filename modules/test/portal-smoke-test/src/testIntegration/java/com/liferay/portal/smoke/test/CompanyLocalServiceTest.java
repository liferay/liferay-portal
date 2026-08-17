/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.smoke.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.DataCleanupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mika Koivisto
 * @author Dale Shan
 */
@DataGuard(autoDelete = false, scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class CompanyLocalServiceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_classNamesSavepointSafeCloseable =
			DataCleanupTestUtil.getClassNamesSavepointSafeCloseable();
	}

	@After
	public void tearDown() throws Exception {
		_classNamesSavepointSafeCloseable.close();
	}

	@Test
	public void testAddAndDeleteCompany() throws Exception {
		String webId = RandomTestUtil.randomString() + "test.com";

		Company company = _companyLocalService.addCompany(
			null, webId, webId, "test.com", 0, true, true, null, null, null,
			null, null, null);

		_companyLocalService.deleteCompany(company.getCompanyId());

		for (String curWebId : PortalInstancePool.getWebIds()) {
			Assert.assertNotEquals(company.getWebId(), curWebId);
		}
	}

	private SafeCloseable _classNamesSavepointSafeCloseable;

	@Inject
	private CompanyLocalService _companyLocalService;

}