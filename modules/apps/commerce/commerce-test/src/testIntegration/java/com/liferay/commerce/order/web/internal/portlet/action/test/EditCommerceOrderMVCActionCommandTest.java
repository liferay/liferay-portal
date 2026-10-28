/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.portlet.action.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.constants.CommerceOrderActionKeys;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.constants.CommerceOrderPaymentConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.order.engine.CommerceOrderEngine;
import com.liferay.commerce.payment.test.util.TestCommercePaymentMethod;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalServiceUtil;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.service.CommerceOrderItemLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.test.util.CommerceInventoryTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.portlet.MockPortletSession;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.math.BigDecimal;

import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Crescenzo Rega
 */
@RunWith(Arquillian.class)
@Sync
public class EditCommerceOrderMVCActionCommandTest {

	@ClassRule
	@Rule
	public static AggregateTestRule aggregateTestRule = new AggregateTestRule(
		new LiferayIntegrationTestRule(),
		PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_user = UserTestUtil.addUser();

		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());

		_accountEntry = CommerceAccountTestUtil.addBusinessAccountEntry(
			_user.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString() + "@liferay.com",
			RandomTestUtil.randomString(), new long[] {_user.getUserId()}, null,
			_serviceContext);

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			_group.getCompanyId());

		CommerceChannelLocalServiceUtil.addCommerceChannel(
			null, AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
			_group.getGroupId(), "Test Channel",
			CommerceChannelConstants.CHANNEL_TYPE_SITE, null,
			_commerceCurrency.getCode(), _serviceContext);
	}

	@Test
	@TestInfo("LPD-111712")
	public void testProcessAction() throws Exception {
		_testProcessActionWithManageCommerceOrderPaymentPermissions();
		_testProcessActionWithRecalculateOrderSummary();
	}

	private MockLiferayPortletActionRequest _processAction(
			String cmd, long commerceOrderId, Map<String, String> parameters)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setParameter(Constants.CMD, cmd);
		mockLiferayPortletActionRequest.setParameter(
			"commerceOrderId", String.valueOf(commerceOrderId));

		for (Map.Entry<String, String> entry : parameters.entrySet()) {
			mockLiferayPortletActionRequest.setParameter(
				entry.getKey(), entry.getValue());
		}

		mockLiferayPortletActionRequest.setPortletSession(
			new MockPortletSession());

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		return mockLiferayPortletActionRequest;
	}

	private void _recalculateOrderSummary(
			long commerceOrderId, BigDecimal discountAmount1,
			BigDecimal discountAmount2, BigDecimal price1, BigDecimal price2)
		throws Exception {

		_processAction(
			"recalculateOrderSummary", commerceOrderId, Collections.emptyMap());

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.getCommerceOrder(commerceOrderId);

		BigDecimal expectedSubtotal = price1.add(price2);

		Assert.assertTrue(
			BigDecimalUtil.eq(expectedSubtotal, commerceOrder.getSubtotal()));

		BigDecimal expectedSubtotalDiscountAmount = discountAmount1.add(
			discountAmount2);

		Assert.assertTrue(
			BigDecimalUtil.eq(
				expectedSubtotalDiscountAmount,
				commerceOrder.getSubtotalDiscountAmount()));

		BigDecimal expectedTotal = expectedSubtotal.subtract(
			expectedSubtotalDiscountAmount);

		Assert.assertTrue(
			BigDecimalUtil.eq(expectedTotal, commerceOrder.getTotal()));
	}

	private void _testProcessActionWithManageCommerceOrderPaymentPermissions()
		throws Exception {

		CommerceOrder commerceOrder1 = CommerceTestUtil.addB2BCommerceOrder(
			_group.getGroupId(), _user.getUserId(),
			_accountEntry.getAccountEntryId(),
			_commerceCurrency.getCommerceCurrencyId());

		CommerceTestUtil.addCommercePaymentMethodGroupRel(
			_user.getUserId(), commerceOrder1.getGroupId());

		Role role = _roleLocalService.addRole(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(), null, 0,
			RandomTestUtil.randomString(), null, null,
			RoleConstants.TYPE_REGULAR, null, _serviceContext);

		_resourcePermissionLocalService.setResourcePermissions(
			commerceOrder1.getCompanyId(), CommerceOrderConstants.RESOURCE_NAME,
			ResourceConstants.SCOPE_GROUP,
			String.valueOf(_accountEntry.getAccountEntryGroupId()),
			role.getRoleId(),
			new String[] {CommerceOrderActionKeys.MANAGE_COMMERCE_ORDERS});

		User user = UserTestUtil.addUser();

		_roleLocalService.addUserRole(user.getUserId(), role.getRoleId());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			Assert.assertTrue(
				SessionErrors.contains(
					_processAction(
						"paymentMethod", commerceOrder1.getCommerceOrderId(),
						Collections.singletonMap(
							"paymentMethodKey", TestCommercePaymentMethod.KEY)),
					PrincipalException.MustHavePermission.class));
			Assert.assertTrue(
				SessionErrors.contains(
					_processAction(
						"paymentStatus", commerceOrder1.getCommerceOrderId(),
						Collections.singletonMap(
							"paymentStatus",
							String.valueOf(
								CommerceOrderPaymentConstants.
									STATUS_COMPLETED))),
					PrincipalException.MustHavePermission.class));
		}

		CommerceOrder commerceOrder2 =
			_commerceOrderLocalService.getCommerceOrder(
				commerceOrder1.getCommerceOrderId());

		Assert.assertEquals(
			commerceOrder1.getCommercePaymentMethodKey(),
			commerceOrder2.getCommercePaymentMethodKey());
		Assert.assertEquals(
			commerceOrder1.getPaymentStatus(),
			commerceOrder2.getPaymentStatus());

		_resourcePermissionLocalService.setResourcePermissions(
			commerceOrder1.getCompanyId(), CommerceOrderConstants.RESOURCE_NAME,
			ResourceConstants.SCOPE_GROUP,
			String.valueOf(commerceOrder1.getGroupId()), role.getRoleId(),
			new String[] {
				CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_PAYMENT_METHODS,
				CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_PAYMENT_STATUSES
			});

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			Assert.assertTrue(
				SessionErrors.isEmpty(
					_processAction(
						"paymentMethod", commerceOrder1.getCommerceOrderId(),
						Collections.singletonMap(
							"paymentMethodKey",
							TestCommercePaymentMethod.KEY))));
			Assert.assertTrue(
				SessionErrors.isEmpty(
					_processAction(
						"paymentStatus", commerceOrder1.getCommerceOrderId(),
						Collections.singletonMap(
							"paymentStatus",
							String.valueOf(
								CommerceOrderPaymentConstants.
									STATUS_COMPLETED)))));
		}

		commerceOrder2 = _commerceOrderLocalService.getCommerceOrder(
			commerceOrder1.getCommerceOrderId());

		Assert.assertEquals(
			TestCommercePaymentMethod.KEY,
			commerceOrder2.getCommercePaymentMethodKey());
		Assert.assertEquals(
			CommerceOrderPaymentConstants.STATUS_COMPLETED,
			commerceOrder2.getPaymentStatus());
	}

	private void _testProcessActionWithRecalculateOrderSummary()
		throws Exception {

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), LocaleUtil.US.getDisplayLanguage(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		CommerceOrder commerceOrder = CommerceTestUtil.addB2BCommerceOrder(
			_group.getGroupId(), _user.getUserId(),
			_accountEntry.getAccountEntryId(),
			_commerceCurrency.getCommerceCurrencyId());

		BigDecimal price1 = BigDecimal.valueOf(25);

		CPInstance cpInstance1 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId(), price1);

		CommerceTestUtil.updateBackOrderCPDefinitionInventory(
			cpInstance1.getCPDefinition());

		CommerceOrderItem commerceOrderItem1 =
			CommerceTestUtil.addCommerceOrderItem(
				commerceOrder.getCommerceOrderId(),
				cpInstance1.getCPInstanceId(), BigDecimal.ONE);

		BigDecimal discountAmount1 = BigDecimal.valueOf(10);

		_commerceOrderItemLocalService.updateCommerceOrderItemPrices(
			commerceOrderItem1.getCommerceOrderItemId(), discountAmount1, null,
			null, null, null, price1, null, price1);

		BigDecimal price2 = BigDecimal.valueOf(35);

		CPInstance cpInstance2 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId(), price2);

		CommerceTestUtil.updateBackOrderCPDefinitionInventory(
			cpInstance2.getCPDefinition());

		CommerceOrderItem commerceOrderItem2 =
			CommerceTestUtil.addCommerceOrderItem(
				commerceOrder.getCommerceOrderId(),
				cpInstance2.getCPInstanceId(), BigDecimal.ONE);

		BigDecimal discountAmount2 = BigDecimal.valueOf(5);

		_commerceOrderItemLocalService.updateCommerceOrderItemPrices(
			commerceOrderItem2.getCommerceOrderItemId(), discountAmount2, null,
			null, null, null, price2, null, price2);

		Country country = CommerceInventoryTestUtil.addCountry(_serviceContext);

		Region region = CommerceInventoryTestUtil.addRegion(
			country.getCountryId(), _serviceContext);

		Address address = _addressLocalService.addAddress(
			RandomTestUtil.randomString(), _user.getUserId(),
			AccountEntry.class.getName(), _accountEntry.getAccountEntryId(),
			country.getCountryId(), 0, region.getRegionId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), true,
			RandomTestUtil.randomString(), false, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			_serviceContext);

		commerceOrder = _commerceOrderLocalService.updateBillingAddress(
			commerceOrder.getCommerceOrderId(), address.getAddressId());

		commerceOrder = _commerceOrderEngine.checkoutCommerceOrder(
			commerceOrder, _user.getUserId());

		commerceOrder = _commerceOrderEngine.transitionCommerceOrder(
			commerceOrder, CommerceOrderConstants.ORDER_STATUS_PROCESSING,
			_user.getUserId(), true);

		_recalculateOrderSummary(
			commerceOrder.getCommerceOrderId(), discountAmount1,
			discountAmount2, price1, price2);

		price1 = BigDecimal.valueOf(40);

		_commerceOrderItemLocalService.updateCommerceOrderItemPrices(
			commerceOrderItem1.getCommerceOrderItemId(), discountAmount1, null,
			null, null, null, price1, null, price1);

		_recalculateOrderSummary(
			commerceOrder.getCommerceOrderId(), discountAmount1,
			discountAmount2, price1, price2);

		discountAmount1 = BigDecimal.valueOf(3);

		_commerceOrderItemLocalService.updateCommerceOrderItemPrices(
			commerceOrderItem1.getCommerceOrderItemId(), discountAmount1, null,
			null, null, null, price1, null, price1);

		_recalculateOrderSummary(
			commerceOrder.getCommerceOrderId(), discountAmount1,
			discountAmount2, price1, price2);

		price2 = BigDecimal.valueOf(60);

		_commerceOrderItemLocalService.updateCommerceOrderItemPrices(
			commerceOrderItem2.getCommerceOrderItemId(), discountAmount2, null,
			null, null, null, price2, null, price2);

		_recalculateOrderSummary(
			commerceOrder.getCommerceOrderId(), discountAmount1,
			discountAmount2, price1, price2);

		discountAmount2 = BigDecimal.valueOf(3);

		_commerceOrderItemLocalService.updateCommerceOrderItemPrices(
			commerceOrderItem2.getCommerceOrderItemId(), discountAmount2, null,
			null, null, null, price2, null, price2);

		_commerceOrderItemLocalService.deleteCommerceOrderItem(
			commerceOrderItem2.getCommerceOrderItemId());

		_recalculateOrderSummary(
			commerceOrder.getCommerceOrderId(), discountAmount1,
			BigDecimal.ZERO, price1, BigDecimal.ZERO);
	}

	private AccountEntry _accountEntry;

	@Inject
	private AddressLocalService _addressLocalService;

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	private CommerceCurrency _commerceCurrency;

	@Inject
	private CommerceOrderEngine _commerceOrderEngine;

	@Inject
	private CommerceOrderItemLocalService _commerceOrderItemLocalService;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	private Group _group;

	@Inject(
		filter = "mvc.command.name=/commerce_order/edit_commerce_order",
		type = MVCActionCommand.class
	)
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	private ServiceContext _serviceContext;
	private User _user;

}