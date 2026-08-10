/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.subscription.web.internal.portlet.action;

import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.CommerceSubscriptionEntry;
import com.liferay.commerce.service.CommerceSubscriptionEntryLocalService;
import com.liferay.commerce.subscription.CommerceSubscriptionEntryActionHelper;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.test.portlet.MockActionRequest;
import com.liferay.portal.kernel.test.portlet.MockActionResponse;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Stefano Motta
 */
public class EditCommerceSubscriptionContentMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		PortalUtil portalUtil = new PortalUtil();

		Portal portal = Mockito.mock(Portal.class);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		Mockito.when(
			portal.getHttpServletRequest(Mockito.any())
		).thenReturn(
			mockHttpServletRequest
		);

		Mockito.when(
			portal.getLiferayPortletRequest(Mockito.any())
		).thenReturn(
			Mockito.mock(LiferayPortletRequest.class)
		);

		Mockito.when(
			portal.getOriginalServletRequest(Mockito.any())
		).thenReturn(
			mockHttpServletRequest
		);

		portalUtil.setPortal(portal);
	}

	@Test
	public void testDoProcessAction() throws Exception {
		long commerceSubscriptionEntryId = RandomTestUtil.randomLong();
		long companyId = RandomTestUtil.randomLong();
		long userId = RandomTestUtil.randomLong();

		_setUpCommerceSubscriptionEntry(
			commerceSubscriptionEntryId, companyId, userId);

		ThemeDisplay themeDisplay = _getThemeDisplay(companyId, userId);

		Mockito.when(
			themeDisplay.isSignedIn()
		).thenReturn(
			false
		);

		MockActionRequest mockActionRequest = _getMockActionRequest(
			"suspend", themeDisplay);

		SessionErrors.clear(mockActionRequest);

		MockActionResponse mockActionResponse = new MockActionResponse();

		_editCommerceSubscriptionContentMVCActionCommand.doProcessAction(
			mockActionRequest, mockActionResponse);

		Assert.assertTrue(
			SessionErrors.contains(
				mockActionRequest, PrincipalException.class.getName()));
		Assert.assertEquals(
			"/error.jsp", mockActionResponse.getRenderParameter("mvcPath"));

		Mockito.verify(
			_commerceSubscriptionEntryActionHelper, Mockito.never()
		).suspendCommerceSubscriptionEntry(
			commerceSubscriptionEntryId
		);

		commerceSubscriptionEntryId = RandomTestUtil.randomLong();

		_setUpCommerceSubscriptionEntry(
			commerceSubscriptionEntryId, companyId,
			RandomTestUtil.randomLong());

		mockActionRequest = _getMockActionRequest(
			"suspend",
			_getThemeDisplay(companyId + 1, RandomTestUtil.randomLong()));

		SessionErrors.clear(mockActionRequest);

		mockActionResponse = new MockActionResponse();

		_editCommerceSubscriptionContentMVCActionCommand.doProcessAction(
			mockActionRequest, mockActionResponse);

		Assert.assertTrue(
			SessionErrors.contains(
				mockActionRequest, PrincipalException.class.getName()));
		Assert.assertEquals(
			"/error.jsp", mockActionResponse.getRenderParameter("mvcPath"));

		Mockito.verify(
			_commerceSubscriptionEntryActionHelper, Mockito.never()
		).suspendCommerceSubscriptionEntry(
			commerceSubscriptionEntryId
		);

		commerceSubscriptionEntryId = RandomTestUtil.randomLong();
		userId = RandomTestUtil.randomLong();

		_setUpCommerceSubscriptionEntry(
			commerceSubscriptionEntryId, companyId, userId);

		mockActionRequest = _getMockActionRequest(
			"suspend", _getThemeDisplay(companyId, userId + 1));

		SessionErrors.clear(mockActionRequest);

		mockActionResponse = new MockActionResponse();

		_editCommerceSubscriptionContentMVCActionCommand.doProcessAction(
			mockActionRequest, mockActionResponse);

		Assert.assertTrue(
			SessionErrors.contains(
				mockActionRequest, PrincipalException.class.getName()));
		Assert.assertEquals(
			"/error.jsp", mockActionResponse.getRenderParameter("mvcPath"));

		Mockito.verify(
			_commerceSubscriptionEntryActionHelper, Mockito.never()
		).suspendCommerceSubscriptionEntry(
			commerceSubscriptionEntryId
		);

		commerceSubscriptionEntryId = RandomTestUtil.randomLong();
		userId = RandomTestUtil.randomLong();

		_setUpCommerceSubscriptionEntry(
			commerceSubscriptionEntryId, companyId, userId);

		mockActionRequest = _getMockActionRequest(
			"suspend", _getThemeDisplay(companyId, userId));

		SessionErrors.clear(mockActionRequest);

		_editCommerceSubscriptionContentMVCActionCommand.doProcessAction(
			mockActionRequest, new MockActionResponse());

		Assert.assertFalse(
			SessionErrors.contains(
				mockActionRequest, PrincipalException.class.getName()));

		Mockito.verify(
			_commerceSubscriptionEntryActionHelper, Mockito.times(1)
		).suspendCommerceSubscriptionEntry(
			commerceSubscriptionEntryId
		);

		commerceSubscriptionEntryId = RandomTestUtil.randomLong();
		userId = RandomTestUtil.randomLong();

		_setUpCommerceSubscriptionEntry(
			commerceSubscriptionEntryId, companyId, userId);

		Mockito.when(
			_portletResourcePermission.contains(
				Mockito.any(PermissionChecker.class), Mockito.anyLong(),
				Mockito.anyString())
		).thenReturn(
			true
		);

		mockActionRequest = _getMockActionRequest(
			"suspend", _getThemeDisplay(companyId, userId + 1));

		SessionErrors.clear(mockActionRequest);

		_editCommerceSubscriptionContentMVCActionCommand.doProcessAction(
			mockActionRequest, new MockActionResponse());

		Assert.assertFalse(
			SessionErrors.contains(
				mockActionRequest, PrincipalException.class.getName()));

		Mockito.verify(
			_commerceSubscriptionEntryActionHelper, Mockito.times(1)
		).suspendCommerceSubscriptionEntry(
			commerceSubscriptionEntryId
		);

		commerceSubscriptionEntryId = RandomTestUtil.randomLong();
		userId = RandomTestUtil.randomLong();

		CommerceSubscriptionEntry commerceSubscriptionEntry =
			_setUpCommerceSubscriptionEntry(
				commerceSubscriptionEntryId, companyId, userId);

		long commerceOrderId = RandomTestUtil.randomLong();

		CommerceOrderItem commerceOrderItem = Mockito.mock(
			CommerceOrderItem.class);

		Mockito.when(
			commerceOrderItem.getCommerceOrderId()
		).thenReturn(
			commerceOrderId
		);

		Mockito.when(
			commerceSubscriptionEntry.fetchCommerceOrderItem()
		).thenReturn(
			commerceOrderItem
		);

		Mockito.when(
			_commerceOrderModelResourcePermission.contains(
				Mockito.any(PermissionChecker.class),
				Mockito.eq(commerceOrderId), Mockito.eq(ActionKeys.UPDATE))
		).thenReturn(
			true
		);

		mockActionRequest = _getMockActionRequest(
			"suspend", _getThemeDisplay(companyId, userId + 1));

		SessionErrors.clear(mockActionRequest);

		_editCommerceSubscriptionContentMVCActionCommand.doProcessAction(
			mockActionRequest, new MockActionResponse());

		Assert.assertFalse(
			SessionErrors.contains(
				mockActionRequest, PrincipalException.class.getName()));

		Mockito.verify(
			_commerceSubscriptionEntryActionHelper, Mockito.times(1)
		).suspendCommerceSubscriptionEntry(
			commerceSubscriptionEntryId
		);
	}

	private MockActionRequest _getMockActionRequest(
		String cmd, ThemeDisplay themeDisplay) {

		MockActionRequest mockActionRequest = new MockActionRequest();

		mockActionRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);
		mockActionRequest.setParameter(Constants.CMD, cmd);
		mockActionRequest.setParameter(
			"commerceSubscriptionEntryId",
			String.valueOf(RandomTestUtil.randomLong()));

		return mockActionRequest;
	}

	private ThemeDisplay _getThemeDisplay(long companyId, long userId) {
		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getCompanyId()
		).thenReturn(
			companyId
		);

		Mockito.when(
			themeDisplay.getPermissionChecker()
		).thenReturn(
			Mockito.mock(PermissionChecker.class)
		);

		Mockito.when(
			themeDisplay.getUserId()
		).thenReturn(
			userId
		);

		Mockito.when(
			themeDisplay.isSignedIn()
		).thenReturn(
			true
		);

		return themeDisplay;
	}

	private CommerceSubscriptionEntry _setUpCommerceSubscriptionEntry(
			long commerceSubscriptionEntryId, long companyId, long userId)
		throws Exception {

		CommerceSubscriptionEntry commerceSubscriptionEntry = Mockito.mock(
			CommerceSubscriptionEntry.class);

		Mockito.when(
			commerceSubscriptionEntry.getCommerceSubscriptionEntryId()
		).thenReturn(
			commerceSubscriptionEntryId
		);

		Mockito.when(
			commerceSubscriptionEntry.getCompanyId()
		).thenReturn(
			companyId
		);

		Mockito.when(
			commerceSubscriptionEntry.getGroupId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			commerceSubscriptionEntry.getUserId()
		).thenReturn(
			userId
		);

		Mockito.when(
			_commerceSubscriptionEntryLocalService.getCommerceSubscriptionEntry(
				Mockito.anyLong())
		).thenReturn(
			commerceSubscriptionEntry
		);

		Mockito.when(
			_portletResourcePermission.contains(
				Mockito.any(PermissionChecker.class), Mockito.anyLong(),
				Mockito.anyString())
		).thenReturn(
			false
		);

		return commerceSubscriptionEntry;
	}

	@Mock
	private ModelResourcePermission<CommerceOrder>
		_commerceOrderModelResourcePermission;

	@Mock
	private CommerceSubscriptionEntryActionHelper
		_commerceSubscriptionEntryActionHelper;

	@Mock
	private CommerceSubscriptionEntryLocalService
		_commerceSubscriptionEntryLocalService;

	@InjectMocks
	private final EditCommerceSubscriptionContentMVCActionCommand
		_editCommerceSubscriptionContentMVCActionCommand =
			new EditCommerceSubscriptionContentMVCActionCommand();

	@Mock
	private PortletResourcePermission _portletResourcePermission;

}