/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.order.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.CommerceShipment;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.type.virtual.constants.VirtualCPTypeConstants;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.service.CommerceShipmentItemLocalService;
import com.liferay.commerce.service.CommerceShipmentLocalService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.headless.commerce.delivery.order.client.dto.v1_0.Shipment;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.test.rule.Inject;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.runner.RunWith;

/**
 * @author Andrea Sbarra
 * @author Alessio Antonio Rendina
 */
@RunWith(Arquillian.class)
public class ShipmentResourceTest extends BaseShipmentResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_user = UserTestUtil.addUser(testCompany);

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		PrincipalThreadLocal.setName(_user.getUserId());

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			testCompany.getCompanyId());

		_commerceCatalog = CommerceTestUtil.addCommerceCatalog(
			testCompany.getCompanyId(), testGroup.getGroupId(),
			_user.getUserId(), _commerceCurrency.getCode());

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			testGroup.getGroupId(), _commerceCurrency.getCode());

		_commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency.getCommerceCurrencyId());

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), VirtualCPTypeConstants.NAME, true,
			true);

		CommerceTestUtil.updateBackOrderCPDefinitionInventory(cpDefinition);

		List<CPInstance> cpInstances = cpDefinition.getCPInstances();

		CPInstance cpInstance = cpInstances.get(0);

		CommerceTestUtil.addCommerceOrderItem(
			_commerceOrder.getCommerceOrderId(), cpInstance.getCPInstanceId(),
			BigDecimal.TEN);

		_commerceOrder = _commerceOrderLocalService.getCommerceOrder(
			_commerceOrder.getCommerceOrderId());

		_commerceOrder = CommerceTestUtil.addCommerceOrderShippingDetails(
			_commerceOrder, BigDecimal.valueOf(RandomTestUtil.nextDouble()));

		_commerceOrder.setOrderStatus(
			CommerceOrderConstants.ORDER_STATUS_PROCESSING);

		_commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
			_commerceOrder);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			testCompany.getCompanyId(), testGroup.getGroupId(),
			_user.getUserId());
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"accountId", "shippingAddressId", "shippingMethodId",
			"shippingOptionName"
		};
	}

	@Override
	protected Collection<EntityField> getEntityFields() throws Exception {
		return new ArrayList<>();
	}

	@Override
	protected Shipment randomShipment() throws Exception {
		return new Shipment() {
			{
				accountId = _commerceOrder.getCommerceAccountId();
				carrier = StringUtil.toLowerCase(RandomTestUtil.randomString());
				createDate = RandomTestUtil.nextDate();
				expectedDate = RandomTestUtil.nextDate();
				externalReferenceCode = RandomTestUtil.randomString();
				modifiedDate = RandomTestUtil.nextDate();
				orderId = _commerceOrder.getCommerceOrderId();
				shippingAddressId = _commerceOrder.getShippingAddressId();
				shippingDate = RandomTestUtil.nextDate();
				shippingMethodId = _commerceOrder.getCommerceShippingMethodId();
				shippingOptionName = _commerceOrder.getShippingOptionName();
				trackingNumber = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				userName = _commerceOrder.getUserName();
			}
		};
	}

	@Override
	protected Shipment
			testGetPlacedOrderByExternalReferenceCodeShipmentsPage_addShipment(
				String externalReferenceCode, Shipment shipment)
		throws Exception {

		_commerceOrderLocalService.updateCommerceOrderExternalReferenceCode(
			externalReferenceCode, _commerceOrder.getCommerceOrderId());

		shipment = _addShipment();

		List<CommerceOrderItem> commerceOrderItems =
			_commerceOrder.getCommerceOrderItems();

		CommerceOrderItem commerceOrderItem = commerceOrderItems.get(0);

		_commerceShipmentItemLocalService.addCommerceShipmentItem(
			RandomTestUtil.randomString(),
			_commerceShipment.getCommerceShipmentId(),
			commerceOrderItem.getCommerceOrderItemId(), 0, BigDecimal.ONE, null,
			false, _serviceContext);

		return shipment;
	}

	@Override
	protected String
			testGetPlacedOrderByExternalReferenceCodeShipmentsPage_getExternalReferenceCode()
		throws Exception {

		return _commerceOrder.getExternalReferenceCode();
	}

	@Override
	protected Shipment testGetPlacedOrderShipmentsPage_addShipment(
			Long placedOrderId, Shipment shipment)
		throws Exception {

		shipment = _addShipment();

		List<CommerceOrderItem> commerceOrderItems =
			_commerceOrder.getCommerceOrderItems();

		CommerceOrderItem commerceOrderItem = commerceOrderItems.get(0);

		_commerceShipmentItemLocalService.addCommerceShipmentItem(
			RandomTestUtil.randomString(),
			_commerceShipment.getCommerceShipmentId(),
			commerceOrderItem.getCommerceOrderItemId(), 0, BigDecimal.ONE, null,
			false, _serviceContext);

		return shipment;
	}

	@Override
	protected Long testGetPlacedOrderShipmentsPage_getPlacedOrderId()
		throws Exception {

		return _commerceOrder.getCommerceOrderId();
	}

	private Shipment _addShipment() throws Exception {
		_commerceShipment = _commerceShipmentLocalService.addCommerceShipment(
			RandomTestUtil.randomString(), _commerceOrder.getGroupId(),
			_commerceOrder.getCommerceAccountId(),
			_commerceOrder.getShippingAddressId(),
			_commerceOrder.getCommerceShippingMethodId(),
			_commerceOrder.getShippingOptionName(), _serviceContext);

		_commerceShipments.add(_commerceShipment);

		return _toShipment(_commerceShipment);
	}

	private Shipment _toShipment(CommerceShipment commerceShipment)
		throws Exception {

		return new Shipment() {
			{
				accountId = commerceShipment.getCommerceAccountId();
				carrier = commerceShipment.getCarrier();
				createDate = commerceShipment.getCreateDate();
				expectedDate = commerceShipment.getExpectedDate();
				externalReferenceCode =
					commerceShipment.getExternalReferenceCode();
				id = commerceShipment.getCommerceShipmentId();
				modifiedDate = commerceShipment.getModifiedDate();
				shippingAddressId = commerceShipment.getCommerceAddressId();
				shippingDate = commerceShipment.getShippingDate();
				shippingMethodId =
					commerceShipment.getCommerceShippingMethodId();
				shippingOptionName = commerceShipment.getShippingOptionName();
				trackingNumber = commerceShipment.getTrackingNumber();
				userName = commerceShipment.getUserName();
			}
		};
	}

	@DeleteAfterTestRun
	private CommerceCatalog _commerceCatalog;

	@DeleteAfterTestRun
	private CommerceChannel _commerceChannel;

	@DeleteAfterTestRun
	private CommerceCurrency _commerceCurrency;

	@DeleteAfterTestRun
	private CommerceOrder _commerceOrder;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	@DeleteAfterTestRun
	private CommerceShipment _commerceShipment;

	@Inject
	private CommerceShipmentItemLocalService _commerceShipmentItemLocalService;

	@Inject
	private CommerceShipmentLocalService _commerceShipmentLocalService;

	@DeleteAfterTestRun
	private final List<CommerceShipment> _commerceShipments = new ArrayList<>();

	private ServiceContext _serviceContext;

	@DeleteAfterTestRun
	private User _user;

}