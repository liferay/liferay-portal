/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.channel.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.commerce.admin.channel.client.dto.v1_0.Creator;
import com.liferay.headless.commerce.admin.channel.client.dto.v1_0.TaxCategory;
import com.liferay.headless.commerce.admin.channel.client.pagination.Page;
import com.liferay.headless.commerce.admin.channel.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.channel.client.resource.v1_0.TaxCategoryResource;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;

import java.util.Collections;
import java.util.List;

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

	@Override
	@Test
	public void testGetTaxCategoriesPage() throws Exception {
		super.testGetTaxCategoriesPage();

		_testGetTaxCategoriesPageWithFilter();
	}

	@Ignore
	@Override
	@Test
	public void testGetTaxCategoriesPageWithFilterDateTimeEquals()
		throws Exception {
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
		_testPostTaxCategoryWithCreator();
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
	protected TaxCategory testGetTaxCategoriesPage_addTaxCategory(
			TaxCategory taxCategory)
		throws Exception {

		return taxCategoryResource.postTaxCategory(taxCategory);
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
	protected TaxCategory testGetTaxCategoryPermissionsPage_addTaxCategory()
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
	protected TaxCategory testPostTaxCategory_addPermissionsTaxCategory(
			TaxCategory taxCategory)
		throws Exception {

		return permissionsTaxCategoryResource.postTaxCategory(taxCategory);
	}

	@Override
	protected TaxCategory testPostTaxCategory_addTaxCategory(
			TaxCategory taxCategory)
		throws Exception {

		return taxCategoryResource.postTaxCategory(taxCategory);
	}

	@Override
	protected TaxCategory testPutTaxCategoryPermissionsPage_addTaxCategory()
		throws Exception {

		return taxCategoryResource.postTaxCategory(randomTaxCategory());
	}

	private void _testGetTaxCategoriesPageWithFilter() throws Exception {
		TaxCategory randomTaxCategory = randomTaxCategory();

		randomTaxCategory.setExternalReferenceCode(
			"ERC-" + RandomTestUtil.randomString());

		TaxCategory taxCategory = testGetTaxCategoriesPage_addTaxCategory(
			randomTaxCategory);

		Page<TaxCategory> taxCategoriesPage =
			taxCategoryResource.getTaxCategoriesPage(
				null,
				"externalReferenceCode eq '" +
					taxCategory.getExternalReferenceCode() + "'",
				Pagination.of(1, 10), null);

		Assert.assertEquals(1, taxCategoriesPage.getTotalCount());

		assertEquals(
			Collections.singletonList(taxCategory),
			(List<TaxCategory>)taxCategoriesPage.getItems());
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

	private void _testPostTaxCategoryWithCreator() throws Exception {
		String password = RandomTestUtil.randomString();
		User user = UserTestUtil.addOmniadminUser();

		_userLocalService.updatePassword(
			user.getUserId(), password, password, false, true);

		TaxCategoryResource taxCategoryResource = TaxCategoryResource.builder(
		).authentication(
			user.getEmailAddress(), password
		).locale(
			LocaleUtil.getDefault()
		).parameters(
			"nestedFields", "creator"
		).build();

		TaxCategory postTaxCategory = taxCategoryResource.postTaxCategory(
			randomTaxCategory());

		Creator creator = postTaxCategory.getCreator();

		Assert.assertEquals(
			user.getExternalReferenceCode(),
			creator.getExternalReferenceCode());
	}

	@Inject
	private UserLocalService _userLocalService;

}