/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.servlet.filter;

import com.liferay.osb.faro.engine.client.constants.OSBAsahHeaderConstants;
import com.liferay.osb.faro.engine.client.util.TokenUtil;
import com.liferay.osb.faro.web.internal.util.ServletRequestUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.BaseFilter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Objects;

import org.apache.commons.codec.digest.DigestUtils;

import org.osgi.service.component.annotations.Component;

/**
 * @author Matthew Kong
 */
@Component(
	property = {
		"osgi.http.whiteboard.filter.name=com.liferay.osb.faro.web.internal.servlet.filter.EmailServletFilter",
		"osgi.http.whiteboard.filter.pattern=/email/*"
	},
	service = Filter.class
)
public class EmailServletFilter extends BaseFilter {

	@Override
	protected Log getLog() {
		return _log;
	}

	protected boolean isInvalidRequest(HttpServletRequest httpServletRequest) {
		String faroBackendSecuritySignature = httpServletRequest.getHeader(
			OSBAsahHeaderConstants.FARO_BACKEND_SECURITY_SIGNATURE);

		if (faroBackendSecuritySignature == null) {
			_logInvalidRequest(null, httpServletRequest);

			return true;
		}

		String originalURL = ServletRequestUtil.getOriginalURL(
			httpServletRequest);

		if (!Objects.equals(
				faroBackendSecuritySignature,
				DigestUtils.sha256Hex(
					TokenUtil.getOSBAsahSecurityToken() + originalURL))) {

			_logInvalidRequest(
				faroBackendSecuritySignature, httpServletRequest);

			return true;
		}

		return false;
	}

	@Override
	protected void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		if (isInvalidRequest(httpServletRequest)) {
			httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN);

			return;
		}

		Class<?> clazz = getClass();

		processFilter(
			clazz.getName(), httpServletRequest, httpServletResponse,
			filterChain);
	}

	private void _logInvalidRequest(
		String faroBackendSecuritySignature,
		HttpServletRequest httpServletRequest) {

		if (_log.isDebugEnabled()) {
			_log.debug(
				String.format(
					"%s attempted to access %s with an invalid security " +
						"signature %s",
					httpServletRequest.getRemoteAddr(),
					httpServletRequest.getRequestURI(),
					faroBackendSecuritySignature));
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EmailServletFilter.class);

}