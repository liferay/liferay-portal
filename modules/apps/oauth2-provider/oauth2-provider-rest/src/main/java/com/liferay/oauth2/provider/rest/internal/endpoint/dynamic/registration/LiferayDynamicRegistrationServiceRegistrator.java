/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.endpoint.dynamic.registration;

import com.liferay.oauth2.provider.rest.internal.endpoint.liferay.LiferayOAuthDataProvider;
import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;

import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge García Jiménez
 */
@Component(
	property = {
		"block.unsecure.requests=true", "can.support.public.clients=true",
		"enabled=true"
	},
	service = {}
)
public class LiferayDynamicRegistrationServiceRegistrator {

	@Activate
	@Modified
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		if (!FeatureFlagManagerUtil.isEnabled(
				CompanyThreadLocal.getNonsystemCompanyId(), "LPD-63416") &&
			!MapUtil.getBoolean(properties, "enabled", true)) {

			return;
		}

		_serviceRegistration = bundleContext.registerService(
			Object.class,
			(Object)new ServiceFactory<Object>() {

				@Override
				public Object getService(
					Bundle bundle,
					ServiceRegistration<Object> serviceRegistration) {

					return _liferayDynamicRegistrationServiceDCLSingleton.
						getSingleton(
							() -> {
								LiferayDynamicRegistrationService
									liferayDynamicRegistrationService =
										new LiferayDynamicRegistrationService();

								liferayDynamicRegistrationService.
									setClientProvider(
										_liferayOAuthDataProvider);
								liferayDynamicRegistrationService.setPortal(
									_portal);
								liferayDynamicRegistrationService.
									setSupportRegistrationAccessTokens(true);

								return liferayDynamicRegistrationService;
							});
				}

				@Override
				public void ungetService(
					Bundle bundle,
					ServiceRegistration<Object> serviceRegistration,
					Object service) {
				}

			},
			HashMapDictionaryBuilder.<String, Object>put(
				"osgi.jaxrs.application.select",
				"(osgi.jaxrs.name=Liferay.OAuth2.Application)"
			).put(
				"osgi.jaxrs.name", "Liferay.Dynamic.Registration.Service."
			).put(
				"osgi.jaxrs.resource", true
			).build());
	}

	@Deactivate
	protected void deactivate() {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();

			_serviceRegistration = null;
		}
	}

	private final DCLSingleton<LiferayDynamicRegistrationService>
		_liferayDynamicRegistrationServiceDCLSingleton = new DCLSingleton<>();

	@Reference
	private LiferayOAuthDataProvider _liferayOAuthDataProvider;

	@Reference
	private Portal _portal;

	private volatile ServiceRegistration<Object> _serviceRegistration;

}