/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR
 * LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.site.cms.site.initializer.internal.display.context.EditCategoryDisplayContext;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cheryl Tang
 */
@Component(service = FragmentRenderer.class)
public class EditCategoryJSPSectionFragmentRenderer
	extends BaseJSPSectionFragmentRenderer<EditCategoryDisplayContext> {

	@Override
	public String getLabelKey() {
		return "edit-category";
	}

	@Override
	protected EditCategoryDisplayContext getDisplayContext(
		HttpServletRequest httpServletRequest) {

		return new EditCategoryDisplayContext(
			httpServletRequest, _layoutLocalService, language, _portal);
	}

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

}