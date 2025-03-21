/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.options.web.internal.portlet.action;

import com.liferay.commerce.product.constants.CPConstants;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.constants.CPWebKeys;
import com.liferay.commerce.product.exception.NoSuchCPOptionCategoryException;
import com.liferay.commerce.product.model.CPOptionCategory;
import com.liferay.commerce.product.options.web.internal.display.context.CPOptionCategoryDisplayContext;
import com.liferay.commerce.product.options.web.internal.portlet.action.helper.ActionHelper;
import com.liferay.commerce.product.service.CPOptionCategoryService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

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
		"jakarta.portlet.name=" + CPPortletKeys.CP_SPECIFICATION_OPTIONS,
		"mvc.command.name=/cp_specification_options/view_cp_option_categories"
	},
	service = MVCRenderCommand.class
)
public class ViewCPOptionCategoriesMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			CPOptionCategoryDisplayContext cpOptionCategoryDisplayContext =
				new CPOptionCategoryDisplayContext(
					_actionHelper, _portal.getHttpServletRequest(renderRequest),
					_cpOptionCategoryService, _portletResourcePermission);

			renderRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT,
				cpOptionCategoryDisplayContext);

			setCPOptionCategoryRequestAttribute(renderRequest);
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchCPOptionCategoryException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(renderRequest, exception.getClass());

				return "/error.jsp";
			}

			throw new PortletException(exception);
		}

		return "/view_cp_option_categories.jsp";
	}

	protected void setCPOptionCategoryRequestAttribute(
			RenderRequest renderRequest)
		throws PortalException {

		long cpOptionCategoryId = ParamUtil.getLong(
			renderRequest, "cpOptionCategoryId");

		CPOptionCategory cpOptionCategory = null;

		if (cpOptionCategoryId > 0) {
			cpOptionCategory = _cpOptionCategoryService.getCPOptionCategory(
				cpOptionCategoryId);
		}

		renderRequest.setAttribute(
			CPWebKeys.CP_OPTION_CATEGORY, cpOptionCategory);
	}

	@Reference
	private ActionHelper _actionHelper;

	@Reference
	private CPOptionCategoryService _cpOptionCategoryService;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(resource.name=" + CPConstants.RESOURCE_NAME_PRODUCT + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}