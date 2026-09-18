/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.site.pim.site.initializer.internal.display.context.PIMConnectorFieldMappingDisplayContext;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 */
@Component(service = FragmentRenderer.class)
public class PIMConnectorFieldMappingJSPSectionFragmentRenderer
	extends BaseJSPSectionFragmentRenderer
		<PIMConnectorFieldMappingDisplayContext> {

	@Override
	public String getLabelKey() {
		return "field-mapping";
	}

	@Override
	protected PIMConnectorFieldMappingDisplayContext getDisplayContext(
		HttpServletRequest httpServletRequest) {

		return new PIMConnectorFieldMappingDisplayContext(
			httpServletRequest, _objectEntryLocalService);
	}

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

}