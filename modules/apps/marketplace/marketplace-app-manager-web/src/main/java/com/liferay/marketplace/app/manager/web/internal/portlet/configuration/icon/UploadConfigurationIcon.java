/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.marketplace.app.manager.web.internal.portlet.configuration.icon;

import com.liferay.marketplace.app.manager.web.internal.constants.MarketplaceAppManagerPortletKeys;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.configuration.icon.BasePortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.configuration.icon.PortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Enoch Chu
 */
@Component(
	property = "jakarta.portlet.name=" + MarketplaceAppManagerPortletKeys.MARKETPLACE_APP_MANAGER,
	service = PortletConfigurationIcon.class
)
public class UploadConfigurationIcon extends BasePortletConfigurationIcon {

	@Override
	public String getMessage(PortletRequest portletRequest) {
		return _language.get(getLocale(portletRequest), "upload");
	}

	@Override
	public String getURL(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				portletRequest,
				MarketplaceAppManagerPortletKeys.MARKETPLACE_APP_MANAGER,
				PortletRequest.RENDER_PHASE)
		).setMVCPath(
			"/install_local_app.jsp"
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	@Override
	public double getWeight() {
		return 102;
	}

	@Override
	public boolean isShow(PortletRequest portletRequest) {
		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		User user = themeDisplay.getUser();

		return !user.isGuestUser();
	}

	@Override
	public boolean isUseDialog() {
		return true;
	}

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}