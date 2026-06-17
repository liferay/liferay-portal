/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.ldap.internal;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.lang.ThreadContextClassLoaderUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.security.ldap.LDAPSettings;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropertiesUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.ldap.SafeLdapContext;
import com.liferay.portal.security.ldap.SafeLdapFilter;
import com.liferay.portal.security.ldap.SafeLdapFilterConstraints;
import com.liferay.portal.security.ldap.SafeLdapName;
import com.liferay.portal.security.ldap.SafePortalLDAP;
import com.liferay.portal.security.ldap.UserConverterKeys;
import com.liferay.portal.security.ldap.configuration.ConfigurationProvider;
import com.liferay.portal.security.ldap.configuration.LDAPServerConfiguration;
import com.liferay.portal.security.ldap.configuration.SystemLDAPConfiguration;
import com.liferay.portal.security.ldap.constants.LDAPConstants;
import com.liferay.portal.security.ldap.internal.util.SafeLDAPReferralUtil;
import com.liferay.portal.security.ldap.internal.validator.SafeLdapContextImpl;
import com.liferay.portal.security.ldap.util.LDAPUtil;
import com.liferay.portal.security.ldap.validator.LDAPFilterValidator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.OperationNotSupportedException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.PagedResultsControl;
import javax.naming.ldap.PagedResultsResponseControl;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Michael Young
 * @author Brian Wing Shun Chan
 * @author Jerry Niu
 * @author Scott Lee
 * @author Hervé Ménage
 * @author Samuel Kong
 * @author Ryan Park
 * @author Wesley Gong
 * @author Marcellus Tavares
 * @author Hugo Huijser
 * @author Edward Han
 * @author Tomas Polesovsky
 * @author Marta Medio
 */
@Component(service = SafePortalLDAP.class)
public class SafePortalLDAPImpl implements SafePortalLDAP {

	@Override
	public Binding getGroup(long ldapServerId, long companyId, String groupName)
		throws Exception {

		LDAPServerConfiguration ldapServerConfiguration =
			_ldapServerConfigurationProvider.getConfiguration(
				companyId, ldapServerId);

		if (ldapServerConfiguration.ldapServerId() != ldapServerId) {
			return null;
		}

		SafeLdapContext safeLdapContext = getSafeLdapContext(
			ldapServerId, companyId);

		NamingEnumeration<SearchResult> enumeration = null;

		try {
			if (safeLdapContext == null) {
				return null;
			}

			Properties groupMappings = _ldapSettings.getGroupMappings(
				ldapServerId, companyId);

			SafeLdapFilter safeLdapFilter = SafeLdapFilterConstraints.eq(
				groupMappings.getProperty("groupName"), groupName);

			SafeLdapFilter groupSafeLdapFilter =
				LDAPUtil.getGroupSearchSafeLdapFilter(
					ldapServerConfiguration, _ldapFilterValidator);

			if (groupSafeLdapFilter != null) {
				safeLdapFilter = safeLdapFilter.and(groupSafeLdapFilter);
			}

			SearchControls searchControls = new SearchControls(
				SearchControls.SUBTREE_SCOPE, 1, 0, null, false, false);

			enumeration = safeLdapContext.search(
				LDAPUtil.getGroupsDNSafeLdapName(ldapServerConfiguration),
				safeLdapFilter, searchControls);

			if (enumeration.hasMoreElements()) {
				return enumeration.nextElement();
			}

			return null;
		}
		finally {
			if (enumeration != null) {
				enumeration.close();
			}

			if (safeLdapContext != null) {
				safeLdapContext.close();
			}
		}
	}

	@Override
	public Attributes getGroupAttributes(
			long ldapServerId, long companyId, SafeLdapContext safeLdapContext,
			SafeLdapName userGroupDNSafeLdapName)
		throws Exception {

		return getGroupAttributes(
			ldapServerId, companyId, safeLdapContext, userGroupDNSafeLdapName,
			false);
	}

	@Override
	public Attributes getGroupAttributes(
			long ldapServerId, long companyId, SafeLdapContext safeLdapContext,
			SafeLdapName userGroupDNSafeLdapName,
			boolean includeReferenceAttributes)
		throws Exception {

		Properties groupMappings = _ldapSettings.getGroupMappings(
			ldapServerId, companyId);

		List<String> mappedGroupAttributeIds = new ArrayList<>();

		mappedGroupAttributeIds.add(groupMappings.getProperty("description"));
		mappedGroupAttributeIds.add(groupMappings.getProperty("groupName"));

		if (includeReferenceAttributes) {
			mappedGroupAttributeIds.add(groupMappings.getProperty("user"));
		}

		Attributes attributes = _getAttributes(
			safeLdapContext, userGroupDNSafeLdapName,
			mappedGroupAttributeIds.toArray(new String[0]));

		if (_log.isDebugEnabled()) {
			if ((attributes == null) || (attributes.size() == 0)) {
				_log.debug(
					"No LDAP group attributes found for " +
						userGroupDNSafeLdapName);
			}
			else {
				for (String attributeId : mappedGroupAttributeIds) {
					Attribute attribute = attributes.get(attributeId);

					if (attribute == null) {
						continue;
					}

					_log.debug("LDAP group attribute " + attribute.toString());
				}
			}
		}

		return attributes;
	}

	@Override
	public byte[] getGroups(
			long ldapServerId, long companyId, SafeLdapContext safeLdapContext,
			byte[] cookie, int maxResults, List<SearchResult> searchResults)
		throws Exception {

		LDAPServerConfiguration ldapServerConfiguration =
			_ldapServerConfigurationProvider.getConfiguration(
				companyId, ldapServerId);

		if (ldapServerConfiguration.ldapServerId() != ldapServerId) {
			return null;
		}

		return getGroups(
			companyId, safeLdapContext, cookie, maxResults,
			LDAPUtil.getBaseDNSafeLdapName(ldapServerConfiguration),
			LDAPUtil.getGroupSearchSafeLdapFilter(
				ldapServerConfiguration, _ldapFilterValidator),
			searchResults);
	}

	@Override
	public byte[] getGroups(
			long ldapServerId, long companyId, SafeLdapContext safeLdapContext,
			byte[] cookie, int maxResults, String[] attributeIds,
			List<SearchResult> searchResults)
		throws Exception {

		LDAPServerConfiguration ldapServerConfiguration =
			_ldapServerConfigurationProvider.getConfiguration(
				companyId, ldapServerId);

		if (ldapServerConfiguration.ldapServerId() != ldapServerId) {
			return null;
		}

		return getGroups(
			companyId, safeLdapContext, cookie, maxResults,
			LDAPUtil.getBaseDNSafeLdapName(ldapServerConfiguration),
			LDAPUtil.getGroupSearchSafeLdapFilter(
				ldapServerConfiguration, _ldapFilterValidator),
			attributeIds, searchResults);
	}

	@Override
	public byte[] getGroups(
			long companyId, SafeLdapContext safeLdapContext, byte[] cookie,
			int maxResults, SafeLdapName baseDNSafeLdapName,
			SafeLdapFilter groupLDAPFilter, List<SearchResult> searchResults)
		throws Exception {

		return searchLDAP(
			companyId, safeLdapContext, cookie, maxResults, baseDNSafeLdapName,
			groupLDAPFilter, null, searchResults);
	}

	@Override
	public byte[] getGroups(
			long companyId, SafeLdapContext safeLdapContext, byte[] cookie,
			int maxResults, SafeLdapName baseDNSafeLdapName,
			SafeLdapFilter groupLDAPFilter, String[] attributeIds,
			List<SearchResult> searchResults)
		throws Exception {

		return searchLDAP(
			companyId, safeLdapContext, cookie, maxResults, baseDNSafeLdapName,
			groupLDAPFilter, attributeIds, searchResults);
	}

	@Override
	public SafeLdapName getGroupsDNSafeLdapName(
			long ldapServerId, long companyId)
		throws Exception {

		LDAPServerConfiguration ldapServerConfiguration =
			_ldapServerConfigurationProvider.getConfiguration(
				companyId, ldapServerId);

		if (ldapServerConfiguration.ldapServerId() != ldapServerId) {
			return null;
		}

		return LDAPUtil.getGroupsDNSafeLdapName(ldapServerConfiguration);
	}

	@Override
	public long getLdapServerId(
			long companyId, String screenName, String emailAddress)
		throws Exception {

		long preferredLDAPServerId = _ldapSettings.getPreferredLDAPServerId(
			companyId, screenName);

		if ((preferredLDAPServerId >= 0) &&
			hasUser(
				preferredLDAPServerId, companyId, screenName, emailAddress)) {

			return preferredLDAPServerId;
		}

		List<LDAPServerConfiguration> ldapServerConfigurations =
			_ldapServerConfigurationProvider.getConfigurations(companyId);

		for (LDAPServerConfiguration ldapServerConfiguration :
				ldapServerConfigurations) {

			if (hasUser(
					ldapServerConfiguration.ldapServerId(), companyId,
					screenName, emailAddress)) {

				return ldapServerConfiguration.ldapServerId();
			}
		}

		if (ListUtil.isNotEmpty(ldapServerConfigurations)) {
			LDAPServerConfiguration ldapServerConfiguration =
				ldapServerConfigurations.get(0);

			return ldapServerConfiguration.ldapServerId();
		}

		return LDAPServerConfiguration.LDAP_SERVER_ID_DEFAULT;
	}

	@Override
	public Attribute getMultivaluedAttribute(
			long companyId, SafeLdapContext safeLdapContext,
			SafeLdapName baseDNSafeLdapName, SafeLdapFilter safeLdapFilter,
			Attribute attribute)
		throws Exception {

		if (attribute.size() > 0) {
			return attribute;
		}

		SystemLDAPConfiguration systemLDAPConfiguration =
			_systemLDAPConfigurationProvider.getConfiguration(companyId);

		String[] attributeIds = {
			_getNextRange(systemLDAPConfiguration, attribute.getID())
		};

		while (true) {
			List<SearchResult> searchResults = new ArrayList<>();

			searchLDAP(
				companyId, safeLdapContext, new byte[0], 0, baseDNSafeLdapName,
				safeLdapFilter, attributeIds, searchResults);

			if (searchResults.size() != 1) {
				break;
			}

			SearchResult searchResult = searchResults.get(0);

			Attributes attributes = searchResult.getAttributes();

			if (attributes.size() != 1) {
				break;
			}

			NamingEnumeration<? extends Attribute> enumeration = null;

			try {
				enumeration = attributes.getAll();

				if (!enumeration.hasMoreElements()) {
					break;
				}

				Attribute currentAttribute = enumeration.nextElement();

				for (int i = 0; i < currentAttribute.size(); i++) {
					attribute.add(currentAttribute.get(i));
				}

				if (StringUtil.endsWith(
						currentAttribute.getID(), StringPool.STAR) ||
					(currentAttribute.size() <
						systemLDAPConfiguration.rangeSize())) {

					break;
				}
			}
			finally {
				if (enumeration != null) {
					enumeration.close();
				}
			}

			attributeIds[0] = _getNextRange(
				systemLDAPConfiguration, attributeIds[0]);
		}

		return attribute;
	}

	@Override
	public SafeLdapContext getSafeLdapContext(
		long ldapServerId, long companyId) {

		LDAPServerConfiguration ldapServerConfiguration =
			_ldapServerConfigurationProvider.getConfiguration(
				companyId, ldapServerId);

		if (ldapServerConfiguration.ldapServerId() != ldapServerId) {
			return null;
		}

		return getSafeLdapContext(
			companyId, ldapServerConfiguration.baseProviderURL(),
			ldapServerConfiguration.securityPrincipal(),
			ldapServerConfiguration.securityCredential());
	}

	@Override
	public SafeLdapContext getSafeLdapContext(
		long companyId, String providerURL, String principal,
		String credentials) {

		SystemLDAPConfiguration systemLDAPConfiguration =
			_systemLDAPConfigurationProvider.getConfiguration(companyId);

		Properties environmentProperties = new Properties();

		environmentProperties.put(
			Context.INITIAL_CONTEXT_FACTORY,
			systemLDAPConfiguration.factoryInitial());
		environmentProperties.put(Context.PROVIDER_URL, providerURL);
		environmentProperties.put(Context.SECURITY_CREDENTIALS, credentials);
		environmentProperties.put(Context.SECURITY_PRINCIPAL, principal);

		String[] connectionProperties =
			systemLDAPConfiguration.connectionProperties();

		for (String connectionPropertyString : connectionProperties) {
			String[] connectionProperty = StringUtil.split(
				connectionPropertyString, CharPool.EQUAL);

			if (connectionProperty.length != 2) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Invalid LDAP connection property: " +
							connectionPropertyString);

					continue;
				}
			}

			environmentProperties.put(
				connectionProperty[0], connectionProperty[1]);
		}

		SafeLDAPReferralUtil.setProperties(
			environmentProperties, systemLDAPConfiguration.referral());

		if (_log.isDebugEnabled()) {
			_log.debug(
				MapUtil.toString(
					environmentProperties, null, Context.SECURITY_CREDENTIALS));
		}

		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				SafeLdapContextImpl.class.getClassLoader())) {

			return new SafeLdapContextImpl(
				new InitialLdapContext(environmentProperties, null),
				Objects.equals(
					systemLDAPConfiguration.referral(),
					LDAPConstants.REFERRAL_FOLLOW));
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to bind to the LDAP server", exception);
			}

			return null;
		}
	}

	@Override
	public Binding getUser(
			long ldapServerId, long companyId, String screenName,
			String emailAddress)
		throws Exception {

		return getUser(
			ldapServerId, companyId, screenName, emailAddress, false, true);
	}

	@Override
	public Binding getUser(
			long ldapServerId, long companyId, String screenName,
			String emailAddress, boolean checkOriginalEmailAddress,
			boolean useUserSearchSafeLdapFilter)
		throws Exception {

		SafeLdapContext safeLdapContext = getSafeLdapContext(
			ldapServerId, companyId);

		NamingEnumeration<SearchResult> enumeration = null;

		try {
			if (safeLdapContext == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"No LDAP server configuration available for LDAP ",
							"server ", ldapServerId, " and company ",
							companyId));
				}

				return null;
			}

			String authType = PrefsPropsUtil.getString(
				companyId, PropsKeys.COMPANY_SECURITY_AUTH_TYPE,
				_companySecurityAuthType);
			Properties userMappings = _ldapSettings.getUserMappings(
				ldapServerId, companyId);

			String login = null;
			String loginMapping = null;

			if (authType.equals(CompanyConstants.AUTH_TYPE_SN) &&
				!PrefsPropsUtil.getBoolean(
					companyId,
					PropsKeys.USERS_SCREEN_NAME_ALWAYS_AUTOGENERATE)) {

				login = screenName;
				loginMapping = userMappings.getProperty("screenName");
			}
			else {
				login = emailAddress;
				loginMapping = userMappings.getProperty("emailAddress");
			}

			LDAPServerConfiguration ldapServerConfiguration =
				_ldapServerConfigurationProvider.getConfiguration(
					companyId, ldapServerId);

			if (ldapServerConfiguration.ldapServerId() != ldapServerId) {
				return null;
			}

			SafeLdapFilter safeLdapFilter = SafeLdapFilterConstraints.eq(
				loginMapping, login);

			if (useUserSearchSafeLdapFilter) {
				SafeLdapFilter userSearchSafeLdapFilter =
					LDAPUtil.getUserSearchSafeLdapFilter(
						ldapServerConfiguration, _ldapFilterValidator);

				if (userSearchSafeLdapFilter != null) {
					safeLdapFilter = safeLdapFilter.and(
						userSearchSafeLdapFilter);
				}
			}

			SearchControls searchControls = new SearchControls(
				SearchControls.SUBTREE_SCOPE, 1, 0, null, false, false);

			enumeration = safeLdapContext.search(
				LDAPUtil.getBaseDNSafeLdapName(ldapServerConfiguration),
				safeLdapFilter, searchControls);

			if (enumeration.hasMoreElements()) {
				return enumeration.nextElement();
			}

			if (checkOriginalEmailAddress) {
				String originalEmailAddress =
					UserImportTransactionThreadLocal.getOriginalEmailAddress();

				if (Validator.isNotNull(originalEmailAddress) &&
					!originalEmailAddress.equals(emailAddress)) {

					return getUser(
						ldapServerId, companyId, screenName,
						originalEmailAddress, false, true);
				}
			}

			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Unable to retrieve user with LDAP server ",
						ldapServerId, ", company ", companyId,
						", loginMapping ", loginMapping, ", and login ",
						login));
			}

			return null;
		}
		finally {
			if (enumeration != null) {
				enumeration.close();
			}

			if (safeLdapContext != null) {
				safeLdapContext.close();
			}
		}
	}

	@Override
	public Attributes getUserAttributes(
			long ldapServerId, long companyId, SafeLdapContext safeLdapContext,
			SafeLdapName userSafeLdapName)
		throws Exception {

		Properties userMappings = _ldapSettings.getUserMappings(
			ldapServerId, companyId);

		PropertiesUtil.merge(
			userMappings,
			_ldapSettings.getUserExpandoMappings(ldapServerId, companyId));

		Properties contactMappings = _ldapSettings.getContactMappings(
			ldapServerId, companyId);

		PropertiesUtil.merge(
			contactMappings,
			_ldapSettings.getContactExpandoMappings(ldapServerId, companyId));

		PropertiesUtil.merge(userMappings, contactMappings);

		Collection<Object> values = userMappings.values();

		values.removeIf(Validator::isNull);

		String[] mappedUserAttributeIds = ArrayUtil.toStringArray(
			values.toArray(new Object[userMappings.size()]));

		Attributes attributes = _getAttributes(
			safeLdapContext, userSafeLdapName, mappedUserAttributeIds);

		if (_log.isDebugEnabled()) {
			if ((attributes == null) || (attributes.size() == 0)) {
				_log.debug(
					"No LDAP user attributes found for: " + userSafeLdapName);
			}
			else {
				for (String attributeId : mappedUserAttributeIds) {
					Attribute attribute = attributes.get(attributeId);

					if (attribute == null) {
						continue;
					}

					String attributeID = StringUtil.toLowerCase(
						attribute.getID());

					if (attributeID.indexOf("password") > -1) {
						Attribute clonedAttribute =
							(Attribute)attribute.clone();

						clonedAttribute.clear();

						clonedAttribute.add("********");

						_log.debug(
							"LDAP user attribute " +
								clonedAttribute.toString());

						continue;
					}

					_log.debug("LDAP user attribute " + attribute.toString());
				}
			}
		}

		return attributes;
	}

	@Override
	public byte[] getUsers(
			long ldapServerId, long companyId, SafeLdapContext safeLdapContext,
			byte[] cookie, int maxResults, List<SearchResult> searchResults)
		throws Exception {

		LDAPServerConfiguration ldapServerConfiguration =
			_ldapServerConfigurationProvider.getConfiguration(
				companyId, ldapServerId);

		if (ldapServerConfiguration.ldapServerId() != ldapServerId) {
			return null;
		}

		return getUsers(
			companyId, safeLdapContext, cookie, maxResults,
			LDAPUtil.getBaseDNSafeLdapName(ldapServerConfiguration),
			LDAPUtil.getUserSearchSafeLdapFilter(
				ldapServerConfiguration, _ldapFilterValidator),
			searchResults);
	}

	@Override
	public byte[] getUsers(
			long ldapServerId, long companyId, SafeLdapContext safeLdapContext,
			byte[] cookie, int maxResults, String[] attributeIds,
			List<SearchResult> searchResults)
		throws Exception {

		LDAPServerConfiguration ldapServerConfiguration =
			_ldapServerConfigurationProvider.getConfiguration(
				companyId, ldapServerId);

		if (ldapServerConfiguration.ldapServerId() != ldapServerId) {
			return null;
		}

		return getUsers(
			companyId, safeLdapContext, cookie, maxResults,
			LDAPUtil.getBaseDNSafeLdapName(ldapServerConfiguration),
			LDAPUtil.getUserSearchSafeLdapFilter(
				ldapServerConfiguration, _ldapFilterValidator),
			attributeIds, searchResults);
	}

	@Override
	public byte[] getUsers(
			long companyId, SafeLdapContext safeLdapContext, byte[] cookie,
			int maxResults, SafeLdapName baseDNSafeLdapName,
			SafeLdapFilter userLDAPFilter, List<SearchResult> searchResults)
		throws Exception {

		return searchLDAP(
			companyId, safeLdapContext, cookie, maxResults, baseDNSafeLdapName,
			userLDAPFilter, null, searchResults);
	}

	@Override
	public byte[] getUsers(
			long companyId, SafeLdapContext safeLdapContext, byte[] cookie,
			int maxResults, SafeLdapName baseDNSafeLdapName,
			SafeLdapFilter userLDAPFilter, String[] attributeIds,
			List<SearchResult> searchResults)
		throws Exception {

		return searchLDAP(
			companyId, safeLdapContext, cookie, maxResults, baseDNSafeLdapName,
			userLDAPFilter, attributeIds, searchResults);
	}

	@Override
	public SafeLdapName getUsersDNSafeLdapName(
			long ldapServerId, long companyId)
		throws Exception {

		LDAPServerConfiguration ldapServerConfiguration =
			_ldapServerConfigurationProvider.getConfiguration(
				companyId, ldapServerId);

		if (ldapServerConfiguration.ldapServerId() != ldapServerId) {
			return null;
		}

		return LDAPUtil.getUsersDNSafeLdapName(ldapServerConfiguration);
	}

	@Override
	public boolean hasUser(
			long ldapServerId, long companyId, String screenName,
			String emailAddress)
		throws Exception {

		if (getUser(ldapServerId, companyId, screenName, emailAddress) !=
				null) {

			return true;
		}

		return false;
	}

	@Override
	public boolean isGroupMember(
			long ldapServerId, long companyId, SafeLdapName groupSafeLdapName,
			SafeLdapName userSafeLdapName)
		throws Exception {

		SafeLdapContext safeLdapContext = getSafeLdapContext(
			ldapServerId, companyId);

		NamingEnumeration<SearchResult> enumeration = null;

		try {
			if (safeLdapContext == null) {
				return false;
			}

			Properties groupMappings = _ldapSettings.getGroupMappings(
				ldapServerId, companyId);

			SafeLdapFilter safeLdapFilter = SafeLdapFilterConstraints.eq(
				groupMappings.getProperty("user"), userSafeLdapName);

			SearchControls searchControls = new SearchControls(
				SearchControls.SUBTREE_SCOPE, 1, 0, null, false, false);

			enumeration = safeLdapContext.search(
				groupSafeLdapName, safeLdapFilter, searchControls);

			if (enumeration.hasMoreElements()) {
				return true;
			}
		}
		catch (NameNotFoundException nameNotFoundException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Unable to determine if user DN ", userSafeLdapName,
						" is a member of group DN ", groupSafeLdapName),
					nameNotFoundException);
			}
		}
		finally {
			if (enumeration != null) {
				enumeration.close();
			}

			if (safeLdapContext != null) {
				safeLdapContext.close();
			}
		}

		return false;
	}

	@Override
	public boolean isUserGroupMember(
			long ldapServerId, long companyId, SafeLdapName groupSafeLdapName,
			SafeLdapName userSafeLdapName)
		throws Exception {

		SafeLdapContext safeLdapContext = getSafeLdapContext(
			ldapServerId, companyId);

		NamingEnumeration<SearchResult> enumeration = null;

		try {
			if (safeLdapContext == null) {
				return false;
			}

			Properties userMappings = _ldapSettings.getUserMappings(
				ldapServerId, companyId);

			SafeLdapFilter safeLdapFilter = SafeLdapFilterConstraints.eq(
				userMappings.getProperty(UserConverterKeys.GROUP),
				groupSafeLdapName);

			SearchControls searchControls = new SearchControls(
				SearchControls.SUBTREE_SCOPE, 1, 0, null, false, false);

			enumeration = safeLdapContext.search(
				userSafeLdapName, safeLdapFilter, searchControls);

			if (enumeration.hasMoreElements()) {
				return true;
			}
		}
		catch (NameNotFoundException nameNotFoundException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Unable to determine if group DN ", groupSafeLdapName,
						" is a member of user DN ", userSafeLdapName),
					nameNotFoundException);
			}
		}
		finally {
			if (enumeration != null) {
				enumeration.close();
			}

			if (safeLdapContext != null) {
				safeLdapContext.close();
			}
		}

		return false;
	}

	@Override
	public byte[] searchLDAP(
			long companyId, SafeLdapContext safeLdapContext, byte[] cookie,
			int maxResults, SafeLdapName baseDNSafeLdapName,
			SafeLdapFilter safeLdapFilter, String[] attributeIds,
			List<SearchResult> searchResults)
		throws Exception {

		SearchControls searchControls = new SearchControls(
			SearchControls.SUBTREE_SCOPE, maxResults, 0, attributeIds, false,
			false);

		NamingEnumeration<SearchResult> enumeration = null;

		try {
			if (cookie != null) {
				SystemLDAPConfiguration systemLDAPConfiguration =
					_systemLDAPConfigurationProvider.getConfiguration(
						companyId);

				if (cookie.length == 0) {
					safeLdapContext.setRequestControls(
						new Control[] {
							new PagedResultsControl(
								systemLDAPConfiguration.pageSize(),
								Control.CRITICAL)
						});
				}
				else {
					safeLdapContext.setRequestControls(
						new Control[] {
							new PagedResultsControl(
								systemLDAPConfiguration.pageSize(), cookie,
								Control.CRITICAL)
						});
				}

				enumeration = safeLdapContext.search(
					baseDNSafeLdapName, safeLdapFilter, searchControls);

				while (enumeration.hasMoreElements()) {
					searchResults.add(enumeration.nextElement());
				}

				return _getCookie(safeLdapContext.getResponseControls());
			}
		}
		catch (OperationNotSupportedException operationNotSupportedException) {
			if (_log.isDebugEnabled()) {
				_log.debug(operationNotSupportedException);
			}

			if (enumeration != null) {
				enumeration.close();
			}

			safeLdapContext.setRequestControls(null);

			enumeration = safeLdapContext.search(
				baseDNSafeLdapName, safeLdapFilter, searchControls);

			while (enumeration.hasMoreElements()) {
				searchResults.add(enumeration.nextElement());
			}
		}
		finally {
			if (enumeration != null) {
				enumeration.close();
			}

			safeLdapContext.setRequestControls(null);
		}

		return null;
	}

	@Activate
	protected void activate() {
		_companySecurityAuthType = GetterUtil.getString(
			PropsUtil.get(PropsKeys.COMPANY_SECURITY_AUTH_TYPE));
	}

	private Attributes _getAttributes(
			LdapContext ldapContext, SafeLdapName fullDNSafeLdapName,
			String[] attributeIds)
		throws Exception {

		Attributes attributes = null;

		String[] auditAttributeIds = {
			"creatorsName", "createTimestamp", "modifiersName",
			"modifyTimestamp"
		};

		if (attributeIds == null) {

			// Get complete listing of LDAP attributes (slow)

			attributes = ldapContext.getAttributes(fullDNSafeLdapName);

			NamingEnumeration<? extends Attribute> enumeration = null;

			try {
				Attributes auditAttributes = ldapContext.getAttributes(
					fullDNSafeLdapName, auditAttributeIds);

				enumeration = auditAttributes.getAll();

				while (enumeration.hasMoreElements()) {
					attributes.put(enumeration.nextElement());
				}
			}
			finally {
				if (enumeration != null) {
					enumeration.close();
				}
			}
		}
		else {

			// Get specified LDAP attributes

			int attributeCount = attributeIds.length + auditAttributeIds.length;

			String[] allAttributeIds = new String[attributeCount];

			System.arraycopy(
				attributeIds, 0, allAttributeIds, 0, attributeIds.length);
			System.arraycopy(
				auditAttributeIds, 0, allAttributeIds, attributeIds.length,
				auditAttributeIds.length);

			attributes = ldapContext.getAttributes(
				fullDNSafeLdapName, allAttributeIds);
		}

		return attributes;
	}

	private byte[] _getCookie(Control[] controls) {
		if (controls == null) {
			return null;
		}

		for (Control control : controls) {
			if (control instanceof PagedResultsResponseControl) {
				PagedResultsResponseControl pagedResultsResponseControl =
					(PagedResultsResponseControl)control;

				return pagedResultsResponseControl.getCookie();
			}
		}

		return null;
	}

	private String _getNextRange(
		SystemLDAPConfiguration systemLDAPConfiguration, String attributeId) {

		String originalAttributeId = null;
		int start = 0;
		int end = 0;

		int x = attributeId.indexOf(CharPool.SEMICOLON);

		if (x < 0) {
			originalAttributeId = attributeId;
			end = systemLDAPConfiguration.rangeSize() - 1;
		}
		else {
			int y = attributeId.indexOf(CharPool.EQUAL, x);

			int z = attributeId.indexOf(CharPool.DASH, y);

			originalAttributeId = attributeId.substring(0, x);

			start = GetterUtil.getInteger(attributeId.substring(y + 1, z));
			end = GetterUtil.getInteger(attributeId.substring(z + 1));

			start += systemLDAPConfiguration.rangeSize();
			end += systemLDAPConfiguration.rangeSize();
		}

		return StringBundler.concat(
			originalAttributeId, ";range=", start, StringPool.DASH, end);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SafePortalLDAPImpl.class);

	private String _companySecurityAuthType;

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	private volatile LDAPFilterValidator _ldapFilterValidator;

	@Reference(
		target = "(factoryPid=com.liferay.portal.security.ldap.configuration.LDAPServerConfiguration)"
	)
	private ConfigurationProvider<LDAPServerConfiguration>
		_ldapServerConfigurationProvider;

	@Reference
	private LDAPSettings _ldapSettings;

	@Reference(
		target = "(factoryPid=com.liferay.portal.security.ldap.configuration.SystemLDAPConfiguration)"
	)
	private ConfigurationProvider<SystemLDAPConfiguration>
		_systemLDAPConfigurationProvider;

}