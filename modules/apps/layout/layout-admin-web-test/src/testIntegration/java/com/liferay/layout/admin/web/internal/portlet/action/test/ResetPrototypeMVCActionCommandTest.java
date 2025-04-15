/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.staging.MergeLayoutPrototypesThreadLocal;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentEntryLinkService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRel;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.structure.DeletedLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockActionResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.segments.service.SegmentsExperienceLocalServiceUtil;
import com.liferay.sites.kernel.util.Sites;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Víctor Galán
 */
@RunWith(Arquillian.class)
public class ResetPrototypeMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layoutSetPrototype = LayoutTestUtil.addLayoutSetPrototype(
			RandomTestUtil.randomString());

		_layoutSetPrototypeGroup = _layoutSetPrototype.getGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_layoutSetPrototypeGroup.getGroupId());

		MergeLayoutPrototypesThreadLocal.clearMergeComplete();

		_sites.updateLayoutSetPrototypesLinks(
			_group, _layoutSetPrototype.getLayoutSetPrototypeId(), 0, true,
			true);
	}

	@Test
	public void testResetLayoutPrototype() throws Exception {
		Layout layout = _layoutLocalService.addLayout(
			null, TestPropsValues.getUserId(),
			_layoutSetPrototypeGroup.getGroupId(), true,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
			RandomTestUtil.randomString(), null, null,
			LayoutConstants.TYPE_CONTENT, false, StringPool.BLANK,
			_serviceContext);

		Layout draftLayout = layout.fetchDraftLayout();

		FragmentEntry fragmentEntry = _addFragmentEntry();

		ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			null, fragmentEntry.getCss(), fragmentEntry.getConfiguration(),
			fragmentEntry.getFragmentEntryId(), fragmentEntry.getHtml(),
			fragmentEntry.getJs(), draftLayout,
			fragmentEntry.getFragmentEntryKey(), fragmentEntry.getType(), null,
			0,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid()));

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		_propagateChanges(_group);

		Layout groupPublishedLayout =
			_layoutLocalService.fetchLayoutByFriendlyURL(
				_group.getGroupId(), false, layout.getFriendlyURL());

		Assert.assertNotNull(groupPublishedLayout);

		groupPublishedLayout = _removeFragmentEntryPublishAndGetLayout(
			groupPublishedLayout);

		MergeLayoutPrototypesThreadLocal.setSkipMerge(false);

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "doProcessAction",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			_getMockLiferayPortletActionRequest(
				_getThemeDisplay(groupPublishedLayout)),
			new MockActionResponse());

		groupPublishedLayout = _layoutLocalService.getLayout(
			groupPublishedLayout.getPlid());

		long segmentsExperienceId =
			SegmentsExperienceLocalServiceUtil.fetchDefaultSegmentsExperienceId(
				groupPublishedLayout.getPlid());

		LayoutStructure layoutStructure = _getLayoutStructure(
			_group.getGroupId(), groupPublishedLayout, segmentsExperienceId);

		Map<Long, LayoutStructureItem> fragmentLayoutStructureItems =
			layoutStructure.getFragmentLayoutStructureItems();

		Assert.assertEquals(
			fragmentLayoutStructureItems.toString(), 1,
			fragmentLayoutStructureItems.size());
	}

	private FragmentEntry _addFragmentEntry() throws Exception {
		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.addFragmentCollection(
				null, TestPropsValues.getUserId(),
				_layoutSetPrototypeGroup.getGroupId(),
				RandomTestUtil.randomString(), StringPool.BLANK,
				_serviceContext);

		return _fragmentEntryLocalService.addFragmentEntry(
			null, TestPropsValues.getUserId(),
			_layoutSetPrototypeGroup.getGroupId(),
			fragmentCollection.getFragmentCollectionId(), null,
			RandomTestUtil.randomString(), StringPool.BLANK,
			"Fragment Entry HTML", StringPool.BLANK, false, null, null, 0,
			false, false, FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, _serviceContext);
	}

	private LayoutStructure _getLayoutStructure(
		long groupId, Layout layout, long segmentsExperienceId) {

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(groupId, layout.getPlid());

		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
			_layoutPageTemplateStructureRelLocalService.
				fetchLayoutPageTemplateStructureRel(
					layoutPageTemplateStructure.
						getLayoutPageTemplateStructureId(),
					segmentsExperienceId);

		return LayoutStructure.of(layoutPageTemplateStructureRel.getData());
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			ThemeDisplay themeDisplay)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockLiferayPortletActionRequest;
	}

	private ThemeDisplay _getThemeDisplay(Layout layout) throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setLayout(layout);
		themeDisplay.setLayoutSet(layout.getLayoutSet());
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setRealUser(TestPropsValues.getUser());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private void _propagateChanges(Group group) throws Exception {
		MergeLayoutPrototypesThreadLocal.clearMergeComplete();

		LayoutSet layoutSet = _layoutSetLocalService.getLayoutSet(
			group.getGroupId(), false);

		MergeLayoutPrototypesThreadLocal.setSkipMerge(false);

		_sites.mergeLayoutSetPrototypeLayouts(group, layoutSet);

		Thread.sleep(2000);

		LayoutSetPrototype layoutSetPrototype =
			_layoutSetPrototypeLocalService.
				getLayoutSetPrototypeByUuidAndCompanyId(
					layoutSet.getLayoutSetPrototypeUuid(),
					layoutSet.getCompanyId());

		LayoutSet layoutSetPrototypeLayoutSet =
			layoutSetPrototype.getLayoutSet();

		UnicodeProperties layoutSetPrototypeSettingsUnicodeProperties =
			layoutSetPrototypeLayoutSet.getSettingsProperties();

		int mergeFailCount = GetterUtil.getInteger(
			layoutSetPrototypeSettingsUnicodeProperties.getProperty(
				Sites.MERGE_FAIL_COUNT));

		Assert.assertEquals(0, mergeFailCount);
	}

	private Layout _removeFragmentEntryPublishAndGetLayout(Layout layout)
		throws Exception {

		Layout draftLayout = layout.fetchDraftLayout();

		Assert.assertNotNull(draftLayout);

		LayoutStructure layoutStructure = _getLayoutStructure(
			layout.getGroupId(), draftLayout,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid()));

		List<DeletedLayoutStructureItem> deletedLayoutStructureItems =
			layoutStructure.getDeletedLayoutStructureItems();

		Assert.assertEquals(
			deletedLayoutStructureItems.toString(), 0,
			deletedLayoutStructureItems.size());

		Map<Long, LayoutStructureItem> fragmentLayoutStructureItems =
			layoutStructure.getFragmentLayoutStructureItems();

		Assert.assertEquals(
			fragmentLayoutStructureItems.toString(), 1,
			fragmentLayoutStructureItems.size());

		List<String> deletedItemIds = new ArrayList<>();

		for (LayoutStructureItem layoutStructureItem :
				fragmentLayoutStructureItems.values()) {

			ContentLayoutTestUtil.markItemForDeletionFromLayout(
				layoutStructureItem.getItemId(), draftLayout, StringPool.BLANK);

			deletedItemIds.add(layoutStructureItem.getItemId());
		}

		draftLayout = _layoutLocalService.getLayout(draftLayout.getPlid());

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		layout = _layoutLocalService.getLayout(layout.getPlid());

		layoutStructure = _getLayoutStructure(
			layout.getGroupId(), layout,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layout.getPlid()));

		Assert.assertArrayEquals(
			deletedItemIds.toArray(new String[0]),
			TransformUtil.transformToArray(
				layoutStructure.getDeletedLayoutStructureItems(),
				deletedLayoutStructureItem ->
					deletedLayoutStructureItem.getItemId(),
				String.class));

		fragmentLayoutStructureItems =
			layoutStructure.getFragmentLayoutStructureItems();

		Assert.assertArrayEquals(
			deletedItemIds.toArray(new String[0]),
			TransformUtil.transformToArray(
				fragmentLayoutStructureItems.values(),
				layoutStructureItem -> layoutStructureItem.getItemId(),
				String.class));

		return layout;
	}

	@Inject
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Inject
	private FragmentEntryLinkService _fragmentEntryLinkService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject
	private LayoutPageTemplateStructureRelLocalService
		_layoutPageTemplateStructureRelLocalService;

	@Inject
	private LayoutSetLocalService _layoutSetLocalService;

	@DeleteAfterTestRun
	private LayoutSetPrototype _layoutSetPrototype;

	private Group _layoutSetPrototypeGroup;

	@Inject
	private LayoutSetPrototypeLocalService _layoutSetPrototypeLocalService;

	@Inject(filter = "mvc.command.name=/layout_admin/reset_prototype")
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private ServiceContext _serviceContext;

	@Inject
	private Sites _sites;

}