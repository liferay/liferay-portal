/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.checkout.web.internal.frontend.data.set.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.service.CommerceOrderItemLocalService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.FDSDataProviderRegistry;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.math.BigDecimal;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Alessio Antonio Rendina
 */
@RunWith(Arquillian.class)
public class CommerceCheckoutDeliveryGroupsFDSDataProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_fdsDataProvider = _fdsDataProviderRegistry.getFDSDataProvider(
			CommercePortletKeys.COMMERCE_CHECKOUT + "-deliveryGroups");

		Group group = GroupTestUtil.addGroup();

		CommerceCurrency commerceCurrency =
			CommerceCurrencyTestUtil.addCommerceCurrency(group.getCompanyId());

		CommerceChannel commerceChannel = CommerceTestUtil.addCommerceChannel(
			group.getGroupId(), commerceCurrency.getCode());

		CPInstance cpInstance = CPTestUtil.addCPInstance(group.getGroupId());

		CommerceTestUtil.updateBackOrderCPDefinitionInventory(
			cpInstance.getCPDefinition());

		CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			TestPropsValues.getUserId(), commerceChannel.getGroupId(),
			commerceCurrency.getCommerceCurrencyId());

		CommerceAddress commerceAddress =
			CommerceTestUtil.addUserCommerceAddress(
				group.getGroupId(), TestPropsValues.getUserId());
		CommerceOrderItem commerceOrderItem =
			CommerceTestUtil.addCommerceOrderItem(
				commerceOrder.getCommerceOrderId(),
				cpInstance.getCPInstanceId(), BigDecimal.ONE);

		_commerceOrderItemLocalService.updateCommerceOrderItemInfo(
			commerceOrderItem.getCommerceOrderItemId(),
			commerceAddress.getCommerceAddressId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		_mockHttpServletRequest.setParameter(
			"commerceOrderId",
			String.valueOf(commerceOrder.getCommerceOrderId()));

		_user = UserTestUtil.addUser();
	}

	@Test
	public void testGetItems() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_fdsDataProvider.getItems(
				null, null, _mockHttpServletRequest, null);

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			_assertPrincipalException(principalException);
		}

		List<?> items = _fdsDataProvider.getItems(
			null, null, _mockHttpServletRequest, null);

		Assert.assertEquals(items.toString(), 1, items.size());
	}

	@Test
	public void testGetItemsCount() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_fdsDataProvider.getItemsCount(null, _mockHttpServletRequest);

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			_assertPrincipalException(principalException);
		}

		Assert.assertEquals(
			1, _fdsDataProvider.getItemsCount(null, _mockHttpServletRequest));
	}

	private void _assertPrincipalException(
		PrincipalException.MustHavePermission principalException) {

		Assert.assertArrayEquals(
			new String[] {ActionKeys.VIEW}, principalException.actionId);
		Assert.assertEquals(_user.getUserId(), principalException.userId);
	}

	@Inject
	private CommerceOrderItemLocalService _commerceOrderItemLocalService;

	private FDSDataProvider<?> _fdsDataProvider;

	@Inject
	private FDSDataProviderRegistry _fdsDataProviderRegistry;

	private final MockHttpServletRequest _mockHttpServletRequest =
		new MockHttpServletRequest();
	private User _user;

}