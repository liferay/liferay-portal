/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.channel.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.commerce.admin.channel.client.dto.v1_0.TaxCategory;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.StringUtil;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Andrea Sbarra
 * @author Alessio Antonio Rendina
 */
@RunWith(Arquillian.class)
public class TaxCategoryResourceTest extends BaseTaxCategoryResourceTestCase {

	@Ignore
	@Override
	@Test
	public void testGetTaxCategoriesPage() throws Exception {
		super.testGetTaxCategoriesPage();
	}

	@Ignore
	@Override
	@Test
	public void testGetTaxCategoriesPageWithPagination() throws Exception {
		super.testGetTaxCategoriesPageWithPagination();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetTaxCategoriesPage() throws Exception {
		super.testGraphQLGetTaxCategoriesPage();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetTaxCategory() throws Exception {
		super.testGraphQLGetTaxCategory();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetTaxCategoryByExternalReferenceCode()
		throws Exception {

		super.testGraphQLGetTaxCategoryByExternalReferenceCode();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetTaxCategoryByExternalReferenceCodeNotFound()
		throws Exception {

		super.testGraphQLGetTaxCategoryByExternalReferenceCodeNotFound();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetTaxCategoryNotFound() throws Exception {
		super.testGraphQLGetTaxCategoryNotFound();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLPostTaxCategory() throws Exception {
		super.testGraphQLPostTaxCategory();
	}

	@Override
	@Test
	public void testPatchTaxCategoryByExternalReferenceCode() throws Exception {
		super.testPatchTaxCategoryByExternalReferenceCode();

		_testPatchTaxCategoryByExternalReferenceCodeKeepsName();
	}

	@Override
	@Test
	public void testPostTaxCategory() throws Exception {
		super.testPostTaxCategory();

		_testPostTaxCategoryUpsertsByExternalReferenceCode();
	}

	@Ignore
	@Override
	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		super.testVulcanCRUDItemDelegateGetItem();
	}

	@Override
	protected TaxCategory randomTaxCategory() throws Exception {
		return new TaxCategory() {
			{
				description = LanguageUtils.getLanguageIdMap(
					RandomTestUtil.randomLocaleStringMap());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				name = LanguageUtils.getLanguageIdMap(
					RandomTestUtil.randomLocaleStringMap());
			}
		};
	}

	@Override
	protected TaxCategory testGetTaxCategory_addTaxCategory() throws Exception {
		return taxCategoryResource.postTaxCategory(randomTaxCategory());
	}

	@Override
	protected TaxCategory
			testGetTaxCategoryByExternalReferenceCode_addTaxCategory()
		throws Exception {

		return taxCategoryResource.postTaxCategory(randomTaxCategory());
	}

	@Override
	protected TaxCategory testPatchTaxCategory_addTaxCategory()
		throws Exception {

		return taxCategoryResource.postTaxCategory(randomTaxCategory());
	}

	@Override
	protected TaxCategory
			testPatchTaxCategoryByExternalReferenceCode_addTaxCategory()
		throws Exception {

		return taxCategoryResource.postTaxCategory(randomTaxCategory());
	}

	@Override
	protected TaxCategory testPostTaxCategory_addTaxCategory(
			TaxCategory taxCategory)
		throws Exception {

		return taxCategoryResource.postTaxCategory(taxCategory);
	}

	private void _testPatchTaxCategoryByExternalReferenceCodeKeepsName()
		throws Exception {

		TaxCategory postTaxCategory = taxCategoryResource.postTaxCategory(
			randomTaxCategory());

		TaxCategory patchTaxCategory = new TaxCategory() {
			{
				description = LanguageUtils.getLanguageIdMap(
					RandomTestUtil.randomLocaleStringMap());
			}
		};

		taxCategoryResource.patchTaxCategoryByExternalReferenceCode(
			postTaxCategory.getExternalReferenceCode(), patchTaxCategory);

		TaxCategory getTaxCategory =
			taxCategoryResource.getTaxCategoryByExternalReferenceCode(
				postTaxCategory.getExternalReferenceCode());

		Assert.assertEquals(
			postTaxCategory.getName(), getTaxCategory.getName());
		Assert.assertEquals(
			patchTaxCategory.getDescription(), getTaxCategory.getDescription());
	}

	private void _testPostTaxCategoryUpsertsByExternalReferenceCode()
		throws Exception {

		TaxCategory randomTaxCategory = randomTaxCategory();

		TaxCategory postTaxCategory = taxCategoryResource.postTaxCategory(
			randomTaxCategory);

		Assert.assertEquals(
			randomTaxCategory.getExternalReferenceCode(),
			postTaxCategory.getExternalReferenceCode());

		randomTaxCategory.setName(
			LanguageUtils.getLanguageIdMap(
				RandomTestUtil.randomLocaleStringMap()));

		TaxCategory upsertTaxCategory = taxCategoryResource.postTaxCategory(
			randomTaxCategory);

		Assert.assertEquals(postTaxCategory.getId(), upsertTaxCategory.getId());
		Assert.assertEquals(
			randomTaxCategory.getName(), upsertTaxCategory.getName());
	}

}