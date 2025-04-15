/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.web.internal.portlet.action;

import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.product.portlet.action.ActionHelper;
import com.liferay.commerce.product.type.virtual.web.internal.display.context.CPDefinitionVirtualSettingDisplayContext;
import com.liferay.commerce.product.type.virtual.web.internal.portlet.action.helper.CPDefinitionVirtualSettingActionHelper;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.item.selector.ItemSelector;
import com.liferay.journal.service.JournalArticleService;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_ORDER,
		"mvc.command.name=/commerce_order/edit_commerce_virtual_order_item_file_entry"
	},
	service = MVCRenderCommand.class
)
public class EditCommerceVirtualOrderItemFileEntryMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher(
					"/edit_commerce_virtual_order_item_file_entry.jsp");

			HttpServletRequest httpServletRequest =
				_portal.getHttpServletRequest(renderRequest);
			HttpServletResponse httpServletResponse =
				_portal.getHttpServletResponse(renderResponse);

			CPDefinitionVirtualSettingDisplayContext
				cpDefinitionVirtualSettingDisplayContext =
					new CPDefinitionVirtualSettingDisplayContext(
						_actionHelper, httpServletRequest, _dlAppService,
						_journalArticleService,
						_cpDefinitionVirtualSettingActionHelper, _itemSelector);

			renderRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT,
				cpDefinitionVirtualSettingDisplayContext);

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			if (exception instanceof PrincipalException) {
				SessionErrors.add(renderRequest, exception.getClass());

				return "/error.jsp";
			}

			throw new PortletException(exception);
		}

		return MVCRenderConstants.MVC_PATH_VALUE_SKIP_DISPATCH;
	}

	@Reference
	private ActionHelper _actionHelper;

	@Reference
	private CPDefinitionVirtualSettingActionHelper
		_cpDefinitionVirtualSettingActionHelper;

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private JournalArticleService _journalArticleService;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.product.type.virtual.web)"
	)
	private ServletContext _servletContext;

}