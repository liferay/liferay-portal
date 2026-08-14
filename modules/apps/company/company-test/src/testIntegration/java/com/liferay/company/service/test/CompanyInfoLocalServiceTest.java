/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.company.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.encryptor.Encryptor;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyInfo;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyInfoLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alberto Chaparro
 */
@RunWith(Arquillian.class)
public class CompanyInfoLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_company = CompanyTestUtil.addCompany();

		_safeCloseable = CompanyThreadLocal.setCompanyIdWithSafeCloseable(
			_company.getCompanyId());
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_safeCloseable.close();

		_companyLocalService.deleteCompany(_company);
	}

	@Test
	public void testDeleteCompanyInfo() throws Exception {
		Company company = CompanyTestUtil.addCompany();

		long companyId = company.getCompanyId();

		_companyLocalService.deleteCompany(companyId);

		Assert.assertNull(_companyInfoLocalService.fetchCompany(companyId));
	}

	@Test
	public void testGetCompanyInfoKey() {
		CompanyInfo companyInfo = _companyInfoLocalService.fetchCompany(
			_company.getCompanyId());

		Assert.assertEquals(companyInfo.getKey(), _company.getKey());
	}

	@Test
	public void testGetCompanyInfoKeyObj() {
		CompanyInfo companyInfo = _companyInfoLocalService.fetchCompany(
			_company.getCompanyId());

		Assert.assertEquals(
			_encryptor.deserializeKey(companyInfo.getKey()),
			_company.getKeyObj());
	}

	@Test
	public void testUpdateCompanyInfoKey() {
		String key = RandomTestUtil.randomString();

		_company.setKey(key);

		_company = _companyLocalService.updateCompany(_company);

		CompanyInfo companyInfo = _companyInfoLocalService.fetchCompany(
			_company.getCompanyId());

		Assert.assertEquals(key, companyInfo.getKey());
	}

	@Test
	public void testUpdateCompanyInfoKeyObj() {
		String key = RandomTestUtil.randomString();

		_company.setKey(key);

		_company = _companyLocalService.updateCompany(_company);

		Assert.assertEquals(
			_encryptor.deserializeKey(key), _company.getKeyObj());
	}

	private static Company _company;

	@Inject
	private static CompanyLocalService _companyLocalService;

	private static SafeCloseable _safeCloseable;

	@Inject
	private CompanyInfoLocalService _companyInfoLocalService;

	@Inject
	private CounterLocalService _counterLocalService;

	@Inject
	private Encryptor _encryptor;

}