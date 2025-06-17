/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR
 * LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.fragment.renderer;

import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.site.cms.site.initializer.internal.display.context.ViewCategoriesDisplayContext;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cheryl Tang
 */
@Component(service = FragmentRenderer.class)
public class ViewCategoriesJSPSectionFragmentRenderer
	extends BaseJSPSectionFragmentRenderer<ViewCategoriesDisplayContext> {

	@Override
	public String getLabelKey() {
		return "categories";
	}

	@Override
	protected ViewCategoriesDisplayContext getDisplayContext(
		HttpServletRequest httpServletRequest) {

		return new ViewCategoriesDisplayContext(
			_assetVocabularyLocalService, httpServletRequest,
			_layoutLocalService, language, _portal);
	}

	@Override
	protected String getJSPPath() {
		return "/view_categories.jsp";
	}

	@Reference
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

}