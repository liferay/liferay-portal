/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.web.internal.frontend.data.set.provider;

import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.pricing.constants.CommercePricingPortletKeys;
import com.liferay.commerce.pricing.web.internal.constants.CommercePricingFDSNames;
import com.liferay.commerce.pricing.web.internal.model.PricingClassDiscount;
import com.liferay.frontend.data.set.provider.FDSActionProvider;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Alberti
 */
@Component(
	property = "fds.data.provider.key=" + CommercePricingFDSNames.PRICING_CLASSES_DISCOUNTS,
	service = FDSActionProvider.class
)
public class CommercePricingClassDiscountFDSActionProvider
	implements FDSActionProvider {

	@Override
	public List<DropdownItem> getDropdownItems(
			long groupId, HttpServletRequest httpServletRequest, Object model)
		throws PortalException {

		PricingClassDiscount pricingClassDiscount = (PricingClassDiscount)model;

		return DropdownItemListBuilder.add(
			() -> _commerceDiscountModelResourcePermission.contains(
				PermissionThreadLocal.getPermissionChecker(),
				pricingClassDiscount.getCommerceDiscountId(),
				ActionKeys.UPDATE),
			dropdownItem -> {
				dropdownItem.setHref(
					_getDiscountEditURL(
						pricingClassDiscount.getCommerceDiscountId(),
						httpServletRequest));
				dropdownItem.setLabel(
					_language.get(httpServletRequest, "edit"));
			}
		).build();
	}

	private PortletURL _getDiscountEditURL(
		long commerceDiscountId, HttpServletRequest httpServletRequest) {

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest,
				CommercePricingPortletKeys.COMMERCE_DISCOUNT,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/commerce_discount/edit_commerce_discount"
		).setRedirect(
			ParamUtil.getString(
				httpServletRequest, "currentUrl",
				_portal.getCurrentURL(httpServletRequest))
		).setParameter(
			"commerceDiscountId", commerceDiscountId
		).buildPortletURL();
	}

	@Reference(
		target = "(model.class.name=com.liferay.commerce.discount.model.CommerceDiscount)"
	)
	private ModelResourcePermission<CommerceDiscount>
		_commerceDiscountModelResourcePermission;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}