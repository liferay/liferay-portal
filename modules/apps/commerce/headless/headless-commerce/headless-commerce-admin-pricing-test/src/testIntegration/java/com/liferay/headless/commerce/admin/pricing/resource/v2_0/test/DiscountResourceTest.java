/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.resource.v2_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.discount.constants.CommerceDiscountConstants;
import com.liferay.commerce.discount.constants.CommerceDiscountRuleConstants;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.model.CommerceDiscountOrderTypeRel;
import com.liferay.commerce.discount.model.CommerceDiscountRel;
import com.liferay.commerce.discount.model.CommerceDiscountRule;
import com.liferay.commerce.discount.service.CommerceDiscountLocalService;
import com.liferay.commerce.discount.service.CommerceDiscountOrderTypeRelLocalService;
import com.liferay.commerce.discount.service.CommerceDiscountRelLocalService;
import com.liferay.commerce.discount.service.CommerceDiscountRuleLocalService;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPInstanceUnitOfMeasure;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.model.CommerceChannelRel;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.service.CommerceChannelRelLocalService;
import com.liferay.commerce.product.service.CommerceChannelRelLocalServiceUtil;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.type.simple.constants.SimpleCPTypeConstants;
import com.liferay.commerce.service.CommerceOrderTypeLocalService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.exportimport.test.util.LazyReferencingTestUtil;
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.Creator;
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.Discount;
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.DiscountChannel;
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.DiscountOrderType;
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.DiscountProduct;
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.DiscountRule;
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.DiscountSku;
import com.liferay.headless.commerce.admin.pricing.client.pagination.Page;
import com.liferay.headless.commerce.admin.pricing.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.pricing.client.problem.Problem;
import com.liferay.headless.commerce.admin.pricing.client.resource.v2_0.DiscountResource;
import com.liferay.headless.commerce.admin.pricing.client.resource.v2_0.DiscountRuleResource;
import com.liferay.headless.commerce.core.util.DateConfig;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.SystemEvent;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.SystemEventLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Fabio Monaco
 */
@RunWith(Arquillian.class)
public class DiscountResourceTest extends BaseDiscountResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			testCompany.getCompanyId());

		User user = UserTestUtil.addUser(testCompany);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			testCompany.getCompanyId(), testGroup.getGroupId(),
			user.getUserId());
	}

	@Override
	@Test
	public void testDeleteDiscountByExternalReferenceCode() throws Exception {
		super.testDeleteDiscountByExternalReferenceCode();

		_testDeleteDiscountByExternalReferenceCodeWithSystemEvent();
	}

	@Override
	@Test
	public void testGetDiscountsPage() throws Exception {
		super.testGetDiscountsPage();

		_testGetDiscountsPageWithChannelFilter();
	}

	@Ignore
	@Override
	@Test
	public void testGetDiscountsPageWithFilterDateTimeEquals()
		throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGetDiscountsPageWithFilterStringContains()
		throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGetDiscountsPageWithFilterStringEquals() throws Exception {
		super.testGetDiscountsPageWithFilterStringEquals();
	}

	@Ignore
	@Override
	@Test
	public void testGetDiscountsPageWithFilterStringStartsWith()
		throws Exception {

		super.testGetDiscountsPageWithFilterStringStartsWith();
	}

	@Ignore
	@Override
	@Test
	public void testGetDiscountsPageWithSortString() throws Exception {
		super.testGetDiscountsPageWithSortString();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetDiscount() throws Exception {
		super.testGraphQLGetDiscount();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetDiscountByExternalReferenceCode()
		throws Exception {

		super.testGraphQLGetDiscountByExternalReferenceCode();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetDiscountByExternalReferenceCodeNotFound()
		throws Exception {

		super.testGraphQLGetDiscountByExternalReferenceCodeNotFound();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetDiscountNotFound() throws Exception {
		super.testGraphQLGetDiscountNotFound();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetDiscountsPage() throws Exception {
		super.testGraphQLGetDiscountsPage();
	}

	@Override
	@Test
	public void testPostDiscount() throws Exception {
		super.testPostDiscount();

		_testPostDiscountWithCreator();
		_testPostDiscountWithExistingIds();
		_testPostDiscountWithLazyReferencingDisabled();
		_testPostDiscountWithLazyReferencingEnabled();
		_testPostDiscountWithSkuUnitOfMeasure();
		_testPostDiscountWithTargetKey();
		_testPostDiscountWithTypeSettingsValue();
	}

	@Ignore
	@Override
	@Test
	public void testPutDiscountByExternalReferenceCode() throws Exception {
		super.testPutDiscountByExternalReferenceCode();
	}

	@Override
	protected Discount randomDiscount() throws Exception {
		Discount discount = super.randomDiscount();

		discount.setLevel(CommerceDiscountConstants.LEVEL_L1);
		discount.setLimitationType(
			CommerceDiscountConstants.LIMITATION_TYPE_UNLIMITED);
		discount.setNeverExpire(true);
		discount.setTarget(CommerceDiscountConstants.TARGET_TOTAL);
		discount.setTargetKey(CommerceDiscountConstants.TARGET_TOTAL);
		discount.setUseCouponCode(false);
		discount.setUsePercentage(false);

		return discount;
	}

	@Override
	protected Discount testDeleteDiscount_addDiscount() throws Exception {
		return discountResource.postDiscount(randomDiscount());
	}

	@Override
	protected Discount testDeleteDiscountByExternalReferenceCode_addDiscount()
		throws Exception {

		return discountResource.postDiscount(randomDiscount());
	}

	@Override
	protected Discount testGetDiscount_addDiscount() throws Exception {
		return discountResource.postDiscount(randomDiscount());
	}

	@Override
	protected Discount testGetDiscountByExternalReferenceCode_addDiscount()
		throws Exception {

		return discountResource.postDiscount(randomDiscount());
	}

	@Override
	protected Discount testGetDiscountPermissionsPage_addDiscount()
		throws Exception {

		return discountResource.postDiscount(randomDiscount());
	}

	@Override
	protected Discount testGetDiscountsPage_addDiscount(Discount discount)
		throws Exception {

		return discountResource.postDiscount(discount);
	}

	@Override
	protected Discount testPatchDiscount_addDiscount() throws Exception {
		return discountResource.postDiscount(randomDiscount());
	}

	@Override
	protected Discount testPatchDiscountByExternalReferenceCode_addDiscount()
		throws Exception {

		return discountResource.postDiscount(randomDiscount());
	}

	@Override
	protected Discount testPostDiscount_addDiscount(Discount discount)
		throws Exception {

		return discountResource.postDiscount(discount);
	}

	@Override
	protected Discount testPostDiscount_addPermissionsDiscount(
			Discount discount)
		throws Exception {

		return permissionsDiscountResource.postDiscount(discount);
	}

	@Override
	protected Discount testPutDiscountByExternalReferenceCode_addDiscount()
		throws Exception {

		return discountResource.postDiscount(randomDiscount());
	}

	@Override
	protected Discount testPutDiscountPermissionsPage_addDiscount()
		throws Exception {

		return discountResource.postDiscount(randomDiscount());
	}

	private DiscountRule _randomDiscountRule() {
		DiscountRule discountRule = new DiscountRule();

		discountRule.setExternalReferenceCode(
			StringUtil.toLowerCase(RandomTestUtil.randomString()));
		discountRule.setName(RandomTestUtil.randomString());
		discountRule.setType(CommerceDiscountRuleConstants.TYPE_CART_TOTAL);
		discountRule.setTypeSettingsValue(
			String.valueOf(RandomTestUtil.randomInt()));

		return discountRule;
	}

	private Discount _randomDiscountWithEmptyTargets() throws Exception {
		Discount discount = randomDiscount();

		DiscountChannel discountChannel = new DiscountChannel();

		discountChannel.setChannelExternalReferenceCode(
			StringUtil.toLowerCase(RandomTestUtil.randomString()));

		discount.setDiscountChannels(new DiscountChannel[] {discountChannel});

		DiscountProduct discountProduct = new DiscountProduct();

		String catalogExternalReferenceCode = StringUtil.toLowerCase(
			RandomTestUtil.randomString());
		String productExternalReferenceCode = StringUtil.toLowerCase(
			RandomTestUtil.randomString());

		discountProduct.setCatalogCurrencyCode(_commerceCurrency.getCode());
		discountProduct.setCatalogCurrencyExternalReferenceCode(
			_commerceCurrency.getExternalReferenceCode());
		discountProduct.setCatalogExternalReferenceCode(
			catalogExternalReferenceCode);
		discountProduct.setProductExternalReferenceCode(
			productExternalReferenceCode);
		discountProduct.setProductType(SimpleCPTypeConstants.NAME);

		discount.setDiscountProducts(new DiscountProduct[] {discountProduct});

		DiscountSku discountSku = new DiscountSku();

		discountSku.setCatalogCurrencyCode(_commerceCurrency.getCode());
		discountSku.setCatalogCurrencyExternalReferenceCode(
			_commerceCurrency.getExternalReferenceCode());
		discountSku.setCatalogExternalReferenceCode(
			catalogExternalReferenceCode);
		discountSku.setProductExternalReferenceCode(
			productExternalReferenceCode);
		discountSku.setProductType(SimpleCPTypeConstants.NAME);
		discountSku.setSkuExternalReferenceCode(
			StringUtil.toLowerCase(RandomTestUtil.randomString()));

		discount.setDiscountSkus(new DiscountSku[] {discountSku});

		discount.setExternalReferenceCode(
			StringUtil.toLowerCase(RandomTestUtil.randomString()));

		return discount;
	}

	private Discount _randomDiscountWithSku(
			String skuExternalReferenceCode, String unitOfMeasureKey)
		throws Exception {

		Discount discount = randomDiscount();

		DiscountSku discountSku = new DiscountSku();

		discountSku.setSkuExternalReferenceCode(skuExternalReferenceCode);
		discountSku.setUnitOfMeasureKey(unitOfMeasureKey);

		discount.setDiscountSkus(new DiscountSku[] {discountSku});

		discount.setExternalReferenceCode(
			StringUtil.toLowerCase(RandomTestUtil.randomString()));

		return discount;
	}

	private void _testDeleteDiscountByExternalReferenceCodeWithSystemEvent()
		throws Exception {

		Discount discount =
			testDeleteDiscountByExternalReferenceCode_addDiscount();

		discountResource.deleteDiscountByExternalReferenceCode(
			discount.getExternalReferenceCode());

		List<SystemEvent> systemEvents =
			_systemEventLocalService.getSystemEvents(
				0,
				_classNameLocalService.getClassNameId(CommerceDiscount.class),
				discount.getId(), SystemEventConstants.TYPE_DELETE);

		Assert.assertEquals(systemEvents.toString(), 1, systemEvents.size());

		SystemEvent systemEvent = systemEvents.get(0);

		Assert.assertEquals(
			discount.getExternalReferenceCode(),
			systemEvent.getClassExternalReferenceCode());
	}

	private void _testGetDiscountsPageWithChannelFilter() throws Exception {
		CommerceChannel commerceChannel1 = CommerceTestUtil.addCommerceChannel(
			testGroup.getGroupId(), _commerceCurrency.getCode());
		CommerceChannel commerceChannel2 = CommerceTestUtil.addCommerceChannel(
			testGroup.getGroupId(), _commerceCurrency.getCode());

		_commerceChannels.add(commerceChannel1);
		_commerceChannels.add(commerceChannel2);

		Discount discount1 = discountResource.postDiscount(randomDiscount());
		Discount discount2 = discountResource.postDiscount(randomDiscount());

		_commerceDiscounts.add(
			_commerceDiscountLocalService.getCommerceDiscount(
				discount1.getId()));
		_commerceDiscounts.add(
			_commerceDiscountLocalService.getCommerceDiscount(
				discount2.getId()));

		CommerceChannelRelLocalServiceUtil.addCommerceChannelRel(
			CommerceDiscount.class.getName(), discount1.getId(),
			commerceChannel1.getCommerceChannelId(), _serviceContext);
		CommerceChannelRelLocalServiceUtil.addCommerceChannelRel(
			CommerceDiscount.class.getName(), discount2.getId(),
			commerceChannel2.getCommerceChannelId(), _serviceContext);

		Indexer<CommerceDiscount> indexer =
			IndexerRegistryUtil.nullSafeGetIndexer(CommerceDiscount.class);

		indexer.reindex(CommerceDiscount.class.getName(), discount1.getId());
		indexer.reindex(CommerceDiscount.class.getName(), discount2.getId());

		Page<Discount> page = discountResource.getDiscountsPage(
			null,
			String.format(
				"(channelId/any(x:(x eq %s)))",
				commerceChannel1.getCommerceChannelId()),
			Pagination.of(1, 10), null);

		assertEquals(
			Collections.singletonList(discount1),
			(List<Discount>)page.getItems());
	}

	private void _testPostDiscountWithCreator() throws Exception {
		String password = RandomTestUtil.randomString();
		User user = UserTestUtil.addOmniadminUser();

		_userLocalService.updatePassword(
			user.getUserId(), password, password, false, true);

		DiscountResource discountResource = DiscountResource.builder(
		).authentication(
			user.getEmailAddress(), password
		).locale(
			LocaleUtil.getDefault()
		).parameters(
			"nestedFields", "creator"
		).build();

		Discount postDiscount = discountResource.postDiscount(randomDiscount());

		Creator creator = postDiscount.getCreator();

		Assert.assertEquals(
			user.getExternalReferenceCode(),
			creator.getExternalReferenceCode());
	}

	private void _testPostDiscountWithExistingIds() throws Exception {
		Discount randomDiscount1 = randomDiscount();

		DiscountRule randomDiscountRule1 = _randomDiscountRule();

		randomDiscount1.setDiscountRules(
			new DiscountRule[] {randomDiscountRule1});

		Discount postDiscount1 = discountResource.postDiscount(randomDiscount1);

		Discount randomDiscount2 = randomDiscount();

		randomDiscount2.setId(postDiscount1.getId());

		DiscountRule randomDiscountRule2 = _randomDiscountRule();

		CommerceDiscountRule commerceDiscountRule =
			_commerceDiscountRuleLocalService.
				fetchCommerceDiscountRuleByExternalReferenceCode(
					randomDiscountRule1.getExternalReferenceCode(),
					testCompany.getCompanyId());

		Assert.assertNotNull(commerceDiscountRule);

		randomDiscountRule2.setId(
			commerceDiscountRule.getCommerceDiscountRuleId());

		randomDiscount2.setDiscountRules(
			new DiscountRule[] {randomDiscountRule2});

		Discount postDiscount2 = discountResource.postDiscount(randomDiscount2);

		Assert.assertNotEquals(postDiscount1.getId(), postDiscount2.getId());

		Discount discount = discountResource.getDiscount(postDiscount1.getId());

		Assert.assertEquals(postDiscount1.getTitle(), discount.getTitle());

		commerceDiscountRule =
			_commerceDiscountRuleLocalService.getCommerceDiscountRule(
				commerceDiscountRule.getCommerceDiscountRuleId());

		Assert.assertEquals(
			randomDiscountRule1.getName(), commerceDiscountRule.getName());
	}

	private void _testPostDiscountWithLazyReferencingDisabled()
		throws Exception {

		try {
			discountResource.postDiscount(_randomDiscountWithEmptyTargets());

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
		}
	}

	private void _testPostDiscountWithLazyReferencingEnabled()
		throws Exception {

		Discount discount = _randomDiscountWithEmptyTargets();

		DiscountChannel[] discountChannels = discount.getDiscountChannels();

		DiscountChannel discountChannel = discountChannels[0];

		CommerceChannel existingCommerceChannel =
			CommerceTestUtil.addCommerceChannel(
				testGroup.getGroupId(), _commerceCurrency.getCode());

		_commerceChannels.add(existingCommerceChannel);

		discountChannel.setChannelId(
			existingCommerceChannel.getCommerceChannelId());

		DiscountOrderType discountOrderType = new DiscountOrderType();

		User user = _userLocalService.getUser(_serviceContext.getUserId());

		DateConfig displayDateConfig = DateConfig.toDisplayDateConfig(
			RandomTestUtil.nextDate(), user.getTimeZone());
		DateConfig expirationDateConfig = DateConfig.toExpirationDateConfig(
			RandomTestUtil.nextDate(), user.getTimeZone());

		CommerceOrderType existingCommerceOrderType =
			_commerceOrderTypeLocalService.addCommerceOrderType(
				RandomTestUtil.randomString(), _serviceContext.getUserId(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomBoolean(), displayDateConfig.getMonth(),
				displayDateConfig.getDay(), displayDateConfig.getYear(),
				displayDateConfig.getHour(), displayDateConfig.getMinute(), 0,
				expirationDateConfig.getMonth(), expirationDateConfig.getDay(),
				expirationDateConfig.getYear(), expirationDateConfig.getHour(),
				expirationDateConfig.getMinute(), true, _serviceContext);

		_commerceOrderTypes.add(existingCommerceOrderType);

		discountOrderType.setOrderTypeExternalReferenceCode(
			StringUtil.toLowerCase(RandomTestUtil.randomString()));
		discountOrderType.setOrderTypeId(
			existingCommerceOrderType.getCommerceOrderTypeId());
		discountOrderType.setPriority(1);

		discount.setDiscountOrderTypes(
			new DiscountOrderType[] {discountOrderType});

		DiscountRule discountRule = new DiscountRule();

		discountRule.setExternalReferenceCode(
			StringUtil.toLowerCase(RandomTestUtil.randomString()));
		discountRule.setName(RandomTestUtil.randomString());
		discountRule.setType(CommerceDiscountRuleConstants.TYPE_ADDED_ALL);
		discountRule.setTypeSettings(RandomTestUtil.randomString());

		discount.setDiscountRules(new DiscountRule[] {discountRule});

		try (SafeCloseable safeCloseable =
				LazyReferencingTestUtil.setLazyReferencingWithSafeCloseable(
					true)) {

			discountResource.postDiscount(discount);

			discountOrderType.setPriority(2);

			discountRule.setName(RandomTestUtil.randomString());
			discountRule.setType(CommerceDiscountRuleConstants.TYPE_ADDED_ANY);

			discountResource.putDiscountByExternalReferenceCode(
				discount.getExternalReferenceCode(), discount);
		}

		DiscountSku[] discountSkus = discount.getDiscountSkus();

		DiscountSku discountSku = discountSkus[0];

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.
				fetchCommerceCatalogByExternalReferenceCode(
					discountSku.getCatalogExternalReferenceCode(),
					testCompany.getCompanyId());

		_commerceCatalogs.add(commerceCatalog);

		Assert.assertEquals(
			WorkflowConstants.STATUS_EMPTY, commerceCatalog.getStatus());

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.
				fetchCommerceChannelByExternalReferenceCode(
					discountChannel.getChannelExternalReferenceCode(),
					testCompany.getCompanyId());

		_commerceChannels.add(commerceChannel);

		Assert.assertEquals(
			WorkflowConstants.STATUS_EMPTY, commerceChannel.getStatus());

		CPInstance cpInstance =
			_cpInstanceLocalService.fetchCPInstanceByExternalReferenceCode(
				discountSku.getSkuExternalReferenceCode(),
				testCompany.getCompanyId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_EMPTY, cpInstance.getStatus());

		CommerceDiscount commerceDiscount =
			_commerceDiscountLocalService.
				fetchCommerceDiscountByExternalReferenceCode(
					discount.getExternalReferenceCode(),
					testCompany.getCompanyId());

		List<CommerceDiscountRel> commerceDiscountRels =
			_commerceDiscountRelLocalService.getCommerceDiscountRels(
				commerceDiscount.getCommerceDiscountId(),
				CPInstance.class.getName());

		Assert.assertEquals(
			commerceDiscountRels.toString(), 1, commerceDiscountRels.size());

		CommerceDiscountRel commerceDiscountRel = commerceDiscountRels.get(0);

		Assert.assertEquals(
			cpInstance.getCPInstanceId(), commerceDiscountRel.getClassPK());

		List<CommerceChannelRel> commerceChannelRels =
			_commerceChannelRelLocalService.getCommerceChannelRels(
				CommerceDiscount.class.getName(),
				commerceDiscount.getCommerceDiscountId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			commerceChannelRels.toString(), 1, commerceChannelRels.size());

		CommerceChannelRel commerceChannelRel = commerceChannelRels.get(0);

		Assert.assertEquals(
			commerceChannel.getCommerceChannelId(),
			commerceChannelRel.getCommerceChannelId());

		List<CommerceDiscountOrderTypeRel> commerceDiscountOrderTypeRels =
			_commerceDiscountOrderTypeRelLocalService.
				getCommerceDiscountOrderTypeRels(
					commerceDiscount.getCommerceDiscountId());

		Assert.assertEquals(
			commerceDiscountOrderTypeRels.toString(), 1,
			commerceDiscountOrderTypeRels.size());

		CommerceOrderType commerceOrderType =
			_commerceOrderTypeLocalService.
				fetchCommerceOrderTypeByExternalReferenceCode(
					discountOrderType.getOrderTypeExternalReferenceCode(),
					testCompany.getCompanyId());

		_commerceOrderTypes.add(commerceOrderType);

		CommerceDiscountOrderTypeRel commerceDiscountOrderTypeRel =
			commerceDiscountOrderTypeRels.get(0);

		Assert.assertEquals(
			commerceOrderType.getCommerceOrderTypeId(),
			commerceDiscountOrderTypeRel.getCommerceOrderTypeId());

		Assert.assertEquals(2, commerceDiscountOrderTypeRel.getPriority());

		List<CommerceDiscountRule> commerceDiscountRules =
			_commerceDiscountRuleLocalService.getCommerceDiscountRules(
				commerceDiscount.getCommerceDiscountId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			commerceDiscountRules.toString(), 1, commerceDiscountRules.size());

		CommerceDiscountRule commerceDiscountRule = commerceDiscountRules.get(
			0);

		Assert.assertEquals(
			discountRule.getExternalReferenceCode(),
			commerceDiscountRule.getExternalReferenceCode());
		Assert.assertEquals(
			discountRule.getName(), commerceDiscountRule.getName());
		Assert.assertEquals(
			discountRule.getType(), commerceDiscountRule.getType());

		_commerceDiscountLocalService.deleteCommerceDiscount(commerceDiscount);
	}

	private void _testPostDiscountWithSkuUnitOfMeasure() throws Exception {
		_cpInstance = CPTestUtil.addCPInstanceWithRandomSku(
			testGroup.getGroupId(), BigDecimal.TEN);

		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure1 =
			CPTestUtil.addCPInstanceUnitOfMeasure(
				testGroup.getGroupId(), _cpInstance.getCPInstanceId(),
				RandomTestUtil.randomString(), BigDecimal.ONE,
				_cpInstance.getSku());

		Discount discount = _randomDiscountWithSku(
			_cpInstance.getExternalReferenceCode(),
			cpInstanceUnitOfMeasure1.getKey());

		discountResource.postDiscount(discount);

		DiscountSku[] discountSkus = discount.getDiscountSkus();

		DiscountSku discountSku = discountSkus[0];

		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure2 =
			CPTestUtil.addCPInstanceUnitOfMeasure(
				testGroup.getGroupId(), _cpInstance.getCPInstanceId(),
				RandomTestUtil.randomString(), BigDecimal.ONE,
				_cpInstance.getSku());

		discountSku.setUnitOfMeasureKey(cpInstanceUnitOfMeasure2.getKey());

		discountResource.putDiscountByExternalReferenceCode(
			discount.getExternalReferenceCode(), discount);

		CommerceDiscount commerceDiscount =
			_commerceDiscountLocalService.
				fetchCommerceDiscountByExternalReferenceCode(
					discount.getExternalReferenceCode(),
					testCompany.getCompanyId());

		_commerceDiscounts.add(commerceDiscount);

		List<CommerceDiscountRel> commerceDiscountRels =
			_commerceDiscountRelLocalService.getCommerceDiscountRels(
				commerceDiscount.getCommerceDiscountId(),
				CPInstance.class.getName());

		Assert.assertEquals(
			commerceDiscountRels.toString(), 1, commerceDiscountRels.size());

		CommerceDiscountRel commerceDiscountRel = commerceDiscountRels.get(0);

		UnicodeProperties typeSettingsUnicodeProperties =
			commerceDiscountRel.getTypeSettingsUnicodeProperties();

		Assert.assertEquals(
			cpInstanceUnitOfMeasure2.getKey(),
			typeSettingsUnicodeProperties.getProperty("unitOfMeasureKey"));
	}

	private void _testPostDiscountWithTargetKey() throws Exception {
		Discount discount = randomDiscount();

		discount.setTarget(CommerceDiscountConstants.TARGET_SUBTOTAL);

		Discount postDiscount = discountResource.postDiscount(discount);

		Assert.assertEquals(
			CommerceDiscountConstants.TARGET_TOTAL,
			postDiscount.getTargetKey());

		discount.setTargetKey((String)null);

		Discount putDiscount =
			discountResource.putDiscountByExternalReferenceCode(
				discount.getExternalReferenceCode(), discount);

		Assert.assertEquals(
			CommerceDiscountConstants.TARGET_SUBTOTAL,
			putDiscount.getTargetKey());
	}

	private void _testPostDiscountWithTypeSettingsValue() throws Exception {
		Discount discount = randomDiscount();

		DiscountRule randomDiscountRule = _randomDiscountRule();

		discount.setDiscountRules(new DiscountRule[] {randomDiscountRule});

		discountResource.postDiscount(discount);

		CommerceDiscount commerceDiscount =
			_commerceDiscountLocalService.
				fetchCommerceDiscountByExternalReferenceCode(
					discount.getExternalReferenceCode(),
					testCompany.getCompanyId());

		_commerceDiscounts.add(commerceDiscount);

		List<CommerceDiscountRule> commerceDiscountRules =
			_commerceDiscountRuleLocalService.getCommerceDiscountRules(
				commerceDiscount.getCommerceDiscountId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		CommerceDiscountRule commerceDiscountRule = commerceDiscountRules.get(
			0);

		randomDiscountRule.setTypeSettings(
			commerceDiscountRule.getTypeSettings());

		discountResource.putDiscountByExternalReferenceCode(
			discount.getExternalReferenceCode(), discount);

		commerceDiscountRule =
			_commerceDiscountRuleLocalService.getCommerceDiscountRule(
				commerceDiscountRule.getCommerceDiscountRuleId());

		Assert.assertEquals(
			randomDiscountRule.getTypeSettingsValue(),
			commerceDiscountRule.getSettingsProperty(
				CommerceDiscountRuleConstants.TYPE_CART_TOTAL));

		User omniadminUser = UserTestUtil.addOmniadminUser();
		String password = RandomTestUtil.randomString();

		_userLocalService.updatePassword(
			omniadminUser.getUserId(), password, password, false, true);

		DiscountRuleResource discountRuleResource =
			DiscountRuleResource.builder(
			).authentication(
				omniadminUser.getEmailAddress(), password
			).locale(
				LocaleUtil.getDefault()
			).build();

		DiscountRule discountRule = discountRuleResource.getDiscountRule(
			commerceDiscountRule.getCommerceDiscountRuleId());

		Assert.assertEquals(
			randomDiscountRule.getTypeSettingsValue(),
			discountRule.getTypeSettingsValue());
	}

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@DeleteAfterTestRun
	private List<CommerceCatalog> _commerceCatalogs = new ArrayList<>();

	@Inject
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Inject
	private CommerceChannelRelLocalService _commerceChannelRelLocalService;

	@DeleteAfterTestRun
	private List<CommerceChannel> _commerceChannels = new ArrayList<>();

	private CommerceCurrency _commerceCurrency;

	@Inject
	private CommerceDiscountLocalService _commerceDiscountLocalService;

	@Inject
	private CommerceDiscountOrderTypeRelLocalService
		_commerceDiscountOrderTypeRelLocalService;

	@Inject
	private CommerceDiscountRelLocalService _commerceDiscountRelLocalService;

	@Inject
	private CommerceDiscountRuleLocalService _commerceDiscountRuleLocalService;

	@DeleteAfterTestRun
	private List<CommerceDiscount> _commerceDiscounts = new ArrayList<>();

	@Inject
	private CommerceOrderTypeLocalService _commerceOrderTypeLocalService;

	@DeleteAfterTestRun
	private List<CommerceOrderType> _commerceOrderTypes = new ArrayList<>();

	@DeleteAfterTestRun
	private CPInstance _cpInstance;

	@Inject
	private CPInstanceLocalService _cpInstanceLocalService;

	private ServiceContext _serviceContext;

	@Inject
	private SystemEventLocalService _systemEventLocalService;

	@Inject
	private UserLocalService _userLocalService;

}