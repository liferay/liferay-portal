/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.cart.resource.v1_0.test;

import com.liferay.account.configuration.AccountEntryAddressSubtypeConfiguration;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.discount.constants.CommerceDiscountConstants;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.service.CommerceDiscountLocalService;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.headless.commerce.delivery.cart.client.dto.v1_0.Address;
import com.liferay.headless.commerce.delivery.cart.client.dto.v1_0.Cart;
import com.liferay.headless.commerce.delivery.cart.client.dto.v1_0.CouponCode;
import com.liferay.headless.commerce.delivery.cart.client.pagination.Page;
import com.liferay.headless.commerce.delivery.cart.client.pagination.Pagination;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.service.CountryLocalService;
import com.liferay.portal.kernel.service.RegionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.math.BigDecimal;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Andrea Sbarra
 */
@RunWith(Arquillian.class)
public class CartResourceTest extends BaseCartResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_user = UserTestUtil.addUser(testCompany);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			testCompany.getCompanyId(), testGroup.getGroupId(),
			_user.getUserId());

		_accountEntry = CommerceAccountTestUtil.addBusinessAccountEntry(
			_serviceContext.getUserId(), "Test Business Account", null, null,
			new long[] {_user.getUserId()}, null, _serviceContext);

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			testGroup.getCompanyId());

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			testGroup.getGroupId(), _commerceCurrency.getCode());

		Calendar calendar = CalendarFactoryUtil.getCalendar(
			_user.getTimeZone());

		_commerceDiscount = _commerceDiscountLocalService.addCommerceDiscount(
			_user.getUserId(), RandomTestUtil.randomString(),
			CommerceDiscountConstants.TARGET_SUBTOTAL, true,
			RandomTestUtil.randomString(), true, BigDecimal.TEN, BigDecimal.TEN,
			BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
			CommerceDiscountConstants.LIMITATION_TYPE_UNLIMITED, 0, true,
			calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
			calendar.get(Calendar.YEAR), calendar.get(Calendar.HOUR_OF_DAY),
			calendar.get(Calendar.MINUTE), calendar.get(Calendar.MONTH),
			calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR),
			calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
			true, _serviceContext);

		_country = _countryLocalService.addCountry(
			"XY", "XYZ", true, true, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.nextDouble(), true, true, false, _serviceContext);

		_region = _regionLocalService.addRegion(
			_country.getCountryId(), true, RandomTestUtil.randomString(),
			RandomTestUtil.nextDouble(), RandomTestUtil.randomString(),
			_serviceContext);
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		List<CommerceOrder> commerceOrders =
			_commerceOrderLocalService.getCommerceOrders(
				_commerceChannel.getGroupId(),
				_accountEntry.getAccountEntryId(), -1, -1, null);

		for (CommerceOrder commerceOrder : commerceOrders) {
			_commerceOrderLocalService.deleteCommerceOrder(
				commerceOrder.getCommerceOrderId());
		}

		if (_accountEntry != null) {
			_accountEntryLocalService.deleteAccountEntry(_accountEntry);
		}
	}

	@Override
	@Test
	public void testGetCartByExternalReferenceCodePaymentUrl()
		throws Exception {

		Cart cart = _createCart();

		String callbackURL = RandomTestUtil.randomString();

		Assert.assertEquals(
			StringBundler.concat(
				"http://localhost:8080/o/commerce-payment?groupId=",
				_commerceChannel.getGroupId(), "&nextStep=", callbackURL,
				"&uuid=", cart.getOrderUUID()),
			cartResource.getCartByExternalReferenceCodePaymentUrl(
				cart.getExternalReferenceCode(), callbackURL));
	}

	@Override
	@Test
	public void testGetCartPaymentURL() throws Exception {
		Cart cart = _createCart();

		String callbackURL = RandomTestUtil.randomString();

		Assert.assertEquals(
			StringBundler.concat(
				"http://localhost:8080/o/commerce-payment?groupId=",
				_commerceChannel.getGroupId(), "&nextStep=", callbackURL,
				"&uuid=", cart.getOrderUUID()),
			cartResource.getCartPaymentURL(cart.getId(), callbackURL));
	}

	@Override
	@Test
	public void testGetChannelCartsPage() throws Exception {
		super.testGetChannelCartsPage();

		_testGetChannelCartsPageWithFilter();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteCart() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteCartByExternalReferenceCode()
		throws Exception {
	}

	@Override
	@Test
	public void testPatchCart() throws Exception {
		super.testPatchCart();

		_testPatchCartWithAddressSubtype();
		_testPatchCartWithMoreExternalReferenceCodes();
	}

	@Override
	@Test
	public void testPatchCartByExternalReferenceCode() throws Exception {
		super.testPatchCartByExternalReferenceCode();

		_testPatchCartByExternalReferenceCodeWithMoreExternalReferenceCodes();
	}

	@Override
	@Test
	public void testPostChannelCart() throws Exception {
		super.testPostChannelCart();

		_testPostChannelCartWithMoreExternalReferenceCodes();
	}

	@Override
	@Test
	public void testPostChannelCartByExternalReferenceCode() throws Exception {
		super.testPostChannelCartByExternalReferenceCode();

		_testPostChannelCartByExternalReferenceCodeWithMoreExternalReferenceCodes();
	}

	@Override
	@Test
	public void testPutCart() throws Exception {
		super.testPutCart();

		_testPutCartWithMoreExternalReferenceCodes();
	}

	@Override
	@Test
	public void testPutCartByExternalReferenceCode() throws Exception {
		super.testPutCartByExternalReferenceCode();

		_testPutCartByExternalReferenceCodeWithMoreExternalReferenceCodes();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"account", "accountId", "billingAddressId", "shippingAddressId",
			"status"
		};
	}

	@Override
	protected String[] getIgnoredEntityFieldNames() {
		return new String[] {
			"account", "accountId", "author", "name", "orderDate", "orderId",
			"orderType", "purchaseOrderNumber"
		};
	}

	@Override
	protected Cart randomCart() throws Exception {
		return new Cart() {
			{
				account = _accountEntry.getName();
				accountId = _accountEntry.getAccountEntryId();
				billingAddressId = 0L;
				channelId = _commerceChannel.getCommerceChannelId();
				couponCode = RandomTestUtil.randomString();
				currencyCode = _commerceCurrency.getCode();
				currencyExternalReferenceCode =
					_commerceCurrency.getExternalReferenceCode();
				currencyId = _commerceCurrency.getCommerceCurrencyId();
				externalReferenceCode = RandomTestUtil.randomString();
				name = RandomTestUtil.randomString();
				orderTypeId = RandomTestUtil.randomLong();
				orderUUID = RandomTestUtil.randomString();
				paymentStatus = RandomTestUtil.randomInt();
				shippingAddressId = 0L;
				status = WorkflowConstants.getStatusLabel(
					WorkflowConstants.STATUS_APPROVED);
			}
		};
	}

	@Override
	protected Cart testDeleteCart_addCart() throws Exception {
		Cart cart = randomCart();

		return cartResource.postChannelCart(cart.getChannelId(), cart);
	}

	@Override
	protected Cart testDeleteCartByExternalReferenceCode_addCart()
		throws Exception {

		return cartResource.postChannelCartByExternalReferenceCode(
			_commerceChannel.getExternalReferenceCode(), randomCart());
	}

	@Override
	protected Cart testGetCart_addCart() throws Exception {
		Cart cart = randomCart();

		return cartResource.postChannelCart(cart.getChannelId(), cart);
	}

	@Override
	protected Cart testGetCartByExternalReferenceCode_addCart()
		throws Exception {

		return cartResource.postChannelCartByExternalReferenceCode(
			_commerceChannel.getExternalReferenceCode(), randomCart());
	}

	@Override
	protected Cart testGetChannelAccountCartsPage_addCart(
			Long accountId, Long channelId, Cart cart)
		throws Exception {

		return cartResource.postChannelCart(channelId, cart);
	}

	@Override
	protected Long testGetChannelAccountCartsPage_getAccountId()
		throws Exception {

		return _accountEntry.getAccountEntryId();
	}

	@Override
	protected Long testGetChannelAccountCartsPage_getChannelId()
		throws Exception {

		return _commerceChannel.getCommerceChannelId();
	}

	@Override
	protected Cart
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage_addCart(
				String accountExternalReferenceCode,
				String channelExternalReferenceCode, Cart cart)
		throws Exception {

		return cartResource.postChannelCartByExternalReferenceCode(
			channelExternalReferenceCode, cart);
	}

	@Override
	protected String
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage_getAccountExternalReferenceCode()
		throws Exception {

		return _accountEntry.getExternalReferenceCode();
	}

	@Override
	protected String
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage_getChannelExternalReferenceCode()
		throws Exception {

		return _commerceChannel.getExternalReferenceCode();
	}

	@Override
	protected Cart testGetChannelCartsPage_addCart(Long channelId, Cart cart)
		throws Exception {

		return cartResource.postChannelCart(channelId, cart);
	}

	@Override
	protected Long testGetChannelCartsPage_getChannelId() throws Exception {
		return _commerceChannel.getCommerceChannelId();
	}

	@Override
	protected Cart testGraphQLCart_addCart() throws Exception {
		Cart cart = randomCart();

		return cartResource.postChannelCart(cart.getChannelId(), cart);
	}

	@Override
	protected Cart testPatchCart_addCart() throws Exception {
		return _createCart();
	}

	@Override
	protected Cart testPatchCartByExternalReferenceCode_addCart()
		throws Exception {

		return _createCart();
	}

	@Override
	protected Cart testPostCartByExternalReferenceCodeCheckout_addCart(
			Cart cart)
		throws Exception {

		Cart createCart = _createCart();

		return cartResource.postCartByExternalReferenceCodeCheckout(
			createCart.getExternalReferenceCode());
	}

	@Override
	protected Cart testPostCartByExternalReferenceCodeCouponCode_addCart(
			Cart cart)
		throws Exception {

		Cart createCart = _createCart();

		_updateCommerceDiscount(cart.getCouponCode());

		return cartResource.postCartByExternalReferenceCodeCouponCode(
			createCart.getExternalReferenceCode(),
			new CouponCode() {
				{
					code = cart.getCouponCode();
				}
			});
	}

	@Override
	protected Cart testPostCartCheckout_addCart(Cart cart) throws Exception {
		Cart createCart = _createCart();

		return cartResource.postCartCheckout(createCart.getId());
	}

	@Override
	protected Cart testPostCartCouponCode_addCart(Cart cart) throws Exception {
		Cart createCart = _createCart();

		_updateCommerceDiscount(cart.getCouponCode());

		return cartResource.postCartCouponCode(
			createCart.getId(),
			new CouponCode() {
				{
					code = cart.getCouponCode();
				}
			});
	}

	@Override
	protected Cart testPostChannelCartByExternalReferenceCode_addCart(Cart cart)
		throws Exception {

		return cartResource.postChannelCartByExternalReferenceCode(
			_commerceChannel.getExternalReferenceCode(), cart);
	}

	@Override
	protected Cart testPutCart_addCart() throws Exception {
		Cart cart = randomCart();

		return cartResource.postChannelCart(cart.getChannelId(), cart);
	}

	@Override
	protected Cart testPutCartByExternalReferenceCode_addCart()
		throws Exception {

		return cartResource.postChannelCartByExternalReferenceCode(
			_commerceChannel.getExternalReferenceCode(), randomCart());
	}

	@Override
	protected Cart testPutCartByExternalReferenceCode_createCart()
		throws Exception {

		return _createCart();
	}

	private CommerceOrder _addCommerceOrder() throws Exception {
		return _commerceOrderLocalService.addCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_accountEntry.getAccountEntryId(), _commerceCurrency.getCode(), 0);
	}

	private Cart _createCart() throws Exception {
		CommerceOrder commerceOrder = _addCommerceOrder();

		return new Cart() {
			{
				account = commerceOrder.getCommerceAccountName();
				accountId = commerceOrder.getCommerceAccountId();
				billingAddressId = commerceOrder.getBillingAddressId();
				couponCode = commerceOrder.getCouponCode();
				currencyCode = _commerceCurrency.getCode();
				currencyExternalReferenceCode =
					_commerceCurrency.getExternalReferenceCode();
				currencyId = _commerceCurrency.getCommerceCurrencyId();
				externalReferenceCode =
					commerceOrder.getExternalReferenceCode();
				id = commerceOrder.getCommerceOrderId();
				name = commerceOrder.getName();
				orderTypeId = commerceOrder.getCommerceOrderTypeId();
				orderUUID = commerceOrder.getUuid();
				paymentStatus = commerceOrder.getPaymentStatus();
				shippingAddressId = commerceOrder.getShippingAddressId();
				status = WorkflowConstants.getStatusLabel(
					commerceOrder.getStatus());
			}
		};
	}

	private void _testGetChannelCartsPageWithFilter() throws Exception {
		CommerceOrder commerceOrder = _addCommerceOrder();

		AccountEntry accountEntry =
			CommerceAccountTestUtil.addBusinessAccountEntry(
				_serviceContext.getUserId(),
				RandomTestUtil.randomString() + StringPool.SEMICOLON, null,
				null, new long[] {_user.getUserId()}, null, _serviceContext);

		commerceOrder.setCommerceAccountId(accountEntry.getAccountEntryId());

		commerceOrder.setExternalReferenceCode(
			RandomTestUtil.randomString() + StringPool.CLOSE_CURLY_BRACE);
		commerceOrder.setName(RandomTestUtil.randomString() + StringPool.AT);
		commerceOrder.setPurchaseOrderNumber(
			RandomTestUtil.randomString() + StringPool.AMPERSAND);

		commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
			commerceOrder);

		Page<Cart> page = cartResource.getChannelCartsPage(
			_commerceChannel.getCommerceChannelId(), null,
			String.format("(account eq '%s')", accountEntry.getName()),
			Pagination.of(1, 10), null);

		Assert.assertEquals(1, page.getTotalCount());

		page = cartResource.getChannelCartsPage(
			_commerceChannel.getCommerceChannelId(), null,
			String.format(
				"(externalReferenceCode eq '%s')",
				commerceOrder.getExternalReferenceCode()),
			Pagination.of(1, 10), null);

		Assert.assertEquals(1, page.getTotalCount());

		page = cartResource.getChannelCartsPage(
			_commerceChannel.getCommerceChannelId(), null,
			String.format("(name eq '%s')", commerceOrder.getName()),
			Pagination.of(1, 10), null);

		Assert.assertEquals(1, page.getTotalCount());

		page = cartResource.getChannelCartsPage(
			_commerceChannel.getCommerceChannelId(), null,
			String.format(
				"(purchaseOrderNumber eq '%s')",
				commerceOrder.getPurchaseOrderNumber()),
			Pagination.of(1, 10), null);

		Assert.assertEquals(1, page.getTotalCount());
	}

	private void _testPatchCartByExternalReferenceCodeWithMoreExternalReferenceCodes()
		throws Exception {

		Cart postCart = cartResource.postChannelCart(
			_commerceChannel.getCommerceChannelId(), randomCart());

		Cart randomPatchCart = randomPatchCart();

		com.liferay.portal.kernel.model.Address serviceBuilderAddress =
			_addressLocalService.addAddress(
				RandomTestUtil.randomString(), _user.getUserId(),
				AccountEntry.class.getName(), _accountEntry.getAccountEntryId(),
				_country.getCountryId(), 0, _region.getRegionId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				false, RandomTestUtil.randomString(), true,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				_serviceContext);

		randomPatchCart.setBillingAddressExternalReferenceCode(
			serviceBuilderAddress.getExternalReferenceCode());

		randomPatchCart.setBillingAddressId(0L);
		randomPatchCart.setShippingAddressExternalReferenceCode(
			serviceBuilderAddress.getExternalReferenceCode());
		randomPatchCart.setShippingAddressId(0L);

		Cart patchCart = cartResource.patchCartByExternalReferenceCode(
			postCart.getExternalReferenceCode(), randomPatchCart);

		randomPatchCart.setBillingAddressId(
			serviceBuilderAddress.getAddressId());
		randomPatchCart.setShippingAddressId(
			serviceBuilderAddress.getAddressId());

		Cart expectedPatchCart = postCart.clone();

		BeanTestUtil.copyProperties(randomPatchCart, expectedPatchCart);

		Cart getCart = cartResource.getCartByExternalReferenceCode(
			patchCart.getExternalReferenceCode());

		assertEquals(expectedPatchCart, getCart);
		assertValid(getCart);
		Assert.assertEquals(
			serviceBuilderAddress.getAddressId(),
			GetterUtil.getLong(getCart.getBillingAddressId()));
		Assert.assertEquals(
			serviceBuilderAddress.getExternalReferenceCode(),
			getCart.getBillingAddressExternalReferenceCode());
		Assert.assertEquals(
			serviceBuilderAddress.getAddressId(),
			GetterUtil.getLong(getCart.getShippingAddressId()));
		Assert.assertEquals(
			serviceBuilderAddress.getExternalReferenceCode(),
			getCart.getShippingAddressExternalReferenceCode());
	}

	private void _testPatchCartWithAddressSubtype() throws Exception {
		Cart postCart = cartResource.postChannelCart(
			_commerceChannel.getCommerceChannelId(), randomCart());

		Cart randomPatchCart = randomPatchCart();

		ListTypeDefinition listTypeDefinition =
			_listTypeDefinitionLocalService.addListTypeDefinition(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				false);

		ListTypeEntry listTypeEntry =
			_listTypeEntryLocalService.addListTypeEntry(
				null, TestPropsValues.getUserId(),
				listTypeDefinition.getListTypeDefinitionId(),
				RandomTestUtil.randomString(),
				Collections.singletonMap(
					LocaleUtil.US, RandomTestUtil.randomString()),
				listTypeDefinition.isSystem());

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						AccountEntryAddressSubtypeConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"billingAddressSubtypeListTypeDefinition" +
								"ExternalReferenceCode",
							listTypeDefinition.getExternalReferenceCode()
						).put(
							"shippingAddressSubtypeListTypeDefinition" +
								"ExternalReferenceCode",
							listTypeDefinition.getExternalReferenceCode()
						).build())) {

			com.liferay.portal.kernel.model.Address serviceBuilderAddress =
				_addressLocalService.addAddress(
					RandomTestUtil.randomString(), _user.getUserId(),
					AccountEntry.class.getName(),
					_accountEntry.getAccountEntryId(), _country.getCountryId(),
					3, _region.getRegionId(), RandomTestUtil.randomString(),
					RandomTestUtil.randomString(), false,
					RandomTestUtil.randomString(), true,
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(), listTypeEntry.getKey(),
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(), _serviceContext);

			randomPatchCart.setBillingAddress(
				new Address() {
					{
						city = RandomTestUtil.randomString();
						countryISOCode = _country.getA2();
						name = RandomTestUtil.randomString();
						street1 = RandomTestUtil.randomString();
						subtype = listTypeEntry.getKey();
						zip = RandomTestUtil.randomString();
					}
				});
			randomPatchCart.setBillingAddressId(0L);
			randomPatchCart.setShippingAddress(
				new Address() {
					{
						city = serviceBuilderAddress.getCity();
						id = serviceBuilderAddress.getAddressId();
						name = serviceBuilderAddress.getName();
						street1 = serviceBuilderAddress.getStreet1();
						subtype = serviceBuilderAddress.getSubtype();
					}
				});
			randomPatchCart.setShippingAddressId(
				serviceBuilderAddress.getAddressId());

			Cart patchCart = cartResource.patchCart(
				postCart.getId(), randomPatchCart);

			Address address = patchCart.getBillingAddress();

			Assert.assertEquals(listTypeEntry.getKey(), address.getSubtype());

			address = patchCart.getShippingAddress();

			Assert.assertEquals(listTypeEntry.getKey(), address.getSubtype());
		}
	}

	private void _testPatchCartWithMoreExternalReferenceCodes()
		throws Exception {

		Cart postCart = cartResource.postChannelCart(
			_commerceChannel.getCommerceChannelId(), randomCart());

		Cart randomPatchCart = randomPatchCart();

		com.liferay.portal.kernel.model.Address serviceBuilderAddress =
			_addressLocalService.addAddress(
				RandomTestUtil.randomString(), _user.getUserId(),
				AccountEntry.class.getName(), _accountEntry.getAccountEntryId(),
				_country.getCountryId(), 0, _region.getRegionId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				false, RandomTestUtil.randomString(), true,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				_serviceContext);

		randomPatchCart.setBillingAddressExternalReferenceCode(
			serviceBuilderAddress.getExternalReferenceCode());

		randomPatchCart.setBillingAddressId(0L);

		randomPatchCart.setShippingAddressExternalReferenceCode(
			serviceBuilderAddress.getExternalReferenceCode());
		randomPatchCart.setShippingAddressId(0L);

		Cart patchCart = cartResource.patchCart(
			postCart.getId(), randomPatchCart);

		randomPatchCart.setBillingAddressId(
			serviceBuilderAddress.getAddressId());
		randomPatchCart.setShippingAddressId(
			serviceBuilderAddress.getAddressId());

		Cart expectedPatchCart = postCart.clone();

		BeanTestUtil.copyProperties(randomPatchCart, expectedPatchCart);

		Cart getCart = cartResource.getCart(patchCart.getId());

		assertEquals(expectedPatchCart, getCart);
		assertValid(getCart);
		Assert.assertEquals(
			serviceBuilderAddress.getAddressId(),
			GetterUtil.getLong(getCart.getBillingAddressId()));
		Assert.assertEquals(
			serviceBuilderAddress.getExternalReferenceCode(),
			getCart.getBillingAddressExternalReferenceCode());
		Assert.assertEquals(
			serviceBuilderAddress.getAddressId(),
			GetterUtil.getLong(getCart.getShippingAddressId()));
		Assert.assertEquals(
			serviceBuilderAddress.getExternalReferenceCode(),
			getCart.getShippingAddressExternalReferenceCode());
	}

	private void _testPostChannelCartByExternalReferenceCodeWithMoreExternalReferenceCodes()
		throws Exception {

		Cart randomCart = randomCart();

		com.liferay.portal.kernel.model.Address serviceBuilderAddress =
			_addressLocalService.addAddress(
				RandomTestUtil.randomString(), _user.getUserId(),
				AccountEntry.class.getName(), _accountEntry.getAccountEntryId(),
				_country.getCountryId(), 0, _region.getRegionId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				false, RandomTestUtil.randomString(), true,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				_serviceContext);

		randomCart.setBillingAddressExternalReferenceCode(
			serviceBuilderAddress.getExternalReferenceCode());

		randomCart.setBillingAddressId(0L);
		randomCart.setShippingAddressExternalReferenceCode(
			serviceBuilderAddress.getExternalReferenceCode());
		randomCart.setShippingAddressId(0L);

		Cart postCart = cartResource.postChannelCartByExternalReferenceCode(
			_commerceChannel.getExternalReferenceCode(), randomCart);

		randomCart.setBillingAddressId(serviceBuilderAddress.getAddressId());
		randomCart.setShippingAddressId(serviceBuilderAddress.getAddressId());

		assertEquals(randomCart, postCart);
		assertValid(postCart);
		Assert.assertEquals(
			serviceBuilderAddress.getAddressId(),
			GetterUtil.getLong(postCart.getBillingAddressId()));
		Assert.assertEquals(
			serviceBuilderAddress.getExternalReferenceCode(),
			postCart.getBillingAddressExternalReferenceCode());
		Assert.assertEquals(
			serviceBuilderAddress.getAddressId(),
			GetterUtil.getLong(postCart.getShippingAddressId()));
		Assert.assertEquals(
			serviceBuilderAddress.getExternalReferenceCode(),
			postCart.getShippingAddressExternalReferenceCode());
	}

	private void _testPostChannelCartWithMoreExternalReferenceCodes()
		throws Exception {

		Cart randomCart = randomCart();

		com.liferay.portal.kernel.model.Address serviceBuilderAddress =
			_addressLocalService.addAddress(
				RandomTestUtil.randomString(), _user.getUserId(),
				AccountEntry.class.getName(), _accountEntry.getAccountEntryId(),
				_country.getCountryId(), 0, _region.getRegionId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				false, RandomTestUtil.randomString(), true,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				_serviceContext);

		randomCart.setBillingAddressExternalReferenceCode(
			serviceBuilderAddress.getExternalReferenceCode());

		randomCart.setBillingAddressId(0L);
		randomCart.setShippingAddressExternalReferenceCode(
			serviceBuilderAddress.getExternalReferenceCode());
		randomCart.setShippingAddressId(0L);

		Cart postCart = testPostChannelCart_addCart(randomCart);

		randomCart.setBillingAddressId(serviceBuilderAddress.getAddressId());
		randomCart.setShippingAddressId(serviceBuilderAddress.getAddressId());

		assertEquals(randomCart, postCart);
		assertValid(postCart);
		Assert.assertEquals(
			serviceBuilderAddress.getAddressId(),
			GetterUtil.getLong(postCart.getBillingAddressId()));
		Assert.assertEquals(
			serviceBuilderAddress.getExternalReferenceCode(),
			postCart.getBillingAddressExternalReferenceCode());
		Assert.assertEquals(
			serviceBuilderAddress.getAddressId(),
			GetterUtil.getLong(postCart.getShippingAddressId()));
		Assert.assertEquals(
			serviceBuilderAddress.getExternalReferenceCode(),
			postCart.getShippingAddressExternalReferenceCode());
	}

	private void _testPutCartByExternalReferenceCodeWithMoreExternalReferenceCodes()
		throws Exception {

		Cart postCart = cartResource.postChannelCart(
			_commerceChannel.getCommerceChannelId(), randomCart());

		Cart randomCart = randomCart();

		com.liferay.portal.kernel.model.Address serviceBuilderAddress =
			_addressLocalService.addAddress(
				RandomTestUtil.randomString(), _user.getUserId(),
				AccountEntry.class.getName(), _accountEntry.getAccountEntryId(),
				_country.getCountryId(), 0, _region.getRegionId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				false, RandomTestUtil.randomString(), true,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				_serviceContext);

		randomCart.setBillingAddressExternalReferenceCode(
			serviceBuilderAddress.getExternalReferenceCode());

		randomCart.setBillingAddressId(0L);

		randomCart.setShippingAddressExternalReferenceCode(
			serviceBuilderAddress.getExternalReferenceCode());
		randomCart.setShippingAddressId(0L);

		Cart putCart = cartResource.putCartByExternalReferenceCode(
			postCart.getExternalReferenceCode(), randomCart);

		randomCart.setBillingAddressId(serviceBuilderAddress.getAddressId());
		randomCart.setShippingAddressId(serviceBuilderAddress.getAddressId());

		assertEquals(randomCart, putCart);
		assertValid(putCart);

		Cart getCart = cartResource.getCartByExternalReferenceCode(
			putCart.getExternalReferenceCode());

		assertEquals(randomCart, getCart);
		assertValid(getCart);
		Assert.assertEquals(
			serviceBuilderAddress.getAddressId(),
			GetterUtil.getLong(getCart.getBillingAddressId()));
		Assert.assertEquals(
			serviceBuilderAddress.getExternalReferenceCode(),
			getCart.getBillingAddressExternalReferenceCode());
		Assert.assertEquals(
			serviceBuilderAddress.getAddressId(),
			GetterUtil.getLong(getCart.getShippingAddressId()));
		Assert.assertEquals(
			serviceBuilderAddress.getExternalReferenceCode(),
			getCart.getShippingAddressExternalReferenceCode());
	}

	private void _testPutCartWithMoreExternalReferenceCodes() throws Exception {
		Cart postCart = cartResource.postChannelCart(
			_commerceChannel.getCommerceChannelId(), randomCart());

		Cart randomCart = randomCart();

		com.liferay.portal.kernel.model.Address serviceBuilderAddress =
			_addressLocalService.addAddress(
				RandomTestUtil.randomString(), _user.getUserId(),
				AccountEntry.class.getName(), _accountEntry.getAccountEntryId(),
				_country.getCountryId(), 0, _region.getRegionId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				false, RandomTestUtil.randomString(), true,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				_serviceContext);

		randomCart.setBillingAddressExternalReferenceCode(
			serviceBuilderAddress.getExternalReferenceCode());

		randomCart.setBillingAddressId(0L);
		randomCart.setShippingAddressExternalReferenceCode(
			serviceBuilderAddress.getExternalReferenceCode());
		randomCart.setShippingAddressId(0L);

		Cart putCart = cartResource.putCart(postCart.getId(), randomCart);

		randomCart.setBillingAddressId(serviceBuilderAddress.getAddressId());
		randomCart.setShippingAddressId(serviceBuilderAddress.getAddressId());

		assertEquals(randomCart, putCart);
		assertValid(putCart);

		Cart getCart = cartResource.getCart(putCart.getId());

		assertEquals(randomCart, getCart);
		assertValid(getCart);
		Assert.assertEquals(
			serviceBuilderAddress.getAddressId(),
			GetterUtil.getLong(getCart.getBillingAddressId()));
		Assert.assertEquals(
			serviceBuilderAddress.getExternalReferenceCode(),
			getCart.getBillingAddressExternalReferenceCode());
		Assert.assertEquals(
			serviceBuilderAddress.getAddressId(),
			GetterUtil.getLong(getCart.getShippingAddressId()));
		Assert.assertEquals(
			serviceBuilderAddress.getExternalReferenceCode(),
			getCart.getShippingAddressExternalReferenceCode());
	}

	private void _updateCommerceDiscount(String couponCode) throws Exception {
		Calendar calendar = CalendarFactoryUtil.getCalendar(
			_user.getTimeZone());

		_commerceDiscountLocalService.updateCommerceDiscount(
			_commerceDiscount.getCommerceDiscountId(),
			_commerceDiscount.getTitle(), _commerceDiscount.getTarget(),
			_commerceDiscount.isUseCouponCode(), couponCode,
			_commerceDiscount.isUsePercentage(),
			_commerceDiscount.getMaximumDiscountAmount(),
			_commerceDiscount.getLevel1(), _commerceDiscount.getLevel2(),
			_commerceDiscount.getLevel3(), _commerceDiscount.getLevel4(),
			_commerceDiscount.getLimitationType(),
			_commerceDiscount.getLimitationTimes(),
			_commerceDiscount.isActive(), calendar.get(Calendar.MONTH),
			calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR),
			calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
			calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
			calendar.get(Calendar.YEAR), calendar.get(Calendar.HOUR_OF_DAY),
			calendar.get(Calendar.MINUTE), true, _serviceContext);
	}

	private AccountEntry _accountEntry;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private AddressLocalService _addressLocalService;

	@DeleteAfterTestRun
	private CommerceChannel _commerceChannel;

	@DeleteAfterTestRun
	private CommerceCurrency _commerceCurrency;

	@DeleteAfterTestRun
	private CommerceDiscount _commerceDiscount;

	@Inject
	private CommerceDiscountLocalService _commerceDiscountLocalService;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	@DeleteAfterTestRun
	private Country _country;

	@Inject
	private CountryLocalService _countryLocalService;

	@Inject
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	@Inject
	private ListTypeEntryLocalService _listTypeEntryLocalService;

	@DeleteAfterTestRun
	private Region _region;

	@Inject
	private RegionLocalService _regionLocalService;

	private ServiceContext _serviceContext;

	@DeleteAfterTestRun
	private User _user;

}