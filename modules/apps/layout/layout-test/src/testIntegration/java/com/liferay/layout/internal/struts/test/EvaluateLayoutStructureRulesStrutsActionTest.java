/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.struts.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.helper.DefaultInputFragmentEntryConfigurationProvider;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.type.BooleanInfoFieldType;
import com.liferay.info.field.type.InfoFieldType;
import com.liferay.info.field.type.TextInfoFieldType;
import com.liferay.info.form.InfoForm;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFormProvider;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.provider.LayoutStructureProvider;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructureRule;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
public class EvaluateLayoutStructureRulesStrutsActionTest {

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

		_draftLayout = _layout.fetchDraftLayout();

		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition(
			ListUtil.fromArray(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING,
					RandomTestUtil.randomString(), "text"),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_BOOLEAN,
					ObjectFieldConstants.DB_TYPE_BOOLEAN,
					RandomTestUtil.randomString(), "boolean")));
	}

	@Test
	public void testEvaluateLayoutStructureRules() throws Exception {
		long segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_draftLayout.getPlid());

		JSONObject jsonObject = ContentLayoutTestUtil.addFormToLayout(
			false,
			String.valueOf(
				_portal.getClassNameId(_objectDefinition.getClassName())),
			"0", _draftLayout, _layoutStructureProvider, segmentsExperienceId);

		Map<String, String> inputTypesMap = _addInputFragmentEntryLinks(
			jsonObject.getString("addedItemId"), segmentsExperienceId);

		String[] layoutStructureRuleIds = {
			_addLayoutStructureRule(
				_getActionsJSONArray(
					LinkedHashMapBuilder.put(
						inputTypesMap.get(TextInfoFieldType.INSTANCE.getName()),
						"hide"
					).put(
						inputTypesMap.get(
							DefaultInputFragmentEntryConfigurationProvider.
								FORM_INPUT_SUBMIT_BUTTON),
						"enable"
					).build()),
				_getConditionsJSONArray(
					LinkedHashMapBuilder.<String, Object>put(
						inputTypesMap.get(
							BooleanInfoFieldType.INSTANCE.getName()),
						Boolean.TRUE
					).put(
						"user", String.valueOf(TestPropsValues.getUserId())
					).build()),
				"all", segmentsExperienceId),
			_addLayoutStructureRule(
				_getActionsJSONArray(
					LinkedHashMapBuilder.put(
						inputTypesMap.get(TextInfoFieldType.INSTANCE.getName()),
						"show"
					).put(
						inputTypesMap.get(
							DefaultInputFragmentEntryConfigurationProvider.
								FORM_INPUT_SUBMIT_BUTTON),
						"disable"
					).build()),
				_getConditionsJSONArray(
					LinkedHashMapBuilder.<String, Object>put(
						inputTypesMap.get(
							BooleanInfoFieldType.INSTANCE.getName()),
						Boolean.FALSE
					).put(
						"role",
						() -> {
							Role role = _roleLocalService.getRole(
								TestPropsValues.getCompanyId(),
								RoleConstants.ADMINISTRATOR);

							return String.valueOf(role.getRoleId());
						}
					).build()),
				"any", segmentsExperienceId)
		};

		ContentLayoutTestUtil.publishLayout(_draftLayout, _layout);

		_testExecute(
			JSONUtil.putAll(
				JSONUtil.put(
					"action", "hide"
				).put(
					"itemId",
					inputTypesMap.get(TextInfoFieldType.INSTANCE.getName())
				),
				JSONUtil.put(
					"action", "enable"
				).put(
					"itemId",
					inputTypesMap.get(
						DefaultInputFragmentEntryConfigurationProvider.
							FORM_INPUT_SUBMIT_BUTTON)
				),
				JSONUtil.put(
					"action", "show"
				).put(
					"itemId",
					inputTypesMap.get(TextInfoFieldType.INSTANCE.getName())
				),
				JSONUtil.put(
					"action", "disable"
				).put(
					"itemId",
					inputTypesMap.get(
						DefaultInputFragmentEntryConfigurationProvider.
							FORM_INPUT_SUBMIT_BUTTON)
				)),
			JSONUtil.put(
				inputTypesMap.get(BooleanInfoFieldType.INSTANCE.getName()),
				Boolean.TRUE
			).put(
				RandomTestUtil.randomString(), RandomTestUtil.randomString()
			).toString(),
			layoutStructureRuleIds, TestPropsValues.getUser());
		_testExecute(
			JSONUtil.putAll(
				JSONUtil.put(
					"action", "show"
				).put(
					"itemId",
					inputTypesMap.get(TextInfoFieldType.INSTANCE.getName())
				),
				JSONUtil.put(
					"action", "disable"
				).put(
					"itemId",
					inputTypesMap.get(
						DefaultInputFragmentEntryConfigurationProvider.
							FORM_INPUT_SUBMIT_BUTTON)
				),
				JSONUtil.put(
					"action", "show"
				).put(
					"itemId",
					inputTypesMap.get(TextInfoFieldType.INSTANCE.getName())
				),
				JSONUtil.put(
					"action", "disable"
				).put(
					"itemId",
					inputTypesMap.get(
						DefaultInputFragmentEntryConfigurationProvider.
							FORM_INPUT_SUBMIT_BUTTON)
				)),
			JSONUtil.put(
				inputTypesMap.get(BooleanInfoFieldType.INSTANCE.getName()),
				Boolean.FALSE
			).put(
				RandomTestUtil.randomString(), RandomTestUtil.randomString()
			).toString(),
			layoutStructureRuleIds, TestPropsValues.getUser());
		_testExecute(
			JSONUtil.putAll(
				JSONUtil.put(
					"action", "show"
				).put(
					"itemId",
					inputTypesMap.get(TextInfoFieldType.INSTANCE.getName())
				),
				JSONUtil.put(
					"action", "disable"
				).put(
					"itemId",
					inputTypesMap.get(
						DefaultInputFragmentEntryConfigurationProvider.
							FORM_INPUT_SUBMIT_BUTTON)
				),
				JSONUtil.put(
					"action", "hide"
				).put(
					"itemId",
					inputTypesMap.get(TextInfoFieldType.INSTANCE.getName())
				),
				JSONUtil.put(
					"action", "enable"
				).put(
					"itemId",
					inputTypesMap.get(
						DefaultInputFragmentEntryConfigurationProvider.
							FORM_INPUT_SUBMIT_BUTTON)
				)),
			JSONUtil.put(
				inputTypesMap.get(BooleanInfoFieldType.INSTANCE.getName()),
				Boolean.TRUE
			).put(
				RandomTestUtil.randomString(), RandomTestUtil.randomString()
			).toString(),
			layoutStructureRuleIds,
			_userLocalService.getGuestUser(TestPropsValues.getCompanyId()));
	}

	private long _addFragmentEntryLinkToLayout(
			String editableValues, String infoFieldTypeName,
			String parentItemId, int position, long segmentsExperienceId)
		throws Exception {

		FragmentEntry fragmentEntry =
			_fragmentCollectionContributorRegistry.getFragmentEntry(
				_getInputFragmentEntryKey(infoFieldTypeName));

		FragmentEntryLink fragmentEntryLink =
			ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
				editableValues, fragmentEntry.getCss(),
				fragmentEntry.getConfiguration(),
				fragmentEntry.getFragmentEntryId(), fragmentEntry.getHtml(),
				fragmentEntry.getJs(), _draftLayout,
				fragmentEntry.getFragmentEntryKey(), fragmentEntry.getType(),
				parentItemId, position, segmentsExperienceId);

		return fragmentEntryLink.getFragmentEntryLinkId();
	}

	private Map<String, String> _addInputFragmentEntryLinks(
			String parentItemId, long segmentsExperienceId)
		throws Exception {

		InfoItemFormProvider<?> infoItemFormProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormProvider.class, _objectDefinition.getClassName());

		InfoForm infoForm = infoItemFormProvider.getInfoForm(
			StringPool.BLANK, _group.getGroupId());

		Map<String, Long> map = new HashMap<>();

		int position = 0;

		for (InfoField<?> infoField :
				ListUtil.filter(
					infoForm.getAllInfoFields(), InfoField::isEditable)) {

			InfoFieldType infoFieldType = infoField.getInfoFieldType();

			map.put(
				infoFieldType.getName(),
				_addFragmentEntryLinkToLayout(
					JSONUtil.put(
						FragmentEntryProcessorConstants.
							KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
						JSONUtil.put("inputFieldId", infoField.getUniqueId())
					).toString(),
					infoFieldType.getName(), parentItemId, position,
					segmentsExperienceId));

			position++;
		}

		map.put(
			DefaultInputFragmentEntryConfigurationProvider.
				FORM_INPUT_SUBMIT_BUTTON,
			_addFragmentEntryLinkToLayout(
				"{}",
				DefaultInputFragmentEntryConfigurationProvider.
					FORM_INPUT_SUBMIT_BUTTON,
				parentItemId, position, segmentsExperienceId));

		Assert.assertEquals(MapUtil.toString(map), 3, map.size());

		LayoutStructure layoutStructure =
			_layoutStructureProvider.getLayoutStructure(
				_draftLayout.getPlid(), segmentsExperienceId);

		Map<Long, LayoutStructureItem> fragmentLayoutStructureItems =
			layoutStructure.getFragmentLayoutStructureItems();

		Assert.assertEquals(
			MapUtil.toString(fragmentLayoutStructureItems), map.size(),
			fragmentLayoutStructureItems.size());

		Map<String, String> inputTypesMap = new HashMap<>();

		for (Map.Entry<String, Long> entry : map.entrySet()) {
			LayoutStructureItem layoutStructureItem =
				fragmentLayoutStructureItems.get(entry.getValue());

			inputTypesMap.put(entry.getKey(), layoutStructureItem.getItemId());
		}

		return inputTypesMap;
	}

	private String _addLayoutStructureRule(
			JSONArray actionsJSONArray, JSONArray conditionsJSONArray,
			String conditionType, long segmentsExperienceId)
		throws Exception {

		LayoutStructure layoutStructure =
			_layoutStructureProvider.getLayoutStructure(
				_draftLayout.getPlid(), segmentsExperienceId);

		LayoutStructureRule layoutStructureRule =
			layoutStructure.addLayoutStructureRule(
				RandomTestUtil.randomString());

		layoutStructureRule.setActionsJSONArray(actionsJSONArray);

		layoutStructureRule.setConditionsJSONArray(conditionsJSONArray);

		layoutStructureRule.setConditionType(conditionType);

		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				_group.getGroupId(), _draftLayout.getPlid(),
				segmentsExperienceId, layoutStructure.toString());

		return layoutStructureRule.getId();
	}

	private JSONArray _getActionsJSONArray(Map<String, String> itemIdsMap) {
		JSONArray jsonArray = _jsonFactory.createJSONArray();

		for (Map.Entry<String, String> entry : itemIdsMap.entrySet()) {
			jsonArray.put(
				JSONUtil.put(
					"id", RandomTestUtil.randomString()
				).put(
					"itemId", entry.getKey()
				).put(
					"type", entry.getValue()
				));
		}

		return jsonArray;
	}

	private JSONArray _getConditionsJSONArray(Map<String, Object> fieldMap) {
		JSONArray jsonArray = _jsonFactory.createJSONArray();

		for (Map.Entry<String, Object> entry : fieldMap.entrySet()) {
			String field = entry.getKey();

			jsonArray.put(
				JSONUtil.put(
					"field", field
				).put(
					"id", RandomTestUtil.randomString()
				).put(
					"options",
					JSONUtil.put(
						"type", "equal"
					).put(
						"value", entry.getValue()
					)
				).put(
					"type",
					() -> {
						if (Objects.equals(field, "role") ||
							Objects.equals(field, "segment") ||
							Objects.equals(field, "user")) {

							return "user";
						}

						return "form";
					}
				));
		}

		return jsonArray;
	}

	private String _getInputFragmentEntryKey(String infoFieldTypeName) {
		if (Objects.equals(
				infoFieldTypeName, BooleanInfoFieldType.INSTANCE.getName())) {

			return "INPUTS-checkbox";
		}

		if (Objects.equals(
				infoFieldTypeName,
				DefaultInputFragmentEntryConfigurationProvider.
					FORM_INPUT_SUBMIT_BUTTON)) {

			return "INPUTS-submit-button";
		}

		if (Objects.equals(
				infoFieldTypeName, TextInfoFieldType.INSTANCE.getName())) {

			return "INPUTS-text-input";
		}

		return null;
	}

	private HttpServletRequest _getMockHttpServletRequest(
			String fieldValues, String[] layoutStructureRuleIds, User user)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));
		themeDisplay.setPlid(_layout.getPlid());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setUser(user);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockHttpServletRequest.setParameter("fieldValues", fieldValues);
		mockHttpServletRequest.setParameter(
			"layoutStructureRuleIds", layoutStructureRuleIds);
		mockHttpServletRequest.setParameter(
			"plid", String.valueOf(_layout.getPlid()));
		mockHttpServletRequest.setParameter(
			"segmentsExperienceId",
			String.valueOf(
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(_layout.getPlid())));

		return mockHttpServletRequest;
	}

	private void _testExecute(
			JSONArray expectedJSONArray, String fieldValues,
			String[] layoutStructureRuleIds, User user)
		throws Exception {

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_evaluateLayoutStructureRulesStrutsAction.execute(
			_getMockHttpServletRequest(
				fieldValues, layoutStructureRuleIds, user),
			mockHttpServletResponse);

		JSONArray actualJSONArray = _jsonFactory.createJSONArray(
			mockHttpServletResponse.getContentAsString());

		Assert.assertEquals(
			actualJSONArray.toString(), expectedJSONArray.length(),
			actualJSONArray.length());

		for (int i = 0; i < expectedJSONArray.length(); i++) {
			JSONObject jsonObject = expectedJSONArray.getJSONObject(i);
			JSONObject actualJSONObject = actualJSONArray.getJSONObject(i);

			Assert.assertEquals(
				jsonObject.getString("action"),
				actualJSONObject.getString("action"));
			Assert.assertEquals(
				jsonObject.getString("itemId"),
				actualJSONObject.getString("itemId"));
		}
	}

	private Layout _draftLayout;

	@Inject(filter = "path=/portal/evaluate_layout_structure_rules")
	private StrutsAction _evaluateLayoutStructureRulesStrutsAction;

	@Inject
	private FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Inject
	private JSONFactory _jsonFactory;

	private Layout _layout;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject
	private LayoutStructureProvider _layoutStructureProvider;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	@Inject
	private Portal _portal;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Inject
	private UserLocalService _userLocalService;

}