/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.options.web.internal.portlet.action;

import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.constants.CPWebKeys;
import com.liferay.commerce.product.exception.NoSuchCPSpecificationOptionException;
import com.liferay.commerce.product.model.CPSpecificationOption;
import com.liferay.commerce.product.service.CPSpecificationOptionService;
import com.liferay.portal.kernel.exception.PortalException;
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
 * @author Andrea Di Giorgi
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.CP_SPECIFICATION_OPTIONS,
		"mvc.command.name=/cp_specification_options/edit_cp_specification_option"
	},
	service = MVCRenderCommand.class
)
public class EditCPSpecificationOptionMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			_setCPSpecificationOptionRequestAttribute(renderRequest);
			_populatePortletDisplay(renderRequest);
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchCPSpecificationOptionException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(renderRequest, exception.getClass());

				return "/error.jsp";
			}

			throw new PortletException(exception);
		}

		return "/edit_cp_specification_option.jsp";
	}

	private void _populatePortletDisplay(RenderRequest renderRequest) {
		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		portletDisplay.setShowBackIcon(true);
		portletDisplay.setURLBack(
			PortletURLBuilder.create(
				_portal.getControlPanelPortletURL(
					renderRequest, CPPortletKeys.CP_SPECIFICATION_OPTIONS,
					PortletRequest.RENDER_PHASE)
			).setParameter(
				"toolbarItem", "specification-labels"
			).buildString());
	}

	private void _setCPSpecificationOptionRequestAttribute(
			RenderRequest renderRequest)
		throws PortalException {

		long cpSpecificationOptionId = ParamUtil.getLong(
			renderRequest, "cpSpecificationOptionId");

		CPSpecificationOption cpSpecificationOption = null;

		if (cpSpecificationOptionId > 0) {
			cpSpecificationOption =
				_cpSpecificationOptionService.getCPSpecificationOption(
					cpSpecificationOptionId);
		}

		renderRequest.setAttribute(
			CPWebKeys.CP_SPECIFICATION_OPTION, cpSpecificationOption);
	}

	@Reference
	private CPSpecificationOptionService _cpSpecificationOptionService;

	@Reference
	private Portal _portal;

}