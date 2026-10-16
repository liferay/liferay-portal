/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.channel.web.internal.frontend.data.set.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel;
import com.liferay.commerce.payment.test.util.TestCommercePaymentMethod;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.frontend.data.set.provider.FDSActionProvider;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.FDSDataProviderRegistry;
import com.liferay.frontend.data.set.provider.search.FDSKeywordsFactory;
import com.liferay.frontend.data.set.provider.search.FDSKeywordsFactoryRegistry;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.bean.BeanPropertiesUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Lianne Louie
 */
@RunWith(Arquillian.class)
public class CommercePaymentMethodFDSActionProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testGetDropdownItems() throws Exception {
		_group = GroupTestUtil.addGroup();

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			_group.getCompanyId());

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), _commerceCurrency.getCode());

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			CommerceTestUtil.addCommercePaymentMethodGroupRel(
				TestPropsValues.getUserId(), _commerceChannel.getGroupId());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.CURRENT_URL, RandomTestUtil.randomString());
		mockHttpServletRequest.setParameter(
			"commerceChannelId",
			String.valueOf(_commerceChannel.getCommerceChannelId()));

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_commerceChannel.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.US);
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		_role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_resourcePermissionLocalService.setResourcePermissions(
			_group.getCompanyId(), CommerceChannel.class.getName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(_group.getCompanyId()), _role.getRoleId(),
			new String[] {ActionKeys.UPDATE, ActionKeys.VIEW});

		_user = UserTestUtil.addUser();

		_userLocalService.addRoleUser(_role.getRoleId(), _user);

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		Object testPaymentMethod = _getPaymentMethod(
			TestCommercePaymentMethod.KEY, mockHttpServletRequest);

		_assertDropdownItemLabels(
			mockHttpServletRequest, testPaymentMethod, "Edit");

		_resourcePermissionLocalService.setResourcePermissions(
			_group.getCompanyId(),
			CommercePaymentMethodGroupRel.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(
				commercePaymentMethodGroupRel.
					getCommercePaymentMethodGroupRelId()),
			_role.getRoleId(), new String[] {ActionKeys.PERMISSIONS});

		_assertDropdownItemLabels(
			mockHttpServletRequest,
			_getPaymentMethod("money-order", mockHttpServletRequest), "Edit");
		_assertDropdownItemLabels(
			mockHttpServletRequest, testPaymentMethod, "Edit", "Permissions");
	}

	private void _assertDropdownItemLabels(
			MockHttpServletRequest mockHttpServletRequest, Object paymentMethod,
			String... expectedDropdownItemLabels)
		throws Exception {

		Assert.assertEquals(
			Arrays.asList(expectedDropdownItemLabels),
			TransformUtil.transform(
				_fdsActionProvider.getDropdownItems(
					TestPropsValues.getGroupId(), mockHttpServletRequest,
					paymentMethod),
				dropdownItem -> dropdownItem.get("label")));
	}

	private Object _getPaymentMethod(
			String key, MockHttpServletRequest mockHttpServletRequest)
		throws Exception {

		FDSDataProvider<?> fdsDataProvider =
			_fdsDataProviderRegistry.getFDSDataProvider(_FDS_NAME);

		int itemsCount = fdsDataProvider.getItemsCount(
			null, mockHttpServletRequest);

		FDSKeywordsFactory fdsKeywordsFactory =
			_fdsKeywordsFactoryRegistry.getFDSKeywordsFactory(_FDS_NAME);

		List<?> items = fdsDataProvider.getItems(
			fdsKeywordsFactory.create(mockHttpServletRequest),
			new FDSPagination() {

				@Override
				public int getEndPosition() {
					return itemsCount;
				}

				@Override
				public int getPage() {
					return 1;
				}

				@Override
				public int getPageSize() {
					return itemsCount;
				}

				@Override
				public int getStartPosition() {
					return 0;
				}

			},
			mockHttpServletRequest, null);

		for (Object item : items) {
			if (Objects.equals(
					key, BeanPropertiesUtil.getString(item, "key"))) {

				return item;
			}
		}

		throw new AssertionError(
			"No payment method was found with key \"" + key + "\"");
	}

	private static final String _FDS_NAME =
		CPPortletKeys.COMMERCE_CHANNELS + "-paymentMethod";

	@DeleteAfterTestRun
	private CommerceChannel _commerceChannel;

	@DeleteAfterTestRun
	private CommerceCurrency _commerceCurrency;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(filter = "fds.data.provider.key=" + _FDS_NAME)
	private FDSActionProvider _fdsActionProvider;

	@Inject
	private FDSDataProviderRegistry _fdsDataProviderRegistry;

	@Inject
	private FDSKeywordsFactoryRegistry _fdsKeywordsFactoryRegistry;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@DeleteAfterTestRun
	private Role _role;

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}