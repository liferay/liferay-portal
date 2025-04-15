/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.dynamic.feature;

import com.liferay.portal.vulcan.internal.jaxrs.container.response.filter.StatusContainerResponseFilter;
import com.liferay.portal.vulcan.status.Status;

import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.DynamicFeature;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.FeatureContext;
import jakarta.ws.rs.ext.Provider;

import java.lang.reflect.Method;

/**
 * @author Zoltán Takács
 */
@Provider
public class StatusDynamicFeature implements DynamicFeature {

	public void configure(ResourceInfo resourceInfo, FeatureContext context) {
		Method resourceMethod = resourceInfo.getResourceMethod();

		Status status = resourceMethod.getAnnotation(Status.class);

		if (status == null) {
			return;
		}

		StatusContainerResponseFilter statusContainerResponseFilter =
			new StatusContainerResponseFilter(status.value());

		context.register(statusContainerResponseFilter, Priorities.USER + 100);
	}

}