/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.grouped.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CProductLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalServiceUtil;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.type.grouped.constants.GroupedCPTypeConstants;
import com.liferay.commerce.product.type.grouped.model.CPDefinitionGroupedEntry;
import com.liferay.commerce.product.type.grouped.service.CPDefinitionGroupedEntryLocalService;
import com.liferay.commerce.product.type.simple.constants.SimpleCPTypeConstants;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brian I. Kim
 */
@RunWith(Arquillian.class)
public class CProductModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			company.getGroupId(), TestPropsValues.getUserId());

		_commerceCatalog = CommerceCatalogLocalServiceUtil.addCommerceCatalog(
			null, RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			LocaleUtil.US.getDisplayLanguage(), _serviceContext);
	}

	@Test
	public void testOnBeforeRemove() throws Exception {
		CPDefinition cpDefinition1 = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), GroupedCPTypeConstants.NAME, true,
			true);

		CPDefinition cpDefinition2 = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		_cpDefinitionGroupedEntryLocalService.addCPDefinitionGroupedEntry(
			cpDefinition1.getCPDefinitionId(), cpDefinition2.getCProductId(), 0,
			1, _serviceContext);

		_cProductLocalService.deleteCProduct(cpDefinition2.getCProductId());

		List<CPDefinitionGroupedEntry> cpDefinitionGroupedEntries =
			_cpDefinitionGroupedEntryLocalService.getCPDefinitionGroupedEntries(
				cpDefinition1.getCPDefinitionId());

		Assert.assertEquals(
			cpDefinitionGroupedEntries.toString(), 0,
			cpDefinitionGroupedEntries.size());
	}

	@Inject
	private CProductLocalService _cProductLocalService;

	private CommerceCatalog _commerceCatalog;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private CPDefinitionGroupedEntryLocalService
		_cpDefinitionGroupedEntryLocalService;

	private ServiceContext _serviceContext;

}