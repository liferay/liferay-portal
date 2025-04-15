/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipment.web.internal.frontend.data.set.provider;

import com.liferay.commerce.constants.CommerceActionKeys;
import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.constants.CommerceShipmentFDSNames;
import com.liferay.commerce.frontend.model.Shipment;
import com.liferay.frontend.data.set.provider.FDSActionProvider;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowStateException;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "fds.data.provider.key=" + CommerceShipmentFDSNames.ORDER_SHIPMENTS,
	service = FDSActionProvider.class
)
public class CommerceOrderShipmentFDSActionProvider
	implements FDSActionProvider {

	@Override
	public List<DropdownItem> getDropdownItems(
			long groupId, HttpServletRequest httpServletRequest, Object model)
		throws PortalException {

		Shipment shipment = (Shipment)model;

		return DropdownItemListBuilder.add(
			() -> _portletResourcePermission.contains(
				PermissionThreadLocal.getPermissionChecker(), null,
				CommerceActionKeys.MANAGE_COMMERCE_SHIPMENTS),
			dropdownItem -> {
				dropdownItem.setHref(
					_getShipmentEditURL(
						shipment.getShipmentId(), httpServletRequest));
				dropdownItem.setLabel(
					_language.get(httpServletRequest, "edit"));
			}
		).add(
			() -> _portletResourcePermission.contains(
				PermissionThreadLocal.getPermissionChecker(), null,
				CommerceActionKeys.MANAGE_COMMERCE_SHIPMENTS),
			dropdownItem -> {
				dropdownItem.setHref(
					_getShipmentDeleteURL(
						shipment.getShipmentId(), httpServletRequest));
				dropdownItem.setLabel(
					_language.get(httpServletRequest, "delete"));
				dropdownItem.setTarget("modal-lg");
			}
		).build();
	}

	private String _getShipmentDeleteURL(
		long commerceShipmentId, HttpServletRequest httpServletRequest) {

		PortletURL portletURL = PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, CommercePortletKeys.COMMERCE_SHIPMENT,
				ActionRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/commerce_shipment/delete_commerce_shipment"
		).setRedirect(
			_portal.getCurrentURL(httpServletRequest)
		).setParameter(
			"commerceShipmentId", commerceShipmentId
		).buildPortletURL();

		try {
			portletURL.setWindowState(LiferayWindowState.POP_UP);
		}
		catch (WindowStateException windowStateException) {
			_log.error(windowStateException);
		}

		return portletURL.toString();
	}

	private String _getShipmentEditURL(
		long commerceShipmentId, HttpServletRequest httpServletRequest) {

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, CommercePortletKeys.COMMERCE_SHIPMENT,
				ActionRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/commerce_shipment/edit_commerce_shipment"
		).setParameter(
			"commerceOrderId",
			ParamUtil.getLong(httpServletRequest, "commerceOrderId")
		).setParameter(
			"commerceShipmentId", commerceShipmentId
		).buildString();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceOrderShipmentFDSActionProvider.class);

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(resource.name=" + CommerceConstants.RESOURCE_NAME_COMMERCE_SHIPMENT + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}