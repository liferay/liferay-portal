/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.friendly.url.checker.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.friendly.url.checker.FriendlyURLPublicMappingChecker;
import com.liferay.friendly.url.checker.FriendlyURLPublicMappingConflict;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.layout.friendly.url.LayoutFriendlyURLEntryHelper;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.model.SiteFriendlyURL;
import com.liferay.site.service.SiteFriendlyURLLocalService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author David Truong
 */
@RunWith(Arquillian.class)
public class FriendlyURLPublicMappingCheckerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_originalVirtualHostsDefaultSiteName =
			ReflectionTestUtil.getAndSetFieldValue(
				PropsValues.class, "VIRTUAL_HOSTS_DEFAULT_SITE_NAME",
				GroupConstants.GUEST);

		_defaultGroup = _groupLocalService.getGroup(
			TestPropsValues.getCompanyId(),
			PropsValues.VIRTUAL_HOSTS_DEFAULT_SITE_NAME);
	}

	@After
	public void tearDown() {
		ReflectionTestUtil.setFieldValue(
			PropsValues.class, "VIRTUAL_HOSTS_DEFAULT_SITE_NAME",
			_originalVirtualHostsDefaultSiteName);
	}

	@Test
	public void testGetFriendlyURLPublicMappingConflictsForTypeCrossSite()
		throws Exception {

		String defaultLocaleFriendlyURL =
			StringPool.SLASH +
				StringUtil.toLowerCase(RandomTestUtil.randomString());
		String nondefaultGroupFriendlyURL =
			StringPool.SLASH +
				StringUtil.toLowerCase(RandomTestUtil.randomString());
		String nondefaultLocaleFriendlyURL =
			StringPool.SLASH +
				StringUtil.toLowerCase(RandomTestUtil.randomString());

		Group group1 = _addGroup();

		_addSiteFriendlyURL(
			group1, defaultLocaleFriendlyURL,
			_language.getLanguageId(LocaleUtil.US));
		_addSiteFriendlyURL(
			group1, nondefaultLocaleFriendlyURL,
			_language.getLanguageId(LocaleUtil.SPAIN));

		_addLayout(defaultLocaleFriendlyURL, _defaultGroup);

		_addLayoutWithFriendlyURLEntry(
			_language.getLanguageId(LocaleUtil.SPAIN),
			nondefaultLocaleFriendlyURL);

		Group group2 = _addGroup();

		_addSiteFriendlyURL(
			group2, nondefaultGroupFriendlyURL,
			_language.getLanguageId(LocaleUtil.US));

		_addLayout(nondefaultGroupFriendlyURL, group2);

		List<FriendlyURLPublicMappingConflict>
			friendlyURLPublicMappingConflicts =
				_getFriendlyURLPublicMappingConflicts();

		FriendlyURLPublicMappingConflict defaultLocaleConflict =
			_findFriendlyURLPublicMappingConflictByURL(
				friendlyURLPublicMappingConflicts, defaultLocaleFriendlyURL);

		Assert.assertNotNull(defaultLocaleConflict);
		Assert.assertEquals(
			FriendlyURLPublicMappingConflict.TYPE_CROSS_SITE,
			defaultLocaleConflict.getType());
		Assert.assertEquals(
			Long.valueOf(group1.getGroupId()),
			defaultLocaleConflict.getConflictingGroupId());

		FriendlyURLPublicMappingConflict nondefaultLocaleConflict =
			_findFriendlyURLPublicMappingConflictByURL(
				friendlyURLPublicMappingConflicts, nondefaultLocaleFriendlyURL);

		Assert.assertNotNull(nondefaultLocaleConflict);
		Assert.assertEquals(
			FriendlyURLPublicMappingConflict.TYPE_CROSS_SITE,
			nondefaultLocaleConflict.getType());
		Assert.assertEquals(
			Long.valueOf(group1.getGroupId()),
			nondefaultLocaleConflict.getConflictingGroupId());

		Assert.assertNull(
			friendlyURLPublicMappingConflicts.toString(),
			_findFriendlyURLPublicMappingConflictByURL(
				friendlyURLPublicMappingConflicts, nondefaultGroupFriendlyURL));
	}

	@Test
	public void testGetFriendlyURLPublicMappingConflictsForTypeReservedKeyword()
		throws Exception {

		_addSiteFriendlyURL(
			_addGroup(), PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING,
			_language.getLanguageId(LocaleUtil.SPAIN));

		_addLayoutWithFriendlyURLEntry(
			_language.getLanguageId(LocaleUtil.US),
			PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING);

		List<FriendlyURLPublicMappingConflict>
			friendlyURLPublicMappingConflicts = ListUtil.filter(
				_getFriendlyURLPublicMappingConflicts(),
				friendlyURLPublicMappingConflict -> Objects.equals(
					PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING,
					friendlyURLPublicMappingConflict.getFriendlyURL()));

		boolean hasGroupConflict = false;
		boolean hasLayoutConflict = false;

		for (FriendlyURLPublicMappingConflict friendlyURLPublicMappingConflict :
				friendlyURLPublicMappingConflicts) {

			if (!Objects.equals(
					FriendlyURLPublicMappingConflict.TYPE_RESERVED_KEYWORD,
					friendlyURLPublicMappingConflict.getType())) {

				continue;
			}

			String className = friendlyURLPublicMappingConflict.getClassName();

			if (Objects.equals(Group.class.getName(), className)) {
				hasGroupConflict = true;
			}
			else if (Objects.equals(Layout.class.getName(), className)) {
				hasLayoutConflict = true;
			}
		}

		Assert.assertTrue(
			friendlyURLPublicMappingConflicts.toString(), hasGroupConflict);
		Assert.assertTrue(
			friendlyURLPublicMappingConflicts.toString(), hasLayoutConflict);
	}

	@Test
	public void testGetFriendlyURLPublicMappingConflictsForTypeSelf()
		throws Exception {

		_addLayoutWithFriendlyURLEntry(
			_language.getLanguageId(LocaleUtil.US),
			_defaultGroup.getFriendlyURL());

		FriendlyURLPublicMappingConflict friendlyURLPublicMappingConflict =
			_findFriendlyURLPublicMappingConflictByURL(
				_getFriendlyURLPublicMappingConflicts(),
				_defaultGroup.getFriendlyURL());

		Assert.assertNotNull(friendlyURLPublicMappingConflict);
		Assert.assertEquals(
			FriendlyURLPublicMappingConflict.TYPE_SELF,
			friendlyURLPublicMappingConflict.getType());
		Assert.assertNull(
			friendlyURLPublicMappingConflict.getConflictingGroupId());
	}

	private Group _addGroup() throws Exception {
		Group group = GroupTestUtil.addGroup();

		_groups.add(group);

		return group;
	}

	private Layout _addLayout(String friendlyURL, Group group)
		throws Exception {

		Layout layout = _layoutLocalService.addLayout(
			null, TestPropsValues.getUserId(), group.getGroupId(), false,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
			RandomTestUtil.randomString(), StringPool.BLANK, StringPool.BLANK,
			LayoutConstants.TYPE_CONTENT, false, friendlyURL,
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId()));

		_layouts.add(layout);

		return layout;
	}

	private void _addLayoutWithFriendlyURLEntry(
			String languageId, String urlTitle)
		throws Exception {

		Layout layout = _addLayout(
			StringPool.SLASH + RandomTestUtil.randomString(), _defaultGroup);

		_friendlyURLEntries.add(
			_friendlyURLEntryLocalService.addFriendlyURLEntry(
				_defaultGroup.getGroupId(),
				_layoutFriendlyURLEntryHelper.getClassNameId(false),
				layout.getPlid(), RandomTestUtil.randomString(),
				Collections.singletonMap(languageId, urlTitle),
				_getServiceContext()));
	}

	private void _addSiteFriendlyURL(
			Group group, String friendlyURL, String languageId)
		throws Exception {

		SiteFriendlyURL siteFriendlyURL =
			_siteFriendlyURLLocalService.createSiteFriendlyURL(
				_counterLocalService.increment());

		siteFriendlyURL.setGroupId(group.getGroupId());
		siteFriendlyURL.setCompanyId(TestPropsValues.getCompanyId());
		siteFriendlyURL.setUserId(TestPropsValues.getUserId());
		siteFriendlyURL.setFriendlyURL(friendlyURL);
		siteFriendlyURL.setLanguageId(languageId);

		_siteFriendlyURLLocalService.addSiteFriendlyURL(siteFriendlyURL);
	}

	private FriendlyURLPublicMappingConflict
		_findFriendlyURLPublicMappingConflictByURL(
			List<FriendlyURLPublicMappingConflict>
				friendlyURLPublicMappingConflicts,
			String url) {

		for (FriendlyURLPublicMappingConflict friendlyURLPublicMappingConflict :
				friendlyURLPublicMappingConflicts) {

			if (url.equals(friendlyURLPublicMappingConflict.getFriendlyURL())) {
				return friendlyURLPublicMappingConflict;
			}
		}

		return null;
	}

	private List<FriendlyURLPublicMappingConflict>
			_getFriendlyURLPublicMappingConflicts()
		throws Exception {

		List<FriendlyURLPublicMappingConflict>
			friendlyURLPublicMappingConflicts =
				_friendlyURLPublicMappingChecker.
					getFriendlyURLPublicMappingConflicts(
						TestPropsValues.getCompanyId());

		Assert.assertNotNull(friendlyURLPublicMappingConflicts);

		return friendlyURLPublicMappingConflicts;
	}

	private ServiceContext _getServiceContext() throws Exception {
		return ServiceContextTestUtil.getServiceContext(
			_defaultGroup.getGroupId(), TestPropsValues.getUserId());
	}

	@Inject
	private CounterLocalService _counterLocalService;

	private Group _defaultGroup;

	@DeleteAfterTestRun
	private final List<FriendlyURLEntry> _friendlyURLEntries =
		new ArrayList<>();

	@Inject
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Inject
	private FriendlyURLPublicMappingChecker _friendlyURLPublicMappingChecker;

	@Inject
	private GroupLocalService _groupLocalService;

	@DeleteAfterTestRun
	private final List<Group> _groups = new ArrayList<>();

	@Inject
	private Language _language;

	@Inject
	private LayoutFriendlyURLEntryHelper _layoutFriendlyURLEntryHelper;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@DeleteAfterTestRun
	private final List<Layout> _layouts = new ArrayList<>();

	private String _originalVirtualHostsDefaultSiteName;

	@Inject
	private SiteFriendlyURLLocalService _siteFriendlyURLLocalService;

}