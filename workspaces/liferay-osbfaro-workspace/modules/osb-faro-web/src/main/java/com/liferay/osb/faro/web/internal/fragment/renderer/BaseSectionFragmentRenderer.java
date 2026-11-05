/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.portal.kernel.language.LanguageUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

/**
 * @author Eudaldo Alonso
 */
public abstract class BaseSectionFragmentRenderer extends BaseFragmentRenderer {

	public static final String SECTION =
		BaseSectionFragmentRenderer.class.getName() + "#SECTION";

	@Override
	public String getCollectionKey() {
		return "liferay-data-platform";
	}

	@Override
	public String getLabel(Locale locale) {
		return LanguageUtil.get(locale, getLabelKey());
	}

	@Override
	public void render(
		FragmentRendererContext fragmentRendererContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		httpServletRequest.setAttribute(SECTION, getSection());

		try {
			super.render(
				fragmentRendererContext, httpServletRequest,
				httpServletResponse);
		}
		finally {
			httpServletRequest.removeAttribute(SECTION);
		}
	}

	protected abstract String getLabelKey();

	protected abstract String getSection();

}