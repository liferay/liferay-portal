/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.web.internal.frontend.data.set.provider;

import com.liferay.commerce.pricing.constants.CommercePricingPortletKeys;
import com.liferay.commerce.pricing.model.CommercePricingClass;
import com.liferay.commerce.pricing.web.internal.constants.CommercePricingClassScreenNavigationConstants;
import com.liferay.commerce.pricing.web.internal.constants.CommercePricingFDSNames;
import com.liferay.commerce.pricing.web.internal.model.PricingClass;
import com.liferay.frontend.data.set.provider.FDSActionProvider;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletQName;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
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
	property = "fds.data.provider.key=" + CommercePricingFDSNames.PRICING_CLASSES,
	service = FDSActionProvider.class
)
public class CommercePricingClassFDSActionProvider
	implements FDSActionProvider {

	@Override
	public List<DropdownItem> getDropdownItems(
			long groupId, HttpServletRequest httpServletRequest, Object model)
		throws PortalException {

		PricingClass pricingClass = (PricingClass)model;

		return DropdownItemListBuilder.add(
			() -> _commercePricingClassModelResourcePermission.contains(
				PermissionThreadLocal.getPermissionChecker(),
				pricingClass.getPricingClassId(), ActionKeys.UPDATE),
			dropdownItem -> {
				dropdownItem.setHref(
					_getPricingClassEditURL(
						pricingClass.getPricingClassId(), httpServletRequest));
				dropdownItem.setLabel(
					_language.get(httpServletRequest, "edit"));
			}
		).add(
			() -> _commercePricingClassModelResourcePermission.contains(
				PermissionThreadLocal.getPermissionChecker(),
				pricingClass.getPricingClassId(), ActionKeys.PERMISSIONS),
			dropdownItem -> {
				dropdownItem.setHref(
					_getManageCatalogPermissionsURL(
						pricingClass, httpServletRequest));
				dropdownItem.setLabel(
					_language.get(httpServletRequest, "permissions"));
				dropdownItem.setTarget("modal-permissions");
			}
		).add(
			() -> _commercePricingClassModelResourcePermission.contains(
				PermissionThreadLocal.getPermissionChecker(),
				pricingClass.getPricingClassId(), ActionKeys.DELETE),
			dropdownItem -> {
				dropdownItem.putData("method", "delete");
				dropdownItem.setHref(
					_getPricingClassDeleteURL(
						pricingClass.getPricingClassId()));
				dropdownItem.setLabel(
					_language.get(httpServletRequest, "delete"));
				dropdownItem.setTarget("async");
			}
		).build();
	}

	private PortletURL _getManageCatalogPermissionsURL(
			PricingClass pricingClass, HttpServletRequest httpServletRequest)
		throws PortalException {

		PortletURL portletURL = PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest,
				"com_liferay_portlet_configuration_web_portlet_" +
					"PortletConfigurationPortlet",
				ActionRequest.RENDER_PHASE)
		).setMVCPath(
			"/edit_permissions.jsp"
		).setParameter(
			PortletQName.PUBLIC_RENDER_PARAMETER_NAMESPACE + "backURL",
			ParamUtil.getString(
				httpServletRequest, "currentUrl",
				_portal.getCurrentURL(httpServletRequest))
		).setParameter(
			"modelResource", CommercePricingClass.class.getName()
		).setParameter(
			"modelResourceDescription", pricingClass.getTitle()
		).setParameter(
			"resourcePrimKey", pricingClass.getPricingClassId()
		).buildPortletURL();

		try {
			portletURL.setWindowState(LiferayWindowState.POP_UP);
		}
		catch (WindowStateException windowStateException) {
			throw new PortalException(windowStateException);
		}

		return portletURL;
	}

	private String _getPricingClassDeleteURL(long pricingClassId) {
		return "/o/headless-commerce-admin-catalog/v1.0/product-groups/" +
			pricingClassId;
	}

	private PortletURL _getPricingClassEditURL(
		long pricingClassId, HttpServletRequest httpServletRequest) {

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest,
				CommercePricingPortletKeys.COMMERCE_PRICING_CLASSES,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/commerce_pricing_classes/edit_commerce_pricing_class"
		).setParameter(
			"commercePricingClassId", pricingClassId
		).setParameter(
			"screenNavigationCategoryKey",
			CommercePricingClassScreenNavigationConstants.CATEGORY_KEY_DETAILS
		).buildPortletURL();
	}

	@Reference(
		target = "(model.class.name=com.liferay.commerce.pricing.model.CommercePricingClass)"
	)
	private ModelResourcePermission<CommercePricingClass>
		_commercePricingClassModelResourcePermission;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}