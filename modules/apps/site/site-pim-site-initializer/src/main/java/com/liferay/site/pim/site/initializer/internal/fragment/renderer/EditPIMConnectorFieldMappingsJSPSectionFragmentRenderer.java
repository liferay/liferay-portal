/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.site.pim.site.initializer.connector.PIMConnectorRegistry;
import com.liferay.site.pim.site.initializer.internal.display.context.EditPIMConnectorFieldMappingsDisplayContext;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 * @author Stefano Motta
 */
@Component(service = FragmentRenderer.class)
public class EditPIMConnectorFieldMappingsJSPSectionFragmentRenderer
	extends BaseJSPSectionFragmentRenderer
		<EditPIMConnectorFieldMappingsDisplayContext> {

	@Override
	public String getLabelKey() {
		return "edit-field-mappings";
	}

	@Override
	protected EditPIMConnectorFieldMappingsDisplayContext getDisplayContext(
		HttpServletRequest httpServletRequest) {

		return new EditPIMConnectorFieldMappingsDisplayContext(
			httpServletRequest, _objectEntryLocalService,
			_objectFieldLocalService, _pimConnectorRegistry);
	}

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private PIMConnectorRegistry _pimConnectorRegistry;

}