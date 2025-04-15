/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipping.engine.fixed.web.internal.frontend.data.set.provider;

import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.shipping.engine.fixed.web.internal.constants.CommerceShippingFixedOptionFDSNames;
import com.liferay.commerce.shipping.engine.fixed.web.internal.model.ShippingFixedOptionSetting;
import com.liferay.frontend.data.set.provider.FDSActionProvider;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pedro Victor Silvestre
 */
@Component(
	property = "fds.data.provider.key=" + CommerceShippingFixedOptionFDSNames.SHIPPING_FIXED_OPTION_SETTINGS,
	service = FDSActionProvider.class
)
public class CommerceShippingFixedOptionSettingFDSActionProvider
	implements FDSActionProvider {

	@Override
	public List<DropdownItem> getDropdownItems(
			long groupId, HttpServletRequest httpServletRequest, Object model)
		throws PortalException {

		ShippingFixedOptionSetting shippingFixedOptionSetting =
			(ShippingFixedOptionSetting)model;

		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.setHref(
					_getShippingFixedOptionSettingEditURL(
						httpServletRequest,
						shippingFixedOptionSetting.
							getShippingFixedOptionSettingId()));
				dropdownItem.setLabel(
					_language.get(httpServletRequest, "edit"));
				dropdownItem.setTarget("sidePanel");
			}
		).add(
			dropdownItem -> {
				dropdownItem.setHref(
					_getShippingFixedOptionSettingDeleteURL(
						httpServletRequest,
						shippingFixedOptionSetting.
							getShippingFixedOptionSettingId()));
				dropdownItem.setLabel(
					_language.get(httpServletRequest, "delete"));
			}
		).build();
	}

	private String _getShippingFixedOptionSettingDeleteURL(
		HttpServletRequest httpServletRequest,
		long shippingFixedOptionSettingId) {

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest,
				CommercePortletKeys.COMMERCE_SHIPPING_METHODS,
				PortletRequest.ACTION_PHASE)
		).setActionName(
			"/commerce_shipping_methods/edit_commerce_shipping_fixed_option_rel"
		).setCMD(
			Constants.DELETE
		).setRedirect(
			ParamUtil.getString(
				httpServletRequest, "currentUrl",
				_portal.getCurrentURL(httpServletRequest))
		).setParameter(
			"commerceShippingFixedOptionRelId", shippingFixedOptionSettingId
		).buildString();
	}

	private String _getShippingFixedOptionSettingEditURL(
			HttpServletRequest httpServletRequest,
			long shippingFixedOptionSettingId)
		throws Exception {

		return PortletURLBuilder.create(
			PortletProviderUtil.getPortletURL(
				httpServletRequest, CommerceShippingMethod.class.getName(),
				PortletProvider.Action.EDIT)
		).setMVCRenderCommandName(
			"/commerce_shipping_methods/edit_commerce_shipping_fixed_option_rel"
		).setParameter(
			"commerceShippingFixedOptionRelId", shippingFixedOptionSettingId
		).setParameter(
			"commerceShippingMethodId",
			ParamUtil.getLong(httpServletRequest, "commerceShippingMethodId")
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}