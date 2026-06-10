/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.checkout.web.internal.util.test;

import com.liferay.account.configuration.AccountEntryValidatorConfiguration;
import com.liferay.account.constants.AccountEntryValidatorConstants;
import com.liferay.account.constants.AccountRoleConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.validator.AccountEntryValidator;
import com.liferay.account.validator.AccountEntryValidatorResult;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.constants.CommerceCheckoutWebKeys;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.commerce.util.CommerceCheckoutStep;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.theme.ThemeDisplayFactory;

import jakarta.servlet.http.HttpServletRequest;

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

/**
 * @author Tancredi Covioli
 */
@FeatureFlag("LPD-89850")
@RunWith(Arquillian.class)
public class OrderSummaryCommerceCheckoutStepTest {

	@ClassRule
	@Rule
	public static AggregateTestRule aggregateTestRule = new AggregateTestRule(
		new LiferayIntegrationTestRule(),
		PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		AccountEntry accountEntry =
			CommerceAccountTestUtil.addBusinessAccountEntry(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString() + "@liferay.com",
				RandomTestUtil.randomString(),
				new long[] {TestPropsValues.getUserId()}, null,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
		Role role = _roleLocalService.getRole(
			_group.getCompanyId(),
			AccountRoleConstants.ROLE_NAME_ACCOUNT_BUYER);
		_user = UserTestUtil.addUser(_group.getGroupId());

		_userGroupRoleLocalService.addUserGroupRole(
			_user.getUserId(), accountEntry.getAccountEntryGroupId(),
			role.getRoleId());

		CommerceCurrency commerceCurrency =
			CommerceCurrencyTestUtil.addCommerceCurrency(_group.getCompanyId());

		CommerceChannel commerceChannel = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), commerceCurrency.getCode());

		_commerceOrder = _commerceOrderLocalService.addCommerceOrder(
			_user.getUserId(), commerceChannel.getGroupId(),
			accountEntry.getAccountEntryId(), commerceCurrency.getCode(), 0);
	}

	@Test
	public void testShowControls() throws Exception {
		String key = RandomTestUtil.randomString();

		Bundle bundle = FrameworkUtil.getBundle(
			OrderSummaryCommerceCheckoutStepTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		TestAccountEntryValidator testAccountEntryValidator =
			new TestAccountEntryValidator(
				key, RandomTestUtil.randomString(),
				AccountEntryValidatorConstants.RESULT_FAILURE);

		ServiceRegistration<AccountEntryValidator>
			accountEntryValidatorServiceRegistration =
				bundleContext.registerService(
					AccountEntryValidator.class, testAccountEntryValidator,
					HashMapDictionaryBuilder.<String, Object>put(
						"account.entry.validator.key", key
					).build());

		try {
			Assert.assertFalse(
				_commerceCheckoutStep.showControls(
					_getMockHttpServletRequest(), null));
			_checkTestAccountEntryValidator(testAccountEntryValidator);
			_commerceOrder.setBillingAddressId(RandomTestUtil.randomLong());

			_commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
				_commerceOrder);

			testAccountEntryValidator.setResultStatus(
				AccountEntryValidatorConstants.RESULT_MANUAL);
			Assert.assertTrue(
				_commerceCheckoutStep.showControls(
					_getMockHttpServletRequest(), null));
			_checkTestAccountEntryValidator(testAccountEntryValidator);
			_commerceOrder.setBillingAddressId(RandomTestUtil.randomLong());

			_commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
				_commerceOrder);

			testAccountEntryValidator.setResultStatus(
				AccountEntryValidatorConstants.RESULT_SUCCESS);
			Assert.assertTrue(
				_commerceCheckoutStep.showControls(
					_getMockHttpServletRequest(), null));
			_checkTestAccountEntryValidator(testAccountEntryValidator);
			_commerceOrder.setBillingAddressId(RandomTestUtil.randomLong());

			_commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
				_commerceOrder);

			testAccountEntryValidator.setResultStatus(
				AccountEntryValidatorConstants.RESULT_WARNING);
			Assert.assertTrue(
				_commerceCheckoutStep.showControls(
					_getMockHttpServletRequest(), null));
			_checkTestAccountEntryValidator(testAccountEntryValidator);
		}
		finally {
			accountEntryValidatorServiceRegistration.unregister();
		}
	}

	private void _checkTestAccountEntryValidator(
		TestAccountEntryValidator testAccountEntryValidator) {

		JSONObject jsonObject = testAccountEntryValidator.getJSONObject();

		Assert.assertEquals(
			_commerceOrder.getBillingAddressId(),
			jsonObject.getLong("billingAddressId"));
		Assert.assertEquals(
			_commerceOrder.getCommerceOrderId(),
			jsonObject.getLong("commerceOrderId"));
		Assert.assertEquals(
			_commerceOrder.getShippingAddressId(),
			jsonObject.getLong("shippingAddressId"));
	}

	private HttpServletRequest _getMockHttpServletRequest() throws Exception {
		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute(
			CommerceCheckoutWebKeys.COMMERCE_ORDER, _commerceOrder);

		ThemeDisplay themeDisplay = ThemeDisplayFactory.create();

		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSignedIn(true);
		themeDisplay.setUser(_user);

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		return httpServletRequest;
	}

	@Inject(
		filter = "component.name=com.liferay.commerce.checkout.web.internal.util.OrderSummaryCommerceCheckoutStep"
	)
	private CommerceCheckoutStep _commerceCheckoutStep;

	private CommerceOrder _commerceOrder;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	private Group _group;

	@Inject
	private RoleLocalService _roleLocalService;

	private User _user;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	public class TestAccountEntryValidator implements AccountEntryValidator {

		public TestAccountEntryValidator(
			String key, String resultMessage, String resultStatus) {

			_key = key;
			_resultMessage = resultMessage;
			_resultStatus = resultStatus;
		}

		@Override
		public AccountEntryValidatorConfiguration getConfiguration(long companyId) {
			return new AccountEntryValidatorConfiguration() {

				@Override
				public int checkInterval() {
					return 0;
				}

				@Override
				public boolean enabled() {
					return true;
				}

			};
		}

		public JSONObject getJSONObject() {
			return _jsonObject;
		}

		@Override
		public String getKey(AccountEntry accountEntry, JSONObject jsonObject) {
			return _key;
		}

		public void setResultStatus(String resultStatus) {
			_resultStatus = resultStatus;
		}

		@Override
		public AccountEntryValidatorResult validate(
			AccountEntry accountEntry, JSONObject jsonObject) {

			_jsonObject = jsonObject;

			return AccountEntryValidatorResult.builder(
				_key
			).resultMessage(
				_resultMessage
			).resultStatus(
				_resultStatus
			).build();
		}

		private volatile JSONObject _jsonObject;
		private final String _key;
		private final String _resultMessage;
		private String _resultStatus;

	}
}