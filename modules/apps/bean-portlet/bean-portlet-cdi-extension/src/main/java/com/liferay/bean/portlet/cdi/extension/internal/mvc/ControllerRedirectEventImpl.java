/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.cdi.extension.internal.mvc;

import jakarta.mvc.event.ControllerRedirectEvent;

import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;

/**
 * @author Neil Griffin
 */
public class ControllerRedirectEventImpl
	extends BaseControllerEventImpl implements ControllerRedirectEvent {

	public ControllerRedirectEventImpl(
		URI location, ResourceInfo resourceInfo, UriInfo uriInfo) {

		super(resourceInfo, uriInfo);

		_location = location;
	}

	@Override
	public URI getLocation() {
		return _location;
	}

	private final URI _location;

}