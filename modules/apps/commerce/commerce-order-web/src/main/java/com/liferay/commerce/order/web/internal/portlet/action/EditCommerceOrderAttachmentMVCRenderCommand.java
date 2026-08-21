/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.portlet.action;

import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.exception.NoSuchOrderAttachmentException;
import com.liferay.commerce.model.CommerceOrderAttachment;
import com.liferay.commerce.order.web.internal.display.context.EditCommerceOrderAttachmentDisplayContext;
import com.liferay.commerce.service.CommerceOrderAttachmentService;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tancredi Covioli
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_ORDER,
		"mvc.command.name=/commerce_order/edit_commerce_order_attachment"
	},
	service = MVCRenderCommand.class
)
public class EditCommerceOrderAttachmentMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			CommerceOrderAttachment commerceOrderAttachment = null;

			long commerceOrderAttachmentId = ParamUtil.getLong(
				renderRequest, "commerceOrderAttachmentId");

			if (commerceOrderAttachmentId > 0) {
				commerceOrderAttachment =
					_commerceOrderAttachmentService.
						fetchCommerceOrderAttachment(commerceOrderAttachmentId);
			}

			long commerceOrderId = ParamUtil.getLong(
				renderRequest, "commerceOrderId");

			if ((commerceOrderAttachment != null) &&
				(commerceOrderAttachment.getCommerceOrderId() !=
					commerceOrderId)) {

				throw new NoSuchOrderAttachmentException();
			}

			renderRequest.setAttribute(
				EditCommerceOrderAttachmentDisplayContext.class.getName(),
				new EditCommerceOrderAttachmentDisplayContext(
					commerceOrderAttachment, commerceOrderId,
					_portal.getHttpServletRequest(renderRequest),
					_listTypeDefinitionLocalService,
					_listTypeEntryLocalService));
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchOrderAttachmentException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(renderRequest, exception.getClass());

				return "/error.jsp";
			}

			throw new PortletException(exception);
		}

		return "/commerce_order/edit_commerce_order_attachment.jsp";
	}

	@Reference
	private CommerceOrderAttachmentService _commerceOrderAttachmentService;

	@Reference
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	@Reference
	private ListTypeEntryLocalService _listTypeEntryLocalService;

	@Reference
	private Portal _portal;

}