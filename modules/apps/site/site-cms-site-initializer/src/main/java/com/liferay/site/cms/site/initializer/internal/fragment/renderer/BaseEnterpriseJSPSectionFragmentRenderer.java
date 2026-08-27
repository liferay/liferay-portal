/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.learn.LearnMessageUtil;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * @author Verónica González
 */
public abstract class BaseEnterpriseJSPSectionFragmentRenderer<T>
	extends BaseJSPSectionFragmentRenderer<T> {

	@Override
	public void render(
			FragmentRendererContext fragmentRendererContext,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		if (LicenseManagerUtil.isFreeTier()) {
			ComponentTagUtil.renderComponent(
				"EnterpriseOnlyPlaceholder", httpServletRequest,
				httpServletResponse, "site-cms-site-initializer",
				HashMapBuilder.<String, Object>put(
					"learnResources",
					LearnMessageUtil.getReactDataJSONObject(
						"site-cms-site-initializer")
				).build(),
				servletContext);

			return;
		}

		super.render(
			fragmentRendererContext, httpServletRequest, httpServletResponse);
	}

}