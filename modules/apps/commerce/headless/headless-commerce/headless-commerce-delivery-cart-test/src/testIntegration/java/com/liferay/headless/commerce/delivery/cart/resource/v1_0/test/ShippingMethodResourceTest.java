/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.cart.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouseItem;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.model.CommerceChannelRel;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.service.CommerceAddressLocalServiceUtil;
import com.liferay.commerce.service.CommerceOrderLocalServiceUtil;
import com.liferay.commerce.service.CommerceShippingMethodLocalServiceUtil;
import com.liferay.commerce.test.util.CommerceInventoryTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.headless.commerce.delivery.cart.client.dto.v1_0.ShippingMethod;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * @author Andrea Sbarra
 * @author Crescenzo Rega
 */
@RunWith(Arquillian.class)
public class ShippingMethodResourceTest
	extends BaseShippingMethodResourceTestCase {

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

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			testGroup.getCompanyId());

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			testGroup.getGroupId(), _commerceCurrency.getCode());
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		for (CommerceAddress commerceAddress : _commerceAddresses) {
			CommerceAddressLocalServiceUtil.deleteCommerceAddress(
				commerceAddress);
		}
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"description", "id", "name"};
	}

	@Override
	protected ShippingMethod randomShippingMethod() throws Exception {
		return new ShippingMethod() {
			{
				description = RandomTestUtil.randomString();
				name = RandomTestUtil.randomString();
			}
		};
	}

	@Override
	protected ShippingMethod
			testGetCartByExternalReferenceCodeShippingMethodsPage_addShippingMethod(
				String externalReferenceCode, ShippingMethod shippingMethod)
		throws Exception {

		CommerceShippingMethod commerceShippingMethod =
			CommerceShippingMethodLocalServiceUtil.addCommerceShippingMethod(
				_user.getUserId(), _commerceChannel.getGroupId(),
				Collections.singletonMap(
					LocaleUtil.US, shippingMethod.getName()),
				Collections.singletonMap(
					LocaleUtil.US, shippingMethod.getDescription()),
				true, _getRandomEngineKey(), null, 1,
				RandomTestUtil.randomString());

		_commerceShippingMethods.add(commerceShippingMethod);

		return new ShippingMethod() {
			{
				description = commerceShippingMethod.getDescription(
					LocaleUtil.US);
				id = commerceShippingMethod.getCommerceShippingMethodId();
				name = commerceShippingMethod.getName(LocaleUtil.US);
			}
		};
	}

	@Override
	protected String
			testGetCartByExternalReferenceCodeShippingMethodsPage_getExternalReferenceCode()
		throws Exception {

		CommerceOrder commerceOrder = _addCommerceOrder();

		return commerceOrder.getExternalReferenceCode();
	}

	@Override
	protected ShippingMethod testGetCartShippingMethodsPage_addShippingMethod(
			Long cartId, ShippingMethod shippingMethod)
		throws Exception {

		CommerceShippingMethod commerceShippingMethod =
			CommerceShippingMethodLocalServiceUtil.addCommerceShippingMethod(
				_user.getUserId(), _commerceChannel.getGroupId(),
				Collections.singletonMap(
					LocaleUtil.US, shippingMethod.getName()),
				Collections.singletonMap(
					LocaleUtil.US, shippingMethod.getDescription()),
				true, _getRandomEngineKey(), null, 1,
				RandomTestUtil.randomString());

		_commerceShippingMethods.add(commerceShippingMethod);

		return new ShippingMethod() {
			{
				description = commerceShippingMethod.getDescription(
					LocaleUtil.US);
				id = commerceShippingMethod.getCommerceShippingMethodId();
				name = commerceShippingMethod.getName(LocaleUtil.US);
			}
		};
	}

	@Override
	protected Long testGetCartShippingMethodsPage_getCartId() throws Exception {
		CommerceOrder commerceOrder = _addCommerceOrder();

		return commerceOrder.getCommerceOrderId();
	}

	private CommerceOrder _addCommerceOrder() throws Exception {
		if (_commerceOrder != null) {
			return _commerceOrder;
		}

		_commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency);

		_cpInstance = CPTestUtil.addCPInstanceWithRandomSku(
			testGroup.getGroupId());

		_commerceInventoryWarehouse =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				_serviceContext);

		_commerceInventoryWarehouseItem =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
				_user.getUserId(), _commerceInventoryWarehouse, BigDecimal.TEN,
				_cpInstance.getSku(), StringPool.BLANK);

		_commerceChannelRel = CommerceTestUtil.addWarehouseCommerceChannelRel(
			_commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
			_commerceChannel.getCommerceChannelId());

		CommerceAddress commerceAddress =
			CommerceTestUtil.addUserCommerceAddress(
				_commerceChannel.getGroupId(), _user.getUserId());

		_commerceAddresses.add(commerceAddress);

		_commerceOrder.setShippingAddressId(
			commerceAddress.getCommerceAddressId());

		_commerceOrder = CommerceOrderLocalServiceUtil.updateCommerceOrder(
			_commerceOrder);

		CommerceTestUtil.addCommerceOrderItem(
			_commerceOrder.getCommerceOrderId(), _cpInstance.getCPInstanceId(),
			BigDecimal.ONE);

		return _commerceOrder;
	}

	private String _getRandomEngineKey() {
		Random random = new Random();

		return _engineKeys.remove(random.nextInt(_engineKeys.size()));
	}

	private final List<CommerceAddress> _commerceAddresses = new ArrayList<>();

	@DeleteAfterTestRun
	private CommerceChannel _commerceChannel;

	@DeleteAfterTestRun
	private CommerceChannelRel _commerceChannelRel;

	@DeleteAfterTestRun
	private CommerceCurrency _commerceCurrency;

	@DeleteAfterTestRun
	private CommerceInventoryWarehouse _commerceInventoryWarehouse;

	@DeleteAfterTestRun
	private CommerceInventoryWarehouseItem _commerceInventoryWarehouseItem;

	@DeleteAfterTestRun
	private CommerceOrder _commerceOrder;

	@DeleteAfterTestRun
	private List<CommerceShippingMethod> _commerceShippingMethods =
		new ArrayList<>();

	@DeleteAfterTestRun
	private CPInstance _cpInstance;

	private final List<String> _engineKeys = ListUtil.fromArray(
		"fixed", "by-weight");
	private ServiceContext _serviceContext;

	@DeleteAfterTestRun
	private User _user;

}