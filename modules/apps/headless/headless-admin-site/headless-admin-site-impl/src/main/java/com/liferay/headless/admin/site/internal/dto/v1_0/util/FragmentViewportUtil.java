/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.util;

import com.liferay.headless.admin.site.dto.v1_0.FragmentViewport;
import com.liferay.layout.responsive.ViewportSize;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mikel Lorza
 */
public class FragmentViewportUtil {

	public static FragmentViewport[] toFragmentViewports(
		JSONObject jsonObject) {

		if (JSONUtil.isEmpty(jsonObject)) {
			return null;
		}

		List<FragmentViewport> fragmentViewports = new ArrayList<>();

		FragmentViewport mobileLandscapeFragmentViewport = _toFragmentViewport(
			jsonObject, ViewportSize.MOBILE_LANDSCAPE);

		if (mobileLandscapeFragmentViewport != null) {
			fragmentViewports.add(mobileLandscapeFragmentViewport);
		}

		FragmentViewport portraitMobileFragmentViewport = _toFragmentViewport(
			jsonObject, ViewportSize.PORTRAIT_MOBILE);

		if (portraitMobileFragmentViewport != null) {
			fragmentViewports.add(portraitMobileFragmentViewport);
		}

		FragmentViewport tabletFragmentViewport = _toFragmentViewport(
			jsonObject, ViewportSize.TABLET);

		if (tabletFragmentViewport != null) {
			fragmentViewports.add(tabletFragmentViewport);
		}

		if (ListUtil.isEmpty(fragmentViewports)) {
			return null;
		}

		return fragmentViewports.toArray(new FragmentViewport[0]);
	}

	public static JSONObject toFragmentViewportsJSONObject(
		FragmentViewport[] fragmentViewports) {

		if (ArrayUtil.isEmpty(fragmentViewports)) {
			return null;
		}

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		for (FragmentViewport fragmentViewport : fragmentViewports) {
			String customCSS = fragmentViewport.getCustomCSS();

			if (Validator.isNull(customCSS)) {
				continue;
			}

			jsonObject.put(
				fragmentViewport.getId(), JSONUtil.put("customCSS", customCSS));
		}

		return jsonObject;
	}

	private static FragmentViewport _toFragmentViewport(
		JSONObject jsonObject, ViewportSize viewportSize) {

		JSONObject viewportJSONObject = jsonObject.getJSONObject(
			viewportSize.getViewportSizeId());

		if (JSONUtil.isEmpty(viewportJSONObject) ||
			(Validator.isNull(
				viewportJSONObject.getString("customCSS", null)) &&
			 JSONUtil.isEmpty(viewportJSONObject.getJSONObject("styles")))) {

			return null;
		}

		return new FragmentViewport() {
			{
				setCustomCSS(
					() -> viewportJSONObject.getString("customCSS", null));
				setId(viewportSize::getViewportSizeId);
			}
		};
	}

}