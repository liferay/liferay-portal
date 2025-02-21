/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.resource.v1_0.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPInstanceUnitOfMeasure;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.service.CommerceOrderItemLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.term.model.CommerceTermEntry;
import com.liferay.commerce.term.service.CommerceTermEntryLocalService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.headless.commerce.admin.order.client.dto.v1_0.Order;
import com.liferay.headless.commerce.admin.order.client.dto.v1_0.OrderItem;
import com.liferay.headless.commerce.admin.order.client.pagination.Page;
import com.liferay.headless.commerce.admin.order.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.order.client.resource.v1_0.OrderResource;
import com.liferay.headless.commerce.core.util.DateConfig;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CountryLocalService;
import com.liferay.portal.kernel.service.RegionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

import java.math.BigDecimal;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alessio Antonio Rendina
 * @author Riccardo Ferrari
 * @author Stefano Motta
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class OrderResourceTest extends BaseOrderResourceTestCase {

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

		_accountEntry = _accountEntryLocalService.addAccountEntry(
			_user.getUserId(), 0, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString() + "@liferay.com", null, null,
			"business", 1, _serviceContext);

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			testGroup.getCompanyId());

		_commerceChannel = _commerceChannelLocalService.addCommerceChannel(
			RandomTestUtil.randomString(),
			AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT, testGroup.getGroupId(),
			RandomTestUtil.randomString(),
			CommerceChannelConstants.CHANNEL_TYPE_SITE, null,
			_commerceCurrency.getCode(), _serviceContext);

		_country = _countryLocalService.addCountry(
			"XY", "XYZ", true, true, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.nextDouble(), true, true, false, _serviceContext);

		_region = _regionLocalService.addRegion(
			_country.getCountryId(), true, RandomTestUtil.randomString(),
			RandomTestUtil.nextDouble(), RandomTestUtil.randomString(),
			_serviceContext);

		_orderAddress = _addressLocalService.addAddress(
			RandomTestUtil.randomString(), _user.getUserId(),
			AccountEntry.class.getName(), _accountEntry.getAccountEntryId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), _region.getRegionId(),
			_country.getCountryId(), 0, false, true,
			RandomTestUtil.randomString(), _serviceContext);

		DateConfig displayDateConfig = DateConfig.toDisplayDateConfig(
			RandomTestUtil.nextDate(), _user.getTimeZone());
		DateConfig expirationDateConfig = DateConfig.toExpirationDateConfig(
			RandomTestUtil.nextDate(), _user.getTimeZone());

		_commerceTermEntry =
			_commerceTermEntryLocalService.addCommerceTermEntry(
				RandomTestUtil.randomString(), _user.getUserId(),
				RandomTestUtil.randomBoolean(),
				RandomTestUtil.randomLocaleStringMap(),
				displayDateConfig.getMonth(), displayDateConfig.getDay(),
				displayDateConfig.getYear(), displayDateConfig.getHour(),
				displayDateConfig.getMinute(), expirationDateConfig.getMonth(),
				expirationDateConfig.getDay(), expirationDateConfig.getYear(),
				expirationDateConfig.getHour(),
				expirationDateConfig.getMinute(), true,
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomString(), RandomTestUtil.nextDouble(),
				RandomTestUtil.randomString(), StringPool.BLANK,
				_serviceContext);
	}

	@Ignore
	@Override
	@Test
	public void testGetOrdersPage() throws Exception {
		super.testGetOrdersPage();
	}

	@Ignore
	@Override
	@Test
	public void testGetOrdersPageWithFilterDateTimeEquals() throws Exception {
		super.testGetOrdersPageWithFilterDateTimeEquals();
	}

	@Ignore
	@Override
	@Test
	public void testGetOrdersPageWithFilterStringContains() throws Exception {
		super.testGetOrdersPageWithFilterStringContains();
	}

	@Ignore
	@Override
	@Test
	public void testGetOrdersPageWithFilterStringEquals() throws Exception {

		// Fixes generated test to filter for different order creators

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.STRING);

		if (entityFields.isEmpty()) {
			return;
		}

		Order order1 = testGetOrdersPage_addOrder(randomOrder());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Order order2 = testGetOrdersPage_addOrder(randomOrder());

		for (EntityField entityField : entityFields) {
			String entityFieldName = entityField.getName();

			if (entityFieldName.equals("creatorEmailAddress")) {
				Role role = _roleLocalService.getRole(
					testCompany.getCompanyId(), RoleConstants.ADMINISTRATOR);
				User user = UserTestUtil.addUser(
					testCompany.getCompanyId(), testCompany.getUserId(), "test",
					"UserServiceTest." + RandomTestUtil.nextLong() +
						"@liferay.com",
					StringPool.BLANK, LocaleUtil.getDefault(),
					"UserServiceTest", "UserServiceTest", null,
					_serviceContext);

				_userLocalService.addRoleUser(role.getRoleId(), user);

				Order order3 = orderResource.postOrder(randomOrder());

				Page<Order> page = orderResource.getOrdersPage(
					null, getFilterString(entityField, "eq", order3),
					Pagination.of(1, 2), null);

				assertEquals(
					Collections.singletonList(order3),
					(List<Order>)page.getItems());
			}
			else {
				Page<Order> page = orderResource.getOrdersPage(
					null, getFilterString(entityField, "eq", order1),
					Pagination.of(1, 2), null);

				assertEquals(
					Collections.singletonList(order1),
					(List<Order>)page.getItems());
			}
		}
	}

	@Ignore
	@Override
	@Test
	public void testGetOrdersPageWithFilterStringStartsWith() throws Exception {
		super.testGetOrdersPageWithFilterStringStartsWith();
	}

	@Ignore
	@Override
	@Test
	public void testGetOrdersPageWithPagination() throws Exception {
		super.testGetOrdersPageWithPagination();
	}

	@Ignore
	@Override
	@Test
	public void testGetOrdersPageWithSortDateTime() throws Exception {
		super.testGetOrdersPageWithSortDateTime();
	}

	@Ignore
	@Override
	@Test
	public void testGetOrdersPageWithSortInteger() throws Exception {
		super.testGetOrdersPageWithSortInteger();
	}

	@Ignore
	@Override
	@Test
	public void testGetOrdersPageWithSortString() throws Exception {
		super.testGetOrdersPageWithSortString();
	}

	@Test
	public void testGetOrderWithNestedFields() throws Exception {
		User omniadminUser = UserTestUtil.addOmniadminUser();

		String password = RandomTestUtil.randomString();

		_userLocalService.updatePassword(
			omniadminUser.getUserId(), password, password, false, true);

		OrderResource orderResource = OrderResource.builder(
		).authentication(
			omniadminUser.getEmailAddress(), password
		).locale(
			LocaleUtil.getDefault()
		).parameters(
			"nestedFields", "orderItems,orderItems.shippingAddress"
		).build();

		Order expectedOrder = orderResource.postOrder(
			_randomOrderWithNestedFields(false));

		Order actualOrder = orderResource.getOrder(expectedOrder.getId());

		assertEquals(expectedOrder, actualOrder);

		OrderItem[] expectedOrderItems = expectedOrder.getOrderItems();

		OrderItem[] actualOrderItems = actualOrder.getOrderItems();

		Assert.assertEquals(
			Arrays.toString(actualOrderItems), expectedOrderItems.length,
			actualOrderItems.length);
		Assert.assertNotNull(actualOrderItems[0].getShippingAddress());
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteOrder() throws Exception {
		super.testGraphQLDeleteOrder();
	}

	@Override
	@Test
	public void testPatchOrder() throws Exception {
		super.testPatchOrder();

		_testPatchOrderWithMoreExternalReferenceCodes();
	}

	@Override
	@Test
	public void testPatchOrderByExternalReferenceCode() throws Exception {
		super.testPatchOrderByExternalReferenceCode();

		_testPatchOrderByExternalReferenceCodeWithMoreExternalReferenceCodes();
	}

	@Override
	@Test
	public void testPostOrder() throws Exception {
		super.testPostOrder();

		_testPostOrderWithDateCustomField();
		_testPostOrderWithMoreExternalReferenceCodes();
		_testPostOrderWithOrderItems(
			CommerceOrderConstants.ORDER_STATUS_COMPLETED);
		_testPostOrderWithOrderItems(CommerceOrderConstants.ORDER_STATUS_OPEN);
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"currencyCode", "paymentMethod", "printedNote",
			"purchaseOrderNumber"
		};
	}

	@Override
	protected String[] getIgnoredEntityFieldNames() {
		return new String[] {"channelId", "creatorEmailAddress", "orderId"};
	}

	@Override
	protected Order randomOrder() throws Exception {
		return new Order() {
			{
				accountExternalReferenceCode =
					_accountEntry.getExternalReferenceCode();
				accountId = _accountEntry.getAccountEntryId();
				advanceStatus = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				billingAddressId = _orderAddress.getAddressId();
				channelExternalReferenceCode =
					_commerceChannel.getExternalReferenceCode();
				channelId = _commerceChannel.getCommerceChannelId();
				couponCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				createDate = RandomTestUtil.nextDate();
				currencyCode = _commerceCurrency.getCode();
				currencyExternalReferenceCode =
					_commerceCurrency.getExternalReferenceCode();
				currencyId = _commerceCurrency.getCommerceCurrencyId();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				modifiedDate = RandomTestUtil.nextDate();
				name = RandomTestUtil.randomString();
				paymentMethod = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				printedNote = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				purchaseOrderNumber = RandomTestUtil.randomString();
				requestedDeliveryDate = RandomTestUtil.nextDate();
				shippable = RandomTestUtil.randomBoolean();
				shippingAddressId = _orderAddress.getAddressId();
			}
		};
	}

	@Override
	protected Order testDeleteOrder_addOrder() throws Exception {
		return orderResource.postOrder(randomOrder());
	}

	@Override
	protected Order testDeleteOrderByExternalReferenceCode_addOrder()
		throws Exception {

		return orderResource.postOrder(randomOrder());
	}

	@Override
	protected Order testGetOrder_addOrder() throws Exception {
		return orderResource.postOrder(randomOrder());
	}

	@Override
	protected Order testGetOrderByExternalReferenceCode_addOrder()
		throws Exception {

		return orderResource.postOrder(randomOrder());
	}

	@Override
	protected Order testGetOrdersPage_addOrder(Order order) throws Exception {
		return orderResource.postOrder(order);
	}

	@Override
	protected Order testGraphQLOrder_addOrder() throws Exception {
		return orderResource.postOrder(randomOrder());
	}

	@Override
	protected Order testPatchOrder_addOrder() throws Exception {
		return orderResource.postOrder(randomOrder());
	}

	@Override
	protected Order testPatchOrderByExternalReferenceCode_addOrder()
		throws Exception {

		return orderResource.postOrder(randomOrder());
	}

	@Override
	protected Order testPostOrder_addOrder(Order order) throws Exception {
		return orderResource.postOrder(order);
	}

	@Override
	protected Order testPutOrderByExternalReferenceCode_addOrder()
		throws Exception {

		return orderResource.postOrder(randomOrder());
	}

	private OrderItem _randomOrderItem(boolean useUnitOfMeasure)
		throws Exception {

		CPInstance cpInstance = CPTestUtil.addCPInstanceWithRandomSku(
			testGroup.getGroupId());

		return new OrderItem() {
			{
				bookedQuantityId = RandomTestUtil.randomLong();
				deliveryGroup = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				orderExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				orderId = RandomTestUtil.randomLong();
				printedNote = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				quantity = BigDecimal.valueOf(RandomTestUtil.randomInt());
				replacedSkuExternalReferenceCode =
					RandomTestUtil.randomString();
				shippable = RandomTestUtil.randomBoolean();
				shippedQuantity = BigDecimal.valueOf(
					RandomTestUtil.randomInt());
				shippingAddressExternalReferenceCode =
					RandomTestUtil.randomString();
				shippingAddressId = _orderAddress.getAddressId();
				skuId = cpInstance.getCPInstanceId();
				subscription = RandomTestUtil.randomBoolean();

				setUnitOfMeasureKey(
					() -> {
						if (!useUnitOfMeasure) {
							return null;
						}

						CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure =
							CPTestUtil.addCPInstanceUnitOfMeasure(
								TestPropsValues.getGroupId(),
								cpInstance.getCPInstanceId(),
								RandomTestUtil.randomString(), BigDecimal.TEN,
								cpInstance.getSku());

						return cpInstanceUnitOfMeasure.getKey();
					});
			}
		};
	}

	private Order _randomOrderWithNestedFields(boolean useUnitOfMeasure)
		throws Exception {

		Order order = randomOrder();

		OrderItem orderItem = _randomOrderItem(useUnitOfMeasure);

		orderItem.setOrderId(order.getId());

		order.setOrderItems(new OrderItem[] {orderItem});

		return order;
	}

	private void _testPatchOrderByExternalReferenceCodeWithMoreExternalReferenceCodes()
		throws Exception {

		Order postOrder = orderResource.postOrder(randomOrder());

		Order randomPatchOrder = randomPatchOrder();

		Address randomAddress = _addressLocalService.addAddress(
			RandomTestUtil.randomString(), _user.getUserId(),
			AccountEntry.class.getName(), _accountEntry.getAccountEntryId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), _region.getRegionId(),
			_country.getCountryId(), 0, false, true,
			RandomTestUtil.randomString(), _serviceContext);

		randomPatchOrder.setBillingAddressExternalReferenceCode(
			randomAddress.getExternalReferenceCode());

		randomPatchOrder.setBillingAddressId(0L);
		randomPatchOrder.setDeliveryTermExternalReferenceCode(
			_commerceTermEntry.getExternalReferenceCode());
		randomPatchOrder.setDeliveryTermId(0L);
		randomPatchOrder.setPaymentTermExternalReferenceCode(
			_commerceTermEntry.getExternalReferenceCode());
		randomPatchOrder.setPaymentTermId(0L);
		randomPatchOrder.setShippable(true);
		randomPatchOrder.setShippingAddressExternalReferenceCode(
			randomAddress.getExternalReferenceCode());
		randomPatchOrder.setShippingAddressId(0L);

		Order patchOrder = orderResource.patchOrderByExternalReferenceCode(
			postOrder.getExternalReferenceCode(), randomPatchOrder);

		Order expectedPatchOrder = postOrder.clone();

		BeanTestUtil.copyProperties(randomPatchOrder, expectedPatchOrder);

		Order getOrder = orderResource.getOrderByExternalReferenceCode(
			patchOrder.getExternalReferenceCode());

		assertEquals(expectedPatchOrder, getOrder);
		assertValid(getOrder);
		Assert.assertEquals(
			randomAddress.getAddressId(),
			GetterUtil.getLong(getOrder.getBillingAddressId()));
		Assert.assertEquals(
			randomAddress.getExternalReferenceCode(),
			getOrder.getBillingAddressExternalReferenceCode());
		Assert.assertEquals(
			_commerceTermEntry.getCommerceTermEntryId(),
			GetterUtil.getLong(getOrder.getDeliveryTermId()));
		Assert.assertEquals(
			_commerceTermEntry.getExternalReferenceCode(),
			getOrder.getDeliveryTermExternalReferenceCode());
		Assert.assertEquals(
			_commerceTermEntry.getCommerceTermEntryId(),
			GetterUtil.getLong(getOrder.getPaymentTermId()));
		Assert.assertEquals(
			_commerceTermEntry.getExternalReferenceCode(),
			getOrder.getPaymentTermExternalReferenceCode());
		Assert.assertEquals(
			randomAddress.getAddressId(),
			GetterUtil.getLong(getOrder.getShippingAddressId()));
		Assert.assertEquals(
			randomAddress.getExternalReferenceCode(),
			getOrder.getShippingAddressExternalReferenceCode());
	}

	private void _testPatchOrderWithMoreExternalReferenceCodes()
		throws Exception {

		Order postOrder = orderResource.postOrder(randomOrder());

		Order randomPatchOrder = randomPatchOrder();

		Address randomAddress = _addressLocalService.addAddress(
			RandomTestUtil.randomString(), _user.getUserId(),
			AccountEntry.class.getName(), _accountEntry.getAccountEntryId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), _region.getRegionId(),
			_country.getCountryId(), 0, false, true,
			RandomTestUtil.randomString(), _serviceContext);

		randomPatchOrder.setBillingAddressExternalReferenceCode(
			randomAddress.getExternalReferenceCode());

		randomPatchOrder.setBillingAddressId(0L);
		randomPatchOrder.setDeliveryTermExternalReferenceCode(
			_commerceTermEntry.getExternalReferenceCode());
		randomPatchOrder.setDeliveryTermId(0L);
		randomPatchOrder.setPaymentTermExternalReferenceCode(
			_commerceTermEntry.getExternalReferenceCode());
		randomPatchOrder.setPaymentTermId(0L);
		randomPatchOrder.setShippable(true);
		randomPatchOrder.setShippingAddressExternalReferenceCode(
			randomAddress.getExternalReferenceCode());
		randomPatchOrder.setShippingAddressId(0L);

		Order patchOrder = orderResource.patchOrder(
			postOrder.getId(), randomPatchOrder);

		Order expectedPatchOrder = postOrder.clone();

		BeanTestUtil.copyProperties(randomPatchOrder, expectedPatchOrder);

		Order getOrder = orderResource.getOrder(patchOrder.getId());

		assertEquals(expectedPatchOrder, getOrder);
		assertValid(getOrder);
		Assert.assertEquals(
			randomAddress.getAddressId(),
			GetterUtil.getLong(getOrder.getBillingAddressId()));
		Assert.assertEquals(
			randomAddress.getExternalReferenceCode(),
			getOrder.getBillingAddressExternalReferenceCode());
		Assert.assertEquals(
			_commerceTermEntry.getCommerceTermEntryId(),
			GetterUtil.getLong(getOrder.getDeliveryTermId()));
		Assert.assertEquals(
			_commerceTermEntry.getExternalReferenceCode(),
			getOrder.getDeliveryTermExternalReferenceCode());
		Assert.assertEquals(
			_commerceTermEntry.getCommerceTermEntryId(),
			GetterUtil.getLong(getOrder.getPaymentTermId()));
		Assert.assertEquals(
			_commerceTermEntry.getExternalReferenceCode(),
			getOrder.getPaymentTermExternalReferenceCode());
		Assert.assertEquals(
			randomAddress.getAddressId(),
			GetterUtil.getLong(getOrder.getShippingAddressId()));
		Assert.assertEquals(
			randomAddress.getExternalReferenceCode(),
			getOrder.getShippingAddressExternalReferenceCode());
	}

	private void _testPostOrderWithDateCustomField() throws Exception {
		User adminUser = UserTestUtil.getAdminUser(testGroup.getCompanyId());

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(adminUser));

		PrincipalThreadLocal.setName(adminUser.getUserId());

		CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			adminUser.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency);

		ExpandoTable expandoTable = _expandoTableLocalService.addTable(
			testGroup.getCompanyId(),
			_classNameLocalService.getClassNameId(CommerceOrder.class),
			"CUSTOM_FIELDS");

		ExpandoColumn expandoColumn = _expandoColumnLocalService.addColumn(
			expandoTable.getTableId(), "A" + RandomTestUtil.randomString(),
			ExpandoColumnConstants.DATE);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(testGroup.getGroupId());

		serviceContext.setExpandoBridgeAttributes(
			HashMapBuilder.<String, Serializable>put(
				expandoColumn.getName(), new Date()
			).build());

		commerceOrder.setExpandoBridgeAttributes(serviceContext);

		commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
			commerceOrder);

		Assert.assertNotNull(
			_jsonFactory.createJSONObject(
				String.valueOf(
					orderResource.getOrder(
						commerceOrder.getCommerceOrderId()))));
	}

	private void _testPostOrderWithMoreExternalReferenceCodes()
		throws Exception {

		Order randomOrder = randomOrder();

		randomOrder.setBillingAddressExternalReferenceCode(
			_orderAddress.getExternalReferenceCode());
		randomOrder.setBillingAddressId(0L);
		randomOrder.setDeliveryTermExternalReferenceCode(
			_commerceTermEntry.getExternalReferenceCode());
		randomOrder.setDeliveryTermId(0L);
		randomOrder.setPaymentTermExternalReferenceCode(
			_commerceTermEntry.getExternalReferenceCode());
		randomOrder.setPaymentTermId(0L);
		randomOrder.setShippable(true);
		randomOrder.setShippingAddressExternalReferenceCode(
			_orderAddress.getExternalReferenceCode());
		randomOrder.setShippingAddressId(0L);

		Order postOrder = testPostOrder_addOrder(randomOrder);

		randomOrder.setBillingAddressId(_orderAddress.getAddressId());
		randomOrder.setDeliveryTermId(
			_commerceTermEntry.getCommerceTermEntryId());
		randomOrder.setPaymentTermId(
			_commerceTermEntry.getCommerceTermEntryId());
		randomOrder.setShippingAddressId(_orderAddress.getAddressId());

		assertEquals(randomOrder, postOrder);
		assertValid(postOrder);
		Assert.assertEquals(
			_orderAddress.getAddressId(),
			GetterUtil.getLong(postOrder.getBillingAddressId()));
		Assert.assertEquals(
			_orderAddress.getExternalReferenceCode(),
			postOrder.getBillingAddressExternalReferenceCode());
		Assert.assertEquals(
			_commerceTermEntry.getCommerceTermEntryId(),
			GetterUtil.getLong(postOrder.getDeliveryTermId()));
		Assert.assertEquals(
			_commerceTermEntry.getExternalReferenceCode(),
			postOrder.getDeliveryTermExternalReferenceCode());
		Assert.assertEquals(
			_commerceTermEntry.getCommerceTermEntryId(),
			GetterUtil.getLong(postOrder.getPaymentTermId()));
		Assert.assertEquals(
			_commerceTermEntry.getExternalReferenceCode(),
			postOrder.getPaymentTermExternalReferenceCode());
		Assert.assertEquals(
			_orderAddress.getAddressId(),
			GetterUtil.getLong(postOrder.getShippingAddressId()));
		Assert.assertEquals(
			_orderAddress.getExternalReferenceCode(),
			postOrder.getShippingAddressExternalReferenceCode());
	}

	private void _testPostOrderWithOrderItems(int commerceOrderStatus)
		throws Exception {

		Order order = _randomOrderWithNestedFields(true);

		order.setOrderStatus(commerceOrderStatus);

		Order postOrder = testPostOrder_addOrder(order);

		Order getOrder = orderResource.getOrder(postOrder.getId());

		assertEquals(postOrder, getOrder);
		assertValid(getOrder);

		List<CommerceOrderItem> commerceOrderItems =
			_commerceOrderItemLocalService.getCommerceOrderItems(
				getOrder.getId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (CommerceOrderItem commerceOrderItem : commerceOrderItems) {
			Assert.assertEquals(
				BigDecimal.TEN,
				commerceOrderItem.getUnitOfMeasureIncrementalOrderQuantity());
		}
	}

	private AccountEntry _accountEntry;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private AddressLocalService _addressLocalService;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	private CommerceChannel _commerceChannel;

	@Inject
	private CommerceChannelLocalService _commerceChannelLocalService;

	private CommerceCurrency _commerceCurrency;

	@Inject
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	@Inject
	private CommerceOrderItemLocalService _commerceOrderItemLocalService;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	private CommerceTermEntry _commerceTermEntry;

	@Inject
	private CommerceTermEntryLocalService _commerceTermEntryLocalService;

	private Country _country;

	@Inject
	private CountryLocalService _countryLocalService;

	@Inject
	private ExpandoColumnLocalService _expandoColumnLocalService;

	@Inject
	private ExpandoTableLocalService _expandoTableLocalService;

	@Inject
	private JSONFactory _jsonFactory;

	private Address _orderAddress;
	private Region _region;

	@Inject
	private RegionLocalService _regionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	private ServiceContext _serviceContext;
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}