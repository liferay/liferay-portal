/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.weblogic.support.internal.include;

import com.liferay.portal.servlet.filters.weblogic.WebLogicIncludeServletResponseFactory;

import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Shuyang Zhou
 */
@Component(service = WebLogicIncludeServletResponseFactory.class)
public class WebLogicIncludeServletResponseFactoryImpl
	implements WebLogicIncludeServletResponseFactory {

	@Override
	public HttpServletResponse create(HttpServletResponse httpServletResponse) {
		if (httpServletResponse instanceof WebLogicIncludeServletResponse) {
			return httpServletResponse;
		}

		return new WebLogicIncludeServletResponse(httpServletResponse);
	}

}