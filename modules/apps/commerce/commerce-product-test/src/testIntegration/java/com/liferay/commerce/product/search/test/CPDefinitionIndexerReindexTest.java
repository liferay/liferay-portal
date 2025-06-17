/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.constants.CPAttachmentFileEntryConstants;
import com.liferay.commerce.product.constants.CPField;
import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPOption;
import com.liferay.commerce.product.model.CPSpecificationOption;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPAttachmentFileEntryLocalService;
import com.liferay.commerce.product.service.CPConfigurationEntryLocalService;
import com.liferay.commerce.product.service.CPConfigurationListLocalService;
import com.liferay.commerce.product.service.CPDefinitionSpecificationOptionValueLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.util.BatchEngineImportTaskThreadLocal;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.math.BigDecimal;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@RunWith(Arquillian.class)
public class CPDefinitionIndexerReindexTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_serviceContext = ServiceContextTestUtil.getServiceContext(
			TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
			TestPropsValues.getUserId());

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				LocaleUtil.US.getDisplayLanguage(), _serviceContext);

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		_cpDefinition = cpInstance.getCPDefinition();

		_indexer = _indexerRegistry.getIndexer(CPDefinition.class);
	}

	@Test
	public void testReindex() throws Exception {
		Document document = _getDocument();

		List<String> cpConfigurationListIds = document.getStrings(
			CPField.CP_CONFIGURATION_LIST_IDS);
		String defaultImageFileEntryId = document.getString(
			CPField.DEFAULT_IMAGE_FILE_ENTRY_ID);
		String sku = document.getString(CPField.SKUS);
		String specificationName = document.getString(
			CPField.SPECIFICATION_NAMES);

		try (SafeCloseable safeCloseable =
				BatchEngineImportTaskThreadLocal.setEnabledWithSafeCloseable(
					true)) {

			Calendar calendar = Calendar.getInstance();

			CPConfigurationList cpConfigurationList =
				_cpConfigurationListLocalService.addCPConfigurationList(
					RandomTestUtil.randomString(), TestPropsValues.getUserId(),
					TestPropsValues.getGroupId(), 0, false,
					RandomTestUtil.randomString(), 0D,
					calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH),
					calendar.get(Calendar.YEAR), calendar.get(Calendar.HOUR),
					calendar.get(Calendar.MINUTE), 0, 0, 0, 0, 0, true);

			_cpConfigurationEntryLocalService.addCPConfigurationEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				cpConfigurationList.getGroupId(),
				_classNameLocalService.getClassNameId(CPDefinition.class),
				_cpDefinition.getCPDefinitionId(),
				cpConfigurationList.getCPConfigurationListId(), 0, "123", true,
				0, "cpde", 1.0, true, true, true, 1.0, "lowstoc",
				BigDecimal.TEN, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE,
				true, true, 1.0, true, true, true, 1.0, 1.0);

			document = _getDocument();

			Assert.assertEquals(
				cpConfigurationListIds.size(),
				document.getStrings(
					CPField.CP_CONFIGURATION_LIST_IDS
				).size());

			FileEntry fileEntry = _dlAppLocalService.addFileEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				TestPropsValues.getGroupId(),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				null, null, null, RandomTestUtil.nextDate(), _serviceContext);

			_cpAttachmentFileEntryLocalService.addCPAttachmentFileEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				TestPropsValues.getGroupId(),
				_classNameLocalService.getClassNameId(CPDefinition.class),
				_cpDefinition.getCPDefinitionId(), fileEntry.getFileEntryId(),
				false, null, calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH),
				calendar.get(Calendar.YEAR), calendar.get(Calendar.HOUR),
				calendar.get(Calendar.MINUTE), 0, 0, 0, 0, 0, true, true,
				RandomTestUtil.randomLocaleStringMap(), null,
				RandomTestUtil.nextDouble(),
				CPAttachmentFileEntryConstants.TYPE_IMAGE, _serviceContext);

			document = _getDocument();

			Assert.assertEquals(
				defaultImageFileEntryId,
				document.getString(CPField.DEFAULT_IMAGE_FILE_ENTRY_ID));

			CPTestUtil.addCPDefinitionCPInstance(
				_cpDefinition.getCPDefinitionId(), Collections.emptyMap());

			document = _getDocument();

			Assert.assertEquals(sku, document.getString(CPField.SKUS));

			CPSpecificationOption cpSpecificationOption =
				CPTestUtil.addCPSpecificationOption(
					TestPropsValues.getGroupId(), true);

			_cpDefinitionSpecificationOptionValueLocalService.
				addCPDefinitionSpecificationOptionValue(
					RandomTestUtil.randomString(),
					_cpDefinition.getCPDefinitionId(),
					cpSpecificationOption.getCPSpecificationOptionId(),
					cpSpecificationOption.getCPOptionCategoryId(),
					RandomTestUtil.randomDouble(),
					RandomTestUtil.randomLocaleStringMap(), true,
					_serviceContext);

			document = _getDocument();

			Assert.assertEquals(
				specificationName,
				document.getString(CPField.SPECIFICATION_NAMES));
		}

		_indexer.reindex(_cpDefinition);

		document = _getDocument();

		Assert.assertNotEquals(
			cpConfigurationListIds.size(),
			document.getStrings(
				CPField.CP_CONFIGURATION_LIST_IDS
			).size());
		Assert.assertNotEquals(
			defaultImageFileEntryId,
			document.getString(CPField.DEFAULT_IMAGE_FILE_ENTRY_ID));
		Assert.assertNotEquals(sku, document.getString(CPField.SKUS));
		Assert.assertNotEquals(
			specificationName, document.getString(CPField.SPECIFICATION_NAMES));

		CPOption cpOption = CPTestUtil.addCPOption(
			_cpDefinition.getGroupId(), false);

		CPTestUtil.addCPDefinitionOptionRel(
			_cpDefinition.getGroupId(), _cpDefinition.getCPDefinitionId(),
			cpOption.getCPOptionId());

		document = _getDocument();

		Assert.assertNotNull(document.getString(CPField.OPTION_NAMES));
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	private Document _getDocument() throws PortalException {
		SearchResponse searchResponse = _searcher.search(
			_searchRequestBuilderFactory.builder(
			).companyId(
				TestPropsValues.getCompanyId()
			).emptySearchEnabled(
				true
			).fields(
				StringPool.STAR
			).modelIndexerClasses(
				CPDefinition.class
			).withSearchContext(
				searchContext -> {
					BooleanQueryImpl booleanQueryImpl = new BooleanQueryImpl();

					BooleanFilter booleanFilter = new BooleanFilter();

					booleanFilter.add(
						new TermsFilter(Field.ENTRY_CLASS_PK) {
							{
								addValue(
									String.valueOf(
										_cpDefinition.getCPDefinitionId()));
							}
						},
						BooleanClauseOccur.MUST);

					booleanQueryImpl.setPreBooleanFilter(booleanFilter);

					searchContext.setBooleanClauses(
						new BooleanClause[] {
							BooleanClauseFactoryUtil.create(
								booleanQueryImpl,
								BooleanClauseOccur.MUST.getName())
						});

					searchContext.setCompanyId(_cpDefinition.getCompanyId());
				}
			).build());

		List<Document> documents = searchResponse.getDocuments();

		return documents.get(0);
	}

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@Inject
	private CPAttachmentFileEntryLocalService
		_cpAttachmentFileEntryLocalService;

	@Inject
	private CPConfigurationEntryLocalService _cpConfigurationEntryLocalService;

	@Inject
	private CPConfigurationListLocalService _cpConfigurationListLocalService;

	private CPDefinition _cpDefinition;

	@Inject
	private CPDefinitionSpecificationOptionValueLocalService
		_cpDefinitionSpecificationOptionValueLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	private Indexer<CPDefinition> _indexer;

	@Inject
	private IndexerRegistry _indexerRegistry;

	@Inject
	private Searcher _searcher;

	@Inject
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	private ServiceContext _serviceContext;

}