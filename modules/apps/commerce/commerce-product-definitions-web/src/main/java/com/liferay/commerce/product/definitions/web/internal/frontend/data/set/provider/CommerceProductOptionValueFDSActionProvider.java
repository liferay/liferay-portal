/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.frontend.data.set.provider;

import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.definitions.web.internal.constants.CommerceProductFDSNames;
import com.liferay.commerce.product.definitions.web.internal.model.ProductOptionValue;
import com.liferay.commerce.product.definitions.web.internal.security.permission.resource.CommerceCatalogPermission;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.commerce.product.service.CPDefinitionOptionValueRelService;
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
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowStateException;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"fds.data.provider.key=" + CommerceProductFDSNames.PRODUCT_OPTION_VALUES,
		"fds.data.provider.key=" + CommerceProductFDSNames.PRODUCT_OPTION_VALUES_STATIC
	},
	service = FDSActionProvider.class
)
public class CommerceProductOptionValueFDSActionProvider
	implements FDSActionProvider {

	@Override
	public List<DropdownItem> getDropdownItems(
			long groupId, HttpServletRequest httpServletRequest, Object model)
		throws PortalException {

		ProductOptionValue productOptionValue = (ProductOptionValue)model;

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			_cpDefinitionOptionValueRelService.fetchCPDefinitionOptionValueRel(
				productOptionValue.getCPDefinitionOptionValueRelId());

		if (cpDefinitionOptionValueRel == null) {
			return Collections.emptyList();
		}

		CPDefinitionOptionRel cpDefinitionOptionRel =
			cpDefinitionOptionValueRel.getCPDefinitionOptionRel();

		return DropdownItemListBuilder.add(
			() -> CommerceCatalogPermission.contains(
				PermissionThreadLocal.getPermissionChecker(),
				cpDefinitionOptionRel.getCPDefinition(), ActionKeys.UPDATE),
			dropdownItem -> {
				dropdownItem.setHref(
					_getProductOptionValueEditURL(
						cpDefinitionOptionValueRel, httpServletRequest));
				dropdownItem.setLabel(
					_language.get(httpServletRequest, "edit"));
				dropdownItem.setTarget("sidePanel");
			}
		).add(
			() -> CommerceCatalogPermission.contains(
				PermissionThreadLocal.getPermissionChecker(),
				cpDefinitionOptionRel.getCPDefinition(), ActionKeys.UPDATE),
			dropdownItem -> {
				dropdownItem.setHref(
					_getProductOptionValueDeleteURL(
						cpDefinitionOptionValueRel.
							getCPDefinitionOptionValueRelId(),
						httpServletRequest));
				dropdownItem.setLabel(
					_language.get(httpServletRequest, "delete"));
			}
		).add(
			() -> CommerceCatalogPermission.contains(
				PermissionThreadLocal.getPermissionChecker(),
				cpDefinitionOptionRel.getCPDefinition(), ActionKeys.UPDATE),
			dropdownItem -> {
				dropdownItem.put("id", "updatePreselected");
				dropdownItem.setHref(
					_getProductOptionValueUpdatePreselectedURL(
						cpDefinitionOptionValueRel.
							getCPDefinitionOptionValueRelId(),
						httpServletRequest));
				dropdownItem.setLabel(
					_language.get(httpServletRequest, "toggle-default"));
			}
		).build();
	}

	private PortletURL _getProductOptionValueDeleteURL(
		long cpDefinitionOptionValueRelId,
		HttpServletRequest httpServletRequest) {

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				_portal.getOriginalServletRequest(httpServletRequest),
				CPPortletKeys.CP_DEFINITIONS, PortletRequest.ACTION_PHASE)
		).setActionName(
			"/cp_definitions/edit_cp_definition_option_value_rel"
		).setCMD(
			Constants.DELETE
		).setRedirect(
			ParamUtil.getString(
				httpServletRequest, "currentUrl",
				_portal.getCurrentURL(httpServletRequest))
		).setParameter(
			"cpDefinitionOptionValueRelId", cpDefinitionOptionValueRelId
		).buildPortletURL();
	}

	private PortletURL _getProductOptionValueEditURL(
			CPDefinitionOptionValueRel cpDefinitionOptionValueRel,
			HttpServletRequest httpServletRequest)
		throws PortalException {

		CPDefinitionOptionRel cpDefinitionOptionRel =
			cpDefinitionOptionValueRel.getCPDefinitionOptionRel();

		PortletURL portletURL = PortletURLBuilder.create(
			PortletProviderUtil.getPortletURL(
				httpServletRequest, CPDefinition.class.getName(),
				PortletProvider.Action.MANAGE)
		).setMVCRenderCommandName(
			"/cp_definitions/edit_cp_definition_option_value_rel"
		).setParameter(
			"cpDefinitionId", cpDefinitionOptionRel.getCPDefinitionId()
		).setParameter(
			"cpDefinitionOptionRelId",
			cpDefinitionOptionRel.getCPDefinitionOptionRelId()
		).setParameter(
			"cpDefinitionOptionValueRelId",
			cpDefinitionOptionValueRel.getCPDefinitionOptionValueRelId()
		).buildPortletURL();

		try {
			portletURL.setWindowState(LiferayWindowState.POP_UP);
		}
		catch (WindowStateException windowStateException) {
			_log.error(windowStateException);
		}

		return portletURL;
	}

	private PortletURL _getProductOptionValueUpdatePreselectedURL(
		long cpDefinitionOptionValueRelId,
		HttpServletRequest httpServletRequest) {

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				_portal.getOriginalServletRequest(httpServletRequest),
				CPPortletKeys.CP_DEFINITIONS, PortletRequest.ACTION_PHASE)
		).setActionName(
			"/cp_definitions/edit_cp_definition_option_value_rel"
		).setCMD(
			"updatePreselected"
		).setRedirect(
			ParamUtil.getString(
				httpServletRequest, "currentUrl",
				_portal.getCurrentURL(httpServletRequest))
		).setParameter(
			"cpDefinitionOptionValueRelId", cpDefinitionOptionValueRelId
		).buildPortletURL();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceProductOptionValueFDSActionProvider.class);

	@Reference
	private CPDefinitionOptionValueRelService
		_cpDefinitionOptionValueRelService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}