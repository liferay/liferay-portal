/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.options.web.internal.portlet.action;

import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.constants.CPWebKeys;
import com.liferay.commerce.product.exception.NoSuchCPOptionValueException;
import com.liferay.commerce.product.model.CPOptionValue;
import com.liferay.commerce.product.options.web.internal.display.context.CPOptionValueDisplayContext;
import com.liferay.commerce.product.service.CPOptionValueService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
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
 * @author Marco Leo
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.CP_OPTIONS,
		"mvc.command.name=/cp_options/edit_cp_option_value"
	},
	service = MVCRenderCommand.class
)
public class EditCPOptionValueMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			_setCPOptionValueRequestAttribute(renderRequest);
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchCPOptionValueException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(renderRequest, exception.getClass());

				return "/error.jsp";
			}

			throw new PortletException(exception);
		}

		return "/edit_cp_option_value.jsp";
	}

	private void _setCPOptionValueRequestAttribute(RenderRequest renderRequest)
		throws PortalException {

		long cpOptionValueId = ParamUtil.getLong(
			renderRequest, "cpOptionValueId");

		CPOptionValue cpOptionValue = null;

		if (cpOptionValueId > 0) {
			cpOptionValue = _cpOptionValueService.getCPOptionValue(
				cpOptionValueId);
		}

		renderRequest.setAttribute(CPWebKeys.CP_OPTION_VALUE, cpOptionValue);
		renderRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT,
			new CPOptionValueDisplayContext(cpOptionValue, _portal));
	}

	@Reference
	private CPOptionValueService _cpOptionValueService;

	@Reference
	private Portal _portal;

}