/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemList;
import com.liferay.osb.faro.web.internal.display.context.AssetNavigationDisplayContext;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Eudaldo Alonso
 */
@Component(service = FragmentRenderer.class)
public class FaroAssetAccountsSectionFragmentRenderer
	extends BaseSectionFragmentRenderer {

	@Override
	protected String getLabelKey() {
		return "asset-accounts";
	}

	@Override
	protected String getModule() {
		return "{AssetAccounts} from osb-faro-web/assets";
	}

	@Override
	protected NavigationItemList getNavigationItems(
			HttpServletRequest httpServletRequest)
		throws Exception {

		AssetNavigationDisplayContext assetNavigationDisplayContext =
			new AssetNavigationDisplayContext(httpServletRequest);

		return assetNavigationDisplayContext.getNavigationItems();
	}

	@Override
	protected Map<String, String> getParams(
		HttpServletRequest httpServletRequest) {

		return HashMapBuilder.putAll(
			super.getParams(httpServletRequest)
		).put(
			"assetId", ParamUtil.getString(httpServletRequest, "assetId")
		).put(
			"assetType", ParamUtil.getString(httpServletRequest, "assetType")
		).put(
			"title", ParamUtil.getString(httpServletRequest, "title")
		).put(
			"touchpoint", ParamUtil.getString(httpServletRequest, "touchpoint")
		).put(
			"type", ParamUtil.getString(httpServletRequest, "type")
		).put(
			"useDB", ParamUtil.getString(httpServletRequest, "useDB")
		).build();
	}

}