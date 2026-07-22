/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.pim.site.initializer.internal.constants.PIMObjectDefinitionConstants;
import com.liferay.site.pim.site.initializer.internal.display.context.ViewPIMConnectorsDisplayContext;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 */
@Component(service = FragmentRenderer.class)
public class ViewPIMConnectorsJSPSectionFragmentRenderer
	extends BaseJSPSectionFragmentRenderer<ViewPIMConnectorsDisplayContext> {

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

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

}