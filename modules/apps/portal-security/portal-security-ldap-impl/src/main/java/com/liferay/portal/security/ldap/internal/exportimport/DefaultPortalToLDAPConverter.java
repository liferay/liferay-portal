/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.ldap.internal.exportimport;

import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.expando.util.ExpandoConverterUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanProperties;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.PwdEncryptorException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.Image;
import com.liferay.portal.kernel.model.ListType;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.security.auth.PasswordModificationThreadLocal;
import com.liferay.portal.kernel.security.fips.FIPSModeUtil;
import com.liferay.portal.kernel.security.ldap.LDAPSettings;
import com.liferay.portal.kernel.security.pwd.PasswordEncryptor;
import com.liferay.portal.kernel.security.pwd.PasswordEncryptorUtil;
import com.liferay.portal.kernel.service.ImageLocalService;
import com.liferay.portal.kernel.service.ListTypeService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.exportimport.UserOperation;
import com.liferay.portal.security.ldap.ContactConverterKeys;
import com.liferay.portal.security.ldap.GroupConverterKeys;
import com.liferay.portal.security.ldap.SafeLdapName;
import com.liferay.portal.security.ldap.SafeLdapNameFactory;
import com.liferay.portal.security.ldap.SafePortalLDAP;
import com.liferay.portal.security.ldap.UserConverterKeys;
import com.liferay.portal.security.ldap.authenticator.configuration.LDAPAuthConfiguration;
import com.liferay.portal.security.ldap.configuration.ConfigurationProvider;
import com.liferay.portal.security.ldap.configuration.LDAPServerConfiguration;
import com.liferay.portal.security.ldap.exportimport.Modifications;
import com.liferay.portal.security.ldap.exportimport.PortalToLDAPConverter;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.Binding;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.ldap.Rdn;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Michael C. Han
 * @author Brian Wing Shun Chan
 * @author Marcellus Tavares
 * @author Wesley Gong
 */
@Component(service = PortalToLDAPConverter.class)
public class DefaultPortalToLDAPConverter implements PortalToLDAPConverter {

	public DefaultPortalToLDAPConverter() {
		_reservedUserFieldNames.put(
			UserConverterKeys.GROUP, UserConverterKeys.GROUP);
		_reservedUserFieldNames.put(
			UserConverterKeys.PASSWORD, UserConverterKeys.PASSWORD);
		_reservedUserFieldNames.put(
			UserConverterKeys.PORTRAIT, UserConverterKeys.PORTRAIT);
		_reservedUserFieldNames.put(
			UserConverterKeys.SCREEN_NAME, UserConverterKeys.SCREEN_NAME);
	}

	/**
	 * @deprecated As of Mueller (7.2.x), replaced by {@link
	 *             PortalToLDAPConverter#getGroupSafeLdapName(long, UserGroup,
	 *             Properties)}
	 */
	@Deprecated
	@Override
	public String getGroupDNName(
			long ldapServerId, UserGroup userGroup, Properties groupMappings)
		throws Exception {

		Binding groupBinding = _safePortalLDAP.getGroup(
			ldapServerId, userGroup.getCompanyId(), userGroup.getName());

		if (groupBinding != null) {
			return groupBinding.getNameInNamespace();
		}

		return StringBundler.concat(
			GetterUtil.getString(
				groupMappings.getProperty(GroupConverterKeys.GROUP_NAME),
				_DEFAULT_DN),
			StringPool.EQUAL, Rdn.escapeValue(userGroup.getName()),
			StringPool.COMMA,
			_safePortalLDAP.getGroupsDNSafeLdapName(
				ldapServerId, userGroup.getCompanyId()));
	}

	@Override
	public SafeLdapName getGroupSafeLdapName(
			long ldapServerId, UserGroup userGroup, Properties groupMappings)
		throws Exception {

		Binding groupBinding = _safePortalLDAP.getGroup(
			ldapServerId, userGroup.getCompanyId(), userGroup.getName());

		if (groupBinding != null) {
			return SafeLdapNameFactory.from(groupBinding);
		}

		String rdnType = GetterUtil.getString(
			groupMappings.getProperty(GroupConverterKeys.GROUP_NAME),
			_DEFAULT_DN);

		return SafeLdapNameFactory.from(
			rdnType, userGroup.getName(),
			_safePortalLDAP.getGroupsDNSafeLdapName(
				ldapServerId, userGroup.getCompanyId()));
	}

	@Override
	public Modifications getLDAPContactModifications(
			Contact contact, Map<String, Serializable> contactExpandoAttributes,
			Properties contactMappings, Properties contactExpandoMappings)
		throws Exception {

		if (contactMappings.isEmpty() && contactExpandoMappings.isEmpty()) {
			return null;
		}

		if (contactExpandoMappings.containsKey(ContactConverterKeys.PREFIX)) {
			String prefix = contactExpandoMappings.getProperty(
				ContactConverterKeys.PREFIX);

			contactMappings.put(ContactConverterKeys.PREFIX, prefix);
		}

		if (contactExpandoMappings.containsKey(ContactConverterKeys.SUFFIX)) {
			String suffix = contactExpandoMappings.getProperty(
				ContactConverterKeys.SUFFIX);

			contactMappings.put(ContactConverterKeys.SUFFIX, suffix);
		}

		Modifications modifications = _getModifications(
			contact, contactMappings, _reservedContactFieldNames);

		_populateCustomAttributeModifications(
			contact, contact.getExpandoBridge(), contactExpandoAttributes,
			contactExpandoMappings, modifications);

		return modifications;
	}

	@Override
	public Attributes getLDAPGroupAttributes(
			long ldapServerId, UserGroup userGroup, User user,
			Properties groupMappings, Properties userMappings)
		throws Exception {

		LDAPServerConfiguration ldapServerConfiguration =
			_ldapServerConfigurationProvider.getConfiguration(
				userGroup.getCompanyId(), ldapServerId);

		if (ldapServerConfiguration.ldapServerId() != ldapServerId) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"LDAP server ", ldapServerId,
						" is invalid because company ",
						userGroup.getCompanyId(), " uses ",
						ldapServerConfiguration.ldapServerId()));
			}

			return null;
		}

		Attributes attributes = new BasicAttributes(true);

		Attribute objectClassAttribute = new BasicAttribute(_OBJECT_CLASS);

		String[] defaultObjectClassNames =
			ldapServerConfiguration.groupDefaultObjectClasses();

		for (String defaultObjectClassName : defaultObjectClassNames) {
			objectClassAttribute.add(defaultObjectClassName);
		}

		attributes.put(objectClassAttribute);

		_addAttributeMapping(
			groupMappings.getProperty(GroupConverterKeys.GROUP_NAME),
			userGroup.getName(), attributes);
		_addAttributeMapping(
			groupMappings.getProperty(GroupConverterKeys.DESCRIPTION),
			userGroup.getDescription(), attributes);
		_addAttributeMapping(
			groupMappings.getProperty(GroupConverterKeys.USER),
			getUserDNName(ldapServerId, user, userMappings), attributes);

		return attributes;
	}

	@Override
	public Modifications getLDAPGroupModifications(
			long ldapServerId, UserGroup userGroup, User user,
			Properties groupMappings, Properties userMappings,
			UserOperation userOperation)
		throws Exception {

		Modifications modifications = _getModifications(
			userGroup, groupMappings, new HashMap<String, String>());

		SafeLdapName userGroupSafeLdapName = getGroupSafeLdapName(
			ldapServerId, userGroup, groupMappings);

		SafeLdapName userSafeLdapName = getUserSafeLdapName(
			ldapServerId, user, userMappings);

		if (_safePortalLDAP.isGroupMember(
				ldapServerId, user.getCompanyId(), userGroupSafeLdapName,
				userSafeLdapName)) {

			if (userOperation == UserOperation.REMOVE) {
				modifications.addItem(
					DirContext.REMOVE_ATTRIBUTE,
					groupMappings.getProperty(GroupConverterKeys.USER),
					userSafeLdapName);
			}
		}
		else {
			if (userOperation == UserOperation.ADD) {
				modifications.addItem(
					DirContext.ADD_ATTRIBUTE,
					groupMappings.getProperty(GroupConverterKeys.USER),
					userSafeLdapName);
			}
		}

		return modifications;
	}

	@Override
	public Attributes getLDAPUserAttributes(
		long ldapServerId, User user, Properties userMappings) {

		LDAPServerConfiguration ldapServerConfiguration =
			_ldapServerConfigurationProvider.getConfiguration(
				user.getCompanyId(), ldapServerId);

		if (ldapServerConfiguration.ldapServerId() != ldapServerId) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"LDAP server ", ldapServerId,
						" is invalid because company ", user.getCompanyId(),
						" uses ", ldapServerConfiguration.ldapServerId()));
			}

			return null;
		}

		Attributes attributes = new BasicAttributes(true);

		Attribute objectClassAttribute = new BasicAttribute(_OBJECT_CLASS);

		String[] defaultObjectClassNames =
			ldapServerConfiguration.userDefaultObjectClasses();

		for (String defaultObjectClassName : defaultObjectClassNames) {
			objectClassAttribute.add(defaultObjectClassName);
		}

		attributes.put(objectClassAttribute);

		_addAttributeMapping(
			userMappings.getProperty(UserConverterKeys.UUID), user.getUuid(),
			attributes);
		_addAttributeMapping(
			userMappings.getProperty(UserConverterKeys.SCREEN_NAME),
			user.getScreenName(), attributes);
		_addAttributeMapping(
			userMappings.getProperty(UserConverterKeys.PASSWORD),
			_getEncryptedPasswordForLDAP(user, userMappings), attributes);
		_addAttributeMapping(
			userMappings.getProperty(UserConverterKeys.EMAIL_ADDRESS),
			user.getEmailAddress(), attributes);
		_addAttributeMapping(
			userMappings.getProperty(UserConverterKeys.FULL_NAME),
			user.getFullName(), attributes);
		_addAttributeMapping(
			userMappings.getProperty(UserConverterKeys.FIRST_NAME),
			user.getFirstName(), attributes);
		_addAttributeMapping(
			userMappings.getProperty(UserConverterKeys.MIDDLE_NAME),
			user.getMiddleName(), attributes);
		_addAttributeMapping(
			userMappings.getProperty(UserConverterKeys.LAST_NAME),
			user.getLastName(), attributes);
		_addAttributeMapping(
			userMappings.getProperty(UserConverterKeys.JOB_TITLE),
			user.getJobTitle(), attributes);
		_addAttributeMapping(
			userMappings.getProperty(UserConverterKeys.PORTRAIT),
			_getUserPortrait(user), attributes);
		_addAttributeMapping(
			userMappings.getProperty(UserConverterKeys.STATUS),
			String.valueOf(user.getStatus()), attributes);

		return attributes;
	}

	@Override
	public Modifications getLDAPUserGroupModifications(
			long ldapServerId, List<UserGroup> userGroups, User user,
			Properties userMappings)
		throws Exception {

		Modifications modifications = Modifications.getInstance();

		String groupMappingAttributeName = userMappings.getProperty(
			UserConverterKeys.GROUP);

		if (Validator.isNull(groupMappingAttributeName)) {
			return modifications;
		}

		Properties groupMappings = _ldapSettings.getGroupMappings(
			ldapServerId, user.getCompanyId());

		SafeLdapName userSafeLdapName = getUserSafeLdapName(
			ldapServerId, user, userMappings);

		for (UserGroup userGroup : userGroups) {
			SafeLdapName userGroupSafeLdapName = getGroupSafeLdapName(
				ldapServerId, userGroup, groupMappings);

			if (_safePortalLDAP.isUserGroupMember(
					ldapServerId, user.getCompanyId(), userGroupSafeLdapName,
					userSafeLdapName)) {

				continue;
			}

			modifications.addItem(
				DirContext.ADD_ATTRIBUTE, groupMappingAttributeName,
				userGroupSafeLdapName);
		}

		return modifications;
	}

	@Override
	public Modifications getLDAPUserModifications(
			User user, Map<String, Serializable> userExpandoAttributes,
			Properties userMappings, Properties userExpandoMappings)
		throws Exception {

		Modifications modifications = _getModifications(
			user, userMappings, _reservedUserFieldNames);

		if (PasswordModificationThreadLocal.isPasswordModified() &&
			Validator.isNotNull(
				PasswordModificationThreadLocal.getPasswordUnencrypted())) {

			String newPassword = _getEncryptedPasswordForLDAP(
				user, userMappings);

			String passwordKey = userMappings.getProperty(
				UserConverterKeys.PASSWORD);

			_addModificationItem(passwordKey, newPassword, modifications);
		}

		String portraitKey = userMappings.getProperty(
			UserConverterKeys.PORTRAIT);

		if (Validator.isNotNull(portraitKey)) {
			_addModificationItem(
				new BasicAttribute(portraitKey, _getUserPortrait(user)),
				modifications);
		}

		_populateCustomAttributeModifications(
			user, user.getExpandoBridge(), userExpandoAttributes,
			userExpandoMappings, modifications);

		return modifications;
	}

	/**
	 * @deprecated As of Mueller (7.2.x), replaced by {@link
	 *             PortalToLDAPConverter#getUserSafeLdapName(long, User,
	 *             Properties)}
	 */
	@Deprecated
	@Override
	public String getUserDNName(
			long ldapServerId, User user, Properties userMappings)
		throws Exception {

		Binding userBinding = _safePortalLDAP.getUser(
			ldapServerId, user.getCompanyId(), user.getScreenName(),
			user.getEmailAddress());

		if (userBinding != null) {
			return userBinding.getNameInNamespace();
		}

		return StringBundler.concat(
			GetterUtil.getString(
				userMappings.getProperty(_userDNFieldName), _DEFAULT_DN),
			StringPool.EQUAL,
			_beanProperties.getStringSilent(user, _userDNFieldName),
			StringPool.COMMA,
			_safePortalLDAP.getUsersDNSafeLdapName(
				ldapServerId, user.getCompanyId()));
	}

	@Override
	public SafeLdapName getUserSafeLdapName(
			long ldapServerId, User user, Properties userMappings)
		throws Exception {

		Binding userBinding = _safePortalLDAP.getUser(
			ldapServerId, user.getCompanyId(), user.getScreenName(),
			user.getEmailAddress());

		if (userBinding != null) {
			return SafeLdapNameFactory.from(userBinding);
		}

		String rdnType = GetterUtil.getString(
			userMappings.getProperty(_userDNFieldName), _DEFAULT_DN);
		String rdnValue = _beanProperties.getStringSilent(
			user, _userDNFieldName);

		return SafeLdapNameFactory.from(
			rdnType, rdnValue,
			_safePortalLDAP.getUsersDNSafeLdapName(
				ldapServerId, user.getCompanyId()));
	}

	public void setContactReservedFieldNames(
		List<String> reservedContactFieldNames) {

		for (String reservedContactFieldName : reservedContactFieldNames) {
			_reservedContactFieldNames.put(
				reservedContactFieldName, reservedContactFieldName);
		}
	}

	public void setUserDNFieldName(String userDNFieldName) {
		_userDNFieldName = userDNFieldName;
	}

	public void setUserReservedFieldNames(List<String> reservedUserFieldNames) {
		for (String reservedUserFieldName : reservedUserFieldNames) {
			_reservedUserFieldNames.put(
				reservedUserFieldName, reservedUserFieldName);
		}
	}

	private void _addAttributeMapping(
		String attributeName, Object attributeValue, Attributes attributes) {

		if (Validator.isNotNull(attributeName) && (attributeValue != null)) {
			attributes.put(attributeName, attributeValue);
		}
	}

	private void _addAttributeMapping(
		String attributeName, String attributeValue, Attributes attributes) {

		if (Validator.isNotNull(attributeName) &&
			Validator.isNotNull(attributeValue)) {

			attributes.put(attributeName, attributeValue);
		}
	}

	private void _addModificationItem(
		BasicAttribute basicAttribute, Modifications modifications) {

		if (basicAttribute != null) {
			modifications.addItem(basicAttribute);
		}
	}

	private void _addModificationItem(
		String attributeName, String attributeValue,
		Modifications modifications) {

		if (Validator.isNotNull(attributeName)) {
			modifications.addItem(attributeName, attributeValue);
		}
	}

	private Object _getAttributeValue(Object object, String fieldName)
		throws PortalException {

		boolean listTypeFieldName = false;

		if (fieldName.equals(ContactConverterKeys.PREFIX)) {
			fieldName = "prefixListTypeId";
			listTypeFieldName = true;
		}
		else if (fieldName.equals(ContactConverterKeys.SUFFIX)) {
			fieldName = "suffixListTypeId";
			listTypeFieldName = true;
		}

		Object attributeValue = _beanProperties.getObjectSilent(
			object, fieldName);

		if ((attributeValue != null) && listTypeFieldName) {
			ListType listType = _listTypeService.getListType(
				(Long)attributeValue);

			attributeValue = listType.getName();
		}

		return attributeValue;
	}

	private String _getEncryptedPasswordForLDAP(
		User user, Properties userMappings) {

		String password =
			PasswordModificationThreadLocal.getPasswordUnencrypted();

		if (Validator.isNull(password)) {
			return password;
		}

		LDAPAuthConfiguration ldapAuthConfiguration =
			_ldapAuthConfigurationProvider.getConfiguration(
				user.getCompanyId());

		String algorithm = ldapAuthConfiguration.passwordEncryptionAlgorithm();

		if (Validator.isNotNull(algorithm) &&
			!algorithm.equals(PasswordEncryptor.TYPE_NONE)) {

			if (FIPSModeUtil.isEnabled() &&
				!FIPSModeUtil.isApprovedPasswordAlgorithm(algorithm)) {

				throw new SystemException(
					StringBundler.concat(
						"FIPS mode does not permit LDAP password encryption ",
						"algorithm \"", algorithm, "\" for export"));
			}

			try {
				password = PasswordEncryptorUtil.encrypt(
					algorithm, password, null);
			}
			catch (PwdEncryptorException pwdEncryptorException) {
				throw new SystemException(pwdEncryptorException);
			}
		}

		String passwordKey = userMappings.getProperty(
			UserConverterKeys.PASSWORD);

		if (passwordKey.equals("unicodePwd")) {
			String quotedPassword = StringBundler.concat(
				StringPool.QUOTE, password, StringPool.QUOTE);

			try {
				byte[] unicodePassword = quotedPassword.getBytes("UTF-16LE");

				return new String(unicodePassword);
			}
			catch (UnsupportedEncodingException unsupportedEncodingException) {
				throw new SystemException(unsupportedEncodingException);
			}
		}

		return password;
	}

	private Modifications _getModifications(
		Object object, Properties objectMappings,
		Map<String, String> reservedFieldNames) {

		Modifications modifications = Modifications.getInstance();

		for (Map.Entry<Object, Object> entry : objectMappings.entrySet()) {
			String fieldName = (String)entry.getKey();

			if (reservedFieldNames.containsKey(fieldName)) {
				continue;
			}

			String ldapAttributeName = (String)entry.getValue();

			try {
				Object attributeValue = _getAttributeValue(object, fieldName);

				if (attributeValue != null) {
					_addModificationItem(
						ldapAttributeName, attributeValue.toString(),
						modifications);
				}
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"Unable to map field ", fieldName, " to class ",
							object.getClass()),
						exception);
				}
			}
		}

		return modifications;
	}

	private byte[] _getUserPortrait(User user) {
		byte[] bytes = null;

		if (user.getPortraitId() == 0) {
			return bytes;
		}

		Image image = null;

		try {
			image = _imageLocalService.getImage(user.getPortraitId());

			if (image != null) {
				bytes = image.getTextObj();
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get the portrait for user " + user.getUserId(),
					exception);
			}
		}

		return bytes;
	}

	private void _populateCustomAttributeModifications(
		Object object, ExpandoBridge expandoBridge,
		Map<String, Serializable> expandoAttributes, Properties expandoMappings,
		Modifications modifications) {

		if ((expandoAttributes == null) || expandoAttributes.isEmpty()) {
			return;
		}

		for (Map.Entry<Object, Object> entry : expandoMappings.entrySet()) {
			String fieldName = (String)entry.getKey();

			Serializable fieldValue = expandoAttributes.get(fieldName);

			if (fieldValue == null) {
				continue;
			}

			String ldapAttributeName = (String)entry.getValue();

			try {
				int type = expandoBridge.getAttributeType(fieldName);

				String value = ExpandoConverterUtil.getStringFromAttribute(
					type, fieldValue);

				_addModificationItem(ldapAttributeName, value, modifications);
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"Unable to map field ", fieldName, " to class ",
							object.getClass()),
						exception);
				}
			}
		}
	}

	private static final String _DEFAULT_DN = "cn";

	private static final String _OBJECT_CLASS = "objectclass";

	private static final Log _log = LogFactoryUtil.getLog(
		DefaultPortalToLDAPConverter.class);

	@Reference
	private BeanProperties _beanProperties;

	@Reference
	private ImageLocalService _imageLocalService;

	@Reference(
		target = "(factoryPid=com.liferay.portal.security.ldap.authenticator.configuration.LDAPAuthConfiguration)"
	)
	private ConfigurationProvider<LDAPAuthConfiguration>
		_ldapAuthConfigurationProvider;

	@Reference(
		target = "(factoryPid=com.liferay.portal.security.ldap.configuration.LDAPServerConfiguration)"
	)
	private ConfigurationProvider<LDAPServerConfiguration>
		_ldapServerConfigurationProvider;

	@Reference
	private LDAPSettings _ldapSettings;

	@Reference
	private ListTypeService _listTypeService;

	private final Map<String, String> _reservedContactFieldNames =
		new HashMap<>();
	private final Map<String, String> _reservedUserFieldNames = new HashMap<>();

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	private volatile SafePortalLDAP _safePortalLDAP;

	private String _userDNFieldName = UserConverterKeys.SCREEN_NAME;

}