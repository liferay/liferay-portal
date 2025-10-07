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
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.service.CommerceAddressLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.headless.commerce.delivery.cart.client.dto.v1_0.Address;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CountryLocalService;
import com.liferay.portal.kernel.service.RegionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.After;
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
public class AddressResourceTest extends BaseAddressResourceTestCase {

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
			null, null, _serviceContext);

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			testGroup.getCompanyId());

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			testGroup.getGroupId(), _commerceCurrency.getCode());

		_country = _countryLocalService.fetchCountryByNumber(
			_serviceContext.getCompanyId(), "000");

		if (_country == null) {
			_country = _countryLocalService.addCountry(
				"ZZ", "ZZZ", true, true, null, RandomTestUtil.randomString(),
				"000", RandomTestUtil.randomDouble(), true, false, false,
				_serviceContext);

			_region = _regionLocalService.addRegion(
				_country.getCountryId(), true, RandomTestUtil.randomString(),
				RandomTestUtil.randomDouble(), "ZZ", _serviceContext);
		}
		else {
			_region = _regionLocalService.getRegion(
				_country.getCountryId(), "ZZ");
		}
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		if (_commerceOrder != null) {
			_commerceOrderLocalService.deleteCommerceOrder(_commerceOrder);
		}

		if (_commerceAddress != null) {
			_commerceAddressLocalService.deleteCommerceAddress(
				_commerceAddress);
		}

		if (_accountEntry != null) {
			_accountEntryLocalService.deleteAccountEntry(_accountEntry);
		}
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetCartBillingAddres() throws Exception {
		super.testGraphQLGetCartBillingAddres();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetCartByExternalReferenceCodeBillingAddress()
		throws Exception {

		super.testGraphQLGetCartByExternalReferenceCodeBillingAddress();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetCartByExternalReferenceCodeShippingAddress()
		throws Exception {

		super.testGraphQLGetCartByExternalReferenceCodeShippingAddress();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetCartShippingAddres() throws Exception {
		super.testGraphQLGetCartShippingAddres();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"city", "country", "description", "name", "phoneNumber", "region",
			"subtype", "zip"
		};
	}

	@Override
	protected Address testGetCartBillingAddres_addAddress() throws Exception {
		return _toAddress(_getCommerceAddress());
	}

	@Override
	protected Long testGetCartBillingAddres_getCartId() throws Exception {
		return _getCartBillingAddres_getCartId();
	}

	@Override
	protected Address
			testGetCartByExternalReferenceCodeBillingAddress_addAddress()
		throws Exception {

		return _toAddress(_getCommerceAddress());
	}

	@Override
	protected String
			testGetCartByExternalReferenceCodeBillingAddress_getExternalReferenceCode(
				Address address)
		throws Exception {

		return _getCartBillingAddress_getCartExternalReferenceCode();
	}

	@Override
	protected Address
			testGetCartByExternalReferenceCodeShippingAddress_addAddress()
		throws Exception {

		return _toAddress(_getCommerceAddress());
	}

	@Override
	protected String
			testGetCartByExternalReferenceCodeShippingAddress_getExternalReferenceCode(
				Address address)
		throws Exception {

		return _getCartShippingAddress_getCartExternalReferenceCode();
	}

	@Override
	protected Address testGetCartShippingAddres_addAddress() throws Exception {
		return _toAddress(_getCommerceAddress());
	}

	@Override
	protected Long testGetCartShippingAddres_getCartId() throws Exception {
		return _getCartShippingAddres_getCartId();
	}

	@Override
	protected Long testGraphQLGetCartBillingAddres_getCartId()
		throws Exception {

		return _getCartBillingAddres_getCartId();
	}

	@Override
	protected String
			testGraphQLGetCartByExternalReferenceCodeBillingAddress_getExternalReferenceCode(
				Address address)
		throws Exception {

		return _getCartBillingAddress_getCartExternalReferenceCode();
	}

	@Override
	protected String
			testGraphQLGetCartByExternalReferenceCodeShippingAddress_getExternalReferenceCode(
				Address address)
		throws Exception {

		return _getCartShippingAddress_getCartExternalReferenceCode();
	}

	@Override
	protected Long testGraphQLGetCartShippingAddres_getCartId()
		throws Exception {

		return _getCartShippingAddres_getCartId();
	}

	private long _getCartBillingAddres_getCartId() throws Exception {
		_commerceOrder = _getCommerceOrder();

		_commerceOrder.setBillingAddressId(
			_getCommerceAddress().getCommerceAddressId());

		_commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
			_commerceOrder);

		return _commerceOrder.getCommerceOrderId();
	}

	private String _getCartBillingAddress_getCartExternalReferenceCode()
		throws Exception {

		_commerceOrder = _getCommerceOrder();

		_commerceOrder.setBillingAddressId(
			_getCommerceAddress().getCommerceAddressId());

		_commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
			_commerceOrder);

		return _commerceOrder.getExternalReferenceCode();
	}

	private long _getCartShippingAddres_getCartId() throws Exception {
		_commerceOrder = _getCommerceOrder();

		_commerceOrder.setShippingAddressId(
			_getCommerceAddress().getCommerceAddressId());

		_commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
			_commerceOrder);

		return _commerceOrder.getCommerceOrderId();
	}

	private String _getCartShippingAddress_getCartExternalReferenceCode()
		throws Exception {

		_commerceOrder = _getCommerceOrder();

		_commerceOrder.setShippingAddressId(
			_getCommerceAddress().getCommerceAddressId());

		_commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
			_commerceOrder);

		return _commerceOrder.getExternalReferenceCode();
	}

	private CommerceAddress _getCommerceAddress() throws Exception {
		if (_commerceAddress != null) {
			return _commerceAddress;
		}

		_commerceAddress = _commerceAddressLocalService.addCommerceAddress(
			RandomTestUtil.randomString(), AccountEntry.class.getName(),
			_accountEntry.getAccountEntryId(), _country.getCountryId(),
			_region.getRegionId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			StringPool.BLANK,
			CommerceAddressConstants.ADDRESS_TYPE_BILLING_AND_SHIPPING,
			String.valueOf(30133), _serviceContext);

		return _commerceAddress;
	}

	private CommerceOrder _getCommerceOrder() throws Exception {
		if (_commerceOrder != null) {
			return _commerceOrder;
		}

		_commerceOrder = _commerceOrderLocalService.addCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_accountEntry.getAccountEntryId(), _commerceCurrency.getCode(), 0);

		return _commerceOrder;
	}

	private Address _toAddress(CommerceAddress commerceAddress)
		throws Exception {

		Country country1 = commerceAddress.getCountry();
		Region region1 = commerceAddress.getRegion();

		return new Address() {
			{
				city = commerceAddress.getCity();
				country = country1.getName();
				description = commerceAddress.getDescription();
				externalReferenceCode =
					commerceAddress.getExternalReferenceCode();
				id = commerceAddress.getCommerceAddressId();
				name = commerceAddress.getName();
				phoneNumber = commerceAddress.getPhoneNumber();
				region = region1.getName();
				subtype = commerceAddress.getSubtype();
				zip = commerceAddress.getZip();
			}
		};
	}

	private AccountEntry _accountEntry;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	private CommerceAddress _commerceAddress;

	@Inject
	private CommerceAddressLocalService _commerceAddressLocalService;

	@DeleteAfterTestRun
	private CommerceChannel _commerceChannel;

	@DeleteAfterTestRun
	private CommerceCurrency _commerceCurrency;

	private CommerceOrder _commerceOrder;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	@DeleteAfterTestRun
	private Country _country;

	@Inject
	private CountryLocalService _countryLocalService;

	@DeleteAfterTestRun
	private Region _region;

	@Inject
	private RegionLocalService _regionLocalService;

	private ServiceContext _serviceContext;

	@DeleteAfterTestRun
	private User _user;

}