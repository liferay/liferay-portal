/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.List;
import java.util.Map;

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
public class PortalImplGetLayoutActualURLTest
	extends BasePortalImplURLTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			group.getGroupId());
	}

	@Test
	public void testGetLayoutActualURL() throws Exception {
		Layout layout = _addLayout(
			publicLayout.getLayoutId(), LayoutConstants.TYPE_PORTLET);

		_assertGetLayoutActualURL(layout, layout);
	}

	@Test
	public void testGetLayoutActualURLNoBrowsableLayout() throws Exception {
		_assertGetLayoutActualURL(
			publicLayout,
			_addLayout(publicLayout.getLayoutId(), LayoutConstants.TYPE_NODE));
	}

	@Test
	public void testGetLayoutActualURLWithNodeLayoutHierarchy()
		throws Exception {

		_assertGetLayoutActualURLWithNodeLayoutHierarchy(
			_NODE_LAYOUT_HIERARCHY_LEVEL_LENGTH - 1,
			_addLayout(publicLayout.getLayoutId(), LayoutConstants.TYPE_NODE),
			_NODE_LAYOUT_HIERARCHY_LEVEL_LENGTH - 1);
	}

	@Test
	public void testGetLayoutActualURLWithNodeLayoutHierarchyFirstLayoutBrowsable()
		throws Exception {

		_assertGetLayoutActualURLWithNodeLayoutHierarchy(
			0,
			_addLayout(publicLayout.getLayoutId(), LayoutConstants.TYPE_NODE),
			_NODE_LAYOUT_HIERARCHY_LEVEL_LENGTH - 1);
	}

	@Test
	public void testGetLayoutActualURLWithNodeLayoutHierarchyFirstLayoutParent()
		throws Exception {

		_assertGetLayoutActualURLWithNodeLayoutHierarchy(
			_NODE_LAYOUT_HIERARCHY_LEVEL_LENGTH - 1,
			_addLayout(publicLayout.getLayoutId(), LayoutConstants.TYPE_NODE),
			0);
	}

	@Test
	public void testGetLayoutActualURLWithNodeLayoutHierarchyMiddleLayoutBrowsable()
		throws Exception {

		_assertGetLayoutActualURLWithNodeLayoutHierarchy(
			GetterUtil.getInteger(
				Math.floor((_NODE_LAYOUT_HIERARCHY_LEVEL_LENGTH - 1) / 2)),
			_addLayout(publicLayout.getLayoutId(), LayoutConstants.TYPE_NODE),
			_NODE_LAYOUT_HIERARCHY_LEVEL_LENGTH - 1);
	}

	@Test
	public void testGetLayoutActualURLWithNodeLayoutHierarchyMiddleLayoutParent()
		throws Exception {

		_assertGetLayoutActualURLWithNodeLayoutHierarchy(
			_NODE_LAYOUT_HIERARCHY_LEVEL_LENGTH - 1,
			_addLayout(publicLayout.getLayoutId(), LayoutConstants.TYPE_NODE),
			GetterUtil.getInteger(
				Math.floor((_NODE_LAYOUT_HIERARCHY_LEVEL_LENGTH - 1) / 2)));
	}

	private List<Layout> _addChildLayouts(long parentLayoutId, String... types)
		throws Exception {

		return TransformUtil.transformToList(
			types, type -> _addLayout(parentLayoutId, type));
	}

	private Layout _addLayout(long parentLayoutId, String type)
		throws Exception {

		return layoutLocalService.addLayout(
			null, _serviceContext.getUserId(), group.getGroupId(), false,
			parentLayoutId, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), type,
			false, StringPool.BLANK, _serviceContext);
	}

	private Layout _assertAllChildrenAndGetDeeperParentLayout(
			int expectedNumChildren, Layout layout)
		throws Exception {

		List<Layout> childLayouts = layout.getAllChildren();

		Assert.assertEquals(
			childLayouts.toString(), expectedNumChildren, childLayouts.size());

		int ancestorLayoutsCount = 0;
		Layout deeperParentLayout = null;

		for (Layout childLayout : childLayouts) {
			if (!childLayout.hasChildren()) {
				continue;
			}

			List<Layout> ancestorLayouts = childLayout.getAncestors();

			int curAncestorLayoutsCount = ancestorLayouts.size();

			if (curAncestorLayoutsCount < ancestorLayoutsCount) {
				continue;
			}

			ancestorLayoutsCount = curAncestorLayoutsCount;

			deeperParentLayout = childLayout;
		}

		Assert.assertNotNull(deeperParentLayout);

		return deeperParentLayout;
	}

	private Layout _assertAllChildrenAndGetDepperChildLayout(
			int expectedNumChildren, Layout layout, int pos)
		throws Exception {

		Layout parentLayout = _assertAllChildrenAndGetDeeperParentLayout(
			expectedNumChildren, layout);

		List<Layout> lastLevelLayouts = parentLayout.getChildren();

		Assert.assertTrue(
			lastLevelLayouts.toString(), lastLevelLayouts.size() > pos);

		return lastLevelLayouts.get(pos);
	}

	private void _assertGetLayoutActualURL(
		Layout expectedLayout, Layout layout) {

		_multiVMPool.clear();

		String actualURL = null;

		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			actualURL = portal.getLayoutActualURL(layout);
		}

		Map<String, String[]> parameterMap = HttpComponentsUtil.getParameterMap(
			HttpComponentsUtil.getQueryString(actualURL));

		if (expectedLayout.getPlid() == layout.getPlid()) {
			Assert.assertEquals(
				MapUtil.toString(parameterMap), expectedLayout.getPlid(),
				MapUtil.getLong(parameterMap, "p_l_id"));

			return;
		}

		Assert.assertEquals(
			MapUtil.toString(parameterMap), expectedLayout.getGroupId(),
			MapUtil.getLong(parameterMap, "groupId"));
		Assert.assertEquals(
			MapUtil.toString(parameterMap), expectedLayout.isPrivateLayout(),
			MapUtil.getBoolean(parameterMap, "privateLayout"));
		Assert.assertEquals(
			MapUtil.toString(parameterMap), expectedLayout.getLayoutId(),
			MapUtil.getLong(parameterMap, "layoutId"));
	}

	private void _assertGetLayoutActualURLWithNodeLayoutHierarchy(
			int browsableTypePos, Layout layout, int parentIndex)
		throws Exception {

		_createLayoutHierarchy(
			_NODE_LAYOUT_HIERARCHY_DEPTH, parentIndex, layout.getLayoutId(),
			_getTypes(-1, _NODE_LAYOUT_HIERARCHY_LEVEL_LENGTH));

		int expectedNumChildren =
			_NODE_LAYOUT_HIERARCHY_DEPTH * _NODE_LAYOUT_HIERARCHY_LEVEL_LENGTH;

		Layout childLayout = _assertAllChildrenAndGetDepperChildLayout(
			expectedNumChildren, layout, parentIndex);

		Assert.assertEquals(LayoutConstants.TYPE_NODE, childLayout.getType());

		_addChildLayouts(
			childLayout.getLayoutId(),
			_getTypes(browsableTypePos, _NODE_LAYOUT_HIERARCHY_LEVEL_LENGTH));

		expectedNumChildren =
			expectedNumChildren + _NODE_LAYOUT_HIERARCHY_LEVEL_LENGTH;

		childLayout = _assertAllChildrenAndGetDepperChildLayout(
			expectedNumChildren, layout, browsableTypePos);

		Assert.assertEquals(
			LayoutConstants.TYPE_PORTLET, childLayout.getType());

		_assertGetLayoutActualURL(childLayout, layout);
	}

	private void _createLayoutHierarchy(
			int depth, int parentIndex, long parentLayoutId, String... types)
		throws Exception {

		long curParentLayoutId = parentLayoutId;

		for (int i = 0; i < depth; i++) {
			List<Layout> layouts = _addChildLayouts(curParentLayoutId, types);

			Layout parentLayout = layouts.get(parentIndex);

			Assert.assertNotNull(parentLayout);

			curParentLayoutId = parentLayout.getLayoutId();
		}
	}

	private String[] _getTypes(int browsableTypePos, int length) {
		String[] types = new String[length];

		for (int i = 0; i < length; i++) {
			String type = LayoutConstants.TYPE_NODE;

			if ((browsableTypePos >= 0) && (i >= browsableTypePos)) {
				type = LayoutConstants.TYPE_PORTLET;
			}

			types[i] = type;
		}

		return types;
	}

	private static final int _NODE_LAYOUT_HIERARCHY_DEPTH = 5;

	private static final int _NODE_LAYOUT_HIERARCHY_LEVEL_LENGTH = 5;

	@Inject
	private MultiVMPool _multiVMPool;

	private ServiceContext _serviceContext;

}