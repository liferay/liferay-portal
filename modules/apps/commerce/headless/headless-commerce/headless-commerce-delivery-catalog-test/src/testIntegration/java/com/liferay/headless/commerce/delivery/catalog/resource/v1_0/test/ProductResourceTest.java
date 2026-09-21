/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.resource.v1_0.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.product.model.CPConfigurationEntry;
import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPConfigurationEntryLocalService;
import com.liferay.commerce.product.service.CPConfigurationListLocalService;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalServiceUtil;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.headless.commerce.delivery.catalog.client.dto.v1_0.Product;
import com.liferay.headless.commerce.delivery.catalog.client.dto.v1_0.ProductConfiguration;
import com.liferay.headless.commerce.delivery.catalog.client.pagination.Page;
import com.liferay.headless.commerce.delivery.catalog.client.pagination.Pagination;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.test.rule.Inject;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Andrea Sbarra
 * @author Michele Vigilante
 */
@RunWith(Arquillian.class)
public class ProductResourceTest extends BaseProductResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			testGroup.getGroupId(), RandomTestUtil.randomString());

		_user = UserTestUtil.addUser(testCompany);

		_accountEntry = CommerceAccountTestUtil.getPersonAccountEntry(
			_user.getUserId());
		_serviceContext = ServiceContextTestUtil.getServiceContext(
			testCompany.getCompanyId(), testGroup.getGroupId(),
			_user.getUserId());
	}

	@Override
	@Test
	public void testGetChannelProductsPage() throws Exception {
		super.testGetChannelProductsPage();

		_testGetChannelProductsPageWithCustomFields();
		_testGetChannelProductsPageWithProductConfiguration();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"productType"};
	}

	@Override
	protected String[] getIgnoredEntityFieldNames() {
		return new String[] {"catalogId", "productType", "statusCode"};
	}

	@Override
	protected Product testGetChannelProduct_addProduct() throws Exception {
		return _addCPDefinition(randomProduct());
	}

	@Override
	protected Long testGetChannelProduct_getChannelId() throws Exception {
		return _commerceChannel.getCommerceChannelId();
	}

	@Override
	protected Product testGetChannelProductByFriendlyUrlPath_addProduct()
		throws Exception {

		return _addCPDefinition(randomProduct());
	}

	@Override
	protected Long testGetChannelProductByFriendlyUrlPath_getChannelId()
		throws Exception {

		return _commerceChannel.getCommerceChannelId();
	}

	@Override
	protected String testGetChannelProductByFriendlyUrlPath_getFriendlyUrlPath()
		throws Exception {

		return _addCPDefinition(
			randomProduct()
		).getSlug();
	}

	@Override
	protected Product testGetChannelProductsPage_addProduct(
			Long channelId, Product product)
		throws Exception {

		return _addCPDefinition(product);
	}

	@Override
	protected Long testGetChannelProductsPage_getChannelId() throws Exception {
		return _commerceChannel.getCommerceChannelId();
	}

	@Override
	protected Long testGraphQLGetChannelProduct_getChannelId()
		throws Exception {

		return _commerceChannel.getCommerceChannelId();
	}

	@Override
	protected Product testGraphQLGetChannelProductByFriendlyUrlPath_addProduct()
		throws Exception {

		return _addCPDefinition(randomProduct());
	}

	@Override
	protected Long testGraphQLGetChannelProductByFriendlyUrlPath_getChannelId()
		throws Exception {

		return _commerceChannel.getCommerceChannelId();
	}

	@Override
	protected String
			testGraphQLGetChannelProductByFriendlyUrlPath_getFriendlyUrlPath()
		throws Exception {

		return _addCPDefinition(
			randomProduct()
		).getSlug();
	}

	@Override
	protected Product testGraphQLProduct_addProduct() throws Exception {
		return _addCPDefinition(randomProduct());
	}

	private Product _addCPDefinition(Product product) throws Exception {
		CPDefinition cpDefinition1 = CPTestUtil.addCPDefinition(
			testGroup.getGroupId(), "simple", true, false);

		_cpDefinitions.add(cpDefinition1);

		Locale siteDefaultLocale = LocaleUtil.getSiteDefault();

		CPDefinition cpDefinition2 =
			_cpDefinitionLocalService.updateCPDefinition(
				cpDefinition1.getCPDefinitionId(),
				cpDefinition1.getCPTaxCategoryId(),
				cpDefinition1.isAccountGroupFilterEnabled(),
				cpDefinition1.isChannelFilterEnabled(),
				cpDefinition1.getDDMStructureKey(), cpDefinition1.getDepth(),
				HashMapBuilder.put(
					siteDefaultLocale, product.getDescription()
				).build(),
				1, 12, 0, 1, 2022, 0, 0, 0, 0, 0,
				cpDefinition1.isFreeShipping(), cpDefinition1.getHeight(),
				cpDefinition1.isIgnoreSKUCombinations(),
				cpDefinition1.getMetaDescriptionMap(),
				cpDefinition1.getMetaKeywordsMap(),
				cpDefinition1.getMetaTitleMap(),
				HashMapBuilder.put(
					siteDefaultLocale, product.getName()
				).build(),
				true, cpDefinition1.isPublished(),
				cpDefinition1.isShipSeparately(), cpDefinition1.isShippable(),
				cpDefinition1.getShippingExtraPrice(),
				HashMapBuilder.put(
					siteDefaultLocale, product.getShortDescription()
				).build(),
				cpDefinition1.isTaxExempt(),
				cpDefinition1.isTelcoOrElectronics(),
				cpDefinition1.getUrlTitleMap(), cpDefinition1.getWeight(),
				cpDefinition1.getWidth(), _serviceContext);

		_cpDefinitions.add(cpDefinition2);

		return new Product() {
			{
				createDate = cpDefinition2.getCreateDate();
				description = product.getDescription();
				id = cpDefinition2.getCProductId();
				metaDescription = cpDefinition2.getMetaDescription();
				metaKeyword = cpDefinition2.getMetaKeywords();
				metaTitle = cpDefinition2.getMetaTitle();
				modifiedDate = cpDefinition2.getModifiedDate();
				name = product.getName();
				productId = cpDefinition2.getCProductId();
				productType = cpDefinition2.getProductTypeName();
				shortDescription = product.getShortDescription();
				slug = cpDefinition2.getURL(
					LocaleUtil.toLanguageId(siteDefaultLocale));

				setExternalReferenceCode(
					() -> {
						CProduct cProduct = cpDefinition2.getCProduct();

						return cProduct.getExternalReferenceCode();
					});
			}
		};
	}

	private Product _randomProductWithCustomFields(
			String customFieldName, String customFieldValue)
		throws Exception {

		CPDefinition cpDefinition1 = CPTestUtil.addCPDefinition(
			testGroup.getGroupId(), "simple", true, false);

		_cpDefinitions.add(cpDefinition1);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(testGroup.getGroupId());

		serviceContext.setExpandoBridgeAttributes(
			Collections.singletonMap(customFieldName, customFieldValue));

		cpDefinition1.setExpandoBridgeAttributes(serviceContext);

		CPDefinition cpDefinition2 =
			_cpDefinitionLocalService.updateCPDefinition(cpDefinition1);

		return new Product() {
			{
				createDate = cpDefinition2.getCreateDate();
				id = cpDefinition2.getCProductId();
				metaDescription = cpDefinition2.getMetaDescription();
				metaKeyword = cpDefinition2.getMetaKeywords();
				metaTitle = cpDefinition2.getMetaTitle();
				modifiedDate = cpDefinition2.getModifiedDate();
				productId = cpDefinition2.getCProductId();
				productType = cpDefinition2.getProductTypeName();

				setExternalReferenceCode(
					() -> {
						CProduct cProduct = cpDefinition2.getCProduct();

						return cProduct.getExternalReferenceCode();
					});
			}
		};
	}

	private void _testGetChannelProductsPageWithCustomFields()
		throws Exception {

		User adminUser = UserTestUtil.getAdminUser(testGroup.getCompanyId());

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(adminUser));

		PrincipalThreadLocal.setName(adminUser.getUserId());

		ExpandoTable expandoTable = _expandoTableLocalService.addTable(
			testGroup.getCompanyId(),
			_classNameLocalService.getClassNameId(CPDefinition.class),
			"CUSTOM_FIELDS");

		ExpandoColumn expandoColumn = _expandoColumnLocalService.addColumn(
			expandoTable.getTableId(), "A" + RandomTestUtil.randomString(),
			ExpandoColumnConstants.STRING);

		UnicodeProperties unicodeProperties =
			expandoColumn.getTypeSettingsProperties();

		unicodeProperties.setProperty(
			ExpandoColumnConstants.INDEX_TYPE,
			String.valueOf(ExpandoColumnConstants.INDEX_TYPE_KEYWORD));

		expandoColumn.setTypeSettingsProperties(unicodeProperties);

		expandoColumn = _expandoColumnLocalService.updateExpandoColumn(
			expandoColumn);

		String customFieldValue = RandomTestUtil.randomString();

		Long channelId = testGetChannelProductsPage_getChannelId();

		Product randomProduct = _randomProductWithCustomFields(
			expandoColumn.getName(), customFieldValue);

		Product product = testGetChannelProductsPage_addProduct(
			channelId, randomProduct);

		Page<Product> page = productResource.getChannelProductsPage(
			channelId, null, null,
			StringBundler.concat(
				"(customFields/", expandoColumn.getName(), " eq '",
				RandomTestUtil.randomString(), "')"),
			Pagination.of(1, 2), null);

		Assert.assertEquals(0, page.getTotalCount());

		page = productResource.getChannelProductsPage(
			channelId, null, null,
			StringBundler.concat(
				"(customFields/", expandoColumn.getName(), " eq '",
				customFieldValue, "')"),
			Pagination.of(1, 2), null);

		Assert.assertEquals(1, page.getTotalCount());

		assertEquals(
			Collections.singletonList(product), (List<Product>)page.getItems());
	}

	private void _testGetChannelProductsPageWithProductConfiguration()
		throws Exception {

		CommerceCatalog commerceCatalog =
			CommerceCatalogLocalServiceUtil.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				LocaleUtil.US.getDisplayLanguage(), _serviceContext);

		CPConfigurationList cpConfigurationList =
			_cpConfigurationListLocalService.addCPConfigurationList(
				RandomTestUtil.randomString(), _user.getUserId(),
				commerceCatalog.getGroupId(), 0, false,
				RandomTestUtil.randomString(), 2, 1, 1, 2024, 0, 0, 0, 0, 0, 0,
				0, true, new ServiceContext());

		CPDefinition cpDefinition1 = CPTestUtil.addCPDefinition(
			commerceCatalog.getGroupId());

		_cpDefinitions.add(cpDefinition1);

		CPConfigurationEntry cpConfigurationEntry =
			_cpConfigurationEntryLocalService.addCPConfigurationEntry(
				RandomTestUtil.randomString(), _user.getUserId(),
				cpConfigurationList.getGroupId(),
				_portal.getClassNameId(CPDefinition.class),
				cpDefinition1.getCPDefinitionId(),
				cpConfigurationList.getCPConfigurationListId(), 0, "1,234.00",
				true, 0, "cpde", 1.0, true, true, true, 1.0, "lowstoc",
				BigDecimal.TEN, BigDecimal.TEN, BigDecimal.ONE, BigDecimal.ONE,
				true, true, 1.0, true, true, 1.0, 1.0);

		CPDefinition cpDefinition2 = CPTestUtil.addCPDefinition(
			commerceCatalog.getGroupId());

		_cpDefinitions.add(cpDefinition2);

		CPConfigurationList masterCPConfigurationList =
			_cpConfigurationListLocalService.getMasterCPConfigurationList(
				commerceCatalog.getGroupId());

		CPConfigurationEntry masterCPConfigurationEntry =
			_cpConfigurationEntryLocalService.getCPConfigurationEntry(
				_classNameLocalService.getClassNameId(CPDefinition.class),
				cpDefinition2.getCPDefinitionId(),
				masterCPConfigurationList.getCPConfigurationListId());

		Product product1 = productResource.getChannelProduct(
			_commerceChannel.getCommerceChannelId(),
			cpDefinition1.getCProductId(), _accountEntry.getAccountEntryId());

		ProductConfiguration productConfiguration1 =
			product1.getProductConfiguration();

		Assert.assertEquals(
			cpConfigurationEntry.getMaxOrderQuantity(),
			productConfiguration1.getMaxOrderQuantity());
		Assert.assertEquals(
			cpConfigurationEntry.getMinOrderQuantity(),
			productConfiguration1.getMinOrderQuantity());
		Assert.assertEquals(
			cpConfigurationEntry.getMultipleOrderQuantity(),
			productConfiguration1.getMultipleOrderQuantity());

		Product product2 = productResource.getChannelProduct(
			_commerceChannel.getCommerceChannelId(),
			cpDefinition2.getCProductId(), _accountEntry.getAccountEntryId());

		ProductConfiguration productConfiguration2 =
			product2.getProductConfiguration();

		Assert.assertEquals(
			masterCPConfigurationEntry.getMaxOrderQuantity(),
			productConfiguration2.getMaxOrderQuantity());
		Assert.assertEquals(
			masterCPConfigurationEntry.getMinOrderQuantity(),
			productConfiguration2.getMinOrderQuantity());
		Assert.assertEquals(
			masterCPConfigurationEntry.getMultipleOrderQuantity(),
			productConfiguration2.getMultipleOrderQuantity());
	}

	@DeleteAfterTestRun
	private AccountEntry _accountEntry;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@DeleteAfterTestRun
	private CommerceChannel _commerceChannel;

	@Inject
	private CPConfigurationEntryLocalService _cpConfigurationEntryLocalService;

	@Inject
	private CPConfigurationListLocalService _cpConfigurationListLocalService;

	@Inject
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@DeleteAfterTestRun
	private final List<CPDefinition> _cpDefinitions = new ArrayList<>();

	@Inject
	private ExpandoColumnLocalService _expandoColumnLocalService;

	@Inject
	private ExpandoTableLocalService _expandoTableLocalService;

	@Inject
	private Portal _portal;

	private ServiceContext _serviceContext;

	@DeleteAfterTestRun
	private User _user;

}