/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet.internal.filters.util;

import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.service.ThemeLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.servlet.filters.util.CacheFileNameContributor;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carlos Sierra Andrés
 */
@Component(service = CacheFileNameContributor.class)
public class ThemeIdCacheFileNameContributor
	implements CacheFileNameContributor {

	@Override
	public String getParameterName() {
		return "themeId";
	}

	@Override
	public String getParameterValue(HttpServletRequest httpServletRequest) {
		String themeId = httpServletRequest.getParameter(getParameterName());

		Theme theme = _themeLocalService.fetchTheme(
			_portal.getCompanyId(httpServletRequest), themeId);

		if (theme == null) {
			return null;
		}

		return themeId;
	}

	@Reference
	private Portal _portal;

	@Reference
	private ThemeLocalService _themeLocalService;

}