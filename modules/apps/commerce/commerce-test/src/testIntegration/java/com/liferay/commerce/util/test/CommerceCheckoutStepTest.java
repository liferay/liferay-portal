/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.util.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.constants.CommerceCheckoutWebKeys;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContextFactory;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.util.CommerceCheckoutStep;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.CountryLocalService;
import com.liferay.portal.kernel.service.RegionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.ActionRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

import org.frutilla.FrutillaRule;

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
public class CommerceCheckoutStepTest {

	@ClassRule
	@Rule
	public static AggregateTestRule aggregateTestRule = new AggregateTestRule(
		new LiferayIntegrationTestRule(),
		PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());
		_group = GroupTestUtil.addGroup();

		_user = UserTestUtil.addUser();

		_commerceCurrency = _commerceCurrencyLocalService.addCommerceCurrency(
			null, _user.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomString(), BigDecimal.ONE,
			RandomTestUtil.randomLocaleStringMap(), 2, 2, "HALF_EVEN", false,
			RandomTestUtil.nextDouble(), true);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getCompanyId(), _group.getGroupId(), _user.getUserId());

		_commerceChannel = _commerceChannelLocalService.addCommerceChannel(
			null, AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
			_group.getGroupId(), "Test Channel",
			CommerceChannelConstants.CHANNEL_TYPE_SITE, null,
			_commerceCurrency.getCode(), _serviceContext);

		_country = _countryLocalService.addCountry(
			"AA", "AAA", RandomTestUtil.randomBoolean(),
			RandomTestUtil.randomBoolean(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomDouble(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.randomBoolean(), RandomTestUtil.randomBoolean(),
			_serviceContext);

		_region = _regionLocalService.addRegion(
			_country.getCountryId(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.randomString(), RandomTestUtil.nextDouble(),
			RandomTestUtil.randomString(), _serviceContext);
	}

	@Test
	public void testGuestCheckoutWithExistingAccountEmailAddress()
		throws Exception {

		frutillaRule.scenario(
			"A guest user using the same email address of an existing " +
				"account at order checkout must create a new account"
		).given(
			"A guest user checking out the order"
		).when(
			"He uses the same email address of an existing account"
		).then(
			"A different account is created"
		);

		String emailAddress = "buyer@liferay.com";

		AccountEntry accountEntry1 = _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, _serviceContext.getUserId(),
			AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
			RandomTestUtil.randomString(), null, null, emailAddress, null, null,
			AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
			WorkflowConstants.STATUS_APPROVED, _serviceContext);

		Assert.assertEquals(emailAddress, accountEntry1.getEmailAddress());

		CommerceOrder commerceOrder1 =
			_commerceOrderLocalService.addCommerceOrder(
				_user.getUserId(), _commerceChannel.getGroupId(),
				accountEntry1.getAccountEntryId(), _commerceCurrency.getCode(),
				0);

		Assert.assertFalse(commerceOrder1.isGuestOrder());

		CommerceOrder commerceOrder2 =
			_commerceOrderLocalService.addCommerceOrder(
				_user.getUserId(), _commerceChannel.getGroupId(), -1,
				_commerceCurrency.getCode(), 0);

		Assert.assertTrue(commerceOrder2.isGuestOrder());

		ActionRequest actionRequest = _processAction(
			commerceOrder2, emailAddress);

		commerceOrder2 = (CommerceOrder)actionRequest.getAttribute(
			CommerceCheckoutWebKeys.COMMERCE_ORDER);

		Assert.assertTrue(commerceOrder2.getCommerceAccountId() != -1);
		Assert.assertTrue(
			commerceOrder1.getCommerceAccountId() !=
				commerceOrder2.getCommerceAccountId());
		Assert.assertTrue(
			commerceOrder1.getCommerceOrderId() !=
				commerceOrder2.getCommerceOrderId());

		AccountEntry accountEntry2 = commerceOrder2.getAccountEntry();

		Assert.assertEquals(emailAddress, accountEntry2.getEmailAddress());

		Assert.assertEquals(
			1,
			_commerceOrderLocalService.
				getCommerceOrdersCountByCommerceAccountId(
					accountEntry1.getAccountEntryId()));
		Assert.assertEquals(
			1,
			_commerceOrderLocalService.
				getCommerceOrdersCountByCommerceAccountId(
					accountEntry2.getAccountEntryId()));
	}

	@Test
	public void testGuestCheckoutWithSameEmailAddress() throws Exception {
		frutillaRule.scenario(
			"Two guest users using the same email address at order checkout " +
				"must create two different accounts"
		).given(
			"Two guest users checking out the orders"
		).when(
			"They use the same email address"
		).then(
			"Two different accounts are created"
		);

		CommerceOrder commerceOrder1 =
			_commerceOrderLocalService.addCommerceOrder(
				_user.getUserId(), _commerceChannel.getGroupId(), -1,
				_commerceCurrency.getCode(), 0);

		Assert.assertTrue(commerceOrder1.isGuestOrder());

		String emailAddress = "buyer@liferay.com";

		ActionRequest actionRequest = _processAction(
			commerceOrder1, emailAddress);

		commerceOrder1 = (CommerceOrder)actionRequest.getAttribute(
			CommerceCheckoutWebKeys.COMMERCE_ORDER);

		CommerceOrder commerceOrder2 =
			_commerceOrderLocalService.addCommerceOrder(
				_user.getUserId(), _commerceChannel.getGroupId(), -1,
				_commerceCurrency.getCode(), 0);

		Assert.assertTrue(commerceOrder2.isGuestOrder());

		actionRequest = _processAction(commerceOrder2, emailAddress);

		commerceOrder2 = (CommerceOrder)actionRequest.getAttribute(
			CommerceCheckoutWebKeys.COMMERCE_ORDER);

		Assert.assertTrue(commerceOrder1.getCommerceAccountId() != -1);
		Assert.assertTrue(commerceOrder2.getCommerceAccountId() != -1);
		Assert.assertTrue(
			commerceOrder1.getCommerceAccountId() !=
				commerceOrder2.getCommerceAccountId());
		Assert.assertTrue(
			commerceOrder1.getCommerceOrderId() !=
				commerceOrder2.getCommerceOrderId());

		AccountEntry accountEntry1 = commerceOrder1.getAccountEntry();
		AccountEntry accountEntry2 = commerceOrder2.getAccountEntry();

		Assert.assertEquals(emailAddress, accountEntry1.getEmailAddress());
		Assert.assertEquals(emailAddress, accountEntry2.getEmailAddress());
	}

	@Rule
	public FrutillaRule frutillaRule = new FrutillaRule();

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(_company);
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setRequest(new MockHttpServletRequest());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private MockLiferayPortletActionRequest _processAction(
			CommerceOrder commerceOrder, String emailAddress)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		HttpServletRequest httpServletRequest =
			mockLiferayPortletActionRequest.getHttpServletRequest();

		httpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_CONFIG, null);

		mockLiferayPortletActionRequest.setAttribute(
			CommerceCheckoutWebKeys.COMMERCE_ORDER, commerceOrder);
		mockLiferayPortletActionRequest.setAttribute(
			CommerceWebKeys.COMMERCE_CONTEXT,
			_commerceContextFactory.create(
				commerceOrder.getCommerceAccountId(), _group.getGroupId(), null,
				commerceOrder.getCommerceOrderId(),
				_serviceContext.getCompanyId()));
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());
		mockLiferayPortletActionRequest.setParameter(
			"city", RandomTestUtil.randomString());
		mockLiferayPortletActionRequest.setParameter(
			"countryId", String.valueOf(_country.getCountryId()));
		mockLiferayPortletActionRequest.setParameter(
			"description", RandomTestUtil.randomString());
		mockLiferayPortletActionRequest.setParameter("email", emailAddress);
		mockLiferayPortletActionRequest.setParameter(
			"name", RandomTestUtil.randomString());
		mockLiferayPortletActionRequest.setParameter("newAddress", "true");
		mockLiferayPortletActionRequest.setParameter(
			"phoneNumber", RandomTestUtil.randomString());
		mockLiferayPortletActionRequest.setParameter(
			"regionId", String.valueOf(_region.getRegionId()));
		mockLiferayPortletActionRequest.setParameter(
			"street1", RandomTestUtil.randomString());
		mockLiferayPortletActionRequest.setParameter(
			"street2", RandomTestUtil.randomString());
		mockLiferayPortletActionRequest.setParameter(
			"street3", RandomTestUtil.randomString());
		mockLiferayPortletActionRequest.setParameter(
			"zip", RandomTestUtil.randomString());

		_commerceCheckoutStep.processAction(
			mockLiferayPortletActionRequest, null);

		return mockLiferayPortletActionRequest;
	}

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	private CommerceChannel _commerceChannel;

	@Inject
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Inject(
		filter = "component.name=com.liferay.commerce.checkout.web.internal.util.BillingAddressCommerceCheckoutStep"
	)
	private CommerceCheckoutStep _commerceCheckoutStep;

	@Inject
	private CommerceContextFactory _commerceContextFactory;

	private CommerceCurrency _commerceCurrency;

	@Inject
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Country _country;

	@Inject
	private CountryLocalService _countryLocalService;

	private Group _group;
	private Region _region;

	@Inject
	private RegionLocalService _regionLocalService;

	private ServiceContext _serviceContext;
	private User _user;

}