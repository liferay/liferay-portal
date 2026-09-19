/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.ldap.util;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.ldap.SafeLdapFilter;
import com.liferay.portal.security.ldap.SafeLdapFilterFactory;
import com.liferay.portal.security.ldap.SafeLdapFilterTemplate;
import com.liferay.portal.security.ldap.SafeLdapName;
import com.liferay.portal.security.ldap.SafeLdapNameFactory;
import com.liferay.portal.security.ldap.configuration.LDAPServerConfiguration;
import com.liferay.portal.security.ldap.validator.LDAPFilterException;
import com.liferay.portal.security.ldap.validator.LDAPFilterValidator;

import java.text.DateFormat;

import java.util.Date;
import java.util.Properties;

import javax.naming.InvalidNameException;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * @author Toma Bedolla
 * @author Michael Young
 * @author Brian Wing Shun Chan
 * @author James Lefeu
 * @author Vilmos Papp
 */
public class LDAPUtil {

	/**
	 * @deprecated As of Mueller (7.2.x), replaced by {@link SafeLdapFilter}
	 */
	@Deprecated
	public static String escapeCharacters(String attribute) {
		if (attribute.contains(StringPool.BACK_SLASH)) {
			String escapedSingleBackSlash = StringPool.DOUBLE_BACK_SLASH.concat(
				StringPool.BACK_SLASH);

			attribute = StringUtil.replace(
				attribute, CharPool.BACK_SLASH, escapedSingleBackSlash);
		}
		else {
			attribute = StringEscapeUtils.escapeJava(attribute);
		}

		return StringUtil.replace(
			attribute, _INVALID_CHARS, _INVALID_CHARS_SUBS);
	}

	public static Object getAttributeObject(
			Attributes attributes, Properties properties, String key)
		throws NamingException {

		String id = properties.getProperty(key);

		return getAttributeObject(attributes, id);
	}

	public static Object getAttributeObject(
			Attributes attributes, Properties properties, String key,
			Object defaultValue)
		throws NamingException {

		String id = properties.getProperty(key);

		return getAttributeObject(attributes, id, defaultValue);
	}

	public static Object getAttributeObject(Attributes attributes, String id)
		throws NamingException {

		return getAttributeObject(attributes, id, null);
	}

	public static Object getAttributeObject(
			Attributes attributes, String id, Object defaultValue)
		throws NamingException {

		if (Validator.isNull(id)) {
			return defaultValue;
		}

		Attribute attribute = attributes.get(id);

		if (attribute == null) {
			return defaultValue;
		}

		Object object = attribute.get();

		if (object == null) {
			return defaultValue;
		}

		return object;
	}

	public static String getAttributeString(
			Attributes attributes, Properties properties, String key)
		throws NamingException {

		String id = properties.getProperty(key);

		return getAttributeString(attributes, id);
	}

	public static String getAttributeString(
			Attributes attributes, Properties properties, String key,
			String defaultValue)
		throws NamingException {

		String id = properties.getProperty(key);

		return getAttributeString(attributes, id, defaultValue);
	}

	public static String getAttributeString(Attributes attributes, String id)
		throws NamingException {

		return getAttributeString(attributes, id, StringPool.BLANK);
	}

	public static String getAttributeString(
			Attributes attributes, String id, String defaultValue)
		throws NamingException {

		if (Validator.isNull(id)) {
			return defaultValue;
		}

		Attribute attribute = attributes.get(id);

		if (attribute == null) {
			return defaultValue;
		}

		Object object = attribute.get();

		if (object == null) {
			return defaultValue;
		}

		return object.toString();
	}

	public static String[] getAttributeStringArray(
			Attributes attributes, Properties properties, String key)
		throws NamingException {

		String id = properties.getProperty(key);

		return getAttributeStringArray(attributes, id);
	}

	public static String[] getAttributeStringArray(
			Attributes attributes, String id)
		throws NamingException {

		if (Validator.isNull(id)) {
			return null;
		}

		Attribute attribute = attributes.get(id);

		if (attribute == null) {
			return new String[0];
		}

		int size = attribute.size();

		if (size == 0) {
			return null;
		}

		String[] array = new String[size];

		for (int i = 0; i < size; i++) {
			Object object = attribute.get(i);

			if (object == null) {
				array[i] = StringPool.BLANK;
			}
			else {
				array[i] = object.toString();
			}
		}

		return array;
	}

	public static SafeLdapFilterTemplate getAuthSearchSafeLdapFilterTemplate(
			LDAPServerConfiguration ldapServerConfiguration,
			LDAPFilterValidator ldapFilterValidator)
		throws LDAPFilterException {

		if (ldapServerConfiguration.authSearchFilter() == null) {
			return null;
		}

		try {
			return new SafeLdapFilterTemplate(
				ldapServerConfiguration.authSearchFilter(),
				new String[] {
					"@company_id@", "@email_address@", "@screen_name@",
					"@user_id@"
				},
				ldapFilterValidator);
		}
		catch (LDAPFilterException ldapFilterException) {
			throw new LDAPFilterException(
				"Invalid filter " +
					LDAPServerConfiguration.class.getSimpleName() +
						".authSearchFilter",
				ldapFilterException);
		}
	}

	public static SafeLdapName getBaseDNSafeLdapName(
			LDAPServerConfiguration ldapServerConfiguration)
		throws InvalidNameException {

		return SafeLdapNameFactory.fromUnsafe(ldapServerConfiguration.baseDN());
	}

	public static String getFullProviderURL(String baseURL, String baseDN) {
		return baseURL + StringPool.SLASH + baseDN;
	}

	public static SafeLdapFilter getGroupSearchSafeLdapFilter(
			LDAPServerConfiguration ldapServerConfiguration,
			LDAPFilterValidator ldapFilterValidator)
		throws LDAPFilterException {

		if (ldapServerConfiguration.groupSearchFilter() == null) {
			return null;
		}

		try {
			return SafeLdapFilterFactory.fromUnsafeFilter(
				ldapServerConfiguration.groupSearchFilter(),
				ldapFilterValidator);
		}
		catch (LDAPFilterException ldapFilterException) {
			throw new LDAPFilterException(
				"Invalid filter " +
					LDAPServerConfiguration.class.getSimpleName() +
						".groupSearchFilter",
				ldapFilterException);
		}
	}

	public static SafeLdapName getGroupsDNSafeLdapName(
			LDAPServerConfiguration ldapServerConfiguration)
		throws InvalidNameException {

		return SafeLdapNameFactory.fromUnsafe(
			ldapServerConfiguration.groupsDN());
	}

	public static SafeLdapFilter getUserSearchSafeLdapFilter(
			LDAPServerConfiguration ldapServerConfiguration,
			LDAPFilterValidator ldapFilterValidator)
		throws LDAPFilterException {

		if (ldapServerConfiguration.userSearchFilter() == null) {
			return null;
		}

		try {
			return SafeLdapFilterFactory.fromUnsafeFilter(
				ldapServerConfiguration.userSearchFilter(),
				ldapFilterValidator);
		}
		catch (LDAPFilterException ldapFilterException) {
			throw new LDAPFilterException(
				"Invalid filter " +
					LDAPServerConfiguration.class.getSimpleName() +
						".userSearchFilter",
				ldapFilterException);
		}
	}

	public static SafeLdapName getUsersDNSafeLdapName(
			LDAPServerConfiguration ldapServerConfiguration)
		throws InvalidNameException {

		return SafeLdapNameFactory.fromUnsafe(
			ldapServerConfiguration.usersDN());
	}

	public static Date parseDate(String date) throws Exception {
		if (Validator.isNull(date)) {
			return null;
		}

		String format = "yyyyMMddHHmmss";

		if (date.endsWith("Z")) {
			if (date.indexOf(CharPool.PERIOD) != -1) {
				format = "yyyyMMddHHmmss.S'Z'";
			}
			else {
				format = "yyyyMMddHHmmss'Z'";
			}
		}
		else if ((date.indexOf(CharPool.DASH) != -1) ||
				 (date.indexOf(CharPool.PLUS) != -1)) {

			if (date.indexOf(CharPool.PERIOD) != -1) {
				format = "yyyyMMddHHmmss.SSSZ";
			}
			else {
				format = "yyyyMMddHHmmssZ";
			}
		}
		else if (date.indexOf(CharPool.PERIOD) != -1) {
			format = "yyyyMMddHHmmss.S";
		}

		DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			format);

		return dateFormat.parse(date);
	}

	private static final String[] _INVALID_CHARS = {
		StringPool.GREATER_THAN, StringPool.LESS_THAN, StringPool.PLUS,
		StringPool.POUND, StringPool.QUOTE, StringPool.SEMICOLON
	};

	private static final String[] _INVALID_CHARS_SUBS = {
		StringPool.DOUBLE_BACK_SLASH.concat(StringPool.GREATER_THAN),
		StringPool.DOUBLE_BACK_SLASH.concat(StringPool.LESS_THAN),
		StringPool.DOUBLE_BACK_SLASH.concat(StringPool.PLUS),
		StringPool.DOUBLE_BACK_SLASH.concat(StringPool.POUND),
		StringPool.DOUBLE_BACK_SLASH.concat(StringPool.QUOTE),
		StringPool.DOUBLE_BACK_SLASH.concat(StringPool.SEMICOLON)
	};

}