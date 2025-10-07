/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.BulkLayoutConverter;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.model.LayoutTypePortletConstants;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rubén Pulido
 */
@RunWith(Arquillian.class)
public class BulkLayoutConverterTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setUserId(TestPropsValues.getUserId());

		ServiceContextThreadLocal.pushServiceContext(serviceContext);
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();

		_contentLayout = null;
		_corruptedLayout = null;
		_privateLayout = null;
		_publicLayout = null;
	}

	@Test(expected = PortalException.class)
	public void testConvertCorruptedLayout() throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		Assert.assertEquals(LayoutConstants.TYPE_PORTLET, layout.getType());

		_layoutLocalService.updateLayout(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			StringPool.BLANK);

		_bulkLayoutConverter.convertLayout(layout.getPlid());
	}

	@Test
	public void testConvertLayouts() throws Exception {
		_addLayouts();

		_assertPlids(
			_getConvertibleLayoutPlids(),
			_bulkLayoutConverter.convertLayouts(_getLayoutPlids()));

		_assertLayouts();
	}

	@Test
	public void testConvertLayoutsInGroup() throws Exception {
		_addLayouts();

		_assertPlids(
			_getConvertibleLayoutPlids(),
			_bulkLayoutConverter.convertLayouts(_group.getGroupId()));

		_assertLayouts();
	}

	@Test
	public void testConvertLinkedLayout() throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(),
			UnicodePropertiesBuilder.put(
				LayoutTypePortletConstants.LAYOUT_TEMPLATE_ID, "1_column"
			).buildString());

		LayoutPrototype layoutPrototype = LayoutTestUtil.addLayoutPrototype(
			StringUtil.randomString());

		layout.setLayoutPrototypeUuid(layoutPrototype.getUuid());

		layout.setLayoutPrototypeLinkEnabled(true);

		layout = _layoutLocalService.updateLayout(layout);

		Assert.assertEquals(LayoutConstants.TYPE_PORTLET, layout.getType());
		Assert.assertTrue(layout.isLayoutPrototypeLinkEnabled());
		Assert.assertNotNull(layout.getLayoutPrototypeUuid());

		_bulkLayoutConverter.convertLayout(layout.getPlid());

		Layout convertedLayout = _layoutLocalService.getLayoutByUuidAndGroupId(
			layout.getUuid(), layout.getGroupId(), layout.isPrivateLayout());

		Assert.assertEquals(
			LayoutConstants.TYPE_CONTENT, convertedLayout.getType());

		Assert.assertFalse(convertedLayout.isLayoutPrototypeLinkEnabled());
		Assert.assertEquals(
			convertedLayout.getLayoutPrototypeUuid(), StringPool.BLANK);
	}

	@Test
	public void testConvertMultipleWidgetPages() throws Exception {
		List<Layout> layouts = new ArrayList<>();

		String[] layoutTemplateIds = {
			"1_column", "2_columns_i", "2_columns_ii", "2_columns_iii",
			"3_columns", "1_2_columns_i", "1_2_columns_ii", "1_2_1_columns_i",
			"1_2_1_columns_ii", "1_3_1_columns", "1_3_2_columns",
			"2_1_2_columns", "2_2_columns", "3_2_3_columns"
		};

		for (String layoutTemplateId : layoutTemplateIds) {
			Layout layout = LayoutTestUtil.addTypePortletLayout(
				_group.getGroupId(),
				UnicodePropertiesBuilder.put(
					LayoutTypePortletConstants.LAYOUT_TEMPLATE_ID,
					layoutTemplateId
				).buildString());

			LayoutTestUtil.addPortletToLayout(
				layout, AssetPublisherPortletKeys.ASSET_PUBLISHER);

			layouts.add(layout);
		}

		long[] plids = TransformUtil.unsafeTransformToLongArray(
			layouts, layout -> layout.getPlid());

		_bulkLayoutConverter.convertLayouts(plids);

		for (long plid : plids) {
			Layout layout = _layoutLocalService.fetchLayout(plid);

			Assert.assertTrue(layout.isTypeContent());

			LayoutPageTemplateStructure layoutPageTemplateStructure =
				_layoutPageTemplateStructureLocalService.
					fetchLayoutPageTemplateStructure(_group.getGroupId(), plid);

			long defaultSegmentsExperienceId =
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(plid);

			LayoutStructure layoutStructure = LayoutStructure.of(
				layoutPageTemplateStructure.getData(
					defaultSegmentsExperienceId));

			Map<Long, LayoutStructureItem> fragmentEntryLinkIdMap =
				layoutStructure.getFragmentLayoutStructureItems();

			Assert.assertEquals(
				fragmentEntryLinkIdMap.toString(), 1,
				fragmentEntryLinkIdMap.size());

			for (long fragmentEntryLinkId : fragmentEntryLinkIdMap.keySet()) {
				FragmentEntryLink fragmentEntryLink =
					_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
						fragmentEntryLinkId);

				Assert.assertNotNull(fragmentEntryLink);
				Assert.assertTrue(fragmentEntryLink.isTypePortlet());

				JSONObject jsonObject =
					fragmentEntryLink.getEditableValuesJSONObject();

				Assert.assertEquals(
					AssetPublisherPortletKeys.ASSET_PUBLISHER,
					jsonObject.getString("portletId"));
			}
		}
	}

	@Test
	public void testConvertPrivateLayout() throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), true, RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			UnicodePropertiesBuilder.put(
				LayoutTypePortletConstants.LAYOUT_TEMPLATE_ID, "1_column"
			).buildString(),
			new HashMap<>(), false);

		Assert.assertEquals(LayoutConstants.TYPE_PORTLET, layout.getType());

		_bulkLayoutConverter.convertLayout(layout.getPlid());

		Layout convertedLayout = _layoutLocalService.getLayoutByUuidAndGroupId(
			layout.getUuid(), layout.getGroupId(), layout.isPrivateLayout());

		Assert.assertEquals(
			LayoutConstants.TYPE_CONTENT, convertedLayout.getType());
	}

	@Test
	public void testConvertPublicLayout() throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(),
			UnicodePropertiesBuilder.put(
				LayoutTypePortletConstants.LAYOUT_TEMPLATE_ID, "1_column"
			).buildString());

		Assert.assertEquals(LayoutConstants.TYPE_PORTLET, layout.getType());

		_bulkLayoutConverter.convertLayout(layout.getPlid());

		Layout convertedLayout = _layoutLocalService.getLayoutByUuidAndGroupId(
			layout.getUuid(), layout.getGroupId(), layout.isPrivateLayout());

		Assert.assertEquals(
			LayoutConstants.TYPE_CONTENT, convertedLayout.getType());
	}

	@Test
	public void testConvertWidgetPagesWithNestedApplicationAndCustomizationSettingToContentPages()
		throws Exception {

		Layout layout1 = LayoutTestUtil.addTypePortletLayout(_group);

		LayoutTestUtil.addPortletToLayout(
			layout1, AssetPublisherPortletKeys.ASSET_PUBLISHER);
		LayoutTestUtil.addPortletToLayout(
			layout1,
			"com_liferay_nested_portlets_web_portlet_NestedPortletsPortlet");

		Layout layout2 = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(),
			UnicodePropertiesBuilder.put(
				LayoutConstants.CUSTOMIZABLE_LAYOUT, Boolean.TRUE.toString()
			).buildString());

		LayoutTestUtil.addPortletToLayout(
			layout2, AssetPublisherPortletKeys.ASSET_PUBLISHER);
		LayoutTestUtil.addPortletToLayout(
			layout2,
			"com_liferay_nested_portlets_web_portlet_NestedPortletsPortlet");

		_bulkLayoutConverter.convertLayouts(
			new long[] {layout1.getPlid(), layout2.getPlid()});

		layout1 = _layoutLocalService.fetchLayout(layout1.getPlid());

		Assert.assertTrue(layout1.isTypeContent());

		layout2 = _layoutLocalService.fetchLayout(layout2.getPlid());

		Assert.assertTrue(layout2.isTypeContent());
	}

	@Test
	public void testGetConvertibleLayoutPlids() throws Exception {
		_addLayouts();

		_assertPlids(
			_getConvertibleLayoutPlids(),
			_bulkLayoutConverter.getConvertibleLayoutPlids(
				_group.getGroupId()));
	}

	@Test
	public void testGetConvertibleLayoutPlidsAfterConvertLayoutsInGroup()
		throws Exception {

		_addLayouts();

		_bulkLayoutConverter.convertLayouts(_group.getGroupId());

		_assertPlids(
			new long[0],
			_bulkLayoutConverter.getConvertibleLayoutPlids(
				_group.getGroupId()));
	}

	private void _addLayouts() throws Exception {
		_contentLayout = LayoutTestUtil.addTypeContentLayout(_group);

		_corruptedLayout = LayoutTestUtil.addTypePortletLayout(_group);

		_layoutLocalService.updateLayout(
			_corruptedLayout.getGroupId(), _corruptedLayout.isPrivateLayout(),
			_corruptedLayout.getLayoutId(), StringPool.BLANK);

		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.put(
				LayoutTypePortletConstants.LAYOUT_TEMPLATE_ID, "1_column"
			).build();

		_privateLayout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), true, RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			typeSettingsUnicodeProperties.toString(), new HashMap<>(), false);

		_publicLayout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), typeSettingsUnicodeProperties.toString());

		Assert.assertEquals(
			LayoutConstants.TYPE_CONTENT, _contentLayout.getType());
		Assert.assertEquals(
			LayoutConstants.TYPE_PORTLET, _corruptedLayout.getType());
		Assert.assertEquals(
			LayoutConstants.TYPE_PORTLET, _privateLayout.getType());
		Assert.assertEquals(
			LayoutConstants.TYPE_PORTLET, _publicLayout.getType());
	}

	private void _assertLayouts() throws Exception {
		_contentLayout = _layoutLocalService.getLayoutByUuidAndGroupId(
			_contentLayout.getUuid(), _contentLayout.getGroupId(),
			_contentLayout.isPrivateLayout());
		_corruptedLayout = _layoutLocalService.getLayoutByUuidAndGroupId(
			_corruptedLayout.getUuid(), _corruptedLayout.getGroupId(),
			_corruptedLayout.isPrivateLayout());
		_privateLayout = _layoutLocalService.getLayoutByUuidAndGroupId(
			_privateLayout.getUuid(), _privateLayout.getGroupId(),
			_privateLayout.isPrivateLayout());
		_publicLayout = _layoutLocalService.getLayoutByUuidAndGroupId(
			_publicLayout.getUuid(), _publicLayout.getGroupId(),
			_publicLayout.isPrivateLayout());

		Assert.assertEquals(
			LayoutConstants.TYPE_CONTENT, _contentLayout.getType());
		Assert.assertEquals(
			LayoutConstants.TYPE_PORTLET, _corruptedLayout.getType());
		Assert.assertEquals(
			LayoutConstants.TYPE_CONTENT, _privateLayout.getType());
		Assert.assertEquals(
			LayoutConstants.TYPE_CONTENT, _publicLayout.getType());
	}

	private void _assertPlids(long[] expectedPlids, long[] actualPlids) {
		for (long plid : expectedPlids) {
			Assert.assertTrue(ArrayUtil.contains(actualPlids, plid));
		}

		Assert.assertEquals(
			Arrays.toString(actualPlids), expectedPlids.length,
			actualPlids.length);
	}

	private long[] _getConvertibleLayoutPlids() {
		return new long[] {_privateLayout.getPlid(), _publicLayout.getPlid()};
	}

	private long[] _getLayoutPlids() {
		return new long[] {
			_contentLayout.getPlid(), _corruptedLayout.getPlid(),
			_privateLayout.getPlid(), _publicLayout.getPlid()
		};
	}

	@Inject
	private BulkLayoutConverter _bulkLayoutConverter;

	private Layout _contentLayout;
	private Layout _corruptedLayout;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	private Layout _privateLayout;
	private Layout _publicLayout;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}