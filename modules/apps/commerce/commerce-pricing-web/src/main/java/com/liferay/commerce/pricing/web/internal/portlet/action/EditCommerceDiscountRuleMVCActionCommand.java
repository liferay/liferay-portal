/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.web.internal.portlet.action;

import com.liferay.commerce.discount.exception.CommerceDiscountRuleTypeSettingsException;
import com.liferay.commerce.discount.model.CommerceDiscountRule;
import com.liferay.commerce.discount.service.CommerceDiscountRuleService;
import com.liferay.commerce.pricing.constants.CommercePricingPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Alberti
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommercePricingPortletKeys.COMMERCE_DISCOUNT,
		"mvc.command.name=/commerce_discount/edit_commerce_discount_rule"
	},
	service = MVCActionCommand.class
)
public class EditCommerceDiscountRuleMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				_updateCommerceDiscountRule(actionRequest);
			}
			else {
				_deleteCommerceDiscountCPDefinition(actionRequest);
			}
		}
		catch (Throwable throwable) {
			if (throwable instanceof
					CommerceDiscountRuleTypeSettingsException) {

				SessionErrors.add(
					actionRequest, throwable.getClass(), throwable);

				String redirect = ParamUtil.getString(
					actionRequest, "redirect");

				sendRedirect(actionRequest, actionResponse, redirect);
			}
			else {
				SessionErrors.add(actionRequest, throwable.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
		}
	}

	private void _deleteCommerceDiscountCPDefinition(
			ActionRequest actionRequest)
		throws PortalException {

		long commerceDiscountRuleId = ParamUtil.getLong(
			actionRequest, "commerceDiscountRuleId");

		CommerceDiscountRule commerceDiscountRule =
			_commerceDiscountRuleService.getCommerceDiscountRule(
				commerceDiscountRuleId);

		String type = commerceDiscountRule.getType();

		String typeSettings = commerceDiscountRule.getSettingsProperty(type);

		String[] typeSettingsArray = StringUtil.split(typeSettings);

		long cpDefinitionId = ParamUtil.getLong(
			actionRequest, "cpDefinitionId");

		typeSettingsArray = ArrayUtil.remove(
			typeSettingsArray, String.valueOf(cpDefinitionId));

		_commerceDiscountRuleService.updateCommerceDiscountRule(
			commerceDiscountRuleId, type, StringUtil.merge(typeSettingsArray));
	}

	private void _updateCommerceDiscountRule(ActionRequest actionRequest)
		throws Exception {

		String name = ParamUtil.getString(actionRequest, "name");
		String commerceDiscountRuleType = ParamUtil.getString(
			actionRequest, "commerceDiscountRuleType");
		String typeSettings = ParamUtil.getString(
			actionRequest, "typeSettings");

		long commerceDiscountRuleId = ParamUtil.getLong(
			actionRequest, "commerceDiscountRuleId");

		if (commerceDiscountRuleId > 0) {
			_commerceDiscountRuleService.updateCommerceDiscountRule(
				commerceDiscountRuleId, name, commerceDiscountRuleType,
				typeSettings);
		}
		else {
			long commerceDiscountId = ParamUtil.getLong(
				actionRequest, "commerceDiscountId");

			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				CommerceDiscountRule.class.getName(), actionRequest);

			_commerceDiscountRuleService.addCommerceDiscountRule(
				commerceDiscountId, name, commerceDiscountRuleType,
				typeSettings, serviceContext);
		}
	}

	@Reference
	private CommerceDiscountRuleService _commerceDiscountRuleService;

}