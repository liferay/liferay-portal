/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.display.context;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.URLCodec;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author Stefano Motta
 */
public class ProductsSectionDisplayContext {

	public ProductsSectionDisplayContext(
		HttpServletRequest httpServletRequest) {

		_httpServletRequest = httpServletRequest;
	}

	public String getAPIURL() {
		return "/o/search/v1.0/search?emptySearch=true&filter=" +
			URLCodec.encodeURL("cmsSection eq 'products'") +
				"&nestedFields=embedded";
	}

	public Map<String, Object> getEmptyState() {
		return HashMapBuilder.<String, Object>put(
			"description",
			LanguageUtil.get(
				_httpServletRequest, "click-new-to-create-your-first-product")
		).put(
			"image", "/states/cms_empty_state_content.svg"
		).put(
			"title", LanguageUtil.get(_httpServletRequest, "no-products-yet")
		).build();
	}

	private final HttpServletRequest _httpServletRequest;

}