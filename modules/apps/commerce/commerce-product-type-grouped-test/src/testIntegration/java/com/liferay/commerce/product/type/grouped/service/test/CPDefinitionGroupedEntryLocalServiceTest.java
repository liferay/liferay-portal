/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.grouped.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceCatalog;
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
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Collections;
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
public class CPDefinitionGroupedEntryLocalServiceTest {

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
	public void testDeleteCPDefinitionGroupedEntries() throws Exception {
		CPDefinition cpDefinition1 = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), GroupedCPTypeConstants.NAME, true,
			true);
		CPDefinition cpDefinition2 = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		_cpDefinitionGroupedEntryLocalService.addCPDefinitionGroupedEntry(
			cpDefinition1.getCPDefinitionId(), cpDefinition2.getCProductId(), 0,
			1, _serviceContext);

		CPDefinition cpDefinition3 = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		_cpDefinitionGroupedEntryLocalService.addCPDefinitionGroupedEntry(
			cpDefinition1.getCPDefinitionId(), cpDefinition3.getCProductId(), 0,
			1, _serviceContext);

		_assertSearchCPDefinitionGroupedEntriesCount(cpDefinition1, 2);

		_cpDefinitionGroupedEntryLocalService.deleteCPDefinitionGroupedEntries(
			cpDefinition1.getCPDefinitionId());

		Assert.assertEquals(
			Collections.emptyList(),
			_cpDefinitionGroupedEntryLocalService.getCPDefinitionGroupedEntries(
				cpDefinition1.getCPDefinitionId()));

		_assertSearchCPDefinitionGroupedEntriesCount(cpDefinition1, 0);
	}

	@Test
	public void testDeleteCPDefinitionGroupedEntriesByEntryCProductId()
		throws Exception {

		CPDefinition cpDefinition1 = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), GroupedCPTypeConstants.NAME, true,
			true);
		CPDefinition cpDefinition2 = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		_cpDefinitionGroupedEntryLocalService.addCPDefinitionGroupedEntry(
			cpDefinition1.getCPDefinitionId(), cpDefinition2.getCProductId(), 0,
			1, _serviceContext);

		CPDefinition cpDefinition3 = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		_cpDefinitionGroupedEntryLocalService.addCPDefinitionGroupedEntry(
			cpDefinition1.getCPDefinitionId(), cpDefinition3.getCProductId(), 0,
			1, _serviceContext);

		List<CPDefinitionGroupedEntry> cpDefinitionGroupedEntries =
			_cpDefinitionGroupedEntryLocalService.getCPDefinitionGroupedEntries(
				cpDefinition1.getCPDefinitionId());

		Assert.assertEquals(
			cpDefinitionGroupedEntries.toString(), 2,
			cpDefinitionGroupedEntries.size());

		_assertSearchCPDefinitionGroupedEntriesCount(cpDefinition1, 2);

		_cpDefinitionGroupedEntryLocalService.
			deleteCPDefinitionGroupedEntriesByEntryCProductId(
				cpDefinition2.getCProductId());

		cpDefinitionGroupedEntries =
			_cpDefinitionGroupedEntryLocalService.getCPDefinitionGroupedEntries(
				cpDefinition1.getCPDefinitionId());

		Assert.assertEquals(
			cpDefinitionGroupedEntries.toString(), 1,
			cpDefinitionGroupedEntries.size());

		CPDefinitionGroupedEntry cpDefinitionGroupedEntry =
			cpDefinitionGroupedEntries.get(0);

		Assert.assertEquals(
			cpDefinition3.getCPDefinitionId(),
			cpDefinitionGroupedEntry.getEntryCPDefinitionId());

		_assertSearchCPDefinitionGroupedEntriesCount(cpDefinition1, 1);

		_cpDefinitionGroupedEntryLocalService.
			deleteCPDefinitionGroupedEntriesByEntryCProductId(
				cpDefinition3.getCProductId());

		_assertSearchCPDefinitionGroupedEntriesCount(cpDefinition1, 0);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	private void _assertSearchCPDefinitionGroupedEntriesCount(
		CPDefinition cpDefinition, int expectedCount) {

		SearchResponse searchResponse = _searcher.search(
			_searchRequestBuilderFactory.builder(
			).companyId(
				cpDefinition.getCompanyId()
			).emptySearchEnabled(
				true
			).modelIndexerClasses(
				CPDefinitionGroupedEntry.class
			).withSearchContext(
				searchContext -> searchContext.setAttribute(
					"cpDefinitionId", cpDefinition.getCPDefinitionId())
			).build());

		Assert.assertEquals(expectedCount, searchResponse.getTotalHits());
	}

	private CommerceCatalog _commerceCatalog;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private CPDefinitionGroupedEntryLocalService
		_cpDefinitionGroupedEntryLocalService;

	@Inject
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Inject
	private Searcher _searcher;

	private ServiceContext _serviceContext;

}