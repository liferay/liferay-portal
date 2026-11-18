/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.discount.constants.CommerceDiscountConstants;
import com.liferay.commerce.discount.constants.CommerceDiscountRuleConstants;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.model.CommerceDiscountRule;
import com.liferay.commerce.discount.service.CommerceDiscountLocalService;
import com.liferay.commerce.discount.service.CommerceDiscountRuleLocalService;
import com.liferay.commerce.discount.service.CommerceDiscountRuleService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.math.BigDecimal;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Andrea Sbarra
 */
@RunWith(Arquillian.class)
public class CommerceDiscountRuleServiceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_commerceDiscountRule = _addCommerceDiscountRule();

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), CommerceDiscount.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(_commerceDiscountRule.getCommerceDiscountId()),
			role.getRoleId(), new String[] {ActionKeys.UPDATE});

		_user = UserTestUtil.addUser();

		_userLocalService.addRoleUser(role.getRoleId(), _user);
	}

	@Test
	public void testAddOrUpdateCommerceDiscountRule() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			String name = RandomTestUtil.randomString();

			CommerceDiscountRule commerceDiscountRule =
				_commerceDiscountRuleService.addOrUpdateCommerceDiscountRule(
					null, _commerceDiscountRule.getCommerceDiscountRuleId(),
					_commerceDiscountRule.getCommerceDiscountId(), name,
					CommerceDiscountRuleConstants.TYPE_CART_TOTAL,
					String.valueOf(RandomTestUtil.randomInt()),
					ServiceContextTestUtil.getServiceContext());

			Assert.assertEquals(name, commerceDiscountRule.getName());
		}

		CommerceDiscountRule commerceDiscountRule = _addCommerceDiscountRule();

		_testAddOrUpdateCommerceDiscountRule(
			commerceDiscountRule, 0,
			commerceDiscountRule.getExternalReferenceCode());
		_testAddOrUpdateCommerceDiscountRule(
			commerceDiscountRule,
			commerceDiscountRule.getCommerceDiscountRuleId(), null);
	}

	private CommerceDiscountRule _addCommerceDiscountRule() throws Exception {
		Calendar calendar = new GregorianCalendar();

		CommerceDiscount commerceDiscount =
			_commerceDiscountLocalService.addCommerceDiscount(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				CommerceDiscountConstants.TARGET_PRODUCTS, false, null, false,
				BigDecimal.valueOf(RandomTestUtil.randomDouble()),
				BigDecimal.valueOf(RandomTestUtil.randomDouble()),
				BigDecimal.valueOf(RandomTestUtil.randomDouble()),
				BigDecimal.valueOf(RandomTestUtil.randomDouble()),
				BigDecimal.valueOf(RandomTestUtil.randomDouble()),
				CommerceDiscountConstants.LIMITATION_TYPE_UNLIMITED,
				RandomTestUtil.randomInt(), true, calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH),
				calendar.get(Calendar.YEAR), calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE), 0, 0, 0, 0, 0, true,
				ServiceContextTestUtil.getServiceContext());

		return _commerceDiscountRuleLocalService.addCommerceDiscountRule(
			null, commerceDiscount.getCommerceDiscountId(),
			RandomTestUtil.randomString(),
			CommerceDiscountRuleConstants.TYPE_CART_TOTAL,
			String.valueOf(RandomTestUtil.randomInt()),
			ServiceContextTestUtil.getServiceContext());
	}

	private void _testAddOrUpdateCommerceDiscountRule(
			CommerceDiscountRule commerceDiscountRule,
			long commerceDiscountRuleId, String externalReferenceCode)
		throws Exception {

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			_commerceDiscountRuleService.addOrUpdateCommerceDiscountRule(
				externalReferenceCode, commerceDiscountRuleId,
				_commerceDiscountRule.getCommerceDiscountId(),
				RandomTestUtil.randomString(),
				CommerceDiscountRuleConstants.TYPE_CART_TOTAL,
				String.valueOf(RandomTestUtil.randomInt()),
				ServiceContextTestUtil.getServiceContext());

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			String message = principalException.getMessage();

			Assert.assertTrue(
				message.contains(
					StringBundler.concat(
						"User ", _user.getUserId(), " must have ",
						ActionKeys.UPDATE, " permission for")));
		}

		CommerceDiscountRule actualCommerceDiscountRule =
			_commerceDiscountRuleLocalService.getCommerceDiscountRule(
				commerceDiscountRule.getCommerceDiscountRuleId());

		Assert.assertEquals(
			commerceDiscountRule.getName(),
			actualCommerceDiscountRule.getName());
	}

	@Inject
	private CommerceDiscountLocalService _commerceDiscountLocalService;

	private CommerceDiscountRule _commerceDiscountRule;

	@Inject
	private CommerceDiscountRuleLocalService _commerceDiscountRuleLocalService;

	@Inject
	private CommerceDiscountRuleService _commerceDiscountRuleService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}