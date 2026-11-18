/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Eudaldo Alonso
 */
@Component(service = FragmentRenderer.class)
public class FaroAssetKnownIndividualsSectionFragmentRenderer
	extends BaseSectionFragmentRenderer {

	@Override
	protected String getLabelKey() {
		return "asset-known-individuals";
	}

	@Override
	protected String getModule() {
		return "{AssetKnownIndividuals} from osb-faro-web/assets";
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