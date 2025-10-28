/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.test.util.LayoutPageTemplateTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Kyle Miho
 */
@RunWith(Arquillian.class)
public class GroupModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testDeletingGroupDeletesFragmentCollections() throws Exception {
		Group group = GroupTestUtil.addGroup();

		FragmentCollection fragmentCollection = _addFragmentCollection(
			group.getGroupId());

		_groupLocalService.deleteGroup(group);

		Assert.assertNull(
			_fragmentCollectionLocalService.fetchFragmentCollection(
				fragmentCollection.getFragmentCollectionId()));
	}

	@Test
	public void testDeletingGroupDeletesFragmentEntryLinks() throws Exception {
		Group group = GroupTestUtil.addGroup();

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_addLayoutPageTemplateEntry(group.getGroupId());

		FragmentEntryLink fragmentEntryLink = _addFragmentEntryLink(
			group.getGroupId(), layoutPageTemplateEntry.getPlid());

		_groupLocalService.deleteGroup(group);

		Assert.assertNull(
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				fragmentEntryLink.getFragmentEntryLinkId()));
	}

	@Test
	public void testDeletingGroupDeletesLayoutPageTemplateCollections()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_addLayoutPageTemplateCollection(group.getGroupId());

		_groupLocalService.deleteGroup(group);

		Assert.assertNull(
			_layoutPageTemplateCollectionLocalService.
				fetchLayoutPageTemplateCollection(
					layoutPageTemplateCollection.
						getLayoutPageTemplateCollectionId()));
	}

	@Test
	public void testDeletingGroupDeletesLayoutPageTemplateEntries()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		List<LayoutPageTemplateEntry> originalLayoutPageTemplateEntries =
			_layoutPageTemplateEntryLocalService.getLayoutPageTemplateEntries(
				group.getGroupId());

		_addLayoutPageTemplateEntry(group.getGroupId());
		_addLayoutPageTemplateEntry(group.getGroupId());
		_addLayoutPageTemplateEntry(group.getGroupId());

		_groupLocalService.deleteGroup(group);

		List<LayoutPageTemplateEntry> actualLayoutPageTemplateEntries =
			_layoutPageTemplateEntryLocalService.getLayoutPageTemplateEntries(
				group.getGroupId());

		Assert.assertEquals(
			originalLayoutPageTemplateEntries.toString(),
			originalLayoutPageTemplateEntries.size(),
			actualLayoutPageTemplateEntries.size());
	}

	private FragmentCollection _addFragmentCollection(long groupId)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				groupId, TestPropsValues.getUserId());

		return _fragmentCollectionLocalService.addFragmentCollection(
			null, TestPropsValues.getUserId(), groupId,
			RandomTestUtil.randomString(), StringPool.BLANK, serviceContext);
	}

	private FragmentEntryLink _addFragmentEntryLink(long groupId, long plid)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				groupId, TestPropsValues.getUserId());

		FragmentCollection fragmentCollection = _addFragmentCollection(groupId);

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(), groupId,
				fragmentCollection.getFragmentCollectionId(), null,
				RandomTestUtil.randomString(), StringPool.BLANK,
				RandomTestUtil.randomString(), StringPool.BLANK, false,
				StringPool.BLANK, null, 0, false, false,
				FragmentConstants.TYPE_SECTION, null,
				WorkflowConstants.STATUS_APPROVED, serviceContext);

		return _fragmentEntryLinkLocalService.addFragmentEntryLink(
			null, TestPropsValues.getUserId(), groupId, null,
			fragmentEntry.getExternalReferenceCode(),
			fragmentEntry.getScopeERC(),
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				plid),
			plid, fragmentEntry.getCss(), fragmentEntry.getHtml(),
			fragmentEntry.getJs(), fragmentEntry.getConfiguration(),
			StringPool.BLANK, StringPool.BLANK, 0, null,
			fragmentEntry.getType(), serviceContext);
	}

	private LayoutPageTemplateCollection _addLayoutPageTemplateCollection(
			long groupId)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				groupId, TestPropsValues.getUserId());

		return _layoutPageTemplateCollectionLocalService.
			addLayoutPageTemplateCollection(
				null, TestPropsValues.getUserId(), groupId,
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				null, RandomTestUtil.randomString(), StringPool.BLANK,
				LayoutPageTemplateCollectionTypeConstants.BASIC,
				serviceContext);
	}

	private LayoutPageTemplateEntry _addLayoutPageTemplateEntry(long groupId)
		throws Exception {

		return LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
			groupId, LayoutPageTemplateEntryTypeConstants.BASIC,
			WorkflowConstants.STATUS_DRAFT);
	}

	@Inject
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutPageTemplateCollectionLocalService
		_layoutPageTemplateCollectionLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}