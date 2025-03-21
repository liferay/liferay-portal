/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.taglib.internal.display.context;

import com.liferay.exportimport.configuration.ExportImportServiceConfiguration;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerBoolean;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author Daniel Szimko
 */
public class RenderControlsDisplayContext {

	public RenderControlsDisplayContext(HttpServletRequest httpServletRequest) {
		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public boolean includeThumbnailsAndPreviewsDuringStaging()
		throws ConfigurationException {

		ExportImportServiceConfiguration exportImportServiceConfiguration =
			ConfigurationProviderUtil.getCompanyConfiguration(
				ExportImportServiceConfiguration.class,
				CompanyThreadLocal.getCompanyId());

		if (!isStagingEnabled() ||
			exportImportServiceConfiguration.
				includeThumbnailsAndPreviewsDuringStaging()) {

			return true;
		}

		return false;
	}

	public boolean isControlCheckboxEnabled(
			PortletDataHandlerBoolean control,
			Map<String, String[]> parameterMap)
		throws ConfigurationException {

		String controlName = control.getControlName();

		if (controlName.equals(_DOCUMENT_LIBRARY_PREVIEWS_AND_THUMBNAILS)) {
			return includeThumbnailsAndPreviewsDuringStaging();
		}

		if (MapUtil.getBoolean(
				parameterMap, controlName, control.getDefaultState()) ||
			MapUtil.getBoolean(
				parameterMap, PortletDataHandlerKeys.PORTLET_DATA_ALL)) {

			return true;
		}

		return false;
	}

	public boolean isStagingEnabled() {
		Group group = _themeDisplay.getScopeGroup();

		return group.isStaged();
	}

	private static final String _DOCUMENT_LIBRARY_PREVIEWS_AND_THUMBNAILS =
		"previews-and-thumbnails";

	private final ThemeDisplay _themeDisplay;

}