/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.measurement.unit.web.internal.portlet.action;

import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
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
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.CP_MEASUREMENT_UNIT,
		"mvc.command.name=/cp_measurement_unit/edit_cp_measurement_unit"
	},
	service = MVCRenderCommand.class
)
public class EditCPMeasurementUnitMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		_populatePortletDisplay(renderRequest);

		return "/edit_cp_measurement_unit.jsp";
	}

	private void _populatePortletDisplay(RenderRequest renderRequest) {
		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		portletDisplay.setShowBackIcon(true);
		portletDisplay.setURLBack(
			PortletURLBuilder.create(
				_portal.getControlPanelPortletURL(
					renderRequest, CPPortletKeys.CP_MEASUREMENT_UNIT,
					PortletRequest.RENDER_PHASE)
			).setParameter(
				"toolbarItem",
				() -> {
					int type = ParamUtil.getInteger(renderRequest, "type");

					if (type == 1) {
						return "view-all-weight-product-measurement-units";
					}
					else if (type == 2) {
						return "view-all-unit-product-measurement-units";
					}

					return "view-all-dimension-product-measurement-units";
				}
			).setParameter(
				"type", ParamUtil.getInteger(renderRequest, "type")
			).buildString());
	}

	@Reference
	private Portal _portal;

}