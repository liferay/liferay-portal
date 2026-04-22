/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.entry.rel.service.AssetEntryAssetCategoryRelLocalService;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.commerce.product.configuration.CProductVersionConfiguration;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.Category;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Zoltán Takács
 */
@RunWith(Arquillian.class)
public class CategoryResourceTest extends BaseCategoryResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_adminUser = UserTestUtil.getAdminUser(testCompany.getCompanyId());
	}

	@Ignore
	@Override
	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		super.testBatchEngineDeleteImportTask();
	}

	@Ignore
	@Override
	@Test
	public void testClientSerDesToDTO() throws Exception {
		super.testClientSerDesToDTO();
	}

	@Ignore
	@Override
	@Test
	public void testClientSerDesToJSON() throws Exception {
		super.testClientSerDesToJSON();
	}

	@Ignore
	@Override
	@Test
	public void testEscapeRegexInStringFields() throws Exception {
		super.testEscapeRegexInStringFields();
	}

	@Ignore
	@Override
	@Test
	public void testGetProductByExternalReferenceCodeCategoriesPage()
		throws Exception {

		super.testGetProductByExternalReferenceCodeCategoriesPage();
	}

	@Ignore
	@Override
	@Test
	public void testGetProductByExternalReferenceCodeCategoriesPageWithPagination()
		throws Exception {

		super.
			testGetProductByExternalReferenceCodeCategoriesPageWithPagination();
	}

	@Ignore
	@Override
	@Test
	public void testGetProductIdCategoriesPage() throws Exception {
		super.testGetProductIdCategoriesPage();
	}

	@Ignore
	@Override
	@Test
	public void testGetProductIdCategoriesPageWithPagination()
		throws Exception {

		super.testGetProductIdCategoriesPageWithPagination();
	}

	@Ignore
	@Override
	@Test
	public void testPatchProductByExternalReferenceCodeCategory()
		throws Exception {

		super.testPatchProductByExternalReferenceCodeCategory();
	}

	@Ignore
	@Override
	@Test
	public void testPatchProductIdCategory() throws Exception {
		super.testPatchProductIdCategory();
	}

	@Test
	public void testPostProductIdCategoryProductVersioning() throws Exception {
		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						testCompany.getCompanyId(),
						CProductVersionConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"enabled", true
						).build())) {

			CommerceCatalog commerceCatalog =
				CPTestUtil.getSystemCommerceCatalog(testCompany.getCompanyId());

			CPDefinition cpDefinition1 = CPTestUtil.addCPDefinitionFromCatalog(
				commerceCatalog.getGroupId(), "simple", null, null, true, true,
				WorkflowConstants.STATUS_APPROVED);

			_cpDefinitions.add(cpDefinition1);

			ServiceContext serviceContext =
				ServiceContextTestUtil.getServiceContext(
					testGroup.getGroupId());

			AssetVocabulary assetVocabulary =
				_assetVocabularyLocalService.addVocabulary(
					_adminUser.getUserId(), testGroup.getGroupId(),
					RandomTestUtil.randomString(), serviceContext);

			AssetCategory assetCategory =
				_assetCategoryLocalService.addCategory(
					_adminUser.getUserId(), testGroup.getGroupId(),
					RandomTestUtil.randomString(),
					assetVocabulary.getVocabularyId(), serviceContext);

			Assert.assertEquals(
				1,
				_cpDefinitionLocalService.getCProductCPDefinitions(
					cpDefinition1.getCProductId(),
					WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS
				).size());

			long classNameId = _classNameLocalService.getClassNameId(
				CPDefinition.class.getName());

			Assert.assertEquals(
				0,
				_getCategoriesCount(
					classNameId, cpDefinition1.getCPDefinitionId()));

			Category category1 = new Category();

			category1.setId(assetCategory.getCategoryId());

			categoryResource.patchProductIdCategory(
				cpDefinition1.getCProductId(), new Category[] {category1});

			Assert.assertEquals(
				1,
				_cpDefinitionLocalService.getCProductCPDefinitions(
					cpDefinition1.getCProductId(),
					WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS
				).size());

			List<CPDefinition> draftDefinitions =
				_cpDefinitionLocalService.getCProductCPDefinitions(
					cpDefinition1.getCProductId(),
					WorkflowConstants.STATUS_DRAFT, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS);

			Assert.assertEquals(
				draftDefinitions.toString(), 1, draftDefinitions.size());

			CPDefinition cpDefinition2 = draftDefinitions.get(0);

			_cpDefinitions.add(cpDefinition2);

			Assert.assertEquals(
				0,
				_getCategoriesCount(
					classNameId, cpDefinition1.getCPDefinitionId()));

			Assert.assertEquals(
				1,
				_getCategoriesCount(
					classNameId, cpDefinition2.getCPDefinitionId()));
		}
	}

	private int _getCategoriesCount(long classNameId, long classPK) {
		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			classNameId, classPK);

		if (assetEntry == null) {
			return 0;
		}

		return _assetEntryAssetCategoryRelLocalService.
			getAssetEntryAssetCategoryRelsCount(assetEntry.getEntryId());
	}

	private User _adminUser;

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetEntryAssetCategoryRelLocalService
		_assetEntryAssetCategoryRelLocalService;

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@DeleteAfterTestRun
	private List<CPDefinition> _cpDefinitions = new ArrayList<>();

	@Inject
	private CPDefinitionService _cpDefinitionService;

	@Inject
	private ServiceContextHelper _serviceContextHelper;

}