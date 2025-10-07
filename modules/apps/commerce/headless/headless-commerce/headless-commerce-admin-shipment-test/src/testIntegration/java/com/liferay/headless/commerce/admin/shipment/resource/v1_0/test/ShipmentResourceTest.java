/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.shipment.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceShipment;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.service.CommerceAddressService;
import com.liferay.commerce.service.CommerceShipmentLocalServiceUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.headless.commerce.admin.shipment.client.dto.v1_0.Shipment;
import com.liferay.headless.commerce.admin.shipment.client.dto.v1_0.ShippingAddress;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Region;
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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
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

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			testCompany.getCompanyId(), testGroup.getGroupId(),
			_user.getUserId());

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			testCompany.getCompanyId());

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			testGroup.getGroupId(), _commerceCurrency.getCode());

		BigDecimal value = BigDecimal.valueOf(RandomTestUtil.nextDouble());

		_commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency.getCommerceCurrencyId());

		_commerceOrder = CommerceTestUtil.addCommerceOrderShippingDetails(
			_commerceOrder, value);
	}

	@Override
	@Test
	public void testDeleteShipmentByExternalReferenceCode() throws Exception {
	}

	@Override
	@Test
	public void testGetShipmentByExternalReferenceCode() throws Exception {
	}

	@Override
	@Test
	public void testPatchShipmentByExternalReferenceCode() throws Exception {
	}

	@Override
	@Test
	public void testPostShipment() throws Exception {
		super.testPostShipment();

		_testPostShipmentWithMoreExternalReferenceCodes();
	}

	@Override
	@Test
	public void testPutShipmentByExternalReferenceCode() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		super.testVulcanCRUDItemDelegateGetItem();
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
	protected Shipment testDeleteShipment_addShipment() throws Exception {
		return _addShipment(null);
	}

	@Override
	protected Shipment testDeleteShipmentByExternalReferenceCode_addShipment()
		throws Exception {

		return _addShipment(null);
	}

	@Override
	protected Shipment testGetShipment_addShipment() throws Exception {
		return _addShipment(null);
	}

	@Override
	protected Shipment testGetShipmentByExternalReferenceCode_addShipment()
		throws Exception {

		return _addShipment(null);
	}

	@Override
	protected Shipment testGetShipmentsPage_addShipment(Shipment shipment)
		throws Exception {

		return _addShipment(shipment.getExternalReferenceCode());
	}

	@Override
	protected Shipment testGraphQLShipment_addShipment() throws Exception {
		return _addShipment(null);
	}

	@Override
	protected Shipment testGraphQLShipment_addShipment(Shipment shipment)
		throws Exception {

		return _addShipment(shipment.getExternalReferenceCode());
	}

	@Override
	protected Shipment testPatchShipment_addShipment() throws Exception {
		return _addShipment(null);
	}

	@Override
	protected Shipment testPatchShipmentByExternalReferenceCode_addShipment()
		throws Exception {

		return _addShipment(null);
	}

	@Override
	protected Shipment testPostShipment_addShipment(Shipment shipment)
		throws Exception {

		return _addShipment(shipment.getExternalReferenceCode());
	}

	@Override
	protected Shipment
			testPostShipmentByExternalReferenceCodeStatusDelivered_addShipment(
				Shipment shipment)
		throws Exception {

		return _addShipment(shipment.getExternalReferenceCode());
	}

	@Override
	protected Shipment
			testPostShipmentByExternalReferenceCodeStatusFinishProcessing_addShipment(
				Shipment shipment)
		throws Exception {

		return _addShipment(shipment.getExternalReferenceCode());
	}

	@Override
	protected Shipment
			testPostShipmentByExternalReferenceCodeStatusShipped_addShipment(
				Shipment shipment)
		throws Exception {

		return _addShipment(shipment.getExternalReferenceCode());
	}

	@Override
	protected Shipment testPostShipmentStatusDelivered_addShipment(
			Shipment shipment)
		throws Exception {

		return _addShipment(shipment.getExternalReferenceCode());
	}

	@Override
	protected Shipment testPostShipmentStatusFinishProcessing_addShipment(
			Shipment shipment)
		throws Exception {

		return _addShipment(shipment.getExternalReferenceCode());
	}

	@Override
	protected Shipment testPostShipmentStatusShipped_addShipment(
			Shipment shipment)
		throws Exception {

		return _addShipment(shipment.getExternalReferenceCode());
	}

	@Override
	protected Shipment testPutShipmentByExternalReferenceCode_addShipment()
		throws Exception {

		return _addShipment(null);
	}

	private Shipment _addShipment(String externalReferenceCode)
		throws Exception {

		_commerceShipment =
			CommerceShipmentLocalServiceUtil.addCommerceShipment(
				externalReferenceCode, _commerceOrder.getGroupId(),
				_commerceOrder.getCommerceAccountId(),
				_commerceOrder.getShippingAddressId(),
				_commerceOrder.getCommerceShippingMethodId(),
				_commerceOrder.getShippingOptionName(), _serviceContext);

		_commerceShipments.add(_commerceShipment);

		return _toShipment(_commerceShipment);
	}

	private ShippingAddress _randomShippingAddress() throws Exception {
		CommerceAddress commerceAddress = _commerceOrder.getShippingAddress();

		Country country = commerceAddress.getCountry();
		Region region = commerceAddress.getRegion();

		return new ShippingAddress() {
			{
				city = RandomTestUtil.randomString();
				countryISOCode = country.getA2();
				description = RandomTestUtil.randomString();
				externalReferenceCode = RandomTestUtil.randomString();
				latitude = RandomTestUtil.randomDouble();
				longitude = RandomTestUtil.randomDouble();
				name = RandomTestUtil.randomString();
				phoneNumber = RandomTestUtil.randomString();
				regionISOCode = region.getRegionCode();
				street1 = RandomTestUtil.randomString();
				street2 = RandomTestUtil.randomString();
				street3 = RandomTestUtil.randomString();
				zip = RandomTestUtil.randomString();
			}
		};
	}

	private void _testPostShipmentWithMoreExternalReferenceCodes()
		throws Exception {

		Shipment randomShipment = randomShipment();

		randomShipment.setOrderExternalReferenceCode(
			_commerceOrder.getExternalReferenceCode());
		randomShipment.setOrderId(0L);

		ShippingAddress randomShippingAddress = _randomShippingAddress();

		randomShippingAddress.setExternalReferenceCode(
			RandomTestUtil.randomString());
		randomShippingAddress.setId(0L);

		randomShipment.setShippingAddress(randomShippingAddress);

		randomShipment.setShippingAddressId(0L);

		Shipment postShipment = shipmentResource.postShipment(randomShipment);

		Assert.assertEquals(
			randomShipment.getAccountId(), postShipment.getAccountId());
		Assert.assertEquals(
			randomShipment.getShippingMethodId(),
			postShipment.getShippingMethodId());

		ShippingAddress getShippingAddress = _toShippingAddress(
			_commerceAddressService.getCommerceAddress(
				postShipment.getShippingAddressId()));

		Assert.assertEquals(
			randomShippingAddress.getExternalReferenceCode(),
			getShippingAddress.getExternalReferenceCode());
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

	private ShippingAddress _toShippingAddress(CommerceAddress commerceAddress)
		throws Exception {

		Country country = commerceAddress.getCountry();
		Region region = commerceAddress.getRegion();

		return new ShippingAddress() {
			{
				city = commerceAddress.getCity();
				countryISOCode = country.getA2();
				description = commerceAddress.getDescription();
				externalReferenceCode =
					commerceAddress.getExternalReferenceCode();
				id = commerceAddress.getCommerceAddressId();
				latitude = commerceAddress.getLatitude();
				longitude = commerceAddress.getLongitude();
				name = commerceAddress.getName();
				phoneNumber = commerceAddress.getPhoneNumber();
				regionISOCode = region.getRegionCode();
				street1 = commerceAddress.getStreet1();
				street2 = commerceAddress.getStreet2();
				street3 = commerceAddress.getStreet3();
				zip = commerceAddress.getZip();
			}
		};
	}

	@Inject
	private CommerceAddressService _commerceAddressService;

	@DeleteAfterTestRun
	private CommerceChannel _commerceChannel;

	@DeleteAfterTestRun
	private CommerceCurrency _commerceCurrency;

	@DeleteAfterTestRun
	private CommerceOrder _commerceOrder;

	@DeleteAfterTestRun
	private CommerceShipment _commerceShipment;

	@DeleteAfterTestRun
	private final List<CommerceShipment> _commerceShipments = new ArrayList<>();

	private ServiceContext _serviceContext;

	@DeleteAfterTestRun
	private User _user;

}