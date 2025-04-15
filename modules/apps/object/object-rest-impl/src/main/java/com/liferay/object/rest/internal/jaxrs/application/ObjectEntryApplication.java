/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.jaxrs.application;

import com.liferay.object.rest.internal.resource.v1_0.OpenAPIResourceImpl;
import com.liferay.object.rest.openapi.v1_0.ObjectEntryOpenAPIResourceProvider;

import jakarta.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Javier de Arcos
 */
public class ObjectEntryApplication extends Application {

	public ObjectEntryApplication(
		ObjectEntryOpenAPIResourceProvider objectEntryOpenAPIResourceProvider) {

		_objectEntryOpenAPIResourceProvider =
			objectEntryOpenAPIResourceProvider;
	}

	@Override
	public Set<Object> getSingletons() {
		Set<Object> objects = new HashSet<>();

		objects.add(
			new OpenAPIResourceImpl(_objectEntryOpenAPIResourceProvider));

		return objects;
	}

	private final ObjectEntryOpenAPIResourceProvider
		_objectEntryOpenAPIResourceProvider;

}