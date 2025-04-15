/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.content.web.internal.portlet.action;

import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.exception.CommerceOrderImporterTypeException;
import com.liferay.commerce.order.importer.type.CommerceOrderImporterType;
import com.liferay.commerce.order.importer.type.CommerceOrderImporterTypeRegistry;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_OPEN_ORDER_CONTENT,
		"mvc.command.name=/commerce_open_order_content/view_commerce_order_importer_type"
	},
	service = MVCRenderCommand.class
)
public class ViewCommerceOrderImporterTypeMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			CommerceOrderImporterType commerceOrderImporterType =
				_commerceOrderImporterTypeRegistry.getCommerceOrderImporterType(
					ParamUtil.getString(
						renderRequest, "commerceOrderImporterTypeKey"));

			if (commerceOrderImporterType == null) {
				throw new CommerceOrderImporterTypeException();
			}

			renderRequest.setAttribute(
				CommerceWebKeys.COMMERCE_ORDER_IMPORTER_ITEM,
				commerceOrderImporterType.getCommerceOrderImporterItem(
					_portal.getHttpServletRequest(renderRequest)));
		}
		catch (Exception exception) {
			SessionErrors.add(renderRequest, exception.getClass());

			return "/error.jsp";
		}

		return "/pending_commerce_orders/view_commerce_order_importer_type.jsp";
	}

	@Reference
	private CommerceOrderImporterTypeRegistry
		_commerceOrderImporterTypeRegistry;

	@Reference
	private Portal _portal;

}