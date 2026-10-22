/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.jaxrs.feature;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;

import org.apache.cxf.feature.Feature;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Shuyang Zhou
 */
@Component(service = {})
public class ScopeFinderFeatureRegistrator {

	@Activate
	protected void activate(BundleContext bundleContext) {

		// The JAX-RS whiteboard releases and refetches every extension each
		// time it rewires an application. Register these features as plain
		// objects rather than DS components so that each refresh does not
		// deactivate and reactivate them.

		_annotationScopeFinderFeatureServiceRegistration =
			bundleContext.registerService(
				Feature.class, new AnnotationScopeFinderFeature(bundleContext),
				HashMapDictionaryBuilder.<String, Object>put(
					"org.apache.cxf.bus.private.extension", true
				).put(
					"osgi.jaxrs.application.select",
					"(|(oauth2.scope.checker.type=annotations)" +
						"(oauth2.scopechecker.type=annotations))"
				).put(
					"osgi.jaxrs.extension", true
				).put(
					"osgi.jaxrs.extension.select",
					"(osgi.jaxrs.name=Liferay.OAuth2)"
				).put(
					"osgi.jaxrs.name", "Liferay.OAuth2.annotations.scope.finder"
				).build());
		_httpMethodScopeFinderFeatureServiceRegistration =
			bundleContext.registerService(
				Feature.class, new HttpMethodScopeFinderFeature(bundleContext),
				HashMapDictionaryBuilder.<String, Object>put(
					"org.apache.cxf.bus.private.extension", true
				).put(
					"osgi.jaxrs.application.select",
					StringBundler.concat(
						"(|(&(!(oauth2.scope.checker.type=*))",
						"(!(oauth2.scopechecker.type=*)))",
						"(|(oauth2.scope.checker.type=http.method)",
						"(oauth2.scopechecker.type=http.method)))")
				).put(
					"osgi.jaxrs.extension", true
				).put(
					"osgi.jaxrs.extension.select",
					"(osgi.jaxrs.name=Liferay.OAuth2)"
				).put(
					"osgi.jaxrs.name", "Liferay.OAuth2.HTTP.method.scope.finder"
				).build());
	}

	@Deactivate
	protected void deactivate() {
		_annotationScopeFinderFeatureServiceRegistration.unregister();
		_httpMethodScopeFinderFeatureServiceRegistration.unregister();
	}

	private ServiceRegistration<Feature>
		_annotationScopeFinderFeatureServiceRegistration;
	private ServiceRegistration<Feature>
		_httpMethodScopeFinderFeatureServiceRegistration;

}