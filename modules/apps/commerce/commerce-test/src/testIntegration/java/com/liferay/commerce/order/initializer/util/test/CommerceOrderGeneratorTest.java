/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.initializer.util.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.initializer.util.CommerceOrderGenerator;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalServiceUtil;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.type.simple.constants.SimpleCPTypeConstants;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.test.util.CommerceInventoryTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ListTypeConstants;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.service.ListTypeLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.settings.FallbackKeysSettingsUtil;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.settings.ModifiableSettings;
import com.liferay.portal.kernel.settings.Settings;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Danny Situ
 */
@RunWith(Arquillian.class)
@Sync
public class CommerceOrderGeneratorTest {

	@ClassRule
	@Rule
	public static AggregateTestRule aggregateTestRule = new AggregateTestRule(
		new LiferayIntegrationTestRule(),
		PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_user = UserTestUtil.addUser();

		_group = GroupTestUtil.addGroup(
			_user.getCompanyId(), _user.getUserId(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID);

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			_user.getCompanyId());

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_user.getCompanyId(), _group.getGroupId(), _user.getUserId());

		_commerceCatalog = _commerceCatalogLocalService.addCommerceCatalog(
			null, RandomTestUtil.randomString(), _commerceCurrency.getCode(),
			LocaleUtil.US.getDisplayLanguage(), _serviceContext);

		_commerceChannel = CommerceChannelLocalServiceUtil.addCommerceChannel(
			null, AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
			_group.getGroupId(), "Test Channel",
			CommerceChannelConstants.CHANNEL_TYPE_SITE, null,
			_commerceCurrency.getCode(), _serviceContext);

		_commerceShippingMethod =
			CommerceTestUtil.addFixedRateCommerceShippingMethod(
				_user.getUserId(), _commerceChannel.getGroupId(),
				new BigDecimal(RandomTestUtil.randomDouble()));

		Settings settings = FallbackKeysSettingsUtil.getSettings(
			new GroupServiceSettingsLocator(
				_commerceChannel.getGroupId(),
				CommerceConstants.SERVICE_NAME_COMMERCE_ACCOUNT));

		ModifiableSettings modifiableSettings =
			settings.getModifiableSettings();

		modifiableSettings.setValue(
			"commerceSiteType",
			String.valueOf(CommerceChannelConstants.SITE_TYPE_B2B));

		modifiableSettings.store();

		_cpDefinition = CPTestUtil.addCPDefinition(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME);

		_country = CommerceInventoryTestUtil.addCountry(_serviceContext);

		_region = CommerceInventoryTestUtil.addRegion(
			_country.getCountryId(), _serviceContext);
	}

	@Test
	public void testSuccessfulCommerceOrderGenerator() throws Exception {
		AccountEntry accountEntry =
			CommerceAccountTestUtil.addBusinessAccountEntry(
				_user.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString() + "@liferay.com",
				RandomTestUtil.randomString(), new long[] {_user.getUserId()},
				null, _serviceContext);

		_accountEntries.add(accountEntry);

		Address address = _addressLocalService.addAddress(
			StringPool.BLANK, _user.getUserId(), AccountEntry.class.getName(),
			accountEntry.getAccountEntryId(), _country.getCountryId(),
			_listTypeLocalService.getListTypeId(
				accountEntry.getCompanyId(), "business",
				ListTypeConstants.CONTACT_ADDRESS),
			_region.getRegionId(), RandomTestUtil.randomString(),
			StringPool.BLANK, false, RandomTestUtil.randomString(), false,
			RandomTestUtil.randomString(), StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, StringPool.BLANK, RandomTestUtil.randomString(),
			_serviceContext);

		_addresses.add(address);

		_commerceOrderGenerator.generate(_group.getGroupId(), 5);

		Assert.assertEquals(
			5,
			_commerceOrderLocalService.getCommerceOrdersCount(
				_commerceChannel.getGroupId(),
				accountEntry.getAccountEntryId()));
	}

	@DeleteAfterTestRun
	private List<AccountEntry> _accountEntries = new ArrayList<>();

	@Inject
	private AddressLocalService _addressLocalService;

	@DeleteAfterTestRun
	private List<Address> _addresses = new ArrayList<>();

	private CommerceCatalog _commerceCatalog;

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@DeleteAfterTestRun
	private CommerceChannel _commerceChannel;

	@DeleteAfterTestRun
	private CommerceCurrency _commerceCurrency;

	@Inject
	private CommerceOrderGenerator _commerceOrderGenerator;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	@DeleteAfterTestRun
	private CommerceShippingMethod _commerceShippingMethod;

	@DeleteAfterTestRun
	private Country _country;

	@DeleteAfterTestRun
	private CPDefinition _cpDefinition;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private ListTypeLocalService _listTypeLocalService;

	@DeleteAfterTestRun
	private Region _region;

	private ServiceContext _serviceContext;
	private User _user;

}