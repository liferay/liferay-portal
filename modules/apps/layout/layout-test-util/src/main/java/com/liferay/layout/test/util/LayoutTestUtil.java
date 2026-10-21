/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.test.util;

import com.liferay.layout.constants.LayoutTypeSettingsConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalServiceUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CustomizedPages;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.model.LayoutTemplate;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutPrototypeLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutServiceUtil;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.randomizerbumpers.NumericStringRandomizerBumper;
import com.liferay.portal.kernel.test.randomizerbumpers.UniqueStringRandomizerBumper;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ScopeUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;

import jakarta.portlet.PortletPreferences;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Manuel de la Peña
 * @author Máté Thurzó
 */
public class LayoutTestUtil {

	public static LayoutPrototype addLayoutPrototype(String name)
		throws Exception {

		return LayoutPrototypeLocalServiceUtil.addLayoutPrototype(
			TestPropsValues.getUserId(), TestPropsValues.getCompanyId(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), name
			).build(),
			(Map<Locale, String>)null, true,
			ServiceContextTestUtil.getServiceContext());
	}

	public static LayoutSetPrototype addLayoutSetPrototype(String name)
		throws Exception {

		return LayoutSetPrototypeLocalServiceUtil.addLayoutSetPrototype(
			TestPropsValues.getUserId(), TestPropsValues.getCompanyId(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), name
			).build(),
			(Map<Locale, String>)null, true, true,
			ServiceContextTestUtil.getServiceContext());
	}

	public static String addPortletToLayout(Layout layout, String portletId)
		throws Exception {

		Map<String, String[]> preferenceMap = null;

		return addPortletToLayout(layout, portletId, preferenceMap);
	}

	public static String addPortletToLayout(
			Layout layout, String portletId,
			Map<String, String[]> preferenceMap)
		throws Exception {

		long userId = TestPropsValues.getUserId();

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		LayoutTemplate layoutTemplate = layoutTypePortlet.getLayoutTemplate();

		List<String> columns = layoutTemplate.getColumns();

		String columnId = columns.get(0);

		return addPortletToLayout(
			userId, layout, portletId, columnId, preferenceMap);
	}

	public static String addPortletToLayout(
			long userId, Layout layout, String portletId, String columnId,
			Map<String, String[]> preferenceMap)
		throws Exception {

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		String newPortletId = layoutTypePortlet.addPortletId(
			userId, portletId, columnId, -1);

		LayoutLocalServiceUtil.updateTypeSettings(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			layout.getTypeSettings());

		if (preferenceMap == null) {
			return newPortletId;
		}

		PortletPreferences portletPreferences = getPortletPreferences(
			layout, newPortletId);

		for (Map.Entry<String, String[]> entry : preferenceMap.entrySet()) {
			portletPreferences.setValues(entry.getKey(), entry.getValue());
		}

		portletPreferences.store();

		return newPortletId;
	}

	public static Layout addTypeContentLayout(Group group) throws Exception {
		return LayoutLocalServiceUtil.addLayout(
			null, TestPropsValues.getUserId(), group.getGroupId(), false,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
			RandomTestUtil.randomString(), StringPool.BLANK, StringPool.BLANK,
			LayoutConstants.TYPE_CONTENT, false, StringPool.BLANK,
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId()));
	}

	public static Layout addTypeContentLayout(
			Group group, boolean privateLayout, boolean system)
		throws Exception {

		return LayoutLocalServiceUtil.addLayout(
			null, TestPropsValues.getUserId(), group.getGroupId(),
			privateLayout, LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
			RandomTestUtil.randomString(), StringPool.BLANK, StringPool.BLANK,
			LayoutConstants.TYPE_CONTENT, false, system, StringPool.BLANK,
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId()));
	}

	public static Layout addTypeContentLayout(
			Group group, boolean privateLayout, boolean system,
			String masterLayoutPageTemplateEntryERC)
		throws Exception {

		return LayoutLocalServiceUtil.addLayout(
			null, TestPropsValues.getUserId(), group.getGroupId(),
			privateLayout, LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, 0, 0,
			HashMapBuilder.put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			Collections.emptyMap(), Collections.emptyMap(),
			Collections.emptyMap(), Collections.emptyMap(),
			LayoutConstants.TYPE_CONTENT, StringPool.BLANK, false, system,
			Collections.emptyMap(), masterLayoutPageTemplateEntryERC,
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId()));
	}

	public static Layout addTypeContentLayout(Group group, long parentPlid)
		throws Exception {

		Layout layout = LayoutLocalServiceUtil.addLayout(
			null, TestPropsValues.getUserId(), group.getGroupId(), false,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
			RandomTestUtil.randomString(), StringPool.BLANK, StringPool.BLANK,
			LayoutConstants.TYPE_CONTENT, false, StringPool.BLANK,
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId()));

		return LayoutLocalServiceUtil.updateParentLayoutId(
			layout.getPlid(), parentPlid);
	}

	public static Layout addTypeContentLayout(
			Group group, Map<Locale, String> nameMap)
		throws Exception {

		return LayoutLocalServiceUtil.addLayout(
			null, TestPropsValues.getUserId(), group.getGroupId(), false,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, 0, 0, nameMap,
			Collections.emptyMap(), Collections.emptyMap(),
			Collections.emptyMap(), Collections.emptyMap(),
			LayoutConstants.TYPE_CONTENT, StringPool.BLANK, false, false,
			Collections.emptyMap(), null,
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId()));
	}

	public static Layout addTypeContentLayout(Group group, String name)
		throws Exception {

		return LayoutLocalServiceUtil.addLayout(
			null, TestPropsValues.getUserId(), group.getGroupId(), false,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, name, StringPool.BLANK,
			StringPool.BLANK, LayoutConstants.TYPE_CONTENT, false,
			StringPool.BLANK,
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId()));
	}

	public static Layout addTypeContentLayout(
			Group group, String name, String title)
		throws Exception {

		return LayoutLocalServiceUtil.addLayout(
			null, TestPropsValues.getUserId(), group.getGroupId(), false,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, name, title,
			StringPool.BLANK, LayoutConstants.TYPE_CONTENT, false,
			StringPool.BLANK,
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId()));
	}

	public static Layout addTypeContentLayout(long userId, Group group)
		throws Exception {

		return LayoutLocalServiceUtil.addLayout(
			null, userId, group.getGroupId(), false,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
			RandomTestUtil.randomString(), StringPool.BLANK, StringPool.BLANK,
			LayoutConstants.TYPE_CONTENT, false, StringPool.BLANK,
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId()));
	}

	public static Layout addTypeContentPublishedLayout(
			Group group, String name, int status)
		throws Exception {

		Layout layout = addTypeContentLayout(group, name);

		Layout draftLayout = LayoutLocalServiceUtil.fetchDraftLayout(
			layout.getPlid());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId());

		serviceContext.setAttribute(
			LayoutTypeSettingsConstants.KEY_PUBLISHED, Boolean.TRUE);

		if (draftLayout != null) {
			LayoutLocalServiceUtil.updateStatus(
				draftLayout.getUserId(), draftLayout.getPlid(), status,
				serviceContext);
		}

		return LayoutLocalServiceUtil.updateStatus(
			layout.getUserId(), layout.getPlid(), status, serviceContext);
	}

	public static Layout addTypeEmbeddedLayout(long groupId) throws Exception {
		return addTypeEmbeddedLayout(groupId, false);
	}

	public static Layout addTypeEmbeddedLayout(
			long groupId, boolean privateLayout)
		throws Exception {

		Layout layout = addTypePortletLayout(groupId, privateLayout);

		layout.setType(LayoutConstants.TYPE_EMBEDDED);

		return LayoutLocalServiceUtil.updateLayout(layout);
	}

	public static Layout addTypeEmptyLayout(Group group) throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId());

		serviceContext.setAttribute(
			"layout.instanceable.allowed", Boolean.TRUE);

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			return LayoutLocalServiceUtil.addLayout(
				null, TestPropsValues.getUserId(), group.getGroupId(), false,
				LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
				RandomTestUtil.randomString(), StringPool.BLANK,
				StringPool.BLANK, LayoutConstants.TYPE_EMPTY, true,
				StringPool.BLANK, serviceContext);
		}
	}

	public static Layout addTypeFullPageApplicationLayout(long groupId)
		throws Exception {

		Layout layout = addTypePortletLayout(groupId, false);

		layout.setType(LayoutConstants.TYPE_FULL_PAGE_APPLICATION);

		return LayoutLocalServiceUtil.updateLayout(layout);
	}

	public static Layout addTypeLinkToLayoutLayout(
			long groupId, boolean privateLayout, long linkToLayoutId)
		throws Exception {

		Layout layout = addTypePortletLayout(groupId, privateLayout);

		layout.setType(LayoutConstants.TYPE_LINK_TO_LAYOUT);

		UnicodeProperties typeSettingsUnicodeProperties =
			layout.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.setProperty(
			"linkToLayoutId", String.valueOf(linkToLayoutId));

		return LayoutLocalServiceUtil.updateLayout(layout);
	}

	public static Layout addTypeLinkToLayoutLayout(
			long groupId, long linkToLayoutId)
		throws Exception {

		return addTypeLinkToLayoutLayout(groupId, false, linkToLayoutId);
	}

	public static Layout addTypeLinkToURLLayout(
			long groupId, boolean privateLayout, String url)
		throws Exception {

		Layout layout = addTypePortletLayout(groupId, privateLayout);

		layout.setType(LayoutConstants.TYPE_URL);

		UnicodeProperties typeSettingsUnicodeProperties =
			layout.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.setProperty("url", url);

		return LayoutLocalServiceUtil.updateLayout(layout);
	}

	public static Layout addTypeLinkToURLLayout(long groupId, String url)
		throws Exception {

		return addTypeLinkToURLLayout(groupId, false, url);
	}

	public static Layout addTypeNodeLayout(long groupId, boolean privateLayout)
		throws Exception {

		Layout layout = addTypePortletLayout(groupId, privateLayout);

		layout.setType(LayoutConstants.TYPE_NODE);

		return LayoutLocalServiceUtil.updateLayout(layout);
	}

	public static Layout addTypePanelLayout(long groupId) throws Exception {
		Layout layout = addTypePortletLayout(groupId, false);

		layout.setType(LayoutConstants.TYPE_PANEL);

		return LayoutLocalServiceUtil.updateLayout(layout);
	}

	public static Layout addTypePortletLayout(Group group) throws Exception {
		return addTypePortletLayout(group.getGroupId());
	}

	public static Layout addTypePortletLayout(
			Group group, boolean privateLayout)
		throws Exception {

		return addTypePortletLayout(group.getGroupId(), privateLayout);
	}

	public static Layout addTypePortletLayout(
			Group group, boolean privateLayout, LayoutPrototype layoutPrototype,
			boolean linkEnabled)
		throws Exception {

		return addTypePortletLayout(
			group.getGroupId(), privateLayout, layoutPrototype, linkEnabled);
	}

	public static Layout addTypePortletLayout(
			Group group, boolean privateLayout,
			long layoutPageTemplateEntryGroupId,
			LayoutPrototype layoutPrototype, boolean linkEnabled)
		throws Exception {

		return addTypePortletLayout(
			group.getGroupId(),
			RandomTestUtil.randomString(
				LayoutFriendlyURLRandomizerBumper.INSTANCE,
				NumericStringRandomizerBumper.INSTANCE,
				UniqueStringRandomizerBumper.INSTANCE),
			privateLayout, layoutPageTemplateEntryGroupId, layoutPrototype,
			linkEnabled, false);
	}

	public static Layout addTypePortletLayout(
			Group group, long parentLayoutPlid)
		throws Exception {

		return addTypePortletLayout(group.getGroupId(), parentLayoutPlid);
	}

	public static Layout addTypePortletLayout(long groupId) throws Exception {
		return addTypePortletLayout(groupId, false);
	}

	public static Layout addTypePortletLayout(
			long groupId, boolean privateLayout)
		throws Exception {

		return addTypePortletLayout(groupId, privateLayout, null, false);
	}

	public static Layout addTypePortletLayout(
			long groupId, boolean privateLayout,
			LayoutPrototype layoutPrototype, boolean linkEnabled)
		throws Exception {

		return addTypePortletLayout(
			groupId,
			RandomTestUtil.randomString(
				LayoutFriendlyURLRandomizerBumper.INSTANCE,
				NumericStringRandomizerBumper.INSTANCE,
				UniqueStringRandomizerBumper.INSTANCE),
			privateLayout, layoutPrototype, linkEnabled);
	}

	public static Layout addTypePortletLayout(
			long groupId, boolean privateLayout, Map<Locale, String> nameMap,
			Map<Locale, String> friendlyURLMap)
		throws Exception {

		return addTypePortletLayout(
			groupId, privateLayout, nameMap, friendlyURLMap, false);
	}

	public static Layout addTypePortletLayout(
			long groupId, boolean privateLayout, Map<Locale, String> nameMap,
			Map<Locale, String> friendlyURLMap, boolean hidden)
		throws Exception {

		return addTypePortletLayout(
			groupId, privateLayout, nameMap, nameMap,
			new HashMap<Locale, String>(), new HashMap<Locale, String>(),
			new HashMap<Locale, String>(), StringPool.BLANK, friendlyURLMap,
			hidden);
	}

	public static Layout addTypePortletLayout(
			long groupId, boolean privateLayout, Map<Locale, String> nameMap,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			Map<Locale, String> keywordsMap, Map<Locale, String> robotsMap,
			String typeSettings, Map<Locale, String> friendlyURLMap,
			boolean hidden)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		return LayoutLocalServiceUtil.addLayout(
			null, serviceContext.getUserId(), groupId, privateLayout,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, nameMap, titleMap,
			descriptionMap, keywordsMap, robotsMap,
			LayoutConstants.TYPE_PORTLET, typeSettings, hidden, friendlyURLMap,
			serviceContext);
	}

	public static Layout addTypePortletLayout(
			long groupId, long parentLayoutPlid)
		throws Exception {

		Layout layout = addTypePortletLayout(groupId, false);

		LayoutLocalServiceUtil.updateParentLayoutId(
			layout.getPlid(), parentLayoutPlid);

		return LayoutLocalServiceUtil.fetchLayout(layout.getPlid());
	}

	public static Layout addTypePortletLayout(long groupId, String typeSettings)
		throws Exception {

		return addTypePortletLayout(
			groupId, false, RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(), typeSettings,
			new HashMap<Locale, String>(), false);
	}

	public static Layout addTypePortletLayout(
			long groupId, String name, boolean privateLayout)
		throws Exception {

		return addTypePortletLayout(groupId, name, privateLayout, null, false);
	}

	public static Layout addTypePortletLayout(
			long groupId, String name, boolean privateLayout,
			LayoutPrototype layoutPrototype, boolean linkEnabled)
		throws Exception {

		return addTypePortletLayout(
			groupId, name, privateLayout, layoutPrototype, linkEnabled, false);
	}

	public static Layout addTypePortletLayout(
			long groupId, String name, boolean privateLayout,
			LayoutPrototype layoutPrototype, boolean linkEnabled,
			boolean hidden)
		throws Exception {

		return addTypePortletLayout(
			groupId, name, privateLayout, groupId, layoutPrototype, linkEnabled,
			hidden);
	}

	public static Layout addTypePortletLayout(
			long groupId, String name, boolean privateLayout,
			long layoutPageTemplateEntryGroupId,
			LayoutPrototype layoutPrototype, boolean linkEnabled,
			boolean hidden)
		throws Exception {

		String friendlyURL =
			StringPool.SLASH + FriendlyURLNormalizerUtil.normalize(name);

		try {
			return LayoutLocalServiceUtil.getFriendlyURLLayout(
				groupId, false, friendlyURL);
		}
		catch (NoSuchLayoutException noSuchLayoutException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchLayoutException);
			}
		}

		Group group = GroupLocalServiceUtil.getGroup(groupId);

		User user = UserTestUtil.getAdminUser(group.getCompanyId());

		String description = "This is a test page.";

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group, user.getUserId());

		if (layoutPrototype != null) {
			LayoutPageTemplateEntry layoutPageTemplateEntry =
				LayoutPageTemplateEntryLocalServiceUtil.
					getFirstLayoutPageTemplateEntry(
						layoutPrototype.getLayoutPrototypeId());

			layoutPageTemplateEntry.setGroupId(layoutPageTemplateEntryGroupId);

			layoutPageTemplateEntry =
				LayoutPageTemplateEntryLocalServiceUtil.
					updateLayoutPageTemplateEntry(layoutPageTemplateEntry);

			serviceContext.setAttribute(
				"portletLayoutPageTemplateEntryERC",
				layoutPageTemplateEntry.getExternalReferenceCode());

			serviceContext.setAttribute(
				"portletLayoutPageTemplateEntryLinkEnabled", linkEnabled);
			serviceContext.setAttribute(
				"portletLayoutPageTemplateEntryScopeERC",
				ScopeUtil.getItemScopeExternalReferenceCode(
					layoutPageTemplateEntryGroupId, groupId));
		}

		return LayoutLocalServiceUtil.addLayout(
			null, user.getUserId(), groupId, privateLayout,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, name, null, description,
			LayoutConstants.TYPE_PORTLET, hidden, friendlyURL, serviceContext);
	}

	public static Layout addTypePortletLayout(
			long groupId, String name, long parentLayoutPlid)
		throws Exception {

		Layout layout = addTypePortletLayout(groupId, name, false);

		LayoutLocalServiceUtil.updateParentLayoutId(
			layout.getPlid(), parentLayoutPlid);

		return LayoutLocalServiceUtil.fetchLayout(layout.getPlid());
	}

	public static String getLayoutTemplateId(Layout layout) {
		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		return layoutTypePortlet.getLayoutTemplateId();
	}

	public static PortletPreferences getPortletPreferences(
			Layout layout, String portletId)
		throws Exception {

		return PortletPreferencesFactoryUtil.getPortletSetup(
			layout, portletId, null);
	}

	public static PortletPreferences getPortletPreferences(
			long plid, String portletId)
		throws Exception {

		return getPortletPreferences(
			LayoutLocalServiceUtil.getLayout(plid), portletId);
	}

	public static List<Portlet> getPortlets(Layout layout) throws Exception {
		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		return layoutTypePortlet.getPortlets();
	}

	public static boolean isLayoutColumnCustomizable(
		Layout layout, String columnId) {

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		return layoutTypePortlet.isColumnCustomizable(columnId);
	}

	public static Layout updateFriendlyURL(
			Layout layout, Map<Locale, String> friendlyURLMap)
		throws Exception {

		return LayoutLocalServiceUtil.updateLayout(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			layout.getParentLayoutId(), layout.getNameMap(),
			layout.getTitleMap(), layout.getDescriptionMap(),
			layout.getKeywordsMap(), layout.getRobotsMap(), layout.getType(),
			layout.isHidden(), friendlyURLMap, layout.getIconImage(), null,
			layout.getStyleBookEntryERC(), layout.getStyleBookEntryScopeERC(),
			layout.getFaviconFileEntryERC(),
			layout.getFaviconFileEntryScopeERC(),
			layout.getMasterLayoutPageTemplateEntryERC(),
			ServiceContextTestUtil.getServiceContext(
				layout.getGroupId(), TestPropsValues.getUserId()));
	}

	public static Layout updateLayoutColumnCustomizable(
			Layout layout, String columnId, boolean customizable)
		throws Exception {

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		layoutTypePortlet.setTypeSettingsProperty(
			CustomizedPages.namespaceColumnId(columnId),
			String.valueOf(customizable));
		layoutTypePortlet.setUpdatePermission(customizable);

		return LayoutServiceUtil.updateTypeSettings(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			layout.getTypeSettings());
	}

	public static Layout updateLayoutPortletPreference(
			Layout layout, String portletId, String portletPreferenceName,
			String portletPreferenceValue)
		throws Exception {

		PortletPreferences layoutPortletPreferences = getPortletPreferences(
			layout, portletId);

		layoutPortletPreferences.setValue(
			portletPreferenceName, portletPreferenceValue);

		layoutPortletPreferences.store();

		return LayoutLocalServiceUtil.getLayout(layout.getPlid());
	}

	public static Layout updateLayoutPortletPreferences(
			Layout layout, String portletId,
			Map<String, String> portletPreferences)
		throws Exception {

		PortletPreferences layoutPortletPreferences = getPortletPreferences(
			layout, portletId);

		for (Map.Entry<String, String> entry : portletPreferences.entrySet()) {
			layoutPortletPreferences.setValue(entry.getKey(), entry.getValue());
		}

		layoutPortletPreferences.store();

		return LayoutLocalServiceUtil.getLayout(layout.getPlid());
	}

	public static Layout updateLayoutTemplateId(
			Layout layout, String layoutTemplateId)
		throws Exception {

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		User user = UserTestUtil.getAdminUser(layout.getCompanyId());

		layoutTypePortlet.setLayoutTemplateId(
			user.getUserId(), layoutTemplateId);

		return LayoutServiceUtil.updateTypeSettings(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			layout.getTypeSettings());
	}

	public static Layout updateMasterLayoutPageTemplateEntryERC(
		Layout layout, String masterLayoutPageTemplateEntryERC,
		String masterLayoutPageTemplateEntryScopeERC) {

		layout.setMasterLayoutPageTemplateEntryERC(
			masterLayoutPageTemplateEntryERC);
		layout.setMasterLayoutPageTemplateEntryScopeERC(
			masterLayoutPageTemplateEntryScopeERC);

		return LayoutLocalServiceUtil.updateLayout(layout);
	}

	private static final Log _log = LogFactoryUtil.getLog(LayoutTestUtil.class);

}