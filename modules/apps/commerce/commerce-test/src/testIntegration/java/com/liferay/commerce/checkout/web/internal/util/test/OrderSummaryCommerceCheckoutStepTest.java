/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.checkout.web.internal.util.test;

import com.liferay.account.constants.AccountEntryValidatorConstants;
import com.liferay.account.constants.AccountRoleConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.validator.AccountEntryValidator;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.constants.CommerceAccountEntryValidationConstants;
import com.liferay.commerce.constants.CommerceCheckoutWebKeys;
import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.commerce.test.util.validator.TestAccountEntryValidator;
import com.liferay.commerce.util.CommerceCheckoutStep;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.settings.FallbackKeysSettingsUtil;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.settings.ModifiableSettings;
import com.liferay.portal.kernel.settings.Settings;
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

		Bundle bundle = FrameworkUtil.getBundle(
			OrderSummaryCommerceCheckoutStepTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		String key = RandomTestUtil.randomString();

		_testAccountEntryValidator = new TestAccountEntryValidator(
			key, RandomTestUtil.randomString(),
			AccountEntryValidatorConstants.RESULT_FAILURE);

		_accountEntryValidatorServiceRegistration =
			bundleContext.registerService(
				AccountEntryValidator.class, _testAccountEntryValidator,
				HashMapDictionaryBuilder.<String, Object>put(
					"account.entry.validator.key", key
				).build());
	}

	@After
	public void tearDown() throws Exception {
		if (_accountEntryValidatorServiceRegistration != null) {
			_accountEntryValidatorServiceRegistration.unregister();
		}
	}

	@Test
	public void testShowControls() throws Exception {
		_setAccountEntryValidationMode(
			CommerceAccountEntryValidationConstants.VALIDATION_MODE_DISABLED);

		Assert.assertTrue(
			_commerceCheckoutStep.showControls(
				_getMockHttpServletRequest(), null));

		Assert.assertNull(_testAccountEntryValidator.getJSONObject());

		_setAccountEntryValidationMode(
			CommerceAccountEntryValidationConstants.VALIDATION_MODE_ALLOW_ALL);

		Assert.assertTrue(
			_commerceCheckoutStep.showControls(
				_getMockHttpServletRequest(), null));

		JSONObject jsonObject = _testAccountEntryValidator.getJSONObject();

		Assert.assertEquals(
			_commerceOrder.getBillingAddressId(),
			jsonObject.getLong("billingAddressId"));
		Assert.assertEquals(
			_commerceOrder.getCommerceOrderId(),
			jsonObject.getLong("commerceOrderId"));
		Assert.assertEquals(
			_commerceOrder.getShippingAddressId(),
			jsonObject.getLong("shippingAddressId"));

		_commerceOrder.setBillingAddressId(RandomTestUtil.randomLong());

		_commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
			_commerceOrder);

		_setAccountEntryValidationMode(
			CommerceAccountEntryValidationConstants.
				VALIDATION_MODE_ALLOW_TECHNICAL_FAILURES);

		Assert.assertFalse(
			_commerceCheckoutStep.showControls(
				_getMockHttpServletRequest(), null));

		_commerceOrder.setBillingAddressId(RandomTestUtil.randomLong());

		_commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
			_commerceOrder);

		_testAccountEntryValidator.setResultStatus(
			AccountEntryValidatorConstants.RESULT_WARNING);

		Assert.assertTrue(
			_commerceCheckoutStep.showControls(
				_getMockHttpServletRequest(), null));

		_commerceOrder.setBillingAddressId(RandomTestUtil.randomLong());

		_commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
			_commerceOrder);

		_setAccountEntryValidationMode(
			CommerceAccountEntryValidationConstants.
				VALIDATION_MODE_ALLOW_SUCCESSES_ONLY);

		Assert.assertFalse(
			_commerceCheckoutStep.showControls(
				_getMockHttpServletRequest(), null));

		_commerceOrder.setBillingAddressId(RandomTestUtil.randomLong());

		_commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
			_commerceOrder);

		_testAccountEntryValidator.setResultStatus(
			AccountEntryValidatorConstants.RESULT_MANUAL);

		Assert.assertTrue(
			_commerceCheckoutStep.showControls(
				_getMockHttpServletRequest(), null));

		_commerceOrder.setBillingAddressId(RandomTestUtil.randomLong());

		_commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
			_commerceOrder);

		_testAccountEntryValidator.setResultStatus(
			AccountEntryValidatorConstants.RESULT_SUCCESS);

		Assert.assertTrue(
			_commerceCheckoutStep.showControls(
				_getMockHttpServletRequest(), null));

		_commerceOrder.setBillingAddressId(RandomTestUtil.randomLong());

		_commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
			_commerceOrder);

		int validateCount = _testAccountEntryValidator.getValidateCount();

		HttpServletRequest httpServletRequest = _getMockHttpServletRequest();

		Assert.assertTrue(
			_commerceCheckoutStep.showControls(httpServletRequest, null));
		Assert.assertTrue(
			_commerceCheckoutStep.showControls(httpServletRequest, null));

		Assert.assertEquals(
			validateCount + 1, _testAccountEntryValidator.getValidateCount());

		_commerceOrder.setBillingAddressId(RandomTestUtil.randomLong());

		_commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
			_commerceOrder);

		_testAccountEntryValidator.setResultStatus(
			AccountEntryValidatorConstants.RESULT_FAILURE);

		validateCount = _testAccountEntryValidator.getValidateCount();

		httpServletRequest = _getMockHttpServletRequest();

		Assert.assertFalse(
			_commerceCheckoutStep.showControls(httpServletRequest, null));
		Assert.assertFalse(
			_commerceCheckoutStep.showControls(httpServletRequest, null));

		Assert.assertEquals(
			validateCount + 1, _testAccountEntryValidator.getValidateCount());
	}

	private HttpServletRequest _getMockHttpServletRequest() {
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

	private void _setAccountEntryValidationMode(String validationMode)
		throws Exception {

		Settings settings = FallbackKeysSettingsUtil.getSettings(
			new GroupServiceSettingsLocator(
				_commerceOrder.getGroupId(),
				CommerceConstants.
					SERVICE_NAME_COMMERCE_ACCOUNT_ENTRY_VALIDATION));

		ModifiableSettings modifiableSettings =
			settings.getModifiableSettings();

		modifiableSettings.setValue("validationMode", validationMode);

		modifiableSettings.store();
	}

	private ServiceRegistration<AccountEntryValidator>
		_accountEntryValidatorServiceRegistration;

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

	private TestAccountEntryValidator _testAccountEntryValidator;
	private User _user;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

}