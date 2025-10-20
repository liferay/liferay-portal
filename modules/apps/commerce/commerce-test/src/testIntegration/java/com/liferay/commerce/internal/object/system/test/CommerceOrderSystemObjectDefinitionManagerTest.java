/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.object.system.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.inventory.service.CommerceInventoryBookedQuantityLocalService;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.attributes.provider.CommerceModelAttributesProvider;
import com.liferay.commerce.order.engine.CommerceOrderEngine;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;

import java.util.Map;

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
@Sync
public class CommerceOrderSystemObjectDefinitionManagerTest {

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

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		PrincipalThreadLocal.setName(_user.getUserId());

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			_group.getCompanyId());

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), _commerceCurrency.getCode());
	}

	@After
	public void tearDown() throws PortalException {
		_commerceOrderLocalService.deleteCommerceOrders(
			_commerceChannel.getGroupId());

		CentralizedThreadLocal.clearShortLivedCentralizedThreadLocals();
	}

	@Test
	public void testGetVariables() throws Exception {
		CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency);

		commerceOrder = CommerceTestUtil.addCheckoutDetailsToCommerceOrder(
			commerceOrder, _user.getUserId(), false, false);

		commerceOrder = _commerceOrderEngine.checkoutCommerceOrder(
			commerceOrder, _user.getUserId());

		ObjectDefinition commerceOrderObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_COMMERCE_ORDER", _user.getCompanyId());

		DTOConverter<?, ?> commerceOrderDTOConverter =
			_dtoConverterRegistry.getDTOConverter(
				"Liferay.Headless.Commerce.Admin.Order",
				CommerceOrder.class.getName(), "v1.0");

		JSONObject payloadJSONObject = JSONUtil.put(
			"classPK", commerceOrder.getCommerceOrderId()
		).put(
			"model" + CommerceOrder.class.getSimpleName(),
			commerceOrder.getModelAttributes()
		).put(
			"modelDTO" + commerceOrderDTOConverter.getContentType(),
			_commerceModelAttributesProvider.getModelAttributes(
				commerceOrder, commerceOrderDTOConverter,
				commerceOrder.getUserId())
		);

		SystemObjectDefinitionManager systemObjectDefinitionManager =
			_systemObjectDefinitionManagerRegistry.
				getSystemObjectDefinitionManager(
					commerceOrderObjectDefinition.getName());

		Map<String, Object> variables =
			systemObjectDefinitionManager.getVariables(
				commerceOrderDTOConverter.getContentType(),
				commerceOrderObjectDefinition, false, payloadJSONObject);

		Assert.assertEquals(
			String.valueOf(commerceOrder.getCommerceAccountId()),
			variables.get("accountId"));
		Assert.assertEquals(
			String.valueOf(_commerceChannel.getCommerceChannelId()),
			variables.get("channelId"));
		Assert.assertEquals(
			_commerceCurrency.getCode(), variables.get("currencyCode"));
		Assert.assertEquals(
			commerceOrder.getOrderDate(
			).getTime(),
			variables.get("orderDate"));
		Assert.assertEquals(
			commerceOrder.getOrderStatus(), variables.get("orderStatus"));
		Assert.assertEquals(
			String.valueOf(commerceOrder.getCommerceOrderTypeId()),
			variables.get("orderTypeId"));
		Assert.assertEquals(
			String.valueOf(commerceOrder.getCommercePaymentMethodKey()),
			variables.get("paymentMethod"));
		Assert.assertEquals(
			commerceOrder.getShippingAmount(), variables.get("shippingAmount"));
		Assert.assertEquals(
			commerceOrder.getTaxAmount(), variables.get("taxAmount"));
		Assert.assertEquals(commerceOrder.getTotal(), variables.get("total"));
	}

	private static User _user;

	@DeleteAfterTestRun
	private CommerceChannel _commerceChannel;

	@Inject
	private CommerceChannelLocalService _commerceChannelLocalService;

	@DeleteAfterTestRun
	private CommerceCurrency _commerceCurrency;

	@Inject
	private CommerceInventoryBookedQuantityLocalService
		_commerceInventoryBookedQuantityLocalService;

	@Inject
	private CommerceModelAttributesProvider _commerceModelAttributesProvider;

	@Inject
	private CommerceOrderEngine _commerceOrderEngine;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Inject
	private DTOConverterRegistry _dtoConverterRegistry;

	private Group _group;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private SystemObjectDefinitionManagerRegistry
		_systemObjectDefinitionManagerRegistry;

}