/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.fragment.renderer;

import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.portal.kernel.language.Language;
import com.liferay.site.pim.site.initializer.connector.PIMConnectorRegistry;
import com.liferay.site.pim.site.initializer.internal.display.context.EditPIMConnectorFieldMappingsDisplayContext;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Andrea Sbarra
 * @author Stefano Motta
 */
public class EditPIMConnectorFieldMappingsJSPSectionFragmentRenderer
	extends BaseJSPSectionFragmentRenderer
		<EditPIMConnectorFieldMappingsDisplayContext> {

	public EditPIMConnectorFieldMappingsJSPSectionFragmentRenderer(
		Language language, ObjectDefinitionService objectDefinitionService,
		ObjectEntryLocalService objectEntryLocalService,
		ObjectFieldLocalService objectFieldLocalService,
		PIMConnectorRegistry pimConnectorRegistry,
		ServletContext servletContext) {

		super(language, servletContext);

		_objectDefinitionService = objectDefinitionService;
		_objectEntryLocalService = objectEntryLocalService;
		_objectFieldLocalService = objectFieldLocalService;
		_pimConnectorRegistry = pimConnectorRegistry;
	}

	@Override
	public String getLabelKey() {
		return "edit-field-mappings";
	}

	@Override
	protected EditPIMConnectorFieldMappingsDisplayContext getDisplayContext(
		HttpServletRequest httpServletRequest) {

		return new EditPIMConnectorFieldMappingsDisplayContext(
			httpServletRequest, _objectDefinitionService,
			_objectEntryLocalService, _objectFieldLocalService,
			_pimConnectorRegistry);
	}

	private final ObjectDefinitionService _objectDefinitionService;
	private final ObjectEntryLocalService _objectEntryLocalService;
	private final ObjectFieldLocalService _objectFieldLocalService;
	private final PIMConnectorRegistry _pimConnectorRegistry;

}