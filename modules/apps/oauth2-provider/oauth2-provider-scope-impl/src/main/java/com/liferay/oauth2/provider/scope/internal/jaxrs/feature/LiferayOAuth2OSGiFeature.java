/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.scope.internal.jaxrs.feature;

import com.liferay.oauth2.provider.scope.internal.constants.OAuth2ProviderScopeConstants;
import com.liferay.oauth2.provider.scope.internal.jaxrs.container.request.filter.BaseContextContainerRequestFilter;
import com.liferay.oauth2.provider.scope.liferay.OAuth2ProviderScopeLiferayAccessControlContext;
import com.liferay.oauth2.provider.scope.liferay.ScopeContext;
import com.liferay.oauth2.provider.scope.spi.application.descriptor.ApplicationDescriptor;
import com.liferay.oauth2.provider.scope.spi.scope.descriptor.ScopeDescriptor;
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.security.access.control.AccessControlUtil;
import com.liferay.portal.kernel.security.auth.AccessControlContext;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifierResult;
import com.liferay.portal.kernel.security.service.access.policy.ServiceAccessPolicy;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;
import jakarta.ws.rs.ext.Provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Carlos Sierra Andrés
 */
@Component(
	property = {
		LiferayOAuth2OSGiFeature.OAUTH2_SERVICE_ACCESS_POLICY_NAME + "=AUTHORIZED_OAUTH2_SAP",
		"liferay.extension=OAuth2",
		"osgi.jaxrs.application.select=(!(liferay.oauth2=false))",
		"osgi.jaxrs.extension=true", "osgi.jaxrs.name=Liferay.OAuth2"
	},
	scope = ServiceScope.PROTOTYPE, service = Feature.class
)
@Provider
public class LiferayOAuth2OSGiFeature implements Feature {

	@Override
	public boolean configure(FeatureContext featureContext) {
		Configuration configuration = featureContext.getConfiguration();

		Map<String, Object> applicationProperties =
			(Map<String, Object>)configuration.getProperty(
				"osgi.jaxrs.application.serviceProperties");

		Class<? extends Application> applicationClass = _application.getClass();

		String osgiJaxRsName = MapUtil.getString(
			applicationProperties, "osgi.jaxrs.name",
			applicationClass.getName());

		featureContext.register(
			new BaseContextContainerRequestFilter() {

				@Override
				public void filter(
					ContainerRequestContext containerRequestContext) {

					_scopeContext.setApplicationName(osgiJaxRsName);
					_scopeContext.setBundle(_bundle);
					_scopeContext.setCompanyId(getCompanyId());
				}

			},
			Priorities.AUTHORIZATION - 10);

		featureContext.register(
			(ContainerResponseFilter)(a, b) -> _scopeContext.clear(),
			Priorities.AUTHORIZATION - 9);

		String oauth2ServiceAccessPolicyName = MapUtil.getString(
			applicationProperties, OAUTH2_SERVICE_ACCESS_POLICY_NAME,
			_oauth2ServiceAccessPolicyName);

		featureContext.register(
			(ContainerRequestFilter)a -> {
				if (!OAuth2ProviderScopeLiferayAccessControlContext.
						isOAuth2AuthVerified()) {

					return;
				}

				if (_log.isDebugEnabled()) {
					_log.debug("Enabling SAP " + oauth2ServiceAccessPolicyName);
				}

				AccessControlContext accessControlContext =
					AccessControlUtil.getAccessControlContext();

				AuthVerifierResult authVerifierResult =
					accessControlContext.getAuthVerifierResult();

				if (authVerifierResult == null) {
					return;
				}

				Map<String, Object> settings = authVerifierResult.getSettings();

				List<String> serviceAccessPolicyNames =
					(List<String>)settings.computeIfAbsent(
						ServiceAccessPolicy.SERVICE_ACCESS_POLICY_NAMES,
						value -> new ArrayList<>());

				serviceAccessPolicyNames.add(oauth2ServiceAccessPolicyName);
			},
			Priorities.AUTHORIZATION - 9);

		_registerDescriptors(osgiJaxRsName);

		return true;
	}

	@Activate
	protected void activate(
		ComponentContext componentContext, Map<String, Object> properties) {

		_bundle = componentContext.getUsingBundle();

		_bundleContext = componentContext.getBundleContext();

		_oauth2ServiceAccessPolicyName = MapUtil.getString(
			properties, OAUTH2_SERVICE_ACCESS_POLICY_NAME);
	}

	@Deactivate
	protected void deactivate() {
		for (ServiceRegistration<?> serviceRegistration :
				_serviceRegistrations) {

			try {
				serviceRegistration.unregister();
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}

		for (ServiceTracker<?, ?> serviceTracker : _serviceTrackers) {
			serviceTracker.close();
		}
	}

	protected static final String OAUTH2_SERVICE_ACCESS_POLICY_NAME =
		"oauth2.service.access.policy.name";

	private void _registerDescriptors(String osgiJaxRsName) {
		String bundleSymbolicName = _bundle.getSymbolicName();

		ServiceTracker<ResourceBundleLoader, ResourceBundleLoader>
			serviceTracker = ServiceTrackerFactory.open(
				_bundleContext,
				StringBundler.concat(
					"(&(bundle.symbolic.name=", bundleSymbolicName,
					")(objectClass=", ResourceBundleLoader.class.getName(),
					")(resource.bundle.base.name=content.Language))"));

		_serviceTrackers.add(serviceTracker);

		Dictionary<String, Object> properties =
			HashMapDictionaryBuilder.<String, Object>put(
				OAuth2ProviderScopeConstants.OSGI_JAXRS_NAME, osgiJaxRsName
			).build();

		_serviceRegistrations.add(
			_bundleContext.registerService(
				new String[] {
					ScopeDescriptor.class.getName(),
					ApplicationDescriptor.class.getName()
				},
				new ApplicationDescriptorsImpl(serviceTracker, osgiJaxRsName),
				properties));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LiferayOAuth2OSGiFeature.class);

	@Context
	private Application _application;

	private Bundle _bundle;
	private BundleContext _bundleContext;

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY, target = "(default=true)"
	)
	private volatile ScopeDescriptor _defaultScopeDescriptor;

	private String _oauth2ServiceAccessPolicyName;

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	private volatile ScopeContext _scopeContext;

	private final Collection<ServiceRegistration<?>> _serviceRegistrations =
		new ArrayList<>();
	private final Collection<ServiceTracker<?, ?>> _serviceTrackers =
		new ArrayList<>();

	private class ApplicationDescriptorsImpl
		implements ApplicationDescriptor, ScopeDescriptor {

		public ApplicationDescriptorsImpl(
			ServiceTracker<?, ResourceBundleLoader> serviceTracker,
			String osgiJaxRsName) {

			_serviceTracker = serviceTracker;
			_osgiJaxRsName = osgiJaxRsName;
		}

		@Override
		public String describeApplication(Locale locale) {
			ResourceBundleLoader resourceBundleLoader =
				_serviceTracker.getService();

			if (resourceBundleLoader == null) {
				return _osgiJaxRsName;
			}

			String key = "oauth2.application.description." + _osgiJaxRsName;

			return GetterUtil.getString(
				ResourceBundleUtil.getString(
					resourceBundleLoader.loadResourceBundle(locale), key),
				key);
		}

		@Override
		public String describeScope(String scope, Locale locale) {
			ResourceBundleLoader resourceBundleLoader =
				_serviceTracker.getService();

			if (resourceBundleLoader == null) {
				return _defaultScopeDescriptor.describeScope(scope, locale);
			}

			String key = "oauth2.scope." + scope;

			return GetterUtil.getString(
				ResourceBundleUtil.getString(
					resourceBundleLoader.loadResourceBundle(locale), key),
				_defaultScopeDescriptor.describeScope(scope, locale));
		}

		private final String _osgiJaxRsName;
		private final ServiceTracker<?, ResourceBundleLoader> _serviceTracker;

	}

}