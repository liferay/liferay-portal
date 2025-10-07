/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.util;

import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationParameterMapFactoryUtil;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.service.StagingLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.thread.local.Lifecycle;
import com.liferay.portal.kernel.cache.thread.local.ThreadLocalCacheManager;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupServiceUtil;
import com.liferay.portal.kernel.service.LayoutSetLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.randomizerbumpers.NumericStringRandomizerBumper;
import com.liferay.portal.kernel.test.randomizerbumpers.UniqueStringRandomizerBumper;
import com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TreeMapBuilder;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;

import java.io.Serializable;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

/**
 * @author Manuel de la Peña
 */
public class GroupTestUtil {

	public static Group addGroup() throws Exception {
		return addGroup(GroupConstants.DEFAULT_PARENT_GROUP_ID);
	}

	public static Group addGroup(long parentGroupId) throws Exception {
		return addGroup(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			parentGroupId);
	}

	public static Group addGroup(long userId, Layout layout) throws Exception {
		return addGroup(userId, GroupConstants.DEFAULT_PARENT_GROUP_ID, layout);
	}

	public static Group addGroup(long userId, long parentGroupId, Layout layout)
		throws Exception {

		Group scopeGroup = layout.getScopeGroup();

		if (scopeGroup != null) {
			return scopeGroup;
		}

		return GroupLocalServiceUtil.addGroup(
			userId, parentGroupId, Layout.class.getName(), layout.getPlid(),
			GroupConstants.DEFAULT_LIVE_GROUP_ID,
			HashMapBuilder.put(
				LocaleUtil.getDefault(), String.valueOf(layout.getPlid())
			).build(),
			null, 0, true, GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION, null,
			false, true, null);
	}

	public static Group addGroup(
			long companyId, long userId, long parentGroupId)
		throws Exception {

		String name = RandomTestUtil.randomString(
			NumericStringRandomizerBumper.INSTANCE,
			UniqueStringRandomizerBumper.INSTANCE);

		Group group = GroupLocalServiceUtil.fetchGroup(companyId, name);

		if (group != null) {
			return group;
		}

		Map<Locale, String> nameMap = HashMapBuilder.put(
			LocaleUtil.getDefault(), name
		).build();

		int type = GroupConstants.TYPE_SITE_OPEN;
		String friendlyURL =
			StringPool.SLASH + FriendlyURLNormalizerUtil.normalize(name);
		boolean site = true;
		boolean active = true;
		boolean manualMembership = true;
		int membershipRestriction =
			GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION;

		return GroupLocalServiceUtil.addGroup(
			userId, parentGroupId, null, 0,
			GroupConstants.DEFAULT_LIVE_GROUP_ID, nameMap,
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			type, manualMembership, membershipRestriction, friendlyURL, site,
			active,
			ServiceContextTestUtil.getServiceContext(
				GroupLocalServiceUtil.getGroup(companyId, GroupConstants.GUEST),
				userId));
	}

	public static Group addGroup(
			long companyId, long userId, long parentGroupId, String groupKey)
		throws Exception {

		Group group = GroupLocalServiceUtil.fetchGroup(companyId, groupKey);

		if (group != null) {
			return group;
		}

		Map<Locale, String> nameMap = HashMapBuilder.put(
			LocaleUtil.getDefault(), groupKey
		).build();

		int type = GroupConstants.TYPE_SITE_OPEN;
		String friendlyURL =
			StringPool.SLASH + FriendlyURLNormalizerUtil.normalize(groupKey);
		boolean site = true;
		boolean active = true;
		boolean manualMembership = true;
		int membershipRestriction =
			GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION;

		return GroupLocalServiceUtil.addGroup(
			userId, parentGroupId, null, 0,
			GroupConstants.DEFAULT_LIVE_GROUP_ID, nameMap,
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			type, manualMembership, membershipRestriction, friendlyURL, site,
			active,
			ServiceContextTestUtil.getServiceContext(
				GroupLocalServiceUtil.getGroup(companyId, GroupConstants.GUEST),
				userId));
	}

	public static Group addGroup(
			long parentGroupId, ServiceContext serviceContext)
		throws Exception {

		String name = RandomTestUtil.randomString(
			NumericStringRandomizerBumper.INSTANCE,
			UniqueStringRandomizerBumper.INSTANCE);

		return addGroup(parentGroupId, name, serviceContext);
	}

	public static Group addGroup(
			long parentGroupId, String name, ServiceContext serviceContext)
		throws Exception {

		Group group = GroupLocalServiceUtil.fetchGroup(
			TestPropsValues.getCompanyId(), name);

		if (group != null) {
			return group;
		}

		Map<Locale, String> nameMap = HashMapBuilder.put(
			LocaleUtil.getDefault(), name
		).build();

		int type = GroupConstants.TYPE_SITE_OPEN;
		String friendlyURL =
			StringPool.SLASH + FriendlyURLNormalizerUtil.normalize(name);
		boolean site = true;
		boolean active = true;
		boolean manualMembership = true;
		int membershipRestriction =
			GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION;

		if (serviceContext == null) {
			serviceContext = ServiceContextTestUtil.getServiceContext();
		}

		return GroupServiceUtil.addGroup(
			parentGroupId, GroupConstants.DEFAULT_LIVE_GROUP_ID, nameMap,
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			type, manualMembership, membershipRestriction, friendlyURL, site,
			active, serviceContext);
	}

	public static Group addGroupToCompany(long companyId) throws Exception {
		return addGroupToCompany(
			companyId, GroupConstants.DEFAULT_PARENT_GROUP_ID);
	}

	public static Group addGroupToCompany(long companyId, long parentGroupId)
		throws Exception {

		User user = UserTestUtil.getAdminUser(companyId);

		return addGroup(companyId, user.getUserId(), parentGroupId);
	}

	public static Group addGroupWithType(int type) throws Exception {
		return addGroupWithType(GroupConstants.DEFAULT_PARENT_GROUP_ID, type);
	}

	public static Group addGroupWithType(long parentGroupId, int type)
		throws Exception {

		return addGroupWithType(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			parentGroupId, type);
	}

	public static Group addGroupWithType(
			long companyId, long userId, long parentGroupId, int type)
		throws Exception {

		String name = RandomTestUtil.randomString(
			NumericStringRandomizerBumper.INSTANCE,
			UniqueStringRandomizerBumper.INSTANCE);

		Group group = GroupLocalServiceUtil.fetchGroup(companyId, name);

		if (group != null) {
			return group;
		}

		Map<Locale, String> nameMap = HashMapBuilder.put(
			LocaleUtil.getDefault(), name
		).build();
		boolean manualMembership = true;
		int membershipRestriction =
			GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION;
		String friendlyURL =
			StringPool.SLASH + FriendlyURLNormalizerUtil.normalize(name);
		boolean site = true;
		boolean active = true;

		return GroupLocalServiceUtil.addGroup(
			userId, parentGroupId, null, 0,
			GroupConstants.DEFAULT_LIVE_GROUP_ID, nameMap,
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			type, manualMembership, membershipRestriction, friendlyURL, site,
			active,
			ServiceContextTestUtil.getServiceContext(
				GroupLocalServiceUtil.getGroup(companyId, GroupConstants.GUEST),
				userId));
	}

	public static void addLayoutSetVirtualHost(
			Group group, boolean privateLayout)
		throws Exception {

		StringBundler sb = new StringBundler(3);

		sb.append(group.getGroupKey());

		if (privateLayout) {
			sb.append("-private.");
		}
		else {
			sb.append("-public.");
		}

		sb.append(RandomTestUtil.randomString(3));

		LayoutSetLocalServiceUtil.updateVirtualHosts(
			group.getGroupId(), privateLayout,
			TreeMapBuilder.put(
				sb.toString(), StringPool.BLANK
			).build());
	}

	public static void addLayoutSetVirtualHosts(Group group) throws Exception {
		addLayoutSetVirtualHost(group, true);
		addLayoutSetVirtualHost(group, false);
	}

	public static Group deleteGroup(Group group) throws Exception {
		return GroupLocalServiceUtil.deleteGroup(group);
	}

	public static void enableLocalStaging(Group group) throws Exception {
		enableLocalStaging(group, TestPropsValues.getUserId());
	}

	public static void enableLocalStaging(Group group, long userId)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);
		serviceContext.setScopeGroupId(group.getGroupId());

		Map<String, Serializable> attributes = serviceContext.getAttributes();

		attributes.putAll(
			ExportImportConfigurationParameterMapFactoryUtil.
				buildParameterMap());

		attributes.put(
			PortletDataHandlerKeys.PORTLET_CONFIGURATION_ALL,
			new String[] {Boolean.FALSE.toString()});
		attributes.put(
			PortletDataHandlerKeys.PORTLET_DATA_ALL,
			new String[] {Boolean.FALSE.toString()});

		StagingLocalServiceUtil.enableLocalStaging(
			userId, group, false, false, serviceContext);
	}

	public static Group updateDisplaySettings(
			long groupId, Collection<Locale> availableLocales,
			Locale defaultLocale)
		throws Exception {

		Group group = GroupLocalServiceUtil.updateGroup(
			groupId,
			UnicodePropertiesBuilder.put(
				GroupConstants.TYPE_SETTINGS_KEY_INHERIT_LOCALES,
				() -> {
					boolean inheritLocales = false;

					if ((availableLocales == null) && (defaultLocale == null)) {
						inheritLocales = true;
					}

					return String.valueOf(inheritLocales);
				}
			).put(
				PropsKeys.LOCALES,
				() -> {
					if (availableLocales != null) {
						return StringUtil.merge(
							LocaleUtil.toLanguageIds(availableLocales));
					}

					return null;
				}
			).put(
				"languageId",
				() -> {
					if (defaultLocale != null) {
						return LocaleUtil.toLanguageId(defaultLocale);
					}

					return null;
				}
			).buildString());

		ThreadLocalCacheManager.clearAll(Lifecycle.REQUEST);

		return group;
	}

}