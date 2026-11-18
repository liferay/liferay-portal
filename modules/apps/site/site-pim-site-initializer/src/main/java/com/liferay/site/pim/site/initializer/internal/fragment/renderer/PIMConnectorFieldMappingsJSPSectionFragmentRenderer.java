/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.fragment.renderer;

import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.pim.site.initializer.connector.PIMConnectorRegistry;
import com.liferay.site.pim.site.initializer.constants.PIMObjectDefinitionConstants;
import com.liferay.site.pim.site.initializer.internal.display.context.PIMConnectorFieldMappingsDisplayContext;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Andrea Sbarra
 * @author Stefano Motta
 */
public class PIMConnectorFieldMappingsJSPSectionFragmentRenderer
	extends BaseJSPSectionFragmentRenderer
		<PIMConnectorFieldMappingsDisplayContext> {

	public PIMConnectorFieldMappingsJSPSectionFragmentRenderer(
		Language language,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectEntryLocalService objectEntryLocalService,
		PIMConnectorRegistry pimConnectorRegistry,
		ServletContext servletContext) {

		super(language, servletContext);

		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectEntryLocalService = objectEntryLocalService;
		_pimConnectorRegistry = pimConnectorRegistry;
	}

	@Override
	public String getLabelKey() {
		return "field-mappings";
	}

	@Override
	protected PIMConnectorFieldMappingsDisplayContext getDisplayContext(
		HttpServletRequest httpServletRequest) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return new PIMConnectorFieldMappingsDisplayContext(
			httpServletRequest,
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					PIMObjectDefinitionConstants.
						EXTERNAL_REFERENCE_CODE_CONNECTOR,
					themeDisplay.getCompanyId()),
			_objectEntryLocalService, _pimConnectorRegistry);
	}

	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectEntryLocalService _objectEntryLocalService;
	private final PIMConnectorRegistry _pimConnectorRegistry;

}