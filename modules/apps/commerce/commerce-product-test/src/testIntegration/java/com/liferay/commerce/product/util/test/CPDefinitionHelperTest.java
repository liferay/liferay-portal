/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.util.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.product.catalog.CPCatalogEntry;
import com.liferay.commerce.product.catalog.CPQuery;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.data.source.CPDataSourceResult;
import com.liferay.commerce.product.helper.CPDefinitionHelper;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.List;

import org.frutilla.FrutillaRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Luca Pellizzon
 */
@RunWith(Arquillian.class)
public class CPDefinitionHelperTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_company = CompanyTestUtil.addCompany();
	}

	@Before
	public void setUp() throws Exception {
		_commerceCatalog = _commerceCatalogLocalService.addCommerceCatalog(
			null, RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			LocaleUtil.toLanguageId(LocaleUtil.US),
			ServiceContextTestUtil.getServiceContext(_company.getGroupId()));
	}

	@After
	public void tearDown() throws Exception {
		List<CPDefinition> cpDefinitions =
			_cpDefinitionLocalService.getCPDefinitions(
				_commerceCatalog.getGroupId(), WorkflowConstants.STATUS_ANY,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (CPDefinition cpDefinition : cpDefinitions) {
			_cpDefinitionLocalService.deleteCPDefinition(cpDefinition);
		}

		_commerceCatalogLocalService.deleteCommerceCatalogs(
			_company.getCompanyId());
	}

	@Test
	public void testSearchAllCPDefinitions() throws PortalException {
		frutillaRule.scenario(
			"Search for CPDefinitions without filters"
		).given(
			"A collection of CPDefinitions"
		).when(
			"I search for CPDefinitions without filters"
		).then(
			"The results will contain all the products available"
		);

		CPInstance[] cpInstances = _addCPInstances(
			_commerceCatalog.getGroupId(), _CP_INSTANCES_COUNT);

		CPDataSourceResult cpDataSourceResult = _cpDefinitionHelper.search(
			_commerceCatalog.getGroupId(),
			CPTestUtil.getSearchContext(
				null, WorkflowConstants.STATUS_APPROVED,
				_commerceCatalog.getGroup()),
			new CPQuery(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		List<Long> actualCPDefinitionIds = TransformUtil.transform(
			cpDataSourceResult.getCPCatalogEntries(),
			cpCatalogEntry -> cpCatalogEntry.getCPDefinitionId());

		List<Long> cpDefinitionIds = TransformUtil.transformToList(
			cpInstances,
			cpInstance -> {
				CPDefinition cpDefinition = cpInstance.getCPDefinition();

				return cpDefinition.getCPDefinitionId();
			});

		Assert.assertTrue(actualCPDefinitionIds.containsAll(cpDefinitionIds));
	}

	@Test
	public void testSearchCPDefinitionsByCategory() throws PortalException {
		frutillaRule.scenario(
			"Search for CPDefinition by Category"
		).given(
			"A collection of CPDefinitions"
		).and(
			"A category"
		).and(
			"Some of the CPDefinitions are associated to that category"
		).when(
			"I search for CPDefinitions given the category as a search filter"
		).then(
			"The results will contain only those product linked to the category"
		);

		CPInstance[] cpInstances = _addCPInstances(
			_commerceCatalog.getGroupId(), _CP_INSTANCES_COUNT);

		int counter = 0;
		int position = 0;

		long[] cpDefinitionIds = new long[_CP_INSTANCES_COUNT / 2];

		for (CPInstance cpInstance : cpInstances) {
			if ((counter % 2) == 0) {
				CPDefinition cpDefinition = cpInstance.getCPDefinition();

				cpDefinitionIds[position] = cpDefinition.getCPDefinitionId();

				position++;
			}

			counter++;
		}

		AssetCategory assetCategory = CPTestUtil.addCategoryToCPDefinitions(
			_commerceCatalog.getGroupId(), cpDefinitionIds);

		SearchContext searchContext = CPTestUtil.getSearchContext(
			null, WorkflowConstants.STATUS_APPROVED,
			_commerceCatalog.getGroup());

		CPQuery cpQuery = new CPQuery();

		cpQuery.setAllCategoryIds(new long[] {assetCategory.getCategoryId()});

		CPDataSourceResult cpDataSourceResult = _cpDefinitionHelper.search(
			_commerceCatalog.getGroupId(), searchContext, cpQuery,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		List<CPCatalogEntry> cpCatalogEntries =
			cpDataSourceResult.getCPCatalogEntries();

		Assert.assertEquals(
			cpCatalogEntries.toString(), cpDefinitionIds.length,
			cpCatalogEntries.size());

		List<Long> actualCPDefinitionIds = TransformUtil.transform(
			cpCatalogEntries,
			cpCatalogEntry -> cpCatalogEntry.getCPDefinitionId());

		List<Long> cpDefinitionIdsList = TransformUtil.transformToList(
			cpDefinitionIds, cpDefinitionId -> cpDefinitionId);

		Assert.assertTrue(
			actualCPDefinitionIds.containsAll(cpDefinitionIdsList));

		AssetCategoryLocalServiceUtil.deleteCategory(
			assetCategory.getCategoryId());
	}

	@Test
	public void testSearchCPDefinitionsByLocalizedName()
		throws PortalException {

		frutillaRule.scenario(
			"Search for CPDefinitions by localized name"
		).given(
			"A CPDefinition with a name and a short description in Portuguese"
		).when(
			"I search for the Portuguese name with the Portuguese locale"
		).then(
			"The result contains the Portuguese name and short description"
		);

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			_commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		String name = RandomTestUtil.randomString();
		String shortDescription = RandomTestUtil.randomString();

		_cpDefinitionLocalService.updateCPDefinitionLocalization(
			cpDefinition, LocaleUtil.toLanguageId(LocaleUtil.BRAZIL), null,
			null, null, null, name, shortDescription);

		Indexer<CPDefinition> indexer = _indexerRegistry.getIndexer(
			CPDefinition.class);

		indexer.reindex(cpDefinition);

		SearchContext searchContext = CPTestUtil.getSearchContext(
			name, WorkflowConstants.STATUS_APPROVED,
			_commerceCatalog.getGroup());

		searchContext.setLocale(LocaleUtil.BRAZIL);

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setLocale(LocaleUtil.BRAZIL);

		CPDataSourceResult cpDataSourceResult = _cpDefinitionHelper.search(
			_commerceCatalog.getGroupId(), searchContext, new CPQuery(),
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		List<CPCatalogEntry> cpCatalogEntries =
			cpDataSourceResult.getCPCatalogEntries();

		Assert.assertEquals(
			cpCatalogEntries.toString(), 1, cpCatalogEntries.size());

		CPCatalogEntry cpCatalogEntry = cpCatalogEntries.get(0);

		Assert.assertEquals(
			cpDefinition.getCPDefinitionId(),
			cpCatalogEntry.getCPDefinitionId());
		Assert.assertEquals(name, cpCatalogEntry.getName());
		Assert.assertEquals(
			shortDescription, cpCatalogEntry.getShortDescription());
	}

	@Test
	public void testSearchCPDefinitionsByName() throws PortalException {
		frutillaRule.scenario(
			"Search for CPDefinitions by name"
		).given(
			"A collection of CPDefinitions"
		).when(
			"I search for CPDefinitions given the name as a search filter"
		).then(
			"The results will contain only those product with the name equal " +
				"to the one in the search filter. In this specific case 1"
		);

		CPInstance[] cpInstances = _addCPInstances(
			_commerceCatalog.getGroupId(), _CP_INSTANCES_COUNT);

		int random = (int)(Math.random() * (_CP_INSTANCES_COUNT - 1));

		CPInstance randomCPInstance = cpInstances[random];

		CPDefinition cpDefinition = randomCPInstance.getCPDefinition();

		CPDataSourceResult cpDataSourceResult = _cpDefinitionHelper.search(
			_commerceCatalog.getGroupId(),
			CPTestUtil.getSearchContext(
				cpDefinition.getName(), WorkflowConstants.STATUS_APPROVED,
				_commerceCatalog.getGroup()),
			new CPQuery(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		List<CPCatalogEntry> cpCatalogEntries =
			cpDataSourceResult.getCPCatalogEntries();

		Assert.assertEquals(
			cpCatalogEntries.toString(), 1, cpCatalogEntries.size());

		CPCatalogEntry cpCatalogEntry = cpCatalogEntries.get(0);

		Assert.assertEquals(
			cpDefinition.getCPDefinitionId(),
			cpCatalogEntry.getCPDefinitionId());
	}

	@FeatureFlag("LPD-109588")
	@Test
	public void testSearchCPDefinitionsWhenGuestBrowsingIsDisabled()
		throws Exception {

		frutillaRule.scenario(
			"Search for CPDefinitions as a guest"
		).given(
			"A collection of CPDefinitions"
		).and(
			"A channel with guest browsing disabled"
		).when(
			"A guest searches for CPDefinitions in that channel"
		).then(
			"The results will not contain any product"
		);

		CommerceCurrency commerceCurrency =
			CommerceCurrencyTestUtil.addCommerceCurrency(
				_company.getCompanyId());

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.addCommerceChannel(
				StringPool.BLANK, AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
				RandomTestUtil.nextLong(), RandomTestUtil.randomString(),
				CommerceChannelConstants.CHANNEL_TYPE_SITE, null,
				commerceCurrency.getCode(),
				ServiceContextTestUtil.getServiceContext(
					_company.getGroupId()));

		_addCPInstances(_commerceCatalog.getGroupId(), _CP_INSTANCES_COUNT);

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_company.getGuestUser()));

		CPDataSourceResult cpDataSourceResult = _cpDefinitionHelper.search(
			commerceChannel.getGroupId(),
			CPTestUtil.getSearchContext(
				null, WorkflowConstants.STATUS_APPROVED,
				_commerceCatalog.getGroup()),
			new CPQuery(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			_CP_INSTANCES_COUNT, cpDataSourceResult.getLength());

		CommerceTestUtil.runWithGuestBrowsingDisabled(
			commerceChannel.getGroupId(),
			() -> {
				CPDataSourceResult guestCPDataSourceResult =
					_cpDefinitionHelper.search(
						commerceChannel.getGroupId(),
						CPTestUtil.getSearchContext(
							null, WorkflowConstants.STATUS_APPROVED,
							_commerceCatalog.getGroup()),
						new CPQuery(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);

				Assert.assertEquals(0, guestCPDataSourceResult.getLength());
			});
	}

	@Rule
	public final FrutillaRule frutillaRule = new FrutillaRule();

	private CPInstance[] _addCPInstances(long groupId, int iterations)
		throws PortalException {

		CPInstance[] cpInstances = new CPInstance[iterations];

		for (int i = 0; i < iterations; i++) {
			cpInstances[i] = CPTestUtil.addCPInstanceFromCatalog(groupId);
		}

		return cpInstances;
	}

	private static final int _CP_INSTANCES_COUNT = 10;

	private static Company _company;

	private CommerceCatalog _commerceCatalog;

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@Inject
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Inject
	private CPDefinitionHelper _cpDefinitionHelper;

	@Inject
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Inject
	private IndexerRegistry _indexerRegistry;

}