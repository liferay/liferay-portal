/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipment.content.web.internal.display.context.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.CommerceShipment;
import com.liferay.commerce.model.CommerceShipmentItem;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.service.CommerceShipmentItemLocalService;
import com.liferay.commerce.service.CommerceShipmentLocalService;
import com.liferay.commerce.test.util.CommerceInventoryTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.commerce.test.util.context.TestCommerceContext;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portlet.test.MockLiferayPortletContext;

import jakarta.portlet.Portlet;

import java.math.BigDecimal;

import java.util.Collections;
import java.util.List;

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
public class CommerceShipmentContentDisplayContextTest {

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

		_user1 = UserTestUtil.addUser();

		_commerceShipment1 = _addCommerceShipment(_commerceChannel, _user1);

		_user2 = UserTestUtil.addUser();

		_commerceShipment2 = _addCommerceShipment(_commerceChannel, _user2);
	}

	@Test
	public void testGetCommerceShipment() throws Exception {
		Assert.assertEquals(
			"/error.jsp",
			_render(
				_commerceShipment1.getCommerceShipmentId(),
				_userLocalService.getGuestUser(_group.getCompanyId())));

		Assert.assertEquals(
			"/view_commerce_shipment_items.jsp",
			_render(_commerceShipment1.getCommerceShipmentId(), _user1));

		Assert.assertEquals(
			_commerceShipment1,
			ReflectionTestUtil.invoke(
				_mockLiferayPortletRenderRequest.getAttribute(
					WebKeys.PORTLET_DISPLAY_CONTEXT),
				"getCommerceShipment", new Class<?>[0]));

		Assert.assertEquals(
			"/error.jsp",
			_render(_commerceShipment2.getCommerceShipmentId(), _user1));

		Group group = GroupTestUtil.addGroup();

		CommerceShipment commerceShipment = _addCommerceShipment(
			CommerceTestUtil.addCommerceChannel(
				group.getGroupId(), _commerceCurrency.getCode()),
			_user1);

		Assert.assertEquals(
			"/error.jsp",
			_render(commerceShipment.getCommerceShipmentId(), _user1));
	}

	@Test
	public void testGetCommerceShipmentItemsSearchContainer() throws Exception {
		_render(_commerceShipment1.getCommerceShipmentId(), _user1);

		SearchContainer<CommerceShipmentItem> searchContainer =
			ReflectionTestUtil.invoke(
				_mockLiferayPortletRenderRequest.getAttribute(
					WebKeys.PORTLET_DISPLAY_CONTEXT),
				"getCommerceShipmentItemsSearchContainer", new Class<?>[0]);

		List<CommerceShipmentItem> commerceShipmentItems =
			searchContainer.getResults();

		Assert.assertEquals(
			commerceShipmentItems.toString(), 1, commerceShipmentItems.size());

		CommerceShipmentItem commerceShipmentItem = commerceShipmentItems.get(
			0);

		Assert.assertEquals(
			_commerceShipment1.getCommerceShipmentId(),
			commerceShipmentItem.getCommerceShipmentId());
	}

	@Test
	public void testGetSearchContainer() throws Exception {
		Assert.assertEquals(
			Collections.emptyList(),
			_getSearchContainerResults(
				_userLocalService.getGuestUser(_group.getCompanyId())));
		Assert.assertEquals(
			Collections.singletonList(_commerceShipment1),
			_getSearchContainerResults(_user1));
		Assert.assertEquals(
			Collections.singletonList(_commerceShipment2),
			_getSearchContainerResults(_user2));
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

		return _commerceShipmentLocalService.getCommerceShipment(
			commerceShipment.getCommerceShipmentId());
	}

	private List<CommerceShipment> _getSearchContainerResults(User user)
		throws Exception {

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		_setUpMockLiferayPortletRenderRequest(permissionChecker, user);

		_mockHttpServletRequest.setAttribute(
			MVCRenderConstants.
				PORTLET_CONTEXT_OVERRIDE_REQUEST_ATTIBUTE_NAME_PREFIX +
					"/view.jsp",
			new MockLiferayPortletContext("/view.jsp"));

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			_portlet.render(
				_mockLiferayPortletRenderRequest,
				_mockLiferayPortletRenderResponse);
		}

		SearchContainer<CommerceShipment> searchContainer =
			ReflectionTestUtil.invoke(
				_mockLiferayPortletRenderRequest.getAttribute(
					WebKeys.PORTLET_DISPLAY_CONTEXT),
				"getSearchContainer", new Class<?>[0]);

		return searchContainer.getResults();
	}

	private String _render(long commerceShipmentId, User user)
		throws Exception {

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		_setUpMockLiferayPortletRenderRequest(permissionChecker, user);

		_mockHttpServletRequest.setParameter(
			"commerceShipmentId", String.valueOf(commerceShipmentId));

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			return _mvcRenderCommand.render(
				_mockLiferayPortletRenderRequest,
				_mockLiferayPortletRenderResponse);
		}
	}

	private void _setUpMockLiferayPortletRenderRequest(
			PermissionChecker permissionChecker, User user)
		throws Exception {

		_mockHttpServletRequest = new MockHttpServletRequest();

		_mockLiferayPortletRenderRequest = new MockLiferayPortletRenderRequest(
			_mockHttpServletRequest);

		_mockLiferayPortletRenderResponse =
			new MockLiferayPortletRenderResponse();

		_mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_REQUEST,
			_mockLiferayPortletRenderRequest);
		_mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE,
			_mockLiferayPortletRenderResponse);

		AccountEntry accountEntry = null;

		if (!user.isGuestUser()) {
			accountEntry = _accountEntryLocalService.fetchPersonAccountEntry(
				user.getUserId());
		}

		_mockHttpServletRequest.setAttribute(
			CommerceWebKeys.COMMERCE_CONTEXT,
			new TestCommerceContext(
				accountEntry, _commerceCurrency, _commerceChannel, user, _group,
				null));

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_group.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.getDefault());
		themeDisplay.setPermissionChecker(permissionChecker);
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSignedIn(!user.isGuestUser());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(user);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		portletDisplay.setId(CommercePortletKeys.COMMERCE_SHIPMENT_CONTENT);

		_mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);
	}

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	private CommerceChannel _commerceChannel;
	private CommerceCurrency _commerceCurrency;
	private CommerceShipment _commerceShipment1;
	private CommerceShipment _commerceShipment2;

	@Inject
	private CommerceShipmentItemLocalService _commerceShipmentItemLocalService;

	@Inject
	private CommerceShipmentLocalService _commerceShipmentLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	private Group _group;
	private MockHttpServletRequest _mockHttpServletRequest;
	private MockLiferayPortletRenderRequest _mockLiferayPortletRenderRequest;
	private MockLiferayPortletRenderResponse _mockLiferayPortletRenderResponse;

	@Inject(
		filter = "mvc.command.name=/commerce_shipment_content/view_commerce_shipment_items",
		type = MVCRenderCommand.class
	)
	private MVCRenderCommand _mvcRenderCommand;

	@Inject(
		filter = "jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_SHIPMENT_CONTENT
	)
	private Portlet _portlet;

	private User _user1;
	private User _user2;

	@Inject
	private UserLocalService _userLocalService;

}