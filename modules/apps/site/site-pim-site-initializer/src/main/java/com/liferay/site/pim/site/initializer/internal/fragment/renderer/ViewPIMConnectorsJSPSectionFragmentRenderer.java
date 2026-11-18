/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.fragment.renderer;

import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.pim.site.initializer.constants.PIMObjectDefinitionConstants;
import com.liferay.site.pim.site.initializer.internal.display.context.ViewPIMConnectorsDisplayContext;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Andrea Sbarra
 */
public class ViewPIMConnectorsJSPSectionFragmentRenderer
	extends BaseJSPSectionFragmentRenderer<ViewPIMConnectorsDisplayContext> {

	public ViewPIMConnectorsJSPSectionFragmentRenderer(
		Language language,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ServletContext servletContext) {

		super(language, servletContext);

		_objectDefinitionLocalService = objectDefinitionLocalService;
	}

	@Override
	public String getLabelKey() {
		return "connectors";
	}

	@Override
	protected ViewPIMConnectorsDisplayContext getDisplayContext(
		HttpServletRequest httpServletRequest) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return new ViewPIMConnectorsDisplayContext(
			httpServletRequest,
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					PIMObjectDefinitionConstants.
						EXTERNAL_REFERENCE_CODE_CONNECTOR,
					themeDisplay.getCompanyId()));
	}

	private final ObjectDefinitionLocalService _objectDefinitionLocalService;

}