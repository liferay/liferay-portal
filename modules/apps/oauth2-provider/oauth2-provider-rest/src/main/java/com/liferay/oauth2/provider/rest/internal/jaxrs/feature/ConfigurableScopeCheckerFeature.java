/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.jaxrs.feature;

import com.liferay.oauth2.provider.rest.internal.jaxrs.feature.configuration.ConfigurableScopeCheckerFeatureConfiguration;
import com.liferay.oauth2.provider.rest.spi.scope.checker.container.request.filter.BaseScopeCheckerContainerRequestFilter;
import com.liferay.oauth2.provider.scope.ScopeChecker;
import com.liferay.oauth2.provider.scope.spi.scope.finder.ScopeFinder;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.annotation.Priority;

import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Carlos Sierra Andrés
 */
@Component(
	configurationPid = "com.liferay.oauth2.provider.rest.internal.jaxrs.feature.configuration.ConfigurableScopeCheckerFeatureConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	property = {
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.extension.select=(osgi.jaxrs.name=Liferay.OAuth2)",
		"osgi.jaxrs.name=Liferay.OAuth2.HTTP.configurable.request.checker"
	},
	scope = ServiceScope.PROTOTYPE, service = Feature.class
)
@Priority(Priorities.AUTHORIZATION - 8)
@Provider
public class ConfigurableScopeCheckerFeature implements Feature {

	@Override
	public boolean configure(FeatureContext context) {
		if (_checkPatterns.isEmpty()) {
			return false;
		}

		Set<String> scopes = new HashSet<>();

		for (CheckPattern checkPattern : _checkPatterns) {
			for (String scope : checkPattern.getScopes()) {
				if (Validator.isNotNull(scope)) {
					scopes.add(scope);
				}
			}
		}

		context.register(
			new ConfigurableContainerScopeCheckerContainerRequestFilter(),
			HashMapBuilder.<Class<?>, Integer>put(
				ContainerRequestFilter.class, Priorities.AUTHORIZATION - 8
			).build());

		_serviceRegistration = _bundleContext.registerService(
			ScopeFinder.class, new CollectionScopeFinder(scopes),
			_buildProperties(context.getConfiguration()));

		return true;
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_bundleContext = bundleContext;

		ConfigurableScopeCheckerFeatureConfiguration
			configurableScopeCheckerFeatureConfiguration =
				ConfigurableUtil.createConfigurable(
					ConfigurableScopeCheckerFeatureConfiguration.class,
					properties);

		_allowUnmatched =
			configurableScopeCheckerFeatureConfiguration.allowUnmatched();

		for (String pattern :
				configurableScopeCheckerFeatureConfiguration.patterns()) {

			String[] split = pattern.split("::");

			if (split.length != 3) {
				_log.error(
					"Invalid syntax " + pattern +
						" does not match 3 sequences of ::");

				return;
			}

			String methodPatternString = split[0];
			String urlPatternString = split[1];

			String scopesString = split[2];

			String[] scopes = scopesString.split(StringPool.COMMA);

			try {
				_checkPatterns.add(
					new CheckPattern(
						Pattern.compile(methodPatternString),
						Pattern.compile(urlPatternString), scopes));
			}
			catch (PatternSyntaxException patternSyntaxException) {
				_log.error(
					"Invalid pattern " + pattern, patternSyntaxException);

				throw new IllegalArgumentException(patternSyntaxException);
			}
		}
	}

	@Deactivate
	protected void deactivate() {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	private Dictionary<String, Object> _buildProperties(
		Configuration configuration) {

		return HashMapDictionaryBuilder.<String, Object>putAll(
			(Map<String, Object>)configuration.getProperty(
				"osgi.jaxrs.application.serviceProperties")
		).put(
			Constants.SERVICE_RANKING, Integer.MIN_VALUE
		).build();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ConfigurableScopeCheckerFeature.class);

	private boolean _allowUnmatched;
	private BundleContext _bundleContext;
	private final List<CheckPattern> _checkPatterns = new ArrayList<>();

	@Reference
	private ScopeChecker _scopeChecker;

	private ServiceRegistration<ScopeFinder> _serviceRegistration;

	private static class CheckPattern {

		public CheckPattern(
			Pattern methodPattern, Pattern urlPattern, String[] scopes) {

			_scopes = scopes;

			_methodPatternPredicate = methodPattern.asPredicate();
			_urlPatternPredicate = urlPattern.asPredicate();
		}

		public Predicate<String> getMethodPatternPredicate() {
			return _methodPatternPredicate;
		}

		public String[] getScopes() {
			return _scopes;
		}

		public Predicate<String> getUrlPatternPredicate() {
			return _urlPatternPredicate;
		}

		private final Predicate<String> _methodPatternPredicate;
		private final String[] _scopes;
		private final Predicate<String> _urlPatternPredicate;

	}

	private class ConfigurableContainerScopeCheckerContainerRequestFilter
		extends BaseScopeCheckerContainerRequestFilter {

		@Override
		public boolean isContainerRequestContextAllowed(
			ContainerRequestContext containerRequestContext) {

			boolean anyMatch = false;
			String path = _getPath();
			Request request = containerRequestContext.getRequest();

			for (CheckPattern checkPattern : _checkPatterns) {
				if (matches(checkPattern, path, request)) {
					anyMatch = true;
				}
				else {
					continue;
				}

				String[] scopes = checkPattern.getScopes();

				if (requiresNoScope(scopes)) {
					if (_log.isDebugEnabled()) {
						_log.debug(
							"Path  " + path +
								" was approved, does not require a scope");
					}

					return true;
				}

				if (_scopeChecker.checkAllScopes(scopes)) {
					if (_log.isDebugEnabled()) {
						_log.debug(
							StringBundler.concat(
								"Path ", path,
								" was approved, token includes all scopes ",
								StringUtil.merge(scopes)));
					}

					return true;
				}
			}

			if (anyMatch) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"Path ", path,
							" was not allowed because it does not have ",
							"required scopes"));
				}

				return false;
			}
			else if (_allowUnmatched) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"Path ", path,
							" was approved, does not match any patterns"));
				}

				return true;
			}

			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Path ", path,
						" was not allowed because it does not match any ",
						"patterns"));
			}

			return false;
		}

		protected boolean matches(
			CheckPattern checkPattern, String path, Request request) {

			Predicate<String> urlPatternPredicate =
				checkPattern.getUrlPatternPredicate();

			if (!urlPatternPredicate.test(path)) {
				return false;
			}

			Predicate<String> methodPatternPredicate =
				checkPattern.getMethodPatternPredicate();

			return methodPatternPredicate.test(request.getMethod());
		}

		protected boolean requiresNoScope(String[] scopes) {
			if (ArrayUtil.isEmpty(scopes) ||
				((scopes.length == 1) && Validator.isNull(scopes[0]))) {

				return true;
			}

			return false;
		}

		private String _getPath() {
			String uriInfoPath = _uriInfo.getPath();

			if (uriInfoPath.startsWith("/")) {
				return uriInfoPath;
			}

			return StringPool.SLASH + uriInfoPath;
		}

		@Context
		private UriInfo _uriInfo;

	}

}