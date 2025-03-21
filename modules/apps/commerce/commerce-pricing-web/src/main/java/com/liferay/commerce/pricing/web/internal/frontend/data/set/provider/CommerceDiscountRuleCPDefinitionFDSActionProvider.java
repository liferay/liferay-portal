/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.web.internal.frontend.data.set.provider;

import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.model.CommerceDiscountRule;
import com.liferay.commerce.discount.service.CommerceDiscountRuleService;
import com.liferay.commerce.pricing.constants.CommercePricingPortletKeys;
import com.liferay.commerce.pricing.web.internal.constants.CommercePricingFDSNames;
import com.liferay.commerce.pricing.web.internal.model.DiscountRuleCPDefinition;
import com.liferay.frontend.data.set.provider.FDSActionProvider;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowStateException;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Alberti
 */
@Component(
	property = "fds.data.provider.key=" + CommercePricingFDSNames.DISCOUNT_RULE_PRODUCT_DEFINITIONS,
	service = FDSActionProvider.class
)
public class CommerceDiscountRuleCPDefinitionFDSActionProvider
	implements FDSActionProvider {

	@Override
	public List<DropdownItem> getDropdownItems(
			long groupId, HttpServletRequest httpServletRequest, Object model)
		throws PortalException {

		DiscountRuleCPDefinition discountRuleCPDefinition =
			(DiscountRuleCPDefinition)model;

		CommerceDiscountRule commerceDiscountRule =
			_commerceDiscountRuleService.getCommerceDiscountRule(
				discountRuleCPDefinition.getDiscountRuleId());

		return DropdownItemListBuilder.add(
			() -> _commerceDiscountModelResourcePermission.contains(
				PermissionThreadLocal.getPermissionChecker(),
				commerceDiscountRule.getCommerceDiscountId(),
				ActionKeys.UPDATE),
			dropdownItem -> {
				dropdownItem.setHref(
					_getDiscountRuleDeleteCPDefinitionURL(
						discountRuleCPDefinition.getCPDefinitionId(),
						discountRuleCPDefinition.getDiscountRuleId(),
						httpServletRequest));
				dropdownItem.setLabel(
					_language.get(httpServletRequest, Constants.REMOVE));
			}
		).build();
	}

	private PortletURL _getDiscountRuleDeleteCPDefinitionURL(
			long cpDefinitionId, long commerceDiscountRuleId,
			HttpServletRequest httpServletRequest)
		throws PortalException {

		PortletURL portletURL = _portal.getControlPanelPortletURL(
			httpServletRequest, CommercePricingPortletKeys.COMMERCE_DISCOUNT,
			PortletRequest.ACTION_PHASE);

		try {
			portletURL.setWindowState(LiferayWindowState.POP_UP);
		}
		catch (WindowStateException windowStateException) {
			_log.error(windowStateException);
		}

		portletURL.setParameter(
			ActionRequest.ACTION_NAME,
			"/commerce_discount/edit_commerce_discount_rule");
		portletURL.setParameter(Constants.CMD, Constants.DELETE);

		String redirect = ParamUtil.getString(
			httpServletRequest, "currentUrl",
			_portal.getCurrentURL(httpServletRequest));

		portletURL.setParameter("redirect", redirect);

		portletURL.setParameter(
			"commerceDiscountRuleId", String.valueOf(commerceDiscountRuleId));
		portletURL.setParameter(
			"cpDefinitionId", String.valueOf(cpDefinitionId));

		return portletURL;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceDiscountRuleCPDefinitionFDSActionProvider.class);

	@Reference(
		target = "(model.class.name=com.liferay.commerce.discount.model.CommerceDiscount)"
	)
	private ModelResourcePermission<CommerceDiscount>
		_commerceDiscountModelResourcePermission;

	@Reference
	private CommerceDiscountRuleService _commerceDiscountRuleService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}