/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.spring.extension.internal.mvc;

import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.UriInfo;

import org.springframework.context.ApplicationEvent;

/**
 * @author Neil Griffin
 */
public class BaseControllerEventImpl extends ApplicationEvent {

	public BaseControllerEventImpl(
		Object eventObject, ResourceInfo resourceInfo, UriInfo uriInfo) {

		super(eventObject);

		_resourceInfo = resourceInfo;
		_uriInfo = uriInfo;
	}

	public ResourceInfo getResourceInfo() {
		return _resourceInfo;
	}

	public UriInfo getUriInfo() {
		return _uriInfo;
	}

	private final ResourceInfo _resourceInfo;
	private final UriInfo _uriInfo;

}