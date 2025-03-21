/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.spring.extension.internal.mvc;

import jakarta.mvc.event.AfterControllerEvent;

import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.UriInfo;

/**
 * @author Neil Griffin
 */
public class AfterControllerEventImpl
	extends BaseControllerEventImpl implements AfterControllerEvent {

	public AfterControllerEventImpl(
		Object eventObject, ResourceInfo resourceInfo, UriInfo uriInfo) {

		super(eventObject, resourceInfo, uriInfo);
	}

}