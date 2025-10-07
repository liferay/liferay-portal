/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.http.servlet.internal.registration;

import com.liferay.portal.osgi.web.http.servlet.internal.Match;
import com.liferay.portal.osgi.web.http.servlet.internal.context.LiferayContextController;

import jakarta.servlet.Servlet;

import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.runtime.dto.ErrorPageDTO;
import org.osgi.service.http.runtime.dto.ServletDTO;

/**
 * @author Dante Wang
 */
public class ServletRegistration extends EndpointRegistration<ServletDTO> {

	public ServletRegistration(
		ErrorPageDTO errorPageDTO,
		LiferayContextController liferayContextController,
		ServiceHolder<Servlet> servletHolder,
		ServletContextHelper servletContextHelper, ServletDTO servletDTO) {

		super(
			servletDTO, liferayContextController, servletHolder,
			servletContextHelper);

		_errorPageDTO = errorPageDTO;
	}

	@Override
	public String getName() {
		ServletDTO servletDTO = getDTO();

		return servletDTO.name;
	}

	@Override
	public String[] getPatterns() {
		ServletDTO servletDTO = getDTO();

		return servletDTO.patterns;
	}

	@Override
	public long getServiceId() {
		ServletDTO servletDTO = getDTO();

		return servletDTO.serviceId;
	}

	@Override
	public String match(
		String extension, Match match, String name, String pathInfo,
		String servletPath) {

		if ((_errorPageDTO != null) && (name != null)) {
			for (long errorCode : _errorPageDTO.errorCodes) {
				String errorCodeString = String.valueOf(errorCode);

				if (errorCodeString.equals(name)) {
					return name;
				}
			}

			for (String exception : _errorPageDTO.exceptions) {
				if (exception.equals(name)) {
					return name;
				}
			}
		}

		return super.match(extension, match, name, pathInfo, servletPath);
	}

	private final ErrorPageDTO _errorPageDTO;

}