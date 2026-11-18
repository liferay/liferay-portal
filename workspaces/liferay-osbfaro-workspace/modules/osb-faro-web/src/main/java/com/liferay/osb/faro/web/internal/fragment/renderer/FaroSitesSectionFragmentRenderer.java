/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemList;
import com.liferay.osb.faro.web.internal.display.context.SitesNavigationDisplayContext;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

/**
 * @author Eudaldo Alonso
 */
@Component(service = FragmentRenderer.class)
public class FaroSitesSectionFragmentRenderer
	extends BaseSectionFragmentRenderer {

	@Override
	protected String getLabelKey() {
		return "sites";
	}

	@Override
	protected String getModule() {
		return "{Sites} from osb-faro-web/sites";
	}

	@Override
	protected NavigationItemList getNavigationItems(
			HttpServletRequest httpServletRequest)
		throws Exception {

		SitesNavigationDisplayContext sitesNavigationDisplayContext =
			new SitesNavigationDisplayContext(httpServletRequest);

		return sitesNavigationDisplayContext.getNavigationItems();
	}

}