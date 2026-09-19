/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.method.authorize.net.internal;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.math.BigDecimal;

import net.authorize.api.contract.v1.CustomerAddressType;
import net.authorize.api.contract.v1.CustomerDataType;
import net.authorize.api.contract.v1.NameAndAddressType;
import net.authorize.api.contract.v1.TransactionRequestType;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Andrea Sbarra
 */
public class AuthorizeNetCommercePaymentMethodTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetTransactionRequestType() throws Exception {
		String billingFirstName = RandomTestUtil.randomString();
		String billingLastName = RandomTestUtil.randomString();
		String shippingFirstName = RandomTestUtil.randomString();
		String shippingLastName = RandomTestUtil.randomString();

		CommerceOrder commerceOrder = _getCommerceOrder(
			_getCommerceAddress(
				StringBundler.concat(
					billingFirstName, StringPool.SPACE, billingLastName)),
			_getCommerceAddress(
				StringBundler.concat(
					shippingFirstName, StringPool.SPACE,
					RandomTestUtil.randomString(), StringPool.SPACE,
					shippingLastName)),
			new BigDecimal("10.555"));

		TransactionRequestType transactionRequestType =
			_getTransactionRequestType(commerceOrder);

		Assert.assertEquals(
			new BigDecimal("10.56"), transactionRequestType.getAmount());

		AccountEntry accountEntry = commerceOrder.getAccountEntry();
		CustomerDataType customerDataType =
			transactionRequestType.getCustomer();

		Assert.assertEquals(
			accountEntry.getEmailAddress(), customerDataType.getEmail());

		CommerceAddress billingCommerceAddress =
			commerceOrder.getBillingAddress();
		CustomerAddressType customerAddressType =
			transactionRequestType.getBillTo();

		_assertNameAndAddressType(
			billingCommerceAddress, billingFirstName, billingLastName,
			customerAddressType);
		Assert.assertEquals(
			accountEntry.getEmailAddress(), customerAddressType.getEmail());
		Assert.assertEquals(
			billingCommerceAddress.getPhoneNumber(),
			customerAddressType.getPhoneNumber());

		_assertNameAndAddressType(
			commerceOrder.getShippingAddress(), shippingFirstName,
			shippingLastName, transactionRequestType.getShipTo());
	}

	@Test
	public void testGetTransactionRequestTypeWithEmptyBillingAddress()
		throws Exception {

		TransactionRequestType transactionRequestType =
			_getTransactionRequestType(
				_getCommerceOrder(
					Mockito.mock(CommerceAddress.class), null,
					new BigDecimal(RandomTestUtil.randomDouble())));

		CustomerAddressType customerAddressType =
			transactionRequestType.getBillTo();

		Assert.assertNull(customerAddressType.getAddress());
		Assert.assertNull(customerAddressType.getCity());
		Assert.assertNull(customerAddressType.getCountry());
		Assert.assertNull(customerAddressType.getFirstName());
		Assert.assertNull(customerAddressType.getLastName());
		Assert.assertNull(customerAddressType.getPhoneNumber());
		Assert.assertNull(customerAddressType.getState());
		Assert.assertNull(customerAddressType.getZip());
	}

	@Test
	public void testGetTransactionRequestTypeWithSingleWordName()
		throws Exception {

		String firstName = RandomTestUtil.randomString();

		TransactionRequestType transactionRequestType =
			_getTransactionRequestType(
				_getCommerceOrder(
					_getCommerceAddress(firstName), null,
					new BigDecimal(RandomTestUtil.randomDouble())));

		CustomerAddressType customerAddressType =
			transactionRequestType.getBillTo();

		Assert.assertEquals(firstName, customerAddressType.getFirstName());
		Assert.assertNull(customerAddressType.getLastName());
	}

	@Test
	public void testGetTransactionRequestTypeWithoutAddresses()
		throws Exception {

		TransactionRequestType transactionRequestType =
			_getTransactionRequestType(
				_getCommerceOrder(
					null, null, new BigDecimal(RandomTestUtil.randomDouble())));

		Assert.assertNull(transactionRequestType.getBillTo());
		Assert.assertNull(transactionRequestType.getShipTo());
	}

	private void _assertNameAndAddressType(
			CommerceAddress commerceAddress, String firstName, String lastName,
			NameAndAddressType nameAndAddressType)
		throws Exception {

		Assert.assertEquals(
			commerceAddress.getStreet1(), nameAndAddressType.getAddress());
		Assert.assertEquals(
			commerceAddress.getCity(), nameAndAddressType.getCity());

		Country country = commerceAddress.fetchCountry();

		Assert.assertEquals(country.getA2(), nameAndAddressType.getCountry());

		Assert.assertEquals(firstName, nameAndAddressType.getFirstName());
		Assert.assertEquals(lastName, nameAndAddressType.getLastName());

		Region region = commerceAddress.getRegion();

		Assert.assertEquals(
			region.getRegionCode(), nameAndAddressType.getState());

		Assert.assertEquals(
			commerceAddress.getZip(), nameAndAddressType.getZip());
	}

	private AccountEntry _getAccountEntry() {
		AccountEntry accountEntry = Mockito.mock(AccountEntry.class);

		Mockito.when(
			accountEntry.getEmailAddress()
		).thenReturn(
			RandomTestUtil.randomString() + "@liferay.com"
		);

		return accountEntry;
	}

	private CommerceAddress _getCommerceAddress(String name) throws Exception {
		CommerceAddress commerceAddress = Mockito.mock(CommerceAddress.class);

		Country country = _getCountry();

		Mockito.when(
			commerceAddress.fetchCountry()
		).thenReturn(
			country
		);

		Mockito.when(
			commerceAddress.getCity()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			commerceAddress.getName()
		).thenReturn(
			name
		);

		Mockito.when(
			commerceAddress.getPhoneNumber()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Region region = _getRegion();

		Mockito.when(
			commerceAddress.getRegion()
		).thenReturn(
			region
		);

		Mockito.when(
			commerceAddress.getStreet1()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			commerceAddress.getZip()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		return commerceAddress;
	}

	private CommerceCurrency _getCommerceCurrency() {
		CommerceCurrency commerceCurrency = Mockito.mock(
			CommerceCurrency.class);

		Mockito.when(
			commerceCurrency.getMaxFractionDigits()
		).thenReturn(
			2
		);

		Mockito.when(
			commerceCurrency.getRoundingMode()
		).thenReturn(
			"HALF_UP"
		);

		return commerceCurrency;
	}

	private CommerceOrder _getCommerceOrder(
			CommerceAddress billingCommerceAddress,
			CommerceAddress shippingCommerceAddress, BigDecimal total)
		throws Exception {

		CommerceOrder commerceOrder = Mockito.mock(CommerceOrder.class);

		AccountEntry accountEntry = _getAccountEntry();

		Mockito.when(
			commerceOrder.getAccountEntry()
		).thenReturn(
			accountEntry
		);

		Mockito.when(
			commerceOrder.getBillingAddress()
		).thenReturn(
			billingCommerceAddress
		);

		CommerceCurrency commerceCurrency = _getCommerceCurrency();

		Mockito.when(
			commerceOrder.getCommerceCurrency()
		).thenReturn(
			commerceCurrency
		);

		Mockito.when(
			commerceOrder.getShippingAddress()
		).thenReturn(
			shippingCommerceAddress
		);

		Mockito.when(
			commerceOrder.getTotal()
		).thenReturn(
			total
		);

		return commerceOrder;
	}

	private Country _getCountry() {
		Country country = Mockito.mock(Country.class);

		Mockito.when(
			country.getA2()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		return country;
	}

	private Region _getRegion() {
		Region region = Mockito.mock(Region.class);

		Mockito.when(
			region.getRegionCode()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		return region;
	}

	private TransactionRequestType _getTransactionRequestType(
		CommerceOrder commerceOrder) {

		return ReflectionTestUtil.invoke(
			_authorizeNetCommercePaymentMethod, "_getTransactionRequestType",
			new Class<?>[] {CommerceOrder.class}, commerceOrder);
	}

	private final AuthorizeNetCommercePaymentMethod
		_authorizeNetCommercePaymentMethod =
			new AuthorizeNetCommercePaymentMethod();

}