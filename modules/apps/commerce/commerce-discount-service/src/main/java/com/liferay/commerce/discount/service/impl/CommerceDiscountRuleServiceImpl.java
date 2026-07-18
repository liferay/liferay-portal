/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service.impl;

import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.model.CommerceDiscountRule;
import com.liferay.commerce.discount.service.base.CommerceDiscountRuleServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommerceDiscountRule"
	},
	service = AopService.class
)
public class CommerceDiscountRuleServiceImpl
	extends CommerceDiscountRuleServiceBaseImpl {

	@Override
	public CommerceDiscountRule addCommerceDiscountRule(
			long commerceDiscountId, String type, String typeSettings,
			ServiceContext serviceContext)
		throws PortalException {

		_commerceDiscountResourcePermission.check(
			getPermissionChecker(), commerceDiscountId, ActionKeys.UPDATE);

		return commerceDiscountRuleLocalService.addCommerceDiscountRule(
			commerceDiscountId, type, typeSettings, serviceContext);
	}

	@Override
	public CommerceDiscountRule addCommerceDiscountRule(
			long commerceDiscountId, String name, String type,
			String typeSettings, ServiceContext serviceContext)
		throws PortalException {

		_commerceDiscountResourcePermission.check(
			getPermissionChecker(), commerceDiscountId, ActionKeys.UPDATE);

		return commerceDiscountRuleLocalService.addCommerceDiscountRule(
			commerceDiscountId, name, type, typeSettings, serviceContext);
	}

	@Override
	public void deleteCommerceDiscountRule(long commerceDiscountRuleId)
		throws PortalException {

		CommerceDiscountRule commerceDiscountRule =
			commerceDiscountRulePersistence.findByPrimaryKey(
				commerceDiscountRuleId);

		_commerceDiscountResourcePermission.check(
			getPermissionChecker(),
			commerceDiscountRule.getCommerceDiscountId(), ActionKeys.UPDATE);

		commerceDiscountRuleLocalService.deleteCommerceDiscountRule(
			commerceDiscountRule);
	}

	@Override
	public CommerceDiscountRule fetchCommerceDiscountRule(
			long commerceDiscountRuleId)
		throws PortalException {

		CommerceDiscountRule commerceDiscountRule =
			commerceDiscountRulePersistence.fetchByPrimaryKey(
				commerceDiscountRuleId);

		if (commerceDiscountRule != null) {
			_commerceDiscountResourcePermission.check(
				getPermissionChecker(),
				commerceDiscountRule.getCommerceDiscountId(),
				ActionKeys.UPDATE);
		}

		return commerceDiscountRule;
	}

	@Override
	public CommerceDiscountRule getCommerceDiscountRule(
			long commerceDiscountRuleId)
		throws PortalException {

		CommerceDiscountRule commerceDiscountRule =
			commerceDiscountRulePersistence.findByPrimaryKey(
				commerceDiscountRuleId);

		_commerceDiscountResourcePermission.check(
			getPermissionChecker(),
			commerceDiscountRule.getCommerceDiscountId(), ActionKeys.UPDATE);

		return commerceDiscountRule;
	}

	@Override
	public List<CommerceDiscountRule> getCommerceDiscountRules(
			long commerceDiscountId, int start, int end,
			OrderByComparator<CommerceDiscountRule> orderByComparator)
		throws PortalException {

		_commerceDiscountResourcePermission.check(
			getPermissionChecker(), commerceDiscountId, ActionKeys.UPDATE);

		return commerceDiscountRuleLocalService.getCommerceDiscountRules(
			commerceDiscountId, start, end, orderByComparator);
	}

	@Override
	public List<CommerceDiscountRule> getCommerceDiscountRules(
			long commerceDiscountId, String name, int start, int end)
		throws PortalException {

		return commerceDiscountRuleFinder.findByCommerceDiscountId(
			commerceDiscountId, name, start, end, true);
	}

	@Override
	public int getCommerceDiscountRulesCount(long commerceDiscountId)
		throws PortalException {

		_commerceDiscountResourcePermission.check(
			getPermissionChecker(), commerceDiscountId, ActionKeys.UPDATE);

		return commerceDiscountRulePersistence.countByCommerceDiscountId(
			commerceDiscountId);
	}

	@Override
	public int getCommerceDiscountRulesCount(
			long commerceDiscountId, String name)
		throws PortalException {

		return commerceDiscountRuleFinder.countByCommerceDiscountId(
			commerceDiscountId, name, true);
	}

	@Override
	public CommerceDiscountRule updateCommerceDiscountRule(
			long commerceDiscountRuleId, String type, String typeSettings)
		throws PortalException {

		CommerceDiscountRule commerceDiscountRule =
			commerceDiscountRulePersistence.findByPrimaryKey(
				commerceDiscountRuleId);

		_commerceDiscountResourcePermission.check(
			getPermissionChecker(),
			commerceDiscountRule.getCommerceDiscountId(), ActionKeys.UPDATE);

		return commerceDiscountRuleLocalService.updateCommerceDiscountRule(
			commerceDiscountRuleId, type, typeSettings);
	}

	@Override
	public CommerceDiscountRule updateCommerceDiscountRule(
			long commerceDiscountRuleId, String name, String type,
			String typeSettings)
		throws PortalException {

		CommerceDiscountRule commerceDiscountRule =
			commerceDiscountRulePersistence.findByPrimaryKey(
				commerceDiscountRuleId);

		_commerceDiscountResourcePermission.check(
			getPermissionChecker(),
			commerceDiscountRule.getCommerceDiscountId(), ActionKeys.UPDATE);

		return commerceDiscountRuleLocalService.updateCommerceDiscountRule(
			commerceDiscountRuleId, name, type, typeSettings);
	}

	@Reference(
		target = "(model.class.name=com.liferay.commerce.discount.model.CommerceDiscount)"
	)
	private ModelResourcePermission<CommerceDiscount>
		_commerceDiscountResourcePermission;

}