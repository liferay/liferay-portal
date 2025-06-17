/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.site.cms.site.initializer.internal.display.context.ViewCategoryUsagesDisplayContext;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

/**
 * @author Pei-Jung Lan
 */
@Component(service = FragmentRenderer.class)
public class ViewCategoryUsagesJSPSectionFragmentRenderer
	extends BaseJSPSectionFragmentRenderer<ViewCategoryUsagesDisplayContext> {

	@Override
	public String getLabelKey() {
		return "category-usages";
	}

	@Override
	protected ViewCategoryUsagesDisplayContext getDisplayContext(
		HttpServletRequest httpServletRequest) {

		return new ViewCategoryUsagesDisplayContext(httpServletRequest);
	}

	@Override
	protected String getJSPPath() {
		return "/view_category_usages.jsp";
	}

}