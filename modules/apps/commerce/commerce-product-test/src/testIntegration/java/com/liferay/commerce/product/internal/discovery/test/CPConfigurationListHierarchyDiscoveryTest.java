/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.discovery.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountGroup;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.product.discovery.CPConfigurationListDiscovery;
import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPConfigurationListLocalService;
import com.liferay.commerce.product.service.CPConfigurationListRelLocalService;
import com.liferay.commerce.product.service.CommerceCatalogService;
import com.liferay.commerce.product.service.CommerceChannelRelLocalService;
import com.liferay.commerce.service.CommerceOrderTypeLocalService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Calendar;
import java.util.Date;

import org.frutilla.FrutillaRule;

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
public class CPConfigurationListHierarchyDiscoveryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_user = UserTestUtil.addUser();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), _user.getUserId());

		_accountEntry1 = CommerceAccountTestUtil.addBusinessAccountEntry(
			_user.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString() + "@liferay.com",
			RandomTestUtil.randomString(), new long[] {_user.getUserId()}, null,
			_serviceContext);

		_accountEntry2 = CommerceAccountTestUtil.addBusinessAccountEntry(
			_user.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString() + "@liferay.com",
			RandomTestUtil.randomString(), new long[] {_user.getUserId()}, null,
			_serviceContext);

		_accountGroup = CommerceAccountTestUtil.addAccountGroupAndAccountRel(
			_group.getCompanyId(), RandomTestUtil.randomString(),
			AccountConstants.ACCOUNT_GROUP_TYPE_STATIC,
			_accountEntry2.getAccountEntryId(), _serviceContext);

		_commerceCatalog = _commerceCatalogService.addCommerceCatalog(
			RandomTestUtil.randomString(),
			AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
			RandomTestUtil.randomString(), "USD", "en_US", _serviceContext);

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), "USD");

		Date date = new Date();

		Calendar calendar = CalendarFactoryUtil.getCalendar(date.getTime());

		_commerceOrderType =
			_commerceOrderTypeLocalService.addCommerceOrderType(
				RandomTestUtil.randomString(), _user.getUserId(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(), true,
				calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH),
				calendar.get(Calendar.YEAR), calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE), 1, calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH),
				calendar.get(Calendar.YEAR), calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE), true, _serviceContext);

		int displayDateHour = calendar.get(Calendar.HOUR);

		if (calendar.get(Calendar.AM_PM) == Calendar.PM) {
			displayDateHour += 12;
		}

		_cpConfigurationList1 =
			_cpConfigurationListLocalService.addCPConfigurationList(
				RandomTestUtil.randomString(), _user.getUserId(),
				_commerceCatalog.getGroupId(), 0, false,
				RandomTestUtil.randomString(), 1D, calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH),
				calendar.get(Calendar.YEAR), displayDateHour,
				calendar.get(Calendar.MINUTE), 0, 0, 0, 0, 0, true,
				new ServiceContext());

		_cpConfigurationList2 =
			_cpConfigurationListLocalService.addCPConfigurationList(
				RandomTestUtil.randomString(), _user.getUserId(),
				_commerceCatalog.getGroupId(), 0, false,
				RandomTestUtil.randomString(), 1D, calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH),
				calendar.get(Calendar.YEAR), displayDateHour,
				calendar.get(Calendar.MINUTE), 0, 0, 0, 0, 0, true,
				new ServiceContext());
		_cpConfigurationList3 =
			_cpConfigurationListLocalService.addCPConfigurationList(
				RandomTestUtil.randomString(), _user.getUserId(),
				_commerceCatalog.getGroupId(), 0, false,
				RandomTestUtil.randomString(), 1D, calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH),
				calendar.get(Calendar.YEAR), displayDateHour,
				calendar.get(Calendar.MINUTE), 0, 0, 0, 0, 0, true,
				new ServiceContext());
		_cpConfigurationList4 =
			_cpConfigurationListLocalService.addCPConfigurationList(
				RandomTestUtil.randomString(), _user.getUserId(),
				_commerceCatalog.getGroupId(), 0, false,
				RandomTestUtil.randomString(), 1D, calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH),
				calendar.get(Calendar.YEAR), displayDateHour,
				calendar.get(Calendar.MINUTE), 0, 0, 0, 0, 0, true,
				new ServiceContext());
		_cpConfigurationList5 =
			_cpConfigurationListLocalService.addCPConfigurationList(
				RandomTestUtil.randomString(), _user.getUserId(),
				_commerceCatalog.getGroupId(), 0, false,
				RandomTestUtil.randomString(), 1D, calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH),
				calendar.get(Calendar.YEAR), displayDateHour,
				calendar.get(Calendar.MINUTE), 0, 0, 0, 0, 0, true,
				new ServiceContext());
		_cpConfigurationList6 =
			_cpConfigurationListLocalService.addCPConfigurationList(
				RandomTestUtil.randomString(), _user.getUserId(),
				_commerceCatalog.getGroupId(), 0, false,
				RandomTestUtil.randomString(), 1D, calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH),
				calendar.get(Calendar.YEAR), displayDateHour,
				calendar.get(Calendar.MINUTE), 0, 0, 0, 0, 0, true,
				new ServiceContext());
		_cpConfigurationList7 =
			_cpConfigurationListLocalService.addCPConfigurationList(
				RandomTestUtil.randomString(), _user.getUserId(),
				_commerceCatalog.getGroupId(), 0, false,
				RandomTestUtil.randomString(), 1D, calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH),
				calendar.get(Calendar.YEAR), displayDateHour,
				calendar.get(Calendar.MINUTE), 0, 0, 0, 0, 0, true,
				new ServiceContext());
		_cpConfigurationList8 =
			_cpConfigurationListLocalService.addCPConfigurationList(
				RandomTestUtil.randomString(), _user.getUserId(),
				_commerceCatalog.getGroupId(), 0, false,
				RandomTestUtil.randomString(), 1D, calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH),
				calendar.get(Calendar.YEAR), displayDateHour,
				calendar.get(Calendar.MINUTE), 0, 0, 0, 0, 0, true,
				new ServiceContext());
		_cpConfigurationList9 =
			_cpConfigurationListLocalService.addCPConfigurationList(
				RandomTestUtil.randomString(), _user.getUserId(),
				_commerceCatalog.getGroupId(), 0, false,
				RandomTestUtil.randomString(), 1D, calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH),
				calendar.get(Calendar.YEAR), displayDateHour,
				calendar.get(Calendar.MINUTE), 0, 0, 0, 0, 0, true,
				new ServiceContext());
		_cpConfigurationList10 =
			_cpConfigurationListLocalService.addCPConfigurationList(
				RandomTestUtil.randomString(), _user.getUserId(),
				_commerceCatalog.getGroupId(), 0, false,
				RandomTestUtil.randomString(), 1D, calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH),
				calendar.get(Calendar.YEAR), displayDateHour,
				calendar.get(Calendar.MINUTE), 0, 0, 0, 0, 0, true,
				new ServiceContext());
		_cpConfigurationList11 =
			_cpConfigurationListLocalService.addCPConfigurationList(
				RandomTestUtil.randomString(), _user.getUserId(),
				_commerceCatalog.getGroupId(), 0, false,
				RandomTestUtil.randomString(), 1D, calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH),
				calendar.get(Calendar.YEAR), displayDateHour,
				calendar.get(Calendar.MINUTE), 0, 0, 0, 0, 0, true,
				new ServiceContext());
		_cpConfigurationList12 =
			_cpConfigurationListLocalService.addCPConfigurationList(
				RandomTestUtil.randomString(), _user.getUserId(),
				_commerceCatalog.getGroupId(), 0, false,
				RandomTestUtil.randomString(), 1D, calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH),
				calendar.get(Calendar.YEAR), displayDateHour,
				calendar.get(Calendar.MINUTE), 0, 0, 0, 0, 0, true,
				new ServiceContext());

		_cpConfigurationListRelLocalService.addCPConfigurationListRel(
			_user.getUserId(), AccountEntry.class.getName(),
			_accountEntry1.getAccountEntryId(),
			_cpConfigurationList2.getCPConfigurationListId());
		_cpConfigurationListRelLocalService.addCPConfigurationListRel(
			_user.getUserId(), AccountGroup.class.getName(),
			_accountGroup.getAccountGroupId(),
			_cpConfigurationList3.getCPConfigurationListId());
		_commerceChannelRelLocalService.addCommerceChannelRel(
			CPConfigurationList.class.getName(),
			_cpConfigurationList4.getCPConfigurationListId(),
			_commerceChannel.getCommerceChannelId(), _serviceContext);
		_cpConfigurationListRelLocalService.addCPConfigurationListRel(
			_user.getUserId(), CommerceOrderType.class.getName(),
			_commerceOrderType.getCommerceOrderTypeId(),
			_cpConfigurationList5.getCPConfigurationListId());
		_cpConfigurationListRelLocalService.addCPConfigurationListRel(
			_user.getUserId(), AccountEntry.class.getName(),
			_accountEntry1.getAccountEntryId(),
			_cpConfigurationList6.getCPConfigurationListId());
		_commerceChannelRelLocalService.addCommerceChannelRel(
			CPConfigurationList.class.getName(),
			_cpConfigurationList6.getCPConfigurationListId(),
			_commerceChannel.getCommerceChannelId(), _serviceContext);
		_cpConfigurationListRelLocalService.addCPConfigurationListRel(
			_user.getUserId(), AccountEntry.class.getName(),
			_accountEntry1.getAccountEntryId(),
			_cpConfigurationList7.getCPConfigurationListId());
		_cpConfigurationListRelLocalService.addCPConfigurationListRel(
			_user.getUserId(), CommerceOrderType.class.getName(),
			_commerceOrderType.getCommerceOrderTypeId(),
			_cpConfigurationList7.getCPConfigurationListId());
		_commerceChannelRelLocalService.addCommerceChannelRel(
			CPConfigurationList.class.getName(),
			_cpConfigurationList8.getCPConfigurationListId(),
			_commerceChannel.getCommerceChannelId(), _serviceContext);
		_cpConfigurationListRelLocalService.addCPConfigurationListRel(
			_user.getUserId(), CommerceOrderType.class.getName(),
			_commerceOrderType.getCommerceOrderTypeId(),
			_cpConfigurationList8.getCPConfigurationListId());
		_cpConfigurationListRelLocalService.addCPConfigurationListRel(
			_user.getUserId(), AccountGroup.class.getName(),
			_accountGroup.getAccountGroupId(),
			_cpConfigurationList9.getCPConfigurationListId());
		_commerceChannelRelLocalService.addCommerceChannelRel(
			CPConfigurationList.class.getName(),
			_cpConfigurationList9.getCPConfigurationListId(),
			_commerceChannel.getCommerceChannelId(), _serviceContext);
		_cpConfigurationListRelLocalService.addCPConfigurationListRel(
			_user.getUserId(), AccountGroup.class.getName(),
			_accountGroup.getAccountGroupId(),
			_cpConfigurationList10.getCPConfigurationListId());
		_cpConfigurationListRelLocalService.addCPConfigurationListRel(
			_user.getUserId(), CommerceOrderType.class.getName(),
			_commerceOrderType.getCommerceOrderTypeId(),
			_cpConfigurationList10.getCPConfigurationListId());
		_cpConfigurationListRelLocalService.addCPConfigurationListRel(
			_user.getUserId(), AccountEntry.class.getName(),
			_accountEntry1.getAccountEntryId(),
			_cpConfigurationList11.getCPConfigurationListId());
		_commerceChannelRelLocalService.addCommerceChannelRel(
			CPConfigurationList.class.getName(),
			_cpConfigurationList11.getCPConfigurationListId(),
			_commerceChannel.getCommerceChannelId(), _serviceContext);
		_cpConfigurationListRelLocalService.addCPConfigurationListRel(
			_user.getUserId(), CommerceOrderType.class.getName(),
			_commerceOrderType.getCommerceOrderTypeId(),
			_cpConfigurationList11.getCPConfigurationListId());
		_cpConfigurationListRelLocalService.addCPConfigurationListRel(
			_user.getUserId(), AccountGroup.class.getName(),
			_accountGroup.getAccountGroupId(),
			_cpConfigurationList12.getCPConfigurationListId());
		_commerceChannelRelLocalService.addCommerceChannelRel(
			CPConfigurationList.class.getName(),
			_cpConfigurationList12.getCPConfigurationListId(),
			_commerceChannel.getCommerceChannelId(), _serviceContext);
		_cpConfigurationListRelLocalService.addCPConfigurationListRel(
			_user.getUserId(), CommerceOrderType.class.getName(),
			_commerceOrderType.getCommerceOrderTypeId(),
			_cpConfigurationList12.getCPConfigurationListId());
	}

	@Test
	public void testGetCPConfigurationListWithoutEligibilityConfigured()
		throws Exception {

		frutillaRule.scenario(
			"When multiple product configuration list are defined for the " +
				"same catalog the highest in the hierarchy shall be taken"
		).given(
			"There is a product configuration list without eligibility " +
				"configured"
		).when(
			"The product configuration list is discovered using no eligibility"
		).then(
			"The eligible product configuration list should be returned"
		);

		CPConfigurationList discoveredCPConfigurationList =
			_cpConfigurationListDiscovery.getCPConfigurationList(
				_commerceCatalog.getCompanyId(), _commerceCatalog.getGroupId(),
				0, 0, 0);

		Assert.assertEquals(
			_cpConfigurationList1.getCPConfigurationListId(),
			discoveredCPConfigurationList.getCPConfigurationListId());
	}

	@Test
	public void testGetCPConfigurationListWithQualifiedAccount()
		throws Exception {

		frutillaRule.scenario(
			"When multiple product configuration list are defined for the " +
				"same catalog the highest in the hierarchy shall be taken"
		).given(
			"There is a product configuration list qualified by an account"
		).when(
			"The product configuration list is discovered using the account"
		).then(
			"The eligible product configuration list should be returned"
		);

		CPConfigurationList discoveredCPConfigurationList =
			_cpConfigurationListDiscovery.getCPConfigurationList(
				_commerceCatalog.getCompanyId(), _commerceCatalog.getGroupId(),
				_accountEntry1.getAccountEntryId(), 0, 0);

		Assert.assertEquals(
			_cpConfigurationList2.getCPConfigurationListId(),
			discoveredCPConfigurationList.getCPConfigurationListId());
	}

	@Test
	public void testGetCPConfigurationListWithQualifiedAccountAndChannel()
		throws Exception {

		frutillaRule.scenario(
			"When multiple product configuration list are defined for the " +
				"same catalog the highest in the hierarchy shall be taken"
		).given(
			"There is a product configuration list qualified by an account " +
				"and channel"
		).when(
			"The product configuration list is discovered using the account " +
				"and channel"
		).then(
			"The eligible product configuration list should be returned"
		);

		CPConfigurationList discoveredCPConfigurationList =
			_cpConfigurationListDiscovery.getCPConfigurationList(
				_commerceCatalog.getCompanyId(), _commerceCatalog.getGroupId(),
				_accountEntry1.getAccountEntryId(),
				_commerceChannel.getCommerceChannelId(), 0);

		Assert.assertEquals(
			_cpConfigurationList6.getCPConfigurationListId(),
			discoveredCPConfigurationList.getCPConfigurationListId());
	}

	@Test
	public void testGetCPConfigurationListWithQualifiedAccountAndChannelAndOrderType()
		throws Exception {

		frutillaRule.scenario(
			"When multiple product configuration list are defined for the " +
				"same catalog the highest in the hierarchy shall be taken"
		).given(
			"There is a product configuration list qualified by an account, " +
				"channel and order type"
		).when(
			"The product configuration list is discovered using the account, " +
				"channel and order type"
		).then(
			"The eligible product configuration list should be returned"
		);

		CPConfigurationList discoveredCPConfigurationList =
			_cpConfigurationListDiscovery.getCPConfigurationList(
				_commerceCatalog.getCompanyId(), _commerceCatalog.getGroupId(),
				_accountEntry1.getAccountEntryId(),
				_commerceChannel.getCommerceChannelId(),
				_commerceOrderType.getCommerceOrderTypeId());

		Assert.assertEquals(
			_cpConfigurationList11.getCPConfigurationListId(),
			discoveredCPConfigurationList.getCPConfigurationListId());
	}

	@Test
	public void testGetCPConfigurationListWithQualifiedAccountAndOrderType()
		throws Exception {

		frutillaRule.scenario(
			"When multiple product configuration list are defined for the " +
				"same catalog the highest in the hierarchy shall be taken"
		).given(
			"There is a product configuration list qualified by an account " +
				"and order type"
		).when(
			"The product configuration list is discovered using the account " +
				"and order type"
		).then(
			"The eligible product configuration list should be returned"
		);

		CPConfigurationList discoveredCPConfigurationList =
			_cpConfigurationListDiscovery.getCPConfigurationList(
				_commerceCatalog.getCompanyId(), _commerceCatalog.getGroupId(),
				_accountEntry1.getAccountEntryId(), 0,
				_commerceOrderType.getCommerceOrderTypeId());

		Assert.assertEquals(
			_cpConfigurationList7.getCPConfigurationListId(),
			discoveredCPConfigurationList.getCPConfigurationListId());
	}

	@Test
	public void testGetCPConfigurationListWithQualifiedAccountGroup()
		throws Exception {

		frutillaRule.scenario(
			"When multiple product configuration list are defined for the " +
				"same catalog the highest in the hierarchy shall be taken"
		).given(
			"There is a product configuration list qualified by an account " +
				"group"
		).when(
			"The product configuration list is discovered using the account " +
				"group"
		).then(
			"The eligible product configuration list should be returned"
		);

		CPConfigurationList discoveredCPConfigurationList =
			_cpConfigurationListDiscovery.getCPConfigurationList(
				_commerceCatalog.getCompanyId(), _commerceCatalog.getGroupId(),
				_accountEntry2.getAccountEntryId(), 0, 0);

		Assert.assertEquals(
			_cpConfigurationList3.getCPConfigurationListId(),
			discoveredCPConfigurationList.getCPConfigurationListId());
	}

	@Test
	public void testGetCPConfigurationListWithQualifiedAccountGroupAndChannel()
		throws Exception {

		frutillaRule.scenario(
			"When multiple product configuration list are defined for the " +
				"same catalog the highest in the hierarchy shall be taken"
		).given(
			"There is a product configuration list qualified by an account " +
				"group and channel"
		).when(
			"The product configuration list is discovered using the account " +
				"group and channel"
		).then(
			"The eligible product configuration list should be returned"
		);

		CPConfigurationList discoveredCPConfigurationList =
			_cpConfigurationListDiscovery.getCPConfigurationList(
				_commerceCatalog.getCompanyId(), _commerceCatalog.getGroupId(),
				_accountEntry2.getAccountEntryId(),
				_commerceChannel.getCommerceChannelId(), 0);

		Assert.assertEquals(
			_cpConfigurationList9.getCPConfigurationListId(),
			discoveredCPConfigurationList.getCPConfigurationListId());
	}

	@Test
	public void testGetCPConfigurationListWithQualifiedAccountGroupAndChannelAndOrderType()
		throws Exception {

		frutillaRule.scenario(
			"When multiple product configuration list are defined for the " +
				"same catalog the highest in the hierarchy shall be taken"
		).given(
			"There is a product configuration list qualified by an account " +
				"group, channel and order type"
		).when(
			"The product configuration list is discovered using the account " +
				"group, channel and order type"
		).then(
			"The eligible product configuration list should be returned"
		);

		CPConfigurationList discoveredCPConfigurationList =
			_cpConfigurationListDiscovery.getCPConfigurationList(
				_commerceCatalog.getCompanyId(), _commerceCatalog.getGroupId(),
				_accountEntry2.getAccountEntryId(),
				_commerceChannel.getCommerceChannelId(),
				_commerceOrderType.getCommerceOrderTypeId());

		Assert.assertEquals(
			_cpConfigurationList12.getCPConfigurationListId(),
			discoveredCPConfigurationList.getCPConfigurationListId());
	}

	@Test
	public void testGetCPConfigurationListWithQualifiedAccountGroupAndOrderType()
		throws Exception {

		frutillaRule.scenario(
			"When multiple product configuration list are defined for the " +
				"same catalog the highest in the hierarchy shall be taken"
		).given(
			"There is a product configuration list qualified by an account " +
				"group and order type"
		).when(
			"The product configuration list is discovered using the account " +
				"group and order type"
		).then(
			"The eligible product configuration list should be returned"
		);

		CPConfigurationList discoveredCPConfigurationList =
			_cpConfigurationListDiscovery.getCPConfigurationList(
				_commerceCatalog.getCompanyId(), _commerceCatalog.getGroupId(),
				_accountEntry2.getAccountEntryId(), 0,
				_commerceOrderType.getCommerceOrderTypeId());

		Assert.assertEquals(
			_cpConfigurationList10.getCPConfigurationListId(),
			discoveredCPConfigurationList.getCPConfigurationListId());
	}

	@Test
	public void testGetCPConfigurationListWithQualifiedChannel()
		throws Exception {

		frutillaRule.scenario(
			"When multiple product configuration list are defined for the " +
				"same catalog the highest in the hierarchy shall be taken"
		).given(
			"There is a product configuration list qualified by an channel"
		).when(
			"The product configuration list is discovered using the channel"
		).then(
			"The eligible product configuration list should be returned"
		);

		CPConfigurationList discoveredCPConfigurationList =
			_cpConfigurationListDiscovery.getCPConfigurationList(
				_commerceCatalog.getCompanyId(), _commerceCatalog.getGroupId(),
				0, _commerceChannel.getCommerceChannelId(), 0);

		Assert.assertEquals(
			_cpConfigurationList4.getCPConfigurationListId(),
			discoveredCPConfigurationList.getCPConfigurationListId());
	}

	@Test
	public void testGetCPConfigurationListWithQualifiedChannelAndOrderType()
		throws Exception {

		frutillaRule.scenario(
			"When multiple product configuration list are defined for the " +
				"same catalog the highest in the hierarchy shall be taken"
		).given(
			"There is a product configuration list qualified by an channel " +
				"and order type"
		).when(
			"The product configuration list is discovered using the channel " +
				"and order type"
		).then(
			"The eligible product configuration list should be returned"
		);

		CPConfigurationList discoveredCPConfigurationList =
			_cpConfigurationListDiscovery.getCPConfigurationList(
				_commerceCatalog.getCompanyId(), _commerceCatalog.getGroupId(),
				0, _commerceChannel.getCommerceChannelId(),
				_commerceOrderType.getCommerceOrderTypeId());

		Assert.assertEquals(
			_cpConfigurationList8.getCPConfigurationListId(),
			discoveredCPConfigurationList.getCPConfigurationListId());
	}

	@Test
	public void testGetCPConfigurationListWithQualifiedOrderType()
		throws Exception {

		frutillaRule.scenario(
			"When multiple product configuration list are defined for the " +
				"same catalog the highest in the hierarchy shall be taken"
		).given(
			"There is a product configuration list qualified by an order type"
		).when(
			"The product configuration list is discovered using the order type"
		).then(
			"The eligible product configuration list should be returned"
		);

		CPConfigurationList discoveredCPConfigurationList =
			_cpConfigurationListDiscovery.getCPConfigurationList(
				_commerceCatalog.getCompanyId(), _commerceCatalog.getGroupId(),
				0, 0, _commerceOrderType.getCommerceOrderTypeId());

		Assert.assertEquals(
			_cpConfigurationList5.getCPConfigurationListId(),
			discoveredCPConfigurationList.getCPConfigurationListId());
	}

	@Rule
	public final FrutillaRule frutillaRule = new FrutillaRule();

	@DeleteAfterTestRun
	private AccountEntry _accountEntry1;

	@DeleteAfterTestRun
	private AccountEntry _accountEntry2;

	@DeleteAfterTestRun
	private AccountGroup _accountGroup;

	@DeleteAfterTestRun
	private CommerceCatalog _commerceCatalog;

	@Inject
	private CommerceCatalogService _commerceCatalogService;

	@DeleteAfterTestRun
	private CommerceChannel _commerceChannel;

	@Inject
	private CommerceChannelRelLocalService _commerceChannelRelLocalService;

	@DeleteAfterTestRun
	private CommerceOrderType _commerceOrderType;

	@Inject
	private CommerceOrderTypeLocalService _commerceOrderTypeLocalService;

	@DeleteAfterTestRun
	private CPConfigurationList _cpConfigurationList1;

	@DeleteAfterTestRun
	private CPConfigurationList _cpConfigurationList2;

	@DeleteAfterTestRun
	private CPConfigurationList _cpConfigurationList3;

	@DeleteAfterTestRun
	private CPConfigurationList _cpConfigurationList4;

	@DeleteAfterTestRun
	private CPConfigurationList _cpConfigurationList5;

	@DeleteAfterTestRun
	private CPConfigurationList _cpConfigurationList6;

	@DeleteAfterTestRun
	private CPConfigurationList _cpConfigurationList7;

	@DeleteAfterTestRun
	private CPConfigurationList _cpConfigurationList8;

	@DeleteAfterTestRun
	private CPConfigurationList _cpConfigurationList9;

	@DeleteAfterTestRun
	private CPConfigurationList _cpConfigurationList10;

	@DeleteAfterTestRun
	private CPConfigurationList _cpConfigurationList11;

	@DeleteAfterTestRun
	private CPConfigurationList _cpConfigurationList12;

	@Inject(
		filter = "component.name=com.liferay.commerce.product.internal.discovery.CPConfigurationListHierarchyDiscoveryImpl"
	)
	private CPConfigurationListDiscovery _cpConfigurationListDiscovery;

	@Inject
	private CPConfigurationListLocalService _cpConfigurationListLocalService;

	@Inject
	private CPConfigurationListRelLocalService
		_cpConfigurationListRelLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private ServiceContext _serviceContext;
	private User _user;

}