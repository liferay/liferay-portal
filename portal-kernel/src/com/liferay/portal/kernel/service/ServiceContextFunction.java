/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.portal.kernel.exception.PortalException;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.function.Function;

/**
 * @author André de Oliveira
 */
public class ServiceContextFunction
	implements Function<String, ServiceContext> {

	public ServiceContextFunction(HttpServletRequest httpServletRequest) {
		_httpServletRequest = httpServletRequest;

		_portletRequest = null;
	}

	public ServiceContextFunction(PortletRequest portletRequest) {
		_portletRequest = portletRequest;

		_httpServletRequest = null;
	}

	@Override
	public ServiceContext apply(String className) {
		try {
			if (_portletRequest != null) {
				return ServiceContextFactory.getInstance(
					className, _portletRequest);
			}

			return ServiceContextFactory.getInstance(
				className, _httpServletRequest);
		}
		catch (PortalException portalException) {
			throw new RuntimeException(portalException);
		}
	}

	private final HttpServletRequest _httpServletRequest;
	private final PortletRequest _portletRequest;

}