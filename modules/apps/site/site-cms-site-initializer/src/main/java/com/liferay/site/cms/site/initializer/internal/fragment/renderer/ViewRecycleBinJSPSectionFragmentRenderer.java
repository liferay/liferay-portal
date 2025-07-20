/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.site.cms.site.initializer.internal.display.context.ViewRecycleBinSectionDisplayContext;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

/**
 * @author Pedro Leite
 */
@Component(service = FragmentRenderer.class)
public class ViewRecycleBinJSPSectionFragmentRenderer
	extends BaseJSPSectionFragmentRenderer
		<ViewRecycleBinSectionDisplayContext> {

	@Override
	public String getLabelKey() {
		return "recycle-bin";
	}

	@Override
	protected ViewRecycleBinSectionDisplayContext getDisplayContext(
		HttpServletRequest httpServletRequest) {

		return new ViewRecycleBinSectionDisplayContext(httpServletRequest);
	}

	@Override
	protected String getJSPPath() {
		return "/view_recycle_bin.jsp";
	}

}