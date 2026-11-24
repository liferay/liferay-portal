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
	public void testGetCommerceShipmentItemsSearchContainer() throws Exception {
		SearchContainer<CommerceShipmentItem> searchContainer = _invoke(
			_commerceShipment1.getCommerceShipmentId(),
			"getCommerceShipmentItemsSearchContainer", _user1);

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

		SearchContainer<CommerceShipment> searchContainer = _invoke(
			0, "getSearchContainer", user);

		return searchContainer.getResults();
	}

	private <T> T _invoke(long commerceShipmentId, String methodName, User user)
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

		mockHttpServletRequest.setAttribute(
			MVCRenderConstants.
				PORTLET_CONTEXT_OVERRIDE_REQUEST_ATTIBUTE_NAME_PREFIX +
					"/view.jsp",
			new MockLiferayPortletContext("/view.jsp"));

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

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		portletDisplay.setId(CommercePortletKeys.COMMERCE_SHIPMENT_CONTENT);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockHttpServletRequest.setParameter(
			"commerceShipmentId", String.valueOf(commerceShipmentId));

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			_portlet.render(
				mockLiferayPortletRenderRequest,
				mockLiferayPortletRenderResponse);

			return ReflectionTestUtil.invoke(
				mockLiferayPortletRenderRequest.getAttribute(
					WebKeys.PORTLET_DISPLAY_CONTEXT),
				methodName, new Class<?>[0]);
		}
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

	@Inject(
		filter = "jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_SHIPMENT_CONTENT
	)
	private Portlet _portlet;

	private User _user1;
	private User _user2;

	@Inject
	private UserLocalService _userLocalService;

}