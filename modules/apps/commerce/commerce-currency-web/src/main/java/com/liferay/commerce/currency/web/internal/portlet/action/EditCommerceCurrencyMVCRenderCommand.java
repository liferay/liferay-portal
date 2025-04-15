/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.currency.web.internal.portlet.action;

import com.liferay.commerce.currency.constants.CommerceCurrencyPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
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
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommerceCurrencyPortletKeys.COMMERCE_CURRENCY,
		"mvc.command.name=/commerce_currency/edit_commerce_currency"
	},
	service = MVCRenderCommand.class
)
public class EditCommerceCurrencyMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		_populatePortletDisplay(renderRequest);

		return "/edit_commerce_currency.jsp";
	}

	private void _populatePortletDisplay(RenderRequest renderRequest) {
		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		portletDisplay.setShowBackIcon(true);
		portletDisplay.setURLBack(
			PortletURLBuilder.create(
				_portal.getControlPanelPortletURL(
					renderRequest,
					CommerceCurrencyPortletKeys.COMMERCE_CURRENCY,
					PortletRequest.RENDER_PHASE)
			).buildString());
	}

	@Reference
	private Portal _portal;

}