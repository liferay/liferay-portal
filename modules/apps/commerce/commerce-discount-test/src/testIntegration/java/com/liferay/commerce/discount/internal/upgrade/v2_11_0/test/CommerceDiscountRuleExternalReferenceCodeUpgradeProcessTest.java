/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.internal.upgrade.v2_11_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.discount.constants.CommerceDiscountConstants;
import com.liferay.commerce.discount.constants.CommerceDiscountRuleConstants;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.model.CommerceDiscountRule;
import com.liferay.commerce.discount.service.CommerceDiscountLocalService;
import com.liferay.commerce.discount.service.CommerceDiscountRuleLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ExternalReferenceCodeModel;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.BaseExternalReferenceCodeUpgradeProcessTestCase;

import java.math.BigDecimal;

import java.util.Calendar;

import org.junit.runner.RunWith;

/**
 * @author Alessio Antonio Rendina
 */
@RunWith(Arquillian.class)
public class CommerceDiscountRuleExternalReferenceCodeUpgradeProcessTest
	extends BaseExternalReferenceCodeUpgradeProcessTestCase {

	@Override
	protected ExternalReferenceCodeModel[] addExternalReferenceCodeModels(
			String tableName)
		throws PortalException {

		Calendar calendar = Calendar.getInstance();

		_commerceDiscount = _commerceDiscountLocalService.addCommerceDiscount(
			RandomTestUtil.randomString(), serviceContext.getUserId(),
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
			true, serviceContext);

		return new ExternalReferenceCodeModel[] {
			_commerceDiscountRuleLocalService.addCommerceDiscountRule(
				_commerceDiscount.getCommerceDiscountId(),
				RandomTestUtil.randomString(),
				CommerceDiscountRuleConstants.TYPE_ADDED_ANY,
				RandomTestUtil.randomString(), serviceContext)
		};
	}

	@Override
	protected ExternalReferenceCodeModel fetchExternalReferenceCodeModel(
		ExternalReferenceCodeModel externalReferenceCodeModel,
		String tableName) {

		CommerceDiscountRule commerceDiscountRule =
			(CommerceDiscountRule)externalReferenceCodeModel;

		return _commerceDiscountRuleLocalService.fetchCommerceDiscountRule(
			commerceDiscountRule.getCommerceDiscountRuleId());
	}

	@Override
	protected String getExternalReferenceCode(
		ExternalReferenceCodeModel externalReferenceCodeModel,
		String tableName) {

		CommerceDiscountRule commerceDiscountRule =
			(CommerceDiscountRule)externalReferenceCodeModel;

		return String.valueOf(commerceDiscountRule.getCommerceDiscountRuleId());
	}

	@Override
	protected String[] getTableNames() {
		return new String[] {"CommerceDiscountRule"};
	}

	@Override
	protected UpgradeStepRegistrator getUpgradeStepRegistrator() {
		return _upgradeStepRegistrator;
	}

	@Override
	protected Version getVersion() {
		return new Version(2, 11, 0);
	}

	@DeleteAfterTestRun
	private CommerceDiscount _commerceDiscount;

	@Inject
	private CommerceDiscountLocalService _commerceDiscountLocalService;

	@Inject
	private CommerceDiscountRuleLocalService _commerceDiscountRuleLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.commerce.discount.internal.upgrade.registry.CommerceDiscountServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}