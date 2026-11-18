/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemList;
import com.liferay.osb.faro.web.internal.display.context.IndividualNavigationDisplayContext;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Eudaldo Alonso
 */
@Component(service = FragmentRenderer.class)
public class FaroIndividualInterestSectionFragmentRenderer
	extends BaseSectionFragmentRenderer {

	@Override
	protected String getLabelKey() {
		return "individual-interest";
	}

	@Override
	protected String getModule() {
		return "{IndividualInterest} from osb-faro-web/individuals";
	}

	@Override
	protected NavigationItemList getNavigationItems(
			HttpServletRequest httpServletRequest)
		throws Exception {

		IndividualNavigationDisplayContext individualNavigationDisplayContext =
			new IndividualNavigationDisplayContext(httpServletRequest);

		return individualNavigationDisplayContext.getNavigationItems();
	}

	@Override
	protected Map<String, String> getParams(
		HttpServletRequest httpServletRequest) {

		return HashMapBuilder.putAll(
			super.getParams(httpServletRequest)
		).put(
			"active", ParamUtil.getString(httpServletRequest, "active")
		).put(
			"id", ParamUtil.getString(httpServletRequest, "id")
		).put(
			"interestId", ParamUtil.getString(httpServletRequest, "interestId")
		).build();
	}

}