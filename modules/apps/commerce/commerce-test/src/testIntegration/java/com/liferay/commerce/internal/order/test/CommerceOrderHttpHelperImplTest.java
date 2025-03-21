/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.order.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.inventory.model.CommerceInventoryBookedQuantity;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.inventory.service.CommerceInventoryBookedQuantityLocalService;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.order.CommerceOrderHttpHelper;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.test.util.CommerceInventoryTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.commerce.test.util.context.TestCommerceContext;
import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.theme.ThemeDisplayFactory;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;

import org.frutilla.FrutillaRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Luca Pellizzon
 */
@RunWith(Arquillian.class)
@Sync
public class CommerceOrderHttpHelperImplTest {

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

		_permissionChecker = PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		PrincipalThreadLocal.setName(_user.getUserId());

		List<CommerceInventoryBookedQuantity>
			commerceInventoryBookedQuantities =
				_commerceInventoryBookedQuantityLocalService.
					getCommerceInventoryBookedQuantities(
						QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (CommerceInventoryBookedQuantity commerceInventoryBookedQuantity :
				commerceInventoryBookedQuantities) {

			_commerceInventoryBookedQuantityLocalService.
				deleteCommerceInventoryBookedQuantity(
					commerceInventoryBookedQuantity);
		}

		PrincipalThreadLocal.setName(_user.getUserId());

		_httpServletRequest = new MockHttpServletRequest();

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			_group.getCompanyId());

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), _commerceCurrency.getCode());

		_accountEntry = CommerceAccountTestUtil.getPersonAccountEntry(
			_user.getUserId());

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, _commerceChannel, _user, _group,
			null);

		_httpServletRequest.setAttribute(
			CommerceWebKeys.COMMERCE_CONTEXT, commerceContext);

		_httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, _themeDisplay);

		_themeDisplay = ThemeDisplayFactory.create();

		_themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));
		_themeDisplay.setScopeGroupId(_group.getGroupId());
		_themeDisplay.setSignedIn(true);
		_themeDisplay.setUser(_user);

		_httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, _themeDisplay);

		_httpServletRequest.setAttribute(WebKeys.USER_ID, _user.getUserId());

		_themeDisplay.setRequest(_httpServletRequest);
	}

	@After
	public void tearDown() throws PortalException {
		for (CommerceOrder commerceOrder : _commerceOrders) {
			_commerceOrderLocalService.deleteCommerceOrder(commerceOrder);
		}

		CentralizedThreadLocal.clearShortLivedCentralizedThreadLocals();
	}

	@Test
	public void testGetCommerceOrder() throws Exception {
		frutillaRule.scenario(
			"Get commerce order from http servlet request"
		).given(
			"An HttpServletRequest and a ThemeDisplay"
		).when(
			"I add an order to the HttpServletRequest"
		).then(
			"I should be able to get it from the HttpServletRequest"
		);

		CommerceOrder expectedCommerceOrder =
			_commerceOrderHttpHelper.addCommerceOrder(_httpServletRequest);

		_commerceOrders.add(expectedCommerceOrder);

		CommerceOrder actualCommerceOrder =
			_commerceOrderHttpHelper.getCurrentCommerceOrder(
				_httpServletRequest);

		Assert.assertEquals(
			expectedCommerceOrder.getCommerceOrderId(),
			actualCommerceOrder.getCommerceOrderId());
	}

	@Test
	public void testGetCommerceOrderItemsQuantity() throws Exception {
		frutillaRule.scenario(
			"Get the total quantity of items in an order retrieved from http " +
				"servlet request"
		).given(
			"A group, a user, an HttpServletRequest and a ThemeDisplay"
		).when(
			"I add an order to the HttpServletRequest"
		).and(
			"I add an item to the order"
		).then(
			"I should be able to get the total amount of items inside the " +
				"order from HttpServletRequest"
		);

		CommerceOrder commerceOrder = _commerceOrderHttpHelper.addCommerceOrder(
			_httpServletRequest);

		_commerceOrders.add(commerceOrder);

		CPInstance cpInstance = CPTestUtil.addCPInstance(_group.getGroupId());

		_commerceInventoryWarehouse =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		CommerceTestUtil.addWarehouseCommerceChannelRel(
			_commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
			_commerceChannel.getCommerceChannelId());

		CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
			_user.getUserId(), _commerceInventoryWarehouse, BigDecimal.TEN,
			cpInstance.getSku(), StringPool.BLANK);

		CommerceOrderItem commerceOrderItem =
			CommerceTestUtil.addCommerceOrderItem(
				commerceOrder.getCommerceOrderId(),
				cpInstance.getCPInstanceId(), BigDecimal.valueOf(2));

		Assert.assertTrue(
			BigDecimalUtil.eq(
				commerceOrderItem.getQuantity(),
				_commerceOrderHttpHelper.getCommerceOrderItemsQuantity(
					_httpServletRequest)));
	}

	@Test
	public void testGetCommerceOrderWithNullCommerceContext() throws Exception {
		frutillaRule.scenario(
			"Attempt to get a commerce order from http servlet request"
		).given(
			"An HttpServletRequest and a ThemeDisplay"
		).when(
			"I use an empty HttpServletRequest with null CommerceContext"
		).then(
			"I should get a null value"
		);

		CommerceOrder commerceOrder =
			_commerceOrderHttpHelper.getCurrentCommerceOrder(
				new MockHttpServletRequest());

		Assert.assertNull(commerceOrder);
	}

	@Rule
	public FrutillaRule frutillaRule = new FrutillaRule();

	private static User _user;

	@DeleteAfterTestRun
	private AccountEntry _accountEntry;

	@DeleteAfterTestRun
	private CommerceChannel _commerceChannel;

	@DeleteAfterTestRun
	private CommerceCurrency _commerceCurrency;

	@Inject
	private CommerceInventoryBookedQuantityLocalService
		_commerceInventoryBookedQuantityLocalService;

	@DeleteAfterTestRun
	private CommerceInventoryWarehouse _commerceInventoryWarehouse;

	@Inject
	private CommerceOrderHttpHelper _commerceOrderHttpHelper;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	private final List<CommerceOrder> _commerceOrders = new ArrayList<>();
	private Group _group;
	private HttpServletRequest _httpServletRequest;
	private PermissionChecker _permissionChecker;
	private ThemeDisplay _themeDisplay;

}