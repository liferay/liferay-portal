/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.fragment.renderer;

import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.pim.site.initializer.connector.PIMConnectorRegistry;
import com.liferay.site.pim.site.initializer.constants.PIMObjectDefinitionConstants;
import com.liferay.site.pim.site.initializer.internal.display.context.EditPIMConnectorDisplayContext;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Andrea Sbarra
 */
public class EditPIMConnectorJSPSectionFragmentRenderer
	extends BaseJSPSectionFragmentRenderer<EditPIMConnectorDisplayContext> {

	public EditPIMConnectorJSPSectionFragmentRenderer(
		Language language, ListTypeEntryLocalService listTypeEntryLocalService,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectEntryLocalService objectEntryLocalService,
		ObjectFieldLocalService objectFieldLocalService,
		PIMConnectorRegistry pimConnectorRegistry,
		ServletContext servletContext) {

		super(language, servletContext);

		_listTypeEntryLocalService = listTypeEntryLocalService;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectEntryLocalService = objectEntryLocalService;
		_objectFieldLocalService = objectFieldLocalService;
		_pimConnectorRegistry = pimConnectorRegistry;
	}

	@Override
	public String getLabelKey() {
		return "edit-connector";
	}

	@Override
	protected EditPIMConnectorDisplayContext getDisplayContext(
		HttpServletRequest httpServletRequest) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return new EditPIMConnectorDisplayContext(
			httpServletRequest, _listTypeEntryLocalService,
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					PIMObjectDefinitionConstants.
						EXTERNAL_REFERENCE_CODE_CONNECTOR,
					themeDisplay.getCompanyId()),
			_objectEntryLocalService, _objectFieldLocalService,
			_pimConnectorRegistry.getPIMConnectors(
				themeDisplay.getCompanyId()));
	}

	private final ListTypeEntryLocalService _listTypeEntryLocalService;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectEntryLocalService _objectEntryLocalService;
	private final ObjectFieldLocalService _objectFieldLocalService;
	private final PIMConnectorRegistry _pimConnectorRegistry;

}