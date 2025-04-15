/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action.test;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalServiceUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pavel Savinov
 */
@RunWith(Arquillian.class)
@Sync
public class UpdateItemConfigMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_objectMapper = new ObjectMapper() {
			{
				configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
			}
		};

		_layout = LayoutTestUtil.addTypeContentLayout(_group);
	}

	@Test
	public void testUpdateColumnItemConfigResponsive() throws Exception {
		_testUpdateColumnItemConfigResponsive(
			"column_item_config_responsive.json");
	}

	@Test
	public void testUpdateColumnItemConfigResponsiveExtra() throws Exception {
		_testUpdateColumnItemConfigResponsive(
			"column_item_config_responsive_extra.json");
	}

	@Test
	public void testUpdateColumnItemConfigResponsiveIncomplete()
		throws Exception {

		_testUpdateColumnItemConfigResponsive(
			"column_item_config_responsive_incomplete.json");
	}

	@Test
	public void testUpdateMultipleItemConfig() throws Exception {
		MockActionRequest mockActionRequest = _getMockActionRequest();

		LayoutStructure layoutStructure = _getLayoutStructure();

		LayoutStructureItem rowStyledLayoutStructureItem1 =
			layoutStructure.addRowStyledLayoutStructureItem(
				layoutStructure.getMainItemId(), 0, 1);

		LayoutStructureItem rowStyledLayoutStructureItem2 =
			layoutStructure.addRowStyledLayoutStructureItem(
				layoutStructure.getMainItemId(), 0, 1);

		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				_layout.getGroupId(), _layout.getPlid(),
				layoutStructure.toString());

		mockActionRequest.setParameter(
			"itemConfig", "{\"styles\":{\"display\":\"none\"}}");

		String[] itemIds = {
			rowStyledLayoutStructureItem1.getItemId(),
			rowStyledLayoutStructureItem2.getItemId()
		};

		mockActionRequest.setParameter("itemIds", itemIds);

		JSONObject jsonObject = ReflectionTestUtil.invoke(
			_mvcActionCommand, "doTransactionalCommand",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			mockActionRequest, new MockLiferayPortletActionResponse());

		JSONObject layoutDataJSONObject = jsonObject.getJSONObject(
			"layoutData");

		layoutStructure = LayoutStructure.of(layoutDataJSONObject.toString());

		rowStyledLayoutStructureItem1 = layoutStructure.getLayoutStructureItem(
			rowStyledLayoutStructureItem1.getItemId());

		JSONObject itemConfigJSONObject1 =
			rowStyledLayoutStructureItem1.getItemConfigJSONObject();

		JSONObject stylesJSONObject1 = itemConfigJSONObject1.getJSONObject(
			"styles");

		Assert.assertEquals("none", stylesJSONObject1.getString("display"));

		rowStyledLayoutStructureItem2 = layoutStructure.getLayoutStructureItem(
			rowStyledLayoutStructureItem2.getItemId());

		JSONObject itemConfigJSONObject2 =
			rowStyledLayoutStructureItem2.getItemConfigJSONObject();

		JSONObject stylesJSONObject2 = itemConfigJSONObject2.getJSONObject(
			"styles");

		Assert.assertEquals("none", stylesJSONObject2.getString("display"));
	}

	@Test
	public void testUpdateRowItemConfigResponsive() throws Exception {
		_testUpdateRowItemConfigResponsive("row_item_config_responsive.json");
	}

	@Test
	public void testUpdateRowItemConfigResponsiveExtra() throws Exception {
		_testUpdateRowItemConfigResponsive(
			"row_item_config_responsive_extra.json");
	}

	@Test
	public void testUpdateRowItemConfigResponsiveIncomplete() throws Exception {
		_testUpdateRowItemConfigResponsive(
			"row_item_config_responsive_incomplete.json");
	}

	private LayoutStructure _getLayoutStructure() throws Exception {
		LayoutPageTemplateStructure layoutPageTemplateStructure =
			LayoutPageTemplateStructureLocalServiceUtil.
				fetchLayoutPageTemplateStructure(
					_layout.getGroupId(), _layout.getPlid());

		return LayoutStructure.of(
			layoutPageTemplateStructure.getDefaultSegmentsExperienceData());
	}

	private MockActionRequest _getMockActionRequest() throws Exception {
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter(
			"segmentsExperienceId",
			String.valueOf(
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(_layout.getPlid())));
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

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

	private String _read(String fileName) throws Exception {
		Class<?> clazz = getClass();

		InputStream inputStream = clazz.getResourceAsStream(
			"dependencies/" + fileName);

		return StringUtil.read(inputStream);
	}

	private void _testUpdateColumnItemConfigResponsive(String itemConfigfile)
		throws Exception {

		MockActionRequest mockActionRequest = _getMockActionRequest();

		LayoutStructure layoutStructure = _getLayoutStructure();

		LayoutStructureItem layoutStructureItem =
			layoutStructure.addColumnLayoutStructureItem(
				layoutStructure.getMainItemId(), 0);

		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				_layout.getGroupId(), _layout.getPlid(),
				layoutStructure.toString());

		JSONObject jsonObject = layoutStructureItem.getItemConfigJSONObject();

		Assert.assertEquals(
			_objectMapper.readTree(_read("column_item_config.json")),
			_objectMapper.readTree(jsonObject.toString()));

		mockActionRequest.setParameter("itemConfig", _read(itemConfigfile));
		mockActionRequest.setParameter(
			"itemId", layoutStructureItem.getItemId());

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "doProcessAction",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			mockActionRequest, new MockLiferayPortletActionResponse());

		layoutStructure = _getLayoutStructure();

		layoutStructureItem = layoutStructure.getLayoutStructureItem(
			layoutStructureItem.getItemId());

		jsonObject = layoutStructureItem.getItemConfigJSONObject();

		Assert.assertEquals(
			_objectMapper.readTree(_read("column_item_config_responsive.json")),
			_objectMapper.readTree(jsonObject.toString()));
	}

	private void _testUpdateRowItemConfigResponsive(String itemConfigFile)
		throws Exception {

		MockActionRequest mockActionRequest = _getMockActionRequest();

		LayoutStructure layoutStructure = _getLayoutStructure();

		LayoutStructureItem layoutStructureItem =
			layoutStructure.addRowStyledLayoutStructureItem(
				layoutStructure.getMainItemId(), 0, 6);

		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				_layout.getGroupId(), _layout.getPlid(),
				layoutStructure.toString());

		JSONObject jsonObject = layoutStructureItem.getItemConfigJSONObject();

		Assert.assertEquals(
			_objectMapper.readTree(_read("row_item_config.json")),
			_objectMapper.readTree(jsonObject.toString()));

		mockActionRequest.setParameter("itemConfig", _read(itemConfigFile));
		mockActionRequest.setParameter(
			"itemId", layoutStructureItem.getItemId());

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "doProcessAction",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			mockActionRequest, new MockLiferayPortletActionResponse());

		layoutStructure = _getLayoutStructure();

		layoutStructureItem = layoutStructure.getLayoutStructureItem(
			layoutStructureItem.getItemId());

		jsonObject = layoutStructureItem.getItemConfigJSONObject();

		Assert.assertEquals(
			_objectMapper.readTree(_read("row_item_config_responsive.json")),
			_objectMapper.readTree(jsonObject.toString()));
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject(
		filter = "mvc.command.name=/layout_content_page_editor/update_item_config"
	)
	private MVCActionCommand _mvcActionCommand;

	private ObjectMapper _objectMapper;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}