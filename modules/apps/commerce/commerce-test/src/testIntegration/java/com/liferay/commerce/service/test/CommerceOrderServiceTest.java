/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.constants.AccountRoleConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.constants.CommerceOrderActionKeys;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.constants.CommerceOrderPaymentConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Crescenzo Rega
 */
@RunWith(Arquillian.class)
public class CommerceOrderServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			TestPropsValues.getCompanyId());

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			TestPropsValues.getGroupId(), _commerceCurrency.getCode());

		_commerceOrder = _commerceOrderLocalService.addCommerceOrder(
			TestPropsValues.getUserId(), _commerceChannel.getGroupId(),
			AccountConstants.ACCOUNT_ENTRY_ID_GUEST,
			_commerceCurrency.getCode(), 0);

		_company = CompanyTestUtil.addCompany(true);
	}

	@Test
	public void testAddCommerceOrder() throws Exception {
		Role role = RoleTestUtil.addRole(
			RandomTestUtil.randomString(), RoleConstants.TYPE_REGULAR);
		User user1 = UserTestUtil.addUser();

		_roleLocalService.addUserRole(user1.getUserId(), role);

		AccountEntry businessAccountEntry =
			CommerceAccountTestUtil.addBusinessAccountEntry(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				null,
				ServiceContextTestUtil.getServiceContext(
					TestPropsValues.getGroupId()));

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user1, PermissionCheckerFactoryUtil.create(user1))) {

			_commerceOrderService.addCommerceOrder(
				_commerceChannel.getGroupId(),
				businessAccountEntry.getAccountEntryId(),
				_commerceCurrency.getCode(), 0);

			Assert.fail();
		}
		catch (Exception exception) {
			_assertMessage(
				CommerceOrderActionKeys.ADD_COMMERCE_ORDER,
				exception.getMessage(), user1.getUserId());
		}

		RoleTestUtil.addResourcePermission(
			role, CommerceOrderConstants.RESOURCE_NAME,
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()),
			CommerceOrderActionKeys.ADD_COMMERCE_ORDER);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user1, PermissionCheckerFactoryUtil.create(user1))) {

			_commerceOrderService.addCommerceOrder(
				_commerceChannel.getGroupId(),
				businessAccountEntry.getAccountEntryId(),
				_commerceCurrency.getCode(), 0);
		}

		User user2 = UserTestUtil.addUser();

		AccountEntry personAccountEntry =
			CommerceAccountTestUtil.getPersonAccountEntry(user2.getUserId());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user1, PermissionCheckerFactoryUtil.create(user1))) {

			_commerceOrderService.addCommerceOrder(
				_commerceChannel.getGroupId(),
				personAccountEntry.getAccountEntryId(),
				_commerceCurrency.getCode(), 0);

			Assert.fail();
		}
		catch (Exception exception) {
			_assertMessage(
				ActionKeys.UPDATE, exception.getMessage(), user1.getUserId());
		}

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user2, PermissionCheckerFactoryUtil.create(user2))) {

			_commerceOrderService.addCommerceOrder(
				_commerceChannel.getGroupId(),
				personAccountEntry.getAccountEntryId(),
				_commerceCurrency.getCode(), 0);
		}

		RoleTestUtil.addResourcePermission(
			role, CommerceOrderConstants.RESOURCE_NAME,
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()),
			CommerceOrderActionKeys.MANAGE_ALL_ACCOUNTS);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user1, PermissionCheckerFactoryUtil.create(user1))) {

			_commerceOrderService.addCommerceOrder(
				_commerceChannel.getGroupId(),
				personAccountEntry.getAccountEntryId(),
				_commerceCurrency.getCode(), 0);
		}

		int count =
			_commerceOrderLocalService.
				getCommerceOrdersCountByCommerceAccountId(
					businessAccountEntry.getAccountEntryId());

		User guestUser = _userLocalService.getGuestUser(
			TestPropsValues.getCompanyId());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				guestUser, PermissionCheckerFactoryUtil.create(guestUser))) {

			CommerceOrder commerceOrder =
				_commerceOrderService.addCommerceOrder(
					_commerceChannel.getGroupId(),
					businessAccountEntry.getAccountEntryId(),
					_commerceCurrency.getCode(), 0);

			Assert.assertEquals(
				AccountConstants.ACCOUNT_ENTRY_ID_GUEST,
				commerceOrder.getCommerceAccountId());
		}

		Assert.assertEquals(
			count,
			_commerceOrderLocalService.
				getCommerceOrdersCountByCommerceAccountId(
					businessAccountEntry.getAccountEntryId()));
	}

	@Test
	public void testAddOrUpdateCommerceOrder() throws Exception {
		_testAddOrUpdateCommerceOrderWithAccountRole(
			AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_ADMINISTRATOR);
		_testAddOrUpdateCommerceOrderWithAccountRole(
			AccountRoleConstants.ROLE_NAME_ACCOUNT_BUYER);
		_testAddOrUpdateCommerceOrderWithGuestUser();
		_testAddOrUpdateCommerceOrderWithRegularRole();
		_testAddOrUpdateCommerceOrderWithSupplierRole();
	}

	@Test
	public void testDeleteCommerceOrder() throws Exception {
		CommerceOrder commerceOrder =
			_commerceOrderLocalService.addCommerceOrder(
				TestPropsValues.getUserId(), _commerceChannel.getGroupId(),
				AccountConstants.ACCOUNT_ENTRY_ID_GUEST,
				_commerceCurrency.getCode(), 0);

		User user = UserTestUtil.addCompanyAdminUser(_company);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			_commerceOrderService.deleteCommerceOrder(
				commerceOrder.getCommerceOrderId());

			Assert.fail();
		}
		catch (Exception exception) {
			_assertMessage(
				ActionKeys.DELETE, exception.getMessage(), user.getUserId());
		}

		_commerceOrderService.deleteCommerceOrder(
			commerceOrder.getCommerceOrderId());
	}

	@Test
	public void testGetCommerceOrder() throws Exception {
		User user = UserTestUtil.addCompanyAdminUser(_company);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			_commerceOrderService.getCommerceOrder(
				_commerceOrder.getCommerceOrderId());

			Assert.fail();
		}
		catch (Exception exception) {
			_assertMessage(
				ActionKeys.VIEW, exception.getMessage(), user.getUserId());
		}

		_commerceOrderService.getCommerceOrder(
			_commerceOrder.getCommerceOrderId());
	}

	@Test
	public void testUpdateCommerceOrder() throws Exception {
		_testUpdateCommerceOrderWithAccountBuyerRole();
		_testUpdateCommerceOrderWithRegularRole();
	}

	private CommerceOrder _addOrUpdateCommerceOrder(
			CommerceOrder commerceOrder, String externalReferenceCode,
			User user)
		throws Exception {

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			return _commerceOrderService.addOrUpdateCommerceOrder(
				externalReferenceCode, commerceOrder.getGroupId(), 0,
				commerceOrder.getCommerceAccountId(),
				_commerceCurrency.getCode(), 0, 0, 0, StringPool.BLANK,
				StringPool.BLANK, StringPool.BLANK,
				CommerceOrderConstants.ORDER_STATUS_PENDING,
				CommerceOrderPaymentConstants.STATUS_COMPLETED,
				StringPool.BLANK,
				BigDecimal.valueOf(RandomTestUtil.randomDouble()),
				StringPool.BLANK,
				BigDecimal.valueOf(RandomTestUtil.randomDouble()),
				BigDecimal.valueOf(RandomTestUtil.randomDouble()),
				BigDecimal.valueOf(RandomTestUtil.randomDouble()),
				BigDecimal.valueOf(RandomTestUtil.randomDouble()),
				BigDecimal.ONE,
				BigDecimal.valueOf(RandomTestUtil.randomDouble()), null,
				ServiceContextTestUtil.getServiceContext(
					commerceOrder.getGroupId(), user.getUserId()));
		}
	}

	private void _assertAddOrUpdateCommerceOrder(
			CommerceOrder commerceOrder, String externalReferenceCode,
			User user)
		throws Exception {

		CommerceOrder addOrUpdateCommerceOrder = _addOrUpdateCommerceOrder(
			commerceOrder, externalReferenceCode, user);

		Assert.assertEquals(
			CommerceOrderConstants.ORDER_STATUS_PENDING,
			addOrUpdateCommerceOrder.getOrderStatus());
		Assert.assertEquals(
			CommerceOrderPaymentConstants.STATUS_COMPLETED,
			addOrUpdateCommerceOrder.getPaymentStatus());
		Assert.assertTrue(
			BigDecimalUtil.eq(
				BigDecimal.ONE, addOrUpdateCommerceOrder.getTotal()));
	}

	private void _assertAddOrUpdateCommerceOrderNotAllowed(
			String actionId, CommerceOrder commerceOrder,
			String externalReferenceCode, User user)
		throws Exception {

		int count =
			_commerceOrderLocalService.
				getCommerceOrdersCountByCommerceAccountId(
					commerceOrder.getCommerceAccountId());

		try {
			_addOrUpdateCommerceOrder(
				commerceOrder, externalReferenceCode, user);

			Assert.fail();
		}
		catch (Exception exception) {
			_assertMessage(actionId, exception.getMessage(), user.getUserId());
		}

		Assert.assertEquals(
			count,
			_commerceOrderLocalService.
				getCommerceOrdersCountByCommerceAccountId(
					commerceOrder.getCommerceAccountId()));
	}

	private void _assertMessage(String actionId, String message, long userId) {
		Assert.assertTrue(
			message.contains(
				StringBundler.concat(
					"User ", userId, " must have ", actionId,
					" permission for")));
	}

	private void _assertUpdateCommerceOrderNotAllowed(
			String actionId, CommerceOrder commerceOrder, BigDecimal subtotal,
			BigDecimal total, User user)
		throws Exception {

		try {
			_updateCommerceOrder(commerceOrder, subtotal, total, user);

			Assert.fail();
		}
		catch (Exception exception) {
			_assertMessage(actionId, exception.getMessage(), user.getUserId());
		}
	}

	private void _assertUpdateCommerceOrderNotAllowed(
			String actionId, CommerceOrder commerceOrder, User user)
		throws Exception {

		try {
			_updateCommerceOrder(commerceOrder, user);

			Assert.fail();
		}
		catch (Exception exception) {
			_assertMessage(actionId, exception.getMessage(), user.getUserId());
		}
	}

	private void _assertUpdateCommerceOrderWithPricesNotAllowed(
			String actionId, CommerceOrder commerceOrder, BigDecimal total,
			User user)
		throws Exception {

		try {
			_updateCommerceOrderWithPrices(commerceOrder, total, user);

			Assert.fail();
		}
		catch (Exception exception) {
			_assertMessage(actionId, exception.getMessage(), user.getUserId());
		}
	}

	private void _testAddOrUpdateCommerceOrderWithAccountRole(String roleName)
		throws Exception {

		User user = UserTestUtil.addUser();

		AccountEntry accountEntry =
			CommerceAccountTestUtil.addBusinessAccountEntry(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				null, StringPool.BLANK, new long[] {user.getUserId()}, null,
				ServiceContextTestUtil.getServiceContext(
					TestPropsValues.getGroupId()));

		Role role = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), roleName);

		_userGroupRoleLocalService.addUserGroupRole(
			user.getUserId(), accountEntry.getAccountEntryGroupId(),
			role.getRoleId());

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.addCommerceOrder(
				user.getUserId(), _commerceChannel.getGroupId(),
				accountEntry.getAccountEntryId(), _commerceCurrency.getCode(),
				0);

		_assertAddOrUpdateCommerceOrderNotAllowed(
			CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_PAYMENT_STATUSES,
			commerceOrder, commerceOrder.getExternalReferenceCode(), user);
		_assertAddOrUpdateCommerceOrderNotAllowed(
			CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_PAYMENT_STATUSES,
			commerceOrder, RandomTestUtil.randomString(), user);
	}

	private void _testAddOrUpdateCommerceOrderWithGuestUser() throws Exception {
		User user = _userLocalService.getGuestUser(
			TestPropsValues.getCompanyId());

		_assertAddOrUpdateCommerceOrderNotAllowed(
			CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_PAYMENT_STATUSES,
			_commerceOrder, _commerceOrder.getExternalReferenceCode(), user);
		_assertAddOrUpdateCommerceOrderNotAllowed(
			CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_PAYMENT_STATUSES,
			_commerceOrder, RandomTestUtil.randomString(), user);
	}

	private void _testAddOrUpdateCommerceOrderWithRegularRole()
		throws Exception {

		Role role = RoleTestUtil.addRole(
			RandomTestUtil.randomString(), RoleConstants.TYPE_REGULAR);
		User user = UserTestUtil.addUser();

		_roleLocalService.addUserRole(user.getUserId(), role);

		RoleTestUtil.addResourcePermission(
			role, CommerceOrderConstants.RESOURCE_NAME,
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()),
			CommerceOrderActionKeys.ADD_COMMERCE_ORDER);

		AccountEntry accountEntry =
			CommerceAccountTestUtil.addBusinessAccountEntry(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				null,
				ServiceContextTestUtil.getServiceContext(
					TestPropsValues.getGroupId()));

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.addCommerceOrder(
				user.getUserId(), _commerceChannel.getGroupId(),
				accountEntry.getAccountEntryId(), _commerceCurrency.getCode(),
				0);

		_assertAddOrUpdateCommerceOrderNotAllowed(
			CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_PAYMENT_STATUSES,
			commerceOrder, commerceOrder.getExternalReferenceCode(), user);
		_assertAddOrUpdateCommerceOrderNotAllowed(
			CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_PAYMENT_STATUSES,
			commerceOrder, RandomTestUtil.randomString(), user);

		RoleTestUtil.addResourcePermission(
			role, CommerceOrderConstants.RESOURCE_NAME,
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()),
			CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_PAYMENT_STATUSES);

		_assertAddOrUpdateCommerceOrderNotAllowed(
			CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_PRICES, commerceOrder,
			commerceOrder.getExternalReferenceCode(), user);
		_assertAddOrUpdateCommerceOrderNotAllowed(
			CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_PRICES, commerceOrder,
			RandomTestUtil.randomString(), user);

		RoleTestUtil.addResourcePermission(
			role, CommerceOrderConstants.RESOURCE_NAME,
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()),
			CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_PRICES);

		_assertAddOrUpdateCommerceOrder(
			commerceOrder, commerceOrder.getExternalReferenceCode(), user);
		_assertAddOrUpdateCommerceOrder(
			commerceOrder, RandomTestUtil.randomString(), user);
	}

	private void _testAddOrUpdateCommerceOrderWithSupplierRole()
		throws Exception {

		AccountEntry supplierAccountEntry =
			_accountEntryLocalService.addAccountEntry(
				StringPool.BLANK, TestPropsValues.getUserId(),
				AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				null, RandomTestUtil.randomString() + "@liferay.com", null,
				RandomTestUtil.randomString(),
				AccountConstants.ACCOUNT_ENTRY_TYPE_SUPPLIER,
				WorkflowConstants.STATUS_APPROVED,
				ServiceContextTestUtil.getServiceContext(
					TestPropsValues.getGroupId()));
		User user = UserTestUtil.addUser();

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			supplierAccountEntry.getAccountEntryId(), user.getUserId());

		Role accountSupplierRole = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(),
			AccountRoleConstants.ROLE_NAME_ACCOUNT_SUPPLIER);

		_userGroupRoleLocalService.addUserGroupRole(
			user.getUserId(), supplierAccountEntry.getAccountEntryGroupId(),
			accountSupplierRole.getRoleId());

		_roleLocalService.addUserRole(
			user.getUserId(),
			_roleLocalService.getRole(
				TestPropsValues.getCompanyId(),
				AccountRoleConstants.ROLE_NAME_SUPPLIER));

		CommerceChannel commerceChannel = CommerceTestUtil.addCommerceChannel(
			TestPropsValues.getGroupId(), _commerceCurrency.getCode());

		commerceChannel.setAccountEntryId(
			supplierAccountEntry.getAccountEntryId());

		commerceChannel = _commerceChannelLocalService.updateCommerceChannel(
			commerceChannel);

		AccountEntry accountEntry =
			CommerceAccountTestUtil.addBusinessAccountEntry(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				null,
				ServiceContextTestUtil.getServiceContext(
					TestPropsValues.getGroupId()));

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.addCommerceOrder(
				TestPropsValues.getUserId(), commerceChannel.getGroupId(),
				accountEntry.getAccountEntryId(), _commerceCurrency.getCode(),
				0);

		_assertAddOrUpdateCommerceOrder(
			commerceOrder, commerceOrder.getExternalReferenceCode(), user);
	}

	private void _testUpdateCommerceOrderWithAccountBuyerRole()
		throws Exception {

		User user = UserTestUtil.addUser();

		AccountEntry accountEntry =
			CommerceAccountTestUtil.addBusinessAccountEntry(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				null, StringPool.BLANK, new long[] {user.getUserId()}, null,
				ServiceContextTestUtil.getServiceContext(
					TestPropsValues.getGroupId()));

		Role role = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(),
			AccountRoleConstants.ROLE_NAME_ACCOUNT_BUYER);

		_userGroupRoleLocalService.addUserGroupRole(
			user.getUserId(), accountEntry.getAccountEntryGroupId(),
			role.getRoleId());

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.addCommerceOrder(
				user.getUserId(), _commerceChannel.getGroupId(),
				accountEntry.getAccountEntryId(), _commerceCurrency.getCode(),
				0);

		commerceOrder.setPaymentStatus(
			CommerceOrderPaymentConstants.STATUS_COMPLETED);

		_assertUpdateCommerceOrderNotAllowed(
			CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_PAYMENT_STATUSES,
			commerceOrder, user);

		long commerceOrderId = commerceOrder.getCommerceOrderId();

		commerceOrder = _commerceOrderLocalService.getCommerceOrder(
			commerceOrderId);

		commerceOrder.setTotal(
			BigDecimal.valueOf(RandomTestUtil.randomDouble()));

		_assertUpdateCommerceOrderNotAllowed(
			CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_PRICES, commerceOrder,
			user);

		commerceOrder = _commerceOrderLocalService.getCommerceOrder(
			commerceOrderId);

		_assertUpdateCommerceOrderNotAllowed(
			CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_PRICES, commerceOrder,
			commerceOrder.getSubtotal(),
			BigDecimal.valueOf(RandomTestUtil.randomDouble()), user);
		_assertUpdateCommerceOrderWithPricesNotAllowed(
			CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_PRICES, commerceOrder,
			BigDecimal.valueOf(RandomTestUtil.randomDouble()), user);

		commerceOrder = _commerceOrderLocalService.getCommerceOrder(
			commerceOrderId);

		commerceOrder.setOrderStatus(
			CommerceOrderConstants.ORDER_STATUS_PENDING);

		commerceOrder = _updateCommerceOrder(commerceOrder, user);

		Assert.assertEquals(
			CommerceOrderConstants.ORDER_STATUS_PENDING,
			commerceOrder.getOrderStatus());
	}

	private void _testUpdateCommerceOrderWithRegularRole() throws Exception {
		Role role = RoleTestUtil.addRole(
			RandomTestUtil.randomString(), RoleConstants.TYPE_REGULAR);
		User user = UserTestUtil.addUser();

		_roleLocalService.addUserRole(user.getUserId(), role);

		AccountEntry accountEntry =
			CommerceAccountTestUtil.addBusinessAccountEntry(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				null,
				ServiceContextTestUtil.getServiceContext(
					TestPropsValues.getGroupId()));

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.addCommerceOrder(
				user.getUserId(), _commerceChannel.getGroupId(),
				accountEntry.getAccountEntryId(), _commerceCurrency.getCode(),
				0);

		_updateCommerceOrder(
			commerceOrder, commerceOrder.getSubtotal(),
			commerceOrder.getTotal(), user);
		_updateCommerceOrderWithPrices(
			commerceOrder, commerceOrder.getTotal(), user);

		_assertUpdateCommerceOrderNotAllowed(
			CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_PRICES, commerceOrder,
			commerceOrder.getSubtotal(),
			BigDecimal.valueOf(RandomTestUtil.randomDouble()), user);
		_assertUpdateCommerceOrderWithPricesNotAllowed(
			CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_PRICES, commerceOrder,
			BigDecimal.valueOf(RandomTestUtil.randomDouble()), user);

		long commerceOrderId = commerceOrder.getCommerceOrderId();

		commerceOrder = _commerceOrderLocalService.getCommerceOrder(
			commerceOrderId);

		commerceOrder.setPaymentStatus(
			CommerceOrderPaymentConstants.STATUS_COMPLETED);

		_assertUpdateCommerceOrderNotAllowed(
			CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_PAYMENT_STATUSES,
			commerceOrder, user);

		RoleTestUtil.addResourcePermission(
			role, CommerceOrderConstants.RESOURCE_NAME,
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()),
			CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_PAYMENT_STATUSES);

		commerceOrder = _commerceOrderLocalService.getCommerceOrder(
			commerceOrderId);

		commerceOrder.setPaymentStatus(
			CommerceOrderPaymentConstants.STATUS_COMPLETED);

		commerceOrder = _updateCommerceOrder(commerceOrder, user);

		Assert.assertEquals(
			CommerceOrderPaymentConstants.STATUS_COMPLETED,
			commerceOrder.getPaymentStatus());

		commerceOrder = _commerceOrderLocalService.getCommerceOrder(
			commerceOrderId);

		_assertUpdateCommerceOrderNotAllowed(
			CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_PRICES, commerceOrder,
			commerceOrder.getSubtotal(),
			BigDecimal.valueOf(RandomTestUtil.randomDouble()), user);

		RoleTestUtil.addResourcePermission(
			role, CommerceOrderConstants.RESOURCE_NAME,
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()),
			CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_PRICES);

		commerceOrder = _commerceOrderLocalService.getCommerceOrder(
			commerceOrderId);

		_updateCommerceOrder(
			commerceOrder, commerceOrder.getSubtotal(),
			BigDecimal.valueOf(RandomTestUtil.randomDouble()), user);
		_updateCommerceOrderWithPrices(
			commerceOrder, BigDecimal.valueOf(RandomTestUtil.randomDouble()),
			user);

		commerceOrder = _commerceOrderLocalService.getCommerceOrder(
			commerceOrderId);

		commerceOrder.setTotal(BigDecimal.valueOf(5));

		commerceOrder = _updateCommerceOrder(commerceOrder, user);

		Assert.assertTrue(
			BigDecimalUtil.eq(BigDecimal.valueOf(5), commerceOrder.getTotal()));
	}

	private CommerceOrder _updateCommerceOrder(
			CommerceOrder commerceOrder, BigDecimal subtotal, BigDecimal total,
			User user)
		throws Exception {

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			return _commerceOrderService.updateCommerceOrder(
				commerceOrder.getExternalReferenceCode(),
				commerceOrder.getCommerceOrderId(),
				commerceOrder.getBillingAddressId(),
				commerceOrder.getCommerceShippingMethodId(),
				commerceOrder.getShippingAddressId(),
				commerceOrder.getAdvanceStatus(),
				commerceOrder.getCommercePaymentMethodKey(),
				commerceOrder.getName(), commerceOrder.getPurchaseOrderNumber(),
				commerceOrder.getShippingAmount(),
				commerceOrder.getShippingOptionName(), subtotal, total);
		}
	}

	private CommerceOrder _updateCommerceOrder(
			CommerceOrder commerceOrder, User user)
		throws Exception {

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			return _commerceOrderService.updateCommerceOrder(commerceOrder);
		}
	}

	private CommerceOrder _updateCommerceOrderWithPrices(
			CommerceOrder commerceOrder, BigDecimal total, User user)
		throws Exception {

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			return _commerceOrderService.updateCommerceOrder(
				commerceOrder.getExternalReferenceCode(),
				commerceOrder.getCommerceOrderId(),
				commerceOrder.getBillingAddressId(),
				commerceOrder.getCommerceShippingMethodId(),
				commerceOrder.getShippingAddressId(),
				commerceOrder.getAdvanceStatus(),
				commerceOrder.getCommercePaymentMethodKey(),
				commerceOrder.getName(), commerceOrder.getPurchaseOrderNumber(),
				commerceOrder.getShippingAmount(),
				commerceOrder.getShippingOptionName(),
				commerceOrder.getShippingWithTaxAmount(),
				commerceOrder.getSubtotal(),
				commerceOrder.getSubtotalWithTaxAmount(),
				commerceOrder.getTaxAmount(), total,
				commerceOrder.getTotalDiscountAmount(),
				commerceOrder.getTotalWithTaxAmount());
		}
	}

	private static CommerceChannel _commerceChannel;
	private static CommerceCurrency _commerceCurrency;
	private static CommerceOrder _commerceOrder;

	@Inject
	private static CommerceOrderLocalService _commerceOrderLocalService;

	private static Company _company;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	@Inject
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Inject
	private CommerceOrderService _commerceOrderService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}