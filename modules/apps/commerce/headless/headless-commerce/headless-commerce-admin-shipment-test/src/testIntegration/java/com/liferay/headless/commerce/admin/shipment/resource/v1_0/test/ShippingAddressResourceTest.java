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
import com.liferay.commerce.service.CommerceShipmentLocalServiceUtil;
import com.liferay.commerce.test.util.CommerceInventoryTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.headless.commerce.admin.shipment.client.dto.v1_0.ShippingAddress;
import com.liferay.headless.commerce.admin.shipment.client.serdes.v1_0.ShippingAddressSerDes;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanPropertiesUtil;
import com.liferay.portal.kernel.json.JSONUtil;
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
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Riccardo Alberti
 * @author Alessio Antonio Rendina
 */
@RunWith(Arquillian.class)
public class ShippingAddressResourceTest
	extends BaseShippingAddressResourceTestCase {

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

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			testGroup.getGroupId(), _commerceCurrency.getCode());

		BigDecimal value = BigDecimal.valueOf(RandomTestUtil.nextDouble());

		_commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency.getCommerceCurrencyId());

		_commerceOrder = CommerceTestUtil.addCommerceOrderShippingDetails(
			_commerceOrder, value);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			testCompany.getCompanyId(), testGroup.getGroupId(),
			_user.getUserId());

		_country = CommerceInventoryTestUtil.addCountry(_serviceContext);

		_region = CommerceInventoryTestUtil.addRegion(
			_country.getCountryId(), _serviceContext);

		_commerceShipment =
			CommerceShipmentLocalServiceUtil.addCommerceShipment(
				_commerceOrder.getCommerceOrderId(), _serviceContext);

		_commerceShipmentWithExternalReferenceCode =
			CommerceShipmentLocalServiceUtil.addCommerceShipment(
				RandomTestUtil.randomString(), _commerceOrder.getGroupId(),
				_commerceOrder.getCommerceAccountId(),
				_commerceOrder.getShippingAddressId(),
				_commerceOrder.getCommerceShippingMethodId(),
				_commerceOrder.getShippingOptionName(), _serviceContext);
	}

	@Override
	@Test
	public void testGetShipmentByExternalReferenceCodeShippingAddress()
		throws Exception {

		ShippingAddress shippingAddress = _toShippingAddress(
			_commerceShipmentWithExternalReferenceCode.fetchCommerceAddress());

		ShippingAddress getShippingAddress =
			shippingAddressResource.getShipmentShippingAddress(
				_commerceShipment.getCommerceShipmentId());

		assertEquals(shippingAddress, getShippingAddress);
		assertValid(getShippingAddress);
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetShipmentByExternalReferenceCodeShippingAddress()
		throws Exception {

		String externalReferenceCode =
			"\"" + _commerceShipment.getExternalReferenceCode() + "\"";

		assertEquals(
			testGraphQLGetShipmentByExternalReferenceCodeShippingAddress_addShippingAddress(),
			ShippingAddressSerDes.toDTO(
				JSONUtil.getValueAsString(
					invokeGraphQLQuery(
						new GraphQLField(
							"shipmentByExternalReferenceCodeShippingAddress",
							HashMapBuilder.<String, Object>put(
								"externalReferenceCode", externalReferenceCode
							).build(),
							getGraphQLFields())),
					"JSONObject/data",
					"Object/shipmentByExternalReferenceCodeShippingAddress")));
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetShipmentShippingAddress() throws Exception {
		super.testGraphQLGetShipmentShippingAddress();
	}

	@Override
	@Test
	public void testPatchShipmentByExternalReferenceCodeShippingAddress()
		throws Exception {

		ShippingAddress randomPatchShippingAddress =
			randomPatchShippingAddress();

		shippingAddressResource.patchShipmentShippingAddress(
			_commerceShipment.getCommerceShipmentId(),
			randomPatchShippingAddress);

		ShippingAddress expectedPatchShippingAddress =
			randomPatchShippingAddress.clone();

		BeanPropertiesUtil.copyProperties(
			expectedPatchShippingAddress, randomPatchShippingAddress);

		ShippingAddress getShippingAddress =
			shippingAddressResource.getShipmentShippingAddress(
				_commerceShipment.getCommerceShipmentId());

		assertEquals(expectedPatchShippingAddress, getShippingAddress);
		assertValid(getShippingAddress);
	}

	@Override
	@Test
	public void testPatchShipmentShippingAddress() throws Exception {
		ShippingAddress randomPatchShippingAddress =
			randomPatchShippingAddress();

		shippingAddressResource.patchShipmentShippingAddress(
			_commerceShipment.getCommerceShipmentId(),
			randomPatchShippingAddress);

		ShippingAddress expectedPatchShippingAddress =
			randomPatchShippingAddress.clone();

		BeanPropertiesUtil.copyProperties(
			expectedPatchShippingAddress, randomPatchShippingAddress);

		ShippingAddress getShippingAddress =
			shippingAddressResource.getShipmentShippingAddress(
				_commerceShipment.getCommerceShipmentId());

		assertEquals(expectedPatchShippingAddress, getShippingAddress);
		assertValid(getShippingAddress);
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"city", "countryISOCode", "name", "street1"};
	}

	@Override
	protected ShippingAddress randomShippingAddress() throws Exception {
		return new ShippingAddress() {
			{
				city = StringUtil.toLowerCase(RandomTestUtil.randomString());
				countryISOCode = _country.getA2();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				latitude = RandomTestUtil.randomDouble();
				longitude = RandomTestUtil.randomDouble();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				phoneNumber = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				regionISOCode = _region.getRegionCode();
				street1 = StringUtil.toLowerCase(RandomTestUtil.randomString());
				street2 = StringUtil.toLowerCase(RandomTestUtil.randomString());
				street3 = StringUtil.toLowerCase(RandomTestUtil.randomString());
				zip = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	@Override
	protected ShippingAddress
			testGetShipmentShippingAddress_addShippingAddress()
		throws Exception {

		return _toShippingAddress(_commerceShipment.fetchCommerceAddress());
	}

	@Override
	protected Long testGetShipmentShippingAddress_getShipmentId()
		throws Exception {

		return _commerceShipment.getCommerceShipmentId();
	}

	@Override
	protected Long testGraphQLGetShipmentShippingAddress_getShipmentId()
		throws Exception {

		return _commerceShipment.getCommerceShipmentId();
	}

	private String _getRegionISOCode(CommerceAddress commerceAddress)
		throws Exception {

		Region region = commerceAddress.getRegion();

		if (region == null) {
			return StringPool.BLANK;
		}

		return region.getRegionCode();
	}

	private ShippingAddress _toShippingAddress(CommerceAddress commerceAddress)
		throws Exception {

		Country country = commerceAddress.getCountry();

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
				regionISOCode = _getRegionISOCode(commerceAddress);
				street1 = commerceAddress.getStreet1();
				street2 = commerceAddress.getStreet2();
				street3 = commerceAddress.getStreet3();
				zip = commerceAddress.getZip();
			}
		};
	}

	@DeleteAfterTestRun
	private CommerceChannel _commerceChannel;

	@DeleteAfterTestRun
	private CommerceCurrency _commerceCurrency;

	@DeleteAfterTestRun
	private CommerceOrder _commerceOrder;

	@DeleteAfterTestRun
	private CommerceShipment _commerceShipment;

	@DeleteAfterTestRun
	private CommerceShipment _commerceShipmentWithExternalReferenceCode;

	@DeleteAfterTestRun
	private Country _country;

	@DeleteAfterTestRun
	private Region _region;

	private ServiceContext _serviceContext;

	@DeleteAfterTestRun
	private User _user;

}