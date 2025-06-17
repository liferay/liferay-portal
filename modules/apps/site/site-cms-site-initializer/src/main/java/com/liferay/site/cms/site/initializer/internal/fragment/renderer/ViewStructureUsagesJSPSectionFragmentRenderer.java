/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.site.cms.site.initializer.internal.display.context.ViewStructureUsagesDisplayContext;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

/**
 * @author Marco Galluzzi
 */
@Component(service = FragmentRenderer.class)
public class ViewStructureUsagesJSPSectionFragmentRenderer
	extends BaseJSPSectionFragmentRenderer<ViewStructureUsagesDisplayContext> {

	@Override
	public String getLabelKey() {
		return "structure-usages";
	}

	@Override
	protected ViewStructureUsagesDisplayContext getDisplayContext(
		HttpServletRequest httpServletRequest) {

		return new ViewStructureUsagesDisplayContext(
			httpServletRequest, language);
	}

	@Override
	protected String getJSPPath() {
		return "/view_structure_usages.jsp";
	}

}