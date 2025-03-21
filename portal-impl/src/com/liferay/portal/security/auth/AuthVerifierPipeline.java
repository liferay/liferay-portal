/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.auth;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.url.pattern.mapper.URLPatternMapper;
import com.liferay.petra.url.pattern.mapper.URLPatternMapperFactory;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.security.auth.AccessControlContext;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifier;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifierConfiguration;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifierResult;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.security.auth.registry.AuthVerifierRegistry;
import com.liferay.portal.spring.context.PortalContextLoaderListener;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Consumer;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Tomas Polesovsky
 * @author Peter Fellwock
 * @author Arthur Chan
 * @author Carlos Sierra Andrés
 */
public class AuthVerifierPipeline {

	public static final String AUTH_TYPE = "auth.type";

	public static String getAuthVerifierPropertyName(String className) {
		String simpleClassName = StringUtil.extractLast(
			className, StringPool.PERIOD);

		return StringBundler.concat(
			PropsKeys.AUTH_VERIFIER, simpleClassName, StringPool.PERIOD);
	}

	public static AuthVerifierPipeline getPortalAuthVerifierPipeline() {
		return PortalAuthVerifierPipelineHolder._PORTAL_AUTH_VERIFIER_PIPELINE;
	}

	public AuthVerifierPipeline(
		List<AuthVerifierConfiguration> authVerifierConfigurations,
		String contextPath) {

		_authVerifierConfigurations = new ArrayList<>(
			authVerifierConfigurations);

		_contextPath = contextPath;

		_buildURLPatternMapper();
	}

	public AuthVerifierResult verifyRequest(
			AccessControlContext accessControlContext)
		throws PortalException {

		if (accessControlContext == null) {
			throw new IllegalArgumentException(
				"Access control context is null");
		}

		HttpServletRequest httpServletRequest =
			accessControlContext.getRequest();

		String requestURI = httpServletRequest.getRequestURI();

		AuthVerifierConfigurationConsumer authVerifierConfigurationConsumer =
			new AuthVerifierConfigurationConsumer(
				accessControlContext, _excludeURLPatternMapper, requestURI);

		_includeURLPatternMapper.consumeValues(
			authVerifierConfigurationConsumer, requestURI);

		if (authVerifierConfigurationConsumer.getAuthVerifierResult() != null) {
			return authVerifierConfigurationConsumer.getAuthVerifierResult();
		}

		return _createGuestVerificationResult(accessControlContext);
	}

	private static List<AuthVerifierConfiguration> _filterSupremeAuthVerifier(
		List<AuthVerifierConfiguration> authVerifierConfigurations) {

		for (AuthVerifierConfiguration authVerifierConfiguration :
				authVerifierConfigurations) {

			Properties properties = authVerifierConfiguration.getProperties();

			for (String key : _SUPREME_AUTH_VERIFIER_KEYS) {
				if (GetterUtil.getBoolean(properties.get(key))) {
					return Collections.singletonList(authVerifierConfiguration);
				}
			}
		}

		return authVerifierConfigurations;
	}

	private synchronized void _addAuthVerifierConfiguration(
		AuthVerifierConfiguration authVerifierConfiguration) {

		_authVerifierConfigurations.add(authVerifierConfiguration);

		_buildURLPatternMapper();
	}

	private void _buildURLPatternMapper() {
		Map<String, List<AuthVerifierConfiguration>>
			excludeAuthVerifierConfigurationsMap = new HashMap<>();
		Map<String, List<AuthVerifierConfiguration>>
			includeAuthVerifierConfigurationsMap = new HashMap<>();

		for (AuthVerifierConfiguration authVerifierConfiguration :
				_authVerifierConfigurations) {

			Properties properties = authVerifierConfiguration.getProperties();

			String[] urlsExcludes = StringUtil.split(
				properties.getProperty("urls.excludes"));

			for (String urlsExclude : urlsExcludes) {
				urlsExclude = _contextPath + _fixLegacyURLPattern(urlsExclude);

				List<AuthVerifierConfiguration>
					excludeAuthVerifierConfigurations =
						excludeAuthVerifierConfigurationsMap.computeIfAbsent(
							urlsExclude, key -> new ArrayList<>());

				excludeAuthVerifierConfigurations.add(
					authVerifierConfiguration);
			}

			String[] urlsIncludes = StringUtil.split(
				properties.getProperty("urls.includes"));

			for (String urlsInclude : urlsIncludes) {
				urlsInclude = _contextPath + _fixLegacyURLPattern(urlsInclude);

				List<AuthVerifierConfiguration>
					includeAuthVerifierConfigurations =
						includeAuthVerifierConfigurationsMap.computeIfAbsent(
							urlsInclude, key -> new ArrayList<>());

				includeAuthVerifierConfigurations.add(
					authVerifierConfiguration);
			}
		}

		_excludeURLPatternMapper = URLPatternMapperFactory.create(
			excludeAuthVerifierConfigurationsMap);
		_includeURLPatternMapper = URLPatternMapperFactory.create(
			includeAuthVerifierConfigurationsMap);
	}

	private AuthVerifierResult _createGuestVerificationResult(
			AccessControlContext accessControlContext)
		throws PortalException {

		AuthVerifierResult authVerifierResult = new AuthVerifierResult();

		authVerifierResult.setState(AuthVerifierResult.State.UNSUCCESSFUL);

		HttpServletRequest httpServletRequest =
			accessControlContext.getRequest();

		authVerifierResult.setUserId(
			UserLocalServiceUtil.getGuestUserId(
				PortalUtil.getCompanyId(httpServletRequest)));

		return authVerifierResult;
	}

	private String _fixLegacyURLPattern(String urlPattern) {
		if ((urlPattern == null) || (urlPattern.length() == 0) ||
			(urlPattern.charAt(urlPattern.length() - 1) != '*')) {

			return urlPattern;
		}

		if ((urlPattern.length() > 1) &&
			(urlPattern.charAt(urlPattern.length() - 2) == '/')) {

			return urlPattern;
		}

		return urlPattern.substring(0, urlPattern.length() - 1) + "/*";
	}

	private synchronized void _removeAuthVerifierConfiguration(
		AuthVerifierConfiguration authVerifierConfiguration) {

		_authVerifierConfigurations.remove(authVerifierConfiguration);

		_buildURLPatternMapper();
	}

	private static final String[] _SUPREME_AUTH_VERIFIER_KEYS = {
		"basic_auth", "digest_auth"
	};

	private static final Log _log = LogFactoryUtil.getLog(
		AuthVerifierPipeline.class);

	private final List<AuthVerifierConfiguration> _authVerifierConfigurations;
	private final String _contextPath;
	private volatile URLPatternMapper<List<AuthVerifierConfiguration>>
		_excludeURLPatternMapper;
	private volatile URLPatternMapper<List<AuthVerifierConfiguration>>
		_includeURLPatternMapper;

	private static class AuthVerifierConfigurationConsumer
		implements Consumer<List<AuthVerifierConfiguration>> {

		@Override
		public void accept(
			List<AuthVerifierConfiguration> authVerifierConfigurations) {

			if (_authVerifierResult != null) {
				return;
			}

			if (_excludedAuthVerifierConfigurations == null) {
				_excludedAuthVerifierConfigurations = new HashSet<>();

				_excludeURLPatternMapper.consumeValues(
					_excludedAuthVerifierConfigurations::addAll, _requestURI);
			}

			for (AuthVerifierConfiguration authVerifierConfiguration :
					_filterSupremeAuthVerifier(authVerifierConfigurations)) {

				if (_excludedAuthVerifierConfigurations.contains(
						authVerifierConfiguration)) {

					continue;
				}

				_authVerifierResult = _verifyWithAuthVerifierConfiguration(
					_accessControlContext, authVerifierConfiguration);

				if (_authVerifierResult != null) {
					return;
				}
			}
		}

		public AuthVerifierResult getAuthVerifierResult() {
			return _authVerifierResult;
		}

		private AuthVerifierConfigurationConsumer(
			AccessControlContext accessControlContext,
			URLPatternMapper<List<AuthVerifierConfiguration>>
				excludeURLPatternMapper,
			String requestURI) {

			_accessControlContext = accessControlContext;
			_excludeURLPatternMapper = excludeURLPatternMapper;
			_requestURI = requestURI;
		}

		private Map<String, Object> _mergeSettings(
			Properties properties, Map<String, Object> settings) {

			Map<String, Object> mergedSettings = new HashMap<>(settings);

			if (properties != null) {
				for (Map.Entry<Object, Object> entry : properties.entrySet()) {
					mergedSettings.put(
						(String)entry.getKey(), entry.getValue());
				}
			}

			return mergedSettings;
		}

		private AuthVerifierResult _verifyWithAuthVerifierConfiguration(
			AccessControlContext accessControlContext,
			AuthVerifierConfiguration authVerifierConfiguration) {

			AuthVerifierResult authVerifierResult = null;

			AuthVerifier authVerifier = AuthVerifierRegistry.getAuthVerifier(
				authVerifierConfiguration.getAuthVerifierClassName());

			if (authVerifier == null) {
				return authVerifierResult;
			}

			Properties properties = authVerifierConfiguration.getProperties();

			try {
				authVerifierResult = authVerifier.verify(
					accessControlContext, properties);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					Class<?> authVerifierClass = authVerifier.getClass();

					_log.debug(
						"Skipping " + authVerifierClass.getName(), exception);
				}

				return null;
			}

			if (authVerifierResult == null) {
				Class<?> authVerifierClass = authVerifier.getClass();

				_log.error(
					"Auth verifier " + authVerifierClass.getName() +
						" did not return an auth verifier result");

				return null;
			}

			if (authVerifierResult.getState() ==
					AuthVerifierResult.State.NOT_APPLICABLE) {

				return null;
			}

			User user = UserLocalServiceUtil.fetchUser(
				authVerifierResult.getUserId());

			if ((user != null) &&
				(!user.isActive() ||
				 !user.isEmailAddressVerificationComplete() ||
				 user.isPasswordResetRequired())) {

				long userId = authVerifierResult.getUserId();

				if (_log.isDebugEnabled()) {
					Class<?> authVerifierClass = authVerifier.getClass();

					if (!user.isActive()) {
						_log.debug(
							StringBundler.concat(
								"Auth verifier ", authVerifierClass.getName(),
								" returned inactive user ", userId));
					}
					else if (!user.isEmailAddressVerificationComplete()) {
						_log.debug(
							StringBundler.concat(
								"Auth verifier ", authVerifierClass.getName(),
								" returned user ", userId,
								" who must verify his email address"));
					}
					else {
						_log.debug(
							StringBundler.concat(
								"Auth verifier ", authVerifierClass.getName(),
								" returned user ", userId,
								" who must reset his password"));
					}
				}

				authVerifierResult = new AuthVerifierResult();

				authVerifierResult.setState(
					AuthVerifierResult.State.UNSUCCESSFUL);

				authVerifierResult.setUserId(userId);
			}

			Map<String, Object> settings = _mergeSettings(
				properties, authVerifierResult.getSettings());

			settings.put(AUTH_TYPE, authVerifier.getAuthType());

			authVerifierResult.setSettings(settings);

			return authVerifierResult;
		}

		private final AccessControlContext _accessControlContext;
		private AuthVerifierResult _authVerifierResult;
		private Set<AuthVerifierConfiguration>
			_excludedAuthVerifierConfigurations;
		private final URLPatternMapper<List<AuthVerifierConfiguration>>
			_excludeURLPatternMapper;
		private final String _requestURI;

	}

	private static class PortalAuthVerifierPipelineHolder {

		private static final AuthVerifierPipeline
			_PORTAL_AUTH_VERIFIER_PIPELINE;

		static {
			AuthVerifierPipeline portalAuthVerifierPipeline =
				new AuthVerifierPipeline(
					Collections.emptyList(),
					PortalContextLoaderListener.getPortalServletContextPath());

			BundleContext bundleContext = SystemBundleUtil.getBundleContext();

			ServiceTracker<AuthVerifierConfiguration, AuthVerifierConfiguration>
				serviceTracker = new ServiceTracker<>(
					bundleContext, AuthVerifierConfiguration.class,
					new ServiceTrackerCustomizer
						<AuthVerifierConfiguration,
						 AuthVerifierConfiguration>() {

						@Override
						public AuthVerifierConfiguration addingService(
							ServiceReference<AuthVerifierConfiguration>
								serviceReference) {

							AuthVerifierConfiguration
								authVerifierConfiguration =
									bundleContext.getService(serviceReference);

							if (authVerifierConfiguration != null) {
								portalAuthVerifierPipeline.
									_addAuthVerifierConfiguration(
										authVerifierConfiguration);
							}

							return authVerifierConfiguration;
						}

						@Override
						public void modifiedService(
							ServiceReference<AuthVerifierConfiguration>
								serviceReference,
							AuthVerifierConfiguration
								authVerifierConfiguration) {
						}

						@Override
						public void removedService(
							ServiceReference<AuthVerifierConfiguration>
								serviceReference,
							AuthVerifierConfiguration
								authVerifierConfiguration) {

							portalAuthVerifierPipeline.
								_removeAuthVerifierConfiguration(
									authVerifierConfiguration);

							bundleContext.ungetService(serviceReference);
						}

					});

			serviceTracker.open();

			_PORTAL_AUTH_VERIFIER_PIPELINE = portalAuthVerifierPipeline;
		}

	}

}