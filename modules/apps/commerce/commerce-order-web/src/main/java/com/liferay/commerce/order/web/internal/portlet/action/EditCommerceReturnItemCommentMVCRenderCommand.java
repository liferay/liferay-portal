/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.portlet.action;

import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.exception.NoSuchOrderException;
import com.liferay.commerce.order.web.internal.display.context.CommerceReturnItemCommentEditDisplayContext;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Danny Situ
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_RETURN,
		"mvc.command.name=/commerce_return/edit_commerce_return_item_comment"
	},
	service = MVCRenderCommand.class
)
public class EditCommerceReturnItemCommentMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			CommerceReturnItemCommentEditDisplayContext
				commerceReturnItemCommentEditDisplayContext =
					new CommerceReturnItemCommentEditDisplayContext(
						renderRequest);

			renderRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT,
				commerceReturnItemCommentEditDisplayContext);

			_populatePortletDisplay(renderRequest);
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchOrderException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(renderRequest, exception.getClass());

				return "/error.jsp";
			}

			throw new PortletException(exception);
		}

		return "/commerce_return_item/edit_commerce_return_item_comment.jsp";
	}

	private void _populatePortletDisplay(RenderRequest renderRequest) {
		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		portletDisplay.setShowBackIcon(true);
		portletDisplay.setURLBack(
			PortletURLBuilder.create(
				_portal.getControlPanelPortletURL(
					renderRequest, CommercePortletKeys.COMMERCE_RETURN,
					PortletRequest.RENDER_PHASE)
			).setMVCRenderCommandName(
				"/commerce_return/edit_commerce_return_item"
			).setParameter(
				"commerceReturnId",
				ParamUtil.getLong(renderRequest, "commerceReturnId")
			).setParameter(
				"commerceReturnItemId",
				ParamUtil.getLong(renderRequest, "commerceReturnItemId")
			).setWindowState(
				LiferayWindowState.POP_UP
			).buildString());
	}

	@Reference
	private Portal _portal;

}