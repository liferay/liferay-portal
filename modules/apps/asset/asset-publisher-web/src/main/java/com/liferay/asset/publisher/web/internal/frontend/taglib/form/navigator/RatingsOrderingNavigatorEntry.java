/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.frontend.taglib.form.navigator;

import com.liferay.asset.publisher.constants.AssetPublisherConstants;
import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.frontend.taglib.form.navigator.FormNavigatorEntry;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import jakarta.servlet.ServletContext;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael Bowerman
 */
@Component(
	property = "form.navigator.entry.order:Integer=200",
	service = FormNavigatorEntry.class
)
public class RatingsOrderingNavigatorEntry
	extends BaseConfigurationFormNavigatorEntry {

	@Override
	public String getCategoryKey() {
		return AssetPublisherConstants.CATEGORY_KEY_ASSET_SELECTION;
	}

	@Override
	public String getKey() {
		return "ordering";
	}

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	public boolean isVisible(User user, Object object) {
		if (!isDynamicAssetSelection()) {
			return false;
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		Portlet portlet = _portletLocalService.getPortletById(
			themeDisplay.getCompanyId(), portletDisplay.getPortletResource());

		return Objects.equals(
			portlet.getRootPortletId(),
			AssetPublisherPortletKeys.HIGHEST_RATED_ASSETS);
	}

	@Override
	protected String getJspPath() {
		return "/configuration/ratings_ordering.jsp";
	}

	@Reference
	private PortletLocalService _portletLocalService;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.asset.publisher.web)"
	)
	private ServletContext _servletContext;

}