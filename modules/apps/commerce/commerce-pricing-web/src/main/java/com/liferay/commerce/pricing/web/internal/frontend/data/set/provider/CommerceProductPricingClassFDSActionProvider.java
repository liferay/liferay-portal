/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.web.internal.frontend.data.set.provider;

import com.liferay.commerce.pricing.model.CommercePricingClass;
import com.liferay.commerce.pricing.model.CommercePricingClassCPDefinitionRel;
import com.liferay.commerce.pricing.service.CommercePricingClassCPDefinitionRelService;
import com.liferay.commerce.pricing.web.internal.constants.CommercePricingFDSNames;
import com.liferay.commerce.pricing.web.internal.model.ProductPricingClass;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.frontend.data.set.provider.FDSActionProvider;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;

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
	property = "fds.data.provider.key=" + CommercePricingFDSNames.PRODUCT_PRICING_CLASSES,
	service = FDSActionProvider.class
)
public class CommerceProductPricingClassFDSActionProvider
	implements FDSActionProvider {

	@Override
	public List<DropdownItem> getDropdownItems(
			long groupId, HttpServletRequest httpServletRequest, Object model)
		throws PortalException {

		ProductPricingClass productPricingClass = (ProductPricingClass)model;

		return DropdownItemListBuilder.add(
			() -> _commercePricingClassModelResourcePermission.contains(
				PermissionThreadLocal.getPermissionChecker(),
				productPricingClass.getPricingClassId(), ActionKeys.UPDATE),
			dropdownItem -> {
				dropdownItem.setHref(
					_getPricingClassEditURL(
						productPricingClass.getPricingClassId(),
						httpServletRequest));
				dropdownItem.setLabel(
					_language.get(httpServletRequest, "edit"));
				dropdownItem.setTarget("sidePanel");
			}
		).add(
			() -> _commercePricingClassModelResourcePermission.contains(
				PermissionThreadLocal.getPermissionChecker(),
				productPricingClass.getPricingClassId(), ActionKeys.DELETE),
			dropdownItem -> {
				dropdownItem.putData("method", "delete");
				dropdownItem.setHref(
					_getProductPricingClassDeleteURL(
						productPricingClass.getPricingClassId(),
						productPricingClass.getCpDefinitionId()));
				dropdownItem.setLabel(
					_language.get(httpServletRequest, "remove"));
				dropdownItem.setTarget("async");
			}
		).build();
	}

	private PortletURL _getPricingClassEditURL(
			long pricingClassId, HttpServletRequest httpServletRequest)
		throws PortalException {

		PortletURL portletURL = PortletURLBuilder.create(
			PortletProviderUtil.getPortletURL(
				httpServletRequest, CPDefinition.class.getName(),
				PortletProvider.Action.MANAGE)
		).setMVCRenderCommandName(
			"/cp_definitions/edit_cp_definition_pricing_class"
		).setParameter(
			"commercePricingClassId", pricingClassId
		).buildPortletURL();

		try {
			portletURL.setWindowState(LiferayWindowState.POP_UP);
		}
		catch (WindowStateException windowStateException) {
			_log.error(windowStateException);
		}

		return portletURL;
	}

	private String _getProductPricingClassDeleteURL(
			long pricingClassId, long cpDefinitionId)
		throws PortalException {

		CommercePricingClassCPDefinitionRel
			commercePricingClassCPDefinitionRel =
				_commercePricingClassCPDefinitionRelService.
					fetchCommercePricingClassCPDefinitionRel(
						pricingClassId, cpDefinitionId);

		long commercePricingClassCPDefinitionRelId =
			commercePricingClassCPDefinitionRel.
				getCommercePricingClassCPDefinitionRelId();

		return "/o/headless-commerce-admin-catalog/v1.0" +
			"/product-group-products/" + commercePricingClassCPDefinitionRelId;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceProductPricingClassFDSActionProvider.class);

	@Reference
	private CommercePricingClassCPDefinitionRelService
		_commercePricingClassCPDefinitionRelService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.pricing.model.CommercePricingClass)"
	)
	private ModelResourcePermission<CommercePricingClass>
		_commercePricingClassModelResourcePermission;

	@Reference
	private Language _language;

}