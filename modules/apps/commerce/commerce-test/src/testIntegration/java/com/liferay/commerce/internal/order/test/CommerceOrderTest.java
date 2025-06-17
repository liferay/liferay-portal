/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.order.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountEntryOrganizationRel;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryOrganizationRelLocalService;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.constants.CommerceAddressConstants;
import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.context.CommerceContextFactory;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.exception.CommerceOrderAccountLimitException;
import com.liferay.commerce.helper.CommerceAccountHelper;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.order.engine.CommerceOrderEngine;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceAddressLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.commerce.test.util.context.TestCustomCommerceContextFactory;
import com.liferay.commerce.test.util.context.TestCustomCommerceContextHttp;
import com.liferay.commerce.util.CommerceOrderThreadLocal;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CountryLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.RegionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.settings.FallbackKeysSettingsUtil;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.settings.ModifiableSettings;
import com.liferay.portal.kernel.settings.Settings;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.ArrayList;
import java.util.List;

import org.frutilla.FrutillaRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.runtime.ServiceComponentRuntime;
import org.osgi.service.component.runtime.dto.ComponentDescriptionDTO;
import org.osgi.util.promise.Promise;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Alec Sloan
 */
@RunWith(Arquillian.class)
public class CommerceOrderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		ComponentDescriptionDTO componentDescriptionDTO =
			_serviceComponentRuntime.getComponentDescriptionDTO(
				FrameworkUtil.getBundle(TestCustomCommerceContextFactory.class),
				TestCustomCommerceContextFactory.class.getName());

		Promise<Void> voidPromise = _serviceComponentRuntime.enableComponent(
			componentDescriptionDTO);

		voidPromise.getValue();
	}

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
		_user = UserTestUtil.addUser(true);

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			_group.getCompanyId());

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());

		_commerceChannel = _commerceChannelLocalService.addCommerceChannel(
			null, AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
			_group.getGroupId(), "Test Channel",
			CommerceChannelConstants.CHANNEL_TYPE_SITE, null,
			_commerceCurrency.getCode(), _serviceContext);

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

		_countryLocalService.deleteCompanyCountries(_group.getCompanyId());
	}

	@After
	public void tearDown() throws Exception {
		_commerceOrderLocalService.deleteCommerceOrders(
			_commerceChannel.getGroupId());

		ComponentDescriptionDTO componentDescriptionDTO =
			_serviceComponentRuntime.getComponentDescriptionDTO(
				FrameworkUtil.getBundle(TestCustomCommerceContextFactory.class),
				TestCustomCommerceContextFactory.class.getName());

		Promise<Void> voidPromise = _serviceComponentRuntime.disableComponent(
			componentDescriptionDTO);

		voidPromise.getValue();
	}

	@Test
	public void testCommerceOrderCurrency() throws Exception {
		CommerceContext commerceContext = _commerceContextFactory.create(
			new MockHttpServletRequest());

		Assert.assertTrue(
			commerceContext instanceof TestCustomCommerceContextHttp);

		CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			commerceContext.getCommerceCurrency());

		Assert.assertEquals(
			commerceContext.getCommerceCurrency(),
			commerceOrder.getCommerceCurrency());

		CommerceCurrency commerceOrderCommerceCurrency =
			commerceOrder.getCommerceCurrency();

		Assert.assertNotEquals(
			commerceOrderCommerceCurrency.getCode(),
			_commerceChannel.getCommerceCurrencyCode());
	}

	@Test
	public void testGetCommerceOrderFilteredByAccount() throws Exception {
		frutillaRule.scenario(
			"Ensure that users can only see orders for accounts they belong to"
		).given(
			"2 accounts on a B2B site, with 1 order each"
		).and(
			"A Group"
		).and(
			"A User"
		).when(
			"I pull all orders for that user"
		).then(
			"I should only see 1 order"
		).but(
			"If they are added to the second account they should see 2 orders"
		);

		AccountEntry accountEntry =
			CommerceAccountTestUtil.addBusinessAccountEntry(
				_serviceContext.getUserId(), "Test Business Account", null,
				null, new long[] {_user.getUserId()}, null, _serviceContext);

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.addCommerceOrder(
				_user.getUserId(), _commerceChannel.getGroupId(),
				accountEntry.getAccountEntryId(), _commerceCurrency.getCode(),
				0);

		User secondUser = UserTestUtil.addUser();

		AccountEntry secondAccountEntry =
			CommerceAccountTestUtil.addBusinessAccountEntry(
				_serviceContext.getUserId(), "Second Test Business Account",
				null, null, null, null, _serviceContext);

		CommerceOrder secondCommerceOrder =
			_commerceOrderLocalService.addCommerceOrder(
				secondUser.getUserId(), _commerceChannel.getGroupId(),
				secondAccountEntry.getAccountEntryId(),
				_commerceCurrency.getCode(), 0);

		List<CommerceOrder> commerceOrders = _getUserOrders(
			_commerceChannel.getGroupId(), false);

		Assert.assertEquals(
			commerceOrders.toString(), 1, commerceOrders.size());

		CommerceOrder actualCommerceOrder = commerceOrders.get(0);

		AccountEntry actualAccountEntry = actualCommerceOrder.getAccountEntry();

		Assert.assertEquals(commerceOrder, actualCommerceOrder);

		Assert.assertEquals(accountEntry, actualAccountEntry);
		Assert.assertNotEquals(secondAccountEntry, actualAccountEntry);
		Assert.assertNotEquals(secondCommerceOrder, actualCommerceOrder);

		// Add the user to the second account and they should see 2 orders

		CommerceAccountTestUtil.addAccountEntryUserRels(
			secondAccountEntry.getAccountEntryId(),
			new long[] {_user.getUserId()}, _serviceContext);

		commerceOrders = _getUserOrders(_commerceChannel.getGroupId(), false);

		Assert.assertEquals(
			commerceOrders.toString(), 2, commerceOrders.size());

		Assert.assertTrue(commerceOrders.contains(commerceOrder));
		Assert.assertTrue(commerceOrders.contains(secondCommerceOrder));

		_commerceOrderLocalService.deleteCommerceOrders(
			_commerceChannel.getGroupId());
		_accountEntryLocalService.deleteAccountEntry(accountEntry);
		_accountEntryLocalService.deleteAccountEntry(secondAccountEntry);
		_userLocalService.deleteUser(secondUser);
	}

	@Test
	public void testGetCommerceOrderFromAccountPermission() throws Exception {
		frutillaRule.scenario(
			"Ensure that users can only see orders for accounts they belong to"
		).given(
			"2 accounts on a B2B site, with 1 order each"
		).and(
			"A Group"
		).and(
			"A User"
		).and(
			"The User is part of both accounts"
		).when(
			"I pull all orders for that user"
		).then(
			"I should see 2 order"
		).but(
			"If I remove the user from the second account they should only " +
				"see 1 order"
		);

		AccountEntry accountEntry =
			CommerceAccountTestUtil.addBusinessAccountEntry(
				_serviceContext.getUserId(), "Test Business Account", null,
				null, new long[] {_user.getUserId()}, null, _serviceContext);

		long commerceChannelGroupId = _commerceChannel.getGroupId();

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.addCommerceOrder(
				_user.getUserId(), commerceChannelGroupId,
				accountEntry.getAccountEntryId(), _commerceCurrency.getCode(),
				0);

		User secondUser = UserTestUtil.addUser();

		AccountEntry secondAccountEntry =
			CommerceAccountTestUtil.addBusinessAccountEntry(
				_serviceContext.getUserId(), "Second Test Business Account",
				null, null, null, null, _serviceContext);

		// Add the user to the second account

		CommerceAccountTestUtil.addAccountEntryUserRels(
			secondAccountEntry.getAccountEntryId(),
			new long[] {_user.getUserId()}, _serviceContext);

		CommerceOrder secondCommerceOrder =
			_commerceOrderLocalService.addCommerceOrder(
				secondUser.getUserId(), commerceChannelGroupId,
				secondAccountEntry.getAccountEntryId(),
				_commerceCurrency.getCode(), 0);

		List<CommerceOrder> commerceOrders = _getUserOrders(
			commerceChannelGroupId, false);

		Assert.assertEquals(
			commerceOrders.toString(), 2, commerceOrders.size());

		Assert.assertTrue(commerceOrders.contains(commerceOrder));
		Assert.assertTrue(commerceOrders.contains(secondCommerceOrder));

		// Remove the user from the second account and get user's orders again

		_accountEntryUserRelLocalService.deleteAccountEntryUserRels(
			secondAccountEntry.getAccountEntryId(),
			new long[] {_user.getUserId()});

		commerceOrders = _getUserOrders(commerceChannelGroupId, false);

		Assert.assertEquals(
			commerceOrders.toString(), 1, commerceOrders.size());

		CommerceOrder actualCommerceOrder = commerceOrders.get(0);

		AccountEntry acutalAccountEntry = actualCommerceOrder.getAccountEntry();

		Assert.assertEquals(commerceOrder, actualCommerceOrder);

		Assert.assertEquals(accountEntry, acutalAccountEntry);
		Assert.assertNotEquals(secondAccountEntry, acutalAccountEntry);
		Assert.assertNotEquals(secondCommerceOrder, actualCommerceOrder);

		_commerceOrderLocalService.deleteCommerceOrders(commerceChannelGroupId);
		_accountEntryLocalService.deleteAccountEntry(accountEntry);
		_accountEntryLocalService.deleteAccountEntry(secondAccountEntry);
		_userLocalService.deleteUser(secondUser);
	}

	@Test
	public void testGetCommerceOrdersForOmniadmin() throws Exception {
		frutillaRule.scenario(
			"Ensure that the Omni-Admin is able to pull all orders"
		).given(
			"A Group"
		).and(
			"A User"
		).and(
			"A random amount of Accounts"
		).and(
			"A random amount of Orders for each Account"
		).when(
			"I get the count of all Orders the Omni-Admin can see"
		).then(
			"I should get the same count of Orders that were created"
		);

		long commerceChannelGroupId = _commerceChannel.getGroupId();

		int ordersCreated = 0;

		int accountsToCreate = RandomTestUtil.randomInt(2, 10);

		List<User> randomUsers = new ArrayList<>();
		List<AccountEntry> randomAccountEntries = new ArrayList<>();
		List<CommerceOrder> randomOrders = new ArrayList<>();

		for (int i = 0; i < accountsToCreate; i++) {
			User user = UserTestUtil.addUser(true);

			randomUsers.add(user);

			AccountEntry accountEntry =
				CommerceAccountTestUtil.addBusinessAccountEntry(
					_user.getUserId(), "Test Generated Account " + i, null,
					null, new long[] {_user.getUserId()}, null,
					_serviceContext);

			randomAccountEntries.add(accountEntry);

			int ordersToCreate = RandomTestUtil.randomInt(1, 3);

			for (int j = 0; j < ordersToCreate; j++) {
				randomOrders.add(
					_commerceOrderLocalService.addCommerceOrder(
						user.getUserId(), commerceChannelGroupId,
						accountEntry.getAccountEntryId(),
						_commerceCurrency.getCode(), 0));
			}

			ordersCreated += ordersToCreate;
		}

		long ordersCount =
			_commerceOrderService.getUserPendingCommerceOrdersCount(
				_group.getCompanyId(), commerceChannelGroupId,
				StringPool.BLANK);

		Assert.assertEquals(
			"Assert the Omni-admin can see the count of all pending orders",
			ordersCreated, ordersCount);

		List<CommerceOrder> commerceOrders =
			_commerceOrderService.getUserPendingCommerceOrders(
				_group.getCompanyId(), commerceChannelGroupId, StringPool.BLANK,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			"Assert the Omni-admin can see all pending orders were created",
			ListUtil.sort(randomOrders), ListUtil.sort(commerceOrders));

		// Checkout a random number of orders

		List<CommerceOrder> placedCommerceOrders = new ArrayList<>();

		for (CommerceOrder commerceOrder : randomOrders) {
			if (RandomTestUtil.randomBoolean()) {
				CommerceAddress commerceAddress = _addAddressToAccount(
					commerceOrder.getCommerceAccountId());

				commerceOrder.setBillingAddressId(
					commerceAddress.getCommerceAddressId());
				commerceOrder.setShippingAddressId(
					commerceAddress.getCommerceAddressId());

				commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
					commerceOrder);

				placedCommerceOrders.add(
					_commerceOrderEngine.checkoutCommerceOrder(
						commerceOrder, _user.getUserId()));
			}
		}

		ordersCount = _commerceOrderService.getUserPlacedCommerceOrdersCount(
			_group.getCompanyId(), commerceChannelGroupId, StringPool.BLANK);

		Assert.assertEquals(
			"Assert the Omni-admin can see the count of all placed orders",
			placedCommerceOrders.size(), ordersCount);

		commerceOrders = _commerceOrderService.getUserPlacedCommerceOrders(
			_group.getCompanyId(), commerceChannelGroupId, StringPool.BLANK,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			"Assert the Omni-admin can see all placed orders",
			ListUtil.sort(placedCommerceOrders), ListUtil.sort(commerceOrders));

		_commerceOrderLocalService.deleteCommerceOrders(commerceChannelGroupId);

		for (AccountEntry accountEntry : randomAccountEntries) {
			_accountEntryLocalService.deleteAccountEntry(accountEntry);
		}

		for (User user : randomUsers) {
			_userLocalService.deleteUser(user);
		}
	}

	@Test
	public void testGetCommerceOrdersForSalesAgent() throws Exception {
		frutillaRule.scenario(
			"Ensure that the Sales Agents are able to pull orders for their " +
				"organizations"
		).given(
			"2 Organizations"
		).and(
			"2 Accounts with 1 order each"
		).and(
			"A User who has the Sales Agent role"
		).and(
			"The User has not been assigned to either account"
		).and(
			"The User is part of both organizations"
		).when(
			"The User queries for orders"
		).then(
			"They should see and count 0 orders"
		).but(
			"If 1 Organization is associated with 1 accounts, the user " +
				"should see and count 1 order"
		).and(
			"If the other Organization is associated with the other account, " +
				"the user should see and count 2 orders"
		);

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		long adminUserId = permissionChecker.getUserId();

		Organization organization = _organizationLocalService.addOrganization(
			adminUserId, OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			"Test Organization" + RandomTestUtil.randomString(), false);

		_organizationLocalService.addUserOrganization(
			_user.getUserId(), organization);

		Organization secondOrganization =
			_organizationLocalService.addOrganization(
				adminUserId,
				OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
				"Test Organization" + RandomTestUtil.randomString(), false);

		_organizationLocalService.addUserOrganization(
			_user.getUserId(), secondOrganization);

		long commerceChannelGroupId = _commerceChannel.getGroupId();

		AccountEntry accountEntry =
			CommerceAccountTestUtil.addBusinessAccountEntry(
				_serviceContext.getUserId(), "Test Business Account", null,
				null, null, null, _serviceContext);

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.addCommerceOrder(
				adminUserId, commerceChannelGroupId,
				accountEntry.getAccountEntryId(), _commerceCurrency.getCode(),
				0);

		AccountEntry secondAccountEntry =
			CommerceAccountTestUtil.addBusinessAccountEntry(
				_serviceContext.getUserId(), "Test Business Account 2", null,
				null, null, null, _serviceContext);

		CommerceOrder secondCommerceOrder =
			_commerceOrderLocalService.addCommerceOrder(
				adminUserId, commerceChannelGroupId,
				secondAccountEntry.getAccountEntryId(),
				_commerceCurrency.getCode(), 0);

		Role role = _roleLocalService.fetchRole(
			_serviceContext.getCompanyId(), "Sales Agent");

		if (role == null) {
			role = _addSalesAgentRole();
		}

		_userGroupRoleLocalService.addUserGroupRoles(
			_user.getUserId(), accountEntry.getAccountEntryGroupId(),
			new long[] {role.getRoleId()});

		long ordersCountByUser = _getUserOrdersCount(
			commerceChannelGroupId, false);

		Assert.assertEquals(0, ordersCountByUser);

		List<CommerceOrder> commerceOrders = _getUserOrders(
			commerceChannelGroupId, false);

		Assert.assertEquals(
			"The Organizations are not associated with any accounts. They " +
				"should not be able to get any orders",
			0, commerceOrders.size());

		// The Sales Agent's first organization is added to the first Account

		AccountEntryOrganizationRel accountEntryOrganizationRel =
			_accountEntryOrganizationRelLocalService.
				addAccountEntryOrganizationRel(
					accountEntry.getAccountEntryId(),
					organization.getOrganizationId());

		ordersCountByUser = _getUserOrdersCount(commerceChannelGroupId, false);

		Assert.assertEquals(1, ordersCountByUser);

		commerceOrders = _getUserOrders(commerceChannelGroupId, false);

		Assert.assertEquals(commerceOrder.toString(), 1, commerceOrders.size());

		Assert.assertEquals(
			"The Sales Agent should see first order created, which belongs " +
				"to the account their 1 organization is associated with",
			commerceOrder, commerceOrders.get(0));

		// The Sales Agent's Second organization is added to the second Account

		AccountEntryOrganizationRel secondAccountEntryOrganizationRel =
			_accountEntryOrganizationRelLocalService.
				addAccountEntryOrganizationRel(
					secondAccountEntry.getAccountEntryId(),
					secondOrganization.getOrganizationId());

		ordersCountByUser = _getUserOrdersCount(commerceChannelGroupId, false);

		Assert.assertEquals(2, ordersCountByUser);

		commerceOrders = _getUserOrders(commerceChannelGroupId, false);

		Assert.assertEquals(
			"The Sales Agent should see the 2 Orders that were created", 2,
			commerceOrders.size());

		Assert.assertEquals(
			"The first Order the Sales manager can see should match the " +
				"first created",
			true, commerceOrders.contains(commerceOrder));
		Assert.assertEquals(
			"The second Order the Sales manager can see should match the " +
				"second created",
			true, commerceOrders.contains(secondCommerceOrder));

		// Checkout the first order

		CommerceAddress commerceAddress = _addAddressToAccount(
			accountEntry.getAccountEntryId());

		commerceOrder.setBillingAddressId(
			commerceAddress.getCommerceAddressId());
		commerceOrder.setShippingAddressId(
			commerceAddress.getCommerceAddressId());

		commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
			commerceOrder);

		commerceOrder = _commerceOrderEngine.checkoutCommerceOrder(
			commerceOrder, _user.getUserId());

		ordersCountByUser = _getUserOrdersCount(commerceChannelGroupId, true);

		Assert.assertEquals(1, ordersCountByUser);

		commerceOrders = _getUserOrders(commerceChannelGroupId, true);

		Assert.assertEquals(
			commerceOrders.toString(), 1, commerceOrders.size());

		Assert.assertEquals(
			"The Sales Agent should see first order created, which belongs " +
				"to the account their 1 organization is associated with",
			commerceOrder, commerceOrders.get(0));

		// Checkout the second order

		CommerceAddress secondCommerceAddress = _addAddressToAccount(
			secondAccountEntry.getAccountEntryId());

		secondCommerceOrder.setBillingAddressId(
			secondCommerceAddress.getCommerceAddressId());
		secondCommerceOrder.setShippingAddressId(
			secondCommerceAddress.getCommerceAddressId());

		secondCommerceOrder = _commerceOrderLocalService.updateCommerceOrder(
			secondCommerceOrder);

		secondCommerceOrder = _commerceOrderEngine.checkoutCommerceOrder(
			secondCommerceOrder, _user.getUserId());

		ordersCountByUser = _getUserOrdersCount(commerceChannelGroupId, true);

		Assert.assertEquals(2, ordersCountByUser);

		commerceOrders = _getUserOrders(commerceChannelGroupId, true);

		Assert.assertEquals(
			"The Sales Agent should see the 2 Placed Orders", 2,
			commerceOrders.size());

		Assert.assertEquals(
			"The first Order the Sales manager can see should match the " +
				"first created",
			true, commerceOrders.contains(commerceOrder));
		Assert.assertEquals(
			"The second Order the Sales manager can see should match the " +
				"second created",
			true, commerceOrders.contains(secondCommerceOrder));

		// There shouldn't be any open orders

		ordersCountByUser = _getUserOrdersCount(commerceChannelGroupId, false);

		Assert.assertEquals(0, ordersCountByUser);

		commerceOrders = _getUserOrders(commerceChannelGroupId, false);

		Assert.assertEquals(
			commerceOrders.toString(), 0, commerceOrders.size());

		// Remove the Organization association from the second account

		_accountEntryOrganizationRelLocalService.
			deleteAccountEntryOrganizationRel(
				secondAccountEntryOrganizationRel);

		ordersCountByUser = _getUserOrdersCount(commerceChannelGroupId, true);

		Assert.assertEquals(1, ordersCountByUser);

		commerceOrders = _getUserOrders(commerceChannelGroupId, true);

		Assert.assertEquals(
			commerceOrders.toString(), 1, commerceOrders.size());

		Assert.assertEquals(
			"The Sales Agent should only see the first order", commerceOrder,
			commerceOrders.get(0));

		// Remove the Organization association from the first account

		_accountEntryOrganizationRelLocalService.
			deleteAccountEntryOrganizationRel(accountEntryOrganizationRel);

		ordersCountByUser = _getUserOrdersCount(commerceChannelGroupId, true);

		Assert.assertEquals(0, ordersCountByUser);

		commerceOrders = _getUserOrders(commerceChannelGroupId, true);

		Assert.assertEquals(
			commerceOrders.toString(), 0, commerceOrders.size());

		_commerceOrderLocalService.deleteCommerceOrder(commerceOrder);
		_commerceOrderLocalService.deleteCommerceOrder(secondCommerceOrder);
		_commerceAddressLocalService.deleteCommerceAddress(commerceAddress);
		_commerceAddressLocalService.deleteCommerceAddress(
			secondCommerceAddress);
		_accountEntryLocalService.deleteAccountEntry(accountEntry);
		_accountEntryLocalService.deleteAccountEntry(secondAccountEntry);
		_organizationLocalService.deleteUserOrganization(
			_user.getUserId(), organization);
		_organizationLocalService.deleteUserOrganization(
			_user.getUserId(), secondOrganization);
		_organizationLocalService.deleteOrganization(organization);
		_organizationLocalService.deleteOrganization(secondOrganization);
	}

	@Test
	public void testGetPendingCommerceOrder() throws Exception {
		frutillaRule.scenario(
			"Try to get a pending order based on the userId, and directly " +
				"based on the commerceAccountId"
		).given(
			"A B2B Site"
		).and(
			"A Group"
		).and(
			"A User"
		).when(
			"I try to get that order by the current user's id or the accountId"
		).then(
			"I should be able to get it both ways"
		);

		AccountEntry accountEntry =
			CommerceAccountTestUtil.addBusinessAccountEntry(
				_user.getUserId(), "Test Business Account", null, null,
				new long[] {_user.getUserId()}, null, _serviceContext);

		long commerceChannelGroupId = _commerceChannel.getGroupId();

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.addCommerceOrder(
				_user.getUserId(), commerceChannelGroupId,
				accountEntry.getAccountEntryId(), _commerceCurrency.getCode(),
				0);

		int ordersCountByAccountId =
			_commerceOrderService.getPendingCommerceOrdersCount(
				commerceChannelGroupId, accountEntry.getAccountEntryId(),
				StringPool.BLANK);

		Assert.assertEquals(1, ordersCountByAccountId);

		List<CommerceOrder> commerceOrders =
			_commerceOrderService.getPendingCommerceOrders(
				commerceChannelGroupId, accountEntry.getAccountEntryId(),
				StringPool.BLANK, 0, 1);

		CommerceOrder actualCommerceOrder = commerceOrders.get(0);

		Assert.assertEquals(commerceOrder, actualCommerceOrder);

		long ordersCountByUser = _getUserOrdersCount(
			commerceChannelGroupId, false);

		Assert.assertEquals(1, ordersCountByUser);

		commerceOrders = _getUserOrders(commerceChannelGroupId, false);

		actualCommerceOrder = commerceOrders.get(0);

		Assert.assertEquals(commerceOrder, actualCommerceOrder);

		_commerceOrderLocalService.deleteCommerceOrders(commerceChannelGroupId);
		_accountEntryLocalService.deleteAccountEntry(accountEntry);
	}

	@Test
	public void testGetPendingCommerceOrderByPartialOrderId() throws Exception {
		frutillaRule.scenario(
			"Try to get a pending order based on the orderId"
		).given(
			"A B2B Site"
		).and(
			"A Group"
		).and(
			"A User"
		).when(
			"I try to get that order by a partial orderId"
		).then(
			"I should be able to get the order"
		);

		AccountEntry accountEntry =
			CommerceAccountTestUtil.addBusinessAccountEntry(
				_user.getUserId(), "Test Business Account", null, null,
				new long[] {_user.getUserId()}, null, _serviceContext);

		long commerceChannelGroupId = _commerceChannel.getGroupId();

		CommerceOrder commerceOrder1 =
			_commerceOrderLocalService.addCommerceOrder(
				_user.getUserId(), commerceChannelGroupId,
				accountEntry.getAccountEntryId(), _commerceCurrency.getCode(),
				0);

		String commerceOrderId = String.valueOf(
			commerceOrder1.getCommerceOrderId());

		String partialCommerceOrderId = commerceOrderId.substring(
			0, commerceOrderId.length() - 1);

		int ordersCountByAccountId =
			_commerceOrderService.getPendingCommerceOrdersCount(
				commerceChannelGroupId, accountEntry.getAccountEntryId(),
				partialCommerceOrderId);

		Assert.assertEquals(1, ordersCountByAccountId);

		List<CommerceOrder> commerceOrders =
			_commerceOrderService.getPendingCommerceOrders(
				commerceChannelGroupId, accountEntry.getAccountEntryId(),
				partialCommerceOrderId, 0, 1);

		CommerceOrder actualCommerceOrder = commerceOrders.get(0);

		Assert.assertEquals(commerceOrder1, actualCommerceOrder);

		long ordersCountByUser = _getUserOrdersCount(
			commerceChannelGroupId, false);

		Assert.assertEquals(1, ordersCountByUser);

		commerceOrders = _getUserOrders(commerceChannelGroupId, false);

		Assert.assertEquals(commerceOrder1, commerceOrders.get(0));

		_commerceOrderLocalService.deleteCommerceOrders(commerceChannelGroupId);
		_accountEntryLocalService.deleteAccountEntry(accountEntry);
	}

	@Test
	public void testGetPendingCommerceOrdersExceedingMaxAllowedBooleanClauses()
		throws Exception {

		List<AccountEntry> accountEntries = new ArrayList<>();

		long accountEntryIdsCount = _MAX_CLAUSES_COUNT + 1;

		for (int i = 0; i < accountEntryIdsCount; i++) {
			AccountEntry accountEntry =
				CommerceAccountTestUtil.addBusinessAccountEntry(
					_user.getUserId(), "Test Business Account" + i, null, null,
					new long[] {_user.getUserId()}, null, _serviceContext);

			accountEntries.add(accountEntry);
		}

		long commerceChannelGroupId = _commerceChannel.getGroupId();

		AccountEntry userAccountEntry = accountEntries.get(0);

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.addCommerceOrder(
				_user.getUserId(), commerceChannelGroupId,
				userAccountEntry.getAccountEntryId(),
				_commerceCurrency.getCode(), 0);

		List<CommerceOrder> commerceOrders = _getUserOrders(
			commerceChannelGroupId, false);

		CommerceOrder actualCommerceOrder = commerceOrders.get(0);

		Assert.assertEquals(commerceOrder, actualCommerceOrder);

		_commerceOrderLocalService.deleteCommerceOrders(commerceChannelGroupId);

		for (AccountEntry accountEntry : accountEntries) {
			_accountEntryLocalService.deleteAccountEntry(accountEntry);
		}
	}

	@Test
	public void testGetPlacedCommerceOrder() throws Exception {
		frutillaRule.scenario(
			"Try to get a placed order based on the userId, and directly " +
				"based on the commerceAccountId"
		).given(
			"A B2B Site"
		).and(
			"A Group"
		).and(
			"A User"
		).when(
			"I try to get that order by the current user's id or the accountId"
		).then(
			"I should be able to get it both ways"
		);

		AccountEntry accountEntry =
			CommerceAccountTestUtil.addBusinessAccountEntry(
				_user.getUserId(), "Test Business Account", null, null,
				new long[] {_user.getUserId()}, null, _serviceContext);

		long commerceChannelGroupId = _commerceChannel.getGroupId();

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.addCommerceOrder(
				_user.getUserId(), commerceChannelGroupId,
				accountEntry.getAccountEntryId(), _commerceCurrency.getCode(),
				0);

		CommerceAddress commerceAddress = _addAddressToAccount(
			accountEntry.getAccountEntryId());

		commerceOrder.setBillingAddressId(
			commerceAddress.getCommerceAddressId());
		commerceOrder.setShippingAddressId(
			commerceAddress.getCommerceAddressId());

		commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
			commerceOrder);

		commerceOrder = _commerceOrderEngine.checkoutCommerceOrder(
			commerceOrder, _user.getUserId());

		int ordersCountByAccountId =
			_commerceOrderService.getPlacedCommerceOrdersCount(
				commerceChannelGroupId, accountEntry.getAccountEntryId(),
				StringPool.BLANK);

		Assert.assertEquals(1, ordersCountByAccountId);

		List<CommerceOrder> commerceOrders =
			_commerceOrderService.getPlacedCommerceOrders(
				commerceChannelGroupId, accountEntry.getAccountEntryId(),
				StringPool.BLANK, 0, 1);

		CommerceOrder actualCommerceOrder = commerceOrders.get(0);

		Assert.assertEquals(commerceOrder, actualCommerceOrder);

		long ordersCountByUser = _getUserOrdersCount(
			commerceChannelGroupId, true);

		Assert.assertEquals(1, ordersCountByUser);

		commerceOrders = _getUserOrders(commerceChannelGroupId, true);

		actualCommerceOrder = commerceOrders.get(0);

		Assert.assertEquals(commerceOrder, actualCommerceOrder);

		_commerceOrderLocalService.deleteCommerceOrders(commerceChannelGroupId);
		_accountEntryLocalService.deleteAccountEntry(accountEntry);
	}

	@Test
	public void testSkipValidateAccountLimit() throws Exception {
		Settings settings = FallbackKeysSettingsUtil.getSettings(
			new GroupServiceSettingsLocator(
				_commerceChannel.getGroupId(),
				CommerceConstants.SERVICE_NAME_COMMERCE_ORDER_FIELDS));

		ModifiableSettings modifiableSettings =
			settings.getModifiableSettings();

		modifiableSettings.setValue("accountCartMaxAllowed", "1");

		modifiableSettings.store();

		AccountEntry accountEntry =
			CommerceAccountTestUtil.addBusinessAccountEntry(
				_user.getUserId(), "Test Business Account", null, null,
				new long[] {_user.getUserId()}, null, _serviceContext);

		long commerceChannelGroupId = _commerceChannel.getGroupId();

		_commerceOrderLocalService.addCommerceOrder(
			_user.getUserId(), commerceChannelGroupId,
			accountEntry.getAccountEntryId(), _commerceCurrency.getCode(), 0);

		try {
			_commerceOrderLocalService.addCommerceOrder(
				_user.getUserId(), commerceChannelGroupId,
				accountEntry.getAccountEntryId(), _commerceCurrency.getCode(),
				0);
		}
		catch (CommerceOrderAccountLimitException
					commerceOrderAccountLimitException) {

			Assert.assertNotNull(commerceOrderAccountLimitException);
		}

		Assert.assertEquals(
			1,
			_commerceOrderService.getPendingCommerceOrdersCount(
				commerceChannelGroupId, accountEntry.getAccountEntryId(),
				StringPool.BLANK));

		boolean skipValidateAccountLimit =
			CommerceOrderThreadLocal.isSkipValidateAccountLimit();

		try {
			CommerceOrderThreadLocal.setSkipValidateAccountLimit(true);

			_commerceOrderLocalService.addCommerceOrder(
				_user.getUserId(), commerceChannelGroupId,
				accountEntry.getAccountEntryId(), _commerceCurrency.getCode(),
				0);

			Assert.assertEquals(
				2,
				_commerceOrderService.getPendingCommerceOrdersCount(
					commerceChannelGroupId, accountEntry.getAccountEntryId(),
					StringPool.BLANK));

			_accountEntries.add(accountEntry);
		}
		finally {
			CommerceOrderThreadLocal.setSkipValidateAccountLimit(
				skipValidateAccountLimit);
		}
	}

	@Test
	public void testValidateAccountOrdersLimit() throws Exception {
		frutillaRule.scenario(
			"Try to add 3 orders with account cart number limit set to 3"
		).given(
			"A B2B Site"
		).and(
			"A Group"
		).and(
			"A User"
		).when(
			"I try to get pending orders by commerceChannelGroupId and the " +
				"accountId"
		).then(
			"I should have only 2 order"
		);

		Settings settings = FallbackKeysSettingsUtil.getSettings(
			new GroupServiceSettingsLocator(
				_commerceChannel.getGroupId(),
				CommerceConstants.SERVICE_NAME_COMMERCE_ORDER_FIELDS));

		ModifiableSettings modifiableSettings =
			settings.getModifiableSettings();

		modifiableSettings.setValue("accountCartMaxAllowed", "2");

		modifiableSettings.store();

		AccountEntry accountEntry =
			CommerceAccountTestUtil.addBusinessAccountEntry(
				_user.getUserId(), "Test Business Account", null, null,
				new long[] {_user.getUserId()}, null, _serviceContext);

		long commerceChannelGroupId = _commerceChannel.getGroupId();

		_commerceOrderLocalService.addCommerceOrder(
			_user.getUserId(), commerceChannelGroupId,
			accountEntry.getAccountEntryId(), _commerceCurrency.getCode(), 0);

		_commerceOrderLocalService.addCommerceOrder(
			_user.getUserId(), commerceChannelGroupId,
			accountEntry.getAccountEntryId(), _commerceCurrency.getCode(), 0);

		try {
			_commerceOrderLocalService.addCommerceOrder(
				_user.getUserId(), commerceChannelGroupId,
				accountEntry.getAccountEntryId(), _commerceCurrency.getCode(),
				0);
		}
		catch (CommerceOrderAccountLimitException
					commerceOrderAccountLimitException) {

			Assert.assertNotNull(commerceOrderAccountLimitException);
		}

		Assert.assertEquals(
			2,
			_commerceOrderService.getPendingCommerceOrdersCount(
				commerceChannelGroupId, accountEntry.getAccountEntryId(),
				StringPool.BLANK));

		_accountEntries.add(accountEntry);
	}

	@Rule
	public FrutillaRule frutillaRule = new FrutillaRule();

	private CommerceAddress _addAddressToAccount(long commerceAccountId)
		throws Exception {

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

		return _commerceAddressLocalService.addCommerceAddress(
			StringPool.BLANK, AccountEntry.class.getName(), commerceAccountId,
			_country.getCountryId(), _region.getRegionId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), StringPool.BLANK,
			CommerceAddressConstants.ADDRESS_TYPE_BILLING_AND_SHIPPING,
			String.valueOf(30133), _serviceContext);
	}

	private Role _addSalesAgentRole() throws Exception {
		Role role = _roleLocalService.addRole(
			RandomTestUtil.randomString(), _user.getUserId(), null, 0,
			"Sales Agent",
			HashMapBuilder.put(
				_serviceContext.getLocale(), "Sales Agent"
			).build(),
			null, 1, null, _serviceContext);

		_resourcePermissionLocalService.addResourcePermission(
			_serviceContext.getCompanyId(), Organization.class.getName(), 1,
			String.valueOf(role.getCompanyId()), role.getRoleId(),
			"MANAGE_AVAILABLE_ACCOUNTS");

		_resourcePermissionLocalService.addResourcePermission(
			_serviceContext.getCompanyId(),
			"com.liferay.account.model.AccountEntry", 1,
			String.valueOf(role.getCompanyId()), role.getRoleId(),
			"MANAGE_ORGANIZATIONS");

		return role;
	}

	private List<CommerceOrder> _getUserOrders(long groupId, boolean negate)
		throws Exception {

		// This simulates using
		// commerceOrderService.getUser(Pending||Placed)CommerceOrders()
		// If we used that method here it would pull accounts for the omni-admin

		long[] commerceAccountIds =
			_commerceAccountHelper.getUserCommerceAccountIds(
				_user.getUserId(), groupId);

		return _commerceOrderLocalService.getCommerceOrders(
			_group.getCompanyId(), groupId, commerceAccountIds,
			StringPool.BLANK,
			new int[] {CommerceOrderConstants.ORDER_STATUS_OPEN}, negate,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	private long _getUserOrdersCount(long groupId, boolean negate)
		throws Exception {

		// This simulates using
		// commerceOrderService.getUser(Pending||Placed)CommerceOrdersCount()
		// If we used that method here it would pull accounts for the omni-admin

		long[] commerceAccountIds =
			_commerceAccountHelper.getUserCommerceAccountIds(
				_user.getUserId(), groupId);

		return _commerceOrderLocalService.getCommerceOrdersCount(
			_group.getCompanyId(), groupId, commerceAccountIds,
			StringPool.BLANK,
			new int[] {CommerceOrderConstants.ORDER_STATUS_OPEN}, negate);
	}

	private static final int _MAX_CLAUSES_COUNT = 1024;

	@Inject
	private static ServiceComponentRuntime _serviceComponentRuntime;

	private final List<AccountEntry> _accountEntries = new ArrayList<>();

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private AccountEntryOrganizationRelLocalService
		_accountEntryOrganizationRelLocalService;

	@Inject
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	@Inject
	private CommerceAccountHelper _commerceAccountHelper;

	@Inject
	private CommerceAddressLocalService _commerceAddressLocalService;

	private CommerceChannel _commerceChannel;

	@Inject
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Inject
	private CommerceContextFactory _commerceContextFactory;

	private CommerceCurrency _commerceCurrency;

	@Inject
	private CommerceOrderEngine _commerceOrderEngine;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Inject
	private CommerceOrderService _commerceOrderService;

	private Country _country;

	@Inject
	private CountryLocalService _countryLocalService;

	private Group _group;

	@Inject
	private OrganizationLocalService _organizationLocalService;

	private Region _region;

	@Inject
	private RegionLocalService _regionLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	private ServiceContext _serviceContext;
	private User _user;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}