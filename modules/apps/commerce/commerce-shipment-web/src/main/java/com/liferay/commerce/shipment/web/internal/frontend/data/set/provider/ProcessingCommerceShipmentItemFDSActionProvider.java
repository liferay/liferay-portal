/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipment.web.internal.frontend.data.set.provider;

import com.liferay.commerce.constants.CommerceActionKeys;
import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.constants.CommerceShipmentConstants;
import com.liferay.commerce.constants.CommerceShipmentFDSNames;
import com.liferay.commerce.frontend.model.ShipmentItem;
import com.liferay.commerce.model.CommerceShipment;
import com.liferay.commerce.model.CommerceShipmentItem;
import com.liferay.commerce.service.CommerceShipmentItemService;
import com.liferay.frontend.data.set.provider.FDSActionProvider;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
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
 * @author Alec Sloan
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "fds.data.provider.key=" + CommerceShipmentFDSNames.PROCESSING_SHIPMENT_ITEMS,
	service = FDSActionProvider.class
)
public class ProcessingCommerceShipmentItemFDSActionProvider
	implements FDSActionProvider {

	@Override
	public List<DropdownItem> getDropdownItems(
			long groupId, HttpServletRequest httpServletRequest, Object model)
		throws PortalException {

		ShipmentItem shipmentItem = (ShipmentItem)model;

		CommerceShipmentItem commerceShipmentItem =
			_commerceShipmentItemService.getCommerceShipmentItem(
				shipmentItem.getShipmentItemId());

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		return DropdownItemListBuilder.add(
			() -> {
				CommerceShipment commerceShipment =
					commerceShipmentItem.getCommerceShipment();

				return _portletResourcePermission.contains(
					permissionChecker, null,
					CommerceActionKeys.MANAGE_COMMERCE_SHIPMENTS) &&
					   (commerceShipment.getStatus() ==
						   CommerceShipmentConstants.
							   SHIPMENT_STATUS_PROCESSING);
			},
			dropdownItem -> {
				dropdownItem.setHref(
					_getShipmentItemEditURL(
						commerceShipmentItem, httpServletRequest));
				dropdownItem.setLabel(
					_language.get(httpServletRequest, "edit"));
				dropdownItem.setTarget("sidePanel");
			}
		).add(
			() -> _portletResourcePermission.contains(
				permissionChecker, null,
				CommerceActionKeys.MANAGE_COMMERCE_SHIPMENTS),
			dropdownItem -> {
				dropdownItem.setHref(
					_getShipmentItemDeleteURL(
						shipmentItem.getShipmentItemId(), httpServletRequest));
				dropdownItem.setLabel(
					_language.get(httpServletRequest, "delete"));
				dropdownItem.setTarget("modal");
			}
		).build();
	}

	private String _getShipmentItemDeleteURL(
		long commerceShipmentItemId, HttpServletRequest httpServletRequest) {

		PortletURL portletURL = PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, CommercePortletKeys.COMMERCE_SHIPMENT,
				ActionRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/commerce_shipment/delete_commerce_shipment"
		).setRedirect(
			_portal.getCurrentURL(httpServletRequest)
		).setParameter(
			"commerceShipmentItemId", commerceShipmentItemId
		).buildPortletURL();

		try {
			portletURL.setWindowState(LiferayWindowState.POP_UP);
		}
		catch (WindowStateException windowStateException) {
			_log.error(windowStateException);
		}

		return portletURL.toString();
	}

	private String _getShipmentItemEditURL(
		CommerceShipmentItem commerceShipmentItem,
		HttpServletRequest httpServletRequest) {

		PortletURL portletURL = PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, CommercePortletKeys.COMMERCE_SHIPMENT,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/commerce_shipment/edit_commerce_shipment_item"
		).setRedirect(
			_portal.getCurrentURL(httpServletRequest)
		).setParameter(
			"commerceShipmentId", commerceShipmentItem.getCommerceShipmentId()
		).setParameter(
			"commerceShipmentItemId",
			commerceShipmentItem.getCommerceShipmentItemId()
		).buildPortletURL();

		try {
			portletURL.setWindowState(LiferayWindowState.POP_UP);
		}
		catch (WindowStateException windowStateException) {
			_log.error(windowStateException);
		}

		return portletURL.toString();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ProcessingCommerceShipmentItemFDSActionProvider.class);

	@Reference
	private CommerceShipmentItemService _commerceShipmentItemService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(resource.name=" + CommerceConstants.RESOURCE_NAME_COMMERCE_SHIPMENT + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}