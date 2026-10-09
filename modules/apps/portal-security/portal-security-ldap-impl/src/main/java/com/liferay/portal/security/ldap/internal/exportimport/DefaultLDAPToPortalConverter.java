/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.ldap.internal.exportimport;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.UserEmailAddressException;
import com.liferay.portal.kernel.exception.UserScreenNameException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.ContactConstants;
import com.liferay.portal.kernel.model.ListType;
import com.liferay.portal.kernel.model.ListTypeConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.FullNameDefinition;
import com.liferay.portal.kernel.security.auth.FullNameDefinitionFactory;
import com.liferay.portal.kernel.security.auth.FullNameGenerator;
import com.liferay.portal.kernel.security.auth.FullNameGeneratorFactory;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ListTypeService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.persistence.ContactPersistence;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.ldap.ContactConverterKeys;
import com.liferay.portal.security.ldap.GroupConverterKeys;
import com.liferay.portal.security.ldap.UserConverterKeys;
import com.liferay.portal.security.ldap.exportimport.LDAPGroup;
import com.liferay.portal.security.ldap.exportimport.LDAPToPortalConverter;
import com.liferay.portal.security.ldap.exportimport.LDAPUser;
import com.liferay.portal.security.ldap.util.LDAPUtil;

import java.text.ParseException;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.naming.directory.Attributes;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Edward Han
 * @author Brian Wing Shun Chan
 */
@Component(service = LDAPToPortalConverter.class)
public class DefaultLDAPToPortalConverter implements LDAPToPortalConverter {

	@Override
	public LDAPGroup importLDAPGroup(
			long companyId, Attributes attributes, Properties groupMappings)
		throws Exception {

		LDAPGroup ldapGroup = new LDAPGroup();

		ldapGroup.setCompanyId(companyId);
		ldapGroup.setDescription(
			LDAPUtil.getAttributeString(
				attributes, groupMappings, GroupConverterKeys.DESCRIPTION));
		ldapGroup.setGroupName(
			LDAPUtil.getAttributeString(
				attributes, groupMappings, GroupConverterKeys.GROUP_NAME));

		return ldapGroup;
	}

	@Override
	public LDAPUser importLDAPUser(
			long companyId, Attributes attributes, Properties userMappings,
			Properties userExpandoMappings, Properties contactMappings,
			Properties contactExpandoMappings, String password)
		throws Exception {

		Company company = _companyLocalService.getCompany(companyId);

		Locale locale = company.getLocale();

		boolean autoScreenName = PrefsPropsUtil.getBoolean(
			companyId, PropsKeys.USERS_SCREEN_NAME_ALWAYS_AUTOGENERATE);

		String screenName = StringUtil.toLowerCase(
			LDAPUtil.getAttributeString(
				attributes, userMappings, UserConverterKeys.SCREEN_NAME));
		String emailAddress = LDAPUtil.getAttributeString(
			attributes, userMappings, UserConverterKeys.EMAIL_ADDRESS);

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Screen name ", screenName, " and email address ",
					emailAddress));
		}

		String firstName = LDAPUtil.getAttributeString(
			attributes, userMappings, UserConverterKeys.FIRST_NAME);
		String middleName = LDAPUtil.getAttributeString(
			attributes, userMappings, UserConverterKeys.MIDDLE_NAME);
		String lastName = LDAPUtil.getAttributeString(
			attributes, userMappings, UserConverterKeys.LAST_NAME);

		FullNameDefinition fullNameDefinition =
			FullNameDefinitionFactory.getInstance(locale);

		if (Validator.isNull(firstName) ||
			(fullNameDefinition.isFieldRequired("last-name") &&
			 Validator.isNull(lastName))) {

			String fullName = LDAPUtil.getAttributeString(
				attributes, userMappings, UserConverterKeys.FULL_NAME);

			FullNameGenerator fullNameGenerator =
				FullNameGeneratorFactory.getInstance();

			String[] names = fullNameGenerator.splitFullName(fullName);

			if (Validator.isNull(firstName)) {
				firstName = names[0];
			}

			if (Validator.isNull(middleName)) {
				middleName = names[1];
			}

			if (Validator.isNull(lastName)) {
				lastName = names[2];
			}
		}

		if (!autoScreenName && Validator.isNull(screenName)) {
			throw new UserScreenNameException.MustNotBeNull(
				ContactConstants.getFullName(firstName, middleName, lastName));
		}

		if (Validator.isNull(emailAddress) &&
			PrefsPropsUtil.getBoolean(
				companyId, PropsKeys.USERS_EMAIL_ADDRESS_REQUIRED)) {

			throw new UserEmailAddressException.MustNotBeNull(
				ContactConstants.getFullName(firstName, middleName, lastName));
		}

		LDAPUser ldapUser = new LDAPUser();

		ldapUser.setAutoPassword(password == null);
		ldapUser.setAutoScreenName(autoScreenName);

		Contact contact = _contactPersistence.create(0);

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

		contact.setPrefixListTypeId(
			_getListTypeId(
				attributes, companyId, contactMappings,
				ContactConverterKeys.PREFIX, ListTypeConstants.CONTACT_PREFIX));
		contact.setSuffixListTypeId(
			_getListTypeId(
				attributes, companyId, contactMappings,
				ContactConverterKeys.SUFFIX, ListTypeConstants.CONTACT_SUFFIX));

		String gender = LDAPUtil.getAttributeString(
			attributes, contactMappings, ContactConverterKeys.GENDER);

		gender = StringUtil.toLowerCase(gender);

		if (Validator.isNull(gender) || GetterUtil.getBoolean(gender) ||
			gender.equals("m") || gender.equals("male")) {

			contact.setMale(true);
		}
		else {
			contact.setMale(false);
		}

		try {
			contact.setBirthday(
				DateUtil.parseDate(
					LDAPUtil.getAttributeString(
						attributes, contactMappings,
						ContactConverterKeys.BIRTHDAY),
					locale));
		}
		catch (ParseException parseException) {
			if (_log.isDebugEnabled()) {
				_log.debug(parseException);
			}

			Calendar calendar = CalendarFactoryUtil.getCalendar(
				1970, Calendar.JANUARY, 1);

			contact.setBirthday(calendar.getTime());
		}

		contact.setSmsSn(
			LDAPUtil.getAttributeString(
				attributes, contactMappings, ContactConverterKeys.SMS_SN));
		contact.setFacebookSn(
			LDAPUtil.getAttributeString(
				attributes, contactMappings, ContactConverterKeys.FACEBOOK_SN));
		contact.setJabberSn(
			LDAPUtil.getAttributeString(
				attributes, contactMappings, ContactConverterKeys.JABBER_SN));
		contact.setSkypeSn(
			LDAPUtil.getAttributeString(
				attributes, contactMappings, ContactConverterKeys.SKYPE_SN));
		contact.setTwitterSn(
			LDAPUtil.getAttributeString(
				attributes, contactMappings, ContactConverterKeys.TWITTER_SN));
		contact.setJobTitle(
			LDAPUtil.getAttributeString(
				attributes, contactMappings, ContactConverterKeys.JOB_TITLE));

		ldapUser.setContact(contact);
		ldapUser.setContactExpandoAttributes(
			_getExpandoAttributes(attributes, contactExpandoMappings));
		ldapUser.setCreatorUserId(0);
		ldapUser.setGroupIds(null);
		ldapUser.setOrganizationIds(null);
		ldapUser.setPasswordReset(false);

		String portrait = userMappings.getProperty(UserConverterKeys.PORTRAIT);

		if (Validator.isNotNull(portrait)) {
			Object portraitObject = LDAPUtil.getAttributeObject(
				attributes, portrait);

			if (portraitObject != null) {
				byte[] portraitBytes = (byte[])portraitObject;

				if (portraitBytes.length > 0) {
					ldapUser.setPortraitBytes((byte[])portraitObject);
				}

				ldapUser.setUpdatePortrait(true);
			}
		}
		else {
			ldapUser.setUpdatePortrait(true);
		}

		ldapUser.setRoleIds(null);
		ldapUser.setSendEmail(false);

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setUuid(
			LDAPUtil.getAttributeString(
				attributes, userMappings, UserConverterKeys.UUID));

		ldapUser.setServiceContext(serviceContext);

		ldapUser.setUpdatePassword(password != null);

		User user = _userPersistence.create(0);

		user.setCompanyId(companyId);
		user.setScreenName(screenName);
		user.setEmailAddress(emailAddress);
		user.setOpenId(StringPool.BLANK);
		user.setLanguageId(locale.toString());
		user.setFirstName(firstName);
		user.setMiddleName(middleName);
		user.setLastName(lastName);
		user.setJobTitle(
			LDAPUtil.getAttributeString(
				attributes, userMappings, UserConverterKeys.JOB_TITLE));
		user.setPasswordUnencrypted(password);

		String status = LDAPUtil.getAttributeString(
			attributes, userMappings, UserConverterKeys.STATUS);

		if (Validator.isNotNull(status)) {
			user.setStatus(GetterUtil.getInteger(status));
		}

		ldapUser.setUser(user);
		ldapUser.setUserExpandoAttributes(
			_getExpandoAttributes(attributes, userExpandoMappings));
		ldapUser.setUserGroupIds(null);
		ldapUser.setUserGroupRoles(null);

		return ldapUser;
	}

	private Map<String, String[]> _getExpandoAttributes(
			Attributes attributes, Properties expandoMappings)
		throws Exception {

		Map<String, String[]> expandoAttributes = new HashMap<>();

		for (Object key : expandoMappings.keySet()) {
			String name = (String)key;

			String[] value = LDAPUtil.getAttributeStringArray(
				attributes, expandoMappings, name);

			if (value == null) {
				continue;
			}

			expandoAttributes.put(name, value);
		}

		return expandoAttributes;
	}

	private long _getListTypeId(
			Attributes attributes, long companyId, Properties contactMappings,
			String contactMappingsKey, String listTypeType)
		throws Exception {

		List<ListType> contactPrefixListTypes = _listTypeService.getListTypes(
			companyId, listTypeType);

		String name = LDAPUtil.getAttributeString(
			attributes, contactMappings, contactMappingsKey);

		for (ListType listType : contactPrefixListTypes) {
			if (name.equals(listType.getName())) {
				return listType.getListTypeId();
			}
		}

		return 0;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DefaultLDAPToPortalConverter.class);

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ContactPersistence _contactPersistence;

	@Reference
	private ListTypeService _listTypeService;

	@Reference
	private UserPersistence _userPersistence;

}