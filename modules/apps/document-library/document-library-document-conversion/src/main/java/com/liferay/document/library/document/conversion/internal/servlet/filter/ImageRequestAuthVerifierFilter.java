/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.document.conversion.internal.servlet.filter;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.servlet.filters.authverifier.AuthVerifierFilter;

import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Tomas Polesovsky
 */
@Component(
	property = {
		"before-filter=Auto Login Filter", "dispatcher=FORWARD",
		"dispatcher=REQUEST", "servlet-context-name=",
		"servlet-filter-name=Image Request Auth Filter",
		"url-pattern=/c/wiki/get_page_attachment", "url-pattern=/documents/*",
		"url-pattern=/image/*"
	},
	service = Filter.class
)
public class ImageRequestAuthVerifierFilter extends AuthVerifierFilter {

	@Override
	public boolean isFilterEnabled(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		String token = ParamUtil.getString(httpServletRequest, "auth_token");

		if (Validator.isBlank(token)) {
			return false;
		}

		return super.isFilterEnabled(httpServletRequest, httpServletResponse);
	}

}