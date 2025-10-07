/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.http.servlet.internal.registration;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.osgi.web.http.servlet.internal.context.LiferayContextController;

import jakarta.servlet.Servlet;

import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.runtime.dto.ResourceDTO;

/**
 * @author Dante Wang
 */
public class ResourceRegistration extends EndpointRegistration<ResourceDTO> {

	public ResourceRegistration(
		LiferayContextController liferayContextController,
		ResourceDTO resourceDTO, ServiceHolder<Servlet> serviceHolder,
		ServletContextHelper servletContextHelper) {

		super(
			resourceDTO, liferayContextController, serviceHolder,
			servletContextHelper);

		Servlet servlet = serviceHolder.get();

		Class<?> clazz = servlet.getClass();

		String className = clazz.getName();

		_name = className.concat(
			StringPool.POUND
		).concat(
			resourceDTO.prefix
		);
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public String[] getPatterns() {
		ResourceDTO resourceDTO = getDTO();

		return resourceDTO.patterns;
	}

	@Override
	public long getServiceId() {
		ResourceDTO resourceDTO = getDTO();

		return resourceDTO.serviceId;
	}

	private final String _name;

}