/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.provider.LayoutStructureProvider;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.constants.LayoutDataItemTypeConstants;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.layout.util.structure.RowStyledLayoutStructureItem;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.servlet.PortletServlet;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.portlet.ActionRequest;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
public class UnmarkItemsForDeletionMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_company = _companyLocalService.getCompany(_group.getCompanyId());

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		_layout = layout.fetchDraftLayout();

		_segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_layout.getPlid());
	}

	@Test
	public void testUnmarkItemsForDeletion() throws Exception {
		JSONObject addItemJSONObject = ContentLayoutTestUtil.addItemToLayout(
			"{}", LayoutDataItemTypeConstants.TYPE_CONTAINER, _layout,
			_layoutStructureProvider, _segmentsExperienceId);

		String containerItemId = addItemJSONObject.getString("addedItemId");

		String rowItemId = _addItemToLayout(
			LayoutDataItemTypeConstants.TYPE_ROW, containerItemId);

		ContentLayoutTestUtil.markItemForDeletionFromLayout(
			containerItemId, _layout, StringPool.BLANK);

		LayoutStructure layoutStructure =
			_layoutStructureProvider.getLayoutStructure(
				_layout.getPlid(), _segmentsExperienceId);

		Assert.assertTrue(
			layoutStructure.isItemMarkedForDeletion(containerItemId));

		Assert.assertTrue(layoutStructure.isItemMarkedForDeletion(rowItemId));

		JSONObject unmarkItemsForDeletionJSONObject = ReflectionTestUtil.invoke(
			_mvcActionCommand, "_unmarkItemsForDeletion",
			new Class<?>[] {ActionRequest.class},
			_getMockLiferayPortletActionRequest(
				JSONUtil.put(
					containerItemId
				).toString()));

		layoutStructure = _layoutStructureProvider.getLayoutStructure(
			_layout.getPlid(), _segmentsExperienceId);

		Assert.assertTrue(
			JSONUtil.equals(
				layoutStructure.toJSONObject(),
				unmarkItemsForDeletionJSONObject.getJSONObject("layoutData")));

		_assertExpectedLayoutStructureItem(
			layoutStructure.getMainItemId(), containerItemId, layoutStructure);

		_assertExpectedLayoutStructureItem(
			containerItemId, rowItemId, layoutStructure);
	}

	@Test
	public void testUnmarkItemsForDeletionRowColumns() throws Exception {
		JSONObject addItemJSONObject = ContentLayoutTestUtil.addItemToLayout(
			"{}", LayoutDataItemTypeConstants.TYPE_CONTAINER, _layout,
			_layoutStructureProvider, _segmentsExperienceId);

		String containerItemId = addItemJSONObject.getString("addedItemId");

		String rowItemId = _addItemToLayout(
			LayoutDataItemTypeConstants.TYPE_ROW, containerItemId);

		LayoutStructure layoutStructure =
			_layoutStructureProvider.getLayoutStructure(
				_layout.getPlid(), _segmentsExperienceId);

		RowStyledLayoutStructureItem rowStyledLayoutStructureItem =
			(RowStyledLayoutStructureItem)
				layoutStructure.getLayoutStructureItem(rowItemId);

		List<String> childrenItemIds =
			rowStyledLayoutStructureItem.getChildrenItemIds();

		Assert.assertEquals(
			childrenItemIds.toString(), 3, childrenItemIds.size());

		String[] originalChildrenItemIds = childrenItemIds.toArray(
			new String[0]);

		layoutStructure.updateRowColumnsLayoutStructureItem(rowItemId, 1);

		Assert.assertEquals(
			childrenItemIds.toString(), 1, childrenItemIds.size());

		Assert.assertEquals(originalChildrenItemIds[0], childrenItemIds.get(0));

		JSONObject unmarkItemsForDeletionJSONObject = ReflectionTestUtil.invoke(
			_mvcActionCommand, "_unmarkItemsForDeletion",
			new Class<?>[] {ActionRequest.class},
			_getMockLiferayPortletActionRequest(
				JSONUtil.putAll(
					originalChildrenItemIds[1], originalChildrenItemIds[2]
				).toString()));

		layoutStructure = _layoutStructureProvider.getLayoutStructure(
			_layout.getPlid(), _segmentsExperienceId);

		Assert.assertTrue(
			JSONUtil.equals(
				layoutStructure.toJSONObject(),
				unmarkItemsForDeletionJSONObject.getJSONObject("layoutData")));

		rowStyledLayoutStructureItem =
			(RowStyledLayoutStructureItem)
				layoutStructure.getLayoutStructureItem(
					rowStyledLayoutStructureItem.getItemId());

		childrenItemIds = rowStyledLayoutStructureItem.getChildrenItemIds();

		Assert.assertArrayEquals(
			originalChildrenItemIds, childrenItemIds.toArray(new String[0]));
	}

	private String _addItemToLayout(String itemType, String parentItemId)
		throws Exception {

		JSONObject addItemJSONObject = ContentLayoutTestUtil.addItemToLayout(
			"{}", itemType, _layout, parentItemId, 0, _segmentsExperienceId);

		return addItemJSONObject.getString("addedItemId");
	}

	private void _assertExpectedLayoutStructureItem(
		String expectedParentItemId, String itemId,
		LayoutStructure layoutStructure) {

		Assert.assertFalse(layoutStructure.isItemMarkedForDeletion(itemId));

		LayoutStructureItem layoutStructureItem =
			layoutStructure.getLayoutStructureItem(itemId);

		Assert.assertNotNull(layoutStructureItem);
		Assert.assertEquals(
			expectedParentItemId, layoutStructureItem.getParentItemId());
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			String itemIds)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			ContentLayoutTestUtil.getMockLiferayPortletActionRequest(
				_company, _group, _layout);

		mockLiferayPortletActionRequest.addParameter("itemIds", itemIds);
		mockLiferayPortletActionRequest.addParameter(
			"segmentsExperienceId", String.valueOf(_segmentsExperienceId));

		ThemeDisplay themeDisplay =
			(ThemeDisplay)mockLiferayPortletActionRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		mockLiferayPortletActionRequest.setAttribute(
			PortletServlet.PORTLET_SERVLET_REQUEST, themeDisplay.getRequest());

		return mockLiferayPortletActionRequest;
	}

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject
	private LayoutStructureProvider _layoutStructureProvider;

	@Inject(
		filter = "mvc.command.name=/layout_content_page_editor/unmark_items_for_deletion"
	)
	private MVCActionCommand _mvcActionCommand;

	private long _segmentsExperienceId;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}