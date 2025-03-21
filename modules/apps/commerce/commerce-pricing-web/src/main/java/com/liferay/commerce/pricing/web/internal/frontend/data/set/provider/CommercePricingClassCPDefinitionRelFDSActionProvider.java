/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.web.internal.frontend.data.set.provider;

import com.liferay.commerce.pricing.model.CommercePricingClass;
import com.liferay.commerce.pricing.model.CommercePricingClassCPDefinitionRel;
import com.liferay.commerce.pricing.service.CommercePricingClassCPDefinitionRelLocalService;
import com.liferay.commerce.pricing.web.internal.constants.CommercePricingFDSNames;
import com.liferay.commerce.pricing.web.internal.model.PricingClassCPDefinitionRel;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.frontend.data.set.provider.FDSActionProvider;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Alberti
 */
@Component(
	property = "fds.data.provider.key=" + CommercePricingFDSNames.PRICING_CLASSES_PRODUCT_DEFINITIONS,
	service = FDSActionProvider.class
)
public class CommercePricingClassCPDefinitionRelFDSActionProvider
	implements FDSActionProvider {

	@Override
	public List<DropdownItem> getDropdownItems(
			long groupId, HttpServletRequest httpServletRequest, Object model)
		throws PortalException {

		PricingClassCPDefinitionRel pricingClassCPDefinitionRel =
			(PricingClassCPDefinitionRel)model;

		CommercePricingClassCPDefinitionRel
			commercePricingClassCPDefinitionRel =
				_commercePricingClassCPDefinitionRelLocalService.
					getCommercePricingClassCPDefinitionRel(
						pricingClassCPDefinitionRel.
							getPricingClassCPDefinitionRelId());

		return DropdownItemListBuilder.add(
			() -> _commercePricingClassModelResourcePermission.contains(
				PermissionThreadLocal.getPermissionChecker(),
				commercePricingClassCPDefinitionRel.getCommercePricingClassId(),
				ActionKeys.UPDATE),
			dropdownItem -> {
				dropdownItem.setHref(
					_getCPDefinitionEditURL(
						pricingClassCPDefinitionRel.getCPDefinitionId(),
						httpServletRequest));
				dropdownItem.setLabel(
					_language.get(httpServletRequest, "edit"));
			}
		).add(
			() -> _commercePricingClassModelResourcePermission.contains(
				PermissionThreadLocal.getPermissionChecker(),
				commercePricingClassCPDefinitionRel.getCommercePricingClassId(),
				ActionKeys.DELETE),
			dropdownItem -> {
				dropdownItem.putData("method", "delete");
				dropdownItem.setHref(
					_getPricingClassCPDefinitionRelDeleteURL(
						commercePricingClassCPDefinitionRel.
							getCommercePricingClassCPDefinitionRelId()));
				dropdownItem.setLabel(
					_language.get(httpServletRequest, "remove"));
				dropdownItem.setTarget("async");
			}
		).build();
	}

	private PortletURL _getCPDefinitionEditURL(
			long cpDefinitionId, HttpServletRequest httpServletRequest)
		throws PortalException {

		return PortletURLBuilder.create(
			PortletProviderUtil.getPortletURL(
				httpServletRequest, CPDefinition.class.getName(),
				PortletProvider.Action.MANAGE)
		).setMVCRenderCommandName(
			"/cp_definitions/edit_cp_definition"
		).setParameter(
			"cpDefinitionId", cpDefinitionId
		).setParameter(
			"screenNavigationCategoryKey", "details"
		).buildPortletURL();
	}

	private String _getPricingClassCPDefinitionRelDeleteURL(
		long pricingClassCPDefinitionRelId) {

		return "/o/headless-commerce-admin-catalog/v1.0" +
			"/product-group-products/" + pricingClassCPDefinitionRelId;
	}

	@Reference
	private CommercePricingClassCPDefinitionRelLocalService
		_commercePricingClassCPDefinitionRelLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.pricing.model.CommercePricingClass)"
	)
	private ModelResourcePermission<CommercePricingClass>
		_commercePricingClassModelResourcePermission;

	@Reference
	private Language _language;

}