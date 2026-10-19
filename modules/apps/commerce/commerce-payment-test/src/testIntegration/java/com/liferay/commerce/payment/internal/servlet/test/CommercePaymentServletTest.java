/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.internal.servlet.test;

import com.liferay.commerce.constants.CommercePaymentEntryConstants;
import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.constants.CommerceOrderPaymentConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.order.engine.CommerceOrderEngine;
import com.liferay.commerce.payment.constants.CommercePaymentIntegrationConstants;
import com.liferay.commerce.payment.engine.CommercePaymentEngine;
import com.liferay.commerce.payment.integration.CommercePaymentIntegration;
import com.liferay.commerce.payment.model.CommercePaymentEntry;
import com.liferay.commerce.payment.service.CommercePaymentEntryLocalService;
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
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.encryptor.Encryptor;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.frutilla.FrutillaRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

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

		Bundle bundle = FrameworkUtil.getBundle(
			CommercePaymentServletTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_commercePaymentIntegrationServiceRegistration =
			bundleContext.registerService(
				CommercePaymentIntegration.class,
				new TestCommercePaymentIntegration(), null);

		_commercePaymentMethodGroupRelLocalService.
			addCommercePaymentMethodGroupRel(
				_user.getUserId(), _commerceChannel.getGroupId(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(), true, null, _KEY, 99,
				null);

		_mockHttpServletRequest = new MockHttpServletRequest("GET", "");

		_mockHttpServletRequest.setAttribute("LOCALE", LocaleUtil.ITALY);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getCompanyId(), _group.getGroupId(), _user.getUserId());
	}

	@After
	public void tearDown() throws Exception {
		CommerceGroupThreadLocal.set(_originalCommerceGroup);

		if (_commercePaymentIntegrationServiceRegistration != null) {
			_commercePaymentIntegrationServiceRegistration.unregister();
		}

		for (CommerceOrder commerceOrder : _commerceOrders) {
			_commerceOrderLocalService.deleteCommerceOrder(commerceOrder);
		}

		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);
		PrincipalThreadLocal.setName(_originalName);
	}

	@Test
	public void testCallbackWithEntryIdAndWithoutEntryKeyProcessesPaymentEntry()
		throws Exception {

		frutillaRule.scenario(
			"A callback request without entry key must process the existing " +
				"payment entry"
		).given(
			"An order whose payment was started by its owner"
		).when(
			"the owner sends a callback request with the payment URL " +
				"parameters and the entry ID but without the entry key"
		).then(
			"No other payment entry should be added and the owner should be " +
				"redirected to the next step of the order"
		);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.commerce.payment.internal.servlet." +
					"CommercePaymentServlet",
				LoggerTestUtil.OFF)) {

			CommerceOrder commerceOrder = _addCommerceOrder();

			String nextStep = "http://localhost/" + commerceOrder.getUuid();

			MockHttpServletRequest mockHttpServletRequest = _startPayment(
				commerceOrder, nextStep);

			CommercePaymentEntry commercePaymentEntry =
				_getCommercePaymentEntry(commerceOrder);

			MockHttpServletRequest callbackMockHttpServletRequest =
				_getMockHttpServletRequest(_user);

			callbackMockHttpServletRequest.addParameter(
				"entryId",
				String.valueOf(
					commercePaymentEntry.getCommercePaymentEntryId()));
			callbackMockHttpServletRequest.addParameter(
				"groupId", String.valueOf(commerceOrder.getGroupId()));
			callbackMockHttpServletRequest.addParameter("nextStep", nextStep);
			callbackMockHttpServletRequest.addParameter(
				"uuid", commerceOrder.getUuid());
			callbackMockHttpServletRequest.setSession(
				mockHttpServletRequest.getSession());

			MockHttpServletResponse mockHttpServletResponse = _service(
				callbackMockHttpServletRequest);

			Assert.assertEquals(
				nextStep, mockHttpServletResponse.getRedirectedUrl());

			Assert.assertEquals(
				1,
				_commercePaymentEntryLocalService.
					getCommercePaymentEntriesCount(
						commerceOrder.getCompanyId(),
						PortalUtil.getClassNameId(CommerceOrder.class),
						commerceOrder.getCommerceOrderId()));
		}
	}

	@Test
	public void testCallbackWithMismatchedEntryKeyDoesNotFailPayment()
		throws Exception {

		frutillaRule.scenario(
			"A callback request with an entry key that does not match the " +
				"payment entry must not change the order"
		).given(
			"An order whose payment was started by its owner"
		).when(
			"the owner sends a callback request with an entry key that does " +
				"not match the payment entry"
		).then(
			"The order payment status should not change"
		);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.commerce.payment.internal.servlet." +
					"CommercePaymentServlet",
				LoggerTestUtil.OFF)) {

			CommerceOrder commerceOrder = _addCommerceOrder();

			String nextStep = "http://localhost/" + commerceOrder.getUuid();

			MockHttpServletRequest mockHttpServletRequest = _startPayment(
				commerceOrder, nextStep);

			commerceOrder = _commerceOrderLocalService.getCommerceOrder(
				commerceOrder.getCommerceOrderId());

			int paymentStatus = commerceOrder.getPaymentStatus();

			CommercePaymentEntry commercePaymentEntry =
				_getCommercePaymentEntry(commerceOrder);

			MockHttpServletRequest callbackMockHttpServletRequest =
				_getMockHttpServletRequest(_user);

			callbackMockHttpServletRequest.addParameter(
				"entryId",
				String.valueOf(
					commercePaymentEntry.getCommercePaymentEntryId()));
			callbackMockHttpServletRequest.addParameter(
				"entryKey", RandomTestUtil.randomString());
			callbackMockHttpServletRequest.setSession(
				mockHttpServletRequest.getSession());

			MockHttpServletResponse mockHttpServletResponse = _service(
				callbackMockHttpServletRequest);

			Assert.assertNotEquals(
				nextStep, mockHttpServletResponse.getRedirectedUrl());

			commerceOrder = _commerceOrderLocalService.getCommerceOrder(
				commerceOrder.getCommerceOrderId());

			Assert.assertEquals(
				paymentStatus, commerceOrder.getPaymentStatus());
		}
	}

	@Test
	public void testCallbackWithoutPermissionDoesNotRedirectToNextStep()
		throws Exception {

		frutillaRule.scenario(
			"A callback request must only redirect to the next step of an " +
				"order the caller can access"
		).given(
			"An order whose payment was started by its owner"
		).when(
			"another user sends a callback request for its payment entry"
		).then(
			"The redirect should not be the next step of the order"
		);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.commerce.payment.internal.servlet." +
					"CommercePaymentServlet",
				LoggerTestUtil.OFF)) {

			CommerceOrder commerceOrder = _addCommerceOrder();

			String nextStep = "http://localhost/" + commerceOrder.getUuid();

			_startPayment(commerceOrder, nextStep);

			CommercePaymentEntry commercePaymentEntry =
				_getCommercePaymentEntry(commerceOrder);

			MockHttpServletRequest mockHttpServletRequest =
				_getMockHttpServletRequest(UserTestUtil.addUser());

			mockHttpServletRequest.addParameter(
				"entryId",
				String.valueOf(
					commercePaymentEntry.getCommercePaymentEntryId()));
			mockHttpServletRequest.addParameter("entryKey", _KEY);

			MockHttpServletResponse mockHttpServletResponse = _service(
				mockHttpServletRequest);

			Assert.assertNotEquals(
				nextStep, mockHttpServletResponse.getRedirectedUrl());
		}
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

	@Test
	public void testForeignNextStepDoesNotPersist() throws Exception {
		frutillaRule.scenario(
			"A rejected next step must not be used by later requests"
		).given(
			"An order whose payment was started by its owner"
		).when(
			"another user sends a request with a next step on another host " +
				"and the owner sends the callback request"
		).then(
			"The owner should be redirected to the next step of the order"
		);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.commerce.payment.internal.servlet." +
					"CommercePaymentServlet",
				LoggerTestUtil.OFF)) {

			CommerceOrder commerceOrder = _addCommerceOrder();

			String nextStep = "http://localhost/" + commerceOrder.getUuid();

			MockHttpServletRequest mockHttpServletRequest = _startPayment(
				commerceOrder, nextStep);

			MockHttpServletRequest foreignMockHttpServletRequest =
				_getMockHttpServletRequest(UserTestUtil.addUser());

			foreignMockHttpServletRequest.addParameter("entryKey", _KEY);
			foreignMockHttpServletRequest.addParameter(
				"nextStep",
				StringBundler.concat(
					"http://", RandomTestUtil.randomString(), ".com/",
					RandomTestUtil.randomString()));

			_service(foreignMockHttpServletRequest);

			CommercePaymentEntry commercePaymentEntry =
				_getCommercePaymentEntry(commerceOrder);

			MockHttpServletRequest callbackMockHttpServletRequest =
				_getMockHttpServletRequest(_user);

			callbackMockHttpServletRequest.addParameter(
				"entryId",
				String.valueOf(
					commercePaymentEntry.getCommercePaymentEntryId()));
			callbackMockHttpServletRequest.addParameter("entryKey", _KEY);
			callbackMockHttpServletRequest.setSession(
				mockHttpServletRequest.getSession());

			MockHttpServletResponse mockHttpServletResponse = _service(
				callbackMockHttpServletRequest);

			Assert.assertEquals(
				nextStep, mockHttpServletResponse.getRedirectedUrl());
		}
	}

	@Test
	public void testGuestCallbackRedirectsToNextStep() throws Exception {
		frutillaRule.scenario(
			"A guest must be able to complete the payment of a guest order"
		).given(
			"A guest order whose payment was started by a guest with the " +
				"guest token"
		).when(
			"the guest sends the callback request in the same session or " +
				"with the guest token"
		).then(
			"The guest should be redirected to the next step of the order"
		);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.commerce.payment.internal.servlet." +
					"CommercePaymentServlet",
				LoggerTestUtil.OFF)) {

			CommerceOrder commerceOrder = _addGuestCommerceOrder();

			String nextStep = "http://localhost/" + commerceOrder.getUuid();

			MockHttpServletRequest mockHttpServletRequest = _startGuestPayment(
				commerceOrder, nextStep);

			CommercePaymentEntry commercePaymentEntry =
				_getCommercePaymentEntry(commerceOrder);

			MockHttpServletRequest callbackMockHttpServletRequest =
				_getMockHttpServletRequest(null);

			callbackMockHttpServletRequest.addParameter(
				"entryId",
				String.valueOf(
					commercePaymentEntry.getCommercePaymentEntryId()));
			callbackMockHttpServletRequest.addParameter("entryKey", _KEY);
			callbackMockHttpServletRequest.setSession(
				mockHttpServletRequest.getSession());

			MockHttpServletResponse mockHttpServletResponse = _service(
				callbackMockHttpServletRequest);

			Assert.assertEquals(
				nextStep, mockHttpServletResponse.getRedirectedUrl());

			MockHttpServletRequest guestTokenMockHttpServletRequest =
				_getMockHttpServletRequest(null);

			guestTokenMockHttpServletRequest.addParameter(
				"entryId",
				String.valueOf(
					commercePaymentEntry.getCommercePaymentEntryId()));
			guestTokenMockHttpServletRequest.addParameter(
				"groupId", String.valueOf(commerceOrder.getGroupId()));
			guestTokenMockHttpServletRequest.addParameter(
				"guestToken", _getGuestToken(commerceOrder));
			guestTokenMockHttpServletRequest.addParameter(
				"uuid", commerceOrder.getUuid());

			mockHttpServletResponse = _service(
				guestTokenMockHttpServletRequest);

			Assert.assertEquals(
				nextStep, mockHttpServletResponse.getRedirectedUrl());
		}
	}

	@Test
	public void testGuestOrderCallbackWithoutSessionDoesNotRedirectToNextStep()
		throws Exception {

		frutillaRule.scenario(
			"A callback request for a guest order must only be accepted from " +
				"the session that started the payment"
		).given(
			"A guest order whose payment was started by a guest"
		).when(
			"a signed in user sends a callback request for its payment entry"
		).then(
			"The redirect should not be the next step of the order"
		);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.commerce.payment.internal.servlet." +
					"CommercePaymentServlet",
				LoggerTestUtil.OFF)) {

			CommerceOrder commerceOrder = _addGuestCommerceOrder();

			String nextStep = "http://localhost/" + commerceOrder.getUuid();

			_startGuestPayment(commerceOrder, nextStep);

			CommercePaymentEntry commercePaymentEntry =
				_getCommercePaymentEntry(commerceOrder);

			MockHttpServletRequest mockHttpServletRequest =
				_getMockHttpServletRequest(UserTestUtil.addUser());

			mockHttpServletRequest.addParameter(
				"entryId",
				String.valueOf(
					commercePaymentEntry.getCommercePaymentEntryId()));
			mockHttpServletRequest.addParameter("entryKey", _KEY);

			MockHttpServletResponse mockHttpServletResponse = _service(
				mockHttpServletRequest);

			Assert.assertNotEquals(
				nextStep, mockHttpServletResponse.getRedirectedUrl());
		}
	}

	@Test
	public void testManagePaymentIntegrationCancelsOwnPaymentEntry()
		throws Exception {

		frutillaRule.scenario(
			"A buyer cancels the in-progress payment entry of their own order"
		).given(
			"An order of the buyer with a payment entry in the created status"
		).when(
			"the buyer requests the payment servlet to cancel that entry"
		).then(
			"The entry and the order payment status become cancelled"
		);

		_addPayPalCommercePaymentMethodGroupRel();

		CommerceOrder commerceOrder = _addPayPalCommerceOrder(_user);

		CommercePaymentEntry commercePaymentEntry =
			_addCreatedCommercePaymentEntry(_user, commerceOrder);

		MockHttpServletRequest mockHttpServletRequest =
			_getCancelMockHttpServletRequest(
				_user, commerceOrder, commercePaymentEntry);

		_servlet.service(mockHttpServletRequest, new MockHttpServletResponse());

		commercePaymentEntry =
			_commercePaymentEntryLocalService.getCommercePaymentEntry(
				commercePaymentEntry.getCommercePaymentEntryId());

		Assert.assertEquals(
			CommercePaymentEntryConstants.STATUS_CANCELLED,
			commercePaymentEntry.getPaymentStatus());
	}

	@Test
	public void testManagePaymentIntegrationDoesNotCancelOtherOrderEntry()
		throws Exception {

		frutillaRule.scenario(
			"A buyer must not cancel a payment entry that belongs to another " +
				"order"
		).given(
			"Two buyers, each with their own order, and the other buyer with " +
				"a payment entry in the created status"
		).when(
			"a buyer requests the payment servlet with their own order but " +
				"the other buyer's entry id"
		).then(
			"The other buyer's entry and order payment status are unchanged"
		);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.commerce.payment.internal.servlet." +
					"CommercePaymentServlet",
				LoggerTestUtil.OFF)) {

			_addPayPalCommercePaymentMethodGroupRel();

			CommerceOrder attackerCommerceOrder = _addPayPalCommerceOrder(
				_user);

			User victimUser = UserTestUtil.addUser();

			CommerceOrder victimCommerceOrder = _addPayPalCommerceOrder(
				victimUser);

			CommercePaymentEntry victimCommercePaymentEntry =
				_addCreatedCommercePaymentEntry(
					victimUser, victimCommerceOrder);

			int victimOrderPaymentStatus =
				victimCommerceOrder.getPaymentStatus();

			MockHttpServletRequest mockHttpServletRequest =
				_getCancelMockHttpServletRequest(
					_user, attackerCommerceOrder, victimCommercePaymentEntry);

			_servlet.service(
				mockHttpServletRequest, new MockHttpServletResponse());

			victimCommercePaymentEntry =
				_commercePaymentEntryLocalService.getCommercePaymentEntry(
					victimCommercePaymentEntry.getCommercePaymentEntryId());

			Assert.assertEquals(
				CommercePaymentEntryConstants.STATUS_CREATED,
				victimCommercePaymentEntry.getPaymentStatus());

			victimCommerceOrder = _commerceOrderLocalService.getCommerceOrder(
				victimCommerceOrder.getCommerceOrderId());

			Assert.assertEquals(
				victimOrderPaymentStatus,
				victimCommerceOrder.getPaymentStatus());
		}
	}

	@Test
	public void testRequestWithoutParametersDoesNotFailPayment()
		throws Exception {

		frutillaRule.scenario(
			"A request without parameters must not change any order"
		).given(
			"An order whose payment was started by its owner"
		).when(
			"a guest sends a request without parameters"
		).then(
			"The order payment status should not change"
		);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.commerce.payment.internal.servlet." +
					"CommercePaymentServlet",
				LoggerTestUtil.OFF)) {

			CommerceOrder commerceOrder = _addCommerceOrder();

			_startPayment(
				commerceOrder,
				"http://localhost/" + RandomTestUtil.randomString());

			commerceOrder = _commerceOrderLocalService.getCommerceOrder(
				commerceOrder.getCommerceOrderId());

			int paymentStatus = commerceOrder.getPaymentStatus();

			Assert.assertNotEquals(
				CommerceOrderPaymentConstants.STATUS_COMPLETED, paymentStatus);

			_service(_getMockHttpServletRequest(null));

			commerceOrder = _commerceOrderLocalService.getCommerceOrder(
				commerceOrder.getCommerceOrderId());

			Assert.assertEquals(
				paymentStatus, commerceOrder.getPaymentStatus());
		}
	}

	@Rule
	public FrutillaRule frutillaRule = new FrutillaRule();

	private CommerceOrder _addCommerceOrder() throws Exception {
		return _updateCommerceOrder(
			CommerceTestUtil.addB2CCommerceOrder(
				_user.getUserId(), _commerceChannel.getGroupId(),
				_commerceCurrency));
	}

	private CommerceOrder _addGuestCommerceOrder() throws Exception {
		AccountEntry accountEntry = _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, _user.getUserId(),
			AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
			RandomTestUtil.randomString(), null, null, null, null,
			StringPool.BLANK, AccountConstants.ACCOUNT_ENTRY_TYPE_GUEST,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		return _updateCommerceOrder(
			_commerceOrderLocalService.addCommerceOrder(
				_user.getUserId(), _commerceChannel.getGroupId(),
				accountEntry.getAccountEntryId(), _commerceCurrency.getCode(),
				0));
	}

	private CommercePaymentEntry _addCreatedCommercePaymentEntry(
			User user, CommerceOrder commerceOrder)
		throws Exception {

		CommercePaymentEntry commercePaymentEntry =
			_commercePaymentEntryLocalService.addCommercePaymentEntry(
				user.getUserId(),
				PortalUtil.getClassNameId(CommerceOrder.class),
				commerceOrder.getCommerceOrderId(),
				_commerceChannel.getCommerceChannelId(), BigDecimal.TEN, null,
				null, _commerceCurrency.getCode(),
				LocaleUtil.toLanguageId(LocaleUtil.US), null, null,
				"paypal-integration", 1, null, RandomTestUtil.randomString(),
				CommercePaymentEntryConstants.TYPE_PAYMENT,
				ServiceContextTestUtil.getServiceContext(
					_group.getCompanyId(), _group.getGroupId(),
					user.getUserId()));

		commercePaymentEntry.setPaymentStatus(
			CommercePaymentEntryConstants.STATUS_CREATED);

		return _commercePaymentEntryLocalService.updateCommercePaymentEntry(
			commercePaymentEntry);
	}

	private CommerceOrder _addPayPalCommerceOrder(User user) throws Exception {
		CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			user.getUserId(), _commerceChannel.getGroupId(), _commerceCurrency);

		_commerceOrders.add(commerceOrder);

		commerceOrder.setCommercePaymentMethodKey("paypal-integration");

		return _commerceOrderLocalService.updateCommerceOrder(commerceOrder);
	}

	private void _addPayPalCommercePaymentMethodGroupRel() throws Exception {
		_commercePaymentMethodGroupRelLocalService.
			addCommercePaymentMethodGroupRel(
				_user.getUserId(), _commerceChannel.getGroupId(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(), true, null,
				"paypal-integration", 98, null);
	}

	private MockHttpServletRequest _getCancelMockHttpServletRequest(
		User user, CommerceOrder commerceOrder,
		CommercePaymentEntry commercePaymentEntry) {

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));

		PrincipalThreadLocal.setName(user.getUserId());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest("GET", "/o/commerce-payment");

		mockHttpServletRequest.setAttribute(WebKeys.USER, user);
		mockHttpServletRequest.setAttribute("LOCALE", LocaleUtil.US);

		mockHttpServletRequest.addParameter(
			"groupId", String.valueOf(commerceOrder.getGroupId()));
		mockHttpServletRequest.addParameter("uuid", commerceOrder.getUuid());
		mockHttpServletRequest.addParameter(
			"entryId",
			String.valueOf(commercePaymentEntry.getCommercePaymentEntryId()));
		mockHttpServletRequest.addParameter("cancel", "true");
		mockHttpServletRequest.addParameter("redirect", "false");

		return mockHttpServletRequest;
	}

	private CommercePaymentEntry _getCommercePaymentEntry(
		CommerceOrder commerceOrder) {

		List<CommercePaymentEntry> commercePaymentEntries =
			_commercePaymentEntryLocalService.getCommercePaymentEntries(
				commerceOrder.getCompanyId(),
				PortalUtil.getClassNameId(CommerceOrder.class),
				commerceOrder.getCommerceOrderId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		return commercePaymentEntries.get(0);
	}

	private String _getGuestToken(CommerceOrder commerceOrder)
		throws Exception {

		Company company = _companyLocalService.getCompany(
			commerceOrder.getCompanyId());

		return _encryptor.encrypt(
			company.getKeyObj(),
			String.valueOf(commerceOrder.getCommerceOrderId()));
	}

	private MockHttpServletRequest _getMockHttpServletRequest(User user) {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest("GET", "/o/commerce-payment");

		mockHttpServletRequest.setAttribute("LOCALE", LocaleUtil.ITALY);

		if (user != null) {
			mockHttpServletRequest.setAttribute(WebKeys.USER, user);
		}

		return mockHttpServletRequest;
	}

	private MockHttpServletResponse _service(
			MockHttpServletRequest mockHttpServletRequest)
		throws Exception {

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

		return mockHttpServletResponse;
	}

	private MockHttpServletRequest _startGuestPayment(
			CommerceOrder commerceOrder, String nextStep)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(null);

		mockHttpServletRequest.addParameter(
			"groupId", String.valueOf(commerceOrder.getGroupId()));
		mockHttpServletRequest.addParameter(
			"guestToken", _getGuestToken(commerceOrder));
		mockHttpServletRequest.addParameter("nextStep", nextStep);
		mockHttpServletRequest.addParameter("uuid", commerceOrder.getUuid());

		_service(mockHttpServletRequest);

		return mockHttpServletRequest;
	}

	private MockHttpServletRequest _startPayment(
			CommerceOrder commerceOrder, String nextStep)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(_user);

		mockHttpServletRequest.addParameter(
			"groupId", String.valueOf(commerceOrder.getGroupId()));
		mockHttpServletRequest.addParameter("nextStep", nextStep);
		mockHttpServletRequest.addParameter("uuid", commerceOrder.getUuid());

		_service(mockHttpServletRequest);

		return mockHttpServletRequest;
	}

	private CommerceOrder _updateCommerceOrder(CommerceOrder commerceOrder)
		throws Exception {

		_commerceOrders.add(commerceOrder);

		commerceOrder.setCommercePaymentMethodKey(_KEY);
		commerceOrder.setTotal(BigDecimal.TEN);

		return _commerceOrderLocalService.updateCommerceOrder(commerceOrder);
	}

	private static final String _KEY = RandomTestUtil.randomString();

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

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
	private CommercePaymentEntryLocalService _commercePaymentEntryLocalService;

	private ServiceRegistration<CommercePaymentIntegration>
		_commercePaymentIntegrationServiceRegistration;

	@Inject
	private CommercePaymentMethodGroupRelLocalService
		_commercePaymentMethodGroupRelLocalService;

	@Inject
	private CommercePriceEntryLocalService _commercePriceEntryLocalService;

	@Inject
	private CommercePriceListLocalService _commercePriceListLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private Encryptor _encryptor;

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

	private User _user;

	private static class TestCommercePaymentIntegration
		implements CommercePaymentIntegration {

		@Override
		public CommercePaymentEntry authorize(
			HttpServletRequest httpServletRequest,
			CommercePaymentEntry commercePaymentEntry) {

			return commercePaymentEntry;
		}

		@Override
		public CommercePaymentEntry cancel(
			HttpServletRequest httpServletRequest,
			CommercePaymentEntry commercePaymentEntry) {

			return commercePaymentEntry;
		}

		@Override
		public CommercePaymentEntry capture(
			HttpServletRequest httpServletRequest,
			CommercePaymentEntry commercePaymentEntry) {

			return commercePaymentEntry;
		}

		@Override
		public String getDescription(Locale locale) {
			return _KEY;
		}

		@Override
		public String getKey() {
			return _KEY;
		}

		@Override
		public String getName(Locale locale) {
			return _KEY;
		}

		@Override
		public int getPaymentIntegrationType() {
			return CommercePaymentIntegrationConstants.TYPE_INTERNAL_OFFLINE;
		}

		@Override
		public CommercePaymentEntry refund(
			HttpServletRequest httpServletRequest,
			CommercePaymentEntry commercePaymentEntry) {

			return commercePaymentEntry;
		}

	}

}