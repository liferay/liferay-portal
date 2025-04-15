/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.character.encoding.test.servlet.filter;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.BaseFilter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.OutputStream;

import org.osgi.service.component.annotations.Component;

/**
 * @author Matthew Tambara
 */
@Component(
	enabled = true,
	property = {
		"after-filter=Absolute Redirects Filter", "dispatcher=FORWARD",
		"dispatcher=REQUEST", "servlet-context-name=",
		"servlet-filter-name=Character Encoding Filter", "url-pattern=/*"
	},
	service = Filter.class
)
public class CharacterEncodingFilter extends BaseFilter {

	public static final String REQUEST_PARAMETER_NAME =
		"request.parameter.name";

	@Override
	protected Log getLog() {
		return _log;
	}

	@Override
	protected void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws IOException {

		String parameter = httpServletRequest.getParameter(
			REQUEST_PARAMETER_NAME);

		try (OutputStream outputStream =
				httpServletResponse.getOutputStream()) {

			outputStream.write(
				parameter.getBytes(httpServletRequest.getCharacterEncoding()));
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CharacterEncodingFilter.class);

}