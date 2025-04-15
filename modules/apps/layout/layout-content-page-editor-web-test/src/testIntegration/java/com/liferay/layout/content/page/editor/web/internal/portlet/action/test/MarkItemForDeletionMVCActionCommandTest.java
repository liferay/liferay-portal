/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.structure.DeletedLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class MarkItemForDeletionMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypeContentLayout(_group);
	}

	@Test
	public void testMarkMultipleItemForDeletion() throws Exception {
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest();

		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		LayoutStructureItem containerStyledLayoutStructureItem1 =
			layoutStructure.addContainerStyledLayoutStructureItem(
				rootLayoutStructureItem.getItemId(), 0);

		LayoutStructureItem rowStyledLayoutStructureItem1 =
			layoutStructure.addRowStyledLayoutStructureItem(
				containerStyledLayoutStructureItem1.getItemId(), 0, 1);

		LayoutStructureItem columnLayoutStructureItem1 =
			layoutStructure.addColumnLayoutStructureItem(
				rowStyledLayoutStructureItem1.getItemId(), 0);

		FragmentEntryLink fragmentEntryLink1 = _addFragmentEntryLink();

		layoutStructure.addFragmentStyledLayoutStructureItem(
			fragmentEntryLink1.getFragmentEntryLinkId(),
			columnLayoutStructureItem1.getItemId(), 0);

		LayoutStructureItem containerStyledLayoutStructureItem2 =
			layoutStructure.addContainerStyledLayoutStructureItem(
				rootLayoutStructureItem.getItemId(), 0);

		LayoutStructureItem rowStyledLayoutStructureItem2 =
			layoutStructure.addRowStyledLayoutStructureItem(
				containerStyledLayoutStructureItem2.getItemId(), 0, 1);

		LayoutStructureItem columnLayoutStructureItem2 =
			layoutStructure.addColumnLayoutStructureItem(
				rowStyledLayoutStructureItem2.getItemId(), 0);

		FragmentEntryLink fragmentEntryLink2 = _addFragmentEntryLink();

		layoutStructure.addFragmentStyledLayoutStructureItem(
			fragmentEntryLink2.getFragmentEntryLinkId(),
			columnLayoutStructureItem2.getItemId(), 0);

		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				_group.getGroupId(), _layout.getPlid(),
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(_layout.getPlid()),
				layoutStructure.toString());

		String[] itemIds = {
			columnLayoutStructureItem1.getItemId(),
			rowStyledLayoutStructureItem2.getItemId()
		};

		mockLiferayPortletActionRequest.setParameter("itemIds", itemIds);

		JSONObject jsonObject = ReflectionTestUtil.invoke(
			_mvcActionCommand, "doTransactionalCommand",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		JSONObject layoutDataJSONObject = jsonObject.getJSONObject(
			"layoutData");

		layoutStructure = LayoutStructure.of(layoutDataJSONObject.toString());

		List<DeletedLayoutStructureItem> deletedLayoutStructureItems =
			layoutStructure.getDeletedLayoutStructureItems();

		Assert.assertEquals(
			deletedLayoutStructureItems.toString(), 2,
			deletedLayoutStructureItems.size());
		Assert.assertTrue(
			ArrayUtil.containsAll(
				itemIds,
				TransformUtil.transformToArray(
					deletedLayoutStructureItems,
					DeletedLayoutStructureItem::getItemId, String.class)));

		fragmentEntryLink1 =
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				fragmentEntryLink1.getFragmentEntryLinkId());

		Assert.assertTrue(fragmentEntryLink1.isDeleted());

		fragmentEntryLink2 =
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				fragmentEntryLink2.getFragmentEntryLinkId());

		Assert.assertTrue(fragmentEntryLink2.isDeleted());
	}

	private FragmentEntryLink _addFragmentEntryLink() throws Exception {
		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
				StringUtil.randomString(), StringUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), false, "{fieldSets: []}", null,
				0, false, false, FragmentConstants.TYPE_COMPONENT, null,
				WorkflowConstants.STATUS_APPROVED,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		return _fragmentEntryLinkLocalService.addFragmentEntryLink(
			null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
			fragmentEntry.getFragmentEntryId(),
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_layout.getPlid()),
			_layout.getPlid(), fragmentEntry.getCss(), fragmentEntry.getHtml(),
			fragmentEntry.getJs(), fragmentEntry.getConfiguration(), null,
			StringPool.BLANK, 0, null, fragmentEntry.getType(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	private MockLiferayPortletActionRequest
			_getMockLiferayPortletActionRequest()
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		mockLiferayPortletActionRequest.addParameter(
			"groupId", String.valueOf(_group.getGroupId()));
		mockLiferayPortletActionRequest.addParameter(
			"segmentsExperienceId",
			String.valueOf(
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(_layout.getPlid())));

		return mockLiferayPortletActionRequest;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.fetchCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLayout(_layout);
		themeDisplay.setLayoutSet(_layout.getLayoutSet());
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setPlid(_layout.getPlid());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject(
		filter = "mvc.command.name=/layout_content_page_editor/mark_item_for_deletion"
	)
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}