/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.vulcan.graphql.validation;

import com.liferay.oauth2.provider.rest.internal.scope.logic.AnnotationScopeLogic;
import com.liferay.oauth2.provider.rest.internal.scope.logic.HttpMethodScopeLogic;
import com.liferay.oauth2.provider.rest.internal.scope.logic.ScopeLogic;
import com.liferay.oauth2.provider.scope.ScopeChecker;
import com.liferay.oauth2.provider.scope.liferay.OAuth2ProviderScopeLiferayAccessControlContext;
import com.liferay.oauth2.provider.scope.liferay.ScopeContext;
import com.liferay.portal.kernel.security.access.control.AccessControlUtil;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.security.auth.AccessControlContext;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifierResult;
import com.liferay.portal.kernel.security.service.access.policy.ServiceAccessPolicy;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.access.control.AccessControlAdvisor;
import com.liferay.portal.security.access.control.AccessControlAdvisorImpl;
import com.liferay.portal.vulcan.graphql.validation.GraphQLRequestContext;
import com.liferay.portal.vulcan.graphql.validation.GraphQLRequestContextValidator;

import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.core.Application;

import java.lang.annotation.Annotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Carlos Correa
 * @author Stian Sigvartsen
 */
@Component(service = GraphQLRequestContextValidator.class)
public class OAuth2GraphQLRequestContextValidator
	implements GraphQLRequestContextValidator {

	@Override
	public void validate(GraphQLRequestContext graphQLRequestContext)
		throws Exception {

		if (!graphQLRequestContext.isJaxRsResourceInvocation()) {
			return;
		}

		if (OAuth2ProviderScopeLiferayAccessControlContext.
				isOAuth2AuthVerified()) {

			ServiceReference<?> serviceReference = _getServiceReference(
				graphQLRequestContext.getApplicationName());

			_enableSAP(serviceReference);

			_checkScope(graphQLRequestContext, serviceReference);
		}

		_setServiceDepth();

		_accessControlAdvisor.accept(
			graphQLRequestContext.getResourceMethod(), new Object[0],
			_NULL_ACCESS_CONTROLLED);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		_scopeLogics.put("annotations", new AnnotationScopeLogic());
		_scopeLogics.put(
			"http.method", new HttpMethodScopeLogic(bundleContext));
	}

	private void _checkScope(
			GraphQLRequestContext graphQLRequestContext,
			ServiceReference<?> applicationServiceReference)
		throws Exception {

		String scopeSheckerType = _getProperty(
			null, "oauth2.scope.checker.type", applicationServiceReference);

		if (scopeSheckerType == null) {
			scopeSheckerType = _getProperty(
				"http.method", "oauth2.scope.checker.type",
				applicationServiceReference);
		}

		ScopeLogic scopeLogic = _scopeLogics.get(scopeSheckerType);

		if (scopeLogic == null) {
			throw new ForbiddenException();
		}

		_scopeContext.setApplicationName(
			graphQLRequestContext.getApplicationName());
		_scopeContext.setBundle(
			FrameworkUtil.getBundle(graphQLRequestContext.getResourceClass()));
		_scopeContext.setCompanyId(graphQLRequestContext.getCompanyId());

		try {
			if (!scopeLogic.check(
					applicationServiceReference::getProperty,
					graphQLRequestContext.getResourceClass(),
					graphQLRequestContext.getResourceMethod(), _scopeChecker)) {

				throw new ForbiddenException();
			}
		}
		catch (Exception exception) {
			throw new ForbiddenException(exception);
		}
		finally {
			_scopeContext.setApplicationName(null);
			_scopeContext.setBundle(null);
			_scopeContext.setCompanyId(0L);
		}
	}

	private void _enableSAP(ServiceReference<?> serviceReference)
		throws Exception {

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

		String policyName = _getProperty(
			"AUTHORIZED_OAUTH2_SAP", "oauth2.service.access.policy.name",
			serviceReference);

		if (!serviceAccessPolicyNames.contains(policyName)) {
			serviceAccessPolicyNames.add(policyName);
		}
	}

	private String _getProperty(
		String defaultValue, String propertyName,
		ServiceReference<?> serviceReference) {

		String propertyValue = (String)serviceReference.getProperty(
			propertyName);

		if (Validator.isBlank(propertyValue)) {
			return defaultValue;
		}

		return propertyValue;
	}

	private ServiceReference<?> _getServiceReference(String applicationName)
		throws Exception {

		List<ServiceReference<Application>> serviceReferences =
			(List<ServiceReference<Application>>)
				_bundleContext.getServiceReferences(
					Application.class,
					"(osgi.jaxrs.name=" + applicationName + ")");

		if (ListUtil.isNotEmpty(serviceReferences)) {
			return serviceReferences.get(0);
		}

		throw new UnsupportedOperationException(
			"Invalid JAX-RS application " + applicationName);
	}

	private void _setServiceDepth() {
		AccessControlContext accessControlContext =
			AccessControlUtil.getAccessControlContext();

		if (accessControlContext == null) {
			return;
		}

		Map<String, Object> settings = accessControlContext.getSettings();

		settings.put(
			AccessControlContext.Settings.SERVICE_DEPTH.toString(),
			Integer.valueOf(1));
	}

	private static final AccessControlled _NULL_ACCESS_CONTROLLED =
		new AccessControlled() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return AccessControlled.class;
			}

			@Override
			public boolean guestAccessEnabled() {
				return false;
			}

			@Override
			public boolean hostAllowedValidationEnabled() {
				return false;
			}

		};

	private final AccessControlAdvisor _accessControlAdvisor =
		new AccessControlAdvisorImpl();
	private BundleContext _bundleContext;

	@Reference
	private ScopeChecker _scopeChecker;

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	private volatile ScopeContext _scopeContext;

	private final Map<String, ScopeLogic> _scopeLogics = new HashMap<>();

}