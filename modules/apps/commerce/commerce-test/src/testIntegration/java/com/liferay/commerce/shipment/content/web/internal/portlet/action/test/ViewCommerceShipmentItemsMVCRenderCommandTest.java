/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipment.content.web.internal.portlet.action.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.CommerceShipment;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.service.CommerceShipmentItemLocalService;
import com.liferay.commerce.service.CommerceShipmentLocalService;
import com.liferay.commerce.test.util.CommerceInventoryTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.commerce.test.util.context.TestCommerceContext;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Tancredi Covioli
 */
@RunWith(Arquillian.class)
public class ViewCommerceShipmentItemsMVCRenderCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			_group.getCompanyId());

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), _commerceCurrency.getCode());
	}

	@Test
	public void testRender() throws Exception {
		User user1 = UserTestUtil.addUser();

		CommerceShipment commerceShipment1 = _addCommerceShipment(
			_commerceChannel, user1);

		Assert.assertEquals(
			"/error.jsp",
			_render(
				commerceShipment1.getCommerceShipmentId(),
				_userLocalService.getGuestUser(_group.getCompanyId())));
		Assert.assertEquals(
			"/view_commerce_shipment_items.jsp",
			_render(commerceShipment1.getCommerceShipmentId(), user1));

		CommerceShipment commerceShipment2 = _addCommerceShipment(
			_commerceChannel, UserTestUtil.addUser());

		Assert.assertEquals(
			"/error.jsp",
			_render(commerceShipment2.getCommerceShipmentId(), user1));

		Group group = GroupTestUtil.addGroup();

		CommerceShipment commerceShipment3 = _addCommerceShipment(
			CommerceTestUtil.addCommerceChannel(
				group.getGroupId(), _commerceCurrency.getCode()),
			user1);

		Assert.assertEquals(
			"/error.jsp",
			_render(commerceShipment3.getCommerceShipmentId(), user1));
	}

	private CommerceShipment _addCommerceShipment(
			CommerceChannel commerceChannel, User user)
		throws Exception {

		CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			user.getUserId(), commerceChannel.getGroupId(),
			_commerceCurrency.getCommerceCurrencyId());
		CPInstance cpInstance = CPTestUtil.addCPInstanceWithRandomSku(
			commerceChannel.getSiteGroupId());

		CommerceOrderItem commerceOrderItem =
			CommerceTestUtil.addCommerceOrderItem(
				commerceOrder.getCommerceOrderId(),
				cpInstance.getCPInstanceId(), BigDecimal.ONE);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				commerceChannel.getGroupId());

		CommerceShipment commerceShipment =
			_commerceShipmentLocalService.addCommerceShipment(
				commerceOrder.getCommerceOrderId(), serviceContext);

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse();

		_commerceShipmentItemLocalService.addCommerceShipmentItem(
			null, commerceShipment.getCommerceShipmentId(),
			commerceOrderItem.getCommerceOrderItemId(),
			commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
			commerceOrderItem.getQuantity(), null, false, serviceContext);

		return commerceShipment;
	}

	private String _render(long commerceShipmentId, User user)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest(mockHttpServletRequest);

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_REQUEST,
			mockLiferayPortletRenderRequest);

		MockLiferayPortletRenderResponse mockLiferayPortletRenderResponse =
			new MockLiferayPortletRenderResponse();

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE,
			mockLiferayPortletRenderResponse);

		AccountEntry accountEntry = null;

		if (!user.isGuestUser()) {
			accountEntry = _accountEntryLocalService.fetchPersonAccountEntry(
				user.getUserId());
		}

		mockHttpServletRequest.setAttribute(
			CommerceWebKeys.COMMERCE_CONTEXT,
			new TestCommerceContext(
				accountEntry, _commerceCurrency, _commerceChannel, user, _group,
				null));

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_group.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.getDefault());
		themeDisplay.setPermissionChecker(permissionChecker);
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSignedIn(!user.isGuestUser());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(user);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockHttpServletRequest.setParameter(
			"commerceShipmentId", String.valueOf(commerceShipmentId));

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			return _mvcRenderCommand.render(
				mockLiferayPortletRenderRequest,
				mockLiferayPortletRenderResponse);
		}
	}

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	private CommerceChannel _commerceChannel;
	private CommerceCurrency _commerceCurrency;

	@Inject
	private CommerceShipmentItemLocalService _commerceShipmentItemLocalService;

	@Inject
	private CommerceShipmentLocalService _commerceShipmentLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	private Group _group;

	@Inject(
		filter = "mvc.command.name=/commerce_shipment_content/view_commerce_shipment_items",
		type = MVCRenderCommand.class
	)
	private MVCRenderCommand _mvcRenderCommand;

	@Inject
	private UserLocalService _userLocalService;

}