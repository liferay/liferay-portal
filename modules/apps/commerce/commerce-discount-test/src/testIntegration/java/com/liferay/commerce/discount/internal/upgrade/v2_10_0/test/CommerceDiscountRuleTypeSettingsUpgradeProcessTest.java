/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.internal.upgrade.v2_10_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.discount.constants.CommerceDiscountConstants;
import com.liferay.commerce.discount.constants.CommerceDiscountRuleConstants;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.model.CommerceDiscountRule;
import com.liferay.commerce.discount.service.CommerceDiscountLocalService;
import com.liferay.commerce.discount.service.CommerceDiscountRuleLocalService;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.math.BigDecimal;

import java.sql.Connection;
import java.sql.PreparedStatement;

import java.util.Calendar;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alessio Antonio Rendina
 */
@RunWith(Arquillian.class)
public class CommerceDiscountRuleTypeSettingsUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_user = UserTestUtil.addUser();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), _user.getUserId());
	}

	@Test
	public void testUpgrade() throws Exception {
		CPDefinition cpDefinition = CPTestUtil.addCPDefinition(
			_group.getGroupId());

		CProduct cProduct = cpDefinition.getCProduct();

		CommerceDiscountRule commerceDiscountRule = _addCommerceDiscountRule();

		_updateTypeSettings(
			commerceDiscountRule.getCommerceDiscountRuleId(),
			CommerceDiscountRuleConstants.TYPE_ADDED_ANY + "=" +
				cpDefinition.getCPDefinitionId());

		_runUpgrade();

		EntityCacheUtil.clearCache();

		commerceDiscountRule =
			_commerceDiscountRuleLocalService.getCommerceDiscountRule(
				commerceDiscountRule.getCommerceDiscountRuleId());

		Assert.assertEquals(
			cProduct.getExternalReferenceCode(),
			commerceDiscountRule.getSettingsProperty(
				CommerceDiscountRuleConstants.TYPE_ADDED_ANY));
	}

	private CommerceDiscountRule _addCommerceDiscountRule() throws Exception {
		Calendar calendar = Calendar.getInstance();

		_commerceDiscount = _commerceDiscountLocalService.addCommerceDiscount(
			RandomTestUtil.randomString(), _user.getUserId(),
			RandomTestUtil.randomString(),
			CommerceDiscountConstants.TARGET_PRODUCTS, false, null, true,
			BigDecimal.ONE, CommerceDiscountConstants.LEVEL_L1, BigDecimal.ONE,
			BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE,
			CommerceDiscountConstants.LIMITATION_TYPE_UNLIMITED, 0, false, true,
			calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
			calendar.get(Calendar.YEAR), calendar.get(Calendar.HOUR_OF_DAY),
			calendar.get(Calendar.MINUTE), calendar.get(Calendar.MONTH),
			calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR),
			calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
			true, _serviceContext);

		return _commerceDiscountRuleLocalService.addCommerceDiscountRule(
			_commerceDiscount.getCommerceDiscountId(),
			RandomTestUtil.randomString(),
			CommerceDiscountRuleConstants.TYPE_ADDED_ANY,
			RandomTestUtil.randomString(), _serviceContext);
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();
	}

	private void _updateTypeSettings(
			long commerceDiscountRuleId, String typeSettings)
		throws Exception {

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"update CommerceDiscountRule set typeSettings = ? where " +
					"commerceDiscountRuleId = ?")) {

			preparedStatement.setString(1, typeSettings);
			preparedStatement.setLong(2, commerceDiscountRuleId);

			preparedStatement.executeUpdate();
		}
	}

	private static final String _CLASS_NAME =
		"com.liferay.commerce.discount.internal.upgrade.v2_10_0." +
			"CommerceDiscountRuleTypeSettingsUpgradeProcess";

	@DeleteAfterTestRun
	private CommerceDiscount _commerceDiscount;

	@Inject
	private CommerceDiscountLocalService _commerceDiscountLocalService;

	@Inject
	private CommerceDiscountRuleLocalService _commerceDiscountRuleLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private ServiceContext _serviceContext;

	@Inject(
		filter = "(&(component.name=com.liferay.commerce.discount.internal.upgrade.registry.CommerceDiscountServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

	@DeleteAfterTestRun
	private User _user;

}