/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.model.impl.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.constants.CommerceAddressConstants;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.service.CommerceAddressLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.CountryLocalService;
import com.liferay.portal.kernel.service.RegionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CountryTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@RunWith(Arquillian.class)
public class CommerceAddressImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		Group group = GroupTestUtil.addGroup();

		User user = UserTestUtil.addUser(
			_companyLocalService.getCompany(group.getCompanyId()));

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			group.getGroupId());

		_accountEntry = CommerceAccountTestUtil.addBusinessAccountEntry(
			user.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString() + "@liferay.com",
			RandomTestUtil.randomString(), new long[] {user.getUserId()}, null,
			_serviceContext);

		_country1 = _countryLocalService.addCountry(
			null, CountryTestUtil.randomA2(group.getCompanyId()),
			CountryTestUtil.randomA3(group.getCompanyId()), true,
			RandomTestUtil.randomBoolean(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomDouble(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.randomBoolean(), RandomTestUtil.randomBoolean(),
			_serviceContext);

		_regionLocalService.addRegion(
			null, _country1.getCountryId(), true, RandomTestUtil.randomString(),
			1, RandomTestUtil.randomString(), _serviceContext);

		_country2 = _countryLocalService.addCountry(
			null, CountryTestUtil.randomA2(group.getCompanyId()),
			CountryTestUtil.randomA3(group.getCompanyId()), true,
			RandomTestUtil.randomBoolean(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomDouble(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.randomBoolean(), RandomTestUtil.randomBoolean(),
			_serviceContext);

		_regionLocalService.addRegion(
			null, _country2.getCountryId(), true, RandomTestUtil.randomString(),
			1, RandomTestUtil.randomString(), _serviceContext);
	}

	@Test
	public void testIsSameAddress() throws Exception {
		CommerceAddress commerceAddress1 = _addCommerceAddress(
			_country1, CommerceAddressConstants.ADDRESS_TYPE_SHIPPING);
		CommerceAddress commerceAddress2 = _addCommerceAddress(
			_country2, CommerceAddressConstants.ADDRESS_TYPE_BILLING);

		Assert.assertFalse(commerceAddress1.isSameAddress(commerceAddress2));

		commerceAddress2.setCity(commerceAddress1.getCity());

		Assert.assertFalse(commerceAddress1.isSameAddress(commerceAddress2));

		commerceAddress2.setCountryId(commerceAddress1.getCountryId());

		Assert.assertFalse(commerceAddress1.isSameAddress(commerceAddress2));

		commerceAddress2.setLatitude(commerceAddress1.getLatitude());

		Assert.assertFalse(commerceAddress1.isSameAddress(commerceAddress2));

		commerceAddress2.setLongitude(commerceAddress1.getLongitude());

		Assert.assertFalse(commerceAddress1.isSameAddress(commerceAddress2));

		commerceAddress2.setName(commerceAddress1.getName());

		Assert.assertFalse(commerceAddress1.isSameAddress(commerceAddress2));

		commerceAddress2.setPhoneNumber(commerceAddress1.getPhoneNumber());

		Assert.assertFalse(commerceAddress1.isSameAddress(commerceAddress2));

		commerceAddress2.setRegionId(commerceAddress1.getRegionId());

		Assert.assertFalse(commerceAddress1.isSameAddress(commerceAddress2));

		commerceAddress2.setStreet1(commerceAddress1.getStreet1());

		Assert.assertFalse(commerceAddress1.isSameAddress(commerceAddress2));

		commerceAddress2.setStreet2(commerceAddress1.getStreet2());

		Assert.assertFalse(commerceAddress1.isSameAddress(commerceAddress2));

		commerceAddress2.setStreet3(commerceAddress1.getStreet3());

		Assert.assertFalse(commerceAddress1.isSameAddress(commerceAddress2));

		commerceAddress2.setType(commerceAddress1.getType());

		Assert.assertFalse(commerceAddress1.isSameAddress(commerceAddress2));

		commerceAddress2.setZip(commerceAddress1.getZip());

		Assert.assertTrue(commerceAddress1.isSameAddress(commerceAddress2));
	}

	private CommerceAddress _addCommerceAddress(Country country, int type)
		throws Exception {

		List<Region> regions = _regionLocalService.getRegions(
			country.getCountryId(), true);

		Region region = regions.get(0);

		CommerceAddress commerceAddress =
			_commerceAddressLocalService.addCommerceAddress(
				StringPool.BLANK, AccountEntry.class.getName(),
				_accountEntry.getAccountEntryId(), country.getCountryId(),
				region.getRegionId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				StringPool.BLANK, type, RandomTestUtil.randomString(),
				_serviceContext);

		commerceAddress.setLatitude(RandomTestUtil.nextDouble());
		commerceAddress.setLongitude(RandomTestUtil.nextDouble());

		return commerceAddress;
	}

	private AccountEntry _accountEntry;

	@Inject
	private CommerceAddressLocalService _commerceAddressLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	private Country _country1;
	private Country _country2;

	@Inject
	private CountryLocalService _countryLocalService;

	@Inject
	private RegionLocalService _regionLocalService;

	private ServiceContext _serviceContext;

}