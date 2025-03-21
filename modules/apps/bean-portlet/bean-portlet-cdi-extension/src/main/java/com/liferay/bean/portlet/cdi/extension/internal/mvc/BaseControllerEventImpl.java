/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.cdi.extension.internal.mvc;

import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.UriInfo;

/**
 * @author Neil Griffin
 */
public class BaseControllerEventImpl {

	public BaseControllerEventImpl(ResourceInfo resourceInfo, UriInfo uriInfo) {
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