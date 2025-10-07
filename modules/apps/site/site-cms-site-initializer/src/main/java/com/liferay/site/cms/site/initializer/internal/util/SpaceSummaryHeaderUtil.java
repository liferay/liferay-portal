/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.util;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author Roberto Díaz
 */
public class SpaceSummaryHeaderUtil {

	public static Map<String, Object> getSpaceSummaryHeaderProps(
			HttpServletRequest httpServletRequest, String labelKey,
			Map<String, Object> permissions,
			Map<String, Object> spaceModalProps, String titleKey, String url)
		throws Exception {

		return HashMapBuilder.<String, Object>put(
			"label", LanguageUtil.get(httpServletRequest, labelKey)
		).put(
			"permissions", permissions
		).put(
			"spaceModalProps", spaceModalProps
		).put(
			"title", LanguageUtil.get(httpServletRequest, titleKey)
		).put(
			"url", url
		).build();
	}

}