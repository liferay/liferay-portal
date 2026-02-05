/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.upgrade.v3_4_2.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.test.util.BaseCTUpgradeProcessTestCase;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRel;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelLocalService;
import com.liferay.layout.provider.LayoutStructureProvider;
import com.liferay.layout.responsive.ViewportSize;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.structure.ColumnLayoutStructureItem;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.change.tracking.CTModel;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
public class LayoutPageTemplateStructureRelUpgradeProcessTest
	extends BaseCTUpgradeProcessTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testUpgradeBorderRadius() throws Exception {
		_assertUpgradeWithItemConfig(
			null,
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put("borderRadius", "lg")),
			HashMapBuilder.<String, Object>put(
				"styles", JSONUtil.put("borderRadius", "0.375rem")
			).build(),
			HashMapBuilder.<String, Object>put(
				"borderRadius", RandomTestUtil.randomString()
			).build());
		_assertUpgradeWithItemConfig(
			null,
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put("borderRadius", "none")),
			HashMapBuilder.<String, Object>put(
				"styles", JSONUtil.put("borderRadius", StringPool.BLANK)
			).build(),
			HashMapBuilder.<String, Object>put(
				"borderRadius", RandomTestUtil.randomString()
			).build());
		_assertUpgradeWithItemConfig(
			null,
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put("borderRadius", "sm")),
			HashMapBuilder.<String, Object>put(
				"styles", JSONUtil.put("borderRadius", "0.1875rem")
			).build(),
			HashMapBuilder.<String, Object>put(
				"borderRadius", RandomTestUtil.randomString()
			).build());

		String expectedBorderRadious = RandomTestUtil.randomString();

		_assertUpgradeWithItemConfig(
			null,
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put("borderRadius", RandomTestUtil.randomString())),
			HashMapBuilder.<String, Object>put(
				"styles", JSONUtil.put("borderRadius", expectedBorderRadious)
			).build(),
			HashMapBuilder.<String, Object>put(
				"borderRadius", expectedBorderRadious
			).build());
	}

	@Test
	public void testUpgradeMarginBottom() throws Exception {
		String expectedMarginBottom = String.valueOf(
			RandomTestUtil.randomInt());

		HashMap<String, Object> expectedMap =
			HashMapBuilder.<String, Object>put(
				"styles", JSONUtil.put("marginBottom", expectedMarginBottom)
			).build();

		_assertUpgradeWithItemConfig(
			JSONUtil.put(
				"fieldSets",
				JSONUtil.put(
					JSONUtil.put(
						"fields",
						JSONUtil.putAll(
							JSONUtil.put(
								"dataType", "int"
							).put(
								"defaultValue",
								String.valueOf(RandomTestUtil.randomInt())
							).put(
								"name", "bottomSpacing"
							).put(
								"type", "text"
							),
							JSONUtil.put(
								"dataType", "int"
							).put(
								"defaultValue",
								String.valueOf(RandomTestUtil.randomInt())
							).put(
								"name", "marginBottom"
							).put(
								"type", "text"
							))
					).put(
						"label", "Configuration"
					))),
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put(
					"bottomSpacing", expectedMarginBottom
				).put(
					"marginBottom", String.valueOf(RandomTestUtil.randomInt())
				)),
			expectedMap,
			HashMapBuilder.<String, Object>put(
				"marginBottom", String.valueOf(RandomTestUtil.randomInt())
			).build());
		_assertUpgradeWithItemConfig(
			JSONUtil.put(
				"fieldSets",
				JSONUtil.put(
					JSONUtil.put(
						"fields",
						JSONUtil.putAll(
							JSONUtil.put(
								"dataType", "int"
							).put(
								"defaultValue", expectedMarginBottom
							).put(
								"name", "bottomSpacing"
							).put(
								"type", "text"
							),
							JSONUtil.put(
								"dataType", "int"
							).put(
								"defaultValue",
								String.valueOf(RandomTestUtil.randomInt())
							).put(
								"name", "marginBottom"
							).put(
								"type", "text"
							))
					).put(
						"label", "Configuration"
					))),
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put(
					"marginBottom",
					String.valueOf(RandomTestUtil.randomInt()))),
			expectedMap,
			HashMapBuilder.<String, Object>put(
				"marginBottom", String.valueOf(RandomTestUtil.randomInt())
			).build());
		_assertUpgradeWithItemConfig(
			JSONUtil.put(
				"fieldSets",
				JSONUtil.put(
					JSONUtil.put(
						"fields",
						JSONUtil.putAll(
							JSONUtil.put(
								"dataType", "int"
							).put(
								"defaultValue",
								String.valueOf(RandomTestUtil.randomInt())
							).put(
								"name", "marginBottom"
							).put(
								"type", "text"
							))
					).put(
						"label", "Configuration"
					))),
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put("marginBottom", expectedMarginBottom)),
			expectedMap,
			HashMapBuilder.<String, Object>put(
				"marginBottom", String.valueOf(RandomTestUtil.randomInt())
			).build());
		_assertUpgradeWithItemConfig(
			JSONUtil.put(
				"fieldSets",
				JSONUtil.put(
					JSONUtil.put(
						"fields",
						JSONUtil.put(
							JSONUtil.put(
								"dataType", "int"
							).put(
								"defaultValue", expectedMarginBottom
							).put(
								"name", "marginBottom"
							).put(
								"type", "text"
							))
					).put(
						"label", "Configuration"
					))),
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				_jsonFactory.createJSONObject()),
			expectedMap,
			HashMapBuilder.<String, Object>put(
				"marginBottom", String.valueOf(RandomTestUtil.randomInt())
			).build());
		_assertUpgradeWithItemConfig(
			_jsonFactory.createJSONObject(),
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				_jsonFactory.createJSONObject()),
			expectedMap,
			HashMapBuilder.<String, Object>put(
				"marginBottom", expectedMarginBottom
			).build());
	}

	@Test
	public void testUpgradeShadow() throws Exception {
		_assertUpgradeWithItemConfig(
			null,
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put("boxShadow", "lg")),
			HashMapBuilder.<String, Object>put(
				"styles",
				JSONUtil.put("shadow", "0 1rem 3rem rgba(0, 0, 0, .175)")
			).build(),
			HashMapBuilder.<String, Object>put(
				"shadow", RandomTestUtil.randomString()
			).build());
		_assertUpgradeWithItemConfig(
			null,
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put("boxShadow", "sm")),
			HashMapBuilder.<String, Object>put(
				"styles",
				JSONUtil.put("shadow", "0 .125rem .25rem rgba(0, 0, 0, .075)")
			).build(),
			HashMapBuilder.<String, Object>put(
				"shadow", RandomTestUtil.randomString()
			).build());

		String expectedShadow = RandomTestUtil.randomString();

		_assertUpgradeWithItemConfig(
			null,
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put("boxShadow", RandomTestUtil.randomString())),
			HashMapBuilder.<String, Object>put(
				"styles", JSONUtil.put("shadow", expectedShadow)
			).build(),
			HashMapBuilder.<String, Object>put(
				"shadow", expectedShadow
			).build());
	}

	@Test
	public void testUpgradeTextAlign() throws Exception {
		String expectedTextAlign = RandomTestUtil.randomString();

		HashMap<String, Object> expectedMap =
			HashMapBuilder.<String, Object>put(
				"styles", JSONUtil.put("textAlign", expectedTextAlign)
			).build();

		_assertUpgradeWithItemConfig(
			null,
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put(
					"buttonAlign", expectedTextAlign
				).put(
					"contentAlign", RandomTestUtil.randomString()
				).put(
					"imageAlign", RandomTestUtil.randomString()
				).put(
					"textAlign", RandomTestUtil.randomString()
				).put(
					RandomTestUtil.randomString(), RandomTestUtil.randomString()
				)),
			expectedMap,
			HashMapBuilder.<String, Object>put(
				"textAlign", RandomTestUtil.randomString()
			).build());
		_assertUpgradeWithItemConfig(
			null,
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put(
					"contentAlign", expectedTextAlign
				).put(
					"imageAlign", RandomTestUtil.randomString()
				).put(
					"textAlign", RandomTestUtil.randomString()
				).put(
					RandomTestUtil.randomString(), RandomTestUtil.randomString()
				)),
			expectedMap,
			HashMapBuilder.<String, Object>put(
				"textAlign", RandomTestUtil.randomString()
			).build());
		_assertUpgradeWithItemConfig(
			null,
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put(
					"imageAlign", expectedTextAlign
				).put(
					"textAlign", RandomTestUtil.randomString()
				).put(
					RandomTestUtil.randomString(), RandomTestUtil.randomString()
				)),
			expectedMap,
			HashMapBuilder.<String, Object>put(
				"textAlign", RandomTestUtil.randomString()
			).build());
		_assertUpgradeWithItemConfig(
			null,
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put(
					"textAlign", expectedTextAlign
				).put(
					RandomTestUtil.randomString(), RandomTestUtil.randomString()
				)),
			expectedMap,
			HashMapBuilder.<String, Object>put(
				"textAlign", RandomTestUtil.randomString()
			).build());
		_assertUpgradeWithItemConfig(
			null,
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put(
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString())),
			expectedMap,
			HashMapBuilder.<String, Object>put(
				"textAlign", expectedTextAlign
			).build());
	}

	@Test
	public void testUpgradeTextColor() throws Exception {
		_assertUpgradeWithItemConfig(
			null,
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put("textColor", JSONUtil.put("cssClass", "danger"))),
			HashMapBuilder.<String, Object>put(
				"styles", JSONUtil.put("textColor", "#DA1414")
			).build(),
			HashMapBuilder.<String, Object>put(
				"textColor", RandomTestUtil.randomString()
			).build());
		_assertUpgradeWithItemConfig(
			null,
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put("textColor", JSONUtil.put("cssClass", "dark"))),
			HashMapBuilder.<String, Object>put(
				"styles", JSONUtil.put("textColor", "#272833")
			).build(),
			HashMapBuilder.<String, Object>put(
				"textColor", RandomTestUtil.randomString()
			).build());
		_assertUpgradeWithItemConfig(
			null,
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put(
					"textColor", JSONUtil.put("cssClass", "gray-dark"))),
			HashMapBuilder.<String, Object>put(
				"styles", JSONUtil.put("textColor", "#393A4A")
			).build(),
			HashMapBuilder.<String, Object>put(
				"textColor", RandomTestUtil.randomString()
			).build());
		_assertUpgradeWithItemConfig(
			null,
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put("textColor", JSONUtil.put("cssClass", "info"))),
			HashMapBuilder.<String, Object>put(
				"styles", JSONUtil.put("textColor", "#2E5AAC")
			).build(),
			HashMapBuilder.<String, Object>put(
				"textColor", RandomTestUtil.randomString()
			).build());
		_assertUpgradeWithItemConfig(
			null,
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put("textColor", JSONUtil.put("cssClass", "light"))),
			HashMapBuilder.<String, Object>put(
				"styles", JSONUtil.put("textColor", "#F1F2F5")
			).build(),
			HashMapBuilder.<String, Object>put(
				"textColor", RandomTestUtil.randomString()
			).build());
		_assertUpgradeWithItemConfig(
			null,
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put("textColor", JSONUtil.put("cssClass", "lighter"))),
			HashMapBuilder.<String, Object>put(
				"styles", JSONUtil.put("textColor", "#F7F8F9")
			).build(),
			HashMapBuilder.<String, Object>put(
				"textColor", RandomTestUtil.randomString()
			).build());
		_assertUpgradeWithItemConfig(
			null,
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put("textColor", JSONUtil.put("cssClass", "primary"))),
			HashMapBuilder.<String, Object>put(
				"styles", JSONUtil.put("textColor", "#0B5FFF")
			).build(),
			HashMapBuilder.<String, Object>put(
				"textColor", RandomTestUtil.randomString()
			).build());
		_assertUpgradeWithItemConfig(
			null,
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put(
					"textColor", JSONUtil.put("cssClass", "secondary"))),
			HashMapBuilder.<String, Object>put(
				"styles", JSONUtil.put("textColor", "#6B6C7E")
			).build(),
			HashMapBuilder.<String, Object>put(
				"textColor", RandomTestUtil.randomString()
			).build());
		_assertUpgradeWithItemConfig(
			null,
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put("textColor", JSONUtil.put("cssClass", "success"))),
			HashMapBuilder.<String, Object>put(
				"styles", JSONUtil.put("textColor", "#287D3C")
			).build(),
			HashMapBuilder.<String, Object>put(
				"textColor", RandomTestUtil.randomString()
			).build());
		_assertUpgradeWithItemConfig(
			null,
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put("textColor", JSONUtil.put("cssClass", "warning"))),
			HashMapBuilder.<String, Object>put(
				"styles", JSONUtil.put("textColor", "#B95000")
			).build(),
			HashMapBuilder.<String, Object>put(
				"textColor", RandomTestUtil.randomString()
			).build());
		_assertUpgradeWithItemConfig(
			null,
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put("textColor", JSONUtil.put("cssClass", "white"))),
			HashMapBuilder.<String, Object>put(
				"styles", JSONUtil.put("textColor", "#FFFFFF")
			).build(),
			HashMapBuilder.<String, Object>put(
				"textColor", RandomTestUtil.randomString()
			).build());

		String expectedTextColor = RandomTestUtil.randomString();

		_assertUpgradeWithItemConfig(
			null,
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put(
					"textColor", JSONUtil.put("cssClass", expectedTextColor))),
			HashMapBuilder.<String, Object>put(
				"styles", JSONUtil.put("textColor", expectedTextColor)
			).build(),
			HashMapBuilder.<String, Object>put(
				"textColor", RandomTestUtil.randomString()
			).build());
		_assertUpgradeWithItemConfig(
			null,
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put(
					"textColor",
					JSONUtil.put(
						RandomTestUtil.randomString(),
						RandomTestUtil.randomString()))),
			HashMapBuilder.<String, Object>put(
				"styles", JSONUtil.put("textColor", expectedTextColor)
			).build(),
			HashMapBuilder.<String, Object>put(
				"textColor", expectedTextColor
			).build());
		_assertUpgradeWithItemConfig(
			null,
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put("textColor", RandomTestUtil.randomString())),
			HashMapBuilder.<String, Object>put(
				"styles", JSONUtil.put("textColor", expectedTextColor)
			).build(),
			HashMapBuilder.<String, Object>put(
				"textColor", expectedTextColor
			).build());
		_assertUpgradeWithItemConfig(
			null,
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				_jsonFactory.createJSONObject()),
			HashMapBuilder.<String, Object>put(
				"styles", JSONUtil.put("textColor", expectedTextColor)
			).build(),
			HashMapBuilder.<String, Object>put(
				"textColor", expectedTextColor
			).build());
	}

	@Test
	public void testUpgradeViewportConfigurationSize() throws Exception {
		_assertUpgradeWithViewportConfigurationSize(
			HashMapBuilder.put(
				ViewportSize.MOBILE_LANDSCAPE.getViewportSizeId(), "12"
			).build(),
			Collections.emptyMap());
		_assertUpgradeWithViewportConfigurationSize(
			HashMapBuilder.put(
				ViewportSize.MOBILE_LANDSCAPE.getViewportSizeId(),
				String.valueOf(RandomTestUtil.randomInt(1, 10))
			).put(
				ViewportSize.PORTRAIT_MOBILE.getViewportSizeId(),
				String.valueOf(RandomTestUtil.randomInt(1, 10))
			).put(
				ViewportSize.TABLET.getViewportSizeId(),
				String.valueOf(RandomTestUtil.randomInt(1, 10))
			).build());
		_assertUpgradeWithViewportConfigurationSize(
			HashMapBuilder.put(
				ViewportSize.PORTRAIT_MOBILE.getViewportSizeId(),
				String.valueOf(RandomTestUtil.randomInt(1, 10))
			).put(
				ViewportSize.TABLET.getViewportSizeId(),
				String.valueOf(RandomTestUtil.randomInt(1, 10))
			).build());
		_assertUpgradeWithViewportConfigurationSize(
			HashMapBuilder.put(
				ViewportSize.TABLET.getViewportSizeId(),
				String.valueOf(RandomTestUtil.randomInt(1, 10))
			).build());
	}

	@Override
	protected CTModel<?> addCTModel() throws Exception {
		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0, null,
				0, 0, null, RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE, 0, true, 0,
				0, 0, WorkflowConstants.STATUS_APPROVED, new ServiceContext());

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					_group.getGroupId(), layoutPageTemplateEntry.getPlid());

		return _layoutPageTemplateStructureRelLocalService.
			fetchLayoutPageTemplateStructureRel(
				layoutPageTemplateStructure.getLayoutPageTemplateStructureId(),
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(
						layoutPageTemplateEntry.getPlid()));
	}

	@Override
	protected CTService<?> getCTService() {
		return _layoutPageTemplateStructureRelLocalService;
	}

	@Override
	protected void runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();
	}

	@Override
	protected CTModel<?> updateCTModel(CTModel<?> ctModel) throws Exception {
		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
			(LayoutPageTemplateStructureRel)ctModel;

		layoutPageTemplateStructureRel.setData(RandomTestUtil.randomString());

		return _layoutPageTemplateStructureRelLocalService.
			updateLayoutPageTemplateStructureRel(
				layoutPageTemplateStructureRel);
	}

	private String _addColumnLayoutStructureItem(
			Layout layout, Map<String, String> map, long segmentsExperienceId)
		throws Exception {

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					_group.getGroupId(), layout.getPlid());

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructure.getData(segmentsExperienceId));

		ColumnLayoutStructureItem columnLayoutStructureItem =
			(ColumnLayoutStructureItem)
				layoutStructure.addColumnLayoutStructureItem(
					layoutStructure.getMainItemId(), 0);

		for (Map.Entry<String, String> entry : map.entrySet()) {
			columnLayoutStructureItem.setViewportConfiguration(
				entry.getKey(), JSONUtil.put("size", entry.getValue()));
		}

		_layoutPageTemplateStructureRelLocalService.
			updateLayoutPageTemplateStructureRel(
				layoutPageTemplateStructure.getLayoutPageTemplateStructureId(),
				segmentsExperienceId, layoutStructure.toString());

		_assertViewportSizeConfiguration(
			map, columnLayoutStructureItem.getItemId(), layout,
			segmentsExperienceId);

		return columnLayoutStructureItem.getItemId();
	}

	private String _addFragmentStyledLayoutStructureItem(
			JSONObject configurationJSONObject,
			JSONObject editableValuesJSONObject, Layout layout,
			Map<String, Object> map, long segmentsExperienceId)
		throws Exception {

		FragmentEntryLink fragmentEntryLink =
			ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
				editableValuesJSONObject.toString(), layout,
				segmentsExperienceId);

		if (configurationJSONObject != null) {
			fragmentEntryLink.setConfiguration(
				configurationJSONObject.toString());

			fragmentEntryLink =
				_fragmentEntryLinkLocalService.updateFragmentEntryLink(
					fragmentEntryLink);
		}

		LayoutStructure layoutStructure =
			_layoutStructureProvider.getLayoutStructure(
				layout.getPlid(), segmentsExperienceId);

		Map<Long, LayoutStructureItem> fragmentLayoutStructureItems =
			layoutStructure.getFragmentLayoutStructureItems();

		Assert.assertEquals(
			MapUtil.toString(fragmentLayoutStructureItems), 1,
			fragmentLayoutStructureItems.size());

		FragmentStyledLayoutStructureItem fragmentStyledLayoutStructureItem =
			(FragmentStyledLayoutStructureItem)fragmentLayoutStructureItems.get(
				fragmentEntryLink.getFragmentEntryLinkId());

		_updateItemConfig(
			fragmentStyledLayoutStructureItem.getItemId(), layout, map,
			segmentsExperienceId);

		return fragmentStyledLayoutStructureItem.getItemId();
	}

	private void _assertItemConfigJSONObject(
			Map<String, Object> expectedMap, String itemId, Layout layout,
			long segmentsExperienceId)
		throws Exception {

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					_group.getGroupId(), layout.getPlid());

		JSONObject configJSONObject = _getItemConfigJSONObject(
			itemId, layoutPageTemplateStructure, segmentsExperienceId);

		for (Map.Entry<String, Object> entry : expectedMap.entrySet()) {
			if (Validator.isNull(entry.getValue())) {
				Assert.assertTrue(
					entry.getKey(),
					Validator.isNull(configJSONObject.get(entry.getKey())));
			}
			else if (entry.getValue() instanceof JSONObject) {
				Assert.assertTrue(
					JSONUtil.toString(
						configJSONObject.getJSONObject(entry.getKey())),
					JSONUtil.equals(
						(JSONObject)entry.getValue(),
						configJSONObject.getJSONObject(entry.getKey())));
			}
			else {
				Assert.assertEquals(
					entry.getValue(), configJSONObject.get(entry.getKey()));
			}
		}
	}

	private void _assertUpgradeWithItemConfig(
			JSONObject configurationJSONObject,
			JSONObject editableValuesJSONObject,
			Map<String, Object> expectedMap, Map<String, Object> map)
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		String draftLayoutItemId = _addFragmentStyledLayoutStructureItem(
			configurationJSONObject, editableValuesJSONObject, draftLayout, map,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid()));

		String layoutItemId = _addFragmentStyledLayoutStructureItem(
			configurationJSONObject, editableValuesJSONObject, layout, map,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layout.getPlid()));

		_runUpgrade();

		_assertItemConfigJSONObject(
			expectedMap, draftLayoutItemId, draftLayout,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid()));
		_assertItemConfigJSONObject(
			expectedMap, layoutItemId, layout,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layout.getPlid()));
	}

	private void _assertUpgradeWithViewportConfigurationSize(
			Map<String, String> viewPortsMap)
		throws Exception {

		_assertUpgradeWithViewportConfigurationSize(viewPortsMap, viewPortsMap);
	}

	private void _assertUpgradeWithViewportConfigurationSize(
			Map<String, String> expectedMap, Map<String, String> map)
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		String draftLayoutItemId = _addColumnLayoutStructureItem(
			draftLayout, map,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid()));

		String layoutItemId = _addColumnLayoutStructureItem(
			layout, map,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layout.getPlid()));

		_runUpgrade();

		_assertViewportSizeConfiguration(
			expectedMap, draftLayoutItemId, draftLayout,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid()));
		_assertViewportSizeConfiguration(
			expectedMap, layoutItemId, layout,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layout.getPlid()));
	}

	private void _assertViewportSizeConfiguration(
			Map<String, String> expectedMap, String itemId, Layout layout,
			long segmentsExperienceId)
		throws Exception {

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					_group.getGroupId(), layout.getPlid());

		JSONObject configJSONObject = _getItemConfigJSONObject(
			itemId, layoutPageTemplateStructure, segmentsExperienceId);

		for (Map.Entry<String, String> entry : expectedMap.entrySet()) {
			JSONObject jsonObject = configJSONObject.getJSONObject(
				entry.getKey());

			Assert.assertEquals(entry.getValue(), jsonObject.getString("size"));
		}
	}

	private JSONObject _getItemConfigJSONObject(
		String itemId, JSONObject jsonObject) {

		JSONObject itemsJSONObject = jsonObject.getJSONObject("items");

		JSONObject itemJSONObject = itemsJSONObject.getJSONObject(itemId);

		return itemJSONObject.getJSONObject("config");
	}

	private JSONObject _getItemConfigJSONObject(
			String itemId,
			LayoutPageTemplateStructure layoutPageTemplateStructure,
			long segmentsExperienceId)
		throws Exception {

		return _getItemConfigJSONObject(
			itemId,
			JSONFactoryUtil.createJSONObject(
				layoutPageTemplateStructure.getData(segmentsExperienceId)));
	}

	private void _runUpgrade() throws Exception {
		runUpgrade();

		_multiVMPool.clear();
	}

	private void _updateItemConfig(
			String itemId, Layout layout, Map<String, Object> map,
			long segmentsExperienceId)
		throws Exception {

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					_group.getGroupId(), layout.getPlid());

		JSONObject layoutStructureJSONObject = JSONFactoryUtil.createJSONObject(
			layoutPageTemplateStructure.getData(segmentsExperienceId));

		JSONObject itemConfigJSONObject = _getItemConfigJSONObject(
			itemId, layoutStructureJSONObject);

		for (Map.Entry<String, Object> entry : map.entrySet()) {
			itemConfigJSONObject.put(entry.getKey(), entry.getValue());
		}

		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
			_layoutPageTemplateStructureRelLocalService.
				fetchLayoutPageTemplateStructureRel(
					layoutPageTemplateStructure.
						getLayoutPageTemplateStructureId(),
					segmentsExperienceId);

		layoutPageTemplateStructureRel.setData(
			layoutStructureJSONObject.toString());

		_layoutPageTemplateStructureRelLocalService.
			updateLayoutPageTemplateStructureRel(
				layoutPageTemplateStructureRel);

		_assertItemConfigJSONObject(map, itemId, layout, segmentsExperienceId);
	}

	private static final String _CLASS_NAME =
		"com.liferay.layout.page.template.internal.upgrade.v3_4_2." +
			"LayoutPageTemplateStructureRelUpgradeProcess";

	@Inject(
		filter = "(&(component.name=com.liferay.layout.page.template.internal.upgrade.registry.LayoutPageTemplateServiceUpgradeStepRegistrator))"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject
	private LayoutPageTemplateStructureRelLocalService
		_layoutPageTemplateStructureRelLocalService;

	@Inject
	private LayoutStructureProvider _layoutStructureProvider;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}