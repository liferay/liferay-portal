/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.jaxrs.feature;

import com.liferay.object.rest.internal.jaxrs.param.converter.provider.ScopeKeyParamConverterProvider;
import com.liferay.portal.kernel.service.GroupLocalService;

import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Carlos Correa
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(liferay.objects=true)",
		"osgi.jaxrs.extension=true", "osgi.jaxrs.name=Liferay.Object"
	},
	scope = ServiceScope.PROTOTYPE, service = Feature.class
)
public class ObjectFeature implements Feature {

	@Override
	public boolean configure(FeatureContext featureContext) {
		featureContext.register(
			new ScopeKeyParamConverterProvider(_groupLocalService));

		return true;
	}

	@Reference
	private GroupLocalService _groupLocalService;

}