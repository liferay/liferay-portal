/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.order.content.web.internal.portlet.action;

import com.liferay.commerce.product.type.virtual.order.constants.CommerceVirtualOrderPortletKeys;
import com.liferay.commerce.product.type.virtual.order.service.CommerceVirtualOrderItemService;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.PortletException;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.io.File;
import java.io.FileInputStream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommerceVirtualOrderPortletKeys.COMMERCE_VIRTUAL_ORDER_ITEM_CONTENT,
		"mvc.command.name=/commerce_virtual_order_item_content/download_commerce_virtual_order_item"
	},
	service = MVCResourceCommand.class
)
public class DownloadCommerceVirtualOrderItemMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	public void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws PortletException {

		long commerceVirtualOrderItemId = ParamUtil.getLong(
			resourceRequest, "commerceVirtualOrderItemId");
		long commerceVirtualOrderItemFileEntryId = ParamUtil.getLong(
			resourceRequest, "commerceVirtualOrderItemFileEntryId");

		try {
			File file = _commerceVirtualOrderItemService.getFile(
				commerceVirtualOrderItemId,
				commerceVirtualOrderItemFileEntryId);

			PortletResponseUtil.sendFile(
				resourceRequest, resourceResponse, file.getName(),
				new FileInputStream(file), 0,
				MimeTypesUtil.getContentType(file),
				HttpHeaders.CONTENT_DISPOSITION_ATTACHMENT);
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DownloadCommerceVirtualOrderItemMVCResourceCommand.class);

	@Reference
	private CommerceVirtualOrderItemService _commerceVirtualOrderItemService;

}