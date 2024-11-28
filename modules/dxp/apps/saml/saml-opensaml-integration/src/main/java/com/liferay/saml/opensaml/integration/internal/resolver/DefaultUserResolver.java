/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.opensaml.integration.internal.resolver;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.UserEmailAddressException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.saml.opensaml.integration.field.expression.handler.UserFieldExpressionHandler;
import com.liferay.saml.opensaml.integration.field.expression.handler.registry.UserFieldExpressionHandlerRegistry;
import com.liferay.saml.opensaml.integration.field.expression.resolver.UserFieldExpressionResolver;
import com.liferay.saml.opensaml.integration.field.expression.resolver.registry.UserFieldExpressionResolverRegistry;
import com.liferay.saml.opensaml.integration.processor.UserProcessor;
import com.liferay.saml.opensaml.integration.processor.factory.UserProcessorFactory;
import com.liferay.saml.opensaml.integration.resolver.UserResolver;
import com.liferay.saml.persistence.model.SamlPeerBinding;
import com.liferay.saml.persistence.model.SamlSpIdpConnection;
import com.liferay.saml.persistence.service.SamlPeerBindingLocalService;
import com.liferay.saml.persistence.service.SamlSpIdpConnectionLocalService;
import com.liferay.saml.runtime.configuration.SamlProviderConfigurationHelper;
import com.liferay.saml.runtime.exception.SubjectException;

import java.io.Serializable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mika Koivisto
 */
@Component(
	property = "service.ranking:Integer=" + Integer.MIN_VALUE,
	service = UserResolver.class
)
public class DefaultUserResolver implements UserResolver {

	@Override
	public User resolveUser(
			UserResolverSAMLContext userResolverSAMLContext,
			ServiceContext serviceContext)
		throws Exception {

		String subjectNameFormat =
			userResolverSAMLContext.resolveSubjectNameFormat();

		if (_log.isDebugEnabled()) {
			String subjectNameIdentifier =
				userResolverSAMLContext.resolveSubjectNameIdentifier();

			_log.debug(
				StringBundler.concat(
					"Resolving user with name ID format ", subjectNameFormat,
					" and value ", subjectNameIdentifier));
		}

		long companyId = CompanyThreadLocal.getCompanyId();

		SamlSpIdpConnection samlSpIdpConnection =
			_samlSpIdpConnectionLocalService.getSamlSpIdpConnection(
				companyId, userResolverSAMLContext.resolvePeerEntityId());

		subjectNameFormat = _getNameIdFormat(
			userResolverSAMLContext, samlSpIdpConnection.getNameIdFormat());

		return _importUser(
			companyId, samlSpIdpConnection,
			userResolverSAMLContext.resolveSubjectNameIdentifier(),
			subjectNameFormat, userResolverSAMLContext, serviceContext);
	}

	private User _addUser(
			long companyId, SamlSpIdpConnection samlSpIdpConnection,
			Map<String, List<Serializable>> attributesMap,
			ServiceContext serviceContext)
		throws Exception {

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Adding user with attributes map " +
					MapUtil.toString(attributesMap));
		}

		Company company = _companyLocalService.getCompany(companyId);
		String emailAddress = _getValueAsString("emailAddress", attributesMap);

		if (samlSpIdpConnection.isUnknownUsersAreStrangers()) {
			if (!company.isStrangers()) {
				throw new SubjectException(
					"User is a stranger and company " + companyId +
						" does not allow strangers to create accounts");
			}
			else if (Validator.isNotNull(emailAddress) &&
					 !company.isStrangersWithMx() &&
					 company.hasCompanyMx(emailAddress)) {

				throw new UserEmailAddressException.MustNotUseCompanyMx(
					emailAddress);
			}
		}

		User user = _userLocalService.createUser(0);

		user.setCompanyId(companyId);

		user = _processUser(user, attributesMap, serviceContext);

		if (_log.isDebugEnabled()) {
			_log.debug("Added user " + user.toString());
		}

		return user;
	}

	private Map<String, List<Serializable>> _getAttributesMap(
		SamlSpIdpConnection samlSpIdpConnection,
		UserResolverSAMLContext userResolverSAMLContext) {

		try {
			return userResolverSAMLContext.
				resolveBearerAssertionAttributesWithMapping(
					samlSpIdpConnection.getNormalizedUserAttributeMappings());
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}

		return Collections.emptyMap();
	}

	private String _getNameIdFormat(
		UserResolverSAMLContext userResolverSAMLContext,
		String defaultNameIdFormat) {

		String format = userResolverSAMLContext.resolveSubjectNameFormat();

		if (Validator.isNull(format)) {
			format = defaultNameIdFormat;
		}

		return format;
	}

	private String _getPrefix(String userFieldExpression) {
		if (userFieldExpression == null) {
			return null;
		}

		int prefixEndIndex = userFieldExpression.indexOf(CharPool.COLON);

		if (prefixEndIndex == -1) {
			return StringPool.BLANK;
		}

		return userFieldExpression.substring(0, prefixEndIndex);
	}

	private UserFieldExpressionResolver _getUserFieldExpressionResolver(
		String userIdentifierExpression) {

		String userFieldExpressionResolverKey = _getPrefix(
			userIdentifierExpression);

		if (Validator.isBlank(userFieldExpressionResolverKey)) {
			userFieldExpressionResolverKey = userIdentifierExpression;
		}

		return _userFieldExpressionResolverRegistry.
			getUserFieldExpressionResolver(userFieldExpressionResolverKey);
	}

	private String _getValueAsString(
		String key, Map<String, List<Serializable>> attributesMap) {

		List<Serializable> values = attributesMap.get(key);

		if (ListUtil.isEmpty(values)) {
			return null;
		}

		return String.valueOf(values.get(0));
	}

	private String[] _getValuesAsString(
		String key, Map<String, List<Serializable>> attributesMap) {

		List<Serializable> values = attributesMap.get(key);

		if (ListUtil.isEmpty(values)) {
			return null;
		}

		return ArrayUtil.toStringArray(values);
	}

	private User _importUser(
			long companyId, SamlSpIdpConnection samlSpIdpConnection,
			String subjectNameIdentifier, String nameIdFormat,
			UserResolverSAMLContext userResolverSAMLContext,
			ServiceContext serviceContext)
		throws Exception {

		UserFieldExpressionResolver userFieldExpressionResolver =
			_getUserFieldExpressionResolver(
				samlSpIdpConnection.getUserIdentifierExpression());

		Map<String, List<Serializable>> attributesMap = _getAttributesMap(
			samlSpIdpConnection, userResolverSAMLContext);

		String userFieldExpression = _removePrefix(
			StringPool.BLANK,
			GetterUtil.getString(
				userFieldExpressionResolver.resolveUserFieldExpression(
					attributesMap, userResolverSAMLContext)));

		if (Validator.isBlank(userFieldExpression)) {
			if (_log.isDebugEnabled()) {
				_log.debug("User field expression is null");
			}

			User user = _resolveByNameId(
				companyId, nameIdFormat,
				userResolverSAMLContext.resolveSubjectNameQualifier(),
				subjectNameIdentifier,
				userResolverSAMLContext.resolvePeerEntityId());

			if (user != null) {
				return _updateUser(user, attributesMap, serviceContext);
			}

			return null;
		}

		String searchFieldValue = subjectNameIdentifier;

		if (attributesMap.containsKey(userFieldExpression)) {
			searchFieldValue = _getValueAsString(
				userFieldExpression, attributesMap);

			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"User identifier expression is mapped to SAML ",
						"attribute value \"", searchFieldValue, "\""));
			}
		}
		else {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Resolving user using subject naming identifier ",
						subjectNameIdentifier, " and name ID format ",
						nameIdFormat));
			}
		}

		String prefix = _getPrefix(userFieldExpression);

		UserFieldExpressionHandler userFieldExpressionHandler =
			_userFieldExpressionHandlerRegistry.getFieldExpressionHandler(
				prefix);

		User user;

		if (Validator.isNotNull(userFieldExpression) &&
			_samlProviderConfigurationHelper.isLDAPImportEnabled()) {

			user = userFieldExpressionHandler.getLdapUser(
				companyId, searchFieldValue,
				_removePrefix(prefix, userFieldExpression));

			if (user != null) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Matched and imported LDAP user " + user.toString());
				}

				return user;
			}
		}

		user = _resolveByNameId(
			companyId, nameIdFormat,
			userResolverSAMLContext.resolveSubjectNameQualifier(),
			subjectNameIdentifier,
			userResolverSAMLContext.resolvePeerEntityId());

		if (user == null) {
			user = userFieldExpressionHandler.getUser(
				companyId, searchFieldValue,
				_removePrefix(prefix, userFieldExpression));
		}

		if (user == null) {
			return _addUser(
				companyId, samlSpIdpConnection, attributesMap, serviceContext);
		}

		return _updateUser(user, attributesMap, serviceContext);
	}

	private User _processUser(
			User user, Map<String, List<Serializable>> attributesMap,
			ServiceContext serviceContext)
		throws Exception {

		UserProcessor userProcessor = _userProcessorFactory.create(
			user, _userFieldExpressionHandlerRegistry);

		for (String key : attributesMap.keySet()) {
			userProcessor.setValueArray(
				key, _getValuesAsString(key, attributesMap));
		}

		return userProcessor.process(serviceContext);
	}

	private String _removePrefix(
		String prefix, String prefixedUserFieldExpression) {

		if ((prefixedUserFieldExpression.length() > prefix.length()) &&
			(prefixedUserFieldExpression.charAt(prefix.length()) ==
				CharPool.COLON) &&
			prefixedUserFieldExpression.startsWith(prefix)) {

			return prefixedUserFieldExpression.substring(prefix.length() + 1);
		}

		return prefixedUserFieldExpression;
	}

	private User _resolveByNameId(
		long companyId, String subjectNameFormat, String subjectNameQualifier,
		String subjectNameIdentifier, String samlIdpEntityId) {

		SamlPeerBinding samlPeerBinding =
			_samlPeerBindingLocalService.fetchSamlPeerBinding(
				companyId, false, subjectNameFormat, subjectNameQualifier,
				subjectNameIdentifier, samlIdpEntityId);

		if (samlPeerBinding != null) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Matched known subject name identifier ",
						subjectNameIdentifier, " of subject name format ",
						subjectNameFormat, " with subject name qualifier \"",
						subjectNameQualifier, "\" for SAML IDP entity ID ",
						samlIdpEntityId));
			}

			return _userLocalService.fetchUserById(samlPeerBinding.getUserId());
		}

		return null;
	}

	private User _updateUser(
			User user, Map<String, List<Serializable>> attributesMap,
			ServiceContext serviceContext)
		throws Exception {

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Updating user ", user.getUserId(), " with attributes map ",
					MapUtil.toString(attributesMap)));
		}

		return _processUser(user, attributesMap, serviceContext);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DefaultUserResolver.class);

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private SamlPeerBindingLocalService _samlPeerBindingLocalService;

	@Reference
	private SamlProviderConfigurationHelper _samlProviderConfigurationHelper;

	@Reference
	private SamlSpIdpConnectionLocalService _samlSpIdpConnectionLocalService;

	@Reference
	private UserFieldExpressionHandlerRegistry
		_userFieldExpressionHandlerRegistry;

	@Reference
	private UserFieldExpressionResolverRegistry
		_userFieldExpressionResolverRegistry;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private UserProcessorFactory _userProcessorFactory;

}