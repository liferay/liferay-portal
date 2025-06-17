/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.internal.servlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.constants.CommerceOrderPaymentConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.order.engine.CommerceOrderEngine;
import com.liferay.commerce.payment.engine.CommercePaymentEngine;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelLocalService;
import com.liferay.commerce.payment.test.util.TestCommercePaymentMethod;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceEntryLocalService;
import com.liferay.commerce.price.list.service.CommercePriceListLocalService;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceCatalogLocalServiceUtil;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.test.util.CommerceInventoryTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.commerce.util.CommerceGroupThreadLocal;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.Servlet;

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
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Brian I. Kim
 */
@RunWith(Arquillian.class)
public class CommercePaymentServletTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_originalCommerceGroup = CommerceGroupThreadLocal.get();
		_originalName = PrincipalThreadLocal.getName();
		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		_user = UserTestUtil.addUser();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		PrincipalThreadLocal.setName(_user.getUserId());

		_group = GroupTestUtil.addGroup();

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			_group.getCompanyId());

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), _commerceCurrency.getCode());

		CommerceGroupThreadLocal.set(
			_groupLocalService.fetchGroup(_commerceChannel.getGroupId()));

		_commercePaymentMethodGroupRelLocalService.
			addCommercePaymentMethodGroupRel(
				_user.getUserId(), _commerceChannel.getGroupId(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(), true, null,
				TestCommercePaymentMethod.KEY, 99, null);

		_mockHttpServletRequest = new MockHttpServletRequest("GET", "");

		_mockHttpServletRequest.setAttribute("LOCALE", LocaleUtil.ITALY);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getCompanyId(), _group.getGroupId(), _user.getUserId());
	}

	@After
	public void tearDown() throws Exception {
		CommerceGroupThreadLocal.set(_originalCommerceGroup);

		for (CommerceOrder commerceOrder : _commerceOrders) {
			_commerceOrderLocalService.deleteCommerceOrder(commerceOrder);
		}

		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);
		PrincipalThreadLocal.setName(_originalName);
	}

	@Test
	public void testCompletePaymentPersists() throws Exception {
		frutillaRule.scenario(
			"When a payment is completed the payment status should be completed"
		).given(
			"An order with valid products"
		).when(
			"the order is complete and another user reuses the payment URL"
		).then(
			"The order payment status should stay complete"
		);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.commerce.payment.internal.servlet." +
					"CommercePaymentServlet",
				LoggerTestUtil.OFF)) {

			CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
				_user.getUserId(), _commerceChannel.getGroupId(),
				_commerceCurrency);

			_commerceOrders.add(commerceOrder);

			commerceOrder.setCommercePaymentMethodKey(
				TestCommercePaymentMethod.KEY);

			commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
				commerceOrder);

			CommerceCatalog commerceCatalog =
				CommerceCatalogLocalServiceUtil.addCommerceCatalog(
					null, RandomTestUtil.randomString(),
					_commerceCurrency.getCode(),
					LocaleUtil.toLanguageId(LocaleUtil.US), _serviceContext);

			CommercePriceList commercePriceList =
				_commercePriceListLocalService.
					fetchCatalogBaseCommercePriceList(
						commerceCatalog.getGroupId());

			CPInstance cpInstance =
				CPTestUtil.addCPInstanceWithRandomSkuFromCatalog(
					commerceCatalog.getGroupId());

			CPDefinition cpDefinition = cpInstance.getCPDefinition();

			_commercePriceEntryLocalService.addCommercePriceEntry(
				null, cpDefinition.getCProductId(),
				cpInstance.getCPInstanceUuid(),
				commercePriceList.getCommercePriceListId(), BigDecimal.ZERO,
				false, BigDecimal.ZERO, null,
				ServiceContextTestUtil.getServiceContext(_user.getGroupId()));

			CommerceInventoryWarehouse commerceInventoryWarehouse =
				CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
					_serviceContext);

			CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
				_user.getUserId(), commerceInventoryWarehouse, BigDecimal.TEN,
				cpInstance.getSku(), StringPool.BLANK);

			CommerceTestUtil.addWarehouseCommerceChannelRel(
				commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
				_commerceChannel.getCommerceChannelId());

			CommerceTestUtil.addCommerceOrderItem(
				commerceOrder.getCommerceOrderId(),
				cpInstance.getCPInstanceId(), BigDecimal.ONE);

			CommerceOrder checkoutCommerceOrder =
				_commerceOrderEngine.checkoutCommerceOrder(
					commerceOrder, _user.getUserId());

			_mockHttpServletRequest = new MockHttpServletRequest("GET", "");

			_mockHttpServletRequest.setAttribute("LOCALE", LocaleUtil.ITALY);

			_commercePaymentEngine.processPayment(
				commerceOrder.getCommerceOrderId(), null,
				_mockHttpServletRequest);

			CommerceOrder paymentCommerceOrder =
				_commerceOrderLocalService.getCommerceOrder(
					checkoutCommerceOrder.getCommerceOrderId());

			Assert.assertEquals(
				CommerceOrderPaymentConstants.STATUS_AUTHORIZED,
				paymentCommerceOrder.getPaymentStatus());

			Assert.assertNotNull(paymentCommerceOrder.getTransactionId());

			_commercePaymentEngine.completePayment(
				paymentCommerceOrder.getCommerceOrderId(),
				paymentCommerceOrder.getTransactionId(),
				_mockHttpServletRequest);

			paymentCommerceOrder = _commerceOrderLocalService.getCommerceOrder(
				checkoutCommerceOrder.getCommerceOrderId());

			Assert.assertEquals(
				CommerceOrderPaymentConstants.STATUS_COMPLETED,
				paymentCommerceOrder.getPaymentStatus());

			_mockHttpServletRequest = new MockHttpServletRequest(
				"GET", "/o/payment");

			User user = UserTestUtil.addUser();

			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(user));

			PrincipalThreadLocal.setName(user.getUserId());

			_mockHttpServletRequest.setAttribute(WebKeys.USER, user);

			_mockHttpServletRequest.addParameter(
				"groupId", String.valueOf(_commerceChannel.getGroupId()));
			_mockHttpServletRequest.addParameter(
				"uuid", String.valueOf(commerceOrder.getUuid()));

			_servlet.service(
				_mockHttpServletRequest, new MockHttpServletResponse());

			paymentCommerceOrder = _commerceOrderLocalService.getCommerceOrder(
				checkoutCommerceOrder.getCommerceOrderId());

			Assert.assertEquals(
				CommerceOrderPaymentConstants.STATUS_COMPLETED,
				paymentCommerceOrder.getPaymentStatus());
		}
	}

	@Rule
	public FrutillaRule frutillaRule = new FrutillaRule();

	private static User _user;

	@DeleteAfterTestRun
	private CommerceChannel _commerceChannel;

	@DeleteAfterTestRun
	private CommerceCurrency _commerceCurrency;

	@Inject
	private CommerceOrderEngine _commerceOrderEngine;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	private final List<CommerceOrder> _commerceOrders = new ArrayList<>();

	@Inject
	private CommercePaymentEngine _commercePaymentEngine;

	@Inject
	private CommercePaymentMethodGroupRelLocalService
		_commercePaymentMethodGroupRelLocalService;

	@Inject
	private CommercePriceEntryLocalService _commercePriceEntryLocalService;

	@Inject
	private CommercePriceListLocalService _commercePriceListLocalService;

	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	private MockHttpServletRequest _mockHttpServletRequest;
	private Group _originalCommerceGroup;
	private String _originalName;
	private PermissionChecker _originalPermissionChecker;
	private ServiceContext _serviceContext;

	@Inject(
		filter = "osgi.http.whiteboard.servlet.name=com.liferay.commerce.payment.internal.servlet.CommercePaymentServlet"
	)
	private Servlet _servlet;

}