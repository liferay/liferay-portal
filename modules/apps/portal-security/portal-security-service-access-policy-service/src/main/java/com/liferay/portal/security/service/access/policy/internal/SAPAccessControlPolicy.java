/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.service.access.policy.internal;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.security.access.control.AccessControlPolicy;
import com.liferay.portal.kernel.security.access.control.AccessControlUtil;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.security.access.control.BaseAccessControlPolicy;
import com.liferay.portal.kernel.security.auth.AccessControlContext;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifierResult;
import com.liferay.portal.kernel.security.service.access.policy.ServiceAccessPolicy;
import com.liferay.portal.kernel.security.service.access.policy.ServiceAccessPolicyThreadLocal;
import com.liferay.portal.kernel.settings.CompanyServiceSettingsLocator;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.service.access.policy.configuration.SAPConfiguration;
import com.liferay.portal.security.service.access.policy.constants.SAPConstants;
import com.liferay.portal.security.service.access.policy.model.SAPEntry;
import com.liferay.portal.security.service.access.policy.service.SAPEntryLocalService;

import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mika Koivisto
 */
@Component(service = AccessControlPolicy.class)
public class SAPAccessControlPolicy extends BaseAccessControlPolicy {

	@Override
	public void onServiceRemoteAccess(
			Method method, Object[] arguments,
			AccessControlled accessControlled)
		throws SecurityException {

		if (_isChecked()) {
			return;
		}

		List<String> serviceAccessPolicyNames = new ArrayList<>();

		serviceAccessPolicyNames.addAll(_getActiveServiceAccessPolicyNames());
		serviceAccessPolicyNames.addAll(
			_getDefaultServiceAccessPolicyNames(
				CompanyThreadLocal.getCompanyId()));
		serviceAccessPolicyNames.addAll(
			_getSystemServiceAccessPolicyNames(
				CompanyThreadLocal.getCompanyId()));

		Set<String> allowedServiceSignatures = _loadAllowedServiceSignatures(
			CompanyThreadLocal.getCompanyId(), serviceAccessPolicyNames);

		Class<?> clazz = method.getDeclaringClass();

		_checkAccess(
			allowedServiceSignatures, clazz.getName(), method.getName());
	}

	protected boolean matches(
		String className, String methodName, String allowedServiceSignature) {

		String allowedClassName = null;
		String allowedMethodName = null;

		int index = allowedServiceSignature.indexOf(CharPool.POUND);

		if (index > -1) {
			allowedClassName = allowedServiceSignature.substring(0, index);
			allowedMethodName = allowedServiceSignature.substring(index + 1);
		}
		else {
			allowedClassName = allowedServiceSignature;
		}

		boolean wildcardMatchClass = false;

		if (Validator.isNotNull(allowedClassName) &&
			allowedClassName.endsWith(StringPool.STAR)) {

			allowedClassName = allowedClassName.substring(
				0, allowedClassName.length() - 1);
			wildcardMatchClass = true;
		}

		boolean wildcardMatchMethod = false;

		if (Validator.isNotNull(allowedMethodName) &&
			allowedMethodName.endsWith(StringPool.STAR)) {

			allowedMethodName = allowedMethodName.substring(
				0, allowedMethodName.length() - 1);
			wildcardMatchMethod = true;
		}

		if (Validator.isNotNull(allowedClassName) &&
			Validator.isNotNull(allowedMethodName)) {

			if (wildcardMatchClass && !className.startsWith(allowedClassName)) {
				return false;
			}
			else if (!wildcardMatchClass &&
					 !className.equals(allowedClassName)) {

				return false;
			}

			if (wildcardMatchMethod &&
				!methodName.startsWith(allowedMethodName)) {

				return false;
			}
			else if (!wildcardMatchMethod &&
					 !methodName.equals(allowedMethodName)) {

				return false;
			}

			return true;
		}
		else if (Validator.isNotNull(allowedClassName)) {
			if (wildcardMatchClass && !className.startsWith(allowedClassName)) {
				return false;
			}
			else if (!wildcardMatchClass &&
					 !className.equals(allowedClassName)) {

				return false;
			}

			return true;
		}
		else if (Validator.isNotNull(allowedMethodName)) {
			if (wildcardMatchMethod &&
				!methodName.startsWith(allowedMethodName)) {

				return false;
			}
			else if (!wildcardMatchMethod &&
					 !methodName.equals(allowedMethodName)) {

				return false;
			}

			return true;
		}
		else if (wildcardMatchClass && Validator.isNull(allowedClassName)) {
			return true;
		}

		return false;
	}

	private void _checkAccess(
		Set<String> allowedServiceSignatures, String className,
		String methodName) {

		if (allowedServiceSignatures.contains(StringPool.STAR) ||
			allowedServiceSignatures.contains(className)) {

			return;
		}

		String classNameAndMethodName = StringBundler.concat(
			className, StringPool.POUND, methodName);

		if (allowedServiceSignatures.contains(classNameAndMethodName)) {
			return;
		}

		for (String allowedServiceSignature : allowedServiceSignatures) {
			if (matches(className, methodName, allowedServiceSignature)) {
				return;
			}
		}

		throw new SecurityException(
			"Access denied to " + classNameAndMethodName);
	}

	private List<String> _getActiveServiceAccessPolicyNames() {
		List<String> activeServiceAccessPolicyNames =
			ServiceAccessPolicyThreadLocal.getActiveServiceAccessPolicyNames();

		if (activeServiceAccessPolicyNames == null) {
			activeServiceAccessPolicyNames = new ArrayList<>();

			ServiceAccessPolicyThreadLocal.setActiveServiceAccessPolicyNames(
				activeServiceAccessPolicyNames);
		}

		AccessControlContext accessControlContext =
			AccessControlUtil.getAccessControlContext();

		if (accessControlContext == null) {
			return activeServiceAccessPolicyNames;
		}

		AuthVerifierResult authVerifierResult =
			accessControlContext.getAuthVerifierResult();

		if (authVerifierResult == null) {
			return activeServiceAccessPolicyNames;
		}

		Map<String, Object> settings = authVerifierResult.getSettings();

		List<String> serviceAccessPolicyNames = (List<String>)settings.get(
			ServiceAccessPolicy.SERVICE_ACCESS_POLICY_NAMES);

		if (serviceAccessPolicyNames != null) {
			activeServiceAccessPolicyNames.addAll(serviceAccessPolicyNames);
		}

		return activeServiceAccessPolicyNames;
	}

	private List<String> _getDefaultServiceAccessPolicyNames(long companyId) {
		List<SAPEntry> defaultSAPEntries =
			_sapEntryLocalService.getDefaultSAPEntries(companyId, true);

		List<String> defaultServiceAccessPolicyNames = new ArrayList<>(
			defaultSAPEntries.size());

		for (SAPEntry sapEntry : defaultSAPEntries) {
			defaultServiceAccessPolicyNames.add(sapEntry.getName());
		}

		return defaultServiceAccessPolicyNames;
	}

	private List<String> _getSystemServiceAccessPolicyNames(long companyId) {
		SAPConfiguration sapConfiguration = null;

		try {
			sapConfiguration = _configurationProvider.getConfiguration(
				SAPConfiguration.class,
				new CompanyServiceSettingsLocator(
					companyId, SAPConstants.SERVICE_NAME));
		}
		catch (ConfigurationException configurationException) {
			throw new SystemException(
				"Unable to get service access policy configuration",
				configurationException);
		}

		List<String> systemServiceAccessPolicyNames = new ArrayList<>(2);

		if (!sapConfiguration.useSystemSAPEntries()) {
			return systemServiceAccessPolicyNames;
		}

		systemServiceAccessPolicyNames.add(
			sapConfiguration.systemDefaultSAPEntryName());

		boolean passwordBasedAuthentication = false;

		AccessControlContext accessControlContext =
			AccessControlUtil.getAccessControlContext();

		if (accessControlContext != null) {
			AuthVerifierResult authVerifierResult =
				accessControlContext.getAuthVerifierResult();

			if (authVerifierResult != null) {
				passwordBasedAuthentication =
					authVerifierResult.isPasswordBasedAuthentication();
			}

			HttpServletRequest httpServletRequest =
				accessControlContext.getRequest();

			if (GetterUtil.getBoolean(
					httpServletRequest.getAttribute(
						"com.liferay.portal.vulcan.internal.template.servlet." +
							"RESTClientHttpRequestDelegate"))) {

				systemServiceAccessPolicyNames.add(
					sapConfiguration.
						systemRESTClientTemplateObjectSAPEntryName());
			}
		}

		if (passwordBasedAuthentication) {
			systemServiceAccessPolicyNames.add(
				sapConfiguration.systemUserPasswordSAPEntryName());
		}

		return systemServiceAccessPolicyNames;
	}

	private boolean _isChecked() {
		AccessControlContext accessControlContext =
			AccessControlUtil.getAccessControlContext();

		if (accessControlContext != null) {
			Map<String, Object> settings = accessControlContext.getSettings();

			int serviceDepth = (Integer)settings.get(
				AccessControlContext.Settings.SERVICE_DEPTH.toString());

			if (serviceDepth > 1) {
				return true;
			}
		}

		return false;
	}

	private Set<String> _loadAllowedServiceSignatures(
		long companyId, List<String> serviceAccessPolicyNames) {

		Set<String> allowedServiceSignatures = new HashSet<>();

		for (String serviceAccessPolicyName : serviceAccessPolicyNames) {
			try {
				SAPEntry sapEntry = _sapEntryLocalService.getSAPEntry(
					companyId, serviceAccessPolicyName);

				if (!sapEntry.isEnabled()) {
					continue;
				}

				allowedServiceSignatures.addAll(
					sapEntry.getAllowedServiceSignaturesList());
			}
			catch (PortalException portalException) {
				throw new SystemException(portalException);
			}
		}

		return allowedServiceSignatures;
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private SAPEntryLocalService _sapEntryLocalService;

}