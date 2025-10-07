/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.membership.policy.test.util;

import com.liferay.announcements.kernel.model.AnnouncementsDelivery;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetTagLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetVocabularyLocalServiceUtil;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.expando.kernel.util.ExpandoBridgeFactoryUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.EmailAddress;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ListTypeConstants;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.model.Phone;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.model.Website;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupServiceUtil;
import com.liferay.portal.kernel.service.ListTypeServiceUtil;
import com.liferay.portal.kernel.service.OrganizationServiceUtil;
import com.liferay.portal.kernel.service.RoleServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupServiceUtil;
import com.liferay.portal.kernel.service.UserServiceUtil;
import com.liferay.portal.kernel.test.randomizerbumpers.NumericStringRandomizerBumper;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Roberto Díaz
 */
public class MembershipPolicyTestUtil {

	// All these methods are not using UserTestUtil.java or GroupTestUtil.java
	// because we need to call the Remote Service in order to verify the
	// Membership Policies

	public static Group addGroup() throws Exception {
		String name = RandomTestUtil.randomString();

		Map<Locale, String> nameMap = HashMapBuilder.put(
			LocaleUtil.getDefault(), name
		).build();

		String friendlyURL =
			StringPool.SLASH + FriendlyURLNormalizerUtil.normalize(name);

		return GroupServiceUtil.addGroup(
			GroupConstants.DEFAULT_PARENT_GROUP_ID,
			GroupConstants.DEFAULT_LIVE_GROUP_ID, nameMap,
			RandomTestUtil.randomLocaleStringMap(),
			GroupConstants.TYPE_SITE_OPEN, true,
			GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION, friendlyURL, true,
			true, populateServiceContext(Group.class, true));
	}

	public static Organization addOrganization() throws Exception {
		String name = RandomTestUtil.randomString();

		return OrganizationServiceUtil.addOrganization(
			null, OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID, name,
			OrganizationConstants.TYPE_ORGANIZATION, 0, 0,
			ListTypeServiceUtil.getListTypeId(
				TestPropsValues.getCompanyId(),
				ListTypeConstants.ORGANIZATION_STATUS_DEFAULT,
				ListTypeConstants.ORGANIZATION_STATUS),
			StringPool.BLANK, false,
			populateServiceContext(Organization.class, true));
	}

	public static Role addRole(int type) throws Exception {
		String name = RandomTestUtil.randomString();

		return RoleServiceUtil.addRole(
			RandomTestUtil.randomString(), null, 0, name,
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(), type,
			RandomTestUtil.randomString(),
			populateServiceContext(Role.class, false));
	}

	public static User addUser(
			long[] organizationIds, long[] roleIds, long[] siteIds,
			long[] userGroupIds)
		throws Exception {

		boolean autoPassword = true;
		String password1 = StringPool.BLANK;
		String password2 = StringPool.BLANK;
		boolean autoScreenName = true;
		String screenName = StringPool.BLANK;
		String emailAddress =
			"UserServiceTest." + RandomTestUtil.nextLong() + "@liferay.com";
		Locale locale = LocaleUtil.getDefault();
		String firstName = "UserServiceTest";
		String middleName = StringPool.BLANK;
		String lastName = "UserServiceTest";
		long prefixListTypeId = 0;
		long suffixListTypeId = 0;
		boolean male = true;
		int birthdayMonth = Calendar.JANUARY;
		int birthdayDay = 1;
		int birthdayYear = 1970;
		String jobTitle = StringPool.BLANK;
		boolean sendMail = false;

		return UserServiceUtil.addUser(
			TestPropsValues.getCompanyId(), autoPassword, password1, password2,
			autoScreenName, screenName, emailAddress, locale, firstName,
			middleName, lastName, prefixListTypeId, suffixListTypeId, male,
			birthdayMonth, birthdayDay, birthdayYear, jobTitle, siteIds,
			organizationIds, roleIds, userGroupIds, sendMail,
			new ServiceContext());
	}

	public static UserGroup addUserGroup() throws Exception {
		String name = RandomTestUtil.randomString();
		String description = RandomTestUtil.randomString(50);

		return UserGroupServiceUtil.addUserGroup(
			StringPool.BLANK, name, description,
			populateServiceContext(UserGroup.class, false));
	}

	public static User updateUser(
			User user, long[] organizationIds, long[] roleIds, long[] siteIds,
			long[] userGroupIds, List<UserGroupRole> userGroupRoles)
		throws Exception {

		long userId = user.getUserId();
		String oldPassword = user.getPassword();

		String newPassword1 = RandomTestUtil.randomString();

		String newPassword2 = newPassword1;

		boolean passwordReset = true;
		String reminderQueryQuestion = RandomTestUtil.randomString();
		String reminderQueryAnswer = RandomTestUtil.randomString();

		String screenName = RandomTestUtil.randomString(
			NumericStringRandomizerBumper.INSTANCE);
		String emailAddress =
			"UserServiceTest." + RandomTestUtil.nextLong() + "@liferay.com";
		String languageId = LocaleUtil.toLanguageId(LocaleUtil.getDefault());
		String timeZoneId = RandomTestUtil.randomString();
		String greeting = RandomTestUtil.randomString();
		String comments = RandomTestUtil.randomString();
		String firstName = "UserServiceTest";
		String middleName = StringPool.BLANK;
		String lastName = "UserServiceTest";
		long prefixListTypeId = 0;
		long suffixListTypeId = 0;
		boolean male = true;
		int birthdayMonth = Calendar.JANUARY;
		int birthdayDay = 1;
		int birthdayYear = 1970;
		String jobTitle = StringPool.BLANK;
		String smsSn =
			"UserServiceTestSmsSn." + RandomTestUtil.nextInt() + "@liferay.com";
		String facebookSn = RandomTestUtil.randomString();
		String jabberSn = RandomTestUtil.randomString();
		String skypeSn = RandomTestUtil.randomString();
		String twitterSn = StringPool.BLANK;

		List<Address> addresses = new ArrayList<>();
		List<EmailAddress> emailAddresses = new ArrayList<>();
		List<Phone> phones = new ArrayList<>();
		List<Website> websites = new ArrayList<>();
		List<AnnouncementsDelivery> announcementsDelivers = new ArrayList<>();

		return UserServiceUtil.updateUser(
			userId, oldPassword, newPassword1, newPassword2, passwordReset,
			reminderQueryQuestion, reminderQueryAnswer, screenName,
			emailAddress, false, null, languageId, timeZoneId, greeting,
			comments, firstName, middleName, lastName, prefixListTypeId,
			suffixListTypeId, male, birthdayMonth, birthdayDay, birthdayYear,
			smsSn, facebookSn, jabberSn, skypeSn, twitterSn, jobTitle, siteIds,
			organizationIds, roleIds, userGroupRoles, userGroupIds, addresses,
			emailAddresses, phones, websites, announcementsDelivers,
			new ServiceContext());
	}

	protected static Map<String, Serializable> addExpandoMap(Class<?> clazz)
		throws PortalException {

		ExpandoBridge expandoBridge = ExpandoBridgeFactoryUtil.getExpandoBridge(
			TestPropsValues.getCompanyId(), clazz.getName());

		expandoBridge.addAttribute("key1", false);
		expandoBridge.addAttribute("key2", false);
		expandoBridge.addAttribute("key3", false);
		expandoBridge.addAttribute("key4", false);

		return HashMapBuilder.<String, Serializable>put(
			"key1", "value1"
		).put(
			"key2", "value2"
		).put(
			"key3", "value3"
		).put(
			"key4", "value4"
		).build();
	}

	protected static ServiceContext populateServiceContext(
			Class<?> clazz, boolean includeCategorization)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);

		if (includeCategorization) {
			AssetTag tag = AssetTagLocalServiceUtil.addTag(
				null, TestPropsValues.getUserId(), TestPropsValues.getGroupId(),
				RandomTestUtil.randomString(), new ServiceContext());

			serviceContext.setAssetTagNames(new String[] {tag.getName()});

			Company company = CompanyLocalServiceUtil.getCompany(
				TestPropsValues.getCompanyId());

			serviceContext.setScopeGroupId(company.getGroupId());

			AssetVocabulary vocabulary =
				AssetVocabularyLocalServiceUtil.addVocabulary(
					TestPropsValues.getUserId(), company.getGroupId(),
					RandomTestUtil.randomString(), serviceContext);

			AssetCategory category = AssetCategoryLocalServiceUtil.addCategory(
				TestPropsValues.getUserId(), company.getGroupId(),
				RandomTestUtil.randomString(), vocabulary.getVocabularyId(),
				serviceContext);

			serviceContext.setAssetCategoryIds(
				new long[] {category.getCategoryId()});
		}

		serviceContext.setExpandoBridgeAttributes(addExpandoMap(clazz));

		return serviceContext;
	}

}