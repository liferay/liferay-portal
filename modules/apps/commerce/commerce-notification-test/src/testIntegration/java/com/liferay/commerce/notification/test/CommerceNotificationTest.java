/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.notification.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.constants.AccountRoleConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.notification.CommerceNotificationSender;
import com.liferay.commerce.notification.model.CommerceNotificationQueueEntry;
import com.liferay.commerce.notification.model.CommerceNotificationTemplate;
import com.liferay.commerce.notification.service.CommerceNotificationQueueEntryLocalService;
import com.liferay.commerce.notification.test.util.CommerceNotificationTestUtil;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Luca Pellizzon
 */
@RunWith(Arquillian.class)
public class CommerceNotificationTest {

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

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			_group.getCompanyId());

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getCompanyId(), _group.getGroupId(), _user.getUserId());

		_commerceChannel = _commerceChannelLocalService.addCommerceChannel(
			StringPool.BLANK, AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
			_group.getGroupId(),
			_group.getName(_serviceContext.getLanguageId()) + " Portal",
			CommerceChannelConstants.CHANNEL_TYPE_SITE, null, StringPool.BLANK,
			_serviceContext);
	}

	@After
	public void tearDown() throws PortalException {
		if (_createdAdminRole) {
			_roleLocalService.deleteRole(_accountAdminRole.getRoleId());
		}

		if (_createdOrderManagerRole) {
			_roleLocalService.deleteRole(_orderManagerRole.getRoleId());
		}

		_commerceNotificationQueueEntryLocalService.
			deleteCommerceNotificationQueueEntries(_group.getGroupId());
	}

	@Test
	public void testAccountAdministratorRecipient() throws Exception {
		_setUpAccountAdministrator();

		_commerceNotificationTemplate =
			CommerceNotificationTestUtil.addNotificationTemplate(
				"[%ACCOUNT_ROLE_ADMINISTRATOR%]",
				CommerceOrderConstants.ORDER_NOTIFICATION_PLACED,
				_serviceContext);

		_commerceOrder = CommerceTestUtil.addB2BCommerceOrder(
			_group.getGroupId(), _user.getUserId(),
			_accountEntry.getAccountEntryId(),
			_commerceCurrency.getCommerceCurrencyId());

		_commerceNotificationSender.sendNotifications(
			_group.getGroupId(), _user.getUserId(),
			CommerceOrderConstants.ORDER_NOTIFICATION_PLACED, _commerceOrder);

		List<CommerceNotificationQueueEntry> commerceNotificationQueueEntries =
			CommerceNotificationTestUtil.getCommerceNotificationQueueEntries(
				_group.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				null);

		Assert.assertEquals(
			commerceNotificationQueueEntries.toString(), 1,
			commerceNotificationQueueEntries.size());

		CommerceNotificationQueueEntry commerceNotificationQueueEntry =
			commerceNotificationQueueEntries.get(0);

		Assert.assertEquals(
			commerceNotificationQueueEntry.getTo(),
			_accountAdmin.getEmailAddress());
	}

	@Test
	public void testEmailAddressRecipient() throws Exception {
		_user.setEmailAddress("mail@mail.com");

		_user = _userLocalService.updateUser(_user);

		_commerceNotificationTemplate =
			CommerceNotificationTestUtil.addNotificationTemplate(
				_user.getEmailAddress(),
				CommerceOrderConstants.ORDER_NOTIFICATION_PLACED,
				_serviceContext);

		_commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency.getCommerceCurrencyId());

		_commerceNotificationSender.sendNotifications(
			_group.getGroupId(), _user.getUserId(),
			CommerceOrderConstants.ORDER_NOTIFICATION_PLACED, _commerceOrder);

		Assert.assertEquals(
			1,
			CommerceNotificationTestUtil.
				getCommerceNotificationQueueEntriesCount(_group.getGroupId()));
	}

	@Test
	public void testOrderCreatorBccField() throws Exception {
		_commerceNotificationTemplate =
			CommerceNotificationTestUtil.addNotificationTemplate(
				"[%ORDER_CREATOR%]", StringPool.BLANK, "[%ORDER_CREATOR%]",
				CommerceOrderConstants.ORDER_NOTIFICATION_PLACED,
				_serviceContext);

		_commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency.getCommerceCurrencyId());

		_commerceNotificationSender.sendNotifications(
			_group.getGroupId(), _user.getUserId(),
			CommerceOrderConstants.ORDER_NOTIFICATION_PLACED, _commerceOrder);

		List<CommerceNotificationQueueEntry> commerceNotificationQueueEntries =
			CommerceNotificationTestUtil.getCommerceNotificationQueueEntries(
				_group.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				null);

		Assert.assertEquals(
			commerceNotificationQueueEntries.toString(), 1,
			commerceNotificationQueueEntries.size());

		CommerceNotificationQueueEntry commerceNotificationQueueEntry =
			commerceNotificationQueueEntries.get(0);

		User user = _userLocalService.getUser(_commerceOrder.getUserId());

		Assert.assertEquals(
			commerceNotificationQueueEntry.getCc(), StringPool.BLANK);
		Assert.assertEquals(
			commerceNotificationQueueEntry.getBcc(), user.getEmailAddress());
	}

	@Test
	public void testOrderCreatorCcField() throws Exception {
		_commerceNotificationTemplate =
			CommerceNotificationTestUtil.addNotificationTemplate(
				"[%ORDER_CREATOR%]", "[%ORDER_CREATOR%]", StringPool.BLANK,
				CommerceOrderConstants.ORDER_NOTIFICATION_PLACED,
				_serviceContext);

		_commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency.getCommerceCurrencyId());

		_commerceNotificationSender.sendNotifications(
			_group.getGroupId(), _user.getUserId(),
			CommerceOrderConstants.ORDER_NOTIFICATION_PLACED, _commerceOrder);

		List<CommerceNotificationQueueEntry> commerceNotificationQueueEntries =
			CommerceNotificationTestUtil.getCommerceNotificationQueueEntries(
				_group.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				null);

		Assert.assertEquals(
			commerceNotificationQueueEntries.toString(), 1,
			commerceNotificationQueueEntries.size());

		CommerceNotificationQueueEntry commerceNotificationQueueEntry =
			commerceNotificationQueueEntries.get(0);

		User user = _userLocalService.getUser(_commerceOrder.getUserId());

		Assert.assertEquals(
			commerceNotificationQueueEntry.getCc(), user.getEmailAddress());

		Assert.assertEquals(
			commerceNotificationQueueEntry.getBcc(), StringPool.BLANK);
	}

	@Test
	public void testOrderCreatorCcFieldAndBccField() throws Exception {
		_commerceNotificationTemplate =
			CommerceNotificationTestUtil.addNotificationTemplate(
				"[%ORDER_CREATOR%]", "[%ORDER_CREATOR%]", "[%ORDER_CREATOR%]",
				CommerceOrderConstants.ORDER_NOTIFICATION_PLACED,
				_serviceContext);

		_commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency.getCommerceCurrencyId());

		_commerceNotificationSender.sendNotifications(
			_group.getGroupId(), _user.getUserId(),
			CommerceOrderConstants.ORDER_NOTIFICATION_PLACED, _commerceOrder);

		List<CommerceNotificationQueueEntry> commerceNotificationQueueEntries =
			CommerceNotificationTestUtil.getCommerceNotificationQueueEntries(
				_group.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				null);

		Assert.assertEquals(
			commerceNotificationQueueEntries.toString(), 1,
			commerceNotificationQueueEntries.size());

		CommerceNotificationQueueEntry commerceNotificationQueueEntry =
			commerceNotificationQueueEntries.get(0);

		User user = _userLocalService.getUser(_commerceOrder.getUserId());

		Assert.assertEquals(
			commerceNotificationQueueEntry.getCc(), user.getEmailAddress());
		Assert.assertEquals(
			commerceNotificationQueueEntry.getBcc(), user.getEmailAddress());
	}

	@Test
	public void testOrderCreatorCcFieldAndBccFieldWithFixedValues()
		throws Exception {

		String fixedValue = "automatic1@liferay.com,automatic2@liferay.com";

		_commerceNotificationTemplate =
			CommerceNotificationTestUtil.addNotificationTemplate(
				"[%ORDER_CREATOR%]", fixedValue, fixedValue,
				CommerceOrderConstants.ORDER_NOTIFICATION_PLACED,
				_serviceContext);

		_commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency.getCommerceCurrencyId());

		_commerceNotificationSender.sendNotifications(
			_group.getGroupId(), _user.getUserId(),
			CommerceOrderConstants.ORDER_NOTIFICATION_PLACED, _commerceOrder);

		List<CommerceNotificationQueueEntry> commerceNotificationQueueEntries =
			CommerceNotificationTestUtil.getCommerceNotificationQueueEntries(
				_group.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				null);

		Assert.assertEquals(
			commerceNotificationQueueEntries.toString(), 1,
			commerceNotificationQueueEntries.size());

		CommerceNotificationQueueEntry commerceNotificationQueueEntry =
			commerceNotificationQueueEntries.get(0);

		Assert.assertEquals(commerceNotificationQueueEntry.getCc(), fixedValue);
		Assert.assertEquals(
			commerceNotificationQueueEntry.getBcc(), fixedValue);
	}

	@Test
	public void testOrderCreatorCcFieldAndBccFieldWithInvalidValues()
		throws Exception {

		_commerceNotificationTemplate =
			CommerceNotificationTestUtil.addNotificationTemplate(
				"[%ORDER_CREATOR%]", "test", "test",
				CommerceOrderConstants.ORDER_NOTIFICATION_PLACED,
				_serviceContext);

		_commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency.getCommerceCurrencyId());

		_commerceNotificationSender.sendNotifications(
			_group.getGroupId(), _user.getUserId(),
			CommerceOrderConstants.ORDER_NOTIFICATION_PLACED, _commerceOrder);

		List<CommerceNotificationQueueEntry> commerceNotificationQueueEntries =
			CommerceNotificationTestUtil.getCommerceNotificationQueueEntries(
				_group.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				null);

		Assert.assertEquals(
			commerceNotificationQueueEntries.toString(), 1,
			commerceNotificationQueueEntries.size());

		CommerceNotificationQueueEntry commerceNotificationQueueEntry =
			commerceNotificationQueueEntries.get(0);

		Assert.assertEquals(
			commerceNotificationQueueEntry.getCc(), StringPool.BLANK);
		Assert.assertEquals(
			commerceNotificationQueueEntry.getBcc(), StringPool.BLANK);
	}

	@Test
	public void testOrderCreatorRecipient() throws Exception {
		_commerceNotificationTemplate =
			CommerceNotificationTestUtil.addNotificationTemplate(
				"[%ORDER_CREATOR%]",
				CommerceOrderConstants.ORDER_NOTIFICATION_PLACED,
				_serviceContext);

		_commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency.getCommerceCurrencyId());

		_commerceNotificationSender.sendNotifications(
			_group.getGroupId(), _user.getUserId(),
			CommerceOrderConstants.ORDER_NOTIFICATION_PLACED, _commerceOrder);

		List<CommerceNotificationQueueEntry> commerceNotificationQueueEntries =
			CommerceNotificationTestUtil.getCommerceNotificationQueueEntries(
				_group.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				null);

		Assert.assertEquals(
			commerceNotificationQueueEntries.toString(), 1,
			commerceNotificationQueueEntries.size());

		CommerceNotificationQueueEntry commerceNotificationQueueEntry =
			commerceNotificationQueueEntries.get(0);

		User user = _userLocalService.getUser(_commerceOrder.getUserId());

		Assert.assertEquals(
			commerceNotificationQueueEntry.getTo(), user.getEmailAddress());
	}

	@Test
	public void testOrderCreatorWithMixedValues() throws Exception {
		String mixedValue = "automatic1@liferay.com,111,[%ORDER_CREATOR%]";

		_commerceNotificationTemplate =
			CommerceNotificationTestUtil.addNotificationTemplate(
				mixedValue, mixedValue, mixedValue,
				CommerceOrderConstants.ORDER_NOTIFICATION_PLACED,
				_serviceContext);

		_commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency.getCommerceCurrencyId());

		_commerceNotificationSender.sendNotifications(
			_group.getGroupId(), _user.getUserId(),
			CommerceOrderConstants.ORDER_NOTIFICATION_PLACED, _commerceOrder);

		List<CommerceNotificationQueueEntry> commerceNotificationQueueEntries =
			CommerceNotificationTestUtil.getCommerceNotificationQueueEntries(
				_group.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				null);

		Assert.assertEquals(
			commerceNotificationQueueEntries.toString(), 2,
			commerceNotificationQueueEntries.size());

		User user = _userLocalService.getUser(_commerceOrder.getUserId());

		for (CommerceNotificationQueueEntry commerceNotificationQueueEntry :
				commerceNotificationQueueEntries) {

			Assert.assertTrue(
				StringUtil.equalsIgnoreCase(
					commerceNotificationQueueEntry.getTo(),
					user.getEmailAddress()) ||
				StringUtil.equalsIgnoreCase(
					commerceNotificationQueueEntry.getTo(),
					"automatic1@liferay.com"));
			Assert.assertTrue(
				StringUtil.contains(
					commerceNotificationQueueEntry.getCc(),
					user.getEmailAddress()) &&
				StringUtil.contains(
					commerceNotificationQueueEntry.getCc(),
					"automatic1@liferay.com") &&
				!StringUtil.contains(
					commerceNotificationQueueEntry.getCc(), "111"));
			Assert.assertTrue(
				StringUtil.contains(
					commerceNotificationQueueEntry.getBcc(),
					user.getEmailAddress()) &&
				StringUtil.contains(
					commerceNotificationQueueEntry.getBcc(),
					"automatic1@liferay.com") &&
				!StringUtil.contains(
					commerceNotificationQueueEntry.getBcc(), "111"));
		}
	}

	@Test
	public void testOrderManagerRecipient() throws Exception {
		_setUpAccountAdministrator();

		_setUpOrderManager();

		_commerceNotificationTemplate =
			CommerceNotificationTestUtil.addNotificationTemplate(
				"[%ACCOUNT_ROLE_ORDER_MANAGER%]",
				CommerceOrderConstants.ORDER_NOTIFICATION_PLACED,
				_serviceContext);

		_commerceOrder = CommerceTestUtil.addB2BCommerceOrder(
			_group.getGroupId(), _user.getUserId(),
			_accountEntry.getAccountEntryId(),
			_commerceCurrency.getCommerceCurrencyId());

		_commerceNotificationSender.sendNotifications(
			_group.getGroupId(), _user.getUserId(),
			CommerceOrderConstants.ORDER_NOTIFICATION_PLACED, _commerceOrder);

		List<CommerceNotificationQueueEntry> commerceNotificationQueueEntries =
			CommerceNotificationTestUtil.getCommerceNotificationQueueEntries(
				_group.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				null);

		Assert.assertEquals(
			commerceNotificationQueueEntries.toString(), 1,
			commerceNotificationQueueEntries.size());

		CommerceNotificationQueueEntry commerceNotificationQueueEntry =
			commerceNotificationQueueEntries.get(0);

		Assert.assertEquals(
			commerceNotificationQueueEntry.getTo(),
			_orderManager.getEmailAddress());
	}

	@Test
	public void testOrderPlacedNotification() throws Exception {
		_commerceNotificationTemplate =
			CommerceNotificationTestUtil.addNotificationTemplate(
				"[%ORDER_CREATOR%]",
				CommerceOrderConstants.ORDER_NOTIFICATION_PLACED,
				_serviceContext);

		_commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency.getCommerceCurrencyId());

		_commerceNotificationSender.sendNotifications(
			_group.getGroupId(), _user.getUserId(),
			CommerceOrderConstants.ORDER_NOTIFICATION_PLACED, _commerceOrder);

		Assert.assertEquals(
			1,
			CommerceNotificationTestUtil.
				getCommerceNotificationQueueEntriesCount(_group.getGroupId()));
	}

	@Test
	public void testUserGroupRecipient() throws Exception {
		_setUpAccountAdministrator();

		_setUpOrderManager();

		String groupName = _setUpUserGroup();

		_commerceNotificationTemplate =
			CommerceNotificationTestUtil.addNotificationTemplate(
				"[%USER_GROUP_" + groupName + "%]",
				CommerceOrderConstants.ORDER_NOTIFICATION_PLACED,
				_serviceContext);

		_commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency.getCommerceCurrencyId());

		_commerceNotificationSender.sendNotifications(
			_group.getGroupId(), _user.getUserId(),
			CommerceOrderConstants.ORDER_NOTIFICATION_PLACED, _commerceOrder);

		Assert.assertEquals(
			1,
			CommerceNotificationTestUtil.
				getCommerceNotificationQueueEntriesCount(_group.getGroupId()));
	}

	private void _setUpAccountAdministrator() throws Exception {
		_accountAdmin = UserTestUtil.addUser(
			_user.getCompanyId(), _user.getUserId(), "businessUser",
			_serviceContext.getLocale(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			new long[] {_serviceContext.getScopeGroupId()}, _serviceContext);

		_accountEntry = CommerceAccountTestUtil.addBusinessAccountEntry(
			_accountAdmin.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString() + "@liferay.com",
			RandomTestUtil.randomString(), new long[] {_user.getUserId()}, null,
			_serviceContext);

		_accountAdminRole = _roleLocalService.fetchRole(
			_group.getCompanyId(),
			AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_ADMINISTRATOR);

		if (_accountAdminRole == null) {
			_accountAdminRole = _roleLocalService.addRole(
				RandomTestUtil.randomString(), _serviceContext.getUserId(),
				null, 0,
				AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_ADMINISTRATOR,
				Collections.singletonMap(
					_serviceContext.getLocale(),
					AccountRoleConstants.
						REQUIRED_ROLE_NAME_ACCOUNT_ADMINISTRATOR),
				Collections.emptyMap(), RoleConstants.TYPE_SITE,
				StringPool.BLANK, _serviceContext);

			_createdAdminRole = true;
		}

		_userGroupRoleLocalService.deleteUserGroupRoles(
			_accountAdmin.getUserId(),
			new long[] {_accountEntry.getAccountEntryGroupId()});

		_userGroupRoleLocalService.addUserGroupRoles(
			_accountAdmin.getUserId(), _accountEntry.getAccountEntryGroupId(),
			new long[] {_accountAdminRole.getRoleId()});
	}

	private void _setUpOrderManager() throws Exception {
		_orderManager = UserTestUtil.addUser(
			_user.getCompanyId(), _user.getUserId(), "businessUser",
			_serviceContext.getLocale(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			new long[] {_serviceContext.getScopeGroupId()}, _serviceContext);

		_orderManagerRole = _roleLocalService.fetchRole(
			_group.getCompanyId(), "Order Manager");

		if (_orderManagerRole == null) {
			_orderManagerRole = _roleLocalService.addRole(
				RandomTestUtil.randomString(), _serviceContext.getUserId(),
				null, 0, "Order Manager",
				Collections.singletonMap(
					_serviceContext.getLocale(), "Order Manager"),
				Collections.emptyMap(), RoleConstants.TYPE_SITE,
				StringPool.BLANK, _serviceContext);

			_createdOrderManagerRole = true;
		}

		_userGroupRoleLocalService.deleteUserGroupRoles(
			_orderManager.getUserId(),
			new long[] {_accountEntry.getAccountEntryGroupId()});

		_userGroupRoleLocalService.addUserGroupRoles(
			_orderManager.getUserId(), _accountEntry.getAccountEntryGroupId(),
			new long[] {_orderManagerRole.getRoleId()});
	}

	private String _setUpUserGroup() throws Exception {
		UserGroup userGroup = _userGroupLocalService.addUserGroup(
			StringPool.BLANK, _user.getUserId(), _group.getCompanyId(),
			"Test User Group", RandomTestUtil.randomString(), _serviceContext);

		long[] userIds = new long[1];

		userIds[0] = _orderManager.getUserId();

		_userLocalService.addUserGroupUsers(
			userGroup.getUserGroupId(), userIds);

		return userGroup.getName();
	}

	@DeleteAfterTestRun
	private User _accountAdmin;

	private Role _accountAdminRole;
	private AccountEntry _accountEntry;

	@DeleteAfterTestRun
	private CommerceChannel _commerceChannel;

	@Inject
	private CommerceChannelLocalService _commerceChannelLocalService;

	@DeleteAfterTestRun
	private CommerceCurrency _commerceCurrency;

	@Inject
	private CommerceNotificationQueueEntryLocalService
		_commerceNotificationQueueEntryLocalService;

	@Inject
	private CommerceNotificationSender _commerceNotificationSender;

	@DeleteAfterTestRun
	private CommerceNotificationTemplate _commerceNotificationTemplate;

	@DeleteAfterTestRun
	private CommerceOrder _commerceOrder;

	private boolean _createdAdminRole;
	private boolean _createdOrderManagerRole;
	private Group _group;
	private User _orderManager;
	private Role _orderManagerRole;

	@Inject
	private RoleLocalService _roleLocalService;

	private ServiceContext _serviceContext;
	private User _user;

	@Inject
	private UserGroupLocalService _userGroupLocalService;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}