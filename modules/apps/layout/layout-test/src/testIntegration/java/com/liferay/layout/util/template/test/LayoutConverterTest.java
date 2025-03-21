/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.util.template.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructureItemUtil;
import com.liferay.layout.util.template.LayoutConversionResult;
import com.liferay.layout.util.template.LayoutConverter;
import com.liferay.layout.util.template.LayoutConverterRegistry;
import com.liferay.layout.util.template.LayoutData;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.LayoutTypePortletConstants;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TreeMapBuilder;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.portlet.GenericPortlet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Rubén Pulido
 */
@RunWith(Arquillian.class)
public class LayoutConverterTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_bundle = FrameworkUtil.getBundle(getClass());

		_bundleContext = _bundle.getBundleContext();

		_group = GroupTestUtil.addGroup();

		_user = UserTestUtil.addGroupAdminUser(_group);

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(
				_group, _user.getUserId()));

		_initPortlets();
	}

	@After
	public void tearDown() throws Exception {
		ServiceContextThreadLocal.popServiceContext();

		for (ServiceRegistration<?> serviceRegistration :
				_serviceRegistrations) {

			serviceRegistration.unregister();
		}

		_serviceRegistrations.clear();
	}

	@Test
	public void testConvertOneColumnMultiplePortlets() throws Exception {
		List<Map<String, String[]>> portletIdsMaps =
			new ArrayList<Map<String, String[]>>() {
				{
					add(
						HashMapBuilder.put(
							"column-1",
							new String[] {
								_testPortletName1, _testPortletName2,
								_testPortletName3
							}
						).build());
				}
			};

		_testConvert("1_column", portletIdsMaps, false);
	}

	@Test
	public void testConvertOneColumnNoPortlets() throws Exception {
		_testConvertNoPortlets("1_column");
	}

	@Test
	public void testConvertOneColumnSinglePortlet() throws Exception {
		List<Map<String, String[]>> portletIdsMaps =
			new ArrayList<Map<String, String[]>>() {
				{
					add(
						HashMapBuilder.put(
							"column-1", new String[] {_testPortletName2}
						).build());
				}
			};

		_testConvert("1_column", portletIdsMaps, true);
	}

	@Test
	public void testConvertOneThreeOneColumnsMultiplePortlets()
		throws Exception {

		List<Map<String, String[]>> portletIdsMaps =
			new ArrayList<Map<String, String[]>>() {
				{
					add(
						TreeMapBuilder.put(
							"column-1",
							new String[] {_testPortletName4, _testPortletName4}
						).build());
					add(
						TreeMapBuilder.put(
							"column-2",
							new String[] {_testPortletName1, _testPortletName4}
						).put(
							"column-3",
							new String[] {_testPortletName2, _testPortletName4}
						).put(
							"column-4",
							new String[] {_testPortletName4, _testPortletName4}
						).build());
					add(
						TreeMapBuilder.put(
							"column-5",
							new String[] {_testPortletName4, _testPortletName4}
						).build());
				}
			};

		_testConvert("1_3_1_columns", portletIdsMaps, false);
	}

	@Test
	public void testConvertOneThreeOneColumnsNoPortlets() throws Exception {
		_testConvertNoPortlets("1_3_1_columns");
	}

	@Test
	public void testConvertOneThreeOneColumnsSinglePortlet() throws Exception {
		List<Map<String, String[]>> portletIdsMaps =
			new ArrayList<Map<String, String[]>>() {
				{
					add(
						TreeMapBuilder.put(
							"column-1", new String[] {_testPortletName4}
						).build());
					add(
						TreeMapBuilder.put(
							"column-2", new String[] {_testPortletName1}
						).put(
							"column-3", new String[] {_testPortletName2}
						).put(
							"column-4", new String[] {_testPortletName4}
						).build());
					add(
						TreeMapBuilder.put(
							"column-5", new String[] {_testPortletName4}
						).build());
				}
			};

		_testConvert("1_3_1_columns", portletIdsMaps, true);
	}

	@Test
	public void testConvertOneThreeTwoColumnsMultiplePortlets()
		throws Exception {

		List<Map<String, String[]>> portletIdsMaps =
			new ArrayList<Map<String, String[]>>() {
				{
					add(
						TreeMapBuilder.put(
							"column-1",
							new String[] {_testPortletName4, _testPortletName4}
						).build());
					add(
						TreeMapBuilder.put(
							"column-2",
							new String[] {_testPortletName1, _testPortletName4}
						).put(
							"column-3",
							new String[] {_testPortletName2, _testPortletName4}
						).put(
							"column-4",
							new String[] {_testPortletName4, _testPortletName4}
						).build());
					add(
						TreeMapBuilder.put(
							"column-5",
							new String[] {_testPortletName4, _testPortletName4}
						).put(
							"column-6",
							new String[] {_testPortletName4, _testPortletName4}
						).build());
				}
			};

		_testConvert("1_3_2_columns", portletIdsMaps, false);
	}

	@Test
	public void testConvertOneThreeTwoColumnsNoPortlets() throws Exception {
		_testConvertNoPortlets("1_3_2_columns");
	}

	@Test
	public void testConvertOneThreeTwoColumnsSinglePortlet() throws Exception {
		List<Map<String, String[]>> portletIdsMaps =
			new ArrayList<Map<String, String[]>>() {
				{
					add(
						TreeMapBuilder.put(
							"column-1", new String[] {_testPortletName4}
						).build());
					add(
						TreeMapBuilder.put(
							"column-2", new String[] {_testPortletName1}
						).put(
							"column-3", new String[] {_testPortletName2}
						).put(
							"column-4", new String[] {_testPortletName4}
						).build());
					add(
						TreeMapBuilder.put(
							"column-5", new String[] {_testPortletName4}
						).put(
							"column-6", new String[] {_testPortletName4}
						).build());
				}
			};

		_testConvert("1_3_2_columns", portletIdsMaps, true);
	}

	@Test
	public void testConvertOneTwoColumnsIIMultiplePortlets() throws Exception {
		_testConvertOneTwoColumnsMultiplePortlets("1_2_columns_ii");
	}

	@Test
	public void testConvertOneTwoColumnsIINoPortlets() throws Exception {
		_testConvertNoPortlets("1_2_columns_ii");
	}

	@Test
	public void testConvertOneTwoColumnsIISinglePortlet() throws Exception {
		_testConvertOneTwoColumnsSinglePortlet("1_2_columns_ii");
	}

	@Test
	public void testConvertOneTwoColumnsIMultiplePortlets() throws Exception {
		_testConvertOneTwoColumnsMultiplePortlets("1_2_columns_i");
	}

	@Test
	public void testConvertOneTwoColumnsINoPortlets() throws Exception {
		_testConvertNoPortlets("1_2_columns_i");
	}

	@Test
	public void testConvertOneTwoColumnsISinglePortlet() throws Exception {
		_testConvertOneTwoColumnsSinglePortlet("1_2_columns_i");
	}

	@Test
	public void testConvertOneTwoOneColumnsIIMultiplePortlets()
		throws Exception {

		_testConvertOneTwoOneColumnsMultiplePortlets("1_2_1_columns_ii");
	}

	@Test
	public void testConvertOneTwoOneColumnsIINoPortlets() throws Exception {
		_testConvertNoPortlets("1_2_1_columns_ii");
	}

	@Test
	public void testConvertOneTwoOneColumnsIISinglePortlet() throws Exception {
		_testConvertOneTwoOneColumnsSinglePortlet("1_2_1_columns_ii");
	}

	@Test
	public void testConvertOneTwoOneColumnsIMultiplePortlets()
		throws Exception {

		_testConvertOneTwoOneColumnsMultiplePortlets("1_2_1_columns_i");
	}

	@Test
	public void testConvertOneTwoOneColumnsINoPortlets() throws Exception {
		_testConvertNoPortlets("1_2_1_columns_i");
	}

	@Test
	public void testConvertOneTwoOneColumnsISinglePortlet() throws Exception {
		_testConvertOneTwoOneColumnsSinglePortlet("1_2_1_columns_i");
	}

	@Test
	public void testConvertThreeColumnsMultiplePortlets() throws Exception {
		_testConvert(
			"3_columns",
			Collections.singletonList(
				TreeMapBuilder.put(
					"column-1",
					new String[] {_testPortletName1, _testPortletName4}
				).put(
					"column-2",
					new String[] {_testPortletName2, _testPortletName3}
				).put(
					"column-3",
					new String[] {_testPortletName5, _testPortletName5}
				).build()),
			false);
	}

	@Test
	public void testConvertThreeColumnsNoPortlets() throws Exception {
		_testConvertNoPortlets("3_columns");
	}

	@Test
	public void testConvertThreeColumnsSinglePortlet() throws Exception {
		_testConvert(
			"3_columns",
			Collections.singletonList(
				TreeMapBuilder.put(
					"column-1", new String[] {_testPortletName1}
				).put(
					"column-2", new String[] {_testPortletName2}
				).put(
					"column-3", new String[] {_testPortletName3}
				).build()),
			true);
	}

	@Test
	public void testConvertThreeTwoThreeColumnsMultiplePortlets()
		throws Exception {

		List<Map<String, String[]>> portletIdsMaps =
			new ArrayList<Map<String, String[]>>() {
				{
					add(
						TreeMapBuilder.put(
							"column-1",
							new String[] {_testPortletName4, _testPortletName4}
						).put(
							"column-2",
							new String[] {_testPortletName4, _testPortletName4}
						).put(
							"column-3",
							new String[] {_testPortletName4, _testPortletName4}
						).build());
					add(
						TreeMapBuilder.put(
							"column-4",
							new String[] {_testPortletName1, _testPortletName4}
						).put(
							"column-5",
							new String[] {_testPortletName2, _testPortletName4}
						).build());
					add(
						TreeMapBuilder.put(
							"column-6",
							new String[] {_testPortletName4, _testPortletName4}
						).put(
							"column-7",
							new String[] {_testPortletName4, _testPortletName4}
						).put(
							"column-8",
							new String[] {_testPortletName4, _testPortletName4}
						).build());
				}
			};

		_testConvert("3_2_3_columns", portletIdsMaps, false);
	}

	@Test
	public void testConvertThreeTwoThreeColumnsNoPortlets() throws Exception {
		_testConvertNoPortlets("3_2_3_columns");
	}

	@Test
	public void testConvertThreeTwoThreeColumnsSinglePortlet()
		throws Exception {

		List<Map<String, String[]>> portletIdsMaps =
			new ArrayList<Map<String, String[]>>() {
				{
					add(
						TreeMapBuilder.put(
							"column-1", new String[] {_testPortletName4}
						).put(
							"column-2", new String[] {_testPortletName4}
						).put(
							"column-3", new String[] {_testPortletName4}
						).build());
					add(
						TreeMapBuilder.put(
							"column-4", new String[] {_testPortletName1}
						).put(
							"column-5", new String[] {_testPortletName2}
						).build());
					add(
						TreeMapBuilder.put(
							"column-6", new String[] {_testPortletName4}
						).put(
							"column-7", new String[] {_testPortletName4}
						).put(
							"column-8", new String[] {_testPortletName4}
						).build());
				}
			};

		_testConvert("3_2_3_columns", portletIdsMaps, true);
	}

	@Test
	public void testConvertTwoColumnsIIIMultiplePortlets() throws Exception {
		_testConvertTwoColumnsMultiplePortlets("2_columns_iii");
	}

	@Test
	public void testConvertTwoColumnsIIINoPortlets() throws Exception {
		_testConvertNoPortlets("2_columns_iii");
	}

	@Test
	public void testConvertTwoColumnsIIISinglePortlet() throws Exception {
		_testConvertTwoColumnsSinglePortlet("2_columns_iii");
	}

	@Test
	public void testConvertTwoColumnsIIMultiplePortlets() throws Exception {
		_testConvertTwoColumnsMultiplePortlets("2_columns_ii");
	}

	@Test
	public void testConvertTwoColumnsIINoPortlets() throws Exception {
		_testConvertNoPortlets("2_columns_ii");
	}

	@Test
	public void testConvertTwoColumnsIISinglePortlet() throws Exception {
		_testConvertTwoColumnsSinglePortlet("2_columns_ii");
	}

	@Test
	public void testConvertTwoColumnsIMultiplePortlets() throws Exception {
		_testConvertTwoColumnsMultiplePortlets("2_columns_i");
	}

	@Test
	public void testConvertTwoColumnsINoPortlets() throws Exception {
		_testConvertNoPortlets("2_columns_i");
	}

	@Test
	public void testConvertTwoColumnsISinglePortlet() throws Exception {
		_testConvertTwoColumnsSinglePortlet("2_columns_i");
	}

	@Test
	public void testConvertTwoOneTwoColumnsMultiplePortlets() throws Exception {
		List<Map<String, String[]>> portletIdsMaps =
			new ArrayList<Map<String, String[]>>() {
				{
					add(
						TreeMapBuilder.put(
							"column-1",
							new String[] {_testPortletName4, _testPortletName4}
						).put(
							"column-2",
							new String[] {_testPortletName4, _testPortletName4}
						).build());
					add(
						TreeMapBuilder.put(
							"column-3",
							new String[] {_testPortletName1, _testPortletName4}
						).build());
					add(
						TreeMapBuilder.put(
							"column-4",
							new String[] {_testPortletName2, _testPortletName4}
						).put(
							"column-5",
							new String[] {_testPortletName4, _testPortletName4}
						).build());
				}
			};

		_testConvert("2_1_2_columns", portletIdsMaps, false);
	}

	@Test
	public void testConvertTwoOneTwoColumnsNoPortlets() throws Exception {
		_testConvertNoPortlets("2_1_2_columns");
	}

	@Test
	public void testConvertTwoOneTwoColumnsSinglePortlet() throws Exception {
		List<Map<String, String[]>> portletIdsMaps =
			new ArrayList<Map<String, String[]>>() {
				{
					add(
						TreeMapBuilder.put(
							"column-1", new String[] {_testPortletName4}
						).put(
							"column-2", new String[] {_testPortletName4}
						).build());
					add(
						TreeMapBuilder.put(
							"column-3", new String[] {_testPortletName1}
						).build());
					add(
						TreeMapBuilder.put(
							"column-4", new String[] {_testPortletName2}
						).put(
							"column-5", new String[] {_testPortletName4}
						).build());
				}
			};

		_testConvert("2_1_2_columns", portletIdsMaps, true);
	}

	@Test
	public void testConvertTwoTwoColumnsMultiplePortlets() throws Exception {
		List<Map<String, String[]>> portletIdsMaps =
			new ArrayList<Map<String, String[]>>() {
				{
					add(
						TreeMapBuilder.put(
							"column-1",
							new String[] {_testPortletName4, _testPortletName4}
						).put(
							"column-2",
							new String[] {_testPortletName4, _testPortletName4}
						).build());
					add(
						TreeMapBuilder.put(
							"column-3",
							new String[] {_testPortletName1, _testPortletName4}
						).put(
							"column-4",
							new String[] {_testPortletName2, _testPortletName4}
						).build());
				}
			};

		_testConvert("2_2_columns", portletIdsMaps, false);
	}

	@Test
	public void testConvertTwoTwoColumnsNoPortlets() throws Exception {
		_testConvertNoPortlets("2_2_columns");
	}

	@Test
	public void testConvertTwoTwoColumnsSinglePortlet() throws Exception {
		List<Map<String, String[]>> portletIdsMaps =
			new ArrayList<Map<String, String[]>>() {
				{
					add(
						TreeMapBuilder.put(
							"column-1", new String[] {_testPortletName4}
						).put(
							"column-2", new String[] {_testPortletName4}
						).build());
					add(
						TreeMapBuilder.put(
							"column-3", new String[] {_testPortletName1}
						).put(
							"column-4", new String[] {_testPortletName2}
						).build());
				}
			};

		_testConvert("2_2_columns", portletIdsMaps, true);
	}

	@Test
	@TestInfo("LPS-105943")
	public void testGetConversionWarningMessagesWithNestedApplicationsWidgetAndWidgetPageCustomizable()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(),
			UnicodePropertiesBuilder.put(
				LayoutConstants.CUSTOMIZABLE_LAYOUT, Boolean.TRUE.toString()
			).put(
				LayoutTypePortletConstants.NESTED_COLUMN_IDS,
				StringUtil.randomString()
			).buildString());

		LayoutTestUtil.addPortletToLayout(
			layout, LayoutTypePortletConstants.NESTED_COLUMN_IDS);

		layout = _layoutLocalService.fetchLayout(layout.getPlid());

		LayoutConverter layoutConverter =
			_layoutConverterRegistry.getLayoutConverter(
				_getLayoutTemplateId(layout));

		LayoutConversionResult layoutConversionResult = layoutConverter.convert(
			layout, LocaleUtil.getSiteDefault());

		String[] conversionWarningMessages =
			layoutConversionResult.getConversionWarningMessages();

		Assert.assertEquals(
			Arrays.toString(conversionWarningMessages), 2,
			conversionWarningMessages.length);
		Assert.assertTrue(
			ArrayUtil.contains(
				conversionWarningMessages,
				StringBundler.concat(
					"This page uses nested applications widgets. All widgets ",
					"that were inside a nested application widget have been ",
					"placed in a single column and may require manual ",
					"reorganization.")));
		Assert.assertTrue(
			ArrayUtil.contains(
				conversionWarningMessages,
				"This page has customizable columns. This capability is not " +
					"supported for content pages and will be lost if the " +
						"conversion draft is published."));
	}

	@Test
	@TestInfo("LPS-105943")
	public void testGetConversionWarningMessagesWithNonstandarLayout()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(),
			UnicodePropertiesBuilder.put(
				LayoutTypePortletConstants.LAYOUT_TEMPLATE_ID,
				"custom-template-id"
			).buildString());

		LayoutConverter layoutConverter =
			_layoutConverterRegistry.getLayoutConverter(
				_getLayoutTemplateId(layout));

		LayoutConversionResult layoutConversionResult = layoutConverter.convert(
			layout, LocaleUtil.getSiteDefault());

		String[] conversionWarningMessages =
			layoutConversionResult.getConversionWarningMessages();

		Assert.assertEquals(
			Arrays.toString(conversionWarningMessages), 1,
			conversionWarningMessages.length);
		Assert.assertTrue(
			ArrayUtil.contains(
				conversionWarningMessages,
				"This page uses a custom page layout. All widgets have been " +
					"placed in a single column and will require manual " +
						"reorganization."));
	}

	@Test
	public void testIsConvertibleTrue() throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId());

		LayoutConverter layoutConverter =
			_layoutConverterRegistry.getLayoutConverter(
				_getLayoutTemplateId(layout));

		Assert.assertTrue(layoutConverter.isConvertible(layout));
	}

	@Test
	public void testIsConvertibleTrueWidgetPageCustomizable() throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(),
			UnicodePropertiesBuilder.put(
				LayoutConstants.CUSTOMIZABLE_LAYOUT, Boolean.TRUE.toString()
			).buildString());

		LayoutConverter layoutConverter =
			_layoutConverterRegistry.getLayoutConverter(
				_getLayoutTemplateId(layout));

		Assert.assertTrue(layoutConverter.isConvertible(layout));
	}

	@Test
	public void testIsConvertibleTrueWidgetPageWithNestedApplicationsWidget()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(),
			UnicodePropertiesBuilder.put(
				LayoutTypePortletConstants.NESTED_COLUMN_IDS,
				StringUtil.randomString()
			).buildString());

		LayoutConverter layoutConverter =
			_layoutConverterRegistry.getLayoutConverter(
				_getLayoutTemplateId(layout));

		Assert.assertTrue(layoutConverter.isConvertible(layout));
	}

	private LayoutStructure _convertToReadableItemIds(
		LayoutStructure layoutStructure) {

		LayoutStructure newLayoutStructure = new LayoutStructure();

		Map<String, String> itemIds = new HashMap<>();

		for (LayoutStructureItem layoutStructureItem :
				layoutStructure.getLayoutStructureItems()) {

			String parentItemId = StringPool.BLANK;

			if (Validator.isNotNull(layoutStructureItem.getParentItemId())) {
				LayoutStructureItem parentLayoutStructureItem =
					layoutStructure.getLayoutStructureItem(
						layoutStructureItem.getParentItemId());

				parentItemId = itemIds.computeIfAbsent(
					parentLayoutStructureItem.getItemId(),
					itemId -> _getReadableItemId(
						layoutStructure, parentLayoutStructureItem));
			}

			LayoutStructureItem newLayoutStructureItem =
				LayoutStructureItemUtil.create(
					layoutStructureItem.getItemType(), parentItemId);

			String newItemId = itemIds.computeIfAbsent(
				layoutStructureItem.getItemId(),
				itemId -> _getReadableItemId(
					layoutStructure, layoutStructureItem));

			newLayoutStructureItem.setItemId(newItemId);

			List<String> newChildrenItemIds = new ArrayList<>();

			for (String childrenItemId :
					layoutStructureItem.getChildrenItemIds()) {

				LayoutStructureItem childrenLayoutStructureItem =
					layoutStructure.getLayoutStructureItem(childrenItemId);

				String newChildrenItemId = itemIds.computeIfAbsent(
					childrenItemId,
					itemId -> _getReadableItemId(
						layoutStructure, childrenLayoutStructureItem));

				newChildrenItemIds.add(newChildrenItemId);
			}

			newLayoutStructureItem.setChildrenItemIds(newChildrenItemIds);

			newLayoutStructureItem.updateItemConfig(
				layoutStructureItem.getItemConfigJSONObject());

			newLayoutStructure.addLayoutStructureItem(newLayoutStructureItem);
		}

		newLayoutStructure.setMainItemId(
			itemIds.computeIfAbsent(
				layoutStructure.getMainItemId(),
				itemId -> _getReadableItemId(
					layoutStructure,
					layoutStructure.getMainLayoutStructureItem())));

		return newLayoutStructure;
	}

	private String _getLayoutTemplateId(Layout layout) {
		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		return layoutTypePortlet.getLayoutTemplateId();
	}

	private String _getReadableItemId(
		LayoutStructure layoutStructure,
		LayoutStructureItem layoutStructureItem) {

		String parentItemId = layoutStructureItem.getParentItemId();

		if (Validator.isNull(parentItemId)) {
			return StringUtil.toUpperCase(layoutStructureItem.getItemType());
		}

		StringBundler sb = new StringBundler(4);

		LayoutStructureItem parentLayoutStructureItem =
			layoutStructure.getLayoutStructureItem(parentItemId);

		String uuid = _getReadableItemId(
			layoutStructure, parentLayoutStructureItem);

		sb.append(uuid);

		sb.append(StringPool.DASH);
		sb.append(StringUtil.toUpperCase(layoutStructureItem.getItemType()));

		List<String> childrenItemIds =
			parentLayoutStructureItem.getChildrenItemIds();

		int position = childrenItemIds.indexOf(layoutStructureItem.getItemId());

		sb.append(position);

		return sb.toString();
	}

	private void _initPortlets() {
		_testPortletName1 = "TEST_PORTLET_" + RandomTestUtil.randomString();

		_registerTestPortlet(_testPortletName1);

		_testPortletName2 = "TEST_PORTLET_" + RandomTestUtil.randomString();

		_registerTestPortlet(_testPortletName2);

		_testPortletName3 = "TEST_PORTLET_" + RandomTestUtil.randomString();

		_registerTestPortlet(_testPortletName3);

		_testPortletName4 = "TEST_PORTLET_" + RandomTestUtil.randomString();

		_registerTestPortlet(_testPortletName4);

		_testPortletName5 = "TEST_PORTLET_" + RandomTestUtil.randomString();

		_registerTestPortlet(_testPortletName5);
	}

	private String _read(String fileName) throws Exception {
		String content = new String(
			FileUtil.getBytes(getClass(), "dependencies/" + fileName));

		return StringUtil.replace(
			content, "${", "}",
			HashMapBuilder.put(
				"TEST_PORTLET_NAME_1", _testPortletName1
			).put(
				"TEST_PORTLET_NAME_2", _testPortletName2
			).put(
				"TEST_PORTLET_NAME_3", _testPortletName3
			).put(
				"TEST_PORTLET_NAME_4", _testPortletName4
			).put(
				"TEST_PORTLET_NAME_5", _testPortletName5
			).build());
	}

	private void _registerTestPortlet(String portletName) {
		_serviceRegistrations.add(
			_bundleContext.registerService(
				jakarta.portlet.Portlet.class, new TestPortlet(),
				HashMapDictionaryBuilder.put(
					"com.liferay.portlet.instanceable", "true"
				).put(
					"jakarta.portlet.name", portletName
				).build()));
	}

	private void _testConvert(
			String layoutTemplateId, List<Map<String, String[]>> portletIdsMaps,
			boolean singlePortlet)
		throws Exception {

		int columnId = 0;
		List<Map<String, List<String>>> encodedPortletIdsMaps =
			new ArrayList<>();

		Layout layout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(),
			UnicodePropertiesBuilder.put(
				LayoutTypePortletConstants.LAYOUT_TEMPLATE_ID, layoutTemplateId
			).put(
				"lfr-theme:regular:wrap-widget-page-content",
				Boolean.FALSE.toString()
			).buildString());

		_segmentsExperienceLocalService.addDefaultSegmentsExperience(
			null, PrincipalThreadLocal.getUserId(), layout.getPlid(),
			ServiceContextThreadLocal.getServiceContext());

		for (Map<String, String[]> portletIdsMap : portletIdsMaps) {
			Set<Map.Entry<String, String[]>> entries = portletIdsMap.entrySet();

			Map<String, List<String>> encodedPortletIdsMap = new TreeMap<>();

			for (Map.Entry<String, String[]> entry : entries) {
				columnId++;

				encodedPortletIdsMap.put(entry.getKey(), new ArrayList<>());

				List<String> encodedPortletIds = encodedPortletIdsMap.get(
					entry.getKey());

				for (String portletId : entry.getValue()) {
					Portlet portlet = _portletLocalService.getPortletById(
						_group.getCompanyId(), portletId);

					String encodedPortletId = portletId;

					if (portlet.isInstanceable()) {
						encodedPortletId = PortletIdCodec.encode(portletId);
					}

					LayoutTestUtil.addPortletToLayout(
						_user.getUserId(), layout, encodedPortletId,
						"column-" + columnId, new HashMap<>());

					encodedPortletIds.add(encodedPortletId);
				}
			}

			encodedPortletIdsMaps.add(encodedPortletIdsMap);
		}

		LayoutConverter layoutConverter =
			_layoutConverterRegistry.getLayoutConverter(
				_getLayoutTemplateId(layout));

		LayoutConversionResult layoutConversionResult = layoutConverter.convert(
			layout, LocaleUtil.getSiteDefault());

		LayoutData layoutData = layoutConversionResult.getLayoutData();

		LayoutStructure actualLayoutStructure = LayoutStructure.of(
			String.valueOf(layoutData.getLayoutDataJSONObject()));

		actualLayoutStructure = _convertToReadableItemIds(
			actualLayoutStructure);

		String format = "expected_layout_data_%s_multiple_portlets.json";

		if (singlePortlet) {
			format = "expected_layout_data_%s_single_portlet.json";
		}

		String expectedLayoutData = _read(
			String.format(format, layoutTemplateId));

		List<FragmentEntryLink> fragmentEntryLinks =
			_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
				_group.getGroupId(), layout.getPlid());

		List<FragmentEntryLink> sortedFragmentEntryLinks = ListUtil.sort(
			fragmentEntryLinks,
			Comparator.comparing(FragmentEntryLink::getFragmentEntryLinkId));

		int fromIndex = 0;

		for (Map<String, List<String>> encodedPortletIdsMap :
				encodedPortletIdsMaps) {

			for (Map.Entry<String, List<String>> entry :
					encodedPortletIdsMap.entrySet()) {

				List<String> portletIds = entry.getValue();

				int numberOfPortletsInColumn = portletIds.size();

				List<FragmentEntryLink> fragmentEntryLinksInColumn =
					sortedFragmentEntryLinks.subList(
						fromIndex, fromIndex + numberOfPortletsInColumn);

				fromIndex = fromIndex + numberOfPortletsInColumn;

				Set<String> existingPortletIds = new HashSet<>();

				for (FragmentEntryLink fragmentEntryLink :
						fragmentEntryLinksInColumn) {

					JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
						fragmentEntryLink.getEditableValues());

					String portletId = jsonObject.getString("portletId");

					existingPortletIds.add(
						PortletIdCodec.encode(
							portletId, jsonObject.getString("instanceId")));

					expectedLayoutData = StringUtil.replaceFirst(
						expectedLayoutData,
						entry.getKey() + ":" +
							PortletIdCodec.decodePortletName(portletId),
						String.valueOf(
							fragmentEntryLink.getFragmentEntryLinkId()));
				}

				Assert.assertEquals(
					fragmentEntryLinksInColumn.toString(), portletIds.size(),
					fragmentEntryLinksInColumn.size());

				for (String portletId : portletIds) {
					Assert.assertTrue(existingPortletIds.contains(portletId));
				}
			}
		}

		LayoutStructure expectedLayoutStructure = LayoutStructure.of(
			expectedLayoutData);

		Assert.assertEquals(expectedLayoutStructure, actualLayoutStructure);
	}

	private void _testConvertNoPortlets(String layoutTemplateId)
		throws Exception {

		Layout layout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(),
			UnicodePropertiesBuilder.put(
				LayoutTypePortletConstants.LAYOUT_TEMPLATE_ID, layoutTemplateId
			).buildString());

		LayoutConverter layoutConverter =
			_layoutConverterRegistry.getLayoutConverter(
				_getLayoutTemplateId(layout));

		LayoutConversionResult layoutConversionResult = layoutConverter.convert(
			layout, LocaleUtil.getSiteDefault());

		LayoutData layoutData = layoutConversionResult.getLayoutData();

		LayoutStructure actualLayoutStructure = LayoutStructure.of(
			String.valueOf(layoutData.getLayoutDataJSONObject()));

		actualLayoutStructure = _convertToReadableItemIds(
			actualLayoutStructure);

		String expectedLayoutData = _read(
			String.format(
				"expected_layout_data_%s_no_portlets.json", layoutTemplateId));

		LayoutStructure expectedLayoutStructure = LayoutStructure.of(
			expectedLayoutData);

		Assert.assertEquals(expectedLayoutStructure, actualLayoutStructure);
	}

	private void _testConvertOneTwoColumnsMultiplePortlets(
			String layoutTemplateId)
		throws Exception {

		List<Map<String, String[]>> portletIdsMaps =
			new ArrayList<Map<String, String[]>>() {
				{
					add(
						TreeMapBuilder.put(
							"column-1",
							new String[] {_testPortletName4, _testPortletName4}
						).build());
					add(
						TreeMapBuilder.put(
							"column-2",
							new String[] {_testPortletName1, _testPortletName4}
						).put(
							"column-3",
							new String[] {_testPortletName2, _testPortletName4}
						).build());
				}
			};

		_testConvert(layoutTemplateId, portletIdsMaps, false);
	}

	private void _testConvertOneTwoColumnsSinglePortlet(String layoutTemplateId)
		throws Exception {

		List<Map<String, String[]>> portletIdsMaps =
			new ArrayList<Map<String, String[]>>() {
				{
					add(
						TreeMapBuilder.put(
							"column-1", new String[] {_testPortletName4}
						).build());
					add(
						TreeMapBuilder.put(
							"column-2", new String[] {_testPortletName1}
						).put(
							"column-3", new String[] {_testPortletName2}
						).build());
				}
			};

		_testConvert(layoutTemplateId, portletIdsMaps, true);
	}

	private void _testConvertOneTwoOneColumnsMultiplePortlets(
			String layoutTemplateId)
		throws Exception {

		List<Map<String, String[]>> portletIdsMaps =
			new ArrayList<Map<String, String[]>>() {
				{
					add(
						TreeMapBuilder.put(
							"column-1",
							new String[] {_testPortletName4, _testPortletName4}
						).build());
					add(
						TreeMapBuilder.put(
							"column-2",
							new String[] {_testPortletName1, _testPortletName4}
						).put(
							"column-3",
							new String[] {_testPortletName2, _testPortletName4}
						).build());
					add(
						TreeMapBuilder.put(
							"column-4",
							new String[] {_testPortletName4, _testPortletName4}
						).build());
				}
			};

		_testConvert(layoutTemplateId, portletIdsMaps, false);
	}

	private void _testConvertOneTwoOneColumnsSinglePortlet(
			String layoutTemplateId)
		throws Exception {

		List<Map<String, String[]>> portletIdsMaps =
			new ArrayList<Map<String, String[]>>() {
				{
					add(
						TreeMapBuilder.put(
							"column-1", new String[] {_testPortletName4}
						).build());
					add(
						TreeMapBuilder.put(
							"column-2", new String[] {_testPortletName1}
						).put(
							"column-3", new String[] {_testPortletName2}
						).build());
					add(
						TreeMapBuilder.put(
							"column-4", new String[] {_testPortletName4}
						).build());
				}
			};

		_testConvert(layoutTemplateId, portletIdsMaps, true);
	}

	private void _testConvertTwoColumnsMultiplePortlets(String layoutTemplateId)
		throws Exception {

		List<Map<String, String[]>> portletIdsMaps =
			new ArrayList<Map<String, String[]>>() {
				{
					add(
						TreeMapBuilder.put(
							"column-1",
							new String[] {_testPortletName1, _testPortletName4}
						).put(
							"column-2",
							new String[] {_testPortletName2, _testPortletName3}
						).build());
				}
			};

		_testConvert(layoutTemplateId, portletIdsMaps, false);
	}

	private void _testConvertTwoColumnsSinglePortlet(String layoutTemplateId)
		throws Exception {

		List<Map<String, String[]>> portletIdsMaps =
			new ArrayList<Map<String, String[]>>() {
				{
					add(
						TreeMapBuilder.put(
							"column-1", new String[] {_testPortletName1}
						).put(
							"column-2", new String[] {_testPortletName2}
						).build());
				}
			};

		_testConvert(layoutTemplateId, portletIdsMaps, true);
	}

	private Bundle _bundle;
	private BundleContext _bundleContext;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutConverterRegistry _layoutConverterRegistry;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private PortletLocalService _portletLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private final List<ServiceRegistration<?>> _serviceRegistrations =
		new CopyOnWriteArrayList<>();
	private String _testPortletName1;
	private String _testPortletName2;
	private String _testPortletName3;
	private String _testPortletName4;
	private String _testPortletName5;
	private User _user;

	private class TestPortlet extends GenericPortlet {
	}

}