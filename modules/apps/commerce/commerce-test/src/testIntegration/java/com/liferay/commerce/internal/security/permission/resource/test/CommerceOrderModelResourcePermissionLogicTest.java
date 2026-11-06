/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.security.permission.resource.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.constants.CommerceOrderActionKeys;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.order.CommerceOrderHttpHelper;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

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
 * @author Stefano Motta
 */
@RunWith(Arquillian.class)
public class CommerceOrderModelResourcePermissionLogicTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			_group.getCompanyId());

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), _commerceCurrency.getCode());

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), TestPropsValues.getUserId());
	}

	@Test
	public void testContains() throws Exception {
		AccountEntry accountEntry1 = _addAccountEntry(
			AccountConstants.ACCOUNT_ENTRY_TYPE_GUEST);
		User guestUser = _userLocalService.getGuestUser(
			TestPropsValues.getCompanyId());

		CommerceOrder commerceOrder1 =
			_commerceOrderLocalService.addCommerceOrder(
				guestUser.getUserId(), _commerceChannel.getGroupId(),
				accountEntry1.getAccountEntryId(), _commerceCurrency.getCode(),
				0);

		Assert.assertTrue(commerceOrder1.isGuestOrder());
		Assert.assertTrue(
			_contains(
				commerceOrder1,
				_createMockHttpServletRequest(commerceOrder1, null, null),
				TestPropsValues.getUser(), ActionKeys.VIEW));
		Assert.assertEquals(
			Collections.emptyList(),
			_filterActionIds(commerceOrder1, null, null, guestUser));
		Assert.assertFalse(
			_contains(
				commerceOrder1,
				_createMockHttpServletRequest(
					commerceOrder1, RandomTestUtil.randomString(), null),
				guestUser, ActionKeys.VIEW));
		Assert.assertTrue(
			_contains(
				commerceOrder1,
				_createMockHttpServletRequest(
					commerceOrder1, commerceOrder1.getUuid(), null),
				guestUser, ActionKeys.VIEW));
		Assert.assertFalse(
			_contains(
				commerceOrder1,
				_createMockHttpServletRequest(
					commerceOrder1, null, RandomTestUtil.randomString()),
				guestUser, ActionKeys.VIEW));
		Assert.assertTrue(
			_contains(
				commerceOrder1,
				_createMockHttpServletRequest(
					commerceOrder1, null, commerceOrder1.getUuid()),
				guestUser, ActionKeys.VIEW));
		Assert.assertTrue(
			_contains(
				commerceOrder1,
				_createMockHttpServletRequest(
					commerceOrder1, commerceOrder1.getUuid(),
					commerceOrder1.getUuid()),
				guestUser, ActionKeys.VIEW));

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		HttpSession httpSession = mockHttpServletRequest.getSession();

		httpSession.setAttribute(
			_commerceOrderHttpHelper.getCookieName(commerceOrder1.getGroupId()),
			commerceOrder1.getUuid());

		Assert.assertTrue(
			_contains(
				commerceOrder1, mockHttpServletRequest, guestUser,
				ActionKeys.VIEW));
		Assert.assertTrue(
			_contains(commerceOrder1, null, guestUser, ActionKeys.VIEW));

		User user = UserTestUtil.addUser();

		Assert.assertFalse(
			_contains(
				commerceOrder1,
				_createMockHttpServletRequest(commerceOrder1, null, null), user,
				ActionKeys.VIEW));
		Assert.assertFalse(
			_contains(
				commerceOrder1,
				_createMockHttpServletRequest(
					commerceOrder1, RandomTestUtil.randomString(), null),
				user, ActionKeys.VIEW));
		Assert.assertTrue(
			_contains(
				commerceOrder1,
				_createMockHttpServletRequest(
					commerceOrder1, commerceOrder1.getUuid(), null),
				user, ActionKeys.VIEW));
		Assert.assertFalse(
			_contains(
				commerceOrder1,
				_createMockHttpServletRequest(
					commerceOrder1, null, RandomTestUtil.randomString()),
				user, ActionKeys.VIEW));
		Assert.assertTrue(
			_contains(
				commerceOrder1,
				_createMockHttpServletRequest(
					commerceOrder1, null, commerceOrder1.getUuid()),
				user, ActionKeys.VIEW));
		Assert.assertTrue(
			_contains(
				commerceOrder1,
				_createMockHttpServletRequest(
					commerceOrder1, commerceOrder1.getUuid(),
					commerceOrder1.getUuid()),
				user, ActionKeys.VIEW));

		CommerceOrder commerceOrder2 =
			_commerceOrderLocalService.addCommerceOrder(
				guestUser.getUserId(), _commerceChannel.getGroupId(),
				AccountConstants.ACCOUNT_ENTRY_ID_GUEST,
				_commerceCurrency.getCode(), 0);

		Assert.assertTrue(commerceOrder2.isGuestOrder());
		Assert.assertEquals(
			Collections.emptyList(),
			_filterActionIds(commerceOrder2, null, null, guestUser));

		AccountEntry accountEntry2 = _addAccountEntry(
			AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS);

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			accountEntry2.getAccountEntryId(), user.getUserId());

		CommerceOrder commerceOrder3 =
			_commerceOrderLocalService.addCommerceOrder(
				user.getUserId(), _commerceChannel.getGroupId(),
				accountEntry2.getAccountEntryId(), _commerceCurrency.getCode(),
				0);

		Assert.assertFalse(commerceOrder3.isGuestOrder());
		Assert.assertFalse(
			_contains(
				commerceOrder3,
				_createMockHttpServletRequest(commerceOrder3, null, null),
				guestUser, ActionKeys.VIEW));
		Assert.assertTrue(
			_contains(
				commerceOrder3,
				_createMockHttpServletRequest(commerceOrder3, null, null), user,
				ActionKeys.VIEW));

		AccountEntry accountEntry3 = _addAccountEntry(
			AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS);

		CommerceOrder commerceOrder4 =
			_commerceOrderLocalService.addCommerceOrder(
				TestPropsValues.getUserId(), _commerceChannel.getGroupId(),
				accountEntry3.getAccountEntryId(), _commerceCurrency.getCode(),
				0);

		Assert.assertFalse(commerceOrder4.isGuestOrder());
		Assert.assertFalse(
			_contains(
				commerceOrder4,
				_createMockHttpServletRequest(commerceOrder4, null, null), user,
				ActionKeys.VIEW));
	}

	private AccountEntry _addAccountEntry(String type) throws Exception {
		return _accountEntryLocalService.addAccountEntry(
			null, TestPropsValues.getUserId(),
			AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
			RandomTestUtil.randomString(), null, null,
			RandomTestUtil.randomString() + "@liferay.com", null, null, type,
			WorkflowConstants.STATUS_APPROVED, _serviceContext);
	}

	private boolean _contains(
			CommerceOrder commerceOrder, HttpServletRequest httpServletRequest,
			User user, String actionId)
		throws Exception {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setRequest(httpServletRequest);

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		try {
			PermissionChecker permissionChecker =
				PermissionCheckerFactoryUtil.create(user);

			try (ContextUserReplace contextUserReplace = new ContextUserReplace(
					user, permissionChecker)) {

				return _commerceOrderModelResourcePermission.contains(
					permissionChecker, commerceOrder, actionId);
			}
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	private MockHttpServletRequest _createMockHttpServletRequest(
		CommerceOrder commerceOrder, String cookieCommerceOrderUUID,
		String headerCommerceOrderUUID) {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		if (Validator.isNotNull(cookieCommerceOrderUUID)) {
			mockHttpServletRequest.setCookies(
				new Cookie(
					_commerceOrderHttpHelper.getCookieName(
						commerceOrder.getGroupId()),
					cookieCommerceOrderUUID + StringPool.PIPE +
						RandomTestUtil.randomString()));
		}

		if (Validator.isNotNull(headerCommerceOrderUUID)) {
			mockHttpServletRequest.addHeader(
				"Commerce-Cart-UUID", headerCommerceOrderUUID);
		}

		return mockHttpServletRequest;
	}

	private List<String> _filterActionIds(
			CommerceOrder commerceOrder, String cookieCommerceOrderUUID,
			String headerCommerceOrderUUID, User user)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			_createMockHttpServletRequest(
				commerceOrder, cookieCommerceOrderUUID,
				headerCommerceOrderUUID);

		return TransformUtil.transform(
			ListUtil.fromArray(
				ActionKeys.DELETE, ActionKeys.UPDATE, ActionKeys.VIEW,
				CommerceOrderActionKeys.CHECKOUT_COMMERCE_ORDER,
				CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_DELIVERY_TERMS,
				CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_NOTES,
				CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_PAYMENT_METHODS,
				CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_PAYMENT_TERMS,
				CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_SHIPPING_OPTIONS,
				CommerceOrderActionKeys.VIEW_BILLING_ADDRESS),
			actionId -> {
				if (_contains(
						commerceOrder, mockHttpServletRequest, user,
						actionId)) {

					return actionId;
				}

				return null;
			});
	}

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	private CommerceChannel _commerceChannel;
	private CommerceCurrency _commerceCurrency;

	@Inject
	private CommerceOrderHttpHelper _commerceOrderHttpHelper;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Inject(
		filter = "model.class.name=com.liferay.commerce.model.CommerceOrder"
	)
	private ModelResourcePermission<CommerceOrder>
		_commerceOrderModelResourcePermission;

	private Group _group;
	private ServiceContext _serviceContext;

	@Inject
	private UserLocalService _userLocalService;

}