/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.renderer;

import com.liferay.portal.kernel.json.JSONObject;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.Writer;

import java.util.Map;

/**
 * @author Daniel Sanz
 */
public interface FDSRenderer {

	public String getFDSAPIURL(
		String fdsName, HttpServletRequest httpServletRequest,
		boolean interpolate, JSONObject tokenResolutionsJSONObject);

	public void render(
		Map<String, Object> baseProps, String componentId, String fdsName,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, boolean inline,
		String propsTransformer, Writer writer);

}