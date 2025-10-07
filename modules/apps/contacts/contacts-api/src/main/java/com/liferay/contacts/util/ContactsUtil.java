/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.contacts.util;

import com.liferay.contacts.constants.ContactsConstants;
import com.liferay.contacts.constants.SocialRelationConstants;
import com.liferay.contacts.model.Entry;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.EmailAddress;
import com.liferay.portal.kernel.model.ListType;
import com.liferay.portal.kernel.model.Phone;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.Website;
import com.liferay.portal.kernel.service.AddressLocalServiceUtil;
import com.liferay.portal.kernel.service.CountryServiceUtil;
import com.liferay.portal.kernel.service.EmailAddressLocalServiceUtil;
import com.liferay.portal.kernel.service.ListTypeServiceUtil;
import com.liferay.portal.kernel.service.PhoneLocalServiceUtil;
import com.liferay.portal.kernel.service.RegionServiceUtil;
import com.liferay.portal.kernel.service.WebsiteLocalServiceUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.social.kernel.model.SocialRequestConstants;
import com.liferay.social.kernel.service.SocialRelationLocalServiceUtil;
import com.liferay.social.kernel.service.SocialRequestLocalServiceUtil;

import java.lang.reflect.Field;

import java.util.List;

/**
 * @author Ryan Park
 * @author Jonathan Lee
 */
public class ContactsUtil {

	public static JSONObject getEntryJSONObject(Entry entry) {
		return JSONUtil.put(
			"comments", entry.getComments()
		).put(
			"emailAddress", entry.getEmailAddress()
		).put(
			"entryId", String.valueOf(entry.getEntryId())
		).put(
			"fullName", entry.getFullName()
		).put(
			"portalUser", false
		);
	}

	public static long getGroupId(String filterBy) {
		String groupIdString = filterBy.substring(
			ContactsConstants.FILTER_BY_GROUP.length());

		return GetterUtil.getLong(groupIdString);
	}

	public static String[] getPortalPropsValue(String key) {
		try {
			ClassLoader portalClassLoader =
				PortalClassLoaderUtil.getClassLoader();

			Class<?> targetClass = portalClassLoader.loadClass(
				"com.liferay.portal.kernel.util.PropsValues");

			Field field = targetClass.getField(key);

			return (String[])field.get((Object)null);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	public static long getSocialRelationType(String filterBy) {
		String socialRelationTypeString = filterBy.substring(
			ContactsConstants.FILTER_BY_TYPE.length());

		return GetterUtil.getLong(socialRelationTypeString);
	}

	public static JSONObject getUserJSONObject(long userId, User user)
		throws PortalException {

		JSONObject jsonObject = JSONUtil.put(
			"block",
			SocialRelationLocalServiceUtil.hasRelation(
				userId, user.getUserId(),
				SocialRelationConstants.TYPE_UNI_ENEMY)
		).put(
			"contactId", String.valueOf(user.getContactId())
		).put(
			"emailAddress", user.getEmailAddress()
		).put(
			"firstName", user.getFirstName()
		).put(
			"fullName", user.getFullName()
		).put(
			"jobTitle", user.getJobTitle()
		).put(
			"lastName", user.getLastName()
		).put(
			"portalUser", true
		).put(
			"portraitId", String.valueOf(user.getPortraitId())
		).put(
			"userId", String.valueOf(user.getUserId())
		).put(
			"uuid", user.getUuid()
		);

		if (!SocialRelationLocalServiceUtil.hasRelation(
				user.getUserId(), userId,
				SocialRelationConstants.TYPE_UNI_ENEMY) &&
			!SocialRelationLocalServiceUtil.hasRelation(
				userId, user.getUserId(),
				SocialRelationConstants.TYPE_UNI_ENEMY)) {

			boolean connectionRequested =
				SocialRequestLocalServiceUtil.hasRequest(
					userId, User.class.getName(), userId,
					SocialRelationConstants.TYPE_BI_CONNECTION,
					user.getUserId(), SocialRequestConstants.STATUS_PENDING);

			jsonObject.put("connectionRequested", connectionRequested);

			boolean connected = false;

			if (!connectionRequested &&
				SocialRelationLocalServiceUtil.hasRelation(
					userId, user.getUserId(),
					SocialRelationConstants.TYPE_BI_CONNECTION)) {

				connected = true;
			}

			jsonObject.put("connected", connected);

			boolean following = SocialRelationLocalServiceUtil.hasRelation(
				userId, user.getUserId(),
				SocialRelationConstants.TYPE_UNI_FOLLOWER);

			jsonObject.put("following", following);
		}

		return jsonObject;
	}

	public static String getVCard(User user) throws Exception {
		StringBundler sb = new StringBundler(9);

		sb.append(_getHeader());

		Contact contact = user.getContact();

		sb.append(_getName(user, contact));

		sb.append(_getJobTitle(user));
		sb.append(_getEmailAddresses(user));
		sb.append(_getPhones(user));
		sb.append(_getAddresses(user));
		sb.append(_getWebsites(user));
		sb.append(_getInstantMessaging(contact));
		sb.append(_getFooter());

		return sb.toString();
	}

	public static String getVCards(List<User> users) throws Exception {
		StringBundler sb = new StringBundler(users.size());

		for (User user : users) {
			sb.append(getVCard(user));
		}

		return sb.toString();
	}

	private static String _getAddresses(User user) throws Exception {
		List<Address> addresses = AddressLocalServiceUtil.getAddresses(
			user.getCompanyId(), Contact.class.getName(), user.getContactId());

		StringBundler sb = new StringBundler(addresses.size() * 19);

		for (Address address : addresses) {
			sb.append("ADR;TYPE=");
			sb.append(
				StringUtil.toUpperCase(
					_getVCardListTypeName(address.getListType())));
			sb.append(StringPool.COLON);
			sb.append(StringPool.SEMICOLON);
			sb.append(StringPool.SEMICOLON);

			if (Validator.isNotNull(address.getStreet1())) {
				sb.append(address.getStreet1());
			}

			if (Validator.isNotNull(address.getStreet2())) {
				sb.append("\\n");
				sb.append(address.getStreet2());
			}

			if (Validator.isNotNull(address.getStreet3())) {
				sb.append("\\n");
				sb.append(address.getStreet3());
			}

			sb.append(StringPool.SEMICOLON);

			if (Validator.isNotNull(address.getCity())) {
				sb.append(address.getCity());
			}

			sb.append(StringPool.SEMICOLON);

			long regionId = address.getRegionId();

			if (regionId > 0) {
				Region region = RegionServiceUtil.getRegion(regionId);

				sb.append(region.getName());
			}

			sb.append(StringPool.SEMICOLON);

			if (Validator.isNotNull(address.getZip())) {
				sb.append(address.getZip());
			}

			sb.append(StringPool.SEMICOLON);

			long countryId = address.getCountryId();

			if (countryId > 0) {
				Country country = CountryServiceUtil.getCountry(countryId);

				sb.append(country.getName());
			}

			sb.append(StringPool.NEW_LINE);
		}

		return sb.toString();
	}

	private static String _getEmailAddresses(User user) throws Exception {
		List<EmailAddress> emailAddresses =
			EmailAddressLocalServiceUtil.getEmailAddresses(
				user.getCompanyId(), Contact.class.getName(),
				user.getContactId());

		StringBundler sb = new StringBundler(3 + (emailAddresses.size() * 5));

		sb.append("EMAIL;TYPE=INTERNET;TYPE=HOME:");
		sb.append(user.getEmailAddress());
		sb.append(StringPool.NEW_LINE);

		for (EmailAddress emailAddress : emailAddresses) {
			sb.append("EMAIL;TYPE=INTERNET;TYPE=");

			ListType listType = emailAddress.getListType();

			sb.append(StringUtil.toUpperCase(listType.getName()));

			sb.append(StringPool.COLON);
			sb.append(emailAddress.getAddress());
			sb.append(StringPool.NEW_LINE);
		}

		return sb.toString();
	}

	private static String _getFooter() {
		return "END:VCARD\n";
	}

	private static String _getHeader() {
		return "BEGIN:VCARD\nVERSION:3.0\n";
	}

	private static String _getInstantMessaging(Contact contact) {
		StringBundler sb = new StringBundler(6);

		if (Validator.isNotNull(contact.getJabberSn())) {
			sb.append("X-JABBER;type=OTHER;type=pref:");
			sb.append(contact.getJabberSn());
			sb.append(StringPool.NEW_LINE);
		}

		if (Validator.isNotNull(contact.getSkypeSn())) {
			sb.append("X-SKYPE;type=OTHER;type=pref:");
			sb.append(contact.getSkypeSn());
			sb.append(StringPool.NEW_LINE);
		}

		return sb.toString();
	}

	private static String _getJobTitle(User user) {
		String jobTitle = user.getJobTitle();

		if (Validator.isNotNull(jobTitle)) {
			return StringBundler.concat(
				"TITLE:", jobTitle, StringPool.NEW_LINE);
		}

		return StringPool.BLANK;
	}

	private static String _getName(User user, Contact contact)
		throws Exception {

		StringBundler sb = new StringBundler(13);

		sb.append("N:");
		sb.append(user.getLastName());
		sb.append(StringPool.SEMICOLON);
		sb.append(user.getFirstName());
		sb.append(StringPool.SEMICOLON);
		sb.append(user.getMiddleName());
		sb.append(StringPool.SEMICOLON);

		long prefixListTypeId = contact.getPrefixListTypeId();

		if (prefixListTypeId > 0) {
			ListType listType = ListTypeServiceUtil.getListType(
				prefixListTypeId);

			sb.append(listType.getName());
		}

		sb.append(StringPool.SEMICOLON);

		long suffixListTypeId = contact.getSuffixListTypeId();

		if (suffixListTypeId > 0) {
			ListType listType = ListTypeServiceUtil.getListType(
				suffixListTypeId);

			sb.append(listType.getName());
		}

		sb.append("\nFN:");
		sb.append(user.getFullName());
		sb.append(StringPool.NEW_LINE);

		return sb.toString();
	}

	private static String _getPhones(User user) throws Exception {
		List<Phone> phones = PhoneLocalServiceUtil.getPhones(
			user.getCompanyId(), Contact.class.getName(), user.getContactId());

		StringBundler sb = new StringBundler(phones.size() * 7);

		for (Phone phone : phones) {
			sb.append("TEL;TYPE=");
			sb.append(
				StringUtil.toUpperCase(
					_getVCardListTypeName(phone.getListType())));
			sb.append(StringPool.COLON);
			sb.append(phone.getNumber());
			sb.append(StringPool.SPACE);
			sb.append(phone.getExtension());
			sb.append(StringPool.NEW_LINE);
		}

		return sb.toString();
	}

	private static String _getVCardListTypeName(ListType listType) {
		String listTypeName = listType.getName();

		if (StringUtil.equalsIgnoreCase(listTypeName, "business")) {
			listTypeName = "work";
		}
		else if (StringUtil.equalsIgnoreCase(listTypeName, "personal")) {
			listTypeName = "home";
		}

		return listTypeName;
	}

	private static String _getWebsites(User user) throws Exception {
		List<Website> websites = WebsiteLocalServiceUtil.getWebsites(
			user.getCompanyId(), Contact.class.getName(), user.getContactId());

		StringBundler sb = new StringBundler(websites.size() * 5);

		for (Website website : websites) {
			sb.append("URL;TYPE=");

			sb.append(
				StringUtil.toUpperCase(
					_getVCardListTypeName(website.getListType())));

			sb.append(StringPool.COLON);

			String url = website.getUrl();

			sb.append(url.replaceAll(StringPool.COLON, "\\:"));

			sb.append(StringPool.NEW_LINE);
		}

		return sb.toString();
	}

	private static final Log _log = LogFactoryUtil.getLog(ContactsUtil.class);

}