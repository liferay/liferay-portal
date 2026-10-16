/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.cart.resource.v1_0.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.constants.CommerceAddressConstants;
import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.media.CommerceMediaResolver;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.order.CommerceOrderHttpHelper;
import com.liferay.commerce.price.list.constants.CommercePriceListConstants;
import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.service.CommercePriceEntryLocalService;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.service.CommerceAddressLocalService;
import com.liferay.commerce.service.CommerceOrderItemLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.test.util.CommerceInventoryTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.headless.commerce.delivery.cart.client.dto.v1_0.CartItem;
import com.liferay.headless.commerce.delivery.cart.client.dto.v1_0.Price;
import com.liferay.headless.commerce.delivery.cart.client.dto.v1_0.SkuUnitOfMeasure;
import com.liferay.headless.commerce.delivery.cart.client.pagination.Page;
import com.liferay.headless.commerce.delivery.cart.client.pagination.Pagination;
import com.liferay.headless.commerce.delivery.cart.client.problem.Problem;
import com.liferay.headless.commerce.delivery.cart.client.resource.v1_0.CartItemResource;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CountryLocalService;
import com.liferay.portal.kernel.service.RegionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.settings.FallbackKeysSettingsUtil;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.settings.ModifiableSettings;
import com.liferay.portal.kernel.settings.Settings;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.rule.Inject;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Andrea Sbarra
 * @author Crescenzo Rega
 */
@RunWith(Arquillian.class)
public class CartItemResourceTest extends BaseCartItemResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_user = UserTestUtil.addUser(testCompany);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testCompany.getCompanyId(), testGroup.getGroupId(),
				_user.getUserId());

		_accountEntry = CommerceAccountTestUtil.addBusinessAccountEntry(
			_user.getUserId(), "Test Business Account", null, null, null, null,
			serviceContext);

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			testGroup.getCompanyId());

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			testGroup.getGroupId(), _commerceCurrency.getCode());

		_commerceInventoryWarehouse =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				serviceContext);
		_commerceOrder = _commerceOrderLocalService.addCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_accountEntry.getAccountEntryId(), _commerceCurrency.getCode(), 0);

		_country = _countryLocalService.addCountry(
			null, "XY", "XYZ", true, true, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.nextDouble(), true, true, false, serviceContext);

		_region = _regionLocalService.addRegion(
			null, _country.getCountryId(), true, RandomTestUtil.randomString(),
			RandomTestUtil.nextDouble(), RandomTestUtil.randomString(),
			serviceContext);
	}

	@Ignore
	@Override
	@Test
	public void testDeleteCartItem() throws Exception {
	}

	@Override
	@Test
	public void testDeleteCartItemByExternalReferenceCode() throws Exception {
		super.testDeleteCartItemByExternalReferenceCode();

		_testDeleteCartItemByExternalReferenceCodeWithGuestAccountEntry();
	}

	@Override
	@Test
	public void testGetCartByExternalReferenceCodeItemsPage() throws Exception {
		super.testGetCartByExternalReferenceCodeItemsPage();

		_testGetCartByExternalReferenceCodeItemsPage();
	}

	@Ignore
	@Override
	@Test
	public void testGetCartByExternalReferenceCodeItemsPageWithPagination()
		throws Exception {
	}

	@Override
	@Test
	public void testGetCartItem() throws Exception {
		super.testGetCartItem();

		_testGetCartItemPriceOnApplication();
		_testGetCartItemPriceOnApplicationForUnitOfMeasure();
	}

	@Override
	@Test
	public void testGetCartItemByExternalReferenceCode() throws Exception {
		super.testGetCartItemByExternalReferenceCode();

		_testGetCartItemPriceOnApplication();
	}

	@Override
	@Test
	public void testGetCartItemsPage() throws Exception {
		super.testGetCartItemsPage();

		_testGetCartItemsPage();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteCartItem() throws Exception {
	}

	@Override
	@Test
	public void testPatchCartItem() throws Exception {
		super.testPatchCartItem();

		_testPatchCartItemWithGuestAccountEntry();
		_testPatchCartItemWithInvalidShippingAddress();
	}

	@Override
	@Test
	public void testPostCartByExternalReferenceCodeItem() throws Exception {
		CartItem randomCartItem = randomCartItem();

		CartItem postCartItem1 =
			testPostCartByExternalReferenceCodeItem_addCartItem(randomCartItem);

		assertEquals(randomCartItem, postCartItem1);
		assertValid(postCartItem1);

		Settings settings = FallbackKeysSettingsUtil.getSettings(
			new GroupServiceSettingsLocator(
				_commerceChannel.getGroupId(),
				CommerceConstants.SERVICE_NAME_COMMERCE_ORDER));

		ModifiableSettings modifiableSettings =
			settings.getModifiableSettings();

		modifiableSettings.setValue(
			"showSeparateOrderItems", Boolean.TRUE.toString());

		modifiableSettings.store();

		CartItem postCartItem2 =
			testPostCartByExternalReferenceCodeItem_addCartItem(randomCartItem);

		Assert.assertNotEquals(postCartItem1.getId(), postCartItem2.getId());
	}

	@Override
	@Test
	public void testPostCartItem() throws Exception {
		super.testPostCartItem();

		_testPostCartItemDefaultImage();
		_testPostCartItemToGuestOrderWithGuestCheckoutDisabledOnB2BChannel();
		_testPostCartItemWithGuestAccountEntry();
	}

	@Override
	@Test
	public void testPutCartItem() throws Exception {
		super.testPutCartItem();

		_testPutCartItemWithInvalidShippingAddress();
	}

	@Override
	@Test
	public void testPutCartItemByExternalReferenceCode() throws Exception {
		CartItem postCartItem =
			testPutCartItemByExternalReferenceCode_addCartItem();

		CartItem randomCartItem = randomCartItem();

		CartItem putCartItem =
			cartItemResource.putCartItemByExternalReferenceCode(
				postCartItem.getExternalReferenceCode(), randomCartItem);

		assertEquals(randomCartItem, putCartItem);
		assertValid(putCartItem);

		CartItem getCartItem =
			cartItemResource.getCartItemByExternalReferenceCode(
				putCartItem.getExternalReferenceCode());

		assertEquals(randomCartItem, getCartItem);
		assertValid(getCartItem);
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"quantity"};
	}

	@Override
	protected CartItem randomCartItem() throws Exception {
		return _randomCartItem(RandomTestUtil.randomBoolean());
	}

	@Override
	protected CartItem testDeleteCartItem_addCartItem() throws Exception {
		return cartItemResource.postCartItem(
			_commerceOrder.getCommerceOrderId(), randomCartItem());
	}

	@Override
	protected CartItem testDeleteCartItemByExternalReferenceCode_addCartItem()
		throws Exception {

		return cartItemResource.postCartByExternalReferenceCodeItem(
			_commerceOrder.getExternalReferenceCode(), randomCartItem());
	}

	@Override
	protected CartItem testGetCartByExternalReferenceCodeItemsPage_addCartItem(
			String externalReferenceCode, CartItem cartItem)
		throws Exception {

		return cartItemResource.postCartByExternalReferenceCodeItem(
			externalReferenceCode, cartItem);
	}

	@Override
	protected String
			testGetCartByExternalReferenceCodeItemsPage_getExternalReferenceCode()
		throws Exception {

		return _commerceOrder.getExternalReferenceCode();
	}

	@Override
	protected CartItem testGetCartItem_addCartItem() throws Exception {
		return cartItemResource.postCartItem(
			_commerceOrder.getCommerceOrderId(), randomCartItem());
	}

	@Override
	protected CartItem testGetCartItemByExternalReferenceCode_addCartItem()
		throws Exception {

		return cartItemResource.postCartByExternalReferenceCodeItem(
			_commerceOrder.getExternalReferenceCode(), randomCartItem());
	}

	@Override
	protected CartItem testGetCartItemsPage_addCartItem(
			Long cartId, CartItem cartItem)
		throws Exception {

		return cartItemResource.postCartItem(cartId, cartItem);
	}

	@Override
	protected Long testGetCartItemsPage_getCartId() throws Exception {
		return _commerceOrder.getCommerceOrderId();
	}

	@Override
	protected CartItem testGraphQLCartItem_addCartItem() throws Exception {
		return cartItemResource.postCartItem(
			_commerceOrder.getCommerceOrderId(), randomCartItem());
	}

	@Override
	protected CartItem
			testGraphQLGetCartItemByExternalReferenceCode_addCartItem()
		throws Exception {

		return cartItemResource.postCartByExternalReferenceCodeItem(
			_commerceOrder.getExternalReferenceCode(), randomCartItem());
	}

	@Override
	protected CartItem testPatchCartItem_addCartItem() throws Exception {
		return cartItemResource.postCartItem(
			_commerceOrder.getCommerceOrderId(), randomCartItem());
	}

	@Override
	protected CartItem testPatchCartItemByExternalReferenceCode_addCartItem()
		throws Exception {

		return cartItemResource.postCartByExternalReferenceCodeItem(
			_commerceOrder.getExternalReferenceCode(), randomCartItem());
	}

	@Override
	protected CartItem testPostCartByExternalReferenceCodeItem_addCartItem(
			CartItem cartItem)
		throws Exception {

		return cartItemResource.postCartByExternalReferenceCodeItem(
			_commerceOrder.getExternalReferenceCode(), cartItem);
	}

	@Override
	protected CartItem testPostCartItem_addCartItem(CartItem cartItem)
		throws Exception {

		return cartItemResource.postCartItem(
			_commerceOrder.getCommerceOrderId(), cartItem);
	}

	@Override
	protected CartItem testPutCartItem_addCartItem() throws Exception {
		return cartItemResource.postCartItem(
			_commerceOrder.getCommerceOrderId(), randomCartItem());
	}

	@Override
	protected CartItem testPutCartItemByExternalReferenceCode_addCartItem()
		throws Exception {

		return cartItemResource.postCartByExternalReferenceCodeItem(
			_commerceOrder.getExternalReferenceCode(), randomCartItem());
	}

	private CPInstance _addCPInstance(boolean priceOnApplication)
		throws Exception {

		CPInstance cpInstance = CPTestUtil.addCPInstanceWithRandomSku(
			testGroup.getGroupId(),
			BigDecimal.valueOf(RandomTestUtil.randomDouble()));

		_cpInstances.add(cpInstance);

		CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
			_user.getUserId(), _commerceInventoryWarehouse, BigDecimal.TEN,
			cpInstance.getSku(), StringPool.BLANK);

		if (priceOnApplication) {
			_updateCommercePriceEntry(
				cpInstance, priceOnApplication,
				CommercePriceListConstants.TYPE_PRICE_LIST);
			_updateCommercePriceEntry(
				cpInstance, priceOnApplication,
				CommercePriceListConstants.TYPE_PROMOTION);
		}

		return cpInstance;
	}

	private CommerceAddress _addCommerceAddress(long accountEntryId)
		throws Exception {

		return _commerceAddressLocalService.addCommerceAddress(
			RandomTestUtil.randomString(), AccountEntry.class.getName(),
			accountEntryId, _country.getCountryId(), _region.getRegionId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), StringPool.BLANK,
			CommerceAddressConstants.ADDRESS_TYPE_BILLING_AND_SHIPPING,
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				testCompany.getCompanyId(), testGroup.getGroupId(),
				_user.getUserId()));
	}

	private CommerceOrder _addGuestCommerceOrder() throws Exception {
		User user = testCompany.getGuestUser();
		AccountEntry accountEntry =
			_accountEntryLocalService.getGuestAccountEntry(
				testCompany.getCompanyId());

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.addCommerceOrder(
				user.getUserId(), _commerceChannel.getGroupId(),
				accountEntry.getAccountEntryId(), _commerceCurrency.getCode(),
				0);

		_guestCommerceOrders.add(commerceOrder);

		return commerceOrder;
	}

	private CommerceAddress _addInvalidCommerceAddress() throws Exception {
		User user = UserTestUtil.addUser(testCompany);

		AccountEntry accountEntry =
			CommerceAccountTestUtil.addBusinessAccountEntry(
				user.getUserId(), RandomTestUtil.randomString(), null,
				ServiceContextTestUtil.getServiceContext(
					testCompany.getCompanyId(), testGroup.getGroupId(),
					user.getUserId()));

		return _addCommerceAddress(accountEntry.getAccountEntryId());
	}

	private void _assertProblemException(
			CartItem cartItem, String expectedStatus,
			UnsafeConsumer<CartItem, Exception> unsafeConsumer)
		throws Exception {

		Problem.ProblemException problemException = Assert.assertThrows(
			Problem.ProblemException.class,
			() -> unsafeConsumer.accept(cartItem));

		Problem problem = problemException.getProblem();

		Assert.assertEquals(expectedStatus, problem.getStatus());
	}

	private CartItemResource _getCartItemResource() throws Exception {
		_userLocalService.updatePassword(
			_user.getUserId(), _PASSWORD, _PASSWORD, false, true);

		return CartItemResource.builder(
		).authentication(
			_user.getEmailAddress(), _PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	private CartItemResource _getCartItemResource(CommerceOrder commerceOrder)
		throws Exception {

		User user = UserTestUtil.getAdminUser(testCompany.getCompanyId());

		return CartItemResource.builder(
		).authentication(
			user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).header(
			HttpHeaders.COOKIE,
			StringBundler.concat(
				_commerceOrderHttpHelper.getCookieName(
					commerceOrder.getGroupId()),
				StringPool.EQUAL, commerceOrder.getUuid(), StringPool.PIPE,
				RandomTestUtil.randomString())
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	private CartItem _randomCartItem(boolean priceOnApplication)
		throws Exception {

		return _randomCartItem(_addCPInstance(priceOnApplication));
	}

	private CartItem _randomCartItem(CPInstance cpInstance) {
		return new CartItem() {
			{
				deliveryGroup = RandomTestUtil.randomString();
				externalReferenceCode = RandomTestUtil.randomString();
				quantity = BigDecimal.valueOf(RandomTestUtil.randomInt(1, 10));
				replacedSkuExternalReferenceCode =
					RandomTestUtil.randomString();
				requestedDeliveryDate = RandomTestUtil.nextDate();
				shippingAddressExternalReferenceCode =
					RandomTestUtil.randomString();
				sku = cpInstance.getSku();
				skuId = cpInstance.getCPInstanceId();
			}
		};
	}

	private void _testDeleteCartItemByExternalReferenceCodeWithGuestAccountEntry()
		throws Exception {

		CommerceOrder commerceOrder = _addGuestCommerceOrder();

		CartItemResource cartItemResource1 = _getCartItemResource(
			commerceOrder);

		CartItem postCartItem = cartItemResource1.postCartItem(
			commerceOrder.getCommerceOrderId(), randomCartItem());

		CartItemResource cartItemResource2 = _getCartItemResource(
			_addGuestCommerceOrder());

		_assertProblemException(
			postCartItem, "NOT_FOUND",
			cartItem -> cartItemResource2.deleteCartItemByExternalReferenceCode(
				cartItem.getExternalReferenceCode()));

		assertEquals(
			postCartItem, cartItemResource1.getCartItem(postCartItem.getId()));
	}

	private void _testGetCartByExternalReferenceCodeItemsPage()
		throws Exception {

		CartItem postCartItem = cartItemResource.postCartItem(
			_commerceOrder.getCommerceOrderId(), _randomCartItem(false));

		cartItemResource.postCartItem(
			_commerceOrder.getCommerceOrderId(), _randomCartItem(false));

		Page<CartItem> cartItemsPage =
			cartItemResource.getCartByExternalReferenceCodeItemsPage(
				testGetCartByExternalReferenceCodeItemsPage_getExternalReferenceCode(),
				RandomTestUtil.randomString(), null, Pagination.of(1, 10));

		List<CartItem> cartItems = (List<CartItem>)cartItemsPage.getItems();

		Assert.assertEquals(cartItems.toString(), 0, cartItems.size());

		cartItemsPage =
			cartItemResource.getCartByExternalReferenceCodeItemsPage(
				testGetCartByExternalReferenceCodeItemsPage_getExternalReferenceCode(),
				postCartItem.getSku(), null, Pagination.of(1, 10));

		cartItems = (List<CartItem>)cartItemsPage.getItems();

		Assert.assertEquals(cartItems.toString(), 1, cartItems.size());

		CartItem cartItem = cartItems.get(0);

		assertEquals(postCartItem, cartItem);
	}

	private void _testGetCartItemPriceOnApplication() throws Exception {
		boolean priceOnApplication = RandomTestUtil.randomBoolean();

		CartItem postCartItem = cartItemResource.postCartItem(
			_commerceOrder.getCommerceOrderId(),
			_randomCartItem(priceOnApplication));

		CartItem getCartItem = cartItemResource.getCartItem(
			postCartItem.getId());

		assertEquals(postCartItem, getCartItem);
		assertValid(getCartItem);

		Price price = getCartItem.getPrice();

		Assert.assertEquals(priceOnApplication, price.getPriceOnApplication());
	}

	private void _testGetCartItemPriceOnApplicationForUnitOfMeasure()
		throws Exception {

		CPInstance cpInstance = _addCPInstance(false);
		String unitOfMeasureKey1 = RandomTestUtil.randomString();

		CPTestUtil.addCPInstanceUnitOfMeasure(
			testGroup.getGroupId(), cpInstance.getCPInstanceId(),
			unitOfMeasureKey1, BigDecimal.ONE, cpInstance.getSku());

		String unitOfMeasureKey2 = RandomTestUtil.randomString();

		CPTestUtil.addCPInstanceUnitOfMeasure(
			testGroup.getGroupId(), cpInstance.getCPInstanceId(),
			unitOfMeasureKey2, BigDecimal.ONE, cpInstance.getSku());

		CommercePriceEntry commercePriceEntry =
			_commercePriceEntryLocalService.getInstanceBaseCommercePriceEntry(
				cpInstance.getCPInstanceUuid(),
				CommercePriceListConstants.TYPE_PRICE_LIST, unitOfMeasureKey2);

		commercePriceEntry.setPriceOnApplication(true);

		_commercePriceEntryLocalService.updateCommercePriceEntry(
			commercePriceEntry);

		CartItem cartItemUnitOfMeasure2 = _randomCartItem(cpInstance);

		SkuUnitOfMeasure skuUnitOfMeasure2 = new SkuUnitOfMeasure();

		skuUnitOfMeasure2.setKey(unitOfMeasureKey2);

		cartItemUnitOfMeasure2.setSkuUnitOfMeasure(skuUnitOfMeasure2);

		CartItem postCartItemUnitOfMeasure2 = cartItemResource.postCartItem(
			_commerceOrder.getCommerceOrderId(), cartItemUnitOfMeasure2);

		Price priceUnitOfMeasure2 = postCartItemUnitOfMeasure2.getPrice();

		Assert.assertTrue(priceUnitOfMeasure2.getPriceOnApplication());

		CartItem cartItemUnitOfMeasure1 = _randomCartItem(cpInstance);

		SkuUnitOfMeasure skuUnitOfMeasure1 = new SkuUnitOfMeasure();

		skuUnitOfMeasure1.setKey(unitOfMeasureKey1);

		cartItemUnitOfMeasure1.setSkuUnitOfMeasure(skuUnitOfMeasure1);

		CartItem postCartItemUnitOfMeasure1 = cartItemResource.postCartItem(
			_commerceOrder.getCommerceOrderId(), cartItemUnitOfMeasure1);

		Price priceUnitOfMeasure1 = postCartItemUnitOfMeasure1.getPrice();

		Assert.assertFalse(priceUnitOfMeasure1.getPriceOnApplication());
	}

	private void _testGetCartItemsPage() throws Exception {
		CartItem postCartItem = cartItemResource.postCartItem(
			_commerceOrder.getCommerceOrderId(), _randomCartItem(false));

		cartItemResource.postCartItem(
			_commerceOrder.getCommerceOrderId(), _randomCartItem(false));

		Page<CartItem> cartItemsPage = cartItemResource.getCartItemsPage(
			_commerceOrder.getCommerceOrderId(), RandomTestUtil.randomString(),
			null, Pagination.of(1, 10));

		List<CartItem> cartItems = (List<CartItem>)cartItemsPage.getItems();

		Assert.assertEquals(cartItems.toString(), 0, cartItems.size());

		cartItemsPage = cartItemResource.getCartItemsPage(
			_commerceOrder.getCommerceOrderId(), postCartItem.getSku(), null,
			Pagination.of(1, 10));

		cartItems = (List<CartItem>)cartItemsPage.getItems();

		Assert.assertEquals(cartItems.toString(), 1, cartItems.size());

		CartItem cartItem = cartItems.get(0);

		assertEquals(postCartItem, cartItem);
	}

	private void _testPatchCartItemWithGuestAccountEntry() throws Exception {
		CommerceOrder commerceOrder = _addGuestCommerceOrder();

		CartItemResource cartItemResource1 = _getCartItemResource(
			commerceOrder);

		CartItem postCartItem = cartItemResource1.postCartItem(
			commerceOrder.getCommerceOrderId(), randomCartItem());

		CartItemResource cartItemResource2 = _getCartItemResource(
			_addGuestCommerceOrder());

		_assertProblemException(
			randomCartItem(), "NOT_FOUND",
			patchCartItem -> cartItemResource2.patchCartItem(
				postCartItem.getId(), patchCartItem));

		assertEquals(
			postCartItem, cartItemResource1.getCartItem(postCartItem.getId()));
	}

	private void _testPatchCartItemWithInvalidShippingAddress()
		throws Exception {

		CartItemResource cartItemResource = _getCartItemResource();

		CPInstance cpInstance = _addCPInstance(false);

		CartItem postCartItem = cartItemResource.postCartItem(
			_commerceOrder.getCommerceOrderId(), _randomCartItem(cpInstance));

		CommerceAddress commerceAddress1 = _addInvalidCommerceAddress();

		_assertProblemException(
			new CartItem() {
				{
					quantity = BigDecimal.valueOf(
						RandomTestUtil.randomInt(1, 10));
					shippingAddressId = commerceAddress1.getCommerceAddressId();
					skuId = cpInstance.getCPInstanceId();
				}
			},
			"FORBIDDEN",
			patchCartItem -> cartItemResource.patchCartItem(
				postCartItem.getId(), patchCartItem));
		_assertProblemException(
			new CartItem() {
				{
					quantity = BigDecimal.valueOf(
						RandomTestUtil.randomInt(1, 10));
					shippingAddressExternalReferenceCode =
						commerceAddress1.getExternalReferenceCode();
					skuId = cpInstance.getCPInstanceId();
				}
			},
			"FORBIDDEN",
			patchCartItem -> cartItemResource.patchCartItem(
				postCartItem.getId(), patchCartItem));

		CommerceAddress commerceAddress2 = _addCommerceAddress(
			_accountEntry.getAccountEntryId());

		CartItem patchCartItem = cartItemResource.patchCartItem(
			postCartItem.getId(),
			new CartItem() {
				{
					quantity = BigDecimal.valueOf(
						RandomTestUtil.randomInt(1, 10));
					shippingAddressId = commerceAddress2.getCommerceAddressId();
					skuId = cpInstance.getCPInstanceId();
				}
			});

		Assert.assertEquals(
			Long.valueOf(commerceAddress2.getCommerceAddressId()),
			patchCartItem.getShippingAddressId());
	}

	private void _testPostCartItemDefaultImage() throws Exception {
		CPInstance cpInstance = _addCPInstance(false);

		CartItem cartItem = cartItemResource.postCartItem(
			_commerceOrder.getCommerceOrderId(), _randomCartItem(cpInstance));

		String adaptiveMediaImageHTMLTag =
			cartItem.getAdaptiveMediaImageHTMLTag();

		String defaultURL = _commerceMediaResolver.getDefaultURL(
			cpInstance.getGroupId());

		Assert.assertTrue(
			adaptiveMediaImageHTMLTag.contains("src=\"" + defaultURL + "\""));
	}

	private void _testPostCartItemToGuestOrderWithGuestCheckoutDisabledOnB2BChannel()
		throws Exception {

		CommerceTestUtil.runWithGuestCheckoutDisabledOnB2BChannel(
			_commerceChannel.getGroupId(),
			() -> {
				CommerceOrder commerceOrder = _addGuestCommerceOrder();

				CartItemResource cartItemResource = _getCartItemResource(
					commerceOrder);

				_assertProblemException(
					randomCartItem(), "BAD_REQUEST",
					postCartItem -> cartItemResource.postCartItem(
						commerceOrder.getCommerceOrderId(), postCartItem));
			});
	}

	private void _testPostCartItemWithGuestAccountEntry() throws Exception {
		CommerceOrder commerceOrder1 = _addGuestCommerceOrder();

		CartItemResource cartItemResource = _getCartItemResource(
			commerceOrder1);

		CartItem randomCartItem = randomCartItem();

		assertEquals(
			randomCartItem,
			cartItemResource.postCartItem(
				commerceOrder1.getCommerceOrderId(), randomCartItem));

		CommerceOrder commerceOrder2 = _addGuestCommerceOrder();

		_assertProblemException(
			randomCartItem(), "NOT_FOUND",
			postCartItem -> cartItemResource.postCartItem(
				commerceOrder2.getCommerceOrderId(), postCartItem));
	}

	private void _testPutCartItemWithInvalidShippingAddress() throws Exception {
		CartItemResource cartItemResource = _getCartItemResource();

		CPInstance cpInstance = _addCPInstance(false);

		CartItem postCartItem = cartItemResource.postCartItem(
			_commerceOrder.getCommerceOrderId(), _randomCartItem(cpInstance));

		CommerceAddress commerceAddress1 = _addInvalidCommerceAddress();

		_assertProblemException(
			new CartItem() {
				{
					quantity = BigDecimal.valueOf(
						RandomTestUtil.randomInt(1, 10));
					shippingAddressId = commerceAddress1.getCommerceAddressId();
					skuId = cpInstance.getCPInstanceId();
				}
			},
			"FORBIDDEN",
			putCartItem -> cartItemResource.putCartItem(
				postCartItem.getId(), putCartItem));
		_assertProblemException(
			new CartItem() {
				{
					quantity = BigDecimal.valueOf(
						RandomTestUtil.randomInt(1, 10));
					shippingAddressExternalReferenceCode =
						commerceAddress1.getExternalReferenceCode();
					skuId = cpInstance.getCPInstanceId();
				}
			},
			"FORBIDDEN",
			putCartItem -> cartItemResource.putCartItem(
				postCartItem.getId(), putCartItem));

		CommerceAddress commerceAddress2 = _addCommerceAddress(
			_accountEntry.getAccountEntryId());

		CartItem putCartItem = cartItemResource.putCartItem(
			postCartItem.getId(),
			new CartItem() {
				{
					quantity = BigDecimal.valueOf(
						RandomTestUtil.randomInt(1, 10));
					shippingAddressId = commerceAddress2.getCommerceAddressId();
					skuId = cpInstance.getCPInstanceId();
				}
			});

		Assert.assertEquals(
			Long.valueOf(commerceAddress2.getCommerceAddressId()),
			putCartItem.getShippingAddressId());
	}

	private void _updateCommercePriceEntry(
		CPInstance cpInstance, boolean priceOnApplication,
		String typePriceList) {

		CommercePriceEntry commercePriceEntry =
			_commercePriceEntryLocalService.getInstanceBaseCommercePriceEntry(
				cpInstance.getCPInstanceUuid(), typePriceList,
				StringPool.BLANK);

		if (commercePriceEntry == null) {
			return;
		}

		commercePriceEntry.setPriceOnApplication(priceOnApplication);

		_commercePriceEntryLocalService.updateCommercePriceEntry(
			commercePriceEntry);
	}

	private static final String _PASSWORD = RandomTestUtil.randomString();

	@DeleteAfterTestRun
	private AccountEntry _accountEntry;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private CommerceAddressLocalService _commerceAddressLocalService;

	@DeleteAfterTestRun
	private CommerceChannel _commerceChannel;

	@DeleteAfterTestRun
	private CommerceCurrency _commerceCurrency;

	@DeleteAfterTestRun
	private CommerceInventoryWarehouse _commerceInventoryWarehouse;

	@Inject
	private CommerceMediaResolver _commerceMediaResolver;

	@DeleteAfterTestRun
	private CommerceOrder _commerceOrder;

	@Inject
	private CommerceOrderHttpHelper _commerceOrderHttpHelper;

	@Inject
	private CommerceOrderItemLocalService _commerceOrderItemLocalService;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Inject
	private CommercePriceEntryLocalService _commercePriceEntryLocalService;

	@DeleteAfterTestRun
	private Country _country;

	@Inject
	private CountryLocalService _countryLocalService;

	@DeleteAfterTestRun
	private final List<CPInstance> _cpInstances = new ArrayList<>();

	@DeleteAfterTestRun
	private final List<CommerceOrder> _guestCommerceOrders = new ArrayList<>();

	@DeleteAfterTestRun
	private Region _region;

	@Inject
	private RegionLocalService _regionLocalService;

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}