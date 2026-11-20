/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.structure.DropZoneLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.io.ByteArrayOutputStream;

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
public class GetDropZoneAllowedFragmentEntryKeysMVCResourceCommandTest {

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
	@TestInfo("LPD-108786")
	public void testDoServeResource() throws Exception {
		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		DropZoneLayoutStructureItem dropZoneLayoutStructureItem =
			(DropZoneLayoutStructureItem)
				layoutStructure.addDropZoneLayoutStructureItem(
					rootLayoutStructureItem.getItemId(), 0);

		dropZoneLayoutStructureItem.setAllowNewFragmentEntries(false);

		String fragmentEntryRendererKey1 = RandomTestUtil.randomString();
		String fragmentEntryRendererKey2 = RandomTestUtil.randomString();

		dropZoneLayoutStructureItem.setFragmentEntriesJSONArray(
			JSONUtil.putAll(
				JSONUtil.put(
					"fragmentEntryRendererKey", fragmentEntryRendererKey1),
				JSONUtil.put(
					"fragmentEntryRendererKey", fragmentEntryRendererKey2)));

		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				TestPropsValues.getUserId(), _group.getGroupId(),
				_layout.getPlid(),
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(_layout.getPlid()),
				layoutStructure.toString());

		_testDoServeResource(
			dropZoneLayoutStructureItem.getItemId(), fragmentEntryRendererKey1,
			fragmentEntryRendererKey2);
		_testDoServeResource(rootLayoutStructureItem.getItemId());
	}

	private JSONArray _getFragmentEntryKeysJSONArray(
			MockLiferayResourceResponse mockLiferayResourceResponse)
		throws Exception {

		ByteArrayOutputStream byteArrayOutputStream =
			(ByteArrayOutputStream)
				mockLiferayResourceResponse.getPortletOutputStream();

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			byteArrayOutputStream.toString());

		return jsonObject.getJSONArray("fragmentEntryKeys");
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_layout.getCompanyId()));
		themeDisplay.setLayout(_layout);

		return themeDisplay;
	}

	private void _testDoServeResource(
			String itemId, String... expectedFragmentEntryKeys)
		throws Exception {

		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		mockLiferayResourceRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());
		mockLiferayResourceRequest.setParameter("itemId", itemId);

		MockLiferayResourceResponse mockLiferayResourceResponse =
			new MockLiferayResourceResponse();

		_mvcResourceCommand.serveResource(
			mockLiferayResourceRequest, mockLiferayResourceResponse);

		Assert.assertEquals(
			JSONUtil.putAll(
				(Object[])expectedFragmentEntryKeys
			).toString(),
			String.valueOf(
				_getFragmentEntryKeysJSONArray(mockLiferayResourceResponse)));
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	private Group _group;
	private Layout _layout;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject(
		filter = "mvc.command.name=/layout_content_page_editor/get_drop_zone_allowed_fragment_entry_keys"
	)
	private MVCResourceCommand _mvcResourceCommand;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}