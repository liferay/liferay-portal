/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.service.StagingLocalServiceUtil;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutRevision;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutRevisionLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.service.impl.LayoutRevisionLocalServiceImpl;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Roberto Díaz
 */
@RunWith(Arquillian.class)
public class StagingLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_user = TestPropsValues.getUser();

		UserTestUtil.setUser(_user);
	}

	@Test
	public void testBranchingLayoutLayoutUpdate() throws Exception {
		Group group = GroupTestUtil.addGroup();

		LayoutTestUtil.addTypePortletLayout(group);

		Map<Locale, String> nameMap = HashMapBuilder.put(
			LocaleUtil.getSiteDefault(), RandomTestUtil.randomString()
		).build();

		try {
			StagingLocalServiceUtil.enableLocalStaging(
				_user.getUserId(), group, true, true, new ServiceContext());

			Group stagingGroup = group.getStagingGroup();

			List<Layout> stagingLayouts = _layoutLocalService.getLayouts(
				stagingGroup.getGroupId(), false);

			Layout stagingLayout = stagingLayouts.get(0);

			stagingLayout = _layoutLocalService.updateLayout(
				stagingLayout.getGroupId(), stagingLayout.isPrivateLayout(),
				stagingLayout.getLayoutId(), stagingLayout.getParentLayoutId(),
				nameMap, stagingLayout.getTitleMap(),
				stagingLayout.getDescriptionMap(),
				stagingLayout.getKeywordsMap(), stagingLayout.getRobotsMap(),
				stagingLayout.getType(), stagingLayout.isHidden(),
				stagingLayout.getFriendlyURLMap(), false, null,
				stagingLayout.getStyleBookEntryERC(),
				stagingLayout.getFaviconFileEntryId(),
				stagingLayout.getMasterLayoutPlid(), new ServiceContext());

			stagingLayout = _layoutLocalService.updateLayout(
				stagingLayout.getGroupId(), stagingLayout.isPrivateLayout(),
				stagingLayout.getLayoutId(), stagingLayout.getParentLayoutId(),
				stagingLayout.getNameMap(),
				HashMapBuilder.put(
					LocaleUtil.getSiteDefault(), RandomTestUtil.randomString()
				).build(),
				stagingLayout.getDescriptionMap(),
				stagingLayout.getKeywordsMap(), stagingLayout.getRobotsMap(),
				stagingLayout.getType(), stagingLayout.isHidden(),
				stagingLayout.getFriendlyURLMap(), false, null,
				stagingLayout.getStyleBookEntryERC(),
				stagingLayout.getFaviconFileEntryId(),
				stagingLayout.getMasterLayoutPlid(), new ServiceContext());

			Map<Locale, String> layoutNameMap = stagingLayout.getNameMap();

			Assert.assertEquals(
				nameMap.get(LocaleUtil.getSiteDefault()),
				layoutNameMap.get(LocaleUtil.getSiteDefault()));
		}
		finally {
			GroupLocalServiceUtil.deleteGroup(group.getGroupId());
		}
	}

	@Test
	public void testBranchingLayoutPortletRemoval() throws Exception {
		Group group = GroupTestUtil.addGroup();

		Layout layout = LayoutTestUtil.addTypePortletLayout(group);

		UnicodeProperties unicodeProperties =
			layout.getTypeSettingsProperties();

		String portletId = PortletIdCodec.encode(
			JournalPortletKeys.JOURNAL, PortletIdCodec.generateInstanceId());

		PortletPreferencesFactoryUtil.getLayoutPortletSetup(layout, portletId);

		unicodeProperties.setProperty("column-1", portletId);

		layout.setTypeSettingsProperties(unicodeProperties);

		_layoutLocalService.updateLayout(layout);

		try {
			StagingLocalServiceUtil.enableLocalStaging(
				_user.getUserId(), group, true, true, new ServiceContext());

			Group stagingGroup = group.getStagingGroup();

			List<Layout> stagingLayouts = _layoutLocalService.getLayouts(
				stagingGroup.getGroupId(), false);

			Layout stagingLayout = stagingLayouts.get(0);

			List<LayoutRevision> layoutRevisions =
				LayoutRevisionLocalServiceUtil.getLayoutRevisions(
					stagingLayout.getPlid());

			LayoutRevision layoutRevision = layoutRevisions.get(0);

			_portletLocalService.deletePortlet(
				stagingGroup.getCompanyId(), portletId,
				layoutRevision.getPlid());

			List<PortletPreferences> portletPreferences =
				PortletPreferencesLocalServiceUtil.getPortletPreferences(
					layoutRevision.getPlid(), portletId);

			Assert.assertFalse(
				portletPreferences.toString(), portletPreferences.isEmpty());

			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			String[] removePortletIdsArray =
				(String[])serviceContext.getAttribute("removePortletIds");

			Assert.assertArrayEquals(
				new String[] {portletId}, removePortletIdsArray);
		}
		finally {
			GroupLocalServiceUtil.deleteGroup(group.getGroupId());
		}
	}

	@Test
	public void testBranchingLayoutPortletRemovalLayoutUpdate()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		Layout layout = LayoutTestUtil.addTypePortletLayout(group);

		UnicodeProperties unicodeProperties =
			layout.getTypeSettingsProperties();

		String portletId = PortletIdCodec.encode(
			JournalPortletKeys.JOURNAL, PortletIdCodec.generateInstanceId());

		PortletPreferencesFactoryUtil.getLayoutPortletSetup(layout, portletId);

		unicodeProperties.setProperty("column-1", portletId);

		layout.setTypeSettingsProperties(unicodeProperties);

		_layoutLocalService.updateLayout(layout);

		try {
			StagingLocalServiceUtil.enableLocalStaging(
				_user.getUserId(), group, true, true, new ServiceContext());

			Group stagingGroup = group.getStagingGroup();

			List<Layout> stagingLayouts = _layoutLocalService.getLayouts(
				stagingGroup.getGroupId(), false);

			Layout stagingLayout = stagingLayouts.get(0);

			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			serviceContext.setAttribute(
				"removePortletIds", new String[] {portletId});
			serviceContext.setCommand("delete");
			serviceContext.setWorkflowAction(
				WorkflowConstants.ACTION_SAVE_DRAFT);

			ThreadLocal<Long> layoutRevisionIdThreadLocal =
				ReflectionTestUtil.getFieldValue(
					LayoutRevisionLocalServiceImpl.class, "_layoutRevisionId");

			layoutRevisionIdThreadLocal.set(0L);

			UnicodeProperties updatedUnicodeProperties =
				stagingLayout.getTypeSettingsProperties();

			updatedUnicodeProperties.remove("column-1");

			_layoutLocalService.updateLayout(
				stagingGroup.getGroupId(), stagingLayout.isPrivateLayout(),
				stagingLayout.getLayoutId(),
				updatedUnicodeProperties.toString());

			LayoutRevision lastLayoutRevision =
				LayoutRevisionLocalServiceUtil.fetchLastLayoutRevision(
					stagingLayout.getPlid(), false);

			List<PortletPreferences> portletPreferences =
				PortletPreferencesLocalServiceUtil.getPortletPreferences(
					lastLayoutRevision.getLayoutRevisionId(), portletId);

			Assert.assertTrue(
				portletPreferences.toString(), portletPreferences.isEmpty());
		}
		finally {
			GroupLocalServiceUtil.deleteGroup(group.getGroupId());
		}
	}

	@Test
	public void testBranchingLayoutPortletRemovalLayoutUpdateNoContextParam()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		Layout layout = LayoutTestUtil.addTypePortletLayout(group);

		UnicodeProperties unicodeProperties =
			layout.getTypeSettingsProperties();

		String portletId = PortletIdCodec.encode(
			JournalPortletKeys.JOURNAL, PortletIdCodec.generateInstanceId());

		PortletPreferencesFactoryUtil.getLayoutPortletSetup(layout, portletId);

		unicodeProperties.setProperty("column-1", portletId);

		layout.setTypeSettingsProperties(unicodeProperties);

		_layoutLocalService.updateLayout(layout);

		try {
			StagingLocalServiceUtil.enableLocalStaging(
				_user.getUserId(), group, true, true, new ServiceContext());

			Group stagingGroup = group.getStagingGroup();

			List<Layout> stagingLayouts = _layoutLocalService.getLayouts(
				stagingGroup.getGroupId(), false);

			Layout stagingLayout = stagingLayouts.get(0);

			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			serviceContext.setCommand("delete");
			serviceContext.setWorkflowAction(
				WorkflowConstants.ACTION_SAVE_DRAFT);

			ThreadLocal<Long> layoutRevisionIdThreadLocal =
				ReflectionTestUtil.getFieldValue(
					LayoutRevisionLocalServiceImpl.class, "_layoutRevisionId");

			layoutRevisionIdThreadLocal.set(0L);

			UnicodeProperties updatedUnicodeProperties =
				stagingLayout.getTypeSettingsProperties();

			updatedUnicodeProperties.remove("column-1");

			_layoutLocalService.updateLayout(
				stagingGroup.getGroupId(), stagingLayout.isPrivateLayout(),
				stagingLayout.getLayoutId(),
				updatedUnicodeProperties.toString());

			LayoutRevision lastLayoutRevision =
				LayoutRevisionLocalServiceUtil.fetchLastLayoutRevision(
					stagingLayout.getPlid(), false);

			List<PortletPreferences> portletPreferences =
				PortletPreferencesLocalServiceUtil.getPortletPreferences(
					lastLayoutRevision.getLayoutRevisionId(), portletId);

			Assert.assertFalse(
				portletPreferences.toString(), portletPreferences.isEmpty());
		}
		finally {
			GroupLocalServiceUtil.deleteGroup(group.getGroupId());
		}
	}

	@Test
	public void testDisableStaging() throws Exception {
		Group group = GroupTestUtil.addGroup();

		try {
			StagingLocalServiceUtil.enableLocalStaging(
				_user.getUserId(), group, false, false, new ServiceContext());

			StagingLocalServiceUtil.disableStaging(group, new ServiceContext());

			group = GroupLocalServiceUtil.getGroup(group.getGroupId());

			Assert.assertFalse(group.hasStagingGroup());
		}
		finally {
			GroupLocalServiceUtil.deleteGroup(group.getGroupId());
		}
	}

	@Test
	public void testDisableStagingWithParentGroup() throws Exception {
		Group parentGroup = GroupTestUtil.addGroup();

		Group childGroup = GroupTestUtil.addGroup(parentGroup.getGroupId());

		try {
			StagingLocalServiceUtil.enableLocalStaging(
				_user.getUserId(), childGroup, false, false,
				new ServiceContext());

			StagingLocalServiceUtil.disableStaging(
				childGroup, new ServiceContext());

			childGroup = GroupLocalServiceUtil.getGroup(
				childGroup.getGroupId());

			Assert.assertFalse(childGroup.hasStagingGroup());
		}
		finally {
			GroupLocalServiceUtil.deleteGroup(childGroup.getGroupId());

			GroupLocalServiceUtil.deleteGroup(parentGroup.getGroupId());
		}
	}

	@Test
	public void testDisableStagingWithStagedChildGroup() throws Exception {
		Group parentGroup = GroupTestUtil.addGroup();

		Group childGroup = GroupTestUtil.addGroup(parentGroup.getGroupId());

		try {
			StagingLocalServiceUtil.enableLocalStaging(
				_user.getUserId(), parentGroup, false, false,
				new ServiceContext());

			StagingLocalServiceUtil.enableLocalStaging(
				_user.getUserId(), childGroup, false, false,
				new ServiceContext());

			StagingLocalServiceUtil.disableStaging(
				parentGroup, new ServiceContext());

			childGroup = GroupLocalServiceUtil.getGroup(
				childGroup.getGroupId());

			Group childGroupStagingGroup = childGroup.getStagingGroup();

			Assert.assertEquals(
				childGroupStagingGroup.getParentGroupId(),
				childGroup.getParentGroupId());
		}
		finally {
			GroupLocalServiceUtil.deleteGroup(childGroup.getGroupId());

			GroupLocalServiceUtil.deleteGroup(parentGroup.getGroupId());
		}
	}

	@Test
	public void testEnableLocalStaging() throws Exception {
		Group group = GroupTestUtil.addGroup();

		try {
			StagingLocalServiceUtil.enableLocalStaging(
				_user.getUserId(), group, false, false, new ServiceContext());

			group = GroupLocalServiceUtil.getGroup(group.getGroupId());

			Assert.assertTrue(group.hasStagingGroup());
		}
		finally {
			GroupLocalServiceUtil.deleteGroup(group.getGroupId());
		}
	}

	@Test
	public void testEnableLocalStagingWithParentGroup() throws Exception {
		Group parentGroup = GroupTestUtil.addGroup();

		Group group = GroupTestUtil.addGroup(parentGroup.getGroupId());

		try {
			StagingLocalServiceUtil.enableLocalStaging(
				_user.getUserId(), group, false, false, new ServiceContext());

			group = GroupLocalServiceUtil.getGroup(group.getGroupId());

			Group groupStagingGroup = group.getStagingGroup();

			Assert.assertEquals(
				parentGroup.getGroupId(), group.getParentGroupId());

			Assert.assertEquals(
				parentGroup.getGroupId(), groupStagingGroup.getParentGroupId());
		}
		finally {
			GroupLocalServiceUtil.deleteGroup(group.getGroupId());

			GroupLocalServiceUtil.deleteGroup(parentGroup.getGroupId());
		}
	}

	@Test
	public void testEnableLocalStagingWithStagedChildGroup() throws Exception {
		Group parentGroup = GroupTestUtil.addGroup();

		Group childGroup = GroupTestUtil.addGroup(parentGroup.getGroupId());

		try {
			StagingLocalServiceUtil.enableLocalStaging(
				_user.getUserId(), childGroup, false, false,
				new ServiceContext());

			StagingLocalServiceUtil.enableLocalStaging(
				_user.getUserId(), parentGroup, false, false,
				new ServiceContext());

			childGroup = GroupLocalServiceUtil.getGroup(
				childGroup.getGroupId());

			Group childGroupStagingGroup = childGroup.getStagingGroup();

			Assert.assertEquals(
				parentGroup.getGroupId(), childGroup.getParentGroupId());

			Assert.assertEquals(
				parentGroup.getGroupId(),
				childGroupStagingGroup.getParentGroupId());
		}
		finally {
			GroupLocalServiceUtil.deleteGroup(childGroup.getGroupId());

			GroupLocalServiceUtil.deleteGroup(parentGroup.getGroupId());
		}
	}

	@Test
	public void testEnableLocalStagingWithStagedParentGroup() throws Exception {
		Group parentGroup = GroupTestUtil.addGroup();

		Group childGroup = GroupTestUtil.addGroup(parentGroup.getGroupId());

		try {
			StagingLocalServiceUtil.enableLocalStaging(
				_user.getUserId(), parentGroup, false, false,
				new ServiceContext());

			StagingLocalServiceUtil.enableLocalStaging(
				_user.getUserId(), childGroup, false, false,
				new ServiceContext());

			childGroup = GroupLocalServiceUtil.getGroup(
				childGroup.getGroupId());

			Group childGroupStagingGroup = childGroup.getStagingGroup();

			Assert.assertEquals(
				parentGroup.getGroupId(), childGroup.getParentGroupId());

			Assert.assertEquals(
				parentGroup.getGroupId(),
				childGroupStagingGroup.getParentGroupId());
		}
		finally {
			GroupLocalServiceUtil.deleteGroup(childGroup.getGroupId());

			GroupLocalServiceUtil.deleteGroup(parentGroup.getGroupId());
		}
	}

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private PortletLocalService _portletLocalService;

	private User _user;

}