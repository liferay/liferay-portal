/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.channel.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDisplayLayout;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPDisplayLayoutLocalService;
import com.liferay.commerce.product.service.CProductLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.headless.commerce.admin.channel.client.dto.v1_0.ProductDisplayPage;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.test.rule.Inject;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Danny Situ
 */
@RunWith(Arquillian.class)
public class ProductDisplayPageResourceTest
	extends BaseProductDisplayPageResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_user = UserTestUtil.addOmniadminUser();

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			testCompany.getCompanyId());

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			testCompany.getGroupId(), _commerceCurrency.getCode());

		_commerceCatalog = CommerceTestUtil.addCommerceCatalog(
			testCompany.getCompanyId(), testCompany.getGroupId(),
			_user.getUserId(), _commerceCurrency.getCode());
	}

	@Override
	@Test
	public void testPatchProductDisplayPage() throws Exception {
		super.testPatchProductDisplayPage();

		_testPatchProductDisplayPageWithProductExternalReferenceCode();
	}

	@Override
	@Test
	public void testPostChannelByExternalReferenceCodeProductDisplayPage()
		throws Exception {

		super.testPostChannelByExternalReferenceCodeProductDisplayPage();

		_testPostChannelByExternalReferenceCodeProductDisplayPageWithProductExternalReferenceCode();
	}

	@Override
	@Test
	public void testPostChannelIdProductDisplayPage() throws Exception {
		super.testPostChannelIdProductDisplayPage();

		_testPostChannelIdProductDisplayPageWithProductExternalReferenceCode();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"pageUuid"};
	}

	@Override
	protected ProductDisplayPage randomProductDisplayPage() throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(
			testCompany.getGroupId());

		_layouts.add(layout);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinition(
			_commerceCatalog.getGroupId());

		_cpDefinitions.add(cpDefinition);

		return new ProductDisplayPage() {
			{
				id = RandomTestUtil.randomLong();
				pageTemplateUuid = StringPool.BLANK;
				pageUuid = layout.getUuid();
				productId = cpDefinition.getCPDefinitionId();
			}
		};
	}

	@Override
	protected ProductDisplayPage
			testDeleteProductDisplayPage_addProductDisplayPage()
		throws Exception {

		return _addChannelProductDisplayPage(
			_commerceChannel, randomProductDisplayPage());
	}

	@Override
	protected ProductDisplayPage
			testGetChannelByExternalReferenceCodeProductDisplayPagesPage_addProductDisplayPage(
				String externalReferenceCode,
				ProductDisplayPage productDisplayPage)
		throws Exception {

		return _addChannelProductDisplayPage(
			externalReferenceCode, productDisplayPage);
	}

	@Override
	protected String
			testGetChannelByExternalReferenceCodeProductDisplayPagesPage_getExternalReferenceCode()
		throws Exception {

		return _commerceChannel.getExternalReferenceCode();
	}

	@Override
	protected ProductDisplayPage
			testGetChannelIdProductDisplayPagesPage_addProductDisplayPage(
				Long id, ProductDisplayPage productDisplayPage)
		throws Exception {

		return _addChannelProductDisplayPage(
			_commerceChannel, productDisplayPage);
	}

	@Override
	protected Long testGetChannelIdProductDisplayPagesPage_getId()
		throws Exception {

		return _commerceChannel.getCommerceChannelId();
	}

	@Override
	protected ProductDisplayPage
			testGetProductDisplayPage_addProductDisplayPage()
		throws Exception {

		return _addChannelProductDisplayPage(
			_commerceChannel, randomProductDisplayPage());
	}

	@Override
	protected ProductDisplayPage
			testGraphQLProductDisplayPage_addProductDisplayPage()
		throws Exception {

		return _addChannelProductDisplayPage(
			_commerceChannel, randomProductDisplayPage());
	}

	@Override
	protected ProductDisplayPage
			testPatchProductDisplayPage_addProductDisplayPage()
		throws Exception {

		return _addChannelProductDisplayPage(
			_commerceChannel, randomProductDisplayPage());
	}

	@Override
	protected ProductDisplayPage
			testPostChannelByExternalReferenceCodeProductDisplayPage_addProductDisplayPage(
				ProductDisplayPage productDisplayPage)
		throws Exception {

		return _addChannelProductDisplayPage(
			_commerceChannel, productDisplayPage);
	}

	@Override
	protected ProductDisplayPage
			testPostChannelIdProductDisplayPage_addProductDisplayPage(
				ProductDisplayPage productDisplayPage)
		throws Exception {

		return _addChannelProductDisplayPage(
			_commerceChannel, productDisplayPage);
	}

	private ProductDisplayPage _addChannelProductDisplayPage(
			CommerceChannel commerceChannel,
			ProductDisplayPage productDisplayPage)
		throws Exception {

		CPDisplayLayout cpDisplayLayout =
			_cpDisplayLayoutLocalService.addCPDisplayLayout(
				_user.getUserId(), commerceChannel.getSiteGroupId(),
				CPDefinition.class, productDisplayPage.getProductId(),
				productDisplayPage.getPageTemplateUuid(),
				productDisplayPage.getPageUuid());

		_cpDisplayLayouts.add(cpDisplayLayout);

		return new ProductDisplayPage() {
			{
				setId(cpDisplayLayout::getCPDisplayLayoutId);
				setPageTemplateUuid(
					cpDisplayLayout::getLayoutPageTemplateEntryUuid);
				setPageUuid(cpDisplayLayout::getLayoutUuid);
				setProductId(cpDisplayLayout::getClassPK);
			}
		};
	}

	private ProductDisplayPage _addChannelProductDisplayPage(
			String externalReferenceCode, ProductDisplayPage productDisplayPage)
		throws Exception {

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.
				fetchCommerceChannelByExternalReferenceCode(
					externalReferenceCode, testCompany.getCompanyId());

		if (commerceChannel == null) {
			commerceChannel = CommerceTestUtil.addCommerceChannel(
				testCompany.getGroupId(), _commerceCurrency.getCode());

			_commerceChannels.add(commerceChannel);
		}

		commerceChannel =
			_commerceChannelLocalService.
				updateCommerceChannelExternalReferenceCode(
					externalReferenceCode,
					commerceChannel.getCommerceChannelId());

		return _addChannelProductDisplayPage(
			commerceChannel, productDisplayPage);
	}

	private void _testPatchProductDisplayPageWithProductExternalReferenceCode()
		throws Exception {

		ProductDisplayPage postProductDisplayPage =
			testPatchProductDisplayPage_addProductDisplayPage();

		ProductDisplayPage randomPatchProductDisplayPage =
			randomPatchProductDisplayPage();

		long randomPatchProductDisplayPageProductId =
			randomPatchProductDisplayPage.getProductId();

		CPDefinition cpDefinition = _cpDefinitionLocalService.getCPDefinition(
			randomPatchProductDisplayPageProductId);

		CProduct cProduct = _cProductLocalService.getCProduct(
			cpDefinition.getCProductId());

		randomPatchProductDisplayPage.setProductExternalReferenceCode(
			cProduct.getExternalReferenceCode());

		randomPatchProductDisplayPage.setProductId(0L);

		ProductDisplayPage patchProductDisplayPage =
			productDisplayPageResource.patchProductDisplayPage(
				postProductDisplayPage.getId(), randomPatchProductDisplayPage);

		randomPatchProductDisplayPage.setProductId(
			randomPatchProductDisplayPageProductId);

		ProductDisplayPage expectedPatchProductDisplayPage =
			postProductDisplayPage.clone();

		BeanTestUtil.copyProperties(
			randomPatchProductDisplayPage, expectedPatchProductDisplayPage);

		ProductDisplayPage getProductDisplayPage =
			productDisplayPageResource.getProductDisplayPage(
				patchProductDisplayPage.getId());

		assertEquals(expectedPatchProductDisplayPage, getProductDisplayPage);
		assertValid(getProductDisplayPage);
		Assert.assertEquals(
			randomPatchProductDisplayPage.getProductExternalReferenceCode(),
			getProductDisplayPage.getProductExternalReferenceCode());
		Assert.assertEquals(
			randomPatchProductDisplayPageProductId,
			GetterUtil.getLong(getProductDisplayPage.getProductId()));
	}

	private void _testPostChannelByExternalReferenceCodeProductDisplayPageWithProductExternalReferenceCode()
		throws Exception {

		ProductDisplayPage randomProductDisplayPage =
			randomProductDisplayPage();

		CPDefinition cpDefinition = _cpDefinitionLocalService.getCPDefinition(
			randomProductDisplayPage.getProductId());

		CProduct cProduct = _cProductLocalService.getCProduct(
			cpDefinition.getCProductId());

		randomProductDisplayPage.setProductExternalReferenceCode(
			cProduct.getExternalReferenceCode());

		randomProductDisplayPage.setProductId(0L);

		ProductDisplayPage postProductDisplayPage =
			productDisplayPageResource.
				postChannelByExternalReferenceCodeProductDisplayPage(
					_commerceChannel.getExternalReferenceCode(),
					randomProductDisplayPage);

		assertEquals(randomProductDisplayPage, postProductDisplayPage);
		assertValid(postProductDisplayPage);
		Assert.assertEquals(
			randomProductDisplayPage.getProductExternalReferenceCode(),
			postProductDisplayPage.getProductExternalReferenceCode());
		Assert.assertEquals(
			cpDefinition.getCPDefinitionId(),
			GetterUtil.getLong(postProductDisplayPage.getProductId()));
	}

	private void _testPostChannelIdProductDisplayPageWithProductExternalReferenceCode()
		throws Exception {

		ProductDisplayPage randomProductDisplayPage =
			randomProductDisplayPage();

		CPDefinition cpDefinition = _cpDefinitionLocalService.getCPDefinition(
			randomProductDisplayPage.getProductId());

		CProduct cProduct = _cProductLocalService.getCProduct(
			cpDefinition.getCProductId());

		randomProductDisplayPage.setProductExternalReferenceCode(
			cProduct.getExternalReferenceCode());

		randomProductDisplayPage.setProductId(0L);

		ProductDisplayPage postProductDisplayPage =
			productDisplayPageResource.postChannelIdProductDisplayPage(
				_commerceChannel.getCommerceChannelId(),
				randomProductDisplayPage);

		assertEquals(randomProductDisplayPage, postProductDisplayPage);
		assertValid(postProductDisplayPage);
		Assert.assertEquals(
			randomProductDisplayPage.getProductExternalReferenceCode(),
			postProductDisplayPage.getProductExternalReferenceCode());
		Assert.assertEquals(
			cpDefinition.getCPDefinitionId(),
			GetterUtil.getLong(postProductDisplayPage.getProductId()));
	}

	@Inject
	private CProductLocalService _cProductLocalService;

	@DeleteAfterTestRun
	private CommerceCatalog _commerceCatalog;

	@DeleteAfterTestRun
	private CommerceChannel _commerceChannel;

	@Inject
	private CommerceChannelLocalService _commerceChannelLocalService;

	@DeleteAfterTestRun
	private List<CommerceChannel> _commerceChannels = new ArrayList<>();

	@DeleteAfterTestRun
	private CommerceCurrency _commerceCurrency;

	@Inject
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@DeleteAfterTestRun
	private List<CPDefinition> _cpDefinitions = new ArrayList<>();

	@Inject
	private CPDisplayLayoutLocalService _cpDisplayLayoutLocalService;

	@DeleteAfterTestRun
	private List<CPDisplayLayout> _cpDisplayLayouts = new ArrayList<>();

	@DeleteAfterTestRun
	private List<Layout> _layouts = new ArrayList<>();

	@DeleteAfterTestRun
	private User _user;

}